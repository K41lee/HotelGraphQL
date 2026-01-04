package Impl;

public class AgencyCredentials {
    private final String agencyId;
    private final String login;
    private final String password;

    public AgencyCredentials(String agencyId, String login, String password) {
        this.agencyId = agencyId;
        this.login = login;
        this.password = password;
    }

    public String getAgencyId() { return agencyId; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
}
