package org.examples.server.entity;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "hotels")
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column
    private String ville;
    @Column
    private String rue;
    @Column
    private String numero;
    @Column
    private String pays;
    @Column
    private String categorie;
    @Column
    private int nbEtoiles;
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChambreEntity> chambres = new ArrayList<>();
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgenceEntity> agences = new ArrayList<>();
    public HotelEntity() {}
    public HotelEntity(String nom, String ville, String rue, String numero, String pays, String categorie, int nbEtoiles) {
        this.nom = nom;
        this.ville = ville;
        this.rue = rue;
        this.numero = numero;
        this.pays = pays;
        this.categorie = categorie;
        this.nbEtoiles = nbEtoiles;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public int getNbEtoiles() { return nbEtoiles; }
    public void setNbEtoiles(int nbEtoiles) { this.nbEtoiles = nbEtoiles; }
    public List<ChambreEntity> getChambres() { return chambres; }
    public void setChambres(List<ChambreEntity> chambres) { this.chambres = chambres; }
    public List<AgenceEntity> getAgences() { return agences; }
    public void setAgences(List<AgenceEntity> agences) { this.agences = agences; }
    public void addChambre(ChambreEntity chambre) {
        chambres.add(chambre);
        chambre.setHotel(this);
    }
    public void addAgence(AgenceEntity agence) {
        agences.add(agence);
        agence.setHotel(this);
    }
}
