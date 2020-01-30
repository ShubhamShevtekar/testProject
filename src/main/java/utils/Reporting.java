package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Session;

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
	
	public ExtentHtmlReporter htmlReporter,htmlReporter1;
	public ExtentReports extent;
	public static ExtentTest test;
	String fileName = this.getClass().getSimpleName();
	protected static String level;
	protected String[] tokenValues = new String[2];
	protected String token, currentDate;
	
	@BeforeSuite
	public void cleanFolder(){
		
		File folder = new File("./Extend_Reports_Mail");
		deleteFolder(folder);
	}
	
	@BeforeClass
	public void beforeSuite() throws IOException
	{
		level = Miscellaneous.getEnv();
		if (level.isEmpty()){
			new SkipException("exiting test");
		}
		
	}
	
	@BeforeClass 
	public void startReport() throws Exception {
		String currentDateTime = new SimpleDateFormat("dd-MMMM-yyyy_HH.mm.ss").format(Calendar.getInstance().getTime());
		htmlReporter = new ExtentHtmlReporter("./Extend_Reports_Mail/"+"/"+fileName+".html");
		htmlReporter1 = new ExtentHtmlReporter("./Extend_Reports/"+fileName+"/"+fileName+"-"+currentDateTime+".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.attachReporter(htmlReporter1);
		extent.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", Miscellaneous.getEnv());
		htmlReporter.config().setDocumentTitle(fileName);
		htmlReporter.config().setReportName("Report"); 
		htmlReporter1.config().setDocumentTitle(fileName);
		htmlReporter1.config().setReportName("Report"); 
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
	
	@BeforeMethod
	public void getCurrentDate()
	{
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		currentDate = simpleDateFormat.format(new Date());
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

	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	   // folder.delete();
	}
	
	@AfterSuite
	public static void zipFolder() throws Exception{
	
		ZipUtils z = new ZipUtils();
		String zippedFilePath = z.zipFiles();
		
		File file = new File("Config.properties");
		FileInputStream fileInput;
		fileInput = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInput);
		String senderEmailID = prop.getProperty("sender");
		if (senderEmailID.isEmpty()){
			new SkipException("sender not found");
		}
		
		String receiverEmailID = prop.getProperty("receiver");
		if (receiverEmailID.isEmpty()){
			new SkipException("receiver not found");
		}
		
	    String smtpHostServer = "smtp.mail.fedex.com";
	    
	    Properties props = System.getProperties();
	    props.put("mail.smtp.host", smtpHostServer);

	    Session session = Session.getInstance(props, null);
	    z.sendEmail(session, senderEmailID, receiverEmailID, zippedFilePath, level);
	}
	
}
