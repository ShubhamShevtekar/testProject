package scenarios.GEO.v1;

import utils.v1.Reporting;

public class GeoPost extends Reporting {
//	String scenarioName = getClass().getSimpleName();
//	String TestCaseDescription, scenarioType, userId, geoTypeName, token;
//	Queries query = new Queries();
//	String[] tokenValues = new String[2];
//	String fileName = this.getClass().getSimpleName();
//	ExcelUtil ex = new ExcelUtil();
//	String runFlag = null;
//	@BeforeClass
//	public void before(){
//		
//		//***create test result excel file
//		ex.createResultExcel(fileName);
//		//***get token properties
//		tokenValues = RetrieveEndPoints.getTokenProperties(fileName);
//		//***get token
//		URL url;
////		try {
////			url = new URL(tokenValues[1]);
////			URLConnection con = url.openConnection();
////			InputStream in = con.getInputStream();
////			String encoding = con.getContentEncoding();
////			encoding = encoding == null ? "UTF-8" : encoding;
////			token = IOUtils.toString(in, encoding);
////		} catch (IOException e) {
////			e.printStackTrace();
////			test.fail("Unable to get the token, exception thrown: "+e.toString());
////		}
//	}
//	
//	@BeforeMethod
//	protected void startRepo(Method m) throws IOException
//	{
//		
//		runFlag = getExecutionFlag(m.getName(), fileName);
//		if(runFlag.equalsIgnoreCase("Yes")) {
//			String testCaseName = m.getName();
//			test = extent.createTest(testCaseName);
//		}
//	}
//	@Test
//	public void TC_01()
//	{	
//		if(!runFlag.equalsIgnoreCase("Yes")) {
//			throw new SkipException("Execution skipped as per test flag set");
//		}
//		test.log(Status.INFO, MarkupHelper.createLabel("Validate that the Geopolitical Type service is successfully created when valid values are passed for all attributes in JSON Request.", ExtentColor.PURPLE));
//		boolean testResult=false;
//		//***get test case ID with method name
//		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
//		//***get the test data from sheet
//		testDataFields(scenarioName, testCaseID);
//		//***send the data to create request and get request
//		String payload = PostMethod.geoPost_request(userId, geoTypeName);
//		String reqFormatted = Miscellaneous.jsonFormat(payload);
//		test.info("Input Request created:");
//		test.info(reqFormatted.replaceAll("\n", "<br />"));
//		//***get end point url
//		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("locTypePost", fileName);
//		//***send request and get response
//		Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
//		String responsestr=res.asString(); 
//		String responsestr1 = Miscellaneous.jsonFormat(responsestr);
//		test.info("Response Recieved:");
//		test.info(responsestr1.replaceAll("\n", "<br />"));
//		JsonPath js = new JsonPath(responsestr);
//        String Wsstatus= res.getStatusLine();
//        String internalMsg = js.getString("message");
//        int Wscode= res.statusCode();
//        
//        
//        if(Wscode == 200)
//        {
//        	test.pass("Response status validation passed: "+Wscode);
//        	//***get the DB query
//    		String geoTypePostQuery = query.GeoTypePost_Query(geoTypeName);
//    		//***get the fields needs to be validate in DB
//    		List<String> fields = ValidationFields.geoType_DB_Fields();
//    		//***get the result from DB
//    		List<String> getResultDB = DbConnect.getResultSetFor(geoTypePostQuery, fields, fileName, testCaseID);
//    		//***send the input, response, DB result for validation
//    		String[] inputFieldValues = {userId, geoTypeName};
//    		List<String> resFields = ValidationFields.geoType_Response_Fields(res);
//    		testResult = TestResultValidation.testValidation_withDB(res, inputFieldValues, getResultDB, resFields);
//			//***write result to excel
//    		String writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues, inputFieldValues);
//    		String writableDB_Fields = Miscellaneous.geoDB_FieldNames(getResultDB, inputFieldValues);
//    		test.info("Input Data Values:");
//    		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
//    		test.info("DB Data Values:");
//    		test.info(writableDB_Fields.replaceAll("\n", "<br />"));
//    		if(testResult)
//    		{
//    			test.pass("Comparison with DataBase passed");
//    			
//	    			if(js.getString("geoplTypeId")!=null)
//	    			{
//	    			List<String> fields1 = ValidationFields.geoType_ID_DB_Fields();
//	    			List<String> getResultDB1 = DbConnect.getResultSetFor(geoTypePostQuery, fields1, fileName, testCaseID);
//	    			fields1.add("Geopolitical Type Details are successfully saved with GeoplTypeId :"+js.getString("geoplTypeId"));
//	    			String[] inputFieldValues1 = {js.getString("geoplTypeId"),js.getString("message")};
//	    			testResult = false;
//	    			testResult = TestResultValidation.testValidation_withDB(res, inputFieldValues1, getResultDB1, resFields);
//	    			if(testResult)
//	    			{
//	    				test.pass("Geopolitical ID: "+js.getString("geoplTypeId")+" is matching with DB");
//	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
//	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Pass", "" );
//	    				test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
//	    			}else {
//	    				test.fail("Geopolitical type ID: "+js.getString("geoplTypeId")+" comparison between response and DB failed");
//	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
//	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
//	        			Assert.fail("Test Failed");
//	    			}
//	    			}else {
//	    				test.fail("Geopolitical Type ID is not available in response");
//	        			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
//	        					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
//	        			Assert.fail("Test Failed");
//	    			}
//    		}else {
//    			test.fail("Comparison with DataBase failed");
//    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
//    					writableInputFields, writableDB_Fields, Wsstatus, ""+Wscode, responsestr1, "Fail", "" );
//    			Assert.fail("Test Failed");
//    		}
//        	
//        }else {
//        	test.fail("Response staus validation failed: "+Wscode);
//        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
//					responsestr1, "Fail", internalMsg );
//        	Assert.fail("Test Failed");
//        }	
//		
//	}
////	@Test
////	public void TC_02()
////	{
////		if(!runFlag.equalsIgnoreCase("Yes")) {
////			throw new SkipException("Execution skipped as per test flag set");
////		}
////		test.log(Status.INFO, MarkupHelper.createLabel("Validate that the location Type service is not created when passing existing location type code in JSON Request", ExtentColor.PURPLE));
////		boolean testResult=false;
////		//***get test case ID with method name
////				String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
////				//***get the test data from sheet
////				testDataFields(scenarioName, testCaseID);
////				//***send the data to create request and get request
////				String payload = PostMethod.geoPost_request(userId, locTypeCode, locTypeName, locTypeDesc);
////				String reqFormatted = Miscellaneous.jsonFormat(payload);
////				test.info("Input Request created:");
////				test.info(reqFormatted.replaceAll("\n", "<br />"));
////				//***get end point url
////				String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("locTypePost", fileName);
////				//***send request and get response
////				Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
////				String responsestr=res.asString(); 
////				responsestr = Miscellaneous.jsonFormat(responsestr);
////				test.info("Response:");
////				test.info(responsestr.replaceAll("\n", "<br />"));
////				JsonPath js = new JsonPath(responsestr);
////		        String Wsstatus= js.get("meta.message.status");
////		        String internalMsg = js.getString("meta.message.internalMessage");
////		        int Wscode= res.statusCode();
////		        if(Wsstatus!=null)
////		        {
////		        if(Wsstatus.equalsIgnoreCase("ERROR") && Wscode == 400)
////		        {
////		        	test.pass("Response status code 400 validation passed: "+Wscode);
////		        	//***send the input & response for validation
////		        	String[] validationMessage = {"Location Type already exists with code: "+locTypeCode};
////		        	String[] resFields = {internalMsg};
////		        	testResult = TestResultValidation.testValidation_withoutDB(res, validationMessage, resFields);
////		        	//***write result to excel
////		        	String[] inputFieldValues = {locTypeCode, locTypeName, locTypeDesc};
////		        	String writableInputFields = Miscellaneous.geoFieldInputNames(inputFieldValues);
////		    		test.info("Input Data Values:");
////		    		test.info(writableInputFields.replaceAll("\n", "<br />"));    		
////		    		if(testResult)
////		    		{
////		    			test.pass("Response error message validated");
////		    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
////		    					writableInputFields, "", Wsstatus, ""+Wscode, responsestr, "Pass", "" );
////		    			test.log(Status.PASS, MarkupHelper.createLabel("test status", ExtentColor.GREEN));
////		    		}else {
////		    			test.fail("Response error message validation failed");
////		    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
////		    					writableInputFields, "", Wsstatus, ""+Wscode, responsestr, "Fail", "" );
////		    			Assert.fail("Test Failed");
////		    		}
////		        }else {
////		        	test.fail("Response staus code 400 validation failed: "+Wscode);
////		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,"", "", Wsstatus, ""+Wscode,
////							responsestr, "Fail", internalMsg );
////		        	Assert.fail("Test Failed");
////		        }
////		        }else {
////		        	test.fail("Response not getting received as expected");
////		        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", Wsstatus, ""+Wscode,
////							responsestr, "Fail", internalMsg );
////		        	Assert.fail("Test Failed");
////		        }
////	}
//	
//	@AfterSuite
//	public void endReportSuite()
//	{
//		extent.flush();
//	}
//	public void testDataFields(String scenarioName, String testCaseId)
//	{
//		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
//		try {
//			inputData1 = ex.getTestData(scenarioName);
//		} catch (IOException e) {
//			e.printStackTrace();
//			ex.writeExcel(fileName, testCaseId, "", "", "", "", "", "", "", "", "Fail", "Exception: "+e.toString());
//			test.fail("Unable to retrieve the test data file/fields");
//		}
//		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
//		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
//		userId = inputData1.get(testCaseId).get("UserName");
//		geoTypeName = inputData1.get(testCaseId).get("GeopoliticalTypeName");
//	}

}
