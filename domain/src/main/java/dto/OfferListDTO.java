package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO REST pour la liste des offres (wrapper)
 */
public class OfferListDTO {
    
    @JsonProperty("offers")
    private List<OfferDTO> offers;

    // Constructeur par défaut
    public OfferListDTO() {
        this.offers = new ArrayList<>();
    }

    public OfferListDTO(List<OfferDTO> offers) {
        this.offers = offers != null ? offers : new ArrayList<>();
    }

    // Getters et Setters
    public List<OfferDTO> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferDTO> offers) {
        this.offers = offers;
    }

    @Override
    public String toString() {
        return "OfferListDTO{offers=" + (offers != null ? offers.size() : 0) + " offres}";
    }
}

