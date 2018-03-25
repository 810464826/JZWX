package com.jz.web.common.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.BslPlugin;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.tx.TxByActionKeyRegex;
import com.jfinal.plugin.activerecord.tx.TxByActionKeys;
import com.jfinal.plugin.activerecord.tx.TxByMethodRegex;
import com.jfinal.plugin.activerecord.tx.TxByMethods;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jz.jzcore.config.Cron4jPlugin;
import com.jz.jzcore.controller.front.FrontController;
import com.jz.jzcore.model._MappingKit;
import com.jz.web.common.controller.AutoBindRoutes;
import com.jz.web.common.interceptor.BaseInterceptor;

public abstract class BaseConfig extends JFinalConfig {

	/**
	 * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
	 * 
	 * @param pro
	 *            生产环境配置文件
	 * @param dev
	 *            开发环境配置文件
	 */
	public void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		} catch (Exception e) {
			PropKit.use(dev);
		}
	}

	@Override
	public void configConstant(Constants me) {
		//设置APP图片上传路径，读取配置文件配置的路径
		me.setBaseUploadPath(PropKit.use("system.properties").get("baseUploadPath"));
		//
		me.setMaxPostSize(512000000);
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		loadProp("develop.properties", "system.properties");
		loadProp("develop.properties", "task.properties");
		PropKit.use("system.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
		ApiConfigKit.setDevMode(me.getDevMode());
		ApiConfigKit.setThreadLocalAppId(getApiConfig().getAppId());
		me.setError404View("/error/404.html");
		me.setError500View("/error/500.html");
	}
	
	@Override
	public void configRoute(Routes me) {
		AutoBindRoutes autoBindRoutes = new AutoBindRoutes();
		autoBindRoutes.addExcludeClass(ApiController.class);
		autoBindRoutes.addExcludeClass(MsgController.class);
		autoBindRoutes.addExcludeClass(FrontController.class);
		me.add(autoBindRoutes);
	}

	public static DruidPlugin createDruidPlugin() {
		return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}

	@Override
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin cp = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());  
		//配置Oracle驱动  
		cp. setDriverClass("oracle.jdbc.driver.OracleDriver");  
		me.add(cp);  
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		arp.setTransactionLevel(2);
//		 transactionLevel:
//			static int TRANSACTION_NONE = 0;      //JDBC驱动不支持事务
//			static int TRANSACTION_READ_UNCOMMITTED = 1;  //允许脏读、不可重复读和幻读。
//			static int TRANSACTION_READ_COMMITTED = 2;  //禁止脏读，但允许不可重复读和幻读。
//			static int TRANSACTION_REPEATABLE_READ = 4; //禁止脏读和不可重复读，单运行幻读。
//			static int TRANSACTION_SERIALIZABLE = 8; //禁止脏读、不可重复读和幻读。
		me.add(arp);  
		// 配置Oracle方言   
		arp.setDialect(new OracleDialect());   
		// 配置属性名(字段名)大小写不敏感容器工厂   
		arp.setContainerFactory(new CaseInsensitiveContainerFactory()); 
	
		//配置定时器
		me.add(new Cron4jPlugin(PropKit.use("task.properties")));
		
		// 所有配置在 MappingKit 中搞定
		_MappingKit.mapping(arp); 	

		// 缓存配置
		EhCachePlugin ecp = new EhCachePlugin();
		me.add(ecp);

		// 配置Bsl引擎模版视图
		me.add(new BslPlugin(PropKit.get("viewPath")));

	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new BaseInterceptor());
		me.add(new I18nInterceptor());
		me.add(new TxByMethodRegex("(.*save.*|.*update.*)")); 
		me.add(new TxByMethods("save", "update")); 
		me.add(new TxByActionKeyRegex("/trans.*")); 
		me.add(new TxByActionKeys("/tx/save", "/tx/update"));
	}
	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}
}
