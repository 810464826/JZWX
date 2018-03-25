package com.jz.jzcore.config;

import com.jfinal.config.Handlers;
import com.jz.web.common.config.BaseConfig;

public class ExpressConfig extends BaseConfig {

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub
	}
	
	//在系统停止时调用的方法
    public void beforeJFinalStop() {
    
    };
    
    //在系统启动时调用的方法
    @Override
    public void afterJFinalStart() {
        // TODO Auto-generated method stub
    	
    	
        super.afterJFinalStart();
    }
}
