package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

@Listeners
public class Reporting {
	
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public static ExtentTest test;
	String fileName = this.getClass().getSimpleName();
	protected String level;
	protected String[] tokenValues = new String[2];
	protected String token;
	
	@BeforeSuite
	public void beforeSuite() throws IOException
	{
		level = Miscellaneous.getEnv();//System.getProperty("level");
		System.out.println("level from PROP :: " + level);
		if (level.isEmpty()){
			System.out.println("Please pass the level you need to execute the TCs");
			new SkipException("exiting test");
		}
	}
	
	@BeforeClass 
	public void startReport() throws Exception {
		String currentDateTime = new SimpleDateFormat("dd-MMMM-yyyy_HH.mm.ss").format(Calendar.getInstance().getTime());
		htmlReporter = new ExtentHtmlReporter("./Extend_Reports/"+fileName+"/"+fileName+"-"+currentDateTime+".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", Miscellaneous.getEnv());
		htmlReporter.config().setDocumentTitle(fileName);
		htmlReporter.config().setReportName("Report"); 
		return;
	}
	@BeforeClass 
	public void getToken() {
		//***get token properties
		tokenValues = RetrieveEndPoints.getTokenProperties(fileName);
		//***get token
		URL url;
		try {
			url = new URL(tokenValues[1]);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			token = IOUtils.toString(in, encoding);
		} catch (IOException e) {
			e.printStackTrace();
			test.fail("Unable to get the token, exception thrown: "+e.toString());
		}
	}
	
	@AfterClass
	public void endReport()
	{
		extent.flush();
	}
	

	protected String getExecutionFlag(String testCaseID, String scenarioName) throws IOException {
		String test1 = "No";
		File inputWorkbook = new File("WS_TestData.xlsx");
	      FileInputStream fis = new FileInputStream(inputWorkbook);
	      XSSFWorkbook w = new XSSFWorkbook(fis);
	      XSSFSheet sheet = w.getSheet(scenarioName);
	      Iterator rowIterator = sheet.iterator();
	      if(rowIterator.hasNext())
	    	  rowIterator.next();
	      while (rowIterator.hasNext()) {
	    	  XSSFRow row = (XSSFRow) rowIterator.next();
	    	  if(row.getCell(0).toString().equalsIgnoreCase(testCaseID))
	    	  {
	    		  test1 = row.getCell(1).toString();
	    	  }
	      }
	      fis.close();
		return test1;
	}

}
