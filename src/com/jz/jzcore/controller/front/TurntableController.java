package com.jz.jzcore.controller.front;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.NestedTransactionHelpException;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.IntegralRule;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.ProductInfo;
import com.jz.jzcore.model.TurntableRaffle;
import com.jz.jzcore.model.WinningDelivery;
import com.jz.web.common.controller.ControllerPath;

/**
 * 转盘抽奖处理
 * */
@ControllerPath(controllerKey = "/turntable")
public class TurntableController extends FrontController {
	/**
	 * 生成中奖奖品
	 * */
	
	public void Prize(){
		SqlController sqlconfig = new SqlController();
		ThingDao dao = new ThingDao();
		Map<String, Object> map = new HashMap<String, Object>();
		Connection conn=null;
		try {
			 conn=sqlconfig.bengin();
			    //中间写代码   用所有数据链接    Conn你链接数据库     时间不够这么了
			 String openid = getOpenId();
				//减少用户积分
				String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
				List<IntegralVip> vip = dao.findIntegralVip(conn, sqlvip, openid);
				//获取转盘抽奖所需积分
				String sqlintergral = "SELECT * FROM INTEGRAL_RULE ";
				List<IntegralRule> rule= IntegralRule.dao.find(sqlintergral);
				String prizeNumber = "";
				IntegralRule integralrule = new IntegralRule();
				for(IntegralRule ir : rule){
					if(ir.getNumber().equals("drawNumber")){
						prizeNumber = ir.getValue();
					}else if(ir.getNumber().equals("draw")){
						integralrule = ir;
					}
				}
				String id = "";
				int SurplusIntegra = 0;
				//减少当前人员的积分
				for(IntegralVip integra : vip){
					id = integra.getId();
					SurplusIntegra = integra.getAllIntegral()-Integer.parseInt(integralrule.getValue());
				}
				String sql = "SELECT * FROM TURNTABLE_RAFFLE WHERE TURNTABLE = ?";
				//这里查询转盘配置里的奖品
				List<TurntableRaffle> turn = dao.findTurntableRaffle(conn,sql,"0");
		        TurntableRaffle turntableRaffle = null ;
		        ProductInfo product = new ProductInfo();
				 //总的概率区间
		        float totalPro = 0f;
		        //存储每个奖品新的概率区间
		        List<Float> proSection = new ArrayList<Float>();
		        proSection.add(0f);
		        //这个用来存储所有的奖品总数
		        int number = 0;
		        //遍历每个奖品，设置概率区间，总的概率区间为每个概率区间的总和
		        for (TurntableRaffle Raffle : turn) {
		            //计算奖品总数
		        	number =number+Raffle.getSurplusNumber();
		        	//每个概率区间为奖品概率乘以1000（把三位小数转换为整）再乘以剩余奖品数量
		            totalPro += Float.parseFloat(Raffle.getNewProbability()) * 1000  * Raffle.getSurplusNumber();
		            //构建新的概率的集合
		            proSection.add(totalPro);
		        }
		        //获取总的概率区间中的随机数
		        Random random = new Random();
		        float randomPro = (float)random.nextInt((int)totalPro);
		        //判断取到的随机数在哪个奖品的概率区间中
		        for (int i = 0,size = proSection.size(); i < size; i++) {
		            if(randomPro >= proSection.get(i) && randomPro < proSection.get(i + 1)){
		            	turntableRaffle = turn.get(i);
		            		//奖品剩余数量减一	
		            		number = number -1;
		            		product = ProductInfo.dao.findById(turntableRaffle.getPrizeName());
		            		//获奖数量           特别备注：此处使用ProductInfo中的getProductsize，临时存放当前一次扫码的获奖数量
		            		product.setProductsize(turntableRaffle.getWinningNumber()+"");
		                	//修改当前奖品的剩余数量
		            		turn.get(i).setSurplusNumber(turntableRaffle.getSurplusNumber()-1);
		            		//统计修改后的概率
		            		Float allprobability =(float)0.00;
		            		String probability = null;
		            		//重新算出所有奖品概率   turntablelist所有奖品概率，包括积分奖品
		            		for(TurntableRaffle reffle : turn){
		            			//判断是否都是0
		            			if(reffle.getSurplusNumber()==0){
		            				probability = "0.00";
		            			}else{
			            			//重算后的概率
			            			probability = algorithm(reffle.getSurplusNumber(),number);
			            			if(Float.parseFloat(probability) == 100 | Float.parseFloat(probability) == 0){
			            				allprobability = Float.parseFloat(probability);
			            			}else{
			            				allprobability = allprobability + Float.parseFloat(probability);
			            			}
		            			}
		            			reffle.setWinningProbability(probability);
		            			//根据ID，保存当前概率
		            			new TurntableRaffle().set("ID", reffle.getId()).set("SURPLUS_NUMBER", reffle.getSurplusNumber()).set("NEWPROBABILITY", reffle.getWinningProbability()).update();
		            	}
		            }
		        }
		        //修改当前人员的积分
		        new IntegralVip().set("ID",id).set("ALL_INTEGRAL",SurplusIntegra).update();
		        map.put("SurplusIntegra", SurplusIntegra);
		        map.put("prizeList", turn);
		        map.put("prizeNumber", prizeNumber);
		        map.put("product", product);
		        map.put("prizeId", turntableRaffle.getId());
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
	 * 领取转盘中奖的积分
	 * */
	public void getInteral(){
		String openid = getOpenId();
		//中奖积分
		String interal = getPara("interal");
		//产品ID
		String productId = getPara("productId");
		//判断是转盘还是兑换
		String info = getPara("info");
		//积分累计
		String sqlvip = "SELECT * FROM INTEGRAL_VIP WHERE OPENID = ?";
		List<IntegralVip> vip = IntegralVip.dao.find(sqlvip, openid);
		String id = "";
		int SurplusIntegra = 0;
		for(IntegralVip integra : vip){
			id = integra.getId();
			SurplusIntegra = integra.getAllIntegral() + Integer.parseInt(interal);
		}
		boolean rsutle = false;
		//判断地址是否为空
		if(info.equals("转盘")){
			rsutle = new WinningDelivery().
					set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
					set("WINNING_WAY","2").
					set("PRIZE_NAME", productId).
					set("WINNING_TIME",Timestamp.valueOf(newDate())).
					set("OPENID",openid).
					set("DISTRIBUTION_STATUS","3").
					set("GRAIN_GRAM",interal).
					set("PRIZE_STATUS","1").
					set("PRIZE_TYPE","1").
					save();
		}else if(info.equals("兑换")){
			rsutle = new WinningDelivery().
					set("ID",UUID.randomUUID().toString().replaceAll("-", "")).
					set("WINNING_WAY","3").
					set("PRIZE_NAME", productId).
					set("WINNING_TIME",Timestamp.valueOf(newDate())).
					set("OPENID",openid).
					set("DISTRIBUTION_STATUS","3").
					set("GRAIN_GRAM",interal).
					set("PRIZE_STATUS","1").
					set("PRIZE_TYPE","1").
					save();
		}
		//修改当前人员的积分
		if(rsutle){
			new IntegralVip().set("ID",id).set("ALL_INTEGRAL",SurplusIntegra).update();
		}
		renderJson(rsutle);
	}
	
	/**
	 * 算概率
	 * */
	public String algorithm(int SurplusNumber ,int number){
		Float nowprize = (float)SurplusNumber/ (float)number*100;
		DecimalFormat df = new DecimalFormat("##0.00");//格式化小数   
		String probability = df.format(nowprize);//返回的是String类型
		return probability;
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
