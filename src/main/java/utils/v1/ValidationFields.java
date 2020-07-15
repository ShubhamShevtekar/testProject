package utils.v1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	public static List<String> holidayGetMethodDbGrpgQLFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("HDAY_ID");
		fields.add("HDAY_DT_PARM_TXT");
		return fields;
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

	//*** Graph QL Geopl Relationship
		public static List<String> geopRltspGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_COMPT_ID");
			fields.add("RELTD_GEOPL_COMPT_ID");
			fields.add("GEOPL_RLTSP_TYPE_CD");
			return fields;
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
	//***depnCntryRltspGrapphQL
			public static List<String> depnCntryRltspGrapphQLMethodDbFields()
			{
				List<String> fields = new ArrayList<>();
				fields.add("GEOPL_ID");
				fields.add("DEPN_RLTSP_DESC");
				return fields;
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
	/*public static List<String> scriptDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("SCRPT_CD");
		fields.add("SCRPT_NM");
		fields.add("SCRPT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");



		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}

	public static List<String> scriptDbAuditFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("SCRIPT_CD");
		fields.add("SCRPT_NM");
		fields.add("SCRPT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
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

	// Scripts Graph QL
	public static List<String> scriptGraphQLAttributesMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("SCRPT_NM");
		fields.add("SCRPT_DESC");
		return fields;
	}*/

	//*** Script
		/*public static List<String> scriptDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("SCRPT_CD");
			fields.add("SCRPT_NM");
			fields.add("SCRPT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			return fields;
		}*/

		public static List<String> scriptDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("SCRPT_CD");
			fields.add("SCRPT_NM");
			fields.add("SCRPT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			return fields;
		}


		/*public static List<String> scriptDbAuditFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("SCRIPT_CD");
			fields.add("SCRPT_NM");
			fields.add("SCRPT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			fields.add("REVISION_TYPE_CD");
			return fields;
		}*/
		 // added for code merge
		public static List<String> scriptDbAuditFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("SCRPT_CD");
			fields.add("SCRPT_NM");
			fields.add("SCRPT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			fields.add("REVISION_TYPE_CD");
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

		// Scripts Graph QL
		public static List<String> scriptGraphQLAttributesMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("SCRPT_NM");
			fields.add("SCRPT_DESC");
			return fields;
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

	public static List<String> cntryOrgStdAuditDbFields()
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
		fields.add("REVISION_TYPE_CD");
		return fields;
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
	//*** GeoRltsp PUT Audit
	public static List<String> geoRltspAuditDbFields()
	{
		List<String> fields = new ArrayList<>();

		fields.add("GEOPL_COMPT_ID");
		fields.add("RELTD_GEOPL_COMPT_ID");
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
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

	public static List<String> geoRltspDbFieldsAudit()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_COMPT_ID");
		fields.add("RELTD_GEOPL_COMPT_ID");
		fields.add("GEOPL_RLTSP_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
		return fields;
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

	public static List<String> auditDBCntFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("count(*)");

		return fields;
	}

	//***StProvStd POST Audit
		public static List<String> stProvStdAuditDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
//			fields.add("GEOPL_ID");
			fields.add("ORG_STD_CD");
			fields.add("ST_PROV_CD");
			fields.add("ST_PROV_NM");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("LAST_UPDATED_BY_USER_ID");
			fields.add("REVISION_TYPE_CD");
			return fields;
		}

		//***StProvStd PUT Audit
				public static List<String> stProvStdPutAuditDbFields()
				{
					List<String> fields = new ArrayList<>();
					fields.add("ORG_STD_CD");
					fields.add("ST_PROV_CD");
					fields.add("ST_PROV_NM");
					fields.add("EFFECTIVE_DT");
					fields.add("EXPIRATION_DT");
					fields.add("LAST_UPDATED_BY_USER_ID");
					fields.add("REVISION_TYPE_CD");
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
			/*fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");*/
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

	public static List<String> langNewDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("LANGUAGE_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}
	public static List<String> langPutNewDbFields()
	{
		List<String> fields = new ArrayList<>();

		fields.add("LANGUAGE_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}

	public static List<String> langPutJMSNewDbFields()
	{
		List<String> fields = new ArrayList<>();

		fields.add("LANGUAGE_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		return fields;
	}


	public static List<String> langNewAuditDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("LANGUAGE_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> langPutNewAuditDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LANGUAGE_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}


	public static List<String> langLocalesNewDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
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

	public static List<String> auditLangLocalesNewDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
		fields.add("SCRIPT_CD");
		fields.add("CLDR_VERS_DT");
		fields.add("CLDR_VERS_NBR");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> langLocalesNewGetDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
		fields.add("SCRIPT_CD");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("CLDR_VERS_NBR");
		fields.add("CLDR_VERS_DT");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}






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

	public static List<String> langTrnslMonthOfYearGraphQLDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("LANGUAGE_CD");
		fields.add("TRNSL_MONTH_OF_YEAR_NM");
		return fields;
	}

	public static List<String> langJMSDbFields()
	{
		List<String> fields = new ArrayList<>();
			fields.add("LANGUAGE_CD");
			fields.add("ENGL_LANGUAGE_NM");
			fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
			fields.add("LANGUAGE_CD");
			fields.add("CLDR_VERS_DT");
			fields.add("CLDR_VERS_NBR");
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("CNTRY_CD");
			fields.add("DT_FULL_FORMT_DESC");
			fields.add("DT_LONG_FORMT_DESC");
			fields.add("DT_MED_FORMT_DESC");
			fields.add("DT_SHT_FORMT_DESC");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("SCRIPT_CD");
			fields.add("ENGL_LANGUAGE_NM");

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

	public static List<String> langDbAuditFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("LANGUAGE_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");

		return fields;
	}
	public static List<String> langTrnslDowDbAuditFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("DAY_OF_WEEK_NBR");
		fields.add("TRNSL_DOW_NM");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}
	public static List<String> langTrnslMonthOfYearDbAuditFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("MONTH_OF_YEAR_NBR");
		fields.add("TRNSL_MONTH_OF_YEAR_NM");
		fields.add("REVISION_TYPE_CD");
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
		fields.add("DEPN_CNTRY_CD");//Changes made here
		fields.add("INTL_DIAL_CD");
		fields.add("LAND_PH_MAX_LTH_NBR");
		fields.add("LAND_PH_MIN_LTH_NBR");
		fields.add("MOBL_PH_MAX_LTH_NBR");
		fields.add("MOBL_PH_MIN_LTH_NBR");
		fields.add("PH_NBR_PATRN_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		return fields;
	}

	public static List<String> cntryAuditDbFields()

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
		fields.add("DEPN_CNTRY_CD");//Changes made here
		fields.add("INTL_DIAL_CD");
		fields.add("LAND_PH_MAX_LTH_NBR");
		fields.add("LAND_PH_MIN_LTH_NBR");
		fields.add("MOBL_PH_MAX_LTH_NBR");
		fields.add("MOBL_PH_MIN_LTH_NBR");
		fields.add("PH_NBR_PATRN_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	/*public static List<String> cntryCountryDialingsDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("INTL_DIAL_PREFX_CD");//Changes made here
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> cntryCountryDialingsAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("INTL_DIAL_PREFX_CD");//Changes made here
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}*/

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

	public static List<String> cntryCurrenciesAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("CURR_NUM_CD");
		fields.add("CURR_CD");
		fields.add("MINOR_UNIT_CD");
		fields.add("MONEY_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
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

	public static List<String> cntryGeopoliticalUOMAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("UOM_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
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

	public static List<String> cntryGeopoliticalHolidaysAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("HDAY_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
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

	public static List<String> cntryGeopoliticalAffiliationsAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("AFFIL_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> cntryLocalesDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("LANGUAGE_CD");
		fields.add("LOCL_CD");
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

	public static List<String> langLocalesDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LANGUAGE_CD");
		fields.add("CLDR_VERS_DT");
		fields.add("CLDR_VERS_NBR");
		fields.add("GEOPL_ID");
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("SCRIPT_CD");
		fields.add("ENGL_LANGUAGE_NM");
		return fields;
	}

	public static List<String> langLocalesGetDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
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

	public static List<String> langLocalesGraphQLDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("CNTRY_CD");
		fields.add("LANGUAGE_CD");
		fields.add("SCRIPT_CD");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("CLDR_VERS_NBR");
		fields.add("CLDR_VERS_DT");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}



	public static List<String> cntryLocalesAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("LANGUAGE_CD");
		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("CLDR_VERS_NBR");
		fields.add("CLDR_VERS_DT");
		fields.add("DT_FULL_FORMT_DESC");
		fields.add("DT_LONG_FORMT_DESC");
		fields.add("DT_MED_FORMT_DESC");
		fields.add("DT_SHT_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> cntryTranslationGeopoliticalsDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("VERS_NBR");
		fields.add("VERS_DT");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");

		return fields;
	}

	public static List<String> cntryTranslationGeopoliticalsAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("VERS_NBR");
		fields.add("VERS_DT");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> cntryGeopoliticalTypeDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("GEOPL_TYPE_NM");

		return fields;
	}

	public static List<String> cntryGeopoliticalTypeAuditDbFields()

	{
		List<String> fields = new ArrayList<>();

		fields.add("GEOPL_TYPE_NM");
		fields.add("REVISION_TYPE_CD");
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

	// JMS Validation Fields
		public static List<String> depnCntryRltspGetMethodDbFieldsJms()
		{
			List<String> fields = new ArrayList<>();
			fields.add("DEPN_RLTSP_ID");
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
		fields.add("SCRPT_CD");
		fields.add("SCRPT_NM");
		fields.add("SCRPT_DESC");
		/*fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");*/
		return fields;
	}

	public static List<String> scriptJMSMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("SCRPT_CD");
		fields.add("SCRPT_NM");
		fields.add("SCRPT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
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
	//***StProvStd for JMS
			public static List<String> stProvStdGetMethodDbFieldsJMS()
			{
				List<String> fields = new ArrayList<>();
				fields.add("GEOPL_ID");
				fields.add("ORG_STD_CD");
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

	public static List<String> langGraphQLtMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LANGUAGE_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");

		return fields;
	}
	public static List<String> langGetMethodDbNewFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LANGUAGE_CD");
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("NATIVE_SCRIPT_LANGUAGE_NM");
		fields.add("NATV_SCRIPT_CD");
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
		fields.add("INTL_DIAL_CD");
		fields.add("LAND_PH_MAX_LTH_NBR");
		fields.add("LAND_PH_MIN_LTH_NBR");
		fields.add("MOBL_PH_MAX_LTH_NBR");
		fields.add("MOBL_PH_MIN_LTH_NBR");
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

	public static List<String> countryAffilTypeJMSDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("AFFIL_TYPE_CD");
		fields.add("AFFIL_TYPE_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> countryTrnslGeoplGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("VERS_DT");
		fields.add("VERS_NBR");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> countryTrnslGeoplJMSDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("LOCL_CD");
		fields.add("VERS_DT");
		fields.add("VERS_NBR");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> countryOrgStdGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ORG_STD_CD");
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
	//	fields.add("ENGL_LANGUAGE_NM");
		fields.add("LOCL_CD");
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

	public static List<String> countryLocaleJMSDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("ENGL_LANGUAGE_NM");
		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("LANGUAGE_CD");
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

	public static List<String> countryOrgStdGetMethodDbFieldsJMS()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("ORG_STD_CD");
		fields.add("CNTRY_CD");
		fields.add("CNTRY_FULL_NM");
		fields.add("CNTRY_SHT_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> countryGraphQLTypeNameGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("GEOPL_TYPE_ID");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("GEOPL_TYPE_ID");
		fields.add("GEOPL_TYPE_NM");
		return fields;
	}

	public static List<String> countryLocaleGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("LANGUAGE_CD");
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

	public static List<String> countryTrnslGeoplGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("LOCL_CD");
		fields.add("SCRIPT_CD");
		fields.add("TRNSL_NM");
		fields.add("VERS_DT");
		fields.add("VERS_NBR");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> countryAffilTypeGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();

		fields.add("GEOPL_ID");
		fields.add("AFFIL_TYPE_ID");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("AFFIL_TYPE_ID");
		fields.add("AFFIL_TYPE_CD");
		fields.add("AFFIL_TYPE_NM");
		return fields;
	}

	public static List<String> countryUomTypeGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("UOM_TYPE_CD");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("UOM_TYPE_CD");
		fields.add("UOM_TYPE_NM");
		fields.add("UOM_TYPE_DESC");
		return fields;
	}

	public static List<String> countryHolidayGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("HDAY_ID");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("HDAY_ID");
		fields.add("HDAY_NM");
		fields.add("HDAY_DT_PARM_TXT");
		return fields;
	}

	public static List<String> countryOrgStdGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		//fields.add("GEOPL_ID");
		//fields.add("ORG_STD_CD");
		fields.add("CNTRY_FULL_NM");
		fields.add("CNTRY_SHT_NM");
		fields.add("ORG_STD_CD");
		fields.add("ORG_STD_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}
	public static List<String> countryDialGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
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

	public static List<String> countryStateProvStdGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("GEOPL_ID");
		fields.add("ORG_STD_CD");
		fields.add("ST_PROV_CD");
		fields.add("ST_PROV_NM");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}


	public static List<String> countryDepnCntryRltspGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		fields.add("DEPN_RLTSP_ID");
		fields.add("DEPN_RLTSP_DESC");
		return fields;
	}

	public static List<String> countryCurrencyGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		//fields.add("GEOPL_ID");
		fields.add("CURR_NUM_CD");
		fields.add("CURR_CD");
		fields.add("MINOR_UNIT_CD");
		fields.add("MONEY_FORMT_DESC");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}


	public static List<String> AddressLabelGetMethodDbFields()
	{
		List<String> fields = new ArrayList<>();

		return fields;
	}


	public static List<String> addressLabelsGraphQLMethodDbFields()
	{
		List<String> fields = new ArrayList<>();
		//fields.add("GEOPL_ID");
		fields.add("LOCL_CD");
		fields.add("ADDR_LINE_NBR");
		fields.add("BRAND_ADDR_LINE_LABEL_DESC");
		fields.add("APPL_FLG");
		return fields;
	}

	public static List<String> addressLabelResponseFileds(Response res)
	{
		String responsestr=res.asString();
		JsonPath js = new JsonPath(responsestr);
		List<String> fieldsPath = new ArrayList<>();
		fieldsPath.add(js.getString("data.geopoliticalId"));
		fieldsPath.add(js.getString("meta.message.internalMessage"));
		return fieldsPath;
	}

	public static List<String> addressLabelsGetLocaleCodePUTMethodDbFields() {
		List<String> fields = new ArrayList<>();
		fields.add("LOCL_CD");
		return fields;
	}
	public static List<String> addressLabelsPUTNewMethodDbFields() {
		List<String> fields = new ArrayList<>();
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("GEOPL_ID");
		fields.add("LANGUAGE_CD");
		fields.add("ADDR_LINE_NBR");
		fields.add("FULL_ADDR_LINE_LABEL_DESC");
		fields.add("BRAND_ADDR_LINE_LABEL_DESC");
		fields.add("APPL_FLG");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> addressLabelsAuditPutNewMethodDbFields() {
		List<String> fields = new ArrayList<>();
		fields.add("LAST_UPDATED_BY_USER_ID");
		fields.add("GEOPL_ID");
		fields.add("LANGUAGE_CD");
		fields.add("ADDR_LINE_NBR");
		fields.add("FULL_ADDR_LINE_LABEL_DESC");
		fields.add("BRAND_ADDR_LINE_LABEL_DESC");
		fields.add("APPL_FLG");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	public static List<String> addressLabelsPOSTNewMethodDbFields() {
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_ID");
		fields.add("LANGUAGE_CD");
		fields.add("ADDR_LINE_NBR");
		fields.add("FULL_ADDR_LINE_LABEL_DESC");
		fields.add("BRAND_ADDR_LINE_LABEL_DESC");
		fields.add("APPL_FLG");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		return fields;
	}

	public static List<String> addressLabelsAuditPOSTNewMethodDbFields() {
		List<String> fields = new ArrayList<>();
		fields.add("CREATED_BY_USER_ID");
		fields.add("GEOPL_ID");
		fields.add("LANGUAGE_CD");
		fields.add("ADDR_LINE_NBR");
		fields.add("FULL_ADDR_LINE_LABEL_DESC");
		fields.add("BRAND_ADDR_LINE_LABEL_DESC");
		fields.add("APPL_FLG");
		fields.add("EFFECTIVE_DT");
		fields.add("EXPIRATION_DT");
		fields.add("REVISION_TYPE_CD");
		return fields;
	}

	//***AddressLabels POST
		public static List<String> addressLabelsPOSTMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("FULL_ADDR_LINE_LABEL_DESC");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			return fields;
		}

		public static List<String> addressLabelsPOSTCntrycodeGetMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CNTRY_CD");
			return fields;
		}

		//***AddressLabels PUT
			public static List<String> addressLabelsPUTMethodDbFields()
			{
				List<String> fields = new ArrayList<>();
				fields.add("LAST_UPDATED_BY_USER_ID");
				fields.add("GEOPL_ID");
				fields.add("LOCL_CD");
				fields.add("ADDR_LINE_NBR");
				fields.add("FULL_ADDR_LINE_LABEL_DESC");
				fields.add("BRAND_ADDR_LINE_LABEL_DESC");
				fields.add("APPL_FLG");
				fields.add("EFFECTIVE_DT");
				fields.add("EXPIRATION_DT");
				return fields;
			}


		public static List<String> addressLabelsJMSMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("FULL_ADDR_LINE_LABEL_DESC");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			return fields;
		}

		public static List<String> addressLabelsAuditPOSTMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("CREATED_BY_USER_ID");
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("FULL_ADDR_LINE_LABEL_DESC");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("REVISION_TYPE_CD");
			return fields;
		}

		public static List<String> addressLabelsAuditPutMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("LAST_UPDATED_BY_USER_ID");
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("FULL_ADDR_LINE_LABEL_DESC");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			fields.add("EFFECTIVE_DT");
			fields.add("EXPIRATION_DT");
			fields.add("REVISION_TYPE_CD");
			return fields;
		}

		/**** Address Label GET ** */
		public static List<String> addressLabelGetMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("ADDR_LINE_NBR");
			fields.add("APPL_FLG");
			return fields;
		}
		/* CountryAddressLabel GEt Field*/

		public static List<String> CountryAddressLabelGetMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("ADDR_LINE_NBR");
			fields.add("APPL_FLG");
			return fields;
		}

		public static List<String> addressLabelsWithoutGeoplIdGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			return fields;
		}

		public static List<String> addressLabelsWithoutLocaleCdGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("ADDR_LINE_NBR");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			return fields;
		}

		public static List<String> addressLabelsWithoutAddressLineNumberGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			fields.add("APPL_FLG");
			return fields;
		}

		public static List<String> addressLabelsWithoutBrandAddressGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("APPL_FLG");
			return fields;
		}

		public static List<String> addressLabelsWithoutApplicalbeFlaggGraphQLMethodDbFields()
		{
			List<String> fields = new ArrayList<>();
			fields.add("GEOPL_ID");
			fields.add("LOCL_CD");
			fields.add("ADDR_LINE_NBR");
			fields.add("BRAND_ADDR_LINE_LABEL_DESC");
			return fields;
		}

		/**
		 * Following method is for timestamp and status code validation*/
		public static void timestampValidation(JsonPath js, Response res){
			Date date = new Date();
			String todaysDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
			String timestamp = js.getString("meta.timeStamp");
			String statusCode = js.getString("meta.statusCode");
			Integer Wscode = res.statusCode();
			timestamp = timestamp.substring(0, 10);
			if(timestamp.equals(todaysDate)){
				utils.v1.Reporting.test.pass("Meta timestamp validation passed");
			}else{
				utils.v1.Reporting.test.fail("Meta timestamp validation Failed");
			}
			if(statusCode.equals(Wscode.toString())){
				utils.v1.Reporting.test.pass("Meta statusCode validation passed");
			}else{
				utils.v1.Reporting.test.fail("Meta statusCode validation Failed");
			}
		}


	/** Following method is for transactionId Validation **/
	public static void transactionIdValidation(JsonPath js, Response res) {
		String meta = js.getString("meta");
		boolean result = false, result1 = false, result2 = false, result3 = false, result4 = false, result5 = false;
		if (meta.contains("transactionId")) {
			result = true;
			String Res_transactionId = js.getString("meta.transactionId");
			if (Res_transactionId != null) {
				// utils.v1.Reporting.test.pass("Meta transactionId validation
				// Passed as transactionId is Not Null");
				int countHypen = 0;
				result = true;
				for (char ch : Res_transactionId.toCharArray()) {
					if (ch == '-')
						countHypen++;
				}

				if (countHypen == 4 && (Res_transactionId.length() == (36))) {
					result = true;
					String[] words = Res_transactionId.split("-");
					if (words[0].length() == 8)
						result1 = true;
					else
						result1 = false;
					if (words[1].length() == 4)
						result2 = true;
					else
						result2 = false;
					if (words[2].length() == 4)
						result3 = true;
					else
						result3 = false;
					if (words[3].length() == 4)
						result4 = true;
					else
						result4 = false;
					if (words[4].length() == 12)
						result5 = true;
					else
						result5 = false;
				} else
					result = false;
			} else {
				utils.v1.Reporting.test.fail("Meta transactionId validation Failed as transactionId is Null");
				result = false;
			}
		} else {
			utils.v1.Reporting.test.fail("transactionId attribute not present in meta section");
			result = false;
		}

		if (result && result1 && result2 && result3 && result4 && result5)
			utils.v1.Reporting.test.pass("Meta transactionId validation is Passed");
		else
			utils.v1.Reporting.test.fail("Meta transactionId validation is Failed ");

	}


}
