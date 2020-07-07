package scenarios.GEO.v1;

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

public class ScriptGet extends Reporting{

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, scriptCode;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(ScriptGet.class);
	String actuatorQueryVersion;
	TestResultValidation resultValidation = new TestResultValidation();
	
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
		/// *** getting actautor version
				String tokenKey = tokenValues[0];
				String tokenVal = token;
				String actuatorQueryVersionURL=RetrieveEndPoints.getEndPointUrl("queryActuator", fileName, level+".query.version");
				//actuatorQueryVersion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorQueryVersionURL);
				actuatorQueryVersion = "1.0.0";
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException
	{

		runFlag = getExecutionFlag(m.getName(), fileName);
		if(runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}
	
	@Test
	public void TC_01()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".script.get");
			logger.info("URI passed: "+getEndPoinUrl);
			test.pass("URI passed: "+getEndPoinUrl);
			
			//***send request and get response
			Thread.sleep(5000);
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode= res.statusCode();
			String meta =  js.getString("meta"); 
			if(meta!=null)
			{
				test.pass("Response meta validation passed");
			}else{
				test.fail("Response validation failed as meta not found");

			}
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response API version number validation passed");
				//***get the DB query
				String scriptGetQuery = query.scriptGetQuery();
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptGetMethodDbFields();
				//***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptGetQuery, fields, fileName, testCaseID);

				if(getResultDB.size() == responseRows.size()*fields.size())
				{
					logger.info("Total number of records matching between DB & Response: "+responseRows.size());
					test.pass("Total number of records matching between DB & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
							Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptCode")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptCode"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptDescription"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z=1;
					for(int j=0; j<getResultDB.size(); j=j+fields.size())
					{
						if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString())
								&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()))
						{
							//***write result to excel
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
					Assert.fail("Test Failed");
				}
			}else if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");	
			}

			else {
				logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: "+Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: "+e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", ""+e );
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_02()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".script.get");
			getEndPoinUrl = getEndPoinUrl+scriptCode;
			logger.info("URI passed: "+getEndPoinUrl);
			test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode= res.statusCode();
			String meta =  js.getString("meta"); 
			if(meta!=null)
			{
				test.pass("Response meta validation passed");
			}else{
				test.fail("Response validation failed as meta not found");
			}
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response API version number validation passed");
				//***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptGetMethodDbFields();
				//***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);

				if(getResultDB.size() == responseRows.size()*fields.size())
				{
					logger.info("Total number of records matching between DB & Response: "+responseRows.size());
					test.pass("Total number of records matching between DB & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
							Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptCode")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptCode"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptDescription"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z=1;
					for(int j=0; j<getResultDB.size(); j=j+fields.size())
					{
						if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString())
								&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()))
						{
							//***write result to excel
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}

				else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
					Assert.fail("Test Failed");
				}
			}else {
				logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: "+Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: "+e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", ""+e );
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_03()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".script.get");
			getEndPoinUrl = getEndPoinUrl+scriptCode;
			logger.info("URI passed: "+getEndPoinUrl);
			test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode= res.statusCode();
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			String meta =  js.getString("meta"); 
			if(meta!=null)
			{
				test.pass("Response meta validation passed");
			}else{
				test.fail("Response validation failed as meta not found");
			}			
			if(responseRows.size()==0)
			{
				logger.info("As expected total number of records available in response: "+responseRows.size());
				test.pass("As expected total number of records available in response: "+responseRows.size());
				String actualRespVersionNum = js.getString("meta.version"); 
				if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") 
						&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
				{
					logger.info("Response status code 200 validation passed: "+Wscode);
					test.pass("Response status code 200 validation passed: "+Wscode);
					test.pass("Response API version number validation passed");
					//***error message validation
					String expectMessage = resMsgs.getErrorMsg;
					if(internalMsg.equals(expectMessage))
					{
						String[] inputFieldValues = {scriptCode};
						String[] inputFieldNames = {"Input_scriptCode: "};
						writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
						logger.info("Expected error message is getting received in response when passing the invalid scriptCode in URI");
						logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
						logger.info("------------------------------------------------------------------");
						test.pass("Expected error message is getting received in response when passing the invalid scriptCode in URI");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	writableInputFields, "NA",
								Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Expected error message is not getting received in response");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", internalMsg );
						Assert.fail("Test Failed");
					}
				}else if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}else {
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
					Assert.fail("Test Failed");
				}
			}else {
				logger.error("Total number of records are available in response is: "+responseRows.size()+" , instead 0");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Total number of records are available in response is: "+responseRows.size()+" , instead 0");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "Total number of records are available in response is: "+responseRows.size()+" , instead 0" );
				Assert.fail("Test Failed");
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: "+e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", ""+e );
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_04()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".script.get");
			getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 2);
			logger.info("URI passed: "+getEndPoinUrl);
			test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("status");
			String internalMsg = js.getString("error");
			int Wscode= res.statusCode();
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			if(Wsstatus.equals("404"))
			{
				logger.info("Response status code 404 validation passed: "+Wscode);
				test.pass("Response status code 404 validation passed: "+Wscode);
				//***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if(internalMsg.equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when passing the invalid URI");
					logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response when passing the invalid URI");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "NA",
							Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", internalMsg );
					Assert.fail("Test Failed");
				}
			}else {
				logger.error("Response status validation failed: "+Wsstatus);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: "+Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wsstatus,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: "+e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", ""+e );
			Assert.fail("Test Failed");
		}
	}

	@Test
	public void TC_05()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".script.get");
			getEndPoinUrl = getEndPoinUrl+scriptCode;
			logger.info("URI passed: "+getEndPoinUrl);
			test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows = js.get("data");
			int Wscode= res.statusCode();
			String meta =  js.getString("meta"); 
			if(meta!=null)
			{
				test.pass("Response meta validation passed");
			}else{
				test.fail("Response validation failed as meta not found");
			}
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") 
					&& actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response API version number validation passed");
				//***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptGetMethodDbFields();
				//***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);

				if(getResultDB.size() == responseRows.size()*fields.size())
				{
					logger.info("Total number of records matching between DB & Response: "+responseRows.size());
					test.pass("Total number of records matching between DB & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
							Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptCode")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptCode"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data["+i+"].scriptDescription"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z=1;
					for(int j=0; j<getResultDB.size(); j=j+fields.size())
					{
						if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()))
						{
							//***write result to excel
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scriptCode: ", "DB_scriptCode: ", 
									"Response_scriptName: ", "DB_scriptName: ", "Response_scriptDescription: ", "DB_scriptDescription: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
					Assert.fail("Test Failed");
				}
			}else if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
				logger.error("Response validation failed as API version number is not matching with expected");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response validation failed as API version number is not matching with expected");	
			}else {
				logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Response status validation failed: "+Wscode);
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			logger.info("------------------------------------------------------------------");
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: "+e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", ""+e );
			Assert.fail("Test Failed");
		}
	}

	//***get the values from test data sheet
	public void testDataFields(String scenarioName, String testCaseId)
	{
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		try {
			inputData1 = ex.getTestData(scenarioName);
		} catch (IOException e) {
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseId, "", "", "", "", "", "", "", "", "Fail", "Exception: "+e.toString());
			test.fail("Unable to retrieve the test data file/fields");
		}
		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
		scriptCode = inputData1.get(testCaseId).get("scriptCode");
	}

}
