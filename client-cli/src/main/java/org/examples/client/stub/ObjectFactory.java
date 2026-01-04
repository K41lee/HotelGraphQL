
package org.examples.client.stub;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.examples.client.stub package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ReservationConfirmationDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "ReservationConfirmationDTO");
    private final static QName _ReservationRequestDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "ReservationRequestDTO");
    private final static QName _MakeReservationResponse_QNAME = new QName("http://service.hotel.examples.org/", "makeReservationResponse");
    private final static QName _PingResponse_QNAME = new QName("http://service.hotel.examples.org/", "pingResponse");
    private final static QName _SearchOffers_QNAME = new QName("http://service.hotel.examples.org/", "searchOffers");
    private final static QName _SearchOffersResponseDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "SearchOffersResponseDTO");
    private final static QName _MakeReservation_QNAME = new QName("http://service.hotel.examples.org/", "makeReservation");
    private final static QName _ServiceFault_QNAME = new QName("http://service.hotel.examples.org/", "ServiceFault");
    private final static QName _RoomDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "RoomDTO");
    private final static QName _OfferListDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "OfferListDTO");
    private final static QName _GetCatalog_QNAME = new QName("http://service.hotel.examples.org/", "getCatalog");
    private final static QName _SearchOffersResponse_QNAME = new QName("http://service.hotel.examples.org/", "searchOffersResponse");
    private final static QName _AgencyAuthDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "AgencyAuthDTO");
    private final static QName _OfferDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "OfferDTO");
    private final static QName _AddressDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "AddressDTO");
    private final static QName _CatalogDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "CatalogDTO");
    private final static QName _Ping_QNAME = new QName("http://service.hotel.examples.org/", "ping");
    private final static QName _SearchCriteriaDTO_QNAME = new QName("http://service.hotel.examples.org/dto", "SearchCriteriaDTO");
    private final static QName _GetCatalogResponse_QNAME = new QName("http://service.hotel.examples.org/", "getCatalogResponse");
    private final static QName _ReservationRequestDTOAgence_QNAME = new QName("http://service.hotel.examples.org/dto", "agence");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.examples.client.stub
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MakeReservation }
     * 
     */
    public MakeReservation createMakeReservation() {
        return new MakeReservation();
    }

    /**
     * Create an instance of {@link SearchOffers }
     * 
     */
    public SearchOffers createSearchOffers() {
        return new SearchOffers();
    }

    /**
     * Create an instance of {@link ServiceFault }
     * 
     */
    public ServiceFault createServiceFault() {
        return new ServiceFault();
    }

    /**
     * Create an instance of {@link GetCatalogResponse }
     * 
     */
    public GetCatalogResponse createGetCatalogResponse() {
        return new GetCatalogResponse();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link MakeReservationResponse }
     * 
     */
    public MakeReservationResponse createMakeReservationResponse() {
        return new MakeReservationResponse();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link GetCatalog }
     * 
     */
    public GetCatalog createGetCatalog() {
        return new GetCatalog();
    }

    /**
     * Create an instance of {@link SearchOffersResponse }
     * 
     */
    public SearchOffersResponse createSearchOffersResponse() {
        return new SearchOffersResponse();
    }

    /**
     * Create an instance of {@link SearchCriteriaDTO }
     * 
     */
    public SearchCriteriaDTO createSearchCriteriaDTO() {
        return new SearchCriteriaDTO();
    }

    /**
     * Create an instance of {@link RoomDTO }
     * 
     */
    public RoomDTO createRoomDTO() {
        return new RoomDTO();
    }

    /**
     * Create an instance of {@link CatalogDTO }
     * 
     */
    public CatalogDTO createCatalogDTO() {
        return new CatalogDTO();
    }

    /**
     * Create an instance of {@link SearchOffersResponseDTO }
     * 
     */
    public SearchOffersResponseDTO createSearchOffersResponseDTO() {
        return new SearchOffersResponseDTO();
    }

    /**
     * Create an instance of {@link ReservationRequestDTO }
     * 
     */
    public ReservationRequestDTO createReservationRequestDTO() {
        return new ReservationRequestDTO();
    }

    /**
     * Create an instance of {@link AgencyAuthDTO }
     * 
     */
    public AgencyAuthDTO createAgencyAuthDTO() {
        return new AgencyAuthDTO();
    }

    /**
     * Create an instance of {@link OfferDTO }
     * 
     */
    public OfferDTO createOfferDTO() {
        return new OfferDTO();
    }

    /**
     * Create an instance of {@link AddressDTO }
     * 
     */
    public AddressDTO createAddressDTO() {
        return new AddressDTO();
    }

    /**
     * Create an instance of {@link ReservationConfirmationDTO }
     * 
     */
    public ReservationConfirmationDTO createReservationConfirmationDTO() {
        return new ReservationConfirmationDTO();
    }

    /**
     * Create an instance of {@link OfferListDTO }
     * 
     */
    public OfferListDTO createOfferListDTO() {
        return new OfferListDTO();
    }

    /**
     * Create an instance of {@link AgenciesDTO }
     * 
     */
    public AgenciesDTO createAgenciesDTO() {
        return new AgenciesDTO();
    }

    /**
     * Create an instance of {@link CitiesDTO }
     * 
     */
    public CitiesDTO createCitiesDTO() {
        return new CitiesDTO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReservationConfirmationDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "ReservationConfirmationDTO")
    public JAXBElement<ReservationConfirmationDTO> createReservationConfirmationDTO(ReservationConfirmationDTO value) {
        return new JAXBElement<ReservationConfirmationDTO>(_ReservationConfirmationDTO_QNAME, ReservationConfirmationDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReservationRequestDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "ReservationRequestDTO")
    public JAXBElement<ReservationRequestDTO> createReservationRequestDTO(ReservationRequestDTO value) {
        return new JAXBElement<ReservationRequestDTO>(_ReservationRequestDTO_QNAME, ReservationRequestDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeReservationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "makeReservationResponse")
    public JAXBElement<MakeReservationResponse> createMakeReservationResponse(MakeReservationResponse value) {
        return new JAXBElement<MakeReservationResponse>(_MakeReservationResponse_QNAME, MakeReservationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchOffers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "searchOffers")
    public JAXBElement<SearchOffers> createSearchOffers(SearchOffers value) {
        return new JAXBElement<SearchOffers>(_SearchOffers_QNAME, SearchOffers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchOffersResponseDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "SearchOffersResponseDTO")
    public JAXBElement<SearchOffersResponseDTO> createSearchOffersResponseDTO(SearchOffersResponseDTO value) {
        return new JAXBElement<SearchOffersResponseDTO>(_SearchOffersResponseDTO_QNAME, SearchOffersResponseDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeReservation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "makeReservation")
    public JAXBElement<MakeReservation> createMakeReservation(MakeReservation value) {
        return new JAXBElement<MakeReservation>(_MakeReservation_QNAME, MakeReservation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "ServiceFault")
    public JAXBElement<ServiceFault> createServiceFault(ServiceFault value) {
        return new JAXBElement<ServiceFault>(_ServiceFault_QNAME, ServiceFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RoomDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "RoomDTO")
    public JAXBElement<RoomDTO> createRoomDTO(RoomDTO value) {
        return new JAXBElement<RoomDTO>(_RoomDTO_QNAME, RoomDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OfferListDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "OfferListDTO")
    public JAXBElement<OfferListDTO> createOfferListDTO(OfferListDTO value) {
        return new JAXBElement<OfferListDTO>(_OfferListDTO_QNAME, OfferListDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCatalog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "getCatalog")
    public JAXBElement<GetCatalog> createGetCatalog(GetCatalog value) {
        return new JAXBElement<GetCatalog>(_GetCatalog_QNAME, GetCatalog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchOffersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "searchOffersResponse")
    public JAXBElement<SearchOffersResponse> createSearchOffersResponse(SearchOffersResponse value) {
        return new JAXBElement<SearchOffersResponse>(_SearchOffersResponse_QNAME, SearchOffersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AgencyAuthDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "AgencyAuthDTO")
    public JAXBElement<AgencyAuthDTO> createAgencyAuthDTO(AgencyAuthDTO value) {
        return new JAXBElement<AgencyAuthDTO>(_AgencyAuthDTO_QNAME, AgencyAuthDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OfferDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "OfferDTO")
    public JAXBElement<OfferDTO> createOfferDTO(OfferDTO value) {
        return new JAXBElement<OfferDTO>(_OfferDTO_QNAME, OfferDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "AddressDTO")
    public JAXBElement<AddressDTO> createAddressDTO(AddressDTO value) {
        return new JAXBElement<AddressDTO>(_AddressDTO_QNAME, AddressDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CatalogDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "CatalogDTO")
    public JAXBElement<CatalogDTO> createCatalogDTO(CatalogDTO value) {
        return new JAXBElement<CatalogDTO>(_CatalogDTO_QNAME, CatalogDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriteriaDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "SearchCriteriaDTO")
    public JAXBElement<SearchCriteriaDTO> createSearchCriteriaDTO(SearchCriteriaDTO value) {
        return new JAXBElement<SearchCriteriaDTO>(_SearchCriteriaDTO_QNAME, SearchCriteriaDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCatalogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/", name = "getCatalogResponse")
    public JAXBElement<GetCatalogResponse> createGetCatalogResponse(GetCatalogResponse value) {
        return new JAXBElement<GetCatalogResponse>(_GetCatalogResponse_QNAME, GetCatalogResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.hotel.examples.org/dto", name = "agence", scope = ReservationRequestDTO.class)
    public JAXBElement<String> createReservationRequestDTOAgence(String value) {
        return new JAXBElement<String>(_ReservationRequestDTOAgence_QNAME, String.class, ReservationRequestDTO.class, value);
    }

}
