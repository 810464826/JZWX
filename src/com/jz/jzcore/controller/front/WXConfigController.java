package com.jz.jzcore.controller.front;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.json.Jackson;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.AccessToken;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.WxConfig;
import com.jz.jzcore.model.WxUser;
import com.jz.jzcore.model.WxUserInfo;
import com.jz.web.common.controller.ControllerPath;
import com.jz.weixin.WeixinUtil;

@ControllerPath(controllerKey = "/Joffro/wxconfig")
public class WXConfigController extends FrontController {
	
	public static String accessToken = null;  
	private Map<String, Object> attrs;
    
    public Map<String, String> getConfig(String url) {
    	accessToken = AccessTokenByName();
    	String jsapi_ticket =WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi");
    	System.out.println("jsapi_ticket"+jsapi_ticket);
        String[] jsapi1 = jsapi_ticket.split(":");
    	String[] jsapi2 = jsapi1[3].split(",");
    	char [] stringArray = jsapi2[0].toCharArray();
    	String ticket3 = "" ;
    	for(int i=1;i<stringArray.length-1;i++){
    		String ticket = String.valueOf(stringArray[i]);
    		ticket3 += ticket;
    	} 
        Map<String, String> ret = new HashMap<String, String>();  
        String nonce_str = create_nonce_str();  
        String timestamp = create_timestamp();  
        String string1;  
        String signature = "";
        //注意这里参数名必须全部小写，且必须有序  
        string1 = "jsapi_ticket=" + ticket3 +  
                  "&noncestr=" + nonce_str +  
                  "&timestamp=" + timestamp +  
                  "&url=" + url;  
        System.out.println("string1="+string1);  
        try  
        {  
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");  
            crypt.reset();  
            crypt.update(string1.getBytes("UTF-8"));  
            signature = byteToHex(crypt.digest());  
        }  
        catch (NoSuchAlgorithmException e)  
        {  
            e.printStackTrace();  
        }  
        catch (UnsupportedEncodingException e)  
        {  
            e.printStackTrace();  
        }  
        //获取数据的APPID
        String appid = "";
      	String sql = "SELECT * FROM WX_CONFIG WHERE NAME =?";
      	List<WxConfig> config = WxConfig.dao.find(sql, "appid");
      	if(config != null){
      		for(WxConfig  wxconfig : config){
      			appid = wxconfig.getValve();
      		}
      	}
        ret.put("url", url);  
        ret.put("jsapi_ticket", ticket3);  
        ret.put("nonceStr", nonce_str);  
        ret.put("timestamp", timestamp);  
        ret.put("signature", signature);  
        ret.put("appId",appid);   
        return ret;  
    }
    
