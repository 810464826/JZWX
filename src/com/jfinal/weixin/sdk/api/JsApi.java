package com.jfinal.weixin.sdk.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfinal.kit.HttpKit;
import com.jz.jzcore.controller.front.WXConfigController;
import com.jz.jzcore.controller.front.WxCardSign;
import com.jz.jzcore.model.WxConfig;

public class JsApi {
	private final static String access_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=TOKEN&type=wx_card";
	// 利用 appId 与 accessToken 建立关联，支持多账户
	private static Map<String, JsapiTicket> ticketMap = new ConcurrentHashMap<String, JsapiTicket>();

	/**
	 * 获取jsapi_ticket
	 * 
	 * @param access_token
	 * @return
	 */
	public static JsapiTicket getJsapiTicket() {
		String appId = "";
		// 获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "appid");
		if (config != null) {
			for (WxConfig wxconfig : config) {
				appId = wxconfig.getValve();
			}
		}
		JsapiTicket result = ticketMap.get(appId);
		if (result != null && result.isAvailable())
			return result;

		refreshTicket();

		return ticketMap.get(appId);
	}

	/**
	 * 强制更新 ticket
	 */
	public static synchronized void refreshTicket() {
		String appId = "";
		// 获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "appid");
		if (config != null) {
			for (WxConfig wxconfig : config) {
				appId = wxconfig.getValve();
			}
		}
		JsapiTicket ticket = null;
		for (int i = 0; i < 3; i++) { // 最多三次请求
			WXConfigController wxconfig = new WXConfigController();
			String token = wxconfig.AccessTokenByName();
			String requestUrl = access_ticket_url.replace("TOKEN", token);
			String json = HttpKit.get(requestUrl);
			ticket = new JsapiTicket(json);
			if (ticket.isAvailable())
				break;
		}

		// 三次请求如果仍然返回了不可用的JsapiTicket 仍然 put 进去，便于上层通过 JsapiTicket 中的属性判断底层的情况
		ticketMap.put(appId, ticket);
	}

	/**
	 * 获取投放的微信卡券签名
	 */
	public static String getWxSingature(String timestamp, String nonceStr, String cardid, String openid) {
		String singature = null;
		JsapiTicket ticket = getJsapiTicket();
		if (ticket.isAvailable()) {
			String ticketStr = ticket.getTicket();
//			ArrayList<String> list = new ArrayList<String>();
//			list.add(ticketStr);
//			list.add(timestamp);
//			list.add(nonceStr);
//			list.add(cardid);
//			list.add(openid);
//			Collections.sort(list);
//			String aa = timestamp + ticketStr + nonceStr + openid + cardid;
			WxCardSign signer = new WxCardSign();
			signer.AddData(ticketStr);
	        signer.AddData(cardid);
	        signer.AddData(timestamp);
	        signer.AddData(nonceStr);
	        signer.AddData(openid);
	        
			singature = signer.GetSignature();//DigestUtils.shaHex(list.get(0) + list.get(1) + list.get(2) + list.get(3) + list.get(4));// byteToHex(digest);
		}

		return singature;

	}
}