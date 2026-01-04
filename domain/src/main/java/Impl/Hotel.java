package Impl;

// Hotel.java
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Hotel {
    private final String nom;
    private final Adresse adresse;
    private final Categorie categorie;
    private final int nbEtoiles;
    private final List<Chambre> chambres = new ArrayList<>();
    private final List<Agence> agences = new ArrayList<>();

    public Hotel(String nom, Adresse adresse, Categorie categorie, int nbEtoiles) {
        this.nom = nom; this.adresse = adresse; this.categorie = categorie; this.nbEtoiles = nbEtoiles;
    }
    public String getNom() { return nom; }
    public Adresse getAdresse() { return adresse; }
    public Categorie getCategorie() { return categorie; }
    public int getNbEtoiles() { return nbEtoiles; }
    public List<Chambre> getChambres() { return chambres; }
    public void addChambre(Chambre c) { chambres.add(c); }

    public List<Chambre> chambresDisponibles(LocalDate debut, LocalDate fin, int nbPersonnes) {
        return chambres.stream()
                .filter(c -> c.getNbLits() >= nbPersonnes && c.isDisponible(debut, fin))
                .collect(Collectors.toList());
    }
    public List<Agence> getAgences() { return agences; }
    public void addAgence(Agence a) { if (a != null && !agences.contains(a)) agences.add(a); }

    public Optional<Agence> findAgenceByName(String nom) {
        if (nom == null) return Optional.empty();
        return agences.stream().filter(a -> a.getNom().equalsIgnoreCase(nom)).findFirst();
    }
}
