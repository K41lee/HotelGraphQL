package org.examples.server.service;
import Impl.*;
import org.examples.server.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.util.Optional;
@Service
public class DataSyncService {
    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);
    @Autowired
    private HotelDatabaseService dbService;
    @Autowired(required = false)
    private Gestionnaire gestionnaire;
    @PostConstruct
    public void syncDomainToDatabase() {
        if (gestionnaire == null) {
            log.warn("[SYNC] No Gestionnaire found, skipping sync");
            return;
        }
        log.info("[SYNC] Starting synchronization from domain to database...");
        for (Hotel hotel : gestionnaire.getHotels()) {
            syncHotel(hotel);
        }
        log.info("[SYNC] Synchronization completed");
    }
    private void syncHotel(Hotel hotel) {
        Optional<HotelEntity> existingHotel = dbService.findHotelByNom(hotel.getNom());
        HotelEntity hotelEntity;
        if (existingHotel.isPresent()) {
            hotelEntity = existingHotel.get();
            log.info("[SYNC] Hotel '{}' already exists in DB", hotel.getNom());
        } else {
            log.info("[SYNC] Creating new hotel '{}' in DB", hotel.getNom());
            hotelEntity = new HotelEntity();
            hotelEntity.setNom(hotel.getNom());
            if (hotel.getAdresse() != null) {
                hotelEntity.setVille(hotel.getAdresse().getVille());
                hotelEntity.setRue(hotel.getAdresse().getRue());
                hotelEntity.setNumero(String.valueOf(hotel.getAdresse().getNumero()));
                hotelEntity.setPays(hotel.getAdresse().getPays());
            }
            hotelEntity.setCategorie(hotel.getCategorie().name());
            hotelEntity.setNbEtoiles(hotel.getNbEtoiles());
            hotelEntity = dbService.saveHotel(hotelEntity);
            for (Chambre chambre : hotel.getChambres()) {
                syncChambre(chambre, hotelEntity);
            }
            for (Agence agence : hotel.getAgences()) {
                syncAgence(agence, hotelEntity);
            }
        }
    }
    private void syncChambre(Chambre chambre, HotelEntity hotelEntity) {
        ChambreEntity chambreEntity = new ChambreEntity();
        chambreEntity.setNumero(chambre.getNumero());
        chambreEntity.setNbLits(chambre.getNbLits());
        chambreEntity.setPrixParNuit(chambre.getPrixParNuit());
        chambreEntity.setImageUrl(chambre.getImageUrl()); // Copier l'URL de l'image
        chambreEntity.setHotel(hotelEntity);
        dbService.saveChambre(chambreEntity);
        log.info("[SYNC] Synchronized chambre {} (imageUrl: {}) for hotel '{}'",
                 chambre.getNumero(), chambre.getImageUrl(), hotelEntity.getNom());
    }
    private void syncAgence(Agence agence, HotelEntity hotelEntity) {
        AgenceEntity agenceEntity = new AgenceEntity();
        agenceEntity.setNom(agence.getNom());
        agenceEntity.setReduction(agence.getReduction());
        agenceEntity.setHotel(hotelEntity);
        dbService.saveAgence(agenceEntity);
        log.info("[SYNC] Synchronized agence '{}' for hotel '{}'", 
                 agence.getNom(), hotelEntity.getNom());
    }
}
