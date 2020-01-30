package scenarios.GEO;

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
import utils.DbConnect;
import utils.ExcelUtil;
import utils.Miscellaneous;
import utils.Queries;
import utils.Reporting;
import utils.ResponseMessages;
import utils.RetrieveEndPoints;
import utils.TestResultValidation;
import utils.ValidationFields;
import wsMethods.GetResponse;
import wsMethods.PostMethod;

public class StProvStdPut extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate;// ,
																													// token;
	Queries query = new Queries();
	// String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields = null, writableResult = null;
	Long geopoliticalIdJMS = null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(StProvStdPut.class);
	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");
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
				//String actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level+".command.version");
				//actuatorcommandversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,//actuatorCommandeVersionURL);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));

			// ** get DB data count before put request
			String geoStProvStdPutCountQuery = query.stProvStdAuditPutCountQuery(stProvCd);
			List<String> fieldsAuditcnt = ValidationFields.auditDBCntFields();
			List<String> getAuditBeforeCntResultDB = DbConnect.getResultSetFor(geoStProvStdPutCountQuery,
					fieldsAuditcnt, fileName, testCaseID);
			int beforePutCount = Integer.parseInt(getAuditBeforeCntResultDB.get(0));
			test.info("Before put request data count: " + beforePutCount);
			System.out.println("before put: " + beforePutCount);

			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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

			// ** get DB data coutn After put request
			List<String> getAuditAfetrCntResultDB = DbConnect.getResultSetFor(geoStProvStdPutCountQuery, fieldsAuditcnt,
					fileName, testCaseID);
			int afterPutCount = Integer.parseInt(getAuditAfetrCntResultDB.get(0));
			test.info("After put request data count: " + afterPutCount);
			System.out.println("After put: " + afterPutCount);

			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")
					&& (beforePutCount + 1 == afterPutCount)
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				// ***Converting dateFormat according to DB
				DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");

				String formatEffectiveDate;
				Date dateEffectiveDate = null;

				String formatExpirationDate;
				Date dateExpirationDate = null;

				if (effectiveDate.isEmpty()) {
					formatEffectiveDate = "";
				} else {
					formatEffectiveDate = effectiveDate;
				}

				try {
					formatExpirationDate = expirationDate;
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
				String geoStProvStdPutQuery = query.stProvStdPostQuery(stProvCd, formatEffectiveDate,
						formatExpirationDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name

				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPutQuery, fields, fileName,
						testCaseID);
				if (js.getString("data.stProvCd") != null) {
					String stProvCd1 = js.getString("data.stProvCd");
					// ***success message validation
					String expectMessage = resMsgs.stProvStdPutSuccessMsg + stProvCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with stProvCd is getting received as expected in response");
						test.pass("Success message with stProvCd is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate,
								userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.stProvStdResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_orgStdCd: ", "Input_stProvCd: ", "Input_stProvNm: ",
								"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:" };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ", "DB_effectiveDate",
								"DB_expirationDate", "DB_LastUpdateUserName:" };
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

							// ***Audit table validation starts
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

							String geoStProvStdPostQueryAudit = query.stProvStdAuditQuery(stProvCd,
									formatAuditEffectiveDate, formatAuditExpirationDate, stProvNm);
							// ***get the fields needs to be validate in DB
							List<String> fieldsAudit = ValidationFields.stProvStdPutAuditDbFields();
							// ***get the result from DB
							List<String> getResultDBAudit = DbConnect.getResultSetFor(geoStProvStdPostQueryAudit,
									fieldsAudit, fileName, testCaseID);
							if (getResultDBAudit.get(6).equals("0")) {
								getResultDBAudit.set(6, "1");
							}
							String[] inputFieldValuesAudit = { orgStdCd, stProvCd, stProvNm, effectiveDate,
									expirationDate, userId, "1" };
							testResult = false;
							testResult = TestResultValidation.testValidationWithDB(res, inputFieldValuesAudit,
									getResultDBAudit, resFields);
							if (testResult) {
								String[] inputFieldNamesAudit = { "Input_orgStdCd: ", "Input_stProvCd: ",
										"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate",
										"Input_LastUpdateUserName:", "Expected RevisionType CD:" };
								writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValuesAudit,
										inputFieldNamesAudit);
								String[] dbFieldNamesAudit = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ",
										"DB_effectiveDate", "DB_expirationDate", "DB_LastUpdateUserName:",
										"DB_RevisionType CD:" };
								writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBAudit, dbFieldNamesAudit);
								test.info("***Audit Table Validation Starts***");
								test.info("Input Data Values:");
								test.info(writableInputFields.replaceAll("\n", "<br />"));
								test.info("DB Audit Table Data Values:");
								test.info(writableDB_Fields.replaceAll("\n", "<br />"));
								logger.info("Comparison between input data & DB Audit table data matching and passed");
								logger.info(
										"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
								logger.info("------------------------------------------------------------------");
								test.pass("Comparison between input data & DB Audit table data matching and passed");
								ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType,
										reqFormatted, writableInputFields, writableDB_Fields, "", "", "", "Pass",
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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("stProvCd is not available in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");

			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");

			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("stProvCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				// ***Converting dateFormat according to DB
				// ***Converting dateFormat according to DB
				DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");

				String formatEffectiveDate;
				Date dateEffectiveDate = null;

				String formatExpirationDate;
				Date dateExpirationDate = null;

				if (effectiveDate.isEmpty()) {
					formatEffectiveDate = "";
				} else {
					formatEffectiveDate = effectiveDate;
				}

				try {
					formatExpirationDate = expirationDate;
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
				String geoStProvStdPutQuery = query.stProvStdPostQuery(stProvCd, formatEffectiveDate,
						formatExpirationDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name

				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPutQuery, fields, fileName,
						testCaseID);
				if (js.getString("data.stProvCd") != null) {
					String stProvCd1 = js.getString("data.stProvCd");
					// ***success message validation
					String expectMessage = resMsgs.stProvStdPutSuccessMsg + stProvCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with stProvCd is getting received as expected in response");
						test.pass("Success message with stProvCd is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate,
								userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.stProvStdResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_orgStdCd: ", "Input_stProvCd: ", "Input_stProvNm: ",
								"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:" };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ", "DB_effectiveDate",
								"DB_expirationDate", "DB_LastUpdateUserName:" };
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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("stProvCd is not available in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (errorMsg1.get(0).equals("effectiveDate") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "effectiveDate" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "effectiveDate" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***Converting dateFormat according to DB

				String formatEffectiveDate;
				Date dateEffectiveDate = null;

				String formatExpirationDate;
				Date dateExpirationDate = null;

				if (effectiveDate.isEmpty()) {
					formatEffectiveDate = "";
				} else {
					formatEffectiveDate = effectiveDate;
				}
				if (expirationDate.isEmpty()) {
					formatExpirationDate = "9999-12-31";
				} else {
					formatExpirationDate = expirationDate;
				}

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
				String geoStProvStdPutQuery = query.stProvStdPostQuery(stProvCd, formatEffectiveDate,
						formatExpirationDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name

				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPutQuery, fields, fileName,
						testCaseID);
				if (js.getString("data.stProvCd") != null) {
					String stProvCd1 = js.getString("data.stProvCd");
					// ***success message validation
					String expectMessage = resMsgs.stProvStdPutSuccessMsg + stProvCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with stProvCd is getting received as expected in response");
						test.pass("Success message with stProvCd is getting received as expected in response");
						// ***send the input, response, DB result for validation
						if (expirationDate.isEmpty()) {
							expirationDate = "9999-12-31";
						}
						String[] inputFieldValues = { orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate,
								userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.stProvStdResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_orgStdCd: ", "Input_stProvCd: ", "Input_stProvNm: ",
								"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:" };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ", "DB_effectiveDate",
								"DB_expirationDate", "DB_LastUpdateUserName:" };
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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("stProvCd is not available in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.inValidFieldMsg;
			if (Wscode == 404 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.invalidDateMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			// ***send the data to create request and get request
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.invalidDateMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequestWithoutMeta(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequestWithoutOrgStdCd(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequestWithoutstProvCd(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("stProvCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequestWithoutStProvNm(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				// ***Converting dateFormat according to DB

				DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");

				String formatEffectiveDate;
				Date dateEffectiveDate = null;

				String formatExpirationDate;
				Date dateExpirationDate = null;

				if (effectiveDate.isEmpty()) {
					formatEffectiveDate = "";
				} else {
					formatEffectiveDate = effectiveDate;
				}

				try {
					formatExpirationDate = expirationDate;
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
				String geoStProvStdPutQuery = query.stProvStdPostQuery(stProvCd, formatEffectiveDate,
						formatExpirationDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name

				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPutQuery, fields, fileName,
						testCaseID);
				if (js.getString("data.stProvCd") != null) {
					String stProvCd1 = js.getString("data.stProvCd");
					// ***success message validation
					String expectMessage = resMsgs.stProvStdPutSuccessMsg + stProvCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with stProvCd is getting received as expected in response");
						test.pass("Success message with stProvCd is getting received as expected in response");
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = { orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate,
								userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.stProvStdResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_orgStdCd: ", "Input_stProvCd: ", "Input_stProvNm: ",
								"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:" };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ", "DB_effectiveDate",
								"DB_expirationDate", "DB_LastUpdateUserName:" };
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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("stProvCd is not available in response");
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
			String payload = PostMethod.stProvStdPostRequestWithoutEffectiveDt(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (errorMsg1.get(0).equals("effectiveDate") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "effectiveDate" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "effectiveDate" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequestWithoutExpirationDt(userId, orgStdCd, stProvCd, stProvNm,
					effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				// ***Converting dateFormat according to DB

				String formatEffectiveDate;
				Date dateEffectiveDate = null;

				String formatExpirationDate;
				Date dateExpirationDate = null;

				if (effectiveDate.isEmpty()) {
					formatEffectiveDate = "";
				} else {
					formatEffectiveDate = effectiveDate;
				}
				if (expirationDate.isEmpty()) {
					formatExpirationDate = "9999-12-31";
				} else {
					formatExpirationDate = expirationDate;
				}

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
				String geoStProvStdPutQuery = query.stProvStdPostQuery(stProvCd, formatEffectiveDate,
						formatExpirationDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name

				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPutQuery, fields, fileName,
						testCaseID);
				if (js.getString("data.stProvCd") != null) {
					String stProvCd1 = js.getString("data.stProvCd");
					// ***success message validation
					String expectMessage = resMsgs.stProvStdPutSuccessMsg + stProvCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success message with stProvCd is getting received as expected in response");
						test.pass("Success message with stProvCd is getting received as expected in response");
						// ***send the input, response, DB result for validation
						if (expirationDate.isEmpty()) {
							expirationDate = "9999-12-31";
						}
						String[] inputFieldValues = { orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate,
								userId };
						// ***get response fields values
						List<String> resFields = ValidationFields.stProvStdResponseFileds(res);
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_orgStdCd: ", "Input_stProvCd: ", "Input_stProvNm: ",
								"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:" };
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						String[] dbFieldNames = { "DB_orgStdCd: ", "DB_stProvCd: ", "DB_stProvNm: ", "DB_effectiveDate",
								"DB_expirationDate", "DB_LastUpdateUserName:" };
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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("stProvCd is not available in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.requiredFieldMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.lengthExceeds10Char1;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.lengthExceeds10Char1;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("stProvCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "stProvCd" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.lengthExceeds120Char;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("stProvNm") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "stProvNm" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "stProvNm" + expectMessage);
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.invalidDateMsg;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.invalidDateMsg;

			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			
			String Wsstatus = res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage = resMsgs.lengthExceeds25Char;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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
			String payload = PostMethod.stProvStdPostRequest(userId, orgStdCd, stProvCd, stProvNm, effectiveDate,
					expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".stProvStd.put");
			getEndPoinUrl = getEndPoinUrl.substring(0, 95);
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
					String[] inputFieldValues = { userId, orgStdCd, stProvCd, stProvNm, effectiveDate, expirationDate };
					String[] inputFieldNames = { "Input_UserName: ", "Input_orgStdCd: ", "Input_stProvCd: ",
							"Input_stProvNm: ", "Input_effectiveDate", "Input_expirationDate" };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("Expected error message is getting received in response ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response");
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

	@Test(priority = 1)
	public void TC_24() {
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

			// ***send request and get response from JMS

			JSONObject getJMSResult = jmsReader.messageGetsPublished("STATE_PROV_STD");
			if (getJMSResult != null) {
				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Received:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				// String scrptCd=getJMSResult.getString("scrptCd");
				geopoliticalIdJMS = getJMSResult.getJSONObject("data").getLong("geopoliticalId");
				String orgStdCd = getJMSResult.getJSONObject("data").getString("orgStdCd");
				String stProvCd = getJMSResult.getJSONObject("data").getString("stProvCd");
				String stProvNm = getJMSResult.getJSONObject("data").getString("stProvNm");
				String effectiveDate = getJMSResult.getJSONObject("data").getString("effectiveDate");
				String expirationDate = getJMSResult.getJSONObject("data").getString("expirationDate");

				// Date Conversation code
				if (stProvCd != null) {
					// ***get the DB query
					// Converting date according DB Format
					DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
					DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");

					String formatEffectiveDate;
					Date dateEffectiveDate = null;

					String formatExpirationDate;
					Date dateExpirationDate = null;

					if (effectiveDate.isEmpty()) {
						formatEffectiveDate = "";
					} else {
						formatEffectiveDate = effectiveDate;
					}

					try {
						formatExpirationDate = expirationDate;
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

					// ** Date Convert from JMS format to DB Validation format
					// (to validate with DB values)

					// DateFormat srcDf1 = new SimpleDateFormat("E MMM dd
					// HH:mm:ss Z yyyy");
					DateFormat srcDf1 = new SimpleDateFormat("yyyy-MM-dd");
					DateFormat destDf1 = new SimpleDateFormat("yyyy-MM-dd");

					String formatEffectiveDateJMS;
					Date dateEffectiveDate1 = null;

					String formatExpirationDateJMS;
					Date dateExpirationDate1 = null;

					if (effectiveDate.isEmpty()) {
						formatEffectiveDateJMS = "";
					} else {
						formatEffectiveDateJMS = effectiveDate;
					}

					try {
						formatExpirationDateJMS = expirationDate;
						dateEffectiveDate1 = srcDf1.parse(formatEffectiveDateJMS);
						dateExpirationDate1 = srcDf1.parse(formatExpirationDateJMS);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					formatEffectiveDateJMS = destDf1.format(dateEffectiveDate1);
					formatEffectiveDateJMS = formatEffectiveDateJMS.toUpperCase();

					formatExpirationDateJMS = destDf1.format(dateExpirationDate);
					formatExpirationDateJMS = formatExpirationDateJMS.toUpperCase();

					System.out.println("formatEffectiveDateJMS: " + formatEffectiveDateJMS);
					System.out.println("formatExpirationDateJMS: " + formatExpirationDateJMS);

					String geoStProvStdPostQuery = query.stProvStdPostQueryJMS(stProvCd, formatEffectiveDate,
							formatExpirationDate);

					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.stProvStdGetMethodDbFieldsJMS();

					// ***get the result from DB

					List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdPostQuery, fields, fileName,
							testCaseID);
					String geopoliticalId = geopoliticalIdJMS.toString();
					String[] JMSValue = { geopoliticalId, orgStdCd, stProvCd, stProvNm, formatEffectiveDateJMS,
							formatExpirationDateJMS };
					testResult = TestResultValidation.testValidationForJMS(JMSValue, getResultDB);
					if (testResult) {
						// ***write result to excel
						String[] responseDbFieldValues = { geopoliticalId, getResultDB.get(0), orgStdCd,
								getResultDB.get(1), stProvCd, getResultDB.get(2), stProvNm, getResultDB.get(3),
								formatEffectiveDateJMS, getResultDB.get(4), formatExpirationDateJMS,
								getResultDB.get(5) };
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_orgStdCd: ", "DB_orgStdCd: ", "Response_stProvCd: ", "DB_stProvCd: ",
								"Response_stProvNm: ", "DB_stProvNm: ", "Response_EffectiveDate: ",
								"DB_EffectiveDate: ", "Response_ExpirationDate: ", "DB_ExpirationDate: " };

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
						String[] responseDbFieldValues = { geopoliticalId, getResultDB.get(0), orgStdCd,
								getResultDB.get(1), stProvCd, getResultDB.get(2), stProvNm, getResultDB.get(3),
								formatEffectiveDateJMS, getResultDB.get(4), formatExpirationDateJMS,
								getResultDB.get(5) };
						String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
								"Response_orgStdCd: ", "DB_orgStdCd: ", "Response_stProvCd: ", "DB_stProvCd: ",
								"Response_stProvNm: ", "DB_stProvNm: ", "Response_EffectiveDate: ",
								"DB_EffectiveDate: ", "Response_ExpirationDate: ", "DB_ExpirationDate: " };

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
					logger.error("stProvCd is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("UOM Type CD is not available in response");
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
		orgStdCd = inputData1.get(testCaseId).get("orgStdCd");
		stProvCd = inputData1.get(testCaseId).get("stProvCd");
		stProvNm = inputData1.get(testCaseId).get("stProvNm");
		effectiveDate = inputData1.get(testCaseId).get("effectiveDate");
		expirationDate = inputData1.get(testCaseId).get("expirationDate");
	}

}
