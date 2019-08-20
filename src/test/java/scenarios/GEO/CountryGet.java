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

public class CountryGet extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, geopoliticalId, countryShortName, orgStandardCode, countryCd, targetDate, endDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(CountryGet.class);
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
		countryWithGeoplId(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
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
		countryWithGeoplIdAndDates(testCaseID);
	}
	
	@Test
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
		countryWithGeoplIdAndDates(testCaseID);
	}
	
	@Test
	public void TC_28()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_29()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_30()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_31()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_32()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_33()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_34()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_35()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_36()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_37()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_38()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_39()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}
	@Test
	public void TC_40()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryWithGeoplIdAndDates(testCaseID);
	}

	private void countryWithGeoplId(String testCaseID) {
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".country.get");
			getEndPoinUrl = getEndPoinUrl.replace("?", "/");
			getEndPoinUrl = getEndPoinUrl+geopoliticalId;
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
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String countryGetQuery = query.countryGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.countryGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
	    		System.out.println("Response rows: "+responseRows.size());
	    		System.out.println("DB records: "+getResultDB.size()/fields.size());
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=responseRows.size()-1; i>=0; i=i-1)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryNumberCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryNumberCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].threeCharCountryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].threeCharCountryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].independentFlag")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].independentFlag"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependentRelationshipId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependentRelationshipId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependentCountryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependentCountryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalFormatDescription")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalFormatDescription"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalFlag")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalFlag"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalLengthNumber")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalLengthNumber"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].firstWorkWeekDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].firstWorkWeekDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].lastWorkWeekDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].lastWorkWeekDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].weekendFirstDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].weekendFirstDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].internetDomainName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].internetDomainName"));
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
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(
		        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
		        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString())
		        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
		        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
		        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
		        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString())
		        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(j+6).toString())
		        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(j+7).toString())
		        				&& getResultDB.get(j+8).toString().equals(getResponseRows.get(j+8).toString())
		        				&& getResultDB.get(j+9).toString().equals(getResponseRows.get(j+9).toString())
		        				&& getResultDB.get(j+10).toString().equals(getResponseRows.get(j+10).toString())
		        				&& getResultDB.get(j+11).toString().equals(getResponseRows.get(j+11).toString())
		        				&& getResultDB.get(j+12).toString().equals(getResponseRows.get(j+12).toString())
		        				&& getResultDB.get(j+13).toString().equals(getResponseRows.get(j+13).toString())
		        				&& getResultDB.get(j+14).toString().equals(getResponseRows.get(j+14).toString())
		        				&& getResultDB.get(j+15).toString().equals(getResponseRows.get(j+15).toString())
		        				)
			        	{
		        			String[] responseDbFieldValues = {
		        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), 
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(), 
		        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(), 
		        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
		        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
		        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
		        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
		        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
		        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
		        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString(),
		        					getResponseRows.get(j+11).toString(), getResultDB.get(j+11).toString(),
		        					getResponseRows.get(j+12).toString(), getResultDB.get(j+12).toString(),
		        					getResponseRows.get(j+13).toString(), getResultDB.get(j+13).toString(),
		        					getResponseRows.get(j+14).toString(), getResultDB.get(j+14).toString(),
		        					getResponseRows.get(j+15).toString(), getResultDB.get(j+15).toString()
		        					};
		        			String[] responseDbFieldNames = {
		        					"Response_geopoliticalId: ", "DB_geopoliticalId: ",
		        					"Response_countryNumberCd: ", "DB_countryNumberCd: ",
		        					"Response_countryCd: ", "DB_countryCd: ",
		        					"Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
		        					"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
		        					"Response_postalFlag: ", "DB_postalFlag: ",
		        					"Response_postalLengthNumber: ", "DB_postalLengthNumber: ",
		        					"Response_firstWorkWeekDayName: ", "DB_firstWorkWeekDayName: ",
		        					"Response_lastWorkWeekDayName: ", "DB_lastWorkWeekDayName: ",
		        					"Response_weekendFirstDayName: ", "DB_weekendFirstDayName: ",
		        					"Response_internetDomainName: ", "DB_internetDomainName: ",
		        					"Response_effectiveDate: ", "DB_effectiveDate: ",
		        					"Response_expirationDate: ", "DB_expirationDate: "		        					
		        					};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			test.info("Geopolitical Type validation starts:");
		        			countryGeoTypeValidation(responsestr, testCaseID, z-1);
		        			countryUomTypeValidation(responsestr, testCaseID, z-1);
		        			countryHolidayValidation(responsestr, testCaseID, z-1);
		        			countryAffilTypeValidation(responsestr, testCaseID, z-1);
		        			countryTrnslGeoplValidation(responsestr, testCaseID, z-1);
		        			countryOrgStdValidation(responsestr, testCaseID, z-1);
		        			countryLocaleValidation(responsestr, testCaseID, z-1);
		        			countryDialValidation(responsestr, testCaseID, z-1);
		        			countryCurrencyValidation(responsestr, testCaseID, z-1);
		        			z++;
			        	}else {
			        		String[] responseDbFieldValues = {
		        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), 
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(), 
		        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(), 
		        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
		        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
		        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
		        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
		        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
		        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
		        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString(),
		        					getResponseRows.get(j+11).toString(), getResultDB.get(j+11).toString(),
		        					getResponseRows.get(j+12).toString(), getResultDB.get(j+12).toString(),
		        					getResponseRows.get(j+13).toString(), getResultDB.get(j+13).toString(),
		        					getResponseRows.get(j+14).toString(), getResultDB.get(j+14).toString(),
		        					getResponseRows.get(j+15).toString(), getResultDB.get(j+15).toString()
		        					};
		        			String[] responseDbFieldNames = {
		        					"Response_geopoliticalId: ", "DB_geopoliticalId: ",
		        					"Response_countryNumberCd: ", "DB_countryNumberCd: ",
		        					"Response_countryCd: ", "DB_countryCd: ",
		        					"Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
		        					"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
		        					"Response_postalFlag: ", "DB_postalFlag: ",
		        					"Response_postalLengthNumber: ", "DB_postalLengthNumber: ",
		        					"Response_firstWorkWeekDayName: ", "DB_firstWorkWeekDayName: ",
		        					"Response_lastWorkWeekDayName: ", "DB_lastWorkWeekDayName: ",
		        					"Response_weekendFirstDayName: ", "DB_weekendFirstDayName: ",
		        					"Response_internetDomainName: ", "DB_internetDomainName: ",
		        					"Response_effectiveDate: ", "DB_effectiveDate: ",
		        					"Response_expirationDate: ", "DB_expirationDate: "		        					
		        					};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", writableResult, "Fail", "This record is not matching" );
		        			z++;
			        	}
			        		
		        	}
	    		}else {
		        	logger.error("Total number of records not matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Total number of records not matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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
	
	private void countryWithGeoplIdAndDates(String testCaseID) {
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".country.get");
			if(geopoliticalId!="")
			{
				getEndPoinUrl = getEndPoinUrl+"geopoliticalId="+geopoliticalId;
			}
			if(countryCd!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCd;
			}
			if(countryShortName!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryShortName="+countryShortName;
			}
			if(orgStandardCode!="")
			{
				getEndPoinUrl = getEndPoinUrl+"&orgStandardCode="+orgStandardCode;
			}
			if(targetDate!="" && !targetDate.equalsIgnoreCase("NoTargetDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&targetDate="+targetDate;
			}
			if(endDate!="" && !endDate.equalsIgnoreCase("NoEndDate"))
			{
				getEndPoinUrl = getEndPoinUrl+"&endDate="+endDate;
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
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS"))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	        	if(geopoliticalId=="" && js.get("data[0].geopoliticalId")!=null)
	        	{
	        		geopoliticalId = js.get("data[0].geopoliticalId").toString();
	        	}
	    		String countryGetQuery = query.countryGetQuery(geopoliticalId, targetDate , endDate);
	    		//***get the fields needs to be validate in DB
	    		List<String> fields = ValidationFields.countryGetMethodDbFields();
	    		//***get the result from DB
	    		List<String> getResultDB = DbConnect.getResultSetFor(countryGetQuery, fields, fileName, testCaseID);
	    		System.out.println("Response rows: "+responseRows.size());
	    		System.out.println("DB records: "+getResultDB.size()/fields.size());
	    		if(getResultDB.size() == responseRows.size()*fields.size())
	    		{
	    			logger.info("Total number of records matching between DB & Response: "+responseRows.size());
		        	test.pass("Total number of records matching between DB & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
        					Wsstatus, ""+Wscode, "", "Pass", "Total number of records matching between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
		        	List<String> getResponseRows = new ArrayList<>();
		        	for(int i=responseRows.size()-1; i>=0; i--)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryNumberCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryNumberCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].threeCharCountryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].threeCharCountryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].independentFlag")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].independentFlag"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependentRelationshipId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependentRelationshipId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependentCountryCd")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependentCountryCd"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalFormatDescription")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalFormatDescription"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalFlag")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalFlag"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].postalLengthNumber")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].postalLengthNumber"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].firstWorkWeekDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].firstWorkWeekDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].lastWorkWeekDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].lastWorkWeekDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].weekendFirstDayName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].weekendFirstDayName"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].internetDomainName")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].internetDomainName"));
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
		        	int z=1;
		        	for(int j=0; j<getResultDB.size(); j=j+fields.size())
		        	{
		        		if(
		        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
		        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString())
		        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
		        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
		        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
		        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString())
		        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(j+6).toString())
		        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(j+7).toString())
		        				&& getResultDB.get(j+8).toString().equals(getResponseRows.get(j+8).toString())
		        				&& getResultDB.get(j+9).toString().equals(getResponseRows.get(j+9).toString())
		        				&& getResultDB.get(j+10).toString().equals(getResponseRows.get(j+10).toString())
		        				&& getResultDB.get(j+11).toString().equals(getResponseRows.get(j+11).toString())
		        				&& getResultDB.get(j+12).toString().equals(getResponseRows.get(j+12).toString())
		        				&& getResultDB.get(j+13).toString().equals(getResponseRows.get(j+13).toString())
		        				&& getResultDB.get(j+14).toString().equals(getResponseRows.get(j+14).toString())
		        				&& getResultDB.get(j+15).toString().equals(getResponseRows.get(j+15).toString())
		        				)
			        	{
		        			String[] responseDbFieldValues = {
		        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), 
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(), 
		        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(), 
		        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
		        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
		        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
		        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
		        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
		        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
		        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString(),
		        					getResponseRows.get(j+11).toString(), getResultDB.get(j+11).toString(),
		        					getResponseRows.get(j+12).toString(), getResultDB.get(j+12).toString(),
		        					getResponseRows.get(j+13).toString(), getResultDB.get(j+13).toString(),
		        					getResponseRows.get(j+14).toString(), getResultDB.get(j+14).toString(),
		        					getResponseRows.get(j+15).toString(), getResultDB.get(j+15).toString()
		        					};
		        			String[] responseDbFieldNames = {
		        					"Response_geopoliticalId: ", "DB_geopoliticalId: ",
		        					"Response_countryNumberCd: ", "DB_countryNumberCd: ",
		        					"Response_countryCd: ", "DB_countryCd: ",
		        					"Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
		        					"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
		        					"Response_postalFlag: ", "DB_postalFlag: ",
		        					"Response_postalLengthNumber: ", "DB_postalLengthNumber: ",
		        					"Response_firstWorkWeekDayName: ", "DB_firstWorkWeekDayName: ",
		        					"Response_lastWorkWeekDayName: ", "DB_lastWorkWeekDayName: ",
		        					"Response_weekendFirstDayName: ", "DB_weekendFirstDayName: ",
		        					"Response_internetDomainName: ", "DB_internetDomainName: ",
		        					"Response_effectiveDate: ", "DB_effectiveDate: ",
		        					"Response_expirationDate: ", "DB_expirationDate: "		        					
		        					};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.pass(writableResult.replaceAll("\n", "<br />"));  
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", writableResult, "Pass", "" );
		        			test.info("Geopolitical Type validation starts:");
		        			countryGeoTypeValidation(responsestr, testCaseID, z-1);
		        			countryUomTypeValidation(responsestr, testCaseID, z-1);
		        			countryHolidayValidation(responsestr, testCaseID, z-1);
		        			countryAffilTypeValidation(responsestr, testCaseID, z-1);
		        			countryTrnslGeoplValidation(responsestr, testCaseID, z-1);
		        			countryOrgStdValidation(responsestr, testCaseID, z-1);
		        			countryLocaleValidation(responsestr, testCaseID, z-1);
		        			countryDialValidation(responsestr, testCaseID, z-1);
		        			countryCurrencyValidation(responsestr, testCaseID, z-1);
		        			z++;
			        	}else {
			        		String[] responseDbFieldValues = {
		        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(), 
		        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(), 
		        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(), 
		        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(), 
		        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
		        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
		        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
		        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
		        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
		        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
		        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString(),
		        					getResponseRows.get(j+11).toString(), getResultDB.get(j+11).toString(),
		        					getResponseRows.get(j+12).toString(), getResultDB.get(j+12).toString(),
		        					getResponseRows.get(j+13).toString(), getResultDB.get(j+13).toString(),
		        					getResponseRows.get(j+14).toString(), getResultDB.get(j+14).toString(),
		        					getResponseRows.get(j+15).toString(), getResultDB.get(j+15).toString()
		        					};
		        			String[] responseDbFieldNames = {
		        					"Response_geopoliticalId: ", "DB_geopoliticalId: ",
		        					"Response_countryNumberCd: ", "DB_countryNumberCd: ",
		        					"Response_countryCd: ", "DB_countryCd: ",
		        					"Response_threeCharCountryCd: ", "DB_threeCharCountryCd: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependentCountryCd: ", "DB_dependentCountryCd: ",
		        					"Response_postalFormatDescription: ", "DB_postalFormatDescription: ",
		        					"Response_postalFlag: ", "DB_postalFlag: ",
		        					"Response_postalLengthNumber: ", "DB_postalLengthNumber: ",
		        					"Response_firstWorkWeekDayName: ", "DB_firstWorkWeekDayName: ",
		        					"Response_lastWorkWeekDayName: ", "DB_lastWorkWeekDayName: ",
		        					"Response_weekendFirstDayName: ", "DB_weekendFirstDayName: ",
		        					"Response_internetDomainName: ", "DB_internetDomainName: ",
		        					"Response_effectiveDate: ", "DB_effectiveDate: ",
		        					"Response_expirationDate: ", "DB_expirationDate: "		        					
		        					};
		        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
		        			test.info("Record "+z+" Validation:");
		            		test.fail(writableResult.replaceAll("\n", "<br />"));  
		        			logger.info("Record "+z+" Validation:");
		        			logger.error(writableResult);
		        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA",	"", "",
	            					"", "", writableResult, "Fail", "This record is not matching" );
		        			z++;
			        	}
			        		
		        	}
	    		}else {
		        	logger.error("Total number of records not matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
					logger.error("------------------------------------------------------------------");
		        	test.fail("Total number of records not matching between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus, ""+Wscode,
							responsestr1, "Fail", internalMsg );
		        	Assert.fail("Test Failed");
		        }
	        }else {
	        	logger.error("Response status validation failed: "+Wscode);
				logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
				logger.error("------------------------------------------------------------------");
	        	test.fail("Response status validation failed: "+Wscode);
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

	private void countryCurrencyValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].currencies");
		//***get query
		String countryCurrencyGetQuery = query.countryCurrencyGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryCurrencyGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryCurrencyGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for Currency record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for Currency record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].currencyNumberCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].currencyNumberCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].currencyCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].currencyCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].minorUnitCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].minorUnitCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].moneyFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].moneyFormatDescription"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].currencies["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].currencies["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString())
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_currencyNumberCd: ", "DB_currencyNumberCd: ",
	        					"Response_currencyCd: ", "DB_currencyCd: ",
	        					"Response_minorUnitCd: ", "DB_minorUnitCd: ",
	        					"Response_moneyFormatDescription: ", "DB_moneyFormatDescription: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_currencyNumberCd: ", "DB_currencyNumberCd: ",
	        					"Response_currencyCd: ", "DB_currencyCd: ",
	        					"Response_minorUnitCd: ", "DB_minorUnitCd: ",
	        					"Response_moneyFormatDescription: ", "DB_moneyFormatDescription: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for Currency record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Currency record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryDialValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].countryDialings");
		//***get query
		String countryDialGetQuery = query.countryDialGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryDialGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryDialGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for Dial record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for Dial record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].intialDialingPrefixCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].intialDialingPrefixCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].intialDialingCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].intialDialingCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].landPhMaxLthNbr")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].landPhMaxLthNbr"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].landPhMinLthNbr")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].landPhMinLthNbr"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].moblPhMaxLthNbr")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].moblPhMaxLthNbr"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].moblPhMinLthNbr")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].moblPhMinLthNbr"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryDialings["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryDialings["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			System.out.println(getResultDB.get(j).toString());
        			System.out.println(getResponseRows.get(j).toString());
        			System.out.println(getResultDB.get(j+1).toString());
        			System.out.println(getResponseRows.get(j+1).toString());
        			System.out.println(getResultDB.get(j+2).toString());
        			System.out.println(getResponseRows.get(j+2).toString());
        			System.out.println(getResultDB.get(j+3).toString());
        			System.out.println(getResponseRows.get(j+3).toString());
        			System.out.println(getResultDB.get(j+4).toString());
        			System.out.println(getResponseRows.get(j+4).toString());
        			System.out.println(getResultDB.get(j+5).toString());
        			System.out.println(getResponseRows.get(j+5).toString());
        			System.out.println(getResultDB.get(j+6).toString());
        			System.out.println(getResponseRows.get(j+6).toString());
        			System.out.println(getResultDB.get(j+7).toString());
        			System.out.println(getResponseRows.get(j+7).toString());
        			
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString().trim()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString().trim()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString().trim()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString().trim())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString().trim())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString().trim())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(j+6).toString().trim())
	        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(j+7).toString().trim())
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_intialDialingPrefixCd: ", "DB_intialDialingPrefixCd: ",
	        					"Response_intialDialingCd: ", "DB_intialDialingCd: ",
	        					"Response_landPhMaxLthNbr: ", "DB_landPhMaxLthNbr: ",
	        					"Response_landPhMinLthNbr: ", "DB_landPhMinLthNbr: ",
	        					"Response_moblPhMaxLthNbr: ", "DB_moblPhMaxLthNbr: ",
	        					"Response_moblPhMinLthNbr: ", "DB_moblPhMinLthNbr: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_intialDialingPrefixCd: ", "DB_intialDialingPrefixCd: ",
	        					"Response_intialDialingCd: ", "DB_intialDialingCd: ",
	        					"Response_landPhMaxLthNbr: ", "DB_landPhMaxLthNbr: ",
	        					"Response_landPhMinLthNbr: ", "DB_landPhMinLthNbr: ",
	        					"Response_moblPhMaxLthNbr: ", "DB_moblPhMaxLthNbr: ",
	        					"Response_moblPhMinLthNbr: ", "DB_moblPhMinLthNbr: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for Dial record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Dial record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryLocaleValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].locales");
		//***get query
		String countryLocaleGetQuery = query.countryLocaleGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryLocaleGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryLocaleGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for Locale record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for Locale record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].englLanguageNm")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].englLanguageNm"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].localeCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].localeCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].scrptCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].scrptCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].cldrVersionDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].locales["+i+"].cldrVersionDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].cldrVersionNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].cldrVersionNumber"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].dateFullFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].dateFullFormatDescription"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].dateLongFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].dateLongFormatDescription"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].dateMediumFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].dateMediumFormatDescription"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].dateShortFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].dateShortFormatDescription"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].locales["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].locales["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(j+6).toString())
	        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(j+7).toString())
	        				&& getResultDB.get(j+8).toString().equals(getResponseRows.get(j+8).toString())
	        				&& getResultDB.get(j+9).toString().equals(getResponseRows.get(j+9).toString())
	        				&& getResultDB.get(j+10).toString().equals(getResponseRows.get(j+10).toString())
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
	        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
	        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
	        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_englLanguageNm: ", "DB_englLanguageNm: ",
	        					"Response_localeCd: ", "DB_localeCd: ",
	        					"Response_scrptCd: ", "DB_scrptCd: ",
	        					"Response_cldrVersionDate: ", "DB_cldrVersionDate: ",
	        					"Response_cldrVersionNumber: ", "DB_cldrVersionNumber: ",
	        					"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
	        					"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
	        					"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
	        					"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(j+7).toString(), getResultDB.get(j+7).toString(),
	        					getResponseRows.get(j+8).toString(), getResultDB.get(j+8).toString(),
	        					getResponseRows.get(j+9).toString(), getResultDB.get(j+9).toString(),
	        					getResponseRows.get(j+10).toString(), getResultDB.get(j+10).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_englLanguageNm: ", "DB_englLanguageNm: ",
	        					"Response_localeCd: ", "DB_localeCd: ",
	        					"Response_scrptCd: ", "DB_scrptCd: ",
	        					"Response_cldrVersionDate: ", "DB_cldrVersionDate: ",
	        					"Response_cldrVersionNumber: ", "DB_cldrVersionNumber: ",
	        					"Response_dateFullFormatDescription: ", "DB_dateFullFormatDescription: ",
	        					"Response_dateLongFormatDescription: ", "DB_dateLongFormatDescription: ",
	        					"Response_dateMediumFormatDescription: ", "DB_dateMediumFormatDescription: ",
	        					"Response_dateShortFormatDescription: ", "DB_dateShortFormatDescription: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for Locale record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Locale record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryOrgStdValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].countryOrgStds");
		//***get query
		String countryOrgStdGetQuery = query.countryOrgStdGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryOrgStdGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryOrgStdGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for OrgStd record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for OrgStd record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for OrgStd record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrgStds["+i+"].orgStdNm")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrgStds["+i+"].orgStdNm"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrgStds["+i+"].countryFullName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrgStds["+i+"].countryFullName"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrgStds["+i+"].countryShortName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrgStds["+i+"].countryShortName"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrgStds["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrgStds["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrgStds["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrgStds["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_orgStdNm: ", "DB_orgStdNm: ",
	        					"Response_countryFullName: ", "DB_countryFullName: ",
	        					"Response_countryShortName: ", "DB_countryShortName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_orgStdNm: ", "DB_orgStdNm: ",
	        					"Response_countryFullName: ", "DB_countryFullName: ",
	        					"Response_countryShortName: ", "DB_countryShortName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryTrnslGeoplValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].translationGeopoliticals");
		//***get query
		String countryTrnslGeoplGetQuery = query.countryTrnslGeoplGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryTrnslGeoplGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryTrnslGeoplGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for TrnslGeopl record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for TrnslGeopl record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for TrnslGeopl record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].englLanguageNm")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].englLanguageNm"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].scrptCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].scrptCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].translationName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].translationName"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].versionDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].translationGeopoliticals["+i+"].versionDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].versionNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].versionNumber"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].translationGeopoliticals["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].translationGeopoliticals["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        				//&& getResultDB.get(j+3).toString().equals(getResponseRows.get(j+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(j+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(j+5).toString())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(j+6).toString())
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_englLanguageNm: ", "DB_englLanguageNm: ",
	        					"Response_scrptCd: ", "DB_scrptCd: ",
	        					"Response_translationName: ", "DB_translationName: ",
	        					"Response_versionDate: ", "DB_versionDate: ",
	        					"Response_versionNumber: ", "DB_versionNumber: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(j+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(j+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(j+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(j+6).toString(), getResultDB.get(j+6).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_englLanguageNm: ", "DB_englLanguageNm: ",
	        					"Response_scrptCd: ", "DB_scrptCd: ",
	        					"Response_translationName: ", "DB_translationName: ",
	        					"Response_versionDate: ", "DB_versionDate: ",
	        					"Response_versionNumber: ", "DB_versionNumber: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for TrnslGeopl record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for TrnslGeopl record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryAffilTypeValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalAffiliations");
		//***get query
		String countryAffilTypeGetQuery = query.countryAffilTypeGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryAffilTypeGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryAffilTypeGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for AffilType record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for AffilType record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for AffilType record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].affilTypeName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].affilTypeName"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalAffiliations["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalAffiliations["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_affilTypeName: ", "DB_affilTypeName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_affilTypeName: ", "DB_affilTypeName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for AffilTyp record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for AffilTyp record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryHolidayValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalHolidays");
		//***get query
		String countryHolidayGetQuery = query.countryHolidayGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryHolidayGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryHolidayGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for Holiday record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for Holiday record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Holiday record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalHolidays["+i+"].holidayName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalHolidays["+i+"].holidayName"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalHolidays["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalHolidays["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalHolidays["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalHolidays["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_holidayName: ", "DB_holidayName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_holidayName: ", "DB_holidayName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for Holiday record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Holiday record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
	}

	private void countryUomTypeValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalUnitOfMeasures");
		//***get query
		String countryUomTypeGetQuery = query.countryUomTypeGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryUomTypeGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryUomTypeGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for UOM Type record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for UOM Type record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for UOM Type record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].uomTypeCd")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].uomTypeCd"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(j+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(j+2).toString()) 
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_uomTypeCd: ", "DB_uomTypeCd: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(j+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(j+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_uomTypeCd: ", "DB_uomTypeCd: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for UOM Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for UOM Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
	}

	private void countryGeoTypeValidation(String responsestr, String testCaseID, int k) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalType");
		//***get query
		String countryGeoTypeNameGetQuery = query.countryGeoTypeNameGetQuery(geopoliticalId);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryGeoTypeNameGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = DbConnect.getResultSetFor(countryGeoTypeNameGetQuery, fields, fileName, testCaseID);
		System.out.println("Response rows: "+responseRows.size());
		System.out.println("DB records: "+getResultDB.size()/fields.size());
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
			logger.info("Total number of records matching for Geopolitical Type record between DB & Response: "+responseRows.size());
        	test.pass("Total number of records matching for Geopolitical Type record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Geopolitical Type record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalType["+i+"].geopoliticalTypeName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalType["+i+"].geopoliticalTypeName"));
        		}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(j).toString()) 
	        			)
        			{
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_geopoliticalTypeName: ", "DB_geopoliticalTypeName: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
        			}else {
        				String[] responseDbFieldValues = {
	        					getResponseRows.get(j).toString(), getResultDB.get(j).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_geopoliticalTypeName: ", "DB_geopoliticalTypeName: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.fail(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Fail", "" );
        			}
	        	}
        	}
		}else {
        	logger.error("Total number of records not matching for Geopolitical Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Geopolitical Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
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
		countryCd = inputData1.get(testCaseId).get("countryCd");
		countryShortName = inputData1.get(testCaseId).get("countryShortName");
		orgStandardCode = inputData1.get(testCaseId).get("orgStandardCode");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}
}
