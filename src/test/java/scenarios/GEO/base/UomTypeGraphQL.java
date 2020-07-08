package scenarios.GEO.base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import utils.base.DbConnect;
import utils.base.ExcelUtil;
import utils.base.Miscellaneous;
import utils.base.Queries;
import utils.base.Reporting;
import utils.base.ResponseMessages;
import utils.base.RetrieveEndPoints;
import utils.base.TestResultValidation;
import utils.base.ValidationFields;
import wsMethods.base.GetResponse;
import wsMethods.base.PostMethod;

public class UomTypeGraphQL extends Reporting {
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, uomTypeCode, uomTypeName, uomTypeDesc;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableDBFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(UomTypeGraphQL.class);
	String actuatorGraphQLversion;
	TestResultValidation resultValidation = new TestResultValidation();
	@BeforeClass
	public void before() {
		DOMConfigurator.configure("log4j.xml");
		// ***create test result excel file
		ex.createResultExcel(fileName);
		// *** getting actautor version
		String tokenKey = tokenValues[0];
		String tokenVal = token;
		//String actuatorGraphQLVersionURL=RetrieveEndPoints.getEndPointUrl("graphQLActuator", fileName, level+".graphQL.version");
		//actuatorGraphQLversion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorGraphQLVersionURL);
	}

