package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseSystemOffice<M extends BaseSystemOffice<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setParentId(java.lang.String PARENT_ID) {
		set("PARENT_ID", PARENT_ID);
	}

	public java.lang.String getParentId() {
		return get("PARENT_ID");
	}
	public void setParentIdS(java.lang.String PARENT_IDS) {
		set("PARENT_IDS", PARENT_IDS);
	}

	public java.lang.String getParentIdS() {
		return get("PARENT_IDS");
	}
	public void setName(java.lang.String NAME) {
		set("NAME", NAME);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	public void setAreaId(java.lang.String AREA_ID) {
		set("AREA_ID", AREA_ID);
	}

	public java.lang.String getAreaId() {
		return get("AREA_ID");
	}
	public void setAddressName(java.lang.String ADDRESSNAME) {
		set("ADDRESSNAME", ADDRESSNAME);
	}

	public java.lang.String getAddressName() {
		return get("ADDRESSNAME");
	}
}
