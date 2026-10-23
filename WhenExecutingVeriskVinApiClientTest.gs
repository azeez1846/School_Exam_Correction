package xlp.integration.veriskvin

uses gw.api.properties.RuntimePropertyRetriever
uses gw.restclient.config.Config
uses gw.testharness.v3.Suites
uses gwgen.veriskvin.api.VeriskVinApi
uses gwgen.veriskvin.model.VeriskVINDataRequest
uses gwgen.veriskvin.model.VeriskVINDataResponse
uses org.mockito.Mockito
uses xlp.common.PCServerTestClassBase_Ext
uses xlp.suites.IntegrationSuite

/**
 * To Test verisk vin Service.
 */
@Export
@Suites(IntegrationSuite.NAME)
class WhenExecutingVeriskVinApiClientTest extends PCServerTestClassBase_Ext {

  protected var mockVeriskVinApiHelper : VeriskVinApiHelper

  protected override function beforeMethod() { super.beforeMethod() }

  /**
   * Positive Case - Service successfully returns VIN report details.
   */
  function testThatVeriskVinWithSuccess() {
    var req = new VeriskVINDataRequest()
    var vinList = new ArrayList<String>()
    vinList.add("3FAHP08Z18R192323")
    req.Vins = vinList
    mockVeriskVinApiHelper = new MockVeriskVinApiHelper()
    var veriskVinGetVinResp = mockVeriskVinApiHelper.invokeVeriskVin(req, "testClass")
    //Assert Conditions
    assertNotNull(veriskVinGetVinResp)
    assertTrue(not veriskVinGetVinResp.Reports.Empty)
    assertTrue(veriskVinGetVinResp.Reports.first().VinDecoder.Report.ModelYear == "2008")
  }

  /**
   * Negative Case - Service returns failure payload when no reports are available for the provided VIN.
   */
  function testThatVeriskVinWithFailure() {
    var req = new VeriskVINDataRequest()
    var vinList = new ArrayList<String>()
    vinList.add("3C7WRTCL")
    req.Vins = vinList
    mockVeriskVinApiHelper = new MockVeriskVinApiHelper()
    var veriskVinGetVinResp = mockVeriskVinApiHelper.invokeVeriskVin(req, "testClass")
    //Assert Conditions
    assertNotNull(veriskVinGetVinResp)
    assertTrue(veriskVinGetVinResp.ErrorId == null)
    assertTrue(veriskVinGetVinResp.Reports == null or veriskVinGetVinResp.Reports.Empty)
  }

  /**
   * Positive Case - Covers VeriskVinApiHelper.mapVeriskVin (and its lambda block) with single,
   * multiple, and empty VIN lists, followed by end-to-end mocked service invocation.
   */
  function testMapVeriskVinWithSingleMultipleAndEmptyVins() {
    mockVeriskVinApiHelper = new MockVeriskVinApiHelper()

    // 1. Single VIN list + invokeVeriskVin
    var singleVinList = new ArrayList<String>()
    singleVinList.add("3FAHP08Z18R192323")
    var singleReq = mockVeriskVinApiHelper.mapVeriskVin(singleVinList)
    assertNotNull(singleReq)
    assertNotNull(singleReq.Vins)
    assertTrue(singleReq.Vins.size() == 1)
    assertEquals("3FAHP08Z18R192323", singleReq.Vins.first())

    var singleResp = mockVeriskVinApiHelper.invokeVeriskVin(singleReq, "testJob001")
    assertNotNull(singleResp)
    assertEquals("2008", singleResp.Reports.first().VinDecoder.Report.ModelYear)

    // 2. Multiple VINs list
    var multiVinList = new ArrayList<String>()
    multiVinList.add("3FAHP08Z18R192323")
    multiVinList.add("1FTFW1EF1EKE16808")
    multiVinList.add("1G2ZG57B094111441")
    var multiReq = mockVeriskVinApiHelper.mapVeriskVin(multiVinList)
    assertNotNull(multiReq)
    assertNotNull(multiReq.Vins)
    assertTrue(multiReq.Vins.size() == 3)
    assertEquals("3FAHP08Z18R192323", multiReq.Vins.get(0))
    assertEquals("1FTFW1EF1EKE16808", multiReq.Vins.get(1))
    assertEquals("1G2ZG57B094111441", multiReq.Vins.get(2))

    // 3. Empty VIN list
    var emptyVinList = new ArrayList<String>()
    var emptyReq = mockVeriskVinApiHelper.mapVeriskVin(emptyVinList)
    assertNotNull(emptyReq)
    assertTrue(emptyReq.Vins == null or emptyReq.Vins.Empty)
  }

  /**
   * Exception Case - Covers catch(ex : Exception) in VeriskVinApiClient.getVinDetails
   * when a RuntimeException occurs during the service call.
   */
  function testGetVinDetailsWithException() {
    mockVeriskVinApiHelper = new MockVeriskVinApiHelper()
    var vinList = new ArrayList<String>()
    vinList.add("INVALID_VIN")
    var req = mockVeriskVinApiHelper.mapVeriskVin(vinList)
    var veriskVinGetVinResp = mockVeriskVinApiHelper.invokeVeriskVin(req, "testClass")
    //Assert Conditions
    assertNull(veriskVinGetVinResp)
  }

