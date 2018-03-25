package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseTurntableRaffle <M extends BaseTurntableRaffle<M>> extends Model<M> implements IBean {
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//奖品名称
	public void setPrizeName(java.lang.String PRIZE_NAME) {
		set("PRIZE_NAME", PRIZE_NAME);
	}
	public java.lang.String getPrizeName() {
		return get("PRIZE_NAME");
	}
	//奖品总数
	public void setPrizeNumber(java.lang.Integer PRIZE_NUMBER) {
		set("PRIZE_NUMBER", PRIZE_NUMBER);
	}

	public java.lang.Integer getPrizeNumber() {
		Object s = get("PRIZE_NUMBER");
		return Integer.parseInt(s.toString());
	}
	//剩余数量
	public void setSurplusNumber(java.lang.Integer SURPLUS_NUMBER) {
		set("SURPLUS_NUMBER", SURPLUS_NUMBER);
	}

	public java.lang.Integer getSurplusNumber() {
		Object s = get("SURPLUS_NUMBER");
		return Integer.parseInt(s.toString());
	}
	//奖品类型
	public void setPrizeType(java.lang.String PRIZE_TYPE) {
		set("PRIZE_TYPE", PRIZE_TYPE);
	}
	public java.lang.String getPrizeType() {
		return get("PRIZE_TYPE");
	}
	//获取数量
	public void setWinningNumber(java.lang.Integer WINNING_NUMBER) {
		set("WINNING_NUMBER", WINNING_NUMBER);
	}

	public java.lang.Integer getWinningNumber() {
		Object s = get("WINNING_NUMBER");
		return Integer.parseInt(s.toString());
	}
	//获奖概率
	public void setWinningProbability(java.lang.String WINNING_PROBABILITY) {
		set("WINNING_PROBABILITY", WINNING_PROBABILITY);
	}
	public java.lang.String getWinningProbability() {
		return get("WINNING_PROBABILITY");
	}
	//微信卡券ID
	public void setWxCardId(java.lang.String WX_CARD_ID) {
		set("WX_CARD_ID", WX_CARD_ID);
	}
	public java.lang.String getWxCardId() {
		return get("WX_CARD_ID");
	}
	//创建时间
	public void setCreateTime(java.util.Date create_Time) {
		set("create_Time", create_Time);
	}

	public java.util.Date getCreateTime() {
		return get("create_Time");
	}
	//所属运单
	public void setActivityId(java.lang.String ACTIVITY_ID) {
		set("ACTIVITY_ID", ACTIVITY_ID);
	}
	public java.lang.String getActivityId() {
		return get("ACTIVITY_ID");
	}
	//当前中奖概率
	public void setNewProbability(java.lang.String NEWPROBABILITY) {
		set("NEWPROBABILITY", NEWPROBABILITY);
	}
	public java.lang.String getNewProbability() {
		return get("NEWPROBABILITY");
	}
	
}
