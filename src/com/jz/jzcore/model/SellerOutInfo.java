package com.jz.jzcore.model;

import com.jfinal.plugin.activerecord.Record;
import com.jz.jzcore.model.base.BaseSellerOutInfo;

/**
 * 发货信息表
 * @author 小石潭记丶
 *
 * 2017年7月28日
 */
@SuppressWarnings("serial")
public class SellerOutInfo extends BaseSellerOutInfo<SellerOutInfo>{
	public static final SellerOutInfo dao = new SellerOutInfo();
	
	/**
	 * 映射 数据库查询后映射对象
	 * @param cord
	 * @param info
	 * @return
	 */
	public SellerOutInfo mapping(Record cord , SellerOutInfo info){
		info.setRecordId(cord.getNumber("RECORDID"));
		info.setUserId(cord.getNumber("USERID"));
		info.setStoreId(cord.getNumber("STOREID"));
		info.setProductId(cord.getNumber("PRODUCTID"));
		info.setSellerId(cord.getNumber("SELLERID"));
		info.setOutNo(cord.getStr("OUTNO"));
		info.setProductName(cord.getStr("PRODUCTNAME"));
		info.setOutType(cord.getStr("OUTTYPE"));
		info.setSellerName(cord.getStr("SELLERNAME"));
		info.setPackCode(cord.getStr("PACKCODE"));
		info.setBoxCode(cord.getNumber("BOXCODE").toString());
		info.setBoxCount(cord.getNumber("BOXCOUNT"));
		info.setBottleCode(cord.getStr("BOTTLECODE"));
		info.setSpec(cord.getStr("SPEC"));
		info.setDegree(cord.getNumber("DEGREE"));
		info.setVolume(cord.getNumber("VOLUME"));
		info.setOutDate(cord.getDate("OUTDATE"));
		info.setLogisNum(cord.getStr("LOGISNUM"));
		info.setPlateNum(cord.getStr("PLATENUM"));
		info.setRecsellerCode(cord.getStr("RECSELLERCODE"));
		info.setRecsellerName(cord.getStr("RECSELLERNAME"));
		info.setUserName(cord.getStr("USERNAME"));
		info.setRecsellerArea(cord.getStr("RECSELLERAREA"));
		info.setLogisticId(cord.getNumber("LOGISTICID"));
		info.setContacts(cord.getStr("CONTACTS"));
		info.setProductType(cord.getStr("PRODUCTTYPE"));
		info.setLogisticName(cord.getStr("LOGISTICNAME"));
		info.setStatus(cord.getNumber("STATUS"));
		info.setBrandsId(cord.getNumber("BRANDSID"));
		info.setBrandsName(cord.getStr("BRANDSNAME"));
		info.setLogisticNo(cord.getStr("LOGISTICNO"));
		info.setRemark(cord.getStr("REMARK"));
		info.setBillNumber(cord.getStr("BILLNUMBER"));
		info.setWlySign(cord.getNumber("WLYSIGN"));
		return info;
	}
}
