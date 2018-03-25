package com.jz.web.common.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.boilit.bsl.IEngine;
import org.boilit.bsl.ITemplate;

import com.jfinal.core.BslPlugin;
import com.jfinal.core.JFinal;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

/**
 * 采用Bsl模版引擎的二进制html内容输出 http://boilit.github.io/bsl/zh/index.html
 * 
 * @author lucio
 *
 */
public class BslRender extends Render {
	private static final String CONTENT_TYPE = "text/html; charset=" + getEncoding();
	/** 默认视图访问路径 */
	public static String WEB_BASE = "";
	private static Log log = Log.getLog(BslRender.class);
	public static Map<String, String> viewPathCache = new HashMap<String, String>();

	public BslRender(String view) {
		this.view = view;
	}

	@Override
	public void render() {
		Map<String, Object> data = new HashMap<String, Object>();
		for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements();) {
			String attrName = attrs.nextElement();
			data.put(attrName, request.getAttribute(attrName));
		}
		OutputStream responseOut = null;
		ByteArrayOutputStream byteOutput = null;
		try {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType(CONTENT_TYPE);
			response.setCharacterEncoding(getEncoding()); // 与 contentType 分开设置
			IEngine engine = BslPlugin.getEngine();
			// if (null == engine) {
			// engine = Engine.getEngine();
			// }
			// engine.setSpecifiedEncoder(true);
			// 判断设备是移动端还是PC
			String deviceType = (String) request.getSession().getAttribute("deviceType");
			// 读取用户主题,如果用户没有设置,则使用默认主题
			String themes = (String) request.getSession().getAttribute("themes");

			String realView = JFinal.me().getServletContext()
					.getRealPath(WEB_BASE + deviceType + "/" + themes + "/" + view);
			if (log.isDebugEnabled()) {
				log.debug("realView: " + realView);
			}

			ITemplate template = null;
			String templatePath = realView;
			// 是否使用缓存,获取对应模版文件路径
			if (engine.isUseTemplateCache()) {
				templatePath = null == viewPathCache.get(realView) ? realView : viewPathCache.get(realView);
			}
			try {
				template = engine.getTemplate(templatePath);
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("Try to use default Template, No Template file at : " + templatePath);
				}
				// 如果文件加载视图加载不了,则使用默认样式
				templatePath = JFinal.me().getServletContext().getRealPath(WEB_BASE + deviceType + "/default/" + view);
				template = engine.getTemplate(templatePath);
			}
			if (engine.isUseTemplateCache()) {
				viewPathCache.put(realView, templatePath);
			}
			if (log.isInfoEnabled()) {
				log.info("Use Template file is: " + templatePath);
			}
			byteOutput = new ByteArrayOutputStream(8192);
			template.execute(data, byteOutput);
			responseOut = response.getOutputStream();
			responseOut.write(byteOutput.toByteArray());
			responseOut.flush();
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			try {
				if (responseOut != null) {
					responseOut.close();
				}
				if (byteOutput != null) {
					byteOutput.close();
				}
			} catch (IOException e) {
				new RenderException(e);
			}
		}
	}
}
