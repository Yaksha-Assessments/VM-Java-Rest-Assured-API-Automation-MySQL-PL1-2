package testcases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rest.CustomResponse;

public class TestCodeValidator {

	// Method to validate if specific keywords are used in the method's source code
	public static boolean validateTestMethodFromFile(String filePath, String methodName, List<String> keywords)
			throws IOException {
		// Read the content of the test class file
		String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

		// Extract the method body for the specified method using regex
		String methodRegex = "(public\\s+CustomResponse\\s+" + methodName + "\\s*\\(.*?\\)\\s*\\{)([\\s\\S]*?)}";
		Pattern methodPattern = Pattern.compile(methodRegex);
		Matcher methodMatcher = methodPattern.matcher(fileContent);

		if (methodMatcher.find()) {

			String methodBody = fetchBody(filePath, methodName);

			// Now we validate the method body for the required keywords
			boolean allKeywordsPresent = true;

			// Loop over the provided keywords and check if each one is present in the
			// method body
			for (String keyword : keywords) {
				Pattern keywordPattern = Pattern.compile("\\b" + keyword + "\\s*\\(");
				if (!keywordPattern.matcher(methodBody).find()) {
					System.out.println("'" + keyword + "()' is missing in the method.");
					allKeywordsPresent = false;
				}
			}

			return allKeywordsPresent;

		} else {
			System.out.println("Method " + methodName + " not found in the file.");
			return false;
		}
	}

