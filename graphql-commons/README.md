# GraphQL Commons Module

Module partagé contenant les schémas GraphQL et types communs pour la communication entre les agences et les serveurs hôteliers.

## 📦 Contenu

### Schémas GraphQL

Les schémas sont définis dans `src/main/resources/graphql/` :

- **`common-schema.graphqls`** : Types communs (Date, Address, etc.)
- **`hotel-schema.graphqls`** : Schéma des services hôteliers
- **`agency-schema.graphqls`** : Schéma des services d'agence

### Types Principaux

#### HotelInfo
Informations sur un hôtel (nom, adresse, étoiles)

#### RoomType
Caractéristiques d'une chambre (catégorie, capacité, prix)

#### OfferType
Offre complète (hôtel + chambre + prix + disponibilité)

#### ReservationInput
Données nécessaires pour créer une réservation

#### SearchOffersInput
Critères de recherche de chambres

## 🔧 Utilisation

Ce module est une dépendance partagée utilisée par :
- `server-opera`
- `server-rivage`
- `agency-server`
- `agency-server-2`

Pour l'inclure dans un module :

```xml
<dependency>
    <groupId>org.examples</groupId>
    <artifactId>graphql-commons</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 📝 Modification des Schémas

1. Éditez les fichiers `.graphqls` dans `src/main/resources/graphql/`
2. Recompilez le module : `mvn clean install`
3. Recompilez les modules dépendants

**Note** : Toute modification des schémas nécessite de redémarrer les serveurs.

