package cIcD.testrun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class DefineClasses {
	
	@Test
	public void createTestNg() throws IOException
	{
		String[] totalClasses = readLines("cIcD_properties");
		for(int i=0; i<totalClasses.length; i++)
		{
			System.out.println(totalClasses[i]);
		}
		
		XmlSuite suite = new XmlSuite();
		suite.setName("WebServices");
		 
		XmlTest test = new XmlTest(suite);
		test.setName("Test");
		List<XmlClass> classes = new ArrayList<XmlClass>();
		for(int i=0; i<totalClasses.length; i++)
		{
			classes.add(new XmlClass(totalClasses[i]));
		}
		test.setXmlClasses(classes) ;
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.run();


	}
	
	public String[] readLines(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
         
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
         
        while ((line = bufferedReader.readLine()) != null) 
        {
            lines.add(line);
        }
         
        bufferedReader.close();
         
        return lines.toArray(new String[lines.size()]);
    }  

}
