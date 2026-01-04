package Impl;

import java.time.LocalDate;

public class Reservation {
    private final Chambre chambre;
    private final Client client;
    private final LocalDate debut;  // inclus
    private final LocalDate fin;    // exclus

    public Reservation(Chambre chambre, Client client, LocalDate debut, LocalDate fin) {
        this.chambre = chambre; this.client = client; this.debut = debut; this.fin = fin;
    }
    public Chambre getChambre() { return chambre; }
    public Client getClient() { return client; }
    public LocalDate getDebut() { return debut; }
    public LocalDate getFin() { return fin; }

    // Chevauchement si [d1,f1) intersecte [d2,f2)
    public static boolean chevauche(LocalDate d1, LocalDate f1, LocalDate d2, LocalDate f2) {
        return d1.isBefore(f2) && d2.isBefore(f1);
    }
}
