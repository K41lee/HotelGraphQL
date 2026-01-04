package org.examples.server.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chambre_id", nullable = false)
    private ChambreEntity chambre;

    @Column(nullable = false)
    private String clientNom;

    @Column(nullable = false)
    private String clientPrenom;

    @Column
    private String clientCarte;

    @Column(nullable = false)
    private LocalDate debut;

    @Column(nullable = false)
    private LocalDate fin;

    @Column
    private String reference;

    @Column
    private String agence;

    public ReservationEntity() {}

    public ReservationEntity(String clientNom, String clientPrenom, LocalDate debut, LocalDate fin) {
        this.clientNom = clientNom;
        this.clientPrenom = clientPrenom;
        this.debut = debut;
        this.fin = fin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChambreEntity getChambre() { return chambre; }
    public void setChambre(ChambreEntity chambre) { this.chambre = chambre; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getClientPrenom() { return clientPrenom; }
    public void setClientPrenom(String clientPrenom) { this.clientPrenom = clientPrenom; }

    public String getClientCarte() { return clientCarte; }
    public void setClientCarte(String clientCarte) { this.clientCarte = clientCarte; }

    public LocalDate getDebut() { return debut; }
    public void setDebut(LocalDate debut) { this.debut = debut; }

    public LocalDate getFin() { return fin; }
    public void setFin(LocalDate fin) { this.fin = fin; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getAgence() { return agence; }
    public void setAgence(String agence) { this.agence = agence; }

    public boolean chevauche(LocalDate autreDebut, LocalDate autreFin) {
        return debut.isBefore(autreFin) && autreDebut.isBefore(fin);
    }
}

