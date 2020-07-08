package utils.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;


public class DbConnect extends Reporting {
	
	public static String testlevel=null;
	public static String dbServer=null;
	public static String jdbcUrl=null;
	public static String dbUser=null;
	public static String dbPassword=null;
	static ExcelUtil ex = new ExcelUtil();
	
	public static void getProperty(String fileName, String testCaseID)
	{
		try {
			testlevel = Miscellaneous.getEnv();//System.getProperty("level");
			
			//get DB details as per environment
			File file1 = new File("src/main/resources/DB_Properties/database.properties");
			FileInputStream fileInput1 = new FileInputStream(file1);
			Properties prop1 = new Properties();
			prop1.load(fileInput1);
			
			switch(testlevel) {
			
			case "L1":
	//			dbServer = prop1.getProperty("l1.db.server");
				jdbcUrl = prop1.getProperty("l1.db.jdbc");
				dbUser = prop1.getProperty("l1.db.user");
				dbPassword = prop1.getProperty("l1.db.password");
				break;
				
			case "L2":
	//			dbServer = prop1.getProperty("l2.db.server");
				jdbcUrl = prop1.getProperty("l2.db.jdbc");
				dbUser = prop1.getProperty("l2.db.user");
				dbPassword = prop1.getProperty("l2.db.password");
				break;
				
			case "L3":
	//			dbServer = prop1.getProperty("l3.db.server");
				jdbcUrl = prop1.getProperty("l3.db.jdbc");
				dbUser = prop1.getProperty("l3.db.user");
				dbPassword = prop1.getProperty("l3.db.password");
				break;		
			}
			fileInput1.close();
		} catch (IOException e) {
			e.printStackTrace();
			test.fail("Retrieve database properties file failed:"+e.toString());
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "","", "", "Fail", "Exception: "+e.toString());
			Assert.fail("Test Failed");
		}
	}
	
	public static List<String> getResultSetFor(String query, List<String> fields, String fileName, String testCaseID)
	{
		getProperty(fileName, testCaseID);
		List<String> queryResult = new ArrayList<>();
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver"); 
		Connection con=DriverManager.getConnection(jdbcUrl,dbUser,dbPassword);
//		if(con != null) {
//            test.pass("DB connection success");
//        }
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet result = stmt.executeQuery(query);
		result.last();
		int rows = result.getRow();
		result.beforeFirst();
		String checkNull = null;
		while(result.next()) {
			int j=0;
			for(int i=0; i<fields.size(); i++)
			{
				checkNull = result.getString(fields.get(i));
				if(StringUtils.isBlank(checkNull)) {
					checkNull="";
				}	
				queryResult.add(checkNull.trim());
			}
        }
		}catch(ClassNotFoundException | SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		return queryResult;
	}
	
	
	public static Connection getSqlStatement(String fileName, String testCaseID)
	{
		getProperty(fileName, testCaseID);
		Connection con=null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			con=DriverManager.getConnection(jdbcUrl,dbUser,dbPassword);
		}catch(ClassNotFoundException | SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		return con;
	}
	
//	public static HashMap<String, LinkedHashMap<String, String>> getResultSetForGet(String query, List<String> fields, String fileName, String testCaseID)
//	{
//		HashMap<String, LinkedHashMap<String, String>> DB_Get = new LinkedHashMap<String, LinkedHashMap<String, String>>();
//		getProperty(fileName, testCaseID);
//		try {
//			Class.forName("oracle.jdbc.driver.OracleDriver"); 
//			Connection con=DriverManager.getConnection(jdbcUrl,dbUser,dbPassword);
//			if(con != null) {
//	            test.pass("DB connection success");
//	        }
//			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//			ResultSet result = stmt.executeQuery(query);
//			result.last();
//			int rows = result.getRow();
//			result.beforeFirst();
//			while(result.next()) {
//				LinkedHashMap<String, String> list = new LinkedHashMap<String, String>();
//				int j=0;
//				String keyValue = null;
//				for(int i=0; i<fields.size(); i++)
//				{
//						String valueDBRow = result.getString(fields.get(i));
//						if(i==0)
//						{
//							keyValue = valueDBRow;
//						}
//						list.put(fields.get(i), valueDBRow);
//				}
//				DB_Get.put(keyValue,  list);
//	        }
//			}catch(ClassNotFoundException | SQLException e) {
//				ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
//				test.fail("DB connection failed");
//				Assert.fail("Test Failed");
//			}
//		return DB_Get;
//	}
	
	public static List<String> getPrimaryKey(String query, List<String> fields, String fileName, String testCaseID)
	{
		List<String> getPrimKey = new ArrayList<>();
		getProperty(fileName, testCaseID);
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			Connection con=DriverManager.getConnection(jdbcUrl,dbUser,dbPassword);
			if(con != null) {
	            test.pass("DB connection success");
	        }
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(query);
			result.last();
			int rows = result.getRow();
			result.beforeFirst();
			int j=0;
			while(result.next()) {				
					getPrimKey.add(result.getString(fields.get(j)));
	        }
			}catch(ClassNotFoundException | SQLException e) {
				ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
				test.fail("DB connection failed");
				Assert.fail("Test Failed");
			}
		return getPrimKey;
	}
	
	
	public static List<String> getResultSetFor1(String query, List<String> fields, String fileName, String testCaseID)
	{
		getProperty(fileName, testCaseID);
		List<String> queryResult = new ArrayList<>();
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver"); 
		Connection con1=DriverManager.getConnection(jdbcUrl,dbUser,dbPassword);
		if(con1 != null) {
            test.pass("DB connection success");
        }
		Statement stmt = con1.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet result = stmt.executeQuery(query);
		result.last();
		int rows = result.getRow();
		result.beforeFirst();
		String checkNull = null;
		while(result.next()) {
			int j=0;
			for(int i=0; i<fields.size(); i++)
			{
				checkNull = result.getString(fields.get(i));
				if(StringUtils.isBlank(checkNull)) {
					checkNull="";
				}	
				queryResult.add(checkNull.trim());
			}
        }
		}catch(ClassNotFoundException | SQLException e) {
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "DB Connection Exception: "+e.toString());
			test.fail("DB connection failed: "+e);
			Assert.fail("Test Failed");
		}
		return queryResult;
	}

}
