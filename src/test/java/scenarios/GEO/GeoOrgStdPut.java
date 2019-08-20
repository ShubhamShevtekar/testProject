package scenarios.GEO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class GeoOrgStdPut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, geoOrgStdCode, geoOrgStdName;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDBFields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(GeoOrgStdPut.class);
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
			String testCaseID = m.getName();
			test = extent.createTest(testCaseID);
		}
	}
	
	@Test
	public void TC_01()
	{	
		String testCaseID = "TC_01";
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is getting updated when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoOrgStd.put");
		
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String meta =  js.getString("meta");
		String timestamp = js.getString("meta.timestamp");
		String Wsstatus= js.getString("meta.message.status");
        String internalMsg = js.getString("meta.message.internalMessage");
        int Wscode= res.statusCode();
        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
//***get the DB query
    		String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
    		//***get the fields needs to be validate in DB
    		List<String> fields = ValidationFields.geoOrgStdDBFields();
    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
    		//***get the result from DB
    		List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
    		if(js.getString("data.orgStdCd")!=null)
    		{
    			
    			String geoOrgStdCode1 = js.getString("data.orgStdCd");
    			//***success message validation
    			String expectMessage = resMsgs.geoOrgStdPutSuccessMsg+geoOrgStdCode1;
    			if(internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode))
    			{
    				logger.info("Success message with Geopitical Org Std Cosde is getting received as expected in response");
    				test.pass("Success message with Geopitical Org Std Cosde is getting received as expected in response");
    			}else {
    				logger.info("Success message is not getting received as expected in response");
    				test.fail("Success message is not getting received as expected in response");
    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
        					"Success message is not getting received as expected in response" );
        			Assert.fail("Test Failed");
    			}
    			//***send the input, response, DB result for validation
        		String[] inputFieldValues = {geoOrgStdCode, geoOrgStdName, userId};
        		//***get response fields values
        		List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
        		//***write result to excel
        		String[] inputFieldNames = {"Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ", "Input_LastUpdateUserName: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		String[] dbFieldNames = {"DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: "};
        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
        		test.info("Input Data Values:");
        		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
        		test.info("DB Data Values:");
        		test.info(writableDBFields.replaceAll("\n", "<br />"));
        		if(testResult)
        		{
        			
        			logger.info("Comparison between input data & DB data matching and passed");
        			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
        			logger.info("------------------------------------------------------------------");
        			
        			test.pass("Comparison between input data & DB data matching and passed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
        		}else{
        			
        			logger.error("Comparison between input data & DB data not matching and failed");
        			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
        			logger.error("------------------------------------------------------------------");
        			
        			test.fail("Comparison between input data & DB data not matching and failed");
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
        			Assert.fail("Test Failed");
        		}
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
    	       }else if(timestamp == null){
    	    	logger.error("Response validation failed as timestamp not found");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
    	    	test.fail("Response validation failed as timestamp not found");
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not getting updated when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
		String reqFormatted = Miscellaneous.jsonFormat(payload);
		test.info("Input Request created:");
		test.info(reqFormatted.replaceAll("\n", "<br />"));
		//***get end point url
		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoOrgStd.put");
		//***send request and get response
		Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
		String responsestr=res.asString(); 
		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
		test.info("Response Recieved:");
		test.info(responsestr1.replaceAll("\n", "<br />"));
		JsonPath js = new JsonPath(responsestr);
		String meta =  js.getString("meta");
		String timestamp = js.getString("meta.timestamp");
		String Wsstatus= res.getStatusLine();
		int errorMsgLength = js.get("errors.size");
		List<String> errorMsg1 = new ArrayList<>();
		List<String> errorMsg2 = new ArrayList<>();
		String expectMessage = resMsgs.geoOrgStdCodeBlankMsg;
		for(int i=0; i<errorMsgLength; i++){
			errorMsg1.add(js.getString("errors["+i+"].fieldName"));
			errorMsg2.add(js.getString("errors["+i+"].message"));
		}
		//String fieldName = js.getString("errors.fieldName");
        //String errorMsg = js.getString("errors.message");
        int Wscode= res.statusCode();
        if(Wscode == 400 &&  meta != null && timestamp != null)

	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
//***error message validation
			
        	if(errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage))
			{
				logger.info("Expected error message is getting received in response when sending the blank GeoOrgStdCode");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
    			
				String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		test.pass("Expected error message is getting received in response when sending the blank GeoOrgStdCode");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
    			
    			test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage  );
	        	Assert.fail("Test Failed");
	        }
	    }  else {
	    	
        	
	    	{
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
        	Assert.fail("Test Failed");
	        }
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
	
	
	@Test
	public void TC_03()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_03";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
	        {

	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***get the DB query
	    		String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoOrgStdDBFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.orgStdCd")!=null)
	    		{
	    			String geoOrgStdCode1 = js.getString("data.orgStdCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoOrgStdPutSuccessMsg+geoOrgStdCode1;
	    			if(internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode))
	    			{
	    				logger.info("Success message with Geopitical Org Std Cosde is getting received as expected in response");
	    				test.pass("Success message with Geopitical Org Std Cosde is getting received as expected in response");
	    			}else {
	    				logger.info("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    			//***send the input, response, DB result for validation
	        		String[] inputFieldValues = {geoOrgStdCode, geoOrgStdName, userId};
	        		//***get response fields values
	        		List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
	        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	        		//***write result to excel
	        		String[] inputFieldNames = {"Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ", "Input_LastUpdateUserName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		String[] dbFieldNames = {"DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: "};
	        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	        		test.info("Input Data Values:");
	        		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	        		test.info("DB Data Values:");
	        		test.info(writableDBFields.replaceAll("\n", "<br />"));
	        		if(testResult)
	        		{
	        			logger.info("Comparison between input data & DB data matching and passed");
	        			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	        			logger.info("------------------------------------------------------------------");
	        			
	        			test.pass("Comparison between input data & DB data matching and passed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
	        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	        		}else{
	        			
	        			logger.error("Comparison between input data & DB data not matching and failed");
	        			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	        			logger.error("------------------------------------------------------------------");
	        			
	        			test.fail("Comparison between input data & DB data not matching and failed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	        			Assert.fail("Test Failed");
	        		}
	    		}else {
					
	    			logger.error("Geopolitical Org Std Code is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
	    			test.fail("Geopolitical Org Std Code is not available in response");
	    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
	    			Assert.fail("Test Failed");
				}
	        }}
	        
	       
	        else{
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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

		
		@Test
		public void TC_04()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequestWithoutMeta(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
				if(errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in responsewhen the meta data section is not passed in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}
		    }else
		    	{
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
			       }else if(timestamp == null){
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "meta"+expectMessage );
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
		
		@Test
		public void TC_05()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequestWithoutOrgStdCd(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
	        	
				
	        	if(errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the orgStdCd attribute is not passed in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
		        	
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail",  "geoOrgStdCode"+expectMessage  );
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	
		    	logger.error("Response status code 400 validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
				
		    	test.fail("Response status code 400 validation failed: "+Wscode);
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail",  "geoOrgStdCode"+expectMessage  );
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
			//String testCaseID = "TC_01";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequestWithoutOrgStdNm(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
	        {

	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***get the DB query
	    		String geoOrgStdPostQuery = query.geoOrgStdPostQuery(geoOrgStdCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.geoOrgStdDBFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoOrgStdPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.orgStdCd")!=null)
	    		{
	    			String geoOrgStdCode1 = js.getString("data.orgStdCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.geoOrgStdPutSuccessMsg+geoOrgStdCode1;
	    			if(internalMsg.equals(expectMessage) && geoOrgStdCode1.equals(geoOrgStdCode))
	    			{
	    				logger.info("Success message with Geopitical Org Std Cosde is getting received as expected in response");
	    				test.pass("Success message with Geopitical Org Std Cosde is getting received as expected in response");
	    			}else {
	    				logger.info("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    			//***send the input, response, DB result for validation
	        		String[] inputFieldValues = {geoOrgStdCode, geoOrgStdName, userId};
	        		//***get response fields values
	        		List<String> resFields = ValidationFields.geoOrgStdResponseFields(res);
	        		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	        		//***write result to excel
	        		String[] inputFieldNames = {"Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: ", "Input_LastUpdateUserName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		String[] dbFieldNames = {"DB_GeoOrgStdCode: ", "DB_GeoOrgStdName: ", "DB_LastUpdateUserName: "};
	        		writableDBFields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
	        		test.info("Input Data Values:");
	        		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	        		test.info("DB Data Values:");
	        		test.info(writableDBFields.replaceAll("\n", "<br />"));
	        		if(testResult)
	        		{
	        			logger.info("Comparison between input data & DB data matching and passed");
	        			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	        			logger.info("------------------------------------------------------------------");
	        			
	        			test.pass("Comparison between input data & DB data matching and passed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDBFields,
	        					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	        		}else{
	        			
	        			logger.error("Comparison between input data & DB data not matching and failed");
	        			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	        			logger.error("------------------------------------------------------------------");
	        			
	        			test.fail("Comparison between input data & DB data not matching and failed");
	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDBFields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	        			Assert.fail("Test Failed");
	        		}
	    		}else {
					
	    			logger.error("Geopolitical Org Std Code is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
	    			test.fail("Geopolitical Org Std Code is not available in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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


		
		@Test
		public void TC_07()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequestNullUser(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.requiredFieldMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)

		    
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the user name is null or Empty in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
		        	
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userId"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	
	        	
		    	{
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
			       }else if(timestamp == null){
		        	logger.error("Response validation failed as timestamp not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as timestamp not found");
			       }
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userId"+expectMessage );
	        	Assert.fail("Test Failed");
		        }
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

		
		
		@Test
		public void TC_08()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds10Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)

		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
				if(errorMsg1.get(0).equals("orgStdCd") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the orgStdCd is more than 10 characters length in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
		        	
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoOrgStdCode"+expectMessage  );
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
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
		
		@Test
		public void TC_09()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds65Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)

		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
			
				if(errorMsg1.get(0).equals("orgStdNm") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the orgStdNm is ore than 65 characters length in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
		        	
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail",  "geoOrgStdName"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else  {
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
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
		
		@Test
		public void TC_10()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.lengthExceeds25Char;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 400 &&  meta != null && timestamp != null)

		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
				if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the User Name is more than 25 characters length in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
		        	
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "userId"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else  {
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
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
		
		
		@Test
		public void TC_11()
		{	
			String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
			//String testCaseID = "TC_02";
			logger.info("Executing Test Case: "+testCaseID);
			if(!runFlag.equalsIgnoreCase("Yes")) {
				logger.info("Skipped Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
				throw new SkipException("Execution skipped as per test flag set");
			}
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not created when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".geoOrgStd.put");
			getEndPoinUrl=getEndPoinUrl.substring(0, 118);
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
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
					logger.info("Expected error message is getting received in response when the URI is not correct");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response");
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
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not getting updated when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.recordNotFoundMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 404 &&  meta != null && timestamp != null)

		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when the orgStdCd provided in JSON is not exist in DB");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response when sending the blank GeoOrgStdCode");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
	    			test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "geoOrgStdCode"+expectMessage  );
		        	Assert.fail("Test Failed");
		        }
		    }else  {
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
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
			//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Org Std service is not getting updated when the Org Std Code is empty in JSON Request.", ExtentColor.PURPLE));
			boolean testResult=false;
			//***get test case ID with method name
			try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.geoOrgStdPostRequest(userId, geoOrgStdCode, geoOrgStdName);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".geoOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta =  js.getString("meta");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			String expectMessage = resMsgs.recordNotFoundMsg;
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
			//String fieldName = js.getString("errors.fieldName");
	        //String errorMsg = js.getString("errors.message");
	        int Wscode= res.statusCode();
	        if(Wscode == 404 &&  meta != null && timestamp != null)

		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
//***error message validation
				
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					logger.info("Expected error message is getting received in response when special characters are provided for orgStdCd in JSON");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			
					String[] inputFieldValues = {userId, geoOrgStdCode, geoOrgStdName};
	        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoOrgStdCode: ", "Input_GeoOrgStdName: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.pass("Expected error message is getting received in response when sending the blank GeoOrgStdCode");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			
	    			test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail",  "geoOrgStdCode"+expectMessage );
		        	Assert.fail("Test Failed");
		        }
		    }else  {
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geoOrgStdCode"+expectMessage );
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
			geoOrgStdCode = inputData1.get(testCaseId).get("orgStdCd");
			geoOrgStdName = inputData1.get(testCaseId).get("orgStdNm");
		}
}
