package scenarios.GEO.v1;

//Testing
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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

public class AddressLabelsGet extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, localeCode, addressLineNumber, geopoliticalId;
	String applicableValue = "";
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(AddressLabelsGet.class);
	String actuatorQueryVersion = "1.0.0";
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		/// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		// String
		// actuatorQueryVersionURL=RetrieveEndPoints.getEndPointUrl("queryActuator",
		// fileName, level+".query.version");
		// actuatorQueryVersion =resultValidation.versionValidation(fileName,
		// tokenKey, tokenVal,actuatorQueryVersionURL);
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			/// getEndPoinUrl=getEndPoinUrl+addresslabels;
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);

			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			HashMap<String, String> data = new HashMap<>();
			Set<String> dataKey = new HashSet<String>();
			data = js.get("data");
			dataKey = data.keySet();
			int n = dataKey.size();
			String strArray[] = new String[n];
			strArray = dataKey.toArray(strArray);
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				for (int k = 0; k < strArray.length; k++) {

					List<String> responseRows = js.get("data." + strArray[k]);
					String AddressLabelsGettQuery = query.AddressLabelsGettQueryAlldata(strArray[k]);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.addressLabelGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(AddressLabelsGettQuery, fields, fileName,
							testCaseID);

					if (getResultDB.size() == responseRows.size() * fields.size()) {
						logger.info("Total number of records matching between DB & Response " + " for LocalCode, "
								+ strArray[k] + " is :" + responseRows.size());
						test.pass("Total number of records matching between DB & Response " + " for LocalCode, "
								+ strArray[k] + " is :" + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
								"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
										+ responseRows.size() + ", below are the test steps for this test case");
						List<String> getResponseRows = new ArrayList<>();
						for (int i = 0; i < responseRows.size(); i++) {
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"));
							}

							if (StringUtils.isBlank(
									js.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js
										.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"));
							}
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {
								getResponseRows
										.add(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"));
							}

							if (StringUtils.isBlank(js.getString("data." + strArray[k] + "[" + i + "].applicable"))) {
								getResponseRows.add("");
							} else {
								applicableValue = js.getString("data." + strArray[k] + "[" + i + "].applicable");
								if (applicableValue.contains("true")) {
									getResponseRows.add("1");
								} else {
									getResponseRows.add("0");
								}
							}
						}
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
						int z = 1;
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {

							if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
									&& getResultDB.get(j + 2).toString()
											.equals(getResponseRows.get(j + 2).toString())) {
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								logger.info("Record " + z + " Validation: Passed");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;
							} else {
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.fail(writableResult.replaceAll("\n", "<br />"));
								logger.info("Record " + z + " Validation: Failed");
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Fail", "This record is not matching");
								z++;
							}

						}
					} else {
						logger.error("Total number of records matching between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
								+ " & Response: " + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
								"" + Wscode, responsestr1, "Fail", internalMsg);
						Assert.fail("Test Failed");
					}

				}
			} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");
			}

			else {
				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId;
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
			HashMap<String, String> data = new HashMap<>();
			Set<String> dataKey = new HashSet<String>();
			data = js.get("data");
			dataKey = data.keySet();
			int n = dataKey.size();

			String strArray[] = new String[n];
			strArray = dataKey.toArray(strArray);
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				for (int k = 0; k < strArray.length; k++) {

					List<String> responseRows = js.get("data." + strArray[k]);
					String AddresslabelQuery = query.AddresslabelQuery(geopoliticalId);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.addressLabelGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(AddresslabelQuery, fields, fileName,
							testCaseID);

					if (getResultDB.size() == responseRows.size() * fields.size()) {
						logger.info("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + " is :" + responseRows.size());
						test.pass("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + " is :" + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
								"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
										+ responseRows.size() + ", below are the test steps for this test case");
						List<String> getResponseRows = new ArrayList<>();
						for (int i = 0; i < responseRows.size(); i++) {
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"));
							}

							if (StringUtils.isBlank(
									js.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js
										.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"));
							}
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {
								getResponseRows
										.add(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"));
							}

							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {

								applicableValue = js.getString("data." + strArray[k] + "[" + i + "].applicable");
								if (applicableValue.contains("true")) {
									getResponseRows.add("1");
								} else {
									getResponseRows.add("0");
								}
							}

						}
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
						int z = 1;
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())

							) {
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
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
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.fail(writableResult.replaceAll("\n", "<br />"));
								logger.info("Record " + z + " Validation:");
								logger.error(writableResult);
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Fail", "This record is not matching");
								z++;
							}
						}
					} else {
						logger.error("Total number of records matching between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
								+ " & Response: " + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
								"" + Wscode, responsestr1, "Fail", internalMsg);
						Assert.fail("Test Failed");
					}
				}
			} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");
			}

			else {
				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId + "/" + localeCode;
			// getEndPoinUrl = getEndPoinUrl+geopoliticalId;
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
			HashMap<String, String> data = new HashMap<>();
			Set<String> dataKey = new HashSet<String>();
			data = js.get("data");
			dataKey = data.keySet();
			int n = dataKey.size();

			String strArray[] = new String[n];
			strArray = dataKey.toArray(strArray);
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				for (int k = 0; k < strArray.length; k++) {

					List<String> responseRows = js.get("data." + strArray[k]);
					String AddresslabelQuery = query.AddresslabelQuery(geopoliticalId);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.addressLabelGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(AddresslabelQuery, fields, fileName,
							testCaseID);

					if (getResultDB.size() == responseRows.size() * fields.size()) {
						logger.info("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + " is :" + responseRows.size());
						test.pass("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + "is :" + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
								"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
										+ responseRows.size() + ", below are the test steps for this test case");
						List<String> getResponseRows = new ArrayList<>();
						for (int i = 0; i < responseRows.size(); i++) {
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"));
							}

							if (StringUtils.isBlank(
									js.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js
										.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"));
							}
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {
								getResponseRows
										.add(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"));
							}

							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {

								applicableValue = js.getString("data." + strArray[k] + "[" + i + "].applicable");
								if (applicableValue.contains("true")) {
									getResponseRows.add("1");
								} else {
									getResponseRows.add("0");
								}
							}

						}
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
						int z = 1;
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())

							) {
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
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
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.fail(writableResult.replaceAll("\n", "<br />"));
								logger.info("Record " + z + " Validation:");
								logger.error(writableResult);
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Fail", "This record is not matching");
								z++;
							}
						}
					} else {
						logger.error("Total number of records matching between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
								+ " & Response: " + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
								"" + Wscode, responsestr1, "Fail", internalMsg);
						Assert.fail("Test Failed");
					}
				}
			} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");
			}

			else {
				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId + "/" + localeCode + "/" + addressLineNumber;
			// getEndPoinUrl = getEndPoinUrl+geopoliticalId;
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
			HashMap<String, String> data = new HashMap<>();
			Set<String> dataKey = new HashSet<String>();
			data = js.get("data");
			dataKey = data.keySet();
			int n = dataKey.size();

			String strArray[] = new String[n];
			strArray = dataKey.toArray(strArray);
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS")
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***get the DB query
				for (int k = 0; k < strArray.length; k++) {

					List<String> responseRows = js.get("data." + strArray[k]);
					String AddresslabelQuery = query.AddresslabelQueryParameter(geopoliticalId, localeCode,
							addressLineNumber);
					// ***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.addressLabelGetMethodDbFields();
					// ***get the result from DB
					List<String> getResultDB = DbConnect.getResultSetFor(AddresslabelQuery, fields, fileName,
							testCaseID);

					if (getResultDB.size() == responseRows.size() * fields.size()) {
						logger.info("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + " is :" + responseRows.size());
						test.pass("Total number of records matching between DB & Response " + " for LocalCode "
								+ strArray[k] + "is :" + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", Wsstatus,
								"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
										+ responseRows.size() + ", below are the test steps for this test case");
						List<String> getResponseRows = new ArrayList<>();
						for (int i = 0; i < responseRows.size(); i++) {
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js.getString("data." + strArray[k] + "[" + i + "].geopoliticalId"));
							}

							if (StringUtils.isBlank(
									js.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"))) {
								getResponseRows.add("");
							} else {
								getResponseRows.add(js
										.getString("data." + strArray[k] + "[" + i + "].brandAddressLineDescription"));
							}
							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {
								getResponseRows
										.add(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"));
							}

							if (StringUtils
									.isBlank(js.getString("data." + strArray[k] + "[" + i + "].addressLineNumber"))) {
								getResponseRows.add("");
							} else {

								applicableValue = js.getString("data." + strArray[k] + "[" + i + "].applicable");
								if (applicableValue.contains("true")) {
									getResponseRows.add("1");
								} else {
									getResponseRows.add("0");
								}
							}

						}
						logger.info("Each record validation starts");
						test.info("Each record validation starts");
						int z = 1;
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())
									&& getResultDB.get(j + 2).toString().equals(getResponseRows.get(j + 2).toString())
									&& getResultDB.get(j + 3).toString().equals(getResponseRows.get(j + 3).toString())

							) {
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString(), getResponseRows.get(j + 2).toString(),
										getResultDB.get(j + 2).toString(), getResponseRows.get(j + 3).toString(),
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
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
										getResultDB.get(j + 3).toString() };
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_brandAddressLineDescription: ", "DB_brandAddressLineDescription: ",
										"Response_addressLineNumber: ", "DB_addressLineNumber:",
										"Response_applicable: ", "DB_applicable: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.fail(writableResult.replaceAll("\n", "<br />"));
								logger.info("Record " + z + " Validation:");
								logger.error(writableResult);
								ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
										"", writableResult, "Fail", "This record is not matching");
								z++;
							}
						}
					} else {
						logger.error("Total number of records matching between DB: "
								+ getResultDB.size() / fields.size() + " & Response: " + responseRows.size());
						logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
								+ " & Response: " + responseRows.size());
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
								"" + Wscode, responsestr1, "Fail", internalMsg);
						Assert.fail("Test Failed");
					}
				}
			} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");
			}

			else {
				logger.error("Response status validation failed: " + Wscode);
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: " + Wscode);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId;
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed:  " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String expectMessage = resMsgs.Invalidgeopolitical;
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}

			if (Wscode == 200 && internalMsg.equalsIgnoreCase(expectMessage)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				test.pass("Response API version number validation passed");
				test.pass("Expected error messages is getting received in response");

				String[] inputFieldValues = { geopoliticalId };
				String[] inputFieldNames = { "Input_geopoliticalId: " };
				writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
				logger.info(
						"Expected error message is getting received in response when passing the invalid geopolitical id in URI");
				logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass(
						"Expected error message is getting received in response when passing the invalid geopolitical id in URI");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", writableInputFields, "NA",
						Wsstatus, "" + Wscode, responsestr1, "Pass", "");
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
			} else {

				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId + "/" + localeCode;
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
			String expectMessage = resMsgs.InvalidgeopoliticalIDAndLocale;
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}

			if (Wscode == 200 && internalMsg.equalsIgnoreCase(expectMessage))

			{
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				test.pass("Expected error messages is getting received in response");

				String[] inputFieldValues = { geopoliticalId, localeCode };
				String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_localeCode" };
				writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
				logger.info(
						"Expected error message is getting received in response when passing the invalid geopoliticalId And Locale CD in URI");
				logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass(
						"Expected error message is getting received in response when passing the invalid geopoliticalId And Locale Cd in URI");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", writableInputFields, "NA",
						Wsstatus, "" + Wscode, responsestr1, "Pass", "");
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
			} else {

				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			getEndPoinUrl = getEndPoinUrl + geopoliticalId + "/" + localeCode + "/" + addressLineNumber;
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
			String expectMessage = resMsgs.InvalidgeopoliticalIDAndLocaleAndAddressLine;
			int Wscode = res.statusCode();
			String meta = js.getString("meta");
			if (meta != null) {
				test.pass("Response meta validation passed");
			} else {
				test.fail("Response validation failed as meta not found");

			}
			if (Wscode == 200 && internalMsg.equalsIgnoreCase(expectMessage)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				test.pass("Expected error messages is getting received in response");

				String[] inputFieldValues = { geopoliticalId, localeCode, addressLineNumber };
				String[] inputFieldNames = { "Input_geopoliticalId: ", "Input_localeCode", "Input_addressLineNumber" };
				writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
				logger.info(
						"Expected error message is getting received in response when passing the invalid geopoliticalId And Locale CD And addressLineNumber in URI");
				logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass(
						"Expected error message is getting received in response when passing the invalid geopoliticalId And Locale Cd and addressLineNumber in URI");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", writableInputFields, "NA",
						Wsstatus, "" + Wscode, responsestr1, "Pass", "");
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
			} else {

				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
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
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength;
			if (StringUtils.isBlank(js.getString("errors.size"))) {
				errorMsgLength = 0;
			} else {
				errorMsgLength = js.getInt("errors.size");
			}

			for (int i = 0; i < errorMsgLength; i++) {

				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			if (Wscode == 404) {
				logger.info("Response status code 404 validation passed: " + Wscode);
				test.pass("Response status code 404 validation passed: " + Wscode);
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.invalidUrlAddreLblGet;
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
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
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response

			Response res = GetResponse.sendRequestPost("", tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);

			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("status");
			String internalMsg = js.getString("error");
			int Wscode = res.statusCode();
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength;
			if (StringUtils.isBlank(js.getString("errors.size"))) {
				errorMsgLength = 0;
			} else {
				errorMsgLength = js.getInt("errors.size");
			}

			for (int i = 0; i < errorMsgLength; i++) {

				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 405 && actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status code 405 validation passed: " + Wscode);
				test.pass("Response status code 405 validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				ValidationFields.timestampValidation(js, res);
				ValidationFields.transactionIdValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.usePOST_Instead_GET;
				if (errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when use GET url but selecting POST method.");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when use GET url but selecting POST method.");
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level + ".AddressLabels.get");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			// ***send request and get response
			Response res = GetResponse.sendRequestGet("", token, getEndPoinUrl, fileName, testCaseID);

			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus = js.getString("status");
			String internalMsg = js.getString("error");
			int Wscode = res.statusCode();
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			int errorMsgLength;
			if (StringUtils.isBlank(js.getString("errors.size"))) {
				errorMsgLength = 0;
			} else {
				errorMsgLength = js.getInt("errors.size");
			}

			for (int i = 0; i < errorMsgLength; i++) {

				errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
				errorMsg2.add(js.getString("errors[" + i + "].message"));
			}
			String actualRespVersionNum = js.getString("meta.version");
			if (Wscode == 401 && actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)) {
				logger.info("Response status code 401 validation passed: " + Wscode);
				test.pass("Response status code 401 validation passed: " + Wscode);
				test.pass("Response API version number validation passed");
				// ValidationFields.transactionIdValidation(js, res);
				ValidationFields.timestampValidation(js, res);
				// ***error message validation
				String expectMessage = resMsgs.missingHeaderTokenGET;
				if (errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage)) {
					logger.info(
							"Expected error message is getting received in response when missing HTTP Header X-CSR-SECURITY_TOKEN. ");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response when missing HTTP Header X-CSR-SECURITY_TOKEN. ");
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

	// Read or get the values from test data sheet
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
		localeCode = inputData1.get(testCaseId).get("localeCode");
		geopoliticalId = inputData1.get(testCaseId).get("geopoliticalId");
		addressLineNumber = inputData1.get(testCaseId).get("addressLineNumber");

	}

}
