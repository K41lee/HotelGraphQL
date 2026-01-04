package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RoomDTO", namespace="http://service.hotel.examples.org/dto")
@XmlRootElement(name="RoomDTO", namespace="http://service.hotel.examples.org/dto")
public class Room {
    public int numero;
    public int nbLits;
    public int prixParNuit;
    public int getNumero(){ return numero; }
    public void setNumero(int n){ this.numero = n; }
    public int getNbLits(){ return nbLits; }
    public void setNbLits(int n){ this.nbLits = n; }
}
