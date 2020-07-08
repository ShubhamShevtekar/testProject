package scenarios.GEO.v1;

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

public class LanguageGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, langCd, locale_code, country_code;

	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguageGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();
	Connection conn;
	Statement stmt;

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		// String tokenKey = tokenValues[0];
		// String tokenVal = token;
		// String
		// actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator",
		// fileName, level+".graphQL.version");
		// actuatorGraphQLversion =resultValidation.versionValidation(fileName,
		// tokenKey, tokenVal,actuatorGraphQLVersionURL);
		actuatorGraphQLversion = "1.0.0";
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		String testCaseName = null;
		if (runFlag.equalsIgnoreCase("Yes")) {
			testCaseName = m.getName();
			test = extent.createTest(testCaseName);
			conn = DbConnect.getSqlStatement(fileName, testCaseName);
		}

		if (conn != null) {

		} else {
			test.fail("DB not connected");
		}
	}

	@AfterMethod
	public void after() {
		try {
			conn.close();
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			String payload = "{\"query\":\"{  languages {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName "
					+ "   locales {     localeCode      countryCode      languageCode      scriptCode  "
					+ "   dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription "
					+ "     dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate "
					+ "     expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }  "
					+ "    translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";

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
				String langGetQuery = query.langGraphQL_AllDataQuery();
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info(
							"Total number of Language records matching between DB & Response: " + responseRows.size());
					test.pass(
							"Total number of Language records matching between DB & Response: " + responseRows.size());
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							logger.info("Record " + z + " Validation: ");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");

							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							if (z == DbCon)
								DbCon += 50;
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							if (z == DbCon)
								DbCon += 50;
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

			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";

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
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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
			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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
			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOY {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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

			String expectMessage = resMsgs.moyLangLocaleErrorMsg;
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
			String payload = "{\"query\":\"{  languages {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOW {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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

			String expectMessage = resMsgs.dowLangLocaleErrorMsg;
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

			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }        }  }}\",\"variables\":null,\"operationName\":null}";

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
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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

			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate          translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";

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
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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

			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate           }  }}\",\"variables\":null,\"operationName\":null}";

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
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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

			String payload = "{\"query\":\"{  languages {    languageCod    engLanguageNam    nativeScriptCode    nativeScriptLanguageNam    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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

			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    }}\",\"variables\":null,\"operationName\":null}";

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
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
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
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// *** payload
			String payload = "{\"query\":\"{  languages ( languageCode : \\\"" + langCd
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locale {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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

			String expectMessage = resMsgs.langLocaleErrorMsg;
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
							"Expected error message is getting received in response when sending the invalid locales atrribute in request body");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid locales atrribute in request body");

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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);

			// ** without parameter

			String payload = "{\"query\":\"{  languages {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCod      countryCod      languageCod      scriptCod    dateFullFormatDescriptio      dateLongFormatDescriptio      dateMediumFormatDescriptio      dateShortFormatDescriptio      cldrVersionNumbe      cldrVersionDat      effectiveDat      expirationDat      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";
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
			String expectMessage = resMsgs.langLocalelocaleCodeErrorMsg;
			String expectMessage1 = resMsgs.langLocalecountryCodErrorMsg;
			String expectMessage2 = resMsgs.langLocalellanguageCodErrorMsg;
			String expectMessage3 = resMsgs.langLocalescriptCodErrorMsg;
			String expectMessage4 = resMsgs.langLocaledateFullFormatDescriptioErrorMsg;
			String expectMessage5 = resMsgs.langLocaledateLongFormatDescriptioErrorMsg;
			String expectMessage6 = resMsgs.langLocaledateMediumFormatDescriptioErrorMsg;
			String expectMessage7 = resMsgs.langLocaledateShortFormatDescriptioErrorMsg;
			String expectMessage8 = resMsgs.langLocalecldrVersionNumbeErrorMsg;
			String expectMessage9 = resMsgs.langLocalecldrVersionDatErrorMsg;
			String expectMessage10 = resMsgs.langLocaleeffectiveDatErrorMsg;
			String expectMessage11 = resMsgs.langLocaleexpirationDatErrorMsg;

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
						&& (errorMsg1.get(2).equals("ValidationError") && errorMsg2.get(2).equals(expectMessage2))
						&& (errorMsg1.get(3).equals("ValidationError") && errorMsg2.get(3).equals(expectMessage3))
						&& (errorMsg1.get(4).equals("ValidationError") && errorMsg2.get(4).equals(expectMessage4))
						&& (errorMsg1.get(5).equals("ValidationError") && errorMsg2.get(5).equals(expectMessage5))
						&& (errorMsg1.get(6).equals("ValidationError") && errorMsg2.get(6).equals(expectMessage6))
						&& (errorMsg1.get(7).equals("ValidationError") && errorMsg2.get(7).equals(expectMessage7))
						&& (errorMsg1.get(8).equals("ValidationError") && errorMsg2.get(8).equals(expectMessage8))
						&& (errorMsg1.get(9).equals("ValidationError") && errorMsg2.get(9).equals(expectMessage9))
						&& (errorMsg1.get(10).equals("ValidationError") && errorMsg2.get(10).equals(expectMessage10))
						&& (errorMsg1.get(11).equals("ValidationError") && errorMsg2.get(11).equals(expectMessage11)))

				{

					logger.info(
							"Expected error message is getting received in response when sending the invalid localeCode, countryCode, languageCode, scriptCode, dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription, dateShortFormatDescription, cldrVersionNumber, cldrVersionDate, effectiveDate, expirationDate  attribute name in request body.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid localeCode, countryCode, languageCode, scriptCode, dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription, dateShortFormatDescription, cldrVersionNumber, cldrVersionDate, effectiveDate, expirationDate  attribute name in request body.");

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

			String payload = "{\"query\":\"{  languages ( localeCode : \\\"" + locale_code
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";

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
				String langGetQuery = query.lang_LocaleCdGraphQLQuery(locale_code);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation_LocaleCd(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							localeDBValidation_LocaleCd(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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

			String payload = "{\"query\":\"{  languages ( countryCode : \\\"" + country_code
					+ "\\\") {    languageCode    engLanguageName    nativeScriptCode    nativeScriptLanguageName    locales {     localeCode      countryCode      languageCode      scriptCode     dateFullFormatDescription      dateLongFormatDescription      dateMediumFormatDescription      dateShortFormatDescription      cldrVersionNumber      cldrVersionDate      effectiveDate      expirationDate      translatedDOWs {        dayOfWeekNumber        translatedDayOfWeekName      }      translatedMOYs {        monthOfYearNumber        translatedMonthOfYearName     }    }  }}\",\"variables\":null,\"operationName\":null}";

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
				String langGetQuery = query.lang_countryCdGraphQLQuery(country_code);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGraphQLtMethodDbFields();
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
						if (StringUtils.isBlank(js.getString("data.languages[" + i + "].nativeScriptCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.languages[" + i + "].nativeScriptCode"));
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
					int DbCon = 50;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "",
									writableResult, "Pass", "");
							localeDBValidation_countryCd(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ", "Response_nativeScriptCode: ",
									"DB_nativeScriptCode: ", "Response_nativeScriptLanguageNm: ",
									"DB_nativeScriptLanguageNm: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);

							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "", "",
									"", writableResult, "Fail", "This record is not matching");
							localeDBValidation_countryCd(testCaseID, getResponseRows.get(j).toString(), js, z,
									responseRows.size(), DbCon);
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

	public void localeDBValidation(String testCaseID, String languageCode, JsonPath js, int z, int langSize, int DbCon)
			throws java.text.ParseException, ParseException {

		if (z == DbCon) {
			System.out.println("z: " + z);
			conn = DbConnect.getSqlStatement(fileName, testCaseID);

		}
		List<String> responseLocaleRows = js.get("data.languages[" + (z - 1) + "].locales");
		String langLocalePostQuery = query.langLocalesGraphQLQuery(languageCode);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langLocalesGraphQLDbFields();
		// ***get the result from DB
		List<String> getResultDBData = new ArrayList<String>();
		getResultDBData = getDBResultSet(langLocalePostQuery, fields, fileName, testCaseID, conn);
		if (getResultDBData.size() / fields.size() == responseLocaleRows.size()) {
			logger.info("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			test.pass("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			List<String> getLocaleResponseRows = new ArrayList<>();
			for (int i = 0; i < responseLocaleRows.size(); i++) {
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"));
				}

				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate"))) {
					getLocaleResponseRows.add("");
				} else {
					String res_CldeVersionDate = js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate");
					res_CldeVersionDate = res_CldeVersionDate.substring(0, 10);
					getLocaleResponseRows.add(res_CldeVersionDate);
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"));
				}
			}

			if (getResultDBData.size() == 0 && getLocaleResponseRows.size() * fields.size() == 0) {
				test.info("Locale code records are not available for this LanguageCode: " + languageCode);
			} else
				test.info("Locale code validation starts for LanguageCode: " + languageCode);

			List<String> getTempResponse = new ArrayList<>();
			List<String> getTempDB = new ArrayList<>();
			List<String> getSortDB = new ArrayList<>();
			getTempResponse.addAll(getLocaleResponseRows);
			getTempDB.addAll(getResultDBData);

			// sort DB record as per JSON response
			for (int i = 0; i < getTempResponse.size(); i += 12) {
				for (int j = 0; j < getTempDB.size(); j += 12) {
					if (getTempResponse.get(i).toString().equals(getTempDB.get(j).toString())) {
						if (getTempResponse.get(i + 1).toString().equals(getTempDB.get(j + 1).toString())
								&& getTempResponse.get(i + 2).toString().equals(getTempDB.get(j + 2).toString())
								&& getTempResponse.get(i + 3).toString().equals(getTempDB.get(j + 3).toString())
								&& getTempResponse.get(i + 4).toString().equals(getTempDB.get(j + 4).toString())
								&& getTempResponse.get(i + 5).toString().equals(getTempDB.get(j + 5).toString())
								&& getTempResponse.get(i + 6).toString().equals(getTempDB.get(j + 6).toString())
								&& getTempResponse.get(i + 7).toString().equals(getTempDB.get(j + 7).toString())
								&& getTempResponse.get(i + 8).toString().equals(getTempDB.get(j + 8).toString())
								&& getTempResponse.get(i + 9).toString().equals(getTempDB.get(j + 9).toString())
								&& getTempResponse.get(i + 10).toString().equals(getTempDB.get(j + 10).toString())
								&& getTempResponse.get(i + 11).toString().equals(getTempDB.get(j + 11).toString())) {
							for (int k = 0; k < 12; k++) {
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
			int count = 1;
			for (int j = 0; j < getLocaleResponseRows.size(); j += 12)
				if (getResultDB.get(j).toString().equals(getLocaleResponseRows.get(j).toString())
						&& getResultDB.get(j + 1).toString().equals(getLocaleResponseRows.get(j + 1).toString())
						&& getResultDB.get(j + 2).toString().equals(getLocaleResponseRows.get(j + 2).toString())
						&& getResultDB.get(j + 3).toString().equals(getLocaleResponseRows.get(j + 3).toString())
						&& getResultDB.get(j + 4).toString().equals(getLocaleResponseRows.get(j + 4).toString())
						&& getResultDB.get(j + 5).toString().equals(getLocaleResponseRows.get(j + 5).toString())
						&& getResultDB.get(j + 6).toString().equals(getLocaleResponseRows.get(j + 6).toString())
						&& getResultDB.get(j + 7).toString().equals(getLocaleResponseRows.get(j + 7).toString())
						&& getResultDB.get(j + 8).toString().equals(getLocaleResponseRows.get(j + 8).toString())
						&& getResultDB.get(j + 9).toString().equals(getLocaleResponseRows.get(j + 9).toString())
						&& getResultDB.get(j + 10).toString().equals(getLocaleResponseRows.get(j + 10).toString())
						&& getResultDB.get(j + 11).toString().equals(getLocaleResponseRows.get(j + 11).toString())) {
					// ***write result to excel
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.pass(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				} else {
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.fail(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				}
		} else {
			logger.error("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			test.fail("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "" + "", "", "Fail",
					"");
		}
	}

	public void localeDBValidation_LocaleCd(String testCaseID, String languageCode, JsonPath js, int z, int langSize,
			int DbCon) throws java.text.ParseException, ParseException {

		if (z == DbCon) {
			System.out.println("z: " + z);
			conn = DbConnect.getSqlStatement(fileName, testCaseID);

		}
		List<String> responseLocaleRows = js.get("data.languages[" + (z - 1) + "].locales");
		String langLocalePostQuery = query.langLocales_LocaleCdGraphQLQuery(locale_code);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langLocalesGraphQLDbFields();
		// ***get the result from DB
		List<String> getResultDBData = new ArrayList<String>();
		getResultDBData = getDBResultSet(langLocalePostQuery, fields, fileName, testCaseID, conn);
		if (getResultDBData.size() / fields.size() == responseLocaleRows.size()) {
			logger.info("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			test.pass("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			List<String> getLocaleResponseRows = new ArrayList<>();
			for (int i = 0; i < responseLocaleRows.size(); i++) {
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"));
				}

				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate"))) {
					getLocaleResponseRows.add("");
				} else {
					String res_CldeVersionDate = js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate");
					res_CldeVersionDate = res_CldeVersionDate.substring(0, 10);
					getLocaleResponseRows.add(res_CldeVersionDate);
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"));
				}
			}

			if (getResultDBData.size() == 0 && getLocaleResponseRows.size() * fields.size() == 0) {
				test.info("Locale code records are not available for this LanguageCode: " + languageCode);
			} else
				test.info("Locale code validation starts for LanguageCode: " + languageCode);

			List<String> getTempResponse = new ArrayList<>();
			List<String> getTempDB = new ArrayList<>();
			List<String> getSortDB = new ArrayList<>();
			getTempResponse.addAll(getLocaleResponseRows);
			getTempDB.addAll(getResultDBData);

			// sort DB record as per JSON response
			for (int i = 0; i < getTempResponse.size(); i += 12) {
				for (int j = 0; j < getTempDB.size(); j += 12) {
					if (getTempResponse.get(i).toString().equals(getTempDB.get(j).toString())) {
						if (getTempResponse.get(i + 1).toString().equals(getTempDB.get(j + 1).toString())
								&& getTempResponse.get(i + 2).toString().equals(getTempDB.get(j + 2).toString())
								&& getTempResponse.get(i + 3).toString().equals(getTempDB.get(j + 3).toString())
								&& getTempResponse.get(i + 4).toString().equals(getTempDB.get(j + 4).toString())
								&& getTempResponse.get(i + 5).toString().equals(getTempDB.get(j + 5).toString())
								&& getTempResponse.get(i + 6).toString().equals(getTempDB.get(j + 6).toString())
								&& getTempResponse.get(i + 7).toString().equals(getTempDB.get(j + 7).toString())
								&& getTempResponse.get(i + 8).toString().equals(getTempDB.get(j + 8).toString())
								&& getTempResponse.get(i + 9).toString().equals(getTempDB.get(j + 9).toString())
								&& getTempResponse.get(i + 10).toString().equals(getTempDB.get(j + 10).toString())
								&& getTempResponse.get(i + 11).toString().equals(getTempDB.get(j + 11).toString())) {
							for (int k = 0; k < 12; k++) {
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
			int count = 1;
			for (int j = 0; j < getLocaleResponseRows.size(); j += 12)
				if (getResultDB.get(j).toString().equals(getLocaleResponseRows.get(j).toString())
						&& getResultDB.get(j + 1).toString().equals(getLocaleResponseRows.get(j + 1).toString())
						&& getResultDB.get(j + 2).toString().equals(getLocaleResponseRows.get(j + 2).toString())
						&& getResultDB.get(j + 3).toString().equals(getLocaleResponseRows.get(j + 3).toString())
						&& getResultDB.get(j + 4).toString().equals(getLocaleResponseRows.get(j + 4).toString())
						&& getResultDB.get(j + 5).toString().equals(getLocaleResponseRows.get(j + 5).toString())
						&& getResultDB.get(j + 6).toString().equals(getLocaleResponseRows.get(j + 6).toString())
						&& getResultDB.get(j + 7).toString().equals(getLocaleResponseRows.get(j + 7).toString())
						&& getResultDB.get(j + 8).toString().equals(getLocaleResponseRows.get(j + 8).toString())
						&& getResultDB.get(j + 9).toString().equals(getLocaleResponseRows.get(j + 9).toString())
						&& getResultDB.get(j + 10).toString().equals(getLocaleResponseRows.get(j + 10).toString())
						&& getResultDB.get(j + 11).toString().equals(getLocaleResponseRows.get(j + 11).toString())) {
					// ***write result to excel
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.pass(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				} else {
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.fail(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				}
		} else {
			logger.error("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			test.fail("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "" + "", "", "Fail",
					"");
		}
	}

	public void localeDBValidation_countryCd(String testCaseID, String languageCode, JsonPath js, int z, int langSize,
			int DbCon) throws java.text.ParseException, ParseException {

		if (z == DbCon) {
			System.out.println("z: " + z);
			conn = DbConnect.getSqlStatement(fileName, testCaseID);

		}
		List<String> responseLocaleRows = js.get("data.languages[" + (z - 1) + "].locales");
		String langLocalePostQuery = query.langLocalesCountryCdGraphQLQuery(country_code);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langLocalesGraphQLDbFields();
		// ***get the result from DB
		List<String> getResultDBData = new ArrayList<String>();
		getResultDBData = getDBResultSet(langLocalePostQuery, fields, fileName, testCaseID, conn);
		if (getResultDBData.size() / fields.size() == responseLocaleRows.size()) {
			logger.info("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			test.pass("Total number of Language Locale records matching between DB & Response: "
					+ responseLocaleRows.size());
			List<String> getLocaleResponseRows = new ArrayList<>();
			for (int i = 0; i < responseLocaleRows.size(); i++) {
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].localeCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].countryCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].languageCode"));
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].scriptCode"));
				}

				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateFullFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateLongFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateMediumFormatDescription"));
				}
				if (StringUtils.isBlank(js
						.getString("data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows.add(js.getString(
							"data.languages[" + (z - 1) + "].locales[" + i + "].dateShortFormatDescription"));
				}
				if (StringUtils.isBlank(
						js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionNumber"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate"))) {
					getLocaleResponseRows.add("");
				} else {
					String res_CldeVersionDate = js
							.getString("data.languages[" + (z - 1) + "].locales[" + i + "].cldrVersionDate");
					res_CldeVersionDate = res_CldeVersionDate.substring(0, 10);
					getLocaleResponseRows.add(res_CldeVersionDate);
				}

				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].effectiveDate"));
				}
				if (StringUtils
						.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"))) {
					getLocaleResponseRows.add("");
				} else {
					getLocaleResponseRows
							.add(js.getString("data.languages[" + (z - 1) + "].locales[" + i + "].expirationDate"));
				}
			}

			if (getResultDBData.size() == 0 && getLocaleResponseRows.size() * fields.size() == 0) {
				test.info("Locale code records are not available for this LanguageCode: " + languageCode);
			} else
				test.info("Locale code validation starts for LanguageCode: " + languageCode);

			List<String> getTempResponse = new ArrayList<>();
			List<String> getTempDB = new ArrayList<>();
			List<String> getSortDB = new ArrayList<>();
			getTempResponse.addAll(getLocaleResponseRows);
			getTempDB.addAll(getResultDBData);

			// sort DB record as per JSON response
			for (int i = 0; i < getTempResponse.size(); i += 12) {
				for (int j = 0; j < getTempDB.size(); j += 12) {
					if (getTempResponse.get(i).toString().equals(getTempDB.get(j).toString())) {
						if (getTempResponse.get(i + 1).toString().equals(getTempDB.get(j + 1).toString())
								&& getTempResponse.get(i + 2).toString().equals(getTempDB.get(j + 2).toString())
								&& getTempResponse.get(i + 3).toString().equals(getTempDB.get(j + 3).toString())
								&& getTempResponse.get(i + 4).toString().equals(getTempDB.get(j + 4).toString())
								&& getTempResponse.get(i + 5).toString().equals(getTempDB.get(j + 5).toString())
								&& getTempResponse.get(i + 6).toString().equals(getTempDB.get(j + 6).toString())
								&& getTempResponse.get(i + 7).toString().equals(getTempDB.get(j + 7).toString())
								&& getTempResponse.get(i + 8).toString().equals(getTempDB.get(j + 8).toString())
								&& getTempResponse.get(i + 9).toString().equals(getTempDB.get(j + 9).toString())
								&& getTempResponse.get(i + 10).toString().equals(getTempDB.get(j + 10).toString())
								&& getTempResponse.get(i + 11).toString().equals(getTempDB.get(j + 11).toString())) {
							for (int k = 0; k < 12; k++) {
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
			int count = 1;
			for (int j = 0; j < getLocaleResponseRows.size(); j += 12)
				if (getResultDB.get(j).toString().equals(getLocaleResponseRows.get(j).toString())
						&& getResultDB.get(j + 1).toString().equals(getLocaleResponseRows.get(j + 1).toString())
						&& getResultDB.get(j + 2).toString().equals(getLocaleResponseRows.get(j + 2).toString())
						&& getResultDB.get(j + 3).toString().equals(getLocaleResponseRows.get(j + 3).toString())
						&& getResultDB.get(j + 4).toString().equals(getLocaleResponseRows.get(j + 4).toString())
						&& getResultDB.get(j + 5).toString().equals(getLocaleResponseRows.get(j + 5).toString())
						&& getResultDB.get(j + 6).toString().equals(getLocaleResponseRows.get(j + 6).toString())
						&& getResultDB.get(j + 7).toString().equals(getLocaleResponseRows.get(j + 7).toString())
						&& getResultDB.get(j + 8).toString().equals(getLocaleResponseRows.get(j + 8).toString())
						&& getResultDB.get(j + 9).toString().equals(getLocaleResponseRows.get(j + 9).toString())
						&& getResultDB.get(j + 10).toString().equals(getLocaleResponseRows.get(j + 10).toString())
						&& getResultDB.get(j + 11).toString().equals(getLocaleResponseRows.get(j + 11).toString())) {
					// ***write result to excel
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.pass(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				} else {
					String[] responseDbFieldValues = { getLocaleResponseRows.get(j).toString(),
							getResultDB.get(j).toString(), getLocaleResponseRows.get(j + 1).toString(),
							getResultDB.get(j + 1).toString(), getLocaleResponseRows.get(j + 2).toString(),
							getResultDB.get(j + 2).toString(), getLocaleResponseRows.get(j + 3).toString(),
							getResultDB.get(j + 3).toString(), getLocaleResponseRows.get(j + 4).toString(),
							getResultDB.get(j + 4).toString(), getLocaleResponseRows.get(j + 5).toString(),
							getResultDB.get(j + 5).toString(), getLocaleResponseRows.get(j + 6).toString(),
							getResultDB.get(j + 6).toString(), getLocaleResponseRows.get(j + 7).toString(),
							getResultDB.get(j + 7).toString(), getLocaleResponseRows.get(j + 8).toString(),
							getResultDB.get(j + 8).toString(), getLocaleResponseRows.get(j + 9).toString(),
							getResultDB.get(j + 9).toString(), getLocaleResponseRows.get(j + 10).toString(),
							getResultDB.get(j + 10).toString(), getLocaleResponseRows.get(j + 11).toString(),
							getResultDB.get(j + 11).toString() };

					String[] responseDbFieldNames = { "Response_localeCode:  ", "DB_localeCode:  ",
							"Response_CountryCode:  ", "DB_CountryCode:  ", "Response_languageCode:  ",
							"DB_languageCode:  ", "Response_localeScriptCode: ", "DB_localeScriptCode: ",
							"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
							"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
							"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
							"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
							"Response_cldrVersionNumber :", "DB_cldrVersionNumber: ", "Response_cldrVersionDate: ",
							"DB_cldrVersionDate: ", "Response_localesEffectiveDate: ", "DB_localesEffectiveDate: ",
							"Response_localesExpirationDate: ", "DB_localesExpirationDate: " };

					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
					test.info("Language code: " + getLocaleResponseRows.get(j + 2).toString()
							+ " :: Record Validation: " + count);
					test.fail(writableResult.replaceAll("\n", "<br />"));
					if ((!testCaseID.contains("TC_07")) && (!testCaseID.contains("TC_08")))
						DOW_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					if ((!testCaseID.contains("TC_06")) && (!testCaseID.contains("TC_08")))
						MOY_SectionValidation(testCaseID, getLocaleResponseRows.get(j).toString(), js, count, z);
					count++;
				}
		} else {
			logger.error("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			test.fail("Total number of Language Locale records not matching between DB: "
					+ getResultDBData.size() / fields.size() + " & Response: " + responseLocaleRows.size());
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "" + "", "", "Fail",
					"");
		}
	}

	public void DOW_SectionValidation(String testCaseID, String localeCode, JsonPath js, int localeCount, int z) {
		localeCount -= 1;

		List<String> responseDOW = js
				.get("data.languages[" + (z - 1) + "].locales[" + localeCount + "].translatedDOWs");
		// ***Get TranslatedDOWs query
		String translatedDOWsDbQuery = query.langTrnslDowGraphQLQuery(localeCode);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langTrnslDowDbFields();
		// ***get the result from DB
		List<String> getResultDB_DOW = new ArrayList<String>();
		List<String> getResponsDOW = new ArrayList<>();
		getResultDB_DOW = getDBResultSet(translatedDOWsDbQuery, fields, fileName, testCaseID, conn);
		if (getResultDB_DOW.size() == responseDOW.size() * fields.size()) {
			for (int i = 0; i < responseDOW.size(); i++) {
				if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
						+ "].translatedDOWs[" + i + "].dayOfWeekNumber"))) {
					getResponsDOW.add("");
				} else {
					getResponsDOW.add(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
							+ "].translatedDOWs[" + i + "].dayOfWeekNumber"));
				}
				if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
						+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"))) {
					getResponsDOW.add("");
				} else {
					getResponsDOW.add(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
							+ "].translatedDOWs[" + i + "].translatedDayOfWeekName"));
				}
			}
			if (getResultDB_DOW.size() == 0 && getResponsDOW.size() * fields.size() == 0) {
				test.info("TranslatedDOWs records are not available for this Locale code: " + localeCode);
			} else
				test.info("TranslatedDOWs validation starts for Locale code: " + localeCode);
			int cnt = 1;
			for (int x = 0; x < getResultDB_DOW.size(); x = x + fields.size()) {
				if (getResultDB_DOW.get(x).toString().equals(getResponsDOW.get(x).toString())
						&& getResultDB_DOW.get(x + 1).toString().equals(getResponsDOW.get(x + 1).toString())) {
					// ***write result to excel
					String[] responseDbFieldValues1 = { getResponsDOW.get(x).toString(),
							getResultDB_DOW.get(x).toString(), getResponsDOW.get(x + 1).toString(),
							getResultDB_DOW.get(x + 1).toString() };
					String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber_" + cnt + ": ",
							"    DB_dayOfWeekNumber_" + cnt + ": ",
							"    Response_translatedDayOfWeekName_" + cnt + ": ",
							"    DB_translatedDayOfWeekName_" + cnt + ": " };
					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1, responseDbFieldNames1);
					test.pass(writableResult.replaceAll("\n", "<br />"));
					ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "", writableResult,
							"Pass", "");
					cnt++;
				} else {
					String[] responseDbFieldValues1 = { getResponsDOW.get(x).toString(),
							getResultDB_DOW.get(x).toString(), getResponsDOW.get(x + 1).toString(),
							getResultDB_DOW.get(x + 1).toString() };
					String[] responseDbFieldNames1 = { "    Response_dayOfWeekNumber_" + cnt + ": ",
							"    DB_dayOfWeekNumber_" + cnt + ": ",
							"    Response_translatedDayOfWeekName_" + cnt + ": ",
							"    DB_translatedDayOfWeekName_" + cnt + ": " };
					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1, responseDbFieldNames1);
					test.fail(writableResult.replaceAll("\n", "<br />"));
					ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "", writableResult,
							"Fail", "");
					cnt++;
				}
			}
		} else {
			logger.error("Total number of records not matching for TranslatedDOWs between DB: "
					+ getResultDB_DOW.size() / fields.size() + " & Response: " + getResponsDOW.size());
			test.fail("Total number of records not matching for TranslatedDOWs matching between DB: "
					+ getResultDB_DOW.size() / fields.size() + " & Response: " + getResponsDOW.size());
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "" + "", "", "Fail",
					"");
		}
	}

	public void MOY_SectionValidation(String testCaseID, String localeCode, JsonPath js, int localeCount, int z) {
		localeCount -= 1;

		List<String> responseMOY = js
				.get("data.languages[" + (z - 1) + "].locales[" + localeCount + "].translatedMOYs");

		// ***Get TranslatedMOYs query
		String translatedMOYsDbQuery = query.langTrnslMOYGraphQLQuery(localeCode);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.langTrnslMonthOfYearDbFields();
		// ***get the result from DB
		List<String> getResultDB_MOY = new ArrayList<String>();
		List<String> getResponsMOY = new ArrayList<>();
		getResultDB_MOY = getDBResultSet(translatedMOYsDbQuery, fields, fileName, testCaseID, conn);
		if (getResultDB_MOY.size() == responseMOY.size() * fields.size()) {
			for (int i = 0; i < responseMOY.size(); i++) {
				if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
						+ "].translatedMOYs[" + i + "].monthOfYearNumber"))) {
					getResponsMOY.add("");
				} else {
					getResponsMOY.add(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
							+ "].translatedMOYs[" + i + "].monthOfYearNumber"));
				}
				if (StringUtils.isBlank(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
						+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"))) {
					getResponsMOY.add("");
				} else {
					getResponsMOY.add(js.getString("data.languages[" + (z - 1) + "].locales[" + localeCount
							+ "].translatedMOYs[" + i + "].translatedMonthOfYearName"));
				}
			}
			if (getResultDB_MOY.size() == 0 && getResponsMOY.size() * fields.size() == 0) {
				test.info("translatedMOYs records are not available for this Locale code: " + localeCode);
			} else
				test.info("translatedMOYs validation starts for Locale code: " + localeCode);
			int cnt = 1;
			for (int x = 0; x < getResultDB_MOY.size(); x = x + fields.size()) {
				if (getResultDB_MOY.get(x).toString().equals(getResponsMOY.get(x).toString())
						&& getResultDB_MOY.get(x + 1).toString().equals(getResponsMOY.get(x + 1).toString())) {
					// ***write result to excel
					String[] responseDbFieldValues1 = { getResponsMOY.get(x).toString(),
							getResultDB_MOY.get(x).toString(), getResponsMOY.get(x + 1).toString(),
							getResultDB_MOY.get(x + 1).toString() };
					String[] responseDbFieldNames1 = { "    Response_monthOfYearNumber_" + cnt + ": ",
							"    DB_monthOfYearNumber_" + cnt + ": ",
							"    Response_translatedMonthOfYearName_" + cnt + ": ",
							"    DB_translatedMonthOfYearName_" + cnt + ": " };
					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1, responseDbFieldNames1);
					test.pass(writableResult.replaceAll("\n", "<br />"));
					ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "", writableResult,
							"Pass", "");
					cnt++;
				} else {
					String[] responseDbFieldValues1 = { getResponsMOY.get(x).toString(),
							getResultDB_MOY.get(x).toString(), getResponsMOY.get(x + 1).toString(),
							getResultDB_MOY.get(x + 1).toString() };
					String[] responseDbFieldNames1 = { "    Resonse_monthOfYearNumber_" + cnt + ": ",
							"    DB_monthOfYearNumber_" + cnt + ": ",
							"    Response_translatedMonthOfYearName_" + cnt + ": ",
							"    DB_translatedMonthOfYearName_" + cnt + ": " };
					writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1, responseDbFieldNames1);
					test.fail(writableResult.replaceAll("\n", "<br />"));
					ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "", "", "", "", "", writableResult,
							"Fail", "");
					cnt++;
				}
			}
		} else {
			logger.error("Total number of records not matching for translatedMOYs between DB: "
					+ getResultDB_MOY.size() / fields.size() + " & Response: " + getResponsMOY.size());
			test.fail("Total number of records not matching for TranslatedDOWs matching between DB: "
					+ getResultDB_MOY.size() / fields.size() + " & Response: " + getResponsMOY.size());
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "" + "", "", "Fail",
					"");
		}
	}

	public static List<String> getDBResultSet(String query, List<String> fields, String fileName, String testCaseID,
			Connection con) {
		List<String> queryResult = new ArrayList<>();
		try {

			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(query);
			result.last();
			int rows = result.getRow();
			result.beforeFirst();
			String checkNull = null;
			while (result.next()) {
				int j = 0;
				for (int i = 0; i < fields.size(); i++) {
					checkNull = result.getString(fields.get(i));
					if (StringUtils.isBlank(checkNull)) {
						checkNull = "";
					}
					queryResult.add(checkNull.trim());
				}
			}
		} catch (SQLException e) {
			// ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "",
			// "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: " + e);
			// Assert.fail("Test Failed");
		}
		return queryResult;
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
		locale_code = inputData1.get(testCaseId).get("localeCode");
		country_code = inputData1.get(testCaseId).get("countryCode");
	}

}
