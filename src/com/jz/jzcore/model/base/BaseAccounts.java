package com.jz.jzcore.model.base;


import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseAccounts <M extends BaseAccounts<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setAccountsUserId(java.lang.String ACCOUNTS_USER_ID) {
		set("ACCOUNTS_USER_ID", ACCOUNTS_USER_ID);
	}

	public java.lang.String getAccountsUserId() {
		return get("ACCOUNTS_USER_ID");
	}
	public void setAccountsConfirmId(java.lang.String ACCOUNTS_CONFIRM_ID) {
		set("ACCOUNTS_CONFIRM_ID", ACCOUNTS_CONFIRM_ID);
	}

	public java.lang.String getAccountsConfirmId() {
		return get("ACCOUNTS_CONFIRM_ID");
	}
	public void setAccountsConfirmDistrbutor(java.lang.String ACCOUNTS_CONFIRM_DISTRIBUTOR_I) {
		set("ACCOUNTS_CONFIRM_DISTRIBUTOR_I", ACCOUNTS_CONFIRM_DISTRIBUTOR_I);
	}

	public java.lang.String getAccountsConfirmDistrbutor() {
		return get("ACCOUNTS_CONFIRM_DISTRIBUTOR_I");
	}
	public void setAccountsDistrbutorId(java.lang.String ACCOUNTS_DISTRIBUTOR_ID) {
		set("ACCOUNTS_DISTRIBUTOR_ID", ACCOUNTS_DISTRIBUTOR_ID);
	}

	public java.lang.String getAccountsDistrbutorId() {
		return get("ACCOUNTS_DISTRIBUTOR_ID");
	}
	public void setAccountsTime(java.util.Date ACCOUNTS_TIME) {
		set("ACCOUNTS_TIME", ACCOUNTS_TIME);
	}

	public java.util.Date getAccountsTime() {
		//String[] a = Timestamp.valueOf(get("ACCOUNTS_TIME").toString()).toString().split("\\.");
		return get("ACCOUNTS_TIME");
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
	
	public void setActivityId(java.lang.String ACTIVITY_ID) {
		set("ACTIVITY_ID", ACTIVITY_ID);
	}

	public java.lang.String getActivityId() {
		return get("ACTIVITY_ID");
	}	
	public void setPrizeId(java.lang.String PRIZE_ID) {
		set("PRIZE_ID", PRIZE_ID);
	}

	public java.lang.String getPrizeId() {
		return get("PRIZE_ID");
	}
	public void setPrizeName(java.lang.String PRIZE_NAME) {
		set("PRIZE_NAME", PRIZE_NAME);
	}

	public java.lang.String getPrizeName() {
		return get("PRIZE_NAME");
	}
	public void setPrizeNumber(java.lang.String CANCEL_NUMBER) {
		set("CANCEL_NUMBER", CANCEL_NUMBER);
	}

	public java.lang.String getPrizeNumber() {
		return get("CANCEL_NUMBER");
	}	
	public void setCancelId(java.lang.String CANCEL_ID) {
		set("CANCEL_ID", CANCEL_ID);
	}

	public java.lang.String getCancelId() {
		return get("CANCEL_ID");
	}	
	public void setAccountsSubordinate(java.lang.String ACCOUNTS_SUBORDINATE) {
		set("ACCOUNTS_SUBORDINATE", ACCOUNTS_SUBORDINATE);
	}

	public java.lang.String getAccountsSubordinate() {
		return get("ACCOUNTS_SUBORDINATE");
	}
	public void setSpareOne(java.lang.String SPARE_ONE) {
		set("SPARE_ONE", SPARE_ONE);
	}

	public java.lang.String getSpareOne() {
		return get("SPARE_ONE");
	}
	public void setSpareTwo(java.lang.String SPARE_TWO) {
		set("SPARE_TWO", SPARE_TWO);
	}

	public java.lang.String getSpareTwo() {
		return get("SPARE_TWO");
	}
	
	public void setSelectTime(java.lang.String SELECTTIME) {
		set("SELECTTIME", SELECTTIME);
	}

	public java.lang.String getSelectTime() {
		return get("SELECTTIME");
	}
	
	public void setSalesman(java.lang.String SALESMAN) {
		set("SALESMAN", SALESMAN);
	}

	public java.lang.String getSalesman() {
		return get("SALESMAN");
	}
	
	public void setSalesname(java.lang.String SALESNAME) {
		set("SALESNAME", SALESNAME);
	}

	public java.lang.String getSalesname() {
		return get("SALESNAME");
	}
}
