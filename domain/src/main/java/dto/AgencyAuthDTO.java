package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO REST pour l'authentification d'une agence
 */
public class AgencyAuthDTO {
    
    @JsonProperty("agencyId")
    private String agencyId;
    
    @JsonProperty("login")
    private String login;
    
    @JsonProperty("password")
    private String password;

    // Constructeur par défaut
    public AgencyAuthDTO() {}

    public AgencyAuthDTO(String agencyId, String login, String password) {
        this.agencyId = agencyId;
        this.login = login;
        this.password = password;
    }

    // Getters et Setters
    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AgencyAuthDTO{" +
                "agencyId='" + agencyId + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}

