
package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.v1.DbConnect;
import utils.v1.ExcelUtil;
import utils.v1.Miscellaneous;
import utils.v1.Queries;
import utils.v1.Reporting;
import utils.v1.ResponseMessages;
import utils.v1.RetrieveEndPoints;
import utils.v1.TestResultValidation;
import utils.v1.ValidationFields;
import wsMethods.v1.GetResponse;

public class AddressLabelsGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, addressLineNumber, brandAddressLineDescription;

	String effectiveDate, expirationDate, geopl_Id, locale_cd, addressLineNo, addrTargetDate, addrEndDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(AddressLabelsGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		/*
		 * String tokenKey = tokenValues[0]; String tokenVal = token; String
		 * actuatorGraphQLVersionURL =
		 * RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level +
		 * ".graphQL.version"); actuatorGraphQLversion =
		 * resultValidation.versionValidation(fileName, tokenKey, tokenVal,
		 * actuatorGraphQLVersionURL);
		 */
		actuatorGraphQLversion = "1.0.0";
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}

	@Test
	public void TC_01() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGraphQLQuery();
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDBData = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDBData.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}

					List<String> getTempResponse = new ArrayList<>();
					List<String> getTempDB = new ArrayList<>();
					List<String> getSortDB = new ArrayList<>();
					getTempResponse.addAll(getResponseRows);
					getTempDB.addAll(getResultDBData);

					// sort DB record as per JSON response
					for (int i = 0; i < getTempResponse.size(); i += 5) {
						for (int j = 0; j < getTempDB.size(); j += 5) {
							if (getTempResponse.get(i).toString().equals(getTempDB.get(j).toString())) {
								if (getTempResponse.get(i + 1).toString().equals(getTempDB.get(j + 1).toString())
										&& getTempResponse.get(i + 2).toString().equals(getTempDB.get(j + 2).toString())
										&& getTempResponse.get(i + 3).toString().equals(getTempDB.get(j + 3).toString())
										&& getTempResponse.get(i + 4).toString()
												.equals(getTempDB.get(j + 4).toString())) {
									for (int k = 0; k < 5; k++) {
										getSortDB.add(getTempDB.get(j).toString());
										getTempDB.remove(j);
									}
									break;
								}

							}
						}
					}
					List<String> getResultDB = new ArrayList<>();
					getResultDB.addAll(getSortDB);

					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: "
							+ getResultDBData.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDBData.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_02() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id
					+ "\\\"){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdGraphQLQuery(geopl_Id);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_03() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd
					+ "\\\"){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCdGraphQLQuery(geopl_Id,
						locale_cd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_04() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_05() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id
					+ "\\\"){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdGraphQLQuery(geopl_Id);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == 0 && responseRows.size() * fields.size() == 0) {
					test.pass("Records are not available for this geopoliticalId: " + geopl_Id);
					logger.info("Records are not available for this geopoliticalId: " + geopl_Id);
					test.pass("TestCase Passed");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
				} else {
					logger.error("Records are available for this geopoliticalId: " + geopl_Id);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Records are available for this geopoliticalId: " + geopl_Id);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "Getting record for language code: " + geopl_Id);
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_06() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd
					+ "\\\"){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCdGraphQLQuery(geopl_Id,
						locale_cd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == 0 && responseRows.size() * fields.size() == 0) {
					test.pass("Records are not available for this localeCode: " + locale_cd);
					logger.info("Records are not available for this localeCode: " + locale_cd);
					test.pass("TestCase Passed");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
				} else {
					logger.error("Records are available for this localeCode: " + locale_cd);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Records are available for this localeCode: " + locale_cd);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "Getting record for language code: " + locale_cd);
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_07() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == 0 && responseRows.size() * fields.size() == 0) {
					test.pass("Records are not available for this addressLineNumber: " + addressLineNo);
					logger.info("Records are not available for this addressLineNumber: " + addressLineNo);
					test.pass("TestCase Passed");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
				} else {
					logger.error("Records are available for this addressLineNumber: " + addressLineNo);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Records are available for this addressLineNumber: " + addressLineNo);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "Getting record for language code: " + addressLineNo);
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_08() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalI  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.addrLblGeoplIdErrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid geopoliticalId atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid geopoliticalId atrribute in request body");

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", "Expected and Actual error is not matching");
					Assert.fail("Test Failed");
				}

			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_09() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalId  localeCod  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.addrLblLocaleCdErrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid Locale code atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid Locale code atrribute in request body");

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", "Expected and Actual error is not matching");
					Assert.fail("Test Failed");
				}

			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_10() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalId  localeCode  addressLineNumbe  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.addrLblAddrLineNumberErrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid addressLineNumber atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid addressLineNumber atrribute in request body");

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", "Expected and Actual error is not matching");
					Assert.fail("Test Failed");
				}

			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_11() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescriptio  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.addrLblBrandAddrErrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid brandAddressLineDescription atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid brandAddressLineDescription atrribute in request body");

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", "Expected and Actual error is not matching");
					Assert.fail("Test Failed");
				}

			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_12() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels {  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicabl}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.addrLblapplicableErrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid applicable atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid applicable atrribute in request body");

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", "Expected and Actual error is not matching");
					Assert.fail("Test Failed");
				}

			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_13() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){    localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsWithoutGeoplIdGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_localeCode: ", "DB_localeCode: ",
									"Response_addressLineNumber: ", "DB_addressLineNumber: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_localeCode: ", "DB_localeCode: ",
									"Response_addressLineNumber: ", "DB_addressLineNumber: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_14() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId    addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsWithoutLocaleCdGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_addressLineNumber: ", "DB_addressLineNumber: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_addressLineNumber: ", "DB_addressLineNumber: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_15() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId  localeCode    brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsWithoutAddressLineNumberGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ",
									"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
									"Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_16() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId  localeCode  addressLineNumber    applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsWithoutBrandAddressGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_17() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (geopoliticalId:\\\"" + geopl_Id + "\\\", localeCode:\\\""
					+ locale_cd + "\\\" ,  addressLineNumber: " + addressLineNo
					+ "){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  }} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(
						geopl_Id, locale_cd, addressLineNo);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsWithoutApplicalbeFlaggGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}

					}
					if (getResultDB.size() == 0 && responseRows.size() == 0)
						test.fail("Record not present in DB and Response");
					else {
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_18() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (targetDate:\\\"" + addrTargetDate + "\\\", endDate:\\\""
					+ addrEndDate
					+ "\\\" ){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);

				// ***Converting dateFormat according to DB
				DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

				String formatTargetDate = null;
				Date dateTargetDate = null;

				String formatEndDate;
				Date dateEndDate = null;

				try {
					formatTargetDate = addrTargetDate;
					dateTargetDate = srcDf.parse(formatTargetDate);
					formatEndDate = addrEndDate;
					dateEndDate = srcDf.parse(formatEndDate);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				formatTargetDate = destDf.format(dateTargetDate);
				formatTargetDate = formatTargetDate.toUpperCase();

				formatEndDate = destDf.format(dateEndDate);
				formatEndDate = formatEndDate.toUpperCase();

				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelTargetEndDateGraphQLQuery(formatTargetDate,
						formatEndDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDBData = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDBData.size() == 0 && responseRows.size() == 0) {
					test.info("Total number of records response: " + responseRows.size() + " & DB: "
							+ getResultDBData.size());
					test.pass("TestCase Passed");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

				} else {
					logger.error("Total number of records DB: " + getResultDBData.size() + " & Response: "
							+ responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDBData.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_19() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{addressLabels (targetDate:\\\"" + addrTargetDate + "\\\", endDate:\\\""
					+ addrEndDate
					+ "\\\" ){  geopoliticalId  localeCode  addressLineNumber  brandAddressLineDescription  applicable}} \",\"variables\":null,\"operationName\":null}";
			// ***send request and get response
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.addressLabels");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.transactionIdValidation(js, res);
				// ***Converting dateFormat according to DB
				DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

				String formatTargetDate = null;
				Date dateTargetDate = null;

				String formatEndDate;
				Date dateEndDate = null;

				try {
					formatTargetDate = addrTargetDate;
					dateTargetDate = srcDf.parse(formatTargetDate);
					formatEndDate = addrEndDate;
					dateEndDate = srcDf.parse(formatEndDate);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				formatTargetDate = destDf.format(dateTargetDate);
				formatTargetDate = formatTargetDate.toUpperCase();

				formatEndDate = destDf.format(dateEndDate);
				formatEndDate = formatEndDate.toUpperCase();

				// ***get the DB query
				String addressLabelGraphQLQuery = query.addressLabelTargetEndDateGraphQLQuery(formatTargetDate,
						formatEndDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.addressLabelsGraphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDBData = DbConnect.getResultSetFor(addressLabelGraphQLQuery, fields, fileName,
						testCaseID);

				if (getResultDBData.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.addressLabels.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.geopoliticalId[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.localeCode[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.localeCode[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.addressLineNumber[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.addressLabels.addressLineNumber[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.addressLabels.brandAddressLineDescription[" + i + "]"));
						}
						if (StringUtils.isBlank(js.getString("data.addressLabels.applicable[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							String appl_flg = js.getString("data.addressLabels.applicable[" + i + "]");
							if (appl_flg.contains("true"))
								getResponseRows.add("1");
							else
								getResponseRows.add("0");
						}

					}

					List<String> getTempResponse = new ArrayList<>();
					List<String> getTempDB = new ArrayList<>();
					List<String> getSortDB = new ArrayList<>();
					getTempResponse.addAll(getResponseRows);
					getTempDB.addAll(getResultDBData);

					// sort DB record as per JSON response
					for (int i = 0; i < getTempResponse.size(); i += 5) {
						for (int j = 0; j < getTempDB.size(); j += 5) {
							if (getTempResponse.get(i).toString().equals(getTempDB.get(j).toString())) {
								if (getTempResponse.get(i + 1).toString().equals(getTempDB.get(j + 1).toString())
										&& getTempResponse.get(i + 2).toString().equals(getTempDB.get(j + 2).toString())
										&& getTempResponse.get(i + 3).toString().equals(getTempDB.get(j + 3).toString())
										&& getTempResponse.get(i + 4).toString()
												.equals(getTempDB.get(j + 4).toString())) {
									for (int k = 0; k < 5; k++) {
										getSortDB.add(getTempDB.get(j).toString());
										getTempDB.remove(j);
									}
									break;
								}

							}
						}
					}
					List<String> getResultDB = new ArrayList<>();
					getResultDB.addAll(getSortDB);

					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							logger.info("Record " + z + " Validation: Passed");
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload, "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							// ***write result to excelm
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
									"Response_localeCode: ", "DB_localeCode: ", "Response_addressLineNumber: ",
									"DB_addressLineNumber: ", "Response_brandAddressLineDescription: ",
									"DB_brandAddressLineDescription: ", "Response_applicable: ", "DB_applicable: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation: Failed");
							// logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records matching between DB: "
							+ getResultDBData.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDBData.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "");
				Assert.fail("Test Failed");
			}

			logger.info("------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	// ***get the values from test data sheet
	public void testDataFields(String scenarioName, String testCaseId) {
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		try {
			inputData1 = ex.getTestData(scenarioName);
		} catch (IOException e) {
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseId, "", "", "", "", "", "", "", "", "Fail", "Exception: " + e.toString());
			test.fail("Unable to retrieve the test data file/fields");
		}
		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
		geopl_Id = inputData1.get(testCaseId).get("geopoliticalId");
		locale_cd = inputData1.get(testCaseId).get("localeCd");
		addressLineNo = inputData1.get(testCaseId).get("addressLineNumber");
		addrTargetDate = inputData1.get(testCaseId).get("targetDate");
		addrEndDate = inputData1.get(testCaseId).get("endDate");
	}

}
