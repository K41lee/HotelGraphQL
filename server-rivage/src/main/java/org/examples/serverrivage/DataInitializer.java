package org.examples.serverrivage;

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
 * Initialise les données de l'hôtel Rivage dans la base de données
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
        log.info("=== Initialisation des données Rivage ===");

        // Vérifier si l'hôtel existe déjà
        if (hotelRepository.findByNom("rivage").isPresent()) {
            log.info("Hotel 'rivage' existe déjà dans la base de données");
            return;
        }

        // Créer l'hôtel Rivage
        HotelEntity hotel = new HotelEntity();
        hotel.setNom("rivage");
        hotel.setVille("Sète");
        hotel.setPays("France");
        hotel.setRue("Quai du Large");
        hotel.setNumero("1");
        hotel.setCategorie("HAUT_DE_GAMME");
        hotel.setNbEtoiles(4);

        hotel = hotelRepository.save(hotel);
        log.info("✓ Hôtel 'rivage' créé avec succès (ID: {})", hotel.getId());

        // Créer les chambres avec des images SVG encodées en base64
        // SVG simple pour chambre double avec thème mer
        String svgDouble1 = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2U2ZjNmZiIvPjxyZWN0IHg9IjUwIiB5PSIxMDAiIHdpZHRoPSIxNDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjMDA3OGQ0IiByeD0iNSIvPjxyZWN0IHg9IjIxMCIgeT0iMTAwIiB3aWR0aD0iMTQwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzAwNzhkNCIgcng9IjUiLz48cmVjdCB4PSI2MCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjIyMCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjx0ZXh0IHg9IjIwMCIgeT0iNTAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+Uml2YWdlIC0gQ2hhbWJyZSAxMDE8L3RleHQ+PHRleHQgeD0iMjAwIiB5PSI3NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNjY2IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj4yIGxpdHMgLSAxMjDigqwvbnVpdDwvdGV4dD48L3N2Zz4=";
        String svgTriple = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2U2ZjdmZiIvPjxyZWN0IHg9IjMwIiB5PSIxMDAiIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjMDA5NmM3IiByeD0iNSIvPjxyZWN0IHg9IjE1MCIgeT0iMTAwIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzAwOTZjNyIgcng9IjUiLz48cmVjdCB4PSIyNzAiIHk9IjEwMCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIGZpbGw9IiMwMDk2YzciIHJ4PSI1Ii8+PHJlY3QgeD0iNDAiIHk9IjExMCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjE2MCIgeT0iMTEwIiB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiNmZmZmZmYiIHJ4PSIzIi8+PHJlY3QgeD0iMjgwIiB5PSIxMTAiIHdpZHRoPSI4MCIgaGVpZ2h0PSI2MCIgZmlsbD0iI2ZmZmZmZiIgcng9IjMiLz48dGV4dCB4PSIyMDAiIHk9IjUwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiMzMzMiIHRleHQtYW5jaG9yPSJtaWRkbGUiPlJpdmFnZSAtIENoYW1icmUgMTAyPC90ZXh0Pjx0ZXh0IHg9IjIwMCIgeT0iNzUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiIgZmlsbD0iIzY2NiIgdGV4dC1hbmNob3I9Im1pZGRsZSI+MyBsaXRzIC0gMTUw4oKsL251aXQ8L3RleHQ+PC9zdmc+";
        String svgDouble2 = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2VmZjhmZiIvPjxyZWN0IHg9IjUwIiB5PSIxMDAiIHdpZHRoPSIxNDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjMDA4YmQwIiByeD0iNSIvPjxyZWN0IHg9IjIxMCIgeT0iMTAwIiB3aWR0aD0iMTQwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzAwOGJkMCIgcng9IjUiLz48cmVjdCB4PSI2MCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjxyZWN0IHg9IjIyMCIgeT0iMTEwIiB3aWR0aD0iMTIwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmZmZmIiByeD0iMyIvPjx0ZXh0IHg9IjIwMCIgeT0iNTAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+Uml2YWdlIC0gQ2hhbWJyZSAxMDM8L3RleHQ+PHRleHQgeD0iMjAwIiB5PSI3NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNjY2IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj4yIGxpdHMgLSAxMzDigqwvbnVpdDwvdGV4dD48L3N2Zz4=";

        ChambreEntity chambre1 = new ChambreEntity();
        chambre1.setHotel(hotel);
        chambre1.setNumero(101);
        chambre1.setNbLits(2);
        chambre1.setPrixParNuit(120);
        chambre1.setImageUrl(svgDouble1);
        chambreRepository.save(chambre1);
        log.info("✓ Chambre 101 créée (prix: 120€/nuit, lits: 2)");

        ChambreEntity chambre2 = new ChambreEntity();
        chambre2.setHotel(hotel);
        chambre2.setNumero(102);
        chambre2.setNbLits(3);
        chambre2.setPrixParNuit(150);
        chambre2.setImageUrl(svgTriple);
        chambreRepository.save(chambre2);
        log.info("✓ Chambre 102 créée (prix: 150€/nuit, lits: 3)");

        ChambreEntity chambre3 = new ChambreEntity();
        chambre3.setHotel(hotel);
        chambre3.setNumero(103);
        chambre3.setNbLits(2);
        chambre3.setPrixParNuit(130);
        chambre3.setImageUrl(svgDouble2);
        chambreRepository.save(chambre3);
        log.info("✓ Chambre 103 créée (prix: 130€/nuit, lits: 2)");

        log.info("=== Initialisation Rivage terminée : 1 hôtel, 3 chambres ===");
    }
}

