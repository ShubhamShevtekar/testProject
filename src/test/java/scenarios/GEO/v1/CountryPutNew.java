package scenarios.GEO.v1;

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

public class CountryPutNew extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, countryNumericCode, countryCode, threeCharacterCountryCode,
			independentFlag, postalFormatDescription, postalFlag, postalLengthNumber, firstWorkWeekDayName,
			lastWorkWeekDayName, weekendFirstDayName, internetDomainName, dependentRelationshipId, dependentCountryCode,
			countryEffectiveDate, countryExpirationDate, intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr,
			moblPhMaxLthNbr, moblPhMinLthNbr, currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription,
			currenciesEffectiveDate, currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
			geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
			geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
			geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd, cldrVersionNumber,
			cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
			dateShortFormatDescription, localesEffectiveDate, localesExpirationDate, translationGeopoliticalsLanguageCd,
			translationGeopoliticalsScriptCd, translationName, versionNumber, versionDate,
			translationGeopoliticalsEffectiveDate, translationGeopoliticalsExpirationDate, geopoliticalTypeName,
			phoneNumberFormatPattern;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	JMSReader jmsReader = new JMSReader();
	String writableInputFields, writableDB_Fields = null, writableInputAuditFields, writableDBAuditFields = null,
			writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(CountryPutNew.class);
	String actuatorcommandversion;
	Date date = new Date();
	String todaysDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");
	boolean testResult = false;
	TestResultValidation resultValidation = new TestResultValidation();

	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		/*String actuatorCommandeVersionURL = RetrieveEndPoints.getEndPointUrl("commandActuator", fileName,
				level + ".command.version");
		actuatorcommandversion = resultValidation.versionValidation(fileName, tokenKey, tokenVal,
				actuatorCommandeVersionURL);*/
		actuatorcommandversion = "1.0.0";
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {

		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}

	@Test(priority=1)
	public void TC_01()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			auditDBCountBeforeMethod(testCaseID);
			getReqRes = countryPostRequestCreation(testCaseID);
			auditDBCountAfterMethod(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_02(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_03(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_04(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_05()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_06()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_07()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_08()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_09()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_10()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_11()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_12()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_13()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_14()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_15()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_16()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_17()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_18()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_19()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_20(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_21()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_22(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_23(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_24(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_25()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_26(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_27()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_28(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_29(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_30()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_31(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_32(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_33()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_34(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_35(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_36()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_37(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_38(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_39()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_40()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_41()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_42()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_43()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_44()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_45(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_46()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_47(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_48()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_49()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_50()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_51(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_52()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_53(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_54(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_55(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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
	public void TC_56(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_57()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_58()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_59()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_60()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_61()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_62()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_63()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_64()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_65()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_66(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_67()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_68()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_69()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_70()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_71()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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
	public void TC_72(){
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			requiredFieldErrorMethod(testCaseID, js, res, responsestr1, reqFormatted);
		}catch(Exception e){
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

	@Test(priority=2)
	public void TC_73()
	{
		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		/*List<String> getResultDBFinal = new ArrayList<String>();
		List<String> getResultAuditDBFinal = new ArrayList<String>();*/
		List<Boolean> getTestResult = new ArrayList<Boolean>();
		boolean testResult = false;
		//Response res = null;
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try{
			testDataFields(scenarioName, testCaseID);
			ArrayList getReqRes = new ArrayList();
			//ArrayList getTestResult = new ArrayList();
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			getReqRes = countryPostRequestCreation(testCaseID);
			String reqFormatted = (String) getReqRes.get(0);
			Response res = (Response) getReqRes.get(1);
			System.out.println(res);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			int Wscode = res.statusCode();
			if(Wscode == 200){
			String geoplId = js.get("data.geopoliticalId").toString();
			String Wsstatus = js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String actualRespVersionNum = js.getString("meta.version");

			if (Wsstatus.equalsIgnoreCase("SUCCESS") && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");
				test.pass("Response API version number validation passed");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
						"" + Wscode, responsestr1, "Pass", internalMsg);
				testResult = countryDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = currenciesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = uomTypesDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = holidaysDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = affilTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = localeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = translationalGeopoliticalDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				testResult = geopoliticalTypeDBValidation(testCaseID,geoplId,res);
				getTestResult.add(testResult);
				if(getTestResult.contains(false)){
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				}else{
					test.pass("Validation Passed.");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}
			}else {
				if (meta == null) {
					logger.error("Response validation failed as meta not present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not present");
				} else if (timestamp != null) {
					logger.error("Response validation failed as timestamp is present");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp is present");
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
			}else {
					logger.error("Response status validation failed: " + Wscode);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: " + Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", "Response status validation failed");
					test.fail("Validation Failed.");
					test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
					Assert.fail("Test Failed");
				}
		}catch(Exception e){
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

 	public ArrayList countryPostRequestCreation(String testCaseID){

		Response res = null;
		ArrayList getReqRes = new ArrayList();
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			//test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = null;
			if(testCaseID == "TC_53"){
				geopoliticalTypeName = null;
				payload = PostMethod.cntryPostRequestWithNullGeoTypeName(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_54"){
				payload = PostMethod.cntryPostRequestWithoutcountryNumberCd(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_55"){
				payload = PostMethod.cntryPostRequestWithoutCntryCd(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_56"){
				payload = PostMethod.cntryPostRequestWithoutThreeCharCntryCd(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_57"){
				payload = PostMethod.cntryPostRequestWithoutIndependentFlag(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_58"){
				payload = PostMethod.cntryPostRequestWithoutPostalFormatDescription(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_59"){
				payload = PostMethod.cntryPostRequestWithoutPostalFlag(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_60"){
				payload = PostMethod.cntryPostRequestWithoutPostalLengthNumber(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_61"){
				payload = PostMethod.cntryPostRequestWithoutFirstWorkWeekDayName(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_62"){
				payload = PostMethod.cntryPostRequestWithoutWeekendFirstDayName(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_63"){
				payload = PostMethod.cntryPostRequestWithoutInternetDomainName(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_64"){
				payload = PostMethod.cntryPostRequestWithoutDependentRelationshipId(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_65"){
				payload = PostMethod.cntryPostRequestWithoutDependentCountryCd(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_66"){
				payload = PostMethod.cntryPostRequestWithoutIntialDialingCd(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_67"){
				payload = PostMethod.cntryPostRequestWithoutLandPhMaxLthNbr(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_68"){
				payload = PostMethod.cntryPostRequestWithoutlandPhMinLthNbr(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_69"){
				payload = PostMethod.cntryPostRequestWithoutmoblPhMaxLthNbr(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_70"){
				payload = PostMethod.cntryPostRequestWithoutmoblPhMinLthNbr(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_71"){
				payload = PostMethod.cntryPostRequestWithoutphoneNumberFormatPattern(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_72"){
				payload = PostMethod.cntryPostRequestWithoutEffectiveDate(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else if(testCaseID == "TC_73"){
				payload = PostMethod.cntryPostRequestWithoutExpirationDate(userId, countryNumericCode, countryCode,
						threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
						intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
						currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
						currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
						cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
						dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
						localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}else{
				payload = PostMethod.cntryPostRequestNew(userId, countryNumericCode, countryCode,
					threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
					firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
					dependentRelationshipId, dependentCountryCode, countryEffectiveDate, countryExpirationDate,
					intialDialingCd, landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
					currencyNumericCode, currencyCode, minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
					currenciesExpirationDate, uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
					geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
					geopoliticalHolidaysExpirationDate, affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
					geopoliticalAffiliationsExpirationDate, localesLanguageCd, localeCode, localesScriptCd,
					cldrVersionNumber, cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription,
					dateMediumFormatDescription, dateShortFormatDescription, localesEffectiveDate,
					localesExpirationDate, translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
					translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
					translationGeopoliticalsExpirationDate, geopoliticalTypeName, phoneNumberFormatPattern);
			}

			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			getReqRes.add(reqFormatted);
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level + ".cntry.put");
			// ***send request and get response
			res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			getReqRes.add(res);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
		return getReqRes;
	}

	public void auditDBCountBeforeMethod(String testCaseID) {
		// TODO Auto-generated method stub
		// ** get DB data count before put request
		String countryPutCountQuery = query.cntryAuditPutCountQuery(countryCode);
		List<String> fieldsAuditcnt = ValidationFields.auditDBCntFields();
		List<String> getAuditBeforeCntResultDB1 = DbConnect.getResultSetFor(countryPutCountQuery, fieldsAuditcnt,
				fileName, testCaseID);
		int beforePutCountryCount = Integer.parseInt(getAuditBeforeCntResultDB1.get(0));
		test.info("Before put request data count for Country Query: " + beforePutCountryCount);

		/*
		 * String countryDialingsPutCountQuery =
		 * query.cntryCountryDialingsPutAuditCountQuery(intialDialingCd);
		 * List<String> getAuditBeforeCntResultDB2 =
		 * DbConnect.getResultSetFor(countryDialingsPutCountQuery,
		 * fieldsAuditcnt, fileName, testCaseID); int
		 * beforePutCountryDialingsCount =
		 * Integer.parseInt(getAuditBeforeCntResultDB2.get(0)) ; test.info(
		 * "Before put request data count for Country Dialings Query: "
		 * +beforePutCountryDialingsCount);
		 */

		String countryCurrenciesPutCountQuery = query.cntryCurrenciesPutAuditCountQuery(currencyCode);
		List<String> getAuditBeforeCntResultDB3 = DbConnect.getResultSetFor(countryCurrenciesPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryCurrenciesCount = Integer.parseInt(getAuditBeforeCntResultDB3.get(0));
		test.info("Before put request data count for Country Currencies Query: " + beforePutCountryCurrenciesCount);

		String countryGeopoliticalUOMPutCountQuery = query.cntryGeopoliticalUOMPutAuditCountQuery(uomTypeCode);
		List<String> getAuditBeforeCntResultDB4 = DbConnect.getResultSetFor(countryGeopoliticalUOMPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryGeopoliticalUOMCount = Integer.parseInt(getAuditBeforeCntResultDB4.get(0));
		test.info("Before put request data count for Country GeopoliticalUOM Query: "
				+ beforePutCountryGeopoliticalUOMCount);

		String holidayNameFormated;
		if (holidayName.contains("'")) {
			holidayNameFormated = holidayName.replace("'", "''");
		} else {
			holidayNameFormated = holidayName;
		}

		String countryGeopoliticalHolidaysPutCountQuery = query
				.cntryGeopoliticalHolidaysPostAuditCounyQuery(holidayNameFormated);
		List<String> getAuditBeforeCntResultDB5 = DbConnect
				.getResultSetFor(countryGeopoliticalHolidaysPutCountQuery, fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryGeopoliticalHolidaysCount = Integer.parseInt(getAuditBeforeCntResultDB5.get(0));
		test.info("Before put request data count for Country GeopoliticalHolidays Query: "
				+ beforePutCountryGeopoliticalHolidaysCount);

		String countryGeopoliticalAffiliationsPutCountQuery = query
				.cuntryGeopoliticalAffiliationsPutAuditCountQuery(affiliationTypeCd);
		List<String> getAuditBeforeCntResultDB6 = DbConnect.getResultSetFor(
				countryGeopoliticalAffiliationsPutCountQuery, fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryGeopoliticalAffiliationsCount = Integer.parseInt(getAuditBeforeCntResultDB6.get(0));
		test.info("Before put request data count for Country GeopoliticalAffiliations Query: "
				+ beforePutCountryGeopoliticalAffiliationsCount);

		String countryLocalesPutCountQuery = query.cntryLocalesPutAuditCountQuery(localeCode);
		List<String> getAuditBeforeCntResultDB7 = DbConnect.getResultSetFor(countryLocalesPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryLocalesCount = Integer.parseInt(getAuditBeforeCntResultDB7.get(0));
		test.info("Before put request data count for Country Locales Query: " + beforePutCountryLocalesCount);

		String countryTranslationGeopoliticalsPutCountQuery = query
				.cntryTranslationGeopoliticalsPutAuditCountQuery(translationName);
		List<String> getAuditBeforeCntResultDB8 = DbConnect.getResultSetFor(
				countryTranslationGeopoliticalsPutCountQuery, fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryTranslationGeopoliticalsCount = Integer.parseInt(getAuditBeforeCntResultDB8.get(0));
		test.info("Before put request data count for Country TranslationGeopoliticals Query: "
				+ beforePutCountryTranslationGeopoliticalsCount);

		String countryGeopoliticalTypePutCountQuery = query
				.countryGeopoliticalTypePutAuditCountQuery(geopoliticalTypeName);
		List<String> getAuditBeforeCntResultDB9 = DbConnect.getResultSetFor(countryGeopoliticalTypePutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int beforePutCountryGeopoliticalTypeCount = Integer.parseInt(getAuditBeforeCntResultDB9.get(0));
		test.info("Before put request data count for Country GeopoliticalType Query: "
				+ beforePutCountryGeopoliticalTypeCount);

	}

	private void auditDBCountAfterMethod(String testCaseID) {
		// TODO Auto-generated method stub
		// ** get DB data count after put request
		String countryPutCountQuery = query.cntryAuditPutCountQuery(countryCode);
		List<String> fieldsAuditcnt = ValidationFields.auditDBCntFields();
		List<String> getAuditAfterCntResultDB1 = DbConnect.getResultSetFor(countryPutCountQuery, fieldsAuditcnt,
				fileName, testCaseID);
		int afterPutCountryCount = Integer.parseInt(getAuditAfterCntResultDB1.get(0));
		test.info("After put request data count for Country Query: " + afterPutCountryCount);

		/*
		 * List<String> getAuditAfterCntResultDB2 =
		 * DbConnect.getResultSetFor(countryDialingsPutCountQuery,
		 * fieldsAuditcnt, fileName, testCaseID); int
		 * afterPutCountryDialingsCount =
		 * Integer.parseInt(getAuditAfterCntResultDB2.get(0)) ; test.info(
		 * "After put request data count for Country Dialings Query: "
		 * +afterPutCountryDialingsCount);
		 */

		String countryCurrenciesPutCountQuery = query.cntryCurrenciesPutAuditCountQuery(currencyCode);
		List<String> getAuditAfterCntResultDB3 = DbConnect.getResultSetFor(countryCurrenciesPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryCurrenciesCount = Integer.parseInt(getAuditAfterCntResultDB3.get(0));
		test.info("After put request data count for Country Currencies Query: " + afterPutCountryCurrenciesCount);

		String countryGeopoliticalUOMPutCountQuery = query.cntryGeopoliticalUOMPutAuditCountQuery(uomTypeCode);
		List<String> getAuditAfterCntResultDB4 = DbConnect.getResultSetFor(countryGeopoliticalUOMPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryGeopoliticalUOMCount = Integer.parseInt(getAuditAfterCntResultDB4.get(0));
		test.info("After put request data count for Country GeopoliticalUOM Query: "
				+ afterPutCountryGeopoliticalUOMCount);

		String holidayNameFormated;
		if (holidayName.contains("'")) {
			holidayNameFormated = holidayName.replace("'", "''");
		} else {
			holidayNameFormated = holidayName;
		}
		String countryGeopoliticalHolidaysPutCountQuery = query
				.cntryGeopoliticalHolidaysPostAuditCounyQuery(holidayNameFormated);
		List<String> getAuditAfterCntResultDB5 = DbConnect.getResultSetFor(countryGeopoliticalHolidaysPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryGeopoliticalHolidaysCount = Integer.parseInt(getAuditAfterCntResultDB5.get(0));
		test.info("After put request data count for Country GeopoliticalHolidays Query: "
				+ afterPutCountryGeopoliticalHolidaysCount);

		String countryGeopoliticalAffiliationsPutCountQuery = query
				.cuntryGeopoliticalAffiliationsPutAuditCountQuery(affiliationTypeCd);
		List<String> getAuditAfterCntResultDB6 = DbConnect.getResultSetFor(
				countryGeopoliticalAffiliationsPutCountQuery, fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryGeopoliticalAffiliationsCount = Integer.parseInt(getAuditAfterCntResultDB6.get(0));
		test.info("After put request data count for Country GeopoliticalAffiliations Query: "
				+ afterPutCountryGeopoliticalAffiliationsCount);

		String countryLocalesPutCountQuery = query.cntryLocalesPutAuditCountQuery(localeCode);
		List<String> getAuditAfterCntResultDB7 = DbConnect.getResultSetFor(countryLocalesPutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryLocalesCount = Integer.parseInt(getAuditAfterCntResultDB7.get(0));
		test.info("After put request data count for Country Locales Query: " + afterPutCountryLocalesCount);

		String countryTranslationGeopoliticalsPutCountQuery = query
				.cntryTranslationGeopoliticalsPutAuditCountQuery(translationName);
		List<String> getAuditAfterCntResultDB8 = DbConnect.getResultSetFor(
				countryTranslationGeopoliticalsPutCountQuery, fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryTranslationGeopoliticalsCount = Integer.parseInt(getAuditAfterCntResultDB8.get(0));
		test.info("After put request data count for Country TranslationGeopoliticals Query: "
				+ afterPutCountryTranslationGeopoliticalsCount);

		String countryGeopoliticalTypePutCountQuery = query
				.countryGeopoliticalTypePutAuditCountQuery(geopoliticalTypeName);
		List<String> getAuditAfterCntResultDB9 = DbConnect.getResultSetFor(countryGeopoliticalTypePutCountQuery,
				fieldsAuditcnt, fileName, testCaseID);
		int afterPutCountryGeopoliticalTypeCount = Integer.parseInt(getAuditAfterCntResultDB9.get(0));
		test.info("After put request data count for Country GeopoliticalType Query: "
				+ afterPutCountryGeopoliticalTypeCount);

	}

	public boolean countryDBValidation(String testCaseID, String geoplId, Response res) {
		// TODO Auto-generated method stub
		testResult = false;
		if (countryEffectiveDate.isEmpty()) {
			countryEffectiveDate = todaysDate;
		}
		if (countryExpirationDate.isEmpty()) {
			countryExpirationDate = "9999-12-31";
		}
		String formatCountryEffectiveDate = countryEffectiveDate;
		String formatCountryExpirationDate = countryExpirationDate;

		Date dateCountryEffectiveDate = null;
		Date dateCountryExpirationDate = null;

		try {
			dateCountryEffectiveDate = srcDf.parse(formatCountryEffectiveDate);
			dateCountryExpirationDate = srcDf.parse(formatCountryExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatCountryEffectiveDate = destDf.format(dateCountryEffectiveDate);
		formatCountryEffectiveDate = formatCountryEffectiveDate.toUpperCase();

		formatCountryExpirationDate = destDf.format(dateCountryExpirationDate);
		formatCountryExpirationDate = formatCountryExpirationDate.toUpperCase();

		String cntryPostPostQuery1 = query.cntryPostQuery(countryCode, geoplId, formatCountryEffectiveDate,
				formatCountryExpirationDate);
		// ***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.cntryDbFields();
		fields.remove(0);// ***removing user name field since we are
							// going to validate only last updated user
							// name
		// ***get the result from DB
		List<String> getResultDB1 = DbConnect.getResultSetFor(cntryPostPostQuery1, fields, fileName,
				testCaseID);
		//getResultDBFinal.addAll(getResultDB1);
		String[] inputFieldValues = { countryNumericCode, countryCode, threeCharacterCountryCode,
				independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
				firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
				dependentRelationshipId, dependentCountryCode, intialDialingCd, landPhMaxLthNbr,
				landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr, phoneNumberFormatPattern,
				countryEffectiveDate, countryExpirationDate, userId};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Table Validation Starts:");
		test.info("Country Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB1,
				resFields);
		// ***write result to excel
		String[] inputFieldNames = { "Input_countryNumberCd:", "Input_countryCd:",
				"Input_threeCharCountryCd:", "Input_independentFlag:", "Input_postalFormatDescription:",
				"Input_postalFlag:", "Input_postalLengthNumber:", "Input_firstWorkWeekDayName:",
				"Input_lastWorkWeekDayName:", "Input_weekendFirstDayName:", "Input_internetDomainName:",
				"Input_dependentRelationshipId:", "Input_dependentCountryCd:", "Input_intialDialingCd:",
				"Input_landPhMaxLthNbr:", "Input_landPhMinLthNbr:", "Input_moblPhMaxLthNbr:",
				"Input_moblPhMinLthNbr:", "Input_phoneNumberFormatPattern:",
				"Input_countryEffectiveDate:", "Input_countryExpirationDate:",
				"Input_LastUpdateUserName:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_countryNumberCd:", "Db_countryCd:", "Db_threeCharCountryCd:",
				"Db_independentFlag:", "Db_postalFormatDescription:", "Db_postalFlag:",
				"Db_postalLengthNumber:", "Db_firstWorkWeekDayName:", "Db_lastWorkWeekDayName:",
				"Db_weekendFirstDayName:", "Db_internetDomainName:", "Db_dependentRelationshipId:",
				"Db_dependentCountryCd:", "Db_intialDialingCd:", "Db_landPhMaxLthNbr:",
				"Db_landPhMinLthNbr:", "Db_moblPhMaxLthNbr:", "Db_moblPhMinLthNbr:",
				"Db_phoneNumberFormatPattern:", "Db_countryEffectiveDate:", "Db_countryExpirationDate:",
				"Db_LastUpdateUserName:"};

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB1, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Country Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Country Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){

			String cntryPostPostAuditQuery1 = query.cntryPostAuditQuery(countryCode, geoplId,
					formatCountryEffectiveDate, formatCountryExpirationDate);
			// ***get the fields needs to be validate in DB
			List<String> auditFields = ValidationFields.cntryAuditDbFields();
			auditFields.remove(0);// ***removing user name field since we
									// are going to validate only last
									// updated user name
			// ***get the result from DB
			List<String> getResultAuditDB1 = DbConnect.getResultSetFor(cntryPostPostAuditQuery1, auditFields,
					fileName, testCaseID);
			if (getResultAuditDB1.get(16).equals("0")) {
				getResultAuditDB1.set(16, "1");
			}
			String revisionTypeCd = "1";

			String[] inputAuditFieldValues = { countryNumericCode, countryCode,
					threeCharacterCountryCode, independentFlag, postalFormatDescription, postalFlag,
					postalLengthNumber, firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName,
					internetDomainName, dependentRelationshipId, dependentCountryCode, intialDialingCd,
					landPhMaxLthNbr, landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr,
					phoneNumberFormatPattern, countryEffectiveDate, countryExpirationDate, userId,
					revisionTypeCd};

			testResult = false;
			testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
					getResultAuditDB1, resFields);

			if (testResult) {

				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
						"Pass", "");*/
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				String[] inputAuditFieldNames = { "Input_countryNumberCd:", "Input_countryCd:",
						"Input_threeCharCountryCd:", "Input_independentFlag:",
						"Input_postalFormatDescription:", "Input_postalFlag:",
						"Input_postalLengthNumber:", "Input_firstWorkWeekDayName:",
						"Input_lastWorkWeekDayName:", "Input_weekendFirstDayName:",
						"Input_internetDomainName:", "Input_dependentRelationshipId:",
						"Input_dependentCountryCd:", "Input_intialDialingCd:", "Input_landPhMaxLthNbr:",
						"Input_landPhMinLthNbr:", "Input_moblPhMaxLthNbr:", "Input_moblPhMinLthNbr:",
						"Input_phoneNumberFormatPattern:", "Input_countryEffectiveDate:",
						"Input_countryExpirationDate:", "Input_LastUpdateUserName:",
						"Expected_RevisionTypeCd:"};

				writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
						inputAuditFieldNames);

				String[] dbAuditFieldNames = { "Db_countryNumberCd:", "Db_countryCd:",
						"Db_threeCharCountryCd:", "Db_independentFlag:", "Db_postalFormatDescription:",
						"Db_postalFlag:", "Db_postalLengthNumber:", "Db_firstWorkWeekDayName:",
						"Db_lastWorkWeekDayName:", "Db_weekendFirstDayName:", "Db_internetDomainName:",
						"Db_dependentRelationshipId:", "Db_dependentCountryCd:", "Db_intialDialingCd:",
						"Db_landPhMaxLthNbr:", "Db_landPhMinLthNbr:", "Db_moblPhMaxLthNbr:",
						"Db_moblPhMinLthNbr:", "Db_phoneNumberFormatPattern:",
						"Db_countryEffectiveDate:", "Db_countryExpirationDate:",
						"Input_LastUpdateUserName:", "Expected_RevisionTypeCd:"};

				writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB1,
						dbAuditFieldNames);
				test.info("***Audit Table Validation Starts***");
				test.info("Input Data Values:");
				test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
				test.info("DB Audit Table Data Values:");
				test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

				logger.info("Comparison between input data & DB data matching and passed");
				logger.info(
						"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass("Comparison between input data & DB Audit table data matching and passed");
				ex.writeExcel(fileName, testCaseID, "Country Audit Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
						"Audit Table validation");
			}
			else {
				logger.error("Comparison between input data & audit DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & audit DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "Country Audit Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & audit DB data not matching and failed");
				//Assert.fail("Test Failed");
			}
			}//getResultAuditDBFinal.addAll(getResultAuditDB1);
		}else {
			logger.error("Comparison between input data & DB data not matching and failed");
			logger.error("------------------------------------------------------------------");
			test.fail("Comparison between input data & DB data not matching and failed");
			/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
					writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
					"Comparison between input data & DB data not matching and failed");*/
			ex.writeExcel(fileName, testCaseID, "Country Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Fail",
					"Comparison between input data & DB data not matching and failed");
			//Assert.fail("Test Failed");
		}
		return testResult;
	}

	public boolean currenciesDBValidation(String testCaseID, String geoplId, Response res){
		testResult = false;
		if (currenciesEffectiveDate.isEmpty()) {
			currenciesEffectiveDate = todaysDate;
		}
		if (currenciesExpirationDate.isEmpty()) {
			currenciesExpirationDate = "9999-12-31";
		}
		String formatCurrenciesEffectiveDate = currenciesEffectiveDate;
		String formatCurrenciesExpirationDate = currenciesExpirationDate;
		Date dateCurrenciesEffectiveDate = null;
		Date dateCurrenciesExpirationDate = null;
		try {
			dateCurrenciesEffectiveDate = srcDf.parse(formatCurrenciesEffectiveDate);
			dateCurrenciesExpirationDate = srcDf.parse(formatCurrenciesExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatCurrenciesEffectiveDate = destDf.format(dateCurrenciesEffectiveDate);
		formatCurrenciesExpirationDate = destDf.format(dateCurrenciesExpirationDate);

		formatCurrenciesEffectiveDate = formatCurrenciesEffectiveDate.toUpperCase();
		formatCurrenciesExpirationDate = formatCurrenciesExpirationDate.toUpperCase();

		String countryPostQuery3 = query.cntryCurrenciesPostQuery(geoplId, currencyCode, minorUnitCode,
				formatCurrenciesEffectiveDate, formatCurrenciesExpirationDate);
		// ***get the fields needs to be validate in DB
		List<String> fields3 = ValidationFields.cntryCurrenciesDbFields();
		// ***get the result from DB
		List<String> getResultDB3 = DbConnect.getResultSetFor(countryPostQuery3, fields3, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB3);
		String[] inputFieldValues = { currencyNumericCode, currencyCode,
				minorUnitCode, moneyFormatDescription, currenciesEffectiveDate,
				currenciesExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Currencies Table Validation Starts:");
		test.info("Country Currencies Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB3,
				resFields);
		String[] inputFieldNames = { "Input_currencyNumberCd:", "Input_currencyCd:",
				"Input_minorUnitCd:", "Input_moneyFormatDescription:", "Input_currenciesEffectiveDate:",
				"Input_currenciesExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_currencyNumberCd:", "Db_currencyCd:", "Db_minorUnitCd:",
				"Db_moneyFormatDescription:", "Db_currenciesEffectiveDate:",
				"Db_currenciesExpirationDate:"};

		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB3, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Currencies Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Currencies Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
			// ***Audit table DB query****//
			String countryPostAuditQuery3 = query.cntryCurrenciesPostAuditQuery(geoplId, currencyCode,
					minorUnitCode, formatCurrenciesEffectiveDate, formatCurrenciesExpirationDate);
			// ***get the fields needs to be validate in DB
			List<String> auditFields3 = ValidationFields.cntryCurrenciesAuditDbFields();
			// ***get the result from DB
			List<String> getResultAuditDB3 = DbConnect.getResultSetFor(countryPostAuditQuery3, auditFields3,
					fileName, testCaseID);
			if (getResultAuditDB3.get(6).equals("0")) {
				getResultAuditDB3.set(6, "1");
			}
			// ***send the input, response, DB result for validation
			//getResultAuditDBFinal.addAll(getResultAuditDB3);
			String revisionTypeCd = "1";

			String[] inputAuditFieldValues = { currencyNumericCode, currencyCode, minorUnitCode,
					moneyFormatDescription, currenciesEffectiveDate, currenciesExpirationDate,
					revisionTypeCd};
			testResult = false;
			testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
					getResultAuditDB3, resFields);
			if (testResult) {

				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
						"Pass", "");*/
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				String[] inputAuditFieldNames = { "Input_currencyNumberCd:", "Input_currencyCd:",
						"Input_minorUnitCd:", "Input_moneyFormatDescription:",
						"Input_currenciesEffectiveDate:", "Input_currenciesExpirationDate:",
						"Expected_currenciesRevisionTypeCd:"};

				writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
						inputAuditFieldNames);

				String[] dbAuditFieldNames = { "Db_currencyNumberCd:",
						"Db_currencyCd:", "Db_minorUnitCd:", "Db_moneyFormatDescription:",
						"Db_currenciesEffectiveDate:", "Db_currenciesExpirationDate:",
						"Db_currenciesRevisionTypeCd:"};

				writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB3,dbAuditFieldNames);
				test.info("***Audit Table Validation Starts***");
				test.info("Input Data Values:");
				test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
				test.info("DB Audit Table Data Values:");
				test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

				logger.info("Comparison between input data & DB data matching and passed");
				logger.info(
						"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass("Comparison between input data & DB Audit table data matching and passed");
				ex.writeExcel(fileName, testCaseID, "Currencies Audit Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
						"Audit Table validation");
			}else {
				logger.error("Comparison between input data & audit DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				Assert.fail("Test Failed");
			}
			}
		}else {
			logger.error("Comparison between input data & DB data not matching and failed");
			logger.error("------------------------------------------------------------------");
			test.fail("Comparison between input data & DB data not matching and failed");
			/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
					writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
					"Comparison between input data & DB data not matching and failed");*/
			ex.writeExcel(fileName, testCaseID, "Currencies Table Validation", scenarioType, "",
					writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
					"Comparison between input data & DB data not matching and failed");
			Assert.fail("Test Failed");
		}
		return testResult;
	}

	public boolean uomTypesDBValidation(String testCaseID, String geoplId, Response res){
		if (geopoliticalUnitOfMeasuresEffectiveDate.isEmpty()) {
			geopoliticalUnitOfMeasuresEffectiveDate = todaysDate;
		}
		if (geopoliticalUnitOfMeasuresExpirationDate.isEmpty()) {
			geopoliticalUnitOfMeasuresExpirationDate = "9999-12-31";
		}

		String formatGeopoliticalUnitOfMeasuresEffectiveDate = geopoliticalUnitOfMeasuresEffectiveDate;
		String formatGeopoliticalUnitOfMeasuresExpirationDate = geopoliticalUnitOfMeasuresExpirationDate;
		Date dateGeopoliticalUnitOfMeasuresEffectiveDate = null;
		Date dateGeopoliticalUnitOfMeasuresExpirationDate = null;

		try {
			dateGeopoliticalUnitOfMeasuresEffectiveDate = srcDf
					.parse(formatGeopoliticalUnitOfMeasuresEffectiveDate);
			dateGeopoliticalUnitOfMeasuresExpirationDate = srcDf
					.parse(formatGeopoliticalUnitOfMeasuresExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatGeopoliticalUnitOfMeasuresEffectiveDate = destDf
				.format(dateGeopoliticalUnitOfMeasuresEffectiveDate);
		formatGeopoliticalUnitOfMeasuresEffectiveDate = formatGeopoliticalUnitOfMeasuresEffectiveDate
				.toUpperCase();
		formatGeopoliticalUnitOfMeasuresExpirationDate = destDf
				.format(dateGeopoliticalUnitOfMeasuresExpirationDate);
		formatGeopoliticalUnitOfMeasuresExpirationDate = formatGeopoliticalUnitOfMeasuresExpirationDate
				.toUpperCase();

		String countryPostQuery4 = query.cntryGeopoliticalUOMPostQuery(geoplId,
				formatGeopoliticalUnitOfMeasuresEffectiveDate);
		// ***get the fields needs to be validate in DB
		List<String> fields4 = ValidationFields.cntryGeopoliticalUOMDbFields();
		// ***get the result from DB
		List<String> getResultDB4 = DbConnect.getResultSetFor(countryPostQuery4, fields4, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB4);
		String[] inputFieldValues = { uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
				geopoliticalUnitOfMeasuresExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country UoM Type Table Validation Starts:");
		test.info("Country UoM Type Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB4,
				resFields);
		String[] inputFieldNames = { "Input_uomTypeCd:","Input_geopoliticalUnitOfMeasuresEffectiveDate:",
				"Input_geopoliticalUnitOfMeasuresExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_uomTypeCd:","Db_geopoliticalUnitOfMeasuresEffectiveDate:",
				"Db_geopoliticalUnitOfMeasuresExpirationDate:"};
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB4, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if(testResult){
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "UoM Type Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"UoM Type Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if (testCaseID == "TC_01") {
				// ***Audit table DB query****//
				String countryPostAuditQuery4 = query.cntryGeopoliticalUOMPostAuditQuery(geoplId,
						formatGeopoliticalUnitOfMeasuresEffectiveDate);
				// ***get the fields needs to be validate in DB
				List<String> auditFields4 = ValidationFields.cntryGeopoliticalUOMAuditDbFields();
				// ***get the result from DB
				List<String> getResultAuditDB4 = DbConnect.getResultSetFor(countryPostAuditQuery4, auditFields4,
						fileName, testCaseID);
				if (getResultAuditDB4.get(3).equals("0")) {
					getResultAuditDB4.set(3, "1");
				}
				// ***send the input, response, DB result for validation
				//getResultAuditDBFinal.addAll(getResultAuditDB4);
				String revisionTypeCd = "1";

				String[] inputAuditFieldValues = { uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, revisionTypeCd};
				testResult = false;
				testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
						getResultAuditDB4, resFields);
				if (testResult) {

					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
							"Pass", "");*/
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					String[] inputAuditFieldNames = { "Input_uomTypeCd:","Input_geopoliticalUnitOfMeasuresEffectiveDate:",
							"Input_geopoliticalUnitOfMeasuresExpirationDate:","Expected_geopoliticalUnitOfMeasuresRevisionTypeCd:"};

					writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
							inputAuditFieldNames);

					String[] dbAuditFieldNames = { "Db_uomTypeCd:","Db_geopoliticalUnitOfMeasuresEffectiveDate:",
							"Db_geopoliticalUnitOfMeasuresExpirationDate:","Db_geopoliticalUnitOfMeasuresRevisionTypeCd:"};

					writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB4,dbAuditFieldNames);
					test.info("***Audit Table Validation Starts***");
					test.info("Input Data Values:");
					test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
					test.info("DB Audit Table Data Values:");
					test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

					logger.info("Comparison between input data & DB data matching and passed");
					logger.info(
							"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Comparison between input data & DB Audit table data matching and passed");
					ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
							writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
							"Audit Table validation");
				}else {
					logger.error("Comparison between input data & audit DB data not matching and failed");
					logger.error("------------------------------------------------------------------");
					test.fail("Comparison between input data & DB data not matching and failed");
					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
							"Comparison between input data & DB data not matching and failed");*/
					Assert.fail("Test Failed");
				}
			}
			}else {
				logger.error("Comparison between input data & DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "UoM Type Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & DB data not matching and failed");
				Assert.fail("Test Failed");
		}
		return testResult;
	}

	public boolean holidaysDBValidation(String testCaseID, String geoplId, Response res){
		if (geopoliticalHolidaysEffectiveDate.isEmpty()) {
			geopoliticalHolidaysEffectiveDate = todaysDate;
		}
		if (geopoliticalHolidaysExpirationDate.isEmpty()) {
			geopoliticalHolidaysExpirationDate = "9999-12-31";
		}
		String formatGeopoliticalHolidaysEffectiveDate = geopoliticalHolidaysEffectiveDate;
		String formatGeopoliticalHolidaysExpirationDate = geopoliticalHolidaysExpirationDate;
		Date dateGeopoliticalHolidaysEffectiveDate = null;
		Date dateGeopoliticalHolidaysExpirationDate = null;

		try {
			dateGeopoliticalHolidaysEffectiveDate = srcDf.parse(formatGeopoliticalHolidaysEffectiveDate);
			dateGeopoliticalHolidaysExpirationDate = srcDf.parse(formatGeopoliticalHolidaysExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatGeopoliticalHolidaysEffectiveDate = destDf.format(dateGeopoliticalHolidaysEffectiveDate);
		formatGeopoliticalHolidaysEffectiveDate = formatGeopoliticalHolidaysEffectiveDate.toUpperCase();
		formatGeopoliticalHolidaysExpirationDate = destDf.format(dateGeopoliticalHolidaysExpirationDate);
		formatGeopoliticalHolidaysExpirationDate = formatGeopoliticalHolidaysExpirationDate.toUpperCase();

		String countryPostQuery5 = query.cntryGeopoliticalHolidaysPostQuery(geoplId,
				formatGeopoliticalHolidaysEffectiveDate);
		// ***get the fields needs to be validate in DB
		List<String> fields5 = ValidationFields.cntryGeopoliticalHolidaysDbFields();
		// ***get the result from DB
		List<String> getResultDB5 = DbConnect.getResultSetFor(countryPostQuery5, fields5, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB5);
		String[] inputFieldValues = { holidayName, geopoliticalHolidaysEffectiveDate, geopoliticalHolidaysExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Holiday Table Validation Starts:");
		test.info("Country Holiday Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB5,
				resFields);
		String[] inputFieldNames = { "Input_holidayName:", "Input_geopoliticalHolidaysEffectiveDate:",
				"Input_geopoliticalHolidaysExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_holidayName:", "Db_geopoliticalHolidaysEffectiveDate:",
				"Db_geopoliticalHolidaysExpirationDate:"};
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB5, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Holiday Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Holiday Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
			String countryPostAuditQuery5 = query.cntryGeopoliticalHolidaysPostAuditQuery(geoplId,
					formatGeopoliticalHolidaysEffectiveDate);
			// ***get the fields needs to be validate in DB
			List<String> auditFields5 = ValidationFields.cntryGeopoliticalHolidaysAuditDbFields();
			// ***get the result from DB
			List<String> getResultAuditDB5 = DbConnect.getResultSetFor(countryPostAuditQuery5, auditFields5,
					fileName, testCaseID);
			if (getResultAuditDB5.get(3).equals("0")) {
				getResultAuditDB5.set(3, "1");
			}
			// ***send the input, response, DB result for validation
			//getResultAuditDBFinal.addAll(getResultAuditDB5);
			String revisionTypeCd = "1";

			String[] inputAuditFieldValues = { holidayName, geopoliticalHolidaysEffectiveDate,
					geopoliticalHolidaysExpirationDate, revisionTypeCd};
			testResult = false;
			testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
					getResultAuditDB5, resFields);
			if (testResult) {

				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
						"Pass", "");*/
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				String[] inputAuditFieldNames = { "Input_holidayName:", "Input_geopoliticalHolidaysEffectiveDate:",
						"Input_geopoliticalHolidaysExpirationDate:", "Expected_geopoliticalHolidaysRevisionTypeCd:"};

				writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
						inputAuditFieldNames);

				String[] dbAuditFieldNames = { "Db_holidayName:", "Db_geopoliticalHolidaysEffectiveDate:",
						"Db_geopoliticalHolidaysExpirationDate:", "Db_geopoliticalHolidaysRevisionTypeCd:"};

				writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB5,dbAuditFieldNames);
				test.info("***Audit Table Validation Starts***");
				test.info("Input Data Values:");
				test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
				test.info("DB Audit Table Data Values:");
				test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

				logger.info("Comparison between input data & DB data matching and passed");
				logger.info(
						"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass("Comparison between input data & DB Audit table data matching and passed");
				ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
						"Audit Table validation");
			}else {
				logger.error("Comparison between input data & audit DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				Assert.fail("Test Failed");
			}
		}
		}else {
			logger.error("Comparison between input data & DB data not matching and failed");
			logger.error("------------------------------------------------------------------");
			test.fail("Comparison between input data & DB data not matching and failed");
			/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
					writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
					"Comparison between input data & DB data not matching and failed");*/
			ex.writeExcel(fileName, testCaseID, "Holidays Table Validation", scenarioType, "",
					writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
					"Comparison between input data & DB data not matching and failed");
			Assert.fail("Test Failed");
		}
		return testResult;
	}

	public boolean affilTypeDBValidation(String testCaseID, String geoplId, Response res){
		if (geopoliticalAffiliationsEffectiveDate.isEmpty()) {
			geopoliticalAffiliationsEffectiveDate = todaysDate;
		}
		if (geopoliticalAffiliationsExpirationDate.isEmpty()) {
			geopoliticalAffiliationsExpirationDate = "9999-12-31";
		}
		String formatGeopoliticalAffiliationsEffectiveDate = geopoliticalAffiliationsEffectiveDate;
		String formatGeopoliticalAffiliationsExpirationDate = geopoliticalAffiliationsExpirationDate;
		Date dateGeopoliticalAffiliationsEffectiveDate = null;
		Date dateGeopoliticalAffiliationsExpirationDate = null;

		try {
			dateGeopoliticalAffiliationsEffectiveDate = srcDf.parse(formatGeopoliticalAffiliationsEffectiveDate);
			dateGeopoliticalAffiliationsExpirationDate = srcDf.parse(formatGeopoliticalAffiliationsExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatGeopoliticalAffiliationsEffectiveDate = destDf.format(dateGeopoliticalAffiliationsEffectiveDate);
		formatGeopoliticalAffiliationsEffectiveDate = formatGeopoliticalAffiliationsEffectiveDate.toUpperCase();
		formatGeopoliticalAffiliationsExpirationDate = destDf.format(dateGeopoliticalAffiliationsExpirationDate);
		formatGeopoliticalAffiliationsExpirationDate = formatGeopoliticalAffiliationsExpirationDate.toUpperCase();

		String countryPostQuery6 = query.cuntryGeopoliticalAffiliationsPostQuery(geoplId,
				formatGeopoliticalAffiliationsEffectiveDate);
		// ***get the fields needs to be validate in DB
		List<String> fields6 = ValidationFields.cntryGeopoliticalAffiliationsDbFields();
		// ***get the result from DB
		List<String> getResultDB6 = DbConnect.getResultSetFor(countryPostQuery6, fields6, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB6);
		String[] inputFieldValues = { affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
				geopoliticalAffiliationsExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Affiliation Type Table Validation Starts:");
		test.info("Country Affiliation Type Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB6, resFields);
		String[] inputFieldNames = { "Input_affilTypeCd:", "Input_geopoliticalAffiliationsEffectiveDate:",
				"Input_geopoliticalAffiliationsExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_affilTypeCd:", "Db_geopoliticalAffiliationsEffectiveDate:",
				"Db_geopoliticalAffiliationsExpirationDate:"};
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB6, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Affiliation Type Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Affiliation Type Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
				// ***Audit table DB query****//
				String countryPostAuditQuery6 = query.cuntryGeopoliticalAffiliationsPostAuditQuery(geoplId,
						formatGeopoliticalAffiliationsEffectiveDate);
				// ***get the fields needs to be validate in DB
				List<String> auditFields6 = ValidationFields.cntryGeopoliticalAffiliationsAuditDbFields();
				// ***get the result from DB
				List<String> getResultAuditDB6 = DbConnect.getResultSetFor(countryPostAuditQuery6, auditFields6,
						fileName, testCaseID);
				if (getResultAuditDB6.get(3).equals("0")) {
					getResultAuditDB6.set(3, "1");
				}
				// ***send the input, response, DB result for validation
				//getResultAuditDBFinal.addAll(getResultAuditDB6);
				String revisionTypeCd = "1";

				String[] inputAuditFieldValues = { affiliationTypeCd, geopoliticalAffiliationsEffectiveDate,
						geopoliticalAffiliationsExpirationDate, revisionTypeCd};
				testResult = false;
				testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
						getResultAuditDB6, resFields);
				if (testResult) {

					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
							"Pass", "");*/
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					String[] inputAuditFieldNames = { "Input_affilTypeCd:", "Input_geopoliticalAffiliationsEffectiveDate:",
							"Input_geopoliticalAffiliationsExpirationDate:","Expected_geopoliticalAffiliationsRevisionTypeCd:"};

					writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
							inputAuditFieldNames);

					String[] dbAuditFieldNames = { "Db_affilTypeCd:", "Db_geopoliticalAffiliationsEffectiveDate:",
							"Db_geopoliticalAffiliationsExpirationDate:", "Db_geopoliticalAffiliationsRevisionTypeCd:"};

					writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB6, dbAuditFieldNames);
					test.info("***Audit Table Validation Starts***");
					test.info("Input Data Values:");
					test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
					test.info("DB Audit Table Data Values:");
					test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

					logger.info("Comparison between input data & DB data matching and passed");
					logger.info(
							"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Comparison between input data & DB Audit table data matching and passed");
					ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
							writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
							"Audit Table validation");
				}else {
					logger.error("Comparison between input data & audit DB data not matching and failed");
					logger.error("------------------------------------------------------------------");
					test.fail("Comparison between input data & DB data not matching and failed");
					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
							"Comparison between input data & DB data not matching and failed");*/
					Assert.fail("Test Failed");
				}
			}
			}else {
				logger.error("Comparison between input data & DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "Affiliation Type Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & DB data not matching and failed");
				Assert.fail("Test Failed");
			}
		return testResult;
	}

	public boolean localeDBValidation(String testCaseID, String geoplId, Response res){
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

		try {
			dateLocalesEffectiveDate = srcDf.parse(formatLocalesEffectiveDate);
			dateLocalesExpirationDate = srcDf.parse(formatLocalesExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatLocalesEffectiveDate = destDf.format(dateLocalesEffectiveDate);
		formatLocalesEffectiveDate = formatLocalesEffectiveDate.toUpperCase();
		formatLocalesExpirationDate = destDf.format(dateLocalesExpirationDate);
		formatLocalesExpirationDate = formatLocalesExpirationDate.toUpperCase();

		String countryPostQuery7 = query.cntryLocalesPostQuery(geoplId, localeCode, formatLocalesEffectiveDate,
				localesScriptCd);
		// ***get the fields needs to be validate in DB
		List<String> fields7 = ValidationFields.cntryLocalesDbFields();
		// ***get the result from DB
		List<String> getResultDB7 = DbConnect.getResultSetFor(countryPostQuery7, fields7, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB7);
		String[] inputFieldValues = { localesLanguageCd, localeCode, localesScriptCd, cldrVersionNumber, cldrVersionDate,
				dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription, dateShortFormatDescription,
				localesEffectiveDate, localesExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Locale Table Validation Starts:");
		test.info("Country Locale Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB7, resFields);
		String[] inputFieldNames = { "Input_localesLanguageCd:", "Input_localeCd:", "Input_localesScriptCd:",
				"Input_cldrVersionNumber:", "Input_cldrVersionDate:", "Input_dateFullFormatDescription:",
				"Input_dateLongFormatDescription:", "Input_dateMediumFormatDescription:",
				"Input_dateShortFormatDescription:", "Input_localesEffectiveDate:", "Input_localesExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_localesLanguageCd:", "Db_localeCd:", "Db_localesScriptCd:", "Db_cldrVersionNumber:",
				"Db_cldrVersionDate:", "Db_dateFullFormatDescription:", "Db_dateLongFormatDescription:",
				"Db_dateMediumFormatDescription:", "Db_dateShortFormatDescription:",
				"Db_localesEffectiveDate:", "Db_localesExpirationDate:"};
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB7, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Locales Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Locales Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
				// ***Audit table DB query****//
				String countryPostAuditQuery7 = query.cntryLocalesPostAuditQuery(geoplId, localeCode,
						formatLocalesEffectiveDate, localesScriptCd);
				// ***get the fields needs to be validate in DB
				List<String> auditFields7 = ValidationFields.cntryLocalesAuditDbFields();
				// ***get the result from DB
				List<String> getResultAuditDB7 = DbConnect.getResultSetFor(countryPostAuditQuery7, auditFields7,
						fileName, testCaseID);
				if (getResultAuditDB7.get(11).equals("0")) {
					getResultAuditDB7.set(11, "1");
				}
				// ***send the input, response, DB result for validation
				//getResultAuditDBFinal.addAll(getResultAuditDB7);
				String revisionTypeCd = "1";

				String[] inputAuditFieldValues = { localesLanguageCd, localeCode, localesScriptCd, cldrVersionNumber,
						cldrVersionDate, dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
						dateShortFormatDescription, localesEffectiveDate, localesExpirationDate, revisionTypeCd};
				testResult = false;
				testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
						getResultAuditDB7, resFields);
				if (testResult) {

					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
							"Pass", "");*/
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					String[] inputAuditFieldNames = { "Input_localesLanguageCd:",
							"Input_localeCd:", "Input_localesScriptCd:", "Input_cldrVersionNumber:",
							"Input_cldrVersionDate:", "Input_dateFullFormatDescription:",
							"Input_dateLongFormatDescription:", "Input_dateMediumFormatDescription:",
							"Input_dateShortFormatDescription:", "Input_localesEffectiveDate:",
							"Input_localesExpirationDate:", "Expected_localesRevisionTypeCd:"};

					writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
							inputAuditFieldNames);

					String[] dbAuditFieldNames = { "Db_localesLanguageCd:",
							"Db_localeCd:", "Db_localesScriptCd:", "Db_cldrVersionNumber:",
							"Db_cldrVersionDate:", "Db_dateFullFormatDescription:",
							"Db_dateLongFormatDescription:", "Db_dateMediumFormatDescription:",
							"Db_dateShortFormatDescription:", "Db_localesEffectiveDate:",
							"Db_localesExpirationDate:", "Db_localesRevisionTypeCd:"};

					writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB7, dbAuditFieldNames);
					test.info("***Audit Table Validation Starts***");
					test.info("Input Data Values:");
					test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
					test.info("DB Audit Table Data Values:");
					test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

					logger.info("Comparison between input data & DB data matching and passed");
					logger.info(
							"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Comparison between input data & DB Audit table data matching and passed");
					ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
							writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
							"Audit Table validation");
				}else {
					logger.error("Comparison between input data & audit DB data not matching and failed");
					logger.error("------------------------------------------------------------------");
					test.fail("Comparison between input data & DB data not matching and failed");
					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
							"Comparison between input data & DB data not matching and failed");*/
					Assert.fail("Test Failed");
				}
			}
			}else {
				logger.error("Comparison between input data & DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "Locales Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & DB data not matching and failed");
				Assert.fail("Test Failed");
			}
		return testResult;
	}

	public boolean translationalGeopoliticalDBValidation(String testCaseID, String geoplId, Response res){
		if (translationGeopoliticalsEffectiveDate.isEmpty()) {
			translationGeopoliticalsEffectiveDate = todaysDate;
		}
		if (translationGeopoliticalsExpirationDate.isEmpty()) {
			translationGeopoliticalsExpirationDate = "9999-12-31";
		}
		String formatTranslationGeopoliticalsEffectiveDate = translationGeopoliticalsEffectiveDate;
		String formatTranslationGeopoliticalsExpirationDate = translationGeopoliticalsExpirationDate;
		Date dateTranslationGeopoliticalsEffectiveDate = null;
		Date dateTranslationGeopoliticalsExpirationDate = null;

		try {
			dateTranslationGeopoliticalsEffectiveDate = srcDf.parse(formatTranslationGeopoliticalsEffectiveDate);
			dateTranslationGeopoliticalsExpirationDate = srcDf.parse(formatTranslationGeopoliticalsExpirationDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		formatTranslationGeopoliticalsEffectiveDate = destDf.format(dateTranslationGeopoliticalsEffectiveDate);
		formatTranslationGeopoliticalsEffectiveDate = formatTranslationGeopoliticalsEffectiveDate.toUpperCase();
		formatTranslationGeopoliticalsExpirationDate = destDf.format(dateTranslationGeopoliticalsExpirationDate);
		formatTranslationGeopoliticalsExpirationDate = formatTranslationGeopoliticalsExpirationDate.toUpperCase();

		String countryPostQuery8 = query.cntryTranslationGeopoliticalsPostQuery(geoplId,
				translationGeopoliticalsLanguageCd, formatTranslationGeopoliticalsEffectiveDate);
		// ***get the fields needs to be validate in DB
		List<String> fields8 = ValidationFields.cntryTranslationGeopoliticalsDbFields();
		// ***get the result from DB
		List<String> getResultDB8 = DbConnect.getResultSetFor(countryPostQuery8, fields8, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB8);
		String[] inputFieldValues = { translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd, translationName,
				versionNumber, versionDate, translationGeopoliticalsEffectiveDate, translationGeopoliticalsExpirationDate};
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Translation Geopolitical Table Validation Starts:");
		test.info("Country Translation Geopolitical Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB8, resFields);
		String[] inputFieldNames = { "Input_translationGeopoliticalsLanguageCd:","Input_translationGeopoliticalsScriptCd:",
				"Input_translationName:", "Input_versionNumber:", "Input_versionDate:",
				"Input_translationGeopoliticalsEffectiveDate:", "Input_translationGeopoliticalsExpirationDate:"};

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_translationGeopoliticalsLanguageCd:", "Db_translationGeopoliticalsScriptCd:",
				"Db_translationName:", "Db_versionNumber:", "Db_versionDate:",
				"Db_translationGeopoliticalsEffectiveDate:", "Db_translationGeopoliticalsExpirationDate:"};
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB8, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Translation Geo Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Translation Geo Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
				// ***Audit table DB query****//
				String countryPostAuditQuery8 = query.cntryTranslationGeopoliticalsPostAuditQuery(geoplId,
						translationGeopoliticalsLanguageCd, formatTranslationGeopoliticalsEffectiveDate);
				// ***get the fields needs to be validate in DB
				List<String> auditFields8 = ValidationFields.cntryTranslationGeopoliticalsAuditDbFields();
				// ***get the result from DB
				List<String> getResultAuditDB8 = DbConnect.getResultSetFor(countryPostAuditQuery8, auditFields8,
						fileName, testCaseID);
				if (getResultAuditDB8.get(7).equals("0")) {
					getResultAuditDB8.set(7, "1");
				}
				// ***send the input, response, DB result for validation
				//getResultAuditDBFinal.addAll(getResultAuditDB8);
				String revisionTypeCd = "1";

				String[] inputAuditFieldValues = { translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd,
						translationName, versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, revisionTypeCd};
				testResult = false;
				testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
						getResultAuditDB8, resFields);
				if (testResult) {

					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
							"Pass", "");*/
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					String[] inputAuditFieldNames = { "Input_translationGeopoliticalsLanguageCd:",
							"Input_translationGeopoliticalsScriptCd:", "Input_translationName:",
							"Input_versionNumber:", "Input_versionDate:", "Input_translationGeopoliticalsEffectiveDate:",
							"Input_translationGeopoliticalsExpirationDate:",
							"Expected_translationGeopoliticalsRevisionTypeCd:"};

					writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
							inputAuditFieldNames);

					String[] dbAuditFieldNames = { "Db_translationGeopoliticalsLanguageCd:",
							"Db_translationGeopoliticalsScriptCd:", "Db_translationName:",
							"Db_versionNumber:", "Db_versionDate:", "Db_translationGeopoliticalsEffectiveDate:",
							"Db_translationGeopoliticalsExpirationDate:", "Db_translationGeopoliticalsRevisionTypeCd:"};

					writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB8, dbAuditFieldNames);
					test.info("***Audit Table Validation Starts***");
					test.info("Input Data Values:");
					test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
					test.info("DB Audit Table Data Values:");
					test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

					logger.info("Comparison between input data & DB data matching and passed");
					logger.info(
							"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Comparison between input data & DB Audit table data matching and passed");
					ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
							writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
							"Audit Table validation");
				}else {
					logger.error("Comparison between input data & audit DB data not matching and failed");
					logger.error("------------------------------------------------------------------");
					test.fail("Comparison between input data & DB data not matching and failed");
					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
							"Comparison between input data & DB data not matching and failed");*/
					Assert.fail("Test Failed");
				}
			}
			}else {
				logger.error("Comparison between input data & DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "Translation Geo Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & DB data not matching and failed");
				Assert.fail("Test Failed");
			}
		return testResult;
	}

	public boolean geopoliticalTypeDBValidation(String testCaseID, String geoplId, Response res){
		String countryPostQuery9 = query.countryGeopoliticalTypePostQuery(geoplId);
		// ***get the fields needs to be validate in DB
		List<String> fields9 = ValidationFields.cntryGeopoliticalTypeDbFields();
		// ***get the result from DB
		List<String> getResultDB9 = DbConnect.getResultSetFor(countryPostQuery9, fields9, fileName, testCaseID);
		// ***send the input, response, DB result for validation
		//getResultDBFinal.addAll(getResultDB9);
		String[] inputFieldValues = { geopoliticalTypeName };
		List<String> resFields = ValidationFields.langResponseFileds(res);
		logger.info("Country Geopolitical Type Table Validation Starts:");
		test.info("Country Geopolitical Type Table Validation Starts:");
		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB9, resFields);
		String[] inputFieldNames = { "Input_geopoliticalTypeName:" };

		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);

		String[] dbFieldNames = { "Db_geopoliticalTypeName:" };
		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB9, dbFieldNames);
		test.info("Input Data Values:");
		test.info(writableInputFields.replaceAll("\n", "<br />"));
		test.info("DB Data Values:");
		test.info(writableDB_Fields.replaceAll("\n", "<br />"));

		if (testResult) {
			logger.info("Comparison between input data & DB data matching and passed");
			logger.info(
					"Execution is completed for Passed Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			ex.writeExcel(fileName, testCaseID, "Geo Type Table Validation", scenarioType, "",
					writableInputFields, writableDB_Fields, "", "", "", "Pass",
					"Geo Type Table Validation");
			test.pass("Comparison between input data & DB table data matching and passed");
			if(testCaseID == "TC_01"){
				// ***Audit table DB query****//
				String countryPostAuditQuery9 = query.countryGeopoliticalTypePostAuditQuery(geoplId);
				// ***get the fields needs to be validate in DB
				List<String> auditFields9 = ValidationFields.cntryGeopoliticalTypeAuditDbFields();
				// ***get the result from DB
				List<String> getResultAuditDB9 = DbConnect.getResultSetFor(countryPostAuditQuery9, auditFields9,
						fileName, testCaseID);
				if (getResultAuditDB9.get(1).equals("0")) {
					getResultAuditDB9.set(1, "1");
				}
				// ***send the input, response, DB result for validation
				//getResultAuditDBFinal.addAll(getResultAuditDB9);
				String revisionTypeCd = "1";

				String[] inputAuditFieldValues = { geopoliticalTypeName, revisionTypeCd };
				testResult = false;
				testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues,
						getResultAuditDB9, resFields);
				if (testResult) {

					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1,
							"Pass", "");*/
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					String[] inputAuditFieldNames = { "Input_geopoliticalTypeName:", "Expected_geopoliticalTypeRevisionTypeCd:"};

					writableInputAuditFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues,
							inputAuditFieldNames);

					String[] dbAuditFieldNames = { "Db_geopoliticalTypeName:", "Db_geopoliticalTypeRevisionTypeCd:"};

					writableDBAuditFields = Miscellaneous.geoDBFieldNames(getResultAuditDB9, dbAuditFieldNames);
					test.info("***Audit Table Validation Starts***");
					test.info("Input Data Values:");
					test.info(writableInputAuditFields.replaceAll("\n", "<br />"));
					test.info("DB Audit Table Data Values:");
					test.info(writableDBAuditFields.replaceAll("\n", "<br />"));

					logger.info("Comparison between input data & DB data matching and passed");
					logger.info(
							"Execution is completed for Audit Table Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Comparison between input data & DB Audit table data matching and passed");
					ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",
							writableInputAuditFields, writableDBAuditFields, "", "", "", "Pass",
							"Audit Table validation");
				}else {
					logger.error("Comparison between input data & audit DB data not matching and failed");
					logger.error("------------------------------------------------------------------");
					test.fail("Comparison between input data & DB data not matching and failed");
					/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
							"Comparison between input data & DB data not matching and failed");*/
					Assert.fail("Test Failed");
				}
			}
			}else {
				logger.error("Comparison between input data & DB data not matching and failed");
				logger.error("------------------------------------------------------------------");
				test.fail("Comparison between input data & DB data not matching and failed");
				/*ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, writableDB_Fields, Wsstatus, "" + Wscode, responsestr1, "Fail",
						"Comparison between input data & DB data not matching and failed");*/
				ex.writeExcel(fileName, testCaseID, "Geo Type Table Validation", scenarioType, "",
						writableInputAuditFields, writableDBAuditFields, "", "", "", "Fail",
						"Comparison between input data & DB data not matching and failed");
				Assert.fail("Test Failed");
			}
		return testResult;
	}

	public void requiredFieldErrorMethod(String testCaseID, JsonPath js, Response res, String responsestr1,
			String reqFormatted){
		int Wscode = res.statusCode();
		String Wsstatus = res.getStatusLine();
		if(Wscode == 400){
		int errrorMsgLength = js.get("errors.size");
		List<String> errorMsg1 = new ArrayList<String>();
		List<String> errorMsg2 = new ArrayList<String>();
		for (int i = 0; i < errrorMsgLength; i++) {
			errorMsg1.add(js.getString("errors[" + i + "].fieldName"));
			errorMsg2.add(js.getString("errors[" + i + "].message"));
		}
		String expectMessage = resMsgs.requiredFieldMsg;
		String meta = js.getString("meta");
		String timestamp = js.getString("meta.timestamp");
		String actualRespVersionNum = js.getString("meta.version");
		if (meta != null && (!meta.contains("timestamp"))
				&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
			logger.info("Response status code 400 validation passed: " + Wscode);
			test.pass("Response status code 400 validation passed: " + Wscode);
			test.pass("Response meta validation passed");
			test.pass("Response timestamp validation passed");
			test.pass("Response API version number validation passed");

			// ***error message validation

			if (errorMsg1.get(0).equals("countryCode")
					|| errorMsg1.get(0).equals("countryNumericCode")
					|| errorMsg1.get(0).equals("threeCharacterCountryCode")
					|| errorMsg1.get(0).equals("effectiveDate")
					|| errorMsg1.get(0).equals("intialDialingCd")
					|| errorMsg1.get(0).equals("currencyNumericCode")
					|| errorMsg1.get(0).equals("currencyCode")
					|| errorMsg1.get(0).equals("minorUnitCode")
					|| errorMsg1.get(0).equals("uomTypeCode")
					|| errorMsg1.get(0).equals("holidayName")
					|| errorMsg1.get(0).equals("affiliationTypeCode")
					|| errorMsg1.get(0).equals("languageCode")
					|| errorMsg1.get(0).equals("localeCode")
					|| errorMsg1.get(0).equals("geopoliticalTypeName")
					|| errorMsg1.get(0).equals("meta")
					|| errorMsg1.get(0).equals("userName")
					&& errorMsg2.get(0).equals(expectMessage)) {

				String[] inputFieldValues = { countryNumericCode, countryCode, threeCharacterCountryCode,
						independentFlag, postalFormatDescription, postalFlag, postalLengthNumber,
						firstWorkWeekDayName, lastWorkWeekDayName, weekendFirstDayName, internetDomainName,
						dependentRelationshipId, dependentCountryCode, intialDialingCd, landPhMaxLthNbr,
						landPhMinLthNbr, moblPhMaxLthNbr, moblPhMinLthNbr, phoneNumberFormatPattern,
						countryEffectiveDate, countryExpirationDate, userId, currencyNumericCode, currencyCode,
						minorUnitCode, moneyFormatDescription, currenciesEffectiveDate, currenciesExpirationDate,
						uomTypeCode, geopoliticalUnitOfMeasuresEffectiveDate,
						geopoliticalUnitOfMeasuresExpirationDate, holidayName, geopoliticalHolidaysEffectiveDate,
						geopoliticalHolidaysExpirationDate, affiliationTypeCd,
						geopoliticalAffiliationsEffectiveDate, geopoliticalAffiliationsExpirationDate,
						localesLanguageCd, localeCode, localesScriptCd, cldrVersionNumber, cldrVersionDate,
						dateFullFormatDescription, dateLongFormatDescription, dateMediumFormatDescription,
						dateShortFormatDescription, localesEffectiveDate, localesExpirationDate,
						translationGeopoliticalsLanguageCd, translationGeopoliticalsScriptCd, translationName,
						versionNumber, versionDate, translationGeopoliticalsEffectiveDate,
						translationGeopoliticalsExpirationDate, geopoliticalTypeName };

				String[] inputFieldNames = { "Input_countryNumberCd:", "Input_countryCd:",
						"Input_threeCharCountryCd:", "Input_independentFlag:", "Input_postalFormatDescription:",
						"Input_postalFlag:", "Input_postalLengthNumber:", "Input_firstWorkWeekDayName:",
						"Input_lastWorkWeekDayName:", "Input_weekendFirstDayName:", "Input_internetDomainName:",
						"Input_dependentRelationshipId:", "Input_dependentCountryCd:", "Input_intialDialingCd:",
						"Input_landPhMaxLthNbr:", "Input_landPhMinLthNbr:", "Input_moblPhMaxLthNbr:",
						"Input_moblPhMinLthNbr:", "Input_phoneNumberFormatPattern:", "Input_countryEffectiveDate:",
						"Input_countryExpirationDate:", "Input_LastUpdateUserName:", "Input_currencyNumberCd:",
						"Input_currencyCd:", "Input_minorUnitCd:", "Input_moneyFormatDescription:",
						"Input_currenciesEffectiveDate:", "Input_currenciesExpirationDate:", "Input_uomTypeCd:",
						"Input_geopoliticalUnitOfMeasuresEffectiveDate:",
						"Input_geopoliticalUnitOfMeasuresExpirationDate:", "Input_holidayName:",
						"Input_geopoliticalHolidaysEffectiveDate:", "Input_geopoliticalHolidaysExpirationDate:",
						"Input_affilTypeCd:", "Input_geopoliticalAffiliationsEffectiveDate:",
						"Input_geopoliticalAffiliationsExpirationDate:", "Input_localesLanguageCd:",
						"Input_localeCd:", "Input_localesScriptCd:", "Input_cldrVersionNumber:",
						"Input_cldrVersionDate:", "Input_dateFullFormatDescription:",
						"Input_dateLongFormatDescription:", "Input_dateMediumFormatDescription:",
						"Input_dateShortFormatDescription:", "Input_localesEffectiveDate:",
						"Input_localesExpirationDate:", "Input_translationGeopoliticalsLanguageCd:",
						"Input_translationGeopoliticalsScriptCd:", "Input_translationName:", "Input_versionNumber:",
						"Input_versionDate:", "Input_translationGeopoliticalsEffectiveDate:",
						"Input_translationGeopoliticalsExpirationDate:", "Input_geopoliticalTypeName:" };

				writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
				test.info("Input Data Values:");
				test.info(writableInputFields.replaceAll("\n", "<br />"));
				logger.info(
						"Expected error message is getting received in response when sending the blank "
				+errorMsg1.get(0)+" value");
				logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
				logger.info("------------------------------------------------------------------");
				test.pass(
						"Expected error message is getting received in response when sending the blank "
				+errorMsg1.get(0)+" value");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
						writableInputFields, "NA", Wsstatus, "" + Wscode, responsestr1, "Pass",
						"Expected error message is getting received in response when sending the blank "
				+errorMsg1.get(0)+" value");
				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
			} else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
						Wsstatus, "" + Wscode, responsestr1, "Fail", errorMsg1.get(0) + expectMessage);
				test.fail("Validation Failed.");
				test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
				Assert.fail("Test Failed");
			}
		} else {
			if (meta == null) {
				logger.error("Response validation failed as meta not present");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as meta not present");
			} else if (timestamp != null) {
				logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as timestamp is present");
			} else if (!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)) {
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");
			}

			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
					"" + Wscode, responsestr1, "Fail", errorMsg1.get(0) + expectMessage);
			Assert.fail("Test Failed");
		}
		}else {
			logger.error("Response status validation failed: " + Wscode);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Response status validation failed: " + Wscode);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus,
					"" + Wscode, responsestr1, "Fail", "Response status validation failed");
			test.fail("Validation Failed.");
			test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed", ExtentColor.RED));
			Assert.fail("Test Failed");
		}
	}

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
		countryNumericCode = inputData1.get(testCaseId).get("countryNumberCd");
		countryCode = inputData1.get(testCaseId).get("countryCd");
		threeCharacterCountryCode = inputData1.get(testCaseId).get("threeCharCountryCd");
		independentFlag = inputData1.get(testCaseId).get("independentFlag");
		postalFormatDescription = inputData1.get(testCaseId).get("postalFormatDescription");
		postalFlag = inputData1.get(testCaseId).get("postalFlag");
		postalLengthNumber = inputData1.get(testCaseId).get("postalLengthNumber");
		firstWorkWeekDayName = inputData1.get(testCaseId).get("firstWorkWeekDayName");
		lastWorkWeekDayName = inputData1.get(testCaseId).get("lastWorkWeekDayName");
		weekendFirstDayName = inputData1.get(testCaseId).get("weekendFirstDayName");
		internetDomainName = inputData1.get(testCaseId).get("internetDomainName");
		dependentRelationshipId = inputData1.get(testCaseId).get("dependentRelationshipId");
		dependentCountryCode = inputData1.get(testCaseId).get("dependentCountryCd");
		countryEffectiveDate = inputData1.get(testCaseId).get("countryEffectiveDate");
		countryExpirationDate = inputData1.get(testCaseId).get("countryExpirationDate");
		// = inputData1.get(testCaseId).get("");
		intialDialingCd = inputData1.get(testCaseId).get("intialDialingCd");
		landPhMaxLthNbr = inputData1.get(testCaseId).get("landPhMaxLthNbr");
		landPhMinLthNbr = inputData1.get(testCaseId).get("landPhMinLthNbr");
		moblPhMaxLthNbr = inputData1.get(testCaseId).get("moblPhMaxLthNbr");
		moblPhMinLthNbr = inputData1.get(testCaseId).get("moblPhMinLthNbr");
		// countryDialingsEffectiveDate =
		// inputData1.get(testCaseId).get("countryDialingsEffectiveDate");
		// countryDialingsExpirationDate =
		// inputData1.get(testCaseId).get("countryDialingsExpirationDate");
		currencyNumericCode = inputData1.get(testCaseId).get("currencyNumberCd");
		currencyCode = inputData1.get(testCaseId).get("currencyCd");
		minorUnitCode = inputData1.get(testCaseId).get("minorUnitCd");
		moneyFormatDescription = inputData1.get(testCaseId).get("moneyFormatDescription");
		currenciesEffectiveDate = inputData1.get(testCaseId).get("currenciesEffectiveDate");
		currenciesExpirationDate = inputData1.get(testCaseId).get("currenciesExpirationDate");
		uomTypeCode = inputData1.get(testCaseId).get("uomTypeCd");
		geopoliticalUnitOfMeasuresEffectiveDate = inputData1.get(testCaseId)
				.get("geopoliticalUnitOfMeasuresEffectiveDate");
		geopoliticalUnitOfMeasuresExpirationDate = inputData1.get(testCaseId)
				.get("geopoliticalUnitOfMeasuresExpirationDate");
		holidayName = inputData1.get(testCaseId).get("holidayName");
		geopoliticalHolidaysEffectiveDate = inputData1.get(testCaseId).get("geopoliticalHolidaysEffectiveDate");
		geopoliticalHolidaysExpirationDate = inputData1.get(testCaseId).get("geopoliticalHolidaysExpirationDate");
		affiliationTypeCd = inputData1.get(testCaseId).get("affilTypeCd");
		geopoliticalAffiliationsEffectiveDate = inputData1.get(testCaseId).get("geopoliticalAffiliationsEffectiveDate");
		geopoliticalAffiliationsExpirationDate = inputData1.get(testCaseId)
				.get("geopoliticalAffiliationsExpirationDate");
		localesLanguageCd = inputData1.get(testCaseId).get("localesLanguageCd");
		localeCode = inputData1.get(testCaseId).get("localeCd");
		localesScriptCd = inputData1.get(testCaseId).get("localesScriptCd");
		cldrVersionNumber = inputData1.get(testCaseId).get("cldrVersionNumber");
		cldrVersionDate = inputData1.get(testCaseId).get("cldrVersionDate");
		dateFullFormatDescription = inputData1.get(testCaseId).get("dateFullFormatDescription");
		dateLongFormatDescription = inputData1.get(testCaseId).get("dateLongFormatDescription");
		dateMediumFormatDescription = inputData1.get(testCaseId).get("dateMediumFormatDescription");
		dateShortFormatDescription = inputData1.get(testCaseId).get("dateShortFormatDescription");
		localesEffectiveDate = inputData1.get(testCaseId).get("localesEffectiveDate");
		localesExpirationDate = inputData1.get(testCaseId).get("localesExpirationDate");
		translationGeopoliticalsLanguageCd = inputData1.get(testCaseId).get("translationGeopoliticalsLanguageCd");
		translationGeopoliticalsScriptCd = inputData1.get(testCaseId).get("translationGeopoliticalsScriptCd");
		translationName = inputData1.get(testCaseId).get("translationName");
		versionNumber = inputData1.get(testCaseId).get("versionNumber");
		versionDate = inputData1.get(testCaseId).get("versionDate");
		translationGeopoliticalsEffectiveDate = inputData1.get(testCaseId).get("translationGeopoliticalsEffectiveDate");
		translationGeopoliticalsExpirationDate = inputData1.get(testCaseId)
				.get("translationGeopoliticalsExpirationDate");
		geopoliticalTypeName = inputData1.get(testCaseId).get("geopoliticalTypeName");
		phoneNumberFormatPattern = inputData1.get(testCaseId).get("phoneNumberFormatPattern");

	}
}
