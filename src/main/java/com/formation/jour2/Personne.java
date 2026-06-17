package com.formation.jour2;

import java.io.*;

public class Personne implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private transient int age; //transient veut dire que cet attribut n'est pas sérialisé

    public Personne(String nom, int age) {
        this.nom = nom;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Personne{nom='" + nom + "', age=" + age + "}";
    }

    public static void main(String[] args) {
        Personne p = new Personne("Alice", 30);

        System.out.println("Sérialisation");
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new FileOutputStream("personne.ser"))) {

            oos.writeObject(p);
            System.out.println("Objet sérialisé avec succès.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("DéSérialisation");
        try (ObjectInputStream ois =
                     new ObjectInputStream(
                             new FileInputStream("personne.ser"))) {

            Personne pClone = (Personne) ois.readObject();
            System.out.println("Objet désérialisé : " + pClone);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
