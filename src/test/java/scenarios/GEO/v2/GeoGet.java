package scenarios.GEO.v2;

import utils.v2.Reporting;

public class GeoGet extends Reporting {
//	String token, runFlag;
//	String scenarioName = getClass().getSimpleName();
//	Queries query = new Queries();
//	String[] tokenValues = new String[2];
//	String fileName = this.getClass().getSimpleName();
//	ExcelUtil ex = new ExcelUtil();
//	@BeforeClass
//	public void before() throws IOException{
//		
//		//***create test result excel file
//		ex.createResultExcel(fileName);
//		//***get token properties
//		tokenValues = RetrieveEndPoints.getTokenProperties(fileName);
//		//***get token
//		URL url = new URL(tokenValues[1]);
//		try {
//			url = new URL(tokenValues[1]);
//			URLConnection con = url.openConnection();
//			InputStream in = con.getInputStream();
//			String encoding = con.getContentEncoding();
//			encoding = encoding == null ? "UTF-8" : encoding;
//			token = IOUtils.toString(in, encoding);
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//			test.fail("Unable to get the token, exception thrown: "+e.toString());
//		}
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
//	public void TC_Get()
//	{
//		String TestCaseDescription = "Validate all the records between GET response and DataBase";
//		String scenarioType = "GET";
//		test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
//		//***get test case ID with method name
//		String testCaseID = new Object(){}.getClass().getEnclosingMethod().getName();
//		//***get end point url
//		String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("locTypeGet", fileName);
//		Response res = GetResponse.sendRequestGet(tokenValues[0], token, getEndPoinUrl, fileName, testCaseID);
//		String responsestr=res.asString(); 
////		responsestr = miscellaneous.jsonFormat(responsestr);
////		test.info("Response:");
////		test.info(responsestr);
//		JsonPath js = new JsonPath(responsestr);
//        String Wsstatus= js.get("meta.message.status");
//        String internalMsg = js.getString("meta.message.internalMessage");
//        int Wscode= res.statusCode();
//        if(Wsstatus!=null)
//        {
//        if(Wsstatus.equalsIgnoreCase("success") && Wscode == 200)
//        {
//        	test.pass("Response status validation passed: "+Wscode);
//        	//***get the DB query
//    		String locPutQuery = query.GeoGet_Query();
//    		//***get the fields needs to be validate in DB
//    		List<String> fields = ValidationFields.geoType_DB_Fields();
//    		//***get the result from DB
//    		HashMap<String, LinkedHashMap<String, String>> getResultDB = DbConnect.getResultSetForGet(locPutQuery, fields, fileName, testCaseID);
//    		List<String> primKey = DbConnect.getPrimaryKey(locPutQuery, fields, fileName, testCaseID);
//    		System.out.println(getResultDB.size());
//    		List<String> responseRows = js.get("locationTypes");
//    		int responseCount = responseRows.size();
//    		System.out.println(responseCount);
//    		if(responseCount==getResultDB.size())
//    		{
//    			test.pass("Records count is matching between Response & DB, totat records: "+responseCount);
//    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "NA", "NA", Wsstatus, ""+Wscode,
//    					"", "Pass", "Records count is matching between Response & DB: "+responseCount );
//	    		for(int z=0; z<responseCount; z++) {
//	    			String[] responseRecord = {js.get("locationTypes["+z+"].locationTypeCd"), js.get("locationTypes["+z+"].locationTypeName")
//	    					, js.get("locationTypes["+z+"].locationTypeDescription")};
//	    			int matchcount = 0;
//	    			for(int x=0; x<fields.size(); x++)
//	    			{
//	    				String resValue = responseRecord[x];
//	    				String DBValue = getResultDB.get(primKey.get(z)).get(fields.get(x));
//	    				if(StringUtils.isBlank(DBValue)) {
//	    					DBValue="";
//	    				}	    				
//	    				if(StringUtils.isBlank(responseRecord[x])) {
//	    					resValue="";
//	    				}
//	    				if(DBValue.equals(resValue))
//						{
//							matchcount++;
//						}else {
//							System.out.println(DBValue);
//							System.out.println(resValue);
//							test.fail("For the Location Type Code: "+getResultDB.get(primKey.get(z)).get(fields.get(0))+" the DB field value: "+DBValue.replaceAll("\n", "<br />")+"<br />is not matching with response value: "+resValue.replaceAll("\n", "<br />"));
//		    				ex.writeExcel(fileName, "", "For the Location Type Code: "+getResultDB.get(primKey.get(z)).get(fields.get(0))+" the DB field value: "+DBValue+" is not matching with response value: "+resValue, scenarioType, "NA", "NA", "NA", Wsstatus, ""+Wscode,
//		        					"", "Fail", "" );
//						}
//	    			}
//	    			if(matchcount==3)
//	    			{
//	    				test.pass("All the fields for Location Type Code: "+getResultDB.get(primKey.get(z)).get(fields.get(0))+" is matching between DB & Response");
//	    				ex.writeExcel(fileName, "", "All the fields for Location Type Code: "+getResultDB.get(primKey.get(z)).get(fields.get(0))+" is matching between DB & Response", scenarioType,"NA", "NA", "NA", Wsstatus, ""+Wscode,
//	        					"", "Pass", "" );
//	    			}
//	    		}
//    		}else {
//    			test.fail("Records count is not matching between Response & DB, totat records in Response: "+responseCount+" and total records in DB: "+getResultDB.size());
//    			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "NA", "NA", Wsstatus, ""+Wscode,
//    					"", "Fail", "Records count is not matching between Response & DB" );
//    			Assert.fail("Test Failed");
//    		}
//    		
//        }else {
//        	test.fail("Response staus validation failed: "+Wscode);
//        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "NA", "NA", Wsstatus, ""+Wscode,
//					responsestr, "Fail", internalMsg );
//        	Assert.fail("Test Failed");
//        }
//        }else {
//        	test.fail("Response not getting received as expected");
//        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType,"","", "", Wsstatus, ""+Wscode,
//					responsestr, "Fail", internalMsg );
//        	Assert.fail("Test Failed");
//        }
//	}
}
