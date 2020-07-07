package scenarios.GEO.v1;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

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

public class CountryGet extends Reporting{
	
	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, geopoliticalId, countryShortName, orgStandardCode, countryCode, targetDate, endDate;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult=null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(CountryGet.class);
	Connection con;
	Statement stmt;
	String actuatorQueryVersion;
	TestResultValidation resultValidation = new TestResultValidation();
	
	@BeforeClass
	public void before(){
		DOMConfigurator.configure("log4j.xml");
		//***create test result excel file                                                                                                                                                                                                      
		ex.createResultExcel(fileName);
		/// *** getting actautor version
				String tokenKey = tokenValues[0];
				String tokenVal = token;
				String actuatorQueryVersionURL=RetrieveEndPoints.getEndPointUrl("queryActuator", fileName, level+".query.version");
				//actuatorQueryVersion =resultValidation.versionValidation(fileName, tokenKey, tokenVal,actuatorQueryVersionURL);
				actuatorQueryVersion = "1.0.0";
	}
	
	@BeforeMethod
	protected void startRepo(Method m) throws IOException
	{
		
		runFlag = getExecutionFlag(m.getName(), fileName);
		String testCaseName=null;
		if(runFlag.equalsIgnoreCase("Yes")) {
			testCaseName = m.getName();
			test = extent.createTest(testCaseName);
		}
		con=DbConnect.getSqlStatement(fileName, testCaseName);
		if(con!=null)
		{
			test.info("DB Connection Success");
		}else {
			test.fail("DB not connected");
		}
	}
	
