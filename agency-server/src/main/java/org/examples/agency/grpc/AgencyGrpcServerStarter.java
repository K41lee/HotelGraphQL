package org.examples.agency.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.examples.hotel.grpc.util.LoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Démarreur du serveur gRPC pour l'agence
 * Expose l'API AgencyService via gRPC avec streaming
 */
@Component
public class AgencyGrpcServerStarter {

    private static final Logger log = LoggerFactory.getLogger(AgencyGrpcServerStarter.class);

    @Value("${agency.grpc.port:8070}")
    private int grpcPort;

    @Value("${agency.grpc.enabled:false}")
    private boolean grpcEnabled;

    @Autowired
    private AgencyGrpcServiceImpl agencyGrpcService;

    private Server grpcServer;

    /**
     * Démarre le serveur gRPC une fois que Spring Boot est prêt
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startGrpcServer() {
        if (!grpcEnabled) {
            log.info("[AGENCY-gRPC-SERVER] gRPC server is disabled (agency.grpc.enabled=false)");
            return;
        }

        try {
            grpcServer = ServerBuilder.forPort(grpcPort)
                    .addService(agencyGrpcService)
                    .intercept(new LoggingInterceptor())
                    .build()
                    .start();

            log.info("╔════════════════════════════════════════════════════════════╗");
            log.info("║  ✓ Serveur gRPC Agency démarré avec succès                ║");
            log.info("║  Port: {}                                               ║", grpcPort);
            log.info("║  Service: AgencyService avec STREAMING ✨                 ║");
            log.info("║  RPCs: SearchAllHotels (streaming), CompareOffers, etc.   ║");
            log.info("╚════════════════════════════════════════════════════════════╝");

            // Hook pour arrêt propre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("[AGENCY-gRPC-SERVER] Arrêt du serveur gRPC (shutdown hook)...");
                try {
                    stopGrpcServer();
                } catch (InterruptedException e) {
                    log.error("[AGENCY-gRPC-SERVER] Erreur lors de l'arrêt du serveur", e);
                }
                log.info("[AGENCY-gRPC-SERVER] ✓ Serveur gRPC arrêté");
            }));

        } catch (IOException e) {
            log.error("[AGENCY-gRPC-SERVER] Erreur lors du démarrage du serveur gRPC sur le port {}", 
                    grpcPort, e);
            throw new RuntimeException("Failed to start gRPC server", e);
        }
    }

    /**
     * Arrête le serveur gRPC proprement
     */
    @PreDestroy
    public void stopGrpcServer() throws InterruptedException {
        if (grpcServer != null && !grpcServer.isShutdown()) {
            log.info("[AGENCY-gRPC-SERVER] Arrêt du serveur gRPC...");
            
            // Arrêter le pool de threads du service
            agencyGrpcService.shutdown();
            
            // Arrêter le serveur gRPC
            grpcServer.shutdown();

            // Attendre jusqu'à 30 secondes pour l'arrêt propre
            if (!grpcServer.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("[AGENCY-gRPC-SERVER] Le serveur n'a pas terminé à temps, arrêt forcé");
                grpcServer.shutdownNow();

                // Attendre encore un peu
                if (!grpcServer.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("[AGENCY-gRPC-SERVER] Le serveur n'a pas pu être arrêté proprement");
                }
            }

            log.info("[AGENCY-gRPC-SERVER] ✓ Serveur gRPC arrêté avec succès");
        }
    }

    /**
     * Bloque jusqu'à l'arrêt du serveur (utile pour les tests)
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    /**
     * Retourne true si le serveur gRPC est en cours d'exécution
     */
    public boolean isRunning() {
        return grpcServer != null && !grpcServer.isShutdown();
    }

    /**
     * Retourne le port sur lequel le serveur gRPC écoute
     */
    public int getPort() {
        return grpcPort;
    }
}

