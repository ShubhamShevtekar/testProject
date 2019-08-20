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

public class LanguagePut extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, langCd, englLanguageNm, nativeScriptLanguageNm;
	HashMap<String,String> translatedDOWs;
	HashMap<String,String> translatedMOYs;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDB_Fields=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(LanguagePut.class);
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file
		ex.createResultExcel(fileName);
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
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB2);
	        		
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB3);
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[42];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;	
	    	    		
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm,translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 &&  meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				
	        	if(errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage))
				{
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when sending the blank LangCode");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank LangCode");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if((errorMsg1.get(0).equals("dowNbr") || errorMsg1.get(1).equals("dowNbr")) && errorMsg2.get(0).equals(expectMessage)
					&& (errorMsg1.get(0).equals("transDowName") || errorMsg1.get(1).equals("transDowName")) && errorMsg2.get(1).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when sending the blank DoWNbr");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank DoWNbr");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "dowNbr"+expectMessage  );
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
						responsestr, "Fail", "dowNbr"+expectMessage  );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 &&  meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if((errorMsg1.get(0).equals("mthOfYrNbr") || errorMsg1.get(1).equals("mthOfYrNbr")) && errorMsg2.get(0).equals(expectMessage)
						&& (errorMsg1.get(0).equals("transMoyName") || errorMsg1.get(1).equals("transMoyName")) && errorMsg2.get(1).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when sending the blank MoYNbr");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank MoYNbr");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "mthOfYrNbr"+expectMessage );
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
						responsestr, "Fail", "mthOfYrNbr"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 &&  meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when sending the blank englLangNm");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the blank englLangNm");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "englLanguageNm"+expectMessage );
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
						responsestr, "Fail", "englLanguageNm"+expectMessage );
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
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB2);
	        		
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB3);
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[43];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;	
	    	    		
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequestWithoutDOW(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		if(getResultDB2.isEmpty()){
	        			getResultDBFinal.add("");
	        			getResultDBFinal.add("");
	        		}else{
	        			getResultDBFinal.addAll(getResultDB2);
	        		}
	        		
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB3);
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[43];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;
	    	    		
	    	    			
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
	public void TC_08()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequestWithoutMOY(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB2);
	        		
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		if(getResultDB3.isEmpty()){
	        			getResultDBFinal.add("");
	        			getResultDBFinal.add("");
	        		}else{
	        			getResultDBFinal.addAll(getResultDB3);
	        		}
	        		
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[43];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;
	    	    			
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
	public void TC_09()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();	
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequestWithoutDOWAndMOY(userId, langCd, englLanguageNm, nativeScriptLanguageNm);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		if(getResultDB2.isEmpty()){
	        			getResultDBFinal.add("");
	        			getResultDBFinal.add("");
	        		}else{
	        			getResultDBFinal.addAll(getResultDB2);
	        		}
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		if(getResultDB3.isEmpty()){
	        			getResultDBFinal.add("");
	        			getResultDBFinal.add("");
	        		}else{
	        			getResultDBFinal.addAll(getResultDB3);
	        		}
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[43];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;
	    	    			
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequestWithoutLangCd(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when englLanguageNm is not passed in JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when englLanguageNm is not passed in JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "langCd"+expectMessage );
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
						responsestr, "Fail", "langCd"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutEnglLangNm(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when nativeScriptLanguageNm is not passed in JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when nativeScriptLanguageNm is not passed in JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "englLanguageNm"+expectMessage );
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
						responsestr, "Fail", "englLanguageNm"+expectMessage );
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
		List<String> getResultDBFinal = new ArrayList<String>();
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
			String payload = PostMethod.langPostRequestWithoutNatScrptLangNm(userId, langCd, englLanguageNm, nativeScriptLanguageNm,translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
	        	
	        	//Query1
	    		String langPostPostQuery1 = query.langPostQuery(langCd);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		fields.remove(0);//***removing user name field since we are going to validate only last updated user name
	    		//***get the result from DB
	    		List<String> getResultDB1 = DbConnect.getResultSetFor(langPostPostQuery1, fields, fileName, testCaseID);
	    		getResultDBFinal.addAll(getResultDB1);
	    		
	    		//Query2
	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    			
	    			String languagePostQuery2 = query.langTrnslDowPostQuery(langCd,translatedDOWs.get("translatedDOWs_dowNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields2 = ValidationFields.langTrnslDowDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB2 = DbConnect.getResultSetFor(languagePostQuery2, fields2, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB2);
	        		
	    		}
	    		
	    		//Query3
	    		for(int i=0;i<translatedMOYs.size()/2;i++){
	    			
	        	 	String languagePostQuery3 = query.langTrnslMonthOfYearPostQuery(langCd,translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1)));
	        		//***get the fields needs to be validate in DB
	        		List<String> fields3 = ValidationFields.langTrnslMonthOfYearDbFields();
	        		//***get the result from DB
	        		List<String> getResultDB3 = DbConnect.getResultSetFor(languagePostQuery3, fields3, fileName, testCaseID);
	        		getResultDBFinal.addAll(getResultDB3);
	        		
	    		}
	    		
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPutSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	    				String[] inputFieldValues = new String[42];
	    	    		
	    	    		inputFieldValues[0] = langCd;
	    	    		inputFieldValues[1] = englLanguageNm;
	    	    		inputFieldValues[2] = nativeScriptLanguageNm;
	    	    		inputFieldValues[3] = userId;	
	    	    		
	    	    		int j = 4;
	    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
	    	    			
	    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
	    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
	    	    			j+=2;    			
	    	    			
	    	    		}
	    	    		
	    	    		int k = 18;
	    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
	    	  			
	    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
	    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
	    	  			   k+=2; 
	    	  		    }
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDBFinal, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:","Input_LastUpdateUserName: "
	            					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
	            					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
	            					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
	            					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
	            					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
	            					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
	            					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
	            					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
	            					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
	            					,"Input_mthOfYrNbr12:","Input_transMoyName12:"};
	            		
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		
	            		String[] dbFieldNames = {"DB_langCd: ", "DB_englLanguageNm: ","DB_nativeScriptLanguageNm:","DB_LastUpdateUserName: "
            					,"DB_dowNbr1:","DB_transDowName1:","DB_dowNbr2:","DB_transDowName2:"
            					,"DB_dowNbr3:","DB_transDowName3:","DB_dowNbr4:","DB_transDowName4:"
            					,"DB_dowNbr5:","DB_transDowName5:","DB_dowNbr6:","DB_transDowName6:"
            					,"DB_dowNbr7:","DB_transDowName7:","DB_mthOfYrNbr1:","DB_transMoyName1:"
            					,"DB_mthOfYrNbr2:","DB_transMoyName2:","DB_mthOfYrNbr3:","DB_transMoyName3:"
            					,"DB_mthOfYrNbr4:","DB_transMoyName4:","DB_mthOfYrNbr5:","DB_transMoyName5:"
            					,"DB_mthOfYrNbr6:","DB_transMoyName6:","DB_mthOfYrNbr7:","DB_transMoyName7:"
            					,"DB_mthOfYrNbr8:","DB_transMoyName8:","DB_mthOfYrNbr9:","DB_transMoyName9:"
            					,"DB_mthOfYrNbr10:","DB_transMoyName10:","DB_mthOfYrNbr11:","DB_transMoyName11:"
            					,"DB_mthOfYrNbr12:","DB_transMoyName12:"};
	            		
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDBFinal, dbFieldNames);
	            		test.info("Input Data Values:");
	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
	            		test.info("DB Data Values:");
	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
	            		if(testResult)
	            		{
	            			logger.info("Comparison between input data & DB data matching and passed");
	            			logger.info("------------------------------------------------------------------");
	            			test.pass("Comparison between input data & DB data matching and passed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
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
	    			logger.error("langCd  is not available in response");
	    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
	    			test.fail("langCd is not available in response");
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
			String payload = PostMethod.langPostRequestWithoutUserNm(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when the user name is not passed in JSON");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when the user name is not passed in JSON");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequestWithoutMeta(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.requiredFieldMsg;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
			getEndPoinUrl=getEndPoinUrl.substring(0, 94);
			//***send request and get response
			Response res = GetResponse.sendRequestPut(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String timestamp = js.getString("timestamp");
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
				if(Wscode == 404 && timestamp != null)
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
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
		    	if(Wscode!=404){
	        		logger.error("Response status validation failed: "+Wscode);
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response status validation failed: "+Wscode);
		       	}else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
		       }
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds3Char;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("langCd") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when languageCd is more than 3 characters in JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when languageCd is more than 3 characters in JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "langCd"+expectMessage );
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
						responsestr, "Fail", "langCd"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds256Char1;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("englLanguageNm") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when englLanguageNm is more than 256 characters in JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when englLanguageNm is more than 256 characters in JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "englLanguageNm"+expectMessage );
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
						responsestr, "Fail", "englLanguageNm"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds256Char1;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when nativeScriptLanguageNm is more than 256 characters in JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when nativeScriptLanguageNm is more than 256 characters in JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds38Char;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("dowNbr") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when Downbr is more than 38 characters JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when Downbr is more than 38 characters JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	 		String expectMessage = resMsgs.lengthExceeds256Char1;
	        int Wscode= res.statusCode();
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("transDowName") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when DownNm is more than 256 characters JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when DownNm is more than 256 characters JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "transDowName"+expectMessage );
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
						responsestr, "Fail", "transDowName"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds38Char;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
	        	if(errorMsg1.get(0).equals("mthOfYrNbr") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when mthOfYrNbr is more than 38 characters JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when mthOfYrNbr is more than 38 characters JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds65Char;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("transMoyName") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
	        		logger.info("Expected error message is getting received in response when transMoy is more than 65 characters JSON request");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when transMoy is more than 65 characters JSON request");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "transMoyName"+expectMessage );
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
						responsestr, "Fail", "transMoyName"+expectMessage );
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
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			//***send the data to create request and get request
			String payload = PostMethod.langPostRequest(userId, langCd, englLanguageNm, nativeScriptLanguageNm, translatedDOWs, translatedMOYs);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPut", fileName, level+".lang.put");
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
			for(int i=0; i<errorMsgLength; i++){
				errorMsg1.add(js.getString("errors["+i+"].fieldName"));
				errorMsg2.add(js.getString("errors["+i+"].message"));
			}
	        int Wscode= res.statusCode();
	        String expectMessage = resMsgs.lengthExceeds25Char;
	        if(Wscode == 400 && meta != null && timestamp != null)
		    {
	        	logger.info("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response status code 400 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				if(errorMsg1.get(0).equals("userName") && errorMsg2.get(0).equals(expectMessage))
				{
					
					String[] inputFieldValues = new String[43];
    	    		
    	    		inputFieldValues[0] = userId;
    	    		inputFieldValues[1] = langCd;
    	    		inputFieldValues[2] = englLanguageNm;
    	    		inputFieldValues[3] = nativeScriptLanguageNm;
    	    			
    	    		int j = 4;
    	    		for(int i=0;i<translatedDOWs.size()/2;i++){
    	    			
    	    			inputFieldValues[j] = translatedDOWs.get("translatedDOWs_dowNbr"+(i+1));
    	    			inputFieldValues[j+1] = translatedDOWs.get("translatedDOWs_transDowName"+(i+1));
    	    			j+=2;    			
    	    			
    	    		}
    	    		
    	    		int k = 18;
    	  		    for(int i=0;i<translatedMOYs.size()/2;i++){
    	  			
    	  			   inputFieldValues[k] = translatedMOYs.get("translatedMOYs_mthOfYrNbr"+(i+1));
    	  			   inputFieldValues[k+1] = translatedMOYs.get("translatedMOYs_transMoyName"+(i+1));
    	  			   k+=2; 
    	  		    }
    	  		    inputFieldValues[42] = userId;
					
    	  		  String[] inputFieldNames = {"Input_UserName: ","Input_langCd: ", "Input_englLanguageNm: ","Input_nativeScriptLanguageNm:"
      					,"Input_dowNbr1:","Input_transDowName1:","Input_dowNbr2:","Input_transDowName2:"
      					,"Input_dowNbr3:","Input_transDowName3:","Input_dowNbr4:","Input_transDowName4:"
      					,"Input_dowNbr5:","Input_transDowName5:","Input_dowNbr6:","Input_transDowName6:"
      					,"Input_dowNbr7:","Input_transDowName7:","Input_mthOfYrNbr1:","Input_transMoyName1:"
      					,"Input_mthOfYrNbr2:","Input_transMoyName2:","Input_mthOfYrNbr3:","Input_transMoyName3:"
      					,"Input_mthOfYrNbr4:","Input_transMoyName4:","Input_mthOfYrNbr5:","Input_transMoyName5:"
      					,"Input_mthOfYrNbr6:","Input_transMoyName6:","Input_mthOfYrNbr7:","Input_transMoyName7:"
      					,"Input_mthOfYrNbr8:","Input_transMoyName8:","Input_mthOfYrNbr9:","Input_transMoyName9:"
      					,"Input_mthOfYrNbr10:","Input_transMoyName10:","Input_mthOfYrNbr11:","Input_transMoyName11:"
      					,"Input_mthOfYrNbr12:","Input_transMoyName12:","Input_LastUpdateUserName: "};
    	  		  
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		test.info("Input Data Values:");
            		test.info(writableInputFields.replaceAll("\n", "<br />"));
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
		       }else if(timestamp == null){
	        	logger.error("Response validation failed as timestamp not found");
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response validation failed as timestamp not found");
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
	
	
	
	//***get the values from test data sheet
	public void testDataFields(String scenarioName, String testCaseId)
	{
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		translatedDOWs = new HashMap<String,String>();
		translatedMOYs = new HashMap<String,String>();
		
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
		langCd = inputData1.get(testCaseId).get("langCd");
		englLanguageNm = inputData1.get(testCaseId).get("englLanguageNm");
		nativeScriptLanguageNm = inputData1.get(testCaseId).get("nativeScriptLanguageNm");
		
		for(int i=1;i<=7;i++){
			
			translatedDOWs.put("translatedDOWs_dowNbr"+i, inputData1.get(testCaseId).get("dowNbr"+i));
			translatedDOWs.put("translatedDOWs_transDowName"+i, inputData1.get(testCaseId).get("transDowName"+i));
			
		}
		
		for (int i = 1; i <= 12; i++) {

			translatedMOYs.put("translatedMOYs_mthOfYrNbr" + i,inputData1.get(testCaseId).get("mthOfYrNbr" + i));
			translatedMOYs.put("translatedMOYs_transMoyName" + i,inputData1.get(testCaseId).get("transMoyName" + i));

		}
	}
}
