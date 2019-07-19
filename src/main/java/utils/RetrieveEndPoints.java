package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.testng.Assert;

public class RetrieveEndPoints extends Reporting {
	
	public static String testlevel=null;
	public static String geoPut, geoPost, geoGet=null;
	public static String key, tokenUrl = null;
	static HashMap<String, String> urlOutParams = new LinkedHashMap<String, String>();
	static ExcelUtil ex = new ExcelUtil();
	public static void getProperty(String methodName, String fileName) 	
	{
		try {
		testlevel = Miscellaneous.getEnv();
		
		//get endpoints as per environment
		File file1 = new File("src/main/resources/EndPoint_Properties/endPoints.properties");
		FileInputStream fileInput1 = new FileInputStream(file1);
		Properties prop1 = new Properties();
		prop1.load(fileInput1);
		switch(testlevel) {
				
				case "L1":
					geoPost = prop1.getProperty(methodName);
					break;
				case "L2":
					geoPost = prop1.getProperty(methodName);
					break;
				case "L3":
					geoPost = prop1.getProperty(methodName);
					urlOutParams.put("geoPost", geoPost);
					geoPut = prop1.getProperty(methodName);
					urlOutParams.put("geoPut", geoPut);
					geoGet = prop1.getProperty(methodName);
					urlOutParams.put("geoGet", geoGet);
					break;
		}
		
		//get token key and url as per environment
		File file2 = new File("src/main/resources/Authentication_Properties/token.properties");
		FileInputStream fileInput2 = new FileInputStream(file2);
		Properties prop2 = new Properties();
		prop2.load(fileInput2);
		switch(testlevel) {
		
			case "L1":
				key = prop2.getProperty("l1.key");
				tokenUrl = prop2.getProperty("l1.tokenUrl");
				
			case "L2":
				key = prop2.getProperty("l2.key");
				tokenUrl = prop2.getProperty("l2.tokenUrl");
				
			case "L3":
				key = prop2.getProperty("l3.key");
			tokenUrl = prop2.getProperty("l3.tokenUrl");
		}
		fileInput1.close();
		fileInput2.close();
	}catch(IOException e) {
		e.printStackTrace();
		test.fail("Retrieve end point properties failed");
		ex.writeExcel(fileName, "", "", "", "", "", "", "", "", "", "Fail", "Exception: "+e.toString());
		Assert.fail("Test Failed");
		}	
	}
	
	public static String getEndPointUrl(String getUrlValue, String fileName, String methodName)
	{
		getProperty(methodName, fileName);
		getUrlValue = urlOutParams.get(getUrlValue);
		return getUrlValue;
		
	}
	
	public static String[] getTokenProperties(String fileName)
	{
		getProperty("",fileName);
		String[] tokenValues = new String[2];
		tokenValues[0] = key;
		tokenValues[1] = tokenUrl;
		return tokenValues;
	}

}
