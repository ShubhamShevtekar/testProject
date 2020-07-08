package utils.base;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import wsMethods.base.GetResponse;

public class TestResultValidation extends Reporting{
	static ExcelUtil ex = new ExcelUtil();
	public static boolean testValidationWithDB(Response res, String[] inputFieldValues, List<String> dbFields,List<String> responsefields)
	{
		boolean testResult = false;
		int j=0;
		
		for(int i=0; i<dbFields.size(); i++)
		{
			
			if(inputFieldValues[i].toString().equals(dbFields.get(i).toString()))
			{
				testResult = true;
				j++;
			}

		}
		if(j!=dbFields.size())
		{
			testResult = false;
		}
		return testResult;		
	}
	
	public static boolean testValidationWithoutDB(Response res, String[] validationMessage, String[] responsefields)
	{
		boolean testResult = false;
		int j=0;
		for(int i=0; i<validationMessage.length; i++)
		{
			if(validationMessage[i].toString().equals(responsefields[i].toString()))
			{
				testResult = true;
				j++;
			}
		}
		if(j!=validationMessage.length)
		{
			testResult = false;
		}
		return testResult;
	}
	
	public static boolean testValidationForJMS(String[] inputFieldValues, List<String> dbFields)
	{
		boolean testResult = false;
		int j=0;
		
		for(int i=0; i<dbFields.size(); i++)
		{
			
			if(inputFieldValues[i].toString().equals(dbFields.get(i).toString()))
			{
				testResult = true;
				j++;
			}

		}
		if(j!=dbFields.size())
		{
			testResult = false;
		}
		return testResult;		
	}
	
	public String versionValidation (String fileName, String tokenKey, String tokenVal, String versionURL){		
		Response actuatorRes = GetResponse.sendActuatorRequestCommand(tokenKey, tokenVal, versionURL, fileName);
		String actRes = actuatorRes.asString(); 
		JsonPath json = new JsonPath(actRes);
		String actuatorVersion = json.getString("build.version");
		return actuatorVersion;
	}

}
