package scenarios.GEO.base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

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

public class GeoRltspGet extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, relationshipTypeCode, fromGeopoliticalId, toGeopoliticalId, targetDate,
			endDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(MonthOfYearGet.class);
	String actuatorQueryVersion;
	TestResultValidation resultValidation = new TestResultValidation();
	
	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		/// *** getting actautor version
				String tokenKey = tokenValues[0];
				String tokenVal = token;
				//String actuatorQueryVersionURL=RetrieveEndPoints.getEndPointUrl("queryActuator", fileName, level+".query.version");
				//actuatorQueryVersion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,/*actuatorQueryVersionURL);*/
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				String geoRsGetQuery = query.geopRltspGetQuery();
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithTargetEndDatesGetQuery(relationshipTypeCode, targetDate,
						endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithTargetEndDatesGetQuery(relationshipTypeCode, targetDate,
						endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithTargetEndDatesGetQuery(relationshipTypeCode, targetDate,
						endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithRltspCodeGetQuery(relationshipTypeCode);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromAndToGeoplIdGetQuery(relationshipTypeCode,
						fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromGeoplIdGetQuery(relationshipTypeCode, fromGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					boolean rowmatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
									&& getResultDB.get(j + 4).toString()
											.equals(getResponseRows.get(e + 4).toString())) {
								rowmatch = true;
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
										getResultDB.get(j + 4).toString() };
								String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
										"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ",
										"DB_toGeopoliticalId: ", "Response_relationshipTypeCode: ",
										"DB_relationshipTypeCode: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
										"Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;
								break;
							} 
						}
						if (!rowmatch) {
							test.fail("GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode,
									"Fail", "");
						}
						rowmatch = false;
					}

				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithToGeoplIdGetQuery(relationshipTypeCode, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromToGeoplIdAndTargetEndDatesGetQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromToGeoplIdAndTargetEndDatesGetQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromToGeoplIdAndTargetEndDatesGetQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					boolean rowmatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
									&& getResultDB.get(j + 4).toString()
											.equals(getResponseRows.get(e + 4).toString())) {
								rowmatch = true;
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
										getResultDB.get(j + 4).toString() };
								String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
										"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ",
										"DB_toGeopoliticalId: ", "Response_relationshipTypeCode: ",
										"DB_relationshipTypeCode: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
										"Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;

								break;
							} 
						}
						if (!rowmatch) {
							test.fail("GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode,
									"Fail", "");
						}
						rowmatch = false;
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					boolean rowmatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
									&& getResultDB.get(j + 4).toString()
											.equals(getResponseRows.get(e + 4).toString())) {
								rowmatch = true;
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
										getResultDB.get(j + 4).toString() };
								String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
										"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ",
										"DB_toGeopoliticalId: ", "Response_relationshipTypeCode: ",
										"DB_relationshipTypeCode: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
										"Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;
								break;
							} 
						}
						if (!rowmatch) {
							test.fail("GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode,
									"Fail", "");
						}
						rowmatch = false;

					}

				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, fromGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					boolean rowmatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(e).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(e + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(e + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(e + 3).toString())
									&& getResultDB.get(j + 4).toString()
											.equals(getResponseRows.get(e + 4).toString())) {
								rowmatch = true;
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(e).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(e + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(e + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(e + 3).toString(),
										getResultDB.get(j + 3).toString(), getResponseRows.get(e + 4).toString(),
										getResultDB.get(j + 4).toString() };
								String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
										"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ",
										"DB_toGeopoliticalId: ", "Response_relationshipTypeCode: ",
										"DB_relationshipTypeCode: ", "Response_effectiveDate: ", "DB_effectiveDate: ",
										"Response_expirationDate: ", "DB_expirationDate: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;
								break;
							} 
						}
						if (!rowmatch) {
							test.fail("GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"GeoplRltsp details are not matiching for the geoplId: " + relationshipTypeCode,
									"Fail", "");
						}
						rowmatch = false;
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithToGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithToGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithToGeoplIdGetAndTargetEndDatesQuery(relationshipTypeCode,
						targetDate, endDate, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithTargetEndDatesGetQuery(relationshipTypeCode, targetDate,
						endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithTargetEndDatesGetQuery(relationshipTypeCode, targetDate,
						endDate);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromAndToGeoplIdGetQuery(relationshipTypeCode,
						fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromAndToGeoplIdGetQuery(relationshipTypeCode,
						fromGeopoliticalId, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");

				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithFromGeoplIdGetQuery(relationshipTypeCode, fromGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			if (relationshipTypeCode != "") {
				getEndPoinUrl = getEndPoinUrl + "relationshipTypeCode=" + relationshipTypeCode;
			}
			if (fromGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&fromGeopoliticalId=" + fromGeopoliticalId;
			}
			if (toGeopoliticalId != "") {
				getEndPoinUrl = getEndPoinUrl + "&toGeopoliticalId=" + toGeopoliticalId;
			}
			if (targetDate != "" && !targetDate.equalsIgnoreCase("NoTargetDate")) {
				getEndPoinUrl = getEndPoinUrl + "&targetDate=" + targetDate;
			}
			if (endDate != "" && !endDate.equalsIgnoreCase("NoEndDate")) {
				getEndPoinUrl = getEndPoinUrl + "&endDate=" + endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length() - 1);
			if (lastCharOfUrl.equals("?")) {
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
				test.pass("Response meta validation passed");
				test.pass("Response API version number validation passed");
				// ***get the DB query
				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoRsGetQuery = query.geopRltspWithToGeoplIdGetQuery(relationshipTypeCode, toGeopoliticalId);
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.geopRltspGetMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoRsGetQuery, fields, fileName, testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data[" + i + "].fromGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].fromGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].toGeopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].toGeopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].relationshipTypeCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data[" + i + "].relationshipTypeCode"));
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].effectiveDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data[" + i + "].expirationDate");
							int index = str.indexOf("T");
							str = str.substring(0, index);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					if (responseRows.size() == 0) {
						logger.info("0 matching records and there is no validation required");
						test.info("0 matching records and there is no validation required");
						// ***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if (internalMsg.equals(expectMessage)) {
							logger.info("Expected internal message is getting received in response for 0 records");
							test.pass("Expected internal message is getting received in response for 0 records");
						} else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
							logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
									Wsstatus, "" + Wscode, responsestr, "Fail", internalMsg);
							logger.info("------------------------------------------------------------------");
							Assert.fail("Test Failed");
						}
						logger.info("------------------------------------------------------------------");
					}
					int z = 1;
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
								&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
								&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
								&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())
								&& getResultDB.get(j + 4).toString().equals(getResponseRows.get(j + 4).toString())) {
							// ***write result to excel
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
									responseDbFieldNames);
							test.info("Record " + z + " Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									writableResult, "Pass", "");
							z++;
						} else {
							String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
									getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
									getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
									getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
									getResultDB.get(j + 3).toString(), getResponseRows.get(j + 4).toString(),
									getResultDB.get(j + 4).toString() };
							String[] responseDbFieldNames = { "Response_fromGeopoliticalId: ",
									"DB_fromGeopoliticalId: ", "Response_toGeopoliticalId: ", "DB_toGeopoliticalId: ",
									"Response_relationshipTypeCode: ", "DB_relationshipTypeCode: ",
									"Response_effectiveDate: ", "DB_effectiveDate: ", "Response_expirationDate: ",
									"DB_expirationDate: " };
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
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
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
		boolean testResult = false;
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".geoRltsp.get");
			getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 2);
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			String Wsstatus = js.getString("status");
			String internalMsg = js.getString("error");
			int Wscode = res.statusCode();
			String timestamp = js.getString("timestamp");
			if (Wscode == 404 && timestamp != null) {

				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response timestamp validation passed");
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
				if (Wscode != 404) {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
				} else if (timestamp == null) {
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}
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
		relationshipTypeCode = inputData1.get(testCaseId).get("relationshipTypeCode");
		fromGeopoliticalId = inputData1.get(testCaseId).get("fromGeopoliticalId");
		toGeopoliticalId = inputData1.get(testCaseId).get("toGeopoliticalId");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}

}
