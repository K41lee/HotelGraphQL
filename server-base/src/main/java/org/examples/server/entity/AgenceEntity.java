package org.examples.server.entity;
import javax.persistence.*;
@Entity
@Table(name = "agences")
public class AgenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column
    private double reduction;
    @Column
    private String login;
    @Column
    private String password;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;
    public AgenceEntity() {}
    public AgenceEntity(String nom, double reduction) {
        this.nom = nom;
        this.reduction = reduction;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public double getReduction() { return reduction; }
    public void setReduction(double reduction) { this.reduction = reduction; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public HotelEntity getHotel() { return hotel; }
    public void setHotel(HotelEntity hotel) { this.hotel = hotel; }
}
