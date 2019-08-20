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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

public class StProvStdGet extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, geopoliticalId, stProvCd, orgStdCd, countryCd, targetDate, endDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(MonthOfYearGet.class);
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			getEndPoinUrl = getEndPoinUrl.replace("?", "");
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdGetQuery(targetDate, endDate);
	    		
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		/*if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}*/
		        		 if(eachRecordValidation)
	                        {
	                            eachRecordValidation=false;
	                            test.info("Record "+x+" Validation:");
	                            test.pass(mergedResponseRows.get(i));  
	                            test.pass(mergedDBRows.get(i));
	                            ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",    "", "",
	                                    "", "", mergedResponseRows.get(i), "Pass", "" );
	                            x++;
	                        }else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			getEndPoinUrl = getEndPoinUrl.replace("?", "/");
			getEndPoinUrl=getEndPoinUrl+geopoliticalId;
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i)); 
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			getEndPoinUrl = getEndPoinUrl.replace("?", "/");
			getEndPoinUrl=getEndPoinUrl+geopoliticalId;
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithGeoplIdTargetEndDatesGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i)); 
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i)); 
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i)); 
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(stProvCd, orgStdCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i)); 
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i)); 
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));  
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(stProvCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"stProvCd="+stProvCd;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(orgStdCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"?orgStdCd="+orgStdCd;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
			}
			String lastCharOfUrl = getEndPoinUrl.substring(getEndPoinUrl.length()-1);
			if(lastCharOfUrl.equals("?"))
			{
				getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 1);
			}
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        String meta =  js.getString("meta");
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && meta !=null)
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response meta validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String geoStProvStdGetQuery = query.stProvStdWithCountrCdGetQuery(countryCd, targetDate, endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.stProvStdGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(geoStProvStdGetQuery, fields, fileName, testCaseID);
	    		
	    		
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].orgStdNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].orgStdNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].stProvNm")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].stProvNm"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].effectiveDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].effectiveDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].expirationDate")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			String str = js.getString("data["+i+"].expirationDate");
		        			int index = str.indexOf("T");
		        			str = str.substring(0, index);
		        			getResponseRows.add(str);
		        		}
		        	}
		        	logger.info("Each record validation starts");
		        	test.info("Each record validation starts");
		        	
		        	List<String> mergedResponseRows = new ArrayList<>();
		        	List<String> mergedDBRows = new ArrayList<>();
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		mergedResponseRows.add("-Response_Geopl_ID:"+getResponseRows.get(j)+"-Response_OrgStdNm:"+getResponseRows.get(j+1)+"-Response_StProvCd:"+getResponseRows.get(j+2)+
		        				"-Response_stProvNm:"+getResponseRows.get(j+3)+"-Response_effectiveDate:"+getResponseRows.get(j+4)+"-Response_expirationDate:"+getResponseRows.get(j+5));
		        		mergedDBRows.add("-DB_Geopl_ID:"+getResultDB.get(j)+"-DB_OrgStdNm:"+getResultDB.get(j+1)+"-DB_StProvCd:"+getResultDB.get(j+2)+
		        				"-DB_stProvNm:"+getResultDB.get(j+3)+"-DB_effectiveDate:"+getResultDB.get(j+4)+"-DB_expirationDate:"+getResultDB.get(j+5));
		        	}
		        	int x=1;
		        	boolean eachRecordValidation = false;
		        	for(int i=0; i<mergedResponseRows.size(); i++)
		        	{
		        		for(int j=0; j<mergedResponseRows.size(); j++)
		        		{
		        			String removeCharsMergedResRows = mergedResponseRows.get(i).replaceAll("-Response_Geopl_ID:", "").replaceAll("-Response_OrgStdNm:", "").
		        					replaceAll("-Response_StProvCd:", "").replaceAll("-Response_stProvNm:", "").replaceAll("-Response_effectiveDate:", "").
		        					replaceAll("-Response_expirationDate:", "");
		        			String removeCharsMergedDbRows = mergedDBRows.get(j).replaceAll("-DB_Geopl_ID:", "").replaceAll("-DB_OrgStdNm:", "").
		        					replaceAll("-DB_StProvCd:", "").replaceAll("-DB_stProvNm:", "").replaceAll("-DB_effectiveDate:", "").
		        					replaceAll("-DB_expirationDate:", "");
		        			if(removeCharsMergedResRows.equals(removeCharsMergedDbRows))
		        			{
		        				eachRecordValidation = true;
		        				break;
		        			}
		        		}
		        		if(eachRecordValidation)
		        		{
		        			eachRecordValidation=false;
		        			test.info("Record "+x+" Validation:");
		            		test.pass(mergedResponseRows.get(i));  
		            		 test.pass(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Pass", "" );
		        			x++;
		        		}else {
		        			test.info("Record "+x+" Validation:");
		            		test.fail(mergedResponseRows.get(i));
		            		 test.fail(mergedDBRows.get(i));
		        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", mergedResponseRows.get(i), "Fail", "" );
		        			x++;
		        		}
		        	}
		        	
		        	
		        	if(responseRows.size()==0)
		        	{
		        		logger.info("0 matching records and there is no validation required");
			        	test.info("0 matching records and there is no validation required");
			        	//***error message validation
						String expectMessage = resMsgs.getErrorMsg;
						if(internalMsg.equals(expectMessage))
						{
							logger.info("Expected internal message is getting received in response for 0 records");
				        	test.pass("Expected internal message is getting received in response for 0 records");
						}else {
							logger.error("Expected internal message is not getting received in response for 0 records");
							logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			    			logger.error("------------------------------------------------------------------");
							test.fail("Expected error message is not getting received in response for 0 records");
				        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
									responsestr, "Fail", internalMsg );
				        	test.info("Response Recieved:");
							test.info(responsestr1.replaceAll("\n", "<br />"));
							logger.info("------------------------------------------------------------------");
				        	Assert.fail("Test Failed");
				        }
			        	test.info("Response Recieved:");
						test.info(responsestr1.replaceAll("\n", "<br />"));
						logger.info("------------------------------------------------------------------");
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
		       	}else if(meta == null){
		       		logger.error("Response validation failed as meta not found");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Response validation failed as meta not found");
		       }
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
						responsestr1, "Fail", internalMsg );
	        	Assert.fail("Test Failed");
	        }
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
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
			//***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".stProvStd.get");
			getEndPoinUrl = getEndPoinUrl.substring(0, getEndPoinUrl.length() - 2);
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("status");
	        String internalMsg = js.getString("error");
	        int Wscode= res.statusCode();
	        test.info("Response Recieved:");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			String timestamp = js.getString("timestamp");
			 if(Wsstatus.equals("404") && timestamp!= null)
		        {
		        	logger.info("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response status code 404 validation passed: "+Wscode);
		        	test.pass("Response timestamp validation passed");
	        	//***error message validation
				String expectMessage = resMsgs.invalidUrlMsg;
				if(internalMsg.equals(expectMessage))
				{
	        		logger.info("Expected error message is getting received in response when passing the invalid URI");
	        		logger.info("Execution is completed for Passed Test Case No. "+testCaseID);
	    			logger.info("------------------------------------------------------------------");
	        		test.pass("Expected error message is getting received in response when passing the invalid URI");
	        		ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "NA",
	    					Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
					test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
				}else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
	    			logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA","", "", Wsstatus, ""+Wscode,
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
	        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wsstatus,
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
		geopoliticalId = inputData1.get(testCaseId).get("geopoliticalId");
		stProvCd = inputData1.get(testCaseId).get("stProvCd");
		orgStdCd = inputData1.get(testCaseId).get("orgStdCd");
		countryCd = inputData1.get(testCaseId).get("countryCd");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}

}
