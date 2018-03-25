package com.jz.jzcore.controller.front;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jfinal.weixin.sdk.api.JsApi;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.IntegralMall;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.WxCard;
import com.jz.web.common.controller.ControllerPath;

@ControllerPath(controllerKey = "/mall")
public class MallController extends FrontController{
	/**
	 * 推荐更多
	 * */
	public void Recommendmore(){
		String openid = getOpenId();
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		IntegralVip integravip = new IntegralVip();
		for(IntegralVip iv : vip){
			integravip = iv;
		}
		//推荐
		String sqlRecommend = "SELECT * FROM INTEGRAL_MALL WHERE RECOMMEND = ?";
		List<IntegralMall> mallRecommend = IntegralMall.dao.find(sqlRecommend,"1");
		for(IntegralMall  mall :mallRecommend){
			ProductInfo info = ProductInfo.dao.findById(mall.getName());
			mall.setPrizeName(info.getName());
			mall.setImg(info.getImgid());
		}
		setAttr("integravip", integravip);
		setAttr("mallRecommend", mallRecommend);
		render("/front/Recommend-more.html");
	}
	/**
	 * 全部更多
	 * */
	public void more(){
		String openid = getOpenId();
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		IntegralVip integravip = new IntegralVip();
		for(IntegralVip iv : vip){
			integravip = iv;
		}
		//不推荐
		String sqlDeprecated = "SELECT * FROM INTEGRAL_MALL";
		List<IntegralMall> mallDeprecated = IntegralMall.dao.find(sqlDeprecated);
		for(IntegralMall  mall :mallDeprecated){
			ProductInfo info = ProductInfo.dao.findById(mall.getName());
			mall.setPrizeName(info.getName());
			mall.setImg(info.getImgid());
		}
		setAttr("integravip", integravip);
		setAttr("mallDeprecated", mallDeprecated);
		render("/front/more.html");
	}
	/**
	 * 查询
	 * */
	public void queryMore(){
		String openid = getOpenId();
		String query = getPara("query");
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		IntegralVip integravip = new IntegralVip();
		for(IntegralVip iv : vip){
			integravip = iv;
		}
		String sqlDeprecated = "SELECT a.\"ID\" AS \"ID\" ,a.\"NAME\" AS \"NAME\",a.\"EXCHANGE\" AS \"EXCHANGE\",a.RECOMMEND AS RECOMMEND,a.TOTAL AS TOTAL,"
				+ "a.REMAINING_QUANTIFY AS REMAINING_QUANTIFY,a.PRIZE_TYPE AS PRIZE_TYPE,a.CARD_ID AS CARD_ID FROM INTEGRAL_MALL a LEFT JOIN PRODUCT_INFO b ON a.\"NAME\" = b.\"ID\"  WHERE b.NAME LIKE '%"+query+"%'";
		List<IntegralMall> mallDeprecated = IntegralMall.dao.find(sqlDeprecated);
		for(IntegralMall  mall :mallDeprecated){
			ProductInfo info = ProductInfo.dao.findById(mall.getName());
			mall.setPrizeName(info.getName());
			mall.setImg(info.getImgid());
		}
		setAttr("integravip", integravip);
		setAttr("mallDeprecated", mallDeprecated);
		render("/front/more.html");
	}
	
	
	/**
	 * 去兑换，展示商品详细
	 * */
	public void exchange(){
		String mallId = getPara("mallId");
		IntegralMall mall = IntegralMall.dao.findById(mallId);
		ProductInfo info = ProductInfo.dao.findById(mall.getName());
		mall.setPrizeName(info.getName());
		mall.setImg(info.getImgid());
		setAttr("mall", mall);
		render("/front/exchange-product-details.html");
	}
	
	/** 兑换发卡券或者填写物流 */
	public void exchangeCard(){
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		Map<String, String> map = new HashMap<String, String>();
		Connection conn=null;
		String restul = "0";
		String timestamp = "";
		String signature= "";
		String cardId = "";
		try {
			 conn=sqlconfig.bengin();
			String openid = getOpenId();
			// 随机字符串
			String nonceStr = create_nonce_str();
			//所需积分
			String echange = getPara("echange");
			//卡券ID
			String card = getPara("cardId");
			//兑换奖品ID
			String mallId = getPara("mallId");
			String sqlmall = "SELECT * FROM INTEGRAL_MALL WHERE ID = ?";
			IntegralMall mall = dao.getIntegralMallById(conn, sqlmall, mallId);
			if(!mall.getRemainingQuantify().equals("0")){
				if(!card.equals("")){
					WxCard  wxcard = WxCard.dao.findById(card);
					cardId = wxcard.getCardId();
				}
				//扣除积分 
				String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
				List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
				IntegralVip integravip = new IntegralVip();
				for(IntegralVip iv : vip){
					integravip = iv;
				}
				if(integravip.getAllIntegral() - Integer.parseInt(echange) < 0){
					restul = "1";
				}else{
					int mallremainintg = Integer.parseInt(mall.getRemainingQuantify()) - 1;
					mall.setRemainingQuantify(mallremainintg+"");
					new IntegralMall().set("ID", mall.getId()).set("REMAINING_QUANTIFY", mall.getRemainingQuantify()).update();
					integravip.setAllIntegral(integravip.getAllIntegral() - Integer.parseInt(echange));
					 //修改当前人员的积分
			        new IntegralVip().set("ID",integravip.getId()).set("ALL_INTEGRAL",integravip.getAllIntegral()).update();
				}
				//时间戳
				long time = System.currentTimeMillis() / 1000;
				timestamp = Long.toString(time);
				signature= JsApi.getWxSingature(timestamp,nonceStr,cardId,openid);
			}else{
				restul = "2";
			}
			map.put("cardId", cardId);
			map.put("timestamp", timestamp);
			map.put("nonceStr", nonceStr);
			map.put("signature", signature);
			map.put("openid", openid);
			map.put("restul", restul);
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
		renderJson(map);
	}
	
    /** 
     * 产生随机串--由程序自己随机产生 
     * @return 
     */  
    private static String create_nonce_str() {  
        return UUID.randomUUID().toString();  
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
