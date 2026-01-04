# 🏨 Système de Réservation d'Hôtels - Architecture gRPC Pure

## 🎉 Système 100% gRPC avec Multi-Agences

Système distribué de réservation d'hôtels avec architecture **gRPC/Protobuf** moderne, persistance H2, images des chambres en SVG et **comparaison automatique de 2 agences** offrant des réductions différentes.

**✅ Migration SOAP → REST → gRPC terminée !**  
**✅ Architecture moderne et performante avec Protocol Buffers**

---

## 📋 Architecture

```
Client GUI (Swing)
    ↓ TCP/JSON
Multi-Agency Client
    ├─ Agence 1 (port 7070) → Réduction 10%
    └─ Agence 2 (port 7071) → Réduction 20% ⭐
         ↓ gRPC/Protobuf
    ┌────┴────┐
Server Opera (9090)  +  Server Rivage (9091)
    ↓                     ↓
H2 Database          H2 Database
(opera.db)           (rivage.db)
```

### Composants

- **2 serveurs d'hôtels** (gRPC + Spring Boot + H2 + Images SVG)
  - **Hotel Opera** : gRPC 9090 (Montpellier, 5★) - 3 chambres
  - **Hotel Rivage** : gRPC 9091 (Montpellier, 3★) - 3 chambres
- **2 agences concurrentes** : TCP/JSON client ↔ gRPC serveurs
  - **MegaAgence** : TCP 7070 (client), gRPC client vers hôtels, réduction **10%**
  - **SuperAgence** : TCP 7071 (client), gRPC client vers hôtels, réduction **20%** ⭐
- **Client intelligent** : Interface graphique Swing avec comparaison automatique
- **Module gRPC Commons** : Définitions Protocol Buffers partagées

---

## ⚡ Démarrage Ultra-Rapide

```bash
# Tout en une commande (compile + démarre + GUI)
./lancement.sh

# Script principal qui :
# - Compile le projet Maven
# - Lance les serveurs gRPC des hôtels
# - Lance les agences (clients gRPC + serveurs TCP)
# - Ouvre le client GUI
```

### Prérequis
- **Java 8+** (OpenJDK ou Oracle)
- **Maven 3.8+**
- **netcat** (nc) pour tests TCP (optionnel)
- Ports disponibles : **7070, 7071** (TCP), **9090, 9091** (gRPC)

### Ce qui démarre automatiquement
1. ✅ **Server Opera** (gRPC 9090, H2 Console 9090)
2. ✅ **Server Rivage** (gRPC 9091, H2 Console 9091)
3. ✅ **Agence 1 - MegaAgence** (TCP 7070, -10%)
4. ✅ **Agence 2 - SuperAgence** (TCP 7071, -20%)
5. ✅ **Client GUI** connecté aux 2 agences

**Temps de démarrage :** ~30 secondes

---

## 🏢 Les Deux Agences

### Agence 1 : MegaAgence
- **Port TCP** : 7070
- **Port HTTP** : 8080
- **Réduction** : 10%
- **Log** : `logs/agency.log`

### Agence 2 : SuperAgence ⭐
- **Port TCP** : 7071
- **Port HTTP** : 8081
- **Réduction** : 20%
- **Log** : `logs/agency2.log`

### Comparaison automatique

Le client se connecte **automatiquement aux deux agences** et affiche toutes les offres avec comparaison directe :

| Chambre    | Prix hôtel | MegaAgence | SuperAgence | Économie |
|------------|------------|------------|-------------|----------|
| Opera 201  | 440€       | 396€       | **352€** ⭐  | **44€**  |
| Rivage 101 | 240€       | 216€       | **192€** ⭐  | **24€**  |

---

## 🔌 Services gRPC

### Server Opera (gRPC localhost:9090)

```protobuf
service HotelService {
  rpc GetCatalog(CatalogRequest) returns (HotelCatalog);
  rpc SearchOffers(SearchRequest) returns (OffersResponse);
  rpc MakeReservation(ReservationRequest) returns (Reservation);
  rpc GetReservation(ReservationQuery) returns (Reservation);
  rpc CancelReservation(CancellationRequest) returns (CancellationResponse);
  rpc Ping(PingRequest) returns (PingResponse);
}
```

**Console H2 :** http://localhost:9090/h2-console

### Server Rivage (gRPC localhost:9091)

