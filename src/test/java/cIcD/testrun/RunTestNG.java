package cIcD.testrun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.TestNG;
import org.testng.annotations.Test;

public class RunTestNG {
	

	public static String dir = System.getProperty("user.dir");
	public static String testlevel=null;
	
	@Test
	public void TC_01() {
		TestNG testSuite = new TestNG();
		// Create a list of String 
		List<String> suitefiles = new ArrayList<String>();
		
		try {
			
			getVersion();
			
			switch(testlevel) {
				case"Base":
					System.out.println("Running Regression Base Services");
					// Add xml file which you have to execute
					suitefiles.add(dir+"//src//test//resources//com//xml//base.xml");
					// set xml file for execution
					testSuite.setTestSuites(suitefiles);
					testSuite.run();
					break;
				case"V1":
					System.out.println("Running Attribute Change Services");
					// Add xml file which you have to execute
					suitefiles.add(dir+"//src//test//resources//com//xml//version1.xml");
					// set xml file for execution
					testSuite.setTestSuites(suitefiles);
					testSuite.run();
					break;
				case"V2":
					System.out.println("Running Phone Number Services");
					// Add xml file which you have to execute
					suitefiles.add(dir+"//src//test//resources//com//xml//version2.xml");
					// set xml file for execution
					testSuite.setTestSuites(suitefiles);
					testSuite.run();
					break;
			}	
		} catch(IOException e) {
				e.printStackTrace();
				Assert.fail("File not found");
		}
	}

	public static String getVersion() throws IOException{
		File file = new File(dir+"//Config.properties");
		FileInputStream fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInput);
		testlevel = prop.getProperty("run");
		if (testlevel.isEmpty()) {
			new SkipException("exiting test");
		}
		return testlevel;
	}
	
}
