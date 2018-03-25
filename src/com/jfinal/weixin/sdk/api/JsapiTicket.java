package com.jfinal.weixin.sdk.api;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.weixin.sdk.api.ReturnCode;

/**
 * 接口访问凭证
 * 
 */
public class JsapiTicket {
	private Integer errcode; //未出错值为0
	private String errmsg;   //未出错值为OK
	private String ticket;
	private Integer expires_in;// 凭证有效时间，单位：秒
	
	private Long expiredTime;		// 正确获取到 ticket 时有值，存放过期时间
	private String json;
	
	public JsapiTicket(String jsonStr) {
		this.json = jsonStr;
		
		try {
			@SuppressWarnings("rawtypes")
			Map map = new ObjectMapper().readValue(jsonStr, Map.class);
			ticket = (String)map.get("ticket");
			expires_in = (Integer)map.get("expires_in");
			errcode = (Integer)map.get("errcode");
			errmsg = (String)map.get("errmsg");
			if (expires_in != null)
				expiredTime = System.currentTimeMillis() + ((expires_in -5) * 1000);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getJson() {
		return json;
	}
	
	public boolean isAvailable() {
		if (expiredTime == null)
			return false;
		if (errcode>0)
			return false;
		if (expiredTime < System.currentTimeMillis())
			return false;
		return ticket != null;
	}
	
	public String getTicket() {
		return ticket;
	}
	
	public Integer getExpiresIn() {
		return expires_in;
	}
	
	public Integer getErrorCode() {
		return errcode;
	}
	
	public String getErrorMsg() {
		if (errcode != null) {
			String result = ReturnCode.get(errcode);
			if (result != null)
				return result;
		}
		return errmsg;
	}

}