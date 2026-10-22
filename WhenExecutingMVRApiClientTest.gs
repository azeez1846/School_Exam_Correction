package xlp.integration.motorvehiclereport

uses gw.api.builder.AddressBuilder
uses gw.api.properties.RuntimePropertyRetriever
uses gw.api.util.DateUtil
uses gw.testharness.v3.Suites
uses gwgen.ncci.model.GetModFeinv2Request
uses gwgen.veriskmvr.model.MvrOrderRequest
uses gwgen.veriskmvr.model.MvrReportRequest
uses org.mockito.Mockito
uses xlp.common.PCServerTestClassBase_Ext
uses xlp.integration.addressstandardization.MockAddressStandardizationApiHelper
uses xlp.integration.common.CommonConstants
uses xlp.integration.nameclearance.MockCdiApiHelper
uses xlp.integration.ncci.MockNCCIApiHelper
uses xlp.suites.IntegrationSuite

/**
 * To Test MVR Integration Service.
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
   * on the base VeriskMVRApiHelper class, as well as non-matching request type in doServiceCall.
   */
  function testBaseHelperInvokeMethodsAndDefaultServiceCall() {
    var realHelper = new VeriskMVRApiHelper()

    var orderReq = new MvrOrderRequest()
    orderReq.FirstName = "FRANKLIN"
    var orderResp = realHelper.submitMVRRequest(orderReq)
    assertNull(orderResp)

    var reportReq = realHelper.mapRetrieveReportRequest("191451704")
    var reportResp = realHelper.retrieveMVR(reportReq)
    assertNull(reportResp)

    var mockClient = new MockMVRApiClient(new RuntimePropertyRetriever(RuntimePropertyGroup.TC_MOTORVEHICLEREPORT_EXT))
    assertNull(mockClient.doServiceCall(null, "UNSUPPORTED_REQUEST"))
  }

  /**
   * Covers VeriskMVRApiHelper.mapOrderNumberRequest across all gender branches
   * (MALE, FEMALE, NON_BINARY, and null/else).
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

    // 4. Else / Null Branch
    Mockito.when(driver.DriverGender_Ext).thenReturn(null)
    var nullGenderReq = mockVeriskMVRApiHelper.mapOrderNumberRequest(driver)
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
