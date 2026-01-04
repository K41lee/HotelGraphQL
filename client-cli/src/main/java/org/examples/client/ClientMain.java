package org.examples.client;

import org.examples.client.stub.*;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ClientMain {

    private static XMLGregorianCalendar toXgc(LocalDate d) throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                d.getYear(), d.getMonthValue(), d.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED
        );
    }

    public static void main(String[] args) throws Exception {
        String wsdlUrl = System.getProperty("wsdl.url", "http://localhost:8081/hotel-rivage/hotel?wsdl");
        System.out.println("WSDL : " + wsdlUrl);

        HotelService_Service svc = new HotelService_Service(new URL(wsdlUrl));
        HotelService port = svc.getHotelServiceImplPort();
        Scanner in = new Scanner(System.in);

        String useAgency = System.getProperty("agency.tcp.enabled", "true");
        if ("true".equalsIgnoreCase(useAgency)) {
            String ahost = System.getProperty("agency.tcp.host", "localhost");
            int aport = Integer.parseInt(System.getProperty("agency.tcp.port", "7070"));
            try (AgencyTcpClient agency = new AgencyTcpClient(ahost, aport, "Agence")) {
                // Catalogue via agence
                String catJson = agency.getCatalog();
                String agencyName = MiniJson.getString(catJson, "name");
                System.out.println("=== CATALOGUE (via Agence) ===");
                System.out.println("Nom agence: " + (agencyName != null ? agencyName : "(inconnu)"));
                List<String> cityList = MiniJson.getStringArray(catJson, "cities");
                if (cityList != null && !cityList.isEmpty()) {
                    System.out.println("Villes disponibles:");
                    for (int i=0;i<cityList.size();i++) System.out.println("  " + (i+1) + ") " + cityList.get(i));
                } else {
                    System.out.println("(Aucune ville transmise par l'agence)");
                }
                List<String> agenciesList = MiniJson.getStringArray(catJson, "agencies");
                if (agenciesList != null && !agenciesList.isEmpty()) {
                    System.out.println("Agences partenaires: " + agenciesList);
                }

                boolean again = true;
                while (again) {
                    System.out.println("=== CLIENT (via Agence) ===");

                    String ville;
                    while (true) {
                        if (cityList != null && !cityList.isEmpty()) {
                            System.out.print("Ville (numéro ou texte, '?' pour lister) : ");
                            String s = in.nextLine().trim();
                            if (s.equals("?")) {
                                for (int i=0;i<cityList.size();i++) System.out.println("  " + (i+1) + ") " + cityList.get(i));
                                continue;
                            }
                            // choix numérique
                            try {
                                int ix = Integer.parseInt(s) - 1;
                                if (ix >= 0 && ix < cityList.size()) { ville = cityList.get(ix); break; }
                            } catch (NumberFormatException ignore) {}
                            // tentative de match texte normalisé
                            if (!s.isEmpty()) {
                                String ns = normalizeCity(s);
                                String matched = null;
                                for (String c : cityList) { if (normalizeCity(c).equals(ns)) { matched = c; break; } }
                                if (matched == null) { for (String c : cityList) { if (normalizeCity(c).startsWith(ns)) { matched = c; break; } } }
                                if (matched != null) { ville = matched; break; }
                                System.out.println("Ville inconnue. Tapez '?' pour lister.");
                                continue;
                            }
                            System.out.println("Requis.");
                        } else {
                            System.out.print("Ville (ex: Sète/Montpellier) : ");
                            String s = in.nextLine().trim();
                            if (!s.isEmpty()) { ville = s; break; }
                            System.out.println("Requis.");
                        }
                    }

                    LocalDate dArr;
                    while (true) {
                        System.out.print("Arrivée (YYYY-MM-DD) : ");
                        String s = in.nextLine().trim();
                        try { dArr = LocalDate.parse(s); break; }
                        catch (DateTimeParseException e) { System.out.println("Format attendu YYYY-MM-DD."); }
                    }
                    LocalDate dDep;
                    while (true) {
                        System.out.print("Départ  (YYYY-MM-DD) : ");
                        String s = in.nextLine().trim();
                        try { dDep = LocalDate.parse(s); if (!dDep.isAfter(dArr)) { System.out.println("Départ doit être après l'arrivée."); continue; } break; }
                        catch (DateTimeParseException e) { System.out.println("Format attendu YYYY-MM-DD."); }
                    }
                    int nbPers;
                    while (true) {
                        System.out.print("Nb personnes : ");
                        String s = in.nextLine().trim();
                        try { nbPers = Integer.parseInt(s); if (nbPers < 1) { System.out.println(">=1"); continue; } break; }
                        catch (NumberFormatException e) { System.out.println("Entier attendu."); }
                    }
                    // NE PLUS DEMANDER l'agence en mode agence; l’agence locale gère tout.
                    String srJson = agency.search(ville, dArr.toString(), dDep.toString(), nbPers, "");
                    if (srJson == null || srJson.isEmpty()) {
                        System.out.println("(Agence) aucune réponse, nouvelle tentative...");
                        srJson = agency.search(ville, dArr.toString(), dDep.toString(), nbPers, "");
                        if (srJson == null || srJson.isEmpty()) {
                            System.out.println("Agence indisponible pour la recherche. Réessayez plus tard.");
                            again = askAgain(in);
                            continue;
                        }
                    }
                    List<String> lines = new ArrayList<>();
                    List<String> offerIds = new ArrayList<>();
                    List<String> hotelCodes = new ArrayList<>();
                    List<String> imageUrls = new ArrayList<>();
                    parseOffersForDisplay(srJson, lines, offerIds, hotelCodes, imageUrls);
                    if (lines.isEmpty()) {
                        // Informer l’utilisateur de la réponse vide
                        String arr = MiniJson.getArray(srJson, "offers");
                        if (arr == null) System.out.println("Agence: réponse reçue mais sans champ 'offers'.");
                        else System.out.println("Aucune offre renvoyée par l'agence.");
                        again = askAgain(in);
                        continue;
                    }
                    System.out.println("\nOffres :");
                    for (int i=0;i<lines.size();i++) System.out.println((i+1)+") "+lines.get(i));
                    System.out.println("(Tapez '?' pour voir les images des chambres)");
                    int idx;
                    while (true) {
                        System.out.print("\nChoisissez une offre [1-" + lines.size() + "] ou '?' pour images : ");
                        String s = in.nextLine().trim();
                        if (s.equals("?")) { interactiveImageViewer(in, lines, imageUrls); continue; }
                        try { idx = Integer.parseInt(s) - 1; if (idx < 0 || idx >= lines.size()) { System.out.println("Indice invalide."); continue; } break; }
                        catch (NumberFormatException e) { System.out.println("Entier attendu ou '?' pour mode images."); }
                    }
                    String chosenOfferId = offerIds.get(idx);
                    String chosenHotelCode = hotelCodes.get(idx);

                    System.out.println("\n=== Réservation ===");
                    String nom;
                    while (true) { System.out.print("Nom : "); nom = in.nextLine().trim(); if (!nom.isEmpty()) break; System.out.println("Requis."); }
                    String prenom;
                    while (true) { System.out.print("Prénom : "); prenom = in.nextLine().trim(); if (!prenom.isEmpty()) break; System.out.println("Requis."); }
                    String carte;
                    while (true) { System.out.print("Carte (16 chiffres) : "); carte = in.nextLine().trim(); String n = carte.replaceAll("[ -]", ""); if (n.matches("\\d{16}")) break; System.out.println("Invalide."); }

                    // Passer agencyId vide côté agence, et transmettre les dates
                    String rj = agency.reserve(chosenHotelCode, chosenOfferId, "", nom, prenom, carte, dArr.toString(), dDep.toString());
                    System.out.println(rj);
                    again = askAgain(in);
                }
                return;
            }
        }

        boolean again = true;
        while (again) {
            CatalogDTO cat = port.getCatalog();
            System.out.println("=== CATALOGUE ===");
            System.out.println("Nom hôtel: " + (cat.getName() != null ? cat.getName() : "(inconnu)"));
            // Affichage simple des villes/agences si exposées par les stubs
            try {
                CitiesDTO cities = cat.getCities();
                if (cities != null && cities.getCity() != null && !cities.getCity().isEmpty()) {
                    System.out.println("Villes: " + cities.getCity());
                }
            } catch (Throwable ignore) {}
            try {
                AgenciesDTO agencies = cat.getAgencies();
                if (agencies != null && agencies.getAgency() != null && !agencies.getAgency().isEmpty()) {
                    System.out.println("Agences: " + agencies.getAgency());
                }
            } catch (Throwable ignore) {}

            System.out.println("=== CLIENT SOAP ===");

            String ville;
            while (true) {
                System.out.print("Ville (ex: Sète/Montpellier) : ");
                ville = in.nextLine().trim();
                if (!ville.isEmpty()) break;
                System.out.println("Requis.");
            }

            LocalDate dArr;
            while (true) {
                System.out.print("Arrivée (YYYY-MM-DD) : ");
                String s = in.nextLine().trim();
                try { dArr = LocalDate.parse(s); break; }
                catch (DateTimeParseException e) { System.out.println("Format attendu YYYY-MM-DD."); }
            }
            LocalDate dDep;
            while (true) {
                System.out.print("Départ  (YYYY-MM-DD) : ");
                String s = in.nextLine().trim();
                try { dDep = LocalDate.parse(s); if (!dDep.isAfter(dArr)) { System.out.println("Départ doit être après l'arrivée."); continue; } break; }
                catch (DateTimeParseException e) { System.out.println("Format attendu YYYY-MM-DD."); }
            }
            int nbPers;
            while (true) {
                System.out.print("Nb personnes : ");
                String s = in.nextLine().trim();
                try { nbPers = Integer.parseInt(s); if (nbPers < 1) { System.out.println(">=1"); continue; } break; }
                catch (NumberFormatException e) { System.out.println("Entier attendu."); }
            }
            System.out.print("Agence (vide=aucune) : ");
            String agence = in.nextLine().trim();
            if (agence.isEmpty()) agence = null;

            SearchCriteriaDTO criteria = new SearchCriteriaDTO();
            criteria.setVille(ville);
            criteria.setArrivee(toXgc(dArr));
            criteria.setDepart(toXgc(dDep));
            criteria.setNbPersonnes(nbPers);
            criteria.setAgence(agence);

            SearchOffersResponseDTO sr = port.searchOffers(criteria);
            OfferListDTO list = (sr != null) ? sr.getOffers() : null;
            List<OfferDTO> offers = (list != null) ? list.getOffers() : Collections.emptyList();
            if (offers.isEmpty()) {
                System.out.println("Aucune offre.");
                again = askAgain(in);
                continue;
            }
            System.out.println("\nOffres :");
            for (int i = 0; i < offers.size(); i++) {
                OfferDTO o = offers.get(i);
                String name = (o.getHotelName()!=null)? o.getHotelName() : "(inconnu)";
                String catStr = (o.getCategorie()!=null)? o.getCategorie() : "(n/c)";
                int stars = o.getNbEtoiles();
                int price = o.getPrixTotal();
                AddressDTO a = o.getAddress();
                String addr = (a!=null)? (String.format("%s %s, %s (%s)",
                        a.getNumero(),
                        a.getRue()!=null? a.getRue() : "",
                        a.getVille()!=null? a.getVille() : "",
                        a.getPays()!=null? a.getPays() : "")) : "(adresse n/c)";
                System.out.printf("%d) %s | %d★ %s | %d € | %s%s%n", i+1, name, stars, catStr, price, addr,
                        offerHasImage(o)? " [img disponible]" : "");
            }
            System.out.println("(Tapez '?' pour voir les images des chambres)");
            int idx;
            while (true) {
                System.out.print("\nChoisissez une offre [1-" + offers.size() + "] ou '?' pour images : ");
                String s = in.nextLine().trim();
                if (s.equals("?")) { interactiveImageViewerSOAP(in, offers); continue; }
                try { idx = Integer.parseInt(s) - 1; if (idx < 0 || idx >= offers.size()) { System.out.println("Indice invalide."); continue; } break; }
                catch (NumberFormatException e) { System.out.println("Entier attendu ou '?' pour mode images."); }
            }
            OfferDTO chosen = offers.get(idx);

            System.out.println("\n=== Réservation ===");
            String nom;
            while (true) { System.out.print("Nom : "); nom = in.nextLine().trim(); if (!nom.isEmpty()) break; System.out.println("Requis."); }
            String prenom;
            while (true) { System.out.print("Prénom : "); prenom = in.nextLine().trim(); if (!prenom.isEmpty()) break; System.out.println("Requis."); }
            String carte;
            while (true) { System.out.print("Carte (16 chiffres) : "); carte = in.nextLine().trim(); String n = carte.replaceAll("[ -]", ""); if (n.matches("\\d{16}")) break; System.out.println("Invalide."); }

            ReservationRequestDTO rq = new ReservationRequestDTO();
            // transmettre l'identifiant d'offre si présent pour un WS2 robuste
            if (chosen.getOfferId()!=null) rq.setOfferId(chosen.getOfferId());
            rq.setHotelName(chosen.getHotelName());
            rq.setRoomNumber((chosen.getRoom()!=null)? chosen.getRoom().getNumero() : 0);
            rq.setArrivee(toXgc(dArr));
            rq.setDepart(toXgc(dDep));
            rq.setNom(nom);
            rq.setPrenom(prenom);
            rq.setCarte(carte);
            if (agence != null) {
                ObjectFactory of = new ObjectFactory();
                JAXBElement<String> agenceEl = of.createReservationRequestDTOAgence(agence);
                rq.setAgence(agenceEl);
            }

            try {
                ReservationConfirmationDTO conf = port.makeReservation(rq);
                System.out.println("\n" + (conf.getMessage()!=null? conf.getMessage() : "(sans message)") + " — id=" + conf.getId());
                OfferDTO booked = conf.getOffer();
                if (booked != null) {
                    String bname = booked.getHotelName()!=null? booked.getHotelName() : "(inconnu)";
                    int bnum = (booked.getRoom()!=null)? booked.getRoom().getNumero() : 0;
                    int bprice = booked.getPrixTotal();
                    System.out.printf("Réservé: %s, ch.%d, total=%d €%n", bname, bnum, bprice);
                }
            } catch (ServiceFault_Exception sf) {
                System.out.println("Échec réservation : " + sf.getMessage());
            }
            again = askAgain(in);
        }
    }

    private static boolean askAgain(Scanner in) {
        while (true) {
            System.out.print("\nNouvelle recherche ? (o/n) : ");
            String s = in.nextLine().trim().toLowerCase(Locale.ROOT);
            if (s.equals("o") || s.equals("y")) return true;
            if (s.equals("n") || s.equals("q") || s.equals("non")) return false;
            System.out.println("Réponse attendue: o/n");
        }
    }

    // helpers JSON très simples pour extraire ce qu'il faut
    static class MiniJson {
        static String getString(String json, String key) {
            String pat = "\""+key+"\":\""; int i=json.indexOf(pat); if (i<0) return null; i+=pat.length(); int j=json.indexOf('"', i); if (j<0) return null; return json.substring(i, j);
        }
        static Integer getInt(String json, String key) {
            String pat = "\""+key+"\":"; int i=json.indexOf(pat); if (i<0) return null; i+=pat.length(); int j=i; while (j<json.length() && "0123456789".indexOf(json.charAt(j))>=0) j++; try { return Integer.parseInt(json.substring(i,j)); } catch(Exception e){ return null; }
        }
        static String getArray(String json, String key) {
            String pat = "\""+key+"\":["; int i=json.indexOf(pat); if (i<0) return null; i+=pat.length(); int j=json.indexOf(']', i); if (j<0) return null; return json.substring(i, j);
        }
        static List<String> getStringArray(String json, String key) {
            String arr = getArray(json, key);
            if (arr == null) return Collections.emptyList();
            List<String> out = new ArrayList<>();
            int idx = 0;
            while (true) {
                int q1 = arr.indexOf('"', idx); if (q1<0) break; int q2 = arr.indexOf('"', q1+1); if (q2<0) break; out.add(arr.substring(q1+1, q2)); idx = q2+1;
            }
            return out;
        }
    }

    private static String normalizeCity(String s) {
        if (s == null) return "";
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        n = n.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    static void parseOffersForDisplay(String json, List<String> lines, List<String> offerIds, List<String> hotelCodes, List<String> imageUrls) {
        if (json == null || json.isEmpty()) return;
        int idx = 0;
        while (true) {
            int hi = json.indexOf("\"hotelName\":\"", idx); if (hi<0) break; hi += "\"hotelName\":\"".length(); int hj = json.indexOf('"', hi); if (hj<0) break; String name = json.substring(hi, hj);
            Integer stars = MiniJson.getInt(json.substring(hj), "nbEtoiles"); if (stars==null) stars=0;
            String cat = MiniJson.getString(json.substring(hj), "categorie"); if (cat==null) cat="(n/c)";
            Integer price = MiniJson.getInt(json.substring(hj), "prixTotal"); if (price==null) price=0;
            String city = MiniJson.getString(json.substring(hj), "ville"); if (city==null) city="";
            String pays = MiniJson.getString(json.substring(hj), "pays"); if (pays==null) pays="";
            String rue = MiniJson.getString(json.substring(hj), "rue"); if (rue==null) rue="";
            Integer num = MiniJson.getInt(json.substring(hj), "numero"); if (num==null) num=0;
            String offerId = MiniJson.getString(json.substring(hj), "offerId");
            String hotelCode = MiniJson.getString(json.substring(hj), "hotelCode");
            String imageUrl = MiniJson.getString(json.substring(hj), "imageUrl");
            String line = String.format("%s | %d★ %s | %d € | %s %s, %s (%s)%s",
                    name, stars, cat, price, num, rue, city, pays,
                    (imageUrl!=null && !imageUrl.isEmpty()? " [img disponible]" : ""));
            lines.add(line); offerIds.add(offerId!=null?offerId:"" ); hotelCodes.add(hotelCode!=null?hotelCode:"" ); imageUrls.add(imageUrl!=null?imageUrl:"");
            idx = hj + 1;
        }
    }

    private static void interactiveImageViewer(Scanner in, List<String> lines, List<String> imageUrls) {
        System.out.println("\n=== MODE IMAGES ===");
        System.out.println("Tapez le numéro d'une offre pour afficher son image, '?' pour revenir, 'q' pour quitter le mode images.");
        while (true) {
            for (int i=0;i<lines.size();i++) {
                String hasImg = (imageUrls.get(i)!=null && !imageUrls.get(i).isEmpty())? "(img)" : "(pas d'image)";
                System.out.println((i+1)+") "+lines.get(i)+" "+hasImg);
            }
            System.out.print("Choix image ('?', 'q', numéro) : ");
            String s = in.nextLine().trim();
            if (s.equals("?")) {
                System.out.println("Retour à la sélection des offres.");
                return;
            }
            if (s.equalsIgnoreCase("q")) {
                System.out.println("Fin du mode images.");
                return;
            }
            try {
                int ix = Integer.parseInt(s) - 1;
                if (ix < 0 || ix >= imageUrls.size()) { System.out.println("Indice invalide."); continue; }
                String url = imageUrls.get(ix);
                if (url == null || url.isEmpty()) { System.out.println("Pas d'image pour cette offre."); continue; }
                showImage(url, ix+1);
            } catch (NumberFormatException e) {
                System.out.println("Entrée non reconnue.");
            }
        }
    }

    private static void showImage(String dataUrl, int index) {
        if (!dataUrl.startsWith("data:image")) {
            System.out.println("URL image non supportée: " + dataUrl);
            return;
        }
        int comma = dataUrl.indexOf(",");
        if (comma < 0) { System.out.println("Format data URL invalide."); return; }
        String meta = dataUrl.substring(5, comma); // image/png;base64
        boolean b64 = meta.contains("base64");
        String b64data = dataUrl.substring(comma+1);
        try {
            byte[] bytes = b64? Base64.getDecoder().decode(b64data) : b64data.getBytes();
            java.nio.file.Path p = java.nio.file.Files.createTempFile("room-"+index+"-", ".png");
            java.nio.file.Files.write(p, bytes);
            System.out.println("Image sauvegardée: " + p + " ("+bytes.length+" octets)");
            String autoOpen = System.getProperty("client.image.open", "true");
            if ("true".equalsIgnoreCase(autoOpen)) {
                try {
                    new ProcessBuilder("xdg-open", p.toString()).start();
                    System.out.println("Ouverture de l'image dans le visualiseur système...");
                } catch (Exception e) {
                    System.out.println("Impossible d'ouvrir automatiquement: " + e.getMessage());
                }
            } else {
                System.out.println("Ouverture automatique désactivée (client.image.open=false).");
            }
        } catch (Exception e) {
            System.out.println("Erreur génération image: " + e.getMessage());
        }
    }

    private static boolean offerHasImage(OfferDTO o) {
        try { java.lang.reflect.Field f = o.getClass().getDeclaredField("imageUrl"); f.setAccessible(true); Object v = f.get(o); return v instanceof String && !((String)v).isEmpty(); } catch(Exception e){ return false; }
    }
    private static String getOfferImage(OfferDTO o) {
        try { java.lang.reflect.Field f = o.getClass().getDeclaredField("imageUrl"); f.setAccessible(true); Object v = f.get(o); return v instanceof String ? (String)v : null; } catch(Exception e){ return null; }
    }
    private static void interactiveImageViewerSOAP(Scanner in, List<OfferDTO> offers) {
        System.out.println("\n=== MODE IMAGES (SOAP direct) ===");
        System.out.println("Tapez le numéro pour afficher l'image, '?' pour revenir, 'q' pour quitter.");
        while (true) {
            for (int i=0;i<offers.size();i++) {
                System.out.println((i+1)+") "+(offers.get(i).getHotelName()!=null?offers.get(i).getHotelName():"(inconnu)")+" room="+roomNum(offers.get(i))+" " + (offerHasImage(offers.get(i))?"(img)":"(pas d'image)"));
            }
            System.out.print("Choix image ('?', 'q', numéro) : ");
            String s = in.nextLine().trim();
            if (s.equals("?")) { System.out.println("Retour à la sélection des offres."); return; }
            if (s.equalsIgnoreCase("q")) { System.out.println("Fin du mode images."); return; }
            try {
                int ix = Integer.parseInt(s)-1; if (ix<0 || ix>=offers.size()) { System.out.println("Indice invalide."); continue; }
                String img = getOfferImage(offers.get(ix));
                if (img==null || img.isEmpty()) { System.out.println("Pas d'image."); continue; }
                showImage(img, ix+1);
            } catch(NumberFormatException e){ System.out.println("Entrée non reconnue."); }
        }
    }
    private static int roomNum(OfferDTO o){ try { return (o.getRoom()!=null)? o.getRoom().getNumero() : -1; } catch(Exception e){ return -1; } }
}
