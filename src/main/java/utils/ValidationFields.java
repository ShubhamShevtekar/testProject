package utils;

import java.util.ArrayList;
import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ValidationFields {
	
	//***geopolitical type
	public static List<String> geoTypeDBFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_TYPE_NM");
		fields.add("GEOPL_TYPE_ID");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> geoTypeResponseFields(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("geoplTypeId"));
		fieldsPath.add(js.getString("message"));
		return fieldsPath;
	}
	
	//***geopolitical relationship type
	public static List<String> geoRsTypeDBFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("AREA_RLTSP_TYPE_DESC");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}	
	public static List<String> geoRsTypeResponseFields(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.geopoliticalRelationshipTypeCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***geopolitical STD Org
	public static List<String> geoOrgStdDBFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("ORG_STD_CD");
		fields.add("ORG_STD_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> geoOrgStdResponseFields(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.orgStdCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***Depn Cntry Rlts
	public static List<String> depnCntryRltspDBFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("DEPN_RLTSP_ID");
		fields.add("DEPN_RLTSP_DESC");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> depnCntryRltspResponseFields(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.dependentRelationshipId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***Holiday
	public static List<String> holidayDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("HDAY_ID");
		fields.add("HDAY_NM");
		fields.add("HDAY_DT_PARM_TXT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> holidayResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.holidayId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***UOM Type
	public static List<String> uomTypeDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("UOM_TYPE_CD");
		fields.add("UOM_TYPE_NM");
		fields.add("UOM_TYPE_DESC");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> uomTypeResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.holidayId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//*** Script
	public static List<String> scriptDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("SCRIPT_CD");
		fields.add("SCRIPT_NM");
		fields.add("SCRIPT_DESC");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> scriptResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.scrptCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//*** DayOfWeek
	public static List<String> dayOfWeekDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("DAY_OF_WEEK_NBR");
		fields.add("DAY_OF_WEEK_SHORT_NM");
		fields.add("DAY_OF_WEEK_FULL_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> dayOfWeekResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.scrptCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//*** MonthOfYear
	public static List<String> monthOfYearDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("MONTH_OF_YEAR_SHT_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> monthOfYearResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.scrptCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//*** CntryOrgStd
	public static List<String> cntryOrgStdDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_ID");
		fields.add("CNTRY_SHT_NM");
		fields.add("CNTRY_FULL_NM");
		fields.add("ORG_STD_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> cntryOrgStdResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.geoplId"));
		fieldsPath.add(js.getString("data.orgStdCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//*** GeoRltsp
	public static List<String> geoRltspDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_COMPT_ID");
		fields.add("RELTD_GEOPL_COMPT_ID");
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> geoRltspResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.geoplRltspCmptId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}

	
	
	//***StProvStd
	public static List<String> stProvStdDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
//		fields.add("GEOPL_ID");
		fields.add("ORG_STD_CD");
		fields.add("ST_PROV_CD");
		fields.add("ST_PROV_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> stProvStdResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.stProvCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***AffilType
	public static List<String> affilTypeDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("AFFIL_TYPE_ID");
		fields.add("AFFIL_TYPE_CD");
		fields.add("AFFIL_TYPE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> affilTypeResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.affilTypeId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
	//***Language
	public static List<String> langDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("LANGUAGE_CD");
		fields.add("THREE_CHAR_LANGUAGE_CD");
		fields.add("LANGUAGE_NM");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("SCRIPT_CD");
		fields.add("LANGUAGE_DESC");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> langTrnslDowDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("DAY_OF_WEEK_NBR");
		fields.add("LANGUAGE_CD");
		fields.add("TRNSL_DOW_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> langTrnslMonthOfYearDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("LANGUAGE_CD");
		fields.add("TRNSL_MONTH_OF_YEAR_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> langResponseFileds(Response res)
	{
		String responsestr=res.asString(); 
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.langCd"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}
	
}
