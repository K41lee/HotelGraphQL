package Impl;

import java.util.Objects;

/** Partenaire commercial appliquant une réduction (0.0 à 1.0) sur le prix total. */
public class Agence {
    private final String nom;
    private final double reduction; // ex: 0.10 = -10%

    public Agence(String nom, double reduction) {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Nom d'agence requis");
        if (reduction < 0.0 || reduction > 1.0) throw new IllegalArgumentException("Réduction doit être entre 0.0 et 1.0");
        this.nom = nom.trim();
        this.reduction = reduction;
    }

    public String getNom() { return nom; }
    public double getReduction() { return reduction; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agence)) return false;
        Agence a = (Agence) o;
        return nom.equalsIgnoreCase(a.nom);
    }
    @Override public int hashCode() { return Objects.hash(nom.toLowerCase()); }

    @Override public String toString() {
        return "Agence{" + "nom='" + nom + '\'' + ", reduction=" + reduction + '}';
    }
}
