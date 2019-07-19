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

public class LanguagePost extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, userId, langCd, languageNm, languageDesc, nativeScriptLanguageNm, threeCharLangCd, scrptCd;
	String dowNbr1, transDowName1, dowNbr2, transDowName2, dowNbr3, transDowName3, dowNbr4, transDowName4, dowNbr5, transDowName5, dowNbr6, transDowName6, dowNbr7, transDowName7;
	String mthOfYrNbr1, transMoyName1, mthOfYrNbr2, transMoyName2, mthOfYrNbr3, transMoyName3, mthOfYrNbr4, transMoyName4, mthOfYrNbr5, transMoyName5, mthOfYrNbr6, transMoyName6, 
	mthOfYrNbr8, transMoyName8, mthOfYrNbr9, transMoyName9, mthOfYrNbr7, transMoyName7, mthOfYrNbr10, transMoyName10, mthOfYrNbr11, transMoyName11, mthOfYrNbr12, transMoyName12;
	Queries query = new Queries();
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
			String payload = PostMethod.langPostRequest(userId, langCd, languageNm, languageDesc, nativeScriptLanguageNm, threeCharLangCd, scrptCd,
					dowNbr1, transDowName1, dowNbr2, transDowName2, dowNbr3, transDowName3, dowNbr4, transDowName4, dowNbr5, transDowName5, dowNbr6, transDowName6, dowNbr7, transDowName7,
					mthOfYrNbr1, transMoyName1, mthOfYrNbr2, transMoyName2, mthOfYrNbr3, transMoyName3, mthOfYrNbr4, transMoyName4, mthOfYrNbr5, transMoyName5, mthOfYrNbr6, transMoyName6, 
					mthOfYrNbr8, transMoyName8, mthOfYrNbr9, transMoyName9, mthOfYrNbr7, transMoyName7, mthOfYrNbr10, transMoyName10, mthOfYrNbr11, transMoyName11, mthOfYrNbr12, transMoyName12);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			System.out.println(reqFormatted);
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoPost", fileName, level+".lang.post");
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
	    		String langPostPostQuery = query.langPostQuery(langCd);
	    		String langTrnslMonthOfYearPostQuery = query.langTrnslMonthOfYearPostQuery(langCd, mthOfYrNbr1);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.langDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(langPostPostQuery, fields, fileName, testCaseID);
	    		if(js.getString("data.langCd")!=null)
	    		{
	    			String langCd1 = js.getString("data.langCd");
	    			//***success message validation
	    			String expectMessage = resMsgs.langPostSuccessMsg+langCd1;
	    			if(internalMsg.equals(expectMessage))
	    			{
	    				logger.info("Success response is getting received with Language Code: "+langCd);
	    				test.pass("Success response is getting received with Language Code: "+langCd);
	    				//***send the input, response, DB result for validation
	            		String[] inputFieldValues = {userId, langCd, languageNm, languageDesc, nativeScriptLanguageNm, threeCharLangCd, scrptCd, userId};
	            		//***get response fields values
	            		List<String> resFields = ValidationFields.langResponseFileds(res);
	            		logger.info("Language Table Validation Starts:");
            			test.info("Language Table Validation Starts:");
	            		testResult = TestResultValidation.testValidationWithDB(res, inputFieldValues, getResultDB, resFields);
	            		//***write result to excel
	            		String[] inputFieldNames = {"Input_UserName: ", "Input_langCd: ", "Input_languageNm: ", "Input_languageDesc: ", "Input_nativeScriptLanguageNm: ",
	            				"Input_threeCharLangCd: ", "Input_scrptCd: ", "Input_LastUpdateUserName: "};
	            		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	            		String[] dbFieldNames = {"DB_UserName: ", "DB_langCd: ", "DB_languageNm: ", "DB_languageDesc: ", "DB_nativeScriptLanguageNm: ", 
	            				"DB_threeCharLangCd: ", "DB_scrptCd: ", "DB_LastUpdateUserName: "};
	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getResultDB, dbFieldNames);
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
	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
	            		}else{
	            			logger.error("Comparison between input data & DB data not matching and failed");
	            			logger.error("------------------------------------------------------------------");
	            			test.fail("Comparison between input data & DB data not matching and failed");
	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
	            			Assert.fail("Test Failed");
	            		}
	            		testResult=false;
	            		logger.info("TRNSL_DOW Table Validation Starts:");
            			test.info("TRNSL_DOW Table Validation Starts:");
            			//***Add the input values to array to loop the table row validations
            			String[] dowNbrArray = {dowNbr1, dowNbr2, dowNbr3, dowNbr4, dowNbr5, dowNbr6, dowNbr7};
            			String[] transDowNameArray = {transDowName1, transDowName2, transDowName3, transDowName4, transDowName5, transDowName6, transDowName7};
            			for(int i=0; i<=6; i++)
            			{
            				//***Get the DB query
            				String langTrnslDowPostQuery = query.langTrnslDowPostQuery(langCd, dowNbrArray[i]);
            				//***Input fields
            				String[] trnslDowInputFields = {userId, dowNbrArray[i], langCd, transDowNameArray[i], userId};
            				//***get the fields needs to be validate in DB
            	    		List<String> langTrnslDowFields = ValidationFields.langTrnslDowDbFields();
            	    		//***get the result from DB
            	    		List<String> getTrnslDowResultDB = DbConnect.getResultSetFor(langTrnslDowPostQuery, langTrnslDowFields, fileName, testCaseID);
            	    		testResult = TestResultValidation.testValidationWithDB(res, trnslDowInputFields, getTrnslDowResultDB, resFields);
            	    		//***write result to excel
    	            		String[] inputFieldNames1 = {"Input_UserName: ", "Input_dowNbr: ", "Input_langCd: ", "Input_transDowName: ", "Input_LastUpdateUserName: "};
    	            		writableInputFields = Miscellaneous.geoFieldInputNames(trnslDowInputFields, inputFieldNames1);
    	            		String[] dbFieldNames1 = {"DB_UserName: ", "DB_dowNbr: ", "DB_langCd: ", "DB_transDowName: ", "DB_LastUpdateUserName: "};
    	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(getTrnslDowResultDB, dbFieldNames1);
    	            		test.info("TRNSL_DOW Input Data Values:");
    	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
    	            		test.info("TRNSL_DOW DB Data Values:");
    	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
    	            		if(testResult)
    	            		{
    	            			logger.info("Comparison between input data & DB data matching and passed");
    	            			logger.info("------------------------------------------------------------------");
    	            			test.pass("Comparison between input data & DB data matching and passed");
    	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
    	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
    	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
    	            		}else{
    	            			logger.error("Comparison between input data & DB data not matching and failed");
    	            			logger.error("------------------------------------------------------------------");
    	            			test.fail("Comparison between input data & DB data not matching and failed");
    	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
    	            			Assert.fail("Test Failed");
    	            		}
            			}
            			testResult=false;
	            		logger.info("TRNSL_MTH_OF_YR Table Validation Starts:");
            			test.info("TRNSL_MTH_OF_YR Table Validation Starts:");
            			//***Add the input values to array to loop the table row validations
            			String[] mthOfYrNbrArray = {mthOfYrNbr1, mthOfYrNbr2, mthOfYrNbr3, mthOfYrNbr4, mthOfYrNbr5, mthOfYrNbr6, mthOfYrNbr7, mthOfYrNbr8, 
            					mthOfYrNbr9, mthOfYrNbr10, mthOfYrNbr11, mthOfYrNbr12};
            			String[] translMoyNameArray = {transMoyName1, transMoyName2, transMoyName3, transMoyName4, transMoyName5, transMoyName6, transMoyName7, transMoyName8, 
            					transMoyName9, transMoyName10, transMoyName11, transMoyName12};
            			for(int i=0; i<=11; i++)
            			{
            				//***Get the DB query
            				String translMoyNamePostQuery = query.langTrnslMonthOfYearPostQuery(langCd, mthOfYrNbrArray[i]);
            				//***Input fields
            				String[] translMoyNameInputFields = {userId, mthOfYrNbrArray[i], langCd, translMoyNameArray[i], userId};
            				//***get the fields needs to be validate in DB
            				List<String> translMoyNameFields = ValidationFields.langTrnslMonthOfYearDbFields();
            	    		//***get the result from DB
            				List<String> translMoyNameResultDB = DbConnect.getResultSetFor(translMoyNamePostQuery, translMoyNameFields, fileName, testCaseID);
            	    		testResult = TestResultValidation.testValidationWithDB(res, translMoyNameInputFields, translMoyNameResultDB, resFields);
            	    		//***write result to excel
    	            		String[] inputFieldNames2 = {"Input_UserName: ", "Input_mthOfYrNbr: ", "Input_langCd: ", "Input_transMoyName: ", "Input_LastUpdateUserName: "};
    	            		writableInputFields = Miscellaneous.geoFieldInputNames(translMoyNameInputFields, inputFieldNames2);
    	            		String[] dbFieldNames2 = {"DB_UserName: ", "DB_mthOfYrNbr: ", "DB_langCd: ", "DB_transMoyName: ", "DB_LastUpdateUserName: "};
    	            		writableDB_Fields = Miscellaneous.geoDBFieldNames(translMoyNameResultDB, dbFieldNames2);
    	            		test.info("TRNSL_MTH_OF_YR Input Data Values:");
    	            		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
    	            		test.info("TRNSL_MTH_OF_YR DB Data Values:");
    	            		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
    	            		if(testResult)
    	            		{
    	            			logger.info("Comparison between input data & DB data matching and passed");
    	            			logger.info("------------------------------------------------------------------");
    	            			test.pass("Comparison between input data & DB data matching and passed");
    	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, writableDB_Fields,
    	            					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
    	        				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
    	            		}else{
    	            			logger.error("Comparison between input data & DB data not matching and failed");
    	            			logger.error("------------------------------------------------------------------");
    	            			test.fail("Comparison between input data & DB data not matching and failed");
    	            			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
    	            					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "Comparison between input data & DB data not matching and failed" );
    	            			Assert.fail("Test Failed");
    	            		}
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
		langCd = inputData1.get(testCaseId).get("langCd");
		languageNm = inputData1.get(testCaseId).get("languageNm");
		languageDesc = inputData1.get(testCaseId).get("languageDesc");
		nativeScriptLanguageNm = inputData1.get(testCaseId).get("nativeScriptLanguageNm");
		threeCharLangCd = inputData1.get(testCaseId).get("threeCharLangCd");
		scrptCd = inputData1.get(testCaseId).get("scrptCd");
		dowNbr1 = inputData1.get(testCaseId).get("dowNbr1");
		transDowName1 = inputData1.get(testCaseId).get("transDowName1");
		dowNbr2 = inputData1.get(testCaseId).get("dowNbr2");
		transDowName2 = inputData1.get(testCaseId).get("transDowName2");
		dowNbr3 = inputData1.get(testCaseId).get("dowNbr3");
		transDowName3 = inputData1.get(testCaseId).get("transDowName3");
		dowNbr4 = inputData1.get(testCaseId).get("dowNbr4");
		transDowName4 = inputData1.get(testCaseId).get("transDowName4");
		dowNbr5 = inputData1.get(testCaseId).get("dowNbr5");
		transDowName5 = inputData1.get(testCaseId).get("transDowName5");
		dowNbr6 = inputData1.get(testCaseId).get("dowNbr6");
		transDowName6 = inputData1.get(testCaseId).get("transDowName6");
		dowNbr7 = inputData1.get(testCaseId).get("dowNbr7");
		transDowName7 = inputData1.get(testCaseId).get("transDowName7");
		mthOfYrNbr1 = inputData1.get(testCaseId).get("mthOfYrNbr1");
		transMoyName1 = inputData1.get(testCaseId).get("transMoyName1");
		mthOfYrNbr2 = inputData1.get(testCaseId).get("mthOfYrNbr2");
		transMoyName2 = inputData1.get(testCaseId).get("transMoyName2");
		mthOfYrNbr3 = inputData1.get(testCaseId).get("mthOfYrNbr3");
		transMoyName3 = inputData1.get(testCaseId).get("transMoyName3");
		mthOfYrNbr4 = inputData1.get(testCaseId).get("mthOfYrNbr4");
		transMoyName4 = inputData1.get(testCaseId).get("transMoyName4");
		mthOfYrNbr5 = inputData1.get(testCaseId).get("mthOfYrNbr5");
		transMoyName5 = inputData1.get(testCaseId).get("transMoyName5");
		mthOfYrNbr6 = inputData1.get(testCaseId).get("mthOfYrNbr6");
		transMoyName6 = inputData1.get(testCaseId).get("transMoyName6");
		mthOfYrNbr7 = inputData1.get(testCaseId).get("mthOfYrNbr7");
		transMoyName7 = inputData1.get(testCaseId).get("transMoyName7");
		mthOfYrNbr8 = inputData1.get(testCaseId).get("mthOfYrNbr8");
		transMoyName8 = inputData1.get(testCaseId).get("transMoyName8");
		mthOfYrNbr9 = inputData1.get(testCaseId).get("mthOfYrNbr9");
		transMoyName9 = inputData1.get(testCaseId).get("transMoyName9");
		mthOfYrNbr10 = inputData1.get(testCaseId).get("mthOfYrNbr10");
		transMoyName10 = inputData1.get(testCaseId).get("transMoyName10");
		mthOfYrNbr11 = inputData1.get(testCaseId).get("mthOfYrNbr11");
		transMoyName11 = inputData1.get(testCaseId).get("transMoyName11");
		mthOfYrNbr12 = inputData1.get(testCaseId).get("mthOfYrNbr12");
		transMoyName12 = inputData1.get(testCaseId).get("transMoyName12");
		
	}

}
