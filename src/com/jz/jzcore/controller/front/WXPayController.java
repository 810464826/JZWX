package com.jz.jzcore.controller.front;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.jfinal.kit.PropKit;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.jz.web.common.controller.ControllerPath;

@ControllerPath(controllerKey = "/front/wxpay")
public class WXPayController extends FrontController {
	/** 发红包 */
	public void index() throws Exception {

		int money = (int) (10 * (Math.random()));
		String money1 = money + "";
		String money2 = "1" + "." + money1;

		Map<String, String> map = new HashMap<String, String>();
		// 随机字符串
		String nonceStr = UUID.randomUUID().toString().substring(0, 32);

		// 商户号
		String mch_id = PropKit.use("system.properties").get("mch_id");

		// 订单号mch_id+yyyymmdd+10位一天内不能重复的数字
		String mch_billno = mch_id + getDate();// +RandomNumber();
		String rand = RandomNumber();
		mch_billno = mch_billno + "" + rand;
		// APPID
		String appid = PropKit.use("system.properties").get("appId");

		// 商户名称
		String send_name = PropKit.use("system.properties").get("send_name");// "富贵吉祥酒&文化艺术酒";

		// 接收用户的Openid
		String openid = getOpenId();

		// 支付金额
		BigDecimal re1 = new BigDecimal(money2);
		BigDecimal re2 = new BigDecimal(Float.toString(100.00f));
		Float aa = re1.multiply(re2).floatValue();
		String total_amount = String.valueOf(aa);
		String[] smill = total_amount.split("\\.");
		total_amount = smill[0];

		// 红包发放总人数
		String total_num = PropKit.use("system.properties").get("total_num");

		// 红包祝福语
		String wishing = PropKit.use("system.properties").get("wishing");

		// 调用机器的IP地址
		String client_ip = PropKit.use("system.properties").get("client_ip");

		// 活动名称
		String act_name = PropKit.use("system.properties").get("act_name");

		// 备注
		String remark = PropKit.use("system.properties").get("remark");
		map.put("wxappid", appid);
		System.out.println("这是appid" + appid);
		map.put("mch_id", mch_id);
		System.out.println("这是mch_id" + mch_id);
		map.put("nonce_str", nonceStr);
		System.out.println("这是nonce_str" + nonceStr);
		map.put("mch_billno", mch_billno);
		System.out.println("这是act_name" + act_name);
		map.put("send_name", send_name);
		System.out.println("这是send_name" + send_name);
		map.put("re_openid", openid);
		System.out.println("这是openid" + openid);
		map.put("total_amount", total_amount);
		System.out.println("这是total_amount" + total_amount);
		map.put("total_num", total_num);
		System.out.println("这是total_num" + total_num);
		map.put("wishing", wishing);
		System.out.println("这是wishing" + wishing);
		map.put("client_ip", client_ip);
		System.out.println("这是client_ip" + client_ip);
		map.put("act_name", act_name);
		System.out.println("这是act_name" + act_name);
		map.put("remark", remark);
		System.out.println("这是remark" + remark);
		String key = PropKit.use("system.properties").get("key");
		System.out.println("这是密钥" + key);
		String sign = getPayCustomSign(map, key);
		System.out.println("签名：" + sign);
		map.put("sign", sign);
		String xml = ArrayToXml(map);
		System.out.println("这里是XML" + xml);
		String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
		String response = ssl(url, xml);
		// String xmlStr = HttpKit.post(url, xml);
		System.out.println(response);
		if (response.indexOf("SUCCESS") != -1) {
			Map<String, String> map2 = doXMLParse(response);
			System.out.println(map2);
		}
		Map<String, Object> mapmoney = new HashMap<String, Object>();
		// 返回前端发送的金额
		mapmoney.put("money", money2);
		renderJson(mapmoney);
	}

