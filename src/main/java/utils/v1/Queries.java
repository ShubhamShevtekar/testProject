package utils.v1;

public class Queries {

	public String geoTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_type where GEOPL_TYPE_NM='" + whereField + "'";
		return geoPostQuery;
	}

	public String geoTypePutQuery(String whereField) {
		String geoPutQuery = "select * from geopl_type where GEOPL_TYPE_ID='" + whereField + "'";
		return geoPutQuery;
	}

	public String geoTypeGetQuery() {
		String geoGettQuery = "select * from geopl_type";
		return geoGettQuery;
	}

	public String geoRsTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_rltsp_type where geopl_rltsp_type_cd='" + whereField + "'";
		return geoPostQuery;
	}

	public String geoRsTypeGetQuery() {
		String geoGetQuery = "select * from geopl_rltsp_type";
		return geoGetQuery;
	}


	public String geoOrgStdPostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_org_std where ORG_STD_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String holidayGraphQlQuery(String whereField) {
		String geoPostQuery = "Select hday_id,hday_dt_parm_txt from holiday Where HDAY_NM ='" + whereField + "'";
		return geoPostQuery;
	}

	public String geoOrgStdGetQuery() {
		String geoGettQuery = "select * from geopl_org_std";
		return geoGettQuery;
	}

	public String depnCntryRltspPostQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where DEPN_RLTSP_DESC='" + whereField + "'";
		return geoPostQuery;
	}

	public String depnCntryRltspGraphQl(String whereField) {
		String geoGetQuery = "select country.geopl_id, depn_cntry_rltsp.depn_rltsp_desc from country INNER JOIN depn_cntry_rltsp on depn_cntry_rltsp.depn_rltsp_id = country.DEPN_RLTSP_ID where country.GEOPL_ID='"+whereField+"'" ;
		return geoGetQuery;
	}

	// Below SQL run for JMS validations
	public String depnCntryRltspPostQueryJms(String whereField) {
		String geoPostQuery = "select DEPN_RLTSP_ID, DEPN_RLTSP_DESC from depn_cntry_rltsp where DEPN_RLTSP_DESC='" + whereField + "'";
		return geoPostQuery;
	}

	public String depnCntryRltspPutQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where DEPN_RLTSP_ID='" + whereField + "'";
		return geoPostQuery;
	}

	public String depnCntryRltspGetQuery(String whereField) {
		String geoGetQuery = "select country.geopl_id, depn_cntry_rltsp.depn_rltsp_desc from country INNER JOIN depn_cntry_rltsp on depn_cntry_rltsp.depn_rltsp_id = country.DEPN_RLTSP_ID and country.DEPN_CNTRY_CD ='" + whereField + "'";
		return geoGetQuery;
	}

	public String depnCntryRltspTypeGetQuery() {
		String geoGetQuery = "select * from depn_cntry_rltsp";
		return geoGetQuery;
	}

	public String holidayPostQuery(String whereField) {
		String geoPostQuery = "select * from holiday where HDAY_NM='" + whereField + "'";
		return geoPostQuery;
	}

	public String holidayPutQuery(String whereField) {
		String geoPostQuery = "select * from holiday where HDAY_ID='" + whereField + "'";
		return geoPostQuery;
	}

	public String holidayGetQuery() {
		String geoGetQuery = "select * from holiday";
		return geoGetQuery;
	}

	public String uomTypePostQuery(String whereField) {
		String geoPostQuery = "select * from ref_uom_type where UOM_TYPE_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String uomTypeGetQuery() {
		String geoGetQuery = "select * from ref_uom_type";
		return geoGetQuery;
	}

	/*public String scriptPostQuery(String whereField) {
		String geoPostQuery = "select * from script where SCRPT_CD='" + whereField + "'";
		return geoPostQuery;
	}

	//Audit Table
	public String scriptPostAuditQuery(String whereField) {
		String geoPostQuery = "select * from script_au where SCRPT_CD='" + whereField + "'";
		return geoPostQuery;
	}
	*/
	public String scriptGetQuery() {
		String geoPostQuery = "select * from script";
		return geoPostQuery;
	}

	// adding updated queries
	public String scriptPostQuery(String whereField) {
		String geoPostQuery = "select CREATED_BY_USER_ID, SCRPT_CD, SCRPT_NM, SCRPT_DESC, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
				+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", LAST_UPDATED_BY_USER_ID from script where SCRPT_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String scriptPostQueryJMS(String whereField) {
		String geoPostQuery = "select SCRPT_CD, SCRPT_NM, SCRPT_DESC, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\","
				+ " to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from script where SCRPT_CD='" +
				whereField + "'";
		return geoPostQuery;
	}

	//Audit Table
	public String scriptPostAuditQuery(String whereField) {
		String geoPostQuery = "select CREATED_BY_USER_ID, SCRPT_CD, SCRPT_NM, SCRPT_DESC, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
				+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", LAST_UPDATED_BY_USER_ID from script_au where SCRPT_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String scriptPostAuditQuery1(String whereField) {

		String geoPostQuery = "WITH CTE AS(select CREATED_BY_USER_ID, SCRPT_CD, SCRPT_NM, SCRPT_DESC, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
				+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD"
				+ " from script_au  where SCRPT_CD='" + whereField + "'"+" ORDER BY LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}

	public String dayOfWeekPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_day_of_week where DAY_OF_WEEK_NBR='" + whereField + "'";
		return geoPostQuery;
	}

	public String dayOfWeekGetQuery() {
		String geoGetQuery = "select * from ref_day_of_week";
		return geoGetQuery;
	}

	public String monthOfYearPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_month_of_year where MONTH_OF_YEAR_NBR='" + whereField + "'";
		return geoPostQuery;
	}

	public String monthOfYearGetQuery() {
		String geoGetQuery = "select * from ref_month_of_year";
		return geoGetQuery;
	}

	public String countryOrgStdPostQuery(String cntryCode, String cntryShName, String cntryFullName, String orgStdCode, String effectiveDate, String expirationDate)
	{
		String CNTRY_SHT_NM,CNTRY_FULL_NM;
		if(cntryShName.isEmpty()){
			CNTRY_SHT_NM = "and CNTRY_SHT_NM is null ";
		}else{
			CNTRY_SHT_NM = "and CNTRY_SHT_NM ='"+cntryShName+"'";
		}
		if(cntryFullName.isEmpty()){
			CNTRY_FULL_NM = "and CNTRY_FULL_NM is null";
		}else{
			CNTRY_FULL_NM = "and CNTRY_FULL_NM ='"+cntryFullName+"'";
		}
		String geoPostQuery = "select GEOPL_ID, CNTRY_SHT_NM, CNTRY_FULL_NM, ORG_STD_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " +
				"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from cntry_org_std where geopl_id in (select geopl_id from country where cntry_cd='"+cntryCode+"')  "
				+ CNTRY_SHT_NM+CNTRY_FULL_NM+" and ORG_STD_CD = '"+orgStdCode+"' and EFFECTIVE_DT ='"+effectiveDate +"'  and EXPIRATION_DT ='"+expirationDate +"'";
		return geoPostQuery;
	}

	public String countryOrgStdJMStQuery(String cntryCode, String cntryShName, String cntryFullName, String orgStdCode, String effectiveDate, String expirationDate)
	{
		String CNTRY_SHT_NM,CNTRY_FULL_NM;
		if(cntryShName.isEmpty()){
			CNTRY_SHT_NM = "CNTRY_SHT_NM is null ";
		}else{
			CNTRY_SHT_NM = "CNTRY_SHT_NM ='"+cntryShName+"'";
		}
		if(cntryFullName.isEmpty()){
			CNTRY_FULL_NM = " and CNTRY_FULL_NM is null";
		}else{
			CNTRY_FULL_NM = " and CNTRY_FULL_NM ='"+cntryFullName+"'";
		}
	/*	String geoPostQuery = "select GEOPL_ID, CNTRY_SHT_NM, CNTRY_FULL_NM, ORG_STD_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " +
				"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from cntry_org_std where "
				+ CNTRY_SHT_NM+CNTRY_FULL_NM+" and ORG_STD_CD = '"+orgStdCode+"' and EFFECTIVE_DT ='"+effectiveDate +"'  and EXPIRATION_DT ='"+expirationDate +"'";*/
		//Avinash
		String geoPostQuery1 = "WITH CTE AS(select cntry_org_std.GEOPL_ID,cntry_org_std.ORG_STD_CD, country.cntry_cd,"
				+ " cntry_org_std.CNTRY_SHT_NM, cntry_org_std.CNTRY_FULL_NM, to_char(cntry_org_std.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\","
				+ " to_char(cntry_org_std.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"	 from cntry_org_std inner join country "
				+ "on cntry_org_std.geopl_id = country.geopl_id	where cntry_org_std.CNTRY_SHT_NM ='"+cntryShName+"' and cntry_org_std.CNTRY_FULL_NM ='"
				+cntryFullName+"' and cntry_org_std.ORG_STD_CD = '"+orgStdCode+"' and cntry_org_std.EFFECTIVE_DT ='"+effectiveDate
				+"'  and cntry_org_std.EXPIRATION_DT ='"+expirationDate+"' ORDER BY cntry_org_std.LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";

		return geoPostQuery1;
	}

	public String countryOrgStdPostAuditQuery(String cntryCode, String cntryShName, String cntryFullName, String orgStdCode, String effectiveDate, String expirationDate)
	{
		String CNTRY_SHT_NM,CNTRY_FULL_NM;
		if(cntryShName.isEmpty()){
			CNTRY_SHT_NM = "and CNTRY_SHT_NM is null ";
		}else{
			CNTRY_SHT_NM = "and CNTRY_SHT_NM ='"+cntryShName+"'";
		}
		if(cntryFullName.isEmpty()){
			CNTRY_FULL_NM = "and CNTRY_FULL_NM is null";
		}else{
			CNTRY_FULL_NM = "and CNTRY_FULL_NM ='"+cntryFullName+"'";
		}
		String geoPostQuery = "WITH CTE AS(select GEOPL_ID, CNTRY_SHT_NM, CNTRY_FULL_NM, ORG_STD_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " +
				"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD from cntry_org_std_au where geopl_id in (select geopl_id from country where cntry_cd='"+cntryCode+"')  "
				+ CNTRY_SHT_NM+CNTRY_FULL_NM+" and ORG_STD_CD = '"+orgStdCode+"' and EFFECTIVE_DT ='"+effectiveDate +"' and EXPIRATION_DT = '"+expirationDate +"' ORDER BY LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}

	 // GEOPL RLTSP
	public String geopRltspPostQuery(String whereField1, String whereField2 , String whereField3 ,String whereField4) {
		String geoPostQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "' "
				+"and EFFECTIVE_DT ='"+whereField3 +"'"+" and EXPIRATION_DT ='"+whereField4 +"'";
		return geoPostQuery;
	}

	public String geopRltspPostQueryAudit(String whereField1, String whereField2 , String whereField3,String whereField4) {
		String geoPostQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD from geopl_rltsp_au where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "' "
				+"and EFFECTIVE_DT ='"+whereField3 +"'"+" and EXPIRATION_DT ='"+whereField4 +"'";
		return geoPostQuery;
	}


	public String geopRltspGetQuery() {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp";
		return geoGetQuery;
	}

	public String geopRltspWithTargetEndDatesGetQuery(String whereField1, String whereField2 , String whereField3) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where "
				+ "(GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD')))";
		return geoGetQuery;
	}

	public String geopRltspWithRltspCodeGetQuery(String whereField) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_RLTSP_TYPE_CD='" + whereField + "'";
		return geoGetQuery;
	}

	public String geopRltspWithFromAndToGeoplIdGetQuery(String whereField, String whereField1, String whereField2) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_RLTSP_TYPE_CD='" + whereField + "' and GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID='" + whereField2 + "'";
		return geoGetQuery;
	}

	public String geopRltspWithFromGeoplIdGetQuery(String whereField, String whereField1) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_RLTSP_TYPE_CD='" + whereField + "' and GEOPL_COMPT_ID='" + whereField1 + "'";
		return geoGetQuery;
	}

	public String geopRltspWithToGeoplIdGetQuery(String whereField, String whereField1) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_RLTSP_TYPE_CD='" + whereField + "' and RELTD_GEOPL_COMPT_ID='" + whereField1 + "'";
		return geoGetQuery;
	}

	public String geopRltspWithFromToGeoplIdAndTargetEndDatesGetQuery(String whereField1, String whereField2 , String whereField3, String whereField4, String whereField5) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where "
				+ "((GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))"
				+ " and GEOPL_COMPT_ID='" + whereField4 + "' and RELTD_GEOPL_COMPT_ID='" + whereField5 + "'";
		return geoGetQuery;
	}

	public String geopRltspWithFromGeoplIdGetAndTargetEndDatesQuery(String whereField1, String whereField2 , String whereField3, String whereField4) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where "
				+ "((GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))"
				+ " and GEOPL_COMPT_ID='" + whereField4 + "'";
		return geoGetQuery;
	}

	public String geopRltspWithToGeoplIdGetAndTargetEndDatesQuery(String whereField1, String whereField2 , String whereField3, String whereField4) {
		String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where "
				+ "((GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))"
				+ " and RELTD_GEOPL_COMPT_ID='" + whereField4 + "'";
		return geoGetQuery;
	}

	      // Gep Relationship GraphQL
		public String geopRltspWithTargetEndDatesGraphQLQuery(String whereField1, String whereField2 , String whereField3, String whereField4, String whereField5) {
			String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
					+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
					+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where "
					+ "(GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
					"or (GEOPL_RLTSP_TYPE_CD='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))) and GEOPL_COMPT_ID='"+whereField4+"' and RELTD_GEOPL_COMPT_ID ='"+whereField5+"'";
			return geoGetQuery;
		}

		public String geopRltspWithFromAndToGeoplIdGraphQuery(String whereField, String whereField1, String whereField2) {
			String geoGetQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
					+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
					+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_RLTSP_TYPE_CD='" + whereField + "' and GEOPL_COMPT_ID='" + whereField1 + "' AND RELTD_GEOPL_COMPT_ID='" + whereField2 + "'";
			return geoGetQuery;
		}

	 // stProvStd Post Query

	public String stProvStdPostQuery(String whereField1,String whereField2,String whereField3) {
		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from st_prov_std where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"' and EXPIRATION_DT='"+ whereField3 +"'";
		return geoPostQuery;
	}

	public String stProvStdPostQueryJMS(String whereField1,String whereField2,String whereField3) {
		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\""
				+" from st_prov_std where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"' and EXPIRATION_DT='"+ whereField3 +"'";
		return geoPostQuery;
	}

	public String stProvStdAuditPostQuery(String whereField1,String whereField2) {
		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD from st_prov_std_au where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"'";
		return geoPostQuery;
	}

	public String stProvStdAuditQuery(String whereField1,String whereField2, String whereField3, String whereField4) {
		String geoPostQuery = "WITH CTE AS(select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ "CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD from st_prov_std_au where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"' and EXPIRATION_DT='"+whereField3+"' and ST_PROV_NM='"+whereField4+"' ORDER BY LAST_UPDATED_TMSTP DESC)"+
				"Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}

	public String stProvStdAuditPutCountQuery(String whereField1) {
		String geoAuditCountQuery = "select count(*) from st_prov_std_au where ST_PROV_CD='" + whereField1 + "' AND REVISION_TYPE_CD ='1'";
		return geoAuditCountQuery;
	}

	public String stProvStdGraphQLQuery(String whereField2 , String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_CD, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where "
				+ "((effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String stProvStdGraphQLGidQuery(String wherefield, String whereField2 , String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_CD, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where st.GEOPL_ID='" + wherefield + "'"
				+ " and ((effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String stProvStdGraphQLParamQuery(String wherefield, String whereField2 , String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_CD, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where "
				+ "ST_PROV_CD='" + wherefield + "' AND ((effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String geoRelshipAuditPutCountQuery(String whereField1,String whereField2) {
		String geoAuditCountQuery = "select count(*) from geopl_rltsp_au where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "'  AND REVISION_TYPE_CD ='1'";
		return geoAuditCountQuery;
	}

	public String contryOrgStdCountPutQuery(String wherefield)
	{

		String geoAuditCountQuery = "select count(*) from cntry_org_std_au where CNTRY_SHT_NM='" + wherefield + "' AND REVISION_TYPE_CD = '1'";
		return geoAuditCountQuery;
	}


	public String stProvStdGetQuery(String whereField2 , String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_NM, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where "
				+ "((effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

//	public String stProvStdWithGeoplIdGetQuery(String whereField1, String whereField2 , String whereField3) {
//		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_NM, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\",\r\n" +
//				"				st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where st.GEOPL_ID ='"+whereField+"' and"
//						+ "and effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))\" + " +
//						"or (GEOPL_ID='" + whereField1 + "' and expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
//		return geoPostQuery;
//	}

	public String stProvStdWithGeoplIdTargetEndDatesGetQuery(String whereField1, String whereField2 , String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_NM, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where "
				+ "((st.GEOPL_ID='" + whereField1 + "' and st.effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (st.GEOPL_ID='" + whereField1 + "' and st.expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (st.effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

//	public String stProvStdWithStProvCdOrgStdCdGetQuery(String whereField1, String whereField2) {
//		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
//				+ " CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from st_prov_std  where ST_PROV_CD ='" + whereField1 + "' and ORG_STD_CD = '"+whereField2+"'";
//		return geoPostQuery; organizationStandardName
//	}

	public String stProvStdWithstateProvinceCodeOrgStdCdTargetEndDatesGetQuery(String whereField1, String whereField2 , String whereField3, String whereField4) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_NM, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD  where "
				+ "((st.effective_dt BETWEEN to_date('" + whereField3 + "','YYYY-MM-DD') AND to_date('" + whereField4 + "','YYYY-MM-DD'))" +
				"or (st.expiration_dt >= to_date('" + whereField3 + "','YYYY-MM-DD') and (st.effective_dt <=to_date('" + whereField4 + "','YYYY-MM-DD'))))"
				+ " and (st.ST_PROV_CD ='" + whereField1 + "' and st.ORG_STD_CD = '"+whereField2+"')";
		return geoPostQuery;
	}

	public String stProvStdWithCountrCdGetQuery(String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select st.GEOPL_ID, os.ORG_STD_NM, st.ST_PROV_CD, st.ST_PROV_NM, to_char(st.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(st.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " st.CREATED_BY_USER_ID, st.LAST_UPDATED_BY_USER_ID from st_prov_std  st inner join geopl_org_std os on st.ORG_STD_CD=os.ORG_STD_CD where "
				+ "(st.geopl_id in (select RELTD_GEOPL_COMPT_ID from geopl_rltsp where geopl_compt_id in (select GEOPL_ID from country where CNTRY_CD ='"+whereField1+"')"
				+ " and ((st.effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))"  +
				"or (st.expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (st.effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))))"
				+ " and((st.effective_dt BETWEEN to_date('" + whereField2 + "','YYYY-MM-DD') AND to_date('" + whereField3 + "','YYYY-MM-DD'))" +
				"or (st.expiration_dt >= to_date('" + whereField2 + "','YYYY-MM-DD') and (st.effective_dt <=to_date('" + whereField3 + "','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String affilTypePostQuery(String whereField) {
		String geoPostQuery = "select * from geopl_affil_type where AFFIL_TYPE_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String affilTypePutQuery(String whereField) {
		String geoPostQuery = "select * from geopl_affil_type where AFFIL_TYPE_ID='" + whereField + "'";
		return geoPostQuery;
	}

	public String affilTypeGetQuery() {
		String geoPostQuery = "select * from geopl_affil_type";
		return geoPostQuery;
	}

	public String affilTypeGraphQLWithParamQuery(String whereField) {
		String geoPostQuery = "select * from geopl_affil_type where AFFIL_TYPE_CD='" + whereField + "'";
		return geoPostQuery;
	}

	//Languagew Query
	public String langLocalesNewPutQuery(String whereField1,String whereField2, String whereField3, String whereField4) {


		String geoPostQuery = "Select loc.LOCL_CD, "
				+ "cntry.CNTRY_CD,"
				+ " loc.SCRIPT_CD, "
				+ " To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, "
				+ " loc.CLDR_VERS_NBR ,"
				+ " loc.DT_FULL_FORMT_DESC, "
				+ " loc.DT_LONG_FORMT_DESC, "
				+ " loc.DT_MED_FORMT_DESC, "
				+ " loc.DT_SHT_FORMT_DESC, "
				+ " To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT "
				+ "From  LOCALE loc inner join country cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
						+ "where  loc.LANGUAGE_CD = '"+whereField1+"' "
										+ "And loc.LOCL_CD = '"+whereField2+"' and loc.EFFECTIVE_DT =  '"+whereField3+"' and loc.EXPIRATION_DT =  '"+whereField4+"'";

		return geoPostQuery;
	}

	public String auditLangLocalesNewPostQuery(String whereField1,String whereField2,String whereField3,String whereField4) {


		String geoPostQuery = "Select loc.LOCL_CD, "
				+ "cntry.CNTRY_CD,"
				+ " loc.SCRIPT_CD, "
				+ " To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, "
				+ " loc.CLDR_VERS_NBR ,"
				+ " loc.DT_FULL_FORMT_DESC, "
				+ " loc.DT_LONG_FORMT_DESC, "
				+ " loc.DT_MED_FORMT_DESC, "
				+ " loc.DT_SHT_FORMT_DESC, "
				+ " To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT, "
				+ " loc.REVISION_TYPE_CD "
				+ " From  LOCALE_au loc inner join country_au cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language_au lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
				+ " Where loc.GEOPL_ID = '"+whereField1+"' "
						+ "and loc.LANGUAGE_CD = '"+whereField2+"' "
								+ "And loc.SCRIPT_CD = '"+whereField3+"'  "
										+ "And loc.LOCL_CD = '"+whereField4+"'";

		return geoPostQuery;
	}

	public String auditLangLocalesNewPutQuery(String whereField1,String whereField2,String whereField3) {
		String geoPostQuery = "WITH CTE AS( Select loc.LOCL_CD, "
				+ "cntry.CNTRY_CD,"
				+ " loc.SCRIPT_CD, "
				+ " To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, "
				+ " loc.CLDR_VERS_NBR ,"
				+ " loc.DT_FULL_FORMT_DESC, "
				+ " loc.DT_LONG_FORMT_DESC, "
				+ " loc.DT_MED_FORMT_DESC, "
				+ " loc.DT_SHT_FORMT_DESC, "
				+ " To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT, "
				+ " loc.REVISION_TYPE_CD "
				+ " From  LOCALE_au loc inner join country_au cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language_au lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
						+ " where loc.LANGUAGE_CD = '"+whereField1+"' "
								+ "And loc.SCRIPT_CD = '"+whereField2+"'  "
										+ "And loc.LOCL_CD = '"+whereField3+"'  ORDER BY loc.LAST_UPDATED_TMSTP DESC ) Select * from CTE where rownum ='1'";

		return geoPostQuery;
	}



	public String langPostQuery(String whereField) {
		String geoPostQuery = "select * from language  where LANGUAGE_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String languageJMSQuery(String whereField1,String whereField2,String whereField3) {


		String geoPostQuery = "Select lang.LANGUAGE_CD, lang.ENGL_LANGUAGE_NM, lang.NATIVE_SCRIPT_LANGUAGE_NM, loc.LANGUAGE_CD  ,To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, loc.CLDR_VERS_NBR ,"
				+ "loc. GEOPL_ID ,loc.LOCL_CD  ,cntry.CNTRY_CD ,loc.DT_FULL_FORMT_DESC  ,"
				+ "loc.DT_LONG_FORMT_DESC  ,loc.DT_MED_FORMT_DESC  ,loc.DT_SHT_FORMT_DESC   ,To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT  ,"
				+ "To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT  ,loc.SCRIPT_CD ,lang.ENGL_LANGUAGE_NM "
				+ "From  LOCALE loc inner join country cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
				+ "Where loc.GEOPL_ID = '"+whereField1+"'  And loc.SCRIPT_CD = '"+whereField2+"'  And loc.LOCL_CD = '"+whereField3+"'";


		return geoPostQuery;
	}


//	public String langTrnslDowPostQuery(String whereField1, String whereField2) {
//		String geoPostQuery = "select * from trnsl_dow where locl_cd='" + whereField1 + "' and DAY_OF_WEEK_NBR='" + whereField2 + "'";
//		return geoPostQuery;
//	}

	public String langTrnslDowPostQuery(String whereField1, String whereField2) {
		String geoPostQuery ="Select DAY_OF_WEEK_NBR,TRNSL_DOW_NM FROM trnsl_dow t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='"+whereField1+"' AND t.DAY_OF_WEEK_NBR='"+whereField2+"' order by l.last_updated_tmstp DESC offset 0 rows FETCH NEXT 1 rows only";
	return geoPostQuery;
	}



//	public String langTrnslDowPostQuery(String whereField1, String whereField2) {
//		String geoPostQuery ="select locl_cd  from Locale where language_cd='" + whereField1 + "' order by last_updated_tmstp desc offset 0 rows fetch next 1 rows only";
//		return geoPostQuery;
//	}


	public String langTrnslMonthOfYearPostQuery(String whereField1, String whereField2) {
		String geoPostQuery = "Select MONTH_OF_YEAR_NBR,TRNSL_MONTH_OF_YEAR_NM FROM trnsl_mth_of_yr  t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='"+whereField1+"' AND t.MONTH_OF_YEAR_NBR='"+whereField2+"' order by l.last_updated_tmstp DESC offset 0 rows FETCH NEXT 1 rows only";
		return geoPostQuery;
	}

//	public String langTrnslMonthOfYearPostQuery(String whereField1, String whereField2) {
//		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where locl_cd='" + whereField1 + "' and MONTH_OF_YEAR_NBR='" + whereField2 + "'";
//
//
//
//		return geoPostQuery;
//	}


	public String langGetQuery() {
		String geoPostQuery = "select * from language";
		return geoPostQuery;
	}
	public String langCodeGetQuery(String whereField1) {
		String geoPostQuery = "select * from language where LANGUAGE_CD='" + whereField1 + "' ";
		return geoPostQuery;
	}

	public String langLocalesNewGetQuery(String whereField1) {


		String graphQLQuery = "Select  distinct loc.LOCL_CD, cntry.CNTRY_CD, lang.LANGUAGE_CD, loc.SCRIPT_CD,"
				+ " loc.DT_FULL_FORMT_DESC, loc.DT_LONG_FORMT_DESC, loc.DT_MED_FORMT_DESC, loc.DT_SHT_FORMT_DESC, loc.CLDR_VERS_NBR "
				+ " , To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT , To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT,  "
				+ "To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT From  LOCALE loc inner join country cntry "
				+ "on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD Where lang.LANGUAGE_CD = '"+whereField1+"' ";


		return graphQLQuery;
	}

	public String langPostAuditQuery(String whereField) {
		//String geoPostQuery = "select * from language_au  where LANGUAGE_CD='" + whereField + "'";
		String geoPostQuery = "WITH CTE AS(select * from language_au  where LANGUAGE_CD='" + whereField + "'"+" ORDER BY LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}

	public String langTrnslDowPostAuditQuery(String whereField1, String whereField2) {
		String geoPostQuery = "WITH CTE AS(select * from trnsl_dow_au t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='"+whereField1+"' and t.DAY_OF_WEEK_NBR='"+whereField2+"' ORDER BY l.LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}

//	public String langTrnslMonthOfYearPostAuditQuery(String whereField1, String whereField2) {
//		String geoPostQuery = "WITH CTE AS(select * from TRNSL_MTH_OF_YR_AU  where locl_cd='"+whereField1+"' and MONTH_OF_YEAR_NBR='"+whereField2+"'"+
//				" ORDER BY LAST_UPDATED_TMSTP DESC)" +
//				" Select * from CTE where rownum ='1'";
//		return geoPostQuery;
//	}

	public String langTrnslMonthOfYearPostAuditQuery(String whereField1, String whereField2) {
		String geoPostQuery = "WITH CTE AS(select * from TRNSL_MTH_OF_YR_AU t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='"+whereField1+"' and t.MONTH_OF_YEAR_NBR='"+whereField2+"' ORDER BY l.LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1'";
		return geoPostQuery;
	}


//	WITH CTE AS(select * from TRNSL_MTH_OF_YR_AU t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE
//			l.language_cd='"+whereField1+"' and t.MONTH_OF_YEAR_NBR='"+whereField2+"'" ORDER BY l.LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1';

	public String langTrnslDowGetQuery(String whereField) {
		String geoPostQuery = "select * from trnsl_dow where locl_cd='" + whereField + "' order by DAY_OF_WEEK_NBR";
		return geoPostQuery;
	}

	public String langTrnslMonthOfYearGetQuery(String whereField) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where locl_cd='" + whereField + "' order by month_of_year_nbr";
		return geoPostQuery;
	}

	public String langPutAuditCountQuery1(String whereField) {
		String langAuditCountQuery1 = "select count(*) from language_au where LANGUAGE_CD='" + whereField + "' and REVISION_TYPE_CD='1'";
		return langAuditCountQuery1;
	}

	public String langPutAuditCountQuery2(String whereField1) {
		String langAuditCountQuery2 = "Select count(*) FROM trnsl_dow_au t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='" + whereField1 + "'  and t.REVISION_TYPE_CD='1'";
		return langAuditCountQuery2;
	}

	public String langPutAuditCountQuery3(String whereField1) {
		String langAuditCountQuery3 = "Select count(*) FROM TRNSL_MTH_OF_YR_AU t INNER JOIN Locale l ON t.LOCL_CD = l.LOCL_CD WHERE l.language_cd='" + whereField1 + "'  and t.REVISION_TYPE_CD='1' ";
		return langAuditCountQuery3;
	}

	public String cntryPostQuery(String whereField1,String whereField2,String whereField3,String whereField4) {
		String geoPostQuery = "SELECT GEOPL_ID " +
				",CNTRY_CD " +
				",CNTRY_NUM_CD " +
				",THREE_CHAR_CNTRY_CD " +
				",INDPT_FLG " +
				",DEPN_RLTSP_ID " +
				",DEPN_CNTRY_CD " +
				",PSTL_FORMT_DESC " +
				",PSTL_FLG " +
				",PSTL_LTH_NBR " +
				",FIRST_WORK_WK_DAY_NM " +
				",LAST_WORK_WK_DAY_NM " +
				",WKEND_FIRST_DAY_NM " +
				",INTL_DIAL_CD" +
				",LAND_PH_MAX_LTH_NBR" +
				",LAND_PH_MIN_LTH_NBR" +
				",MOBL_PH_MAX_LTH_NBR" +
				",MOBL_PH_MIN_LTH_NBR" +
				",PH_NBR_PATRN_DESC" +
				",CREATED_BY_USER_ID " +
				",LAST_UPDATED_BY_USER_ID " +
				",INET_DOMN_NM " +
				"  ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				"  ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				",CREATED_BY_USER_ID "+
				",LAST_UPDATED_BY_USER_ID "+
				"FROM COUNTRY " +
				"WHERE CNTRY_CD = '"+whereField1+"' "+
				"AND GEOPL_ID = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' "+
				" And EXPIRATION_DT = '"+whereField4+"' ";
				/*"AND CNTRY_NUM_CD = '"+whereField2+"' " +
				"AND THREE_CHAR_CNTRY_CD = '"+whereField3+"' " +
				"AND FIRST_WORK_WK_DAY_NM = '"+whereField4+"' " +
				"AND LAST_WORK_WK_DAY_NM = '"+whereField5+"' " +
				"AND WKEND_FIRST_DAY_NM = '"+whereField6+"' " +
				" AND EFFECTIVE_DT = '"+whereField7+"'";*/

		return geoPostQuery;
	}


	public String cntryPostAuditQuery(String whereField1,String whereField2,String whereField3,String whereField4) {

		String geoPostQuery = "SELECT GEOPL_ID " +
				",CNTRY_CD " +
				",CNTRY_NUM_CD " +
				",THREE_CHAR_CNTRY_CD " +
				",INDPT_FLG " +
				",DEPN_RLTSP_ID " +
				",DEPN_CNTRY_CD " +
				",PSTL_FORMT_DESC " +
				",PSTL_FLG " +
				",PSTL_LTH_NBR " +
				",FIRST_WORK_WK_DAY_NM " +
				",LAST_WORK_WK_DAY_NM " +
				",WKEND_FIRST_DAY_NM " +
				",INTL_DIAL_CD" +
				",LAND_PH_MAX_LTH_NBR" +
				",LAND_PH_MIN_LTH_NBR" +
				",MOBL_PH_MAX_LTH_NBR" +
				",MOBL_PH_MIN_LTH_NBR" +
				",PH_NBR_PATRN_DESC" +
				",CREATED_BY_USER_ID " +
				",LAST_UPDATED_BY_USER_ID " +
				",INET_DOMN_NM " +
				"  ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				"  ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				",CREATED_BY_USER_ID "+
				",LAST_UPDATED_BY_USER_ID "+
				", REVISION_TYPE_CD "+
				"FROM COUNTRY_AU " +
				"WHERE CNTRY_CD = '"+whereField1+"' "+
				"AND GEOPL_ID = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' "+
				" And EXPIRATION_DT = '"+whereField4+"' "+
				" ORDER BY LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";
				/*"AND CNTRY_NUM_CD = '"+whereField2+"' " +
				"AND THREE_CHAR_CNTRY_CD = '"+whereField3+"' " +
				"AND FIRST_WORK_WK_DAY_NM = '"+whereField4+"' " +
				"AND LAST_WORK_WK_DAY_NM = '"+whereField5+"' " +
				"AND WKEND_FIRST_DAY_NM = '"+whereField6+"' " +
				" AND EFFECTIVE_DT = '"+whereField7+"'";*/

		return geoPostQuery;

	}

	public String cntryAuditPutCountQuery(String whereField1) {

		String geoPostQuery = "SELECT count(*)" +
				"FROM COUNTRY_AU " +
				"WHERE CNTRY_CD = '"+whereField1+"' " +
				"AND REVISION_TYPE_CD = '1'";

		return geoPostQuery;

	}

	/*public String cntryCountryDialingsPostQuery(String whereField1,String whereField3,String whereField4) {
		String geoPostQuery = "SELECT GEOPL_ID" +
				"        ,INTL_DIAL_PREFX_CD" +
				"  ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				"  ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" 		  FROM CNTRY_DIAL" +
				" 		  WHERE GEOPL_ID = '"+whereField1+"' " +
				"         AND INTL_DIAL_PREFX_CD = '"+whereField3+"' "+
		        "         AND EFFECTIVE_DT  = '"+whereField4+"'";

		return geoPostQuery;
	}

	public String cntryCountryDialingsPostAuditQuery(String whereField1,String whereField3,String whereField4) {
		String geoPostQuery = "SELECT GEOPL_ID" +
				"        ,INTL_DIAL_PREFX_CD" +
				"  ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				"  ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				"  , REVISION_TYPE_CD "+
				" 		  FROM CNTRY_DIAL_AU" +
				" 		  WHERE GEOPL_ID = '"+whereField1+"' " +
				"         AND INTL_DIAL_PREFX_CD = '"+whereField3+"' "+
		        "         AND EFFECTIVE_DT  = '"+whereField4+"'" +
				" ORDER BY LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";

		return geoPostQuery;
	}

	public String cntryCountryDialingsPutAuditCountQuery(String whereField1) {
		String geoPostQuery = "SELECT count(*)" +
				" FROM CNTRY_DIAL_AU" +
				" WHERE INTL_DIAL_CD = '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";

		return geoPostQuery;
	}*/

	public String cntryCurrenciesPostQuery(String whereField1,String whereField2,String whereField3,String whereField4,String whereField5) {

		String geoPostQuery = "SELECT GEOPL_ID ," +
							" CURR_NUM_CD," +
							"  CURR_CD," +
							"  MINOR_UNIT_CD ," +
							"  MONEY_FORMT_DESC," +
							"  To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT ," +
							"  To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
							" FROM CURRENCY " +
							" WHERE GEOPL_ID    = '"+whereField1+"' " +
							" AND CURR_CD       = '"+whereField2+"' " +
							" AND MINOR_UNIT_CD = '"+whereField3+"' " +
							" And EFFECTIVE_DT = '"+whereField4+"' "+
							" And EXPIRATION_DT = '"+whereField5+"' ";


		return geoPostQuery;
	}

	public String cntryCurrenciesPostAuditQuery(String whereField1,String whereField2,String whereField3,String whereField4,String whereField5) {

		String geoPostQuery = "SELECT GEOPL_ID ," +
							" CURR_NUM_CD," +
							"  CURR_CD," +
							"  MINOR_UNIT_CD ," +
							"  MONEY_FORMT_DESC," +
							"  To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT ," +
							"  To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
							" , REVISION_TYPE_CD "+
							" FROM CURRENCY_AU " +
							" WHERE GEOPL_ID    = '"+whereField1+"' " +
							" AND CURR_CD       = '"+whereField2+"' " +
							" AND MINOR_UNIT_CD = '"+whereField3+"' " +
							" And EFFECTIVE_DT = '"+whereField4+"' "+
							" And EXPIRATION_DT = '"+whereField5+"' "+
							" ORDER BY LAST_UPDATED_TMSTP DESC "+
							" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cntryCurrenciesPutAuditCountQuery(String whereField1) {

		String geoPostQuery = "SELECT count(*)" +
							" FROM CURRENCY_AU " +
							" Where CURR_CD = '"+whereField1+"' " +
							" AND REVISION_TYPE_CD = '1'";


		return geoPostQuery;
	}

	public String cntryGeopoliticalUOMPostQuery(String whereField1,String whereField2) {


			String geoPostQuery = "Select gpum.GEOPL_ID " +
					" ,rumtyp.UOM_TYPE_CD " +
					" ,To_Char( gpum.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
					" ,To_Char( gpum.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
					" FROM GEOPL_UOM gpum " +
					" LEFT JOIN REF_UOM_TYPE rumtyp " +
					" on gpum.UOM_TYPE_CD = rumtyp.UOM_TYPE_CD " +
					" WHERE  gpum.GEOPL_ID  = '"+whereField1+"' " +
					" AND gpum.EFFECTIVE_DT = '"+whereField2+"' "+
					" ORDER BY gpum.LAST_UPDATED_TMSTP DESC "+
					" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";

			return geoPostQuery;
		}

	public String cntryGeopoliticalUOMPostAuditQuery(String whereField1,String whereField2) {


		String geoPostQuery = "Select gpum.GEOPL_ID " +
				" ,rumtyp.UOM_TYPE_CD " +
				" ,To_Char( gpum.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( gpum.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" , REVISION_TYPE_CD "+
				" FROM GEOPL_UOM_AU gpum " +
				" LEFT JOIN REF_UOM_TYPE rumtyp " +
				" on gpum.UOM_TYPE_CD = rumtyp.UOM_TYPE_CD " +
				" WHERE  gpum.GEOPL_ID  = '"+whereField1+"' " +
				" AND gpum.EFFECTIVE_DT = '"+whereField2+"' "+
				" ORDER BY gpum.LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cntryGeopoliticalUOMPutAuditCountQuery(String whereField1) {


		String geoPostQuery = "Select count(*) " +
				" FROM GEOPL_UOM_AU gpum " +
				" LEFT JOIN REF_UOM_TYPE rumtyp " +
				" on gpum.UOM_TYPE_CD = rumtyp.UOM_TYPE_CD " +
				" WHERE  rumtyp.UOM_TYPE_CD  = '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";



		return geoPostQuery;
	}

	public String cntryGeopoliticalHolidaysPostQuery(String whereField1,String whereField2) {


		String geoPostQuery = "Select hldy.HDAY_NM " +
				" ,To_Char( Ghdy.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( Ghdy.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" From GEOPL_HDAY Ghdy " +
				" Left Join HOLIDAY hldy " +
				" On hldy.HDAY_ID = Ghdy.HDAY_ID " +
				" Where Ghdy.GEOPL_ID= '"+whereField1+"' " +
				" And Ghdy.EFFECTIVE_DT = '"+whereField2+"' ";


		return geoPostQuery;
	}

	public String cntryGeopoliticalHolidaysPostAuditQuery(String whereField1,String whereField2) {


		String geoPostQuery = "Select hldy.HDAY_NM " +
				" ,To_Char( Ghdy.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( Ghdy.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" , REVISION_TYPE_CD "+
				" From GEOPL_HDAY_AU Ghdy " +
				" Left Join HOLIDAY hldy " +
				" On hldy.HDAY_ID = Ghdy.HDAY_ID " +
				" Where Ghdy.GEOPL_ID= '"+whereField1+"' " +
				" And Ghdy.EFFECTIVE_DT = '"+whereField2+"' "+
				" ORDER BY Ghdy.LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cntryGeopoliticalHolidaysPostAuditCounyQuery(String whereField1) {


		String geoPostQuery = "Select count(*) " +
				" From GEOPL_HDAY_AU Ghdy " +
				" Left Join HOLIDAY hldy " +
				" On hldy.HDAY_ID = Ghdy.HDAY_ID " +
				" Where hldy.HDAY_NM= '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";



		return geoPostQuery;
	}

	public String cuntryGeopoliticalAffiliationsPostQuery(String whereField1,String whereField2) {


		String geoPostQuery = "Select gaff.GEOPL_ID," +
				"gafftyp.AFFIL_TYPE_CD " +
				" ,To_Char( gaff.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( gaff.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" From GEOPL_AFFIL gaff " +
				" LEFT JOIN  GEOPL_AFFIL_TYPE gafftyp " +
				" on gafftyp.AFFIL_TYPE_ID= gaff.AFFIL_TYPE_ID " +
				" Where gaff.GEOPL_ID = '"+whereField1+"' " +
				" AND gaff.EFFECTIVE_DT = '"+whereField2+"' "+
				" ORDER BY gaff.LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cuntryGeopoliticalAffiliationsPostAuditQuery(String whereField1,String whereField2) {


		String geoPostQuery = "Select gaff.GEOPL_ID," +
				"gafftyp.AFFIL_TYPE_CD " +
				" ,To_Char( gaff.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( gaff.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" , REVISION_TYPE_CD "+
				" From GEOPL_AFFIL_AU gaff " +
				" LEFT JOIN  GEOPL_AFFIL_TYPE gafftyp " +
				" on gafftyp.AFFIL_TYPE_ID= gaff.AFFIL_TYPE_ID " +
				" Where gaff.GEOPL_ID = '"+whereField1+"' " +
				" AND gaff.EFFECTIVE_DT = '"+whereField2+"' "+
				" ORDER BY gaff.LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cuntryGeopoliticalAffiliationsPutAuditCountQuery(String whereField1) {


		String geoPostQuery = "Select count(*)" +
				" From GEOPL_AFFIL_AU gaff " +
				" LEFT JOIN  GEOPL_AFFIL_TYPE gafftyp " +
				" on gafftyp.AFFIL_TYPE_ID= gaff.AFFIL_TYPE_ID " +
				" Where gafftyp.AFFIL_TYPE_CD = '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";



		return geoPostQuery;
	}

	public String cntryLocalesPostQuery(String whereField1,String whereField2,String whereField3,String whereField4) {


			String geoPostQuery = "Select GEOPL_ID " +
					" ,LANGUAGE_CD " +
					" ,LOCL_CD " +
					" ,SCRIPT_CD " +
					" ,DT_FULL_FORMT_DESC " +
					" ,DT_LONG_FORMT_DESC " +
					" ,DT_MED_FORMT_DESC " +
					" ,DT_SHT_FORMT_DESC " +
					" ,CLDR_VERS_NBR " +
					" ,To_Char( CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT " +
					" ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
					" ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
					" From LOCALE " +
					" Where GEOPL_ID = '"+whereField1+"' " +
					" And SCRIPT_CD = '"+whereField4+"' " +
					" And LOCL_CD = '"+whereField2+"' " +
					" And EFFECTIVE_DT = '"+whereField3+"' ";


			return geoPostQuery;
		}

	public String langLocalesPostQuery(String whereField1,String whereField2,String whereField3) {


		String geoPostQuery = "Select loc.LANGUAGE_CD  ,To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, loc.CLDR_VERS_NBR ,"
				+ "loc. GEOPL_ID ,loc.LOCL_CD  ,cntry.CNTRY_CD ,loc.DT_FULL_FORMT_DESC  ,"
				+ "loc.DT_LONG_FORMT_DESC  ,loc.DT_MED_FORMT_DESC  ,loc.DT_SHT_FORMT_DESC   ,To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT  ,"
				+ "To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT  ,loc.SCRIPT_CD ,lang.ENGL_LANGUAGE_NM "
				+ "From  LOCALE loc inner join country cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
				+ "Where loc.GEOPL_ID = '"+whereField1+"'  And loc.SCRIPT_CD = '"+whereField2+"'  And loc.LOCL_CD = '"+whereField3+"'";


		return geoPostQuery;
	}

	public String langLocalesGetQuery(String whereField1) {

		String geoPostQuery = "Select  distinct loc.LOCL_CD ,cntry.CNTRY_CD ,loc.SCRIPT_CD , To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT, loc.CLDR_VERS_NBR ,"
				+ "loc.DT_FULL_FORMT_DESC  ,"
				+ "loc.DT_LONG_FORMT_DESC  ,loc.DT_MED_FORMT_DESC  ,loc.DT_SHT_FORMT_DESC   ,To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT  ,"
				+ "To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT "
				+ "From  LOCALE loc inner join country cntry on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD "
				+ "Where lang.LANGUAGE_CD = '"+whereField1+"' ";
		return geoPostQuery;
	}



	public String langLocalesGraphQLQuery(String whereField1) {


		String graphQLQuery = "Select  distinct loc.LOCL_CD, cntry.CNTRY_CD, lang.LANGUAGE_CD, loc.SCRIPT_CD,"
				+ " loc.DT_FULL_FORMT_DESC, loc.DT_LONG_FORMT_DESC, loc.DT_MED_FORMT_DESC, loc.DT_SHT_FORMT_DESC, loc.CLDR_VERS_NBR "
				+ " , To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT , To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT,  "
				+ "To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT From  LOCALE loc inner join country cntry "
				+ "on loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD Where lang.LANGUAGE_CD = '"+whereField1+"' ";


		return graphQLQuery;
	}

	public String langLocales_LocaleCdGraphQLQuery(String whereField1) {


		String graphQLQuery = "Select  distinct loc.LOCL_CD, cntry.CNTRY_CD, lang.LANGUAGE_CD, loc.SCRIPT_CD, "
				+ "loc.DT_FULL_FORMT_DESC, loc.DT_LONG_FORMT_DESC, loc.DT_MED_FORMT_DESC, loc.DT_SHT_FORMT_DESC, loc.CLDR_VERS_NBR "
				+ " , To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT , To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT From  LOCALE loc inner join country cntry on "
				+ "loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD Where loc.LOCL_CD = '"+whereField1+"' ";


		return graphQLQuery;
	}

	public String langLocalesCountryCdGraphQLQuery(String whereField1) {


		String graphQLQuery = "Select  distinct loc.LOCL_CD, cntry.CNTRY_CD, lang.LANGUAGE_CD, loc.SCRIPT_CD, "
				+ "loc.DT_FULL_FORMT_DESC, loc.DT_LONG_FORMT_DESC, loc.DT_MED_FORMT_DESC, loc.DT_SHT_FORMT_DESC, loc.CLDR_VERS_NBR "
				+ " , To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT , To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT From  LOCALE loc inner join country cntry on "
				+ "loc. GEOPL_ID = cntry.GEOPL_ID inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD Where cntry.CNTRY_CD = '"+whereField1+"' ";


		return graphQLQuery;
	}

	public String langLocalesDataGraphQLQuery(String whereField1, String whereField2,String whereField3,String whereField4,String whereField5,
			String whereField6,String whereField7,String whereField8, String whereField9,String whereField10,String whereField11,String whereField12) {


		String graphQLQuery = "Select  distinct loc.LOCL_CD, cntry.CNTRY_CD, lang.LANGUAGE_CD, loc.SCRIPT_CD, loc.DT_FULL_FORMT_DESC,"
				+ " loc.DT_LONG_FORMT_DESC, loc.DT_MED_FORMT_DESC, loc.DT_SHT_FORMT_DESC, loc.CLDR_VERS_NBR ,"
				+ " To_Char( loc.CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT , To_Char( loc.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT, "
				+ " To_Char( loc.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT From  LOCALE loc inner join country cntry on loc. GEOPL_ID = cntry.GEOPL_ID "
				+ " inner join  language lang on lang.LANGUAGE_CD = loc.LANGUAGE_CD Where loc.LOCL_CD = '"+whereField1+"'  and cntry.CNTRY_CD = '"+whereField2+"'"
				+ " and lang.LANGUAGE_CD = '"+whereField3+"' and loc.SCRIPT_CD = '"+whereField4+"' and loc.DT_FULL_FORMT_DESC = '"+whereField5+"'"
				+ " and loc.DT_LONG_FORMT_DESC = '"+whereField6+"' and loc.DT_MED_FORMT_DESC = '"+whereField7+"' and loc.DT_SHT_FORMT_DESC = '"+whereField8+"'"
				+ " and loc.CLDR_VERS_NBR = '"+whereField9+"' and loc.CLDR_VERS_DT = '"+whereField10+"' and loc.EFFECTIVE_DT = '"+whereField11+"'"
				+ "loc.EXPIRATION_DT = '"+whereField12+"'";


		return graphQLQuery;
	}



	public String cntryLocalesPostAuditQuery(String whereField1,String whereField2,String whereField3,String whereField4) {


		String geoPostQuery = "Select GEOPL_ID " +
				" ,LANGUAGE_CD " +
				" ,LOCL_CD " +
				" ,SCRIPT_CD " +
				" ,DT_FULL_FORMT_DESC " +
				" ,DT_LONG_FORMT_DESC " +
				" ,DT_MED_FORMT_DESC " +
				" ,DT_SHT_FORMT_DESC " +
				" ,CLDR_VERS_NBR " +
				" ,To_Char( CLDR_VERS_DT ,'yyyy-mm-dd') as CLDR_VERS_DT " +
				" ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" , REVISION_TYPE_CD "+
				" From LOCALE_AU " +
				" Where GEOPL_ID = '"+whereField1+"' " +
				" And SCRIPT_CD = '"+whereField4+"' " +
				" And LOCL_CD = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' "+
				" ORDER BY LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

public String cntryLocalesPutAuditCountQuery(String whereField1) {


		String geoPostQuery = "Select count(*)" +
				" From LOCALE_AU " +
				" Where LOCL_CD = '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";



		return geoPostQuery;
	}



	public String cntryTranslationGeopoliticalsPostQuery(String whereField1,String whereField2,String whereField3) {


		String geoPostQuery = "Select GEOPL_ID " +
				" ,LOCL_CD " +
				" ,SCRIPT_CD " +
				" ,TRNSL_NM " +
				" ,VERS_NBR " +
				" ,To_Char( VERS_DT ,'yyyy-mm-dd') as VERS_DT " +
				" ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" From TRNSL_GEOPL " +
				" Where GEOPL_ID = '"+whereField1+"' " +
				" And LOCL_CD = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' ";


		return geoPostQuery;
	}

	public String cntryTranslationGeopoliticalsPostAuditQuery(String whereField1,String whereField2,String whereField3) {


		String geoPostQuery = "Select GEOPL_ID " +
				" ,LOCL_CD " +
				" ,SCRIPT_CD " +
				" ,TRNSL_NM " +
				" ,VERS_NBR " +
				" ,To_Char( VERS_DT ,'yyyy-mm-dd') as VERS_DT " +
				" ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" , REVISION_TYPE_CD "+
				" From TRNSL_GEOPL_AU " +
				" Where GEOPL_ID = '"+whereField1+"' " +
				" And LOCL_CD = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' "+
				" ORDER BY LAST_UPDATED_TMSTP DESC "+
				" OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";


		return geoPostQuery;
	}

	public String cntryTranslationGeopoliticalsPutAuditCountQuery(String whereField1) {


		String geoPostQuery = "Select count(*)" +
				" From TRNSL_GEOPL_AU " +
				" Where TRNSL_NM = '"+whereField1+"' " +
				" AND REVISION_TYPE_CD = '1'";


		return geoPostQuery;
	}

	public String countryGeopoliticalTypePostQuery(String whereField) {


		String geoPostQuery = "WITH CTE AS(Select geo.GEOPL_ID, " +
				" gtyp.GEOPL_TYPE_NM " +
				" FROM GEOPOLITICAL geo " +
				" LEFT JOIN GEOPL_TYPE gtyp " +
				" ON geo.GEOPL_TYPE_ID = gtyp.GEOPL_TYPE_ID " +
				" WHERE geo.GEOPL_ID = '"+whereField+"'" +
			    " ORDER BY geo.LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";


		return geoPostQuery;
	}

public String countryGeopoliticalTypeJMSQuery(String whereField) {


		String geoPostQuery = "WITH CTE AS(Select geo.GEOPL_ID, " +
				" gtyp.GEOPL_TYPE_NM, gtyp.GEOPL_TYPE_ID " +
				" FROM GEOPOLITICAL geo " +
				" LEFT JOIN GEOPL_TYPE gtyp " +
				" ON geo.GEOPL_TYPE_ID = gtyp.GEOPL_TYPE_ID " +
				" WHERE geo.GEOPL_ID = '"+whereField+"'" +
			    " ORDER BY geo.LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";


		return geoPostQuery;
	}

	public String countryGeopoliticalTypePostAuditQuery(String whereField) {


		String geoPostQuery = "WITH CTE AS(Select geo.GEOPL_ID, " +
				" gtyp.GEOPL_TYPE_NM " +
				" , geo.REVISION_TYPE_CD "+
				" FROM GEOPOLITICAL_AU geo " +
				" LEFT JOIN GEOPL_TYPE gtyp " +
				" ON geo.GEOPL_TYPE_ID = gtyp.GEOPL_TYPE_ID " +
				" WHERE geo.GEOPL_ID = '"+whereField+"'" +
			    " ORDER BY geo.LAST_UPDATED_TMSTP DESC)" +
				" Select * from CTE where rownum ='1'";


		return geoPostQuery;
	}

	public String countryGeopoliticalTypePutAuditCountQuery(String whereField) {


		String geoPostQuery = "Select count(*) " +
				" FROM GEOPOLITICAL_AU geo " +
				" LEFT JOIN GEOPL_TYPE gtyp " +
				" ON geo.GEOPL_TYPE_ID = gtyp.GEOPL_TYPE_ID " +
				" WHERE gtyp.GEOPL_TYPE_NM = '"+whereField+"'" +
			    " AND REVISION_TYPE_CD = '1'";

		return geoPostQuery;
	}


	public String geopRltspPutQueryAudit(String whereField1, String whereField2 , String whereField3, String whereField4) {
		String geoPutAuditQuery = "WITH CTE AS(select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", "
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID, REVISION_TYPE_CD from geopl_rltsp_au where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "' "
				+"and EFFECTIVE_DT ='"+whereField3 +"' and EXPIRATION_DT ='"+whereField4 +"'   ORDER BY LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1' ";
		return geoPutAuditQuery;
	}



	public String countryGetQuery(String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select GEOPL_ID, "
				+ "CNTRY_NUM_CD, "
				+ "CNTRY_CD, "
				+ "THREE_CHAR_CNTRY_CD, "
				+ "INDPT_FLG, "
				+ "DEPN_RLTSP_ID, "
				+ "DEPN_CNTRY_CD, "
				+ "PSTL_FORMT_DESC, "
				+ "PSTL_FLG, "
				+ "PSTL_LTH_NBR, "
				+ "FIRST_WORK_WK_DAY_NM, "
				+ "LAST_WORK_WK_DAY_NM, " +
				" WKEND_FIRST_DAY_NM, "
				+ "INET_DOMN_NM, "
				+ "INTL_DIAL_CD, "
				+ "MOBL_PH_MIN_LTH_NBR, "
				+ "MOBL_PH_MAX_LTH_NBR, "
				+ "LAND_PH_MIN_LTH_NBR, "
				+ "LAND_PH_MAX_LTH_NBR, "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
				+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" "
				+ "from country "
				+ "where geopl_id='"+ whereField1 +"' and " +
				" ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') "
				+ "AND to_date('"+ whereField3 +"','YYYY-MM-DD')) "
				+ "or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
//						+ "and " +
//				" GEOPL_ID in (select GEOPL_ID from geopl_uom where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from geopl_hday where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from geopl_affil where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from trnsl_geopl where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from cntry_org_std where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from locale where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from CNTRY_DIAL where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " +
//				" GEOPL_ID in (select GEOPL_ID from currency where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and" +
//				" GEOPL_ID in (select GEOPL_ID from geopolitical where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
//				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))))";
		return geoPostQuery;
	}

	public String countryGraphQLQuery(String whereField1) {
		/*String geoPostQuery = "select GEOPL_ID, CNTRY_NUM_CD, CNTRY_CD, THREE_CHAR_CNTRY_CD, INDPT_FLG, DEPN_RLTSP_ID, DEPN_CNTRY_CD, PSTL_FORMT_DESC, PSTL_FLG, PSTL_LTH_NBR, FIRST_WORK_WK_DAY_NM, LAST_WORK_WK_DAY_NM, "
				+ " WKEND_FIRST_DAY_NM, INET_DOMN_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from country where geopl_id='"
				+ whereField1 + "'";*/

		String geoPostQuery = "select GEOPL_ID, "
		+ "CNTRY_NUM_CD, "
		+ "CNTRY_CD, "
		+ "THREE_CHAR_CNTRY_CD, "
		+ "INDPT_FLG, "
		+ "DEPN_RLTSP_ID, "
		+ "DEPN_CNTRY_CD, "
		+ "PSTL_FORMT_DESC, "
		+ "PSTL_FLG, "
		+ "PSTL_LTH_NBR, "
		+ "FIRST_WORK_WK_DAY_NM, "
		+ "LAST_WORK_WK_DAY_NM, " +
		" WKEND_FIRST_DAY_NM, "
		+ "INET_DOMN_NM, "
		+ "INTL_DIAL_CD, "
		+ "MOBL_PH_MIN_LTH_NBR, "
		+ "MOBL_PH_MAX_LTH_NBR, "
		+ "LAND_PH_MIN_LTH_NBR, "
		+ "LAND_PH_MAX_LTH_NBR, "
		+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
		+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" "
		+ "from country "
		+ "where geopl_id='"+ whereField1 +"' ";

		return geoPostQuery;
	}

	public String countryGraphQLNoParameterQuery() {
		String geoGraphQLQuery = "select GEOPL_ID, CNTRY_NUM_CD, CNTRY_CD, THREE_CHAR_CNTRY_CD, INDPT_FLG, DEPN_RLTSP_ID, DEPN_CNTRY_CD, PSTL_FORMT_DESC, PSTL_FLG, PSTL_LTH_NBR, FIRST_WORK_WK_DAY_NM, LAST_WORK_WK_DAY_NM, " +
				" WKEND_FIRST_DAY_NM, INET_DOMN_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from country ";
		return geoGraphQLQuery;
	}

	public String countryGetNoParameterQuery(String whereField2, String whereField3) {
		String geoPostQuery = "select GEOPL_ID, CNTRY_NUM_CD, CNTRY_CD, THREE_CHAR_CNTRY_CD, INDPT_FLG, DEPN_RLTSP_ID, DEPN_CNTRY_CD, PSTL_FORMT_DESC, PSTL_FLG, PSTL_LTH_NBR, FIRST_WORK_WK_DAY_NM, LAST_WORK_WK_DAY_NM, " +
				" WKEND_FIRST_DAY_NM, INET_DOMN_NM, INTL_DIAL_CD, "
				+ "MOBL_PH_MIN_LTH_NBR, "
				+ "MOBL_PH_MAX_LTH_NBR, "
				+ "LAND_PH_MIN_LTH_NBR, "
				+ "LAND_PH_MAX_LTH_NBR, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from country where " +
				" ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String countryGetParameterQuery(String whereField4, String whereField5,String whereField2, String whereField3) {
		String geoPostQuery = "select GEOPL_ID, CNTRY_NUM_CD, CNTRY_CD, THREE_CHAR_CNTRY_CD, INDPT_FLG, DEPN_RLTSP_ID, DEPN_CNTRY_CD, PSTL_FORMT_DESC, PSTL_FLG, PSTL_LTH_NBR, FIRST_WORK_WK_DAY_NM, LAST_WORK_WK_DAY_NM, " +
				" WKEND_FIRST_DAY_NM, INET_DOMN_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from country where " +
				"GEOPL_ID ='"+whereField4+"' AND CNTRY_CD ='"+whereField5+"' AND  ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String countryGeoTypeNameGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select * from geopl_type where geopl_Type_id in (select GEOPL_TYPE_ID from geopolitical where geopl_id='"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))) ";;
		return geoPostQuery;
	}

	public String countryUomTypeGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = " select UOM_TYPE_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_uom where GEOPL_ID='"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
		return geoPostQuery;
	}

	public String countryHolidayGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select hd.HDAY_NM, to_char(gh.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(gh.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_hday gh inner join holiday hd on gh.hday_id =hd.hday_id where gh.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryAffilTypeGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select af.geopl_id,af.affil_type_id,aft.affil_type_id,aft.affil_type_cd,aft.affil_type_nm, to_char(af.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(af.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_affil af inner join geopl_affil_type aft on af.AFFIL_TYPE_ID =aft.AFFIL_TYPE_ID where af.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryAffilTypeJMSQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select aft.AFFIL_TYPE_CD, aft.affil_type_nm, to_char(af.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(af.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_affil af inner join geopl_affil_type aft on af.AFFIL_TYPE_ID =aft.AFFIL_TYPE_ID where af.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))  ORDER BY aft.LAST_UPDATED_TMSTP DESC";// OFFSET 1 ROWS FETCH NEXT 1 ROWS ONLY ";
		return geoPostQuery;
	}


	public String countryTrnslGeoplJMSQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select la.engl_language_nm, tr.SCRIPT_CD, tr.trnsl_nm, tr.LOCL_CD, to_char(tr.VERS_DT,'YYYY-MM-DD') \"VERS_DT\", tr.vers_nbr, to_char(tr.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(tr.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from language la inner join trnsl_geopl tr on la.language_cd= tr.locl_cd where tr.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') "
				//" and				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))"
						+ ")) ";
		return geoPostQuery;
	}


	public String countryNoParameterOrgStdGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select go.org_std_nm, co.cntry_full_nm, co.cntry_sht_nm, to_char(co.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(co.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_org_std go inner join cntry_org_std co on go.org_std_cd = co.org_std_cd where co.geopl_id = '"+ whereField +"'"
				+ " and ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))";
		return geoPostQuery;
	}

	public String countryLocaleGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select  lo.LOCL_CD, lo.SCRIPT_CD, to_char(lo.CLDR_VERS_DT,'YYYY-MM-DD') \"CLDR_VERS_DT\", lo.cldr_vers_nbr, lo.dt_full_formt_desc, lo.dt_long_formt_desc, lo.dt_med_formt_desc, lo.dt_sht_formt_desc, to_char(lo.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(lo.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"" +
				"from language la inner join locale lo on la.language_cd=lo.language_cd where lo.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
		return geoPostQuery;
	}

	public String countryLocaleJMSQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select la.engl_language_nm, lo.LOCL_CD, lo.SCRIPT_CD, lo.LANGUAGE_CD, to_char(lo.CLDR_VERS_DT,'YYYY-MM-DD') \"CLDR_VERS_DT\", lo.cldr_vers_nbr, lo.dt_full_formt_desc, lo.dt_long_formt_desc, lo.dt_med_formt_desc, lo.dt_sht_formt_desc, to_char(lo.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(lo.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"" +
				"from language la inner join locale lo on la.language_cd=lo.language_cd where lo.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') "//and" +
				//"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')"
						+ "))  ORDER BY lo.LAST_UPDATED_TMSTP DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ";
		return geoPostQuery;
	}


	public String countryGeoTypeNameGraphQLQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select g.geopl_id,g.geopl_type_id,gt.geopl_type_id,gt.geopl_type_nm,to_char(g.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(g.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_type gt inner join geopolitical g on gt.geopl_type_id = g.geopl_type_id where g.geopl_id='"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryTrnslGeoplGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select tr.geopl_id,tr.locl_cd, tr.SCRIPT_CD, tr.trnsl_nm, to_char(tr.VERS_DT,'YYYY-MM-DD') \"VERS_DT\", tr.vers_nbr, to_char(tr.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(tr.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from language la inner join trnsl_geopl tr on la.language_cd=tr.language_cd where tr.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryDialGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select geopl_id,INTL_DIAL_PREFX_CD,  " +
				"to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"from cntry_dial where geopl_id='"+whereField+"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryCurrencyGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select geopl_id,CURR_NUM_CD, CURR_CD, MINOR_UNIT_CD, MONEY_FORMT_DESC, " +
				"to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from currency where geopl_id='"+whereField+"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryOrgStdGetQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select co.geopl_id,go.org_std_cd, go.org_std_nm,co.cntry_full_nm, co.cntry_sht_nm, to_char(co.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(co.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_org_std go inner join cntry_org_std co on go.org_std_cd = co.org_std_cd where co.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
		return geoPostQuery;
	}



	public String countryUomTypeGraphQLQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = " select gu.GEOPL_ID,gu.UOM_TYPE_CD,ru.UOM_TYPE_CD,ru.UOM_TYPE_NM,ru.UOM_TYPE_DESC, to_char(gu.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(gu.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_uom gu inner join ref_uom_type ru on gu.uom_type_cd = ru.uom_type_cd where gu.GEOPL_ID='"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
		return geoPostQuery;
	}

	public String countryLocaleGraphQLQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select lo.geopl_id,lo.language_cd, lo.LOCL_CD, lo.SCRIPT_CD, to_char(lo.CLDR_VERS_DT,'YYYY-MM-DD') \"CLDR_VERS_DT\", lo.cldr_vers_nbr, lo.dt_full_formt_desc, lo.dt_long_formt_desc, lo.dt_med_formt_desc, lo.dt_sht_formt_desc, to_char(lo.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(lo.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"" +
				"from language la inner join locale lo on la.language_cd=lo.language_cd where lo.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";
		return geoPostQuery;
	}
	public String countryHolidayGraphQLQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select gh.geopl_id,gh.hday_id,hd.hday_id,hd.hday_nm,hd.hday_dt_parm_txt, to_char(gh.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(gh.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_hday gh inner join holiday hd on gh.hday_id =hd.hday_id where gh.geopl_id = '"+ whereField +"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryStateProvStdGraphQLQuery(String whereField, String whereField2, String whereField3) {
		String geoPostQuery = "select geopl_id,ORG_STD_CD, ST_PROV_CD, ST_PROV_CD, " +
				"to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from st_prov_std where geopl_id='"+whereField+"'"
				+ " and " +
				"				 ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" +
				"				 (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) ";;
		return geoPostQuery;
	}

	public String countryDepnCntryRltspGraphQLQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where depn_rltsp_id = '"+whereField+"'";
		return geoPostQuery;
	}

	public String langTrnslDowGraphQLQuery(String whereField) {
		String geoPostQuery = "select * from trnsl_dow where locl_cd='" + whereField + "'";
		return geoPostQuery;
	}

	public String langGraphQLQuery(String whereField) {
		String geoGraphQLQuery = "select * from language where LANGUAGE_CD='"+whereField+"'";
		return geoGraphQLQuery;
	}

	public String lang_LocaleCdGraphQLQuery(String whereField) {
		String geoGraphQLQuery = "select * from language where LANGUAGE_CD in (select LANGUAGE_CD from locale  where LOCL_CD ='"+whereField+"')";
		return geoGraphQLQuery;
	}

	public String lang_countryCdGraphQLQuery(String whereField) {
		String geoGraphQLQuery = "select * from language where LANGUAGE_CD in (select LANGUAGE_CD from locale  where GEOPL_ID in (select GEOPL_ID from country where CNTRY_CD='"+whereField+"'))";
		return geoGraphQLQuery;
	}




	public String langGraphQL_AllDataQuery() {
		String geoGraphQLQuery = "select * from language";
		return geoGraphQLQuery;
	}


	public String langTrnslMonthOfYearGraphQLQuery(String whereField) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where LANGUAGE_CD='" + whereField + "'";
		return geoPostQuery;
	}

	public String langTrnslMOYGraphQLQuery(String whereField) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where locl_cd='" + whereField + "'";
		return geoPostQuery;
	}

