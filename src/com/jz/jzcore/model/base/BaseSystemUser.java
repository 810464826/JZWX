package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseSystemUser <M extends BaseSystemUser<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setCompanyId(java.lang.String COMPANY_ID) {
		set("COMPANY_ID", COMPANY_ID);
	}

	public java.lang.String getCompanyId() {
		return get("COMPANY_ID");
	}
	public void setOfficeId(java.lang.String OFFICE_ID) {
		set("OFFICE_ID", OFFICE_ID);
	}

	public java.lang.String getOfficeId() {
		return get("OFFICE_ID");
	}
	public void setLoginName(java.lang.String LOGIN_NAME) {
		set("LOGIN_NAME", LOGIN_NAME);
	}

	public java.lang.String getLoginName() {
		return get("LOGIN_NAME");
	}
	public void setPassword(java.lang.String PASSWORD) {
		set("PASSWORD", PASSWORD);
	}

	public java.lang.String getPassword() {
		return get("PASSWORD");
	}
	public void setNo(java.lang.String NO) {
		set("NO", NO);
	}

	public java.lang.String getNo() {
		return get("NO");
	}
	public void setName(java.lang.String NAME) {
		set("NAME", NAME);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	public void setEmail(java.lang.String EMAIL) {
		set("EMAIL", EMAIL);
	}

	public java.lang.String getEmail() {
		Object s = get("EMAIL");
		return s.toString();
	}
	public void setPhone(java.lang.String PHONE) {
		set("PHONE", PHONE);
	}

	public java.lang.String getPhone() {
		return get("PHONE");
	}
	public void setMobile(java.lang.String MOBILE) {
		set("MOBILE", MOBILE);
	}

	public java.lang.String getMobile() {
		return get("MOBILE");
	}
	public void setUserType(java.lang.String USER_TYPE) {
		set("USER_TYPE", USER_TYPE);
	}

	public java.lang.String getUserType() {
		Object s = get("USER_TYPE");
		return s.toString();
	}	
	public void setPhoto(java.lang.String PHOTO) {
		set("PHOTO", PHOTO);
	}

	public java.lang.String getPhoto() {
		return get("PHOTO");
	}
	
	public void setOpenId(java.lang.String openId) {
		set("OPENID", openId);
	}

	public java.lang.String getOpenId() {
		return get("OPENID");
	}
}
