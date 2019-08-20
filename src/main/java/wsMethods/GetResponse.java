package wsMethods;

import static io.restassured.RestAssured.given;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import utils.ExcelUtil;
import utils.Reporting;

public class GetResponse extends Reporting{
	
	public static Response response;
	public static ResponseBody body;
	public static JsonPath jsonPathEvaluator;
	static ExcelUtil ex = new ExcelUtil();
	public static Response sendRequestPost(String payload, String key, String token, String url, String fileName, String testCaseID)
	{
		Response res = null;
		try {
			res = given()
	            .header(key,token).and().header("Content-Type","application/json")
	            .when()
	            .body(payload)
	            .post(url)
	            .then()
	            .extract().response();
		}catch(Exception e){
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "Send Request Exception: "+e.toString());
			test.fail("Exception thrown when send request: "+e.toString());
			Assert.fail("Test Failed");
		}
		return res;		
	}
	
	public static Response sendRequestPut(String payload, String key, String token, String url, String fileName, String testCaseID)
	{
		Response res = null;
		try {
			res = given()
	            .header(key,token).and().header("Content-Type","application/json")
	            .when()
	            .body(payload)
	            .put(url)
	            .then()
	            .extract().response();
		}catch(Exception e){
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "Send Request Exception: "+e.toString());
			test.fail("Exception thrown when send request: "+e.toString());
			Assert.fail("Test Failed");
		}
		return res;		
	}
	
	public static Response sendRequestGet(String key, String token, String url, String fileName, String testCaseID)
	{
		Response res = null;
		try {
			res = given()
	            .header(key,token).and().header("Content-Type","application/json")
	            .when()
	            .get(url)
	            .then()
	            .extract().response();
		}catch(Exception e){
			e.printStackTrace();
			ex.writeExcel(fileName, testCaseID, "", "", "", "", "", "", "", "", "Fail", "Send Request Exception: "+e.toString());
			test.fail("Exception thrown when send request: "+e.toString());
			Assert.fail("Test Failed");
		}
		return res;
	}
}
