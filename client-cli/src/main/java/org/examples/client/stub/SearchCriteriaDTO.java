
package org.examples.client.stub;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SearchCriteriaDTO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="SearchCriteriaDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ville" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arrivee" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" minOccurs="0"/>
 *         &lt;element name="depart" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" minOccurs="0"/>
 *         &lt;element name="prixMin" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="prixMax" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="categorie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nbEtoiles" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nbPersonnes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="agence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchCriteriaDTO", propOrder = {
    "ville",
    "arrivee",
    "depart",
    "prixMin",
    "prixMax",
    "categorie",
    "nbEtoiles",
    "nbPersonnes",
    "agence"
})
public class SearchCriteriaDTO {

    protected String ville;
    protected Object arrivee;
    protected Object depart;
    protected Integer prixMin;
    protected Integer prixMax;
    protected String categorie;
    protected Integer nbEtoiles;
    protected int nbPersonnes;
    protected String agence;

    /**
     * Obtient la valeur de la propriété ville.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVille() {
        return ville;
    }

    /**
     * Définit la valeur de la propriété ville.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVille(String value) {
        this.ville = value;
    }

    /**
     * Obtient la valeur de la propriété arrivee.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getArrivee() {
        return arrivee;
    }

    /**
     * Définit la valeur de la propriété arrivee.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setArrivee(Object value) {
        this.arrivee = value;
    }

    /**
     * Obtient la valeur de la propriété depart.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDepart() {
        return depart;
    }

    /**
     * Définit la valeur de la propriété depart.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDepart(Object value) {
        this.depart = value;
    }

    /**
     * Obtient la valeur de la propriété prixMin.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrixMin() {
        return prixMin;
    }

    /**
     * Définit la valeur de la propriété prixMin.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrixMin(Integer value) {
        this.prixMin = value;
    }

    /**
     * Obtient la valeur de la propriété prixMax.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrixMax() {
        return prixMax;
    }

    /**
     * Définit la valeur de la propriété prixMax.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrixMax(Integer value) {
        this.prixMax = value;
    }

    /**
     * Obtient la valeur de la propriété categorie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * Définit la valeur de la propriété categorie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategorie(String value) {
        this.categorie = value;
    }

    /**
     * Obtient la valeur de la propriété nbEtoiles.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNbEtoiles() {
        return nbEtoiles;
    }

    /**
     * Définit la valeur de la propriété nbEtoiles.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNbEtoiles(Integer value) {
        this.nbEtoiles = value;
    }

    /**
     * Obtient la valeur de la propriété nbPersonnes.
     * 
     */
    public int getNbPersonnes() {
        return nbPersonnes;
    }

    /**
     * Définit la valeur de la propriété nbPersonnes.
     * 
     */
    public void setNbPersonnes(int value) {
        this.nbPersonnes = value;
    }

    /**
     * Obtient la valeur de la propriété agence.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgence() {
        return agence;
    }

    /**
     * Définit la valeur de la propriété agence.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgence(String value) {
        this.agence = value;
    }

}
