package com.jz.jzcore.controller.front;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jz.jzcore.model.WxConfig;

public class FrontController extends Controller {
/*	private final Logger log = Logger.getLogger(FrontController.class);
	//获取微信ID,没有则使用IP地址
	public String getOpenId() {
		String openid  = (String) this.getSession().getAttribute(Constants.OPENID);
		if(openid == null || openid.equals("")){
			openid = (String) getSession().getAttribute(Constants.OPENID);
			String code = getPara("code");
			log.info("******FrontController**************获取的code:"+code);
			if (StringUtils.isEmpty(openid) && !StringUtils.isEmpty(code)) {
				String appid ="";
				String appSecret = "";
				//获取数据的APPID
				String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
				List<WxConfig> config = WxConfig.dao.find(sql, "appid");
				if(config != null){
					for(WxConfig  wxconfig : config){
						appid = wxconfig.getValve();
					}
				}
				//获取数据的appSecret
				String sql1 = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
				List<WxConfig> config1 = WxConfig.dao.find(sql1, "appSecret");
				if(config != null){
					for(WxConfig  wxconfig : config1){
						appSecret = wxconfig.getValve();
					}
				}
				SnsAccessToken token = SnsAccessTokenApi.getSnsAccessToken(appid,appSecret,code);
				log.info("******FrontController**************获取的token:"+token);
				openid = token.getOpenid();
				log.info("******FrontController**************获取的openId:"+openid);
			if (StringUtils.isEmpty(openid)) {
				openid = getRequest().getRemoteAddr();
			}
	    	}
			getSession().setAttribute(Constants.OPENID, openid);
		}
		return openid;
	}*/
	
	private Logger logger = Logger.getLogger(FrontController.class);

	/**
	 * 获取openid和返回openid
	 */
	public synchronized String getOpenId() {
		String code = getPara("code");
		String appid ="";
		String appSecret = "";
		//获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "appid");
		if(config != null){
			for(WxConfig  wxconfig : config){
				appid = wxconfig.getValve();
			}
		}
		//获取数据的appSecret
		String sql1 = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config1 = WxConfig.dao.find(sql1, "appSecret");
		if(config != null){
			for(WxConfig  wxconfig : config1){
				appSecret = wxconfig.getValve();
			}
		}
		ApiConfigKit.putApiConfig(getApiConfig("",appid,appSecret));
		String openid = (String) this.getSession().getAttribute(appid);
		logger.info("获取appid="+appid+"--appSecret="+appSecret+"------code"+code);
		logger.error("获取Session中的Openid="+openid);
		if(openid == null || openid.equals("")){
			if (!StringUtils.isEmpty(code)) {
				SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appid, appSecret, code);
				logger.info("获取acctoten="+snsAccessToken);
				openid = snsAccessToken.getOpenid();
				getSession().setAttribute(appid, openid);
				if (StringUtils.isEmpty(openid)) {
					openid = getRequest().getRemoteAddr();
					getSession().setAttribute(appid, openid);
				}
			}else{
				renderError(404);
			}
		}
		// 判读openid是否包含英文字母。ip地址不包含英文字母
		if (openid.matches(".*[a-zA-z].*")) {
			System.out.println("当前用户openid" + openid);
		} else {
			// 不包含英文字母
			getSession().setAttribute(appid, "");
		}
		logger.error("获取的openId****>"+openid);
		return openid;
	}

	/**
	 * 获取AccessToken对象中的accessToken
	 */
	public String getAccessToken() {
		String accessToken = AccessTokenApi.getAccessToken().getAccessToken();
		return accessToken;
	}

	/**
	 * 添加微信公众号配置获取配置
	 */
	public static ApiConfig getApiConfig(String Token, String AppId, String AppSecret) {
		ApiConfig ac = new ApiConfig();
		// 配置微信 API 相关常量
		ac.setToken(Token);
		ac.setAppId(AppId);
		ac.setAppSecret(AppSecret);
		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}
	
	
	/**
	 * 生成ID 当前毫秒数
	 * 
	 * @param args
	 */
	public static String newId() {
		return UUID.randomUUID().toString().replaceAll("-", "") + newDateByYMD();
	}

	/**
	 * 新建时间并格式转换 年月日
	 */
	public static String newDateByYMD() {
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
		return time.format(date);
	}
}
