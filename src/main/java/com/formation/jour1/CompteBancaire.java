package com.formation.jour1;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class CompteBancaire {

    private final String id;
    private double solde;

    private static final ReentrantLock verrou1 = new ReentrantLock();
    private static final ReentrantLock verrou2 = new ReentrantLock();

    public CompteBancaire(String id, double soldeInitial) {
        this.id = id;
        this.solde = soldeInitial;
    }

    public synchronized void deposer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Montant invalide : " + montant);
        }
        solde += montant;
    }

    public synchronized boolean retirer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Montant invalide : " + montant);
        }

        if (solde < montant) {
            return false;
        }

        solde -= montant;
        return true;
    }

    /**
     * Transfert atomique sans deadlock.
     */
    public void transferer(CompteBancaire destination, double montant) throws InterruptedException {

        if (destination == null) {
            throw new IllegalArgumentException("Destination null");
        }

        if (montant <= 0) {
            throw new IllegalArgumentException("Montant invalide : " + montant);
        }

        CompteBancaire premier =
                this.id.compareTo(destination.id) < 0 ? this : destination;

        CompteBancaire second =
                premier == this ? destination : this;

        while (true) {
            if (verrou1.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    if (verrou2.tryLock(50, TimeUnit.MILLISECONDS)) {
                        try {
                            // Opération avec les deux verrous
                            if (this.solde < montant) {
                                return;
                            }

                            this.solde -= montant;
                            destination.solde += montant;
                        } finally {
                            verrou2.unlock();
                        }
                    }
                } finally {
                    verrou1.unlock();
                }
            }
            System.out.println("Souci de deadlock");
            Thread.sleep(10); // Attente avant retry
        }

    }

    public synchronized double getSolde() {
        return solde;
    }

    public String getId() {
        return id;
    }

    /**
     * Surveillance des deadlocks.
     */
    public static void startDeadlockMonitor(Scanner sc) {

        Thread monitor = new Thread(() -> {

            ThreadMXBean bean = ManagementFactory.getThreadMXBean();

            while (true) {

                long[] ids = bean.findDeadlockedThreads();

                if (ids != null && ids.length > 0) {

                    System.err.println("\n==============================");
                    System.err.println(" DEADLOCK DETECTÉ !");
                    System.err.println("==============================");

                    ThreadInfo[] infos =
                            bean.getThreadInfo(ids, true, true);

                    for (ThreadInfo info : infos) {

                        System.err.println("\nThread : "
                                + info.getThreadName());

                        System.err.println("Etat   : "
                                + info.getThreadState());

                        System.err.println("Attend le verrou détenu par : "
                                + info.getLockOwnerName());

                        System.err.println("\nStack trace :");

                        for (StackTraceElement e :
                                info.getStackTrace()) {
                            System.err.println("    at " + e);
                        }
                    }

                    System.out.println("Etape Deadlock détecté");
                    sc.nextLine();

                    System.exit(1);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        monitor.setDaemon(true);
        monitor.setName("DeadlockMonitor");
        monitor.start();
    }

    public static void main(String[] args) throws InterruptedException {

        // Démarrage du détecteur
        Scanner sc = new Scanner(System.in);
        startDeadlockMonitor(sc);
        CompteBancaire compteA =
                new CompteBancaire("A", 1000);

        CompteBancaire compteB =
                new CompteBancaire("B", 1000);

        double totalInitial =
                compteA.getSolde() + compteB.getSolde();

        System.out.println("Total initial : " + totalInitial);

        System.out.println("Etape 1");
        sc.nextLine();

        Thread[] threads = new Thread[5];

        for (int i = 0; i < threads.length; i++) {

            threads[i] = new Thread(() -> {

                long fin =
                        System.currentTimeMillis() + 2000;

                while (System.currentTimeMillis() < fin) {

                    double montant =
                            ThreadLocalRandom.current()
                                    .nextDouble(10, 500);

                    if (ThreadLocalRandom.current()
                            .nextBoolean()) {

                        try {
                            compteA.transferer(compteB, montant);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } else {

                        try {
                            compteB.transferer(compteA, montant);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, "Worker-" + i);
        }

        System.out.println("Etape 2");
        sc.nextLine();
        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Etape 3");
        sc.nextLine();

        double totalFinal =
                compteA.getSolde() + compteB.getSolde();

        System.out.println("\nCompte A : "
                + compteA.getSolde());

        System.out.println("Compte B : "
                + compteB.getSolde());

        System.out.printf(
                "Total final : %.2f %s%n",
                totalFinal,
                Math.abs(totalFinal - totalInitial) < 0.01
                        ? "✓ COHÉRENT"
                        : "❌ INCOHÉRENT"
        );

        List<String> l = new ArrayList<>();
        l.add(compteA.getId()); // add n'est pas synchronizd
        List<String> lSynchronized = Collections.synchronizedList(l);
        l.add(compteA.getId()); // add est synchronizd

        List<String> lImmutable = List.of(compteB.getId(), compteA.getId(), "Hello");
        // lImmutable.add(compteB.getId()); //problem : UnsupportedOperationException

    }
}