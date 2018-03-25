package com.jz.jzcore.controller.front;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jz.jzcore.model.IntegralRule;
import com.jz.jzcore.model.IntegralVip;
import com.jz.web.common.controller.ControllerPath;

@ControllerPath(controllerKey = "/mygranary")
public class MyGranaryController extends FrontController {
	/**
	 * 签到
	 * */
	public void Report() {
		String openid = getOpenId();
		//根据openid查询当前用户信息
		WXConfigController wxconfig = new WXConfigController();
		IntegralVip vip = wxconfig.getIntegralVip(openid);
		String time = newDate();
		IntegralRule intergral = new IntegralRule();
		//获取签到积分
		String sql = "SELECT * FROM INTEGRAL_RULE WHERE \"NUMBER\" = ?";
		List<IntegralRule> rule = IntegralRule.dao.find(sql,"indexSign");
		for(IntegralRule intergr : rule){
			intergral = intergr;	
		}
		boolean result = false;
		//是否第一次签到
		if(vip.getReport()==null){
			//修改签到时间和积分
			int value = Integer.parseInt(intergral.getValue())+vip.getAllIntegral();
			result = new IntegralVip().set("ID", vip.getId()).set("ALL_INTEGRAL",value).set("REPORT", time).update();
		//今天已签过到
		}else if(vip.getReport().equals(time)){
			result = false;
		//今天未签到
		}else if(!vip.getReport().equals(time)){
			//修改签到时间和积分
			int value = Integer.parseInt(intergral.getValue())+vip.getAllIntegral();
			System.out.println("积分总数"+value);
			result = new IntegralVip().set("ID", vip.getId()).set("ALL_INTEGRAL",value).set("REPORT", time).update();
		}
		setAttr("openid", openid);
		//查询出当前用户总积分
		IntegralVip integralnumber = IntegralVip.dao.findById(vip.getId());
		//返回的参数，当前的积分数和签到成功
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", result);
		map.put("integralnumber", integralnumber.getAllIntegral());
		System.out.println("Report-Openid"+openid);
		renderJson(map);
	}
	
	
	/**
	 * 时间格式转换
	 * */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd"); 
		return time.format(date);
	}
}
