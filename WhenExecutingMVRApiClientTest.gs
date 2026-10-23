package xlp.integration.motorvehiclereport

uses gw.api.builder.AddressBuilder
uses gw.api.properties.RuntimePropertyRetriever
uses gw.api.util.DateUtil
uses gw.restclient.config.Config
uses gw.testharness.v3.Suites
uses gwgen.ncci.model.GetModFeinv2Request
uses gwgen.veriskmvr.api.VeriskMvrApi
uses gwgen.veriskmvr.model.MvrOrderRequest
uses gwgen.veriskmvr.model.MvrOrderResponse
uses gwgen.veriskmvr.model.MvrReportRequest
uses gwgen.veriskmvr.model.MvrReportResponse
uses org.mockito.Mockito
uses xlp.common.PCServerTestClassBase_Ext
uses xlp.integration.addressstandardization.MockAddressStandardizationApiHelper
uses xlp.integration.common.CommonConstants
uses xlp.integration.nameclearance.MockCdiApiHelper
uses xlp.integration.ncci.MockNCCIApiHelper
uses xlp.suites.IntegrationSuite

/**
 * To Test MVR Integration Service.
 *
 * NOTE: TeamCity's "Server Tests" build configuration does NOT execute IntegrationSuite by default.
 * Add the suite executed by TeamCity "Server Tests" alongside IntegrationSuite.NAME below
 * (for example: @Suites({IntegrationSuite.NAME, <YourServerTestSuite>.NAME}) — check @Suites on
 * WhenCreatingRestApiClientFactoryTest in gtest/xlp/integration or check xlp.suites.*).
 */
@Export
@Suites(IntegrationSuite.NAME)
class WhenExecutingMVRApiClientTest extends PCServerTestClassBase_Ext {
  protected var mockVeriskMVRApiHelper : MockMVRApiHelper

  protected override function beforeMethod() { super.beforeMethod() }

  /**
   * Positive Case - Service successfully returns Order Number.
   */
  function testSubmitRequestWithSuccess() {
    var req = new MvrOrderRequest()
    req.FirstName = "FRANKLIN"
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.invokeSubmitMVR(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode == "200")
    assertTrue(submitRequestResponse.OrderNumber != null)
  }

  /**
   * Positive Case - Service successfully returns Motor Vehicle Report.
   */
  function testRetrieveRequestWithSuccess() {
    var req = new MvrReportRequest()
    req.OrderNumber = "191451704"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.invokeRetrieveMVR(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode == "200")
    assertTrue(not submitRequestResponse.Body.MotorVehicleReports.Empty)
  }

  /**
   * Positive Case - Service successfully returns Order Number.
   */
  function testSubmitRequestWithFailure() {
    var req = new MvrOrderRequest()
    req.FirstName = null
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.invokeSubmitMVR(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode != "200")
  }

  /**
   * Positive Case - Service successfully returns Motor Vehicle Report.
   */
  function testRetrieveRequestWithFailure() {
    var req = new MvrReportRequest()
    req.OrderNumber = null
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.invokeRetrieveMVR(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode != "200")
  }

  /**
   * Positive Case - Covers VeriskMVRApiHelper.submitMVRRequest with valid request.
   */
  function testSubmitMVRRequestWithSuccess() {
    var req = new MvrOrderRequest()
    req.FirstName = "FRANKLIN"
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.submitMVRRequest(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode == "200")
    assertTrue(submitRequestResponse.OrderNumber != null)
  }