```protobuf
service HotelService {
  rpc GetCatalog(CatalogRequest) returns (HotelCatalog);
  rpc SearchOffers(SearchRequest) returns (OffersResponse);
  rpc MakeReservation(ReservationRequest) returns (Reservation);
  rpc GetReservation(ReservationQuery) returns (Reservation);
  rpc CancelReservation(CancellationRequest) returns (CancellationResponse);
  rpc Ping(PingRequest) returns (PingResponse);
}
```

**Console H2 :** http://localhost:9091/h2-console

### Agences (Interface TCP/JSON)

```bash
GET  /ping                   # Health check
GET  /hotels/catalog         # Catalogue
GET  /hotels/search          # Recherche d'offres
POST /reservations           # Créer une réservation
GET  /images/{filename}      # Images des chambres (SVG)
```

### Agence 1 - MegaAgence (TCP localhost:7070)

```json
{"op":"ping"}                            // Test connexion
{"op":"catalog.get"}                     // Catalogue (10% discount)
{"op":"offers.search","payload":{...}}   // Recherche avec -10%
{"op":"reservation.make","payload":{...}}// Réservation
```

### Agence 2 - SuperAgence (TCP localhost:7071)

```json
{"op":"ping"}                            // Test connexion
{"op":"catalog.get"}                     // Catalogue (20% discount)
{"op":"offers.search","payload":{...}}   // Recherche avec -20%
{"op":"reservation.make","payload":{...}}// Réservation
```

---

## 🧪 Tests Rapides

### Tests via les agences TCP/JSON

```bash
# Test Agence 1 (10%)
echo '{"op":"ping"}' | nc localhost 7070
echo '{"op":"catalog.get"}' | nc localhost 7070 | python3 -m json.tool

# Test Agence 2 (20%)
echo '{"op":"ping"}' | nc localhost 7071
echo '{"op":"catalog.get"}' | nc localhost 7071 | python3 -m json.tool

# Comparaison des prix (même recherche sur les 2 agences)
echo '{"op":"offers.search","payload":{"ville":"Montpellier","arrivee":"2025-12-25","depart":"2025-12-27","nbPersonnes":2}}' | nc localhost 7070 | python3 -m json.tool | grep prixTotal | head -1
echo '{"op":"offers.search","payload":{"ville":"Montpellier","arrivee":"2025-12-25","depart":"2025-12-27","nbPersonnes":2}}' | nc localhost 7071 | python3 -m json.tool | grep prixTotal | head -1
```

### Tests gRPC directs (si grpcurl installé)

```bash
# Health check Opera
grpcurl -plaintext localhost:9090 org.examples.hotel.grpc.HotelService/Ping

# Health check Rivage
grpcurl -plaintext localhost:9091 org.examples.hotel.grpc.HotelService/Ping

# Obtenir le catalogue Opera
grpcurl -plaintext -d '{"hotel_id":"opera"}' localhost:9090 org.examples.hotel.grpc.HotelService/GetCatalog

# Rechercher des offres
grpcurl -plaintext -d '{"hotel_id":"opera","city":"Montpellier","num_persons":2}' localhost:9090 org.examples.hotel.grpc.HotelService/SearchOffers
```

---

## 🗄️ Bases de Données H2

### Accès aux consoles

| Hôtel   | Console H2                       | JDBC URL          | User / Pass |
|---------|----------------------------------|-------------------|-------------|
| Opera   | http://localhost:8082/h2-console | jdbc:h2:file:./data/hotel-opera-db | opera / opera |
| Rivage  | http://localhost:8084/h2-console | jdbc:h2:file:./data/hotel-rivage-db | rivage / rivage |

### Tables principales

```sql
HOTEL           -- Informations hôtel
CHAMBRE         -- Chambres (avec IMAGE_URL)
RESERVATION     -- Réservations actives (gestion conflits)
AGENCE          -- Agences partenaires
ADRESSE         -- Adresses des hôtels
```

### Requêtes SQL utiles

```sql
-- Voir toutes les chambres avec leurs images
SELECT ID, NUMERO, NB_LITS, PRIX_PAR_NUIT, IMAGE_URL FROM CHAMBRE;

-- Voir les réservations récentes
SELECT * FROM RESERVATION ORDER BY ID DESC LIMIT 10;

-- Chambres disponibles pour des dates (détection conflits)
SELECT C.* FROM CHAMBRE C 
LEFT JOIN RESERVATION R 
  ON C.ID = R.CHAMBRE_ID 
  AND R.ARRIVEE <= '2025-12-27' 
  AND R.DEPART >= '2025-12-25'
WHERE R.ID IS NULL;
```

