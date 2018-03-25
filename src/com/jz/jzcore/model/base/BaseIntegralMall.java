package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * action：积分商城
 * author：tanghaobo
 * */
@SuppressWarnings("serial")
public class BaseIntegralMall<M extends BaseIntegralMall<M>> extends Model<M> implements IBean{
	
	private String img;
	private String PrizeName;
	
	
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
	//所需积分
	public void setEchange(java.lang.String EXCHANGE) {
		set("EXCHANGE", EXCHANGE);
	}
	public java.lang.String getEchange() {
		return get("EXCHANGE");
	}	
	//是否推荐
	public void setRecommend(java.lang.String RECOMMEND) {
		set("RECOMMEND", RECOMMEND);
	}
	public java.lang.String getRecommend() {
		return get("RECOMMEND");
	}
	//总数
	public void setTolal(java.lang.String TOTAL) {
		set("TOTAL", TOTAL);
	}
	public java.lang.String getTolal() {
		return get("TOTAL");
	}
	//剩余数量
	public void setRemainingQuantify(java.lang.String REMAINING_QUANTIFY) {
		set("REMAINING_QUANTIFY", REMAINING_QUANTIFY);
	}
	public java.lang.String getRemainingQuantify() {
		return get("REMAINING_QUANTIFY");
	}
	//奖品类型
	public void setPrizeType(java.lang.String PRIZE_TYPE) {
		set("PRIZE_TYPE", PRIZE_TYPE);
	}
	public java.lang.String getPrizeType() {
		return get("PRIZE_TYPE");
	}
	//卡券ID
	public void setCardId(java.lang.String CARD_ID) {
		set("CARD_ID", CARD_ID);
	}
	public java.lang.String getCardId() {
		return get("CARD_ID");
	}
	//奖品名称
	public void setPrizeName(java.lang.String PrizeName) {
		this.PrizeName = PrizeName;
	}
	public java.lang.String getPrizeName() {
		return PrizeName;
	}
	//产品图片
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
}
