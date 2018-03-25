package com.jz.jzcore.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 * */
public class Utils {
	/**
	 * 时间格式转换
	 * */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return time.format(date);
	}
}
