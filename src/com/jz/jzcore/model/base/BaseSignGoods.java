package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class BaseSignGoods <M extends BaseSignGoods<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	
	public void setDriverId(java.lang.String DRIVER_ID) {
		set("DRIVER_ID", DRIVER_ID);
	}

	public java.lang.String getDriverId() {
		return get("DRIVER_ID");
	}
	
	public void setDisreibutorId(java.lang.String DISREIBUTOR_ID) {
		set("DISREIBUTOR_ID", DISREIBUTOR_ID);
	}

	public java.lang.String getDisreibutorId() {
		return get("DISREIBUTOR_ID");
	}
	
	//创建时间
	public void setCreateTime(java.lang.String create_Time) {
		set("CREATE_TIME", create_Time);
	}

	public java.lang.String getCreateTime() {
		return get("CREATE_TIME");
	}
	
	public void setActivityReportId(java.lang.String ACTIVITY_REPORT_ID) {
		set("ACTIVITY_REPORT_ID", ACTIVITY_REPORT_ID);
	}

	public java.lang.String getActivityReportId() {
		return get("ACTIVITY_REPORT_ID");
	}
	
	public void setGoodsNumber(java.lang.String GOODS_NUMBER) {
		set("GOODS_NUMBER", GOODS_NUMBER);
	}

	public java.lang.String getGoodsNumber() {
		return get("GOODS_NUMBER");
	}
	
	public void setDelalerName(java.lang.String DEALER_NAME) {
		set("DEALER_NAME", DEALER_NAME);
	}

	public java.lang.String getDelalerName() {
		return get("DEALER_NAME");
	}
	
	public void setVarieties(java.lang.String VARIETIES) {
		set("VARIETIES", VARIETIES);
	}

	public java.lang.String getVarieties() {
		return get("VARIETIES");
	}
	
	public void setTotalBox(java.lang.String TOTAL_BOX) {
		set("TOTAL_BOX", TOTAL_BOX);
	}

	public java.lang.String getTotalBox() {
		return get("TOTAL_BOX");
	}
	
	
}
