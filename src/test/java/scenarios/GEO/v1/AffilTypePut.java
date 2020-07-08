package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fedex.jms.client.reader.JMSReader;

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
import wsMethods.v1.PostMethod;


public class AffilTypePut extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, affiliationTypeId, affiliationTypeCode, affiliationTypeName;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields = null, writableResult = null;
	Long affiliationTypeIdJ = null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(AffilTypePut.class);
	String actuatorcommandversion;
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		String actuatorCommandeVersionURL = RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level + ".command.version");
		//actuatorcommandversion = resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorCommandeVersionURL);
		actuatorcommandversion = "1.0.0";
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}

	@Test(priority = 1)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			String affiliationTypeId1 = js.getString("data.affiliationTypeId");
			String expectMessage = resMsgs.affilTypePutSuccessNewMsg + affiliationTypeId1;
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String affilPostPostQuery = query.affilTypePutQuery(affiliationTypeId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.affilTypeDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(affilPostPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.affiliationTypeId") != null) {

					// ***success message validation

					if (internalMsg.equals(expectMessage)) {
						logger.info("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						test.pass("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { affiliationTypeId, affiliationTypeCode, affiliationTypeName, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.affilTypeResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_AffiliationTypeId: ", "Input_affiliationTypeCode: ",
								"Input_affiliationTypeName: ", "Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_AffiliationTypeId: ", "DB_affiliationTypeCode: ", "DB_affiliationTypeName: ",
								"DB_LastUpdateUserName: " };
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
							test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("Affiliation ID  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Affiliation ID is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				test.fail("Response status validation failed: " + Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			for (int i = 0; i < errorMsgLength; i++) {

				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}

			String expectMessage = resMsgs.requiredFieldMsg;

			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("affiliationTypeCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String affilPostPostQuery = query.affilTypePutQuery(affiliationTypeId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.affilTypeDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(affilPostPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.affiliationTypeId") != null) {
					String affiliationTypeId1 = js.getString("data.affiliationTypeId");
					// ***success message validation
					String expectMessage = resMsgs.affilTypePutSuccessNewMsg + affiliationTypeId;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						test.pass("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { affiliationTypeId, affiliationTypeCode, affiliationTypeName, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.affilTypeResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_AffiliationTypeId: ", "Input_affiliationTypeCode: ",
								"Input_affiliationTypeName: ", "Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_AffiliationTypeId: ", "DB_affiliationTypeCode: ", "DB_affiliationTypeName: ",
								"DB_LastUpdateUserName: " };
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
							test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("Affiliation ID  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Affiliation ID is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
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
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequestWithoutMeta(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			int Wscode = res.statusCode();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));

			}
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "");
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequestWithoutAffilTypeCd(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));

			}
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation

				if (errorMsg1.get(0).equals("affiliationTypeCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "affiliationTypeCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", " affiliationTypeCode " + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequestWithoutAffilTypeNm(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			String affiliationTypeId1 = js.getString("data.affiliationTypeId");
			// ***success message validation
			String expectMessage = resMsgs.affilTypePutSuccessNewMsg + affiliationTypeId;
			int Wscode = res.statusCode();
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String affilPostPostQuery = query.affilTypePutQuery(affiliationTypeId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.affilTypeDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(affilPostPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.affiliationTypeId") != null) {

					if (internalMsg.equals(expectMessage)) {
						logger.info("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						test.pass("Affiliation Type ID is getting received in success response: " + affiliationTypeId1);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { affiliationTypeId, affiliationTypeCode, affiliationTypeName, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.affilTypeResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_AffiliationTypeId: ", "Input_affiliationTypeCode: ",
								"Input_affiliationTypeName: ", "Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_AffiliationTypeId: ", "DB_affiliationTypeCode: ", "DB_affiliationTypeName: ",
								"DB_LastUpdateUserName: " };
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
							test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("Affiliation ID  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Affiliation ID is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
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
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}

			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "userName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", " userName " + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.AffillengthExceeds10CharNew;
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("affiliationTypeCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", " affiliationTypeCode  " + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", " affiliationTypeCode " + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			int Wscode = res.statusCode();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));

			}
			String expectMessage = resMsgs.lengthExceeds65Char;
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("affiliationTypeName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "");
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();

			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");

			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength = js.getInt("errors.size");
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds25Char;

			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", " userName " + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}  else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", " userName " + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl.substring(0, 101);
			test.info("URL: "+getEndPoinUrl);
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			String internalMsg = js.getString("error");
			int Wscode = res.statusCode();
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength;
			if (StringUtils.isBlank(js.getString("errors.size"))) {
				errorMsgLength = 0;
			} else {
				errorMsgLength = js.getInt("errors.size");
			}

			for (int i = 0; i < errorMsgLength; i++) {

				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			if (Wscode == 404) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.invalidUrlMsgAffilPUT;
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)){

					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response  when the URI is not correct");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response  when the URI is not correct");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				logger.error("Response status code 400 validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status code 400 validation failed: " + Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
	public void TC_12() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get test case ID with method name
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			test.info("URL: "+getEndPoinUrl);
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequestMissingcomma(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			;
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.missingCommaInRequestMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			js.getString("meta.timestamp");
			if (Wscode == 400 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = { userId,  affiliationTypeCode, affiliationTypeName, userId };
					String[] inputFieldNames = { "Input_UserName: ",
							"Input_affiliationTypeCode: ", "Input_affiliationTypeName: ", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when missinbg comma's in request ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when missinbg comma's in request ");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "Error" + expectMessage);
					//Assert.fail("Test Failed");
				}
			} else {

				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				}else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                    logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                    logger.error("------------------------------------------------------------------");
                             test.fail("Response validation failed as API version number is not matching with expected");
           }
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "holidayName" + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
	public void TC_13() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get test case ID with method name
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
		    String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".affilType.put");
			getEndPoinUrl = getEndPoinUrl + affiliationTypeId;
			test.info("URL: "+getEndPoinUrl);
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));


			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.usedGETinCommanderrorMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			js.getString("meta.timestamp");
			if (Wscode == 405 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId,  affiliationTypeCode, affiliationTypeName, userId };
					String[] inputFieldNames = { "Input_UserName: ",
							"Input_affiliationTypeCode: ", "Input_affiliationTypeName: ", "Input_LastUpdateUserName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response we use POST/PUT url but selecting GET method.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response we use POST/PUT url but selecting GET method. ");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "Error" + expectMessage);
					//Assert.fail("Test Failed");
				}
			} else {

				if (Wscode != 400) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                    logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                    logger.error("------------------------------------------------------------------");
                             test.fail("Response validation failed as API version number is not matching with expected");
           }
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "holidayName" + expectMessage);
				Assert.fail("Test Failed");
			}
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

	@Test(priority = 2)
	public void TC_14() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		// ***get test case ID with method name
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.affilTypePostRequest(userId, affiliationTypeCode, affiliationTypeName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".affilType.put");
			test.info("URL: "+getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload,"", token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int errrorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.missingHTTPHeaderInRequestMsg;
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 401 && meta != null
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				//ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, affiliationTypeCode, affiliationTypeName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_affiliationTypeCode: ", "Input_affiliationTypeName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when HTTP Header X-CSR-SECURITY_TOKEN in request ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when HTTP Header X-CSR-SECURITY_TOKEN in request ");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "Error" + expectMessage);
					//Assert.fail("Test Failed");
				}
			} else {

				if (Wscode != 401) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                    logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                    logger.error("------------------------------------------------------------------");
                             test.fail("Response validation failed as API version number is not matching with expected");
           }
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "NA" + expectMessage);
				Assert.fail("Test Failed");
			}
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

	// TC for JMS validation
	@Test(priority = 1)
	public void TC_15() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult = false;
		// ***get test case ID with method name
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));

			JSONObject getJMSResult = jmsReader.messageGetsPublished("GEOPL_AFFIL_TYPE");
			if (getJMSResult != null) {
				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Received:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				// String scrptCd=getJMSResult.getString("scrptCd");
				affiliationTypeIdJ = getJMSResult.getJSONObject("data").getLong("affiliationTypeId");
				String affiliationTypeCode = getJMSResult.getJSONObject("data").getString("affiliationTypeCode");
				String affiliationTypeName = getJMSResult.getJSONObject("data").getString("affiliationTypeName");

				if (affiliationTypeCode != null) {
					// ***get the DB query
					// String uomTypeJMSQuery =
					// query.uomTypePostQuery(uomTypeCd);
					String affilPostPostQuery = query.affilTypePostQuery(affiliationTypeCode);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.affilTypeGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(affilPostPostQuery, fields, fileName,
							testCaseID);
					String affiliationTypeId1 = affiliationTypeIdJ.toString();
					String[] JMSValue = { affiliationTypeId1, affiliationTypeCode, affiliationTypeName };
					testResult = TestResultValidation.testValidationForJMS(JMSValue, getResultDB);

					if (testResult) {
						// ***write result to excel
						String[] responseDbFieldValues = { affiliationTypeId1, getResultDB.get(0), affiliationTypeCode,
								getResultDB.get(1), affiliationTypeName, getResultDB.get(2) };
						String[] responseDbFieldNames = { "Response_affiliationTypeId: ", "DB_affiliationTypeId: ",
								"Response_affiliationTypeCode: ", "DB_affiliationTypeCode: ", "Response_affiliationTypeName: ",
								"DB_affiliationTypeName: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);

						logger.info("Comparison between JMS response & DB data matching and passed");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass("Comparison between JMS response & DB data matching and passed");
						test.pass(writableResult.replaceAll("\n", "<br />"));
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					} else {
						String[] responseDbFieldValues = { affiliationTypeId1, getResultDB.get(0), affiliationTypeCode,
								getResultDB.get(1), affiliationTypeName, getResultDB.get(2) };
						String[] responseDbFieldNames = { "Response_affiliationTypeId: ", "DB_affiliationTypeId: ",
								"Response_affiliationTypeCode: ", "DB_affiliationTypeCode: ", "Response_affiliationTypeName: ",
								"DB_affiliationTypeName: " };
						writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);

						logger.error("Comparison between JMS & DB data not matching and failed");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Comparison between input data & DB data not matching and failed");
						test.fail("Comparison between input data & DB data not matching and failed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
								writableResult, "Fail", "Comparison between JMS & DB data not matching and failed");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("affiliationTypeCode is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Scrpt Code is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "",
							"Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				logger.error("Posted request is not reached to JMS queue");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Posted request is not reached to JMS queue");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
						"");
				Assert.fail("Test Failed");
			}

		}

		catch (Exception e) {
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
		userId = inputData1.get(testCaseId).get("UserName");
		affiliationTypeId = inputData1.get(testCaseId).get("affilTypeId");
		affiliationTypeCode = inputData1.get(testCaseId).get("affilTypeCode");
		affiliationTypeName = inputData1.get(testCaseId).get("affilTypeName");
	}

}
