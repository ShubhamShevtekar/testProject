package scenarios.GEO;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
			String testCaseID = m.getName();
			test = extent.createTest(testCaseID);
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
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
        String meta = js.getString("meta");
        String timestamp = js.getString("meta.timestamp");
        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
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
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else

        {
        	if(Wscode!=200){
	        	logger.error("Response validation failed as Wscode is not present: "+Wscode);
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as Wscode is not present: "+Wscode);
	        	
	        } else if(meta==null){

	        	logger.error("Response validation failed as meta is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as meta is not present: ");
	        	
	        
	        } else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is not present:");
	        	
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.requiredField;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("geopoliticalRelationshipTypeCd"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage );
	        	Assert.fail("Test Failed");
	        }
	    }  else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage );
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
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
        String meta = js.getString("meta");
        String timestamp = js.getString("meta.timestamp");
        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
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
    				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
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
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else

        {
        	if(Wscode!=200){
	        	logger.error("Response validation failed as Wscode is not present: "+Wscode);
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as Wscode is not present: "+Wscode);
	        	
	        } else if(meta==null){

	        	logger.error("Response validation failed as meta is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as meta is not present: ");
	        	
	        
	        } else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is not present:");
	        	
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.requiredField;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("geopoliticalRelationshipTypeCd"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage );
	        	Assert.fail("Test Failed");
	        }
	    }  else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage );
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
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
        String meta = js.getString("meta");
        String timestamp = js.getString("meta.timestamp");
        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
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
    				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
        			logger.info("------------------------------------------------------------------");
    				test.pass("Success message with Geopitical Relationship Type Cosde is getting received as expected in response");
    			}else {
    				logger.error("Success message is not getting received as expected in response");
    				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
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
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else

        {
        	if(Wscode!=200){
	        	logger.error("Response validation failed as Wscode is not present: "+Wscode);
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as Wscode is not present: "+Wscode);
	        	
	        } else if(meta==null){

	        	logger.error("Response validation failed as meta is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as meta is not present: ");
	        	
	        
	        } else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is not present:");
	        	
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
        
        
        if(Wscode == 404)
	    {
        	logger.info("Response status code 404 validation passed: "+Wscode);
        	test.pass("Response status code 404 validation passed: "+Wscode);
        	//***error message validation
			String expectMessage = resMsgs.invalidUrlMsg;
			if(internalMsg.equals(expectMessage))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	    }else {
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.requiredField;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("meta"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "meta"+expectMessage  );
	        	Assert.fail("Test Failed");
	        }
	    } else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "meta"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.requiredField;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("userName"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "userName"+expectMessage );
	        	Assert.fail("Test Failed");
	        }
	    } else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "userName"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.lengthExceeds20Char1;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("geopoliticalRelationshipTypeCd"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
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
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage  );
	        	Assert.fail("Test Failed");
	        }
	    }  else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "geopoliticalRelationshipTypeCd"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.lengthExceeds100Char1;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("areaRelationshipTypeDescription"))
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
						responsestr, "Fail", "areaRelationshipTypeDescription"+expectMessage  );
	        	Assert.fail("Test Failed");
	        }
	    }  else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "areaRelationshipTypeDescription"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.lengthExceeds25Char;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 400 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response status code 400 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("userName"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
        		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail",  "userName"+expectMessage );
	        	Assert.fail("Test Failed");
	        }
	    }  else 
	    {
	    	if(Wscode != 400){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "userName"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.recordNotFoundMsg;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 404 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 404 validation passed: "+Wscode);
        	test.pass("Response status code 404 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("Error"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail",  "Error"+expectMessage );
	        	Assert.fail("Test Failed");
	        }
	    } else 
	    {
	    	if(Wscode != 404){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "Error"+expectMessage );
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
		//***get test case ID with method name
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
        int Wscode= res.statusCode();
        String expectMessage = resMsgs.recordNotFoundMsg;
        String meta = js.getString("meta");
       	String timestamp = js.getString("meta.timestamp");
       	int errorMsgLength =js.get("errors.size");
       	List <String> errorMgs1= new ArrayList<String>();
       	List <String> errorMgs2= new ArrayList<String>();
       	for(int i=0;i<errorMsgLength;i++)
       	  { 
       	        	errorMgs1.add(js.getString("errors["+i+"].fieldName"));
       	        	errorMgs2.add(js.getString("errors["+i+"].message"));
       	        	
       	  }

       	if(Wscode == 404 && meta!=null && timestamp!=null)
	    {
        	logger.info("Response status code 404 validation passed: "+Wscode);
        	test.pass("Response status code 404 validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
        	//***error message validation
        	if(errorMgs2.get(0).equals(expectMessage)&&errorMgs1.get(0).equals("Error"))
			{
				String[] inputFieldValues = {userId, geoRsTypeCode, geoRsTypeDesc};
        		String[] inputFieldNames = {"Input_UserName: ", "Input_GeoRsTypeCode: ", "Input_GeoRsTypeDesc: "};
        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
        		logger.info("Expected error message is getting received in response ");
    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
        		test.pass("Expected error message is getting received in response ");
        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
			}else {
				logger.error("Expected error message is not getting received in response");
				logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
    			logger.info("------------------------------------------------------------------");
	        	test.fail("Expected error message is not getting received in response");
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "Error"+expectMessage  );
	        	Assert.fail("Test Failed");
	        }
	    } else 
	    {
	    	if(Wscode != 404){
	    	logger.error("Response status code 400 validation failed: "+Wscode);
	    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
        	test.fail("Response status code 400 validation failed: "+Wscode);
        	
        } else if(meta == null){
        	
        	logger.error("Response validation failed as meta is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as meta is not present");
        	
        	
        }else if(timestamp == null){
        	logger.error("Response validation failed as timestamp is not present");
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Response validation failed as timestamp is not present");
        	
        	
        }
        ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
				responsestr1, "Fail", "Error"+expectMessage );
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
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Relationship Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		//***get test case ID with method name
		try {
		//***get the test data from sheet
		testDataFields(scenarioName, testCaseID);
		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
		//***send the data to create request and get request
		String payload = PostMethod.geoRsTypePostRequestWithNullAreaDesc(userId, geoRsTypeCode, geoRsTypeDesc);
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
        String meta = js.getString("meta");
        String timestamp = js.getString("meta.timestamp");
        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && timestamp != null)
        {
        	logger.info("Response status validation passed: "+Wscode);
        	test.pass("Response status validation passed: "+Wscode);
        	test.pass("Response meta validation passed");
        	test.pass("Response timestamp validation passed"); 
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
    			if(geoRsTypeDesc.equals("null")){
    				geoRsTypeDesc = "";
    			}
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
    			logger.error("Geopolitical RS Type ID is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("Geopolitical RS Type ID is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    					"", "", Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
    			Assert.fail("Test Failed");
			}
        }else

        {
        	if(Wscode!=200){
	        	logger.error("Response validation failed as Wscode is not present: "+Wscode);
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as Wscode is not present: "+Wscode);
	        	
	        } else if(meta==null){

	        	logger.error("Response validation failed as meta is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as meta is not present: ");
	        	
	        
	        } else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is not present:");
	        	
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
