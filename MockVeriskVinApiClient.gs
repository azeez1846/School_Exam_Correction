package xlp.integration.veriskvin

uses feign.FeignException
uses gw.api.properties.RuntimePropertyRetriever
uses gw.restclient.config.Config
uses gwgen.veriskvin.model.VeriskVINDataRequest
uses gwgen.veriskvin.model.VeriskVINDataResponse
uses org.mockito.Mockito

class MockVeriskVinApiClient extends VeriskVinApiClient {

  public var SERVICE_PROP_RETRIEVER : RuntimePropertyRetriever

  construct(servicePropertyRetreiver : RuntimePropertyRetriever) {
    super(servicePropertyRetreiver)
    SERVICE_PROP_RETRIEVER = servicePropertyRetreiver
  }

  override function getConfig(baseUrl : String, maxRetryAttempts : Integer, connectTimeoutMillis : Integer, readTimeoutMillis : Integer, correlationId : String, oauthToken : String) : Config {
    return super.getConfig(baseUrl, maxRetryAttempts, connectTimeoutMillis, readTimeoutMillis, correlationId, oauthToken)
  }

  override function doGet(request : Object) : Object { return super.doGet(request) }

  override function getVinDetails(req : VeriskVINDataRequest, serviceCallFor : String) : VeriskVINDataResponse {
    return super.getVinDetails(req, serviceCallFor)
  }

  override function retrieveServiceName() : String { return super.retrieveServiceName() }

  override function doServiceCall(config : Config, request : Object) : Object {
    var mockJsonClient = new MockVeriskVinApiClientFromJson(SERVICE_PROP_RETRIEVER)
    if (request typeis VeriskVINDataRequest) {
      var firstVin = (request.Vins != null and not request.Vins.Empty) ? request.Vins.first() : null
      if (firstVin == "3FAHP08Z18R192323") {
        mockJsonClient.initializeMockResponseFromJson(MockVeriskVinApiClientFromJson.MockType.VERISKVIN_GETVIN_SUCCESS)
      } else if (firstVin == "3C7WRTCL" or firstVin == null) {
        mockJsonClient.initializeMockResponseFromJson(MockVeriskVinApiClientFromJson.MockType.VERISKVIN_GETVIN_FAILURE)
      } else if (firstVin == "FEIGN_EXCEPTION") {
        throw Mockito.mock(FeignException)
      } else if (firstVin == "INVALID_VIN") {
        throw new RuntimeException("Simulated Verisk VIN Exception")
      } else if (firstVin == "NULL_RESPONSE") {
        return null
      } else if (firstVin == "WRONG_TYPE_RESPONSE") {
        return "UNEXPECTED_RESPONSE_TYPE"
      } else {
        mockJsonClient.initializeMockResponseFromJson(MockVeriskVinApiClientFromJson.MockType.VERISKVIN_GETVIN_FAILURE)
      }
      return mockJsonClient.generateGetModRespObject()
    }
    return super.doServiceCall(config, request)
  }
}
