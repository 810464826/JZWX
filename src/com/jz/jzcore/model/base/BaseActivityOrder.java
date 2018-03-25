package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseActivityOrder <M extends BaseActivityOrder<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setActivityId(java.lang.String ACTIVITYID) {
		set("ACTIVITYID", ACTIVITYID);
	}

	public java.lang.String getActivityId() {
		return get("ACTIVITYID");
	}
	
	public void setOrderId(java.lang.String ORDERID) {
		set("ORDERID", ORDERID);
	}

	public java.lang.String getOrderId() {
		return get("ORDERID");
	}
	public void setState(java.lang.String STATE) {
		set("STATE", STATE);
	}

	public java.lang.String getState() {
		return get("STATE");
	}
	
}