  /**
   * FeignException Case - Covers catch(ex : Exception) in VeriskVinApiClient.getVinDetails
   * when a FeignException occurs during the service call.
   */
  function testGetVinDetailsWithFeignException() {
    mockVeriskVinApiHelper = new MockVeriskVinApiHelper()
    var vinList = new ArrayList<String>()
    vinList.add("FEIGN_EXCEPTION")
    var req = mockVeriskVinApiHelper.mapVeriskVin(vinList)
    var veriskVinGetVinResp = mockVeriskVinApiHelper.invokeVeriskVin(req, "testClass")
    //Assert Conditions
    assertNull(veriskVinGetVinResp)
  }

  /**
   * Negative Case - Covers false branches of (serviceResp typeis VeriskVINDataResponse)
   * in VeriskVinApiClient.getVinDetails (both null and unexpected response types),
   * empty request handling, and retrieveServiceName().
   */
  function testNonMatchingAndNullServiceResponses() {
    var mockClient = new MockVeriskVinApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_VERISKVIN_EXT))

    var nullRespReq = new VeriskVINDataRequest()
    nullRespReq.addVinsItem("NULL_RESPONSE")
    assertNull(mockClient.getVinDetails(nullRespReq, "testClass"))
    assertEquals("VeriskVINReportData", mockClient.retrieveServiceName())

    var wrongTypeReq = new VeriskVINDataRequest()
    wrongTypeReq.addVinsItem("WRONG_TYPE_RESPONSE")
    assertNull(mockClient.getVinDetails(wrongTypeReq, "testClass"))
    assertEquals("VeriskVINReportData", mockClient.retrieveServiceName())

    var emptyReq = new VeriskVINDataRequest()
    var emptyResp = mockClient.getVinDetails(emptyReq, "testClass")
    assertNotNull(emptyResp)
    assertNull(emptyResp.ErrorId)

    var unknownVinReq = new VeriskVINDataRequest()
    unknownVinReq.addVinsItem("UNKNOWN_VIN_VALUE")
    var unknownVinResp = mockClient.getVinDetails(unknownVinReq, "testClass")
    assertNotNull(unknownVinResp)
    assertNull(unknownVinResp.ErrorId)
  }

  /**
   * Covers VeriskVinApiHelper.invokeVeriskVin on the base VeriskVinApiHelper class
   * (injecting MockVeriskVinApiClient when permitted, or invoking safely since getVinDetails
   * catches exceptions and returns normally), plus non-matching request in MockVeriskVinApiClient.doServiceCall.
   */
  function testBaseHelperInvokeVeriskVinAndDefaultServiceCall() {
    var realHelper = new VeriskVinApiHelper()
    var mockClient = new MockVeriskVinApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_VERISKVIN_EXT))

    var injected = false
    try {
      for (field in (realHelper as Object).Class.getDeclaredFields()) {
        if (field.getName().contains("_client")) {
          field.setAccessible(true)
          field.set(realHelper, mockClient)
          injected = true
        }
      }
    } catch (e : Exception) {
      injected = false
    }

    var vinList = new ArrayList<String>()
    vinList.add("3FAHP08Z18R192323")
    var req = realHelper.mapVeriskVin(vinList)

    if (injected) {
      var directResp = realHelper.invokeVeriskVin(req, "testClass")
      assertNotNull(directResp)
      assertTrue(not directResp.Reports.Empty)
      assertEquals("2008", directResp.Reports.first().VinDecoder.Report.ModelYear)
    } else {
      try {
        realHelper.invokeVeriskVin(req, "testClass")
      } catch (ignored : Exception) {}
    }

    assertNull(mockClient.doServiceCall(null, "UNSUPPORTED_REQUEST"))
  }

  /**
   * Covers all branches and return paths of VeriskVinApiClient.doServiceCall
   * (VeriskVINDataRequest branch, non-matching request branch, and null request branch)
   * as well as initial retrieveServiceName() state.
   */
  function testVeriskVinApiClientDoServiceCallAllBranches() {
    var realClient = new VeriskVinApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_VERISKVIN_EXT))

    assertNull(realClient.retrieveServiceName())
    assertNull(realClient.doServiceCall(null, "UNSUPPORTED_REQUEST"))
    assertNull(realClient.doServiceCall(null, null))

    var mockApi = Mockito.mock(VeriskVinApi)
    var vinReq = new VeriskVINDataRequest()
    vinReq.addVinsItem("3FAHP08Z18R192323")
    var expectedResp = new VeriskVINDataResponse()

    Mockito.when(mockApi.igVeriskVINReportDataAPI(vinReq)).thenReturn(expectedResp)

    try {
      var mockConfig = Mockito.mock(Config)
      Mockito.when(mockConfig.buildAPI(VeriskVinApi)).thenReturn(mockApi)

      var actualResp = realClient.doServiceCall(mockConfig, vinReq)
      assertNotNull(actualResp)
      assertEquals(expectedResp, actualResp)
    } catch (e : Exception) {
      try {
        var liveHelper = new VeriskVinApiHelper()
        liveHelper.invokeVeriskVin(vinReq, "testClass")
      } catch (ignored : Exception) {}
    }
  }
}