### Réinitialisation des bases

```bash
./reset_databases.sh   # Sauvegarde + reset + données initiales
```

---

## 🖼️ Images des Chambres

### Fonctionnalités
- ✅ **Format SVG** (vectoriel, 400×300px)
- ✅ **Stockées** : Fichiers dans `server-*/src/main/resources/static/images/`
- ✅ **URLs en base** : Colonne `IMAGE_URL` dans table `CHAMBRE`
- ✅ **Encodage Base64** : Intégré dans les réponses REST
- ✅ **Affichage GUI** : Images décodées et affichées dans le client
- ✅ **Bouton navigateur** : Option pour ouvrir dans le navigateur

### Images disponibles

| Hôtel  | Chambre | Image                            | Couleur  |
|--------|---------|----------------------------------|----------|
| Opera  | 201     | `/images/opera-room-201.svg`     | Or       |
| Opera  | 202     | `/images/opera-room-202.svg`     | Lavande  |
| Rivage | 101     | `/images/rivage-room-101.svg`    | Bleu     |
| Rivage | 102     | `/images/rivage-room-102.svg`    | Vert     |

### Accès aux images

```bash
# Via serveur REST
curl http://localhost:8084/images/opera-room-201.svg
curl http://localhost:8082/images/rivage-room-101.svg

# Dans le GUI : Cliquer sur "🖼️ Voir" dans la table des résultats
```

---

## 📦 Structure du Projet

```
HotelgRPC/
├── grpc-commons/        # Module gRPC partagé
│   ├── src/main/proto/  # Définitions Protocol Buffers
│   │   ├── hotel.proto  # Service HotelService
│   │   ├── agency.proto # Service AgencyService
│   │   └── common.proto # Types communs (Date, Price, etc.)
│   └── target/generated-sources/  # Classes Java générées
├── domain/              # DTOs + Classes métier
├── server-base/         # Code commun serveurs (JPA entities, repos)
├── server-opera/        # Server Opera gRPC (Montpellier, 5★, port 9090)
├── server-rivage/       # Server Rivage gRPC (Montpellier, 3★, port 9091)
├── agency-server/       # Agence 1 - MegaAgence (TCP 7070, -10%)
├── agency-server-2/     # Agence 2 - SuperAgence (TCP 7071, -20%) ⭐
├── client-cli/          # Client GUI multi-agences + CLI
├── logs/                # Logs des serveurs et agences
│   ├── opera.log        # Logs server Opera
│   ├── rivage.log       # Logs server Rivage
│   ├── agency.log       # Logs MegaAgence
│   └── agency2.log      # Logs SuperAgence
├── lancement.sh         # Script de démarrage principal ⭐
└── test-agency-name.sh  # Test de validation agence en BDD
```

---

## 🎯 Fonctionnalités

### Multi-Agences ⭐
- ✅ **2 agences** avec réductions différentes (10% et 20%)
- ✅ **Connexion automatique** aux deux agences
- ✅ **Affichage fusionné** : 6 offres = 3 chambres × 2 agences (Opera + Rivage)
- ✅ **Colonne "Agence"** dans les résultats
- ✅ **Comparaison directe** des prix
- ✅ **Routage intelligent** : réservation envoyée à la bonne agence
- ✅ **Nom d'agence en BDD** : Traçabilité complète des réservations
- ✅ **Routage intelligent** : réservation envoyée à la bonne agence

### Recherche d'hôtels
- ✅ Filtres : ville, dates, nombre de personnes
- ✅ **Recherche simultanée** dans les 2 agences
- ✅ Agrégation automatique
- ✅ **Détection des doublons** (même chambre, prix différents)
- ✅ Prix final affiché avec réduction

### Réservations
- ✅ Sélection d'une offre (avec indication de l'agence)
- ✅ Formulaire client (nom, prénom, carte)
- ✅ **Routage automatique** vers la bonne agence
- ✅ Confirmation avec référence unique
- ✅ **Persistance en base H2**
- ✅ **Détection des conflits** (chambre déjà réservée)

### Images des chambres ⭐ NOUVEAU
- ✅ **Format SVG** (vectoriel, coloré)
- ✅ **Encodage Base64** dans les réponses API
- ✅ **Affichage dans le GUI** (décodage automatique)
- ✅ **Bouton navigateur** pour ouvrir l'URL
- ✅ Stockage des URLs en base H2

