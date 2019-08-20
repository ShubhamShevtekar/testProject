package utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.restassured.response.Response;

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

}
