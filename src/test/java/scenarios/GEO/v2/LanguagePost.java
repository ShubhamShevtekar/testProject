package scenarios.GEO.v2;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

public class LanguagePost extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, langCd, englLanguageNm, nativeScriptLanguageNm;
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
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguagePost.class);
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
		String actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level+".command.version");
		actuatorcommandversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorCommandeVersionURL);
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Audit Query1
				String langPostAuditQuery1 = query.langPostAuditQuery(langCd);
				List<String> auditFields = ValidationFields.langDbAuditFields();
				List<String> getAuditResultDB1 = DbConnect.getResultSetFor(langPostAuditQuery1, auditFields, fileName,
						testCaseID);
				getAuditResultDBFinal.addAll(getAuditResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB2);
					// getAuditResultDBFinal.addAll(getResultDB2);

				}

				// Audit Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostAuditQuery2 = query.langTrnslDowPostAuditQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));

					// ***get the fields needs to be validate in DB
					List<String> auditfields2 = ValidationFields.langTrnslDowDbAuditFields();
					// ***get the result from DB
					List<String> getResultAuditDB2 = DbConnect.getResultSetFor(languagePostAuditQuery2, auditfields2,
							fileName, testCaseID);
					getAuditResultDBFinal.addAll(getResultAuditDB2);

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					String languageAuditPostQuery3 = query.langTrnslMonthOfYearPostAuditQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					List<String> auditFields3 = ValidationFields.langTrnslMonthOfYearDbAuditFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					List<String> getAuditResultDB3 = DbConnect.getResultSetFor(languageAuditPostQuery3, auditFields3,
							fileName, testCaseID);
					getResultDBFinal.addAll(getResultDB3);
					getAuditResultDBFinal.addAll(getAuditResultDB3);
				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							String revisionTypeCd = "0";
							String[] inputAuditFieldValues = new String[63];

							inputAuditFieldValues[0] = userId;
							inputAuditFieldValues[1] = langCd;
							inputAuditFieldValues[2] = englLanguageNm;
							inputAuditFieldValues[3] = nativeScriptLanguageNm;
							inputAuditFieldValues[4] = userId;
							inputAuditFieldValues[5] = revisionTypeCd;

							int l = 6;
							for (int i = 0; i < translatedDOWs.size() / 2; i++) {

								inputAuditFieldValues[l] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
								inputAuditFieldValues[l + 1] = translatedDOWs
										.get("translatedDOWs_transDowName" + (i + 1));
								inputAuditFieldValues[l + 2] = "0";
								l += 3;
							}

							int m = 27;
							for (int i = 0; i < translatedMOYs.size() / 2; i++) {

								inputAuditFieldValues[m] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
								inputAuditFieldValues[m + 1] = translatedMOYs
										.get("translatedMOYs_transMoyName" + (i + 1));
								inputAuditFieldValues[m + 2] = "0";
								m += 3;

							}
							testResult = false;
							testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
									getAuditResultDBFinal, resFields);

							String[] inputAuditFieldNames = { "Input_UserName: ", "Input_langCd: ",
									"Input_englLanguageNm: ", "Input_nativeScriptLanguageNm:",
									"Input_LastUpdateUserName: ", "Expected_RevisionTypeCd:", "Input_dowNbr1:",
									"Input_transDowName1:", "Expected_transDowName1RevisonCd:", "Input_dowNbr2:",
									"Input_transDowName2:", "Expected_transDowName2RevisonCd:", "Input_dowNbr3:",
									"Input_transDowName3:", "Expected_transDowName3RevisonCd:", "Input_dowNbr4:",
									"Input_transDowName4:", "Expected_transDowName4RevisonCd:", "Input_dowNbr5:",
									"Input_transDowName5:", "Expected_transDowName5RevisonCd:", "Input_dowNbr6:",
									"Input_transDowName6:", "Expected_transDowName6RevisonCd:", "Input_dowNbr7:",
									"Input_transDowName7:", "Expected_transDowName7RevisonCd:", "Input_mthOfYrNbr1:",
									"Input_transMoyName1:", "Expected_transMoyName1RevisonCd:", "Input_mthOfYrNbr2:",
									"Input_transMoyName2:", "Expected_transMoyName2RevisonCd:", "Input_mthOfYrNbr3:",
									"Input_transMoyName3:", "Expected_transMoyName3RevisonCd:", "Input_mthOfYrNbr4:",
									"Input_transMoyName4:", "Expected_transMoyName4RevisonCd:", "Input_mthOfYrNbr5:",
									"Input_transMoyName5:", "Expected_transMoyName5RevisonCd:", "Input_mthOfYrNbr6:",
									"Input_transMoyName6:", "Expected_transMoyName6RevisonCd:", "Input_mthOfYrNbr7:",
									"Input_transMoyName7:", "Expected_transMoyName7RevisonCd:", "Input_mthOfYrNbr8:",
									"Input_transMoyName8:", "Expected_transMoyName8RevisonCd:", "Input_mthOfYrNbr9:",
									"Input_transMoyName9:", "Expected_transMoyName9RevisonCd:", "Input_mthOfYrNbr10:",
									"Input_transMoyName10:", "Expected_transMoyName10RevisonCd:", "Input_mthOfYrNbr11:",
									"Input_transMoyName11:", "Expected_transMoyName11RevisonCd:", "Input_mthOfYrNbr12:",
									"Input_transMoyName12:", "Expected_transMoyName12RevisonCd:" };

							writableAuditInputFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
									inputAuditFieldNames);

							String[] auditDBFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
									"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_RevisionTypeCd:",
									"DB_dowNbr1:", "DB_transDowName1:", "DB_transDowName1RevisionTypeCd:",
									"DB_dowNbr2:", "DB_transDowName2:", "DB_transDowName2RevisionTypeCd:",
									"DB_dowNbr3:", "DB_transDowName3:", "DB_transDowName3RevisionTypeCd:",
									"DB_dowNbr4:", "DB_transDowName4:", "DB_transDowName4RevisionTypeCd:",
									"DB_dowNbr5:", "DB_transDowName5:", "DB_transDowName5RevisionTypeCd:",
									"DB_dowNbr6:", "DB_transDowName6:", "DB_transDowName6RevisionTypeCd:",
									"DB_dowNbr7:", "DB_transDowName7:", "DB_transDowName7RevisionTypeCd:",
									"DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_transMoyName1RevisonCd",
									"DB_mthOfYrNbr2:", "DB_transMoyName2:", "DB_transMoyName2RevisonCd",
									"DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_transMoyName3RevisonCd",
									"DB_mthOfYrNbr4:", "DB_transMoyName4:", "DB_transMoyName4RevisonCd",
									"DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_transMoyName5RevisonCd",
									"DB_mthOfYrNbr6:", "DB_transMoyName6:", "DB_transMoyName6RevisonCd",
									"DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_transMoyName7RevisonCd",
									"DB_mthOfYrNbr8:", "DB_transMoyName8:", "DB_transMoyName8RevisonCd",
									"DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_transMoyName9RevisonCd",
									"DB_mthOfYrNbr10:", "DB_transMoyName10:", "DB_transMoyName10RevisonCd",
									"DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_transMoyName11RevisonCd",
									"DB_mthOfYrNbr12:", "DB_transMoyName12:", "DB_transMoyName12RevisonCd" };

							writableAuditDB_Fields = Miscellaneous.geoDBFieldNames(getAuditResultDBFinal,
									auditDBFieldNames);
							if (testResult) {

								test.info("Input Audit Table Data Values:");
								test.info(writableAuditInputFields.replaceAll("\n", "<br />"));
								test.info("DB Audit Table Data Values:");
								test.info(writableAuditDB_Fields.replaceAll("\n", "<br />"));

								ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
										writableAuditInputFields, writableAuditDB_Fields, "", "", "", "Pass",
										"Audit Table validation");
								test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
							} else {
								logger.error("Comparison between input data & DB data not matching and failed");
								logger.error("------------------------------------------------------------------");
								ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
										writableInputFields, writableDB_Fields, "", "", "", "Fail",
										"Audit Table validation");
								test.log(Status.FAIL, MarkupHelper.createLabel("test status", ExtentColor.RED));
							}
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when sending the blank LangCode");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when sending the blank LangCode");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if ((errorMsg1.get(0).equals("dowNbr") || errorMsg1.get(1).equals("dowNbr"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("transDowName") || errorMsg1.get(1).equals("transDowName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if ((errorMsg1.get(0).equals("mthOfYrNbr") || errorMsg1.get(1).equals("mthOfYrNbr"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("transMoyName") || errorMsg1.get(1).equals("transMoyName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when sending the blank englLangNm");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank englLangNm");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB2);

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB3);

				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutDOW(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedMOYs);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					if (getResultDB2.isEmpty()) {
						getResultDBFinal.add("");
						getResultDBFinal.add("");
					} else {
						getResultDBFinal.addAll(getResultDB2);
					}

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB3);

				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequestWithoutMOY(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB2);

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					if (getResultDB3.isEmpty()) {
						getResultDBFinal.add("");
						getResultDBFinal.add("");
					} else {
						getResultDBFinal.addAll(getResultDB3);
					}
				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequestWithoutDOWAndMOY(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					if (getResultDB2.isEmpty()) {
						getResultDBFinal.add("");
						getResultDBFinal.add("");
					} else {
						getResultDBFinal.addAll(getResultDB2);
					}
				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					if (getResultDB3.isEmpty()) {
						getResultDBFinal.add("");
						getResultDBFinal.add("");
					} else {
						getResultDBFinal.addAll(getResultDB3);
					}

				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequestWithoutLangCd(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;
					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when languageCd is not passed in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when languageCd is not passed in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
			String payload = PostMethod.langPostRequestWithoutEnglLangNm(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when englLanguageNm is not passed in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when englLanguageNm is not passed in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutNatScrptLangNm(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
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
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode = res.statusCode();
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,
							translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB2);

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
							translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB3);

				}

				if (js.getString("data.langCd") != null) {
					String langCd1 = js.getString("data.langCd");
					// ***success message validation
					String expectMessage = resMsgs.langPostSuccessMsg + langCd1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + langCd);
						test.pass("Success response is getting received with Language Code: " + langCd);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = userId;
						inputFieldValues[1] = langCd;
						inputFieldValues[2] = englLanguageNm;
						inputFieldValues[3] = nativeScriptLanguageNm;
						inputFieldValues[4] = userId;

						int j = 5;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
							j += 2;

						}

						int k = 19;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageNm:", "Input_LastUpdateUserName: ", "Input_dowNbr1:",
								"Input_transDowName1:", "Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:",
								"Input_transDowName3:", "Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:",
								"Input_transDowName5:", "Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:",
								"Input_transDowName7:", "Input_mthOfYrNbr1:", "Input_transMoyName1:",
								"Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
								"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
								"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:",
								"Input_transMoyName6:", "Input_mthOfYrNbr7:", "Input_transMoyName7:",
								"Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
								"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
								"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
								"Input_transMoyName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_UserName: ", "DB_langCd: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageNm:", "DB_LastUpdateUserName: ", "DB_dowNbr1:",
								"DB_transDowName1:", "DB_dowNbr2:", "DB_transDowName2:", "DB_dowNbr3:",
								"DB_transDowName3:", "DB_dowNbr4:", "DB_transDowName4:", "DB_dowNbr5:",
								"DB_transDowName5:", "DB_dowNbr6:", "DB_transDowName6:", "DB_dowNbr7:",
								"DB_transDowName7:", "DB_mthOfYrNbr1:", "DB_transMoyName1:", "DB_mthOfYrNbr2:",
								"DB_transMoyName2:", "DB_mthOfYrNbr3:", "DB_transMoyName3:", "DB_mthOfYrNbr4:",
								"DB_transMoyName4:", "DB_mthOfYrNbr5:", "DB_transMoyName5:", "DB_mthOfYrNbr6:",
								"DB_transMoyName6:", "DB_mthOfYrNbr7:", "DB_transMoyName7:", "DB_mthOfYrNbr8:",
								"DB_transMoyName8:", "DB_mthOfYrNbr9:", "DB_transMoyName9:", "DB_mthOfYrNbr10:",
								"DB_transMoyName10:", "DB_mthOfYrNbr11:", "DB_transMoyName11:", "DB_mthOfYrNbr12:",
								"DB_transMoyName12:" };

						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));
						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");
						} else {
							logger.error("Comparison between input data & DB data not matching and failed");
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
					logger.error("langCd  is not available in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequestWithoutUserNm(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequestWithoutMeta(userId, langCd, englLanguageNm,
					nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
			getEndPoinUrl = getEndPoinUrl.substring(0, 94);
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
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if (internalMsg.equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info("Expected error message is getting received in response when the URI is not correct");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when the URI is not correct");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
							Wsstatus, "" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "langCd" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when englLanguageNm is more than 256 characters in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when englLanguageNm is more than 256 characters in JSON request");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "englLanguageNm" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when nativeScriptLanguageNm is more than 256 characters in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when nativeScriptLanguageNm is more than 256 characters in JSON request");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("dowNbr") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("transDowName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when DownNm is more than 256 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when DownNm is more than 256 characters JSON request");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("mthOfYrNbr") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when mthOfYrNbr is more than 38 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when mthOfYrNbr is more than 38 characters JSON request");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("transMoyName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when transMoy is more than 65 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when transMoy is more than 65 characters JSON request");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,
					translatedDOWs, translatedMOYs);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				// ***error message validation

				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = langCd;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageNm;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs.get("translatedDOWs_transDowName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs.get("translatedMOYs_transMoyName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_langCd: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageNm:", "Input_dowNbr1:", "Input_transDowName1:", "Input_dowNbr2:",
							"Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:", "Input_dowNbr4:",
							"Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:", "Input_dowNbr6:",
							"Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:", "Input_mthOfYrNbr1:",
							"Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:", "Input_mthOfYrNbr3:",
							"Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:", "Input_mthOfYrNbr5:",
							"Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:", "Input_mthOfYrNbr7:",
							"Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:", "Input_mthOfYrNbr9:",
							"Input_transMoyName9:", "Input_mthOfYrNbr10:", "Input_transMoyName10:",
							"Input_mthOfYrNbr11:", "Input_transMoyName11:", "Input_mthOfYrNbr12:",
							"Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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

	@Test(priority = 1)
	public void TC_26() {
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

			List<String> getResultDBFinal = new ArrayList<String>();
			// *** get JMS response
			JSONObject getJMSResult = jmsReader.messageGetsPublished("LANGUAGE");
			if (getJMSResult != null) {

				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Recieved:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));

				String langCd = getJMSResult.getJSONObject("data").getString("langCd");
				String englLanguageNm = getJMSResult.getJSONObject("data").getString("englLanguageNm");
				String nativeScriptLanguageNm1 = getJMSResult.getJSONObject("data").getString("nativeScriptLanguageNm");

				String[] jmsFieldValues = new String[41];

				jmsFieldValues[0] = langCd;
				jmsFieldValues[1] = englLanguageNm;
				jmsFieldValues[2] = nativeScriptLanguageNm1;

				// *** Reading translatedDOWs array
				JSONArray jsonArraytranslatedDOWs = getJMSResult.getJSONObject("data").getJSONArray("translatedDOWs");
				JSONObject jObj = null;
				int l = 3;
				for (int i = 0; i < jsonArraytranslatedDOWs.length(); i++) {
					jObj = jsonArraytranslatedDOWs.getJSONObject(i);
					jmsFieldValues[l] = jObj.optString("dowNbr");
					jmsFieldValues[l + 1] = jObj.optString("transDowName");
					l += 2;
				}

				// ***Reading translatedMOYs array
				JSONArray jsonArraytranslatedMOYs = getJMSResult.getJSONObject("data").getJSONArray("translatedMOYs");
				JSONObject jObj1 = null;
				int m = 17;
				for (int i = 0; i < jsonArraytranslatedMOYs.length(); i++) {
					jObj1 = jsonArraytranslatedMOYs.getJSONObject(i);
					jmsFieldValues[m] = jObj1.optString("mthOfYrNbr");
					jmsFieldValues[m + 1] = jObj1.optString("transMoyName");
					m += 2;
				}

				if (langCd != null) {
					// ***get the DB query
					String langPostPostQuery1 = query.langPostQuery(langCd);
					// ***get the fields needs to be validate in DB
					List<String> jmsFields1 = ValidationFields.langJMSDbFields();
					// ***get the result from DB
					List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, jmsFields1, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB1);

					// Query2 for DOW
					for (int i = 0; i < jsonArraytranslatedDOWs.length(); i++) {
						jObj = jsonArraytranslatedDOWs.getJSONObject(i);
						String languagePostQuery2 = query.langTrnslDowPostQuery(langCd, jObj.optString("dowNbr"));
						// ***get the fields needs to be validate in DB
						List<String> jmsFields2 = ValidationFields.langTrnslDowDbFields();
						// ***get the result from DB
						List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, jmsFields2, fileName,
								testCaseID);
						getResultDBFinal.addAll(getResultDB2);
					}

					// Query3
					for (int i = 0; i < jsonArraytranslatedMOYs.length(); i++) {
						jObj1 = jsonArraytranslatedMOYs.getJSONObject(i);
						String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,
								jObj1.optString("mthOfYrNbr"));
						// ***get the fields needs to be validate in DB
						List<String> jmsFlds3 = ValidationFields.langTrnslMonthOfYearDbFields();
						// ***get the result from DB
						List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, jmsFlds3, fileName,
								testCaseID);
						getResultDBFinal.addAll(getResultDB3);
					}

					testResult = TestResultValidation.testValidationForJMS(jmsFieldValues, getResultDBFinal);
					String[] jmsFieldNames = { "Response_langCd:  ", "Response_englLanguageNm:  ",
							"Response_nativeScriptLanguageNm: ", "Response_dowNbr1:  ", "Response_transDowName1:  ",
							"Response_dowNbr2:  ", "Response_transDowName2:  ", "Response_dowNbr3:  ",
							"Response_transDowName3:  ", "Response_dowNbr4:  ", "Response_transDowName4:  ",
							"Response_dowNbr5:  ", "Response_transDowName5:  ", "Response_dowNbr6:  ",
							"Response_transDowName6:  ", "Response_dowNbr7:  ", "Response_transDowName7:  ",
							"Response_mthOfYrNbr1:  ", "Response_transMoyName1:  ", "Response_mthOfYrNbr2:  ",
							"Response_transMoyName2:  ", "Response_mthOfYrNbr3:  ", "Response_transMoyName3:  ",
							"Response_mthOfYrNbr4:  ", "Response_transMoyName4:  ", "Response_mthOfYrNbr5:  ",
							"Response_transMoyName5:  ", "Response_mthOfYrNbr6:  ", "Response_transMoyName6:  ",
							"Response_mthOfYrNbr7:  ", "Response_transMoyName7:  ", "Response_mthOfYrNbr8:  ",
							"Response_transMoyName8:  ", "Response_mthOfYrNbr9:  ", "Response_transMoyName9:  ",
							"Response_mthOfYrNbr10:  ", "Response_transMoyName10:  ", "Response_mthOfYrNbr11:  ",
							"Response_transMoyName11:  ", "Response_mthOfYrNbr12:  ", "Response_transMoyName12:  " };

					String[] dbFieldNames = { "DB_langCd:   ", "DB_englLanguageNm:   ", "DB_nativeScriptLanguageNm:  ",
							"DB_dowNbr1:  ", "DB_transDowName1:  ", "DB_dowNbr2:  ", "DB_transDowName2:  ",
							"DB_dowNbr3:  ", "DB_transDowName3:  ", "DB_dowNbr4:  ", "DB_transDowName4:  ",
							"DB_dowNbr5:  ", "DB_transDowName5:  ", "DB_dowNbr6:  ", "DB_transDowName6:  ",
							"DB_dowNbr7:  ", "DB_transDowName7:  ", "DB_mthOfYrNbr1:  ", "DB_transMoyName1:  ",
							"DB_mthOfYrNbr2:  ", "DB_transMoyName2:  ", "DB_mthOfYrNbr3:  ", "DB_transMoyName3:  ",
							"DB_mthOfYrNbr4:  ", "DB_transMoyName4:  ", "DB_mthOfYrNbr5:  ", "DB_transMoyName5:  ",
							"DB_mthOfYrNbr6:  ", "DB_transMoyName6:  ", "DB_mthOfYrNbr7:  ", "DB_transMoyName7:  ",
							"DB_mthOfYrNbr8:  ", "DB_transMoyName8:  ", "DB_mthOfYrNbr9:  ", "DB_transMoyName9:  ",
							"DB_mthOfYrNbr10:  ", "DB_transMoyName10:  ", "DB_mthOfYrNbr11:  ", "DB_transMoyName11:  ",
							"DB_mthOfYrNbr12:  ", "DB_transMoyName12:  " };

					if (testResult) {
						writableJMSResult = Miscellaneous.geoFieldInputNames(jmsFieldValues, jmsFieldNames);
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("JMS Response Values:");
						test.info(writableJMSResult.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));

						logger.info("Comparison between JMS response & DB data matching are passed");
						logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass("Comparison between JMS response & DB data matching are passed");

						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", writableJMSResult,
								writableDB_Fields, "", "", "", "Pass", "");
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					} else {
						writableJMSResult = Miscellaneous.geoFieldInputNames(jmsFieldValues, jmsFieldNames);
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("JMS Response Values:");
						test.info(writableJMSResult.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));

						logger.info("Comparison between JMS response & DB data matching are Failed");
						logger.info("Execution is completed for Fail Test Case No. " + testCaseID);
						logger.info("------------------------------------------------------------------");
						test.fail("Comparison between JMS response & DB data matching are failed");

						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", writableJMSResult,
								writableDB_Fields, "", "", "", "Fail", "");
						test.log(Status.FAIL, MarkupHelper.createLabel("test status", ExtentColor.RED));
						Assert.fail("Test Failed");
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

	// ***get the values from test data sheet
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
		langCd = inputData1.get(testCaseId).get("langCd");
		englLanguageNm = inputData1.get(testCaseId).get("englLanguageNm");
		nativeScriptLanguageNm = inputData1.get(testCaseId).get("nativeScriptLanguageNm");

		for (int i = 1; i <= 7; i++) {

			translatedDOWs.put("translatedDOWs_dowNbr" + i, inputData1.get(testCaseId).get("dowNbr" + i));
			translatedDOWs.put("translatedDOWs_transDowName" + i, inputData1.get(testCaseId).get("transDowName" + i));

		}

		for (int i = 1; i <= 12; i++) {

			translatedMOYs.put("translatedMOYs_mthOfYrNbr" + i, inputData1.get(testCaseId).get("mthOfYrNbr" + i));
			translatedMOYs.put("translatedMOYs_transMoyName" + i, inputData1.get(testCaseId).get("transMoyName" + i));

		}
	}
}
