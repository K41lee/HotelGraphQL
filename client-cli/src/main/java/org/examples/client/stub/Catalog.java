
package org.examples.client.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Catalog complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Catalog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cities" type="{http://service.hotel.examples.org/}cities" minOccurs="0"/>
 *         &lt;element name="agencies" type="{http://service.hotel.examples.org/}agencies" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Catalog", propOrder = {
    "name",
    "cities",
    "agencies"
})
public class Catalog {

    protected String name;
    protected Cities cities;
    protected Agencies agencies;

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
     *     {@link Cities }
     *     
     */
    public Cities getCities() {
        return cities;
    }

    /**
     * Définit la valeur de la propriété cities.
     * 
     * @param value
     *     allowed object is
     *     {@link Cities }
     *     
     */
    public void setCities(Cities value) {
        this.cities = value;
    }

    /**
     * Obtient la valeur de la propriété agencies.
     * 
     * @return
     *     possible object is
     *     {@link Agencies }
     *     
     */
    public Agencies getAgencies() {
        return agencies;
    }

    /**
     * Définit la valeur de la propriété agencies.
     * 
     * @param value
     *     allowed object is
     *     {@link Agencies }
     *     
     */
    public void setAgencies(Agencies value) {
        this.agencies = value;
    }

}
