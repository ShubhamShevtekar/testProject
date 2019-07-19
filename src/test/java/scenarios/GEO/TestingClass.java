package scenarios.GEO;

import utils.ResponseMessages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

public class TestingClass {
	ResponseMessages resMsg = new ResponseMessages();
	@Test
	public void TC_01() throws ParseException
	{
		String geoPostQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+ "CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_COMPT_ID='3791809569618985503'";
		
		System.out.println(geoPostQuery);
	}

}
