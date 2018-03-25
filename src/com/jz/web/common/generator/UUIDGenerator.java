package com.jz.web.common.generator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class UUIDGenerator {

	private static SimpleDateFormat Format = new SimpleDateFormat("yyyySSSSMMyyssHHddmm");// 设置日期格式

	public UUIDGenerator() {
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		System.err.println("uuid: " + uuid);
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
		System.err.println("uuid: " + str + "," + temp);
		return str + "," + temp;
	}

	// 获得指定数量的UUID
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}
		return ss;
	}

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
			"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };

	public static String generateShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	static int num = 1000;

	public static String generateOrderNum() {
		Calendar calendar = Calendar.getInstance();
		// calendar.getTime()
		// Date date = new Date();
		String string = Format.format(calendar.getTime());
		string = string.substring(0, 2) + string.substring(4);
		if (num >= 10000) {
			num = 1000;
		}
		return string + (num++);// + generateShortUuid();
	}

	public static void main(String[] args) {
		// Random r = new
		// Random(BitConverter.ToInt32(UUID.randomUUID().ToByteArray(), 0));
		// String[] ss = getUUID(100);
		// Thread t1 = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// for (int i = 0; i < 100; i++) {
		// // System.out.println("ss[" + i + "]=====" + ss[i]);
		// System.out.println("t1--num[" + i + "]=====" + generateOrderNum());
		// }
		// }
		// });
		// t1.start();
		// for (int i = 0; i < 1000; i++) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// for (int i = 0; i < 1000; i++) {
		// System.out.println(Thread.currentThread().getName() + "--num[" +
		// generateOrderNum() + "]--");
		// }
		// }
		// }).start();
		// }
		// for (int i = 0; i < 1000; i++) {
		//
		//
		// }
		// for (int i = 0; i < ss.length; i++) {
		// // System.out.println("ss[" + i + "]=====" + ss[i]);
		// System.out.println("mm--num[" + i + "]=====" + generateOrderNum());
		// }
	}
}