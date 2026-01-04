package org.examples.server.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AvailabilityRequest", namespace="http://service.hotel.examples.org/dto")
public class AvailabilityRequest {
    private String agencyId;
    private String login;
    private String password;
    private String from;
    private String to;
    private int persons;

    public String getAgencyId() { return agencyId; }
    public void setAgencyId(String agencyId) { this.agencyId = agencyId; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public int getPersons() { return persons; }
    public void setPersons(int persons) { this.persons = persons; }
}
