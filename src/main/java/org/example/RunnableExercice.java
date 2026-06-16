package org.example;

import java.util.concurrent.*;

public class RunnableExercice {

    public static void main(String[] args) throws Exception {
        // Version 1 : Classe anonyme Runnable
        long debut1 = System.currentTimeMillis();
        Runnable version1 = new Runnable() {
            @Override
            public void run() {
                for(int i = 5; i >= 1; i--) {
                    System.out.println("Version 1 - Nom du thread : " + Thread.currentThread().getName() + " - Compte : " + i);
                    try {
                        Thread.sleep(300);
                    } catch(InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Nom du thread : " + Thread.currentThread().getName() + " - Interrompu à l'étape " + i);
                        return;
                    }
                    System.out.println("Version 1 - Nom du thread : " + Thread.currentThread().getName() + " - Terminé");
                }
            }
        };

        Thread t1 = new Thread(version1);
        t1.start();
        t1.join();

        System.out.printf("Version 1 (anonyme)    : %dms%n",
                System.currentTimeMillis() - debut1);

        // Version 2 : Lambda
        long debut2 = System.currentTimeMillis();
        Runnable version2 = () -> {
            for(int i = 5; i >= 1; i--) {
                System.out.println("Version 2 - Nom du thread : " + Thread.currentThread().getName() + " - Compte : " + i);
                try {
                    Thread.sleep(300);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Nom du thread : " + Thread.currentThread().getName() + " - Interrompu à l'étape " + i);
                    return;
                }
                System.out.println("Version 2 - Nom du thread : " + Thread.currentThread().getName() + " - Terminé");
            }
        };
        Thread t2 = new Thread(version2);
        t2.start();
        t2.join();
        System.out.printf("Version 2 (lambda)     : %dms%n",
                System.currentTimeMillis() - debut2);

        Callable<Long> version3 = new Callable<>() {
            @Override
            public Long call() throws Exception {
                for(int i = 5; i >= 1; i--) {
                    System.out.println("Version 3 - Nom du thread : " + Thread.currentThread().getName() + " - Compte : " + i);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Nom du thread : " + Thread.currentThread().getName() + " - Interrompu à l'étape " + i);
                        return -9999L;
                    }
                    System.out.println("Version 3 - Nom du thread : " + Thread.currentThread().getName() + " - Terminé");
                }
                return 1600L;
            }
        };

        // Version 3 : ExecutorService
        long debut3 = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // soumettre 3 tâches lambda avec submit()
        Future<?> f1 = executor.submit(version1);
        Future<?> f2 = executor.submit(version2);
        Future<Long> f3 = executor.submit(version3);
        Long resultat3 = f3.get();
        f1.get(); //Equivalent à join
        f2.get(); //Equivalent à join

        //  shutdown() et awaitTermination()
        //executor.awaitTermination(60, TimeUnit.SECONDS);
        executor.shutdown();
        // mesurer et afficher le temps pour chaque version
        System.out.printf("Version 3 (pool × 3)   : %dms (parallèle !)%n",
                System.currentTimeMillis() - debut3);
    }
}