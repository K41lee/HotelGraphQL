package org.examples.hotel.grpc.examples;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import org.examples.hotel.grpc.*;
import org.examples.hotel.grpc.common.DateProto;
import org.examples.hotel.grpc.util.DateConverter;
import org.examples.hotel.grpc.util.ErrorHandler;
import org.examples.hotel.grpc.util.LoggingInterceptor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Exemples d'utilisation des services gRPC
 * 
 * Ce fichier montre comment :
 * 1. Implémenter un service gRPC
 * 2. Démarrer un serveur gRPC
 * 3. Créer un client gRPC
 * 4. Effectuer des appels
 */
public class GrpcExamples {

    // ==================== EXEMPLE 1 : Implémentation du Service ====================
    
    /**
     * Implémentation simple du HotelService
     */
    public static class SimpleHotelService extends HotelServiceGrpc.HotelServiceImplBase {
        
        @Override
        public void getCatalog(CatalogRequest request, 
                              StreamObserver<HotelCatalog> responseObserver) {
            try {
                System.out.println("GetCatalog appelé pour hotel: " + request.getHotelId());
                
                // Construire la réponse
                HotelCatalog catalog = HotelCatalog.newBuilder()
                    .setHotel(HotelInfo.newBuilder()
                        .setId(request.getHotelId())
                        .setName("Hôtel Example")
                        .setStars(4)
                        .setDescription("Un hôtel magnifique")
                        .build())
                    .addRoomTypes(RoomType.newBuilder()
                        .setId("room-001")
                        .setCategory("DOUBLE")
                        .setCapacity(2)
                        .setPricePerNight(120.0)
                        .setAvailableCount(5)
                        .build())
                    .setTotalRooms(50)
                    .build();
                
                // Envoyer la réponse
                responseObserver.onNext(catalog);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                // Gestion d'erreur
                responseObserver.onError(ErrorHandler.toGrpcException(e));
            }
        }
        
        @Override
        public void searchOffers(SearchRequest request, 
                                StreamObserver<OffersResponse> responseObserver) {
            try {
                System.out.println("SearchOffers pour ville: " + request.getCity());
                
                // Créer quelques offres d'exemple
                Offer offer1 = Offer.newBuilder()
                    .setOfferId("offer-001")
                    .setHotel(HotelInfo.newBuilder()
                        .setId("hotel-1")
                        .setName("Hôtel Paris Centre")
                        .setStars(4)
                        .build())
                    .setRoom(RoomType.newBuilder()
                        .setCategory("DOUBLE")
                        .setCapacity(2)
                        .setPricePerNight(150.0)
                        .build())
                    .setArrivalDate(request.getArrivalDate())
                    .setDepartureDate(request.getDepartureDate())
                    .setNumNights(3)
                    .setPricePerNight(150.0)
                    .setTotalPrice(450.0)
                    .setFinalPrice(450.0)
                    .setAvailable(true)
                    .setCurrency("EUR")
                    .build();
                
                OffersResponse response = OffersResponse.newBuilder()
                    .addOffers(offer1)
                    .setTotalCount(1)
                    .setOriginalRequest(request)
                    .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                responseObserver.onError(ErrorHandler.toGrpcException(e));
            }
        }
        
        @Override
        public void ping(PingRequest request, 
                        StreamObserver<PingResponse> responseObserver) {
            PingResponse response = PingResponse.newBuilder()
                .setMessage("Pong: " + request.getMessage())
                .setTimestamp(System.currentTimeMillis())
                .setServerId("example-server-1")
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    // ==================== EXEMPLE 2 : Démarrage du Serveur ====================
    
    /**
     * Démarre un serveur gRPC
     */
    public static class GrpcServerExample {
        private Server server;
        
        public void start(int port) throws IOException {
            server = ServerBuilder.forPort(port)
                .addService(new SimpleHotelService())
                .intercept(new LoggingInterceptor())  // Ajouter logging
                .build()
                .start();
            
            System.out.println("✓ Serveur gRPC démarré sur le port " + port);
            
            // Hook pour arrêt propre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.err.println("Arrêt du serveur gRPC...");
                try {
                    GrpcServerExample.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("✓ Serveur arrêté");
            }));
        }
        
        public void stop() throws InterruptedException {
            if (server != null) {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            }
        }
        
        public void blockUntilShutdown() throws InterruptedException {
            if (server != null) {
                server.awaitTermination();
            }
        }
    }

    // ==================== EXEMPLE 3 : Client gRPC ====================
    
    /**
     * Client gRPC pour appeler le service
     */
    public static class HotelGrpcClient {
        private final ManagedChannel channel;
        private final HotelServiceGrpc.HotelServiceBlockingStub blockingStub;
        
        public HotelGrpcClient(String host, int port) {
            this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()  // Pas de TLS pour le développement
                .build();
            
            this.blockingStub = HotelServiceGrpc.newBlockingStub(channel);
        }
        
        /**
         * Obtenir le catalogue d'un hôtel
         */
        public HotelCatalog getCatalog(String hotelId) {
            CatalogRequest request = CatalogRequest.newBuilder()
                .setHotelId(hotelId)
                .setIncludeImages(true)
                .setIncludeUnavailableRooms(false)
                .build();
            
            return blockingStub.getCatalog(request);
        }
        
        /**
         * Rechercher des offres
         */
        public OffersResponse searchOffers(String city, LocalDate arrival, LocalDate departure) {
            SearchRequest request = SearchRequest.newBuilder()
                .setCity(city)
                .setArrivalDate(DateConverter.toProto(arrival))
                .setDepartureDate(DateConverter.toProto(departure))
                .setNumPersons(2)
                .build();
            
            return blockingStub.searchOffers(request);
        }
        
