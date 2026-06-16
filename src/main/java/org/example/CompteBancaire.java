package org.example;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Objectif : Concevoir une classe métier thread-safe complète.
 * Durée estimée : 15 min
 * Compétence : synchronized, cohérence des invariants, tests de concurrence
 *
 * Énoncé
 * Implémentez une classe CompteBancaire avec les opérations deposer(montant), retirer(montant) et transferer(CompteBancaire dest, double montant). Toutes les opérations doivent être thread-safe.
 *
 * Contraintes :
 *
 * Le solde ne peut jamais être négatif
 * Le transfert entre deux comptes doit être atomique (pas de découvert intermédiaire)
 * La méthode transferer ne doit pas créer de deadlock
 * Testez avec 5 threads qui font des dépôts et retraits aléatoires simultanément pendant 2 secondes. Vérifiez la cohérence du solde final.
 *
 *
 */

public class CompteBancaire {
    private final String id;
    private double solde;

    public CompteBancaire(String id, double soldeInitial) {
        this.id = id;
        this.solde = soldeInitial;
    }

    public synchronized void deposer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Montant invalide: " + montant);
        }
        solde += montant;
    }

    public synchronized boolean retirer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Montant invalide: " + montant);
        }
        if(solde < montant) {
            return false; // Solde insuffisant — pas d'exception
        }
        solde -= montant;
        return true;
    }

    /**
     * Transfert atomique sans deadlock.
     * Technique : acquérir les verrous dans un ordre déterministe basé sur l'ID.
     * Les deux threads qui font A→B et B→A acquerront toujours dans le même ordre.
     */
    public void transferer(CompteBancaire destination, double montant) {
        // Déterminer l'ordre d'acquisition

        CompteBancaire premier = this.id.compareTo(destination.id) < 0 ? this : destination;
        CompteBancaire second = premier == this ? destination : this;

        synchronized (premier) {

             synchronized (second) {
                if(this.solde < montant) {
                    System.out.printf("[%s→%s] Solde insuffisant (%.2f < %.2f)%n",
                            this.id, destination.id, this.solde, montant);
                    return;
                }
                this.solde -= montant;
                destination.solde += montant;
                System.out.printf("[%s→%s] Transfert %.2f | Soldes : %.2f / %.2f%n",
                        this.id, destination.id, montant, this.solde, destination.solde);
             }
        }


    }

    public synchronized double getSolde() { return solde; }
    public String getId() { return id; }

    public static void main(String[] args) throws InterruptedException {
        CompteBancaire compteA = new CompteBancaire("A", 1000.0);
        CompteBancaire compteB = new CompteBancaire("B", 1000.0);
        double totalInitial = compteA.getSolde() + compteB.getSolde();
        System.out.printf("Total initial : %.2f€%n", totalInitial);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                long fin = System.currentTimeMillis() + 2000;
                while (System.currentTimeMillis() < fin) {
                    double montant = ThreadLocalRandom.current().nextDouble(10, 1000);
                    if (ThreadLocalRandom.current().nextBoolean())
                        compteA.transferer(compteB, montant);
                    else
                        compteB.transferer(compteA, montant);
                    try { Thread.sleep(10); } catch (InterruptedException e) { return; }
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        double totalFinal = compteA.getSolde() + compteB.getSolde();
        System.out.printf("Total final   : %.2f€ %s%n",
                totalFinal, Math.abs(totalFinal - totalInitial) < 0.01 ? "✓" : "❌ INCOHÉRENT !");
    }
}