### Architecture
- ✅ **gRPC/Protobuf** (moderne et performant)
- ✅ **Microservices** (2 serveurs + 2 agences indépendants)
- ✅ **Client intelligent** (multi-connexions)
- ✅ **Gestion des conflits** de réservation
- ✅ **Logs détaillés** par composant

---

## 🛠️ Commandes Utiles

### Build & Run

```bash
# Compiler tout le projet
mvn clean install -DskipTests

# Démarrer individuellement
cd server-opera && mvn spring-boot:run
cd server-rivage && mvn spring-boot:run
cd agency-server && mvn spring-boot:run
cd agency-server-2 && mvn spring-boot:run

# Lancer le client GUI
cd client-cli && mvn exec:java -Dexec.mainClass=org.examples.client.gui.HotelClientGUI
```

### Logs en temps réel

```bash
# Tous les logs
tail -f logs/*.log

# Par composant
tail -f logs/rivage.log    # Hotel Rivage
tail -f logs/opera.log     # Hotel Opera
tail -f logs/agency.log    # Agence 1 (10%)
tail -f logs/agency2.log   # Agence 2 (20%)
```

### Gestion des processus

```bash
# Voir les processus actifs
ps aux | grep -E "(opera|rivage|agency)" | grep -v grep

# Arrêter tous les serveurs
pkill -9 java

# Libérer les ports TCP et gRPC
fuser -k 7070/tcp 7071/tcp 9090/tcp 9091/tcp
```

---

## 📊 Technologies Utilisées

### Backend
- **gRPC** 1.58.0 / Protocol Buffers 3.x
- **Spring Boot** 2.7.12
- **grpc-spring-boot-starter** 2.14.0
- **Spring Data JPA**
- **H2 Database** 2.1.214 (mode mémoire)
- **Jackson** (JSON serialization pour TCP)

### Frontend
- **Java Swing** (GUI)
- **MiniJson** (parser JSON léger)
- **TCP Sockets** (communication client-agence)

### Protocoles
- **gRPC/Protobuf** (hôtels ↔ agences)
- **TCP/JSON** (client ↔ agences)

---

## 🎓 Architecture Technique

### Flux de recherche multi-agences

```
1. Client GUI démarrage
   └─ Connexion simultanée aux 2 agences

2. Recherche utilisateur
   └─ Client → MultiAgencyClient.searchAll()

3. MultiAgencyClient interroge les 2 agences en parallèle
   ├─ Agence 1 (7070) → Serveurs gRPC → Offres -10%
   └─ Agence 2 (7071) → Serveurs gRPC → Offres -20%

4. Fusion des offres
   ├─ Ajout métadonnées (_agencyName, _agencyPort)
   └─ 6 offres = 3 chambres × 2 agences

5. Affichage dans le GUI
   └─ Table avec colonne "Agence"

6. Réservation
   ├─ Client stocke le port d'agence de l'offre
   └─ Routage vers la bonne agence (7070 ou 7071)

7. Agence → Serveur hôtel
   └─ gRPC MakeReservation + persistance H2
```

### Modules Maven

```
hotel-parent (pom parent)
├── grpc-commons (définitions Protobuf)
│   ├── hotel.proto (HotelService)
│   ├── agency.proto (AgencyService)
│   └── common.proto (types communs)
├── domain (DTOs + entités)
├── server-base (code commun serveurs)
├── server-opera (instance Montpellier 5★)
├── server-rivage (instance Montpellier 3★)
├── agency-server (MegaAgence -10%)
│   ├── HotelGrpcClient
│   └── AgencyService (paramétrable)
├── agency-server-2 (SuperAgence -20%) ⭐
└── client-cli (GUI multi-agences + CLI)
    ├── MultiAgencyClient ⭐
    ├── AgencyTcpClient
    ├── HotelClientGUI
    └── ResultsPanel (avec colonne Agence)
```

---

## 🔍 Exemples d'Utilisation

### Exemple 1 : Comparaison de prix via les 2 agences

```bash
# Recherche via Agence 1 (10%)
echo '{"op":"offers.search","payload":{"ville":"Montpellier","arrivee":"2025-12-25","depart":"2025-12-27","nbPersonnes":2}}' | nc localhost 7070 | python3 -c "import sys,json; o=json.load(sys.stdin)['data']['offers'][0]; print(f\"Agence 1: {o['prixTotal']}€\")"

# Recherche via Agence 2 (20%)
echo '{"op":"offers.search","payload":{"ville":"Montpellier","arrivee":"2025-12-25","depart":"2025-12-27","nbPersonnes":2}}' | nc localhost 7071 | python3 -c "import sys,json; o=json.load(sys.stdin)['data']['offers'][0]; print(f\"Agence 2: {o['prixTotal']}€\")"

# Résultat attendu :
# Agence 1: 396€
# Agence 2: 352€  ← 44€ d'économie !
```