	// This method takes the method name as an argument and returns its body as a
	// String.
	public static String fetchBody(String filePath, String methodName) {
		StringBuilder methodBody = new StringBuilder();
		boolean methodFound = false;
		boolean inMethodBody = false;
		int openBracesCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Check if the method is found by matching method signature
				if (line.contains("public CustomResponse " + methodName + "(")
						|| line.contains("public String " + methodName + "(")
						|| line.contains("public Response " + methodName + "(")) {
					methodFound = true;
				}

				// Once the method is found, start capturing lines
				if (methodFound) {
					if (line.contains("{")) {
						inMethodBody = true;
						openBracesCount++;
					}

					// Capture the method body
					if (inMethodBody) {
						methodBody.append(line).append("\n");
					}

					// Check for closing braces to identify the end of the method
					if (line.contains("}")) {
						openBracesCount--;
						if (openBracesCount == 0) {
							break; // End of method body
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return methodBody.toString();
	}

	public static boolean validateResponseFields(String methodName, CustomResponse customResponse) {
		boolean isValid = true;

		switch (methodName) {
		case "getAllStocks":
			List<String> expectedStockFields = List.of("ItemId", "ItemName", "GenericName", "SalePrice", "CostPrice");

			List<Map<String, Object>> stockResults = customResponse.getResponse().jsonPath().getList("Results");
			if (stockResults == null || stockResults.isEmpty()) {
				isValid = false;
				System.out.println("Results section is missing or empty in the response.");
				break;
			}

			for (int i = 0; i < stockResults.size(); i++) {
				Map<String, Object> stock = stockResults.get(i);
				for (String field : expectedStockFields) {
					if (!stock.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Results[" + i + "]: " + field);
					}
				}
			}

			// Validate top-level fields
			String stockStatusField = customResponse.getResponse().jsonPath().getString("Status");
			if (stockStatusField == null || !stockStatusField.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getMainStore":
			List<String> mainStoreFields = List.of("StoreId", "ParentStoreId", "Category", "IsActive");
			Map<String, Object> mainStoreResults = customResponse.getMapResults();
			for (String field : mainStoreFields) {
				if (!mainStoreResults.containsKey(field)) {
					isValid = false;
					System.out.println("Missing field in Results: " + field);
				}
			}
			break;

		case "getRequisitionByDateRange":
			List<String> requisitionFields = List.of("RequisitionId", "RequistionNo", "RequisitionStatus");
			List<Map<String, Object>> requisitionResults = customResponse.getListResults();
			for (Map<String, Object> requisition : requisitionResults) {
				for (String field : requisitionFields) {
					if (!requisition.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Requisition: " + field);
					}
				}
			}
			break;

		case "getPatientConsumptions":
			List<String> patientConsumptionFields = List.of("PatientId", "HospitalNo", "PatientVisitId");
			List<Map<String, Object>> patientResults = customResponse.getListResults();
			for (Map<String, Object> result : patientResults) {
				for (String field : patientConsumptionFields) {
					if (!result.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Results: " + field);
					}
				}
			}
			break;

		case "getPatientConsumptionInfoByPatientIdAndVisitId":
			// Expected fields for "PatientConsumption" and "PatientConsumptionItems"
			List<String> patientConsumptionField = List.of("PatientId", "PatientName", "HospitalNo", "StoreId");
			List<String> patientConsumptionItemFields = List.of("PatientConsumptionItemId", "ItemId", "ItemName",
					"Quantity", "SalePrice", "TotalAmount", "BatchNo", "ExpiryDate", "StoreId");

			// Retrieve the "Results" section
			Map<String, Object> results = customResponse.getResponse().jsonPath().getMap("Results");

			// Debugging: Print the actual structure
			System.out.println("Actual Results Keys: " + results.keySet());

			// Validate "PatientConsumption" fields
			Map<String, Object> patientConsumption = (Map<String, Object>) results.get("PatientConsumption");
			if (patientConsumption == null) {
				isValid = false;
				System.out.println("Missing 'PatientConsumption' section in Results.");
			} else {
				System.out.println("PatientConsumption Fields: " + patientConsumption.keySet());
				for (String field : patientConsumptionField) {
					if (!patientConsumption.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in PatientConsumption: " + field);
					}
				}
			}

			// Validate "PatientConsumptionItems" array
			List<Map<String, Object>> patientConsumptionItems = (List<Map<String, Object>>) results
					.get("PatientConsumptionItems");
			if (patientConsumptionItems == null || patientConsumptionItems.isEmpty()) {
				isValid = false;
				System.out.println("Missing or empty 'PatientConsumptionItems' in Results.");
			} else {
				for (int i = 0; i < patientConsumptionItems.size(); i++) {
					Map<String, Object> item = patientConsumptionItems.get(i);
					System.out.println("PatientConsumptionItem[" + i + "] Fields: " + item.keySet());
					for (String field : patientConsumptionItemFields) {
						if (!item.containsKey(field)) {
							isValid = false;
							System.out.println("Missing field in PatientConsumptionItems[" + i + "]: " + field);
						}
					}
				}
			}
			break;
		case "getBillingSchemeBySchemeId":
			// Expected fields in "Results" for Billing Scheme
			List<String> billingSchemeFields = List.of("SchemeCode", "SchemeName", "CommunityName", "SchemeId");

			Map<String, Object> billingSchemeResults = customResponse.getMapResults();
			System.out.println("Actual Results Keys for Billing Scheme: " + billingSchemeResults.keySet());

			for (String field : billingSchemeFields) {
				if (!billingSchemeResults.containsKey(field)) {
					isValid = false;
					System.out.println("Missing field in Billing Scheme Results: " + field);
				}
			}
			break;

		case "getBillingSummaryByPatientId":
			// Expected fields in "Results" for Billing Summary
			List<String> billingSummaryFields = List.of("PatientId", "CreditAmount", "ProvisionalAmt", "TotalDue");

			Map<String, Object> billingSummaryResults = customResponse.getMapResults();
			System.out.println("Actual Results Keys for Billing Summary: " + billingSummaryResults.keySet());

			for (String field : billingSummaryFields) {
				if (!billingSummaryResults.containsKey(field)) {
					isValid = false;
					System.out.println("Missing field in Billing Summary Results: " + field);
				}
			}
			break;

		case "getConsumptionsListOfAPatientById":
			// Expected fields in "Results" for Patient Consumption List
			List<String> consumptionFields = List.of("PatientConsumptionId", "ConsumptionReceiptNo", "TotalAmount",
					"ItemName");

			List<Map<String, Object>> consumptionResults = customResponse.getListResults();
			if (consumptionResults == null || consumptionResults.isEmpty()) {
				isValid = false;
				System.out.println("Results section for Patient Consumption List is missing or empty.");
			} else {
				for (int i = 0; i < consumptionResults.size(); i++) {
					Map<String, Object> item = consumptionResults.get(i);
					System.out.println("PatientConsumptionItem[" + i + "] Keys: " + item.keySet());
					for (String field : consumptionFields) {
						if (!item.containsKey(field)) {
							isValid = false;
							System.out.println("Missing field in PatientConsumptionItems[" + i + "]: " + field);
						}
					}
				}
			}
			break;

		case "getReturnConsumptionsList":
			// Expected fields for Return Consumptions
			List<String> returnConsumptionFields = List.of("ConsumptionReturnReceiptNo", "HospitalNo", "PatientId");
			List<Map<String, Object>> returnResults = customResponse.getListResults();

			for (int i = 0; i < returnResults.size(); i++) {
				Map<String, Object> result = returnResults.get(i);
				for (String field : returnConsumptionFields) {
					if (!result.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Return Consumptions Results[" + i + "]: " + field);
					}
				}
			}
			break;

		case "getDischargedPatients":
			// Expected fields for Discharged Patients
			List<String> dischargedFields = List.of("VisitCode", "PatientVisitId", "PatientAdmissionId", "PatientId");
			List<Map<String, Object>> dischargeResults = customResponse.getListResults();

			for (int i = 0; i < dischargeResults.size(); i++) {
				Map<String, Object> result = dischargeResults.get(i);
				for (String field : dischargedFields) {
					if (!result.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Discharged Patients Results[" + i + "]: " + field);
					}
				}
			}
			break;

		case "getAdmittedPatients":
			List<Map<String, Object>> admittedPatients = customResponse.getListResults();
			for (Map<String, Object> result : admittedPatients) {
				if (result.get("PatientId") == null || result.get("VisitId") == null) {
					isValid = false;
					System.out.println("Missing field in Results: " + result);
				}
				if (result.get("DischargeDate") != null) {
					isValid = false;
					System.out.println("DischargeDate should be null: " + result);
				}
			}
			break;

		case "searchIpdPatientByPatientId":
			List<Map<String, Object>> ipdPatients = customResponse.getListResults();
			for (Map<String, Object> result : ipdPatients) {
				if (result.get("PatientId") == null || result.get("PatientCode") == null) {
					isValid = false;
					System.out.println("Missing field in Results: " + result);
				}
			}
			break;

		case "getPatientProvisionalInfo":
			List<Map<String, Object>> provisionalPatients = customResponse.getListResults();
			for (Map<String, Object> result : provisionalPatients) {
				if (result.get("PatientId") == null || result.get("PatientCode") == null) {
					isValid = false;
					System.out.println("Missing field in Results: " + result);
				}
			}
			break;

		case "getProvisionalItemsListByPatientIdAndSchemeId":
			List<String> provisionalFields = List.of("PatientId", "ProvisionalItems");
			Map<String, Object> provisionalResults = customResponse.getMapResults();

			// Check for required fields in the response
			for (String field : provisionalFields) {
				if (!provisionalResults.containsKey(field)) {
					isValid = false;
					System.out.println("Missing field in Results: " + field);
				}
			}

			// Check ProvisionalItems is a valid non-empty list
			List<Map<String, Object>> provisionalItems = (List<Map<String, Object>>) provisionalResults
					.get("ProvisionalItems");
			if (provisionalItems == null || provisionalItems.isEmpty()) {
				isValid = false;
				System.out.println("ProvisionalItems list is null or empty.");
			} else {
				// Validate each item in ProvisionalItems
				for (Map<String, Object> item : provisionalItems) {
					if (!item.containsKey("PatientId") || item.get("PatientId") == null) {
						isValid = false;
						System.out.println("Missing or null PatientId in ProvisionalItems.");
					}
				}
			}
			break;

		case "getInvoicesByDateRange":
			List<String> invoiceFields = List.of("InvoiceNumber", "InvoiceCode");
			List<Map<String, Object>> invoiceResults = customResponse.getListResults();

			// Check Results is not null or empty
			if (invoiceResults == null || invoiceResults.isEmpty()) {
				isValid = false;
				System.out.println("Results array is null or empty.");
			} else {
				// Validate each invoice in Results
				for (Map<String, Object> invoice : invoiceResults) {
					for (String field : invoiceFields) {
						if (!invoice.containsKey(field) || invoice.get(field) == null) {
							isValid = false;
							System.out.println("Missing or null field in Results: " + field);
						}
					}
				}
			}
			break;

		case "getProviderList":
			List<Map<String, Object>> providers = customResponse.getListResults();
			for (Map<String, Object> provider : providers) {
				if (!provider.containsKey("EmployeeId") || provider.get("EmployeeId") == null) {
					isValid = false;
					System.out.println("Missing or null field: EmployeeId");
				}
				if (!provider.containsKey("EmployeeName") || provider.get("EmployeeName") == null) {
					isValid = false;
					System.out.println("Missing or null field: EmployeeName");
				}
			}
			break;

		case "getUserslist":
			List<Map<String, Object>> users = customResponse.getListResults();
			Set<Integer> uniqueUserIds = new HashSet<>();
			for (Map<String, Object> user : users) {
				if (!user.containsKey("UserId") || user.get("UserId") == null) {
					isValid = false;
					System.out.println("Missing or null field: UserId");
				} else if (!uniqueUserIds.add((Integer) user.get("UserId"))) {
					isValid = false;
					System.out.println("Duplicate UserId found: " + user.get("UserId"));
				}
				if (!user.containsKey("ShortName") || user.get("ShortName") == null) {
					isValid = false;
					System.out.println("Missing or null field: ShortName");
				}
				if (!user.containsKey("DepartmentName") || user.get("DepartmentName") == null) {
					isValid = false;
					System.out.println("Missing or null field: DepartmentName");
				}
			}
			break;

		case "getCurrentFiscalYearDetails":
			Map<String, Object> resultss = customResponse.getMapResults();
			if (!resultss.containsKey("FiscalYearId") || resultss.get("FiscalYearId") == null) {
				isValid = false;
				System.out.println("Missing or null field: FiscalYearId");
			}
			if (!resultss.containsKey("FiscalYearName") || resultss.get("FiscalYearName") == null) {
				isValid = false;
				System.out.println("Missing or null field: FiscalYearName");
			}
			break;

		default:
			System.out.println("Method " + methodName + " is not recognized for validation.");
			isValid = false;
		}
		return isValid;
	}

}