	/**
	 * 获取支付所需签名
	 * 
	 * @param ticket
	 * @param timeStamp
	 * @param card_id
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static String getPayCustomSign(Map<String, String> bizObj, String key)
			throws Exception {
		System.out.println("支付签名的MAP" + bizObj.toString());
		System.out.println("支付签名的KEY" + key);
		String bizString = FormatBizQueryParaMap(bizObj, false);
		return sign(bizString, key);
	}

	/**
	 * 字典排序
	 * 
	 * @param paraMap
	 * @param urlencode
	 * @return
	 * @throws Exception
	 */
	public static String FormatBizQueryParaMap(Map<String, String> paraMap,
			boolean urlencode) throws Exception {
		System.out.println("字典排序1" + paraMap.toString());
		System.out.println("字典排序2" + urlencode);
		String buff = "";
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
					paraMap.entrySet());
			Collections.sort(infoIds,
					new Comparator<Map.Entry<String, String>>() {
						public int compare(Map.Entry<String, String> o1,
								Map.Entry<String, String> o2) {
							return (o1.getKey()).toString().compareTo(
									o2.getKey());
						}
					});
			for (int i = 0; i < infoIds.size(); i++) {
				Map.Entry<String, String> item = infoIds.get(i);
				// System.out.println(item.getKey());
				if (item.getKey() != "") {
					String key = item.getKey();
					String val = item.getValue();
					if (urlencode) {
						val = URLEncoder.encode(val, "utf-8");
					}
					buff += key + "=" + val + "&";
				}
			}
			if (buff.isEmpty() == false) {
				buff = buff.substring(0, buff.length() - 1);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		System.out.println("字典排序3" + buff);
		return buff;
	}

	public static String sign(String content, String key) throws Exception {
		System.out.println("拼接1" + content);
		System.out.println("拼接2" + key);
		String signStr = "";
		signStr = content + "&key=" + key;
		System.out.println("拼接3" + signStr);
		return MD5(signStr).toUpperCase();
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("utf-8");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			System.out.println("MD5加密" + new String(str));
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String ArrayToXml(Map<String, String> arr) {
		System.out.println("数组ArrayToXml+1" + arr);
		String xml = "<xml>";
		Iterator<Entry<String, String>> iter = arr.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String val = entry.getValue();
			System.out.println("数组ArrayToXml+key" + key);
			System.out.println("数组ArrayToXml+val" + val);
			if (IsNumeric(val)) {
				xml += "<" + key + ">" + val + "</" + key + ">";
			} else
				xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
		}
		xml += "</xml>";
		// try {
		// return new String(xml.toString().getBytes(), "ISO8859-1");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return xml;
	}

	public static boolean IsNumeric(String str) {
		if (str.matches("\\d *")) {
			return true;
		} else {
			return false;
		}
	}

	private Map<String, String> doXMLParse(String xml)
			throws XmlPullParserException, IOException {

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

		Map<String, String> map = null;

		XmlPullParser pullParser = XmlPullParserFactory.newInstance()
				.newPullParser();

		pullParser.setInput(inputStream, "UTF-8");// 为xml设置要解析的xml数据

		int eventType = pullParser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				map = new HashMap<String, String>();
				break;

			case XmlPullParser.START_TAG:
				String key = pullParser.getName();
				if (key.equals("xml"))
					break;

				String value = pullParser.nextText();
				map.put(key, value);

				break;

			case XmlPullParser.END_TAG:
				break;

			}

			eventType = pullParser.next();

		}

		return map;
	}

	// 10位随机数
	public static String RandomNumber() {
		int a[] = new int[10];
		String number = "";
		for (int i = 0; i < a.length; i++) {
			a[i] = (int) (10 * (Math.random()));
			number += a[i] + "";
		}
		return number + "";
	}

	// 订单号中的时间
	public String getDate() {
		Date dete = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyymmdd");
		String dateString = formatter.format(dete);
		System.out.println(dateString);
		return dateString;
	}

	private String ssl(String url, String data) {
		StringBuffer message = new StringBuffer();
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream instream = new FileInputStream(new File(
					"D:/cert/apiclient_cert.p12"));
			// 商户号
			String mch_id = PropKit.use("system.properties").get("mch_id");
			keyStore.load(instream, mch_id.toCharArray());
			// Trust own CA and all self-signed certs
			SSLContext sslcontext = SSLContexts.custom()
					.loadKeyMaterial(keyStore, mch_id.toCharArray()).build();
			// Allow TLSv1 protocol only
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1" },
					null,
					SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			CloseableHttpClient httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
			HttpPost httpost = new HttpPost(url);

			httpost.addHeader("Connection", "keep-alive");
			httpost.addHeader("Accept", "*/*");
			httpost.addHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httpost.addHeader("Host", "api.mch.weixin.qq.com");
			httpost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpost.addHeader("Cache-Control", "max-age=0");
			httpost.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			System.out.println("executing request" + httpost.getRequestLine());

			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				HttpEntity entity = response.getEntity();

				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				if (entity != null) {
					System.out.println("Response content length: "
							+ entity.getContentLength());
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(entity.getContent(), "UTF-8"));
					String text;
					while ((text = bufferedReader.readLine()) != null) {
						message.append(text);
					}

				}
				EntityUtils.consume(entity);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				response.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return message.toString();
	}
}
