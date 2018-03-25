package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseAccountsApply <M extends BaseAccountsApply<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setApplyOffice(java.lang.String APPLYOFFICE) {
		set("APPLYOFFICE", APPLYOFFICE);
	}

	public java.lang.String getApplyOffice() {
		return get("APPLYOFFICE");
	}
	public void setApplyUsername(java.lang.String APPLYUSERNAME) {
		set("APPLYUSERNAME", APPLYUSERNAME);
	}

	public java.lang.String getApplyUsername() {
		return get("APPLYUSERNAME");
	}
	public void setHandleUsername(java.lang.String HANDLEUSERNAME) {
		set("HANDLEUSERNAME", HANDLEUSERNAME);
	}

	public java.lang.String getHandleUsername() {
		return get("HANDLEUSERNAME");
	}
	public void setHandleOffice(java.lang.String HANDLEOFFICE) {
		set("HANDLEOFFICE", HANDLEOFFICE);
	}

	public java.lang.String getHandleOffice() {
		return get("HANDLEOFFICE");
	}
	
	public void setState(java.lang.String STATE) {
		set("STATE", STATE);
	}

	public java.lang.String getState() {
		return get("STATE");
	}
	public void setRemarks(java.lang.String REMARKS) {
		set("REMARKS", REMARKS);
	}

	public java.lang.String getRemarks() {
		return get("REMARKS");
	}	
	
	public void setApplyTime(java.lang.String APPLYTIME) {
		set("APPLYTIME", APPLYTIME);
	}

	public java.lang.String getApplyTime() {
		return get("APPLYTIME");
	}	
	public void setHandleTime(java.lang.String HANDLETIME) {
		set("HANDLETIME", HANDLETIME);
	}

	public java.lang.String getHandleTime() {
		return get("HANDLETIME");
	}
	public void setApplyUsercode(java.lang.String APPLYUSERCODE) {
		set("APPLYUSERCODE", APPLYUSERCODE);
	}

	public java.lang.String getApplyUsercode() {
		return get("APPLYUSERCODE");
	}
	public void setHandleUsercode(java.lang.String HANDLEUSERCODE) {
		set("HANDLEUSERCODE", HANDLEUSERCODE);
	}

	public java.lang.String getHandleUsercode() {
		return get("HANDLEUSERCODE");
	}	
	public void setCancelId(java.lang.String CANCELID) {
		set("CANCELID", CANCELID);
	}

	public java.lang.String getCancelId() {
		return get("CANCELID");
	}	
	
}
