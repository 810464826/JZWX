package com.jz.jzcore.model.wxbase;

public class UserCardStatus {
	private String errcode;
	private String errmsg;
	private WxCardOnCode card;
	private String openid;
	private String can_consume;
	private String user_card_status;
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public WxCardOnCode getCard() {
		return card;
	}
	public void setCard(WxCardOnCode card) {
		this.card = card;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getCan_consume() {
		return can_consume;
	}
	public void setCan_consume(String can_consume) {
		this.can_consume = can_consume;
	}
	public String getUser_card_status() {
		return user_card_status;
	}
	public void setUser_card_status(String user_card_status) {
		this.user_card_status = user_card_status;
	}
	
}
