package org.example;

import java.util.List;

public class JokerExample {

    public static void afficher(List<String> liste) {
        for (String s : liste) {
            System.out.println(s);
        }
    }

    public static void afficher2(List<?> liste) {
        for (Object obj : liste) {
            System.out.println(obj);
        }
    }

    public static void afficher3(List<Object> liste) {
        for (Object obj : liste) {
            System.out.println(obj);
        }
    }

    public static void main(String[] args) {
        List<Integer> entiers = List.of(1, 2, 3);
        // afficher(entiers); // Erreur de compilation
        afficher2(entiers);
        afficher3(entiers);

    }

}
