package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * 中奖记录
 * @author tanghaobo
 *
 */
@SuppressWarnings("serial")
public class BaseWinningDelivery<M extends BaseWinningDelivery<M>> extends Model<M> implements IBean {
	
	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}
	//名称
	public void setName(java.lang.String name) {
		set("NAME", name);
	}

	public java.lang.String getName() {
		return get("NAME");
	}
	//中奖方式
	public void setWinningWay(java.lang.String winning_way) {
		set("WINNING_WAY", winning_way);
	}

	public java.lang.String getWinningWay() {
		return get("WINNING_WAY");
	}

	//奖品名称
	public void setPrizeName(java.lang.String prize_name) {
		set("PRIZE_NAME", prize_name);
	}

	public java.lang.String getPrizeName() {
		return get("PRIZE_NAME");
	}
	
	//快递单号
	public void setExpressNumber(java.lang.String express_number) {
		set("EXPRESS_NUMBER", express_number);
	}

	public java.lang.String getExpressNumber() {
		return get("EXPRESS_NUMBER");
	}
	
	//快递公司
	public void setExpress(java.lang.String express) {
		set("EXPRESS", express);
	}

	public java.lang.String getExpress() {
		return get("EXPRESS");
	}
	
	//收货地址
	public void setCollectAddress(java.lang.String collect_address) {
		set("COLLECT_ADDRESS", collect_address);
	}

	public java.lang.String getCollectAddress() {
		return get("COLLECT_ADDRESS");
	}
	
	//收货人
	public void setCollectUser(java.lang.String collect_user) {
		set("COLLECT_USER", collect_user);
	}

	public java.lang.String getCollectUser() {
		return get("COLLECT_USER");
	}
	
	//收货电话
	public void setCollectPhone(java.lang.String collect_phone) {
		set("COLLECT_PHONE", collect_phone);
	}

	public java.lang.String getCollectPhone() {
		Object s = get("COLLECT_PHONE");
		return s.toString();
	}
	
	//配送状态
	public void setDistributionStatus(java.lang.String distribution_status) {
		set("DISTRIBUTION_STATUS", distribution_status);
	}

	public java.lang.String getDistributionStatus() {
		return get("DISTRIBUTION_STATUS");
	}
	
	//中奖时间
	public void setWinningTime(java.lang.String winning_time) {
		set("WINNING_TIME", winning_time);
	}

	public java.lang.String getWinningTime() {
		String[] a = get("WINNING_TIME").toString().split("\\.");
		return a[0];
	}
	//发货时间
	public void setDeliveryTime(java.lang.String delivery_time) {
		set("DELIVERY_TIME", delivery_time);
	}

	public java.lang.String getDeliveryTime() {
		String[] a = get("DELIVERY_TIME").toString().split("\\.");
		return a[0];
	}
	//微信ID
	public void setOpenid(java.lang.String openid) {
		set("OPENID", openid);
	}

	public java.lang.String getOpenid() {
		return get("OPENID");
	}
	//粮食克数
	public void setGrainGram(java.lang.String grain_gram) {
		set("GRAIN_GRAM", grain_gram);
	}

	public java.lang.String getGrainGram() {
		return get("GRAIN_GRAM");
	}
	
	//奖品状态  1已使用  2未使用
	public void setPrizeStatus(java.lang.String prize_status) {
		set("PRIZE_STATUS", prize_status);
	}

	public java.lang.String getPrizeStatus() {
		return get("PRIZE_STATUS");
	}
	
	//奖品类型  1积分奖品   2卡券奖品  3物流奖品   4卡券物流奖品
	public void setPrizeType(java.lang.String prize_type) {
		set("PRIZE_TYPE", prize_type);
	}

	public java.lang.String getPrizeType() {
		return get("PRIZE_TYPE");
	}
	//所扫码的ID
	public void setCodeId(java.lang.String CODE_ID) {
		set("CODE_ID", CODE_ID);
	}

	public java.lang.String getCodeId() {
		return get("CODE_ID");
	}
	//微信生成的唯一卡券的ID
	public void setCode(java.lang.String CODE) {
		set("CODE", CODE);
	}

	public java.lang.String getCode() {
		return get("CODE");
	}
	//卡券的ID
	public void setCardId(java.lang.String CARDID) {
		set("CARDID", CARDID);
	}

	public java.lang.String getCardId() {
		return get("CARDID");
	}
	//地址填写  0 未填写   1已填写
	public void setAddressStatus(java.lang.String ADDRESS_STATUS) {
		set("ADDRESS_STATUS", ADDRESS_STATUS);
	}

	public java.lang.String getAddressStatus() {
		return get("ADDRESS_STATUS");
	}
	//活动ID
	public void setActivityId(java.lang.String ACTIVITY_ID) {
		set("ACTIVITY_ID", ACTIVITY_ID);
	}

	public java.lang.String getActivityId() {
		return get("ACTIVITY_ID");
	}
	//活动经销商
	public void setActivityUserName(java.lang.String ACTIVITY_USERNAME) {
		set("ACTIVITY_USERNAME", ACTIVITY_USERNAME);
	}

	public java.lang.String getActivityUserName() {
		return get("ACTIVITY_USERNAME");
	}
	//活动区域
	public void setActivityArea(java.lang.String ACTIVITY_AREA) {
		set("ACTIVITY_AREA", ACTIVITY_AREA);
	}

	public java.lang.String getActivityArea() {
		return get("ACTIVITY_AREA");
	}
}
