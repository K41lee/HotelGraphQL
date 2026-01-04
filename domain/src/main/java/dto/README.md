# 📦 DTOs REST - Documentation

## Vue d'ensemble

Ce package contient tous les Data Transfer Objects (DTOs) utilisés pour la communication REST/JSON entre les serveurs d'hôtels, l'agence et les clients.

## Liste des DTOs

### 1. **SearchRequestDTO**
Critères de recherche d'offres d'hôtel
```json
{
  "ville": "Paris",
  "arrivee": "2025-12-01",
  "depart": "2025-12-03",
  "nbPersonnes": 2,
  "prixMin": 50,
  "prixMax": 200,
  "categorie": "HOTEL",
  "nbEtoiles": 4,
  "agence": "VoyagesPlus"
}
```

### 2. **OfferDTO**
Une offre d'hôtel (résultat de recherche)
```json
{
  "offerId": "opera-101-20251201-20251203",
  "hotelName": "Hotel Opera",
  "address": {...},
  "categorie": "HOTEL",
  "nbEtoiles": 4,
  "roomNumber": 101,
  "nbLits": 2,
  "prixTotal": 300,
  "start": "2025-12-01",
  "end": "2025-12-03",
  "agenceApplied": "VoyagesPlus",
  "room": {...}
}
```

### 3. **AddressDTO**
Adresse d'un hôtel
```json
{
  "pays": "France",
  "ville": "Paris",
  "rue": "Rue de la Paix",
  "numero": 10,
  "lieuDit": "Opéra",
  "latitude": 48.8566,
  "longitude": 2.3522
}
```

### 4. **RoomDTO**
Informations sur une chambre
```json
{
  "numero": 101,
  "nbLits": 2,
  "prixParNuit": 150,
  "imageUrl": "/images/opera/room101.jpg"
}
```

### 5. **ReservationRequestDTO**
Demande de réservation
```json
{
  "auth": {...},
  "offerId": "opera-101-20251201-20251203",
  "hotelName": "Hotel Opera",
  "roomNumber": 101,
  "nom": "Dupont",
  "prenom": "Jean",
  "carte": "1234-5678-9012-3456",
  "agence": "VoyagesPlus",
  "arrivee": "2025-12-01",
  "depart": "2025-12-03"
}
```

### 6. **AgencyAuthDTO**
Authentification d'une agence
```json
{
  "agencyId": "voyagesplus",
  "login": "admin",
  "password": "secret"
}
```

### 7. **ReservationConfirmationDTO**
Confirmation de réservation
```json
{
  "id": "RES-20251124-001",
  "message": "Réservation confirmée",
  "success": true,
  "offer": {...}
}
```

### 8. **OfferListDTO**
Liste d'offres (wrapper)
```json
{
  "offers": [...]
}
```

### 9. **ErrorDTO**
Erreur HTTP standardisée
```json
{
  "timestamp": "2025-11-24T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dates invalides",
  "path": "/api/hotels/search"
}
```

## Annotations Jackson

- `@JsonProperty` : Nom du champ JSON
- `@JsonFormat` : Format de sérialisation (dates en `yyyy-MM-dd`)

## Compatibilité

- ✅ Java 8+ (LocalDate, LocalDateTime)
- ✅ Jackson 2.15.2
- ✅ Support JSR-310 (java.time)

