package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
/**
 * 发货信息表
 * @author 小石潭记丶
 *
 * 2017年7月28日
 */
@SuppressWarnings("serial")
public class BaseSellerOutInfo<M extends BaseSellerOutInfo<M>> extends Model<M> implements IBean{
	/**
	 * 记录ID
	 * @param RECORDID
	 */
	public void setRecordId(java.lang.Number RECORDID) {
		set("RECORDID", RECORDID);
	}
	public java.lang.Number getRecordId() {
		return get("RECORDID");
	}
	/**
	 * 操作员ID
	 * @param USERID
	 */
	public void setUserId(java.lang.Number USERID) {
		set("USERID", USERID);
	}
	public java.lang.Number getUSERID() {
		return get("USERID");
	}
	/**
	 * 仓库ID
	 * @param STOREID
	 */
	public void setStoreId(java.lang.Number STOREID) {
		set("STOREID", STOREID);
	}
	public java.lang.Number getStoreId() {
		return get("STOREID");
	}	
	/**
	 * 产品ID
	 * @param PRODUCTID
	 */
	public void setProductId(java.lang.Number PRODUCTID) {
		set("PRODUCTID", PRODUCTID);
	}
	public java.lang.Number getProductId() {
		return get("PRODUCTID");
	}
	/**
	 * 经销商ID
	 * @param SELLERID
	 */
	public void setSellerId(java.lang.Number SELLERID) {
		set("SELLERID", SELLERID);
	}
	public java.lang.Number getSellerId() {
		return get("SELLERID");
	}
	/**
	 * 出库单号
	 * @param OUTNO
	 */
	public void setOutNo(java.lang.String OUTNO) {
		set("OUTNO", OUTNO);
	}
	public java.lang.String getOutNo() {
		return get("OUTNO");
	}
	/**
	 * 产品名称
	 * @param PRODUCTNAME
	 */
	public void setProductName(java.lang.String PRODUCTNAME) {
		set("PRODUCTNAME", PRODUCTNAME);
	}
	public java.lang.String getProductName() {
		return get("PRODUCTNAME");
	}
	/**
	 * 出库类型
	 * @param OUTTYPE
	 */
	public void setOutType(java.lang.String OUTTYPE) {
		set("OUTTYPE", OUTTYPE);
	}
	public java.lang.String getOutType() {
		return get("OUTTYPE");
	}
	/**
	 * 经销商名称
	 * @param SELLERNAME
	 */
	public void setSellerName(java.lang.String SELLERNAME) {
		set("SELLERNAME", SELLERNAME);
	}
	public java.lang.String getSellerName() {
		return get("SELLERNAME");
	}
	/**
	 * 盒码
	 * @param PACKCODE
	 */
	public void setPackCode(java.lang.String PACKCODE) {
		set("PACKCODE", PACKCODE);
	}
	public java.lang.String getPackCode() {
		return get("PACKCODE");
	}
	/**
	 * 箱码
	 * @param BOXCODE
	 */
	public void setBoxCode(java.lang.String BOXCODE) {
		set("BOXCODE", BOXCODE);
	}
	public java.lang.String getBoxCode() {
		return get("BOXCODE");
	}
	
	/**
	 * 箱数
	 * @param BOXCODE
	 */
	public void setBoxCount(java.lang.Number BOXCOUNT) {
		set("BOXCOUNT", BOXCOUNT);
	}
	public java.lang.Number getBoxCount() {
		return get("BOXCOUNT");
	}
	
