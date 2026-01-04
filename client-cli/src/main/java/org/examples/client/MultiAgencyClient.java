package org.examples.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * Client gérant plusieurs connexions à différentes agences
 */
public class MultiAgencyClient implements Closeable {

    private final List<AgencyTcpClient> agencies;

    public MultiAgencyClient() {
        this.agencies = new ArrayList<>();
    }

    /**
     * Ajoute une agence au pool
     */
    public void addAgency(String host, int port, String name) throws IOException {
        AgencyTcpClient client = new AgencyTcpClient(host, port, name);
        agencies.add(client);
        System.out.println("[MULTI-AGENCY] Connecté à " + name + " (port " + port + ")");
    }

    /**
     * Obtient le catalogue fusionné de toutes les agences
     */
    public String getCatalog() throws IOException {
        if (agencies.isEmpty()) {
            return "{\"status\":\"error\",\"message\":\"Aucune agence connectée\"}";
        }

        // Utiliser la première agence pour le catalogue (même données pour toutes)
        String catalog = agencies.get(0).getCatalog();

        // Construire la liste des agences disponibles
        StringBuilder agencyNames = new StringBuilder();
        for (int i = 0; i < agencies.size(); i++) {
            if (i > 0) agencyNames.append(",");
            agencyNames.append("\"").append(agencies.get(i).getAgencyName()).append(" (port ").append(agencies.get(i).getPort()).append(")\"");
        }

        // Injecter la liste dans le catalogue
        String result = catalog.replace("\"agencies\":[]", "\"availableAgencies\":[" + agencyNames + "],\"agencies\":[]");
        return result;
    }

    /**
     * Recherche dans toutes les agences et fusionne les résultats
     */
    public String searchAll(String ville, String arrivee, String depart, int nbPersonnes, String agencyId) throws IOException {
        List<String> allOffersJson = new ArrayList<>();

        for (AgencyTcpClient agency : agencies) {
            try {
                String response = agency.search(ville, arrivee, depart, nbPersonnes, agencyId);
                String status = MiniJson.getString(response, "status");

                if ("ok".equals(status)) {
                    List<String> offers = MiniJson.getStringArray(response, "offers");
                    if (offers != null) {
                        // Ajouter l'identifiant de l'agence à chaque offre
                        for (String offer : offers) {
                            // Injecter les infos d'agence dans l'offre
                            String augmentedOffer = offer.substring(0, offer.length() - 1) +
                                ",\"_agencyName\":\"" + agency.getAgencyName() + "\"" +
                                ",\"_agencyPort\":" + agency.getPort() + "}";
                            allOffersJson.add(augmentedOffer);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("[MULTI-AGENCY] Erreur avec " + agency.getAgencyName() + ": " + e.getMessage());
                // Continue avec les autres agences
            }
        }

        // Construire la réponse fusionnée
        StringBuilder offersArray = new StringBuilder();
        for (int i = 0; i < allOffersJson.size(); i++) {
            if (i > 0) offersArray.append(",");
            offersArray.append(allOffersJson.get(i));
        }

        String result = "{\"status\":\"ok\",\"data\":{" +
            "\"offers\":[" + offersArray + "]," +
            "\"totalAgencies\":" + agencies.size() + "," +
            "\"totalOffers\":" + allOffersJson.size() +
            "}}";

        return result;
    }

    /**
     * Fait une réservation via l'agence appropriée
     */
    public String reserve(int agencyPort, String hotelCode, String offerId, String agencyId,
                         String nom, String prenom, String carte, String arrivee, String depart) throws IOException {
        // Trouver l'agence correspondante
        for (AgencyTcpClient agency : agencies) {
            if (agency.getPort() == agencyPort) {
                return agency.reserve(hotelCode, offerId, agencyId, nom, prenom, carte, arrivee, depart);
            }
        }

        return "{\"status\":\"error\",\"message\":\"Agence non trouvée (port " + agencyPort + ")\"}";
    }

    @Override
    public void close() {
        for (AgencyTcpClient agency : agencies) {
            try {
                agency.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        agencies.clear();
    }

    public int getAgencyCount() {
        return agencies.size();
    }
}

