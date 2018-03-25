package com.jz.web.common.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jz.jzcore.common.Constants;
import com.jz.web.common.kit.UserAgentUtils;

public class BaseInterceptor implements Interceptor {
	
	String staticPath ="";
	@Override
	public void intercept(Invocation inv) {
		before(inv);
		inv.invoke();
		after(inv);
	}

	private void after(Invocation inv) {
		inv.getController().setAttr("staticPath", staticPath);
		inv.getController().setAttr("basePath", JFinal.me().getContextPath());
	}

	private void before(Invocation inv) {
		// 设置静态资源文件访问路径
		HttpServletRequest request = inv.getController().getRequest();
		System.err.println("sessionId:"+request.getSession().getId());
		// 判断设备是移动端还是PC
		String deviceType = (String) request.getSession().getAttribute(Constants.DEVICE_TYPE);
		String requestDeviceType = "mobile";//UserAgentUtils.isMobileOrTablet(request) ? "mobile" : "views";
		if (StringUtils.isBlank(deviceType) || !requestDeviceType.equalsIgnoreCase(deviceType)) {
			deviceType = requestDeviceType;
			request.getSession().setAttribute(Constants.DEVICE_TYPE, deviceType);
		}
		// 读取用户主题,如果用户没有设置,则使用默认主题
		String themes = (String) request.getSession().getAttribute("themes");
		if (StringUtils.isBlank(themes)) {
			themes = "default";
			request.getSession().setAttribute("themes", themes);
		}
		staticPath = JFinal.me().getContextPath() + "/static/" + deviceType + "/" + themes;
		System.err.println("staticPath: " + staticPath);
		System.err.println("getUserAgent: " + UserAgentUtils.getUserAgent(request));
		System.err.println("deviceType: " + deviceType);
		System.err.println("getBrowser: " + UserAgentUtils.getBrowser(request));
		System.err.println("viewPath: " + inv.getViewPath());
		System.err.println("themes: " + themes);
		System.err.println("viewPath: " + inv.getViewPath());
		System.err.println("basePath: " + JFinal.me().getContextPath());
	}
}
