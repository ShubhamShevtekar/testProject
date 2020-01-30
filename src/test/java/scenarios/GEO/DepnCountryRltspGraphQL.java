package scenarios.GEO;

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

public class DepnCountryRltspGraphQL extends Reporting {

	String scenarioName = getClass().getSimpleName();
	String TestCaseDescription, scenarioType, dependentCountryCd, geopoliticalId;
	Queries query = new Queries();
	String fileName = this.getClass().getSimpleName();
	ExcelUtil ex = new ExcelUtil();
	String runFlag = null;
	String writableInputFields, writableResult = null;
	ResponseMessages resMsgs = new ResponseMessages();
	static Logger logger = Logger.getLogger(DepnCountryRltspGraphQL.class);
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

	//Failing.... Issue raised on 14 oct , Yet to get confirm from tamil
	
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
		// ***get test case ID with method name

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.depnCntryRltspGraphQLWithParam(dependentCountryCd);
			System.out.println("payload "+payload);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.pass("URI passed: "+getEndPoinUrl);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String internalMsg = js.getString("meta.message.internalMessage");
			String Wsstatus=js.getString("meta.message.status");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");

				// ***get the DB query
				String depnCntryRltspTypeGetQuery = query.depnCntryRltspGetQuery(dependentCountryCd);
				System.out.println("Query "+depnCntryRltspTypeGetQuery);
				
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.depnCntryRltspGrapphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(depnCntryRltspTypeGetQuery, fields, fileName,
						testCaseID);
				
				if (getResultDB.size() == responseRows.size() * fields.size()) {
					logger.info("Total number of records matching between DB & Response: " + responseRows.size());
					test.pass("Total number of records matching between DB & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "NA", "", "", "",
							"" + Wscode, "", "Pass", "Total number of records matching between DB & Response: "
									+ responseRows.size() + ", below are the test steps for this test case");
					List<String> getResponseRows = new ArrayList<>();
					for (int i = 0; i < responseRows.size(); i++) {
						if (StringUtils.isBlank(js.getString("data.countries.geopoliticalId[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows.add(js.getString("data.countries.geopoliticalId[" + i + "]"));
						}
						if (StringUtils
								.isBlank(js.getString("data.countries.dependentCountryRelationship.dependentRelationshipDescription[" + i + "]"))) {
							getResponseRows.add("");
						} else {
							getResponseRows
									.add(js.getString("data.countries.dependentCountryRelationship.dependentRelationshipDescription[" + i + "]"));
						}
					}
					logger.info("Each record validation starts");
					test.info("Each record validation starts");
					int z = 1;
					boolean rowmatch = false;
					for (int e = 0; e < getResultDB.size(); e = e + fields.size()) {
						for (int j = 0; j < getResultDB.size(); j = j + fields.size()) {
							if (getResultDB.get(j).toString().equals(getResponseRows.get(j).toString())
									&& getResultDB.get(j + 1).toString().equals(getResponseRows.get(j + 1).toString())) {
								rowmatch = true;
								// ***write result to excel
								String[] responseDbFieldValues = { getResponseRows.get(j).toString(),
										getResultDB.get(j).toString(), getResponseRows.get(j + 1).toString(),
										getResultDB.get(j + 1).toString()};
								String[] responseDbFieldNames = { "Response_geopoliticalId: ", "DB_geopoliticalId: ",
										"Response_dependentRelationshipDescription: ",
										"DB_dependentRelationshipDescription: " };
								writableResult = Miscellaneous.geoFieldInputNames(responseDbFieldValues,
										responseDbFieldNames);
								test.info("Record " + z + " Validation:");
								test.pass(writableResult.replaceAll("\n", "<br />"));
								ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
										writableResult, "Pass", "");
								z++;
								break;
							}
						}

						if (!rowmatch) {
							test.fail("dependent country Relationship details are not matiching for the geoplId: " + dependentCountryCd);
							ex.writeExcel(fileName, "", TestCaseDescription, scenarioType, "NA", "", "", "", "",
									"dependent country Relationship details are not matiching for the geoplId: " + dependentCountryCd,
									"Fail", "");
						}
						rowmatch = false;
					}
				} else {
					logger.error("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Total number of records matching between DB: " + getResultDB.size() / fields.size()
							+ " & Response: " + responseRows.size());
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", Wsstatus,
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}
			}
			
