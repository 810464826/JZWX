package com.jz.jzcore.controller.front;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.aop.Before;
import com.jfinal.json.Jackson;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.JsApi;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.ActivityOrder;
import com.jz.jzcore.model.ActivityReport;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.QRcodeInfo;
import com.jz.jzcore.model.SellerOutInfo;
import com.jz.jzcore.model.SystemOffice;
import com.jz.jzcore.model.SystemUser;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.jzcore.model.WxCard;
import com.jz.web.common.controller.ControllerPath;
import com.jz.weixin.WeixinUtil;
import com.jz.jzcore.model.wxbase.WxCardByCode;

@ControllerPath(controllerKey = "/front/card")
public class WXCardController extends FrontController {
	private final Logger log = LoggerFactory.getLogger(WXCardController.class);

	/** 发卡券 */
	public void index() throws Exception {
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		Map<String, Object> map = new HashMap<String, Object>();
		Connection conn=null;
		try {
			 conn=sqlconfig.bengin();
			String openid = getOpenId();
			// 随机字符串
			String nonceStr = create_nonce_str();
			String codeId = getPara("codeId");
//			String sqlinfo = "SELECT * FROM QRCODE_INFO WHERE ID = ?";
			String getQrcodeById = PropKit.use("database.properties").get("getQrcodeById");
		    QRcodeInfo qrcode = dao.getQRcodeInfoById(conn,getQrcodeById,codeId);
			//判断CODEID是否被领取
			if(qrcode.getExchange().equals("0")){
				//卡券ID
				String card = getPara("cardId");
				WxCard  wxcard = WxCard.dao.findById(card);
				String cardId = wxcard.getCardId();
				//时间戳
				long time = System.currentTimeMillis() / 1000;
				String timestamp = Long.toString(time);
				String signature= JsApi.getWxSingature(timestamp,nonceStr,cardId,openid);
				map.put("cardId", cardId);
				map.put("timestamp", timestamp);
				map.put("nonceStr", nonceStr);
				map.put("signature", signature);
				map.put("openid", openid);
				map.put("restul", "0");
			}else{
				map.put("restul", "1");
			}
			// 5. 事务提交
	        conn.commit();
	    } catch (NestedTransactionHelpException e) {
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        LogKit.logNothing(e);
	    } catch (Throwable t) {
	        // 6. 若有异常就回滚
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        throw t instanceof RuntimeException ? (RuntimeException)t : new ActiveRecordException(t);
	    }
	    finally {
	        try {
	            if (conn != null) {
	                if (sqlconfig.autoCommit != null)
	                    conn.setAutoCommit(sqlconfig.autoCommit);
	                conn.close();
	            }
	        } catch (Throwable t) {
	            LogKit.error(t.getMessage(), t);   
	        }
	        finally {
	        	sqlconfig.config.removeThreadLocalConnection();   
	        }
	      }
        renderJson(map);
	}
	
	/** 发卡券 */
	public void turntasbleCard(){
		String openid = getOpenId();
		Map<String, String> map = new HashMap<String, String>();
		// 随机字符串
		String nonceStr = create_nonce_str();
		//卡券ID
		String card = getPara("cardId");
		WxCard  wxcard = WxCard.dao.findById(card);
		String cardId = wxcard.getCardId();
		//时间戳
		long time = System.currentTimeMillis() / 1000;
		String timestamp = Long.toString(time);
		String signature= JsApi.getWxSingature(timestamp,nonceStr,cardId,openid);
		map.put("cardId", cardId);
		map.put("timestamp", timestamp);
		map.put("nonceStr", nonceStr);
		map.put("signature", signature);
		map.put("openid", openid);
		map.put("restul", "0");
		renderJson(map);
	}
	
