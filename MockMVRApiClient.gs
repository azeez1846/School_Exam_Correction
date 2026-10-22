package xlp.integration.motorvehiclereport

uses gw.api.properties.RuntimePropertyRetriever
uses gw.restclient.config.Config
uses gwgen.veriskmvr.model.MvrOrderRequest
uses gwgen.veriskmvr.model.MvrOrderResponse
uses gwgen.veriskmvr.model.MvrReportRequest
uses gwgen.veriskmvr.model.MvrReportResponse

class MockMVRApiClient extends VeriskMVRApiClient {

  public var SERVICE_PROP_RETRIEVER : RuntimePropertyRetriever

  construct(servicePropertyRetreiver : RuntimePropertyRetriever) {
    super(servicePropertyRetreiver)
    SERVICE_PROP_RETRIEVER = servicePropertyRetreiver
  }

  override function doPost(request : Object) : Object { return super.doPost(request) }

  override function doGet(request : Object) : Object { return super.doGet(request) }

  override function getConfig(baseUrl : String, maxRetryAttempts : Integer, connectTimeoutMillis : Integer, readTimeoutMillis : Integer, correlationId : String, oauthToken : String) : Config {
    return super.getConfig(baseUrl, maxRetryAttempts, connectTimeoutMillis, readTimeoutMillis, correlationId, oauthToken)
  }

  override function getOrderNumber(request : MvrOrderRequest) : MvrOrderResponse {
    return super.getOrderNumber(request)
  }

  override function getMotorVehicleReport(request : MvrReportRequest) : MvrReportResponse {
    return super.getMotorVehicleReport(request)
  }

  override function doServiceCall(config : Config, request : Object) : Object {
    var mockJsonClient = new MockMVRApiClientFromJson(SERVICE_PROP_RETRIEVER)
    if (request typeis MvrOrderRequest) {
      if (request.FirstName == "FRANKLIN") {
        mockJsonClient.initializeMockResponseFromJson(MockMVRApiClientFromJson.MockType.MVR_SUBMIT_REQUEST_SUCCESS)
      } else if (request.FirstName == null) {
        mockJsonClient.initializeMockResponseFromJson(MockMVRApiClientFromJson.MockType.MVR_SUBMIT_REQUEST_FAILURE)
      } else if (request.FirstName == "FEIGN_EXCEPTION") {
        var invalidConfig = getConfig("http://localhost:1", 0, 100, 100, "testCorrelationId", "invalidToken")
        return super.doServiceCall(invalidConfig, request)
      }
      return mockJsonClient.generateSubmitRequestResponseObject()
    } else if (request typeis MvrReportRequest) {
      if (request.OrderNumber == "191451704") {
        mockJsonClient.initializeMockResponseFromJson(MockMVRApiClientFromJson.MockType.MVR_RETRIEVE_REQUEST_SUCCESS)
      } else if (request.OrderNumber == null) {
        mockJsonClient.initializeMockResponseFromJson(MockMVRApiClientFromJson.MockType.MVR_RETRIEVE_REQUEST_FAILURE)
      } else if (request.OrderNumber == "FEIGN_EXCEPTION") {
        var invalidConfig = getConfig("http://localhost:1", 0, 100, 100, "testCorrelationId", "invalidToken")
        return super.doServiceCall(invalidConfig, request)
      }
      return mockJsonClient.generateRetrieveRequestResponseObject()
    }
    return super.doServiceCall(config, request)
  }

  override function retrieveServiceName() : String { return super.retrieveServiceName() }
}
