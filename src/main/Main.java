package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) {
        List<String> noms = List.of(
                "Salon", "Cuisine", "Chambre parentale", "Chambre d'ami",
                "Salle de bain", "WC", "Bureau", "Buanderie", "Garage", "Grenier"
        );

        List<Evenement> logs = new CopyOnWriteArrayList<>();

        ConcurrentLinkedQueue<Piece> fileElectrique = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Piece> filePlatre = new ConcurrentLinkedQueue<>();

        for (String nom : noms) {
            Piece p = new Piece(nom);
            fileElectrique.add(p);
            filePlatre.add(p);
        }

        List<Thread> ouvriers = new ArrayList<>();

        ouvriers.add(new Electricien("Patrick", fileElectrique, logs));
        ouvriers.add(new Electricien("Robert", fileElectrique, logs));
        ouvriers.add(new Electricien("Bob", fileElectrique, logs));

        ouvriers.add(new Platrier("Mathis", filePlatre, logs));
        ouvriers.add(new Platrier("Melwine", filePlatre, logs));

        System.out.println("--- 🏗️ DÉBUT DU CHANTIER ---");

        ouvriers.forEach(Thread::start);

        for (Thread o : ouvriers) {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n--- 🎉 LA MAISON EST TERMINÉE ! ---");

        System.out.println("\nRésumé des activités (" + logs.size() + " événements enregistrés) :");
        logs.forEach(ev -> System.out.println(" > " + ev));
    }
}