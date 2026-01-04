package Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataFactory {
    private final String hotelName;
    private final Set<String> reservedOffers = ConcurrentHashMap.newKeySet();

    private DataFactory(String hotelName) {
        this.hotelName = hotelName;
    }

    public static DataFactory rivage() {
        return new DataFactory("Rivage");
    }

    // Optionnel: une fabrique pour Opera si nécessaire à l'avenir
    public static DataFactory opera() { return new DataFactory("Opera"); }

    private String deterministicId(LocalDate from, LocalDate to, int persons) {
        String base = hotelName + "|" + from + "|" + to + "|" + persons;
        return UUID.nameUUIDFromBytes(base.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
    }

    public List<Offer> findOffers(AgencyCredentials creds, LocalDate from, LocalDate to, int persons) {
        List<Offer> offers = new ArrayList<>();
        LocalDate d = from;
        while (!d.isAfter(to.minusDays(1))) {
            LocalDate next = d.plusDays(1);
            String id = deterministicId(d, next, persons);
            if (!reservedOffers.contains(id)) {
                offers.add(new Offer(id, persons, d, next, 100.0));
            }
            d = d.plusDays(1);
        }
        // adapter prix si agency preferred (exemple simple)
        if (creds != null && "preferred".equals(creds.getAgencyId())) {
            List<Offer> adapted = new ArrayList<>();
            for (Offer o : offers) {
                adapted.add(new Offer(o.getId(), o.getBeds(), o.getFrom(), o.getTo(), o.getPrice() * 0.9));
            }
            return adapted;
        }
        return offers;
    }

    public ReservationResult reserve(String agencyId, String login, String password, String offerId, Client mainPerson) {
        if (offerId == null || mainPerson == null) return ReservationResult.failure("Données manquantes");
        // Marquer l'offre comme réservée si elle fait partie de notre génération déterministe
        reservedOffers.add(offerId);
        String ref = hotelName.substring(0, Math.min(4, hotelName.length())).toUpperCase() + "-" + UUID.randomUUID().toString();
        return ReservationResult.success(ref);
    }

    public String getHotelName() { return hotelName; }
}
