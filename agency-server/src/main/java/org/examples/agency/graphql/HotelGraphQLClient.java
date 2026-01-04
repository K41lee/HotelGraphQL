package org.examples.agency.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Client GraphQL pour communiquer avec les serveurs d'hôtels.
 * Remplace l'ancien HotelGrpcClient.
 */
@Component
public class HotelGraphQLClient {

    private static final Logger log = LoggerFactory.getLogger(HotelGraphQLClient.class);

    private WebClient operaClient;
    private WebClient rivageClient;

    @PostConstruct
    public void init() {
        operaClient = WebClient.builder()
                .baseUrl("http://localhost:8082/graphql")
                .build();

        rivageClient = WebClient.builder()
                .baseUrl("http://localhost:8084/graphql")
                .build();

        log.info("[GraphQL-CLIENT] Clients GraphQL initialisés - Opera: 8082, Rivage: 8084");
    }

    /**
     * Récupère le catalogue d'un hôtel - Retourne directement la structure GraphQL
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCatalog(String hotelCode) {
        log.info("[GraphQL-CLIENT] getCatalog - hotelCode={}", hotelCode);

        String query = "query GetCatalog($hotelId: String!) {" +
                " hotelCatalog(hotelId: $hotelId) {" +
                "  hotel { id name stars description address { street city postalCode country } }" +
                "  roomTypes { id category capacity pricePerNight description images { url } }" +
                "  totalRooms" +
                " }" +
                "}";

        Map<String, Object> variables = new HashMap<>();
        variables.put("hotelId", hotelCode);

        WebClient client = getClientForHotel(hotelCode);

        try {
            Map<String, Object> response = executeQuery(client, query, variables);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                return (Map<String, Object>) data.get("hotelCatalog");
            }
        } catch (Exception e) {
            log.error("[GraphQL-CLIENT] Error getting catalog for {}: {}", hotelCode, e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * Recherche des offres disponibles - Retourne directement les offres GraphQL
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> searchOffers(String ville, LocalDate from, LocalDate to, int nb, String agencyId) {
        log.info("[GraphQL-CLIENT] searchOffers - ville={}, from={}, to={}, nb={}, agency={}",
                 ville, from, to, nb, agencyId);

        String query = "query SearchOffers($input: SearchOffersInput!) {" +
                " searchOffers(input: $input) {" +
                "  offers {" +
                "   offerId available" +
                "   hotel { id name stars address { city } }" +
                "   room { id category capacity pricePerNight images { url } }" +
                "   arrivalDate departureDate numNights" +
                "   pricePerNight totalPrice finalPrice currency" +
                "  }" +
                "  totalCount" +
                " }" +
                "}";

        Map<String, Object> input = new HashMap<>();
        input.put("city", ville);
        input.put("arrivalDate", from.toString());
        input.put("departureDate", to.toString());
        input.put("numPersons", nb);
        if (agencyId != null) {
            input.put("agency", agencyId);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        // Appeler les deux serveurs
        List<Map<String, Object>> allOffers = new ArrayList<>();

        // Opera
        try {
            log.info("[GraphQL-CLIENT] Calling Opera with query variables: {}", variables);
            Map<String, Object> response = executeQuery(operaClient, query, variables);
            log.info("[GraphQL-CLIENT] Opera raw response: {}", response);

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                Map<String, Object> searchResult = (Map<String, Object>) data.get("searchOffers");
                if (searchResult != null) {
                    List<Map<String, Object>> offers = (List<Map<String, Object>>) searchResult.get("offers");
                    if (offers != null) {
                        log.info("[GraphQL-CLIENT] Opera returned {} offers", offers.size());
                        allOffers.addAll(offers);
                    } else {
                        log.warn("[GraphQL-CLIENT] Opera searchResult.offers is null");
                    }
                } else {
                    log.warn("[GraphQL-CLIENT] Opera data.searchOffers is null");
                }
            } else {
                log.warn("[GraphQL-CLIENT] Opera response.data is null");
            }
        } catch (Exception e) {
            log.error("[GraphQL-CLIENT] Error calling Opera: {}", e.getMessage(), e);
        }

        // Rivage
        try {
            Map<String, Object> response = executeQuery(rivageClient, query, variables);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                Map<String, Object> searchResult = (Map<String, Object>) data.get("searchOffers");
                if (searchResult != null) {
                    List<Map<String, Object>> offers = (List<Map<String, Object>>) searchResult.get("offers");
                    if (offers != null) {
                        allOffers.addAll(offers);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[GraphQL-CLIENT] Error calling Rivage: {}", e.getMessage());
        }

        log.info("[GraphQL-CLIENT] searchOffers found {} offers total", allOffers.size());
        return allOffers;
    }

    /**
     * Fait une réservation - Retourne directement la confirmation GraphQL
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> makeReservation(String hotelId, String roomId, String clientName,
                                               String clientFirstName, String clientCard,
                                               LocalDate arrivalDate, LocalDate departureDate,
                                               int numPersons) {
        log.info("[GraphQL-CLIENT] makeReservation - hotel={}, room={}", hotelId, roomId);

        String mutation = "mutation MakeReservation($input: ReservationInput!) {" +
                " makeReservation(input: $input) {" +
                "  reservationId hotelId clientName status totalPrice" +
                "  createdAt confirmationCode arrivalDate departureDate numPersons" +
                " }" +
                "}";

        Map<String, Object> input = new HashMap<>();
        input.put("hotelId", hotelId);
        input.put("roomId", roomId);
        input.put("clientName", clientName);
        input.put("clientFirstName", clientFirstName);
        input.put("clientCard", clientCard);
        input.put("arrivalDate", arrivalDate.toString());
        input.put("departureDate", departureDate.toString());
        input.put("numPersons", numPersons);

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        WebClient client = getClientForHotel(hotelId);

        try {
            Map<String, Object> response = executeQuery(client, mutation, variables);
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null) {
                return (Map<String, Object>) data.get("makeReservation");
            }
        } catch (Exception e) {
            log.error("[GraphQL-CLIENT] Error making reservation: {}", e.getMessage());
            throw new RuntimeException("Failed to make reservation: " + e.getMessage());
        }

        throw new RuntimeException("Empty response from GraphQL server");
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Exécute une query/mutation GraphQL
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> executeQuery(WebClient client, String query, Map<String, Object> variables) {
        Map<String, Object> request = new HashMap<>();
        request.put("query", query);
        if (variables != null && !variables.isEmpty()) {
            request.put("variables", variables);
        }

        Map<String, Object> response = client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null) {
            throw new RuntimeException("Empty response from GraphQL server");
        }

        // Vérifier les erreurs GraphQL
        if (response.containsKey("errors")) {
            List<Map<String, Object>> errors = (List<Map<String, Object>>) response.get("errors");
            String errorMsg = errors.stream()
                    .map(e -> (String) e.get("message"))
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("GraphQL errors: " + errorMsg);
        }

        return response;
    }

    /**
     * Sélectionne le client selon l'hôtel
     */
    private WebClient getClientForHotel(String hotelCode) {
        if ("opera".equalsIgnoreCase(hotelCode)) {
            return operaClient;
        } else if ("rivage".equalsIgnoreCase(hotelCode)) {
            return rivageClient;
        } else {
            log.warn("[GraphQL-CLIENT] Unknown hotel code: {}, defaulting to opera", hotelCode);
            return operaClient;
        }
    }
}
