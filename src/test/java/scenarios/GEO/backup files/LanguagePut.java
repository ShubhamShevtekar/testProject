/*package scenarios.GEO.v1;

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

public class LanguagePut extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, languageCode, englLanguageNm, nativeScriptLanguageName;
	JMSReader jmsReader = new JMSReader();
	HashMap<String, String> translatedDOWs;
	HashMap<String, String> translatedMOYs;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableAuditInputFields, writableDB_Fields = null, writableAuditDB_Fields = null,
			writableJMSResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguagePut.class);
	String actuatorcommandversion = "1.0.0";
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {
		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
			// *** getting actautor version
			String tokenKey = tokenValues[0];
			String tokenVal = token;
			// String
			// actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator",
			// fileName, level+".command.version");
			// actuatorcommandversion
			// =resultValidation.versionValidation(fileName, tokenKey,
			// tokenVal,actuatorCommandeVersionURL);
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

			// Count
			String langPutAuditCountQuery1 = query.langPutAuditCountQuery1(languageCode);
			List<String> countFields1 = ValidationFields.auditDBCntFields();
			List<String> getCountResultDB1 = DbConnect.getResultSetFor(langPutAuditCountQuery1, countFields1, fileName,
					testCaseID);
			String size1 = getCountResultDB1.get(0);
			int count1 = Integer.parseInt(size1);
			test.info("In Language Table Count Before Put: " + count1);

			String langPutAuditCountQuery2 = query.langPutAuditCountQuery2(languageCode);
			List<String> countFields2 = ValidationFields.auditDBCntFields();
			// ***get the result from DB
			List<String> getCountResultDB2 = DbConnect.getResultSetFor(langPutAuditCountQuery2, countFields2, fileName,
					testCaseID);
			String size2 = (getCountResultDB2.get(0));
			int count2 = Integer.parseInt(size2);
			count2 = count2 / 7;
			test.info("In Translated DoW Table Count Before Put: " + count2);

			String langPutAuditCountQuery3 = query.langPutAuditCountQuery3(languageCode);
			List<String> countFields3 = ValidationFields.auditDBCntFields();
			// ***get the result from DB
			List<String> getCountResultDB3 = DbConnect.getResultSetFor(langPutAuditCountQuery3, countFields3, fileName,
					testCaseID);
			String size3 = (getCountResultDB3.get(0));
			int count3 = Integer.parseInt(size3);
			count3 = count3 / 12;
			test.info("In Translated MoY Table Count Before Put: " + count3);
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);

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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Audit Query1
				String langPostAuditQuery1 = query.langPostAuditQuery(languageCode);
				List<String> auditFields = ValidationFields.langDbAuditFields();
				auditFields.remove(0);
				List<String> getAuditResultDB1 = DbConnect.getResultSetFor(langPostAuditQuery1, auditFields, fileName,
						testCaseID);
				getAuditResultDBFinal.addAll(getAuditResultDB1);


				 * // Query2 for (int i = 0; i < translatedDOWs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery2 =
				 * query.langTrnslDowPostQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1))); System.out.println("******************* "
				 * +languagePostQuery2); // ***get the fields needs to be
				 * validate in DB List<String> fields2 =
				 * ValidationFields.langTrnslDowDbFields(); // ***get the result
				 * from DB List<String> getResultDB2 =
				 * DbConnect.getResultSetFor(languagePostQuery2, fields2,
				 * fileName, testCaseID); getResultDBFinal.addAll(getResultDB2);
				 *
				 * }
				 *
				 * // Audit Query2 for (int i = 0; i < translatedDOWs.size() /
				 * 2; i++) {
				 *
				 * String languagePostAuditQuery2 =
				 * query.langTrnslDowPostAuditQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1)));
				 *
				 *
				 * System.out.println(
				 * "languagePostAuditQuery2 *&&&&&&&&&&&&&& + "
				 * +languagePostAuditQuery2); // ***get the fields needs to be
				 * validate in DB List<String> auditfields2 =
				 * ValidationFields.langTrnslDowDbAuditFields(); // ***get the
				 * result from DB List<String> getResultAuditDB2 =
				 * DbConnect.getResultSetFor(languagePostAuditQuery2,
				 * auditfields2, fileName, testCaseID);
				 *
				 * if(getResultAuditDB2.get(2).equals("0")){
				 * getResultAuditDB2.set(2, "1"); }
				 *
				 *
				 * getAuditResultDBFinal.addAll(getResultAuditDB2);
				 *
				 * } // if(getResultAuditDB2.get(3).equals("0")){ //
				 * getResultAuditDB2.set(3, "1"); // }
				 *
				 * System.out.println("after DOW "+getAuditResultDBFinal);
				 *
				 * // Query3 MOY for (int i = 0; i < translatedMOYs.size() / 2;
				 * i++) {
				 *
				 * String languagePostQuery3 =
				 * query.langTrnslMonthOfYearPostQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * System.out.println("languagePostQuery3 ************ "
				 * +languagePostQuery3); List<String> fields3 =
				 * ValidationFields.langTrnslMonthOfYearDbFields(); // ***get
				 * the result from DB List<String> getResultDB3 =
				 * DbConnect.getResultSetFor(languagePostQuery3, fields3,
				 * fileName, testCaseID);
				 *
				 *
				 * getResultDBFinal.addAll(getResultDB3);
				 *
				 * }
				 *
				 * // Audit MOY Query3 for (int i = 0; i < translatedMOYs.size()
				 * / 2; i++) { String languageAuditPostQuery3 =
				 * query.langTrnslMonthOfYearPostAuditQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1)));
				 *
				 *
				 * System.out.println("languageAuditPostQuery3 **** %% "
				 * +languageAuditPostQuery3); // ***get the fields needs to be
				 * validate in DB List<String> auditFields3 =
				 * ValidationFields.langTrnslMonthOfYearDbAuditFields(); //
				 * ***get the result from DB
				 *
				 * List<String> getAuditResultDB3 =
				 * DbConnect.getResultSetFor(languageAuditPostQuery3,
				 * auditFields3, fileName, testCaseID);
				 *
				 * System.out.println("aduit mothksakhsdhn *** "
				 * +getAuditResultDB3);
				 * if(getAuditResultDB3.get(2).equals("0")){
				 * getAuditResultDB3.set(2, "1"); } System.out.println(
				 * "aduit mothksakhsdhn *** "+getAuditResultDB3);
				 *
				 * getAuditResultDBFinal.addAll(getAuditResultDB3); }
				 *
				 *
				 * System.out.println("aftwer moth od ye** "
				 * +getAuditResultDBFinal);


				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[4];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;


						 * int j = 4; for (int i = 0; i < translatedDOWs.size()
						 * / 2; i++) {
						 *
						 *
						 * inputFieldValues[j] =
						 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" +
						 * (i + 1)); inputFieldValues[j + 1] =
						 * translatedDOWs.get(
						 * "translatedDOWs_translatedDayOfWeekName" + (i + 1));
						 * j += 2;
						 *
						 * }
						 *
						 * System.out.println(" inputFieldValues "
						 * +inputFieldValues); int k = 18; for (int i = 0; i <
						 * translatedMOYs.size() / 2; i++) {
						 *
						 * inputFieldValues[k] =
						 * translatedMOYs.get("translatedMOYs_monthOfYearNumber"
						 * + (i + 1)); inputFieldValues[k + 1] =
						 * translatedMOYs.get(
						 * "translatedMOYs_translatedMonthOfYearName" + (i +
						 * 1)); k += 2; }


						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: " };

						// System.out.println("getResultDBFinal
						// ----------------- "+getResultDBFinal);
						//
						//
						// for (String a : dbFieldNames) {
						// System.out.println("dbFieldNames-----------------
						// "+a);
						// }
						//
						// System.out.println("getResultDBFinal
						// "+getResultDBFinal);
						// System.out.println("dbFieldNames "+dbFieldNames);
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
						test.info("Input Data Values:");
						test.info(writableInputFields.replaceAll("\n", "<br />"));
						test.info("DB Data Values:");
						test.info(writableDB_Fields.replaceAll("\n", "<br />"));

						// After Count
						String langPutAuditAfterCountQuery1 = query.langPutAuditCountQuery1(languageCode);
						List<String> countAfterFields1 = ValidationFields.auditDBCntFields();
						List<String> getAfterCountResultDB1 = DbConnect.getResultSetFor(langPutAuditAfterCountQuery1,
								countAfterFields1, fileName, testCaseID);
						// getCountResultDBFinal.addAll(getCountResultDB1);
						String afterSize1 = getAfterCountResultDB1.get(0);
						int afterCount1 = Integer.parseInt(afterSize1);
						test.info("In Language Table Count After Put: " + afterCount1);

						String langPutAuditAfterCountQuery2 = query.langPutAuditCountQuery2(languageCode);

						List<String> countAfterFields2 = ValidationFields.auditDBCntFields();
						// ***get the result from DB
						List<String> getAfterCountResultDB2 = DbConnect.getResultSetFor(langPutAuditAfterCountQuery2,
								countAfterFields2, fileName, testCaseID);
						// getCountResultDBFinal.addAll(getCountResultDB2);
						String afterSize2 = (getAfterCountResultDB2.get(0));
						int afterCount2 = Integer.parseInt(afterSize2);
						afterCount2 = afterCount2 / 7;
						test.info("In Translated DoW Table Count After Put: " + afterCount2);

						String langPutAuditAfterCountQuery3 = query.langPutAuditCountQuery3(languageCode);

						List<String> countAfterFields3 = ValidationFields.auditDBCntFields();
						// ***get the result from DB
						List<String> getAfterCountResultDB3 = DbConnect.getResultSetFor(langPutAuditAfterCountQuery3,
								countAfterFields3, fileName, testCaseID);
						// getCountResultDBFinal.addAll(getCountResultDB3);
						String afterSize3 = (getAfterCountResultDB3.get(0));
						int afterCount3 = Integer.parseInt(afterSize3);
						afterCount3 = afterCount3 / 12;
						test.info("In Translated MoY Table Count After Put: " + afterCount3);

						if (testResult) {
							logger.info("Comparison between input data & DB data matching and passed");
							logger.info("------------------------------------------------------------------");
							test.pass("Comparison between input data & DB data matching and passed");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
									writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Pass",
									"");

							String revisionTypeCd = "1";
							String[] inputAuditFieldValues = new String[5];

							inputAuditFieldValues[0] = languageCode;
							inputAuditFieldValues[1] = englLanguageNm;
							inputAuditFieldValues[2] = nativeScriptLanguageName;
							inputAuditFieldValues[3] = userId;
							inputAuditFieldValues[4] = revisionTypeCd;


							 * int l = 5; for (int i = 0; i <
							 * translatedDOWs.size() / 2; i++) {
							 *
							 * inputAuditFieldValues[l] = translatedDOWs.get(
							 * "translatedDOWs_dayOfWeekNumber" + (i + 1));
							 * inputAuditFieldValues[l + 1] = translatedDOWs
							 * .get("translatedDOWs_translatedDayOfWeekName" +
							 * (i + 1)); inputAuditFieldValues[l + 2] = "1"; l
							 * += 3;
							 *
							 *
							 * }
							 *
							 * int m = 26; for (int i = 0; i <
							 * translatedMOYs.size() / 2; i++) {
							 *
							 * inputAuditFieldValues[m] = translatedMOYs.get(
							 * "translatedMOYs_monthOfYearNumber" + (i + 1));
							 * inputAuditFieldValues[m + 1] = translatedMOYs
							 * .get("translatedMOYs_translatedMonthOfYearName" +
							 * (i + 1)); inputAuditFieldValues[m + 2] = "1"; m
							 * += 3; } testResult = false;
							 *
							 *
							 * //for (String a : inputAuditFieldValues) { // //
							 * System.out.println(
							 * "***************** AAAAAAAAAAAAAAAAAAA "+a); //
							 * //} // // System.out.println(
							 * "*****getAuditResultDBFinal*****"+
							 * getAuditResultDBFinal);


							testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
									getAuditResultDBFinal, resFields);

							String[] inputAuditFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
									"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: ",
									"Expected_RevisionTypeCd:" };

							writableAuditInputFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
									inputAuditFieldNames);

							String[] auditDBFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
									"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: ", "DB_RevisionTypeCd:" };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if ((errorMsg1.get(0).equals("dayOfWeekNumber") || errorMsg1.get(1).equals("dayOfWeekNumber"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("translatedDayOfWeekName")
								|| errorMsg1.get(1).equals("translatedDayOfWeekName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when sending the blank dayOfWeekNumber");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the blank dayOfWeekNumber");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "dayOfWeekNumber" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "dayOfWeekNumber" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if ((errorMsg1.get(0).equals("monthOfYearNumber") || errorMsg1.get(1).equals("monthOfYearNumber"))
						&& errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("translatedMonthOfYearName")
								|| errorMsg1.get(1).equals("translatedMonthOfYearName"))
						&& errorMsg2.get(1).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
							Wsstatus, "" + Wscode, responsestr, "Fail", "monthOfYearNumber" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "monthOfYearNumber" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2

				 * for (int i = 0; i < translatedDOWs.size() / 2; i++) {
				 *
				 * String languagePostQuery2 =
				 * query.langTrnslDowPostQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields2 =
				 * ValidationFields.langTrnslDowDbFields(); // ***get the result
				 * from DB List<String> getResultDB2 =
				 * DbConnect.getResultSetFor(languagePostQuery2, fields2,
				 * fileName, testCaseID); getResultDBFinal.addAll(getResultDB2);
				 *
				 * }
				 *
				 * // Query3 for (int i = 0; i < translatedMOYs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery3 =
				 * query.langTrnslMonthOfYearPostQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields3 =
				 * ValidationFields.langTrnslMonthOfYearDbFields(); // ***get
				 * the result from DB List<String> getResultDB3 =
				 * DbConnect.getResultSetFor(languagePostQuery3, fields3,
				 * fileName, testCaseID); getResultDBFinal.addAll(getResultDB3);
				 *
				 * }


				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[4];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;


						 * int j = 4; for (int i = 0; i < translatedDOWs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[j] =
						 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" +
						 * (i + 1)); inputFieldValues[j + 1] =
						 * translatedDOWs.get(
						 * "translatedDOWs_translatedDayOfWeekName" + (i + 1));
						 * j += 2;
						 *
						 * }
						 *
						 * int k = 18; for (int i = 0; i < translatedMOYs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[k] =
						 * translatedMOYs.get("translatedMOYs_monthOfYearNumber"
						 * + (i + 1)); inputFieldValues[k + 1] =
						 * translatedMOYs.get(
						 * "translatedMOYs_translatedMonthOfYearName" + (i +
						 * 1)); k += 2; }

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: " };

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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutDOW(userId, languageCode, nativeScriptLanguageName,
					englLanguageNm, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);


				 * // Query2 for (int i = 0; i < translatedDOWs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery2 =
				 * query.langTrnslDowPostQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields2 =
				 * ValidationFields.langTrnslDowDbFields(); // ***get the result
				 * from DB List<String> getResultDB2 =
				 * DbConnect.getResultSetFor(languagePostQuery2, fields2,
				 * fileName, testCaseID); if (getResultDB2.isEmpty()) {
				 * getResultDBFinal.add(""); getResultDBFinal.add(""); } else {
				 * getResultDBFinal.addAll(getResultDB2); }
				 *
				 * }
				 *
				 * // Query3 for (int i = 0; i < translatedMOYs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery3 =
				 * query.langTrnslMonthOfYearPostQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields3 =
				 * ValidationFields.langTrnslMonthOfYearDbFields(); // ***get
				 * the result from DB List<String> getResultDB3 =
				 * DbConnect.getResultSetFor(languagePostQuery3, fields3,
				 * fileName, testCaseID); getResultDBFinal.addAll(getResultDB3);
				 *
				 * }


				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[4];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;


						 * int j = 4; for (int i = 0; i < translatedDOWs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[j] =
						 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" +
						 * (i + 1)); inputFieldValues[j + 1] =
						 * translatedDOWs.get(
						 * "translatedDOWs_translatedDayOfWeekName" + (i + 1));
						 * j += 2;
						 *
						 * }
						 *
						 * int k = 18; for (int i = 0; i < translatedMOYs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[k] =
						 * translatedMOYs.get("translatedMOYs_monthOfYearNumber"
						 * + (i + 1)); inputFieldValues[k + 1] =
						 * translatedMOYs.get(
						 * "translatedMOYs_translatedMonthOfYearName" + (i +
						 * 1)); k += 2; }

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						// for(int i=0;i<=inputFieldValues.length;i++){
						//
						// System.out.println("inputFieldValues
						// "+inputFieldValues[i]);
						// }

						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequestWithoutMOY(userId, languageCode, nativeScriptLanguageName,
					englLanguageNm, translatedDOWs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);


				 * // Query2 for (int i = 0; i < translatedDOWs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery2 =
				 * query.langTrnslDowPostQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields2 =
				 * ValidationFields.langTrnslDowDbFields(); // ***get the result
				 * from DB List<String> getResultDB2 =
				 * DbConnect.getResultSetFor(languagePostQuery2, fields2,
				 * fileName, testCaseID); getResultDBFinal.addAll(getResultDB2);
				 *
				 * }
				 *
				 * // Query3 for (int i = 0; i < translatedMOYs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery3 =
				 * query.langTrnslMonthOfYearPostQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields3 =
				 * ValidationFields.langTrnslMonthOfYearDbFields(); // ***get
				 * the result from DB List<String> getResultDB3 =
				 * DbConnect.getResultSetFor(languagePostQuery3, fields3,
				 * fileName, testCaseID); if (getResultDB3.isEmpty()) {
				 * getResultDBFinal.add(""); getResultDBFinal.add(""); } else {
				 * getResultDBFinal.addAll(getResultDB3); }
				 *
				 * }


				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[43];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;


						 * int j = 4; for (int i = 0; i < translatedDOWs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[j] =
						 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" +
						 * (i + 1)); inputFieldValues[j + 1] =
						 * translatedDOWs.get(
						 * "translatedDOWs_translatedDayOfWeekName" + (i + 1));
						 * j += 2;
						 *
						 * }
						 *
						 * int k = 18; for (int i = 0; i < translatedMOYs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[k] =
						 * translatedMOYs.get("translatedMOYs_monthOfYearNumber"
						 * + (i + 1)); inputFieldValues[k + 1] =
						 * translatedMOYs.get(
						 * "translatedMOYs_translatedMonthOfYearName" + (i +
						 * 1)); k += 2; }

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequestWithoutDOWAndMOY(userId, languageCode, nativeScriptLanguageName,
					englLanguageNm);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2

				 * for (int i = 0; i < translatedDOWs.size() / 2; i++) {
				 *
				 * String languagePostQuery2 =
				 * query.langTrnslDowPostQuery(languageCode,
				 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields2 =
				 * ValidationFields.langTrnslDowDbFields(); // ***get the result
				 * from DB List<String> getResultDB2 =
				 * DbConnect.getResultSetFor(languagePostQuery2, fields2,
				 * fileName, testCaseID); if (getResultDB2.isEmpty()) {
				 * getResultDBFinal.add(""); getResultDBFinal.add(""); } else {
				 * getResultDBFinal.addAll(getResultDB2); } }
				 *
				 * // Query3 for (int i = 0; i < translatedMOYs.size() / 2; i++)
				 * {
				 *
				 * String languagePostQuery3 =
				 * query.langTrnslMonthOfYearPostQuery(languageCode,
				 * translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i +
				 * 1))); // ***get the fields needs to be validate in DB
				 * List<String> fields3 =
				 * ValidationFields.langTrnslMonthOfYearDbFields(); // ***get
				 * the result from DB List<String> getResultDB3 =
				 * DbConnect.getResultSetFor(languagePostQuery3, fields3,
				 * fileName, testCaseID); if (getResultDB3.isEmpty()) {
				 * getResultDBFinal.add(""); getResultDBFinal.add(""); } else {
				 * getResultDBFinal.addAll(getResultDB3); }
				 *
				 * }


				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[4];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;


						 * int j = 4; for (int i = 0; i < translatedDOWs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[j] =
						 * translatedDOWs.get("translatedDOWs_dayOfWeekNumber" +
						 * (i + 1)); inputFieldValues[j + 1] =
						 * translatedDOWs.get(
						 * "translatedDOWs_translatedDayOfWeekName" + (i + 1));
						 * j += 2;
						 *
						 * }
						 *
						 * int k = 18; for (int i = 0; i < translatedMOYs.size()
						 * / 2; i++) {
						 *
						 * inputFieldValues[k] =
						 * translatedMOYs.get("translatedMOYs_monthOfYearNumber"
						 * + (i + 1)); inputFieldValues[k + 1] =
						 * translatedMOYs.get(
						 * "translatedMOYs_translatedMonthOfYearName" + (i +
						 * 1)); k += 2; }

						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: " };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequestWithoutlanguageCode(userId, languageCode,
					nativeScriptLanguageName, englLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequestWithoutEnglLangNm(userId, languageCode, englLanguageNm,
					nativeScriptLanguageName, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when nativeScriptLanguageName is not passed in JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when nativeScriptLanguageName is not passed in JSON request");
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
	public void TC_12() {
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
			// String payload =
			// PostMethod.langPostRequestWithoutNatScrptLangNm(userId,
			// languageCode, englLanguageNm
			// , translatedDOWs, translatedMOYs);
			String payload = PostMethod.langPostRequestWithoutNatScrptLangNm(userId, languageCode,
					nativeScriptLanguageName, englLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			if (Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***get the DB query

				// Query1
				String langPostPostQuery1 = query.langPostQuery(languageCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langDbFields();
				fields.remove(0);// ***removing user name field since we are
									// going to validate only last updated user
									// name
				// ***get the result from DB
				List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
				getResultDBFinal.addAll(getResultDB1);

				// Query2
				for (int i = 0; i < translatedDOWs.size() / 2; i++) {

					String languagePostQuery2 = query.langTrnslDowPostQuery(languageCode,
							translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields2 = ValidationFields.langTrnslDowDbFields();
					// ***get the result from DB
					List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB2);

				}

				// Query3
				for (int i = 0; i < translatedMOYs.size() / 2; i++) {

					String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(languageCode,
							translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1)));
					// ***get the fields needs to be validate in DB
					List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
					// ***get the result from DB
					List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName,
							testCaseID);
					getResultDBFinal.addAll(getResultDB3);

				}

				if (js.getString("data.languageCode") != null) {
					String languageCode1 = js.getString("data.languageCode");
					// ***success message validation
					String expectMessage = resMsgs.langPutSuccessMsg + languageCode1;
					if (internalMsg.equals(expectMessage)) {
						logger.info("Success response is getting received with Language Code: " + languageCode);
						test.pass("Success response is getting received with Language Code: " + languageCode);
						// ***send the input, response, DB result for validation
						String[] inputFieldValues = new String[42];

						inputFieldValues[0] = languageCode;
						inputFieldValues[1] = englLanguageNm;
						inputFieldValues[2] = nativeScriptLanguageName;
						inputFieldValues[3] = userId;

						int j = 4;
						for (int i = 0; i < translatedDOWs.size() / 2; i++) {

							inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
							inputFieldValues[j + 1] = translatedDOWs
									.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
							j += 2;

						}

						int k = 18;
						for (int i = 0; i < translatedMOYs.size() / 2; i++) {

							inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
							inputFieldValues[k + 1] = translatedMOYs
									.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
							k += 2;
						}
						// ***get response fields values
						List<String> resFields = ValidationFields.langResponseFileds(res);
						logger.info("Language Table Validation Starts:");
						test.info("Language Table Validation Starts:");
						testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal,
								resFields);
						// ***write result to excel
						String[] inputFieldNames = { "Input_languageCode: ", "Input_englLanguageNm: ",
								"Input_nativeScriptLanguageName:", "Input_LastUpdateUserName: ",
								"Input_dayOfWeekNumber1:", "Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
								"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
								"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
								"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
								"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
								"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
								"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
								"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
								"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
								"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
								"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
								"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
								"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
								"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
								"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
								"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
								"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
								"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
								"Input_translatedMonthOfYearName12:" };

						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

						String[] dbFieldNames = { "DB_languageCode: ", "DB_englLanguageNm: ",
								"DB_nativeScriptLanguageName:", "DB_LastUpdateUserName: ", "DB_dayOfWeekNumber1:",
								"DB_translatedDayOfWeekName1:", "DB_dayOfWeekNumber2:", "DB_translatedDayOfWeekName2:",
								"DB_dayOfWeekNumber3:", "DB_translatedDayOfWeekName3:", "DB_dayOfWeekNumber4:",
								"DB_translatedDayOfWeekName4:", "DB_dayOfWeekNumber5:", "DB_translatedDayOfWeekName5:",
								"DB_dayOfWeekNumber6:", "DB_translatedDayOfWeekName6:", "DB_dayOfWeekNumber7:",
								"DB_translatedDayOfWeekName7:", "DB_monthOfYearNumber1:",
								"DB_translatedMonthOfYearName1:", "DB_monthOfYearNumber2:",
								"DB_translatedMonthOfYearName2:", "DB_monthOfYearNumber3:",
								"DB_translatedMonthOfYearName3:", "DB_monthOfYearNumber4:",
								"DB_translatedMonthOfYearName4:", "DB_monthOfYearNumber5:",
								"DB_translatedMonthOfYearName5:", "DB_monthOfYearNumber6:",
								"DB_translatedMonthOfYearName6:", "DB_monthOfYearNumber7:",
								"DB_translatedMonthOfYearName7:", "DB_monthOfYearNumber8:",
								"DB_translatedMonthOfYearName8:", "DB_monthOfYearNumber9:",
								"DB_translatedMonthOfYearName9:", "DB_monthOfYearNumber10:",
								"DB_translatedMonthOfYearName10:", "DB_monthOfYearNumber11:",
								"DB_translatedMonthOfYearName11:", "DB_monthOfYearNumber12:",
								"DB_translatedMonthOfYearName12:" };

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
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutUserNm(userId, languageCode, englLanguageNm,
					nativeScriptLanguageName, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequestWithoutMeta(userId, languageCode, englLanguageNm,
					nativeScriptLanguageName, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
			getEndPoinUrl = getEndPoinUrl.substring(0, 94);
			// ***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
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
			String expectMessage = resMsgs.invalidUrlMsgLangPut;
			if (Wscode == 404 && meta != null && actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_languageName: ",
							"Input_nativeScriptLanguageName:", "Input_dowNbr1:", "Input_transDowName1:",
							"Input_dowNbr2:", "Input_transDowName2:", "Input_dowNbr3:", "Input_transDowName3:",
							"Input_dowNbr4:", "Input_transDowName4:", "Input_dowNbr5:", "Input_transDowName5:",
							"Input_dowNbr6:", "Input_transDowName6:", "Input_dowNbr7:", "Input_transDowName7:",
							"Input_mthOfYrNbr1:", "Input_transMoyName1:", "Input_mthOfYrNbr2:", "Input_transMoyName2:",
							"Input_mthOfYrNbr3:", "Input_transMoyName3:", "Input_mthOfYrNbr4:", "Input_transMoyName4:",
							"Input_mthOfYrNbr5:", "Input_transMoyName5:", "Input_mthOfYrNbr6:", "Input_transMoyName6:",
							"Input_mthOfYrNbr7:", "Input_transMoyName7:", "Input_mthOfYrNbr8:", "Input_transMoyName8:",
							"Input_mthOfYrNbr9:", "Input_transMoyName9:", "Input_mthOfYrNbr10:",
							"Input_transMoyName10:", "Input_mthOfYrNbr11:", "Input_transMoyName11:",
							"Input_mthOfYrNbr12:", "Input_transMoyName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when Wrong URI is passed ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when Wrong URI is passed ");
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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
			for (int i = 0; i < errorMsgLength; i++) {
				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			int Wscode = res.statusCode();
			String expectMessage = resMsgs.lengthExceeds3Char;
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("languageCode") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("languageName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("dayOfWeekNumber") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
					logger.info(
							"Expected error message is getting received in response when dayOfWeekNumber is more than 38 characters JSON request");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when dayOfWeekNumber is more than 38 characters JSON request");
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("translatedDayOfWeekName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
							Wsstatus, "" + Wscode, responsestr, "Fail", "translatedDayOfWeekName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "translatedDayOfWeekName" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					test.info("Input Data Values:");
					test.info(writableInputFields.replaceAll("\n", "<br />"));
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("translatedMonthOfYearName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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
							Wsstatus, "" + Wscode, responsestr, "Fail", "translatedMonthOfYearName" + expectMessage);
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
						"" + Wscode, responsestr, "Fail", "translatedMonthOfYearName" + expectMessage);
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
			String payload = PostMethod.langPostRequest(userId, languageCode, englLanguageNm, nativeScriptLanguageName,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".lang.put");
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

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				if (errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage)) {

					String[] inputFieldValues = new String[43];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;

					int j = 4;
					for (int i = 0; i < translatedDOWs.size() / 2; i++) {

						inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dayOfWeekNumber" + (i + 1));
						inputFieldValues[j + 1] = translatedDOWs
								.get("translatedDOWs_translatedDayOfWeekName" + (i + 1));
						j += 2;

					}

					int k = 18;
					for (int i = 0; i < translatedMOYs.size() / 2; i++) {

						inputFieldValues[k] = translatedMOYs.get("translatedMOYs_monthOfYearNumber" + (i + 1));
						inputFieldValues[k + 1] = translatedMOYs
								.get("translatedMOYs_translatedMonthOfYearName" + (i + 1));
						k += 2;
					}
					inputFieldValues[42] = userId;

					String[] inputFieldNames = { "Input_UserName: ", "Input_languageCode: ", "Input_englLanguageNm: ",
							"Input_nativeScriptLanguageName:", "Input_dayOfWeekNumber1:",
							"Input_translatedDayOfWeekName1:", "Input_dayOfWeekNumber2:",
							"Input_translatedDayOfWeekName2:", "Input_dayOfWeekNumber3:",
							"Input_translatedDayOfWeekName3:", "Input_dayOfWeekNumber4:",
							"Input_translatedDayOfWeekName4:", "Input_dayOfWeekNumber5:",
							"Input_translatedDayOfWeekName5:", "Input_dayOfWeekNumber6:",
							"Input_translatedDayOfWeekName6:", "Input_dayOfWeekNumber7:",
							"Input_translatedDayOfWeekName7:", "Input_monthOfYearNumber1:",
							"Input_translatedMonthOfYearName1:", "Input_monthOfYearNumber2:",
							"Input_translatedMonthOfYearName2:", "Input_monthOfYearNumber3:",
							"Input_translatedMonthOfYearName3:", "Input_monthOfYearNumber4:",
							"Input_translatedMonthOfYearName4:", "Input_monthOfYearNumber5:",
							"Input_translatedMonthOfYearName5:", "Input_monthOfYearNumber6:",
							"Input_translatedMonthOfYearName6:", "Input_monthOfYearNumber7:",
							"Input_translatedMonthOfYearName7:", "Input_monthOfYearNumber8:",
							"Input_translatedMonthOfYearName8:", "Input_monthOfYearNumber9:",
							"Input_translatedMonthOfYearName9:", "Input_monthOfYearNumber10:",
							"Input_translatedMonthOfYearName10:", "Input_monthOfYearNumber11:",
							"Input_translatedMonthOfYearName11:", "Input_monthOfYearNumber12:",
							"Input_translatedMonthOfYearName12:", "Input_LastUpdateUserName: " };

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

	@Test(priority = 2)
	public void TC_25() {
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
			String payload = PostMethod.langPostRequestwithCommaMissing(userId, languageCode, nativeScriptLanguageName,
					englLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			System.out.println("reqFormatted :: " + reqFormatted);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.put");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			System.out.println("responsestr1 " + responsestr1);
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
			if (Wscode == 400 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 400 validation passed: " + Wscode);
				test.pass("Response status code 400 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
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
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.put");
			// ***send request and get response

			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			System.out.println("responsestr1 " + responsestr1);

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
			if (Wscode == 405 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response meta validation passed");

				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
					inputFieldValues[3] = nativeScriptLanguageName;
					String[] inputFieldNames = { "Input_UserName: ", "Input_GeoRltspTypeCode: ",
							"Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: " };

					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response we use PUT url but selecting GET method.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response we use PUT url but selecting GET method. ");
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
			String payload = PostMethod.langPostRequest(userId, languageCode, nativeScriptLanguageName, englLanguageNm,
					translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level + ".lang.post");
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
				ValidationFields.timestampValidation(js, res);
				// ValidationFields.timestampValidation(js, res);
				// ***error message validation

				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					String[] inputFieldValues = new String[4];

					inputFieldValues[0] = userId;
					inputFieldValues[1] = languageCode;
					inputFieldValues[2] = englLanguageNm;
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

	@Test(priority = 1)
	public void TC_28() {
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

			JSONObject getJMSResult = jmsReader.messageGetsPublished("LANGUAGE");
			if (getJMSResult != null) {
				// *** get JMS response

				String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				test.info("JMS Response Recieved:");
				test.info(reqFormatted.replaceAll("\n", "<br />"));


				 * String nativeScriptLanguageName1; Strin
				 * languageCode=getJMSResult.getString("languageCode"); String
				 * englLanguageNm=getJMSResult.getString("englLanguageNm");
				 * if(getJMSResult.isNull("nativeScriptLanguageName"))
				 * nativeScriptLanguageName1=""; else
				 * nativeScriptLanguageName1=getJMSResult.getString(
				 * "nativeScriptLanguageName");


				// BigInteger nativeScriptLanguageName1 =
				// getJMSResult.getBigInteger("nativeScriptLanguageName");

				String languageCode = getJMSResult.getJSONObject("data").getString("languageCode");
				String englLanguageNm = getJMSResult.getJSONObject("data").getString("languageName");
				String nativeScriptLanguageName1 = getJMSResult.getJSONObject("data")
						.getString("nativeScriptLanguageName");

				String[] jmsFieldValues = new String[3];

				jmsFieldValues[0] = languageCode;
				jmsFieldValues[1] = englLanguageNm;
				jmsFieldValues[2] = nativeScriptLanguageName1;

				// *** Reading translatedDOWs array

				 * JSONArray jsonArraytranslatedDOWs =
				 * getJMSResult.getJSONObject("data").getJSONArray(
				 * "translatedDOWs"); JSONObject jObj = null; int l = 3; for
				 * (int i = 0; i < jsonArraytranslatedDOWs.length(); i++) { jObj
				 * = jsonArraytranslatedDOWs.getJSONObject(i); jmsFieldValues[l]
				 * = jObj.optString("dayOfWeekNumber"); jmsFieldValues[l + 1] =
				 * jObj.optString("translatedDayOfWeekName"); l += 2; }


				// ***Reading translatedMOYs array

				 * JSONArray jsonArraytranslatedMOYs =
				 * getJMSResult.getJSONObject("data").getJSONArray(
				 * "translatedMOYs"); JSONObject jObj1 = null; int m = 17; for
				 * (int i = 0; i < jsonArraytranslatedMOYs.length(); i++) {
				 * jObj1 = jsonArraytranslatedMOYs.getJSONObject(i);
				 * jmsFieldValues[m] = jObj1.optString("monthOfYearNumber");
				 * jmsFieldValues[m + 1] =
				 * jObj1.optString("translatedMonthOfYearName"); m += 2; }

				if (languageCode != null) {
					// ***get the DB query
					String langPostPostQuery1 = query.langPostQuery(languageCode);
					// ***get the fields needs to be validate in DB
					List<String> jmsFields1 = ValidationFields.langJMSDbFields();
					// ***get the result from DB
					List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, jmsFields1, fileName,
							testCaseID);
					// getResultDBFinal.addAll(getResultDB1);


					 * // Query2 for DOW for (int i = 0; i <
					 * jsonArraytranslatedDOWs.length(); i++) { jObj =
					 * jsonArraytranslatedDOWs.getJSONObject(i); String
					 * languagePostQuery2 =
					 * query.langTrnslDowPostQuery(languageCode,
					 * jObj.optString("dayOfWeekNumber")); // ***get the fields
					 * needs to be validate in DB List<String> jmsFields2 =
					 * ValidationFields.langTrnslDowDbFields(); // ***get the
					 * result from DB List<String> getResultDB2 =
					 * DbConnect.getResultSetFor(languagePostQuery2, jmsFields2,
					 * fileName, testCaseID);
					 * getResultDBFinal.addAll(getResultDB2); }


					// Query3

					 * for (int i = 0; i < jsonArraytranslatedMOYs.length();
					 * i++) { jObj1 = jsonArraytranslatedMOYs.getJSONObject(i);
					 * String languagePostQuery3 =
					 * query.langTrnslMonthOfYearPostQuery(languageCode,
					 * jObj1.optString("monthOfYearNumber")); // ***get the
					 * fields needs to be validate in DB List<String> jmsFlds3 =
					 * ValidationFields.langTrnslMonthOfYearDbFields(); //
					 * ***get the result from DB List<String> getResultDB3 =
					 * DbConnect.getResultSetFor(languagePostQuery3, jmsFlds3,
					 * fileName, testCaseID);
					 * getResultDBFinal.addAll(getResultDB3); }


					testResult = TestResultValidation.testValidationForJMS(jmsFieldValues, getResultDB1);

					 * String[] jmsFieldNames = { "Response_languageCode:  ",
					 * "Response_englLanguageNm:  ",
					 * "Response_nativeScriptLanguageName: ",
					 * "Response_dayOfWeekNumber1:  ",
					 * "Response_translatedDayOfWeekName1:  ",
					 * "Response_dayOfWeekNumber2:  ",
					 * "Response_translatedDayOfWeekName2:  ",
					 * "Response_dayOfWeekNumber3:  ",
					 * "Response_translatedDayOfWeekName3:  ",
					 * "Response_dayOfWeekNumber4:  ",
					 * "Response_translatedDayOfWeekName4:  ",
					 * "Response_dayOfWeekNumber5:  ",
					 * "Response_translatedDayOfWeekName5:  ",
					 * "Response_dayOfWeekNumber6:  ",
					 * "Response_translatedDayOfWeekName6:  ",
					 * "Response_dayOfWeekNumber7:  ",
					 * "Response_translatedDayOfWeekName7:  ",
					 * "Response_monthOfYearNumber1:  ",
					 * "Response_translatedMonthOfYearName1:  ",
					 * "Response_monthOfYearNumber2:  ",
					 * "Response_translatedMonthOfYearName2:  ",
					 * "Response_monthOfYearNumber3:  ",
					 * "Response_translatedMonthOfYearName3:  ",
					 * "Response_monthOfYearNumber4:  ",
					 * "Response_translatedMonthOfYearName4:  ",
					 * "Response_monthOfYearNumber5:  ",
					 * "Response_translatedMonthOfYearName5:  ",
					 * "Response_monthOfYearNumber6:  ",
					 * "Response_translatedMonthOfYearName6:  ",
					 * "Response_monthOfYearNumber7:  ",
					 * "Response_translatedMonthOfYearName7:  ",
					 * "Response_monthOfYearNumber8:  ",
					 * "Response_translatedMonthOfYearName8:  ",
					 * "Response_monthOfYearNumber9:  ",
					 * "Response_translatedMonthOfYearName9:  ",
					 * "Response_monthOfYearNumber10:  ",
					 * "Response_translatedMonthOfYearName10:  ",
					 * "Response_monthOfYearNumber11:  ",
					 * "Response_translatedMonthOfYearName11:  ",
					 * "Response_monthOfYearNumber12:  ",
					 * "Response_translatedMonthOfYearName12:  " };
					 *
					 * String[] dbFieldNames = { "DB_languageCode:   ",
					 * "DB_englLanguageNm:   ", "DB_nativeScriptLanguageName:  "
					 * , "DB_dayOfWeekNumber1:  ",
					 * "DB_translatedDayOfWeekName1:  ",
					 * "DB_dayOfWeekNumber2:  ",
					 * "DB_translatedDayOfWeekName2:  ",
					 * "DB_dayOfWeekNumber3:  ",
					 * "DB_translatedDayOfWeekName3:  ",
					 * "DB_dayOfWeekNumber4:  ",
					 * "DB_translatedDayOfWeekName4:  ",
					 * "DB_dayOfWeekNumber5:  ",
					 * "DB_translatedDayOfWeekName5:  ",
					 * "DB_dayOfWeekNumber6:  ",
					 * "DB_translatedDayOfWeekName6:  ",
					 * "DB_dayOfWeekNumber7:  ",
					 * "DB_translatedDayOfWeekName7:  ",
					 * "DB_monthOfYearNumber1:  ",
					 * "DB_translatedMonthOfYearName1:  ",
					 * "DB_monthOfYearNumber2:  ",
					 * "DB_translatedMonthOfYearName2:  ",
					 * "DB_monthOfYearNumber3:  ",
					 * "DB_translatedMonthOfYearName3:  ",
					 * "DB_monthOfYearNumber4:  ",
					 * "DB_translatedMonthOfYearName4:  ",
					 * "DB_monthOfYearNumber5:  ",
					 * "DB_translatedMonthOfYearName5:  ",
					 * "DB_monthOfYearNumber6:  ",
					 * "DB_translatedMonthOfYearName6:  ",
					 * "DB_monthOfYearNumber7:  ",
					 * "DB_translatedMonthOfYearName7:  ",
					 * "DB_monthOfYearNumber8:  ",
					 * "DB_translatedMonthOfYearName8:  ",
					 * "DB_monthOfYearNumber9:  ",
					 * "DB_translatedMonthOfYearName9:  ",
					 * "DB_monthOfYearNumber10:  ",
					 * "DB_translatedMonthOfYearName10:  ",
					 * "DB_monthOfYearNumber11:  ",
					 * "DB_translatedMonthOfYearName11:  ",
					 * "DB_monthOfYearNumber12:  ",
					 * "DB_translatedMonthOfYearName12:  " };


					String[] jmsFieldNames = { "Response_languageCode:  ", "Response_englLanguageNm:  ",
							"Response_nativeScriptLanguageName: " };

					String[] dbFieldNames = { "DB_languageCode:   ", "DB_englLanguageNm:   ",
							"DB_nativeScriptLanguageName:  " };

					if (testResult) {
						writableJMSResult = Miscellaneous.geoFieldInputNames(jmsFieldValues, jmsFieldNames);
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB1, dbFieldNames);
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
						writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB1, dbFieldNames);
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
		languageCode = inputData1.get(testCaseId).get("langCd");
		englLanguageNm = inputData1.get(testCaseId).get("englLanguageNm");
		nativeScriptLanguageName = inputData1.get(testCaseId).get("nativeScriptLanguageNm");

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
*/