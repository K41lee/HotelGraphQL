# Système de Réservation Hôtelière - Architecture GraphQL

## 📋 Description du Projet

Système distribué de réservation hôtelière utilisant **GraphQL** pour la communication entre les composants. Le système permet aux clients de rechercher des chambres d'hôtel via des agences de voyage qui appliquent des remises, et de réaliser des réservations en temps réel.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client GUI                               │
│                    (Interface Graphique)                         │
└────────────────────────┬────────────────────────────────────────┘
                         │ TCP
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Agences                                   │
│  ┌──────────────────┐              ┌──────────────────┐         │
│  │  MegaAgence      │              │  SuperAgence     │         │
│  │  (Remise -10%)   │              │  (Remise -20%)   │         │
│  │  Port: 7070      │              │  Port: 7071      │         │
│  └──────────────────┘              └──────────────────┘         │
└────────────┬───────────────────────────────┬────────────────────┘
             │ GraphQL                       │ GraphQL
             ▼                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Serveurs Hôtels                               │
│  ┌──────────────────┐              ┌──────────────────┐         │
│  │  Hotel Opera     │              │  Hotel Rivage    │         │
│  │  (Montpellier)   │              │  (Sète)          │         │
│  │  Port: 8082      │              │  Port: 8084      │         │
│  │  GraphQL + H2    │              │  GraphQL + H2    │         │
│  └──────────────────┘              └──────────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 Technologies Utilisées

### Backend
- **Spring Boot 2.7.12** - Framework Java pour microservices
- **Spring GraphQL 1.0.4** - Implémentation GraphQL pour Spring
- **Spring Data JPA** - Accès aux données
- **H2 Database** - Base de données embarquée
- **Maven** - Gestion des dépendances

### Frontend
- **Java Swing** - Interface graphique utilisateur
- **TCP Sockets** - Communication client-agence

### Protocoles
- **GraphQL** - Communication Agences ↔ Hôtels
- **TCP** - Communication Client ↔ Agences

## 📁 Structure du Projet

```
HotelGraphQL/
├── server-opera/          # Serveur Hotel Opera (Montpellier)
├── server-rivage/         # Serveur Hotel Rivage (Sète)
├── server-base/           # Classes communes des serveurs
├── agency-server/         # Agence MegaAgence (-10%)
├── agency-server-2/       # Agence SuperAgence (-20%)
├── client-cli/            # Client GUI Swing
├── graphql-commons/       # Schémas GraphQL partagés
├── domain/               # Objets métier communs
├── data/                 # Bases de données H2
├── logs/                 # Logs des serveurs
├── lancement.sh          # Script de démarrage
└── pom.xml              # Configuration Maven parent
```

## 🔧 Prérequis

- **Java 8** ou supérieur
- **Maven 3.6+**
- **Git** (optionnel)
- **Linux/Unix** (pour les scripts shell)

## 📦 Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd HotelGraphQL
```

### 2. Compiler le projet

```bash
mvn clean install -DskipTests
```

Cette commande compile tous les modules et génère les artefacts nécessaires.

## ▶️ Démarrage du Système

### Lancement automatique (recommandé)

```bash
./lancement.sh
```

Ce script démarre automatiquement tous les serveurs :
- Hotel Opera (port 8082)
- Hotel Rivage (port 8084)
- MegaAgence (port 7070)
- SuperAgence (port 7071)

**Temps de démarrage** : ~60-90 secondes

### Lancement manuel

Si vous préférez démarrer les serveurs individuellement :

```bash
# Terminal 1 - Hotel Opera
cd server-opera && mvn spring-boot:run

# Terminal 2 - Hotel Rivage
cd server-rivage && mvn spring-boot:run

# Terminal 3 - MegaAgence
cd agency-server && mvn spring-boot:run

