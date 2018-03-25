package com.jz.jzcore.controller.front;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.ActivityOrder;
import com.jz.jzcore.model.ActivityReport;
import com.jz.jzcore.model.LogisticeFirm;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.QRcodeInfo;
import com.jz.jzcore.model.SellerOutInfo;
import com.jz.jzcore.model.SystemOffice;
import com.jz.jzcore.model.SystemUser;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.web.common.controller.ControllerPath;

@ControllerPath(controllerKey = "/front/logistics")
public class LogisticsController extends FrontController {
	
	private final Logger log =LoggerFactory.getLogger(LogisticsController.class);
	public void index(){
		String openid = getOpenId();
		System.out.println(openid);
	}
	
	
	/**
	 * 扫码 领取物流奖品    填写了地址
	 * */
	public void getLogisticsAddressfillIn(){
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		boolean exchange = false;
		Connection conn=null;
		try {
			 conn=sqlconfig.bengin();
			//用户opneid
			String openid = getPara("openid");
			//客户所扫的二维码
			String codeId = getPara("codeId");
			//产品ID
			String productId = getPara("productId");
			//地址
			String address = getPara("address");
			log.info("获取前端扫描者的地址为：****>:" + address);
			//姓名
			String name = getPara("name");
			//电话
			String phone = getPara("phone");
			//判断是扫码，兑换，抽奖
			String restul = getPara("restul");
			String getQrcodeById = PropKit.use("database.properties").get("getQrcodeById");
		    QRcodeInfo qr = dao.getQRcodeInfoById(conn,getQrcodeById,codeId);
		    log.info("扫描的二维码的信息****>:" + qr);
		    // ******************************====根据瓶码查询运单开始=====***************************************
			// 根据该瓶码查询出该运单
			String getSellerOrderByPingCodeId = PropKit.use("database.properties").get("getSellerOrderByPingCodeId");
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
			// 这是运单号
			Number recordId = sellerOutInfo.getRecordId();
			// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
			String sql1 = "SELECT * FROM ACTIVITYORDER WHERE RECORDID = ? AND  STATE = 1";
			ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql1, recordId);
			// ******************************=====根据运单查活动=====***************************************
			if(qr.getExchange().equals("0")){
				exchange =true;
				if(restul.equals("1")){
					//修改客户扫的二维码，修改奖品已被领取
					new QRcodeInfo().set("ID", codeId).set("EXCHANGE", "1").update();
					String activityId = activityOrder.getActivityId();
					ActivityReport activity = ActivityReport.dao.findById(activityId);
					String applyUserId = activity.getApplyUser();
					SystemUser user = SystemUser.dao.findById(applyUserId);
					String officeId = user.getOfficeId();
					SystemOffice office = SystemOffice.dao.findById(officeId);
					log.info("applyUserId***>"+applyUserId);
					log.info("ACTIVITY_USERNAME***>"+office.getName());
					log.info("ACTIVITY_ADDRESS***>"+office.getAddressName());
					//保存中奖信息
					boolean save = new WinningDelivery().
							set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
							set("WINNING_WAY","1").
							set("PRIZE_NAME", productId).
							set("WINNING_TIME",Timestamp.valueOf(newDate())).
							set("OPENID",openid).
							set("DISTRIBUTION_STATUS","1").
							set("PRIZE_STATUS","2").
							set("PRIZE_TYPE","3").
							set("CODE_ID", codeId).
							set("COLLECT_ADDRESS", address).
							set("COLLECT_USER", name).
							set("COLLECT_PHONE", phone).
							set("ADDRESS_STATUS", "1").
							set("ACTIVITY_ID",activityOrder.getActivityId())
							.set("ACTIVITY_USERNAME", office.getName())
							.set("ACTIVITY_AREA", activity.getActivityAddress())
							.save();
					 log.info("保存中奖信息是否成功****>:" + save);
				}
				//根据OPENID查询出当前人员的扫码宝贝
				String sql = "select * from WINNING_DELIVERY where OPENID = ? AND WINNING_WAY = 1 ORDER BY WINNING_TIME DESC";
				List<WinningDelivery> winningList = WinningDelivery.dao.find(sql, openid); 
				for(WinningDelivery winning : winningList){
					//查询出商品
					ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
					winning.setName(product.getName());
					winning.setPrizeName(product.getImgid());
				}
				setAttr("winningList", winningList);
				setAttr("openid", openid);
			}else{
				exchange =false;
			}
			// 5. 事务提交
	        conn.commit();
	    } catch (NestedTransactionHelpException e) {
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        LogKit.logNothing(e);
	    } catch (Throwable t) {
	        // 6. 若有异常就回滚
	        if (conn != null) try {conn.rollback();} catch (Exception e1) {LogKit.error(e1.getMessage(), e1);}
	        throw t instanceof RuntimeException ? (RuntimeException)t : new ActiveRecordException(t);
	    }
	    finally {
	        try {
	            if (conn != null) {
	                if (sqlconfig.autoCommit != null)
	                    conn.setAutoCommit(sqlconfig.autoCommit);
	                conn.close();
	            }
	        } catch (Throwable t) {
	            LogKit.error(t.getMessage(), t);   
	        }
	        finally {
	        	sqlconfig.config.removeThreadLocalConnection();   
	        }
	      }
		renderJson(exchange);
	}
	
	/**
	 * 转盘与兑换 领取物流奖品    填写了地址
	 * */
	@Before(Tx.class)
	public void turntableAddress(){
		//用户opneid
		String openid = getOpenId();
		//产品ID
		String productId = getPara("productId");
		//地址
		String address = getPara("address");
		//姓名
		String name = getPara("name");
		//电话
		String phone = getPara("phone");
		//判断是转盘还是兑换
		String info = getPara("info");
		boolean rsutle =  false;
		//判断地址是否为空
		if(info.equals("转盘")){
			//是否填写收货地址
			if(address.equals("1")){
				rsutle = new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","2").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","1").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","3").
						set("ADDRESS_STATUS", "0").
						save();
			}else{
			//保存中奖信息
			rsutle = new WinningDelivery().
					set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
					set("WINNING_WAY","2").
					set("PRIZE_NAME", productId).
					set("WINNING_TIME",Timestamp.valueOf(newDate())).
					set("OPENID",openid).
					set("DISTRIBUTION_STATUS","1").
					set("PRIZE_STATUS","2").
					set("PRIZE_TYPE","3").
					set("COLLECT_ADDRESS", address).
					set("COLLECT_USER", name).
					set("COLLECT_PHONE", phone).
					set("ADDRESS_STATUS", "1").
					save();
			}
		}else if(info.equals("兑换")){
			//保存中奖信息
			//是否填写收货地址
			if(address.equals("1")){
				rsutle = new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","3").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","1").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","3").
						set("ADDRESS_STATUS", "0").
						save();
			}else{
				rsutle = new WinningDelivery().
					set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
					set("WINNING_WAY","3").
					set("PRIZE_NAME", productId).
					set("WINNING_TIME",Timestamp.valueOf(newDate())).
					set("OPENID",openid).
					set("DISTRIBUTION_STATUS","1").
					set("PRIZE_STATUS","2").
					set("PRIZE_TYPE","3").
					set("COLLECT_ADDRESS", address).
					set("COLLECT_USER", name).
					set("COLLECT_PHONE", phone).
					set("ADDRESS_STATUS", "1").
					save();
			}
		}
		renderJson(rsutle);
	}
	
	/**
	 * 领取物流奖品    未填写了地址
	 * */
	@Before(Tx.class)
	public void getLogisticsAddressNull(){
		//用户opneid
		String openid = getPara("openid");
		//客户所扫的二维码
		String codeId = getPara("codeId");
		//产品ID
		String productId = getPara("productId");
		//判断是扫码，兑换，抽奖
		String restul = getPara("restul");
		QRcodeInfo qr = QRcodeInfo.dao.findById(codeId);
		 // ******************************====根据瓶码查询运单开始=====***************************************
		// 根据该瓶码查询出该运单
		String getSellerOrderByPingCodeId = PropKit.use("database.properties").get("getSellerOrderByPingCodeId");
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
		// 这是运单号
		String outNo = sellerOutInfo.getOutNo();
		// 从活动和运单表中查询出该运单是否是在活动中 如果有记录则是在活动中 如果没有，则没有开启活动
		String sql1 = "SELECT * FROM ACTIVITYORDER WHERE ORDERID = ? AND  STATE = 1";
		ActivityOrder activityOrder = ActivityOrder.dao.findFirst(sql1, outNo);
		// ******************************=====根据运单查活动=====***************************************
		boolean exchange = false;
		if(qr.getExchange().equals("0")){
			exchange =true;
			if(restul.equals("1")){
				//修改客户扫的二维码，修改奖品已被领取
				new QRcodeInfo().set("ID", codeId).set("EXCHANGE", "1").update();
				//保存中奖信息
				new WinningDelivery().
						set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
						set("WINNING_WAY","1").
						set("PRIZE_NAME", productId).
						set("WINNING_TIME",Timestamp.valueOf(newDate())).
						set("OPENID",openid).
						set("DISTRIBUTION_STATUS","1").
						set("PRIZE_STATUS","2").
						set("PRIZE_TYPE","3").
						set("CODE_ID", codeId).
						set("ADDRESS_STATUS", "0").
						set("ACTIVITY_ID",activityOrder.getActivityId()).
						save();
			}
			//根据OPENID查询出当前人员的扫码宝贝
			String sql = "select * from WINNING_DELIVERY where OPENID = ? AND WINNING_WAY = 1 ORDER BY WINNING_TIME DESC";
			List<WinningDelivery> winningList = WinningDelivery.dao.find(sql, openid); 
			for(WinningDelivery winning : winningList){
				//查询出商品
				ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
				winning.setName(product.getName());
				winning.setPrizeName(product.getImgid());
			}
			setAttr("winningList", winningList);
			setAttr("openid", openid);
		}else{
			exchange =false;
		}
		renderJson(exchange);
	}
	
	/**
	 * 修改物流地址
	 * */
	@Before(Tx.class)
	public void updateAddress(){
		//地址
		String address = getPara("address");
		//姓名
		String name = getPara("name");
		//电话
		String phone = getPara("phone");
		//奖品记录ID 
		String winningId = getPara("winningId");
		boolean restul = new WinningDelivery().
			set("ID",winningId).
			set("COLLECT_ADDRESS", address).
			set("COLLECT_USER", name).
			set("COLLECT_PHONE", phone).
			set("ADDRESS_STATUS", "1").
			update();
		renderJson(restul);
	}
	
	/**
	 * 查看物流信息
	 * */
	public void exchangeInfo(){
		String winningId = getPara("winningId");
		WinningDelivery winning = WinningDelivery.dao.findById(winningId);
		ProductInfo product = ProductInfo.dao.findById(winning.getPrizeName());
		LogisticeFirm firm = LogisticeFirm.dao.findById(winning.getExpress());
		if(firm!=null){
			winning.setExpress(firm.getLogisticsName());
		}
		setAttr("winning", winning);
		setAttr("product", product);
		render("/front/exchangeInfo.html");
	}
	
	/**
	 * 时间格式转换
	 * */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return time.format(date);
	}
}
	