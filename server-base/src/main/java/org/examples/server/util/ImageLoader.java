package org.examples.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

/**
 * Utilitaire pour charger des images réelles et les convertir en data URIs
 * Compatible avec l'architecture gRPC pure (sans REST)
 */
public class ImageLoader {

    private static final Logger log = LoggerFactory.getLogger(ImageLoader.class);

    /**
     * Charge une image depuis un fichier et la convertit en data URI
     *
     * @param imagePath Chemin du fichier image (JPG, PNG, etc.)
     * @return Data URI complète (ex: data:image/jpeg;base64,/9j/4AAQ...)
     */
    public static String loadImageAsDataUri(String imagePath) {
        try {
            Path path = Paths.get(imagePath);

            if (!Files.exists(path)) {
                log.error("[IMAGE-LOADER] Fichier non trouvé: {}", imagePath);
                return generatePlaceholderSvg("Image non trouvée");
            }

            // Lire les bytes de l'image
            byte[] imageBytes = Files.readAllBytes(path);

            // Détecter le type MIME
            String mimeType = detectMimeType(imagePath);

            // Encoder en base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Créer la data URI
            String dataUri = "data:" + mimeType + ";base64," + base64Image;

            log.info("[IMAGE-LOADER] ✓ Image chargée: {} ({} bytes → {} chars)",
                    imagePath, imageBytes.length, dataUri.length());

            return dataUri;

        } catch (IOException e) {
            log.error("[IMAGE-LOADER] Erreur lecture image: {}", e.getMessage());
            return generatePlaceholderSvg("Erreur chargement");
        }
    }

    /**
     * Charge une image depuis les resources du projet
     */
    public static String loadImageFromResources(String resourcePath) {
        try {
            InputStream inputStream = ImageLoader.class.getResourceAsStream(resourcePath);

            if (inputStream == null) {
                log.error("[IMAGE-LOADER] Resource non trouvée: {}", resourcePath);
                return generatePlaceholderSvg("Resource non trouvée");
            }

            // Lire les bytes (compatible Java 8)
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] imageBytes = buffer.toByteArray();
            inputStream.close();

            // Détecter le type MIME
            String mimeType = detectMimeType(resourcePath);

            // Encoder en base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Créer la data URI
            String dataUri = "data:" + mimeType + ";base64," + base64Image;

            log.info("[IMAGE-LOADER] ✓ Resource chargée: {} ({} bytes)",
                    resourcePath, imageBytes.length);

            return dataUri;

        } catch (IOException e) {
            log.error("[IMAGE-LOADER] Erreur lecture resource: {}", e.getMessage());
            return generatePlaceholderSvg("Erreur resource");
        }
    }

    /**
     * Détecte le type MIME à partir de l'extension
     */
    private static String detectMimeType(String filename) {
        String lower = filename.toLowerCase();

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "image/jpeg"; // Par défaut
        }
    }

    /**
     * Génère un SVG de placeholder si l'image n'est pas disponible
     */
    private static String generatePlaceholderSvg(String message) {
        String svg = "<svg width='400' height='300' xmlns='http://www.w3.org/2000/svg'>" +
                "<rect width='400' height='300' fill='#e0e0e0'/>" +
                "<text x='200' y='150' font-family='Arial' font-size='18' fill='#666' text-anchor='middle'>" +
                message +
                "</text>" +
                "</svg>";

        String base64Svg = Base64.getEncoder().encodeToString(svg.getBytes());
        return "data:image/svg+xml;base64," + base64Svg;
    }

    /**
     * Exemple d'utilisation
     */
    public static void main(String[] args) {
        // Exemple 1 : Charger depuis un fichier
        String dataUri1 = loadImageAsDataUri("/chemin/vers/chambre201.jpg");
        System.out.println("Data URI (50 premiers chars): " + dataUri1.substring(0, 50) + "...");

        // Exemple 2 : Charger depuis resources
        String dataUri2 = loadImageFromResources("/images/chambre202.png");
        System.out.println("Data URI (50 premiers chars): " + dataUri2.substring(0, 50) + "...");
    }
}