//Address Label Put Audit query
	public String addressLabelPutNewQueryAudit(String whereField, String whereField1) {
		String geoPostQuery = "WITH CTE AS(select cal.LAST_UPDATED_BY_USER_ID, cal.GEOPL_ID, loc.LANGUAGE_CD, cal.ADDR_LINE_NBR,"
				+ "	cal.FULL_ADDR_LINE_LABEL_DESC,cal.BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG	,to_char(cal.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\""
				+ "	,to_char(cal.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\",  cal.REVISION_TYPE_CD	"
				+ "from country_address_label_au cal inner join locale_au loc on cal.geopl_id= loc.geopl_id	"
				+ "where cal.geopl_id='"+whereField+"' and  cal.LOCL_CD ='"+whereField1+"'   ORDER BY cal.LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1' ";

		return geoPostQuery;
	}


	/*** Address Label Post Query  *****/
	public String addressLabelPostQuery(String whereField, String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select CREATED_BY_USER_ID,GEOPL_ID, LOCL_CD, ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC,"
				+ "BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG,"
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"  "
				+ "from country_address_label where geopl_id='"+whereField+"' and  LOCL_CD ='"+whereField1+"'";
		return geoPostQuery;
	}
	//newly added
	public String getLocaleCodeQuery(String whereField, String whereField1) {
		String geoPostQuery = "select * from locale where geopl_id='" + whereField + "' and  LANGUAGE_CD ='" + whereField1 + "'";
		return geoPostQuery;
	}

	public String addressLabelPostNewQueryAudit(String whereField, String whereField1) {
		String geoPostQuery = "WITH CTE AS(select cal.CREATED_BY_USER_ID, cal.GEOPL_ID, loc.LANGUAGE_CD, cal.ADDR_LINE_NBR,"
				+ "	cal.FULL_ADDR_LINE_LABEL_DESC,cal.BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG	,to_char(cal.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\""
				+ "	,to_char(cal.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\",  cal.REVISION_TYPE_CD	"
				+ "from country_address_label_au cal inner join locale_au loc on cal.geopl_id= loc.geopl_id	"
				+ "where cal.geopl_id='"+whereField+"' and  cal.LOCL_CD ='"+whereField1+"'   ORDER BY cal.LAST_UPDATED_TMSTP DESC) Select * from CTE where rownum ='1' ";

		return geoPostQuery;
	}

	public String addressLabelPutNewQuery(String whereField, String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select cal.LAST_UPDATED_BY_USER_ID, cal.GEOPL_ID, loc.LANGUAGE_CD, cal.ADDR_LINE_NBR,"
				+ "	cal.FULL_ADDR_LINE_LABEL_DESC,cal.BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG	,to_char(cal.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\""
				+ "	,to_char(cal.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"	"
				+ "from country_address_label cal inner join locale loc on cal.geopl_id= loc.geopl_id	"
				+ "where cal.geopl_id='"+whereField+"' and  cal.LOCL_CD ='"+whereField1+"'";
		return geoPostQuery;
	}


	public String addressLabelPostNewQuery(String whereField, String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select cal.CREATED_BY_USER_ID, cal.GEOPL_ID, loc.LANGUAGE_CD, cal.ADDR_LINE_NBR,"
				+ "	cal.FULL_ADDR_LINE_LABEL_DESC,cal.BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG	,to_char(cal.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\""
				+ "	,to_char(cal.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"	"
				+ "from country_address_label cal inner join locale loc on cal.geopl_id= loc.geopl_id	"
				+ "where cal.geopl_id='"+whereField+"' and  cal.LOCL_CD ='"+whereField1+"'";
		return geoPostQuery;
	}

	/*** Address Label Post Query  to get country code from country table*****/
	public String addressLabelPost_GetCntryCodeQuery(String whereField) {
		String geoPostQuery = "select CNTRY_CD from country where GEOPL_ID   ='"+whereField+"'";
		return geoPostQuery;
	}

	/*** Address Label Put Query  *****/
	public String addressLabelPutQuery(String whereField, String whereField1, String whereField2,  String whereField3) {
		String geoPostQuery = "select LAST_UPDATED_BY_USER_ID, GEOPL_ID, LOCL_CD, ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC,"
				+ "BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG,"
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"  from country_address_label "
				+ "where geopl_id='"+whereField+"' and  LOCL_CD ='"+whereField1+"'";
		return geoPostQuery;
	}


	public String addressLabelPutLangNewQuery(String whereField, String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select cal.LAST_UPDATED_BY_USER_ID, cal.GEOPL_ID, loc.LANGUAGE_CD, cal.ADDR_LINE_NBR,"
				+ "	cal.FULL_ADDR_LINE_LABEL_DESC,cal.BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG	,to_char(cal.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\""
				+ "	,to_char(cal.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"	"
				+ "from country_address_label cal inner join locale loc on cal.geopl_id= loc.geopl_id	"
				+ "where cal.geopl_id='"+whereField+"' and  cal.LOCL_CD ='"+whereField1+"'  and loc.LANGUAGE_CD='"+whereField2+"'";
		return geoPostQuery;
	}






	/*** Address Label JMS Query ***/
	public String addressLabelJMSQuery(String whereField, String whereField1, String whereField2) {
		String geoPostQuery = "select GEOPL_ID, LOCL_CD, ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC,"
				+ "BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG,"
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"  from country_address_label where geopl_id='"+whereField+"'";
		return geoPostQuery;
	}

	/*** Address Label JMS Query ***/
	public String addressLabelJMSQuery(String whereField, String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select GEOPL_ID, LOCL_CD, ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC,"
				+ "BRAND_ADDR_LINE_LABEL_DESC, APPL_FLG,"
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"  "
				+ "from country_address_label where geopl_id='"+whereField+"'  and  LOCL_CD ='"+whereField1+"'";
		return geoPostQuery;
	}


  /***  Address Label Audit  Post Query ***/
		public String addressLabelPostQueryAudit(String whereField) {
			String geoPostQuery = "WITH CTE AS(select CREATED_BY_USER_ID,GEOPL_ID, LOCL_CD,"
					+ " ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC, BRAND_ADDR_LINE_LABEL_DESC,"
					+ " APPL_FLG, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
					+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", REVISION_TYPE_CD"
					+ " from country_address_label_au "
					+ " where GEOPL_ID='" + whereField + "'"+" ORDER BY LAST_UPDATED_TMSTP DESC)" +
					" Select * from CTE where rownum ='1'";
			return geoPostQuery;
		}

		/***  Address Label Audit  Put Query ***/
		public String addressLabelPutQueryAudit(String whereField) {
			String geoPostQuery = "WITH CTE AS(select LAST_UPDATED_BY_USER_ID,GEOPL_ID, LOCL_CD,"
					+ " ADDR_LINE_NBR,FULL_ADDR_LINE_LABEL_DESC, BRAND_ADDR_LINE_LABEL_DESC,"
					+ " APPL_FLG, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", "
					+ "to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", REVISION_TYPE_CD"
					+ " from country_address_label_au "
					+ " where GEOPL_ID='" + whereField + "'"+" ORDER BY LAST_UPDATED_TMSTP DESC)" +
					" Select * from CTE where rownum ='1'";
			return geoPostQuery;
		}

		/*** Address Label Get Query **/

		public String AddresslabelQuery(String whereField) {
			String geoPostQuery = "select * from country_address_label where GEOPL_ID='" + whereField + "'";
			return geoPostQuery;
		}

		public String AddresslabelQueryParameter(String whereField, String whereField1,String whereField2) {
			String geoPostQuery = "select * from country_address_label where GEOPL_ID='" + whereField + "'"+"and locl_cd='" + whereField1 + "'"+"and addr_line_nbr='" + whereField2 + "'";
			return geoPostQuery;
		}


		public String AddressLabelsGettQueryCountryAlldata(String whereField, String whereField1) {
			String AddressLabelsGettAlldataQuery = "select * from  country_address_label where GEOPL_ID = '"+whereField1+"' and locl_cd='" + whereField + "' order by full_addr_line_label_desc";
			return AddressLabelsGettAlldataQuery;
		}

		public String AddressLabelsGettQueryAlldata(String whereField) {
			String AddressLabelsGettAlldataQuery = "select * from  country_address_label where locl_cd='" + whereField + "'";
			return AddressLabelsGettAlldataQuery;
		}

		public String addressLabelGraphQLQuery() {
			String geoGetQuery = "select * from country_address_label";
			return geoGetQuery;
		}

		public String addressLabelGeoplIdGraphQLQuery(String whereField) {
			String geoGetQuery = "select * from country_address_label where GEOPL_ID = '" + whereField + "'";
			return geoGetQuery;
		}
		public String addressLabelGeoplIdWithLocaleCdGraphQLQuery(String whereField, String whereField1) {
			String geoGetQuery = "select * from country_address_label where GEOPL_ID = '" + whereField + "' and LOCL_CD = '" + whereField1 + "'";
			return geoGetQuery;
		}

		public String addressLabelGeoplIdWithLocaleCd_AddressLinenumberGraphQLQuery(String whereField, String whereField1, String whereField2) {
			String geoGetQuery = "select * from country_address_label where GEOPL_ID = '" + whereField + "' and LOCL_CD = '" + whereField1 + "' "
					+ " and ADDR_LINE_NBR =  '" + whereField2 + "' ";
			return geoGetQuery;
		}

		public String addressLabelTargetEndDateGraphQLQuery(String targetDate, String endDate) {
			String geoGetQuery = "select * from  country_address_label where (EFFECTIVE_DT BETWEEN '"+targetDate+"' and '"+endDate+"') or (EXPIRATION_DT >= '"+targetDate+"' and EFFECTIVE_DT <= '"+endDate+"')";
			return geoGetQuery;
		}


}
