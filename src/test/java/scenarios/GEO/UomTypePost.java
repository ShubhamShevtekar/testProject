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

public class UomTypePost extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, uomTypeCode, uomTypeName, uomTypeDesc;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(UomTypePost.class);
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
		//***get token properties
/*		tokenValues = RetrieveEndPoints.getTokenProperties(fileName);
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Uom Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
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
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
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
	    			String expectMessage = resMsgs.uomTypePostSuccessMsg+uomTypeCode1;
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.uomTypeCodeBlankMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_03()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeNmNullMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Uom Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
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
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
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
	    			String expectMessage = resMsgs.uomTypePostSuccessMsg+uomTypeCode1;
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	public void TC_05()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.metaBlankMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_06()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeCdNullMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_07()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeNmNullMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Uom Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
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
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
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
	    			String expectMessage = resMsgs.uomTypePostSuccessMsg+uomTypeCode1;
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	public void TC_09()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.userNullMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_10()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeCdLengthMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_11()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeNmLengthMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_12()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.UomTypeDescLengthMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_13()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
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
					String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	@Test
	public void TC_14()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			getEndPoinUrl=getEndPoinUrl.substring(0, 92);
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("error");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
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
					String[] inputFieldValues = {userId, uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UserName: ", "Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank UomType Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_15()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the UomType service is not created when theUOM Type Code is empty in JSON Request.", ExtentColor.PURPLE));
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
			logger.info("Request Created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
			//***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Received");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 400)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.recordExistsMsg;
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
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
	
	
	@Test
	public void TC_16()
	{	
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Uom Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
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
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".uomType.post");
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
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
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
	    			String expectMessage = resMsgs.uomTypePostSuccessMsg+uomTypeCode1;
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
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
		uomTypeCode = inputData1.get(testCaseId).get("uomTypeCd");
		uomTypeName = inputData1.get(testCaseId).get("uomTypeNm");
		uomTypeDesc = inputData1.get(testCaseId).get("uomTypeDesc");
	}
}
