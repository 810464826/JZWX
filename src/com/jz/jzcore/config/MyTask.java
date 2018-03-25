package com.jz.jzcore.config;

import com.jz.jzcore.controller.front.WXConfigController;
public class MyTask implements ITask {
	 public void run() {
	      // 这里放被执行的调试任务代码
		 System.out.println("定时获取accessToken开始");
		 WXConfigController config = new WXConfigController();
//		 String access = config.getAccessToken();
//		 String access = config.AccessTokenByName();
//		 WxConfig wxconfig = config.AccessTokenByNameAndClass();
//		 boolean result = new WxConfig().set("ID", wxconfig.getId()).set("VALUE", access).update();
//		 System.out.println("定时获取accessToken结束"+result);
		 System.out.println("定时获取关注用户开始");
		 String openid = "";
		 config.getUser(openid);
		 System.out.println("定时获取关注用户结束");
	   }
	   
	 public void stop() {
	     // 这里的代码会在 task 被关闭前调用
		 System.out.println("关闭前调用");
	   }
}