        /**
         * Ping le serveur
         */
        public PingResponse ping(String message) {
            PingRequest request = PingRequest.newBuilder()
                .setMessage(message)
                .build();
            
            return blockingStub.ping(request);
        }
        
        /**
         * Fermer la connexion
         */
        public void shutdown() throws InterruptedException {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ==================== EXEMPLE 4 : Utilisation Complète ====================
    
    /**
     * Exemple d'utilisation complète
     */
    public static void main(String[] args) throws Exception {
        int port = 9090;
        
        // 1. Démarrer le serveur
        System.out.println("=== Démarrage du serveur gRPC ===");
        GrpcServerExample server = new GrpcServerExample();
        server.start(port);
        
        // Attendre que le serveur soit prêt
        Thread.sleep(1000);
        
        // 2. Créer un client
        System.out.println("\n=== Création du client gRPC ===");
        HotelGrpcClient client = new HotelGrpcClient("localhost", port);
        
        // 3. Tester le ping
        System.out.println("\n=== Test Ping ===");
        PingResponse pingResponse = client.ping("Hello gRPC!");
        System.out.println("Réponse: " + pingResponse.getMessage());
        System.out.println("Timestamp: " + pingResponse.getTimestamp());
        System.out.println("Server ID: " + pingResponse.getServerId());
        
        // 4. Obtenir le catalogue
        System.out.println("\n=== Récupération du catalogue ===");
        HotelCatalog catalog = client.getCatalog("hotel-paris-001");
        System.out.println("Hôtel: " + catalog.getHotel().getName());
        System.out.println("Étoiles: " + catalog.getHotel().getStars());
        System.out.println("Nombre de types de chambres: " + catalog.getRoomTypesCount());
        
        for (RoomType room : catalog.getRoomTypesList()) {
            System.out.println("  - " + room.getCategory() + 
                             " : " + room.getPricePerNight() + "€/nuit" +
                             " (" + room.getAvailableCount() + " disponibles)");
        }
        
        // 5. Rechercher des offres
        System.out.println("\n=== Recherche d'offres ===");
        LocalDate arrival = LocalDate.now().plusDays(7);
        LocalDate departure = arrival.plusDays(3);
        
        OffersResponse offers = client.searchOffers("Paris", arrival, departure);
        System.out.println("Nombre d'offres trouvées: " + offers.getTotalCount());
        
        for (Offer offer : offers.getOffersList()) {
            System.out.println("\nOffre ID: " + offer.getOfferId());
            System.out.println("  Hôtel: " + offer.getHotel().getName());
            System.out.println("  Chambre: " + offer.getRoom().getCategory());
            System.out.println("  Prix total: " + offer.getTotalPrice() + " " + offer.getCurrency());
            System.out.println("  Disponible: " + (offer.getAvailable() ? "Oui" : "Non"));
        }
        
        // 6. Fermer le client
        System.out.println("\n=== Fermeture du client ===");
        client.shutdown();
        
        // 7. Arrêter le serveur
        System.out.println("=== Arrêt du serveur ===");
        server.stop();
        
        System.out.println("\n✓ Exemple terminé avec succès!");
    }

    // ==================== EXEMPLE 5 : Gestion des erreurs ====================
    
    /**
     * Exemple de gestion d'erreurs
     */
    public static void errorHandlingExample() {
        HotelGrpcClient client = new HotelGrpcClient("localhost", 9090);
        
        try {
            // Appel qui pourrait échouer
            HotelCatalog catalog = client.getCatalog("hotel-inexistant");
            System.out.println("Catalogue récupéré: " + catalog.getHotel().getName());
            
        } catch (io.grpc.StatusRuntimeException e) {
            // Gérer l'erreur gRPC
            switch (e.getStatus().getCode()) {
                case NOT_FOUND:
                    System.err.println("Erreur: Hôtel non trouvé");
                    break;
                case INVALID_ARGUMENT:
                    System.err.println("Erreur: Argument invalide");
                    break;
                case UNAVAILABLE:
                    System.err.println("Erreur: Service indisponible");
                    break;
                default:
                    System.err.println("Erreur: " + e.getStatus().getDescription());
            }
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
        }
    }

    // ==================== EXEMPLE 6 : Utilisation de DateConverter ====================
    
    /**
     * Exemples d'utilisation du DateConverter
     */
    public static void dateConversionExamples() {
        // LocalDate -> Proto
        LocalDate today = LocalDate.now();
        DateProto protoDate = DateConverter.toProto(today);
        System.out.println("Date proto: " + protoDate.getYear() + "-" + 
                         protoDate.getMonth() + "-" + protoDate.getDay());
        
        // Proto -> LocalDate
        LocalDate converted = DateConverter.fromProto(protoDate);
        System.out.println("Date convertie: " + converted);
        
        // String -> Proto
        DateProto parsed = DateConverter.fromString("2025-12-25");
        System.out.println("Date parsée: " + DateConverter.toString(parsed));
        
        // Validation
        boolean isValid = DateConverter.isValid(protoDate);
        System.out.println("Date valide: " + isValid);
        
        // Comparaison
        DateProto future = DateConverter.toProto(today.plusDays(10));
        int comparison = DateConverter.compare(protoDate, future);
        System.out.println("Comparaison: " + (comparison < 0 ? "avant" : "après"));
    }
}

