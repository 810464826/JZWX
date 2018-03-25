package com.jz.jzcore.model.base;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * action：会员积分规则Base
 * author：tanghaobo
 * */
@SuppressWarnings("serial")
public class BaseIntegralRule<M extends BaseIntegralRule<M>> extends Model<M> implements IBean{
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	
	public java.lang.String getNumber() {
		return get("NUMBER");
	}

	public void setNumber(java.lang.String number) {
		set("NUMBER", number);
	}
	
	public java.lang.String getName() {
		return get("NAME");
	}

	public void setName(java.lang.String name) {
		set("NAME", name);
	}
	
	public java.lang.String getValue() {
		return get("VALUE");
	}

	public void setValue(java.lang.String value) {
		set("VALUE", value);
	}
	
	public java.util.Date getCreateTime() {
		return get("CREATETIME");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("CREATETIME", createTime);
	}
	
	public java.util.Date getUpdateTime() {
		return get("UPDATETIME");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("UPDATETIME", updateTime);
	}
}
