package com.jz.jzcore.controller.front;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Config;
import com.jz.jzcore.config.SqlController;
import com.jz.jzcore.model.IntegralMall;
import com.jz.jzcore.model.IntegralVip;
import com.jz.jzcore.model.QRcodeInfo;
import com.jz.jzcore.model.TurntableRaffle;

@SuppressWarnings("serial")
public class ThingDao extends SqlController {
	public List<IntegralVip> findIntegralVip(Connection conn, String sql, Object... paras) throws Exception {
		Config config = getConfig();
		PreparedStatement pst = conn.prepareStatement(sql);
		config.getDialect().fillStatement(pst, paras);
		ResultSet rs = pst.executeQuery();
		List<IntegralVip> LI=new ArrayList<>();
		while (rs.next()) {
			IntegralVip lv=new IntegralVip();
			lv.setId(rs.getString("ID"));
			lv.setOpenid(rs.getString("OPENID"));
			lv.setLuckyDraw(rs.getString("LUCKY_DRAW"));
			lv.setPhone(rs.getString("PHONE"));
			lv.setAllIntegral(rs.getInt("ALL_INTEGRAL"));
			lv.setLuckyNumber(rs.getInt("LUCKY_NUMBER"));
			LI.add(lv);
	    }
		return LI;
	}
    
	public List<TurntableRaffle> findTurntableRaffle(Connection conn, String sql, Object... paras) throws Exception {
		Config config = getConfig();
		PreparedStatement pst = conn.prepareStatement(sql);
		config.getDialect().fillStatement(pst, paras);
		ResultSet rs = pst.executeQuery();
		List<TurntableRaffle> LI=new ArrayList<>();
		while (rs.next()) {
			TurntableRaffle tr=new TurntableRaffle();
			tr.setId(rs.getString("ID"));
			tr.setPrizeName(rs.getString("PRIZE_NAME"));
			tr.setPrizeNumber(rs.getInt("SURPLUS_NUMBER"));
			tr.setSurplusNumber(rs.getInt("SURPLUS_NUMBER"));
			tr.setPrizeType(rs.getString("PRIZE_TYPE"));
			tr.setWinningNumber(rs.getInt("WINNING_NUMBER"));
			tr.setWinningProbability(rs.getString("WINNING_PROBABILITY"));
			tr.setWxCardId(rs.getString("WX_CARD_ID"));
			tr.setActivityId(rs.getString("ACTIVITY_ID"));
			tr.setNewProbability(rs.getString("NEWPROBABILITY"));
			LI.add(tr);
	    }
		return LI;
	}
	/**
	 * 根据ID查询出QRcodeInfo
	 * */
	public QRcodeInfo getQRcodeInfoById(Connection conn, String sql, Object... paras){
		Config config = getConfig();
		PreparedStatement pst;
		QRcodeInfo info = new QRcodeInfo();
		try {
			pst = conn.prepareStatement(sql);
			config.getDialect().fillStatement(pst, paras);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				info.setId(rs.getString("ID"));
				info.setQRcodeId(rs.getString("QRCODE_ID"));
				info.setVerificationCode(rs.getString("VERIFICATION_CODE"));
				info.setProductInfo(rs.getString("PRODUCT_INFO"));
				info.setBoxId(rs.getString("BOX_ID"));
				info.setInfo(rs.getString("INFO"));
				info.setState(rs.getInt("STATE"));
				info.setQRcodeNumber(rs.getInt("QRCODE_NUMBER"));
				info.setFinalPrize(rs.getString("FINAL_PRIZE"));
				info.setDeliverGoodsId(rs.getString("DELIVER_GOODS_ID"));
				info.setExchange(rs.getString("EXCHANGE"));
				info.setWinningNumber(rs.getInt("WINNING_NUMBER"));
				info.setBoxCode(rs.getString("BOXCODE"));
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	/**
	 * 根据ID查询出IntegralMall
	 * */
	public IntegralMall getIntegralMallById(Connection conn, String sql, Object... paras){
		Config config = getConfig();
		PreparedStatement pst;
		IntegralMall mall = new IntegralMall();
		try {
			pst = conn.prepareStatement(sql);
			config.getDialect().fillStatement(pst, paras);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				mall.setId(rs.getString("ID"));
				mall.setName(rs.getString("NAME"));
				mall.setEchange(rs.getString("EXCHANGE"));
				mall.setRecommend(rs.getString("RECOMMEND"));
				mall.setTolal(rs.getString("TOTAL"));
				mall.setRemainingQuantify(rs.getString("REMAINING_QUANTIFY"));
				mall.setPrizeType(rs.getString("PRIZE_TYPE"));
				mall.setCardId(rs.getString("CARD_ID"));
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mall;
	}
	
	/**
	 * 根据ID查询出IntegralMall
	 * */
	public IntegralMall UpdateIntegralMallById(Connection conn, String sql, Object... paras){
		Config config = getConfig();
		PreparedStatement pst;
		IntegralMall mall = new IntegralMall();
		try {
			pst = conn.prepareStatement(sql);
			config.getDialect().fillStatement(pst, paras);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				mall.setId(rs.getString("ID"));
				mall.setName(rs.getString("NAME"));
				mall.setEchange(rs.getString("EXCHANGE"));
				mall.setRecommend(rs.getString("RECOMMEND"));
				mall.setTolal(rs.getString("TOTAL"));
				mall.setRemainingQuantify(rs.getString("REMAINING_QUANTIFY"));
				mall.setPrizeType(rs.getString("PRIZE_TYPE"));
				mall.setCardId(rs.getString("CARD_ID"));
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mall;
	}
	
	
}
