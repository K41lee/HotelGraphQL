package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO REST pour le catalogue d'un hôtel
 */
public class CatalogDTO {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("cities")
    private List<String> cities;
    
    @JsonProperty("agencies")
    private List<String> agencies;

    // Constructeur par défaut
    public CatalogDTO() {
        this.cities = new ArrayList<>();
        this.agencies = new ArrayList<>();
    }

    public CatalogDTO(String name) {
        this();
        this.name = name;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public List<String> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<String> agencies) {
        this.agencies = agencies;
    }

    @Override
    public String toString() {
        return "CatalogDTO{" +
                "name='" + name + '\'' +
                ", cities=" + cities +
                ", agencies=" + agencies +
                '}';
    }
}

