package main;

import java.util.concurrent.Semaphore;

public class Piece {
    private final String nom;
    private final Semaphore peutPlatrer = new Semaphore(0);

    public Piece(String nom) {
        this.nom = nom;
    }

    public String getNom() { return nom; }

    public void signalerElectriciteFinie() {
        peutPlatrer.release();
    }

    public void attendreElectricite() throws InterruptedException {
        peutPlatrer.acquire();
    }
}