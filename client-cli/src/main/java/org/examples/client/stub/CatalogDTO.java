
package org.examples.client.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CatalogDTO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="CatalogDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cities" type="{http://service.hotel.examples.org/dto}CitiesDTO"/>
 *         &lt;element name="agencies" type="{http://service.hotel.examples.org/dto}AgenciesDTO"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CatalogDTO", propOrder = {
    "name",
    "cities",
    "agencies"
})
public class CatalogDTO {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected CitiesDTO cities;
    @XmlElement(required = true)
    protected AgenciesDTO agencies;

    /**
     * Obtient la valeur de la propriété name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété cities.
     * 
     * @return
     *     possible object is
     *     {@link CitiesDTO }
     *     
     */
    public CitiesDTO getCities() {
        return cities;
    }

    /**
     * Définit la valeur de la propriété cities.
     * 
     * @param value
     *     allowed object is
     *     {@link CitiesDTO }
     *     
     */
    public void setCities(CitiesDTO value) {
        this.cities = value;
    }

    /**
     * Obtient la valeur de la propriété agencies.
     * 
     * @return
     *     possible object is
     *     {@link AgenciesDTO }
     *     
     */
    public AgenciesDTO getAgencies() {
        return agencies;
    }

    /**
     * Définit la valeur de la propriété agencies.
     * 
     * @param value
     *     allowed object is
     *     {@link AgenciesDTO }
     *     
     */
    public void setAgencies(AgenciesDTO value) {
        this.agencies = value;
    }

}
