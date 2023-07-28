package ru.netology.murin.multithreading.lesson4.task1;


import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//Программа-анализатор
public class Main {
    private static final AtomicInteger maxACount = new AtomicInteger(0);
    private static final AtomicInteger maxBCount = new AtomicInteger(0);
    private static final AtomicInteger maxCCount = new AtomicInteger(0);

    private static final BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws Exception {


        Thread generator = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String generatedString = generateText("abc", 100000);
                try {
                    aQueue.put(generatedString);
                    bQueue.put(generatedString);
                    cQueue.put(generatedString);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generator.start();
        Thread.sleep(5000);


        Thread threadA = new Thread(() -> {
            try {
                while (true) {
                    calculateAndPrintMaxSize(aQueue.remove(), 'a');
                }
            } catch (Exception e) {
                System.out.println("Processing B queue finished");
            }
        });
        threadA.start();


        Thread threadB = new Thread(() -> {
            try {
                while (true) {
                    calculateAndPrintMaxSize(bQueue.remove(), 'b');
                }
            } catch (Exception e) {
                System.out.println("Processing B queue finished");
            }
        });
        threadB.start();


        Thread threadC = new Thread(() -> {
            try {
                while (true) {
                    calculateAndPrintMaxSize(cQueue.remove(), 'c');
                }
            } catch (Exception e) {
                System.out.println("Processing B queue finished");
            }

        });
        threadC.start();

        generator.join();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("max A count " + maxACount.get());
        System.out.println("max B count " + maxBCount.get());
        System.out.println("max C count " + maxCCount.get());


    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void calculateAndPrintMaxSize(String text, char letter) {
        int maxSize = 0;
        int curSize = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == letter) {
                curSize++;
            } else {
                maxSize = Math.max(maxSize, curSize);
                curSize = 0;
            }
        }
        System.out.println(text.substring(0, 100) + " -> " + letter + " count: " + maxSize);
        switch (letter) {
            case 'a':
                maxACount.set(Math.max(maxSize, maxACount.get()));
                break;
            case 'b':
                maxBCount.set(Math.max(maxSize, maxBCount.get()));
                break;
            case 'c':
                maxBCount.set(Math.max(maxSize, maxBCount.get()));
                break;
        }

    }
}
