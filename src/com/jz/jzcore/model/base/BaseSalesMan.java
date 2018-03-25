package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseSalesMan <M extends BaseSalesMan<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setOfficeId(java.lang.String OFFICEID) {
		set("OFFICEID", OFFICEID);
	}

	public java.lang.String getOfficeId() {
		return get("OFFICEID");
	}
	
	public void setName(java.lang.String NAME) {
		set("NAME", NAME);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	
	public void setPhone(java.lang.String PHONE) {
		set("PHONE", PHONE);
	}

	public java.lang.String getPhone() {
		return get("PHONE");
	}
	public void setOfficeName(java.lang.String OFFICENAME) {
		set("OFFICENAME", OFFICENAME);
	}

	public java.lang.String getOfficeName() {
		return get("OFFICENAME");
	}
	public void setManangeOfficeId(java.lang.String MANANGEOFFICEID) {
		set("MANANGEOFFICEID", MANANGEOFFICEID);
	}

	public java.lang.String getManangeOfficeId() {
		return get("MANANGEOFFICEID");
	}
	public void setRemarks(java.lang.String REMARKS) {
		set("REMARKS", REMARKS);
	}

	public java.lang.String getRemarks() {
		return get("REMARKS");
	}	
	
	public void setManangeOfficeName(java.lang.String MANANGEOFFICENAME) {
		set("MANANGEOFFICENAME", MANANGEOFFICENAME);
	}

	public java.lang.String getManangeOfficeName() {
		return get("MANANGEOFFICENAME");
	}	
	
	public void setUserId(java.lang.String USERID) {
		set("USERID", USERID);
	}

	public java.lang.String getUserId() {
		return get("USERID");
	}	
}
