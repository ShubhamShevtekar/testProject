package scenarios.GEO;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

public class GeoRltspPut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(ScriptPost.class);
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
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
	        	
	        	//***Converting dateFormat according to DB
	        	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	        	DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");
	        	
	        	String  formatEffectiveDate;   
	        	Date dateEffectiveDate = null;
	        	
	        	if(effectiveDate.isEmpty()){
	        		formatEffectiveDate = "";  
	        	}else{
	        		formatEffectiveDate = effectiveDate;  
	        	}  
	        	
	        	try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
	    		
	    		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
	        	
	        	//***get the DB query        	
		   		
		   		String geoRltspPostQuery = query.geopRltspPostQuery(fromGeopoliticalId, toGeopoliticalId,formatEffectiveDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoRltspDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		System.out.println(fields.size());
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoRltspPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.geoplRltspCmptId")!=null)
	    		{
	    			String geoplRltspCmptId = js.getString("data.geoplRltspCmptId");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoRltspPutSuccessMsg+geoplRltspCmptId;
	    			if(internalMsg.equals(expectMessage) && geoplRltspCmptId.equals(fromGeopoliticalId))
	    			{
	    				logger.info("Success message with geoplRltspCmptId is getting received as expected in response");
	    				test.pass("Success message with geoplRltspCmptId is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.geoRltspResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
	    						"Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GEOPL_COMPT_ID: ", "DB_RELTD_GEOPL_COMPT_ID: ", "DB_GEOPL_RLTSP_TYPE_CD: ", 
	    						"DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
	    				logger.error("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				test.fail("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("geoplRltspCmptId is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("geoplRltspCmptId is not available in response");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspFromGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank fromGeopoliticalId");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspToGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the empty value is passed in JSON for to GeopoliticalId ");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspRelationshipTypeCodeBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the empty value is passed in JSON for relationshipTypeCode");
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
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspeffectiveDateBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the empty value is passed in JSON for effectiveDate");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
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
	        	
	        	//***Converting dateFormat according to DB
	        	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	        	DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");
	        	
	        	String  formatEffectiveDate;   
	        	Date dateEffectiveDate = null;
	        	
	        	if(effectiveDate.isEmpty()){
	        		formatEffectiveDate = "";  
	        	}else{
	        		formatEffectiveDate = effectiveDate;  
	        	}  
	        	
	        	try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
	    		
	    		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();	   		
		   	
		   		//***get the DB query
	    		String geoRltspPostQuery = query.geopRltspPostQuery(fromGeopoliticalId, toGeopoliticalId,formatEffectiveDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoRltspDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		System.out.println(fields.size());
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoRltspPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.geoplRltspCmptId")!=null)
	    		{
	    			String geoplRltspCmptId = js.getString("data.geoplRltspCmptId");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoRltspPutSuccessMsg+geoplRltspCmptId;
	    			if(internalMsg.equals(expectMessage) && geoplRltspCmptId.equals(fromGeopoliticalId))
	    			{
	    				logger.info("Success message with geoplRltspCmptId is getting received as expected in response");
	    				test.pass("Success message with geoplRltspCmptId is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				if(expirationDate.isEmpty()){
	    					expirationDate="9999-12-31";
	    				}
	    				String[] inputFieldValues = {fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.geoRltspResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
	    						"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:"};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = { "DB_GEOPL_COMPT_ID: ", "DB_RELTD_GEOPL_COMPT_ID: ", "DB_GEOPL_RLTSP_TYPE_CD: ", 
	    						"DB_effectiveDate", "DB_expirationDate", "DB_LastUpdateUserName:"};
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
	    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
	    				logger.error("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				test.fail("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("geoplRltspCmptId is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("geoplRltspCmptId is not available in response");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspFromGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when invalid fromGeopoliticalId is passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspToGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when invalid toGeopoliticalId is passed in JSON");
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
	public void TC_09()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspRelationshipTypeCodeInvalidMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when invalid relationshipTypeCode is passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspnDateInvalidMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when invalid effectiveDate is passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspnDateInvalidMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when invalid expirationDate is passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutMeta(fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.metaBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when the meta data section is not passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutFromGeopoliticalId(userId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspFromGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when the fromGeopoliticalId attribute is not passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutToGeopoliticalId(userId, fromGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= res.getStatusLine();
	        String internalMsg = js.getString("errorMessages.e");
	        int Wscode= res.statusCode();
	        System.out.println(Wsstatus);
	        System.out.println(internalMsg);
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.geoRltspToGeoIdBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when the toGeopoliticalId attribute is not passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutRelationshipTypeCode(userId, fromGeopoliticalId, toGeopoliticalId, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspRelationshipTypeCodeBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when the relationshipTypeCode attribute is not passed in JSON");
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutEffectiveDate(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspeffectiveDateBlankMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response  when the effectiveDate attribute is not passed in JSON");
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
	public void TC_17()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequestWithoutExpirationDate(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
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
	        	
	        	//***Converting dateFormat according to DB
	        	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	        	DateFormat destDf = new SimpleDateFormat("dd-MMM-yy");
	        	
	        	String  formatEffectiveDate;   
	        	Date dateEffectiveDate = null;
	        	
	        	if(effectiveDate.isEmpty()){
	        		formatEffectiveDate = "";  
	        	}else{
	        		formatEffectiveDate = effectiveDate;  
	        	}  
	        	
	        	try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
	    		
	    		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
	        	//***get the DB query
	        		   		
	    		String geoRltspPostQuery = query.geopRltspPostQuery(fromGeopoliticalId, toGeopoliticalId,formatEffectiveDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoRltspDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		System.out.println(fields.size());
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoRltspPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.geoplRltspCmptId")!=null)
	    		{
	    			String geoplRltspCmptId = js.getString("data.geoplRltspCmptId");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoRltspPutSuccessMsg+geoplRltspCmptId;
	    			if(internalMsg.equals(expectMessage) && geoplRltspCmptId.equals(fromGeopoliticalId))
	    			{
	    				logger.info("Success message with geoplRltspCmptId is getting received as expected in response");
	    				test.pass("Success message with geoplRltspCmptId is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.geoRltspResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
	    						"Input_effectiveDate", "Input_expirationDate", "Input_LastUpdateUserName:"};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GEOPL_COMPT_ID: ", "DB_RELTD_GEOPL_COMPT_ID: ", "DB_GEOPL_RLTSP_TYPE_CD: ", 
	    						"DB_effectiveDate", "DB_expirationDate", "DB_LastUpdateUserName:"};
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
	    				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		    			logger.error("------------------------------------------------------------------");
	    				logger.error("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				test.fail("Success message or geoplRltspCmptId is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("geoplRltspCmptId is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("geoplRltspCmptId is not available in response");
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
	public void TC_18()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.userNullMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the user name is null or Empty in JSON");
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
	public void TC_19()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload,tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspExceedLimitMsg;
				if(internalMsg.contains(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the fromGeopoliticalId is more than 50 characters length in JSON");
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
	public void TC_20()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspExceedLimitMsg;
				if(internalMsg.contains(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the toGeopoliticalId is more than 50 characters length in JSON");
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
	public void TC_21()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspRelationshipTypeCodeExceedLimitMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the relationshipTypeCode is more than 20 characters length in JSON");
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
	public void TC_22()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspnDateInvalidMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the effectiveDate is other than timestamp format in JSON");
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
	public void TC_23()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.geoRltspnDateInvalidMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the expirationDate is other than timestamp format in JSON");
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
	public void TC_24()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
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
	public void TC_25()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");
			getEndPoinUrl = getEndPoinUrl.substring(0, 97);
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the URI is not correct");
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
	public void TC_26()
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
			//***send the data to create request and get request
			String payload = PostMethod.geoRltspPostRequest(userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoRltsp.put");			
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
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
				String expectMessage = resMsgs.recordExistsMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, fromGeopoliticalId, toGeopoliticalId, relationshipTypeCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_fromGeopoliticalId: ", "Input_toGeopoliticalId: ", "Input_relationshipTypeCode: ", 
    						"Input_effectiveDate", "Input_expirationDate"};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response ");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the user tried to process the same JSON request again");
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
		fromGeopoliticalId = inputData1.get(testCaseId).get("fromGeopoliticalId");
		toGeopoliticalId = inputData1.get(testCaseId).get("toGeopoliticalId");
		relationshipTypeCode = inputData1.get(testCaseId).get("relationshipTypeCode");
		effectiveDate = inputData1.get(testCaseId).get("effectiveDate");
		expirationDate = inputData1.get(testCaseId).get("expirationDate");
	}

}
