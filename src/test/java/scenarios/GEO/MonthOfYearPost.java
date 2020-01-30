package scenarios.GEO;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;
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

public class MonthOfYearPost extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, monthOfYearNumber, monthOfYearShortName;//, token;
	JMSReader jmsReader = new JMSReader();
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	
	String writableInputFields, writableDB_Fields=null,writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(MonthOfYearPost.class);
	String actuatorcommandversion;
	TestResultValidation resultValidation = new TestResultValidation();
	
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
		
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		//String actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level+".command.version");
		//actuatorcommandversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,//actuatorCommandeVersionURL);
		
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
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
	        String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null && (!meta.contains("timestamp")) 
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	
	        	//***get the DB query
	    		String monthOfYearPostQuery = query.monthOfYearPostQuery(monthOfYearNumber);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.monthOfYearDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(monthOfYearPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.monthOfYearNumber")!=null)
	    		{
	    			String monthOfYearNumber1 = js.getString("data.monthOfYearNumber");
	    			//***success message validation
	    			String expectMessage = resMsgs.monthOfYearPostSuccessMsg+monthOfYearNumber1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Month of Year Number is getting received as expected in response");
	    				test.pass("Success message with Month of Year Number is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.monthOfYearResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_monthOfYearNumber: ", "DB_monthOfYearShortName: ", "DB_LastUpdateUserName: "};
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	    			}else {
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Month of year  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Month of year is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
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
	           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
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
	

	@Test(priority=3)
	public void TC_02()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        String actualRespVersionNum = js.getString("meta.version"); 
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when sending the blank MonthOfYear Number");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank MonthOfYear Number");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "orgStdCd"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "orgStdCd"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_03()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			
			String actualRespVersionNum = js.getString("meta.version"); 
			
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearShortName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearShortName"+expectMessage );
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
	           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail",  "monthOfYearShortName"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_04()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestWithoutMeta(monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version"); 
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 

	        	//***error message validation
				if(errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the meta data section is not passed in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the meta data section is not passed in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "meta"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "meta"+expectMessage  );
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
	
	@Test(priority=3)
	public void TC_05()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestWithoutMonthOfYearNumber(userId, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version"); 
			
			String Wsstatus= res.getStatusLine();
	     //   String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the monthOfyearNumber attribute is not passed in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the monthOfyearNumber attribute is not passed in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearNumber"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "monthOfYearNumber"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_06()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequesWithoutmonthOfYearShortName(userId, monthOfYearNumber);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearShortName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearShortName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	if(Wscode!=400){
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
	           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "monthOfYearShortName"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_07()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); String actualRespVersionNum = js.getString("meta.version"); 
			String Wsstatus= res.getStatusLine();
	       // String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp")) && actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");   test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the user name is null or Empty in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the user name is null or Empty in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "userName"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_08()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestmonthOfYearNumberAsNumber(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); String actualRespVersionNum = js.getString("meta.version"); 
			
			String Wsstatus= res.getStatusLine();
	       // String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds38Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        
			 if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp")) && actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");   test.pass("Response API version number validation passed"); 
	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the monthOfyearNumber is more than 38 characters length in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the monthOfyearNumber is more than 38 characters length in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "Error"+ expectMessage);
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "Error"+ expectMessage );
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
	
	@Test(priority=3)
	public void TC_09()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version"); 
			
			
			String Wsstatus= res.getStatusLine();
	       // String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds18Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        
			 if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
					 && actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");   
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("monthOfYearShortName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the monthOfyearShortName  is more than 18 characters length in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the monthOfyearShortName  is more than 18 characters length in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearShortName"+ expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "monthOfYearShortName"+ expectMessage );
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
	
	@Test(priority=3)
	public void TC_10()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version"); 
			
			String Wsstatus= res.getStatusLine();
	       // String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds25Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String 
	        
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
					&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			{
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userName"+  expectMessage);
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail",  "userName"+  expectMessage );
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
	
	
	@Test(priority=3)
	public void TC_11()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			getEndPoinUrl = getEndPoinUrl.substring(0, 97);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("error");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 404 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the URI is not correct");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the URI is not correct");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	logger.error("Response status code 400 validation failed: "+Wscode);
		    	logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status code 400 validation failed: "+Wscode);
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
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
	
	@Test(priority=3)
	public void TC_12()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.recordExistsMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp")) 
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
				if(errorMsg1.get(0).equals("NA") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the user tried to process the same JSON request again");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the user tried to process the same JSON request again");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "Error"+ expectMessage);
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "Error"+ expectMessage );
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
	
	
	@Test(priority=3)
	public void TC_13()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestmonthOfYearNumberAsNumber(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
	        String Wsstatus= js.getString("meta.message.status");
	        String meta =  js.getString("meta");
	        String actualRespVersionNum = js.getString("meta.version"); 
			
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp")) 
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 
	        	//***get the DB query
	    		String monthOfYearPostQuery = query.monthOfYearPostQuery(monthOfYearNumber);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.monthOfYearDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(monthOfYearPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.monthOfYearNumber")!=null)
	    		{
	    			String monthOfYearNumber1 = js.getString("data.monthOfYearNumber");
	    			//***success message validation
	    			String expectMessage = resMsgs.monthOfYearPostSuccessMsg+monthOfYearNumber1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Month of Year Number is getting received as expected in response");
	    				test.pass("Success message with Month of Year Number is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.monthOfYearResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_monthOfYearNumber: ", "DB_monthOfYearShortName: ", "DB_LastUpdateUserName: "};
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	    			}else {
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Month of year  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Month of year is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
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
	
	@Test(priority=3)
	public void TC_14()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			
			String Wsstatus= res.getStatusLine();
	       // String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        
			 if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp")) 
					 && actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("monthOfYearNumber") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the null value is passed in JSON for monthOfyearNumber");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the null value is passed in JSON for monthOfyearNumber");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearNumber"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "monthOfYearNumber"+expectMessage  );
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
	
	@Test(priority=3)
	public void TC_15()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestWithNullMonthOfYearShortName(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp")) 
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");   
	        	test.pass("Response API version number validation passed"); 

	        	//***error message validation
				if(errorMsg1.get(0).equals("monthOfYearShortName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the null value is passed in JSON for monthOfyearShortName");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the null value is passed in JSON for monthOfyearShortName");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "monthOfYearShortName"+expectMessage );
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
	           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", "monthOfYearShortName"+expectMessage );
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
	
	@Test(priority=3)
	public void TC_16()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequestWithoutUserName(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			
			
			String Wsstatus= res.getStatusLine();
	      //  String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			
	        
			 if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))
					 && actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 
	//***error message validation
				
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName};
					String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when the null value is passed in JSON for user name");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the null value is passed in JSON for user name");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userName"+ expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	if(Wscode!=400){
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail",  "userName"+ expectMessage );
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
	
	@Test(priority=3)
	public void TC_17()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.monthOfYearPostRequest(userId, monthOfYearNumber, monthOfYearShortName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".monthOfYear.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta"); 
			String actualRespVersionNum = js.getString("meta.version"); 
			
	        String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        
	        
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))
	        		&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");  
	        	test.pass("Response API version number validation passed"); 
	        	
	        	//***get the DB query
	    		String monthOfYearPostQuery = query.monthOfYearPostQuery(monthOfYearNumber);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.monthOfYearDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(monthOfYearPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.monthOfYearNumber")!=null)
	    		{
	    			String monthOfYearNumber1 = js.getString("data.monthOfYearNumber");
	    			//***success message validation
	    			String expectMessage = resMsgs.monthOfYearPostSuccessMsg+monthOfYearNumber1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Month of Year Number is getting received as expected in response");
	    				test.pass("Success message with Month of Year Number is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, monthOfYearNumber, monthOfYearShortName, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.monthOfYearResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Input_monthOfYearNumber: ", "Input_monthOfYearShortName: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_monthOfYearNumber: ", "DB_monthOfYearShortName: ", "DB_LastUpdateUserName: "};
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	    			}else {
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Month of year  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Month of year is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
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
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
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
	

	@Test(priority=2)
	public void TC_18()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		boolean testResult=false;
		//***get test case ID with method name
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			
			//***get JMS response
			 JSONObject getJMSResult =jmsReader.messageGetsPublished("MONTH_OF_YEAR");
			
 		  if(getJMSResult!=null){
			String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
			test.info("JMS Response Recieved:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			 
			String monthOfYearShortName =   getJMSResult.getJSONObject("data").getString("monthOfYearShortName");
			int  monthOfYearNumber1=getJMSResult.getJSONObject("data").getInt("monthOfYearNumber");
			//***Need to convert monthOfYearNumber1 int value to String
			String monthOfYearNumber= Integer.toString(monthOfYearNumber1);
			
			if(monthOfYearNumber!=null){
				//***get the DB query
				String moyJMSQuery = query.monthOfYearPostQuery(monthOfYearNumber);
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.monthOfYearGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(moyJMSQuery, fields, fileName, testCaseID);
	    		String[] JMSValue = {monthOfYearNumber, monthOfYearShortName};
	    		testResult = TestResultValidation.testValidationForJMS(JMSValue,getResultDB) ;
	    		String[] responseDbFieldValues = {monthOfYearNumber,getResultDB.get(0),monthOfYearShortName,getResultDB.get(1)};
    			String[] responseDbFieldNames = {"Response_monthOfYearNumber: ", "DB_monthOfYearNumber: ", 
    					"Response_monthOfYearShortNamem: ", "DB_monthOfYearShortName: "};
	    		if(testResult){
	    			//***write result to excel
        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	    			logger.info("Comparison between JMS response & DB data matching are passed");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			test.pass(writableResult.replaceAll("\n", "<br />"));  
	    			test.pass("Comparison between JMS response & DB data matching are passed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
        					"", "", writableResult, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	    		}else{
        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	    			logger.error("Comparison between JMS & DB data not matching and failed");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail(writableResult.replaceAll("\n", "<br />")); 
	    			test.fail("Comparison between input data & DB data not matching and failed");
	    			test.fail("Comparison between input data & DB data not matching and failed");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
        					"", "", writableResult, "Fail", "Comparison between JMS & DB data matching are Failed " );
	    			Assert.fail("Test Failed");
	    		}
			}else {
    			logger.error("monthOfYearNumber is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("monthOfYearNumber is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "",
    					"", "", "", "", "", "Fail", "monthOfYearNumber is not available in response" );
    			Assert.fail("Test Failed");
			}
 		  }
 		  else
 		  {
 			 logger.error("Posted request is not reached to JMS queue");
 			//   logger.error("msgSource and DataSegement validation not passed");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				test.fail("Posted request is not reached to JMS queue");
				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "",
						"", "", "", "", "", "Fail", "Posted request is not reached to JMS queue" );
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
		monthOfYearNumber = inputData1.get(testCaseId).get("monthOfYearNumber");
		monthOfYearShortName = inputData1.get(testCaseId).get("monthOfYearShortName");
	}
}