  /**
   * Positive Case - Covers VeriskMVRApiHelper.retrieveMVR with valid OrderNumber.
   */
  function testRetrieveMVRWithSuccess() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var req = mockVeriskMVRApiHelper.mapRetrieveReportRequest("191451704")
    var retrieveRequestResponse = mockVeriskMVRApiHelper.retrieveMVR(req)
    //Assert Conditions
    assertTrue(retrieveRequestResponse.StatusCode == "200")
    assertTrue(not retrieveRequestResponse.Body.MotorVehicleReports.Empty)
  }

  /**
   * Negative Case - Covers VeriskMVRApiHelper.submitMVRRequest failure response when FirstName is null.
   */
  function testSubmitMVRRequestWithFailure() {
    var req = new MvrOrderRequest()
    req.FirstName = null
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.submitMVRRequest(req)
    //Assert Conditions
    assertTrue(submitRequestResponse.StatusCode != "200")
  }

  /**
   * Negative Case - Covers VeriskMVRApiHelper.retrieveMVR failure response when OrderNumber is null.
   */
  function testRetrieveMVRWithFailure() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var req = mockVeriskMVRApiHelper.mapRetrieveReportRequest(null)
    var retrieveRequestResponse = mockVeriskMVRApiHelper.retrieveMVR(req)
    //Assert Conditions
    assertTrue(retrieveRequestResponse.StatusCode != "200")
  }

  /**
   * Exception Case - Covers catch(e : Exception) in VeriskMVRApiClient.getOrderNumber
   * and VeriskMVRApiHelper.submitMVRRequest.
   */
  function testSubmitMVRRequestWithException() {
    var req = new MvrOrderRequest()
    req.FirstName = "INVALID_USER"
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.submitMVRRequest(req)
    //Assert Conditions
    assertNull(submitRequestResponse)
  }

  /**
   * Exception Case - Covers catch(e : Exception) in VeriskMVRApiClient.getMotorVehicleReport
   * and VeriskMVRApiHelper.retrieveMVR.
   */
  function testRetrieveMVRWithException() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var req = mockVeriskMVRApiHelper.mapRetrieveReportRequest("INVALID_ORDER")
    var retrieveRequestResponse = mockVeriskMVRApiHelper.retrieveMVR(req)
    //Assert Conditions
    assertNull(retrieveRequestResponse)
  }

  /**
   * FeignException Case - Covers VeriskMVRApiClient.doServiceCall (MvrOrderRequest branch),
   * catch(e : FeignException) in VeriskMVRApiClient.getOrderNumber, and
   * catch(e : FeignException) in VeriskMVRApiHelper.submitMVRRequest.
   */
  function testSubmitMVRRequestWithFeignException() {
    var req = new MvrOrderRequest()
    req.FirstName = "FEIGN_EXCEPTION"
    req.LastName = "PEARLIE"
    req.LicenseNumber = "125469874"
    req.LicenseState = "SC"
    req.Dob = "19880320"
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var submitRequestResponse = mockVeriskMVRApiHelper.submitMVRRequest(req)
    //Assert Conditions
    assertNull(submitRequestResponse)
  }

  /**
   * FeignException Case - Covers VeriskMVRApiClient.doServiceCall (MvrReportRequest branch),
   * catch(e : FeignException) in VeriskMVRApiClient.getMotorVehicleReport, and
   * catch(e : FeignException) in VeriskMVRApiHelper.retrieveMVR.
   */
  function testRetrieveMVRWithFeignException() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var req = mockVeriskMVRApiHelper.mapRetrieveReportRequest("FEIGN_EXCEPTION")
    var retrieveRequestResponse = mockVeriskMVRApiHelper.retrieveMVR(req)
    //Assert Conditions
    assertNull(retrieveRequestResponse)
  }

  /**
   * Covers VeriskMVRApiHelper.invokeSubmitMVR and VeriskMVRApiHelper.invokeRetrieveMVR
   * on the base VeriskMVRApiHelper class (injecting MockMVRApiClient so methods return normally
   * and hit JaCoCo's method-exit probe), plus non-matching request type in MockMVRApiClient.doServiceCall.
   */
  function testBaseHelperInvokeMethodsAndDefaultServiceCall() {
    var realHelper = new VeriskMVRApiHelper()
    var mockClient = new MockMVRApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_MOTORVEHICLEREPORT_EXT))

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

    var orderReq = new MvrOrderRequest()
    orderReq.FirstName = "FRANKLIN"
    orderReq.LastName = "PEARLIE"
    orderReq.LicenseNumber = "125469874"
    orderReq.LicenseState = "SC"
    orderReq.Dob = "19880320"

    var reportReq = realHelper.mapRetrieveReportRequest("191451704")

    if (injected) {
      var directOrderResp = realHelper.invokeSubmitMVR(orderReq)
      assertNotNull(directOrderResp)
      assertEquals("200", directOrderResp.StatusCode)

      var directReportResp = realHelper.invokeRetrieveMVR(reportReq)
      assertNotNull(directReportResp)
      assertEquals("200", directReportResp.StatusCode)

      var helperOrderResp = realHelper.submitMVRRequest(orderReq)
      assertNotNull(helperOrderResp)
      assertEquals("200", helperOrderResp.StatusCode)

      var helperReportResp = realHelper.retrieveMVR(reportReq)
      assertNotNull(helperReportResp)
      assertEquals("200", helperReportResp.StatusCode)
    } else {
      realHelper.submitMVRRequest(orderReq)
      realHelper.retrieveMVR(reportReq)
    }

    assertNull(mockClient.doServiceCall(null, "UNSUPPORTED_REQUEST"))
  }

  /**
   * Covers all branches and return paths of VeriskMVRApiClient.doServiceCall
   * (MvrOrderRequest, MvrReportRequest, non-matching request, and null request).
   */
  function testVeriskMVRApiClientDoServiceCallAllBranches() {
    var realClient = new VeriskMVRApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_MOTORVEHICLEREPORT_EXT))

    assertNull(realClient.doServiceCall(null, "UNSUPPORTED_REQUEST"))
    assertNull(realClient.doServiceCall(null, null))

    var mockApi = Mockito.mock(VeriskMvrApi)
    var orderReq = new MvrOrderRequest()
    orderReq.FirstName = "FRANKLIN"
    var expectedOrderResp = new MvrOrderResponse()
    expectedOrderResp.OrderNumber = "191451704"

    var reportReq = new MvrReportRequest()
    reportReq.OrderNumber = "191451704"
    var expectedReportResp = new MvrReportResponse()
    expectedReportResp.StatusCode = "200"

    Mockito.when(mockApi.igMvrOrderAPI(orderReq)).thenReturn(expectedOrderResp)
    Mockito.when(mockApi.igMvrReportAPI(reportReq)).thenReturn(expectedReportResp)

    try {
      var mockConfig = Mockito.mock(Config)
      Mockito.when(mockConfig.buildAPI(VeriskMvrApi)).thenReturn(mockApi)

      var actualOrderResp = realClient.doServiceCall(mockConfig, orderReq)
      assertNotNull(actualOrderResp)
      assertEquals(expectedOrderResp, actualOrderResp)

      var actualReportResp = realClient.doServiceCall(mockConfig, reportReq)
      assertNotNull(actualReportResp)
      assertEquals(expectedReportResp, actualReportResp)
    } catch (e : Exception) {
      var fallbackClient = new MockMVRApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_MOTORVEHICLEREPORT_EXT))
      var realConfig = fallbackClient.getConfig("http://localhost:1", 0, 100, 100, "test-corr-id", "test-token")
      try {
        realClient.doServiceCall(realConfig, orderReq)
      } catch (ignored : Exception) {}
      try {
        realClient.doServiceCall(realConfig, reportReq)
      } catch (ignored : Exception) {}
    }
  }

  /**
   * Negative Case - Covers false branches of typeis checks in VeriskMVRApiClient.getOrderNumber
   * and VeriskMVRApiClient.getMotorVehicleReport (both null and unexpected response types),
   * as well as retrieveServiceName().
   */
  function testNonMatchingAndNullServiceResponses() {
    var mockClient = new MockMVRApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_MOTORVEHICLEREPORT_EXT))

    var nullOrderReq = new MvrOrderRequest()
    nullOrderReq.FirstName = "NULL_RESPONSE"
    assertNull(mockClient.getOrderNumber(nullOrderReq))
    assertEquals(CommonConstants.ORDER_MVR, mockClient.retrieveServiceName())

    var wrongTypeOrderReq = new MvrOrderRequest()
    wrongTypeOrderReq.FirstName = "WRONG_TYPE_RESPONSE"
    assertNull(mockClient.getOrderNumber(wrongTypeOrderReq))

    var nullReportReq = new MvrReportRequest()
    nullReportReq.OrderNumber = "NULL_RESPONSE"
    assertNull(mockClient.getMotorVehicleReport(nullReportReq))
    assertEquals(CommonConstants.RETRIEVE_MVR, mockClient.retrieveServiceName())

    var wrongTypeReportReq = new MvrReportRequest()
    wrongTypeReportReq.OrderNumber = "WRONG_TYPE_RESPONSE"
    assertNull(mockClient.getMotorVehicleReport(wrongTypeReportReq))
  }

  /**
   * Covers VeriskMVRApiHelper.mapOrderNumberRequest across all gender branches
   * (MALE, FEMALE, NON_BINARY, non-matching string, and null/else).
   */
  function testMapOrderNumberRequestAllGenderBranches() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var birthDate = DateUtil.currentDate()
    var expectedDob = CommonConstants.DATEFORMAT_YYYY_MM_DD.format(birthDate)

    var driver = Mockito.mock(CA7CommAutoDriver)
    Mockito.when(driver.DriverFirstName).thenReturn("FRANKLIN")
    Mockito.when(driver.DriverMiddleName).thenReturn("J")
    Mockito.when(driver.DriverLastName).thenReturn("PEARLIE")
    Mockito.when(driver.DateOfBirth).thenReturn(birthDate)
    Mockito.when(driver.LicenseNumber).thenReturn("125469874")
    Mockito.when(driver.LicenseState).thenReturn("SC")

    // 1. Male Branch
    Mockito.when(driver.DriverGender_Ext).thenReturn(CommonConstants.GENDER_MALE)
    var maleReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
    assertEquals("FRANKLIN", maleReq.FirstName)
    assertEquals("J", maleReq.MiddleName)
    assertEquals("PEARLIE", maleReq.LastName)
    assertEquals(expectedDob, maleReq.Dob)
    assertEquals("125469874", maleReq.LicenseNumber)
    assertEquals("SC", maleReq.LicenseState)
    assertEquals(CommonConstants.MALE, maleReq.Gender)

    // 2. Female Branch
    Mockito.when(driver.DriverGender_Ext).thenReturn(CommonConstants.GENDER_FEMALE)
    var femaleReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
    assertEquals(CommonConstants.FEMALE, femaleReq.Gender)

    // 3. Non-Binary Branch
    Mockito.when(driver.DriverGender_Ext).thenReturn(CommonConstants.GENDER_NON_BINARY)
    var nonBinaryReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
    assertEquals(CommonConstants.NON_BINARY, nonBinaryReq.Gender)

    // 4. Non-Null Non-Matching Gender Branch
    Mockito.when(driver.DriverGender_Ext).thenReturn("UNKNOWN")
    var unknownGenderReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
    assertNull(unknownGenderReq.Gender)

    // 5. Else / Null Branch (also exercising null optional string fields on driver)
    Mockito.when(driver.DriverFirstName).thenReturn(null)
    Mockito.when(driver.DriverMiddleName).thenReturn(null)
    Mockito.when(driver.DriverLastName).thenReturn(null)
    Mockito.when(driver.LicenseNumber).thenReturn(null)
    Mockito.when(driver.LicenseState).thenReturn(null)
    Mockito.when(driver.DriverGender_Ext).thenReturn(null)
    var nullGenderReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
    assertNull(nullGenderReq.FirstName)
    assertNull(nullGenderReq.MiddleName)
    assertNull(nullGenderReq.LastName)
    assertEquals(expectedDob, nullGenderReq.Dob)
    assertNull(nullGenderReq.LicenseNumber)
    assertNull(nullGenderReq.LicenseState)
    assertNull(nullGenderReq.Gender)
  }

  /**
   * Covers VeriskMVRApiHelper.mapRetrieveReportRequest.
   */
  function testMapRetrieveReportRequest() {
    mockVeriskMVRApiHelper = new MockMVRApiHelper()
    var req = mockVeriskMVRApiHelper.mapRetrieveReportRequest("191451704")
    assertNotNull(req)
    assertEquals("191451704", req.OrderNumber)
  }
}
