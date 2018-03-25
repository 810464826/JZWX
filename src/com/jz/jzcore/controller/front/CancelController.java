package com.jz.jzcore.controller.front;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.aop.Before;
import com.jfinal.json.Jackson;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jz.jzcore.config.Sha1Decrypt;
import com.jz.jzcore.controller.front.FrontController;
import com.jz.jzcore.model.Accounts;
import com.jz.jzcore.model.AccountsApply;
import com.jz.jzcore.model.AccountsCancel;
import com.jz.jzcore.model.ActivityOrder;
import com.jz.jzcore.model.ActivityReport;
import com.jz.jzcore.model.CancelUser;
import com.jz.jzcore.model.IntegralRule;
import com.jz.jzcore.model.LocalCancel;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.QRcodeInfo;
import com.jz.jzcore.model.RemoteCancel;
import com.jz.jzcore.model.SalesMan;
import com.jz.jzcore.model.SellerOutInfo;
import com.jz.jzcore.model.SystemArea;
import com.jz.jzcore.model.SystemOffice;
import com.jz.jzcore.model.SystemUser;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.jzcore.model.wxbase.CancelCode;
import com.jz.web.common.controller.ControllerPath;
import com.jz.weixin.WeixinUtil;

@ControllerPath(controllerKey = "/cancel")
public class CancelController extends FrontController {
	private final Logger log = LoggerFactory.getLogger(CancelController.class);

	/**
	 * 进入移动端对账
	 */
	public void moveCancel(){
		getOpenId();
		//进入登录页面。。。
		render("/front/salesman/register.html");
	}
	
	/**
	 * 登录到对账中心
	 */
	public void loginAccount(){
		String openId = getOpenId();
		log.info("获取到的openId********>"+openId);
		String s="select * from sys_user where openid=?";
		//这里查找该openId的用户
		List<SystemUser> find = SystemUser.dao.find(s,openId);
		log.info("根据openId获取到的用户********>"+find);
		String phone = getPara("phone");
		String password = getPara("password");
		String sql="select * from sys_user where login_name=?";
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		if(user==null){
			renderJson("result", false);
		}else{
			log.info("获取到的user********>"+user);
			String openId2 = user.getOpenId();
			log.info("获取到的openId和登录人的openId是否一致********>"+openId.equals(openId2));
			if(openId2!=null&&!"".equals(openId2)){
				//如果绑定过了，则直接登录
				if(openId.equals(openId2)){
					renderJson("result", true);
				}
			}else{
				if(user!=null){
					// 根据登陆名相同的用户，验证密码，
					boolean result = Sha1Decrypt.validatePassword(password, user.getPassword());
					//还要看一下该登录用户是否已经绑定了openId 该openId查找的用户为空是才修改
					if(result&&find.size()==0){
						//登录成功  则将该openId和phone登录人联系起来  绑定在一起
						boolean update = user.set("ID", user.getId()).set("OPENID", openId).update();
						log.info("则将该openId和phone登录人联系起来  绑定在一起"+update);
						renderJson("result", result);
					}else{
						renderJson("result", false);
					}
				}
			}
		}
		
	}
	
	/**
	 * 跳转到对账中心页面去
	 */
	public void goToAccountCenter(){
		String phone = getPara("phone");
		setAttr("phone", phone);
		String userType1=null;
		String sql="select * from sys_user where login_name=?";
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		if(user!=null){
			String userType = user.getUserType();
			log.info("111用户角色是*********》"+userType);
			if("1".equals(userType)){
				userType1="平台商";
			}else if("2".equals(userType)){
				userType1="一级经销商";
			}else if("3".equals(userType)){
				userType1="二级经销商";
			}else if("4".equals(userType)){
				userType1="门店";
			}else if("5".equals(userType)){
				userType1="业务员";
			}
		}
		log.info("222用户角色是*********》"+userType1);
		setAttr("userType", userType1);
		render("/front/salesman/persom-center.html");
	}
	
	/**
	 *  业务员对账中心   修改密码 暂时不做
	 */
	public void updateAccountPwd(){
		
	}
	
	/**
	 * 进入对账申请    对账可申请列表
	 */
	public void canApplyList(){
		String phone = getPara("phone");
		String sql="select * from sys_user where login_name=?";
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		String sql1="select * from local_cancel where cancel_user=?";
		List<LocalCancel> cancelList1=new ArrayList<>();
		List<LocalCancel> cancelList = LocalCancel.dao.find(sql1,user.getId());
		for (LocalCancel localCancel : cancelList) {
			//查询申请对账列表里是否有上面的 有就不添加
			String sql2="select * from accountsapply where cancelid=?";
			List<AccountsApply> list = AccountsApply.dao.find(sql2,localCancel.getId());
			log.info("申请列表里是不是有该条对账信息list*********》"+list);
			if(list.size()==0){
				String cancelOffice = localCancel.getCancelOffice();
				SystemOffice office = SystemOffice.dao.findById(cancelOffice);
				localCancel.setCancelOffice(office.getName());
				cancelList1.add(localCancel);
			}
		}
		setAttr("phone", phone);
		setAttr("cancelList", cancelList1);
		render("/front/salesman/reconciliation-application.html");
	}
	
	/**
	 * 单条核销记录的明细
	 */
	public void lookCancelInfo(){
		String cancelId = getPara("cancelId");
		log.info("接受前端的cancelId***>"+cancelId);
		LocalCancel cancel = LocalCancel.dao.findById(cancelId);
		//奖品名称  这里是奖品的id
		String prizeId = cancel.getPrizeName();
		ProductInfo product = ProductInfo.dao.findById(prizeId);
		Map<String,Object> map=new HashMap<>();
		map.put("productName", product.getName());
		//这里是userId
		String cancelUserId = cancel.getCancelUser();
		SystemUser user = SystemUser.dao.findById(cancelUserId);
		map.put("userName", user.getName());
		map.put("createTime", cancel.getCreateTime());
		map.put("cancelNumber", cancel.getNumber());
		log.info("传给前端的map***>"+map);
		renderJson(map);
	}
	