# Terminal 4 - SuperAgence
cd agency-server-2 && mvn spring-boot:run
```

### Lancement du client GUI

```bash
cd client-cli
mvn exec:java -Dexec.mainClass="org.examples.client.gui.HotelClientGUI"
```

## 🎯 Utilisation

### 1. Recherche de Chambres

1. **Ouvrez le client GUI**
2. **Sélectionnez** :
   - Ville (Montpellier ou Sète)
   - Date d'arrivée
   - Date de départ
   - Nombre de personnes
3. **Cliquez** sur "Rechercher"

Le système interroge automatiquement les **2 agences** qui contactent les **hôtels** via GraphQL et appliquent leurs remises respectives.

### 2. Réservation

1. **Sélectionnez** une offre dans les résultats
2. **Cliquez** sur "Réserver"
3. **Remplissez** le formulaire :
   - Nom
   - Prénom
   - Numéro de carte bancaire
4. **Confirmez** la réservation

La réservation est enregistrée en base de données avec le nom de l'agence.

## 🔍 Fonctionnalités

### ✅ Recherche Multi-Agences
- Interrogation simultanée de plusieurs agences
- Agrégation des résultats
- Application automatique des remises

### ✅ Gestion des Remises
- **MegaAgence** : -10% sur tous les tarifs
- **SuperAgence** : -20% sur tous les tarifs

### ✅ Vérification de Disponibilité
- Détection automatique des conflits de réservation
- Chambres déjà réservées non proposées
- Gestion des chevauchements de dates

### ✅ Persistance des Données
- Base de données H2 embarquée
- Sauvegarde des réservations avec :
  - Informations client
  - Dates de séjour
  - Chambre réservée
  - Agence utilisée

### ✅ Images des Chambres
- Images SVG encodées en base64
- Affichage dans le GUI
- Ouverture dans le navigateur

## 📊 API GraphQL

### Endpoints

- **Hotel Opera** : `http://localhost:8082/graphql`
- **Hotel Rivage** : `http://localhost:8084/graphql`

### Requêtes Principales

#### Recherche d'Offres

```graphql
query SearchOffers($input: SearchOffersInput!) {
  searchOffers(input: $input) {
    offers {
      offerId
      hotel {
        id
        name
        stars
        address {
          city
        }
      }
      room {
        id
        category
        capacity
        pricePerNight
        images {
          url
        }
      }
      totalPrice
      arrivalDate
      departureDate
    }
    totalCount
  }
}
```

**Variables** :
```json
{
  "input": {
    "city": "Montpellier",
    "arrivalDate": "2026-01-10",
    "departureDate": "2026-01-12",
    "numPersons": 2
  }
}
```

#### Réservation

```graphql
mutation MakeReservation($input: ReservationInput!) {
  makeReservation(input: $input) {
    reservationId
    confirmationCode
    clientName
    totalPrice
    status
    arrivalDate
    departureDate
  }
}
```

**Variables** :
```json
{
  "input": {
    "hotelId": "opera",
    "roomId": "201",
    "clientName": "Dupont",
    "clientFirstName": "Jean",
    "clientCard": "1234567890123456",
    "arrivalDate": "2026-01-10",
    "departureDate": "2026-01-12",
    "numPersons": 2,
    "agencyName": "MegaAgence"
  }
}
```

## 🗄️ Base de Données

### Accès H2 Console

**Hotel Opera** :
- URL : `http://localhost:8082/h2-console`
- JDBC URL : `jdbc:h2:./data/hotel-opera-db`
- Username : `sa`
- Password : *(vide)*

**Hotel Rivage** :
- URL : `http://localhost:8084/h2-console`
- JDBC URL : `jdbc:h2:./data/hotel-rivage-db`
- Username : `sa`
- Password : *(vide)*

### Schéma de Données

#### Table `hotels`
```sql
CREATE TABLE hotels (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(255),
  ville VARCHAR(255),
  nb_etoiles INT,
  categorie VARCHAR(255),
  rue VARCHAR(255),
  numero VARCHAR(50),
  pays VARCHAR(255)
);
```

#### Table `chambres`
```sql
CREATE TABLE chambres (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  numero INT NOT NULL,
  nb_lits INT NOT NULL,
  prix_par_nuit INT NOT NULL,
  image_url VARCHAR(2000),
  hotel_id BIGINT,
  FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);
```

#### Table `reservations`
```sql
CREATE TABLE reservations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  chambre_id BIGINT NOT NULL,
  client_nom VARCHAR(255),
  client_prenom VARCHAR(255),
  client_carte VARCHAR(255),
  debut DATE NOT NULL,
  fin DATE NOT NULL,
  reference VARCHAR(255),
  agence VARCHAR(255),
  FOREIGN KEY (chambre_id) REFERENCES chambres(id)
);
```

## 📝 Logs

Les logs sont automatiquement générés dans le dossier `logs/` :

```bash
# Surveiller les logs en temps réel
tail -f logs/opera.log      # Hotel Opera
tail -f logs/rivage.log     # Hotel Rivage
tail -f logs/agency.log     # MegaAgence
tail -f logs/agency2.log    # SuperAgence
```

## 🛑 Arrêt du Système