### Exemple 2 : Utilisation du GUI

```bash
# Lancer le système complet
./lancement.sh

# Dans l'interface graphique :
# 1. Observer : "2 agences connectées"
# 2. Rechercher : Montpellier, 25-27 déc 2025, 2 personnes
# 3. Résultat : 6 offres affichées (3 chambres × 2 agences)
# 4. Observer la colonne "Agence" : MegaAgence (-10%) ou SuperAgence (-20%)
# 5. Sélectionner une offre SuperAgence (meilleur prix)
# 6. Réserver : La réservation est automatiquement routée vers SuperAgence
```

### Exemple 3 : Voir une image de chambre

```bash
# Les images sont encodées en Base64 dans les réponses gRPC

# Dans le GUI
# 1. Rechercher des offres
# 2. Cliquer sur "🖼️ Voir" dans la colonne Image
# 3. L'image s'affiche (décodée depuis Base64)
# 4. Option : "🌐 Ouvrir dans le navigateur"
```

---

## 💡 Points Clés

### Avantages du système multi-agences

1. **Transparence** : L'utilisateur voit toutes les options
2. **Meilleurs prix** : SuperAgence offre systématiquement -20%
3. **Automatique** : Pas besoin de choisir, tout est affiché
4. **Intelligent** : Routage automatique vers la bonne agence
5. **Évolutif** : Facile d'ajouter une 3ème agence

### Configuration des agences

Les taux de réduction sont configurables dans `application.properties` :

**Agence 1** :
```properties
agency.discount.rate=0.10  # 10%
agency.name=MegaAgence
agency.tcp.port=7070
server.port=8080
```

**Agence 2** :
```properties
agency.discount.rate=0.20  # 20%
agency.name=SuperAgence
agency.tcp.port=7071
server.port=8081  # Différent pour éviter les conflits
```

---

## 🐛 Dépannage

### Problème : Agence 2 ne démarre pas

**Cause** : Conflit de port gRPC ou TCP

**Solution** : Vérifier les ports dans `agency-server-2/src/main/resources/application.properties`

### Problème : Images ne s'affichent pas

**Vérifier** :
```bash
# 1. Les fichiers SVG existent
ls -l server-opera/src/main/resources/static/images/
ls -l server-rivage/src/main/resources/static/images/

# 2. Les images sont dans les réponses gRPC (Base64)
# Vérifier les logs des serveurs
```

### Problème : Ports déjà utilisés

```bash
# Libérer tous les ports
fuser -k 7070/tcp 7071/tcp 8080/tcp 8081/tcp 9090/tcp 9091/tcp

# Ou arrêter tous les processus Spring Boot
pkill -f "spring-boot:run"
pkill -9 java
```

---

## 📈 Améliorations Futures Possibles

- [ ] Ajouter une 3ème agence avec un autre taux
- [ ] Interface web (React/Vue) en plus du GUI Swing
- [ ] Authentification utilisateur
- [ ] Historique des réservations
- [ ] Notifications email
- [ ] Paiement en ligne
- [ ] Images PNG/JPG en plus des SVG
- [ ] Recherche par catégorie d'hôtel
- [ ] Filtres avancés (prix max, équipements, etc.)

---

## 📄 Licence

Projet académique - Système distribué de réservation d'hôtels

---

## 👥 Auteurs

Projet développé dans le cadre d'un cours sur les architectures distribuées.

**Version** : 3.0 (Architecture gRPC Pure)  
**Date** : Décembre 2025

---

## 🚀 Quick Start

```bash
# Cloner le projet
cd /home/etudiant/Bureau/gRPC/HotelgRPC

# Lancer tout le système (compile + démarre + GUI)
./lancement.sh

# Attendre ~30 secondes que tout démarre
# Interface GUI s'ouvre automatiquement
# Observer : "2 agences connectées"

# Tester :
# - Ville : Montpellier
# - Dates : 25-27 décembre 2025
# - Personnes : 2
# → Voir 6 offres avec comparaison des prix !
```

**Bon voyage dans le monde des microservices gRPC multi-agences !** 🎉🏨💰

