package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
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

import bsh.ParseException;
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

public class LanguagePost extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, languageCode, languageName, nativeScriptLanguageName,
			localesLanguageCd, localesScriptCd, cldrVersionNumber, cldrVersionDate, dateFullFormatDescription,
			dateLongFormatDescription, dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
			localesExpirationDate;
	String countryCode, engLanguageName, localeCd, nativeScriptCode, localeCd1, countryCode1;
	JMSReader jmsReader = new JMSReader();
	HashMap<String, String> translatedDOWs;
	HashMap<String, String> translatedMOYs;
	HashMap<String, String> translatedDOWsJMS;
	HashMap<String, String> translatedMOYsJMS;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableAuditInputFields, writableDB_Fields = null, writableAuditDB_Fields = null,
			writableJMSResult = null;;
	String writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguagePost.class);
	String actuatorcommandversion = "1.0.0";
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
		// =resultValidation.versionValidation(fileName, tokenKey,
		// tokenVal,actuatorCommandeVersionURL);
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
		List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getAuditResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							auditLanguageDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Language input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Language input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							auditLanguageDBValidation(testCaseID, res);
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			String expectMessage = resMsgs.requiredFieldMsg;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the blank languageCode");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank languageCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if ((errorMsg1.get(0).equals("dayOfWeekNumber") || errorMsg1.get(1).equals("dayOfWeekNumber"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("translatedDayOfWeekName")
								|| errorMsg1.get(1).equals("translatedDayOfWeekName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					logger.info("Expected error message is getting received in response when sending the blank DoWNbr");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when sending the blank DoWNbr");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "dowNbr" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "dowNbr" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if ((errorMsg1.get(0).equals("monthOfYearNumber") || errorMsg1.get(1).equals("monthOfYearNumber"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("translatedMonthOfYearName")
								|| errorMsg1.get(1).equals("translatedMonthOfYearName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					logger.info("Expected error message is getting received in response when sending the blank MoYNbr");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when sending the blank MoYNbr");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "mthOfYrNbr" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "mthOfYrNbr" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			// String internalMsg = js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the blank languageName");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank languageName");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_07() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostWithoutDOWRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_08() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getAuditResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostWithoutMOYNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_09() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getAuditResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostWithoutDOWandMOYNewRequest(userId, languageCode,
					nativeScriptLanguageName, nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd,
					cldrVersionDate, cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			// String internalMsg = js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.recordExistsMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when same JSON request is processed again");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when same JSON request is processed again");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewWithoutLangCdRequest(userId, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			// String internalMsg = js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when languageCd attribute is not passed in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when languageCd attribute is not passed in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewWithoutLangNameRequest(userId, languageCode,
					nativeScriptLanguageName, nativeScriptCode, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when languageName attribute is not passed in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when languageName attribute is not passed in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewWithoutnativeScriptLanguageNameRequest(userId, languageCode,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewWithoutUserNameRequest(languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when the user name is not passed in JSON");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when the user name is not passed in JSON");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewWithoutMetaRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {

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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			getEndPoinUrl = getEndPoinUrl.substring(0, 94);
			test.info("URL: " + getEndPoinUrl);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			JsonPath js = new JsonPath(responsestr);
			test.info(responsestr1.replaceAll("\n", "<br />"));
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");

			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.invalidUrlMsgLangPost;
			if (Wscode == 404 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info("Expected error message is getting received in response when Wrong URI is passed ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response  when Wrong URI is passed ");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			// String internalMsg = js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds3Char;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when languageCd is more than 3 characters in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when languageCd is more than 3 characters in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			// String internalMsg = js.getString("errorMessages.e");
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds256Char1;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when languageName is more than 256 characters in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when languageName is more than 256 characters in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "languageName" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds256Char1;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("nativeScriptLanguageName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when nativeScriptLanguageName is more than 256 characters in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when nativeScriptLanguageName is more than 256 characters in JSON request");
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			String expectMessage = resMsgs.lengthExceeds38Char;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("dayOfWeekNumber") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when Downbr is more than 38 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when Downbr is more than 38 characters JSON request");
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
	public void TC_22() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			String expectMessage = resMsgs.lengthExceeds256Char1;
			int Wscode = res.statusCode();
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("translatedDayOfWeekName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when translatedDayOfWeekName is more than 256 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when translatedDayOfWeekName is more than 256 characters JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "transDowName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "transDowName" + expectMessage);
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
	public void TC_23() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds38Char;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when monthOfYearNumber is more than 38 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when monthOfYearNumber is more than 38 characters JSON request");
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

	public void TC_24() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}

			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds65Char;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("translatedMonthOfYearName") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when translatedMonthOfYearName is more than 65 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when translatedMonthOfYearName is more than 65 characters JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "transMoyName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "transMoyName" + expectMessage);
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
	public void TC_25() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds25Char;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

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
	public void TC_26() {
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
			String payload = PostMethod.langPostNewRequestCommaMissing(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			System.out.println("reqFormatted :: " + reqFormatted);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = languageName;
					inputFieldValues[3] = nativeScriptLanguageName;
					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_languageName: ",
							"Input_nativeScriptLanguageName:" };

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
					// Assert.fail("Test Failed");
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp ispresent");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
	public void TC_27() {
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
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			test.info("URL: " + getEndPoinUrl);
			// ***send request and get response

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
			if (Wscode == 405 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = languageName;
					inputFieldValues[3] = nativeScriptLanguageName;
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoRltspTypeCode: ",
							"Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response we use POST url but selecting GET method.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response we use POST url but selecting GET method. ");
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
					// Assert.fail("Test Failed");
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
				} else if (meta.contains("timestamp")) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp ispresent");
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
	public void TC_28() {
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
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			test.info("URL: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, "", token, getEndPoinUrl, fileName, testCaseID);
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
			if (Wscode == 401 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = languageName;
					inputFieldValues[3] = nativeScriptLanguageName;
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoRltspTypeCode: ",
							"Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: " };

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
					// Assert.fail("Test Failed");
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
				} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
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
	public void TC_29() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			if (Wscode != 200) {
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for (int i = 0; i < errorMsgLength; i++) {
					errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
					errorMsg2.add(js.getString("errors[" + i + "].message"));
				}

				String expectMessage = resMsgs.langCntryCdrequiredFieldMsg;
				if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.info("Response status code 400 validation passed: " + Wscode);
					test.pass("Response status code 400 validation passed: " + Wscode);
					test.pass("Response meta validation passed");

					test.pass("Response API version number validation passed");
					ValidationFields.timestampValidation(js, res);
					ValidationFields.transactionIdValidation(js, res);
					// ***error message validation

					if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

						logger.info(
								"Expected error message is getting received in response when the countryCode is null or Empty in JSON");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass(
								"Expected error message is getting received in response when the countryCode is null or Empty in JSON");
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

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "userName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				test.fail("Expected Error message not getting");
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
	public void TC_30() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			if (Wscode != 200) {
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for (int i = 0; i < errorMsgLength; i++) {
					errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
					errorMsg2.add(js.getString("errors[" + i + "].message"));
				}

				String expectMessage = resMsgs.langLoclCdrequiredField;
				if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.info("Response status code 400 validation passed: " + Wscode);
					test.pass("Response status code 400 validation passed: " + Wscode);
					test.pass("Response meta validation passed");

					test.pass("Response API version number validation passed");
					ValidationFields.timestampValidation(js, res);
					ValidationFields.transactionIdValidation(js, res);
					// ***error message validation

					if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

						logger.info(
								"Expected error message is getting received in response when localeCd is passed as empty/ null in JSON request");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass(
								"Expected error message is getting received in response when localeCd is passed as empty/ null in JSON request");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "languageCode" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else {
				test.fail("Expected Error message not getting");
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
	public void TC_31() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_32() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_33() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_34() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_35() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_36() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_37() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							localeDBValidation(testCaseID, res);
							dowDBValidation(testCaseID, res);
							moyDBValidation(testCaseID, res);
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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
	public void TC_38() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			if (Wscode != 200) {
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for (int i = 0; i < errorMsgLength; i++) {
					errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
					errorMsg2.add(js.getString("errors[" + i + "].message"));
				}

				String expectMessage = resMsgs.invalidDateMsg;
				if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
					logger.info("Response status code 400 validation passed: " + Wscode);
					test.pass("Response status code 400 validation passed: " + Wscode);
					test.pass("Response meta validation passed");

					test.pass("Response API version number validation passed");
					ValidationFields.timestampValidation(js, res);
					ValidationFields.transactionIdValidation(js, res);
					// ***error message validation

					if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

						logger.info(
								"Expected error message is getting received in response   when the effectiveDate is other than timestamp format in JSON request");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass(
								"Expected error message is getting received in response   when the effectiveDate is other than timestamp format in JSON request");
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

					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "userName" + expectMessage);
					Assert.fail("Test Failed");
				}
			} else
				test.fail("Expected response Not getting and Getting status code 200 instead of 400");
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
	public void TC_39() {
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostNewRequest(userId, languageCode, nativeScriptLanguageName,
					nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd, cldrVersionDate,
					cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}

			String expectMessage = resMsgs.invalidDateMsg;
			if (Wscode == 400 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response   when the expirationDate is other than timestamp format in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response   when the expirationDate is other than timestamp format in JSON request");
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
	public void TC_40() {
		// ***get test case ID with method name
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send the data to create request and get request

			String payload = PostMethod.langPostMulitpleLocaleCdNewRequest(userId, languageCode,
					nativeScriptLanguageName, nativeScriptCode, languageName, localeCd, countryCode, localesScriptCd,
					cldrVersionDate, cldrVersionNumber, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translatedDOWs, translatedMOYs, localeCd1, countryCode1);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url

			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langNewDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[6];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = languageCode;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = nativeScriptCode;
						inputFieldValues[4] = languageName;
						inputFieldValues[5] = userId;

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ",
								"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
								"Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
								"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: " };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between Language input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between Language input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
							String locd = localeCd;
							String cntryCd = countryCode;
							for (int i = 0; i < 2; i++) {
								multipleLocaleDBValidation(testCaseID, res, locd, cntryCd);
								dowDBValidation(testCaseID, res);
								moyDBValidation(testCaseID, res);
								locd = localeCd1;
								cntryCd = countryCode1;
							}

						} else {
							logger.error("Comparison between Langauge input data & DB data not matching and failed");
							logger.error("------------------------------------------------------------------");
							test.fail("Comparison between Langauge input data & DB data not matching and failed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
									"Comparison between input data & DB data not matching and failed");
							String locd = localeCd;
							String cntryCd = countryCode;
							for (int i = 0; i < 2; i++) {
								multipleLocaleDBValidation(testCaseID, res, locd, cntryCd);
								dowDBValidation(testCaseID, res);
								moyDBValidation(testCaseID, res);
								locd = localeCd1;
								cntryCd = countryCode1;
							}
							// Assert.fail("Test Failed");
						}
					} else {
						logger.error("Success message is not getting received as expected in response");
						test.fail("Success message is not getting received as expected in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
								writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
								"Success message is not getting received as expected in response");
						// Assert.fail("Test Failed");
					}
				} else {
					logger.error("languageCode  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("languageCode is not available in response");
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

	// JMS Validation
	@Test(priority = 1)
	public void TC_41() {
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

			JSONObject getJMSResult = jmsReader.messageGetsPublished("LANGUAGE");
			if (getJMSResult != null) {
				// *** get JMS response

				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Recieved:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				String langCode = getJMSResult.getJSONObject("data").getString("languageCode");
				if (langCode != null) {

					String nativeScriptLanguageName = getJMSResult.getJSONObject("data")
							.getString("nativeScriptLanguageName");
					String nativeScriptCode = getJMSResult.getJSONObject("data").getString("nativeScriptCode");
					String languageName = getJMSResult.getJSONObject("data").getString("languageName");

					String[] jmsLangauegFieldValues = new String[4];

					jmsLangauegFieldValues[0] = langCode;
					jmsLangauegFieldValues[1] = nativeScriptLanguageName;
					jmsLangauegFieldValues[2] = nativeScriptCode;
					jmsLangauegFieldValues[3] = languageName;

					String langPutQuery = query.langPostQuery(langCode);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.langPutJMSNewDbFields();
					// ***get the result from DB
					List<String> getLangResultDB = DbConnect.getResultSetFor(langPutQuery, fields, fileName,
							testCaseID);
					testResult = TestResultValidation.testValidationForJMS(jmsLangauegFieldValues, getLangResultDB);

					String[] jmsFieldNames = { "JMS_languageCode: ", "JMS_nativeScriptLanguageName: ",
							"JMS_nativeScriptCode: ", "JMS_languageName: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(jmsLangauegFieldValues, jmsFieldNames);
					String[] dbFieldNames = { "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
							"DB_nativeScriptCode: ", "DB_languageName: " };
					writableDB_Fields = Miscellaneous.geoDBFieldNames(getLangResultDB, dbFieldNames);
					test.info("JMS Language Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					test.info("DB Language Data Values:");
					test.info(writableDB_Fields.replaceAll("\n", "<br />"));
					if (testResult) {
						logger.info("Comparison between Language JMS data & DB data matching and passed");
						logger.info("------------------------------------------------------------------");
						test.pass("Comparison between Language JMS data & DB data matching and passed");
					} else {
						logger.info("Comparison between Language JMS data & DB data not matching and failed");
						logger.info("------------------------------------------------------------------");
						test.fail("Comparison between Language JMS data & DB data not matching and failed");
					}

					// *** Reading Locale section array
					List<String> jmsLocalesFieldValues = new ArrayList<>();
					List<String> jmsDOWFieldValues = new ArrayList<>();
					List<String> jmsMOYFieldValues = new ArrayList<>();
					JSONArray jsonArrayLocaleSection = getJMSResult.getJSONObject("data").getJSONArray("locales");
					JSONObject jObj = null, jobjDOW = null, jobjMOY = null;
					JSONArray jsonArrayDOW = null;
					JSONArray jsonArrayMOY = null;
					int count = 1;
					for (int i = 0; i < jsonArrayLocaleSection.length(); i++) {
						List<String> getDOWResultDBFinal = new ArrayList<String>();
						List<String> getMOYResultDBFinal = new ArrayList<String>();
						test.info("Locale Validation: " + (i + 1));
						jObj = jsonArrayLocaleSection.getJSONObject(i);

						jmsLocalesFieldValues.add(jObj.optString("localeCode"));
						jmsLocalesFieldValues.add(jObj.optString("countryCode"));
						jmsLocalesFieldValues.add(jObj.optString("scriptCode"));
						jmsLocalesFieldValues.add(jObj.optString("cldrVersionDate"));
						jmsLocalesFieldValues.add(jObj.optString("cldrVersionNumber"));
						jmsLocalesFieldValues.add(jObj.optString("dateFullFormatDescription"));
						jmsLocalesFieldValues.add(jObj.optString("dateLongFormatDescription"));
						jmsLocalesFieldValues.add(jObj.optString("dateMediumFormatDescription"));
						jmsLocalesFieldValues.add(jObj.optString("dateShortFormatDescription"));
						jmsLocalesFieldValues.add(jObj.optString("effectiveDate"));
						jmsLocalesFieldValues.add(jObj.optString("expirationDate"));

						jsonArrayDOW = jObj.getJSONArray("translatedDOWs");
						for (int j = 0; j < jsonArrayDOW.length(); j++) {
							jobjDOW = jsonArrayDOW.getJSONObject(j);
							jmsDOWFieldValues.add(jobjDOW.optString("dayOfWeekNumber"));
							jmsDOWFieldValues.add(jobjDOW.optString("translatedDayOfWeekName"));

							String localeDOWJMSQuery = query.langTrnslDowPostQuery(langCode,
									jobjDOW.optString("dayOfWeekNumber"));
							// ***get the fields needs to be validate in DB
							List<String> fieldsDOW = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getDOWResultDB = DbConnect.getResultSetFor(localeDOWJMSQuery, fieldsDOW,
									fileName, testCaseID);
							getDOWResultDBFinal.addAll(getDOWResultDB);
						}

						jsonArrayMOY = jObj.getJSONArray("translatedMOYs");
						for (int j = 0; j < jsonArrayMOY.length(); j++) {
							jobjMOY = jsonArrayMOY.getJSONObject(j);
							jmsMOYFieldValues.add(jobjMOY.optString("monthOfYearNumber"));
							jmsMOYFieldValues.add(jobjMOY.optString("translatedMonthOfYearName"));

							String moyJMSQuery = query.langTrnslMonthOfYearPostQuery(langCode,
									jobjMOY.optString("monthOfYearNumber"));
							// ***get the fields needs to be validate in DB
							List<String> fieldsMOY = ValidationFields.langTrnslMonthOfYearDbFields();
							// ***get the result from DB
							List<String> getResultDB3 = DbConnect.getResultSetFor(moyJMSQuery, fieldsMOY, fileName,
									testCaseID);
							getMOYResultDBFinal.addAll(getResultDB3);
						}

						// Locale section validation start
						String formatLocalesEffectiveDate = jObj.optString("effectiveDate");
						String formatLocalesExpirationDate = jObj.optString("expirationDate");
						Date dateLocalesEffectiveDate = null;
						Date dateLocalesExpirationDate = null;
						// ***Converting dateFormat according to DB
						DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
						DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

						dateLocalesEffectiveDate = srcDf.parse(formatLocalesEffectiveDate);
						dateLocalesExpirationDate = srcDf.parse(formatLocalesExpirationDate);

						formatLocalesEffectiveDate = destDf.format(dateLocalesEffectiveDate);
						formatLocalesEffectiveDate = formatLocalesEffectiveDate.toUpperCase();
						formatLocalesExpirationDate = destDf.format(dateLocalesExpirationDate);
						formatLocalesExpirationDate = formatLocalesExpirationDate.toUpperCase();

						String langLocaleJMSQuery = query.langLocalesNewPutQuery(langCode, jObj.optString("localeCode"),
								formatLocalesEffectiveDate, formatLocalesExpirationDate);

						// ***get the fields needs to be validate in DB
						List<String> fieldsLocale = ValidationFields.langLocalesNewDbFields();
						// ***get the result from DB
						List<String> getResultDBLocale = DbConnect.getResultSetFor(langLocaleJMSQuery, fieldsLocale,
								fileName, testCaseID);

						String[] localeDbFieldValues = { jmsLocalesFieldValues.get(i), getResultDBLocale.get(i),
								jmsLocalesFieldValues.get(i + 1), getResultDBLocale.get(i + 1),
								jmsLocalesFieldValues.get(i + 2), getResultDBLocale.get(i + 2),
								jmsLocalesFieldValues.get(i + 3), getResultDBLocale.get(i + 3),
								jmsLocalesFieldValues.get(i + 4), getResultDBLocale.get(i + 4),
								jmsLocalesFieldValues.get(i + 5), getResultDBLocale.get(i + 5),
								jmsLocalesFieldValues.get(i + 6), getResultDBLocale.get(i + 6),
								jmsLocalesFieldValues.get(i + 7), getResultDBLocale.get(i + 7),
								jmsLocalesFieldValues.get(i + 8), getResultDBLocale.get(i + 8),
								jmsLocalesFieldValues.get(i + 9), getResultDBLocale.get(i + 9),
								jmsLocalesFieldValues.get(i + 10), getResultDBLocale.get(i + 10) };

						String[] localeDbFieldNames = { "JMS_localeCode_" + count + ": ",
								"DB_localeCode_" + count + ": ", "JMS_countryCode_" + count + ": ",
								"DB_countryCode_" + count + ": ", "JMS_scriptCode_" + count + ": ",
								"DB_scriptCode_" + count + ": ", "JMS_cldrVersionDate_" + count + ": ",
								"DB_cldrVersionDate_" + count + ": ", "JMS_cldrVersionNumber_" + count + ": ",
								"DB_cldrVersionNumber_" + count + ": ", "JMS_dateFullFormatDescription_" + count + ": ",
								"DB_dateFullFormatDescription_" + count + ": ",
								"JMS_dateLongFormatDescription_" + count + ": ",
								"DB_dateLongFormatDescription" + count + ": ",
								"JMS_dateMediumFormatDescription_" + count + ": ",
								"DB_dateMediumFormatDescription_" + count + ": ",
								"JMS_dateShortFormatDescription" + count + ": ",
								"DB_dateShortFormatDescription_" + count + ": ", "JMS_effectiveDate_" + count + ": ",
								"DB_effectiveDate_" + count + ": ", "JMS_expirationDate_" + count + ": ",
								"DB_expirationDate_" + count + ": " };

						if (getResultDBLocale.get(i).toString().equals(jmsLocalesFieldValues.get(i).toString())
								&& (getResultDBLocale.get(i + 1).toString()
										.equals(jmsLocalesFieldValues.get(i + 1).toString()))
								&& (getResultDBLocale.get(i + 2).toString()
										.equals(jmsLocalesFieldValues.get(i + 2).toString()))
								&& (getResultDBLocale.get(i + 3).toString()
										.equals(jmsLocalesFieldValues.get(i + 3).toString()))
								&& (getResultDBLocale.get(i + 4).toString()
										.equals(jmsLocalesFieldValues.get(i + 4).toString()))
								&& (getResultDBLocale.get(i + 5).toString()
										.equals(jmsLocalesFieldValues.get(i + 5).toString()))
								&& (getResultDBLocale.get(i + 6).toString()
										.equals(jmsLocalesFieldValues.get(i + 6).toString()))
								&& (getResultDBLocale.get(i + 7).toString()
										.equals(jmsLocalesFieldValues.get(i + 7).toString()))
								&& (getResultDBLocale.get(i + 8).toString()
										.equals(jmsLocalesFieldValues.get(i + 8).toString()))
								&& (getResultDBLocale.get(i + 9).toString()
										.equals(jmsLocalesFieldValues.get(i + 9).toString()))
								&& (getResultDBLocale.get(i + 10).toString()
										.equals(jmsLocalesFieldValues.get(i + 10).toString()))) {
							// ***write result to excel

							logger.info("JMS locale section validation: Passed");
							writableResult = Miscellaneous.geoFieldInputNames(localeDbFieldValues, localeDbFieldNames);
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
						} else {

							// ***write result to excel
							logger.info("JMS locale section validation: Failed");
							writableResult = Miscellaneous.geoFieldInputNames(localeDbFieldValues, localeDbFieldNames);
							test.fail(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "");
						}

						logger.info("DOW Table Validation Starts:");
						test.info("DOW Table Validation Starts:");
						int cnt = 1, tempDow = 0;
						for (int dow = 0; dow < jsonArrayDOW.length(); dow++) {
							if (getDOWResultDBFinal.get(tempDow).toString()
									.equals(jmsDOWFieldValues.get(tempDow).toString())
									&& (getDOWResultDBFinal.get(tempDow + 1).toString()
											.equals(jmsDOWFieldValues.get(tempDow + 1).toString()))) {

								// ***write result to excel
								String[] responseDbFieldValues = { jmsDOWFieldValues.get(tempDow),
										getDOWResultDBFinal.get(tempDow), jmsDOWFieldValues.get(tempDow + 1),
										getDOWResultDBFinal.get(tempDow + 1) };
								String[] responseDbFieldNames = { "JMS_dayOfWeekNumber_" + cnt + ": ",
										"DB_dayOfWeekNumber_" + cnt + ": ", "JMS_translatedDayOfWeekName_" + cnt + ": ",
										"DB_translatedDayOfWeekName_" + cnt + ": " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								tempDow += 2;
							} else {
								// ***write result to excel
								String[] responseDbFieldValues = { jmsDOWFieldValues.get(tempDow),
										getDOWResultDBFinal.get(tempDow), jmsDOWFieldValues.get(tempDow + 1),
										getDOWResultDBFinal.get(tempDow + 1) };
								String[] responseDbFieldNames = { "JMS_dayOfWeekNumber_" + cnt + ": ",
										"DB_dayOfWeekNumber_" + cnt + ": ", "JMS_translatedDayOfWeekName_" + cnt + ": ",
										"DB_translatedDayOfWeekName_" + cnt + ": " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.fail(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Fail", "");
								tempDow += 2;
							}

							cnt++;
						}

						logger.info("MOY Table Validation Starts:");
						test.info("MOY Table Validation Starts:");
						int cnt1 = 1, tempMOY = 0;
						for (int moy = 0; moy < jsonArrayMOY.length(); moy++) {
							if (getMOYResultDBFinal.get(tempMOY).toString()
									.equals(jmsMOYFieldValues.get(tempMOY).toString())
									&& (getMOYResultDBFinal.get(tempMOY + 1).toString()
											.equals(jmsMOYFieldValues.get(tempMOY + 1).toString()))) {
								// ***write result to excel
								String[] responseDbFieldValues = { jmsMOYFieldValues.get(tempMOY),
										getMOYResultDBFinal.get(tempMOY), jmsMOYFieldValues.get(tempMOY + 1),
										getMOYResultDBFinal.get(tempMOY + 1) };
								String[] responseDbFieldNames = { "JMS_monthOfYearNumber_" + cnt1 + ": ",
										"DB_monthOfYearNumber_" + cnt1 + ": ",
										"JMS_translatedMonthOfYearName_" + cnt1 + ": ",
										"DB_translatedMonthOfYearName_" + cnt1 + ": " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								tempMOY += 2;
							} else {
								// ***write result to excel
								String[] responseDbFieldValues = { jmsMOYFieldValues.get(tempMOY),
										getMOYResultDBFinal.get(tempMOY), jmsMOYFieldValues.get(tempMOY + 1),
										getMOYResultDBFinal.get(tempMOY + 1) };
								String[] responseDbFieldNames = { "JMS_monthOfYearNumber_" + cnt1 + ": ",
										"DB_monthOfYearNumber_" + cnt1 + ": ",
										"JMS_translatedMonthOfYearName_" + cnt1 + ": ",
										"DB_translatedMonthOfYearName_" + cnt1 + ": " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.fail(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Fail", "");
								tempMOY += 2;
							}

							cnt1++;
						}
						count++;
					}

				} else {
					logger.error("Lang code  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Lang code is not available in response");
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

	public boolean auditLanguageDBValidation(String testCaseID, Response res)
			throws ParseException, java.text.ParseException {
		List<String> getAuditResultDBFinal = new ArrayList<String>();
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("********* Audit Table Validation Starts *******");
		test.info("************Audit Table Validation Starts ********");

		// Audit Query1
		String langPostAuditQuery1 = query.langPostAuditQuery(languageCode);
		List<String> auditFields = ValidationFields.langNewAuditDbFields();
		List<String> getAuditResultDB1 = DbConnect.getResultSetFor(langPostAuditQuery1, auditFields, fileName,
				testCaseID);
		getAuditResultDBFinal.addAll(getAuditResultDB1);

		String revisionTypeCd = "0";
		String[] inputAuditFieldValues = new String[7];

		inputAuditFieldValues[0] = userId;
		inputAuditFieldValues[1] = languageCode;
		inputAuditFieldValues[2] = nativeScriptLanguageName;
		inputAuditFieldValues[3] = nativeScriptCode;
		inputAuditFieldValues[4] = languageName;
		inputAuditFieldValues[5] = userId;
		inputAuditFieldValues[6] = revisionTypeCd;

		boolean testResult = false;
		testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues, getAuditResultDBFinal,
				resFields);

		String[] inputAuditFieldNames = { "Input_UserName: ", "Input_languageCode: ",
				"Input_nativeScriptLanguageName: ", "Input_nativeScriptCode: ", "Input_languageName: ",
				"Input_LastUpdateUserName: ", "Expected_RevisionTypeCd: " };

		writableAuditInputFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues, inputAuditFieldNames);

		String[] auditDBFieldNames = { "DB_UserName: ", "DB_languageCode: ", "DB_nativeScriptLanguageName: ",
				"DB_nativeScriptCode: ", "DB_languageName: ", "DB_LastUpdateUserName: ", "DB_RevisionTypeCd: " };

		writableAuditDB_Fields = Miscellaneous.geoDBFieldNames(getAuditResultDBFinal, auditDBFieldNames);
		test.info("Input Audit Table Data Values:");
		test.info(writableAuditInputFields.replaceAll("\n", "<br />"));
		test.info("DB Audit Table Data Values:");
		test.info(writableAuditDB_Fields.replaceAll("\n", "<br />"));
		if (testResult) {
			ex.writeExcel(fileName, testCaseID, "Audit Language Table Validation", scenarioType, "",
					writableAuditInputFields, writableAuditDB_Fields, "", "", "", "Pass",
					"Audit Language Table validation");
			logger.info("Comparison between Audit Language input data & DB data matching and passed");
			logger.info("------------------------------------------------------------------");
			test.pass("Comparison between Audit Language input data & DB data matching and passed");
			auditlocaleDBValidation(testCaseID, res);
			auditDOWDBValidation(testCaseID, res);
			auditMOYDBValidation(testCaseID, res);
			test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			return testResult;
		} else {
			logger.error("Comparison between Audit Language input data & DB data not matching and failed");
			logger.error("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "", writableInputFields,
					writableDB_Fields, "", "", "", "Fail", "Audit Table validation");
			auditlocaleDBValidation(testCaseID, res);
			auditDOWDBValidation(testCaseID, res);
			auditMOYDBValidation(testCaseID, res);
			test.log(Status.FAIL, MarkupHelper.createLabel("test status", ExtentColor.RED));
			return testResult;
		}

	}

	// as per swagger
	public boolean localeDBValidation(String testCaseID, Response res) throws java.text.ParseException, ParseException {
		Date date = new Date();
		String todaysDate = new SimpleDateFormat("yyy-MM-dd").format(date);
		if (localesEffectiveDate.isEmpty()) {
			localesEffectiveDate = todaysDate;
		}
		if (localesExpirationDate.isEmpty()) {
			localesExpirationDate = "9999-12-31";
		}

		if (localesEffectiveDate.isEmpty()) {
			localesEffectiveDate = todaysDate;
		}
		if (localesExpirationDate.isEmpty()) {
			localesExpirationDate = "9999-12-31";
		}
		String formatLocalesEffectiveDate = localesEffectiveDate;
		String formatLocalesExpirationDate = localesExpirationDate;
		Date dateLocalesEffectiveDate = null;
		Date dateLocalesExpirationDate = null;
		// ***Converting dateFormat according to DB
		DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

		dateLocalesEffectiveDate = srcDf.parse(formatLocalesEffectiveDate);
		dateLocalesExpirationDate = srcDf.parse(formatLocalesExpirationDate);

		formatLocalesEffectiveDate = destDf.format(dateLocalesEffectiveDate);
		formatLocalesEffectiveDate = formatLocalesEffectiveDate.toUpperCase();
		formatLocalesExpirationDate = destDf.format(dateLocalesExpirationDate);
		formatLocalesExpirationDate = formatLocalesExpirationDate.toUpperCase();
		String langLocalePutQuery = query.langLocalesNewPutQuery(languageCode, localeCd, formatLocalesEffectiveDate,
				formatLocalesExpirationDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langLocalesNewDbFields();
		// ***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(langLocalePutQuery, fields, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		String[] inputFieldValues = { localeCd, countryCode, localesScriptCd, cldrVersionDate, cldrVersionNumber,
				dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
				dateShortFormatDescription, localesEffectiveDate, localesExpirationDate };

		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Language Locale Table Validation Starts:");
		test.info("Language Locale Table Validation Starts:");
		boolean result = false;
		result = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		String[] inputFieldNames = { "Input_localeCd: ", "Input_countryCode: ", "Input_localesScriptCd: ",
				"Input_cldrVersionDate: ", "Input_cldrVersionNumber: ", "Input_dateFullFormatDescription: ",
				"Input_dateLongFormatDescription: ", "Input_dateMediumFormatDescription: ",
				"Input_dateShortFormatDescription: ", "Input_localesEffectiveDate: ", "Input_localesExpirationDate: " };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_localeCd: ", "DB_countryCode: ", "DB_localesScriptCd: ", "DB_cldrVersionDate: ",
				"DB_cldrVersionNumber: ", "DB_dateFullFormatDescription: ", "DB_dateLongFormatDescription: ",
				"DB_dateMediumFormatDescription: ", "DB_dateShortFormatDescription: ", "DB_localesEffectiveDate: ",
				"DB_localesExpirationDate: " };
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (result) {
			logger.info("Comparison between Locale input data & DB data matching and passed");
			test.pass("Comparison between Locale input data & DB data matching and passed");
			ex.writeExcel(fileName, testCaseID, "Language Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass", "Language Locales Table Validation");
			return result;
		} else {
			logger.info("Comparison between Locale input data & DB data not matching is failed");
			test.fail("Comparison between Locale input data & DB data not matching and failed");
			ex.writeExcel(fileName, testCaseID, "Language Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Fail", "Language Locales Table Validation");
			return result;
		}
	}

	// as per swagger
	public boolean multipleLocaleDBValidation(String testCaseID, Response res, String locd, String cntryCd)
			throws java.text.ParseException, ParseException {
		Date date = new Date();
		String todaysDate = new SimpleDateFormat("yyy-MM-dd").format(date);
		if (localesEffectiveDate.isEmpty()) {
			localesEffectiveDate = todaysDate;
		}
		if (localesExpirationDate.isEmpty()) {
			localesExpirationDate = "9999-12-31";
		}

		if (localesEffectiveDate.isEmpty()) {
			localesEffectiveDate = todaysDate;
		}
		if (localesExpirationDate.isEmpty()) {
			localesExpirationDate = "9999-12-31";
		}
		String formatLocalesEffectiveDate = localesEffectiveDate;
		String formatLocalesExpirationDate = localesExpirationDate;
		Date dateLocalesEffectiveDate = null;
		Date dateLocalesExpirationDate = null;
		// ***Converting dateFormat according to DB
		DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");

		dateLocalesEffectiveDate = srcDf.parse(formatLocalesEffectiveDate);
		dateLocalesExpirationDate = srcDf.parse(formatLocalesExpirationDate);

		formatLocalesEffectiveDate = destDf.format(dateLocalesEffectiveDate);
		formatLocalesEffectiveDate = formatLocalesEffectiveDate.toUpperCase();
		formatLocalesExpirationDate = destDf.format(dateLocalesExpirationDate);
		formatLocalesExpirationDate = formatLocalesExpirationDate.toUpperCase();
		String langLocalePutQuery = query.langLocalesNewPutQuery(languageCode, locd, formatLocalesEffectiveDate,
				formatLocalesExpirationDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langLocalesNewDbFields();
		// ***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(langLocalePutQuery, fields, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		String[] inputFieldValues = { locd, cntryCd, localesScriptCd, cldrVersionDate, cldrVersionNumber,
				dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
				dateShortFormatDescription, localesEffectiveDate, localesExpirationDate };

		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Language Locale Table Validation Starts:");
		test.info("Language Locale Table Validation Starts:");
		boolean result = false;
		result = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		String[] inputFieldNames = { "Input_localeCd: ", "Input_countryCode: ", "Input_localesScriptCd: ",
				"Input_cldrVersionDate: ", "Input_cldrVersionNumber: ", "Input_dateFullFormatDescription: ",
				"Input_dateLongFormatDescription: ", "Input_dateMediumFormatDescription: ",
				"Input_dateShortFormatDescription: ", "Input_localesEffectiveDate: ", "Input_localesExpirationDate: " };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_localeCd: ", "DB_countryCode: ", "DB_localesScriptCd: ", "DB_cldrVersionDate: ",
				"DB_cldrVersionNumber: ", "DB_dateFullFormatDescription: ", "DB_dateLongFormatDescription: ",
				"DB_dateMediumFormatDescription: ", "DB_dateShortFormatDescription: ", "DB_localesEffectiveDate: ",
				"DB_localesExpirationDate: " };
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (result) {
			logger.info("Comparison between Locale input data & DB data matching and passed");
			test.pass("Comparison between Locale input data & DB data matching and passed");
			ex.writeExcel(fileName, testCaseID, "Language Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass", "Language Locales Table Validation");
			return result;
		} else {
			logger.info("Comparison between Locale input data & DB data not matching is failed");
			test.fail("Comparison between Locale input data & DB data not matching and failed");
			ex.writeExcel(fileName, testCaseID, "Language Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Fail", "Language Locales Table Validation");
			return result;
		}
	}

	public boolean auditlocaleDBValidation(String testCaseID, Response res)
			throws java.text.ParseException, ParseException {
		Date date = new Date();
		String todaysDate = new SimpleDateFormat("yyy-MM-dd").format(date);
		if (localesEffectiveDate.isEmpty()) {
			localesEffectiveDate = todaysDate;
		}
		if (localesExpirationDate.isEmpty()) {
			localesExpirationDate = "9999-12-31";
		}
		String formatLocalesEffectiveDate = localesEffectiveDate;
		String formatLocalesExpirationDate = localesExpirationDate;
		Date dateLocalesEffectiveDate = null;
		Date dateLocalesExpirationDate = null;
		// ***Converting dateFormat according to DB
		DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");

		dateLocalesEffectiveDate = srcDf.parse(formatLocalesEffectiveDate);
		dateLocalesExpirationDate = srcDf.parse(formatLocalesExpirationDate);

		formatLocalesEffectiveDate = destDf.format(dateLocalesEffectiveDate);
		formatLocalesEffectiveDate = formatLocalesEffectiveDate.toUpperCase();
		formatLocalesExpirationDate = destDf.format(dateLocalesExpirationDate);
		formatLocalesExpirationDate = formatLocalesExpirationDate.toUpperCase();
		
		String revisionTypeCd = "0";

		String auditLangLocalePostQuery = query.auditLangLocalesNewPutQuery(languageCode, localesScriptCd, localeCd, revisionTypeCd);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.auditLangLocalesNewDbFields();
		// ***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(auditLangLocalePostQuery, fields, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		String[] inputFieldValues = { localeCd, countryCode, localesScriptCd, cldrVersionDate, cldrVersionNumber,
				dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
				dateShortFormatDescription, localesEffectiveDate, localesExpirationDate, "0" };
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Audit Language Locale Table Validation Starts:");
		test.info("Audit Language Locale Table Validation Starts:");
		boolean result = false;
		result = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		String[] inputFieldNames = { "Input_localeCd: ", "Input_countryCode: ", "Input_localesScriptCd: ",
				"Input_cldrVersionDate: ", "Input_cldrVersionNumber: ", "Input_dateFullFormatDescription: ",
				"Input_dateLongFormatDescription: ", "Input_dateMediumFormatDescription: ",
				"Input_dateShortFormatDescription: ", "Input_localesEffectiveDate: ", "Input_localesExpirationDate: ",
				"Expected_RevisionTypeCode:" };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_localeCd: ", "DB_countryCode: ", "DB_localesScriptCd: ", "DB_cldrVersionDate: ",
				"DB_cldrVersionNumber: ", "DB_dateFullFormatDescription: ", "DB_dateLongFormatDescription: ",
				"DB_dateMediumFormatDescription: ", "DB_dateShortFormatDescription: ", "DB_localesEffectiveDate: ",
				"DB_localesExpirationDate: ", "DB_RevisionTypeCode: " };
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (result) {
			logger.info("Comparison between Audit Locale input data & DB data matching and passed");
			ex.writeExcel(fileName, testCaseID, "Audit Locales Table Validation", scenarioType, "", writableInputFields,
					writableDB_Fields, "", "", "", "Pass", "Language Locales Table Validation");
			test.pass("Comparison between Locale input data & DB table data matching and passed");
			return result;
		} else {
			logger.info("Comparison between Audit Locale input data & DB data matching is failed");
			test.fail("Comparison between Locale input data & DB table data matching is failed");
			ex.writeExcel(fileName, testCaseID, "Language Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Fail", "Language Locales Table Validation");

			return result;
		}
	}

	public boolean dowDBValidation(String testCaseID, Response res) {
		List<String> getResultDBFinal = new ArrayList<String>();
		String[] inputFieldValues = new String[14];
		for (int i = 0; i < translatedDOWs.size() / 2; i++) {

			String languageDOWPostQuery = query.langTrnslDowPostQuery(languageCode,
					translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1)));
			// ***get the fields needs to be validate in DB
			List<String> fields2 = ValidationFields.langTrnslDowDbFields();
			// ***get the result from DB
			List<String> getResultDB2 = DbConnect.getResultSetFor(languageDOWPostQuery, fields2, fileName, testCaseID);
			getResultDBFinal.addAll(getResultDB2);
		}
		int j = 0;
		for (int i = 0; i < translatedDOWs.size() / 2; i++) {

			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
			inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
			j += 2;

		}

		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("DOW Table Validation Starts:");
		test.info("DOW Table Validation Starts:");
		boolean testResult = false;
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
		// ***write result to excel
		String[] inputFieldNames = { "Input_dowNbr1: ", "Input_transDowName1: ", "Input_dowNbr2: ",
				"Input_transDowName2: ", "Input_dowNbr3: ", "Input_transDowName3: ", "Input_dowNbr4: ",
				"Input_transDowName4: ", "Input_dowNbr5: ", "Input_transDowName5: ", "Input_dowNbr6: ",
				"Input_transDowName6: ", "Input_dowNbr7: ", "Input_transDowName7: " };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_dowNbr1: ", "DB_transDowName1: ", "DB_dowNbr2: ", "DB_transDowName2: ",
				"DB_dowNbr3: ", "DB_transDowName3: ", "DB_dowNbr4: ", "DB_transDowName4: ", "DB_dowNbr5: ",
				"DB_transDowName5: ", "DB_dowNbr6: ", "DB_transDowName6: ", "DB_dowNbr7: ", "DB_transDowName7: " };

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
		if (testResult) {
			logger.info("Comparison between DOW input data & DB data is  matching and passed");
			test.pass("Comparison between DOW input data & DB data is matching and passed");
			return true;
		} else {
			logger.info("Comparison between DOW input data & DB data is not matching and failed");
			test.fail("Comparison between DOW input data & DB data is not matching and failed");
			return false;
		}

	}
	// ***get the values from test data sheet

	public boolean moyDBValidation(String testCaseID, Response res) {
		List<String> getResultDBFinal = new ArrayList<String>();
		String[] inputFieldValues = new String[24];
		for (int i = 0; i < translatedMOYs.size() / 2; i++) {

			String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(languageCode,
					translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1)));
			// ***get the fields needs to be validate in DB
			List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
			// ***get the result from DB
			List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
			getResultDBFinal.addAll(getResultDB3);
		}

		int k = 0;
		for (int i = 0; i < translatedMOYs.size() / 2; i++) {

			inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
			inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
			k += 2;
		}

		// ***get response fields values
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("MOY Table Validation Starts:");
		test.info("MOY Table Validation Starts:");
		boolean testResult = false;
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
		// ***write result to excel
		String[] inputFieldNames = { "Input_mthOfYrNbr1: ", "Input_transMoyName1: ", "Input_mthOfYrNbr2: ",
				"Input_transMoyName2: ", "Input_mthOfYrNbr3: ", "Input_transMoyName3: ", "Input_mthOfYrNbr4: ",
				"Input_transMoyName4: ", "Input_mthOfYrNbr5: ", "Input_transMoyName5: ", "Input_mthOfYrNbr6: ",
				"Input_transMoyName6: ", "Input_mthOfYrNbr7: ", "Input_transMoyName7: ", "Input_mthOfYrNbr8: ",
				"Input_transMoyName8: ", "Input_mthOfYrNbr9: ", "Input_transMoyName9: ", "Input_mthOfYrNbr10: ",
				"Input_transMoyName10: ", "Input_mthOfYrNbr11: ", "Input_transMoyName11: ", "Input_mthOfYrNbr12: ",
				"Input_transMoyName12: " };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_mthOfYrNbr1: ", "DB_transMoyName1: ", "DB_mthOfYrNbr2: ", "DB_transMoyName2: ",
				"DB_mthOfYrNbr3: ", "DB_transMoyName3: ", "DB_mthOfYrNbr4: ", "DB_transMoyName4: ", "DB_mthOfYrNbr5: ",
				"DB_transMoyName5: ", "DB_mthOfYrNbr6: ", "DB_transMoyName6: ", "DB_mthOfYrNbr7: ",
				"DB_transMoyName7: ", "DB_mthOfYrNbr8: ", "DB_transMoyName8: ", "DB_mthOfYrNbr9: ",
				"DB_transMoyName9: ", "DB_mthOfYrNbr10: ", "DB_transMoyName10: ", "DB_mthOfYrNbr11: ",
				"DB_transMoyName11: ", "DB_mthOfYrNbr12: ", "DB_transMoyName12: " };

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
		if (testResult) {
			logger.info("Comparison between MOY input data & DB data is matching and passed");
			test.pass("Comparison between MOY input data & DB data is matching and passed");
			return true;
		} else {
			logger.info("Comparison between MOY input data & DB data is not matching and failed");
			test.fail("Comparison between MOY input data & DB data is not matching and failed");
			return true;
		}

	}

	public boolean auditDOWDBValidation(String testCaseID, Response res) {
		List<String> getResultDBFinal = new ArrayList<String>();
		String[] inputFieldValues = new String[21];
		String revisionTypeCd = "0";
		for (int i = 0; i < translatedDOWs.size() / 2; i++) {

			String dowPutAuditQuery = query.langTrnslDowPostAuditQuery(languageCode,
					translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1)), revisionTypeCd);
			// ***get the fields needs to be validate in DB
			List<String> auditfields = ValidationFields.langTrnslDowDbAuditFields();
			// ***get the result from DB
			List<String> getResultDB2 = DbConnect.getResultSetFor(dowPutAuditQuery, auditfields, fileName, testCaseID);
			getResultDBFinal.addAll(getResultDB2);
		}

		int j = 0;
		for (int i = 0; i < translatedDOWs.size() / 2; i++) {

			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
			inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
			inputFieldValues[j + 2] = "0";
			j += 3;

		}

		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Audit DOW Table Validation Starts:");
		test.info("Audit DOW Table Validation Starts:");
		boolean testResult = false;
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
		// ***write result to excel
		String[] inputFieldNames = { "Input_dowNbr1: ", "Input_transDowName1: ", "Expected_RevisionTypeCd1: ",
				"Input_dowNbr2: ", "Input_transDowName2: ", "Expected_RevisionTypeCd2: ", "Input_dowNbr3: ",
				"Input_transDowName3: ", "Expected_RevisionTypeCd3: ", "Input_dowNbr4: ", "Input_transDowName4: ",
				"Expected_RevisionTypeCd4: ", "Input_dowNbr5: ", "Input_transDowName5: ", "Expected_RevisionTypeCd5: ",
				"Input_dowNbr6: ", "Input_transDowName6: ", "Expected_RevisionTypeCd6: ", "Input_dowNbr7: ",
				"Input_transDowName7:  ", "Expected_RevisionTypeCd7: ", };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_dowNbr1:", "DB_transDowName1:", "DB_RevisionTypeCd1: ", "DB_dowNbr2:",
				"DB_transDowName2:", "DB_RevisionTypeCd2: ", "DB_dowNbr3:", "DB_transDowName3:", "DB_RevisionTypeCd3: ",
				"DB_dowNbr4:", "DB_transDowName4:", "DB_RevisionTypeCd4: ", "DB_dowNbr5:", "DB_transDowName5:",
				"DB_RevisionTypeCd5: ", "DB_dowNbr6:", "DB_transDowName6:", "DB_RevisionTypeCd6: ", "DB_dowNbr7:",
				"DB_transDowName7:", "DB_RevisionTypeCd7: ", };

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
		if (testResult) {
			logger.info("Comparison between Audit DOW input data & DB data is  matching and passed");
			test.pass("Comparison between Audit DOW input data & DB data is matching and passed");
			return true;
		} else {
			logger.info("Comparison between Audit DOW input data & DB data is not matching and failed");
			test.fail("Comparison between Audit DOW input data & DB data is not matching and failed");
			return false;
		}

	}

	public boolean auditMOYDBValidation(String testCaseID, Response res) {
		List<String> getResultDBFinal = new ArrayList<String>();
		String[] inputFieldValues = new String[36];
		String revisionTypeCd = "0";
		for (int i = 0; i < translatedMOYs.size() / 2; i++) {

			String languagePostQuery = query.langTrnslMonthOfYearPostAuditQuery(languageCode,
					translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1)), revisionTypeCd);
			// ***get the fields needs to be validate in DB
			List<String> auditFields = ValidationFields.langTrnslMonthOfYearDbAuditFields();
			// ***get the result from DB
			List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery, auditFields, fileName, testCaseID);
			getResultDBFinal.addAll(getResultDB3);
		}

		int k = 0;
		for (int i = 0; i < translatedMOYs.size() / 2; i++) {

			inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
			inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
			inputFieldValues[k + 2] = "0";
			k += 3;
		}

		// ***get response fields values
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Audit MOY Table Validation Starts:");
		test.info("Audit MOY Table Validation Starts:");
		boolean testResult = false;
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
		// ***write result to excel
		String[] inputFieldNames = { "Input_mthOfYrNbr1: ", "Input_transMoyName1: ", "Expected_RevisionTypeCd1: ",
				"Input_mthOfYrNbr2: ", "Input_transMoyName2: ", "Expected_RevisionTypeCd2: ", "Input_mthOfYrNbr3: ",
				"Input_transMoyName3: ", "Expected_RevisionTypeCd3: ", "Input_mthOfYrNbr4: ", "Input_transMoyName4: ",
				"Expected_RevisionTypeCd4: ", "Input_mthOfYrNbr5: ", "Input_transMoyName5: ",
				"Expected_RevisionTypeCd5: ", "Input_mthOfYrNbr6: ", "Input_transMoyName6: ",
				"Expected_RevisionTypeCd6: ", "Input_mthOfYrNbr7: ", "Input_transMoyName7: ",
				"Expected_RevisionTypeCd7: ", "Input_mthOfYrNbr8: ", "Input_transMoyName8: ",
				"Expected_RevisionTypeCd8: ", "Input_mthOfYrNbr9: ", "Input_transMoyName9: ",
				"Expected_RevisionTypeCd9: ", "Input_mthOfYrNbr10: ", "Input_transMoyName10: ",
				"Expected_RevisionTypeCd10: ", "Input_mthOfYrNbr11: ", "Input_transMoyName11: ",
				"Expected_RevisionTypeCd11: ", "Input_mthOfYrNbr12: ", "Input_transMoyName12:",
				"Expected_RevisionTypeCd12: " };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "DB_mthOfYrNbr1: ", "DB_transMoyName1: ", "DB_RevisionTypeCd1: ", "DB_mthOfYrNbr2: ",
				"DB_transMoyName2: ", "DB_RevisionTypeCd2: ", "DB_mthOfYrNbr3: ", "DB_transMoyName3: ",
				"DB_RevisionTypeCd3: ", "DB_mthOfYrNbr4: ", "DB_transMoyName4: ", "DB_RevisionTypeCd4: ",
				"DB_mthOfYrNbr5: ", "DB_transMoyName5: ", "DB_RevisionTypeCd5: ", "DB_mthOfYrNbr6: ",
				"DB_transMoyName6: ", "DB_RevisionTypeCd6: ", "DB_mthOfYrNbr7: ", "DB_transMoyName7: ",
				"DB_RevisionTypeCd7: ", "DB_mthOfYrNbr8: ", "DB_transMoyName8: ", "DB_RevisionTypeCd8: ",
				"DB_mthOfYrNbr9: ", "DB_transMoyName9: ", "DB_RevisionTypeCd9: ", "DB_mthOfYrNbr10: ",
				"DB_transMoyName10: ", "DB_RevisionTypeCd10: ", "DB_mthOfYrNbr11: ", "DB_transMoyName11: ",
				"DB_RevisionTypeCd11: ", "DB_mthOfYrNbr12: ", "DB_transMoyName12:", "DB_RevisionTypeCd12: " };

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
		if (testResult) {
			logger.info("Comparison between Audit MOY input data & DB data is matching and passed");
			test.pass("Comparison between Audit MOY input data & DB data is matching and passed");
			return true;
		} else {
			logger.info("Comparison between Audit MOY input data & DB data is not matching and failed");
			test.fail("Comparison between Audit MOY input data & DB data is not matching and failed");
			return true;
		}

	}

	public void testDataFields(String scenarioName, String testCaseId) {
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		translatedDOWs = new HashMap<String, String>();
		translatedMOYs = new HashMap<String, String>();

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
		languageCode = inputData1.get(testCaseId).get("langCd");
		nativeScriptCode = inputData1.get(testCaseId).get("nativeScriptCode");
		languageName = inputData1.get(testCaseId).get("languageName");
		nativeScriptLanguageName = inputData1.get(testCaseId).get("nativeScriptLanguageNm");
		localeCd = inputData1.get(testCaseId).get("localeCd");
		countryCode = inputData1.get(testCaseId).get("countryCd");
		localeCd1 = inputData1.get(testCaseId).get("localeCd1");
		countryCode1 = inputData1.get(testCaseId).get("countryCd1");
		localesScriptCd = inputData1.get(testCaseId).get("localesScriptCd");
		cldrVersionNumber = inputData1.get(testCaseId).get("cldrVersionNumber");
		cldrVersionDate = inputData1.get(testCaseId).get("cldrVersionDate");

		dateFullFormatDescription = inputData1.get(testCaseId).get("dateFullFormatDescription");
		dateLongFormatDescription = inputData1.get(testCaseId).get("dateLongFormatDescription");
		dateMediumFormatDescription = inputData1.get(testCaseId).get("dateMediumFormatDescription");
		dateShortFormatDescription = inputData1.get(testCaseId).get("dateShortFormatDescription");
		localesEffectiveDate = inputData1.get(testCaseId).get("localesEffectiveDate");
		localesExpirationDate = inputData1.get(testCaseId).get("localesExpirationDate");
		localesScriptCd = inputData1.get(testCaseId).get("localesScriptCd");
		engLanguageName = inputData1.get(testCaseId).get("engLanguageName");
		for (int i = 1; i <= 7; i++) {

			translatedDOWs.put("translatedDOWs_dayOfWeekNumber" + i, inputData1.get(testCaseId).get("dowNbr" + i));
			translatedDOWs.put("translatedDOWs_translatedDayOfWeekName" + i,
					inputData1.get(testCaseId).get("transDowName" + i));

		}

		for (int i = 1; i <= 12; i++) {

			translatedMOYs.put("translatedMOYs_monthOfYearNumber" + i,
					inputData1.get(testCaseId).get("mthOfYrNbr" + i));
			translatedMOYs.put("translatedMOYs_translatedMonthOfYearName" + i,
					inputData1.get(testCaseId).get("transMoyName" + i));

		}
	}
}
