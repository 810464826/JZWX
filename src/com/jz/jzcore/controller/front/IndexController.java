package com.jz.jzcore.controller.front;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.json.Jackson;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jz.jzcore.model.IntegralMall;
import com.jz.jzcore.model.IntegralRule;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.TurntableRaffle;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.jzcore.model.WxUser;
import com.jz.jzcore.model.wxbase.UserCardStatus;
import com.jz.web.common.controller.ControllerPath;
import com.jz.weixin.WeixinUtil;

@ControllerPath(controllerKey = "/")
public class IndexController extends FrontController {
	private final Logger log = LoggerFactory.getLogger(IndexController.class);
	/**
	 * 进入系统方法
	 * */
	public void index() {
		String openid = getOpenId();
		log.info("IndexController>openid****>:"+openid);
		//根据Openid查询用户信息。
		WXConfigController wxconfig = new WXConfigController();
		WxUser user =  wxconfig.getUserByopenid(openid);
		IntegralVip vip = wxconfig.getIntegralVip(openid);
		setAttr("vip", vip);
		setAttr("wxuser", user);
		setAttr("openid", openid);
		System.out.println("名称"+user.getNickname());
		System.out.println("Index-Openid"+openid);
		render("/front/myIndex.html");
	}
	
	
	/**
	 * 扫码宝贝
	 * */
	public void scancode() {
		String openid = getOpenId();
		//根据OPENID查询出当前人员的扫码宝贝
		String sql = "select * from WINNING_DELIVERY where OPENID = ? AND WINNING_WAY = 1 ORDER BY WINNING_TIME DESC";
		List<WinningDelivery> winningList = WinningDelivery.dao.find(sql, openid); 
		for(WinningDelivery winning : winningList){
			System.out.println("这里是"+winning.getCardId());
			//查询出商品
			ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
			winning.setName(product.getName());
			winning.setPrizeName(product.getImgid());
			//判断是否属于卡券奖品
			if(winning.getPrizeType().equals("2")){
				//判断卡券状态，是否赠送
				WXConfigController wxconfig = new WXConfigController();
				String token = wxconfig.AccessTokenByName();
				String url = "https://api.weixin.qq.com/card/code/get?access_token="+token;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("code",winning.getCode());
				map.put("check_consume",false);
				String status = WeixinUtil.sendPost(url, JsonKit.toJson(map));
				UserCardStatus usercard = Jackson.getJson().parse(status,UserCardStatus.class);
				//卡券未使用时
				if(winning.getDistributionStatus().equals("4")){
					if(usercard.getUser_card_status() != null){
						if(usercard.getUser_card_status().equals("GIFTING")){//赠送中
							new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "7").update();
							winning.setDistributionStatus("6");
						}else if(usercard.getUser_card_status().equals("DELETE")){//卡券被删除
								new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "8").update();
								winning.setDistributionStatus("7");
						}
					}else{
						new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "5").update();
						winning.setDistributionStatus("5");
					}
					
				}
				//卡券在赠送中的
				if(winning.getDistributionStatus().equals("7")){
					if(usercard.getErrcode().equals("40056")){//赠送后已领取
						new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "6").update();
						winning.setDistributionStatus("6");
					}
				}
				
			}
		}
		setAttr("winningList", winningList);
		setAttr("openid", openid);
		render("/front/scancode.html");
	}
	
	/**
	 * 我的粮食兑换宝贝
	 * */
	public void foodExchange() {
		String openid = getOpenId();
		System.out.println("Index-Openid"+openid);
		//根据OPENID查询出当前人员的扫码宝贝
		String sql = "select * from WINNING_DELIVERY where OPENID = ? AND WINNING_WAY = 3 ORDER BY WINNING_TIME DESC";
		List<WinningDelivery> winningList = WinningDelivery.dao.find(sql, openid); 
		for(WinningDelivery winning : winningList){
			
			//查询出商品
			ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
			winning.setName(product.getName());
			winning.setPrizeName(product.getImgid());
			//判断是否属于卡券奖品
			if(winning.getPrizeType().equals("2")){
				//判断卡券状态，是否赠送
				WXConfigController wxconfig = new WXConfigController();
				String token = wxconfig.AccessTokenByName();
				String url = "https://api.weixin.qq.com/card/code/get?access_token="+token;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("code",winning.getCode());
				map.put("check_consume",false);
				String status = WeixinUtil.sendPost(url, JsonKit.toJson(map));
				UserCardStatus usercard = Jackson.getJson().parse(status,UserCardStatus.class);
				//卡券未使用时
				if(winning.getDistributionStatus().equals("4")){
					if(usercard.getUser_card_status().equals("GIFTING")){//赠送中
							new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "7").update();
							winning.setDistributionStatus("6");
					}else if(usercard.getUser_card_status().equals("DELETE")){//卡券被删除
							new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "8").update();
							winning.setDistributionStatus("7");
					}
				}
				//卡券在赠送中的
				if(winning.getDistributionStatus().equals("7")){
					if(usercard.getErrcode().equals("40056")){//赠送后已领取
						new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "6").update();
						winning.setDistributionStatus("6");
					}
				}
				
			}
		}
		setAttr("winningList", winningList);
		setAttr("openid", openid);
		render("/front/foodExchange.html");
	}
	
	/**
	 * 实物兑换
	 * */
	public void exchangeMain() {
		String openid = getOpenId();
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		IntegralVip integravip = new IntegralVip();
		for(IntegralVip iv : vip){
			integravip = iv;
		}
		//推荐
		String sqlRecommend = "SELECT * FROM INTEGRAL_MALL WHERE rownum <=8 AND RECOMMEND = ?";
		List<IntegralMall> mallRecommend = IntegralMall.dao.find(sqlRecommend,"1");
		for(IntegralMall  mall :mallRecommend){
			ProductInfo info = ProductInfo.dao.findById(mall.getName());
			mall.setPrizeName(info.getName());
			mall.setImg(info.getImgid());
		}
		//不推荐
		String sqlDeprecated = "SELECT * FROM INTEGRAL_MALL WHERE rownum <=8 AND RECOMMEND = ?";
		List<IntegralMall> mallDeprecated = IntegralMall.dao.find(sqlDeprecated,"2");
		for(IntegralMall  mall :mallDeprecated){
			ProductInfo info = ProductInfo.dao.findById(mall.getName());
			mall.setPrizeName(info.getName());
			mall.setImg(info.getImgid());
		}
		setAttr("integravip", integravip);
		setAttr("mallRecommend", mallRecommend);
		setAttr("mallDeprecated", mallDeprecated);
		render("/front/exchange-main.html");
	}
	
	/**
	 * 转盘宝贝
	 * */
	public void myTurnplate() {
		String openid = getOpenId();
		System.out.println("Index-Openid"+openid);
		//根据OPENID查询出当前人员的扫码宝贝
		String sql = "select * from WINNING_DELIVERY where OPENID = ? AND WINNING_WAY = 2 ORDER BY WINNING_TIME DESC";
		List<WinningDelivery> winningList = WinningDelivery.dao.find(sql, openid); 
		for(WinningDelivery winning : winningList){
			//查询出商品
			ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
			winning.setName(product.getName());
			winning.setPrizeName(product.getImgid());
			//判断是否属于卡券奖品
			if(winning.getPrizeType().equals("2")){
				//判断卡券状态，是否赠送
				WXConfigController wxconfig = new WXConfigController();
				String token = wxconfig.AccessTokenByName();
				String url = "https://api.weixin.qq.com/card/code/get?access_token="+token;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("code",winning.getCode());
				map.put("check_consume",false);
				String status = WeixinUtil.sendPost(url, JsonKit.toJson(map));
				UserCardStatus usercard = Jackson.getJson().parse(status,UserCardStatus.class);
				//卡券未使用时
				if(winning.getDistributionStatus().equals("4")){
					if(usercard.getUser_card_status().equals("GIFTING")){//赠送中
							new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "7").update();
							winning.setDistributionStatus("6");
					}else if(usercard.getUser_card_status().equals("DELETE")){//卡券被删除
							new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "8").update();
							winning.setDistributionStatus("7");
					}
				}
				//卡券在赠送中的
				if(winning.getDistributionStatus().equals("7")){
					if(usercard.getErrcode().equals("40056")){//赠送后已领取
						new WinningDelivery().set("ID", winning.getId()).set("DISTRIBUTION_STATUS", "6").update();
						winning.setDistributionStatus("6");
					}
				}
				
			}
		}
		setAttr("winningList", winningList);
		setAttr("openid", openid);
		render("/front/myTurnplate.html");
	}
	
	/**
	 * 转盘抽奖
	 * */
	@Before(Tx.class)
	public void luckDraw(){
		String openid = getOpenId();
		//减少用户积分
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		IntegralVip integra = new IntegralVip();
		for(IntegralVip iv : vip){
			integra = iv;
		}
		String sql = "SELECT * FROM TURNTABLE_RAFFLE WHERE TURNTABLE = ?";
		List<TurntableRaffle> turn = TurntableRaffle.dao.find(sql,"0");
		Map<String, Object> map = new HashMap<String, Object>();
		for(TurntableRaffle raffle : turn){
			ProductInfo info = ProductInfo.dao.findById(raffle.getPrizeName());
			raffle.setPrizeName(info.getName());
			raffle.setActivityId(info.getImgid());
			map.put(raffle.getPrizeName(), raffle);
		}
		//获取转盘所需积分
		String sqlintergral = "SELECT * FROM INTEGRAL_RULE WHERE \"NUMBER\" = ?";
		List<IntegralRule> rule= IntegralRule.dao.find(sqlintergral, "draw");
		IntegralRule integralrule = new IntegralRule();
		for(IntegralRule ir : rule){
			integralrule = ir;
		}
		setAttr("integralrule", integralrule);
		setAttr("integravip", integra);
		setAttr("turntableRaffle",JsonKit.toJson(map));
		render("/front/luckDraw.html");
	}
	
	/**
	 * 积分领取后，继续抽奖提示
	 * */
	public void successGetAward(){
		
		render("/front/successGetAward.html");
	}
	
	/**
	 * 收货地址
	 * */
	public void harvestAddress() {
		String openid = getOpenId();
		System.out.println("harvestAddress-Openid"+openid);
		String href = getPara("href"); 
		WXConfigController wxconfig = new WXConfigController();
		Map<String, String> map = wxconfig.getConfig(href);
		setAttr("openid", openid);
		renderJson(map);
	}

	/**
	 * 跳转扫一扫页面
	 * */
	public void goOnesweep() {
		render("/front/onesweep.html");
	}
	
	/**
	 * 扫一扫
	 * */
	public void onesweep() {
		String openid = getOpenId();
		String href = getPara("href"); 
		WXConfigController wxconfig = new WXConfigController();
		Map<String, String> map = wxconfig.getConfig(href);
		setAttr("openid", openid);
		renderJson(map);
	}
	
	
	/**
	 * 兑换详情
	 * */
	public void exchangeInfo() {
		String openid = getOpenId();
		System.out.println("Index-Openid"+openid);
		setAttr("openid", openid);
		render("/front/exchangeInfo.html");
	}
	
	
}