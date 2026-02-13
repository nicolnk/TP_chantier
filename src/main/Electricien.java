package main;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Electricien extends Thread {
    private final String nom;
    private final ConcurrentLinkedQueue<Piece> piecesAfaire;
    private final List<Evenement> logs;

    public Electricien(String nom, ConcurrentLinkedQueue<Piece> piecesAfaire, List<Evenement> logs) {
        this.nom = nom;
        this.piecesAfaire = piecesAfaire;
        this.logs = logs;
    }

    @Override
    public void run() {
        Piece p;
        while ((p = piecesAfaire.poll()) != null) {
            logs.add(new Evenement(p.getNom(), "COMMENCE", "Electricien"));
            System.out.println("⚡ [" + nom + "] commence l'électricité dans : " + p.getNom());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            logs.add(new Evenement(p.getNom(), "FINI", "Electricien"));
            System.out.println("✅ [" + nom + "] a fini l'électricité dans : " + p.getNom());

            p.signalerElectriciteFinie();
        }
    }
}