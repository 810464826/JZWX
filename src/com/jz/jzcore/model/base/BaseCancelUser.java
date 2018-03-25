package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
@SuppressWarnings("serial")
public class BaseCancelUser <M extends BaseCancelUser<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	public void setSystemUserId(java.lang.String SYS_USER_ID) {
		set("SYS_USER_ID", SYS_USER_ID);
	}

	public java.lang.String getSystemUserId() {
		return get("SYS_USER_ID");
	}
	public void setCancelLogin(java.lang.String CANCEL_LOGIN) {
		set("CANCEL_LOGIN", CANCEL_LOGIN);
	}

	public java.lang.String getCancelLogin() {
		return get("CANCEL_LOGIN");
	}
	public void setOpenid(java.lang.String OPENID) {
		set("OPENID", OPENID);
	}

	public java.lang.String getOpenid() {
		return get("OPENID");
	}
}
