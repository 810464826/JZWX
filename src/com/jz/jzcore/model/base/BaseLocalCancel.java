package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseLocalCancel<M extends BaseLocalCancel<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	
	public void setActivityName(java.lang.String ActivityName) {
		set("ACTIVITYNAME", ActivityName);
	}

	public java.lang.String getActivityName() {
		return get("ACTIVITYNAME");
	}
	//活动申请人的地址
	public void setActivityPrizesAddress(java.lang.String ACTIVITY_PRIZES_ADDRESS) {
		set("ACTIVITY_PRIZES_ADDRESS", ACTIVITY_PRIZES_ADDRESS);
	}

	public java.lang.String getActivityPrizesAddress() {
		return get("ACTIVITY_PRIZES_ADDRESS");
	}
	//ACTIVITY_PRIZES_ID活动申请人id
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
	public void setCancelUser(java.lang.String CANCEL_USER) {
		set("CANCEL_USER", CANCEL_USER);
	}

	public java.lang.String getCancelUser() {
		return get("CANCEL_USER");
	}
	public void setCancelAddress(java.lang.String CANCEL_ADDRESS) {
		set("CANCEL_ADDRESS", CANCEL_ADDRESS);
	}

	public java.lang.String getCancelAddress() {
		return get("CANCEL_ADDRESS");
	}
	public void setPrizeName(java.lang.String PRIZE_NAME) {
		set("PRIZE_NAME", PRIZE_NAME);
	}

	public java.lang.String getPrizeName() {
		return get("PRIZE_NAME");
	}
	public void setNumber(java.lang.String CANCEL_NUMBER) {
		set("CANCEL_NUMBER", CANCEL_NUMBER);
	}

	public java.lang.String getNumber() {
		return get("CANCEL_NUMBER");
	}
	public void setActivityPreizeOffice(java.lang.String ACTIVITY_PRIZES_OFFICE) {
		set("ACTIVITY_PRIZES_OFFICE", ACTIVITY_PRIZES_OFFICE);
	}

	public java.lang.String getActivityPreizeOffice() {
		return get("ACTIVITY_PRIZES_OFFICE");
	}
	public void setCancelOffice(java.lang.String CANCEL_OFFICE) {
		set("CANCEL_OFFICE", CANCEL_OFFICE);
	}

	public java.lang.String getCancelOffice() {
		return get("CANCEL_OFFICE");
	}
	//创建时间
	public void setCreateTime(java.lang.String  CREATE_DATE) {
		set("CREATE_DATE", CREATE_DATE);
	}

	public java.lang.String  getCreateTime() {
		return get("CREATE_DATE");
	}
	public void setCancelOpenid(java.lang.String CANCEL_OPENID) {
		set("CANCEL_OPENID", CANCEL_OPENID);
	}

	public java.lang.String getCancelOpenid() {
		return get("CANCEL_OPENID");
	}
	public void setCheck(java.lang.String CHECK_STATE) {
		set("CHECK_STATE", CHECK_STATE);
	}

	public java.lang.String getCheck(){
		return get("CHECK_STATE");
	}
	public void setCheckUser(java.lang.String CHECK_USER) {
		set("CHECK_USER", CHECK_USER);
	}

	public java.lang.String getCheckUser(){
		return get("CHECK_USER");
	}
	public void setActivityAddress(java.lang.String ACTIVITY_ADDRESS) {
		set("ACTIVITY_ADDRESS", ACTIVITY_ADDRESS);
	}

	public java.lang.String getActivityAddress(){
		return get("ACTIVITY_ADDRESS");
	}
	
	public void setCancelAddressName(java.lang.String CANCEL_ADDRESSNAME) {
		set("CANCEL_ADDRESSNAME", CANCEL_ADDRESSNAME);
	}

	public java.lang.String getCancelAddressName(){
		return get("CANCEL_ADDRESSNAME");
	}
	
	public void setCancelUserName(java.lang.String CANCELUSERNAME) {
		set("CANCELUSERNAME", CANCELUSERNAME);
	}

	public java.lang.String getCancelUserName(){
		return get("CANCELUSERNAME");
	}
	
	public void setCancelUserPhone(java.lang.String CANCELUSERPHONE) {
		set("CANCELUSERPHONE", CANCELUSERPHONE);
	}

	public java.lang.String getCancelUserPhone(){
		return get("CANCELUSERPHONE");
	}
}
