package test;

import main.Electricien;
import main.Evenement;
import main.Piece;
import main.Platrier;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

class ChantierTest {

    private List<Evenement> logs;
    private ConcurrentLinkedQueue<Piece> fileElec;
    private ConcurrentLinkedQueue<Piece> filePlatre;

    @BeforeEach
    void setUp() {
        logs = new CopyOnWriteArrayList<>();
        fileElec = new ConcurrentLinkedQueue<>();
        filePlatre = new ConcurrentLinkedQueue<>();
    }

    @Test
    @DisplayName("🧪 Test de base : Séquence logique par pièce")
    void testSynchronisationBasique() throws InterruptedException {
        Piece salon = new Piece("Salon");
        fileElec.add(salon);
        filePlatre.add(salon);

        Electricien e = new Electricien("Patrick", fileElec, logs);
        Platrier p = new Platrier("Mathis", filePlatre, logs);

        e.start();
        p.start();
        e.join(2000);
        p.join(2000);

        verifierOrdrePourPiece("Salon");
    }

    @RepeatedTest(5)
    @DisplayName("🧪 Multi-pièces : 5 pièces, 1 ouvrier de chaque")
    void testPlusieursPieces() throws InterruptedException {
        List<String> noms = List.of("P1", "P2", "P3", "P4", "P5");
        for (String n : noms) {
            Piece p = new Piece(n);
            fileElec.add(p);
            filePlatre.add(p);
        }

        Electricien e = new Electricien("Elec-1", fileElec, logs);
        Platrier pl = new Platrier("Plat-1", filePlatre, logs);

        e.start();
        pl.start();
        e.join(5000);
        pl.join(5000);

        for (String n : noms) {
            verifierOrdrePourPiece(n);
        }
    }

    @Test
    @DisplayName("🧪 Stress Test : 3 Électriciens vs 2 Plâtriers")
    void testConcurrenceIntense() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Piece p = new Piece("Pièce " + i);
            fileElec.add(p);
            filePlatre.add(p);
        }

        List<Thread> ouvriers = List.of(
                new Electricien("E1", fileElec, logs),
                new Electricien("E2", fileElec, logs),
                new Electricien("E3", fileElec, logs),
                new Platrier("P1", filePlatre, logs),
                new Platrier("P2", filePlatre, logs)
        );

        ouvriers.forEach(Thread::start);
        for (Thread t : ouvriers) t.join(10000);

        assertEquals(20, logs.stream().filter(ev -> ev.action().equals("FINI")).count(),
                "Toutes les étapes de fin n'ont pas été enregistrées");
    }

    private void verifierOrdrePourPiece(String nomPiece) {
        int indexFinElec = -1;
        int indexDebutPlatre = -1;

        for (int i = 0; i < logs.size(); i++) {
            Evenement ev = logs.get(i);
            if (ev.piece().equalsIgnoreCase(nomPiece.trim())) {
                if (ev.profession().equals("Electricien") && ev.action().equals("FINI")) {
                    indexFinElec = i;
                }
                if (ev.profession().equals("Platrier") && ev.action().equals("COMMENCE")) {
                    indexDebutPlatre = i;
                }
            }
        }

        assertNotEquals(-1, indexFinElec, "L'électricien n'a pas enregistré la FIN du travail pour " + nomPiece);
        assertNotEquals(-1, indexDebutPlatre, "Le plâtrier n'a pas enregistré le DÉBUT du travail pour " + nomPiece);
        assertTrue(indexFinElec < indexDebutPlatre,
                "ERREUR : Le plâtrier a commencé " + nomPiece + " avant que l'électricien ne finisse !");
    }
}