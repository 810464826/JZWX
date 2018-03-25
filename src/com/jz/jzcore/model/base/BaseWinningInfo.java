package com.jz.jzcore.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * 中奖规则
 * @author ChenXb
 *
 */
@SuppressWarnings("serial")
public class BaseWinningInfo<M extends BaseWinningInfo<M>> extends Model<M> implements IBean {
	
		public void setId(java.lang.String id) {
			set("ID", id);
		}

		public java.lang.String getId() {
			return get("ID");
		}
		//名称
		public void setName(java.lang.String name) {
			set("NAME", name);
		}

		public java.lang.String getName() {
			return get("NAME");
		}
		//微信ID
		public void setOpenid(java.lang.String openid) {
			set("OPENID", openid);
		}

		public java.lang.String getOpenid() {
			return get("OPENID");
		}
		//奖品
		public void setPrize(java.lang.String prize) {
			set("PRIZE", prize);
		}

		public java.lang.String getPrize() {
			return get("PRIZE");
		}
		//中奖时间
		public void setWinningTime(java.lang.String winning_time) {
			set("WINNING_TIME", winning_time);
		}

		public java.lang.String getWinningTime() {
			return get("WINNING_TIME");
		}
		//中奖路径
		public void setWinningChannel(java.lang.String winning_channel) {
			set("WINNING_CHANNEL", winning_channel);
		}

		public java.lang.String getWinningChannel() {
			return get("WINNING_CHANNEL");
		}
}

