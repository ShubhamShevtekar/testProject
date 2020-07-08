
package scenarios.GEO.v2;

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

public class DayOfWeekGraphQL extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType,dayOfWeekShortName,dayOfWeekFullName,dayOfWeekNumber;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(DayOfWeekGraphQL.class);
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
				String actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level+".graphQL.version");
				actuatorGraphQLversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorGraphQLVersionURL);
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
      
        	//*** payload without parameter
            String payload="{\"query\":\"{  daysOfWeek {      dayOfWeekNumber      dayOfWeekFullName      dayOfWeekShortName      }}\"}";
        	//***send request and get response
            test.info("GraphQL Request:"); 
     	 	test.info(Miscellaneous.jsonFormat(payload));
            Response res = GetResponse.sendGraphQLRequest(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
		     List<String> responseRows = js.get("data.daysOfWeek");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 200 && meta !=null && (!meta.contains("timestamp")) 
	        		&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	  test.pass("Response API version number validation passed"); 
	        	//***get the DB query
	        	String dayOfWeekGetQuery = query.dayOfWeekGetQuery();
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.dayOfWeekGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(dayOfWeekGetQuery, fields, fileName, testCaseID);
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload, "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data.daysOfWeek.dayOfWeekNumber["+i+"]")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.daysOfWeek.dayOfWeekNumber["+i+"]"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.daysOfWeek.dayOfWeekFullName["+i+"]")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.daysOfWeek.dayOfWeekFullName["+i+"]"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.daysOfWeek.dayOfWeekShortName["+i+"]")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.daysOfWeek.dayOfWeekShortName["+i+"]"));
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()))
		        		{
		        			//***write result to excelm
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_dayOfWeekNumber: ", "DB_dayOfWeekNumber: ", 
		        					"Response_dayOfWeekFullName: ", "DB_dayOfWeekFullName: ", "Response_dayOfWeekShortName: ", "DB_dayOfWeekShortName: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, payload,	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			z++;
		        		}else {
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_dayOfWeekNumber: ", "DB_dayOfWeekNumber: ", 
		        					"Response_dayOfWeekFullName: ", "DB_dayOfWeekFullName: ", "Response_dayOfWeekShortName: ", "DB_dayOfWeekShortName: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload,	"", "",
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
							responsestr1, "Fail", "" );
		        	Assert.fail("Test Failed");
		        }
	        }else {
	        	if(Wscode!=200){
	        		logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response status validation failed: "+Wscode);
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }else if(meta.contains("timestamp")){
		        	logger.error("Response validation failed as timestamp found");
		    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    		logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp found");
		           }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)){
	                    logger.error("Response validation failed as API version number is not matching with expected");
                        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                        logger.error("------------------------------------------------------------------");
                                 test.fail("Response validation failed as API version number is not matching with expected");       
               }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "" );
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
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
        	
        	//** without parameter
        	 String payload="{\"query\":\"{  daysOfWeek {      dayOfWeekNumbe      dayOfWeekFullName      dayOfWeekShortName      }}\"}";
            test.info("GraphQL Request:"); 
      	 	test.info(Miscellaneous.jsonFormat(payload));
        	 //***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("GraphQL Response:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			
			String expectMessage = resMsgs.dowNumberErrorMsg;
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        String actualRespVersionNum = js.getString("meta.version"); 
	    	int errorMsgLength = js.get("meta.errors.size");
	    	List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
	        for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("meta.errors["+i+"].error"));
				errorMsg2.add(js.getString("meta.errors["+i+"].message"));
			}
	        if(Wscode == 200 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion))
		    {
	        	logger.info("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	  test.pass("Response API version number validation passed"); 
	        	//***error message validation
				if(errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage))
				{
	        		logger.info("Expected error message is getting received in response when sending the invalid dayOfWeekNumber atrribute in request body");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the invalid dayOfWeekNumber atrribute in request body");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload,	"NA", "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload,"NA", "NA", Wsstatus, ""+Wscode,
							responsestr, "Fail", "ValidationError "+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	if(Wscode!=200){
	        		logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response status validation failed: "+Wscode);
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }else if(meta.contains("timestamp")){
		        	logger.error("Response validation failed as timestamp found");
		    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    		logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp found");
		           }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)){
	                    logger.error("Response validation failed as API version number is not matching with expected");
                        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                        logger.error("------------------------------------------------------------------");
                                 test.fail("Response validation failed as API version number is not matching with expected");       
               }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "" );
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
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
        	
        	//** without parameter
        	 String payload="{\"query\":\"{  daysOfWeek {      dayOfWeekNumbe      dayOfWeekFullNam      dayOfWeekShortNam      }}\"}";
        	 test.info("GraphQL Request:"); 
      	 	 test.info(Miscellaneous.jsonFormat(payload));
        	 //***send request and get response
			Response res = GetResponse.sendGraphQLRequest(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			 test.info("GraphQL Response:");
			 test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
			
			String expectMessage = resMsgs.dowNumberErrorMsg;
			String expectMessage1 = resMsgs.dowFullNamErrorMsg;
			String expectMessage2 = resMsgs.dowShortNamErrorMsg;
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        String actualRespVersionNum = js.getString("meta.version"); 
	    	int errorMsgLength = js.get("meta.errors.size");
	    	List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
	        for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("meta.errors["+i+"].error"));
				errorMsg2.add(js.getString("meta.errors["+i+"].message"));
			
			}
	        if(Wscode == 200 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion))
		    {
	        	logger.info("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	  test.pass("Response API version number validation passed"); 
	        	//***error message validation
				if((errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessage))
						&&(errorMsg1.get(1).equals("ValidationError") && errorMsg2.get(1).equals(expectMessage1))
						&&(errorMsg1.get(2).equals("ValidationError") && errorMsg2.get(2).equals(expectMessage2)))
				{
	        		logger.info("Expected error message is getting received in response when sending the multiple invalid attribute in request body");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the multiple invalid attribute in request body");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload,	"NA", "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, payload,"NA", "NA", Wsstatus, ""+Wscode,
							responsestr, "Fail", "ValidationError "+expectMessage+" ValidationError "+expectMessage1 +" ValidationError "+expectMessage2);
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	if(Wscode!=200){
	        		logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response status validation failed: "+Wscode);
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }else if(meta.contains("timestamp")){
		        	logger.error("Response validation failed as timestamp found");
		    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    		logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp found");
		           }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorGraphQLversion)){
	                    logger.error("Response validation failed as API version number is not matching with expected");
                        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                        logger.error("------------------------------------------------------------------");
                                 test.fail("Response validation failed as API version number is not matching with expected");       
               }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "" );
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
		
	}

}
