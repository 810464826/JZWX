package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseProvincesCount<M extends BaseProvincesCount<M>> extends Model<M> implements IBean {
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//名称
	public void setUserName(java.lang.String USER_NAME) {
		set("USER_NAME",USER_NAME);
	}
	public java.lang.String getUserName() {
		return get("USER_NAME");
	}
	//国际
	public String getCountry() {
		return get("COUNTRY");
	}

	public void setCountry(String country) {
		set("COUNTRY",country);
	}
	//扫码次数
	public String getScanNumber() {
		return get("SCAN_NUMBER");
	}

	public void setScanNumber(String SCAN_NUMBER) {
		set("SCAN_NUMBER",SCAN_NUMBER);
	}
	//扫码时间 只要年月
	public String getScanTime() {
		return get("SCAN_TIME");
	}

	public void setScanTime(String SCAN_TIME) {
		set("SCAN_TIME",SCAN_TIME);
	}
	//opneid
	public String getOpenid() {
		return get("OPENID");
	}

	public void setOpenid(String openid) {
		set("OPENID",openid);
	}
	public void setActivityPrizesName(java.lang.String ACTIVITY__PRIZES_NAME) {
		set("ACTIVITY_PRIZES_NAME", ACTIVITY__PRIZES_NAME);
	}

	public java.lang.String getActivityPrizesName() {
		return get("ACTIVITY_PRIZES_NAME");
	}
	public void setActivityPrizesAddress(java.lang.String ACTIVITY_PRIZES_ADDRESS) {
		set("ACTIVITY_PRIZES_ADDRESS", ACTIVITY_PRIZES_ADDRESS);
	}

	public java.lang.String getActivityPrizesAddress() {
		return get("ACTIVITY_PRIZES_ADDRESS");
	}
	public void setActivityPrizesId(java.lang.String ACTIVITY_PRIZES_ID) {
		set("ACTIVITY_PRIZES_ID", ACTIVITY_PRIZES_ID);
	}

	public java.lang.String getActivityPrizesId() {
		return get("ACTIVITY_PRIZES_ID");
	}
	public void setActivityId(java.lang.String ACTIVITY_ID ) {
		set("ACTIVITY_ID ", ACTIVITY_ID );
	}

	public java.lang.String getActivityId() {
		return get("ACTIVITY_ID ");
	}
	public void setOutProvinces(java.lang.String OUT_PROVINCES ) {
		set("OUT_PROVINCES ", OUT_PROVINCES );
	}

	public java.lang.String getOutProvinces() {
		return get("OUT_PROVINCES ");
	}
	public void setCodeId(java.lang.String CODEID ) {
		set("CODEID ", CODEID );
	}

	public java.lang.String getCodeId() {
		return get("CODEID ");
	}
	//省
	public String getProvince() {
		return get("PROVINCE");
	}

	public void setProvince(String province) {
		set("PROVINCE",province);
	}
	//城市
	public String getCity() {
		return get("CITY");
	}

	public void setCity(String city) {
		set("CITY",city);
	}
	//区
	public String getDistrict() {
		return get("DISTRICT");
	}

	public void setDistrict(String DISTRICT) {
		set("DISTRICT",DISTRICT);
	}
	//街道
	public String getRoad() {
		return get("ROAD");
	}

	public void setRoad(String ROAD) {
		set("ROAD",ROAD);
	}
	//经度
	public String getLongttude() {
		return get("LONGITUDE");
	}

	public void setLongttude(String LONGITUDE) {
		set("LONGITUDE",LONGITUDE);
	}
	//纬度
	public String getLatiiude() {
		return get("LATITUDE");
	}

	public void setLatiiude(String LATITUDE) {
		set("LATITUDE",LATITUDE);
	}
}