    public Map<String, String> addrSign(String url){
    	//获取数据的APPID
        String appid = "";
      	String sql = "SELECT * FROM WX_CONFIG WHERE NAME =?";
      	List<WxConfig> config = WxConfig.dao.find(sql, "appid");
      	if(config != null){
      		for(WxConfig  wxconfig : config){
      			appid = wxconfig.getValve();
      		}
      	}
    	accessToken = AccessTokenByName();
    	String nonce_str = create_nonce_str();  
        String timestamp = create_timestamp(); 
        String sign = "accesstoken=" + accessToken +  
                "&appid=" + appid +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url; 
        String addrsign = "";
        try  
        {  
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");  
            crypt.reset();  
            crypt.update(sign.getBytes("UTF-8"));  
            addrsign = byteToHex(crypt.digest());  
        }  
        catch (NoSuchAlgorithmException e)  
        {  
            e.printStackTrace();  
        }  
        catch (UnsupportedEncodingException e)  
        {  
            e.printStackTrace();  
        }  
        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", appid);
        map.put("nonce_str", nonce_str);
        map.put("timestamp", timestamp);
        map.put("addrsign", addrsign);
    	return map;
    }
    
    
    /**
	 * 获取关注用户
	 * */
	@SuppressWarnings("unchecked")
	public void getUser(String openid){
		accessToken = AccessTokenByName();
		String userList = WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/user/get?access_token="+accessToken+"&next_openid="+openid);
		try {
			Map<String, Object> temp = JsonUtils.parse(userList, Map.class);
			this.attrs = temp;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(attrs);
		//总用户数
		String total = attrs.get("total").toString();
		System.out.println("总用户数"+total);
		//openid集合
		String data = attrs.get("data").toString();
		String[] openidList = data.substring(9, (data.length())-2).split(", ");
		for(int i=0;i<openidList.length;i++){
			SaveWxUsreByOpenid(openidList[i]);
		}
		//拉取数
		String count = attrs.get("count").toString();
		System.out.println("拉取用户数"+count);
		//最后一个OPENID
		String next_openid =attrs.get("next_openid").toString();
		System.out.println("最后用户的Openid"+next_openid);
		//每次拉取数量最大是10000，大于一万时。backOff回调本方法。需要最后一个人的open id
		if(count.equals("10000")){
			backOff(next_openid);
		}
	}
    
	 /** 
     * 获取微信用户时人数超过10000时，回调上一方法
     * @param hash 
     * @return 
     */
	public void backOff(String openid){
		getUser(openid);
	}
	
	
    /** 
     * 根据openid获取单个用户
     * @param hash 
     * @return 
     */
    public Boolean SaveWxUsreByOpenid(String openid){
    	WxUser user = new WxUser();
    	//读取用户基本信息
		String token = WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN");
		//JSON字符串转为实体对象
		WxUserInfo wxuser = Jackson.getJson().parse(token, WxUserInfo.class);
		System.out.println("obj参数"+wxuser.getHeadimgurl());
		user.setNickname(wxuser.getNickname());
		user.setHeadimgurl(wxuser.getHeadimgurl());
		String sql = "SELECT * FROM WX_USER WHERE OPENID = ?";
		List<WxUser> oldwx = WxUser.dao.find(sql,wxuser.getOpenid());
		Boolean emoji =  containsEmoji(wxuser.getNickname());
		if(emoji){
			try {
				String name=java.net.URLEncoder.encode(wxuser.getNickname(), "UTF-8");
				wxuser.setNickname(name);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean result = false;
		//判断当前用户是否是新用户
		if(oldwx.size()==0){
			//存储用户
			result = new WxUser().set("ID",UUID.randomUUID().toString().replaceAll("-", ""))
										 .set("OPENID",wxuser.getOpenid())
										 .set("NICKNAME", wxuser.getNickname())
										 .set("SEX", wxuser.getSex())
										 .set("COUNTRY", wxuser.getCountry())
										 .set("PROVINCE", wxuser.getProvince())
										 .set("CITY", wxuser.getCity())
										 .set("HEADIMGURL", wxuser.getHeadimgurl())
										 .set("FOLLOTIME", Timestamp.valueOf(timeStamp(wxuser.getSubscribe_time()))).save();
			if(result == true){
				System.out.println("保存新用户成功");
			}else{
				System.out.println("保存新用户出错");
			}
		}else{
			for(WxUser upWxUser : oldwx){
				//修改已有用户信息
				result = new WxUser().set("ID",upWxUser.getId())
											 .set("OPENID",wxuser.getOpenid())
											 .set("NICKNAME", wxuser.getNickname())
											 .set("SEX", wxuser.getSex())
											 .set("COUNTRY", wxuser.getCountry())
											 .set("PROVINCE", wxuser.getProvince())
											 .set("CITY", wxuser.getCity())
											 .set("HEADIMGURL", wxuser.getHeadimgurl())
											 .set("FOLLOTIME", Timestamp.valueOf(timeStamp(wxuser.getSubscribe_time()))).update();
				if(result == true){
					System.out.println("修改用户成功");
				}else{
					System.out.println("修改用户出错");
				}
			}
		}
		return result;
    }
	
	
    /** 
     * 根据openid获取单个用户
     * @param hash 
     * @return 
     */
    public WxUser getUserByopenid(String openid){
    	System.out.println("获取单个用户");
    	accessToken = AccessTokenByName();
    	WxUser user = new WxUser();
    	//读取用户基本信息
		String token = WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN");
		//JSON字符串转为实体对象
		WxUserInfo wxuser = Jackson.getJson().parse(token, WxUserInfo.class);
		System.out.println("obj参数"+wxuser.getHeadimgurl());
		//判断是否有表情符号
		Boolean emoji =  containsEmoji(wxuser.getNickname());
		if(emoji){
			try {
				//转码
				String name=java.net.URLEncoder.encode(wxuser.getNickname(), "UTF-8");
				System.out.println("名称转码"+name);
				wxuser.setNickname(name);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		user.setNickname(wxuser.getNickname());
		user.setHeadimgurl(wxuser.getHeadimgurl());
		String sql = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> integralVip = IntegralVip.dao.find(sql,wxuser.getOpenid());	
		//判断当前用户是否已经是会员
		if(integralVip.size()==0){
			//存储用户为会员
			boolean result = new IntegralVip().set("ID",UUID.randomUUID().toString().replaceAll("-", "")).set("LUCKY_DRAW", wxuser.getNickname()).set("OPENID",wxuser.getOpenid()).set("CREATE_TIME", Timestamp.valueOf(newDate())).set("UPDATE_TIME",Timestamp.valueOf(newDate())).save();
			if(result == true){
				System.out.println("保存会员");
			}else{
				System.out.println("保存会员时出错");
			}
		}else{
			System.out.println("用户已是会员修改信息");
			for(IntegralVip vip :integralVip){
				//修改会员用户
				boolean result = new IntegralVip().set("ID",vip.getId()).set("LUCKY_DRAW", wxuser.getNickname()).set("UPDATE_TIME",Timestamp.valueOf(newDate())).update();
				if(result == true){
					System.out.println("修改会员");
				}else{
					System.out.println("修改会员时出错");
				}
			}
		}
		return user;
    }
    
    /**
	 * 获取当前积分会员对象
	 * */
	public IntegralVip getIntegralVip(String openid){
		IntegralVip vip = new IntegralVip();
		//当前用户的ID
		String sql= "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> integralViplist = IntegralVip.dao.find(sql,openid);
		for(IntegralVip obj : integralViplist){
			vip = obj;
		}
		return vip;
	}
    
    
    /**
	 * 判断当前对象是否已关注当前公众号
	 * */
	public WxUserInfo followByOpenid(String openid){
		accessToken = AccessTokenByName();
    	//读取用户基本信息
		String token = WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN");
		//JSON字符串转为实体对象
		WxUserInfo wxuser = Jackson.getJson().parse(token, WxUserInfo.class);
		return wxuser;
	}	
	
	
	
	
	/**
	 * 时间格式转换
	 * */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return time.format(date);
	}
    
	/** 
     * 数据库中获取两小时时效的accessToken,但是项目MyTask中的定时器是每小时获取一次，一天24小时获取24次
     * @param hash 
     * @return 
     */ 
    public String AccessTokenByName(){
    	accessToken = "";
    	String token = "123456";
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
		ApiConfigKit.setThreadLocalAppId(appid);
		ApiConfig ac = new ApiConfig();
		// 配置微信 API 相关常量
		ac.setToken(token);
		ac.setAppId(appid);
		ac.setAppSecret(appSecret);
		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		ApiConfigKit.putApiConfig(ac);
		AccessToken acc =AccessTokenApi.getAccessToken();
		accessToken = acc.getAccessToken();
    	return accessToken;
    }
    
	/** 
     * 数据库中获取两小时时效的accessToken,返回对象
     * @param hash 
     * @return 
     */ 
    public WxConfig AccessTokenByNameAndClass(){
    	WxConfig wx = new WxConfig();
    	//获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "accessToken");
		if(config != null){
			for(WxConfig  wxconfig : config){
				wx = wxconfig;
			}
		}
    	return wx;
    }
    
	/** 
     * 数据库中获取appid
     * @param hash 
     * @return 
     */ 
    public String getAppId(){
    	WxConfig wx = new WxConfig();
    	//获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "appid");
		if(config != null){
			for(WxConfig  wxconfig : config){
				wx = wxconfig;
			}
		}
    	return wx.getValve();
    }
    
    /** 
     * 数据库中获取域名
     * @param hash 
     * @return 
     */ 
    public String getHttp(){
    	WxConfig wx = new WxConfig();
    	//获取数据的APPID
		String sql = "SELECT * FROM WX_CONFIG WHERE NAME = ?";
		List<WxConfig> config = WxConfig.dao.find(sql, "http");
		if(config != null){
			for(WxConfig  wxconfig : config){
				wx = wxconfig;
			}
		}
    	return wx.getValve();
    }
    
    
	/** 
     * 随机加密 
     * @param hash 
     * @return 
     */  
    private static String byteToHex(final byte[] hash) {  
        Formatter formatter = new Formatter();  
        for (byte b : hash)  
        {  
            formatter.format("%02x", b);  
        }  
        String result = formatter.toString();  
        formatter.close();  
        return result;  
    }  
  
    /** 
     * 产生随机串--由程序自己随机产生 
     * @return 
     */  
    private static String create_nonce_str() {  
        return UUID.randomUUID().toString();  
    }  
  
    /**
     * 只获取AccessToken参数
     * */
//    public String getAccessToken(){
//    	
//    	String appid ="";
//		String appSecret = "";
//		//获取数据的APPID
//		String sql = "SELECT * FROM WX_CONFIG WHERE NAME =?";
//		List<WxConfig> config = WxConfig.dao.find(sql, "appid");
//		if(config != null){
//			for(WxConfig  wxconfig : config){
//				appid = wxconfig.getValve();
//			}
//		}
//		//获取数据的appSecret
//		String sql1 = "SELECT * FROM WX_CONFIG WHERE NAME =?";
//		List<WxConfig> config1 = WxConfig.dao.find(sql1, "appSecret");
//		if(config != null){
//			for(WxConfig  wxconfig : config1){
//				appSecret = wxconfig.getValve();
//			}
//		}
//    	String aToken = WeixinUtil.getAccess_token("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appSecret+"");  
//    	String[] tokenOne = aToken.split(":");
//    	String[] token = tokenOne[1].split(",");
//    	char [] stringArr = token[0].toCharArray();
//    	String token3 = "" ;
//    	for(int i=1;i<stringArr.length-1;i++){
//    		String token2 = String.valueOf(stringArr[i]);
//    		token3 += token2;
//    	}
//    	return token3;
//    }
    
    /** 
     * 由程序自己获取当前时间 
     * @return 
     */  
    private static String create_timestamp() {  
        return Long.toString(System.currentTimeMillis() / 1000);  
    }  
    
    /** 
     * 时间戳时间转为yyyy-MM-dd HH:mm:ss
     * @return 
     */  
    private String timeStamp(String time) {  
    	//时间戳转换
    	long msgCreateTime = Long.parseLong(time) * 1000L;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String date =  format.format(new Date(msgCreateTime));
        String dstr=date;
        return dstr;
    }  
    
    /** 
     * 判断字符串中是否包含表情符号
     * @return 
     */  
    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i+1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50|| hs == 0x231a ) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() -1) {
                    char ls = source.charAt(i+1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return  isEmoji;
    }
    
    /**
	 * 调用微信分享生成微信所需时间
	 * */
	public String getDate(){
		 Date dete = new Date();
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		 String dateString = formatter.format(dete);
		 System.out.println(dateString);
		 return dateString;
	}
}
