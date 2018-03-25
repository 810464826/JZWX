package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * action：微信用户Base
 * author：tanghaobo
 * */
@SuppressWarnings("serial")
public class BaseWxUser<M extends BaseWxUser<M>> extends Model<M> implements IBean {
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//微信名称
	public String getNickname() {
		return get("NICKNAME");
	}

	public void setNickname(String nickname) {
		set("NICKNAME",nickname);
	}
	//性别
	public String getSex() {
		return get("SEX");
	}

	public void setSex(String sex) {
		set("SEX",sex);
	}
	//国际
	public String getCountry() {
		return get("COUNTRY");
	}

	public void setCountry(String country) {
		set("COUNTRY",country);
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
	//头像地址
	public String getHeadimgurl() {
		return get("HEADIMGURL");
	}

	public void setHeadimgurl(String headimgurl) {
		set("HEADIMGURL",headimgurl);
	}
	//opneid
	public String getOpenid() {
		return get("OPENID");
	}

	public void setOpenid(String openid) {
		set("OPENID",openid);
	}
	//电话号码
	public String getPhoneNumber() {
		return get("PHONENUMBER");
	}

	public void setPhoneNumber(String phoneNumber) {
		set("PHONENUMBER",phoneNumber);
	}
	//关注时间
	public java.util.Date getFollotime() {
		return get("FOLLOTIME");
	}

	public void setFollotime(java.util.Date follotime) {
		set("FOLLOTIME",follotime);
	}
}
