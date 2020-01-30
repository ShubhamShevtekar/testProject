package scenarios.GEO;

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

public class StProvStdGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(StProvStdGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();
	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		//String actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level+".graphQL.version");
		//actuatorGraphQLversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorGraphQLVersionURL);
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
			String payload = PostMethod.stProvStdGraphQLRequstWithoutParam();
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.pass("URI passed: "+getEndPoinUrl);
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoStProvStdGetQuery = query.stProvStdGraphQLQuery(targetDate, endDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdGetMethodDbFieldsJMS();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].organizationStandardCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].organizationStandardCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceName"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					List<String> mergedResponseRows = new ArrayList<>();
					List<String> mergedDBRows = new ArrayList<>();
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						mergedResponseRows.add("Response_Geopl_ID:" + getResponseRows.get(j) + "\n Response_OrgStdNm:"
								+ getResponseRows.get(j + 1) + "\n Response_StProvCd:" + getResponseRows.get(j + 2)
								+ "\n Response_stProvNm:" + getResponseRows.get(j + 3) + "\n Response_effectiveDate:"
								+ getResponseRows.get(j + 4) + "\n Response_expirationDate:"
								+ getResponseRows.get(j + 5));
						mergedDBRows.add("DB_Geopl_ID:" + getResultDB.get(j) + "\n DB_OrgStdNm:"
								+ getResultDB.get(j + 1) + "\n DB_StProvCd:" + getResultDB.get(j + 2)
								+ "\n DB_stProvNm:" + getResultDB.get(j + 3) + "\n DB_effectiveDate:"
								+ getResultDB.get(j + 4) + "\n DB_expirationDate:" + getResultDB.get(j + 5));
					}
					int x = 1;
					boolean eachRecordValidation = false;
					for (int i = 0; i < mergedResponseRows.size(); i++) {
						for (int j = 0; j < mergedResponseRows.size();) {
							String removeCharsMergedResRows = mergedResponseRows.get(i)
									.replaceAll("Response_Geopl_ID:", "").replaceAll("\n Response_OrgStdNm:", "")
									.replaceAll("\n Response_StProvCd:", "").replaceAll("\n Response_stProvNm:", "")
									.replaceAll("\n Response_effectiveDate:", "")
									.replaceAll("\n Response_expirationDate:", "");
							String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("DB_Geopl_ID:", "")
									.replaceAll("\n DB_OrgStdNm:", "").replaceAll("\n DB_StProvCd:", "")
									.replaceAll("\n DB_stProvNm:", "").replaceAll("\n DB_effectiveDate:", "")
									.replaceAll("\n DB_expirationDate:", "");
							if (removeCharsMergedResRows.equals(removeCharsMergedDbRows)) {
								eachRecordValidation = true;
								break;
							} else
								j++;
						}

						if (eachRecordValidation) {
							eachRecordValidation = false;
							test.info("Record " + x + " Validation:");
							test.pass(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.pass(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Pass", "");
							x++;
						} else {
							test.info("Record " + x + " Validation:");
							test.fail(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.fail(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Fail", "");
							x++;
						}
					}

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
			String payload = PostMethod.stProvStdGraphQLRequstWithParam(geopoliticalId, orgStdCd, stProvCd, countryCd,
					targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.pass("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoStProvStdGetQuery = query.stProvStdGraphQLParamQuery(stProvCd, targetDate, endDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdGetMethodDbFieldsJMS();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].organizationStandardCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].organizationStandardCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceName"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
  							String str = js.getString("data.stProvStds[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					List<String> mergedResponseRows = new ArrayList<>();
					List<String> mergedDBRows = new ArrayList<>();
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						mergedResponseRows.add("Response_Geopl_ID:" + getResponseRows.get(j) + "Response_OrgStdNm:"
								+ getResponseRows.get(j + 1) + "Response_StProvCd:" + getResponseRows.get(j + 2)
								+ "Response_stProvNm:" + getResponseRows.get(j + 3) + "Response_effectiveDate:"
								+ getResponseRows.get(j + 4) + "Response_expirationDate:" + getResponseRows.get(j + 5));
						mergedDBRows.add("DB_Geopl_ID:" + getResultDB.get(j) + "DB_OrgStdNm:" + getResultDB.get(j + 1)
								+ "DB_StProvCd:" + getResultDB.get(j + 2) + "DB_stProvNm:" + getResultDB.get(j + 3)
								+ "DB_effectiveDate:" + getResultDB.get(j + 4) + "DB_expirationDate:"
								+ getResultDB.get(j + 5));
					}
					int x = 1;
					boolean eachRecordValidation = false;
					for (int i = 0; i < mergedResponseRows.size(); i++) {
						for (int j = 0; j < mergedResponseRows.size();) {
							String removeCharsMergedResRows = mergedResponseRows.get(i)
									.replaceAll("Response_Geopl_ID:", "").replaceAll("Response_OrgStdNm:", "")
									.replaceAll("Response_StProvCd:", "").replaceAll("Response_stProvNm:", "")
									.replaceAll("Response_effectiveDate:", "")
									.replaceAll("Response_expirationDate:", "");
							String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("DB_Geopl_ID:", "")
									.replaceAll("DB_OrgStdNm:", "").replaceAll("DB_StProvCd:", "")
									.replaceAll("DB_stProvNm:", "").replaceAll("DB_effectiveDate:", "")
									.replaceAll("DB_expirationDate:", "");
							if (removeCharsMergedResRows.equals(removeCharsMergedDbRows)) {
								eachRecordValidation = true;
								break;
							} else
								j++;
						}

						if (eachRecordValidation) {
							eachRecordValidation = false;
							test.info("Record " + x + " Validation:");
							test.pass(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.pass(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Pass", "");
							x++;
						} else {
							test.info("Record " + x + " Validation:");
							test.fail(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.fail(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Fail", "");
							x++;
						}
					}

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
			String payload = PostMethod.stProvStdGraphQLRequstWithoutDates(geopoliticalId, stProvCd, orgStdCd,
					countryCd);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			String stProvCd = js.getString("data.stProvStds[0].stateProvinceCode");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoStProvStdGetQuery = query.stProvStdGraphQLParamQuery(stProvCd, targetDate, endDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
				fields.remove(0);
				fields.remove(0);
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceName"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].expirationDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					List<String> mergedResponseRows = new ArrayList<>();
					List<String> mergedDBRows = new ArrayList<>();
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						mergedResponseRows.add("Response_StProvCd: " + getResponseRows.get(j) + " Response_stProvNm: "
								+ getResponseRows.get(j + 1) + " Response_effectiveDate: " + getResponseRows.get(j + 2)
								+ " Response_expirationDate: " + getResponseRows.get(j + 3));
						mergedDBRows.add("DB_StProvCd: " + getResultDB.get(j) + " DB_stProvNm: "
								+ getResultDB.get(j + 1) + " DB_effectiveDate: " + getResultDB.get(j + 2)
								+ " DB_expirationDate: " + getResultDB.get(j + 3));
					}
					int x = 1;
					boolean eachRecordValidation = false;
					for (int i = 0; i < mergedResponseRows.size(); i++) {
						for (int j = 0; j < mergedResponseRows.size(); j++) {
							String removeCharsMergedResRows = mergedResponseRows.get(i)
									.replaceAll("Response_StProvCd:", "").replaceAll("Response_stProvNm:", "")
									.replaceAll("Response_effectiveDate:", "")
									.replaceAll("Response_expirationDate:", "");
							String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("DB_StProvCd:", "")
									.replaceAll("DB_stProvNm:", "").replaceAll("DB_effectiveDate:", "")
									.replaceAll("DB_expirationDate:", "");
							if (removeCharsMergedResRows.equals(removeCharsMergedDbRows)) {
								eachRecordValidation = true;
								break;
							}
						}

						if (eachRecordValidation) {
							eachRecordValidation = false;
							test.info("Record " + x + " Validation:");
							test.pass(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.pass(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Pass", "");
							x++;
						} else {
							test.info("Record " + x + " Validation:");
							test.fail(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.fail(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Fail", "");
							x++;
						}
					}

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
	public void TC_04() {

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
			String payload = PostMethod.stProvStdGraphQLRequstWithDates(geopoliticalId, targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (targetDate.equalsIgnoreCase("NoTargetDate")) {
					targetDate = currentDate;
				}
				if (endDate.equalsIgnoreCase("NoEndDate")) {
					endDate = "9999-12-31";
				}
				String geoStProvStdGetQuery = query.stProvStdGraphQLGidQuery(geopoliticalId, targetDate, endDate);

				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.stProvStdGetMethodDbFieldsJMS();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName,
						testCaseID);

				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].geopoliticalId"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].geopoliticalId"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].organizationStandardCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].organizationStandardCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceCode"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceCode"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].stateProvinceName"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.stProvStds[" + i + "].stateProvinceName"));
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].effectiveDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].effectiveDate");
							// int index = str.indexOf("T");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
						if (StringUtils.isBlank(js.getString("data.stProvStds[" + i + "].expirationDate"))) {
							getResponseRows.add("");
						} else {
							String str = js.getString("data.stProvStds[" + i + "].expirationDate");
							str = str.substring(0, 10);
							getResponseRows.add(str);
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					List<String> mergedResponseRows = new ArrayList<>();
					List<String> mergedDBRows = new ArrayList<>();
					for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
						mergedResponseRows.add("Response_Geopl_ID:" + getResponseRows.get(j) + "Response_OrgStdNm:"
								+ getResponseRows.get(j + 1) + "Response_StProvCd:" + getResponseRows.get(j + 2)
								+ "Response_stProvNm:" + getResponseRows.get(j + 3) + "Response_effectiveDate:"
								+ getResponseRows.get(j + 4) + "Response_expirationDate:" + getResponseRows.get(j + 5));
						mergedDBRows.add("DB_Geopl_ID:" + getResultDB.get(j) + "DB_OrgStdNm:" + getResultDB.get(j + 1)
								+ "DB_StProvCd:" + getResultDB.get(j + 2) + "DB_stProvNm:" + getResultDB.get(j + 3)
								+ "DB_effectiveDate:" + getResultDB.get(j + 4) + "DB_expirationDate:"
								+ getResultDB.get(j + 5));
					}
					int x = 1;
					boolean eachRecordValidation = false;
					for (int i = 0; i < mergedResponseRows.size(); i++) {
						for (int j = 0; j < mergedResponseRows.size();) {
							String removeCharsMergedResRows = mergedResponseRows.get(i)
									.replaceAll("Response_Geopl_ID:", "").replaceAll("Response_OrgStdNm:", "")
									.replaceAll("Response_StProvCd:", "").replaceAll("Response_stProvNm:", "")
									.replaceAll("Response_effectiveDate:", "")
									.replaceAll("Response_expirationDate:", "");
							String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("DB_Geopl_ID:", "")
									.replaceAll("DB_OrgStdNm:", "").replaceAll("DB_StProvCd:", "")
									.replaceAll("DB_stProvNm:", "").replaceAll("DB_effectiveDate:", "")
									.replaceAll("DB_expirationDate:", "");
							if (removeCharsMergedResRows.equals(removeCharsMergedDbRows)) {
								eachRecordValidation = true;
								break;
							} else
								j++;
						}

						if (eachRecordValidation) {
							eachRecordValidation = false;
							test.info("Record " + x + " Validation:");
							test.pass(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.pass(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Pass", "");
							x++;
						} else {
							test.info("Record " + x + " Validation:");
							test.fail(mergedResponseRows.get(i).replaceAll("\n", "<br />"));
							test.fail(mergedDBRows.get(i).replaceAll("\n", "<br />"));
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									mergedResponseRows.get(i), "Fail", "");
							x++;
						}
					}

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
			String payload = PostMethod.stProvStdGraphQLRequstWithInvalidDate(geopoliticalId, targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (responseRows.size() == 0) {
					String[] inputFieldValues = { geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate };
					String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_stProvCd: ", "Input_orgStdCd: ",
							"Input_countryCd: ", "Input_targetDate: ", "Input_endDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("No record is getting fetched for the given invalid Target and End Date Range");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("No record is getting fetched for the given invalid Target and End Date Range");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geopoliticalRelationshipTypeCd");
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
			String payload = PostMethod.stProvStdGraphQLRequstWithParam(geopoliticalId, orgStdCd, stProvCd, countryCd,
					targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.stProvStds");
			List<String> responseRows = js.get("data.stProvStds");
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if (responseRows.size() == 0) {
					String[] inputFieldValues = { geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate };
					String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_stProvCd: ", "Input_orgStdCd: ",
							"Input_countryCd: ", "Input_targetDate: ", "Input_endDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info("No record is getting fetched for the given invalid Org Std Code");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("No record is getting fetched for the given invalid Org Std Code");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail", "geopoliticalRelationshipTypeCd");
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
			String payload = PostMethod.stProvStdGraphQLRequstWithOneInvalidParam(geopoliticalId, stProvCd, orgStdCd,
					countryCd, targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);

			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage1 = resMsgs.stProvStdOrdStdCdFieldNotPresent;
			String meta = js.getString("meta");
			String internalMsg = js.getString("data.stProvStds");

			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("meta.errors[" + i + "].error"));
				errorMgs2.add(js.getString("meta.errors[" + i + "].message"));

			}

			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if ((errorMgs1.get(0).equals("ValidationError") && errorMgs2.get(0).equals(expectMessage1))) {
					String[] inputFieldValues = { geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate };
					String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_stProvCd: ", "Input_orgStdCd: ",
							"Input_countryCd: ", "Input_targetDate: ", "Input_endDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending the invalid attribute");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending the invalid attribute");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail",
							"geopoliticalRelationshipTypeCd" + expectMessage1);
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
	public void TC_08() {

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
			String payload = PostMethod.stProvStdGraphQLRequstWithAllInvalidParam(geopoliticalId, stProvCd, orgStdCd,
					countryCd, targetDate, endDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);

			String Wsstatus = res.getStatusLine();
			int Wscode = res.statusCode();
			String actualRespVersionNum = js.getString("meta.version");
			String expectMessage1 = resMsgs.stProvStdGeoplIdFieldNotPresent;
			String expectMessage2 = resMsgs.stProvStdOrdStdCdFieldNotPresent;
			String expectMessage3 = resMsgs.stProvStdStProvCdFieldNotPresent;
			String expectMessage4 = resMsgs.stProvStdStProvNmFieldNotPresent;
			String expectMessage5 = resMsgs.stProvStdEffeDtFieldNotPresent;
			String expectMessage6 = resMsgs.stProvStdExpirDtFieldNotPresent;
			String meta = js.getString("meta");
			String internalMsg = js.getString("data.stProvStds");
			int errorMsgLength = js.get("meta.errors.size");
			List<String> errorMgs1 = new ArrayList<String>();
			List<String> errorMgs2 = new ArrayList<String>();
			for (int i = 0; i < errorMsgLength; i++) {
				errorMgs1.add(js.getString("meta.errors[" + i + "].error"));
				errorMgs2.add(js.getString("meta.errors[" + i + "].message"));

			}

			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status code 200 validation passed: " + Wscode);
				test.pass("Response status code 200 validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");

				if ((errorMgs1.get(0).equals("ValidationError") && errorMgs2.get(0).equals(expectMessage1))
						&& (errorMgs1.get(1).equals("ValidationError") && errorMgs2.get(1).equals(expectMessage2))
						&& (errorMgs1.get(2).equals("ValidationError") && errorMgs2.get(2).equals(expectMessage3))
						&& (errorMgs1.get(3).equals("ValidationError") && errorMgs2.get(3).equals(expectMessage4))
						&& (errorMgs1.get(4).equals("ValidationError") && errorMgs2.get(4).equals(expectMessage5))
						&& (errorMgs1.get(5).equals("ValidationError") && errorMgs2.get(5).equals(expectMessage6))) {
					String[] inputFieldValues = { geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate };
					String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_stProvCd: ", "Input_orgStdCd: ",
							"Input_countryCd: ", "Input_targetDate: ", "Input_endDate: " };
					writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
					logger.info(
							"Expected error message is getting received in response when sending all the invalid attribute");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when sending all the invalid attribute");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							Wsstatus, "" + Wscode, responsestr, "Fail",
							"geopoliticalRelationshipTypeCd" + expectMessage1);
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
		geopoliticalId = inputData1.get(testCaseId).get("geopoliticalId");
		stProvCd = inputData1.get(testCaseId).get("stProvCd");
		orgStdCd = inputData1.get(testCaseId).get("orgStdCd");
		countryCd = inputData1.get(testCaseId).get("countryCd");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}

}
