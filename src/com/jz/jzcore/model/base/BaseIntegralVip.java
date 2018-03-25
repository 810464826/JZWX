package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * action：会员积分Base
 * author：tanghaobo
 * */
@SuppressWarnings("serial")
public class BaseIntegralVip<M extends BaseIntegralVip<M>> extends Model<M> implements IBean{
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//微信用户ID
	public void setOpenid(java.lang.String openid) {
		set("OPENID", openid);
	}

	public java.lang.String getOpenid() {
		return get("OPENID");
	}
	//抽奖用户
	public void setLuckyDraw(java.lang.String lucky_Draw) {
		set("LUCKY_DRAW", lucky_Draw);
	}

	public java.lang.String getLuckyDraw() {
		return get("LUCKY_DRAW");
	}
	//手机号码
	public void setPhone(java.lang.String phone) {
		set("PHONE", phone);
	}

	public java.lang.String getPhone() {
		return get("PHONE");
	}
	//小麦积分
	public void setWhrat(java.lang.Integer whrat) {
		set("WHRAT", whrat);
	}

	public java.lang.Integer getWhrat() {
		Object s = get("WHRAT");
		return Integer.parseInt(s.toString());
	}
	//大米积分
	public void setRice(java.lang.Integer rice) {
		set("RICE", rice);
	}

	public java.lang.Integer getRice() {
		Object s = get("RICE");
		return Integer.parseInt(s.toString());
	}
	//玉米积分
	public void setCorn(java.lang.Integer corn) {
		set("CORN", corn);
	}

	public java.lang.Integer getCorn() {
		Object s = get("CORN");
		return Integer.parseInt(s.toString());
	}
	//高粱积分
	public void setSorghum(java.lang.Integer sorghum) {
		set("SORGHUM", sorghum);
	}

	public java.lang.Integer getSorghum() {
		Object s = get("SORGHUM");
		return Integer.parseInt(s.toString());
	}
	
	//糯米积分
	public void setGlutinousrice(java.lang.Integer glutinousrice) {
		set("GLUTINOUSRICE", glutinousrice);
	}

	public java.lang.Integer getGlutinousrice() {
		Object s = get("GLUTINOUSRICE");
		return Integer.parseInt(s.toString());
	}
	//全部积分
	public void setAllIntegral(java.lang.Integer all_Integral) {
		set("ALL_INTEGRAL", all_Integral);
	}

	public java.lang.Integer getAllIntegral() {
		Object s = get("ALL_INTEGRAL");
		return Integer.parseInt(s.toString());
	}
	//抽奖次数
	public void setLuckyNumber(java.lang.Integer lucky_Number) {
		set("LUCKY_NUMBER", lucky_Number);
	}

	public java.lang.Integer getLuckyNumber() {
		Object s = get("LUCKY_NUMBER");
		return Integer.parseInt(s.toString());
	}
	//备注
	public void setRemarks(java.lang.String remarks) {
		set("REMARKS", remarks);
	}

	public java.lang.String getRemarks() {
		return get("REMARKS");
	}
	//创建时间
	public void setCreateTime(java.util.Date create_Time) {
		set("CREATE_TIME", create_Time);
	}

	public java.util.Date getCreateTime() {
		return get("CREATE_TIME");
	}
	//修改时间
	public void setUpdateTime(java.util.Date update_Time) {
		set("UPDATE_TIME", update_Time);
	}

	public java.util.Date getUpdateTime() {
		return get("UPDATE_TIME");
	}
	//签到
	public void setReport(java.lang.String report) {
		set("REPORT", report);
	}

	public java.lang.String getReport() {
		return get("REPORT");
	}
}
