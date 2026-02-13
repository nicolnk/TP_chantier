package main;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Platrier extends Thread {
    private final String nom;
    private final ConcurrentLinkedQueue<Piece> piecesAfaire;
    private final List<Evenement> logs;

    public Platrier(String nom, ConcurrentLinkedQueue<Piece> piecesAfaire, List<Evenement> logs) {
        this.nom = nom;
        this.piecesAfaire = piecesAfaire;
        this.logs = logs;
    }

    @Override
    public void run() {
        Piece p;
        while ((p = piecesAfaire.poll()) != null) {
            try {
                p.attendreElectricite();

                logs.add(new Evenement(p.getNom(), "COMMENCE", "Platrier"));
                System.out.println("🧱 [" + nom + "] commence le plâtre dans : " + p.getNom());

                Thread.sleep(500);

                logs.add(new Evenement(p.getNom(), "FINI", "Platrier"));
                System.out.println("🏠 [" + nom + "] a fini le plâtre dans : " + p.getNom());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}