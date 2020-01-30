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
import com.fedex.jms.client.reader.JMSReader;

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

public class ScriptGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, scriptCode, scriptName, scriptDesc;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null,writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(ScriptGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		//String actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level+".graphQL.version");
		//actuatorGraphQLversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorGraphQLVersionURL);
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

	@Test(priority=1)
	public void TC_01()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		//***get test case ID with method name

		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLRequestWithoutParameter();
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			String meta = js.getString("meta");
			List<String> responseRows = js.get("data.scripts");
			responseRows.size();
			int Wscode= res.statusCode();
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta != null && (!meta.contains("timestamp")) 
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
				test.pass("Response API version number validation passed");
				//***get the DB query
				String scriptPostQuery = query.scriptGetQuery();
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptGetMethodDbFields();
				fields.size();
				//***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);

				if(getResultDB.size() == responseRows.size()*fields.size())
				{
					logger.info("Total number of records matching between DB & Response: "+responseRows.size());
					test.pass("Total number of records matching between DB & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted , "", "",
							"", ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptCode")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptCode"));
						}
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptDescription"));
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
							String[] responseDbFieldNames = {"Response_scrptCd: ", "DB_scrptCd: ", 
									"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scrptCd: ", "DB_scrptCd: ", 
									"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType,reqFormatted,	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
							responsestr1, "Fail", "" );
					Assert.fail("Test Failed");
				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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

	@Test(priority=2)
	public void TC_02()
	{	
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
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLRequestWithParameter(scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows= js.get("data.scripts");
			int Wscode= res.statusCode();
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
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
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							"", ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptCode")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptCode"));
						}
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptDescription"));
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
							String[] responseDbFieldNames = {"Response_scrptCd: ", "DB_scrptCd: ", 
									"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {
							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
							String[] responseDbFieldNames = {"Response_scrptCd: ", "DB_scrptCd: ", 
									"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
							responsestr1, "Fail", "" );
					Assert.fail("Test Failed");
				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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

	@Test(priority=2)
	public void TC_03()
	{	
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
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLWithParameterWithAttributes(scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String meta = js.getString("meta");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows= js.get("data.scripts");
			int siz= responseRows.size();
			System.out.println("responseRows Size  :  "+siz);
			int Wscode= res.statusCode();
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
				test.pass("Response API version number validation passed");

				//***get the DB query
				String scriptPostQuery = query.scriptPostQuery(scriptCode);
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.scriptGraphQLAttributesMethodDbFields();
				//***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);

				if(getResultDB.size() == responseRows.size()*fields.size())
				{
					logger.info("Total number of records matching between DB & Response: "+responseRows.size());
					test.pass("Total number of records matching between DB & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
							"", ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
					List<String> getResponseRows = new ArrayList<>();
					for(int i=0; i<responseRows.size(); i++)
					{
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptName")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptName"));
						}
						if(StringUtils.isBlank(js.getString("data.scripts["+i+"].scriptDescription")))
						{
							getResponseRows.add("");
						}else {
							getResponseRows.add(js.getString("data.scripts["+i+"].scriptDescription"));
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
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString()};
							String[] responseDbFieldNames = {"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.pass(writableResult.replaceAll("\n", "<br />"));  
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
									"", "", writableResult, "Pass", "" );
							z++;
						}else {

							String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
									getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString()};
							String[] responseDbFieldNames = {"Response_scrptNm: ", "DB_scrptNm: ", "Response_scrptDesc: ", "DB_scrptDesc: "};
							writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
							test.info("Record "+z+" Validation:");
							test.fail(writableResult.replaceAll("\n", "<br />"));  
							logger.info("Record "+z+" Validation:");
							logger.error(writableResult);
							ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "",
									"", "", writableResult, "Fail", "This record is not matching" );
							z++;
						}
					}
				}else {
					logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
							responsestr1, "Fail", "" );
					Assert.fail("Test Failed");
				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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

	@Test(priority=2)
	public void TC_04()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		//***get test case ID with method name

		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLRequestWithParameter(scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			System.out.println("getEndPoinUrl  =   "+ getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			String meta = js.getString("meta");
			String internalMsg = js.getString("meta.message.internalMessage");
			List<String> responseRows= js.get("data.scripts");
			int Wscode= res.statusCode();
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta!=null && (!meta.contains("timestamp")) 
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
				test.pass("Response API version number validation passed");

				//***error message validation
				int data=responseRows.size();
				if(data==0)
				{
					logger.info(" there is no record in DB for invalid script code received in response ");
					logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(" no record found in DB for invalid script code  : " + scriptCode);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "NA",
							Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received as expected in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "" );
					Assert.fail("Test Failed");
				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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

	@Test(priority=2)
	public void TC_05()
	{	
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
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLWith1AtrributeInvalid(scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode= res.statusCode();
			String expectMessage = resMsgs.scriptInvalidAttribute1; 
			int errrorMsgLength= js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for(int i=0;i<errrorMsgLength;i++){
				errorMsg1.add(js.getString("meta.errors["+i+"].error"));
				errorMsg2.add(js.getString("meta.errors["+i+"].message"));
			}
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta!=null && (!meta.contains("timestamp")) 
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
				test.pass("Response API version number validation passed");
				//***get the DB query

				if(errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when passing the invalid any one of the attribute name");
					logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response passing the invalid any one of the attribute name");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "NA",
							"", ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", "", ""+Wscode,
							responsestr, "Fail", "holidayName"+expectMessage );
					Assert.fail("Test Failed");

				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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

	@Test(priority=2)
	public void TC_06()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}

		//***get test case ID with method name

		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.scriptGraphQLWith2AtrributeInvalid(scriptCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: " + getEndPoinUrl);
			test.pass("URI passed: " + getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus= js.getString("meta.message.status");
			String internalMsg = js.getString("meta.message.internalMessage");
			int Wscode= res.statusCode();
			String expectMessage = resMsgs.scriptInvalidAttribute2;
			String expectMessage1 = resMsgs.scriptInvalidAttribute3; 
			int errrorMsgLength= js.get("meta.errors.size");
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for(int i=0;i<errrorMsgLength;i++){
				errorMsg1.add(js.getString("meta.errors["+i+"].error"));
				errorMsg2.add(js.getString("meta.errors["+i+"].message"));
			}
			String actualRespVersionNum = js.getString("meta.version"); 
			if(Wscode == 200 && meta!=null && (!meta.contains("timestamp"))  
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
				logger.info("Response status validation passed: "+Wscode);
				test.pass("Response status validation passed: "+Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed"); 
				test.pass("Response API version number validation passed");	

				if((errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage))
						&& (errorMsg1.get(1).equals("ValidationError") && errorMsg2.get(1).equals(expectMessage1)))
				{

					logger.info("Expected error message is getting received in response when passing the two invalid attribute name");
					logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass("Expected error message is getting received in response passing the two invalid attribute name");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "NA",
							"", ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", "", ""+Wscode,
							responsestr, "Fail", "holidayName"+expectMessage );
					Assert.fail("Test Failed");

				}
			}else {
				if(Wscode!=200){
					logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response status validation failed: "+Wscode);
				}
				else if(meta == null){
					logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as meta not found");
				}else if(meta.contains("timestamp")){
					logger.error("Response validation failed as timestamp found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as timestamp found");
				}else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					logger.error("Response validation failed as API version number is not matching with expected");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
				}
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
				Assert.fail("Test Failed");
			}
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
		userId = inputData1.get(testCaseId).get("UserName");
		scriptCode = inputData1.get(testCaseId).get("scrptCd");
	}

}
