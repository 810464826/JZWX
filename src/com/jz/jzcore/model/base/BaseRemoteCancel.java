package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseRemoteCancel<M extends BaseRemoteCancel<M>> extends Model<M> implements IBean{
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
	public java.lang.String getGoodsNumber() {
		return get("GOODS_NUMBER");
	}
	public void setGoodsNumber(java.lang.String GOODS_NUMBER) {
		set("GOODS_NUMBER", GOODS_NUMBER);
	}
	//创建时间
	public void setCreateTime(java.lang.String  CREATE_TIME) {
		set("CREATE_TIME", CREATE_TIME);
	}

	public java.lang.String  getCreateTime() {
		return get("CREATE_TIME");
	}
	//抽奖次数
	public void setNumber(java.lang.Integer CANCEL_NUMBER) {
		set("CANCEL_NUMBER", CANCEL_NUMBER);
	}

	public java.lang.Integer getNumber() {
		Object s = get("CANCEL_NUMBER");
		return Integer.parseInt(s.toString());
	}
	public void setPrizeName(java.lang.String PRIZE_NAME) {
		set("PRIZE_NAME", PRIZE_NAME);
	}

	public java.lang.String getPrizeName() {
		return get("PRIZE_NAME");
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
	
	public void setRecordId(java.lang.String RECORDID) {
		set("RECORDID", RECORDID);
	}

	public java.lang.String getRecordId(){
		return get("RECORDID");
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
