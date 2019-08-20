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
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> langTrnslDowDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("DAY_OF_WEEK_NBR");
		fields.add("TRNSL_DOW_NM");
		return fields;
	}
	public static List<String> langTrnslMonthOfYearDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("TRNSL_MONTH_OF_YEAR_NM");
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
	
	//***Country
		public static List<String> cntryDbFields()

		{
			List<String> fields = new ArrayList<>();
		
			fields.add("CREATED_BY_USER_ID");
			fields.add("CNTRY_NUM_CD");
			fields.add("CNTRY_CD");
			fields.add("THREE_CHAR_CNTRY_CD");
			fields.add("INDPT_FLG");
			fields.add("PSTL_FORMT_DESC");
			fields.add("PSTL_FLG");
			fields.add("PSTL_LTH_NBR");
			fields.add("FIRST_WORK_WK_DAY_NM");
			fields.add("LAST_WORK_WK_DAY_NM");
			fields.add("WKEND_FIRST_DAY_NM");
			fields.add("INET_DOMN_NM");
			fields.add("DEPN_RLTSP_ID");
			fields.add("DEPN_CNTRY_CD");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			return fields;
		}
		
		public static List<String> cntryCountryDialingsDbFields()

		{
			List<String> fields = new ArrayList<>();
		
			fields.add("INTL_DIAL_PREFX_CD");
			fields.add("INTL_DIAL_CD");
			fields.add("LAND_PH_MAX_LTH_NBR");
			fields.add("LAND_PH_MIN_LTH_NBR");
			fields.add("MOBL_PH_MAX_LTH_NBR");
			fields.add("MOBL_PH_MIN_LTH_NBR");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		
		public static List<String> cntryCurrenciesDbFields()
		
		{
			List<String> fields = new ArrayList<>();
		
			fields.add("CURR_NUM_CD");
			fields.add("CURR_CD");
			fields.add("MINOR_UNIT_CD");
			fields.add("MONEY_FORMT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			
			
			return fields;
		}
		
		public static List<String> cntryGeopoliticalUOMDbFields()
		
		{
			List<String> fields = new ArrayList<>();
			
			fields.add("UOM_TYPE_CD");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		
		public static List<String> cntryGeopoliticalHolidaysDbFields()
		
		{
			List<String> fields = new ArrayList<>();
			
			fields.add("HDAY_NM");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		
		public static List<String> cntryGeopoliticalAffiliationsDbFields()
		
		{ 	
			List<String> fields = new ArrayList<>();
			
			fields.add("AFFIL_TYPE_CD");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		
		public static List<String> cntryLocalesDbFields()
		
		{ 	
			List<String> fields = new ArrayList<>();
			
			fields.add("LANGUAGE_CD");
			fields.add("LOCLE_CD");
			fields.add("SCRIPT_CD");
			fields.add("CLDR_VERS_NBR");
			fields.add("CLDR_VERS_DT");
			fields.add("DT_FULL_FORMT_DESC");
			fields.add("DT_LONG_FORMT_DESC");
			fields.add("DT_MED_FORMT_DESC");
			fields.add("DT_SHT_FORMT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		
		public static List<String> cntryTranslationGeopoliticalsDbFields()
		
		{ 	
			List<String> fields = new ArrayList<>();

			fields.add("LANGUAGE_CD");
			fields.add("SCRIPT_CD");
			fields.add("TRNSL_NM");
			fields.add("VERS_NBR");
			fields.add("VERS_DT");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			
			return fields;
		}
		 
		public static List<String> cntryGeopoliticalTypeDbFields()
		
		{ 	
			List<String> fields = new ArrayList<>();
		
			fields.add("GEOPL_TYPE_NM");
			
			return fields;
		}
	
	//*** Get Method DB Fields
	
	//***Geo Type
	public static List<String> geoTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_TYPE_ID");
		fields.add("GEOPL_TYPE_NM");
		return fields;
	}
	
	//***Geo Relatioship Type
	public static List<String> geoRsTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("AREA_RLTSP_TYPE_DESC");
		return fields;
	}
	
	//***GeoOrgStd
	public static List<String> geoOrgStdGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ORG_STD_CD");
		fields.add("ORG_STD_NM");
		return fields;
	}
	
	//***DepnCntryRltsp
	public static List<String> depnCntryRltspGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("DEPN_RLTSP_DESC");
		return fields;
	}
	
	//***DepnCntryRltspType
	public static List<String> depnCntryRltspTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("DEPN_RLTSP_ID");
		fields.add("DEPN_RLTSP_DESC");
		return fields;
	}
	
	//***Holiday
	public static List<String> holidayGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("HDAY_ID");
		fields.add("HDAY_NM");
		fields.add("HDAY_DT_PARM_TXT");
		return fields;
	}
	
	//***UomType
	public static List<String> uomTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("UOM_TYPE_CD");
		fields.add("UOM_TYPE_NM");
		fields.add("UOM_TYPE_DESC");
		return fields;
	}
	
	//***Script
	public static List<String> scriptGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("SCRIPT_CD");
		fields.add("SCRIPT_NM");
		fields.add("SCRIPT_DESC");
		return fields;
	}
	
	//***DayOfWeek
	public static List<String> dayOfWeekGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("DAY_OF_WEEK_NBR");
		fields.add("DAY_OF_WEEK_FULL_NM");
		fields.add("DAY_OF_WEEK_SHORT_NM");
		return fields;
	}
	
	//***MonthOfYear
	public static List<String> monthOfYearGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("MONTH_OF_YEAR_SHT_NM");
		return fields;
	}
	
	//***Geopl Relationship
	public static List<String> geopRltspGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_COMPT_ID");
		fields.add("RELTD_GEOPL_COMPT_ID");
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	//***StProvStd
	public static List<String> stProvStdGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("ORG_STD_NM");
		fields.add("ST_PROV_CD");
		fields.add("ST_PROV_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	//***Language
	public static List<String> langGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LANGUAGE_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		return fields;
	}
	
	//*** AffilType
	public static List<String> affilTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("AFFIL_TYPE_ID");
		fields.add("AFFIL_TYPE_CD");
		fields.add("AFFIL_TYPE_NM");
		return fields;
	}
	
	//***Country
	public static List<String> countryGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("CNTRY_NUM_CD");
		fields.add("CNTRY_CD");
		fields.add("THREE_CHAR_CNTRY_CD");
		fields.add("INDPT_FLG");
		fields.add("DEPN_RLTSP_ID");
		fields.add("DEPN_CNTRY_CD");
		fields.add("PSTL_FORMT_DESC");
		fields.add("PSTL_FLG");
		fields.add("PSTL_LTH_NBR");
		fields.add("FIRST_WORK_WK_DAY_NM");
		fields.add("LAST_WORK_WK_DAY_NM");
		fields.add("WKEND_FIRST_DAY_NM");
		fields.add("INET_DOMN_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryGeoTypeNameGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_TYPE_NM");
		return fields;
	}
	
	public static List<String> countryUomTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("UOM_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryHolidayGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("HDAY_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryAffilTypeGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("AFFIL_TYPE_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryTrnslGeoplGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("VERS_DT");
		fields.add("VERS_NBR");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	public static List<String> countryOrgStdGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ORG_STD_NM");
		fields.add("CNTRY_FULL_NM");
		fields.add("CNTRY_SHT_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryLocaleGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LOCLE_CD");
		fields.add("SCRIPT_CD");
		fields.add("CLDR_VERS_DT");
		fields.add("CLDR_VERS_NBR");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryDialGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("INTL_DIAL_PREFX_CD");
		fields.add("INTL_DIAL_CD");
		fields.add("LAND_PH_MAX_LTH_NBR");
		fields.add("LAND_PH_MIN_LTH_NBR");
		fields.add("MOBL_PH_MAX_LTH_NBR");
		fields.add("MOBL_PH_MIN_LTH_NBR");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	
	public static List<String> countryCurrencyGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CURR_NUM_CD");
		fields.add("CURR_CD");
		fields.add("MINOR_UNIT_CD");
		fields.add("MONEY_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
}
