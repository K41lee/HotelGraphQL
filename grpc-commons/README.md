# gRPC Commons Module

Module partagé contenant les définitions Protocol Buffers et les utilitaires pour la communication gRPC entre les services de l'écosystème hôtelier.

## 📦 Contenu

### Fichiers Protocol Buffers

#### `common.proto`
Types communs utilisés par tous les services :
- `DateProto` : Représentation de dates
- `DateRange` : Période de dates
- `PriceRange` : Fourchette de prix
- `Address` : Adresse complète
- `ServiceError` : Gestion d'erreurs structurée
- `GeoLocation` : Coordonnées GPS

#### `hotel.proto`
Service principal pour les opérations hôtelières :
- **Service** : `HotelService`
  - `GetCatalog` : Obtenir le catalogue d'un hôtel
  - `SearchOffers` : Rechercher des offres
  - `MakeReservation` : Créer une réservation
  - `GetReservation` : Consulter une réservation
  - `CancelReservation` : Annuler une réservation
  - `Ping` : Health check

- **Messages** :
  - `HotelCatalog`, `HotelInfo`, `RoomType`
  - `SearchRequest`, `OffersResponse`, `Offer`
  - `Reservation`, `ReservationRequest`

#### `agency.proto`
Service d'agence pour l'agrégation multi-hôtels :
- **Service** : `AgencyService`
  - `SearchAllHotels` : Recherche avec streaming
  - `SearchAllHotelsSync` : Recherche synchrone
  - `CompareOffers` : Comparaison d'offres
  - `GetPartnerHotels` : Liste des partenaires
  - `MakeReservationViaAgency` : Réservation avec commission
  - `GetAgencyStats` : Statistiques

- **Messages** :
  - `AgencySearchRequest`, `AgencySearchResponse`
  - `ComparisonRequest`, `OfferComparison`
  - `PartnerHotel`, `AgencyStats`

### Classes Utilitaires Java

#### `DateConverter`
Conversion entre `java.time.LocalDate` et `DateProto` :
```java
// LocalDate -> Proto
DateProto proto = DateConverter.toProto(LocalDate.now());

// Proto -> LocalDate
LocalDate date = DateConverter.fromProto(proto);

// String -> Proto
DateProto proto = DateConverter.fromString("2025-12-15");
```

#### `ErrorHandler`
Gestion des erreurs gRPC :
```java
// Exception -> StatusRuntimeException
try {
    // ...
} catch (Exception e) {
    throw ErrorHandler.toGrpcException(e);
}

// Exception -> ServiceError
ServiceError error = ErrorHandler.toServiceError(exception);
```

#### `LoggingInterceptor`
Intercepteur pour logger les appels gRPC :
```java
Server server = ServerBuilder.forPort(9090)
    .addService(ServerInterceptors.intercept(hotelService, new LoggingInterceptor()))
    .build();
```

## 🔧 Utilisation

### Ajout de la dépendance

Dans votre `pom.xml` :
```xml
<dependency>
    <groupId>org.examples</groupId>
    <artifactId>grpc-commons</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Génération du code

Le code Java est automatiquement généré lors de la compilation Maven :
```bash
mvn clean compile
```

Les fichiers générés se trouvent dans :
- `target/generated-sources/protobuf/java/` : Messages Protobuf
- `target/generated-sources/protobuf/grpc-java/` : Services gRPC

### Exemple d'implémentation de service

```java
import org.examples.hotel.grpc.*;
import io.grpc.stub.StreamObserver;

public class HotelGrpcService extends HotelServiceGrpc.HotelServiceImplBase {
    
    @Override
    public void getCatalog(CatalogRequest request, 
                          StreamObserver<HotelCatalog> responseObserver) {
        try {
            HotelCatalog catalog = buildCatalog(request.getHotelId());
            responseObserver.onNext(catalog);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(ErrorHandler.toGrpcException(e));
        }
    }
}
```

### Exemple de client gRPC

```java
import org.examples.hotel.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HotelGrpcClient {
    private final ManagedChannel channel;
    private final HotelServiceGrpc.HotelServiceBlockingStub blockingStub;
    
    public HotelGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
        this.blockingStub = HotelServiceGrpc.newBlockingStub(channel);
    }
    
    public HotelCatalog getCatalog(String hotelId) {
        CatalogRequest request = CatalogRequest.newBuilder()
            .setHotelId(hotelId)
            .setIncludeImages(true)
            .build();
        return blockingStub.getCatalog(request);
    }
    
    public void shutdown() {
        channel.shutdown();
    }
}
```

## 📊 Avantages de gRPC

- **Performance** : 3-5x plus rapide que REST/JSON
- **Taille** : Messages 60-70% plus petits (Protobuf vs JSON)
- **Type safety** : Vérification à la compilation
- **Streaming** : Support natif du streaming bidirectionnel
- **Multi-langage** : Génération automatique pour 10+ langages
- **Contrat strict** : API documentée via `.proto`

## 🏗️ Architecture

```
┌─────────────┐          ┌─────────────┐
│   Client    │          │   Agency    │
│     CLI     │◄────────►│   Server    │
└─────────────┘   gRPC   └─────────────┘
                               │ gRPC
                               ▼
                    ┌──────────────────┐
                    │  Hotel Servers   │
                    │ (Opera, Rivage)  │
                    └──────────────────┘
```

## 🔐 Sécurité (Future)

Pour activer TLS :
```java
Server server = NettyServerBuilder.forPort(9090)
    .useTransportSecurity(certChainFile, privateKeyFile)
    .addService(hotelService)
    .build();
```

## 📝 Versions

- **gRPC** : 1.58.0
- **Protobuf** : 3.24.0
- **Java** : 8+

## 📚 Documentation

- [gRPC Official Docs](https://grpc.io/docs/)
- [Protocol Buffers Guide](https://developers.google.com/protocol-buffers)
- [gRPC Java Tutorial](https://grpc.io/docs/languages/java/basics/)

## 🤝 Contribution

1. Modifier les fichiers `.proto` dans `src/main/proto/`
2. Recompiler : `mvn clean compile`
3. Vérifier le code généré
4. Mettre à jour ce README si nécessaire

