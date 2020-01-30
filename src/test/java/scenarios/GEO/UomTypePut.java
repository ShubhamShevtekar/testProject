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

public class UomTypePut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, uomTypeCode, uomTypeName, uomTypeDesc;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null,writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	JMSReader jmsReader = new JMSReader();
	static Logger logger = Logger.getLogger(UomTypePut.class);
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
			String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed");
	        	//***get the DB query
	    		String uomTypePostQuery = query.uomTypePostQuery(uomTypeCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.uomTypeDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypePostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.uomTypeCd")!=null)
	    		{
	    			String uomTypeCode1 = js.getString("data.uomTypeCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.uomTypePutSuccessMsg+uomTypeCode1;
	    			if(internalMsg.equals(expectMessage) && uomTypeCode1.equals(uomTypeCode))
	    			{
	    				logger.info("Success message with Uom Type Code is getting received as expected in response");
	    				test.pass("Success message with Uom Type Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.uomTypeResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UomTypeCode: ", "DB_UomTypeName: ", "DB_UomTypeDesc: ", "DB_LastUpdateUserName: "};
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	    			}else {
	    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("UOM Type CD is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("UOM Type CD is not available in response");
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
		       }else if(timestamp != null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
	public void TC_02()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("uomTypeCd") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd"+expectMessage );
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
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
		       else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
		        	logger.error("Response validation failed as API version number is not matching with expected");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
		        	}
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "uomTypeCd"+expectMessage );
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
	public void TC_03()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("uomTypeNm") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeNm"+expectMessage );
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
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
		        	logger.error("Response validation failed as API version number is not matching with expected");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
		        	}
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "uomTypeNm"+expectMessage );
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
			String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed"); 
	        	//***get the DB query
	    		String uomTypePostQuery = query.uomTypePostQuery(uomTypeCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.uomTypeDbFields();
	    		fields.remove(0);
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypePostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.uomTypeCd")!=null)
	    		{
	    			String uomTypeCode1 = js.getString("data.uomTypeCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.uomTypePutSuccessMsg+uomTypeCode1;
	    			if(internalMsg.equals(expectMessage) && uomTypeCode1.equals(uomTypeCode))
	    			{
	    				logger.info("Success message with Uom Type Code is getting received as expected in response");
	    				test.pass("Success message with Uom Type Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.uomTypeResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UomTypeCode: ", "DB_UomTypeName: ", "DB_UomTypeDesc: ", "DB_LastUpdateUserName: "};
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	    			}else {
	    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("UOM Type CD is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("UOM Type CD is not available in response");
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
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
	public void TC_05()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.uomTypePostRequestWithoutMeta(userId, uomTypeCode, uomTypeName, uomTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
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
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
		        	logger.error("Response validation failed as API version number is not matching with expected");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Response validation failed as API version number is not matching with expected");	
		        	}
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "meta"+expectMessage );
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
		public void TC_06()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequestWithoutUomTypeCd(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.requiredFieldMsg;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("uomTypeCd") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "uomTypeCd"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd"+expectMessage );
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
		public void TC_07()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequestWithoutUomTypeNm(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.requiredFieldMsg;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("uomTypeNm") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "uomTypeNm"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeNm"+expectMessage );
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
				String payload = PostMethod.uomTypePostRequestWithoutUomTypeDesc(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Input Request created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Recieved");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= js.getString("meta.message.status");
		        String internalMsg = js.getString("meta.message.internalMessage");
		        int Wscode= res.statusCode();
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		        {
		        	logger.info("Response status validation passed: "+Wscode);
		        	test.pass("Response status validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***get the DB query
		    		String uomTypePostQuery = query.uomTypePostQuery(uomTypeCode);
		    		//***get the fields needs to be validate in DB
		    		List<String> fields = ValidationFields.uomTypeDbFields();
		    		fields.remove(0);
		    		//***get the result from DB
		    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypePostQuery, fields, fileName, testCaseID);
		    		if(js.getString("data.uomTypeCd")!=null)
		    		{
		    			String uomTypeCode1 = js.getString("data.uomTypeCd");
		    			//***success message validation
		    			String expectMessage = resMsgs.uomTypePutSuccessMsg+uomTypeCode1;
		    			if(internalMsg.equals(expectMessage) && uomTypeCode1.equals(uomTypeCode))
		    			{
		    				logger.info("Success message with Uom Type Code is getting received as expected in response");
		    				test.pass("Success message with Uom Type Code is getting received as expected in response");
		    				//***send the input, response, DB result for validation
		            		String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc, userId};
		            		//***get response fields values
		            		List<String> resFields = ValidationFields.uomTypeResponseFileds(res);
		            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		            		//***write result to excel
		            		String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: ", "Input_LastUpdateUserName: "};
		            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		            		String[] dbFieldNames = {"DB_UomTypeCode: ", "DB_UomTypeName: ", "DB_UomTypeDesc: ", "DB_LastUpdateUserName: "};
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
		        				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
		            		}else{
		            			logger.error("Comparison between input data & DB data not matching and failed");
		            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		            			logger.error("------------------------------------------------------------------");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
		            			Assert.fail("Test Failed");
		            		}
		    			}else {
		    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
		    				logger.error("Success message is not getting received as expected in response");
		    				test.fail("Success message is not getting received as expected in response");
		    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
		        					"Success message is not getting received as expected in response" );
		        			Assert.fail("Test Failed");
		    			}
		    		}else {
		    			logger.error("UOM Type CD is not available in response");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("UOM Type CD is not available in response");
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
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
		public void TC_09()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.requiredFieldMsg;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail",  "userName"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail",  "userName"+expectMessage );
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
		public void TC_10()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.lengthExceeds10Char;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("uomTypeCd") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "uomTypeCd"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd"+expectMessage );
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
		public void TC_11()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.lengthExceeds256Char;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("uomTypeNm") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "uomTypeNm"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeNm"+expectMessage );
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
		public void TC_12()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.lengthExceeds1000Char;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
					if(errorMsg1.get(0).equals("uomTypeDesc") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "uomTypeDesc"+expectMessage );
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeDesc"+expectMessage );
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
		public void TC_13()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.lengthExceeds25Char;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 400 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response status code 400 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	//***error message validation
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
		        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
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
		
		@Test(priority=2)
		public void TC_14()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				getEndPoinUrl=getEndPoinUrl.substring(0, 92);
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String Wsstatus= res.getStatusLine();
		        String internalMsg = js.getString("error");
		        int Wscode= res.statusCode();
		      
		        if(Wscode == 404)
			    {
		        	logger.info("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	//***error message validation
					String expectMessage = resMsgs.invalidUrlMsg;
					if(internalMsg.equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", internalMsg );
			        	Assert.fail("Test Failed");
			        }
			    }else {
			    	logger.error("Response status code 400 validation failed: "+Wscode);
			    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.info("------------------------------------------------------------------");
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
		
		@Test(priority=2)
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
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Input Request created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Recieved");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= js.getString("meta.message.status");
		        String internalMsg = js.getString("meta.message.internalMessage");
		        int Wscode= res.statusCode();
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		        {
		        	logger.info("Response status validation passed: "+Wscode);
		        	test.pass("Response status validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***get the DB query
		    		String uomTypePostQuery = query.uomTypePostQuery(uomTypeCode);
		    		//***get the fields needs to be validate in DB
		    		List<String> fields = ValidationFields.uomTypeDbFields();
		    		//***get the result from DB
		    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypePostQuery, fields, fileName, testCaseID);
		    		if(js.getString("data.uomTypeCd")!=null)
		    		{
		    			String uomTypeCode1 = js.getString("data.uomTypeCd");
		    			//***success message validation
		    			String expectMessage = resMsgs.uomTypePutSuccessMsg+uomTypeCode1;
		    			if(internalMsg.equals(expectMessage) && uomTypeCode1.equals(uomTypeCode))
		    			{
		    				logger.info("Success message with Uom Type Code is getting received as expected in response");
		    				test.pass("Success message with Uom Type Code is getting received as expected in response");
		    				//***send the input, response, DB result for validation
		            		String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc, userId};
		            		//***get response fields values
		            		List<String> resFields = ValidationFields.uomTypeResponseFileds(res);
		            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		            		//***write result to excel
		            		String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: ", "Input_LastUpdateUserName: "};
		            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		            		String[] dbFieldNames = {"DB_UserName: ", "DB_UomTypeCode: ", "DB_UomTypeName: ", "DB_UomTypeDesc: ", "DB_LastUpdateUserName: "};
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
		        				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
		            		}else{
		            			logger.error("Comparison between input data & DB data not matching and failed");
		            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		            			logger.error("------------------------------------------------------------------");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
		            			Assert.fail("Test Failed");
		            		}
		    			}else {
		    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
		    				logger.error("Success message is not getting received as expected in response");
		    				test.fail("Success message is not getting received as expected in response");
		    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
		        					"Success message is not getting received as expected in response" );
		        			Assert.fail("Test Failed");
		    			}
		    		}else {
		    			logger.error("UOM Type CD is not available in response");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("UOM Type CD is not available in response");
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }
			       else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
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
		public void TC_16()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				throw new SkipException("Execution skipped as per test flag set");
			}
			try {
				//***get the test data from sheet
				testDataFields(scenarioName, testCaseID);
				test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
				//***send the data to create request and get request
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Request Created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Received");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= res.getStatusLine();
				int errorMsgLength = js.get("errors.size");
				List<String> errorMsg1 = new ArrayList<>();
				List<String> errorMsg2 = new ArrayList<>();
				for(int i=0; i<errorMsgLength; i++){
					errorMsg1.add(js.getString("errors["+i+"].fieldName"));
					errorMsg2.add(js.getString("errors["+i+"].message"));
				}
		        int Wscode= res.statusCode();
		        String expectMessage = resMsgs.recordNotFoundMsg;
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 404 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
			    {
		        	logger.info("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***error message validation
					if(errorMsg2.get(0).equals(expectMessage))
					{
						String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
						String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
		        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		        		logger.info("Expected error message is getting received in response ");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		        		test.pass("Expected error message is getting received in response when the invalid uomTypeCd (not exist in DB) is passed in JSON ");
		        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
		    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
					}else {
						logger.error("Expected error message is not getting received in response when the invalid uomTypeCd (not exist in DB) is passed in JSON ");
						logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
			        	test.fail("Expected error message is not getting received as expected in response");
			        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
								responsestr, "Fail", "" );
			        	Assert.fail("Test Failed");
			        }
			    }else {
			    	if(Wscode!=404){
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }
			       else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
			        	logger.error("Response validation failed as API version number is not matching with expected");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("Response validation failed as API version number is not matching with expected");	
			        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "" );
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
				String payload = PostMethod.uomTypePostRequest(userId, uomTypeCode, uomTypeName, uomTypeDesc);
				String reqFormatted = Miscellaneous.jsonFormat(payload);
				test.info("Input Request created:");
				logger.info("Input Request created");
				test.info(reqFormatted.replaceAll("\n", "<br />"));
				//***get end point url
				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".uomType.put");
				//***send request and get response
				Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
				String responsestr=res.asString(); 
				String responsestr1 = Miscellaneous.jsonFormat(responsestr);
				test.info("Response Recieved:");
				logger.info("Response Recieved");
				test.info(responsestr1.replaceAll("\n", "<br />"));
				JsonPath js = new JsonPath(responsestr);
				String meta =  js.getString("meta");
				String Wsstatus= js.getString("meta.message.status");
		        String internalMsg = js.getString("meta.message.internalMessage");
		        int Wscode= res.statusCode();
		        String actualRespVersionNum = js.getString("meta.version"); 
		        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		        {
		        	logger.info("Response status validation passed: "+Wscode);
		        	test.pass("Response status validation passed: "+Wscode);
		        	test.pass("Response meta validation passed");
		        	test.pass("Response timestamp validation passed");
		        	test.pass("Response API version number validation passed"); 
		        	//***get the DB query
		    		String uomTypePostQuery = query.uomTypePostQuery(uomTypeCode);
		    		//***get the fields needs to be validate in DB
		    		List<String> fields = ValidationFields.uomTypeDbFields();
		    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
		    		//***get the result from DB
		    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypePostQuery, fields, fileName, testCaseID);
		    		if(js.getString("data.uomTypeCd")!=null)
		    		{
		    			String uomTypeCode1 = js.getString("data.uomTypeCd");
		    			//***success message validation
		    			String expectMessage = resMsgs.uomTypePutSuccessMsg+uomTypeCode1;
		    			if(internalMsg.equals(expectMessage) && uomTypeCode1.equals(uomTypeCode))
		    			{
		    				logger.info("Success message with Uom Type Code is getting received as expected in response");
		    				test.pass("Success message with Uom Type Code is getting received as expected in response");
		    				//***send the input, response, DB result for validation
		            		String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc, userId};
		            		//***get response fields values
		            		List<String> resFields = ValidationFields.uomTypeResponseFileds(res);
		            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
		            		//***write result to excel
		            		String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: ", "Input_LastUpdateUserName: "};
		            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
		            		String[] dbFieldNames = {"DB_UomTypeCode: ", "DB_UomTypeName: ", "DB_UomTypeDesc: ", "DB_LastUpdateUserName: "};
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
		        				test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
		            		}else{
		            			logger.error("Comparison between input data & DB data not matching and failed");
		            			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		            			logger.error("------------------------------------------------------------------");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			test.fail("Comparison between input data & DB data not matching and failed");
		            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
		            			Assert.fail("Test Failed");
		            		}
		    			}else {
		    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
		    				logger.error("Success message is not getting received as expected in response");
		    				test.fail("Success message is not getting received as expected in response");
		    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
		        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
		        					"Success message is not getting received as expected in response" );
		        			Assert.fail("Test Failed");
		    			}
		    		}else {
		    			logger.error("UOM Type CD is not available in response");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
						test.fail("UOM Type CD is not available in response");
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
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }
			       else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
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
	
	
		@Test(priority=1)
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
				
				JSONObject getJMSResult =jmsReader.messageGetsPublished("UOM");
				if(getJMSResult!=null)
				{
					String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
				    test.info("JMS Response Received:");
				    test.info(reqFormatted.replaceAll("\n", "<br />"));
					//String scrptCd=getJMSResult.getString("scrptCd"); 
				    String uomTypeNm=getJMSResult.getJSONObject("data").getString("uomTypeNm");
					String uomTypeDesc=getJMSResult.getJSONObject("data").getString("uomTypeDesc");
					String uomTypeCd=getJMSResult.getJSONObject("data").getString("uomTypeCd");

				if(uomTypeCd!=null){
					//***get the DB query
					String uomTypeJMSQuery = query.uomTypePostQuery(uomTypeCd);
					//***get the fields needs to be validate in DB
					List<String> fields = ValidationFields.uomTypeGetMethodDbFields();
		    		//***get the result from DB
		    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypeJMSQuery, fields, fileName, testCaseID);
		    		String[] JMSValue = {uomTypeCd, uomTypeNm, uomTypeDesc};
		    		testResult = TestResultValidation.testValidationForJMS(JMSValue,getResultDB) ;
		    		
		    		if(testResult){
		    			//***write result to excel
	        			String[] responseDbFieldValues = {uomTypeCd,getResultDB.get(0),uomTypeNm,getResultDB.get(1),uomTypeDesc,getResultDB.get(2)};
	        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
	        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);

		    			logger.info("Comparison between JMS response & DB data matching and passed");
		    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
		    			logger.info("------------------------------------------------------------------");
		    			test.pass("Comparison between JMS response & DB data matching and passed");
		    			test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	        					"", "", writableResult, "Pass", "" );
						test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
		    		}else{
		    			String[] responseDbFieldValues = {uomTypeCd,getResultDB.get(0),uomTypeNm,getResultDB.get(1),uomTypeDesc,getResultDB.get(2)};
	        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
	        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		    			logger.error("Comparison between JMS & DB data not matching and failed");
		    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
		    			test.fail("Comparison between input data & DB data not matching and failed");
		    			test.fail("Comparison between input data & DB data not matching and failed");
		    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	        					"", "", writableResult, "Fail", "Comparison between JMS & DB data not matching and failed" );
		    			Assert.fail("Test Failed");
		    		}
				}else {
	    			logger.error("UOM Type CD is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("UOM Type CD is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "",
	    					"", "", "", "", "", "Fail", "" );
	    			Assert.fail("Test Failed");
				}
			}
				else
				{
						logger.error("Posted request is not reached to JMS queue");
						logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
						logger.error("------------------------------------------------------------------");
						test.fail("Posted request is not reached to JMS queue");
						ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "",
								"", "", "", "", "", "Fail", "" );
						Assert.fail("Test Failed");
				  }
			}
				catch (Exception e) {
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
		uomTypeCode = inputData1.get(testCaseId).get("uomTypeCd");
		uomTypeName = inputData1.get(testCaseId).get("uomTypeNm");
		uomTypeDesc = inputData1.get(testCaseId).get("uomTypeDesc");
	}
}
