package scenarios.GEO.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utils.base.DbConnect;
import utils.base.ExcelUtil;
import utils.base.Miscellaneous;
import utils.base.Queries;
import utils.base.Reporting;
import utils.base.ResponseMessages;
import utils.base.RetrieveEndPoints;
import utils.base.TestResultValidation;
import utils.base.ValidationFields;
import wsMethods.base.GetResponse;
import wsMethods.base.PostMethod;

public class LanguageGet extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, langCd;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguageGet.class);
	Connection con;
	Statement stmt;
	
	String actuatorQueryVersion;
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		//String actuatorQueryVersionURL=RetrieveEndPoints.getEndPointUrl("queryActuator", fileName, level+".query.version");
		//actuatorQueryVersion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,/*actuatorQueryVersionURL);*/
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".lang.get");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Thread.sleep(5000);
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
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
					// ex.writeExcel(fileName, testCaseID, TestCaseDescription,
					// scenarioType, "NA", "", "",
					// Wsstatus, ""+Wscode, "", "Pass", "Total number of records
					// matching between DB & Response: "+responseRows.size()+",
					// below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].langCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].langCd"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].englLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].englLanguageNm"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].nativeScriptLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].nativeScriptLanguageNm"));
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
							// ex.writeExcel(fileName, "", TestCaseDescription,
							// scenarioType, "NA", "", "",
							// "", "", writableResult, "Pass", "" );
							// logger.info("TranslatedDOWs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGetQuery(getResponseRows.get(j).toString());
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
									if (StringUtils.isBlank(
											js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(
												js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString(
												"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"));
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
										// ex.writeExcel(fileName, "",
										// TestCaseDescription, scenarioType,
										// "NA", "", "",
										// "", "", writableResult, "Pass", "" );
									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dowNbr: ", "    DB_dowNbr: ",
												"    Response_transDowName: ", "    DB_transDowName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
								Assert.fail("Test Failed");
							}
							// logger.info("TranslatedMOYs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGetQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearDbFields();
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
									if (StringUtils.isBlank(js
											.getString("data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									// logger.info("TranslatedMOYs records are
									// not available for this language code:
									// "+getResponseRows.get(j).toString());
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										// ex.writeExcel(fileName, "",
										// TestCaseDescription, scenarioType,
										// "NA", "", "",
										// "", "", writableResult, "Pass", "" );
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
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
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
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
							"" + Wscode, responsestr1, "Fail", internalMsg);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}

			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".lang.get");
			getEndPoinUrl = getEndPoinUrl + langCd;
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			String meta = js.getString("meta");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				// ***get the DB query
				String langGetQuery = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].langCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].langCd"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].englLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].englLanguageNm"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].nativeScriptLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].nativeScriptLanguageNm"));
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
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							// logger.info("TranslatedDOWs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGetQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields1 = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getResultDB1 = DbConnect.getResultSetFor(translatedDOWsDbQuery, fields1,
									fileName, testCaseID);
							if (getResultDB1.size() == responseRows1.size() * fields1.size()) {
								List<String> getResponseRows1 = new ArrayList<>();
								for (int i = 0; i < responseRows1.size(); i++) {
									if (StringUtils.isBlank(
											js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(
												js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString(
												"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"));
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
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dowNbr: ", "    DB_dowNbr: ",
												"    Response_transDowName: ", "    DB_transDowName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
								Assert.fail("Test Failed");
							}
							// logger.info("TranslatedMOYs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGetQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearDbFields();
							// ***get the result from DB
							List<String> getResultDB2 = DbConnect.getResultSetFor(translatedMOYsDbQuery, fields2,
									fileName, testCaseID);
							if (getResultDB2.size() == responseRows2.size() * fields2.size()) {
								List<String> getResponseRows2 = new ArrayList<>();
								for (int i = 0; i < responseRows2.size(); i++) {
									if (StringUtils.isBlank(js
											.getString("data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									// logger.info("TranslatedMOYs records are
									// not available for this language code:
									// "+getResponseRows.get(j).toString());
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
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
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
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
							"" + Wscode, responsestr1, "Fail", internalMsg);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".lang.get");
			getEndPoinUrl = getEndPoinUrl + langCd;
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				// ***get the DB query
				String langGetQuery = query.langPostQuery(langCd);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.langGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(langGetQuery, fields, fileName, testCaseID);
				System.out.println("Response rows: " + responseRows.size());
				System.out.println("DB records: " + getResultDB.size() / fields.size());
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].langCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].langCd"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].englLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].englLanguageNm"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].nativeScriptLanguageNm"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].nativeScriptLanguageNm"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].scriptCd"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].scriptCd"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
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
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: ",
									"Response_scriptCd: ", "DB_scriptCd: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							// logger.info("TranslatedDOWs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedDOWs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows1 = js.get("data[" + (z - 1) + "].translatedDOWs");
							// ***Get TranslatedDOWs query
							String translatedDOWsDbQuery = query
									.langTrnslDowGetQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields1 = ValidationFields.langTrnslDowDbFields();
							// ***get the result from DB
							List<String> getResultDB1 = DbConnect.getResultSetFor(translatedDOWsDbQuery, fields1,
									fileName, testCaseID);
							if (getResultDB1.size() == responseRows1.size() * fields1.size()) {
								List<String> getResponseRows1 = new ArrayList<>();
								for (int i = 0; i < responseRows1.size(); i++) {
									if (StringUtils.isBlank(
											js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(
												js.getString("data[" + (z - 1) + "].translatedDOWs[" + i + "].dowNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"))) {
										getResponseRows1.add("");
									} else {
										getResponseRows1.add(js.getString(
												"data[" + (z - 1) + "].translatedDOWs[" + i + "].transDowName"));
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
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues1 = { getResponseRows1.get(x).toString(),
												getResultDB1.get(x).toString(), getResponseRows1.get(x + 1).toString(),
												getResultDB1.get(x + 1).toString() };
										String[] responseDbFieldNames1 = { "    Response_dowNbr: ", "    DB_dowNbr: ",
												"    Response_transDowName: ", "    DB_transDowName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues1,
												responseDbFieldNames1);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
								Assert.fail("Test Failed");
							}
							// logger.info("TranslatedMOYs validation starts for
							// Language code:
							// "+getResponseRows.get(j).toString());
							test.info("TranslatedMOYs validation starts for Language code: "
									+ getResponseRows.get(j).toString());
							List<String> responseRows2 = js.get("data[" + (z - 1) + "].translatedMOYs");
							// ***Get TranslatedDOWs query
							String translatedMOYsDbQuery = query
									.langTrnslMonthOfYearGetQuery(getResponseRows.get(j).toString());
							// ***get the fields needs to be validate in DB
							List<String> fields2 = ValidationFields.langTrnslMonthOfYearDbFields();
							// ***get the result from DB
							List<String> getResultDB2 = DbConnect.getResultSetFor(translatedMOYsDbQuery, fields2,
									fileName, testCaseID);
							if (getResultDB2.size() == responseRows2.size() * fields2.size()) {
								List<String> getResponseRows2 = new ArrayList<>();
								for (int i = 0; i < responseRows2.size(); i++) {
									if (StringUtils.isBlank(js
											.getString("data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].mthOfYrNbr"));
									}
									if (StringUtils.isBlank(js.getString(
											"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"))) {
										getResponseRows2.add("");
									} else {
										getResponseRows2.add(js.getString(
												"data[" + (z - 1) + "].translatedMOYs[" + i + "].transMoyName"));
									}
								}
								test.info("TranslatedMOYs:");
								if (getResultDB2.size() == 0 && responseRows2.size() * fields2.size() == 0) {
									// logger.info("TranslatedMOYs records are
									// not available for this language code:
									// "+getResponseRows.get(j).toString());
									test.info("TranslatedMOYs records are not available for this language code: "
											+ getResponseRows.get(j).toString());
								}
								for (int x = 0; x < getResultDB2.size(); x = x + fields2.size()) {
									if (getResultDB2.get(x).toString().equals(getResponseRows2.get(x).toString())
											&& getResultDB2.get(x + 1).toString()
													.equals(getResponseRows2.get(x + 1).toString())) {
										// ***write result to excel
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.pass(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
												"", writableResult, "Pass", "");
									} else {
										String[] responseDbFieldValues2 = { getResponseRows2.get(x).toString(),
												getResultDB2.get(x).toString(), getResponseRows2.get(x + 1).toString(),
												getResultDB2.get(x + 1).toString() };
										String[] responseDbFieldNames2 = { "    Response_mthOfYrNbr: ",
												"    DB_mthOfYrNbr: ", "    Response_transMoyName: ",
												"    DB_transMoyName: " };
										writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues2,
												responseDbFieldNames2);
										test.fail(writableResult.replaceAll("\n", "<br />"));
										ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "",
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
										Wsstatus, "" + Wscode, responsestr1, "Fail", internalMsg);
								Assert.fail("Test Failed");
							}
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString() };
							String[] responseDbFieldNames = { "Response_langCd: ", "DB_langCd: ",
									"Response_englLanguageNm: ", "DB_englLanguageNm: ",
									"Response_nativeScriptLanguageNm: ", "DB_nativeScriptLanguageNm: ",
									"Response_scriptCd: ", "DB_scriptCd: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));
							logger.info("Record " + z + " Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Fail", "This record is not matching");
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
							"" + Wscode, responsestr1, "Fail", internalMsg);
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
				} else if (!actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");
				}

				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wscode, responsestr1, "Fail", internalMsg);
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".lang.get");
			getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 2);
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("status");
			String internalMsg = js.getString("error");
			int Wscode = res.statusCode();
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			if (Wsstatus.equals("404")) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				// ***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if (internalMsg.equals(expectMessage)) {
					logger.info("Expected error message is getting received in response when passing the invalid URI");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when passing the invalid URI");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "NA", Wsstatus,
							"" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, responsestr, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			} else {
				logger.error("Response status validation failed: " + Wsstatus);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
						"" + Wsstatus, responsestr1, "Fail", internalMsg);
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
		langCd = inputData1.get(testCaseId).get("langCd");
	}

}
