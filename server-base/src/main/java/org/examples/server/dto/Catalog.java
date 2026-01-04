package org.examples.server.dto;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CatalogDTO", namespace="http://service.hotel.examples.org/dto", propOrder={"name","cities","agencies"})
@XmlRootElement(name="CatalogDTO", namespace="http://service.hotel.examples.org/dto")
public class Catalog {
    @XmlElement(required=true)
    public String name;

    @XmlElement(name="cities", required=true)
    private Cities cities = new Cities();
    @XmlElement(name="agencies", required=true)
    private Agencies agencies = new Agencies();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Cities getCities() { return cities; }
    public Agencies getAgencies() { return agencies; }
    public void setCities(Cities c) { this.cities = c; }
    public void setAgencies(Agencies a) { this.agencies = a; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="CitiesDTO", namespace="http://service.hotel.examples.org/dto", propOrder={"city"})
    public static class Cities {
        @XmlElement(name="city")
        private List<String> city = new ArrayList<>();
        public List<String> getCity(){ return city; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="AgenciesDTO", namespace="http://service.hotel.examples.org/dto", propOrder={"agency"})
    public static class Agencies {
        @XmlElement(name="agency")
        private List<String> agency = new ArrayList<>();
        public List<String> getAgency(){ return agency; }
    }
}
