package com.jz.jzcore.controller.front;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jfinal.plugin.activerecord.Record;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.ActivityOrder;
import com.jz.jzcore.model.ActivityReport;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.ProvincesCount;
import com.jz.jzcore.model.QRcodeInfo;
import com.jz.jzcore.model.SellerOutInfo;
import com.jz.jzcore.model.SystemArea;
import com.jz.jzcore.model.SystemOffice;
import com.jz.jzcore.model.SystemUser;
import com.jz.jzcore.model.TurntableRaffle;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.jzcore.model.WxUserInfo;
import com.jz.web.common.controller.ControllerPath;

@ControllerPath(controllerKey = "/sign")
public class SignController extends FrontController {
	private final Logger log = LoggerFactory.getLogger(SignController.class);

	/**
	 * 进入扫码赢大奖页面
	 */
	public void index() {
		// 瓶码
		String w = getPara("w");
		log.info("index()该瓶码QRcode是****>:" + w);
		try {
			WXConfigController wxconfig = new WXConfigController();
			String appid = wxconfig.getAppId();
			String http = wxconfig.getHttp();
			log.info("==========http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid
					+ "&redirect_uri=http%3a%2f%2f" + http + "%2fJZWX%2fsign%2fsign%3fw%3d" + w
					+ "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect");
			getResponse().sendRedirect("http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid
					+ "&redirect_uri=http%3a%2f%2f" + http + "%2fJZWX%2fsign%2fsign%3fw%3d" + w
					+ "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect");
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}

	/**
	 * 扫码赢大奖页面
	 */
	public void sign() {
		String openid = getOpenId();
		log.info("openid****>:" + openid);
		String QRcode = getPara("w");
		log.info("sign()该瓶码QRcode是****>:" + QRcode);
		// 根据该瓶码查询出该运单
		String getSellerOrderByPingCodeId = PropKit.use("database.properties").get("getSellerOrderByPingCodeId");
		log.info("sign()  getSellerOrderByPingCodeId是****>:" + getSellerOrderByPingCodeId);
		List<Record> soiView = Db.find(getSellerOrderByPingCodeId, QRcode);
		List<SellerOutInfo> soiList = new ArrayList<SellerOutInfo>();
		for (Record cord : soiView) {
			SellerOutInfo soi = new SellerOutInfo();
			soi.mapping(cord, soi);
			soiList.add(soi);
		}
		log.info("该瓶码所属的运单是****>:" + soiList);
		// 根据该瓶码查询出该运单
		String getQrCodeValidate = PropKit.use("database.properties").get("getQrCodeValidate");
		List<Record> soiView1 = Db.find(getQrCodeValidate, QRcode);
		List<String> soiList1 = new ArrayList<String>();
		for (Record cord : soiView1) {
			String soi = cord.getStr("VERIFICATIONCODE");
			soiList1.add(soi);
		}
		String resultValidate = soiList1.get(0);
		log.info("该瓶码所属的验证码是****>:" + resultValidate);
		// **************************************************************
		// 判断当前用户是否关注公众号
		WXConfigController wxcofig = new WXConfigController();
		WxUserInfo info = wxcofig.followByOpenid(openid);
		log.info("该用户信息info****>:" + info);
		String exchange = "";
		if (info.getSubscribe().equals("1")) {
			// 判断当前用户是否已经是会员，是就不操作，不是就新建为会员
			wxcofig.getUserByopenid(openid);
			String getQrcodeByQRId = PropKit.use("database.properties").get("getQrcodeByQRId");
			QRcodeInfo rcodeInfo = QRcodeInfo.dao.findFirst(getQrcodeByQRId, QRcode);
			log.info("rcodeInfo****>:" + rcodeInfo);
			// 查询该瓶码是否存在 存在了则修改扫码次数就好了
			if (rcodeInfo != null) {
				Number qRcodeNumber = rcodeInfo.getQRcodeNumber();
				boolean update = rcodeInfo.set("ID", rcodeInfo.getId())
						.set("QRCODE_NUMBER", qRcodeNumber.intValue() + 1).update();
				exchange = rcodeInfo.getExchange();
				log.info("update****>:" + update);
			} else {
				String i = "";
				// 1是箱 2是瓶
				String boxCode = soiList.get(0).getBoxCode();
				if (boxCode == null || boxCode.equals("")) {
					i = "2";
				} else {
					i = "1";
				}
				log.info("boxCode****>:" + boxCode);
				String outNo = soiList.get(0).getOutNo();
				Number recordId = soiList.get(0).getRecordId();
				log.info("outNo****>:" + outNo);
				// 记录扫码次数 保存该二维码的信息
				QRcodeInfo r = new QRcodeInfo();
				boolean save = r.set("ID", UUID.randomUUID().toString().replaceAll("-", "")).set("QRCODE_NUMBER", 1)
						.set("BOX_ID", boxCode).set("VERIFICATION_CODE", resultValidate).set("QRCODE_ID", QRcode)
						// 状态：1可使用 2不可使用
						.set("STATE", 1)
						// 运单号
						.set("DELIVER_GOODS_ID", outNo).set("INFO", i).set("EXCHANGE", 0).set("CREATE_TIME", soiList.get(0).getOutDate())
						.set("BOXCODE", QRcode)
						.set("RECORDID", recordId).save();
				log.info("save****>:" + save);
				exchange = "0";
			}
		}
		// 是否未关注，值为0时未关注，1已关注
		setAttr("Subscribe", info.getSubscribe());
		setAttr("QRcode", QRcode);
		setAttr("exchange", exchange);
		render("/front/verification-code.html");
	}

	/**
	 * 验证四位数是否正确
	 */
	public void Verification() {
		// 扫码编号
		String QRcode = getPara("QRcode");
		// 四位验证码
		String validate = getPara("validate");
		String getQrcodeByQRId = PropKit.use("database.properties").get("getQrcodeByQRId");
		String back = "";
		String qrcodeid = "";
		QRcodeInfo qrcode = QRcodeInfo.dao.findFirst(getQrcodeByQRId, QRcode);
		log.info("qrcode***********>:" + qrcode);
		// 判断数据库中的验证码是否和用户输入严重码是否相同
		if (qrcode.getVerificationCode().equals(validate)) {
			log.info("save***********>:" + "验证码匹配！");
			// 相同做处理，判断是否兑换
			if (qrcode.getExchange().equals("1")) {
				back = "已兑换";
			} else {
				back = "OK";
				qrcodeid = qrcode.getId();
			}
			// 输入四位验证码和数据库不匹配
		} else {
			back = "不匹配";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("back", back);
		map.put("qrcodeid", qrcodeid);
		renderJson(map);
	}

	// 保存扫码记录与位置信息
	public void saveSacnAddress() {
		boolean rutles = false;
		String openid = getOpenId();
		// 扫码编号
		String QRcode = getPara("QRcode");
		// 省
		String province = getPara("province");
		// 市
		String city = getPara("city");
		// 区
		String district = getPara("district");
		// 街道
		String street = getPara("street");
		// 经度
		String longitude = getPara("longitude");
		// 纬度
		String latitude = getPara("latitude");
		// ******************************====根据瓶码查询运单开始=====***************************************
		// 根据该瓶码查询出该运单
		String getSellerOrderByPingCodeId = PropKit.use("database.properties").get("getSellerOrderByPingCodeId");
		List<Record> soiView = Db.find(getSellerOrderByPingCodeId, QRcode);
		List<SellerOutInfo> soiList = new ArrayList<SellerOutInfo>();
		for (Record cord : soiView) {
			SellerOutInfo soi = new SellerOutInfo();
			soi.mapping(cord, soi);
			soiList.add(soi);
		}
		log.info("该瓶码所属的运单是****>:" + soiList);
		// ******************************====根据瓶码查询运单结束=====***************************************
		SellerOutInfo sellerOutInfo = soiList.get(0);
		// 这是运单号
		Number recordId = sellerOutInfo.getRecordId();
		// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
		String sql = "SELECT * FROM ACTIVITYORDER WHERE RECORDID = ? AND  STATE = 1";
		ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql, recordId);
		// ******************************=====根据运单查活动=====***************************************
		if (activityOrder != null) {
			// 该运单处于活动中
			String activityId = activityOrder.getActivityId();
			// 找到活动
			ActivityReport ar = ActivityReport.dao.findById(activityId);
			// 找到当前活动发起人
			SystemUser user = SystemUser.dao.findById(ar.getApplyUser());
			// 找到当前经销商
			SystemOffice office = SystemOffice.dao.findById(user.getOfficeId());
			System.out.println("office的ID" + office.getId());
			// 找到地区
			SystemArea area = SystemArea.dao.findById(office.getAreaId());
			System.out.println("area的ID" + area.getId());
			// 记录是否异地或本地 0属于本地 1不属于本地
			int outProvinces = 0;
			// 循环条件
			int wh = 0;
			while (wh == 0) {
				// 判断是否属于国家下的第一个一级单位，国家-省-市排序
				if (area.getParentId().equals("1") || area.getParentId().equals("0")) {
					// 省级单位 ,判断当前的省是否和我获取到省相同
					System.out.println("活动经销商省份" + area.getName() + "获取" + province);
					if (area.getName().equals(province)) {
						outProvinces = 0;
						wh = 1;
					} else {
						outProvinces = 1;
						wh = 1;
					}
				} else {
					area = SystemArea.dao.findById(area.getParentId());
				}
			}
			// 通过Code找到所扫二维码，然后找到活动经销商，获取经销商在哪个省，活动属于那个省
			String getQrcodeByQRId = PropKit.use("database.properties").get("getQrcodeByQRId");
			QRcodeInfo qrcode = QRcodeInfo.dao.findFirst(getQrcodeByQRId, QRcode);
			rutles = new ProvincesCount().set("ID", UUID.randomUUID().toString().replaceAll("-", "")).set("CITY", city)
					.set("SCAN_TIME", newDateByY()).set("OPENID", openid).set("ACTIVITY_PRIZES_NAME", office.getId())
					.set("ACTIVITY_PRIZES_ADDRESS", area.getId()).set("ACTIVITY_PRIZES_ID", user.getId())
					.set("ACTIVITY_ID", ar.getId()).set("CODEID", qrcode.getId())
					.set("OUT_PROVINCES", outProvinces + "").set("PROVINCE", province).set("DISTRICT", district)
					.set("ROAD", street).set("LONGITUDE", longitude).set("LATITUDE", latitude).save();
		} else {
			// 该运单并没有开启活动
			System.out.println("未发起活动不做记录");
		}
		renderJson(rutles);
	}

	/**
	 * 生成奖品，并进入领奖页面
	 */
	// 加入事物后操作某一个数据时上锁，事物级别高，第二人不能查看，事物级别低，第二人可查看。事物提交就是方法结束事物算提交
	public void Award() {
		log.info("Award()***********>:" + "生成奖品，并进入领奖页面！！！");
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		String url = "";
		Connection conn = null;
		try {
			conn = sqlconfig.bengin();
			String openid = getOpenId();
			boolean restul = false;
			ProductInfo info = new ProductInfo();
			// 所扫码的ID
			String id = getPara("codeId");
			log.info("所扫码的ID***********>:" + id);
			// 记录是积分还是物流
			String information = "";
			String getQrcodeById = PropKit.use("database.properties").get("getQrcodeById");
			log.info("getQrcodeById***********>:" + getQrcodeById);
			// 根据id查询出该二维码的信息
			QRcodeInfo qr = dao.getQRcodeInfoById(conn, getQrcodeById, id);
			log.info("qr***********>:" + qr);
			// 判断扫码是否已生成奖品但是未领取 判断扫码是否在活动中
			if (qr.getFinalPrize() == null || qr.getFinalPrize().equals("")) {
				// ******************************====根据瓶码查询运单开始=====***************************************
				// 根据该瓶码查询出该运单
				String getSellerOrderByPingCodeId = PropKit.use("database.properties")
						.get("getSellerOrderByPingCodeId");
				List<Record> soiView = Db.find(getSellerOrderByPingCodeId, qr.getBoxCode());
				List<SellerOutInfo> soiList = new ArrayList<SellerOutInfo>();
				for (Record cord : soiView) {
					SellerOutInfo soi = new SellerOutInfo();
					soi.mapping(cord, soi);
					soiList.add(soi);
				}
				log.info("该瓶码所属的运单是****>:" + soiList);
				// ******************************====根据瓶码查询运单结束=====***************************************
				SellerOutInfo sellerOutInfo = soiList.get(0);
				// 这是运单号记录ID
				Number recordId = sellerOutInfo.getRecordId();
				// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
				String sql = "SELECT * FROM ACTIVITYORDER WHERE RECORDID = ? AND  STATE = 1";
				ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql, recordId);
				// ******************************=====根据运单查活动=====***************************************
				if (activityOrder == null) {
					log.info("没有开启活动****>");
					// 没有开启活动 ***************************************这里修改所送积分
					info = updateByIntegral();
					log.info("领取的积分产品是****>:" + info);
					information = "积分";
					// 记录所中奖品，但未领取时
					restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId()).update();
					log.info("修改该二维码中奖的情况****>:" + restul);
				} else {
					log.info("活动中****>");
					// 扫描在活动中，查询出活动报备,未通过的
					ActivityReport activity = ActivityReport.dao.findById(activityOrder.getActivityId());
					log.info("该二维码查找的运单处于的活动是****>:" + activity);
					// 不等于已通过
					if (activity.getState() != 2) {
						// 赠送积分
						info = updateByIntegral();
						log.info("领取的积分产品是****>:" + info);
						restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId()).update();
						log.info("修改该二维码中奖的情况****>:" + restul);
						information = "积分";
					} else {
						// 判断当前活动是否已超出活动时间 当前时间小于活动时间返回TRUE .当前时间大于活动时间返回false
						boolean time = isDateBefore(activity.getUpdateTime());
						log.info("time是不是在活动时间内****>:" + time);
						if (time) {
							/*log.info("活动中，时间内****>");
							// 已通过，获得属于当前活动的奖品,剩余奖品的总数
							int number = PrizeNumber(activity.getId());
							//如果奖品数量为0时，则送积分了 
							//******************************************这是修改的************
							if(number==0){
								// 赠送积分
								info = updateByIntegral();
								restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId()).update();
								information = "积分";
								restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId())
										.update();
								log.info("修改该二维码中奖的情况****>:" + restul);
							}else{
								log.info("该活动所剩下的奖品数量****>:" + number);
								info = logisticsOrCard(activity.getId(), number);
								log.info("info******************>:" + info);
								// ******这行的意义？
//								qr.setWinningNumber(Integer.parseInt(info.getProductsize()));
								// 记录所中奖品，但未领取时
								restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId())
										.set("WINNING_NUMBER", info.getProductsize()).update();
								log.info("修改该二维码中奖的情况****>:" + restul);
							}
							//******************************************这是以前的************
							log.info("该活动所剩下的奖品数量****>:" + number);
							info = logisticsOrCard(activity.getId(), number);
							log.info("info******************>:" + info);
							// 判断是否属于积分奖品
							if (info.getType().equals("2")) {
								information = "积分";
							}*/
							log.info("活动中，时间内****>");
							// 已通过，获得属于当前活动的奖品,剩余奖品的总数
							int number = PrizeNumber(activity.getId());
							log.info("该活动所剩下的奖品数量****>:" + number);
							info = logisticsOrCard(activity.getId(), number);
							log.info("info******************>:" + info);
							// 判断是否属于积分奖品
							if (info.getType().equals("2")) {
								information = "积分";
							}
							// ******这行的意义？
							qr.setWinningNumber(Integer.parseInt(info.getProductsize()));
							// 记录所中奖品，但未领取时
							restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId())
									.set("WINNING_NUMBER", info.getProductsize()).update();
							log.info("修改该二维码中奖的情况****>:" + restul);
						} else {
							// 赠送积分
							info = updateByIntegral();
							restul = new QRcodeInfo().set("ID", qr.getId()).set("FINAL_PRIZE", info.getId()).update();
							information = "积分";
						}
					}
				}
			} else if (qr.getExchange().equals("0")) {
				// 已有奖品但是未领取
				info = ProductInfo.dao.findById(qr.getFinalPrize());
				log.info("已有奖品但是未领取****>:" + info);
				if (info.getType().equals("2")) {
					information = "积分";
				} else if (info.getType().equals("1")) {
					information = "卡券";
				} else if (info.getType().equals("3")) {
					information = "物流";
				}
			}
			// 判断是否属于积分或者物流，返回地址不同
			if (information.equals("积分")) {
				// 获取扫描不在活动的赠送的积分  **********************
				/*String sqlintergral = "SELECT * FROM INTEGRAL_RULE WHERE \"NUMBER\" = ?";
				List<IntegralRule> rule = IntegralRule.dao.find(sqlintergral, "scan");
				IntegralRule integralrule = new IntegralRule();
				for (IntegralRule ir : rule) {
					integralrule = ir;
				}*/
				//根据瓶码直接获取积分
				String sql="select  plsdbflat.pack_public.GetWlyScore(?) as integral from dual";
				log.info("根据瓶码为："+qr.getBoxCode());
				List<Record> soiView1 = Db.find(sql,  qr.getBoxCode());
				List<String> soiList1 = new ArrayList<String>();
				for (Record cord : soiView1) {
					String soi = cord.getStr("INTEGRAL");
					soiList1.add(soi);
				}
				log.info("根据瓶码获取soiList1为："+soiList1);
				String integral = soiList1.get(0);
				log.info("根据瓶码获取积分为："+integral);
				setAttr("integralRule", integral);
				url = "/front/getAward.html";// 积分地址
			} else {
				url = "/front/getLogistics.html";// 卡券物流地址
			}
			if (restul) {
				log.info("记录中奖奖品是否成功" + restul);
			}
			setAttr("codeId", id);
			setAttr("openid", openid);
			setAttr("PrizeNumber", qr.getWinningNumber());
			setAttr("ProductInfoId", info.getId());
			setAttr("ProductInfo", info);
			// 5. 事务提交
			// conn.commit();
		} catch (NestedTransactionHelpException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			LogKit.logNothing(e);
		} catch (Throwable t) {
			// 6. 若有异常就回滚
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			throw t instanceof RuntimeException ? (RuntimeException) t : new ActiveRecordException(t);
		} finally {
			try {
				if (conn != null) {
					if (sqlconfig.autoCommit != null)
						conn.setAutoCommit(sqlconfig.autoCommit);
					conn.close();
				}
			} catch (Throwable t) {
				LogKit.error(t.getMessage(), t);
			} finally {
				sqlconfig.config.removeThreadLocalConnection();
			}
		}
		render(url);
	}

	/**
	 * 根据概率和剩余数量生成卡券或物流商品 number多少未中奖的码
	 */
	public ProductInfo logisticsOrCard(String id, int number) {
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		ProductInfo product = new ProductInfo();
		Connection conn = null;
		try {
			conn = sqlconfig.bengin();
			String sql = "SELECT * FROM TURNTABLE_RAFFLE WHERE ACTIVITY_ID = ? ";
			List<TurntableRaffle> turntablelist = dao.findTurntableRaffle(conn, sql, id);
			// 统计剩余数量
			int SurplusNumber = 0;
			Float Spare = (float) 0.01;
			// 已有奖品概率
			Float prizeChance = (float) 0.00;
			// 积分概率 奖品不是百分百中奖，就会产生积分
			float num = (float) 0.00;
			DecimalFormat df = new DecimalFormat("##0.00");// 格式化小数
			for (TurntableRaffle turntable : turntablelist) {
				// 剩余数量
				SurplusNumber += turntable.getSurplusNumber();
				// 已有奖品概率
				prizeChance = prizeChance + Float.parseFloat(turntable.getWinningProbability());
			}

			// 判断统计数量是否和剩余可中奖数量相等，相等说明所有都可以中卡券或物流奖品
			if (SurplusNumber != number) {
				// 剩余总数减去统计的剩余数量，得到积分数量
				int Integral = number - SurplusNumber;
				// 算出积分的概率
				num = (float) Integral / (float) number * 100;
				String IntegralProbability = df.format(num);// 返回的是String类型
				// 概率
				TurntableRaffle turntable = new TurntableRaffle();
				turntable.setId("88888888");
				turntable.setPrizeName("积分");
				turntable.setPrizeNumber(Integral);
				turntable.setSurplusNumber(Integral);
				turntable.setWinningNumber(1);
				Float totalProbability = prizeChance + Float.parseFloat(IntegralProbability);
				// 判断是否等于100%
				if (totalProbability == 100) {
					turntable.setWinningProbability(IntegralProbability);
					turntable.setNewProbability(IntegralProbability);
				} else if (totalProbability > 100) {
					String SpareNumber = df.format(num - Spare);// 返回的是String类型
					turntable.setWinningProbability(SpareNumber);
					turntable.setNewProbability(IntegralProbability);
				} else if (totalProbability < 100) {
					String SpareNumber = df.format(num + Spare);// 返回的是String类型
					turntable.setWinningProbability(SpareNumber);
					turntable.setNewProbability(IntegralProbability);
				}
				turntablelist.add(turntable);
			}
			// 总的概率区间
			float totalPro = 0f;
			// 存储每个奖品新的概率区间
			List<Float> proSection = new ArrayList<Float>();
			proSection.add(0f);
			// 记录所中奖项
			TurntableRaffle turntableRaffle;
			// 遍历每个奖品，设置概率区间，总的概率区间为每个概率区间的总和
			for (TurntableRaffle turn : turntablelist) {
				System.out.println("当前概率" + turn.getNewProbability());
				// 当前中奖概率
				Float probability = Float.parseFloat(turn.getNewProbability());
				// 每个概率区间为奖品概率乘以10000（把三位小数转换为整）再乘以剩余奖品数量
				totalPro += probability * 1000 * turn.getSurplusNumber();
				proSection.add(totalPro);
			}
			// 获取总的概率区间中的随机数
			Random random = new Random();
			float randomPro = (float) random.nextInt((int) totalPro);
			// 判断取到的随机数在哪个奖品的概率区间中
			for (int i = 0, size = proSection.size(); i < size; i++) {
				if (randomPro >= proSection.get(i) && randomPro < proSection.get(i + 1)) {
					turntableRaffle = turntablelist.get(i);
					// 判断中的奖品还是积分
					if (turntableRaffle.getPrizeName().equals("积分")) {
						product = updateByIntegral();
					} else {
						product = ProductInfo.dao.findById(turntableRaffle.getPrizeName());
						// 获奖数量
						// 特别备注：此处使用ProductInfo中的getProductsize，临时存放当前一次扫码的获奖数量
						product.setProductsize(turntableRaffle.getWinningNumber() + "");
						// 总数减去一个
						number = number - 1;
						// 修改当前奖品的剩余数量
						turntablelist.get(i).setSurplusNumber(turntableRaffle.getSurplusNumber() - 1);
						// 统计修改后的概率
						Float allprobability = (float) 0.00;
						Float duoyu = (float) 0.01;
						String probability = null;
						// 重新算出所有奖品概率 turntablelist所有奖品概率，包括积分奖品
						for (TurntableRaffle reffle : turntablelist) {
							// 判断是否都是0
							if (number == 0 | reffle.getSurplusNumber() == 0) {
								probability = "0.00";
							} else {
								// 重算后的概率
								probability = algorithm(reffle.getSurplusNumber(), number);
								if (Float.parseFloat(probability) == 100 | Float.parseFloat(probability) == 0) {
									allprobability = Float.parseFloat(probability);
								} else {
									allprobability = allprobability + Float.parseFloat(probability);
								}
							}
							// 判断是否等于100%
							if (allprobability <= 100) {
								reffle.setWinningProbability(probability);
							} else if (allprobability > 100) {
								String SpareNumber = df.format(Float.parseFloat(probability) - duoyu);// 返回的是String类型
								reffle.setWinningProbability(SpareNumber);
							}
							// 根据ID，保存当前概率，积分除外
							if (!reffle.getPrizeName().equals("积分")) {
								new TurntableRaffle().set("ID", reffle.getId())
										.set("SURPLUS_NUMBER", reffle.getSurplusNumber())
										.set("NEWPROBABILITY", reffle.getWinningProbability()).update();
							}
						}
					}
				}
			}
			// 5. 事务提交
			conn.commit();
		} catch (NestedTransactionHelpException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			LogKit.logNothing(e);
		} catch (Throwable t) {
			// 6. 若有异常就回滚
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			throw t instanceof RuntimeException ? (RuntimeException) t : new ActiveRecordException(t);
		} finally {
			try {
				if (conn != null) {
					if (sqlconfig.autoCommit != null)
						conn.setAutoCommit(sqlconfig.autoCommit);
					conn.close();
				}
			} catch (Throwable t) {
				LogKit.error(t.getMessage(), t);
			} finally {
				sqlconfig.config.removeThreadLocalConnection();
			}
		}
		return product;
	}

	/**
	 * 算概率
	 */
	public String algorithm(int SurplusNumber, int number) {
		Float nowprize = (float) SurplusNumber / (float) number * 100;
		DecimalFormat df = new DecimalFormat("##0.00");// 格式化小数
		String probability = df.format(nowprize);// 返回的是String类型
		return probability;
	}

	/**
	 * 领取积分
	 */
	public void receiveIntegral() {
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		boolean restul = false;
		Connection conn = null;
		try {
			conn = sqlconfig.bengin();
			String openId = getPara("openId");
			System.out.println("领取积分OPENID" + openId);
			String codeId = getPara("codeId");
			String ProductInfoId = getPara("ProductInfoId");
			//获得的积分
			String winIntegral = getPara("integral");
			log.info("获取的积分***>"+winIntegral);
			// String sqlinfo = "SELECT * FROM QRCODE_INFO WHERE ID = ?";
			String getQrcodeById = PropKit.use("database.properties").get("getQrcodeById");
			QRcodeInfo qrcodeInfo = dao.getQRcodeInfoById(conn, getQrcodeById, codeId);
			ProductInfo product = ProductInfo.dao.findById(ProductInfoId);
			// 判断是否已兑换
			if (qrcodeInfo.getExchange().equals("0")) {
				// 获取扫描不在活动的赠送的积分
				/*String sqlintergral = "SELECT * FROM INTEGRAL_RULE WHERE \"NUMBER\" = ?";
				List<IntegralRule> rule = IntegralRule.dao.find(sqlintergral, "scan");
				IntegralRule integralRule = new IntegralRule();
				for (IntegralRule ir : rule) {
					integralRule = ir;
				}*/
				// ******************************====根据瓶码查询运单开始=====***************************************
				// 根据该瓶码查询出该运单
				String getSellerOrderByPingCodeId = PropKit.use("database.properties")
						.get("getSellerOrderByPingCodeId");
				List<Record> soiView = Db.find(getSellerOrderByPingCodeId, qrcodeInfo.getBoxCode());
				List<SellerOutInfo> soiList = new ArrayList<SellerOutInfo>();
				for (Record cord : soiView) {
					SellerOutInfo soi = new SellerOutInfo();
					soi.mapping(cord, soi);
					soiList.add(soi);
				}
				log.info("该瓶码所属的运单是****>:" + soiList);
				// ******************************====根据瓶码查询运单结束=====***************************************
				SellerOutInfo sellerOutInfo = soiList.get(0);
				// 这是运单号 recordId
				Number recordId = sellerOutInfo.getRecordId();
				// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
				String sql = "SELECT * FROM ACTIVITYORDER WHERE RECORDID = ? AND  STATE = 1";
				ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql, recordId);
				// ******************************=====根据运单查活动=====***************************************
				String activityId = null;
				if (activityOrder != null) {
					activityId = activityOrder.getActivityId();
				}
				//这就是获取得到的积分
				int integral = Integer.parseInt(winIntegral);
				// 查询出当前会员的积分
				String sql1 = "SELECT * FROM INTEGRAL_VIP  WHERE OPENID = ? ";
				List<IntegralVip> vip = IntegralVip.dao.find(sql1, openId);
				for (IntegralVip integralVip : vip) {
					// 中小麦
					if (product.getName().equals("小麦")) {
						restul = new IntegralVip().set("ID", integralVip.getId())
								.set("WHRAT", integralVip.getWhrat() + integral)
								.set("ALL_INTEGRAL", integralVip.getAllIntegral() + integral).update();
					}
					// 中大米
					if (product.getName().equals("大米")) {
						restul = new IntegralVip().set("ID", integralVip.getId())
								.set("RICE", integralVip.getRice() + integral)
								.set("ALL_INTEGRAL", integralVip.getAllIntegral() + integral).update();
					}
					// 中玉米
					if (product.getName().equals("玉米")) {
						restul = new IntegralVip().set("ID", integralVip.getId())
								.set("CORN", integralVip.getCorn() + integral)
								.set("ALL_INTEGRAL", integralVip.getAllIntegral() + integral).update();
					}
					// 中高粱
					if (product.getName().equals("高粱")) {
						restul = new IntegralVip().set("ID", integralVip.getId())
								.set("SORGHUM", integralVip.getSorghum() + integral)
								.set("ALL_INTEGRAL", integralVip.getAllIntegral() + integral).update();
					}
					// 中糯米
					if (product.getName().equals("糯米")) {
						restul = new IntegralVip().set("ID", integralVip.getId())
								.set("GLUTINOUSRICE", integralVip.getGlutinousrice() + integral)
								.set("ALL_INTEGRAL", integralVip.getAllIntegral() + integral).update();
					}
				}
				// 修改为已兑换
				new QRcodeInfo().set("ID", qrcodeInfo.getId()).set("EXCHANGE", "1").update();
				// 保存中奖信息
				new WinningDelivery().set("ID", UUID.randomUUID().toString().replaceAll("-", ""))
						.set("WINNING_WAY", "1").set("PRIZE_NAME", product.getId())
						.set("WINNING_TIME", Timestamp.valueOf(newDate())).set("OPENID", openId)
						.set("GRAIN_GRAM", integral).set("DISTRIBUTION_STATUS", "3").set("PRIZE_STATUS", "1")
						.set("PRIZE_TYPE", "1").set("ACTIVITY_ID", activityId).set("CODE_ID", codeId)
						.save();
			}
			// 5. 事务提交
			conn.commit();
		} catch (NestedTransactionHelpException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			LogKit.logNothing(e);
		} catch (Throwable t) {
			// 6. 若有异常就回滚
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e1) {
					LogKit.error(e1.getMessage(), e1);
				}
			throw t instanceof RuntimeException ? (RuntimeException) t : new ActiveRecordException(t);
		} finally {
			try {
				if (conn != null) {
					if (sqlconfig.autoCommit != null)
						conn.setAutoCommit(sqlconfig.autoCommit);
					conn.close();
				}
			} catch (Throwable t) {
				LogKit.error(t.getMessage(), t);
			} finally {
				sqlconfig.config.removeThreadLocalConnection();
			}
		}
		renderJson(restul);
	}

	/**
	 * 扫码不在活动中和在活动但是未通过修改积分方法，此处只是记录中奖奖品
	 */
	public ProductInfo updateByIntegral() {
		List<ProductInfo> info = null;
		// 扫码不在活动中，随机给出积分奖品，随机生产1-5的随机数。
		int number = new Random().nextInt(5) + 1;
		String getproduct = "SELECT * FROM PRODUCT_INFO WHERE NAME = ? ";
		// 中小麦
		if (number == 1) {
			info = ProductInfo.dao.find(getproduct, "小麦");
		}
		// 中大米
		if (number == 2) {
			info = ProductInfo.dao.find(getproduct, "大米");
		}
		// 中玉米
		if (number == 3) {
			info = ProductInfo.dao.find(getproduct, "玉米");
		}
		// 中高粱
		if (number == 4) {
			info = ProductInfo.dao.find(getproduct, "高粱");
		}
		// 中糯米
		if (number == 5) {
			info = ProductInfo.dao.find(getproduct, "糯米");
		}
		ProductInfo productInfo = new ProductInfo();
		for (ProductInfo pi : info) {
			productInfo = pi;
		}
		return productInfo;
	}

	/**
	 * 获取活动报备中剩余奖品总数
	 */
	public int PrizeNumber(String id) {
		int number = 0;
		// 根据活动id 查询该活动所有的剩余的奖品数量
		String sql = "SELECT * FROM TURNTABLE_RAFFLE WHERE ACTIVITY_ID = ? ";
		List<TurntableRaffle> list = TurntableRaffle.dao.find(sql, id);
		for (TurntableRaffle t : list) {
			number += t.getSurplusNumber();
		}
		return number;
	}

	/**
	 * 判断时间当前时间是否大于读取的时间
	 */
	public static boolean isDateBefore(Date date2) {
		Date date1 = new Date();// 当前时间
		return date1.before(date2);// date1比date2时间提前
	}

	/**
	 * Date存放Timestamp类型转换String
	 */
	public Date TimestampOrData(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
		String[] time = date.split("\\.");
		Date da = null;
		try {
			da = sdf.parse(time[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return da;
	}

	/**
	 * 时间格式转换
	 */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return time.format(date);
	}

	/**
	 * 时间格式转换 年月
	 */
	public static String newDateByY() {
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM");
		return time.format(date);
	}
}
