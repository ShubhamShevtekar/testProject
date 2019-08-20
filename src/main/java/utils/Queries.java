package utils;

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
	
	public String geoOrgStdGetQuery() {
		String geoGettQuery = "select * from geopl_org_std";		
		return geoGettQuery;
	}
	
	public String depnCntryRltspPostQuery(String whereField) {
		String geoPostQuery = "select * from depn_cntry_rltsp where DEPN_RLTSP_DESC='" + whereField + "'";		
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
	
	public String scriptPostQuery(String whereField) {
		String geoPostQuery = "select * from ref_script where SCRIPT_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String scriptGetQuery() {
		String geoPostQuery = "select * from ref_script";		
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
	
	public String countryOrgStdPostQuery(String cntryCode, String cntryShName, String cntryFullName, String orgStdCode)
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
				+ CNTRY_SHT_NM+CNTRY_FULL_NM+" and ORG_STD_CD = '"+orgStdCode+"'";		
		return geoPostQuery;
	}
	
	public String geopRltspPostQuery(String whereField1, String whereField2 , String whereField3) {
		String geoPostQuery = "select GEOPL_COMPT_ID, RELTD_GEOPL_COMPT_ID, GEOPL_RLTSP_TYPE_CD,  "
				+ "to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\", " 
				+"CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from geopl_rltsp where GEOPL_COMPT_ID='" + whereField1 + "' and RELTD_GEOPL_COMPT_ID = '" + whereField2 + "' "
				+"and EFFECTIVE_DT ='"+whereField3 +"'";		
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
		
	public String stProvStdPostQuery(String whereField1,String whereField2) {
		String geoPostQuery = "select GEOPL_ID, ORG_STD_CD, ST_PROV_CD, ST_PROV_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\","
				+ " CREATED_BY_USER_ID, LAST_UPDATED_BY_USER_ID from st_prov_std where ST_PROV_CD='" + whereField1 + "'"
				+" and EFFECTIVE_DT='"+ whereField2 +"'";		
		return geoPostQuery;
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
//		return geoPostQuery;
//	}
	
	public String stProvStdWithStProvCdOrgStdCdTargetEndDatesGetQuery(String whereField1, String whereField2 , String whereField3, String whereField4) {
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
	
	public String langPostQuery(String whereField) {
		String geoPostQuery = "select * from language  where LANGUAGE_CD='" + whereField + "'";		
		return geoPostQuery;
	}
	
	public String langTrnslDowPostQuery(String whereField1, String whereField2) {
		String geoPostQuery = "select * from trnsl_dow where LANGUAGE_CD='" + whereField1 + "' and DAY_OF_WEEK_NBR='" + whereField2 + "'";		
		return geoPostQuery;
	}
	
	public String langTrnslMonthOfYearPostQuery(String whereField1, String whereField2) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where LANGUAGE_CD='" + whereField1 + "' and MONTH_OF_YEAR_NBR='" + whereField2 + "'";		
		return geoPostQuery;
	}
	
	public String langGetQuery() {
		String geoPostQuery = "select * from language";		
		return geoPostQuery;
	}
	
	public String langTrnslDowGetQuery(String whereField) {
		String geoPostQuery = "select * from trnsl_dow where LANGUAGE_CD='" + whereField + "' order by DAY_OF_WEEK_NBR";		
		return geoPostQuery;
	}
	
	public String langTrnslMonthOfYearGetQuery(String whereField) {
		String geoPostQuery = "select * from TRNSL_MTH_OF_YR  where LANGUAGE_CD='" + whereField + "' order by month_of_year_nbr";		
		return geoPostQuery;
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

	public String cntryCountryDialingsPostQuery(String whereField1,String whereField2,String whereField3,String whereField4) {
		String geoPostQuery = "SELECT GEOPL_ID" +
				"        ,INTL_DIAL_PREFX_CD" +
				"        ,INTL_DIAL_CD" +
				"        ,LAND_PH_MAX_LTH_NBR" +
				"        ,LAND_PH_MIN_LTH_NBR" +				
				"        ,MOBL_PH_MAX_LTH_NBR" +
				"        ,MOBL_PH_MIN_LTH_NBR" +
				"  ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				"  ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" 		  FROM CNTRY_DIAL" +
				" 		  WHERE GEOPL_ID = '"+whereField1+"' " +	
				"         AND INTL_DIAL_CD = '"+whereField2+"' " +	
				"         AND INTL_DIAL_PREFX_CD = '"+whereField3+"' "+
		        "         AND EFFECTIVE_DT  = '"+whereField4+"'";
		
		return geoPostQuery;
	}
	
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
	
	public String cntryGeopoliticalUOMPostQuery(String whereField1,String whereField2) {
		
			
			String geoPostQuery = "Select gpum.GEOPL_ID " +
					" ,rumtyp.UOM_TYPE_CD " +		
					" ,To_Char( gpum.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
					" ,To_Char( gpum.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
					" FROM GEOPL_UOM gpum " +
					" LEFT JOIN REF_UOM_TYPE rumtyp " +
					" on gpum.UOM_TYPE_CD = rumtyp.UOM_TYPE_CD " +
					" WHERE  gpum.GEOPL_ID  = '"+whereField1+"' " +	
					" AND gpum.EFFECTIVE_DT = '"+whereField2+"' ";
			
			
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
	
	public String cuntryGeopoliticalAffiliationsPostQuery(String whereField1,String whereField2) {
		
		
		String geoPostQuery = "Select gaff.GEOPL_ID," +
				"gafftyp.AFFIL_TYPE_CD " +				
				" ,To_Char( gaff.EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( gaff.EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" From GEOPL_AFFIL gaff " +
				" LEFT JOIN  GEOPL_AFFIL_TYPE gafftyp " +
				" on gafftyp.AFFIL_TYPE_ID= gaff.AFFIL_TYPE_ID " +
				" Where gaff.GEOPL_ID = '"+whereField1+"' " +	
				" AND gaff.EFFECTIVE_DT = '"+whereField2+"' ";
		
		
		return geoPostQuery;
	}
	
		
	public String cntryLocalesPostQuery(String whereField1,String whereField2,String whereField3,String whereField4) {
			
			
			String geoPostQuery = "Select GEOPL_ID " +
					" ,LANGUAGE_CD " +
					" ,LOCLE_CD " +
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
					" And LOCLE_CD = '"+whereField2+"' " +
					" And EFFECTIVE_DT = '"+whereField3+"' ";
					
			
			return geoPostQuery;
		}
	
	

	public String cntryTranslationGeopoliticalsPostQuery(String whereField1,String whereField2,String whereField3) {
		
		
		String geoPostQuery = "Select GEOPL_ID " +
				" ,LANGUAGE_CD " +
				" ,SCRIPT_CD " +
				" ,TRNSL_NM " +
				" ,VERS_NBR " +			
				" ,To_Char( VERS_DT ,'yyyy-mm-dd') as VERS_DT " +
				" ,To_Char( EFFECTIVE_DT ,'yyyy-mm-dd') as EFFECTIVE_DT " +
				" ,To_Char( EXPIRATION_DT ,'yyyy-mm-dd') as EXPIRATION_DT " +
				" From TRNSL_GEOPL " +
				" Where GEOPL_ID = '"+whereField1+"' " +	
				" And LANGUAGE_CD = '"+whereField2+"' " +
				" And EFFECTIVE_DT = '"+whereField3+"' ";
		
		
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



	
	public String countryGetQuery(String whereField1, String whereField2, String whereField3) {
		String geoPostQuery = "select GEOPL_ID, CNTRY_NUM_CD, CNTRY_CD, THREE_CHAR_CNTRY_CD, INDPT_FLG, DEPN_RLTSP_ID, DEPN_CNTRY_CD, PSTL_FORMT_DESC, PSTL_FLG, PSTL_LTH_NBR, FIRST_WORK_WK_DAY_NM, LAST_WORK_WK_DAY_NM, " + 
				" WKEND_FIRST_DAY_NM, INET_DOMN_NM, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from country where geopl_id='"+ whereField1 +"' and " + 
				" ((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))) and " + 
				" GEOPL_ID in (select GEOPL_ID from geopl_uom where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from geopl_hday where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from geopl_affil where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from trnsl_geopl where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from cntry_org_std where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from locale where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from CNTRY_DIAL where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and " + 
				" GEOPL_ID in (select GEOPL_ID from currency where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD')))))) and" + 
				" GEOPL_ID in (select GEOPL_ID from geopolitical where (((effective_dt BETWEEN to_date('"+ whereField2 +"','YYYY-MM-DD') AND to_date('"+ whereField3 +"','YYYY-MM-DD')) or (expiration_dt >= to_date('"+ whereField2 +"','YYYY-MM-DD') and" + 
				" (effective_dt <=to_date('"+ whereField3 +"','YYYY-MM-DD'))))))";		
		return geoPostQuery;
	}
	
	public String countryGeoTypeNameGetQuery(String whereField) {
		String geoPostQuery = "select * from geopl_type where geopl_Type_id in (select GEOPL_TYPE_ID from geopolitical where geopl_id='"+ whereField +"')";
		return geoPostQuery;
	}
	
	public String countryUomTypeGetQuery(String whereField) {
		String geoPostQuery = " select UOM_TYPE_CD, to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_uom where GEOPL_ID='"+ whereField +"'";
		return geoPostQuery;
	}
	
	public String countryHolidayGetQuery(String whereField) {
		String geoPostQuery = "select hd.HDAY_NM, to_char(gh.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(gh.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_hday gh inner join holiday hd on gh.hday_id =hd.hday_id where gh.geopl_id = '"+ whereField +"'";
		return geoPostQuery;
	}
	
	public String countryAffilTypeGetQuery(String whereField) {
		String geoPostQuery = "select aft.affil_type_nm, to_char(af.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(af.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_affil af inner join geopl_affil_type aft on af.AFFIL_TYPE_ID =aft.AFFIL_TYPE_ID where af.geopl_id = '"+ whereField +"'";
		return geoPostQuery;
	}
	
	public String countryTrnslGeoplGetQuery(String whereField) {
		String geoPostQuery = "select la.engl_language_nm, tr.SCRIPT_CD, tr.trnsl_nm, to_char(tr.VERS_DT,'YYYY-MM-DD') \"VERS_DT\", tr.vers_nbr, to_char(tr.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(tr.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from language la inner join trnsl_geopl tr on la.language_cd=tr.language_cd where tr.geopl_id = '"+ whereField +"'";
		return geoPostQuery;
	}
	
	public String countryOrgStdGetQuery(String whereField) {
		String geoPostQuery = "select go.org_std_nm, co.cntry_full_nm, co.cntry_sht_nm, to_char(co.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(co.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from geopl_org_std go inner join cntry_org_std co on go.org_std_cd = co.org_std_cd where co.geopl_id = '"+ whereField +"'";
		return geoPostQuery;
	}
	
	public String countryLocaleGetQuery(String whereField) {
		String geoPostQuery = "select la.engl_language_nm, lo.locle_cd, lo.SCRIPT_CD, to_char(lo.CLDR_VERS_DT,'YYYY-MM-DD') \"CLDR_VERS_DT\", lo.cldr_vers_nbr, lo.dt_full_formt_desc, lo.dt_long_formt_desc, lo.dt_med_formt_desc, lo.dt_sht_formt_desc, to_char(lo.EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(lo.EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"" + 
				"from language la inner join locale lo on la.language_cd=lo.language_cd where lo.geopl_id = '"+ whereField +"'";
		return geoPostQuery;
	}
	public String countryDialGetQuery(String whereField) {
		String geoPostQuery = "select INTL_DIAL_PREFX_CD, INTL_DIAL_CD, MOBL_PH_MIN_LTH_NBR, MOBL_PH_MAX_LTH_NBR, LAND_PH_MIN_LTH_NBR, LAND_PH_MAX_LTH_NBR, " + 
				"to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\"from cntry_dial where geopl_id='"+whereField+"'";
		return geoPostQuery;
	}
	public String countryCurrencyGetQuery(String whereField) {
		String geoPostQuery = "select CURR_NUM_CD, CURR_CD, MINOR_UNIT_CD, MONEY_FORMT_DESC, " + 
				"to_char(EFFECTIVE_DT,'YYYY-MM-DD') \"EFFECTIVE_DT\", to_char(EXPIRATION_DT,'YYYY-MM-DD') \"EXPIRATION_DT\" from currency where geopl_id='"+whereField+"'";
		return geoPostQuery;
	}

}
