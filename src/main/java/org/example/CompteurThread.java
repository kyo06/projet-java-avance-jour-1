package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CompteurThread extends Thread {

    public CompteurThread(String nom) {
        super(nom); // Définit le nom du thread
    }

    @Override
    public void run() {
        // TODO : boucle de 5 à 1
        // Afficher : "[NomThread] Compte : X"
        // Attendre 300ms entre chaque itération
        // Gérer InterruptedException correctement
        for(int i = 100000; i >= 1; i--) {
            System.out.println("Nom du thread : " + this.getName() + " - Compte : " + i);
            try {
                Thread.sleep(300);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Nom du thread : " + this.getName() + " - Interrompu à l'étape " + i);
                return;
            }
            System.out.println("Nom du thread : " + this.getName() + " - Terminé");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        // TODO : créer 3 CompteurThread avec des noms différents
        CompteurThread t1 = new CompteurThread("Alpha");
        CompteurThread t2 = new CompteurThread("Beta");
        CompteurThread t3 = new CompteurThread("Gamma");

        System.out.println(Thread.currentThread().getName());

        // Démarrer les 3 threads
        t1.start();
        t2.start();
        t3.start();
        // Attendre la fin de tous avec join()
        t1.join();
        t2.join();
        t3.join();
        // Afficher "Tous les threads ont terminé"
        System.out.println("Tous les threads ont terminés.");
    }
}