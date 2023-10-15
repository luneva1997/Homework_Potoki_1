import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        //Создание списка потоков
        List<FutureTask<Integer>> futures = new ArrayList<>();

        for (String text : texts) {
            //Создаем поток и реализуем логику
            Callable<Integer> callable;

            futures.add(new FutureTask<> (callable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            }
            ));
        }

        int max = 0;

        List<Thread> threads = new ArrayList<>();

        //Запуск потоков
        for (FutureTask<Integer> one : futures) {
            threads.add(new Thread(one));
        }

        for (Thread thread: threads){
            thread.start();
        }

        for (Thread thread: threads){
            thread.join();
        }

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");

        for (FutureTask<Integer> one : futures){
            int newMax;

            try {
                newMax = one.get();
            } catch (InterruptedException | ExecutionException ex){
                return;
            }

            if (newMax>max){
                max = newMax;
            }
        }

        System.out.println("Maximum: " + max);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
