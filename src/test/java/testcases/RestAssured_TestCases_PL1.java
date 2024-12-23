package testcases;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import coreUtilities.utils.FileOperations;
import rest.ApiUtil;
import rest.CustomResponse;

public class RestAssured_TestCases_PL1 {

	FileOperations fileOperations = new FileOperations();
	private final String EXCEL_FILE_PATH = "src/main/resources/config.xlsx"; // Path to the Excel file
	private final String FILEPATH = "src/main/java/rest/ApiUtil.java";
	ApiUtil apiUtil;

	@Test(priority = 1, groups = { "PL1" }, description = "1. Send a GET request to get list of stocks\n"
			+ "2. Validate that all the ItemId, ItemName, and GenericName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllStocksTest() throws IOException {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getAllStocks("/PharmacyStock/AllStockDetails", null);

		// Validate the implementation of getAllStocks
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllStocks",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAllStocks must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllStocks", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each stock entry has non-null fields
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");
		results.forEach(stock -> {
			Assert.assertNotNull(stock.get("ItemId"), "ItemId should not be null.");
			Assert.assertNotNull(stock.get("ItemName"), "ItemName should not be null.");
			Assert.assertNotNull(stock.get("GenericName"), "GenericName should not be null.");
		});

		// Print response for debugging
		System.out.println("All Stocks Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 2, groups = { "PL1" }, description = "1. Send a GET request to get details of main store\n"
			+ "2. Validate that all StoreId are not null.\n" + "3. Verify the response status code is 200.")
	public void getMainStoreTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getMainStore("/PharmacySettings/MainStore", null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getMainStore",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getMainStore must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getMainStore", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate Results fields
		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertNotNull(results.get("StoreId"), "StoreId should not be null.");
		Assert.assertNotNull(results.get("Category"), "Category should not be null.");
		Assert.assertNotNull(results.get("IsActive"), "IsActive should not be null.");

		// Print the response
		System.out.println("Main Store Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 3, groups = {
			"PL1" }, description = "1. Send a GET request to get requisition list by date range\n"
					+ "2. Validate that RequisitionNo, RequisitionStatus are not null and requisitionIds are unique.\n"
					+ "3. Verify the response status code is 200.")
	public void getRequisitionByDateRangeTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-19";

		CustomResponse customResponse = apiUtil.getRequisitionByDateRange(
				"/DispensaryRequisition/Dispensary/1?FromDate=" + fromDate + "&ToDate=" + toDate, null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getRequisitionByDateRange", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getRequisitionByDateRange must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getRequisitionByDateRange", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate requisition list
		List<Map<String, Object>> requisitionList = customResponse.getListResults();
		Assert.assertFalse(requisitionList.isEmpty(), "Requisition list should not be empty.");

		Set<Integer> uniqueRequisitionIds = requisitionList.stream().map(req -> (Integer) req.get("RequisitionId"))
				.collect(Collectors.toSet());

		Assert.assertEquals(uniqueRequisitionIds.size(), requisitionList.size(), "Requisition IDs should be unique.");

		requisitionList.forEach(requisition -> {
			Assert.assertNotNull(requisition.get("RequistionNo"), "RequistionNo should not be null.");
			Assert.assertNotNull(requisition.get("RequisitionStatus"), "RequisitionStatus should not be null.");
		});

		// Print the response
		System.out.println("Requisition Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 4, groups = { "PL1" }, description = "1. Send a GET request to get patient consumptions list\n"
			+ "2. Validate that all PatientId, HospitalNo, and PatientVisitId are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getPatientConsumptionsTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getPatientConsumptions("/PatientConsumption/PatientConsumptions", null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientConsumptions", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getPatientConsumptions must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getPatientConsumptions", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate Results fields
		List<Map<String, Object>> results = customResponse.getListResults();
		Assert.assertFalse(results.isEmpty(), "Results should not be empty.");
		results.forEach(patient -> {
			Assert.assertNotNull(patient.get("PatientId"), "PatientId should not be null.");
			Assert.assertNotNull(patient.get("HospitalNo"), "HospitalNo should not be null.");
			Assert.assertNotNull(patient.get("PatientVisitId"), "PatientVisitId should not be null.");
		});

		// Print the response
		System.out.println("Patient Consumptions Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 5, groups = {
			"PL1" }, description = "1. Send a GET request to get patient consumption information\n"
					+ "2. Validate that PatientName, HospitalNo, and StoreId are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void getPatientConsumptionInfoTest() throws IOException {
		apiUtil = new ApiUtil();

		String endpoint = "/PatientConsumption/PatientConsumptionInfo?PatientId=114&patientVisitId=53";

		CustomResponse customResponse = apiUtil.getPatientConsumptionInfoByPatientIdAndVisitId(endpoint, null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientConsumptionInfoByPatientIdAndVisitId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getPatientConsumptionInfoByPatientIdAndVisitId must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getPatientConsumptionInfoByPatientIdAndVisitId",
				customResponse), "Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate Results fields
		Map<String, Object> results = customResponse.getMapResults();
		Map<String, Object> patientConsumption = (Map<String, Object>) results.get("PatientConsumption");

		// Null checks for PatientConsumption
		Assert.assertNotNull(patientConsumption, "'PatientConsumption' section is missing in Results.");

		// Field validations
		Assert.assertNotNull(patientConsumption.get("PatientName"), "PatientName should not be null.");
		Assert.assertNotNull(patientConsumption.get("HospitalNo"), "HospitalNo should not be null.");
		Assert.assertNotNull(patientConsumption.get("StoreId"), "StoreId should not be null.");

		// Print the response
		System.out.println("Patient Consumption Info Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 6, groups = { "PL1" }, description = "Retrieve and validate Billing Scheme By Scheme ID.")
	public void getBillingSchemeBySchemeIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String schemeId = "4";

		CustomResponse customResponse = apiUtil
				.getBillingSchemeBySchemeId("/PatientConsumption/PharmacyIpBillingScheme?schemeId=" + schemeId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getBillingSchemeBySchemeId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getBillingSchemeBySchemeId method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertNotNull(results.get("SchemeCode"), "SchemeCode should not be null.");
		Assert.assertEquals(String.valueOf(results.get("SchemeId")), schemeId,
				"SchemeId should match the requested value.");

		System.out.println("Billing Scheme Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 7, groups = { "PL1" }, description = "Retrieve and validate Billing Summary By Patient ID.")
	public void getBillingSummaryByPatientIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "114";

		CustomResponse customResponse = apiUtil
				.getBillingSummaryByPatientId("/PharmacySales/PatientBillingSummary?patientId=" + patientId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getBillingSummaryByPatientId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getBillingSummaryByPatientId method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertEquals(String.valueOf(results.get("PatientId")), patientId,
				"PatientId should match the requested value.");
		Assert.assertNotNull(results.get("TotalDue"), "TotalDue should not be null.");

		System.out.println("Billing Summary Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 8, groups = {
			"PL1" }, description = "Retrieve and validate Patient Consumptions List By Patient ID.")
	public void getConsumptionsListOfAPatientByIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "114";
		String patientVisitId = "53";

		CustomResponse customResponse = apiUtil
				.getConsumptionsListOfAPatientById("/PatientConsumption/ConsumptionsOfPatient?patientId=" + patientId
						+ "&patientVisitId=" + patientVisitId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getConsumptionsListOfAPatientById", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getConsumptionsListOfAPatientById method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		List<Map<String, Object>> results = customResponse.getListResults();
		Set<Integer> uniqueIds = new HashSet<>();
		for (Map<String, Object> result : results) {
			Assert.assertNotNull(result.get("TotalAmount"), "TotalAmount should not be null.");
			uniqueIds.add((Integer) result.get("PatientConsumptionId"));
		}
		Assert.assertEquals(results.size(), uniqueIds.size(), "PatientConsumptionId values should be unique.");

		System.out.println("Patient Consumptions List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 9, groups = { "PL1" }, description = "Retrieve and validate the return consumptions list.")
	public void getReturnConsumptionsListTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getReturnConsumptionsList("/PatientConsumption/Returns", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getReturnConsumptionsList", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getReturnConsumptionsList method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate unique ConsumptionReturnReceiptNo and non-null PatientId
		List<Map<String, Object>> results = customResponse.getListResults();
		Set<Integer> uniqueReceiptNos = new HashSet<>();
		for (Map<String, Object> result : results) {
			Integer receiptNo = (Integer) result.get("ConsumptionReturnReceiptNo");
			Integer patientId = (Integer) result.get("PatientId");

			Assert.assertNotNull(receiptNo, "ConsumptionReturnReceiptNo should not be null.");
			Assert.assertNotNull(patientId, "PatientId should not be null.");
			uniqueReceiptNos.add(receiptNo);
		}
		Assert.assertEquals(uniqueReceiptNos.size(), results.size(),
				"ConsumptionReturnReceiptNo values should be unique.");

		System.out.println("Return Consumptions List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 10, groups = { "PL1" }, description = "Retrieve and validate the list of discharged patients.")
	public void getDischargedPatientsTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-19";

		CustomResponse customResponse = apiUtil.getDischargedPatients(
				"/Admission/DischargedPatients?admissionStatus=discharged&FromDate=" + fromDate + "&ToDate=" + toDate,
				null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getDischargedPatients",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getDischargedPatients method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate unique PatientVisitId and PatientAdmissionId, and non-null PatientId
		List<Map<String, Object>> results = customResponse.getListResults();
		Set<Integer> uniqueVisitIds = new HashSet<>();
		Set<Integer> uniqueAdmissionIds = new HashSet<>();

		for (Map<String, Object> result : results) {
			Integer visitId = (Integer) result.get("PatientVisitId");
			Integer admissionId = (Integer) result.get("PatientAdmissionId");
			Integer patientId = (Integer) result.get("PatientId");

			Assert.assertNotNull(visitId, "PatientVisitId should not be null.");
			Assert.assertNotNull(admissionId, "PatientAdmissionId should not be null.");
			Assert.assertNotNull(patientId, "PatientId should not be null.");

			uniqueVisitIds.add(visitId);
			uniqueAdmissionIds.add(admissionId);
		}
		Assert.assertEquals(uniqueVisitIds.size(), results.size(), "PatientVisitId values should be unique.");
		Assert.assertEquals(uniqueAdmissionIds.size(), results.size(), "PatientAdmissionId values should be unique.");

		System.out.println("Discharged Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 11, groups = { "PL1" }, description = "Retrieve and validate the list of admitted patients.")
	public void getAdmittedPatientsTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getAdmittedPatients("/IpBilling/AdmittedPatients", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAdmittedPatients",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAdmittedPatients method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract results
		List<Map<String, Object>> results = customResponse.getListResults();
		Set<Integer> uniquePatientIds = new HashSet<>();
		Set<Integer> uniqueVisitIds = new HashSet<>();

		for (Map<String, Object> result : results) {
			Integer patientId = (Integer) result.get("PatientId");
			Integer visitId = (Integer) result.get("VisitId");
			Object dischargeDate = result.get("DischargeDate");

			Assert.assertNotNull(patientId, "PatientId should not be null.");
			Assert.assertNotNull(visitId, "VisitId should not be null.");
			Assert.assertNull(dischargeDate, "DischargeDate should be null for admitted patients.");

			uniquePatientIds.add(patientId);
			uniqueVisitIds.add(visitId);
		}

		// Validate uniqueness
		Assert.assertEquals(uniquePatientIds.size(), results.size(), "PatientId values should be unique.");
		Assert.assertEquals(uniqueVisitIds.size(), results.size(), "VisitId values should be unique.");

		System.out.println("Admitted Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 12, groups = { "PL1" }, description = "Retrieve and validate IPD patients by patient name.")
	public void searchIpdPatientByPatientIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientName = "Devid8 Roy8";

		CustomResponse customResponse = apiUtil
				.searchIpdPatientByPatientId("/Patient/IPDPatientSearch?search=" + patientName, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"searchIpdPatientByPatientId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "searchIpdPatientByPatientId method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract results
		List<Map<String, Object>> results = customResponse.getListResults();

		for (Map<String, Object> result : results) {
			Assert.assertNotNull(result.get("PatientId"), "PatientId should not be null.");
			Assert.assertNotNull(result.get("PatientCode"), "PatientCode should not be null.");
		}

		System.out.println("IPD Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 13, groups = { "PL1" }, description = "Retrieve and validate patients' provisional information.")
	public void getPatientProvisionalInfoTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getPatientProvisionalInfo("/Billing/PatientsProvisionalInfo", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientProvisionalInfo", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getPatientProvisionalInfo method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract results
		List<Map<String, Object>> results = customResponse.getListResults();

		for (Map<String, Object> result : results) {
			Assert.assertNotNull(result.get("PatientId"), "PatientId should not be null.");
			Assert.assertNotNull(result.get("PatientCode"), "PatientCode should not be null.");
		}

		System.out.println("Provisional Information Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 14, groups = {
			"PL1" }, description = "Retrieve and validate provisional items list for a specific patient and scheme.")
	public void getProvisionalItemsListByPatientIdAndSchemeIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "188";
		String schemeId = "4";

		// API Call
		CustomResponse customResponse = apiUtil.getProvisionalItemsListByPatientIdAndSchemeId(
				"/Billing/ProvisionalItemsByPatientId?patientId=" + patientId + "&schemeId=" + schemeId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getProvisionalItemsListByPatientIdAndSchemeId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getProvisionalItemsListByPatientIdAndSchemeId method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Map<String, Object>> provisionalItems = customResponse.getListResults();
		Assert.assertNotNull(provisionalItems, "ProvisionalItems list should not be null.");
		Assert.assertTrue(provisionalItems.size() > 0, "ProvisionalItems list should not be empty.");

		// Validate PatientId consistency
		for (Map<String, Object> item : provisionalItems) {
			Assert.assertEquals(item.get("PatientId"), Integer.parseInt(patientId),
					"PatientId in ProvisionalItems should match the requested PatientId.");
		}

		// Print the response
		System.out.println("Provisional Items List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 15, groups = {
			"PL1" }, description = "Retrieve and validate billing invoices within a specific date range.")
	public void getInvoicesByDateRangeTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-21";

		// API Call
		CustomResponse customResponse = apiUtil
				.getInvoicesByDateRange("/Billing/Invoices?FromDate=" + fromDate + "&ToDate=" + toDate, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getInvoicesByDateRange", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getInvoicesByDateRange method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Map<String, Object>> invoices = customResponse.getListResults();
		Assert.assertNotNull(invoices, "Results array should not be null.");
		Assert.assertTrue(invoices.size() > 0, "Results array should contain at least one invoice.");

		// Validate InvoiceNumber and InvoiceCode fields
		for (Map<String, Object> invoice : invoices) {
			Assert.assertNotNull(invoice.get("InvoiceNumber"), "InvoiceNumber should not be null.");
			Assert.assertNotNull(invoice.get("InvoiceCode"), "InvoiceCode should not be null.");
		}

		// Print the response
		System.out.println("Billing Invoices Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 16, groups = { "PL1" }, description = "Retrieve and validate the list of providers.")
	public void getProviderListTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getProviderList("/Billing/GetProviderList", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getProviderList",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getProviderList method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Map<String, Object>> providers = customResponse.getListResults();
		Assert.assertNotNull(providers, "Results array should not be null.");
		Assert.assertTrue(providers.size() > 0, "Results array should contain at least one provider.");

		// Validate EmployeeId and EmployeeName fields
		for (Map<String, Object> provider : providers) {
			Assert.assertNotNull(provider.get("EmployeeId"), "EmployeeId should not be null.");
			Assert.assertNotNull(provider.get("EmployeeName"), "EmployeeName should not be null.");
		}

		// Print the response
		System.out.println("Providers List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 17, groups = { "PL1" }, description = "Retrieve and validate the list of users.")
	public void getUsersListTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getUsersList("/Billing/ListUsers", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getUserslist",
				List.of("given", "then", "extract", "response"));
//		Assert.assertTrue(isValidationSuccessful, "getUserslist method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Map<String, Object>> users = customResponse.getListResults();
		Assert.assertNotNull(users, "Results array should not be null.");
		Assert.assertTrue(users.size() > 0, "Results array should contain at least one user.");

		// Validate UserId, ShortName, and DepartmentName fields
		Set<Integer> userIds = new HashSet<>();
		for (Map<String, Object> user : users) {
			Integer userId = (Integer) user.get("UserId");
			Assert.assertNotNull(userId, "UserId should not be null.");
			Assert.assertTrue(userIds.add(userId), "UserId " + userId + " is not unique.");

			Assert.assertNotNull(user.get("ShortName"), "ShortName should not be null.");
			Assert.assertNotNull(user.get("DepartmentName"), "DepartmentName should not be null.");
		}

		// Print the response
		System.out.println("Users List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 18, groups = { "PL1" }, description = "Retrieve and validate the current fiscal year details.")
	public void getCurrentFiscalYearDetailsTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getCurrentFiscalYearDetails("/Billing/CurrentFiscalYear", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getCurrentFiscalYearDetails", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getCurrentFiscalYearDetails method validation failed.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		Map<String, Object> results = customResponse.getMapResults();
		Assert.assertNotNull(results, "Results object should not be null.");

		// Validate FiscalYearId and FiscalYearName fields
		Assert.assertNotNull(results.get("FiscalYearId"), "FiscalYearId should not be null.");
		Assert.assertNotNull(results.get("FiscalYearName"), "FiscalYearName should not be null.");

		// Print the response
		System.out.println("Current Fiscal Year Response:");
		customResponse.getResponse().prettyPrint();
	}
}
