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

public class ScriptPost extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, scriptCode, scriptName, scriptDesc, effectiveDate, expirationDate;// ,
	// token;
	Queries query = new Queries();
	// String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields = null, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(ScriptPost.class);
	String actuatorcommandversion;
	TestResultValidation resultValidation = new TestResultValidation();

	Date date = new Date();
	String todaysDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);

		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		/*
		 * String actuatorCommandeVersionURL =
		 * RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level +
		 * ".command.version"); actuatorcommandversion =
		 * resultValidation.versionValidation(fileName, tokenKey, tokenVal,
		 * actuatorCommandeVersionURL);
		 */
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
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);

				String formatEffectiveDate = effectiveDate;
				String formatExpirationDate = expirationDate;
				Date dateEffectiveDate = null;
				Date dateExpirationDate = null;

				try {
					dateEffectiveDate = srcDf.parse(formatEffectiveDate);
					dateExpirationDate = srcDf.parse(formatExpirationDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				formatEffectiveDate = destDf.format(dateEffectiveDate);
				formatEffectiveDate = formatEffectiveDate.toUpperCase();

				formatExpirationDate = destDf.format(dateExpirationDate);
				formatExpirationDate = formatExpirationDate.toUpperCase();

				// ***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.scriptCode") != null) {
					String scriptCode1 = js.getString("data.scriptCode");
					// ***success message validation scriptCode
					String expectMessage = resMsgs.scriptPostNewSuccessMsg + scriptCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with Script Code is getting received as expected in response");
						test.pass("Success message with Script Code is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
								expirationDate, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.scriptResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
								"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: ",
								"Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_ScriptCode: ", "DB_ScriptName: ",
								"DB_ScriptDescription: ", "DB_effectiveDate: ", "DB_expirationDate: ",
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

							String formatAuditEffectiveDate = effectiveDate;
							String formatAuditExpirationDate = expirationDate;
							Date dateAuditEffectiveDate = null;
							Date dateAuditExpirationDate = null;

							try {
								dateAuditEffectiveDate = srcDf.parse(formatAuditEffectiveDate);
								dateAuditExpirationDate = srcDf.parse(formatAuditExpirationDate);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							formatAuditEffectiveDate = destDf.format(dateAuditEffectiveDate);
							formatAuditEffectiveDate = formatAuditEffectiveDate.toUpperCase();

							formatAuditExpirationDate = destDf.format(dateAuditExpirationDate);
							formatAuditExpirationDate = formatAuditExpirationDate.toUpperCase();

							String scriptPostAuditQuery = query.scriptPostAuditQuery1(scriptCode);
							// ***get the fields needs to be validate in DB
							List<String> auditFields = ValidationFields.scriptDbAuditFields();
							// ***get the result from DB
							List<String> getResultDBAudit = DbConnect.getResultSetFor(scriptPostAuditQuery, auditFields,
									fileName, testCaseID);

							String[] inputAuditFieldValues = { userId, scriptCode, scriptName, scriptDesc,
									effectiveDate, expirationDate, userId, "0" };
							testResult = false;
							testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
									getResultDBAudit, resFields);

							if (testResult) {
								String[] inputFieldNamesAudit = { "Input_UserName: ", "Input_ScriptCode: ",
										"Input_ScriptName: ", "Input_ScriptDescription: ", "Input_effectiveDate: ",
										"Input_expirationDate: ", "Input_LastUpdateUserName: ",
										"Expected RevisionType CD:" };

								writableInputFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
										inputFieldNamesAudit);

								String[] dbFieldNamesAudit = { "DB_UserName: ", "DB_ScriptCode: ", "DB_ScriptName: ",
										"DB_ScriptDescription: ", "DB_effectiveDate: ", "DB_expirationDate: ",
										"DB_LastUpdateUserName: ", "DB_RevisionType CD:" };
								writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBAudit, dbFieldNamesAudit);
								test.info("Input Data Values:");
								test.info(writableInputFields.replaceAll("\n", "<br />"));
								test.info("DB Data Values:");
								test.info(writableDB_Fields.replaceAll("\n", "<br />"));
								test.pass("Comparison between input data & DB Audit table data matching and passed");
								ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
										writableInputFields, writableDB_Fields, "", "", "", "Pass",
										"Audit Table validation");

								test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));

							} else {
								logger.error(
										"Comparison between input data & DB Audit Table data not matching and failed");
								logger.error(
										"Execution is completed for Audit Table Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Comparison between input data & DB Audit Table data not matching and failed");
								ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
										writableInputFields, writableDB_Fields, "", "", "", "Fail",
										"Comparison between input data & DB Audit table data not matching and failed");
								Assert.fail("Test Failed");
							}
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
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("scrptCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Script Code is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response validation failed as Wscode is not present: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as Wscode is not present: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present: ");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present:");
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
	public void TC_02() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.requiredField;
			int errorMsgLength = js.get("errors.size");
			List<String> errMgs1 = new ArrayList<String>();
			List<String> errMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errMgs2.get(0).equals(expectMessage) && errMgs1.get(0).equals("scriptCode")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank Script Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode " + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.requiredField;
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("scriptName")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the empty value is passed in JSON for scriptName");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the empty value is passed in JSON for scriptName");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptName" + expectMessage);
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
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.scriptCode") != null) {
					String scriptCode1 = js.getString("data.scriptCode");
					// ***success message validation
					String expectMessage = resMsgs.scriptPostNewSuccessMsg + scriptCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with Script Code is getting received as expected in response");
						test.pass("Success message with Script Code is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
								expirationDate, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.scriptResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
								"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: ",
								"Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_ScriptCode: ", "DB_ScriptName: ",
								"DB_ScriptDescription: ", "DB_effectiveDate: ", "DB_expirationDate: ",
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
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("Script Code is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Script Code is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response validation failed as Wscode is not present: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as Wscode is not present: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present: ");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present:");
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
	public void TC_05() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequestWithoutMeta(userId, scriptCode, scriptName, scriptDesc,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredField;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);

				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("meta")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the meta data section is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the meta data section is not passed in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "meta" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {

					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "meta" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequestWithoutSciptCd(userId, scriptCode, scriptName, scriptDesc,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredField;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("scriptCode")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scriptCode attribute is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scriptCode attribute is not passed in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequestWithoutScriptNm(userId, scriptCode, scriptName, scriptDesc,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredField;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("scriptName")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scriptName attribute is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scriptName attribute is not passed in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptName" + expectMessage);
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
			String payload = PostMethod.scriptPostRequestWithoutScriptDesc(userId, scriptCode, scriptName, scriptDesc,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.scriptCode") != null) {
					String scriptCode1 = js.getString("data.scriptCode");
					// ***success message validation
					String expectMessage = resMsgs.scriptPostNewSuccessMsg + scriptCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with Script Code is getting received as expected in response");
						test.pass("Success message with Script Code is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
								expirationDate, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.scriptResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
								"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: ",
								"Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_ScriptCode: ", "DB_ScriptName: ",
								"DB_ScriptDescription: ", "DB_effectiveDate: ", "DB_expirationDate: ",
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
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("Script Code is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Script Code is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response validation failed as Wscode is not present: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as Wscode is not present: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present: ");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present:");
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
	public void TC_09() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredField;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("userName")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the user name is null or Empty in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the user name is null or Empty in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "userName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {

					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "userName" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.lengthExceeds18Char1;
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMgs1.get(0).equals("scriptCode") && errorMgs2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scriptCode is more than 18 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scriptCode is more than 18 characters length in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds256Char1;
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("scriptName")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scriptName is more than 256 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scriptName is more than 256 characters length in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scriptName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {

					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptName" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds4000Char1;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation scriptDescription
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("scriptDescription")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the scrptDesc is more than 4000 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the scrptDesc is more than 4000 characters length in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "scrptDesc" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");

				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scrptDesc" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();

			String expectMessage = resMsgs.lengthExceeds25Char;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("userName")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "userName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "userName" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			getEndPoinUrl = getEndPoinUrl.substring(0, 92);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			String internalMsg = js.getString("errors.message");
			int Wscode = res.statusCode();
			if (Wscode == 404) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.invalidPostUrlMsg;
				if (internalMsg.contains(expectMessage)) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response when the URI is not correct");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when the URI is not correct");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				logger.error("Response status code 400 validation failed: " + Wscode);
				logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
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
	public void TC_15() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.recordExistsMsg;
			String meta = js.getString("meta");
			int errorMsgLength = js.get("errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errorMgs2.add(js.getString("errors[" + i + "].message"));

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				if (errorMgs2.get(0).equals(expectMessage) && errorMgs1.get(0).equals("NA")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank Script Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "Error" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {

					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "Error" + expectMessage);
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
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
				if (js.getString("data.scriptCode") != null) {
					String scriptCode1 = js.getString("data.scriptCode");
					// ***success message validation
					String expectMessage = resMsgs.scriptPostNewSuccessMsg + scriptCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with Script Code is getting received as expected in response");
						test.pass("Success message with Script Code is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
								expirationDate, userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.scriptResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
								"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: ",
								"Input_LastUpdateUserName: " };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_UserName: ", "DB_ScriptCode: ", "DB_ScriptName: ",
								"DB_ScriptDescription: ", "DB_effectiveDate: ", "DB_expirationDate: ",
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
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						Assert.fail("Test Failed");
					}
				} else {
					logger.error("UOM Type ID is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Script Code is not available in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr1, "Fail", "");
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 200) {
					logger.error("Response validation failed as Wscode is not present: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as Wscode is not present: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present: ");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present:");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present:");
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
	public void TC_17() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequestMissingComma(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.missingCommaInRequestMsg;
			int errorMsgLength = js.get("errors.size");
			List<String> errMgs1 = new ArrayList<String>();
			List<String> errMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errMgs2.get(0).equals(expectMessage) && errMgs1.get(0).equals("Error")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank Script Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode " + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			/*Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);*/
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.usedGETinCommanderrorMsg;
			int errorMsgLength = js.get("errors.size");
			List<String> errMgs1 = new ArrayList<String>();
			List<String> errMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 405 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errMgs2.get(0).equals(expectMessage) && errMgs1.get(0).equals("Error")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank Script Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode " + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.scriptPostRequest(userId, scriptCode, scriptName, scriptDesc, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".script.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, "", token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = res.getStatusLine();
			js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String expectMessage = resMsgs.missingHTTPHeaderInRequestMsg;
			int errorMsgLength = js.get("errors.size");
			List<String> errMgs1 = new ArrayList<String>();
			List<String> errMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errMgs1.add(js.getString("errors[" + i + "].fieldName"));
				errMgs2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 401 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);  ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errMgs2.get(0).equals(expectMessage) && errMgs1.get(0).equals("NA")) {
					String[] inputFieldValues = { userId, scriptCode, scriptName, scriptDesc, effectiveDate,
							expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_ScriptCode: ", "Input_ScriptName: ",
							"Input_ScriptDescription: ", "Input_effectiveDate: ", "Input_expirationDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank Script Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				if (Wscode != 400) {
					logger.error("Response status code 400 validation failed: " + Wscode);
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Response status code 400 validation failed: " + Wscode);
				} else if (meta == null) {
					logger.error("Response validation failed as meta is not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta is not present");
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", "scriptCode " + expectMessage);
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
	public void TC_20() {

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
			JSONObject getJMSResult = jmsReader.messageGetsPublished("SCRIPT");

			if (getJMSResult != null) {
				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Received:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				// String scriptCode=getJMSResult.getString("scriptCode");
				String scriptCode = getJMSResult.getJSONObject("data").getString("scriptCode");
				String scriptName = getJMSResult.getJSONObject("data").getString("scriptName");
				String scrptDesc = getJMSResult.getJSONObject("data").getString("scriptDescription");
				String effectiveDate = getJMSResult.getJSONObject("data").getString("effectiveDate");
				String expirationDate = getJMSResult.getJSONObject("data").getString("expirationDate");

				if (scriptCode != null) {
					// ***get the DB query
					String scriptPostQuery = query.scriptPostQueryJMS(scriptCode);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.scriptJMSMethodDbFields();
					// ***get the result from DB

					List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
					String[] JMSValue = { scriptCode, scriptName, scrptDesc, effectiveDate, expirationDate };
					testResult = TestResultValidation.testValidationForJMS(JMSValue, getResultDB);

					if (testResult) {
						// ***write result to excel
						String[] responseDbFieldValues = { scriptCode, getResultDB.get(0), scriptName,
								getResultDB.get(1), scrptDesc, getResultDB.get(2), effectiveDate, getResultDB.get(3),
								expirationDate, getResultDB.get(4) };
						String[] responseDbFieldNames = { "Response_scriptCode: ", "DB_scriptCode: ",
								"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ",
								"DB_scriptDescription: ", "Input_effectiveDate: ", "DB_effectiveDate: ",
								"Input_expirationDate: ", "DB_expirationDate: " };
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
						String[] responseDbFieldValues = { scriptCode, getResultDB.get(0), scriptName,
								getResultDB.get(1), scrptDesc, getResultDB.get(2), effectiveDate, getResultDB.get(3),
								expirationDate, getResultDB.get(4) };
						String[] responseDbFieldNames = { "Response_scriptCode: ", "DB_scriptCode: ",
								"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ",
								"DB_scriptDescription: ", "Input_effectiveDate: ", "DB_effectiveDate: ",
								"Input_expirationDate: ", "DB_expirationDate: " };
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
					logger.error("Scrpt Code is not available in response");
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
		scriptCode = inputData1.get(testCaseId).get("scrptCd");
		scriptName = inputData1.get(testCaseId).get("scrptNm");
		scriptDesc = inputData1.get(testCaseId).get("scrptDesc");
		effectiveDate = inputData1.get(testCaseId).get("effectiveDate");
		expirationDate = inputData1.get(testCaseId).get("expirationDate");
	}

}
