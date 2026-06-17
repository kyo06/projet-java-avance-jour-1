package org.example;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ThreadLocalRandom;

public class CompteBancaire {

    private final String id;
    private double solde;

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
    public void transferer(CompteBancaire destination, double montant) {

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

        synchronized (this) {
            synchronized (destination) {

                if (this.solde < montant) {
                    return;
                }

                this.solde -= montant;
                destination.solde += montant;
            }
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
    public static void startDeadlockMonitor() {

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
        startDeadlockMonitor();

        CompteBancaire compteA =
                new CompteBancaire("A", 1000);

        CompteBancaire compteB =
                new CompteBancaire("B", 1000);

        double totalInitial =
                compteA.getSolde() + compteB.getSolde();

        System.out.println("Total initial : " + totalInitial);

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

                        compteA.transferer(compteB, montant);

                    } else {

                        compteB.transferer(compteA, montant);
                    }
                }
            }, "Worker-" + i);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

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
    }
}