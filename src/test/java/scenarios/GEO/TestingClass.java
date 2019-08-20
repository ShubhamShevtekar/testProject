package scenarios.GEO;

import utils.ResponseMessages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestingClass {
	ResponseMessages resMsg = new ResponseMessages();
	@Test
	public void TC_01() throws ParseException
	{
		List<String> getResponseRows = new ArrayList<>();
		List<String> getResponseRows1 = new ArrayList<>();
		getResponseRows.add("tamil");
		getResponseRows.add("123456");
		getResponseRows.add("12tamil");
		getResponseRows.add("eya");
		getResponseRows.add("123eya");
		getResponseRows.add("eya45656");
		int z=0;
		for(int i=0; i<=5; i=i+3)
		{
			getResponseRows1.add(getResponseRows.get(i)+getResponseRows.get(i+1)+getResponseRows.get(i+2));
		}
		System.out.println(getResponseRows1.get(0));
		System.out.println(getResponseRows1.get(1));
	}
	
	
	
	}