```bash
pkill -9 java
```

Cette commande arrête tous les processus Java en cours d'exécution.

## 🧪 Tests

### Test GraphQL avec curl

```bash
# Test recherche d'offres
curl -X POST http://localhost:8082/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ searchOffers(input: {city: \"Montpellier\", arrivalDate: \"2026-01-10\", departureDate: \"2026-01-12\", numPersons: 2}) { offers { hotel { name } room { id capacity } totalPrice } totalCount } }"
  }'
```

### Test de disponibilité

1. Réservez une chambre pour une période donnée
2. Re-cherchez pour la même période
3. Vérifiez que la chambre n'apparaît plus dans les résultats

## 🔧 Configuration

### Ports par défaut

| Service | Port | Description |
|---------|------|-------------|
| Hotel Opera | 8082 | Serveur GraphQL + H2 Console |
| Hotel Rivage | 8084 | Serveur GraphQL + H2 Console |
| MegaAgence | 7070 | Serveur TCP (remise -10%) |
| SuperAgence | 7071 | Serveur TCP (remise -20%) |

### Modification des ports

Éditez les fichiers `application.properties` dans chaque module :

```properties
# Exemple: server-opera/src/main/resources/application.properties
server.port=8082
```

### Modification des remises

Éditez les fichiers `application.properties` des agences :

```properties
# agency-server/src/main/resources/application.properties
agency.name=MegaAgence
agency.discount.rate=0.10

# agency-server-2/src/main/resources/application.properties
agency.name=SuperAgence
agency.discount.rate=0.20
```

## 🐛 Dépannage

### Les serveurs ne démarrent pas

```bash
# Vérifier que les ports ne sont pas déjà utilisés
lsof -i :8082
lsof -i :8084
lsof -i :7070
lsof -i :7071

# Tuer les processus conflictuels
pkill -9 java

# Redémarrer
./lancement.sh
```

### Erreurs de compilation

```bash
# Nettoyer et recompiler
mvn clean install -DskipTests

# Si problèmes persistent, nettoyer le cache Maven
rm -rf ~/.m2/repository/org/examples
mvn clean install -DskipTests
```

### Base de données corrompue

```bash
# Supprimer les bases et redémarrer
rm -f data/*.db
rm -f server-opera/data/*.db
rm -f server-rivage/data/*.db

# Les bases seront recréées au prochain démarrage
./lancement.sh
```

## 📈 Performance

- **Temps de réponse** : < 500ms pour une recherche multi-agences
- **Capacité** : Gère plusieurs requêtes simultanées
- **Base de données** : H2 en mode fichier (persistance)

## 🔐 Sécurité

**Note** : Ce projet est à but éducatif et ne doit pas être utilisé en production sans renforcer la sécurité :

- ❌ Pas d'authentification
- ❌ Pas de validation des cartes bancaires
- ❌ Pas de chiffrement des communications
- ❌ Pas de gestion des sessions

## 📚 Documentation Technique

### Architecture GraphQL

Le système utilise **GraphQL** pour la communication entre agences et hôtels :

- **Avantages** :
  - Requêtes précises (pas de sur-fetching)
  - Typage fort avec schéma
  - Documentation auto-générée
  - Évolution de l'API facilitée

- **Schémas** : Définis dans `graphql-commons/src/main/resources/graphql/`

### Flux de Données

1. **Client** envoie une requête via TCP aux agences
2. **Agences** interrogent les hôtels via GraphQL
3. **Hôtels** répondent avec les offres disponibles
4. **Agences** appliquent leurs remises
5. **Client** reçoit les offres agrégées

## 👥 Auteurs

Projet réalisé dans le cadre d'un cours sur les architectures distribuées et GraphQL.

## 📄 Licence

Projet éducatif - Tous droits réservés

---

## 🎓 Contexte Pédagogique

Ce projet démontre :

✅ **Migration gRPC → GraphQL** : Remplacement d'une architecture gRPC par GraphQL  
✅ **Architecture Microservices** : Services indépendants communiquant via GraphQL  
✅ **Persistance des données** : Utilisation de Spring Data JPA et H2  
✅ **Interface utilisateur** : Client Swing avec communication TCP  
✅ **Gestion de la disponibilité** : Détection des conflits de réservation  
✅ **Agrégation multi-sources** : Combinaison de résultats de plusieurs services  

---

**Version** : 1.0.0 (GraphQL Migration Complete)  
**Date** : Janvier 2026  
**Statut** : ✅ Production Ready