	/**
	 * 对账申请 保存
	 */
	public void applyAccount(){
		log.info("applyAccount进来啦");
		//根据手机号码查询当前登录人、
		String phone = getPara("phone");
		log.info("手机号码***》"+phone);
		//申请的核销id
		String cancelId = getPara("cancelId");
		log.info("cancelId***》"+cancelId);
		String sql="select * from sys_user where login_name=?";
		//当前登录用户
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		String officeId = user.getOfficeId();
		//当前登录经销商
		SystemOffice office = SystemOffice.dao.findById(officeId);
		String parentId2 = office.getParentId();
		//当前登录经销商的上级经销商
		SystemOffice findById = SystemOffice.dao.findById(parentId2);
		String sql2="select * from sys_user where office_id=?";
		//当前登录人的上级经销商
		List<SystemUser> userList = SystemUser.dao.find(sql2,findById.getId());
		log.info("这是当前登录人的上级经销商用户***》"+userList);
		String dateToString = DateToString(new Date());
		AccountsApply apply=new AccountsApply();
		boolean save = apply.set("ID", UUID.randomUUID().toString().replaceAll("-", ""))
			 .set("APPLYOFFICE", office.getName())
			 .set("APPLYUSERNAME", user.getName())
			 .set("STATE", 1)
			 .set("APPLYTIME", dateToString)
			 .set("APPLYUSERCODE", user.getId())
			 .set("CANCELID", cancelId)
			 .save();
		List<String> openIdList=new ArrayList<>();
		//这里需要筛选一下openIdList,当前登录的经销商是门店老板属于A业务员，发送申请时B业务员就不能收到该条申请通知。
		//门店老板的上级经销商用户中找到业务员-->业务员的管理门店中有没有有这个门店，如果有则收到通知，如果没有则从openIdList过滤掉,不添加进去
		for (int i=0;i<userList.size();i++) {
			//如果是业务员的时候
			if(userList.get(i).getUserType().equals("5")){
				//看一下这个业务员管理的门店有没有当前这个门店
				String id = userList.get(i).getId();
				log.info("循环***》"+i+"次的用户id***>"+id);
				//管理的门店  根据userId查找相关联的业务员  再通过业务员找出他管理的店   通过管理店和申请人的officeid比对 匹配则存储这个用户的openId
				String sq="select * from salesman where userid = ?";
				List<SalesMan> salesManList1 = SalesMan.dao.find(sq,id);
				log.info("循环***》"+i+"次的salesManList1***>"+salesManList1);
				for (SalesMan salesMan : salesManList1) {
					String manangeOfficeId = salesMan.getManangeOfficeId();
					log.info("manangeOfficeId***>"+manangeOfficeId);
					if(manangeOfficeId.equals(office.getId())){
						String openId = userList.get(i).getOpenId();
						log.info("openId***>"+openId);
						if(openId!=null){
							openIdList.add(openId);
						}
					}
				}
			}else{
				//如果不是业务员的话，则直接添加
				String openId = userList.get(i).getOpenId();
				log.info("循环***》"+i+"次的openId***>"+openId);
				if(openId!=null){
					openIdList.add(openId);
				}
			}
			log.info("循环***》"+i+"次的结束***！！！");
		}
		log.info("当前登录人officeid***》"+office.getId());
		log.info("当前登录人上级的openId***》"+openIdList);
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String appid = wxconfig.getAppId();
		String http = wxconfig.getHttp();
		//这里好像只是跳转   暂时未做   已做
		String url = "http%3a%2f%2f" + http + "%2fJZWX%2fcancel%2fgetApplyAccountInfo%3fcancelId%3d" + cancelId;
		String remarks="请尽快处理该对账申请，谢谢！";
		for (String openId : openIdList) {
			String json = " {\"touser\":\"" + openId + "\","
					+ "\"template_id\":\"soDCViL91c9JuAbp2KzO1gR7BFIRDQFLtkVw67HmDGM\","
					+ "\"url\":\"http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url
					+ "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect\"," + "\"topcolor\":\"#FF0000\","
					+ "\"data\":{" + "\"first\": {" + "\"value\":\"您好！您的下级经销商向您发起对账申请了，请帮忙对账！\"," + "\"color\":\"#ca2426"
					+ "\"}," + "\"keyword1\":{" + "\"value\":\"" + dateToString + "\"," + "\"color\":\"#ca2426"
					+ "\"}," + "\"keyword2\":{" + "\"value\":\"" + dateToString + "-" + dateToString
					+ "\"," + "\"color\":\"#ca2426" + "\"}," + "\"keyword3\":{" + "\"value\":\"待对账\","
					+ "\"color\":\"#ca2426" + "\"}," + "\"remark\":{" + "\"value\":\"" + remarks + "\"," + "\"color\":\"#ca2426\"}}}";
			WeixinUtil.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, json);
		}
		if(save){
			renderJson("result","true");
		}else{
			renderJson("result","false");
		}
	}
	
	/**
	 * 手机端推送对账申请的详情
	 */
	public void getApplyAccountInfo(){
		String cancelId = getPara("cancelId");
		log.info("核销记录id****》"+cancelId);
		String sql="select * from accountsapply where cancelid=?";
		AccountsApply apply = AccountsApply.dao.findFirst(sql,cancelId);
		LocalCancel l = LocalCancel.dao.findById(cancelId);
		setAttr("apply", apply);
		log.info("apply记录****》"+apply);
		LocalCancel localCancel=new LocalCancel();
		String number = l.getNumber();
		localCancel.setNumber(number);
		String prizeName = l.getPrizeName();
		ProductInfo productInfo = ProductInfo.dao.findById(prizeName);
		localCancel.setPrizeName(productInfo.getName());
		setAttr("localCancel", localCancel);
		log.info("localCancel记录****》"+localCancel);
		render("/front/cancel/applyInfo.html");
	}
	
	/**
	 * 申请列表管理
	 */
	public void applyAccountManage(){
		String phone = getPara("phone");
		String sql="select * from sys_user where login_name=?";
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		if("1".equals(user.getId())){
			String sql1="select * from accountsapply ";
			List<AccountsApply> applyList = AccountsApply.dao.find(sql1);
			setAttr("applyList", applyList);
			log.info("申请列表数据***》"+applyList);
		}else{
			String sql1="select * from accountsapply where applyusercode=?";
			//当前登录人的申请记录
			List<AccountsApply> applyList = AccountsApply.dao.find(sql1,user.getId());
			String sql2="select * from sys_user where office_id=? ";
			//如果登录人是他的上级则可以看
			String officeId = user.getOfficeId();
			String sql3="select * from sys_office where parent_id=?";
			//当前登录人的下级经销商
			List<SystemOffice> officeList = SystemOffice.dao.find(sql3,officeId);
			//这是当前登录人下面的用户
			List<SystemUser> userList =new ArrayList<>();
			//这里需要判断登录人是不是业务员，如果是业务员的话，他只能看他管理门店用户的申请记录
			if(user.getUserType().equals("5")){
				String id = user.getId();
				//管理的门店
				String sq="select * from salesman where userid = ?";
				//当前登录用户所对应的业务员集合
				List<SalesMan> salesManList = SalesMan.dao.find(sq,id);
				log.info("当前登录人所对应的业务员集合salesManList***》"+salesManList);
				//当前登录用户所对应的业务员所管理的门店集合
				List<String> manOfficeIdList=new ArrayList<>();
				for (SalesMan s : salesManList) {
					String manangeOfficeId = s.getManangeOfficeId();
					if(manangeOfficeId!=null){
						manOfficeIdList.add(manangeOfficeId);
					}
				}
				log.info("当前登录人管理门店的集合manOfficeIdList***》"+manOfficeIdList);
				if(manOfficeIdList.size()!=0){
					for (String s : manOfficeIdList) {
						//这是门店的集合  相当于店铺id
						List<SystemUser> list = SystemUser.dao.find(sql2,s);
						userList.addAll(list);
					}
				}
				log.info("当前登录人业务员的下级用户集合userList***》"+userList);
				//查询出他下面用户的申请记录
				for (SystemUser systemUser : userList) {
					List<AccountsApply> list = AccountsApply.dao.find(sql1,systemUser.getId());
					applyList.addAll(list);
				}
			}else{
				for (SystemOffice systemOffice : officeList) {
					String id = systemOffice.getId();
					List<SystemUser> list = SystemUser.dao.find(sql2,id);
					userList.addAll(list);
				}
				log.info("当前登录人的下级用户集合userList***》"+userList);
				//查询出他下面用户的申请记录
				for (SystemUser systemUser : userList) {
					List<AccountsApply> list = AccountsApply.dao.find(sql1,systemUser.getId());
					applyList.addAll(list);
				}
			}
			setAttr("applyList", applyList);
			log.info("申请列表数据***》"+applyList);
		}
		setAttr("phone", phone);
		render("/front/salesman/reconciliation-application-manage.html");
	}
	
	/**
	 * 对账添加页面
	 */
	public void accountAdd(){
		String phone = getPara("phone");
		String sql="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		String sql2="select * from sys_user where office_id=?";
		//如果登录人是他的上级则可以看
		String officeId = user.getOfficeId();
		String sql3="select * from sys_office where parent_id=?";
		//当前登录人的下级经销商
		List<SystemOffice> officeList = SystemOffice.dao.find(sql3,officeId);
		//这是当前登录人下面的用户
		List<SystemUser> userList =new ArrayList<>();
		//登录的用户不是业务员的时候
		if(!user.getUserType().equals("5")){
			for (SystemOffice systemOffice : officeList) {
				String id = systemOffice.getId();
				List<SystemUser> list = SystemUser.dao.find(sql2,id);
				userList.addAll(list);
			}
		}
		//当前登录用户是业务员的时候  则只能给他的管理的店的用户进行对账
		else{
			String id = user.getId();
			//管理的门店
			String sq="select * from salesman where userid = ?";
			//当前登录用户所对应的业务员集合
			List<SalesMan> salesManList = SalesMan.dao.find(sq,id);
			log.info("当前登录人所对应的业务员集合salesManList***》"+salesManList);
			//当前登录用户所对应的业务员所管理的门店集合
			List<String> manOfficeIdList=new ArrayList<>();
			for (SalesMan s : salesManList) {
				String manangeOfficeId = s.getManangeOfficeId();
				if(manangeOfficeId!=null){
					manOfficeIdList.add(manangeOfficeId);
				}
			}
			log.info("当前登录人管理门店的集合manOfficeIdList***》"+manOfficeIdList);
			if(manOfficeIdList.size()!=0){
				for (String s : manOfficeIdList) {
					//这是门店的集合  相当于店铺id
					List<SystemUser> list = SystemUser.dao.find(sql2,s);
					userList.addAll(list);
				}
			}
		}
		log.info("下面的用户***》"+userList);
		//查询他下级经销商的核销记录
		String sql4="select * from local_cancel where cancel_user=? and check_state =0";
		List<LocalCancel> localCancelList=new ArrayList<>();
		for (SystemUser systemUser : userList) {
			List<LocalCancel> list = LocalCancel.dao.find(sql4,systemUser.getId());
			localCancelList.addAll(list);
		}
		log.info("下级经销商的核销记录***》"+localCancelList);
		List<LocalCancel> localCancelList1=new ArrayList<>();
		for (LocalCancel localCancel : localCancelList) {
			String cancelOffice = localCancel.getCancelOffice();
			SystemOffice findById = SystemOffice.dao.findById(cancelOffice);
			localCancel.setCancelOffice(findById.getName());
			/*String cancelUser = localCancel.getCancelUser();
			SystemUser findById2 = SystemUser.dao.findById(cancelUser);
			localCancel.setCancelUser(findById2.getName());*/
			localCancelList1.add(localCancel);
		}
		log.info("给前端的下级经销商的核销记录***》"+localCancelList1);
		setAttr("localCancelList", localCancelList1);
		setAttr("phone", phone);
		//对账添加页面
		render("/front/salesman/check-add.html");
	}
	
	/**
	 * 查询出该经销商下面的业务员
	 */
	public void getSelfSalesMan(){
		String phone = getPara("phone");
		String sql="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		String sql1="SELECT * FROM salesman where officeid = ?";
		List<SalesMan> salesManList = SalesMan.dao.find(sql1,user.getOfficeId());
		log.info("查询的业务员集合是：***》"+salesManList);
		String json = JsonUtils.toJson(salesManList);
		log.info("查询的业务员集合是json：***》"+json);
		renderJson(json);
	}
	
	/**
	 * 保存对账信息
	 */
	public void saveAccount(){
		//先保存对账信息   
		String phone = getPara("phone");
		log.info("phone******》"+phone);
		String sql="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		String officeId = user.getOfficeId();
		//当前登录经销商
		SystemOffice office = SystemOffice.dao.findById(officeId);
		//对应的核销记录的id
		String cancelId = getPara("cancelId");
		log.info("cancelId******》"+cancelId);
		//备注   对账的详情
		String textarea = getPara("textarea");
		log.info("textarea******》"+textarea);
		//添加对账时选择的时间
		String selectTime = getPara("selectTime");
		log.info("selectTime******》"+selectTime);
		//对账的业务员id
		String salseId = getPara("salseId");
		log.info("salseId******》"+salseId);
		//对账业务员的名字
		String salseName = getPara("salseName");
		log.info("salseName******》"+salseName);
		//该条选择的核销记录
		LocalCancel cancel = LocalCancel.dao.findById(cancelId);
		//这里其实是奖品的id
		String prizeName = cancel.getPrizeName();
		ProductInfo productInfo = ProductInfo.dao.findById(prizeName);
		log.info("productInfo******》"+productInfo);
		Accounts accounts=new Accounts();
		String accountId = UUID.randomUUID().toString().replaceAll("-", "");
		boolean save = accounts.set("ID", accountId)
		        .set("ACCOUNTS_USER_ID", user.getId())
		        .set("ACCOUNTS_DISTRIBUTOR_ID", office.getId())
		        .set("STATE", "0")
		        .set("ACCOUNTS_TIME", new java.sql.Timestamp(dateToString(selectTime).getTime()))
		        .set("REMARKS", textarea)
		        .set("ACTIVITY_ID", cancel.getActivityId())
		        .set("PRIZE_NAME",productInfo.getName())
		        .set("CANCEL_NUMBER", cancel.getNumber())
		        .set("PRIZE_ID", cancel.getPrizeName())
		        .set("CANCEL_ID", cancelId)
		        .set("SELECTTIME", selectTime)
		        .set("SALESMAN", salseId)
		        .set("SALESNAME", salseName)
		        .save();
		log.info("保存对账成功！"+save);
		boolean update = cancel.set("ID", cancel.getId())
		      .set("CHECK_STATE", "2")
		      .update();
		log.info("修改核销记录的状态成功！"+update);
		//再推送消息  accountsInformation**************************************
		// 根据本地核销记录查出对账
		Accounts accoun = Accounts.dao.findById(accountId);
		String selectTime2 = accoun.getSelectTime();
		String salesname = accoun.getSalesname();
		String remarks2 = accoun.getRemarks();
		//推送备注
		String remarks=selectTime2+"业务员"+salesname+remarks2;
		log.info("推送备注*****>"+remarks);
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		log.info("token*****>"+token);
		String appid = wxconfig.getAppId();
		log.info("appid*****>"+appid);
		String http = wxconfig.getHttp();
		log.info("http*****>"+http);
		String url = "http%3a%2f%2f" + http + "%2fJZWX%2fcancel%2fAccountsOk%3fAccountsId%3d" + accoun.getId();
		System.out.println("url这里" + url);
		String json = " {\"touser\":\"" + cancel.getCancelOpenid() + "\","
				+ "\"template_id\":\"soDCViL91c9JuAbp2KzO1gR7BFIRDQFLtkVw67HmDGM\","
				+ "\"url\":\"http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url
				+ "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect\"," + "\"topcolor\":\"#FF0000\","
				+ "\"data\":{" + "\"first\": {" + "\"value\":\"您的奖品补贴账单已经生成，我们将尽快把活动奖品发放到店\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword1\":{" + "\"value\":\"" + accoun.getSelectTime() + "\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword2\":{" + "\"value\":\"" + accoun.getSelectTime() + "-" + accoun.getSelectTime()
				+ "\"," + "\"color\":\"#ca2426" + "\"}," + "\"keyword3\":{" + "\"value\":\"业务员派送中\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"remark\":{" + "\"value\":\"" + remarks + "\"," + "\"color\":\"#ca2426\"}}}";
		WeixinUtil.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, json);
		renderJson("result","true");
	}
	
	/**
	 * 对账列表管理
	 */
	public void accountListManage(){
		String phone = getPara("phone");
		String sql="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql,phone);
		//这是当前登录人下面的用户
		List<SystemUser> userList =new ArrayList<>();
		if("1".equals(user.getId())){
			String sql1="select * from accounts ";
			List<Accounts> accountList = Accounts.dao.find(sql1);
			List<Accounts> accountList1=new ArrayList<>();
			for (Accounts accounts : accountList) {
				String accountsUserId = accounts.getAccountsUserId();
				SystemUser user2 = SystemUser.dao.findById(accountsUserId);
				//对账发起人
				accounts.setAccountsUserId(user2.getName());
				String accountsDistrbutorId = accounts.getAccountsDistrbutorId();
				SystemOffice office = SystemOffice.dao.findById(accountsDistrbutorId);
				//对账发起经销商
				accounts.setAccountsDistrbutorId(office.getName());
				String accountsConfirmId = accounts.getAccountsConfirmId();
				if(!"".equals(accountsConfirmId)&&accountsConfirmId!=null){
					SystemUser user3 = SystemUser.dao.findById(accountsConfirmId);
					//确认对账人
					accounts.setAccountsConfirmId(user3.getName());
				}
				String accountsConfirmDistrbutor = accounts.getAccountsConfirmDistrbutor();
				if(!"".equals(accountsConfirmDistrbutor)&&accountsConfirmDistrbutor!=null){
					SystemOffice office1 = SystemOffice.dao.findById(accountsConfirmDistrbutor);
					//确认对账经销商
					accounts.setAccountsConfirmDistrbutor(office1.getName());
				}
				String state = accounts.getState();
				if(state.equals("0")){
					accounts.setState("待对账");
				}else if(state.equals("1")){
					accounts.setState("已对账");
				}
				accountList1.add(accounts);
			log.info("accountList******>"+accountList1);
			setAttr("accountList", accountList1);
			}
		}
		else{
			List<Accounts> accountList1=new ArrayList<>();
			List<Accounts> accountList=new ArrayList<>();
			if(user.getUserType()!="5"){
				String sql2="select * from sys_user where office_id=?";
				//如果登录人是他的上级则可以看
				String officeId = user.getOfficeId();
				String sql3="select * from sys_office where parent_id=?";
				//当前登录人的下级经销商
				List<SystemOffice> officeList = SystemOffice.dao.find(sql3,officeId);
				for (SystemOffice systemOffice : officeList) {
					String id = systemOffice.getId();
					List<SystemUser> list = SystemUser.dao.find(sql2,id);
					userList.addAll(list);
				}
				log.info("下面的用户***》"+userList);
				String sql1="select * from accounts where accounts_user_id=?";
				if(user.getId().equals("1")){
					String sql4="select * from accounts";
					List<Accounts> find = Accounts.dao.find(sql4);
					accountList.addAll(find);
				}else{
					//查询当前登录人下级用户的对账记录
					for (SystemUser u : userList) {
						List<Accounts> list = Accounts.dao.find(sql1,u.getId());
						accountList.addAll(list);
					}
					//自己的对账记录
					List<Accounts> list1 = Accounts.dao.find(sql1,user.getId());
					accountList.addAll(list1);
				}
			}
			//业务员的模块
			else{
				//如果是业务员的时候，则看他自己管理的店的
				//查询当前登录人下级用户的对账记录
				String id = user.getId();
				//管理的门店
				String sq="select * from salesman where userid = ?";
				//当前登录用户所对应的业务员集合
				List<SalesMan> salesManList = SalesMan.dao.find(sq,id);
				log.info("当前登录人所对应的业务员集合salesManList***》"+salesManList);
				//当前登录用户所对应的业务员所管理的门店集合
				List<String> manOfficeIdList=new ArrayList<>();
				for (SalesMan s : salesManList) {
					String manangeOfficeId = s.getManangeOfficeId();
					if(manangeOfficeId!=null){
						manOfficeIdList.add(manangeOfficeId);
					}
				}
				log.info("当前登录人管理门店的集合manOfficeIdList***》"+manOfficeIdList);
				if(manOfficeIdList.size()!=0){
					for (String s : manOfficeIdList) {
						//这是门店的集合  相当于店铺id
						//查找这些店铺的用户
						String sql2="select * from sys_user where office_id=?";
						List<SystemUser> list = SystemUser.dao.find(sql2,s);
						userList.addAll(list);
					}
				}
				log.info("下面的用户***》"+userList);
				String sql1="select * from accounts where accounts_user_id=?";
				//查询当前登录人下级用户的对账记录
				for (SystemUser u : userList) {
					List<Accounts> list = Accounts.dao.find(sql1,u.getId());
					accountList.addAll(list);
				}
				//自己的对账记录
				List<Accounts> list1 = Accounts.dao.find(sql1,user.getId());
				accountList.addAll(list1);
			}
			//不管是不是业务员都要处理这些的
			for (Accounts accounts : accountList) {
				String accountsUserId = accounts.getAccountsUserId();
				SystemUser user2 = SystemUser.dao.findById(accountsUserId);
				//对账发起人
				accounts.setAccountsUserId(user2.getName());
				String accountsDistrbutorId = accounts.getAccountsDistrbutorId();
				SystemOffice office = SystemOffice.dao.findById(accountsDistrbutorId);
				//对账发起经销商
				accounts.setAccountsDistrbutorId(office.getName());
				String accountsConfirmId = accounts.getAccountsConfirmId();
				if(!"".equals(accountsConfirmId)&&accountsConfirmId!=null){
					SystemUser user3 = SystemUser.dao.findById(accountsConfirmId);
					//确认对账人
					accounts.setAccountsConfirmId(user3.getName());
				}
				String accountsConfirmDistrbutor = accounts.getAccountsConfirmDistrbutor();
				if(!"".equals(accountsConfirmDistrbutor)&&accountsConfirmDistrbutor!=null){
					SystemOffice office1 = SystemOffice.dao.findById(accountsConfirmDistrbutor);
					//确认对账经销商
					accounts.setAccountsConfirmDistrbutor(office1.getName());
				}
				String state = accounts.getState();
				if(state.equals("0")){
					accounts.setState("待对账");
				}else if(state.equals("1")){
					accounts.setState("已对账");
				}
				accountList1.add(accounts);
			}
			log.info("accountList******>"+accountList1);
			setAttr("accountList", accountList);
		}
		setAttr("phone", phone);
		//对账添加页面
		render("/front/salesman/management.html");
	}
	
	/**
	 * 对账申请列表查询        登录人如果是业务员的话  只能查看他自己的申请和他管理的店的申请记录
	 */
	public void searchApplyAccount(){
		//申请人
		String applyUserName = getPara("applyUserName");
		log.info("applyUserName******>"+applyUserName);
		//申请时间  开始
		String startTime = getPara("startTime");
		log.info("startTime******>"+startTime);
		String endTime = getPara("endTime");
		log.info("endTime******>"+endTime);
		//状态
		String applyState = getPara("applyState");
		log.info("applyState******>"+applyState);
		String phone = getPara("phone");
		log.info("phone******>"+phone);
		String sql6="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql6,phone);
		//********查询当前登录人下级的用户start**********
		String sql2="select * from sys_user where office_id=? ";
		//如果登录人是他的上级则可以看
		String officeId = user.getOfficeId();
		String sql3="select * from sys_office where parent_id=?";
		//当前登录人的下级经销商
		List<SystemOffice> officeList = SystemOffice.dao.find(sql3,officeId);
		//这是当前登录人下面的用户
		List<SystemUser> userList =new ArrayList<>();
		//不是业务员的时候  直接找下级
		if(!user.getUserType().equals("5")){
			for (SystemOffice systemOffice : officeList) {
				String id = systemOffice.getId();
				List<SystemUser> list = SystemUser.dao.find(sql2,id);
				userList.addAll(list);
			}
		}else{
			//如果是业务员的时候，找他管理的店的用户
			String id = user.getId();
			//管理的门店
			String sq="select * from salesman where userid = ?";
			List<SalesMan> salesManList = SalesMan.dao.find(sq,id);
			List<String> manOfficeIdList=new ArrayList<>();
			for (SalesMan s : salesManList) {
				String manangeOfficeId = s.getManangeOfficeId();
				if(manangeOfficeId!=null){
					manOfficeIdList.add(manangeOfficeId);
				}
			}
			if(manOfficeIdList.size()!=0){
				for (String s : manOfficeIdList) {
					List<SystemUser> list = SystemUser.dao.find(sql2,s);
					userList.addAll(list);
				}
			}
		}
		userList.add(user);
		//********查询当前登录人下级的用户end************
		String neirong ="1=1";
		// 这里是模糊查询名字
		if (applyUserName!=null&&!"".equals(applyUserName)) {
			neirong += "and applyoffice = " + " '" + applyUserName + "' ";
		}
		if (startTime!=null&&!"".equals(startTime) && endTime!=null&&!"".equals(endTime)) {
			neirong += " and applytime BETWEEN '" + startTime + "' and '" + endTime + "'";
		}
		if (applyState!=null&&!"".equals(applyState)) {
			neirong += "and state = " + " '" + applyState + "' ";
		}
		// 我记得下次模糊的时候 不用插入 用 拼接 是这样的
		String sql = "select * from accountsapply where " + neirong;
		log.info("查询的sql****>"+sql);
		List<AccountsApply> applyList=new ArrayList<>();
		//循环查询他下级的记录
		if(!user.getId().equals("1")){
			for (SystemUser systemUser : userList) {
				String sql1 = "select * from accountsapply where applyusercode = ? ";
				List<AccountsApply> userApplyList = AccountsApply.dao.find(sql1,systemUser.getId());
				if(userApplyList.size()!=0){
					neirong += " and  applyusercode = " + " '" + systemUser.getId() + "'";
					String sql11 = "select * from accountsapply where " + neirong;
					List<AccountsApply> find = AccountsApply.dao.find(sql11);
					applyList.addAll(find);
				}
			}
		}else{
			applyList = AccountsApply.dao.find(sql);
		}
		log.info("查询的applyList****>"+applyList);
		setAttr("applyList", applyList);
		setAttr("phone", phone);
		render("/front/salesman/reconciliation-application-manage.html");
	}
	
	/**
	 * 对账列表查询
	 */
	public void searchAccountList(){
		log.info("进来没有*******************");
		//对账发起经销商
		String applyOffice = getPara("applyOffice");
		log.info("applyOffice******>"+applyOffice);
		//对账确认经销商
		String comfirmOffice = getPara("comfirmOffice");
		log.info("comfirmOffice******>"+comfirmOffice);
		//对账时间
		String startTime = getPara("startTime");
		log.info("startTime******>"+startTime);
		String endTime = getPara("endTime");
		log.info("endTime******>"+endTime);
		//对账状态
		String state = getPara("state");
		log.info("state******>"+state);
		String phone = getPara("phone");
		log.info("phone******>"+phone);
		String sql6="select * from sys_user where login_name=?";
		//当前登录人
		SystemUser user = SystemUser.dao.findFirst(sql6,phone);
		//********查询当前登录人下级的用户start**********
		String sql2="select * from sys_user where office_id=? and user_type!=5";
		//如果登录人是他的上级则可以看
		String officeId = user.getOfficeId();
		String sql3="select * from sys_office where parent_id=?";
		//当前登录人的下级经销商
		List<SystemOffice> officeList = SystemOffice.dao.find(sql3,officeId);
		//这是当前登录人下面的用户
		List<SystemUser> userList =new ArrayList<>();
		if(!user.getUserType().equals("5")){
			for (SystemOffice systemOffice : officeList) {
				String id = systemOffice.getId();
				List<SystemUser> list = SystemUser.dao.find(sql2,id);
				userList.addAll(list);
			}
		}else{
			//如果是业务员的时候，找他管理的店的用户
			String id = user.getId();
			//管理的门店
			String sq="select * from salesman where userid = ?";
			List<SalesMan> salesManList = SalesMan.dao.find(sq,id);
			List<String> manOfficeIdList=new ArrayList<>();
			for (SalesMan s : salesManList) {
				String manangeOfficeId = s.getManangeOfficeId();
				if(manangeOfficeId!=null){
					manOfficeIdList.add(manangeOfficeId);
				}
			}
			if(manOfficeIdList.size()!=0){
				for (String s : manOfficeIdList) {
					List<SystemUser> list = SystemUser.dao.find(sql2,s);
					userList.addAll(list);
				}
			}
		}
		userList.add(user);
		for (SystemOffice systemOffice : officeList) {
			String id = systemOffice.getId();
			List<SystemUser> list = SystemUser.dao.find(sql2,id);
			userList.addAll(list);
		}
		//********查询当前登录人下级的用户end************
		String neirong= "1=1 ";
		String sql1="select * from sys_office where name=? ";
		// 这里是模糊查询名字
		if (applyOffice!=null&&!"".equals(applyOffice)) {
			SystemOffice office = SystemOffice.dao.findFirst(sql1,applyOffice);
			neirong += "and accounts_distributor_id = " + " '" + office.getId() + "' ";
		}
		if (comfirmOffice!=null&&!"".equals(comfirmOffice)) {
			SystemOffice office = SystemOffice.dao.findFirst(sql1,comfirmOffice);
			neirong += "and accounts_confirm_distributor_i = " + " '" + office.getId() + "' ";
		}
		if (startTime!=null&&!"".equals(startTime) && endTime!=null&&!"".equals(endTime)) {
			neirong += " and selecttime BETWEEN '" + startTime + "' and '" + endTime + "'";
		}
		if (state!=null&&!"".equals(state)) {
			neirong += "and state = " + " '" + state + "' ";
		}
		// 我记得下次模糊的时候 不用插入 用 拼接 是这样的
		String sql = "select * from accounts where " + neirong;
		log.info("查询的sql****>"+sql);
		List<Accounts> accountsList=new ArrayList<>();
		log.info("当前登录人的下级用户userList****>"+userList);
		userList.add(user);
		if(!user.getId().equals("1")){
			for (SystemUser systemUser : userList) {
				String sql9 = "select * from accounts where accounts_user_id = ? ";
				List<Accounts> userAccountsList = Accounts.dao.find(sql9,systemUser.getId());
				log.info("查询的userAccountsList****>"+userAccountsList);
				if(userAccountsList.size()!=0){
					neirong += " and accounts_user_id = " + " '" + systemUser.getId() + "'";
					String sql11 = "select * from accounts where " + neirong;
					List<Accounts> accountsList1 = Accounts.dao.find(sql11);
					log.info("****处理后查询的userAccountsList****>"+userAccountsList);
					accountsList.addAll(accountsList1);
				}
			}
		}else{
			accountsList=Accounts.dao.find(sql);
		}
		List<Accounts> accountsList1=new ArrayList<>();
		for (Accounts a : accountsList) {
			String accountsUserId = a.getAccountsUserId();
			SystemUser findById = SystemUser.dao.findById(accountsUserId);
			//对账发起人
			a.setAccountsUserId(findById.getName());
			String accountsDistrbutorId = a.getAccountsDistrbutorId();
			SystemOffice findById2 = SystemOffice.dao.findById(accountsDistrbutorId);
			//对账发起经销商
			a.setAccountsDistrbutorId(findById2.getName());
			//确认经销商
			String accountsConfirmDistrbutor = a.getAccountsConfirmDistrbutor();
			if(accountsConfirmDistrbutor!=null){
				SystemOffice findById3 = SystemOffice.dao.findById(accountsConfirmDistrbutor);
				a.setAccountsConfirmDistrbutor(findById3.getName());
			}
			//确认人
			String accountsConfirmId = a.getAccountsConfirmId();
			if(accountsConfirmId!=null){
				SystemUser findById4 = SystemUser.dao.findById(accountsConfirmId);
				a.setAccountsConfirmId(findById4.getName());
			}
			String state2 = a.getState();
			if("0".equals(state2)){
				a.setState("待对账");
			}else if("1".equals(state2)){
				a.setState("已对账");
			}
			accountsList1.add(a);
		}
		setAttr("accountList", accountsList1);
		log.info("查询的accountList****>"+accountsList1);
		setAttr("phone", phone);
		//对账添加页面
		render("/front/salesman/management.html");
	}
	
	/**
	 * 进入手机核销系统
	 */
	public void index() {
		String openId = getOpenId();
		// 判断当前openid是否已关联核销账号，如果有查询出登陆账号的信息
		String sql = "SELECT * FROM CANCEL_USER WHERE OPENID = ?";
		List<CancelUser> cancel = CancelUser.dao.find(sql, openId);
		boolean restul = false;
		SystemUser user = new SystemUser();
		List<LocalCancel> local = null;
		List<LocalCancel> localcheck = null;
		int allNumber = 0;
		if (cancel.size() == 0) {
			restul = false;
		} else {
			restul = true;
			for (CancelUser can : cancel) {
				user = SystemUser.dao.findById(can.getSystemUserId());
			}
			// 查询出当前人员之前的核销记录 0未对账的 1已对账 2对账中
			String findcancel = "SELECT * FROM LOCAL_CANCEL WHERE CANCEL_USER = ? AND CHECK_STATE = ?";
			local = LocalCancel.dao.find(findcancel, user.getId(), "0");
			// 查询出当前人员之前的核销记录 0未对账的 1已对账 2对账中
			String findCheck = "SELECT * FROM LOCAL_CANCEL WHERE CANCEL_USER = ? AND CHECK_STATE = ?";
			localcheck = LocalCancel.dao.find(findCheck, user.getId(), "2");
			for (LocalCancel lc : localcheck) {
				SystemOffice off = SystemOffice.dao.findById(lc.getCancelOffice());
				lc.setCancelOffice(off.getName());
			}
			for (LocalCancel loaclcancel : local) {
				ProductInfo info = ProductInfo.dao.findById(loaclcancel.getPrizeName());
				SystemUser canceluser = SystemUser.dao.findById(loaclcancel.getCancelUser());
				loaclcancel.setPrizeName(info.getName());
				loaclcancel.setCancelUser(canceluser.getName());
				allNumber += Integer.parseInt(loaclcancel.getNumber());
			}
		}
		/** 查询对账的记录 start ***/
		String id = user.getId();
		String sqlAccount = "SELECT * FROM ACCOUNTS WHERE ACCOUNTS_CONFIRM_ID = ? ORDER BY ACCOUNTS_TIME DESC";
		Accounts accounts = Accounts.dao.findFirst(sqlAccount, id);
		/** 查询对账的记录 end ***/
		setAttr("allNumber", allNumber);
		setAttr("user", user);
		setAttr("accounts", accounts);
		setAttr("localcheck", localcheck);
		setAttr("local", local);
		setAttr("restul", restul);
		String url = "";
		if (restul) {
			url = "/front/cancel/cancel-main.html";
		} else {
			url = "/front/cancel/register.html";
		}
		render(url);
	}

	/**
	 * 跳转至登陆页面
	 */
	public void goLogin() {
		render("/front/cancel/register.html");
	}

	/**
	 * 去向修改密码的页面
	 */
	public void updatePassword() {
		String userId = getPara("userId");
		setAttr("userId", userId);
		render("/front/cancel/changePassword.html");
	}

	/**
	 * 这里修改密码
	 */
	public void updatePwd() {
		String userId = getPara("userId");
		String oldPwd = getPara("oldPwd");
		String newPwd = getPara("newPwd");
		SystemUser user = SystemUser.dao.findById(userId);
		String sql="select * from cancel_user where sys_user_id=?";
		CancelUser cancelUser = CancelUser.dao.findFirst(sql,user.getId());
		boolean delete = CancelUser.dao.deleteById(cancelUser.getId());
		log.info("删除该用户成功！"+delete);
		boolean validatePassword = Sha1Decrypt.validatePassword(oldPwd, user.getPassword());
		// 判断旧密码是否相同
		if (validatePassword) {
			user.set("ID", userId)
					// 这里是加密
					.set("PASSWORD", Sha1Decrypt.entryptPassword(newPwd)).update();
			renderJson("result", "true");
		} else {
			renderJson("result", "false");
		}
	}

	/**
	 * 验证登陆
	 */
	public void verificationLogin() {
		String openId = getOpenId();
		String phone = getPara("phone");
		String password = getPara("password");
		String err = "";
		String id = "";
		// 判断是否已登陆
		String cancelList = "SELECT * FROM CANCEL_USER WHERE OPENID = ?";
		List<CancelUser> cancel = CancelUser.dao.find(cancelList, openId);
		if (cancel.size() == 0) {
			String sql = "SELECT * FROM SYS_USER WHERE LOGIN_NAME = ?";
			List<SystemUser> sysUser = SystemUser.dao.find(sql, phone);
			if (sysUser.size() != 0) {
				for (SystemUser user : sysUser) {
					// 根据登陆名相同的用户，验证密码，
					boolean paw = Sha1Decrypt.validatePassword(password, user.getPassword());
					if (paw) {
						id = user.getId();
						// CANCEL_LOGIN 判断登录是否未注销 1是 0不是
						new CancelUser().set("ID", UUID.randomUUID().toString().replaceAll("-", ""))
								.set("SYS_USER_ID", id).set("CANCEL_LOGIN", "1").set("OPENID", openId).save();
					} else {
						err = "密码错误";
					}
				}
			} else {
				err = "该账户不存在";
			}
		} else {
			for (CancelUser can : cancel) {
				// 未注销
				if (can.getCancelLogin().equals("1")) {
					err = "已经登陆,请注销后在登陆";
				} else {
					String sql = "SELECT * FROM SYS_USER WHERE LOGIN_NAME = ?";
					List<SystemUser> sysUser = SystemUser.dao.find(sql, phone);
					if (sysUser.size() != 0) {
						for (SystemUser user : sysUser) {
							// 根据登陆名相同的用户，验证密码，
							boolean paw = Sha1Decrypt.validatePassword(password, user.getPassword());
							if (paw) {
								id = user.getId();
								can.set("ID", can.getId()).set("CANCEL_LOGIN", "1").set("OPENID", openId).update();
							} else {
								err = "密码错误";
							}
						}
					} else {
						err = "该账户不存在";
					}
				}
			}

		}
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("err", err);
		ret.put("id", id);
		renderJson(ret);
	}

	/**
	 * 商家注销
	 */
	public void Cancellation() {
		String businessId = getPara("Id");
		log.info("退出登录******注销*****" + businessId);
		String sql = "select * from CANCEL_USER where SYS_USER_ID = ?";
		List<CancelUser> user = CancelUser.dao.find(sql, businessId);
		for (CancelUser cancelUser : user) {
			cancelUser.set("ID", cancelUser.getId()).set("CANCEL_LOGIN", "0").update();
		}
		render("/front/cancel/register.html");
	}

	/**
	 * ***********************核销***********************************
	 */
	public void CancelByCode() {
		String openid = getOpenId();
		// 当前系统登陆人
		String userId = getPara("userId");
		log.info("当前系统登陆人************" + userId);
		String code = getPara("code");
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String Decode = "{\"code\":\"" + code + "\"}";
		String DecodeOk = WeixinUtil.sendPost("https://api.weixin.qq.com/card/code/consume?access_token=" + token,
				Decode);
		CancelCode cancel = Jackson.getJson().parse(DecodeOk, CancelCode.class);
		log.info("核销时code编号**********" + code);
		boolean result = false;
		Map<String, Object> map = new HashMap<String, Object>();
		String err = "";
		String state = "";
		// 判断是否核销成功
		log.info("判断是否核销成功***********" + cancel.getErrcode());
		if (cancel.getErrcode().equals("0")) {
			// 通过CODE修改，当前记录，已被核销。
			String sql = "SELECT * FROM WINNING_DELIVERY WHERE CODE = ?";
			List<WinningDelivery> winning = WinningDelivery.dao.find(sql, code);
			log.info("通过CODE修改，当前记录，已被核销**********" + winning.size());
			for (WinningDelivery delivery : winning) {
				// 是否属于兑换和转盘中奖
				if (delivery.getWinningWay().equals("1")) {
					// 判断是本地核销，还是异地核销
					// 1.判断当前码属于哪个活动
					QRcodeInfo qrcode = QRcodeInfo.dao.findById(delivery.getCodeId());
					// ******************************====根据瓶码查询运单开始=====***************************************
					// 根据该瓶码查询出该运单
					String getSellerOrderByPingCodeId = PropKit.use("database.properties")
							.get("getSellerOrderByPingCodeId");
					List<Record> soiView = Db.find(getSellerOrderByPingCodeId, qrcode.getBoxCode());
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
					ActivityReport activityReport = ActivityReport.dao.findById(activityOrder.getActivityId());
					String theOutNo = activityOrder.getOrderId();
					log.info("该瓶码所属的运单编号是****>:" + theOutNo);
					// 判断活动发起人所属地区是否核销人同一地区。
					// =======================
					SystemUser user = SystemUser.dao.findById(activityReport.getApplyUser());
					log.info("活动申请人是****>:" + user);

					SystemOffice office = SystemOffice.dao.findById(user.getOfficeId());// 活动发起人
					log.info("活动申请人的公司是****>:" + office);

					SystemArea area = SystemArea.dao.findById(office.getAreaId());// 活动发起人
					log.info("活动申请人的地区是****>:" + area);

					SystemUser loginUseruser = SystemUser.dao.findById(userId);
					log.info("当前登录的用户是****>:" + loginUseruser);

					SystemOffice loginUserOffice = SystemOffice.dao.findById(loginUseruser.getOfficeId());// 当前登陆人
					log.info("当前登录的用户的公司是****>:" + loginUserOffice);

					SystemArea loginUserArea = SystemArea.dao.findById(loginUserOffice.getAreaId());// 当前登陆人
					log.info("当前登录的用户的地区是****>:" + loginUserArea);
					// 链路不同属于异地核销，区域不同也属于异地核销，，只有在同一区域，同一链路才属于本地核销
					// 获取活动发起人的下级， 发起活动的用户的地区 和当前登录用户的地区进行比较
					// 其实只比较当前登录人和活动开启者的地区就可以了 不需要进行公司的比较
					result = ifArea(area, loginUserArea, office, loginUserOffice);
					log.info("是不是本地核销？****>:" + result);
					// 本地核销,否则异地核销，发送推送消息
					if (result) {
						saveCancel(openid, userId, activityReport, delivery, area, loginUserArea, office,
								loginUserOffice,theOutNo,recordId);
						delivery.set("ID", delivery.getId()).set("DISTRIBUTION_STATUS", "5").update();// 已使用
						state = "本地";
					} else {
						remoteCancel(openid, userId, activityReport, delivery, area, loginUserArea, office,
								loginUserOffice, theOutNo,recordId);
						delivery.set("ID", delivery.getId()).set("DISTRIBUTION_STATUS", "1").update();// 未配送
						// 发送推文消息
						ProductInfo info = ProductInfo.dao.findById(delivery.getPrizeName());
						wxInformation(cancel.getOpenid(), info, delivery);
						state = "异地";
					}
				} else {
					// 兑换和转盘抽奖核销
					IntegralRule intergral = new IntegralRule();
					// 获取兑换与抽奖是本地核销还是异地核销
					String sqlRule = "SELECT * FROM INTEGRAL_RULE WHERE \"NUMBER\" = ?";
					List<IntegralRule> rule = IntegralRule.dao.find(sqlRule, "wxCardCancel");
					for (IntegralRule intergr : rule) {
						intergral = intergr;
					}
					// 商城兑换卡券与转盘抽奖卡券：1是本地核销 2是异地核销
					if (intergral.getValue().equals("1")) {
						delivery.set("ID", delivery.getId()).set("DISTRIBUTION_STATUS", "5").update();// 已使用
					} else {

					}
				}
			}
			err = "OK";
		} else {
			err = "网络异常";
		}
		map.put("err", err);
		map.put("state", state);
		map.put("result", result);
		renderJson(map);
	}

	/**
	 * 判断是否是否同一个区域
	 */
	public boolean ifArea(SystemArea area, SystemArea loginUserArea, SystemOffice office,
			SystemOffice loginUserOffice) {
		boolean flag = true;
		// 判断是否属于同一地区,或下级地区 如果登陆者和活动申请人的地址相同则是同一个地区 本地核销
		if (area.getId().equals(loginUserArea.getId())) {
			return true;
		}
		// 如果登陆者和活动申请人的地址不相同的时候 再看他们是不是有层级关系 获取活动申请人的地区 然后从地区表里获取父级的ids中是否有该地区
		else {
			if (flag) {
				// 活动申请人的parentIds
				String parentIds = area.getParentIds();
				log.info("parentIds*****活动申请人地区parentIds**************>:" + parentIds);
				String[] ids = parentIds.split(",");
				String loginId = loginUserArea.getId();
				log.info("当前登录人地区的ID**************>:" + loginId);
				for (String id : ids) {
					log.info("活动申请人地区ID有**************>:" + id);
					// 如果登陆者的地区的id在活动申请人的地区的parentIds里能找到,则是属于本地的
					log.info("如果登陆者的地区的id在活动申请人的地区的parentIds里能找到,则是属于本地的");
					if (loginId.equals(id)) {
						flag = false;
						return true;
					}
				}
				// 判断活动申请人的公司 和登录人的公司是不是层级关系 如果是 则是本地
				String parentIdS2 = office.getParentIdS();
				String[] ids2 = parentIdS2.split(",");
				for (String id : ids2) {
					if (loginUserOffice.getId().equals(id)) {
						log.info("活动申请人公司parentIds**************>:" + ids);
						flag = false;
						return true;
					}
				}
			}
			if (flag) {
				// 如果在活动申请人的parentIds没有找到的话 再判断一下 当前登录者的地区的id 是不是活动申请人地区的下级
				String parentIds1 = loginUserArea.getParentIds();
				String[] ids1 = parentIds1.split(",");
				log.info("当前登录人地区parentIds**************>:" + ids1);
				for (String id : ids1) {
					log.info("如果在活动申请人的parentIds没有找到的话   再判断一下 当前登录者的地区的id 是不是活动申请人地区的下级");
					if (area.getId().equals(id)) {
						flag = false;
						return true;
					}
				}
				// 判断活动申请人的公司 和登录人的公司是不是层级关系 如果是 则是本地
				String parentIdS3 = loginUserOffice.getParentIdS();
				String[] ids3 = parentIdS3.split(",");
				log.info("登陆人公司parentIds**************>:" + ids3);
				for (String id : ids3) {
					log.info("判断当前登录人的officeParentIdS3里是不是有活动申请人的officeId");
					if (office.getId().equals(id)) {
						flag = false;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 保存本地核销
	 */
	@Before(Tx.class)
	public boolean saveCancel(String openid, String userId, ActivityReport acti, WinningDelivery delivery,
			SystemArea area, SystemArea loginUserArea, SystemOffice office, SystemOffice loginUserOffice,
			String theOutNo,Number recordId) {
		boolean result = false;
		// 判断当前登陆人是否核销过当前活动的码，核销过就是修改核销总数，没有就新建一个
		String findcancel = "SELECT * FROM LOCAL_CANCEL WHERE ACTIVITY_ID = ? AND CANCEL_USER = ? AND PRIZE_NAME = ? AND CREATE_DATE = ? AND CHECK_STATE = '0'";
		List<LocalCancel> local = LocalCancel.dao.find(findcancel, acti.getId(), userId, delivery.getPrizeName(),
				newDate());
		SystemUser user = SystemUser.dao.findById(userId);
		// 判断是否已经存在当前人核销过当前活动的奖品
		if (local.size() == 0) {
			// 保存本地核销
			new LocalCancel().set("ID", UUID.randomUUID().toString().replaceAll("-", ""))
					.set("ACTIVITY_NAME", acti.getActivityName()).set("ACTIVITY_PRIZES_ID", acti.getApplyUser())
					.set("ACTIVITY_PRIZES_ADDRESS", area.getId()).set("ACTIVITY_ID", acti.getId())
					.set("CANCEL_USER", userId).set("CANCEL_ADDRESS", loginUserArea.getId())
					.set("ACTIVITY_PRIZES_OFFICE", office.getId()).set("CANCEL_OFFICE", loginUserOffice.getId())
					.set("PRIZE_NAME", delivery.getPrizeName()).set("CREATE_DATE", newDate())
					.set("CANCEL_OPENID", openid).set("CANCEL_NUMBER", "1")
					.set("ACTIVITY_ADDRESS", office.getAddressName())
					.set("CANCEL_ADDRESSNAME", loginUserOffice.getAddressName())
					.set("RECORDID", recordId)
					.set("GOODS_NUMBER", theOutNo)
					.set("CANCELUSERNAME", user.getName())
					.set("CANCELUSERPHONE", user.getMobile())
					.save();
		} else if (local.size() == 1) {
			for (LocalCancel loca : local) {
				int number = Integer.parseInt(loca.getNumber()) + 1;
				loca.set("ID", loca.getId()).set("CANCEL_OPENID", openid).set("CANCEL_NUMBER", number + "").update();
			}
		} else {
			// 核销异常
			result = false;
		}
		return result;
	}

	/**
	 * 保存异地核销
	 */
	@Before(Tx.class)
	public boolean remoteCancel(String openid, String userId, ActivityReport acti, WinningDelivery delivery,
			SystemArea area, SystemArea loginUserArea, SystemOffice office, SystemOffice loginUserOffice,
			String theOutNo,Number recordId) {
		boolean result = false;
		// 判断当前登陆人是否核销过当前活动的码，核销过就是修改核销总数，没有就新建一个
		String findcancel = "SELECT * FROM REMOTE_CANCEL WHERE ACTIVITY_ID = ? AND CANCEL_USER = ? AND PRIZE_NAME = ? AND CREATE_TIME = ? AND CHECK_STATE = '0'";
		List<RemoteCancel> remote = RemoteCancel.dao.find(findcancel, acti.getId(), userId, delivery.getPrizeName(),
				newDate());
		SystemUser user = SystemUser.dao.findById(userId);
		// 判断是否已经存在当前人核销过当前活动的奖品
		if (remote.size() == 0) {
			// 保存异地核销
			new RemoteCancel().set("ID", UUID.randomUUID().toString().replaceAll("-", ""))
					.set("ACTIVITY_NAME", acti.getActivityName()).set("ACTIVITY_PRIZES_ID", acti.getApplyUser())
					.set("ACTIVITY_PRIZES_ADDRESS", area.getId()).set("ACTIVITY_ID", acti.getId())
					.set("CANCEL_USER", userId).set("CANCEL_ADDRESS", loginUserArea.getId())
					.set("ACTIVITY_PRIZES_OFFICE", office.getId()).set("CANCEL_OFFICE", loginUserOffice.getId())
					.set("PRIZE_NAME", delivery.getPrizeName()).set("CREATE_TIME", newDate())
					.set("GOODS_NUMBER", theOutNo).set("CANCEL_OPENID", openid).set("CANCEL_NUMBER", "1")
					.set("ACTIVITY_ADDRESS", office.getAddressName())
					.set("CANCEL_ADDRESSNAME", loginUserOffice.getAddressName())
					.set("RecordId", recordId)
					.set("CANCELUSERNAME", user.getName())
					.set("CANCELUSERPHONE", user.getMobile())
					.save();
		} else if (remote.size() == 1) {
			for (RemoteCancel cancel : remote) {
				int number = cancel.getNumber() + 1;
				cancel.set("ID", cancel.getId()).set("CANCEL_NUMBER", number).update();
			}
		} else {
			// 核销异常
			result = false;
		}
		return result;
	}

	/**
	 * 推送消息
	 */
	public boolean wxInformation(String openid, ProductInfo product, WinningDelivery delivery) {
		System.out.println("发送消息" + openid);
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String appid = wxconfig.getAppId();
		String http = wxconfig.getHttp();
		String json = " {\"touser\":\"" + openid + "\","
				+ "\"template_id\":\"J8EL1cNckoue3P-koS1i_kjYkML4rMHGm1zWg4wADLQ\","
				+ "\"url\":\"http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid
				+ "&redirect_uri=http%3a%2f%2f" + http + "%2fJZWX%2fcancel%2fWXInfoByAddress%3fwinningId%3d"
				+ delivery.getId() + "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect\","
				+ "\"topcolor\":\"#FF0000\"," + "\"data\":{" + "\"first\": {" + "\"value\":\"亲~您本次兑换的奖品如下\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"keyword1\":{" + "\"value\":\"" + product.getName() + "\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"keyword2\":{" + "\"value\":\"以实体店为准\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword3\":{" + "\"value\":\"" + newDate() + "\"," + "\"color\":\"#ca2426" + "\"},"
				+ "\"remark\":{" + "\"value\":\"您参与的扫码活动不在活动范围内，我们会尽快把奖品通过邮寄方式送达，请点击填写您的派送地址\","
				+ "\"color\":\"#ca2426\"}}}";
		WeixinUtil.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, json);
		return false;
	}

	/**
	 * 推送消息填写地址
	 */
	public void WXInfoByAddress() {
		// 中奖信息ID
		String winningId = getPara("winningId");
		WinningDelivery delicery = WinningDelivery.dao.findById(winningId);
		ProductInfo info = ProductInfo.dao.findById(delicery.getPrizeName());
		QRcodeInfo qrcode = QRcodeInfo.dao.findById(delicery.getCodeId());
		setAttr("qrcode", qrcode);
		setAttr("info", info);
		setAttr("winningId", winningId);
		setAttr("deliveryAddress", delicery.getAddressStatus());
		setAttr("deliveryType", delicery.getWinningWay());
		render("/front/cancel/cancelAward.html");
	}

	/**
	 * 对账推送消息
	 */
	public void accountsInformation() {
		// 本地核销记录
		String localId = getPara("localid");
		// 根据本地核销记录查出对账
		String sql = "SELECT * FROM ACCOUNTS WHERE CANCEL_ID = ?";
		List<Accounts> acc = Accounts.dao.find(sql, localId);
		LocalCancel local = LocalCancel.dao.findById(localId);
		Accounts accoun = new Accounts();
		for (Accounts accounts : acc) {
			accoun = accounts;
		}
		String selectTime2 = accoun.getSelectTime();
		String salesname = accoun.getSalesname();
		String remarks2 = accoun.getRemarks();
		//推送备注
		String remarks=selectTime2+"业务员"+salesname+remarks2;
		log.info("推送备注*****>"+remarks);
		WXConfigController wxconfig = new WXConfigController();
		String token = wxconfig.AccessTokenByName();
		String appid = wxconfig.getAppId();
		String http = wxconfig.getHttp();
		String url = "http%3a%2f%2f" + http + "%2fJZWX%2fcancel%2fAccountsOk%3fAccountsId%3d" + accoun.getId();
		System.out.println("url这里" + url);
		String json = " {\"touser\":\"" + local.getCancelOpenid() + "\","
				+ "\"template_id\":\"soDCViL91c9JuAbp2KzO1gR7BFIRDQFLtkVw67HmDGM\","
				+ "\"url\":\"http://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + url
				+ "&response_type=code&scope=snsapi_userinfo&state=wx#wechat_redirect\"," + "\"topcolor\":\"#FF0000\","
				+ "\"data\":{" + "\"first\": {" + "\"value\":\"您的奖品补贴账单已经生成，我们将尽快把活动奖品发放到店\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword1\":{" + "\"value\":\"" + accoun.getAccountsTime() + "\"," + "\"color\":\"#ca2426"
				+ "\"}," + "\"keyword2\":{" + "\"value\":\"" + accoun.getAccountsTime() + "-" + accoun.getAccountsTime()
				+ "\"," + "\"color\":\"#ca2426" + "\"}," + "\"keyword3\":{" + "\"value\":\"业务员派送中\","
				+ "\"color\":\"#ca2426" + "\"}," + "\"remark\":{" + "\"value\":\"" + remarks + "\"," + "\"color\":\"#ca2426\"}}}";
		WeixinUtil.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token, json);
		renderNull();
	}

	/**
	 * 跳转确认对账页面
	 */
	public void AccountsOk() {
		// 判断当前人员是否登陆
		String openId = getOpenId();
		String AccountsId = getPara("AccountsId");
		String sql = "SELECT * FROM CANCEL_USER WHERE OPENID = ? ";
		List<CancelUser> user = CancelUser.dao.find(sql, openId);
		if (user.size() != 0) {
			// CancelUser cancelUser2 = user.get(0);
			// 这里获取的用户的ID
			// String userId = cancelUser2.getSystemUserId();
			// SystemUser systemUser = SystemUser.dao.findById(userId);
		}
		boolean rest = false;
		boolean accountsOk = false;
		Accounts acc = new Accounts();
		SystemOffice office = new SystemOffice();
		CancelUser canceluser = new CancelUser();
		List<AccountsCancel> acccancel = new ArrayList<AccountsCancel>();
		// 等于0，属于未登陆
		if (user.size() == 0) {
			rest = false;
		} else {
			for (CancelUser cancel : user) {
				// 已登陆
				if (cancel.getCancelLogin().equals("1")) {
					// 查询出当前的对账
					acc = Accounts.dao.findById(AccountsId);
					// 判断是否已经对账
					if (acc.getState().equals("1")) {// 已核销
						// 查询出当前人员的所有已对账
						String sqlacc = "SELECT * FROM ACCOUNTS WHERE ACCOUNTS_CONFIRM_ID = ?";
						List<Accounts> accList = Accounts.dao.find(sqlacc, cancel.getSystemUserId());
						for (Accounts accounts : accList) {
							AccountsCancel acccan = new AccountsCancel();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
							String a[] = sdf.format(accounts.getAccountsTime()).split(" ");
							acccan.setAcctionTime(a[0]);
							SystemUser sysUser = SystemUser.dao.findById(accounts.getAccountsConfirmId());
							acccan.setAcctionUser(sysUser.getName());
							acccan.setPrizeNumber(accounts.getPrizeNumber());
							ProductInfo info = ProductInfo.dao.findById(accounts.getPrizeId());
							acccan.setProductName(info.getName());
							acccancel.add(acccan);
						}
						accountsOk = true;
					} else {
						// 发起对账经销商
						office = SystemOffice.dao.findById(acc.getAccountsDistrbutorId());
						rest = true;
						canceluser = cancel;
					}
				}
				// 未登陆
				if (cancel.getCancelLogin().equals("0")) {
					rest = false;
				}
			}
		}
		setAttr("office", office);
		setAttr("Accounts", acc);
		setAttr("canceluser", canceluser);
		setAttr("rest", rest);
		setAttr("cancel", acccancel);// 查看核销历史
		setAttr("openId", openId);
		String url = "";
		// accountsOk等于false表示未对账，true已对账
		if (accountsOk) {
			url = "/front/cancel/cancel-history.html";
		} else {
			url = "/front/cancel/statement-account.html";
		}

		render(url);
	}

	/**
	 * 确认对账
	 */
	public void AccountsConfirm() {
		String openId = getOpenId();
		String AccountsId = getPara("accountsId");
		String canceluser = getPara("canceluser");
		// 查询出当前用户
		CancelUser canuser = CancelUser.dao.findById(canceluser);
		SystemUser user = SystemUser.dao.findById(canuser.getSystemUserId());
		// 查询出当前的对账 修改对账人和对账经销商
		Accounts acc = Accounts.dao.findById(AccountsId);
		SystemUser user1 = SystemUser.dao.findById(acc.getAccountsUserId());
		SystemOffice office = SystemOffice.dao.findById(acc.getAccountsDistrbutorId());
		boolean update = acc.set("ID", acc.getId()).set("ACCOUNTS_CONFIRM_ID", user.getId())
				.set("ACCOUNTS_CONFIRM_DISTRIBUTOR_I", user.getOfficeId()).set("STATE", "1").update();
		if (update) {
			/**
			 * 确认对账之后 将LOCAL_CANCEL 里面的状态修改一下
			 */
			String sql = "SELECT * FROM LOCAL_CANCEL WHERE CANCEL_USER = ? ";
			LocalCancel localCancel = LocalCancel.dao.findFirst(sql, user.getId());
			if (localCancel != null) {
				localCancel.set("ID", localCancel.getId()).set("CHECK_STATE", "1").update();
			}
			/**
			 * 还要将申请对账列表里的该条记录的状态修改一下
			 */
			String cancelId = acc.getCancelId();
			String sql1="select * from accountsapply where cancelid = ?";
			List<AccountsApply> list = AccountsApply.dao.find(sql1,cancelId);
			for (AccountsApply a : list) {
				boolean update2 = a.set("ID", a.getId())
				 .set("HANDLEUSERNAME", user1.getName())
				 .set("HANDLEOFFICE", office.getName())
				 .set("HANDLEUSERCODE", user.getId())
				 .set("STATE", "2")
				 .set("HANDLETIME", DateToString(new Date()))
				 .update();
				log.info("申请列表的状态修改成功！****>"+update2);
			}
		}
		// 查询出当前人员的所有已对账
		String sql = "SELECT * FROM ACCOUNTS WHERE ACCOUNTS_CONFIRM_ID = ?";
		List<Accounts> accList = Accounts.dao.find(sql, canuser.getSystemUserId());
		List<AccountsCancel> cancel = new ArrayList<AccountsCancel>();
		for (Accounts accounts : accList) {
			AccountsCancel acccan = new AccountsCancel();
			// String a[] = accounts.getAccountsTime().split(" ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
			String a[] = sdf.format(accounts.getAccountsTime()).split(" ");
			acccan.setAcctionTime(a[0]);
			SystemUser sysUser = SystemUser.dao.findById(accounts.getAccountsConfirmId());
			acccan.setAcctionUser(sysUser.getName());
			acccan.setPrizeNumber(accounts.getPrizeNumber());
			ProductInfo info = ProductInfo.dao.findById(accounts.getPrizeId());
			acccan.setProductName(info.getName());
			cancel.add(acccan);
		}
		setAttr("cancel", cancel);
		setAttr("openId", openId);
		render("/front/cancel/cancel-history.html");
	}

	/**
	 * 查询当前用户的所有的对账记录
	 */
	public void getAllAccounts() {
		// 对账人的ID
		String accountsConfirmId = getPara("accountsConfirmId");
		String sql = "SELECT * FROM ACCOUNTS WHERE 1=1 AND ACCOUNTS_CONFIRM_ID = '" + accountsConfirmId + "'";
		List<Accounts> accList = Accounts.dao.find(sql);
		List<AccountsCancel> cancel = new ArrayList<AccountsCancel>();
		for (Accounts accounts : accList) {
			AccountsCancel acccan = new AccountsCancel();
			// String a[] = accounts.getAccountsTime().split(" ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
			String a[] = sdf.format(accounts.getAccountsTime()).split(" ");
			acccan.setAcctionTime(a[0]);
			SystemUser sysUser = SystemUser.dao.findById(accounts.getAccountsConfirmId());
			acccan.setAcctionUser(sysUser.getName());
			acccan.setPrizeNumber(accounts.getPrizeNumber());
			ProductInfo info = ProductInfo.dao.findById(accounts.getPrizeId());
			acccan.setProductName(info.getName());
			cancel.add(acccan);
		}
		String sqlOpenId = "SELECT * FROM CANCEL_USER WHERE SYS_USER_ID = ? ";
		CancelUser cancelUser = CancelUser.dao.findFirst(sqlOpenId, accountsConfirmId);
		setAttr("cancel", cancel);
		setAttr("openId", cancelUser.getOpenid());
		render("/front/cancel/cancel-history.html");
	}

	/**
	 * 根据日期查询对账的记录
	 */
	public void getAccounts() {
		// 2017年7月3日
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String openId = getPara("openId");
		// 根据日期查询指定范围内的对账的记录
		/********/
		// 查询出当前用户
		String sqlCancelUser = "SELECT * FROM CANCEL_USER WHERE OPENID = ? ";
		CancelUser canuser = CancelUser.dao.findFirst(sqlCancelUser, openId);
		// SystemUser user = SystemUser.dao.findById(canuser.getSystemUserId());
		// 查询出当前人员的所有已对账 2017-07-17 17:38:26
		String systemUserId = canuser.getSystemUserId();
		String sql = null;
		if (!"".equals(startTime) && !"".equals(endTime)) {
			sql = "SELECT * FROM ACCOUNTS WHERE 1=1 AND ACCOUNTS_CONFIRM_ID = '" + systemUserId
					+ "' AND ACCOUNTS_TIME >= TO_DATE('" + startTime
					+ "','yyyy-mm-dd hh24:mi:ss') AND ACCOUNTS_TIME <= TO_DATE('" + endTime
					+ "','yyyy-mm-dd hh24:mi:ss')";
		} else {
			sql = "SELECT * FROM ACCOUNTS WHERE 1=1 AND ACCOUNTS_CONFIRM_ID = '" + systemUserId + "'";
		}
		List<Accounts> accList = Accounts.dao.find(sql);
		List<AccountsCancel> cancel = new ArrayList<AccountsCancel>();
		for (Accounts accounts : accList) {
			AccountsCancel acccan = new AccountsCancel();
			// String a[] = accounts.getAccountsTime().split(" ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
			String a[] = sdf.format(accounts.getAccountsTime()).split(" ");
			acccan.setAcctionTime(a[0]);
			SystemUser sysUser = SystemUser.dao.findById(accounts.getAccountsConfirmId());
			acccan.setAcctionUser(sysUser.getName());
			acccan.setPrizeNumber(accounts.getPrizeNumber());
			ProductInfo info = ProductInfo.dao.findById(accounts.getPrizeId());
			acccan.setProductName(info.getName());
			cancel.add(acccan);
		}
		setAttr("cancel", cancel);
		setAttr("openId", openId);
		render("/front/cancel/cancel-history.html");
	}

	/**
	 * 时间格式转换
	 */
	public static String newDate() {
		Date date = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		return time.format(date);
	}

	/**
	 * 时间格式转换
	 */
	public static String DZDate(Date date) {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		return time.format(date);
	}
	
	/**
	 * 时间格式转换
	 */
	public static String DateToString(Date date) {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return time.format(date);
	}

	/**
	 * 时间格式转换
	 */
	public static String ZDate(String date) {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		return time.format(date);
	}
	
	/**
	 * string转时间
	 */
	public Date dateToString(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date=null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