	@BeforeMethod
	protected void startRepo(Method m) throws IOException {
		runFlag = getExecutionFlag(m.getName(), fileName);
		if (runFlag.equalsIgnoreCase("Yes")) {
			String testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
	}

	@Test
	public void TC_01() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithoutParam();
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			// String Wsstatus= res.getStatusLine();
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.uomTypes");
			List<String> responseRows = js.get("data.uomTypes");
			int Wscode = res.statusCode();
			 String actualRespVersionNum = js.getString("meta.version"); 
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {

	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	//***get the DB query
	    		String uomTypeGetQuery = query.uomTypeGetQuery();
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.uomTypeGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypeGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeDesc")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeDesc"));
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()))
		        		{
		        			//***write result to excel
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
		        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			z++;
		        		}else {
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
		        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Fail", "This record is not matching" );
		        			z++;
		        		}
		        	}
	    		}else {
		        	logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
			  }else {
		        	if(Wscode!=200){
				        logger.error("Response status validation failed: "+Wscode);
				        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				        logger.error("------------------------------------------------------------------");
				        test.fail("Response status validation failed: "+Wscode);
				        }
			    	  else if(meta == null){
			  	        logger.error("Response validation failed as meta not found");
			  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			  	        logger.error("------------------------------------------------------------------");
			  	        test.fail("Response validation failed as meta not found");
			  	        }else if(meta.contains("timestamp")){
				        	logger.error("Response validation failed as timestamp found");
				    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    		logger.error("------------------------------------------------------------------");
				        	test.fail("Response validation failed as timestamp found");
				           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					        	logger.error("Response validation failed as API version number is not matching with expected");
				    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    			logger.error("------------------------------------------------------------------");
								test.fail("Response validation failed as API version number is not matching with expected");	
					        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
				logger.info("------------------------------------------------------------------");
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
	public void TC_02() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithParam(uomTypeCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.uomTypes");
			List<String> responseRows = js.get("data.uomTypes");
			int Wscode = res.statusCode();
			 String actualRespVersionNum = js.getString("meta.version"); 
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {

	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	//***get the DB query
	    		String uomTypeGetQuery = query.uomTypePostQuery(uomTypeCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.uomTypeGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypeGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeDesc")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeDesc"));
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()))
		        		{
		        			//***write result to excel
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
		        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			z++;
		        		}else {
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
		        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Fail", "This record is not matching" );
		        			z++;
		        		}
		        	}
	    		}else {
		        	logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
			  }else {
		        	if(Wscode!=200){
				        logger.error("Response status validation failed: "+Wscode);
				        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				        logger.error("------------------------------------------------------------------");
				        test.fail("Response status validation failed: "+Wscode);
				        }
			    	  else if(meta == null){
			  	        logger.error("Response validation failed as meta not found");
			  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			  	        logger.error("------------------------------------------------------------------");
			  	        test.fail("Response validation failed as meta not found");
			  	        }else if(meta.contains("timestamp")){
				        	logger.error("Response validation failed as timestamp found");
				    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    		logger.error("------------------------------------------------------------------");
				        	test.fail("Response validation failed as timestamp found");
				           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					        	logger.error("Response validation failed as API version number is not matching with expected");
				    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    			logger.error("------------------------------------------------------------------");
								test.fail("Response validation failed as API version number is not matching with expected");	
					        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
				logger.info("------------------------------------------------------------------");
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
	public void TC_03() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithoutUomTypeCd(uomTypeCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.uomTypes");
			List<String> responseRows = js.get("data.uomTypes");
			int Wscode = res.statusCode();
			 String actualRespVersionNum = js.getString("meta.version"); 
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {

	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	//***get the DB query
	    		String uomTypeGetQuery = query.uomTypePostQuery(uomTypeCode);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.uomTypeGetMethodDbFields();
	    		fields.remove(0);
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(uomTypeGetQuery, fields, fileName, testCaseID);
	    		//getResultDB.remove(0);
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data.uomTypes["+i+"].uomTypeDesc")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data.uomTypes["+i+"].uomTypeDesc"));
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) && getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()))
		        		{
		        			//***write result to excel
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			z++;
		        		}else {
		        			String[] responseDbFieldValues = {getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()};
		        			String[] responseDbFieldNames = {"Response_uomTypeCd: ", "DB_uomTypeCd: ", 
		        					"Response_uomTypeNm: ", "DB_uomTypeNm: ", "Response_uomTypeDesc: ", "DB_uomTypeDesc: "};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	"", "",
	            					"", "", writableResult, "Fail", "This record is not matching" );
		        			z++;
		        		}
		        	}
	    		}else {
		        	logger.error("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Total number of records matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
			  }else {
		        	if(Wscode!=200){
				        logger.error("Response status validation failed: "+Wscode);
				        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				        logger.error("------------------------------------------------------------------");
				        test.fail("Response status validation failed: "+Wscode);
				        }
			    	  else if(meta == null){
			  	        logger.error("Response validation failed as meta not found");
			  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			  	        logger.error("------------------------------------------------------------------");
			  	        test.fail("Response validation failed as meta not found");
			  	        }else if(meta.contains("timestamp")){
				        	logger.error("Response validation failed as timestamp found");
				    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    		logger.error("------------------------------------------------------------------");
				        	test.fail("Response validation failed as timestamp found");
				           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					        	logger.error("Response validation failed as API version number is not matching with expected");
				    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    			logger.error("------------------------------------------------------------------");
								test.fail("Response validation failed as API version number is not matching with expected");	
					        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
				logger.info("------------------------------------------------------------------");
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
	public void TC_04() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithParam(uomTypeCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			
			String meta = js.getString("meta");
			String Wsstatus = js.getString("meta.status");
			String internalMsg = js.getString("data.uomTypes");
			List<String> responseRows = js.get("data.uomTypes");
			int Wscode = res.statusCode();
			 String actualRespVersionNum = js.getString("meta.version"); 
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed"); 
	        	test.pass("Response API version number validation passed"); 

	        	if(responseRows.size() == 0)
				{
					String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("No record is getting fetched for the given invalid UOM Type Code");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("No record is getting fetched for the given invalid UOM Type Code");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd" );
		        	Assert.fail("Test Failed");
		        }
			  }else {
		        	if(Wscode!=200){
				        logger.error("Response status validation failed: "+Wscode);
				        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				        logger.error("------------------------------------------------------------------");
				        test.fail("Response status validation failed: "+Wscode);
				        }
			    	  else if(meta == null){
			  	        logger.error("Response validation failed as meta not found");
			  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			  	        logger.error("------------------------------------------------------------------");
			  	        test.fail("Response validation failed as meta not found");
			  	        }else if(meta.contains("timestamp")){
				        	logger.error("Response validation failed as timestamp found");
				    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    		logger.error("------------------------------------------------------------------");
				        	test.fail("Response validation failed as timestamp found");
				           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
					        	logger.error("Response validation failed as API version number is not matching with expected");
				    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				    			logger.error("------------------------------------------------------------------");
								test.fail("Response validation failed as API version number is not matching with expected");	
					        	}
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
				logger.info("------------------------------------------------------------------");
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
	public void TC_05() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithInvalidParam(uomTypeCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			
			String Wsstatus= res.getStatusLine();
	        int Wscode= res.statusCode();
	        String expectMessage1 = resMsgs.uomTypeNmFieldNotPresent;
	        
			String meta = js.getString("meta");
	       	String internalMsg = js.getString("data.uomTypes");
	       	int errorMsgLength =js.get("meta.errors.size");
	       	List <String> errorMgs1= new ArrayList<String>();
	       	List <String> errorMgs2= new ArrayList<String>();
	       	
	       	for(int i=0;i<errorMsgLength;i++)
	       	  { 
	       	        	errorMgs1.add(js.getString("meta.errors["+i+"].error"));
	       	        	errorMgs2.add(js.getString("meta.errors["+i+"].message"));
	       	        	
	       	  }
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 200 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMgs1.get(0).equals("ValidationError") && errorMgs2.get(0).equals(expectMessage1))
				{
					String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when sending the invalid UomType attribute");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending the invalid UomType attribute");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd"+expectMessage1 );
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	if(Wscode!=200){
			        logger.error("Response status validation failed: "+Wscode);
			        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			        logger.error("------------------------------------------------------------------");
			        test.fail("Response status validation failed: "+Wscode);
			        }
		    	  else if(meta == null){
		  	        logger.error("Response validation failed as meta not found");
		  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		  	        logger.error("------------------------------------------------------------------");
		  	        test.fail("Response validation failed as meta not found");
		  	        }else if(meta.contains("timestamp")){
			        	logger.error("Response validation failed as timestamp found");
			    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    		logger.error("------------------------------------------------------------------");
			        	test.fail("Response validation failed as timestamp found");
			           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
				        	logger.error("Response validation failed as API version number is not matching with expected");
			    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Response validation failed as API version number is not matching with expected");	
				        	}
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
			logger.info("------------------------------------------------------------------");
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
	public void TC_06() {

		String testCaseID = new Object() {
		}.getClass().getEnclosingMethod().getName();
		logger.info("Executing Test Case: " + testCaseID);
		if (!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. " + testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.uomTypeGraphQLRequestWithAllInvalidParam(uomTypeCode);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			// logger.info("Input Request created:");
			test.info("Input Request created:");
			// logger.info(reqFormatted.replaceAll("\n", "<br />"));
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level + ".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("URI passed: "+getEndPoinUrl);
			logger.info("Response Recieved:");
			test.info("Response Recieved:");
			// logger.info(responsestr1.replaceAll("\n", "<br />"));
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			
			String Wsstatus= res.getStatusLine();
	        int Wscode= res.statusCode();
	        String expectMessage1 = resMsgs.uomTypeCdFieldNotPresent;
	        String expectMessage2 = resMsgs.uomTypeNmFieldNotPresent;
	        String expectMessage3 = resMsgs.uomTypeDescFieldNotPresent;
	        
			String meta = js.getString("meta");
	       	String internalMsg = js.getString("data.uomTypes");
	       	int errorMsgLength =js.get("meta.errors.size");
	       	List <String> errorMgs1= new ArrayList<String>();
	       	List <String> errorMgs2= new ArrayList<String>();
	       	
	       	for(int i=0;i<errorMsgLength;i++)
	       	  { 
	       	        	errorMgs1.add(js.getString("meta.errors["+i+"].error"));
	       	        	errorMgs2.add(js.getString("meta.errors["+i+"].message"));
	       	        	
	       	  }
	        String actualRespVersionNum = js.getString("meta.version"); 
	        if(Wscode == 200 &&  meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0"))
		    {
	        	logger.info("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response status code 200 validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	test.pass("Response timestamp validation passed");
	        	test.pass("Response API version number validation passed"); 
	        	//***error message validation
	        	if(errorMgs1.get(0).equals("ValidationError") && errorMgs2.get(0).equals(expectMessage1)
	        			&& errorMgs1.get(1).equals("ValidationError") && errorMgs2.get(1).equals(expectMessage2)
	        			&& errorMgs1.get(2).equals("ValidationError") && errorMgs2.get(2).equals(expectMessage3))
				{
					String[] inputFieldValues = {uomTypeCode, uomTypeName, uomTypeDesc};
					String[] inputFieldNames = {"Input_UomTypeCode: ", "Input_UomTypeName: ", "Input_UomTypeDesc: "};
	        		writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldNames);
	        		logger.info("Expected error message is getting received in response when sending all the invalid UomType attribute");
	    			logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when sending all the invalid UomType attribute");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,	writableInputFields, "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.info("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
		        	test.fail("Expected error message is not getting received as expected in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
							responsestr, "Fail", "uomTypeCd"+expectMessage1 );
		        	Assert.fail("Test Failed");
		        }
		    }else {
	        	if(Wscode!=200){
			        logger.error("Response status validation failed: "+Wscode);
			        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			        logger.error("------------------------------------------------------------------");
			        test.fail("Response status validation failed: "+Wscode);
			        }
		    	  else if(meta == null){
		  	        logger.error("Response validation failed as meta not found");
		  	        logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
		  	        logger.error("------------------------------------------------------------------");
		  	        test.fail("Response validation failed as meta not found");
		  	        }else if(meta.contains("timestamp")){
			        	logger.error("Response validation failed as timestamp found");
			    		logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    		logger.error("------------------------------------------------------------------");
			        	test.fail("Response validation failed as timestamp found");
			           }else if(!actualRespVersionNum.equalsIgnoreCase("1.0.0")){
				        	logger.error("Response validation failed as API version number is not matching with expected");
			    			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Response validation failed as API version number is not matching with expected");	
				        	}
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
			logger.info("------------------------------------------------------------------");
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

	

	// ***get the values from test data sheet
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
		uomTypeCode = inputData1.get(testCaseId).get("uomTypeCd");
		uomTypeName = inputData1.get(testCaseId).get("uomTypeNm");
		uomTypeDesc = inputData1.get(testCaseId).get("uomTypeDesc");
	}
}
