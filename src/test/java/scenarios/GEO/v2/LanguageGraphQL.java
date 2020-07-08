package scenarios.GEO.v2;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

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

public class LanguageGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, langCd;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguageGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();
	Connection con;
	Statement stmt;

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		String actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level+".graphQL.version");
		actuatorGraphQLversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorGraphQLVersionURL);
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		String testCaseName = null;
		if (runFlag.equalsIgnoreCase("Yes")) {
			testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
		con = DbConnect.getSqlStatement(fileName, testCaseName);
		if (con != null) {
			test.info("DB Connection Success");
		} else {
			test.fail("DB not connected");
		}
	}

	@AfterMethod
	public void after() {
		try {
			con.close();
		} catch (SQLException e) {
			test.fail("DB connection close failed or connection not active: " + e);
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{  languages {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(Miscellaneous.jsonFormat(responsestr));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");

			List<String> responseRows = js.get("data.languages");
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
				// ***get the DB query
				String langGetQuery = query.langGetQuery();
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].languageCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].languageCode"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].engLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].engLanguageName"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptLanguageName"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size())// getResultDB.size()
					{
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							System.out.println("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));

							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data.languages[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields1 = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getResultDB1 = new ArrayList<>();
							try {
								stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
										ResultSet.CONCUR_READ_ONLY);
								ResultSet result = stmt.executeQuery(translatedDOWsDbQuery);
								result.last();
								result.beforeFirst();
								String checkNull = null;
								while (result.next()) {
									for (int d = 0; d < fields1.size(); d++) {
										checkNull = result.getString(fields1.get(d));
										if (StringUtils.isBlank(checkNull)) {
											checkNull = "";
										}
										getResultDB1.add(checkNull.trim());
									}
								}
								stmt.close();
							} catch (SQLException e) {
								ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
										"DB Connection Exception: " + e.toString());
								test.fail("DB connection failed: " + e);
								Assert.fail("Test Failed");
							}
							if (getResultDB1.size() == responseRows1.size() * fields1.size()) {
								List<String> getResponseRows1 = new ArrayList<>();
								for (int i = 0; i < responseRows1.size(); i++) {
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].dayOfWeekNumber"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].dayOfWeekNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"));
									}
								}
								test.info("TranslatedDOWs:");
								if (getResultDB1.size() == 0 && responseRows1.size() * fields1.size() == 0) {
									// logger.info("TranslatedDOWs records are
									// not available for this language code:
									// "+getResponseRows.get(j).toString());
									test.info("TranslatedDOWs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}

								for (int x = 0; x < getResultDB1.size(); x = x + fields1.size()) {
									if (getResultDB1.get(x).toString().equals(getResponseRows1.get(x).toString())
											&& getResultDB1.get(x + 1).toString()
													.equals(getResponseRows1.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dowNbr: ", "    DB_dowNbr: ",
												"    Response_transDowName: ", "    DB_transDowName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.pass(writableResult.replaceAll("\n", "<br />"));

									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dowNbr: ", "    DB_dowNbr: ",
												"    Response_transDowName: ", "    DB_transDowName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedDOWs between DB: "
										+ getResultDB1.size() / fields.size() + " & Response: " + responseRows1.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedDOWs matching between DB: "
												+ getResultDB1.size() / fields.size() + " & Response: "
												+ responseRows1.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}
							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data.languages[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearGraphQLDbFields();
							// ***get the result from DB
							List<String> getResultDB2 = new ArrayList<>();
							try {
								stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
										ResultSet.CONCUR_READ_ONLY);
								ResultSet result = stmt.executeQuery(translatedMOYsDbQuery);
								result.last();
								result.beforeFirst();
								String checkNull = null;
								while (result.next()) {
									for (int d = 0; d < fields2.size(); d++) {
										checkNull = result.getString(fields2.get(d));
										if (StringUtils.isBlank(checkNull)) {
											checkNull = "";
										}
										getResultDB2.add(checkNull.trim());
									}
								}
								stmt.close();
							} catch (SQLException e) {
								ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail",
										"DB Connection Exception: " + e.toString());
								test.fail("DB connection failed: " + e);
								Assert.fail("Test Failed");
							}
							if (getResultDB2.size() == responseRows2.size() * fields2.size()) {
								List<String> getResponseRows2 = new ArrayList<>();
								for (int i = 0; i < responseRows2.size(); i++) {

									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].monthOfYearNumber"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].monthOfYearNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].languageCode"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].languageCode"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())
											&& getResultDB2.get(x + 2).toString()
													.equals(getResponseRows2.get(x + 2).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedMOYs between DB: "
										+ getResultDB1.size() / fields.size() + " & Response: " + responseRows1.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedMOYs matching between DB: "
												+ getResultDB1.size() / fields.size() + " & Response: "
												+ responseRows1.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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
			// ** With Parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.languages");
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
				// ***get the DB query
				String langGetQuery = query.langGraphQLQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].languageCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].languageCode"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].engLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].engLanguageName"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptLanguageName"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data.languages[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields1 = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getResultDB1 = DbConnect.getResultSetFor(translatedDOWsDbQuery, fields1,
									fileName, testCaseID);
							if (getResultDB1.size() == responseRows1.size() * fields1.size()) {
								List<String> getResponseRows1 = new ArrayList<>();
								for (int i = 0; i < responseRows1.size(); i++) {
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].dayOfWeekNumber"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].dayOfWeekNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"));
									}
								}
								test.info("TranslatedDOWs:");
								if (getResultDB1.size() == 0 && responseRows1.size() * fields1.size() == 0) {
									test.info("TranslatedDOWs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB1.size(); x = x + fields1.size()) {
									if (getResultDB1.get(x).toString().equals(getResponseRows1.get(x).toString())
											&& getResultDB1.get(x + 1).toString()
													.equals(getResponseRows1.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber: ",
												"    DB_dayOfWeekNumber: ", "    Response_translatedDayOfWeekName: ",
												"    DB_translatedDayOfWeekName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber: ",
												"    DB_dayOfWeekNumber: ", "    Response_translatedDayOfWeekName: ",
												"    DB_translatedDayOfWeekName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedDOWs between DB: "
										+ getResultDB1.size() / fields.size() + " & Response: " + responseRows1.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedDOWs matching between DB: "
												+ getResultDB1.size() / fields.size() + " & Response: "
												+ responseRows1.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}
							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data.languages[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearGraphQLDbFields();
							// ***get the result from DB
							List<String> getResultDB2 = DbConnect.getResultSetFor(translatedMOYsDbQuery, fields2,
									fileName, testCaseID);
							if (getResultDB2.size() == responseRows2.size() * fields2.size()) {
								List<String> getResponseRows2 = new ArrayList<>();
								for (int i = 0; i < responseRows2.size(); i++) {
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].monthOfYearNumber"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].monthOfYearNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].languageCode"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].languageCode"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())
											&& getResultDB2.get(x + 2).toString()
													.equals(getResponseRows2.get(x + 2).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedMOYs between DB: "
										+ getResultDB1.size() / fields.size() + " & Response: " + responseRows1.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedMOYs matching between DB: "
												+ getResultDB1.size() / fields.size() + " & Response: "
												+ responseRows1.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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
			// *** Payload With Parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.languages");

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
				// ***get the DB query
				String langGetQuery = query.langGraphQLQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == 0 && responseRows.size() * fields.size() == 0) {
					test.pass("Records are not available for this language code: " + langCd);
					logger.info("Records are not available for this language code: " + langCd);
					test.pass("TestCase Passed");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
				} else {
					logger.error("Records are available for this language code: " + langCd);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Records are available for this language code: " + langCd);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", "Getting record for language code: " + langCd);
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

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "NA", "NA", Wsstatus,
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// *** payload
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOY {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");

			String expectMessage = resMsgs.moyLangErrorMsg;
			int Wscode = res.statusCode();
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
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid translatedMOYs atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid translatedMOYs atrribute in request body");

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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			// ** payload with parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOW {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");

			String expectMessage = resMsgs.dowLangErrorMsg;
			int Wscode = res.statusCode();
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
				// ***error message validation
				if (errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage)) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid translatedDOWs atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid translatedDOWs atrribute in request body");

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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
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
			// ** payload With Parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName        translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.languages");
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
				// ***get the DB query
				String langGetQuery = query.langGraphQLQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].languageCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].languageCode"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].engLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].engLanguageName"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptLanguageName"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data.languages[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields1 = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getResultDB1 = DbConnect.getResultSetFor(translatedDOWsDbQuery, fields1,
									fileName, testCaseID);
							if (getResultDB1.size() == responseRows1.size() * fields1.size()) {
								List<String> getResponseRows1 = new ArrayList<>();
								for (int i = 0; i < responseRows1.size(); i++) {
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].dayOfWeekNumber"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].dayOfWeekNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"));
									}
								}
								test.info("TranslatedDOWs:");
								if (getResultDB1.size() == 0 && responseRows1.size() * fields1.size() == 0) {
									test.info("TranslatedDOWs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB1.size(); x = x + fields1.size()) {
									if (getResultDB1.get(x).toString().equals(getResponseRows1.get(x).toString())
											&& getResultDB1.get(x + 1).toString()
													.equals(getResponseRows1.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber: ",
												"    DB_dayOfWeekNumber: ", "    Response_translatedDayOfWeekName: ",
												"    DB_translatedDayOfWeekName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber: ",
												"    DB_dayOfWeekNumber: ", "    Response_translatedDayOfWeekName: ",
												"    DB_translatedDayOfWeekName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedDOWs between DB: "
										+ getResultDB1.size() / fields.size() + " & Response: " + responseRows1.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedDOWs matching between DB: "
												+ getResultDB1.size() / fields.size() + " & Response: "
												+ responseRows1.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}

							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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
			// ** payload With Parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }      }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.languages");
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
				// ***get the DB query
				String langGetQuery = query.langGraphQLQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].languageCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].languageCode"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].engLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].engLanguageName"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptLanguageName"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");

							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data.languages[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGraphQLQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearGraphQLDbFields();
							// ***get the result from DB
							List<String> getResultDB2 = DbConnect.getResultSetFor(translatedMOYsDbQuery, fields2,
									fileName, testCaseID);
							if (getResultDB2.size() == responseRows2.size() * fields2.size()) {
								List<String> getResponseRows2 = new ArrayList<>();
								for (int i = 0; i < responseRows2.size(); i++) {
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].monthOfYearNumber"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].monthOfYearNumber"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].languageCode"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].languageCode"));
									}
									if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1)
											+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString("data.languages[" + (z - 1)
												+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())
											&& getResultDB2.get(x + 2).toString()
													.equals(getResponseRows2.get(x + 2).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString(),
												getResponseRows2.get(x + 2).toString(),
												getResultDB2.get(x + 2).toString() };
										String[] responseDbFieldNames2 = { "    Response_monthOfYearNumber: ",
												"    DB_monthOfYearNumber: ", "  Response_languageCode: ",
												"    DB_languageCode:    ", "Response_translatedMonthOfYearName: ",
												"    DB_translatedMonthOfYearName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "",
												"", writableResult, "Fail", "");
									}
								}
							} else {
								logger.error("Total number of records not matching for TranslatedMOYs between DB: "
										+ getResultDB2.size() / fields.size() + " & Response: " + responseRows2.size());
								logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
								logger.error("------------------------------------------------------------------");
								test.fail(
										"Total number of records not matching for TranslatedMOYs matching between DB: "
												+ getResultDB2.size() / fields.size() + " & Response: "
												+ responseRows2.size());
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "",
										Wsstatus, "" + Wscode, responsestr1, "Fail", "");
								Assert.fail("Test Failed");
							}
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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
			// ** With Parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptLanguageName      }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));

			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			List<String> responseRows = js.get("data.languages");
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
				// ***get the DB query
				String langGetQuery = query.langGraphQLQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].languageCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].languageCode"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].engLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].engLanguageName"));
						}
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptLanguageName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptLanguageName"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");

							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							z++;
						}
					}
				} else {
					logger.error("Total number of records not matching between DB: "
							+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records not matching between DB: " + getResultDB.size() / fields.size()
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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			// ** without parameter
			String payload = "{\"query\":\"{  languages(langCd : \\\"" + langCd
					+ "\\\") {    languageCod    engLanguageNam    nativeScriptLanguageNam    translatedMOYs {      monthOfYearNumber      languageCode      translatedMonthOfYearName    }    translatedDOWs {      dayOfWeekNumber      translatedDayOfWeekName    }  }}\"}";
			test.info("GraphQL Request:");
			test.info(Miscellaneous.jsonFormat(payload));
			// ***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String expectMessage = resMsgs.langCodeErrorMsg;
			String expectMessage1 = resMsgs.langNameErrorMsg;
			String expectMessage2 = resMsgs.natScriptLangNameErrorMsg;
			int Wscode = res.statusCode();
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
				// ***error message validation
				if ((errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage))
						&& (errorMsg1.get(1).equals("ValidationError") && errorMsg2.get(1).equals(expectMessage1))
						&& (errorMsg1.get(2).equals("ValidationError") && errorMsg2.get(2).equals(expectMessage2))) {

					logger.info(
							"Expected error message is getting received in response when sending the invalid languageCode, engLanguageName and nativeScriptLanguageName attribute name in request body.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid languageCode, engLanguageName and nativeScriptLanguageName attribute name in request body.");

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
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
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

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", Wsstatus,
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
		langCd = inputData1.get(testCaseId).get("langCd");
	}

}
