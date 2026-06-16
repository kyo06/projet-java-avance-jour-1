package org.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CompteurPartage {

    private int valeurNonSafe = 0;
    private int valeurSync = 0;
    private final AtomicInteger valeurAtomic = new AtomicInteger(0);

    public void incrementerNonSafe() {
        valeurNonSafe++; // NE PAS CORRIGER — demonstration de la race condition
    }

    public synchronized void incrementerSync() {
        valeurSync++; // Le verrou garantit l'atomicité de la lecture-modification-écriture
    }

    public void incrementerAtomic() {
        valeurAtomic.incrementAndGet(); // Opération CAS atomique au niveau CPU
    }

    public static void main(String[] args) throws InterruptedException {
        final int NB_THREADS    = 10;
        final int NB_INCREMENTS = 1_000;
        final int ATTENDU       = NB_THREADS * NB_INCREMENTS; // 10 000

        CompteurPartage compteur = new CompteurPartage();

        // --- Version non thread-safe ---
        long debut = System.currentTimeMillis();
        //lancerThreads(NB_THREADS, NB_INCREMENTS, () -> compteur.incrementerNonSafe());
        lancerThreads(NB_THREADS, NB_INCREMENTS, compteur::incrementerNonSafe);
        long tempsNonSafe = System.currentTimeMillis() - debut;
        System.out.printf("Non thread-safe : %5d (attendu %d) — %dms%n",
                compteur.valeurNonSafe, ATTENDU, tempsNonSafe);

        // --- Version synchronized ---
        debut = System.currentTimeMillis();
        lancerThreads(NB_THREADS, NB_INCREMENTS, compteur::incrementerSync);
        long tempsSync = System.currentTimeMillis() - debut;
        System.out.printf("Synchronized    : %5d ✓              — %dms%n",
                compteur.valeurSync, tempsSync);

        // --- Version atomique ---
        debut = System.currentTimeMillis();
        lancerThreads(NB_THREADS, NB_INCREMENTS, compteur::incrementerAtomic);
        long tempsAtomic = System.currentTimeMillis() - debut;
        System.out.printf("AtomicInteger   : %5d ✓              — %dms%n",
                compteur.valeurAtomic.get(), tempsAtomic);
    }

    static void lancerThreads(int nb, int increments, Runnable action)
            throws InterruptedException {
        Thread[] threads = new Thread[nb];
        for (int i = 0; i < nb; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < increments; j++) action.run();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }
}