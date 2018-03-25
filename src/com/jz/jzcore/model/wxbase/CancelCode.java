package com.jz.jzcore.model.wxbase;

public class CancelCode {
	private String errcode;
	private String errmsg;
	private CardByCancel card;
	private String openid;
	
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
	public CardByCancel getCard() {
		return card;
	}
	public void setCard(CardByCancel card) {
		this.card = card;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	
}
