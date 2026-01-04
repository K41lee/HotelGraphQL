package Impl;

import java.time.LocalDate;

public class DateSimple {
    private final int jour, mois, annee;
    public DateSimple(int jour, int mois, int annee) { this.jour = jour; this.mois = mois; this.annee = annee; }
    public int getJour() { return jour; }
    public int getMois() { return mois; }
    public int getAnnee() { return annee; }
    public LocalDate toLocalDate() { return LocalDate.of(annee, mois, jour); }
}