	/**
	 * 领取卡券后
	 * */
	@Before(Tx.class)
	public void getCard(){
		//用户opneid
		String openid = getPara("openid");
		log.info("当前用户的openId*************>:"+openid);
		//用户领取的卡券的唯一ID
		String code = getPara("code");
		log.info("用户领取的卡券的唯一 code ID*************>:"+code);
		//code解码
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String Decode = "{\"encrypt_code\":\""+code+"\"}";
		log.info("code解码Decode*************>:"+Decode);
		String DecodeOk = WeixinUtil.sendPost("https://api.weixin.qq.com/card/code/decrypt?access_token="+token, Decode);
		log.info("DecodeOk*************>:"+DecodeOk);
		WxCardByCode wxcode = Jackson.getJson().parse(DecodeOk,WxCardByCode.class);
		log.info("解码后的code**********>:"+wxcode.getCode());
		//卡券ID
		String cardeId = getPara("cardeId");
		WxCard card =WxCard.dao.findById(cardeId);
		log.info("解码后的code**********>:"+wxcode.getCode());
		//客户所扫的二维码
		String codeId = getPara("codeId");
		log.info("客户所扫的二维码ID**********>:"+codeId);
		//产品ID
		String productId = getPara("productId");
		log.info("产品ID**********>:"+productId);
		//判断是扫码1，2兑换，3抽奖
		String restul = getPara("restul");
		log.info("判断是扫码1，2兑换，3抽奖  restul**********>:"+restul);
		boolean savewinning = false;
		if(restul.equals("1")){
			//修改客户扫的二维码，修改奖品已被领取
			new QRcodeInfo().set("ID", codeId).set("EXCHANGE", "1").update();
			QRcodeInfo qrcodeInfo = QRcodeInfo.dao.findById(codeId);
			// ******************************====根据瓶码查询运单开始=====***************************************
			// 根据该瓶码查询出该运单
			String getSellerOrderByPingCodeId = PropKit.use("database.properties").get("getSellerOrderByPingCodeId");
			List<Record> soiView = Db.find(getSellerOrderByPingCodeId, qrcodeInfo.getBoxCode());
			List<SellerOutInfo> soiList = new ArrayList<SellerOutInfo>();
			for (Record cord : soiView) {
				SellerOutInfo soi = new SellerOutInfo();
				soi.mapping(cord, soi);
				soiList.add(soi);
			}
			log.info("该瓶码所属的运单是****>:" + soiList);
			// ******************************====根据瓶码查询运单结束=====***************************************
			SellerOutInfo sellerOutInfo = soiList.get(0);
			// 这是运单号
			Number recordId = sellerOutInfo.getRecordId();
			log.info("该运单的记录ID是****>:" + recordId);
			// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
			String sql = "SELECT * FROM ACTIVITYORDER WHERE RECORDID = ? AND  STATE = 1";
			ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql, recordId);
			String activityId = activityOrder.getActivityId();
			ActivityReport activity = ActivityReport.dao.findById(activityId);
			String applyUserId = activity.getApplyUser();
			SystemUser user = SystemUser.dao.findById(applyUserId);
			String officeId = user.getOfficeId();
			SystemOffice office = SystemOffice.dao.findById(officeId);
			log.info("applyUserId***>"+applyUserId);
			log.info("ACTIVITY_USERNAME***>"+office.getName());
			log.info("ACTIVITY_ADDRESS***>"+office.getAddressName());
			// ******************************=====根据运单查活动=====***************************************
			//保存中奖信息
			WinningDelivery winningDelivery=new WinningDelivery();
			savewinning=winningDelivery.set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
					set("WINNING_WAY","1").
					set("PRIZE_NAME", productId).
					set("WINNING_TIME",Timestamp.valueOf(newDate())).
					set("OPENID",openid).
					set("DISTRIBUTION_STATUS","4").
					set("PRIZE_STATUS","2").
					set("PRIZE_TYPE","2").
					set("CODE_ID", codeId).
					set("CODE", wxcode.getCode()).
					set("ACTIVITY_ID",activityOrder.getActivityId()).
					set("ACTIVITY_USERNAME", office.getName()).
					set("ACTIVITY_AREA", activity.getActivityAddress()).
					set("CARDID", card.getCardId()).
					save();
			log.info("保存中奖信息save****>:" + savewinning);
			boolean wxInformation = wxInformation(openid,ProductInfo.dao.findById(productId),winningDelivery,activity);
			log.info("是否已经推送了消息****>:" + wxInformation);
		}
		setAttr("openid", openid);
		//render("/front/scancode.html");
		renderJson(savewinning);
	}
	
	/**
	 * 推送消息 中奖后联系他们
	 */
	public boolean wxInformation(String openid, ProductInfo product, WinningDelivery delivery,ActivityReport activity) {
		System.out.println("发送消息" + openid);
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String appid = wxconfig.getAppId();
		String http = wxconfig.getHttp();
		String json = " {\"touser\":\"" + openid + "\","
				+ "\"template_id\":\"YTbbDHJub8JkuWY4gi2PhMd15B0G_W2nb49z5vzgZoU\","
				+ "\"url\":\"http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid
				+ "&redirect_uri=http%3a%2f%2f" + http + "%2fJZWX%2ffront%2fcard%2flookPrizeInfo%3fproductId%3d"
				+ product.getId() + "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect\","
				+ "\"topcolor\":\"#FF0000\"," + "\"data\":{" + "\"first\": {" + "\"value\":\"亲~您本次中奖的奖品如下\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"keyword1\":{" + "\"value\":\"" + activity.getActivityName() + "\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"keyword2\":{" + "\"value\":\"" + product.getName() + "\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword3\":{" + "\"value\":\"" + newDate() + "\"," + "\"color\":\"#ca2426" + "\"},"
				+ "\"remark\":{" + "\"value\":\"恭喜您中奖，核销后请保存您的瓶盖，到时方便业务员对账，感谢您对绵柔尖庄的支持！\","
				+ "\"color\":\"#ca2426\"}}}";
		WeixinUtil.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, json);
		return true;
	}
	
	/**
	 * 这里查看中奖详情
	 */
	public void lookPrizeInfo(){
		String productId = getPara("productId");
		log.info("产品Id****>:" + productId);
		ProductInfo product = ProductInfo.dao.findById(productId);
		log.info("产品信息****>:" + product);
		setAttr("product", product);
		render("/front/cancel/productInfo.html");
	}
	
	
	/**
	 * 转盘宝贝内与兑换宝贝内领取卡券
	 * */
	public void SaveCard(){
		String winningId = getPara("winningId");
		//用户领取的卡券的唯一ID
		String code = getPara("code");
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String Decode = "{\"encrypt_code\":\""+code+"\"}";
		System.out.println("Decode"+Decode);
		String DecodeOk = WeixinUtil.sendPost("https://api.weixin.qq.com/card/code/decrypt?access_token="+token, Decode);
		System.out.println("DecodeOk"+DecodeOk);
		WxCardByCode wxcode = Jackson.getJson().parse(DecodeOk,WxCardByCode.class);
		System.out.println("解码后的code"+wxcode.getCode());
		boolean savewinning =new WinningDelivery().
				set("ID",winningId).
				set("CODE", wxcode.getCode()).
				set("DISTRIBUTION_STATUS","4").
				update();
		renderJson(savewinning);
	}
	
	/**
	 * 转盘与兑换领取卡券记录
	 * */
	public void SaveTurntableCard(){
		//用户opneid
		String openid = getOpenId();
		//产品ID
		String productId = getPara("productId");
		//用户领取的卡券的唯一ID
		String code = getPara("code");
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String Decode = "{\"encrypt_code\":\""+code+"\"}";
		System.out.println("Decode"+Decode);
		String DecodeOk = WeixinUtil.sendPost("https://api.weixin.qq.com/card/code/decrypt?access_token="+token, Decode);
		System.out.println("DecodeOk"+DecodeOk);
		WxCardByCode wxcode = Jackson.getJson().parse(DecodeOk,WxCardByCode.class);
		System.out.println("解码后的code"+wxcode.getCode());
		//卡券ID
		String cardeId = getPara("cardeId");
		//判断是兑换，抽奖   中奖方式 1扫码中奖 2转盘中奖 3商城兑换
		String info = getPara("info");
		//是否领取
		String receive = getPara("receive");
		boolean savewinning = false;
		if(info.equals("转盘")){
			if(receive.equals("已领取")){
				//保存中奖信息
				savewinning =new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","2").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","4").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","2").
						set("CODE", wxcode.getCode()).
						set("CARDID", cardeId).
						save();
			}else{
				//保存中奖信息
				savewinning =new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","2").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","9").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","2").
						set("CODE", wxcode.getCode()).
						set("CARDID", cardeId).
						save();
			}
		}else{
			if(receive.equals("已领取")){
				//保存中奖信息
				savewinning =new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","3").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","4").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","2").
						set("CODE", wxcode.getCode()).
						set("CARDID", cardeId).
						save();
			}else{
				//保存中奖信息
				savewinning =new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","3").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","9").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","2").
						set("CODE", wxcode.getCode()).
						set("CARDID", cardeId).
						save();
			}
		}
		renderJson(savewinning);
	}
	
    /** 
     * 产生随机串--由程序自己随机产生 
     * @return 
     */  
    private static String create_nonce_str() {  
        return UUID.randomUUID().toString();  
    }
	/**
	 * 时间格式转换
	 * */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return time.format(date);
	}
}
	