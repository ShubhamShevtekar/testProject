package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

public class GeoOrgStdPut extends Reporting {
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, geoOrgStdCode, geoOrgStdName;// ,
																					// token;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	JMSReader jmsReader = new JMSReader();
	String writableInputFields, writableDBFields = null, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(GeoOrgStdPut.class);
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
		// String
		// actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator",
		// fileName, level+".command.version");
		// actuatorcommandversion =resultValidation.versionValidation(fileName,
		// tokenKey, tokenVal,//actuatorCommandeVersionURL);
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseID = m.getName();
			test = extent.createTest(testCaseID);
		}
	}

	@Test(priority = 1)
	public void TC_01() {
		String testCaseID = "TC_01";
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
			// ***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***get the DB query
				String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geoOrgStdDBFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.organizationStandardCode") != null) {
					String geoOrgStdCode1 = js.getString("data.organizationStandardCode");
					// ***success message validation
					String expectMessage = resMsgs.geoOrgStdPutNewSuccessMsg + geoOrgStdCode1;
					if (internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode)) {
						logger.info(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
						test.pass(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
					} else {
						logger.info("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
					// ***send the input, response, DB result for validation
					String[] inputFieldValues = { geoOrgStdCode, geoOrgStdName, userId };
					// ***get response fields values
					List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
					testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
							resFields);
					// ***write result to excel
					String[] inputFieldNames = { "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ",
							"Input_LastUpdateUserName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					String[] dbFieldNames = { "DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: " };
					writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					test.info("DB Data Values:");
					test.info(writableDBFields.replaceAll("\n", "<br />"));
					if (testResult) {

						logger.info("Comparison between input data & DB data matching and passed");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");

						test.pass("Comparison between input data & DB data matching and passed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					} else {

						logger.error("Comparison between input data & DB data not matching and failed");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");

						test.fail("Comparison between input data & DB data not matching and failed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Comparison between input data & DB data not matching and failed");
						Assert.fail("Test Failed");
					}
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
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
	public void TC_02() {
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0"))

			{
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when sending the blank GeoOrgStdCode");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.pass(
							"Expected error message is getting received in response when sending the blank GeoOrgStdCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");

					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				{
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
					} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
						logger.error("Response validation failed as API version number is not matching with expected");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");
					}
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
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
			// ***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***get the DB query
				String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geoOrgStdDBFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.organizationStandardCode") != null) {
					String geoOrgStdCode1 = js.getString("data.organizationStandardCode");
					// ***success message validation
					String expectMessage = resMsgs.geoOrgStdPutNewSuccessMsg + geoOrgStdCode1;
					if (internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode)) {
						logger.info(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
						test.pass(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
					} else {
						logger.info("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
					// ***send the input, response, DB result for validation
					String[] inputFieldValues = { geoOrgStdCode, geoOrgStdName, userId };
					// ***get response fields values
					List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
					testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
							resFields);
					// ***write result to excel
					String[] inputFieldNames = { "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ",
							"Input_LastUpdateUserName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					String[] dbFieldNames = { "DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: " };
					writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					test.info("DB Data Values:");
					test.info(writableDBFields.replaceAll("\n", "<br />"));
					if (testResult) {
						logger.info("Comparison between input data & DB data matching and passed");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");

						test.pass("Comparison between input data & DB data matching and passed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					} else {

						logger.error("Comparison between input data & DB data not matching and failed");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");

						test.fail("Comparison between input data & DB data not matching and failed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Comparison between input data & DB data not matching and failed");
						Assert.fail("Test Failed");
					}
				} else {

					logger.error("Geopolitical Org Std Code is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");

					test.fail("Geopolitical Org Std Code is not available in response");
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequestWithoutMeta(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}

			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation

				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in responsewhen the meta data section is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "meta" + expectMessage);
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

	@Test(priority = 2)
	public void TC_05() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequestWithoutOrgStdCd(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the orgStdCd attribute is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {

				logger.error("Response status code 400 validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");

				test.fail("Response status code 400 validation failed: " + Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_01";
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
			// ***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequestWithoutOrgStdNm(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***get the DB query
				String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geoOrgStdDBFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.organizationStandardCode") != null) {
					String geoOrgStdCode1 = js.getString("data.organizationStandardCode");
					// ***success message validation
					String expectMessage = resMsgs.geoOrgStdPutNewSuccessMsg + geoOrgStdCode1;
					if (internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode)) {
						logger.info(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
						test.pass(
								"Success message with Geopitical Org Std Code is getting received as expected in response");
					} else {
						logger.info("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
					// ***send the input, response, DB result for validation
					String[] inputFieldValues = { geoOrgStdCode, geoOrgStdName, userId };
					// ***get response fields values
					List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
					testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
							resFields);
					// ***write result to excel
					String[] inputFieldNames = { "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ",
							"Input_LastUpdateUserName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					String[] dbFieldNames = { "DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: " };
					writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					test.info("DB Data Values:");
					test.info(writableDBFields.replaceAll("\n", "<br />"));
					if (testResult) {
						logger.info("Comparison between input data & DB data matching and passed");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");

						test.pass("Comparison between input data & DB data matching and passed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					} else {

						logger.error("Comparison between input data & DB data not matching and failed");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");

						test.fail("Comparison between input data & DB data not matching and failed");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDBFields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Comparison between input data & DB data not matching and failed");
						Assert.fail("Test Failed");
					}
				} else {

					logger.error("Geopolitical Org Std Code is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");

					test.fail("Geopolitical Org Std Code is not available in response");
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequestNullUser(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the user name is null or Empty in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "userId" + expectMessage);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "userId" + expectMessage);
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds30Char2;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0"))

			{
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the orgStdCd is more than 30 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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

	@Test(priority = 2)
	public void TC_09() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds65Char2;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0"))

			{
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("organizationStandardName") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the orgStdNm is ore than 65 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdName" + expectMessage);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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

	@Test(priority = 2)
	public void TC_10() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds25Char;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0"))

			{
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "userId" + expectMessage);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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

	@Test(priority = 2)
	public void TC_11() {
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
			getEndPoinUrl = getEndPoinUrl.substring(0, 118);
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			String internalMsg = js.getString("errors.message");
			int Wscode = res.statusCode();
			if (Wscode == 404) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.invalidPutUrlMsg;
				if (internalMsg.contains(expectMessage)) {
					logger.info("Expected error message is getting received in response when the URI is not correct");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
			// ***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.recordNotFoundMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 404 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when the orgStdCd provided in JSON is not exist in DB");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");

					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.pass(
							"Expected error message is getting received in response when sending the blank GeoOrgStdCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");

					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 404) {
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.recordNotFoundMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 404 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when special characters are provided for orgStdCd in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.pass(
							"Expected error message is getting received in response when sending the blank GeoOrgStdCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");

					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 404) {
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
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

	@Test(priority = 2)
	public void TC_14() {
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequestMissingComma(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
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
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.missingCommaInRequestMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				{
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
					} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
						logger.error("Response validation failed as API version number is not matching with expected");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");
					}
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
			// ***send request and get response
			/*Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);*/
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.usedGETinCommanderrorMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 405 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);   ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				{
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
					} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
						logger.error("Response validation failed as API version number is not matching with expected");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");
					}
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
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
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		// String testCaseID = "TC_02";
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
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".geoOrgStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, "", token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.missingHTTPHeaderInRequestMsg;
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			if (Wscode == 401 && meta != null && actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				//ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info("Expected error message is getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					String[] inputFieldValues = { userId, geoOrgStdCode, geoOrgStdName };
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				{
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
					} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
						logger.error("Response validation failed as API version number is not matching with expected");
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");
					}
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geoOrgStdCode" + expectMessage);
					Assert.fail("Test Failed");
				}
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
	public void TC_17() {
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

			// ***send request and get response
			JSONObject getJMSResult = jmsReader.messageGetsPublished("GEOPL_ORG_STD");

			if (getJMSResult != null) {
				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Recieved:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				String orgStdCd = getJMSResult.getJSONObject("data").getString("organizationStandardCode");
				String orgStdNm = getJMSResult.getJSONObject("data").getString("organizationStandardName");
				if (orgStdCd != null) {
					// ***get the DB query
					String geoOrgStdPostQuery = query.geoOrgStdPostQuery(orgStdCd);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.geoOrgStdGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName,
							testCaseID);
					System.out.println("getResultDB1::::::::::::::::::::" + getResultDB);
					String[] JMSValue = { orgStdCd, orgStdNm };
					System.out.println("JMSValue::::::::::::::::::::" + JMSValue);
					testResult = TestResultValidation.testValidationForJMS(JMSValue, getResultDB);
					if (testResult) {
						// ***write result to excel
						String[] responseDbFieldValues = { orgStdCd, getResultDB.get(0), orgStdNm, getResultDB.get(1) };
						String[] responseDbFieldNames = { "Response_orgStdCd: ", "DB_orgStdCd: ", "Response_orgStdNm: ",
								"DB_orgStdNm: " };
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
						String[] responseDbFieldValues = { orgStdCd, getResultDB.get(0), orgStdNm, getResultDB.get(1) };
						String[] responseDbFieldNames = { "Response_orgStdCd: ", "DB_orgStdCd: ", "Response_orgStdNm: ",
								"DB_orgStdNm: " };
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
					logger.error("Org Std Cd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Org Std Cd is not available in response");
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
		geoOrgStdCode = inputData1.get(testCaseId).get("orgStdCd");
		geoOrgStdName = inputData1.get(testCaseId).get("orgStdNm");
	}
}
