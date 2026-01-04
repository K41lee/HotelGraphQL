package org.examples.serveropera;

import Impl.Adresse;
import Impl.Chambre;
import Impl.Hotel;
import org.examples.server.entity.ChambreEntity;
import org.examples.server.entity.HotelEntity;
import org.examples.server.repository.ChambreRepository;
import org.examples.server.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise les données de l'hôtel Opera dans la base de données
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Override
    public void run(String... args) {
        log.info("=== Initialisation des données Opera ===");

        // Vérifier si l'hôtel existe déjà
        if (hotelRepository.findByNom("opera").isPresent()) {
            log.info("Hotel 'opera' existe déjà dans la base de données");
            return;
        }

        // Créer l'hôtel Opera
        HotelEntity hotel = new HotelEntity();
        hotel.setNom("opera");
        hotel.setVille("Montpellier");
        hotel.setPays("France");
        hotel.setRue("Bd Victor");
        hotel.setNumero("5");
        hotel.setCategorie("HAUT_DE_GAMME");
        hotel.setNbEtoiles(5);

        hotel = hotelRepository.save(hotel);
        log.info("✓ Hôtel 'opera' créé avec succès (ID: {})", hotel.getId());

        // Créer les chambres avec des images SVG encodées en base64
        // SVG simple pour chambre double
        String svgDouble = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2Y1ZjVmNSIvPjxyZWN0IHg9IjUwIiB5PSIxMDAiIHdpZHRoPSIxNDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjOGI0NTEzIiByeD0iNSIvPjxyZWN0IHg9IjIxMCIgeT0iMTAwIiB3aWR0aD0iMTQwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzhiNDUxMyIgcng9IjUiLz48cmVjdCB4PSI2MCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjIyMCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjx0ZXh0IHg9IjIwMCIgeT0iNTAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+T3BlcmEgLSBDaGFtYnJlIDIwMTwvdGV4dD48dGV4dCB4PSIyMDAiIHk9Ijc1IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTYiIGZpbGw9IiM2NjYiIHRleHQtYW5jaG9yPSJtaWRkbGUiPjIgbGl0cyAtIDIyMOKCrC9udWl0PC90ZXh0Pjwvc3ZnPg==";
        String svgDouble2 = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2VmZjhmZiIvPjxyZWN0IHg9IjUwIiB5PSIxMDAiIHdpZHRoPSIxNDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjNmE1YWNkIiByeD0iNSIvPjxyZWN0IHg9IjIxMCIgeT0iMTAwIiB3aWR0aD0iMTQwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzZhNWFjZCIgcng9IjUiLz48cmVjdCB4PSI2MCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjIyMCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjx0ZXh0IHg9IjIwMCIgeT0iNTAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+T3BlcmEgLSBDaGFtYnJlIDIwMjwvdGV4dD48dGV4dCB4PSIyMDAiIHk9Ijc1IiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTYiIGZpbGw9IiM2NjYiIHRleHQtYW5jaG9yPSJtaWRkbGUiPjIgbGl0cyAtIDI0MOKCrC9udWl0PC90ZXh0Pjwvc3ZnPg==";
        // SVG pour chambre triple
        String svgTriple = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2ZmZjhlZiIvPjxyZWN0IHg9IjMwIiB5PSIxMDAiIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjZDA2MDMwIiByeD0iNSIvPjxyZWN0IHg9IjE1MCIgeT0iMTAwIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2QwNjAzMCIgcng9IjUiLz48cmVjdCB4PSIyNzAiIHk9IjEwMCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIGZpbGw9IiNkMDYwMzAiIHJ4PSI1Ii8+PHJlY3QgeD0iNDAiIHk9IjExMCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjE2MCIgeT0iMTEwIiB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiNmZmZmZmYiIHJ4PSIzIi8+PHJlY3QgeD0iMjgwIiB5PSIxMTAiIHdpZHRoPSI4MCIgaGVpZ2h0PSI2MCIgZmlsbD0iI2ZmZmZmZiIgcng9IjMiLz48dGV4dCB4PSIyMDAiIHk9IjUwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiMzMzMiIHRleHQtYW5jaG9yPSJtaWRkbGUiPk9wZXJhIC0gQ2hhbWJyZSAyMDM8L3RleHQ+PHRleHQgeD0iMjAwIiB5PSI3NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNjY2IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj4zIGxpdHMgLSAyODDigqwvbnVpdDwvdGV4dD48L3N2Zz4=";

        ChambreEntity chambre1 = new ChambreEntity();
        chambre1.setHotel(hotel);
        chambre1.setNumero(201);
        chambre1.setNbLits(2);
        chambre1.setPrixParNuit(220);
        chambre1.setImageUrl(svgDouble);
        chambreRepository.save(chambre1);
        log.info("✓ Chambre 201 créée (prix: 220€/nuit, lits: 2)");

        ChambreEntity chambre2 = new ChambreEntity();
        chambre2.setHotel(hotel);
        chambre2.setNumero(202);
        chambre2.setNbLits(2);
        chambre2.setPrixParNuit(240);
        chambre2.setImageUrl(svgDouble2);
        chambreRepository.save(chambre2);
        log.info("✓ Chambre 202 créée (prix: 240€/nuit, lits: 2)");

        ChambreEntity chambre3 = new ChambreEntity();
        chambre3.setHotel(hotel);
        chambre3.setNumero(203);
        chambre3.setNbLits(3);
        chambre3.setPrixParNuit(280);
        chambre3.setImageUrl(svgTriple);
        chambreRepository.save(chambre3);
        log.info("✓ Chambre 203 créée (prix: 280€/nuit, lits: 3)");

        log.info("=== Initialisation Opera terminée : 1 hôtel, 3 chambres ===");
    }
}

