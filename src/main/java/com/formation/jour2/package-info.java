package com.formation.jour2;

/**
 * Exercice : Implémenter un serveur HTTP et un client HTTP minimalistes en Java
 * Objectifs
 *
 * Comprendre :
 *
 * le fonctionnement d'une connexion TCP ;
 * la structure d'une requête HTTP ;
 * la structure d'une réponse HTTP ;
 * les codes de statut HTTP ;
 * les en-têtes HTTP ;
 * la différence entre TCP et HTTP.
 * Partie 1 : Serveur HTTP minimal
 *
 * Créer un serveur qui écoute sur le port 9090.
 *
 * Lorsqu'un navigateur se connecte sur :
 *
 * http://localhost:9090
 *
 * le serveur doit :
 *
 * accepter la connexion ;
 * lire la requête HTTP ;
 * afficher la requête dans la console ;
 * renvoyer une réponse HTTP valide.
 *
 * Réponse attendue :
 *
 * HTTP/1.1 200 OK
 * Content-Type: text/html
 * Content-Length: 45
 *
 * <h1>Bonjour depuis mon serveur HTTP</h1>
 * Exemple d'affichage console
 *
 * Quand le navigateur appelle le serveur :
 *
 * GET / HTTP/1.1
 * Host: localhost:9090
 * Connection: keep-alive
 * User-Agent: Mozilla/5.0
 *
 * Le serveur affiche :
 *
 * Client connecté
 * GET / HTTP/1.1
 * Host: localhost:9090
 * Connection: keep-alive
 * ...
 *
 */