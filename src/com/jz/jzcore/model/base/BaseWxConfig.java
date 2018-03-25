package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * action：微信配置Base
 * author：tanghaobo
 * */
@SuppressWarnings("serial")
public class BaseWxConfig<M extends BaseWxConfig<M>> extends Model<M> implements IBean{
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//配置名称
	public void setName(java.lang.String name) {
		set("NAME", name);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	//参数
	public void setValve(java.lang.String value) {
		set("VALUE", value);
	}

	public java.lang.String getValve() {
		return get("VALUE");
	}
		//备注
	public void setRemarks(java.lang.String remarks) {
		set("REMARKS", remarks);
	}

	public java.lang.String getRemarks() {
		return get("REMARKS");
	}
	}
