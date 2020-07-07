package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import utils.v1.DbConnect;
import utils.v1.ExcelUtil;
import utils.v1.Miscellaneous;
import utils.v1.Queries;
import utils.v1.Reporting;
import utils.v1.ResponseMessages;
import utils.v1.RetrieveEndPoints;
import utils.v1.TestResultValidation;
import utils.v1.ValidationFields;
import wsMethods.v1.GetResponse;
import wsMethods.v1.PostMethod;

public class CntryOrgStdPut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate;//, token;
	Queries query = new Queries();
	//String[] tokenValues = new String[2];
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	JMSReader jmsReader = new JMSReader();
	String writableInputFields, writableDB_Fields=null,writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	Date date = new Date();
	String todaysDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
	DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat destDf = new SimpleDateFormat("dd-MMM-yyyy");
	static Logger logger = Logger.getLogger(CntryOrgStdPut.class);
	String actuatorcommandversion = "1.0.0";
	TestResultValidation resultValidation = new TestResultValidation();
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
				String tokenKey = tokenValues[0];
				String tokenVal = token;
				String actuatorCommandeVersionURL=RetrieveEndPoints.getEndPointUrl("commandActuator", fileName, level+".command.version");
				//actuatorcommandversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorCommandeVersionURL);
	
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
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// Before Put request DB Count
    		String cntryOrgStdBeforePutQuery = query.contryOrgStdCountPutQuery(countryShortName);
			List<String> fieldsAuditcnt = ValidationFields.auditDBCntFields();
			List<String> getAuditBeforeCntResultDB = DbConnect.getResultSetFor(cntryOrgStdBeforePutQuery, fieldsAuditcnt, fileName, testCaseID);
			int beforePutCount = Integer.parseInt(getAuditBeforeCntResultDB.get(0)) ;
			test.info("Before put request data count: "+beforePutCount);	
			
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			test.info(" Verified for URI :"+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        
	        // After Put Request DB count 
	      			List<String> getAuditAfetrCntResultDB  = DbConnect.getResultSetFor(cntryOrgStdBeforePutQuery, fieldsAuditcnt, fileName, testCaseID);
	      			int AfterPutCount = Integer.parseInt(getAuditAfetrCntResultDB.get(0)) ;
	      			test.info("After put request data count: "+AfterPutCount);
	      			 
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	
	        	String formatEffectiveDate = effectiveDate;   
	        	String formatExpirationDate = expirationDate;   
	    		Date dateEffectiveDate = null;
	    		Date dateExpirationDate = null;
		            
		   		try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}

		   		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
	        	
	        	//***get the DB query
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{ 
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	        				
	        				String formatAuditEffectiveDate = effectiveDate;   
	        	        	String formatAuditExpirationDate = expirationDate;   
	        	    		Date dateAuditEffectiveDate = null;
	        	    		Date dateAuditExpirationDate = null;
	        		            
	        		   		try {
	        		   			dateAuditEffectiveDate = srcDf.parse(formatAuditEffectiveDate);
	        		   			dateAuditExpirationDate = srcDf.parse(formatAuditExpirationDate);
	        		   		} catch (ParseException e) {
	        		   			// TODO Auto-generated catch block
	        		   			e.printStackTrace();
	        		   		}

	        		   		formatAuditEffectiveDate = destDf.format(dateAuditEffectiveDate);            
	        		   		formatAuditEffectiveDate = formatAuditEffectiveDate.toUpperCase();
	        		   		
	        		   		formatAuditExpirationDate = destDf.format(dateAuditExpirationDate);            
	        		   		formatAuditExpirationDate = formatAuditExpirationDate.toUpperCase();
	        		   		
	        				String countryOrgStdPostAuditQuery = query.countryOrgStdPostAuditQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatAuditEffectiveDate, formatAuditExpirationDate);
	        	    		//***get the fields needs to be validate in DB
	        				List<String> auditFields = ValidationFields.cntryOrgStdAuditDbFields();
	        				auditFields.remove(0);
	        	    		//***get the result from DB
	        	    		List<String> getResultDBAudit = DbConnect.getResultSetFor(countryOrgStdPostAuditQuery, auditFields, fileName, testCaseID);
	        	    		if(getResultDBAudit.get(7).equals("0")){
	        	    			getResultDBAudit.set(7, "1");
	        	    		}
	        	    		String[] inputAuditFieldValues = { geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId,"1"};
	        	    		testResult = false;
	        	    		testResult = TestResultValidation.testValidationWithDB(res, inputAuditFieldValues, getResultDBAudit, resFields);
	        	    		if(testResult)
		            		{
			    				String[] inputFieldNamesAudit = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
			    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: " ,"Expected RevisionType CD:"};
		            			
			    				writableInputFields = Miscellaneous.geoFieldInputNames(inputAuditFieldValues, inputFieldNamesAudit);
			            		String[] dbFieldNamesAudit = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
			    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: ", "DB_RevisionType CD:"};
			    				
	    	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBAudit, dbFieldNamesAudit);
	    	            		test.pass("Comparison between input data & DB Audit table data matching and passed");
		            			ex.writeExcel(fileName, testCaseID, "Audit Table Validation", scenarioType, "",	writableInputFields, writableDB_Fields,
		            					"", "", "", "Pass", "Audit Table validation" );
		            			test.info("Input Audit Table Data Values:");
			            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
			            		test.info("DB Audit Table Data Values:");
			            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
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
        	if(Wscode!=200){
	        	logger.error("Response validation failed as Wscode is not present: "+Wscode);
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as Wscode is not present: "+Wscode);
	        	}	
	         else if(meta==null){

	        	logger.error("Response validation failed as meta is not present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as meta is not present: ");
	        	
	        
	        } else if(meta.contains("timestamp")){
	        	logger.error("Response validation failed as timestamp is present:");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present:");
	        	
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode", "Input_effectiveDate", "Input_expirationDate"};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "countryCode"+expectMessage );
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
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	
	        	String formatEffectiveDate = effectiveDate;   
	        	String formatExpirationDate = expirationDate;   
	    		Date dateEffectiveDate = null;
	    		Date dateExpirationDate = null;
		            
		   		try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}

		   		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
	        	//***get the DB query
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Script service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***get the DB query
	        	
	        	
	        	String formatEffectiveDate = effectiveDate;   
	        	String formatExpirationDate = expirationDate;   
	    		Date dateEffectiveDate = null;
	    		Date dateExpirationDate = null;
		            
		   		try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}

		   		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
		   		
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	    				String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName:"};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("effectiveDate") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "effectiveDate"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "effectiveDate"+expectMessage );
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
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Script service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	
	        	if(effectiveDate.isEmpty()){
	        		effectiveDate = todaysDate;
	        	}else
	        	if(expirationDate.isEmpty()){
	        		expirationDate = "9999-12-31";
	        	}
	        	
	        	String  formatEffectiveDate = effectiveDate;
	        	String  formatExpirationDate = expirationDate;
	        	Date dateEffectiveDate = null;
	        	Date dateExpirationDate = null;
	        	
	        	try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   			
		   		}catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
	    		
	    		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
	        	//***get the DB query
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				if(expirationDate.isEmpty()){
	    					expirationDate="9999-12-31";
	    				}
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	    				String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.inValidFieldMsg;
			if(Wscode == 404 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "countryCode"+expectMessage );
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.inValidFieldMsg;
			if(Wscode == 404 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.invalidDateMsg;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", expectMessage );
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.invalidDateMsg;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", expectMessage );
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutMeta(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("meta") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutCntryCd(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

		    	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
	        			responsestr, "Fail", "countryCode"+expectMessage );
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
			String payload = PostMethod.cntryOrgStdPostRequestWithoutCntryShNm(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***get the DB query
	        	
	        	String formatEffectiveDate = effectiveDate;   
	        	String formatExpirationDate = expirationDate;   
	    		Date dateEffectiveDate = null;
	    		Date dateExpirationDate = null;
		            
		   		try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}

		   		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
		   		
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Script service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutCntryFNm(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***get the DB query
	        	
	        	String formatEffectiveDate = effectiveDate;   
	        	String formatExpirationDate = expirationDate;   
	    		Date dateEffectiveDate = null;
	    		Date dateExpirationDate = null;
		            
		   		try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   		} catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}

		   		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
		   		formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
		   		
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	    				String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName:"};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutOrgStdCd(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutEffectiveDate(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("effectiveDate") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "effectiveDate"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "effectiveDate"+expectMessage );
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
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		//test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Script service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
		boolean testResult=false;
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequestWithoutExpirationDate(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && meta != null && Wsstatus.equalsIgnoreCase("SUCCESS") && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	
	        	//***Converting dateFormat according to DB
	        	
	        	
	        	if(effectiveDate.isEmpty()){
	        		effectiveDate = todaysDate;
	        	}else
	        	if(expirationDate.isEmpty()){
	        		expirationDate = "9999-12-31";
	        	}
	        	
	        	String  formatEffectiveDate = effectiveDate;
	        	String  formatExpirationDate = expirationDate;
	        	Date dateEffectiveDate = null;
	        	Date dateExpirationDate = null;
	        	
	        	try {
		   			dateEffectiveDate = srcDf.parse(formatEffectiveDate);
		   			dateExpirationDate = srcDf.parse(formatExpirationDate);
		   			
		   		}catch (ParseException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
	    		
	    		formatEffectiveDate = destDf.format(dateEffectiveDate);            
		   		formatEffectiveDate = formatEffectiveDate.toUpperCase();
		   		
				formatExpirationDate = destDf.format(dateExpirationDate);            
		   		formatExpirationDate = formatExpirationDate.toUpperCase();
	        	//***get the DB query
	    		String scriptPostQuery = query.countryOrgStdPostQuery(countryCode, countryShortName, countryFullName, organizationStandardCode, formatEffectiveDate, formatExpirationDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.cntryOrgStdDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(scriptPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.organizationStandardCode")!=null && js.getString("data.geopoliticalId")!=null)
	    		{
	    			String organizationStandardCode1 = js.getString("data.organizationStandardCode");
	    			String geopId = js.getString("data.geopoliticalId");
	    			//***success message validation
	    			String expectMessage = resMsgs.countryOrgStdPutSuccessMsg+organizationStandardCode1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success message with Org STD Code is getting received as expected in response");
	    				test.pass("Success message with Org STD Code is getting received as expected in response");
	    				//***send the input, response, DB result for validation
	    				if(expirationDate.isEmpty()){
	    					expirationDate="9999-12-31";
	    				}
	    				String[] inputFieldValues = {geopId, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate, userId};
	            		//***get response fields values
	    				List<String> resFields = ValidationFields.cntryOrgStdResponseFileds(res);
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	    				String[] inputFieldNames = {"Response_GeoId: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
	    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: ", "Input_LastUpdateUserName:"};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_GeoId: ", "DB_CountryShortName: ", "DB_CountryFullame: ", 
	    						"DB_organizationStandardCode: ", "DB_effectiveDate: ", "DB_expirationDate: ", "DB_LastUpdateUserName: "};
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
	    				logger.error("Success message is not getting received as expected in response");
	    				test.fail("Success message is not getting received as expected in response");
	    				ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", 
	        					"Success message is not getting received as expected in response" );
	        			Assert.fail("Test Failed");
	    			}
	    		}else {
	    			logger.error("Org STD Code or Geop ID is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Org STD Code or Geop ID is not available in response");
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds10Char1;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "countryCode"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds65Char;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryShortName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryShortName"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "countryShortName"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds120Char;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("countryFullName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "countryFullName"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "countryFullName"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds10Char1;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("organizationStandardCode") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", "organizationStandardCode"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.invalidDateMsg;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String Wsstatus= res.getStatusLine();
			int errorMsgLength = js.get("errors.size");
			List<String> errorMsg1 = new ArrayList<>();
			List<String> errorMsg2 = new ArrayList<>();
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.invalidDateMsg;
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("Error") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
		        			responsestr, "Fail", expectMessage );
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
                   logger.error("Response validation failed as API version number is not matching with expected");
                   logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                   logger.error("------------------------------------------------------------------");
                            test.fail("Response validation failed as API version number is not matching with expected");       
          }

	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
						responsestr, "Fail", expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
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
			if(Wscode == 400 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion))
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
		       }else if((meta.contains("timestamp"))){
	        	logger.error("Response validation failed as timestamp is present");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp is present");
		       }else if(!actualRespVersionNum.equalsIgnoreCase(actuatorcommandversion)){
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
	public void TC_27()
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
			//***send the data to create request and get request
			String payload = PostMethod.cntryOrgStdPostRequest(userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".cntryOrgStd.put");
			getEndPoinUrl=getEndPoinUrl.substring(0, 97);
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
	        if(Wscode == 404)
		    {
	        	logger.info("Response status code 404 validation passed: "+Wscode);
	        	test.pass("Response status code 404 validation passed: "+Wscode);
	        	//***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if(internalMsg.equals(expectMessage))
				{
					String[] inputFieldValues = {userId, countryCode, countryShortName, countryFullName, organizationStandardCode, effectiveDate, expirationDate};
					String[] inputFieldNames = {"Input_UserName: ", "Input_CountryCode: ", "Input_CountryShortName: ", "Input_CountryFullame: ", 
    						"Input_organizationStandardCode: ", "Input_effectiveDate: ", "Input_expirationDate: "};
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
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
		    }else {
		    	logger.error("Response status code 404 validation failed: "+Wscode);
		    	logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.info("------------------------------------------------------------------");
	        	test.fail("Response status code 404 validation failed: "+Wscode);
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
	

	
	
	@Test(priority=1)
	public void TC_28()
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
			/*
			String str;    //  statement 1
		    str = new String("{" +
		    		"  \"data\": {" +
		    		"    \"geopoliticalId\": \"51010919412829160\"," +
		    		"    \"organizationStandardCode\":\"ISO\"," +
		    		"    \"countryCode\":\"N5\"," +
		    		"    \"countryFullName\":\" Unitaaa\"," +
		    		"    \"countryShortName\":\"pa\"," +
		    		"    \"effectiveDate\":\"2020-06-12\"," +
		    		"    \"expirationDate\":\"2020-06-30\"" +
		    		"   }" +
		    		"}");*/
			//***send request and get response
			JSONObject getJMSResult =jmsReader.messageGetsPublished("COUNTRY_ORG_STD");

		/*	String geopoliticalId=getJMSResult.getString("geopoliticalId");
			String orgStdNm=getJMSResult.getString("orgStdNm");
			String countryFullName=getJMSResult.getString("countryFullName");
			String countryShortName=getJMSResult.getString("countryShortName");
			String effectiveDate=getJMSResult.getString("effectiveDate");
			String expirationDate=getJMSResult.getString("expirationDate");*/
			
		//	JSONObject getJMSResult = new JSONObject(str);		    
		//	int  geopoliticalId1=getJMSResult.getJSONObject("data").getInt("geopoliticalId");
		if(getJMSResult!=null)
		{	
			String reqFormatted = Miscellaneous.jsonFormat(getJMSResult.toString());
			test.info("JMS Response Recieved:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			long geopoliticalId1=getJMSResult.getJSONObject("data").getLong("geopoliticalId");
			String geopoliticalId = Long.toString(geopoliticalId1) ;
			String orgStdNm=getJMSResult.getJSONObject("data").getString("organizationStandardCode");
		    //newly added attribute :countryCode
			String countryCode = getJMSResult.getJSONObject("data").getString("countryCode");
			String countryFullName=getJMSResult.getJSONObject("data").getString("countryFullName");			
			String countryShortName=getJMSResult.getJSONObject("data").getString("countryShortName");
			String effectiveDate=getJMSResult.getJSONObject("data").getString("effectiveDate");
			String expirationDate=getJMSResult.getJSONObject("data").getString("expirationDate");
			
			//*** converting date
			
			//***effective and exoiration Date
			String  formatEffectiveDate;   
        	Date dateEffectiveDate = null;     
        	
        	String  formatExpirationDate;   
        	Date dateExpirationDate = null;
        	
        	formatEffectiveDate=effectiveDate;
        	formatExpirationDate=expirationDate;
        	
        	dateEffectiveDate = srcDf.parse(formatEffectiveDate);
        	formatEffectiveDate = destDf.format(dateEffectiveDate);   
        	formatEffectiveDate = formatEffectiveDate.toUpperCase();
        	
        	dateExpirationDate = srcDf.parse(formatExpirationDate);
        	formatExpirationDate = destDf.format(dateExpirationDate);            
	   		formatExpirationDate = formatExpirationDate.toUpperCase();

			if(geopoliticalId!=null){
				//***get the DB query
				String countryOrgStdJMSQuery =query.countryOrgStdJMStQuery(countryCode,countryShortName, countryFullName, orgStdNm, formatEffectiveDate, formatExpirationDate);
				//***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.countryOrgStdGetMethodDbFieldsJMS();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(countryOrgStdJMSQuery, fields, fileName, testCaseID);
	    		String[] JMSValue = {geopoliticalId,orgStdNm,countryCode,countryFullName,countryShortName,effectiveDate,expirationDate};			
	    		testResult = TestResultValidation.testValidationForJMS(JMSValue,getResultDB) ;
	    		
	    		if(testResult){
	    			//***write result to excel
        			String[] responseDbFieldValues = {geopoliticalId,getResultDB.get(0),orgStdNm,getResultDB.get(1),countryCode,getResultDB.get(2),countryFullName,getResultDB.get(3),countryShortName,getResultDB.get(4),effectiveDate,getResultDB.get(5),expirationDate,getResultDB.get(6)};
        			String[] responseDbFieldNames = {"Response_geopoliticalId: ", "DB_geopoliticalId: ", 
        					"Response_orgStdNm ", "DB_orgStdNm: ", "Response_countryCode: ", "DB_countryCode: ", "Response_countryFullName: ", "DB_countryFullName: ","Response_countryShortName: ", "DB_countryShortName : ","Response_effectiveDate : ", "DB_effectiveDate : ","Response_expirationDate : ", "DB_expirationDate "};
        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);

	    			logger.info("Comparison between JMS response & DB data matching and passed");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	    			test.pass("Comparison between JMS response & DB data matching and passed");
	    			test.pass(writableResult.replaceAll("\n", "<br />"));  
        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
        					"", "", writableResult, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	    		}else{
	    			String[] responseDbFieldValues = {geopoliticalId,getResultDB.get(0),orgStdNm,getResultDB.get(1),countryCode,getResultDB.get(2),countryShortName,getResultDB.get(3),countryFullName,getResultDB.get(4),effectiveDate,getResultDB.get(5),expirationDate,getResultDB.get(6)};
        			String[] responseDbFieldNames = {"Response_geopoliticalId: ", "DB_geopoliticalId: ", 
        					"Response_orgStdNm ", "DB_orgStdNm: ", "Response_countryCode: ", "DB_countryCode: ", "Response_countryFullName: ", "DB_countryFullName: ","Response_countryShortName: ", "DB_countryShortName : ","Response_effectiveDate : ", "DB_effectiveDate : ","Response_expirationDate : ", "DB_expirationDate "};
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
    			logger.error("geopoliticalId is not available in response");
    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
    			logger.error("------------------------------------------------------------------");
				test.fail("geopoliticalId is not available in response");
    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "",
    					"", "", "", "", "", "Fail", "" );
    			Assert.fail("Test Failed");
			}
		} else
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
		countryCode = inputData1.get(testCaseId).get("countryCode");
		countryShortName = inputData1.get(testCaseId).get("countryShortName");
		countryFullName = inputData1.get(testCaseId).get("countryFullName");
		organizationStandardCode = inputData1.get(testCaseId).get("organizationStandardCode");
		effectiveDate = inputData1.get(testCaseId).get("effectiveDate");
		expirationDate = inputData1.get(testCaseId).get("expirationDate");
	}


}
