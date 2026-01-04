package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO REST pour la confirmation d'une réservation
 */
public class ReservationConfirmationDTO {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("offer")
    private OfferDTO offer;

    // Constructeur par défaut
    public ReservationConfirmationDTO() {}

    public ReservationConfirmationDTO(String id, String message, boolean success) {
        this.id = id;
        this.message = message;
        this.success = success;
    }

    public ReservationConfirmationDTO(String id, String message, boolean success, OfferDTO offer) {
        this.id = id;
        this.message = message;
        this.success = success;
        this.offer = offer;
    }

    // Méthodes statiques pour faciliter la création
    public static ReservationConfirmationDTO success(String id, String message, OfferDTO offer) {
        return new ReservationConfirmationDTO(id, message, true, offer);
    }

    public static ReservationConfirmationDTO failure(String message) {
        return new ReservationConfirmationDTO(null, message, false, null);
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public OfferDTO getOffer() {
        return offer;
    }

    public void setOffer(OfferDTO offer) {
        this.offer = offer;
    }

    @Override
    public String toString() {
        return "ReservationConfirmationDTO{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

