package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseLogisticeFirm<M extends BaseLogisticeFirm<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setLogisticsName(java.lang.String LOGISTICS_NAME) {
		set("LOGISTICS_NAME", LOGISTICS_NAME);
	}

	public java.lang.String getLogisticsName() {
		return get("LOGISTICS_NAME");
	}
	
	public void setLogisticsCode(java.lang.String LOGISTICS_CODE) {
		set("LOGISTICS_CODE", LOGISTICS_CODE);
	}

	public java.lang.String getLogisticsCode() {
		return get("LOGISTICS_CODE");
	}
	
	
	
	
}




