package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseActivityReport <M extends BaseActivityReport<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	
	public void setActivityName(java.lang.String ACTIVITY_NAME) {
		set("ACTIVITY_NAME", ACTIVITY_NAME);
	}

	public java.lang.String getActivityName() {
		return get("ACTIVITY_NAME");
	}
	
	public void setActivityAddress(java.lang.String ACTIVITY_ADDRESS) {
		set("ACTIVITY_ADDRESS", ACTIVITY_ADDRESS);
	}

	public java.lang.String getActivityAddress() {
		return get("ACTIVITY_ADDRESS");
	}
	
	//创建时间
	public void setCreateTime(java.util.Date create_Time) {
		set("create_Time", create_Time);
	}

	public java.util.Date getCreateTime() {
		return get("create_Time");
	}
	
	//修改时间
	public void setUpdateTime(java.util.Date UPDATE_TIME) {
		set("UPDATE_TIME", UPDATE_TIME);
	}

	public java.util.Date getUpdateTime() {
		return get("UPDATE_TIME");
	}
	
	public void setApplyUser(java.lang.String APPLY_USER) {
		set("APPLY_USER", APPLY_USER);
	}

	public java.lang.String getApplyUser() {
		return get("APPLY_USER");
	}
	
	public void setExamineUser(java.lang.String EXAMINE_USER) {
		set("EXAMINE_USER", EXAMINE_USER);
	}

	public java.lang.String getExamineUser() {
		return get("EXAMINE_USER");
	}
	
	//状态
	public void setState(java.lang.Integer STATE) {
		set("STATE", STATE);
	}

	public java.lang.Integer getState() {
		Object s = get("STATE");
		return Integer.parseInt(s.toString());
	}
}
