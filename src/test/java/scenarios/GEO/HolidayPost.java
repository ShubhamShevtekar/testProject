package scenarios.GEO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
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

public class HolidayPost extends Reporting {
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, holidayName, holidayParamText;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(HolidayPost.class);
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
/*		//***get token properties
		tokenValues = RetrieveEndPoints.getTokenProperties(fileName);
		//***get token
		URL url;
		try {
			url = new URL(tokenValues[1]);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			token = IOUtils.toString(in, encoding);
		} catch (IOException e) {
			e.printStackTrace();
			test.fail("Unable to get the token, exception thrown: "+e.toString());
		}*/
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
			String payload = PostMethod.holidaPostRequest(userId, holidayName, holidayParamText);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	    		String holidayPostQuery = query.holidayPostQuery(holidayName);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.holidayDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(holidayPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.holidayId")!=null)
	    		{
	    			String holidayId1 = js.getString("data.holidayId");
	    			//***success message validation
	    			String expectMessage = resMsgs.holidayPostSuccessMsg+holidayId1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Holiday ID is getting generated and received in response: "+holidayId1);
	    				test.pass("Holiday ID is getting generated and received in response: "+holidayId1);
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, holidayId1, holidayName, holidayParamText, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.holidayResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Response_HolidayId: ", "Input_HolidayName: ", "Input_HolidayParamaText: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_HolidayId: ", "DB_HolidayName: ", "DB_HolidayParamaText: ", "DB_LastUpdateUserName: "};
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
	    			logger.error("Holiday ID  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("Holiday ID is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId, holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.holidayNameBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response when sending the blank Holiday Name");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response when sending the blank Holiday Name");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
			String payload = PostMethod.holidaPostRequest(userId, holidayName, holidayParamText);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	    		String holidayPostQuery = query.holidayPostQuery(holidayName);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.holidayDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(holidayPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.holidayId")!=null)
	    		{
	    			String holidayId1 = js.getString("data.holidayId");
	    			//***success message validation
	    			String expectMessage = resMsgs.holidayPostSuccessMsg+holidayId1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Holiday ID is getting generated and received in response: "+holidayId1);
	    				test.pass("Holiday ID is getting generated and received in response: "+holidayId1);
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, holidayId1, holidayName, holidayParamText, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.holidayResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Response_HolidayId: ", "Input_HolidayName: ", "Input_HolidayParamaText: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_HolidayId: ", "DB_HolidayName: ", "DB_HolidayParamaText: ", "DB_LastUpdateUserName: "};
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
	    			logger.error("Holiday ID  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("Holiday ID is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequestWithoutMeta(holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.metaBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
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
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequestWithoutHolidayName(userId, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.holidayNameBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response when the holidayName attribute is not passed in JSON");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response when the holidayName attribute is not passed in JSON");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
			String payload = PostMethod.holidaPostRequestWithoutHolidayParamText(userId, holidayName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	    		String holidayPostQuery = query.holidayPostQuery(holidayName);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.holidayDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(holidayPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.holidayId")!=null)
	    		{
	    			String holidayId1 = js.getString("data.holidayId");
	    			//***success message validation
	    			String expectMessage = resMsgs.holidayPostSuccessMsg+holidayId1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Holiday ID is getting generated and received in response: "+holidayId1);
	    				test.pass("Holiday ID is getting generated and received in response: "+holidayId1);
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, holidayId1, holidayName, holidayParamText, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.holidayResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Response_HolidayId: ", "Input_HolidayName: ", "Input_HolidayParamaText: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_HolidayId: ", "DB_HolidayName: ", "DB_HolidayParamaText: ", "DB_LastUpdateUserName: "};
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
	    			logger.error("Holiday ID  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("Holiday ID is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.userNullMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
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
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.holidayNameLimitMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response when the holidayName is more than 65 characters length in JSON");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response when the holidayName is more than 65 characters length in JSON");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.holidayDateParamTextLimitMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response when the holidayDateParamText is more than 400 characters length in JSON");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response when the holidayDateParamText is more than 400 characters length in JSON");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.userLenghtMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
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
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response
		getEndPoinUrl = getEndPoinUrl.substring(0, 93);
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
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.invalidUrlMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
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
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
		String payload = PostMethod.holidaPostRequest(userId,holidayName, holidayParamText);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
		//***send request and get response		
		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.recordExistsMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, holidayName, holidayParamText};
				String[] inputFieldNames = {"Input_UserName: ", "Input_HolidayName: ", "Input_HolidayParamaText: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response when the user tried to process the same holidayName again");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response when the user tried to process the same holidayName again");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Expected error message is not getting received in response");
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
	
	@Test
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
			String payload = PostMethod.holidaPostRequest(userId, holidayName, holidayParamText);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".holiday.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	    		String holidayPostQuery = query.holidayPostQuery(holidayName);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.holidayDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(holidayPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.holidayId")!=null)
	    		{
	    			String holidayId1 = js.getString("data.holidayId");
	    			//***success message validation
	    			String expectMessage = resMsgs.holidayPostSuccessMsg+holidayId1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Holiday ID is getting generated and received in response: "+holidayId1);
	    				test.pass("Holiday ID is getting generated and received in response: "+holidayId1);
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, holidayId1, holidayName, holidayParamText, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.holidayResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Response_HolidayId: ", "Input_HolidayName: ", "Input_HolidayParamaText: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_HolidayId: ", "DB_HolidayName: ", "DB_HolidayParamaText: ", "DB_LastUpdateUserName: "};
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
	    			logger.error("Holiday ID  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("Holiday ID is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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
		holidayName = inputData1.get(testCaseId).get("holidayName");
		holidayParamText = inputData1.get(testCaseId).get("holidayDateParamText");
	}
}