			else {
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

        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
					responsestr1, "Fail", "" );
        	Assert.fail("Test Failed");
        }
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
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
		boolean testResult = false;
		// ***get test case ID with method name

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.depnCntryRltspGraphQLWithParam(dependentCountryCd);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String internalMsg = js.getString("meta.message.internalMessage");
			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
				// ***get the DB query
				String depnCntryRltspTypeGetQuery = query.depnCntryRltspGetQuery(dependentCountryCd);
				
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.depnCntryRltspGrapphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(depnCntryRltspTypeGetQuery, fields, fileName,
						testCaseID);				

				if (getResultDB.size() == 0 && responseRows.size() * fields.size() == 0) {
					logger.info("DepnCountryRltsp records are not available for this dependentCountryCd : " + dependentCountryCd);
					test.info("DepnCountryRltsp records are not available for this dependentCountryCd : " + dependentCountryCd);
					test.pass("DepnCountryRltsp records are not available for this dependentCountryCd : " + dependentCountryCd);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Pass", internalMsg);
				} else {
					logger.error("DepnCountryRltsp records are available for this dependentCountryCd : " + dependentCountryCd);
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("DepnCountryRltsp records are available for this dependentCountryCd : " + dependentCountryCd);
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr1, "Fail", internalMsg);
					Assert.fail("Test Failed");
				}

			} else {
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

        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
					responsestr1, "Fail", "" );
        	Assert.fail("Test Failed");
        }

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
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
		boolean testResult = false;
		// ***get test case ID with method name

		try {
			// ***get the test data from sheet
			testDataFields(scenarioName, testCaseID);
			test.log(Status.INFO, MarkupHelper.createLabel(TestCaseDescription, ExtentColor.PURPLE));
			// ***send the data to create request and get request
			String payload = PostMethod.depnCntryRltspGraphQLWithTwoWrongAttribute(dependentCountryCd);
			String reqFormatted = Miscellaneous.jsonFormat(payload);
			test.info("Input Request created:");
			logger.info("Input Request created");
			test.info(reqFormatted.replaceAll("\n", "<br />"));
			// ***get end point url
			String getEndPoinUrl = RetrieveEndPoints.getEndPointUrl("graphQLGet", fileName, level+".GraphQL");
			// ***send request and get response
			Response res = GetResponse.sendRequestPost(payload, tokenValues[0], token, getEndPoinUrl, fileName,
					testCaseID);
			String responsestr = res.asString();
			String responsestr1 = Miscellaneous.jsonFormat(responsestr);
			test.info("Response Recieved:");
			logger.info("Response Recieved");
			test.info(responsestr1.replaceAll("\n", "<br />"));
			JsonPath js = new JsonPath(responsestr);
			
			List<String> responseRows = js.get("data.countries");
			int Wscode = res.statusCode();
			String meta = js.getString("meta");String actualRespVersionNum = js.getString("meta.version");
			String timestamp = js.getString("meta.timestamp");
			String internalMsg = js.getString("meta.message.internalMessage");
			int errrorMsgLength = js.get("meta.errors.size");
			String expectMessag1 = resMsgs.DepCntryOrgeErrorMsg1;
			String expectMessage2 = resMsgs.DepCntryOrgeErrorMsg2;
			String expectMessage3 = resMsgs.DepCntryOrgeErrorMsg3;
			List<String> errorMsg1 = new ArrayList<String>();
			List<String> errorMsg2 = new ArrayList<String>();
			for (int i = 0; i < errrorMsgLength; i++) {
				errorMsg1.add(js.getString("meta.errors[" + i + "].error"));
				errorMsg2.add(js.getString("meta.errors[" + i + "].message"));
			}

			if (Wscode == 200 && meta != null && (!meta.contains("timestamp"))&& actualRespVersionNum.equalsIgnoreCase("1.0.0")) {
				logger.info("Response status validation passed: " + Wscode);
				test.pass("Response status validation passed: " + Wscode);
				test.pass("Response meta validation passed");
				test.pass("Response timestamp validation passed");test.pass("Response API version number validation passed");
				// ***get the DB query
				String depnCntryRltspTypeGetQuery = query.depnCntryRltspGetQuery(dependentCountryCd);
				
				// ***get the fields needs to be validate in DB
				List<String> fields = ValidationFields.depnCntryRltspGrapphQLMethodDbFields();
				// ***get the result from DB
				List<String> getResultDB = DbConnect.getResultSetFor(depnCntryRltspTypeGetQuery, fields, fileName,
						testCaseID);
				
				if ((errorMsg1.get(0).equals("ValidationError") && errorMsg2.get(0).equals(expectMessag1))
						&& (errorMsg1.get(1).equals("ValidationError") && errorMsg2.get(1).equals(expectMessage2))
						&& (errorMsg1.get(2).equals("ValidationError") && errorMsg2.get(2).equals(expectMessage3)) ) {

					logger.info(
							"Expected error message is getting received in response when sending passing the invalid any one of the attribute name");
					logger.info("Execution is completed for Passed Test Case No. " + testCaseID);
					logger.info("------------------------------------------------------------------");
					test.pass(
							"Expected error message is getting received in response passing the invalid any one of the attribute name");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted,
							writableInputFields, "NA", "", "" + Wscode, responsestr1, "Pass", "");
					test.log(Status.PASS, MarkupHelper.createLabel("Test Passed", ExtentColor.GREEN));
				} else {
					logger.error("Expected error message is not getting received in response");
					logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
					logger.error("------------------------------------------------------------------");
					test.fail("Expected error message is not getting received in response");
					ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, reqFormatted, "", "", "",
							"" + Wscode, responsestr, "Fail", "orgStdCd" + expectMessag1);
					Assert.fail("Test Failed");

				}

			}  else {
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

        	ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", ""+Wscode,
					responsestr1, "Fail", "" );
        	Assert.fail("Test Failed");
        }

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception thrown when executing the test case: " + e);
			logger.error("Execution is completed for Failed Test Case No. " + testCaseID);
			logger.error("------------------------------------------------------------------");
			test.fail("Exception thrown when executing the test case: " + e);
			ex.writeExcel(fileName, testCaseID, TestCaseDescription, scenarioType, "", "", "", "", "", "", "Fail",
					"" + e);
			Assert.fail("Test Failed");
		}
	}

	// ***get the values from test data sheet
	public void testDataFields(String scenarioName, String testCaseId) {
		HashMap<String, LinkedHashMap<String, String>> inputData1 = null;
		try {
			inputData1 = ex.getTestData(scenarioName);
		} catch (IOException e) {
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseId, "", "", "", "", "", "", "", "", "Fail", "Exception: " + e.toString());
			test.fail("Unable to retrieve the test data file/fields");
		}
		TestCaseDescription = inputData1.get(testCaseId).get("TestCaseDescription");
		scenarioType = inputData1.get(testCaseId).get("Scenario Type");
		dependentCountryCd = inputData1.get(testCaseId).get("dependentCountryCd");
	}

}
