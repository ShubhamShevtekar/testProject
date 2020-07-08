package utils.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.testng.SkipException;

public class Miscellaneous {
	
	
	public static String getCurrentDateTime() {
		String currentDateTime = new SimpleDateFormat("dd-MMMM-yyyy_HH.mm.ss").format(Calendar.getInstance().getTime());
		return currentDateTime;
	}
	
	public String dateFormat(String getDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(getDate); 
		SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");  
	    String strDate= formatter1.format(date); 
		return strDate;
	}
	
	public static String geoFieldInputNames(String[] inputValues, String[] inputFieldNames)
	{
//		String[] inputFieldNames = {"UserName: ", "GeopoliticalTypeName: "};
		for(int i=0; i<inputFieldNames.length; i++)
		{
			inputValues[i] = inputFieldNames[i]+inputValues[i];
		}
		String combinedFields = arraySB(inputValues);
		return combinedFields;
		
	}
	
	public static String geoDBFieldNames(List<String> DB_Values, String[] DB_FieldNames)
	{
//		String[] DB_FieldNames = {"DB_UserName: ", "DB_GeopoliticalTypeName: "};
		for(int i=0; i<DB_FieldNames.length; i++)
		{
			DB_Values.add(i, DB_FieldNames[i]+DB_Values.get(i));
			DB_Values.remove(i+1);
		}
		String combinedDB_Fields = listSB(DB_Values);
		return combinedDB_Fields;
	}
	
	public static String getEnv() throws IOException
	{
		File file = new File("Config.properties");
		FileInputStream fileInput;
		fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInput);
		String testlevel = prop.getProperty("level");//System.getProperty("level");//prop.getProperty("level");
		if (testlevel.isEmpty()){
			new SkipException("exiting test");
		}
		return testlevel;
	}
	public static String arraySB(String[] getArray)
	{
		String arraySB = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getArray.length; i++) {
		    sb.append(getArray[i] + "\n\n");
		}
		arraySB = sb.toString();
		return arraySB;
	}
	
	public static String listSB(List<String> getList)
	{
		String listSB = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getList.size(); i++) {
		    sb.append(getList.get(i) + "\n\n");
		}
		listSB = sb.toString();
		return listSB;
	}
	
	public static String twoArraySB(String[] getArrayOne, String[] getArrayTwo)
	{
		String twoArraySB = null;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<getArrayOne.length; i++)
		{
			sb.append(getArrayOne[i] + getArrayTwo[i] + "\n\n");
		}
		twoArraySB = sb.toString();
		return twoArraySB;
	}
	
	public static String jsonFormat(String jsonResponse) {
		StringBuilder json = new StringBuilder();
	    String indentString = "";

	    for (int i = 0; i < jsonResponse.length(); i++) {
	        char letter = jsonResponse.charAt(i);
	        switch (letter) {
	            case '{':
	            case '[':
	                json.append("\n" + indentString + letter + "\n");
	                indentString = indentString + "\t";
	                json.append(indentString);
	                break;
	            case '}':
	            case ']':
	                indentString = indentString.replaceFirst("\t", "");
	                json.append("\n" + indentString + letter);
	                break;
	            case ',':
	                json.append(letter + "\n" + indentString);
	                break;

	            default:
	                json.append(letter);
	                break;
	        }
	    }
		return json.toString();
	}

}
