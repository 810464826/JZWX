package com.jz.jzcore.model;


/**
 * 获取微信用户信息时使用，方便转为对象
 * WxUserInfo wxuser = Jackson.getJson().parse(token, WxUserInfo.class);
 * */
public class WxUserInfo {
	private String subscribe;// 是否关注0未关注
	private String openid;// 唯一标识
	private String nickname;// 昵称
	private String sex;// 性别 1男 2女 0未知
	private String language;// 用户的语言，简体中文为zh_CN
	private String city;// 用户所在城市
	private String province;// 用户所在省份
	private String country;// 用户所在国家
	private String headimgurl;// 用户头像
	private String subscribe_time;// 用户关注时间，为时间戳。
	private String remark;// 公众号运营者对粉丝的备注，
	private String groupid;// 用户所在的分组ID（兼容旧的用户分组接口）
	private String[] tagid_list;// 用户被打上的标签ID列表
	private String subscribe_scene;//
	private String qr_scene;// 
	private String qr_scene_str;// 
	
	
	
	
	public String getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getSubscribe_time() {
		return subscribe_time;
	}

	public void setSubscribe_time(String subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String[] getTagid_list() {
		return tagid_list;
	}

	public void setTagid_list(String[] tagid_list) {
		this.tagid_list = tagid_list;
	}

	public String getSubscribe_scene() {
		return subscribe_scene;
	}

	public void setSubscribe_scene(String subscribe_scene) {
		this.subscribe_scene = subscribe_scene;
	}

	public String getQr_scene() {
		return qr_scene;
	}

	public void setQr_scene(String qr_scene) {
		this.qr_scene = qr_scene;
	}

	public String getQr_scene_str() {
		return qr_scene_str;
	}

	public void setQr_scene_str(String qr_scene_str) {
		this.qr_scene_str = qr_scene_str;
	}

}
