package org.example;

/***

 Ex 2.1 — Observer une race condition
 Objectif : Reproduire et comprendre une race condition, puis la corriger.
 Durée estimée : 15 min
 Compétence : Race condition, synchronized, AtomicInteger

 Énoncé
 Créez un compteur partagé CompteurPartage avec un entier valeur. Lancez 10 threads qui incrémentent chacun ce compteur 1000 fois.

 Étape 1 : Implémentez sans synchronisation → observez le résultat incorrect (différent de 10 000).
 Étape 2 : Corrigez avec synchronized.
 Étape 3 : Corrigez avec AtomicInteger et comparez les performances.

 Code de démarrage
 import java.util.concurrent.atomic.AtomicInteger;

 public class CompteurPartage {

 // Version 1 : non thread-safe
 private int valeurNonSafe = 0;

 // Version 2 : synchronized
 private int valeurSync = 0;

 // Version 3 : atomique
 private AtomicInteger valeurAtomic = new AtomicInteger(0);

 public void incrementerNonSafe() {
 valeurNonSafe++; // NE PAS CORRIGER CETTE VERSION
 }

 public synchronized void incrementerSync() {
 // TODO
 }

 public void incrementerAtomic() {
 // TODO
 }

 public static void main(String[] args) throws InterruptedException {
 final int NB_THREADS = 10;
 final int NB_INCREMENTS = 1000;
 CompteurPartage compteur = new CompteurPartage();

 // TODO : lancer 10 threads qui appellent chaque méthode 1000 fois
 // TODO : attendre la fin de tous les threads
 // TODO : afficher les 3 résultats et comparer avec 10 000

 // TODO : mesurer et comparer les temps d'exécution des 3 versions
 }
 }
 Résultat attendu
 Version non-safe  : 9 743  (différent à chaque fois, < 10 000)
 Version sync      : 10 000 ✓ (temps : ~45ms)
 Version atomique  : 10 000 ✓ (temps : ~12ms)

 */