	/**
	 * 瓶码
	 * @param BOTTLECODE
	 */
	public void setBottleCode(java.lang.String BOTTLECODE) {
		set("BOTTLECODE", BOTTLECODE);
	}
	public java.lang.String getBottleCode() {
		return get("BOTTLECODE");
	}
	/**
	 * 规格
	 * @param SPEC
	 */
	public void setSpec(java.lang.String SPEC) {
		set("SPEC", SPEC);
	}
	public java.lang.String getSpec() {
		return get("SPEC");
	}
	/**
	 * 度数
	 * @param DEGREE
	 */
	public void setDegree(java.lang.Number DEGREE) {
		set("DEGREE", DEGREE);
	}
	public java.lang.Number getDegree() {
		return get("DEGREE");
	}
	/**
	 * 容量
	 * @param VOLUME
	 */
	public void setVolume(java.lang.Number VOLUME) {
		set("VOLUME", VOLUME);
	}
	public java.lang.Number getVolume() {
		return get("VOLUME");
	}
	/**
	 * 出库时间
	 * @param OUTDATE
	 */
	public void setOutDate(java.util.Date OUTDATE) {
		set("OUTDATE", OUTDATE);
	}
	public java.util.Date getOutDate() {
		return get("OUTDATE");
	}
	/**
	 * 物流单号
	 * @param LOGISNUM
	 */
	public void setLogisNum(java.lang.String LOGISNUM) {
		set("LOGISNUM", LOGISNUM);
	}
	public java.lang.String getLogisNum() {
		return get("LOGISNUM");
	}
	/**
	 * 车牌号
	 * @param PLATENUM
	 */
	public void setPlateNum(java.lang.String PLATENUM) {
		set("PLATENUM", PLATENUM);
	}
	public java.lang.String getPlateNum() {
		return get("PLATENUM");
	}
	/**
	 * 接收商家编码
	 * @param RECSELLERCODE
	 */
	public void setRecsellerCode(java.lang.String RECSELLERCODE) {
		set("RECSELLERCODE", RECSELLERCODE);
	}
	public java.lang.String getRecsellerCode() {
		return get("RECSELLERCODE");
	}
	/**
	 * 接收商家名称
	 * @param RECSELLERNAME
	 */
	public void setRecsellerName(java.lang.String RECSELLERNAME) {
		set("RECSELLERNAME", RECSELLERNAME);
	}
	public java.lang.String getRecsellerName() {
		return get("RECSELLERNAME");
	}
	/**
	 * 操作员名称
	 * @param USERNAME
	 */
	public void setUserName(java.lang.String USERNAME) {
		set("USERNAME", USERNAME);
	}
	public java.lang.String getUserName() {
		return get("USERNAME");
	}
	/**
	 * 接收商家地址
	 * @param RECSELLERAREA
	 */
	public void setRecsellerArea(java.lang.String RECSELLERAREA) {
		set("RECSELLERAREA", RECSELLERAREA);
	}
	public java.lang.String getRecsellerArea() {
		return get("RECSELLERAREA");
	}
	/**
	 * 物流公司id
	 * @param LOGISTICID
	 */
	public void setLogisticId(java.lang.Number LOGISTICID) {
		set("LOGISTICID", LOGISTICID);
	}
	public java.lang.Number getLogisticId() {
		return get("LOGISTICID");
	}
	/**
	 * 联系人
	 * @param CONTACTS
	 */
	public void setContacts(java.lang.String CONTACTS) {
		set("CONTACTS", CONTACTS);
	}
	public java.lang.String getContacts() {
		return get("CONTACTS");
	}
	/**
	 * 类型（香型）
	 * @param PRODUCTTYPE
	 */
	public void setProductType(java.lang.String PRODUCTTYPE) {
		set("PRODUCTTYPE", PRODUCTTYPE);
	}
	public java.lang.String getProductType() {
		return get("PRODUCTTYPE");
	}
	/**
	 * 物流公司名称
	 * @param LOGISTICNAME
	 */
	public void setLogisticName(java.lang.String LOGISTICNAME) {
		set("LOGISTICNAME", LOGISTICNAME);
	}
	public java.lang.String getLogisticName() {
		return get("LOGISTICNAME");
	}
	/**
	 * 0 正常  1 作废
	 * @param STATUS
	 */
	public void setStatus(java.lang.Number STATUS) {
		set("STATUS", STATUS);
	}
	public java.lang.Number getStatus() {
		return get("STATUS");
	}
	/**
	 * 品牌ID
	 * @param BRANDSID
	 */
	public void setBrandsId(java.lang.Number BRANDSID) {
		set("BRANDSID", BRANDSID);
	}
	public java.lang.Number getBrandsId() {
		return get("BRANDSID");
	}
	/**
	 * 品牌名称
	 * @param BRANDSNAME
	 */
	public void setBrandsName(java.lang.String BRANDSNAME) {
		set("BRANDSNAME", BRANDSNAME);
	}
	public java.lang.String getBrandsName() {
		return get("BRANDSNAME");
	}
	/**
	 * 物流号
	 * @param LOGISTICNO
	 */
	public void setLogisticNo(java.lang.String LOGISTICNO) {
		set("LOGISTICNO", LOGISTICNO);
	}
	public java.lang.String getLogisticNo() {
		return get("LOGISTICNO");
	}
	/**
	 * 备注
	 * @param REMARK
	 */
	public void setRemark(java.lang.String REMARK) {
		set("REMARK", REMARK);
	}
	public java.lang.String getRemark() {
		return get("REMARK");
	}
	/**
	 * 票据号
	 * @param BILLNUMBER
	 */
	public void setBillNumber(java.lang.String BILLNUMBER) {
		set("BILLNUMBER", BILLNUMBER);
	}
	public java.lang.String getBillNumber() {
		return get("BILLNUMBER");
	}
	/**
	 * 扫码
	 * @param WLYSIGN
	 */
	public void setWlySign(java.lang.Number WLYSIGN) {
		set("WLYSIGN", WLYSIGN);
	}
	public java.lang.Number getWlySign() {
		return get("WLYSIGN");
	}
}
