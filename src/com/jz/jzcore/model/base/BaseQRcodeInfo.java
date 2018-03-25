package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * 二维码信息
 * @author tanghaobo
 *
 */
@SuppressWarnings("serial")
public class BaseQRcodeInfo <M extends BaseQRcodeInfo<M>> extends Model<M> implements IBean {
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	
	//二维码ID
	public void setQRcodeId(java.lang.String QRCODE_ID) {
		set("QRCODE_ID", QRCODE_ID);
	}

	public java.lang.String getQRcodeId() {
		return get("QRCODE_ID");
	}
	
	//四位验证码
	public void setVerificationCode(java.lang.String VERIFICATION_CODE) {
		set("VERIFICATION_CODE", VERIFICATION_CODE);
	}

	public java.lang.String getVerificationCode() {
		return get("VERIFICATION_CODE");
	}
	
	//产品信息
	public void setProductInfo(java.lang.String PRODUCT_INFO) {
		set("PRODUCT_INFO", PRODUCT_INFO);
	}

	public java.lang.String getProductInfo() {
		return get("PRODUCT_INFO");
	}

	
	//箱码
	public void setBoxId(java.lang.String BOX_ID) {
		set("BOX_ID", BOX_ID);
	}

	public java.lang.String getBoxId() {
		return get("BOX_ID");
	}
	
	//信息
	public void setInfo(java.lang.String INFO) {
		set("INFO", INFO);
	}

	public java.lang.String getInfo() {
		return get("INFO");
	}
	
	//创建时间
	public void setCreateTime(java.lang.String create_Time) {
		set("CREATE_TIME", create_Time);
	}

	public java.lang.String getCreateTime() {
		return get("CREATE_TIME");
	}
	
	//状态
	public void setState(java.lang.Number STATE) {
		set("STATE", STATE);
	}

	public java.lang.Number getState() {
		return get("STATE");
	}
	
	//扫码次数
	public void setQRcodeNumber(java.lang.Number QRCODE_NUMBER) {
		set("QRCODE_NUMBER", QRCODE_NUMBER);
	}

	public java.lang.Number getQRcodeNumber() {
		Object s = get("QRCODE_NUMBER");
		return Integer.parseInt(s.toString());
	}
	
	//最后获奖
	public void setFinalPrize(java.lang.String FINAL_PRIZE) {
		set("FINAL_PRIZE", FINAL_PRIZE);
	}

	public java.lang.String getFinalPrize() {
		return get("FINAL_PRIZE");
	}
	
	//发货Id
	public void setDeliverGoodsId(java.lang.String DELIVER_GOODS_ID) {
		set("DELIVER_GOODS_ID", DELIVER_GOODS_ID);
	}

	public java.lang.String getDeliverGoodsId() {
		return get("DELIVER_GOODS_ID");
	}
	
	//是否兑换
	public void setExchange(java.lang.String EXCHANGE) {
		set("EXCHANGE", EXCHANGE);
	}

	public java.lang.String getExchange() {
		return get("EXCHANGE");
	}
	//单次获奖数量
	public void setWinningNumber(java.lang.Number WINNING_NUMBER) {
		set("WINNING_NUMBER", WINNING_NUMBER);
	}

	public java.lang.Number getWinningNumber() {
		Object s = get("WINNING_NUMBER");
		return Integer.parseInt(s.toString());
	}
	//瓶码
	public void setBoxCode(java.lang.String BOXCODE) {
		set("BOXCODE", BOXCODE);
	}

	public java.lang.String getBoxCode() {
		return get("BOXCODE");
	}
	//发货记录ID
	public void setRecoredId(java.lang.String RECORDID) {
		set("RECORDID", RECORDID);
	}

	public java.lang.String getRecoredId() {
		return get("RECORDID");
	}
}
