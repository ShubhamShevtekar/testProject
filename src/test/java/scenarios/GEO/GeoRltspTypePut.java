package scenarios.GEO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

public class GeoRltspTypePut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, geoRsTypeCode, geoRsTypeDesc;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDBFields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(GeoRltspTypePut.class);
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
		String testCaseName = "TC_01";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        //String Wsstatus= res.getStatusLine();
		String Wsstatus= js.getString("meta.message.status");
        String internalMsg = js.getString("meta.message.internalMessage");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") )
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	//***get the DB query
    		String geoRsTypePostQuery = query.geoRsTypePostQuery(geoRsTypeCode);
    		//***get the fields needs to be validate in DB
    		List<String> fields = ValidationFields.geoRsTypeDBFields();
    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
    		//***get the result from DB
    		List<String> getResultDB = DbConnect.getResultSetFor(geoRsTypePostQuery, fields, fileName, testCaseID);
    		if(js.getString("data.geopoliticalRelationshipTypeCd")!=null)
    		{
    			String geoRsTypeCode1 = js.getString("data.geopoliticalRelationshipTypeCd");
    			//***success message validation
    			String expectMessage = resMsgs.geoRsTypePutSuccessMsg+geoRsTypeCode1;
    			if(internalMsg.equals(expectMessage) && geoRsTypeCode1.equals(geoRsTypeCode))
    			{
    				logger.info("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
    				test.pass("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
    			}else {
    				logger.error("Success message is not getting received as expected in response");
    				test.fail("Success message is not getting received as expected in response");
    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
        					"Success message is not getting received as expected in response" );
        			Assert.fail("Test Failed");
    			}
    			//***send the input, response, DB result for validation
        		String[] inputFieldValues = {geoRsTypeCode, geoRsTypeDesc, userId};
        		//***get response fields values
        		List<String> resFields = ValidationFields.geoRsTypeResponseFields(res);
        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
        		//***write result to excel
        		String[] inputFieldNames = {"Input_GeoRltspTypeCode: ", "Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		String[] dbFieldNames = {"DB_GeoRltspTypeCode: ", "DB_GeoRltspTypeDesc: ", "DB_LastUpdateUserName: "};
        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
        		logger.info("Input Data Values:");
        		test.info("Input Data Values:");
        		//logger.info(writableInputFields.replaceAll("\n", "<br />"));
        		test.info(writableInputFields.replaceAll("\n", "<br />")); 
        		logger.info("DB Data Values:");
        		test.info("DB Data Values:");
        		//logger.info(writableDBFields.replaceAll("\n", "<br />"));
        		test.info(writableDBFields.replaceAll("\n", "<br />"));
        		if(testResult)
        		{
        			logger.info("Comparison between input data & DB data matching and passed");
        			logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
        			logger.info("------------------------------------------------------------------");
        			test.pass("Comparison between input data & DB data matching and passed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
        		}else{
        			logger.error("Comparison between input data & DB data not matching and failed");
        			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
        			logger.error("------------------------------------------------------------------");
        			test.fail("Comparison between input data & DB data not matching and failed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
        			Assert.fail("Test Failed");
        		}
    		}else {
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else {
        	logger.error("Response status validation failed: "+Wscode);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response status validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
					responsestr1, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_02";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypeCodeBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
			String testCaseName = "TC_03";
			logger.info("Executing Test Case: "+testCaseName);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseName);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			//logger.info("Input Request created:");
			test.info("Input Request created:");
			//logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			//logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
	        //String Wsstatus= res.getStatusLine();
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") )
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	    		String geoRsTypePostQuery = query.geoRsTypePostQuery(geoRsTypeCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoRsTypeDBFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoRsTypePostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.geopoliticalRelationshipTypeCd")!=null)
	    		{
	    			String geoRsTypeCode1 = js.getString("data.geopoliticalRelationshipTypeCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoRsTypePutSuccessMsg+geoRsTypeCode1;
	    			if(internalMsg.equals(expectMessage) && geoRsTypeCode1.equals(geoRsTypeCode))
	    			{
	    				logger.info("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
	    				test.pass("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
	    			}else {
	    				logger.error("Success message is not getting received as expected in response");
	    				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
	        			logger.info("------------------------------------------------------------------");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    			//***send the input, response, DB result for validation
	        		String[] inputFieldValues = {geoRsTypeCode, geoRsTypeDesc, userId};
	        		//***get response fields values
	        		List<String> resFields = ValidationFields.geoRsTypeResponseFields(res);
	        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	        		//***write result to excel
	        		String[] inputFieldNames = {"Input_GeoRltspTypeCode: ", "Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		String[] dbFieldNames = {"DB_GeoRltspTypeCode: ", "DB_GeoRltspTypeDesc: ", "DB_LastUpdateUserName: "};
	        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	        		logger.info("Input Data Values:");
	        		test.info("Input Data Values:");
	        		//logger.info(writableInputFields.replaceAll("\n", "<br />"));
	        		test.info(writableInputFields.replaceAll("\n", "<br />")); 
	        		logger.info("DB Data Values:");
	        		test.info("DB Data Values:");
	        		//logger.info(writableDBFields.replaceAll("\n", "<br />"));
	        		test.info(writableDBFields.replaceAll("\n", "<br />"));
	        		if(testResult)
	        		{
	        			logger.info("Comparison between input data & DB data matching and passed");
	        			logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
	        			logger.info("------------------------------------------------------------------");
	        			test.pass("Comparison between input data & DB data matching and passed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
	        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	        		}else{
	        			logger.error("Comparison between input data & DB data not matching and failed");
	        			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
	        			logger.error("------------------------------------------------------------------");
	        			test.fail("Comparison between input data & DB data not matching and failed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	        			Assert.fail("Test Failed");
	        		}
	    		}else {
	    			logger.error("Geopolitical RS Type ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Geopolitical RS Type ID is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
			}catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception thrown when executing the test case: "+e);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_04";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequestWithoutgeopoliticalRelationshipTypeCd(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypeCodeBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_05";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequestWithoutareaRelationshipTypeDescription(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        //String Wsstatus= res.getStatusLine();
		String Wsstatus= js.getString("meta.message.status");
        String internalMsg = js.getString("meta.message.internalMessage");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") )
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	//***get the DB query
    		String geoRsTypePostQuery = query.geoRsTypePostQuery(geoRsTypeCode);
    		//***get the fields needs to be validate in DB
    		List<String> fields = ValidationFields.geoRsTypeDBFields();
    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
    		//***get the result from DB
    		List<String> getResultDB = DbConnect.getResultSetFor(geoRsTypePostQuery, fields, fileName, testCaseID);
    		if(js.getString("data.geopoliticalRelationshipTypeCd")!=null)
    		{
    			String geoRsTypeCode1 = js.getString("data.geopoliticalRelationshipTypeCd");
    			//***success message validation
    			String expectMessage = resMsgs.geoRsTypePutSuccessMsg+geoRsTypeCode1;
    			if(internalMsg.equals(expectMessage) && geoRsTypeCode1.equals(geoRsTypeCode))
    			{
    				logger.info("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
    				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
        			logger.info("------------------------------------------------------------------");
    				test.pass("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
    			}else {
    				logger.error("Success message is not getting received as expected in response");
    				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
        			logger.info("------------------------------------------------------------------");
    				test.fail("Success message is not getting received as expected in response");
    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
        					"Success message is not getting received as expected in response" );
        			Assert.fail("Test Failed");
    			}
    			//***send the input, response, DB result for validation
        		String[] inputFieldValues = {geoRsTypeCode, geoRsTypeDesc, userId};
        		//***get response fields values
        		List<String> resFields = ValidationFields.geoRsTypeResponseFields(res);
        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
        		//***write result to excel
        		String[] inputFieldNames = {"Input_GeoRltspTypeCode: ", "Input_GeoRltspTypeDesc: ", "Input_LastUpdateUserName: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		String[] dbFieldNames = {"DB_GeoRltspTypeCode: ", "DB_GeoRltspTypeDesc: ", "DB_LastUpdateUserName: "};
        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
        		logger.info("Input Data Values:");
        		test.info("Input Data Values:");
        		//logger.info(writableInputFields.replaceAll("\n", "<br />"));
        		test.info(writableInputFields.replaceAll("\n", "<br />")); 
        		logger.info("DB Data Values:");
        		test.info("DB Data Values:");
        		//logger.info(writableDBFields.replaceAll("\n", "<br />"));
        		test.info(writableDBFields.replaceAll("\n", "<br />"));
        		if(testResult)
        		{
        			logger.info("Comparison between input data & DB data matching and passed");
        			logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
        			logger.info("------------------------------------------------------------------");
        			test.pass("Comparison between input data & DB data matching and passed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
        		}else{
        			logger.error("Comparison between input data & DB data not matching and failed");
        			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
        			logger.error("------------------------------------------------------------------");
        			test.fail("Comparison between input data & DB data not matching and failed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
        			Assert.fail("Test Failed");
        		}
    		}else {
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else {
        	logger.error("Response status validation failed: "+Wscode);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response status validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
					responsestr1, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_06";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		getEndPoinUrl=getEndPoinUrl.substring(0, 101);
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("error");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 404)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.invalidUrlMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_07";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequestWithoutMeta(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypeMetaNull;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_08";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequestWithNullUser(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypeNullUser;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_09";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypegeoRelTypeCdLengthMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_10";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypegeopoRelTypeDescMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_11";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.userLenghtMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
    			logger.error("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
		String testCaseName = "TC_02";
		logger.info("Executing Test Case: "+testCaseName);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is not created when the Geopolitical Relationship Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequest(userId, geoRsTypeCode, geoRsTypeDesc);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		//logger.info("Input Request created:");
		test.info("Input Request created:");
		//logger.info(reqFormatted.replaceAll("\n", "<br />"));
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRsType.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		logger.info("Response Recieved:");
		test.info("Response Recieved:");
		//logger.info(responsestr1.replaceAll("\n", "<br />"));
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
        String Wsstatus= res.getStatusLine();
        String internalMsg = js.getString("errorMessages.e");
        int Wscode= res.statusCode();
        System.out.println(Wsstatus);
        System.out.println(internalMsg);
        if(Wscode == 400)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.geoRsTypeCodeBlankMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseName);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
					responsestr, "Fail", internalMsg );
        	Assert.fail("Test Failed");
        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: "+e);
			logger.error("Execution is completed for Failed Test Case No. "+testCaseName);
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
			logger.error("Unable to retrieve the test data file/fields");
			test.fail("Unable to retrieve the test data file/fields");
		}
		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
		userId = inputData1.get(testCaseId).get("UserName");
		geoRsTypeCode = inputData1.get(testCaseId).get("geopoliticalRelationshipTypeCd");
		geoRsTypeDesc = inputData1.get(testCaseId).get("areaRelationshipTypeDescription");
	}

}