	@AfterMethod
	public void after()
	{
		try {
			con.close();
		} catch (SQLException e) {
			test.fail("DB connection close failed or connection not active: "+e);
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
	@Test
	public void TC_41()
	{	
		//***get test case ID with method name
		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();		
		logger.info("Executing Test Case: "+testCaseID);
		if(!runFlag.equalsIgnoreCase("Yes")) {
			logger.info("Skipped Test Case No. "+testCaseID);
			logger.info("------------------------------------------------------------------");
			throw new SkipException("Execution skipped as per test flag set");
		}
		countryNoParameters(testCaseID);
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
			String actualRespVersionNum = js.getString("meta.version");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response API version number validation passed");
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
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryNumericCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryNumericCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].threeCharacterCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].threeCharacterCountryCode"));
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
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependantCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependantCountryCode"));
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
		        			
		        			test.info("UOM Type validation starts:");
		        			countryUomTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Geopolitical Type validation starts:");
		        			countryGeoTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Holiday validation starts:");
		        			countryHolidayValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Affil Type validation starts:");
		        			countryAffilTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Trnsl Geopolitical validation starts:");
		        			countryTrnslGeoplValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("OrgStd validation starts:");
		        			countryOrgStdValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Locale validation starts:");
		        			countryLocaleValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Cntry Dial validation starts:");
		        			countryDialValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Currency validation starts:");
		        			countryCurrencyValidation(responsestr, testCaseID, z-1, targetDate , endDate);
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
	        	if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                             logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                             logger.error("------------------------------------------------------------------");
                                      test.fail("Response validation failed as API version number is not matching with expected");       
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
	
	private void countryNoParameters(String testCaseID) {
		try {
			//***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("geoGet", fileName, level+".country.get");
			getEndPoinUrl = getEndPoinUrl.replace("?", "/");
			logger.info("URI passed: "+getEndPoinUrl);
        	test.pass("URI passed: "+getEndPoinUrl);
			//***send request and get response
			Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
			String responsestr=res.asString(); 
			
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			JsonPath js = new JsonPath(responsestr);
			String Wsstatus= js.getString("meta.message.status");String actualRespVersionNum = js.getString("meta.version");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response API version number validation passed");
	        	//***get the DB query
	        	if(targetDate.equalsIgnoreCase("NoTargetDate"))
	    		{
	    			targetDate = currentDate;
	    		}
	        	if(endDate.equalsIgnoreCase("NoEndDate"))
	    		{
	    			endDate = "9999-12-31";
	    		}
	    		String countryGetQuery = query.countryGetNoParameterQuery(targetDate , endDate);
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
		        	for(int i=0; i<responseRows.size(); i++)
		        	{
		        		if(StringUtils.isBlank(js.getString("data["+i+"].geopoliticalId")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].geopoliticalId"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryNumericCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryNumericCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].threeCharacterCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].threeCharacterCountryCode"));
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
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependantCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependantCountryCode"));
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
		        			test.info("CntryOrgStd validation starts:");
		        			countryOrgStdValidation(responsestr, testCaseID, z-1, targetDate, endDate);
		        			test.info("Currency validation starts:");
		        			countryCurrencyValidation(responsestr, testCaseID, z-1, targetDate , endDate);
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
	        	if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                             logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                             logger.error("------------------------------------------------------------------");
                                      test.fail("Response validation failed as API version number is not matching with expected");       
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
			if(countryCode!="")
			{
				getEndPoinUrl = getEndPoinUrl+"countryCd="+countryCode;
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
			String Wsstatus= js.getString("meta.message.status");String actualRespVersionNum = js.getString("meta.version");
	        String internalMsg = js.getString("meta.message.internalMessage");
	        List<String> responseRows = js.get("data");
	        int Wscode= res.statusCode();
	        if(Wscode == 200 && Wsstatus.equalsIgnoreCase("SUCCESS") && actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion))
	        {
	        	logger.info("Response status validation passed: "+Wscode);
	        	test.pass("Response status validation passed: "+Wscode);
	        	test.pass("Response API version number validation passed");
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
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryNumericCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryNumericCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].countryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].countryCode"));
		        		}
		        		if(StringUtils.isBlank(js.getString("data["+i+"].threeCharacterCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].threeCharacterCountryCode"));
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
		        		if(StringUtils.isBlank(js.getString("data["+i+"].dependantCountryCode")))
		        		{
		        			getResponseRows.add("");
		        		}else {
		        			getResponseRows.add(js.getString("data["+i+"].dependantCountryCode"));
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
		        			test.info("UOM Type validation starts:");
		        			countryUomTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Geopolitical Type validation starts:");
		        			countryGeoTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Holiday validation starts:");
		        			countryHolidayValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Affil Type validation starts:");
		        			countryAffilTypeValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Trnsl Geopolitical validation starts:");
		        			countryTrnslGeoplValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("OrgStd validation starts:");
		        			countryOrgStdValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Locale validation starts:");
		        			countryLocaleValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Cintry Dial validation starts:");
		        			countryDialValidation(responsestr, testCaseID, z-1, targetDate , endDate);
		        			test.info("Currency validation starts:");
		        			countryCurrencyValidation(responsestr, testCaseID, z-1, targetDate , endDate);
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
		        					"Response_countryNumericCode: ", "DB_countryNumericCode: ",
		        					"Response_countryCode: ", "DB_countryCode: ",
		        					"Response_threeCharacterCountryCode: ", "DB_threeCharacterCountryCode: ",
		        					"Response_independentFlag: ", "DB_independentFlag: ",
		        					"Response_dependentRelationshipId: ", "DB_dependentRelationshipId: ",
		        					"Response_dependantCountryCode: ", "DB_dependantCountryCode: ",
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
	        	if(!actualRespVersionNum.equalsIgnoreCase(actuatorQueryVersion)){
                    logger.error("Response validation failed as API version number is not matching with expected");
                             logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
                             logger.error("------------------------------------------------------------------");
                                      test.fail("Response validation failed as API version number is not matching with expected");       
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

	private void countryCurrencyValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].currencies");
		geopoliticalId = js.get("data["+k+"].geopoliticalId").toString();
		//***get query
		String countryCurrencyGetQuery = query.countryCurrencyGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryCurrencyGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryCurrencyGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for Currency record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].currencyNumericCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].currencyNumericCode").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].currencyCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].currencyCode").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].minorUnitCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].minorUnitCode").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].currencies["+i+"].moneyFormatDescription")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].currencies["+i+"].moneyFormatDescription").trim());
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString())
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_currencyNumericCode: ", "DB_currencyNumericCode: ",
	        					"Response_currencyCode: ", "DB_currencyCode: ",
	        					"Response_minorUnitCode: ", "DB_minorUnitCode: ",
	        					"Response_moneyFormatDescription: ", "DB_moneyFormatDescription: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
        			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Currency details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Currency details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for Currency record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Currency record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryDialValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].countryDialings");
		//***get query
		String countryDialGetQuery = query.countryDialGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryDialGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryDialGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for Dial record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].internationalDialingPrefixCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].internationalDialingPrefixCode"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].internationalDialingCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].internationalDialingCode"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].landPhoneMaximumLengthNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].landPhoneMaximumLengthNumber"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].landPhoneMinimumLengthNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].landPhoneMinimumLengthNumber"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].mobilePhoneMaximumLengthNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].mobilePhoneMaximumLengthNumber"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryDialings["+i+"].mobilePhoneMinimumLengthNumber")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryDialings["+i+"].mobilePhoneMinimumLengthNumber"));
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString().trim()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString().trim()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString().trim()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString().trim())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString().trim())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString().trim())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(e+6).toString().trim())
	        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(e+7).toString().trim())
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(e+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(e+7).toString(), getResultDB.get(j+7).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_internationalDialingPrefixCode: ", "DB_internationalDialingPrefixCode: ",
	        					"Response_internationalDialingCode: ", "DB_internationalDialingCode: ",
	        					"Response_landPhoneMaximumLengthNumber: ", "DB_landPhoneMaximumLengthNumber: ",
	        					"Response_landPhoneMinimumLengthNumber: ", "DB_landPhoneMinimumLengthNumber: ",
	        					"Response_mobilePhoneMaximumLengthNumber: ", "DB_mobilePhoneMaximumLengthNumber: ",
	        					"Response_mobilePhoneMinimumLengthNumber: ", "DB_mobilePhoneMinimumLengthNumber: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
       			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Contry Dial details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Contry Dial details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for Dial record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Dial record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryLocaleValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].locales");
		//***get query
		String countryLocaleGetQuery = query.countryLocaleGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryLocaleGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryLocaleGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for Locale record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for Locale record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].localeCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].localeCode"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].locales["+i+"].scriptCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].locales["+i+"].scriptCode"));
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(e+6).toString())
	        				&& getResultDB.get(j+7).toString().equals(getResponseRows.get(e+7).toString())
	        				&& getResultDB.get(j+8).toString().equals(getResponseRows.get(e+8).toString())
	        				&& getResultDB.get(j+9).toString().equals(getResponseRows.get(e+9).toString())
	        				/*&& getResultDB.get(j+10).toString().equals(getResponseRows.get(e+10).toString())*/
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(e+6).toString(), getResultDB.get(j+6).toString(),
	        					getResponseRows.get(e+7).toString(), getResultDB.get(j+7).toString(),
	        					getResponseRows.get(e+8).toString(), getResultDB.get(j+8).toString(),
	        					getResponseRows.get(e+9).toString(), getResultDB.get(j+9).toString(),
	        					
	        			};
	        			String[] responseDbFieldNames = {
	        				
	        					"Response_localeCode: ", "DB_localeCode: ",
	        					"Response_scriptCode: ", "DB_scriptCode: ",
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
	        			break;
        			}
      			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Locale details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Locale details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for Locale record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Locale record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryOrgStdValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = null;
		if(js.get("data["+k+"].countryOrganizationStandards")!=null)
		{
			responseRows = js.get("data["+k+"].countryOrganizationStandards");
			geopoliticalId = js.get("data["+k+"].geopoliticalId").toString();
		
		//***get query
		String countryOrgStdGetQuery = query.countryOrgStdGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryOrgStdGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryOrgStdGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for OrgStd record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for OrgStd record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardCode").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryFullName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryFullName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryShortName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryShortName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrganizationStandards["+i+"].effectiveDate").trim();
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrganizationStandards["+i+"].expirationDate").trim();
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString())
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_organizationStandardCode: ", "DB_organizationStandardCode: ",
	        					"Response_organizationStandardName: ", "DB_organizationStandardName: ",
	        					"Response_countryFullName: ", "DB_countryFullName: ",
	        					"Response_countryShortName: ", "DB_countryShortName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", "Contry org std details for the geoplId: "+geopoliticalId, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Contry org std details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Contry org std details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
        		}
        	
		}else {
        	logger.error("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		}
	}
	
	private void countryOrgStdValidationWithDates(String responsestr, String testCaseID, int k, String targetDate, String endDate)
	{
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = null;
		if(js.get("data["+k+"].countryOrganizationStandards")!=null)
		{
			responseRows = js.get("data["+k+"].countryOrganizationStandards");
			geopoliticalId = js.get("data["+k+"].geopoliticalId").toString();
		
		//***get query
		String countryOrgStdGetQuery = query.countryNoParameterOrgStdGetQuery(geopoliticalId, targetDate, endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryOrgStdGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryOrgStdGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for OrgStd record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for OrgStd record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardCode").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].organizationStandardName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryFullName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryFullName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryShortName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].countryOrganizationStandards["+i+"].countryShortName").trim());
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].effectiveDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrganizationStandards["+i+"].effectiveDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].countryOrganizationStandards["+i+"].expirationDate")))
        		{
        			getResponseRows.add("");
        		}else {
        			String str = js.getString("data["+k+"].countryOrganizationStandards["+i+"].expirationDate");
        			int index = str.indexOf("T");
        			str = str.substring(0, index);
        			getResponseRows.add(str);
        		}
        	}
        	
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString())
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_organizationStandardCode: ", "DB_organizationStandardCode: ",
	        					"Response_organizationStandardName: ", "DB_organizationStandardName: ",
	        					"Response_countryFullName: ", "DB_countryFullName: ",
	        					"Response_countryShortName: ", "DB_countryShortName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", "Contry org std details for the geoplId: "+geopoliticalId, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Contry org std details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Contry org std details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
        		}
        	
		}else {
        	logger.error("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for OrgStd record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }}
		
	}

	private void countryTrnslGeoplValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].translationGeopoliticals");
		//***get query
		String countryTrnslGeoplGetQuery = query.countryTrnslGeoplGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryTrnslGeoplGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryTrnslGeoplGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for TrnslGeopl record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for TrnslGeopl record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].languageCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].languageCode"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].scriptCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].scriptCode"));
        		}
        		if(StringUtils.isBlank(js.getString("data["+k+"].translationGeopoliticals["+i+"].translatedName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].translationGeopoliticals["+i+"].translatedName"));
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        				&& getResultDB.get(j+3).toString().equals(getResponseRows.get(e+3).toString())
	        				&& getResultDB.get(j+4).toString().equals(getResponseRows.get(e+4).toString())
	        				&& getResultDB.get(j+5).toString().equals(getResponseRows.get(e+5).toString())
	        				&& getResultDB.get(j+6).toString().equals(getResponseRows.get(e+6).toString())
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString(),
	        					getResponseRows.get(e+3).toString(), getResultDB.get(j+3).toString(),
	        					getResponseRows.get(e+4).toString(), getResultDB.get(j+4).toString(),
	        					getResponseRows.get(e+5).toString(), getResultDB.get(j+5).toString(),
	        					getResponseRows.get(e+6).toString(), getResultDB.get(j+6).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_languageCode: ", "DB_languageCode: ",
	        					"Response_scriptCode: ", "DB_scriptCode: ",
	        					"Response_translatedName: ", "DB_translatedName: ",
	        					"Response_versionDate: ", "DB_versionDate: ",
	        					"Response_versionNumber: ", "DB_versionNumber: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("TrnslGeopl details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "TrnslGeopl details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for TrnslGeopl record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for TrnslGeopl record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryAffilTypeValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalAffiliations");
		//***get query
		String countryAffilTypeGetQuery = query.countryAffilTypeGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryAffilTypeGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryAffilTypeGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for AffilType record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for AffilType record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].affiliationTypeName")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalAffiliations["+i+"].affiliationTypeName"));
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_affiliationTypeName: ", "DB_affiliationTypeName: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
       			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("AffilType details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "AffilType details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for AffilTyp record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for AffilTyp record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryHolidayValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalHolidays");
		//***get query
		String countryHolidayGetQuery = query.countryHolidayGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryHolidayGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryHolidayGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString()
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
	        			break;
        			}
        			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("Holiday details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "Holiday details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for Holiday record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Holiday record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
		
	}

	private void countryUomTypeValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = new ArrayList<>();; 
		responseRows = js.get("data["+k+"].geopoliticalUnitOfMeasures");
		//***get query
		String countryUomTypeGetQuery = query.countryUomTypeGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryUomTypeGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryUomTypeGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
        	test.pass("Total number of records matching for UOM Type record between DB & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "",
					"", "", "", "Pass", "Total number of records matching for UOM Type record between DB & Response: "+responseRows.size()+", below are the test steps for this test case" );
        	List<String> getResponseRows = new ArrayList<>();
        	for(int i=0; i<responseRows.size(); i++)
        	{
        		if(StringUtils.isBlank(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].uomTypeCode")))
        		{
        			getResponseRows.add("");
        		}else {
        			getResponseRows.add(js.getString("data["+k+"].geopoliticalUnitOfMeasures["+i+"].uomTypeCode"));
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        				&& getResultDB.get(j+1).toString().equals(getResponseRows.get(e+1).toString()) 
	        				&& getResultDB.get(j+2).toString().equals(getResponseRows.get(e+2).toString()) 
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString(),
	        					getResponseRows.get(e+1).toString(), getResultDB.get(j+1).toString(),
	        					getResponseRows.get(e+2).toString(), getResultDB.get(j+2).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_uomTypeCode: ", "DB_uomTypeCode: ",
	        					"Response_effectiveDate: ", "DB_effectiveDate: ",
	        					"Response_expirationDate: ", "DB_expirationDate: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
       			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("uomType details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "uomType details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for UOM Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for UOM Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
		}
	}

	private void countryGeoTypeValidation(String responsestr, String testCaseID, int k, String targetDate , String endDate) {
		JsonPath js = new JsonPath(responsestr);
		List<String> responseRows = js.get("data["+k+"].geopoliticalType");
		//***get query
		String countryGeoTypeNameGetQuery = query.countryGeoTypeNameGetQuery(geopoliticalId, targetDate , endDate);
		//***get the fields needs to be validate in DB
		List<String> fields = ValidationFields.countryGeoTypeNameGetMethodDbFields();
		//***get the result from DB
		List<String> getResultDB = new ArrayList<>();
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(countryGeoTypeNameGetQuery);
			result.last();
			result.beforeFirst();
			String checkNull = null;
			while(result.next()) {
				for(int d=0; d<fields.size(); d++)
				{
					checkNull = result.getString(fields.get(d));
					if(StringUtils.isBlank(checkNull)) {
						checkNull="";
					}	
					getResultDB.add(checkNull.trim());
				}
	        }
			stmt.close();
		} catch(SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		if(getResultDB.size() != 0 && responseRows!=null)
		{
		if(getResultDB.size() == responseRows.size()*fields.size())
		{
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
        	}
        		if(responseRows.size()==0)
	        	{
	        		logger.info("0 matching records and there is no validation required");
		        	test.info("0 matching records and there is no validation required");
	        	}
        		boolean rowMatch=false;
        		for(int e=0; e<getResultDB.size(); e=e+fields.size())
        		{
        		for(int j=0; j<getResultDB.size(); j=j+fields.size())
	        	{
        			if(
	        				getResultDB.get(j).toString().equals(getResponseRows.get(e).toString()) 
	        			)
        			{
        				rowMatch=true;
        				//***write result to excel
	        			String[] responseDbFieldValues = {
	        					getResponseRows.get(e).toString(), getResultDB.get(j).toString()
	        			};
	        			String[] responseDbFieldNames = {
	        					"Response_geopoliticalTypeName: ", "DB_geopoliticalTypeName: "
	        			};
	        			writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues, responseDbFieldNames);
	            		test.pass(writableResult.replaceAll("\n", "<br />"));  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", writableResult, "Pass", "" );
	        			break;
        			}
        			
	        	}
        		if(!rowMatch)
        		{
	            		test.fail("geoType details are not matiching for the geoplId: "+geopoliticalId);  
	        			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
            					"", "", "geoType details are not matiching for the geoplId: "+geopoliticalId, "Fail", "" );
        			
        		}
        		rowMatch=false;
	        	}
        	
		}else {
        	logger.error("Total number of records not matching for Geopolitical Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
			logger.error("Execution is completed for Failed Test Case No. "+testCaseID);
			logger.error("------------------------------------------------------------------");
        	test.fail("Total number of records not matching for Geopolitical Type record between DB: "+getResultDB.size()/fields.size()+" & Response: "+responseRows.size());
        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "",
					"", "Fail", "" );
        }
		}else {
			int resNullCount;
			if(responseRows==null)
			{
				resNullCount=0;
			}else {
				resNullCount = responseRows.size(); 
			}
			if(getResultDB.size()==0 && resNullCount==0)
			{
				test.pass("Response and DB record count is 0, so validation not needed");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response and DB record count is 0, so validation not needed", "Pass", "" );
			}else {
				test.fail("Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching");  
    			ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA",	"", "",
    					"", "", "Response record count: "+resNullCount+" and DB record count: "+getResultDB.size()+" is not matching", "Fail", "" );
			}
			
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
		countryCode = inputData1.get(testCaseId).get("countryCode");
		countryShortName = inputData1.get(testCaseId).get("countryShortName");
		orgStandardCode = inputData1.get(testCaseId).get("orgStandardCode");
		targetDate = inputData1.get(testCaseId).get("targetDate");
		endDate = inputData1.get(testCaseId).get("endDate");
	}
}
