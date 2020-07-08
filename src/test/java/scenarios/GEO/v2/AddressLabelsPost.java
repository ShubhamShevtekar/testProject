
package scenarios.GEO.v2;

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
import com.sun.istack.NotNull;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.v2.DbConnect;
import utils.v2.ExcelUtil;
import utils.v2.Miscellaneous;
import utils.v2.Queries;
import utils.v2.Reporting;
import utils.v2.ResponseMessages;
import utils.v2.RetrieveEndPoints;
import utils.v2.TestResultValidation;
import utils.v2.ValidationFields;
import wsMethods.v2.GetResponse;
import wsMethods.v2.PostMethod;

public class AddressLabelsPost extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String userId, TestCaseDescription, scenarioType;
	String countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields = null, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(AddressLabelsPost.class);
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
		String actuatorCommandeVersionURL = RetrieveEndPoints.getEndPointUrl("commandActuator", fileName,
				level + ".command.version");
		actuatorcommandversion = resultValidation.versionValidation(fileName, tokenKey, tokenVal,
				actuatorCommandeVersionURL);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			
			test.info("URL: "+getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String addrLabelResponseId = js.getString("data.geopoliticalId");
			String expectMessage = resMsgs.addressLabelSuccessMsg + addrLabelResponseId;
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				
				//****** need to update Query as per data model docuement   ********
				String addrLabelPostPostQuery = query.addressLabelPostQuery(countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
				
				// ***get the fields needs to be validate in DB
			// *****	updated field as per data model docuement
				List<String> fields = ValidationFields.addressLabelsPostMethodDbFields();
				
				
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addrLabelPostPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.geopoliticalId") != null) {
					// ***success message validation
					if (internalMsg.equals(expectMessage)) {
						logger.info(
								"geopoliticalId is getting generated and received in response: " + addrLabelResponseId);
						test.pass("geopoliticalId is getting generated and received in response: " + addrLabelResponseId);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
						// ***get response fields values
						List<String> resFields = ValidationFields.addressLabelResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
								"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_countryCode: ", "DB_addressLineNumber: ",
								"DB_brandAddressLineDescription: ", "DB_fullAddressLineDescription: ", "DB_languageCode: ", "DB_applicable: ", "DB_scriptCode: " };
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
							test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
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
					logger.error("geopoliticalId is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("geopoliticalId is not available in response");
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
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


	@Test(priority = 1)
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String addrLabelResponseId = js.getString("data.geopoliticalId");
			String expectMessage = resMsgs.addressLabelSuccessMsg + addrLabelResponseId;
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				
				//****** need to update Query as per data model docuement   ********
				String addrLabelPostPostQuery = query.addressLabelPostQuery(countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
				
				// ***get the fields needs to be validate in DB
			// *****	need to update field as per data model docuement
				List<String> fields = ValidationFields.addressLabelsPostMethodDbFields();
				
				
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(addrLabelPostPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.geopoliticalId") != null) {
					// ***success message validation
					if (internalMsg.equals(expectMessage)) {
						logger.info(
								"geopoliticalId is getting generated and received in response: " + addrLabelResponseId);
						test.pass("geopoliticalId is getting generated and received in response: " + addrLabelResponseId);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
						// ***get response fields values
						List<String> resFields = ValidationFields.addressLabelResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
								"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_countryCode: ", "DB_addressLineNumber: ",
								"DB_brandAddressLineDescription: ", "DB_fullAddressLineDescription: ", "DB_languageCode: ", "DB_applicable: ", "DB_scriptCode: " };
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
							test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
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
					logger.error("geopoliticalId is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("geopoliticalId is not available in response");
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			//int errorMsgLength = js.getInt("errors.size");
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for countryCode");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for countryCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "countryCode" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCode" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("addressLineNumber") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for addressLineNumber");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for addressLineNumber");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "addressLineNumber" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "addressLineNumber" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("brandAddressLineDescription") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for brandAddressLineDescription");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for brandAddressLineDescription");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "brandAddressLineDescription" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "brandAddressLineDescription" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("fullAddressLineDescription") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for fullAddressLineDescription");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for fullAddressLineDescription");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "fullAddressLineDescription" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "fullAddressLineDescription" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for languageCode");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for languageCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageCode" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "languageCode" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("applicable") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for applicable");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for applicable");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "applicable" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "applicable" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.recordExistsMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending invalid(not existing in DB) value for countryCode attribute");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending invalid(not existing in DB) value for countryCode attribute");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "NA" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.recordExistsMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending invalid(not existing in DB) value for languageCode attribute");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending invalid(not existing in DB) value for languageCode attribute");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "NA" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.recordExistsMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when user try to process same request again.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when user try to process same request again.");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "NA" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
	

	@Test(priority = 2)
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
				boolean testResult = false;
				try {
					// ***get the test data from sheet
					testDataFields(scenarioName, testCaseID);
					test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
					// ***send the data to create request and get request
					String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
					String reqFormatted = Miscellaneous.jsonFormat(payload);
					test.info("Input Request created:");
					test.info(reqFormatted.replaceAll("\n", "<br />"));
					// ***get end point url
					String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
					getEndPoinUrl = getEndPoinUrl.substring(0, 101);
					// ***send request and get response
					Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
							testCaseID);
					String responsestr = res.asString();
					String responsestr1 = Miscellaneous.jsonFormat(responsestr);
					test.info("Response Recieved:");
					test.info(responsestr1.replaceAll("\n", "<br />"));
					JsonPath js = new JsonPath(responsestr);
					String Wsstatus = res.getStatusLine();
					String internalMsg = js.getString("error");
					int Wscode = res.statusCode();
					if (Wscode == 404) {
						logger.info("Response status code 404 validation passed: " + Wscode);
						test.pass("Response status code 404 validation passed: " + Wscode);
						// ***error message validation
						String expectMessage = resMsgs.invalidUrlMsg;
						if (internalMsg.equals(expectMessage)) {
							String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
							
							String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
													"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
								
							writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
							logger.info("Expected error message is getting received in response  when the URI is not correct");
							logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
							logger.info("------------------------------------------------------------------");
							test.pass("Expected error message is getting received in response  when the URI is not correct");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
							test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
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
						logger.error("Response status code 404 validation failed: " + Wscode);
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Response status code 404 validation failed: " + Wscode);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if ((errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage))
						&& (errorMsg1.get(1).equals("languageCode") && errorMsg2.get(1).equals(expectMessage))) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
												"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when passing multiple invalid values for attributes ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when passing multiple invalid values for attributes ");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "localeCd" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "localeCd" + expectMessage);
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the countryCode is more than 2 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the countryCode is more than 2 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "countryCode" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "countryCode" + expectMessage);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the brandAddressLineDescription is more than 35 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the brandAddressLineDescription is more than 35 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "brandAddressLineDescription" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "brandAddressLineDescription" + expectMessage);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the fullAddressLineDescription is more than 80 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the fullAddressLineDescription is more than 80 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "fullAddressLineDescription" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "fullAddressLineDescription" + expectMessage);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the languageCode is more than 03 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the languageCode is more than 03 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageCode" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "languageCode" + expectMessage);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scriptCode is more than 18 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scriptCode is more than 18 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptCode" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "scriptCode" + expectMessage);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the userName is more than 18 characters length");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the userName is more than 18 characters length");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "userName" + expectMessage);
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
	public void TC_20() {
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
			String payload = PostMethod.addressLabelPostRequest(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the null value for userName");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the null value for userName");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "userName" + expectMessage);
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
	public void TC_21() {
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
			String payload = PostMethod.addressLabelPostRequestWithoutMeta(userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".addressLabel.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = {userId, countryCode, addressLineNumber, brandAddressLineDescription, fullAddressLineDescription, languageCode, applicable, scriptCode};
					String[] inputFieldNames = { "Input_UserName: ", "Input_countryCode: ",
							"Input_addressLineNumber: ", "Input_brandAddressLineDescription: ", "Input_fullAddressLineDescription: " , "Input_languageCode: ", "Input_applicable: ", "Input_scriptCode: "};
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when when the meta data section is not passed");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when when the meta data section is not passed");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("TC Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "meta" + expectMessage);
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "meta" + expectMessage);
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
		countryCode = inputData1.get(testCaseId).get("countryCode");
		addressLineNumber = inputData1.get(testCaseId).get("addressLineNumber");
		brandAddressLineDescription = inputData1.get(testCaseId).get("brandAddressLineDescription");
		fullAddressLineDescription = inputData1.get(testCaseId).get("fullAddressLineDescription");
		languageCode = inputData1.get(testCaseId).get("languageCode");
		applicable = inputData1.get(testCaseId).get("applicable");
		scriptCode = inputData1.get(testCaseId).get("scriptCode");
	}
}
