package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseProductInfo<M extends BaseProductInfo<M>> extends Model<M> implements IBean {
	//id
	public void setId(java.lang.String id) {
		set("ID", id);
	}
	public java.lang.String getId() {
		return get("ID");
	}
	//名称
	public void setName(java.lang.String NAME) {
		set("NAME", NAME);
	}
	public java.lang.String getName() {
		return get("NAME");
	}
	//产品编号
	public void setNumber(java.lang.String NUMBER) {
		set("NUMBER", NUMBER);
	}
	public java.lang.String getNumber() {
		return get("NUMBER");
	}	
	//图片
	public void setImgid(java.lang.String IMGID) {
		set("IMGID", IMGID);
	}
	public java.lang.String getImgid() {
		return get("IMGID");
	}	
	//类型
	public void setType(java.lang.String TYPE) {
		set("TYPE", TYPE);
	}
	public java.lang.String getType() {
		return get("TYPE");
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
	//价格
	public void setPrice(java.lang.Double PRICE) {
		set("PRICE", PRICE);
	}
	public java.lang.Double getPrice() {
		return get("PRICE");
	}	
	//单位
	public void setUnit(java.lang.String UNIT) {
		set("UNIT", UNIT);
	}
	public java.lang.String getUnit() {
		return get("UNIT");
	}	
	//度数
	public void setAlcoholic(java.lang.String ALCOHOLIC) {
		set("ALCOHOLIC", ALCOHOLIC);
	}
	public java.lang.String getAlcoholic() {
		return get("ALCOHOLIC");
	}	
	//规格
	public void setProductsize(java.lang.String PRODUCTSIZE) {
		set("PRODUCTSIZE", PRODUCTSIZE);
	}
	public java.lang.String getProductsize() {
		return get("PRODUCTSIZE");
	}	
	//净含量
	public void setCapacity(java.lang.String CAPACITY) {
		set("CAPACITY",CAPACITY);
	}
	public java.lang.String getCapacity() {
		return get("CAPACITY");
	}	
	//产品描述
	public void setDescription(java.lang.String DESCRIPTION) {
		set("DESCRIPTION", DESCRIPTION);
	}
	public java.lang.String getDescription() {
		return get("DESCRIPTION");
	}	
	//微信卡券ID
	public void setCardId(java.lang.String CARD_ID) {
		set("CARD_ID", CARD_ID);
	}
	public java.lang.String getCardId() {
		return get("CARD_ID");
	}
	//备注
	public void setRemark(java.lang.String REMARK) {
		set("REMARK", REMARK);
	}
	public java.lang.String getRemark() {
		return get("REMARK");
	}
	//状态
	public void setState(java.lang.Integer STATE) {
		set("STATE", STATE);
	}

	public java.lang.Integer getState() {
		Object s = get("STATE");
		return Integer.parseInt(s.toString());
	}
}
