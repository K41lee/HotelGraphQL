package org.examples.server.service;

import org.examples.server.entity.*;
import org.examples.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class HotelDatabaseService {
    private static final Logger log = LoggerFactory.getLogger(HotelDatabaseService.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AgenceRepository agenceRepository;

    public HotelEntity saveHotel(HotelEntity hotel) {
        log.info("[DB] Saving hotel: {}", hotel.getNom());
        return hotelRepository.save(hotel);
    }

    public Optional<HotelEntity> findHotelByNom(String nom) {
        return hotelRepository.findByNom(nom);
    }

    public List<HotelEntity> findAllHotels() {
        return hotelRepository.findAll();
    }

    public ChambreEntity saveChambre(ChambreEntity chambre) {
        log.info("[DB] Saving chambre: {} for hotel: {}", 
                 chambre.getNumero(), 
                 chambre.getHotel() != null ? chambre.getHotel().getNom() : "null");
        return chambreRepository.save(chambre);
    }

    public List<ChambreEntity> findChambresDisponibles(Long hotelId, int nbPersonnes, LocalDate debut, LocalDate fin) {
        log.info("[DB] Finding available chambres for hotel: {}, nbPersonnes: {}, debut: {}, fin: {}", 
                 hotelId, nbPersonnes, debut, fin);
        return chambreRepository.findAvailableChambres(hotelId, nbPersonnes, debut, fin);
    }

    public List<ChambreEntity> findChambresByHotel(Long hotelId) {
        return chambreRepository.findByHotelId(hotelId);
    }

    public ReservationEntity createReservation(ChambreEntity chambre, String nom, String prenom, 
                                               String carte, String agence, LocalDate debut, LocalDate fin) {
        // Vérifier qu'il n'y a pas de chevauchement
        List<ReservationEntity> overlapping = reservationRepository.findOverlappingReservations(
            chambre.getId(), debut, fin);
        
        if (!overlapping.isEmpty()) {
            log.warn("[DB] Reservation conflict for chambre {} between {} and {}", 
                     chambre.getNumero(), debut, fin);
            throw new IllegalStateException("Chambre déjà réservée sur cette période");
        }

        ReservationEntity reservation = new ReservationEntity(nom, prenom, debut, fin);
        reservation.setChambre(chambre);
        reservation.setClientCarte(carte);
        reservation.setAgence(agence);
        reservation.setReference(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        log.info("[DB] Creating reservation ref: {} for chambre: {} from {} to {}", 
                 reservation.getReference(), chambre.getNumero(), debut, fin);
        
        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> findReservationsByChambre(Long chambreId) {
        return reservationRepository.findByChambreId(chambreId);
    }

    public AgenceEntity saveAgence(AgenceEntity agence) {
        log.info("[DB] Saving agence: {}", agence.getNom());
        return agenceRepository.save(agence);
    }

    public Optional<AgenceEntity> findAgenceByNom(String nom) {
        return agenceRepository.findByNom(nom);
    }

    public List<AgenceEntity> findAgencesByHotel(Long hotelId) {
        return agenceRepository.findByHotelId(hotelId);
    }

    public void initializeHotelData(HotelEntity hotel) {
        Optional<HotelEntity> existing = findHotelByNom(hotel.getNom());
        if (existing.isPresent()) {
            log.info("[DB] Hotel '{}' already exists in database", hotel.getNom());
        } else {
            log.info("[DB] Initializing new hotel data: {}", hotel.getNom());
            saveHotel(hotel);
        }
    }
}

