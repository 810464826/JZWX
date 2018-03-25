package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseWxCard<M extends BaseWxCard<M>> extends Model<M> implements IBean {
	// id
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}

	// 名称
	public void setName(java.lang.String name) {
		set("NAME", name);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	
	// 卡券ID
	public void setCardId(java.lang.String CARD_ID) {
		set("CARD_ID", CARD_ID);
	}

	public java.lang.String getCardId() {
		return get("CARD_ID");
	}
	//卡券类型
	public void setCardType(java.lang.String CARD_TYPE) {
		set("CARD_TYPE", CARD_TYPE);
	}

	public java.lang.String getCardType() {
		return get("CARD_TYPE");
	}
	// 备注
	public void setRemarks(java.lang.String REMARKS) {
		set("REMARKS", REMARKS);
	}

	public java.lang.String getRemarks() {
		return get("REMARKS");
	}
}
