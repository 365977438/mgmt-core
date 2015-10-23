package com.yoju360.mgmt.core.util;


import javax.servlet.ServletContextEvent;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 方便获取spring {@link ApplicationContext}的类，它暴露一个public static的spring context变量。
 * 
 * <p>需要适当初始化才能获得spring context: 
 * <ul>
 * 	<li>在web应用中：在web.xml中注册本类作为一个listener</li>
 * 	<li>在独立应用中：在spring的bean定义中增加本类作为一个bean</li>
 * </ul>
 * </p>
 * 
 * @author Evan Wu
 *
 */
public class SpringContextUtils implements javax.servlet.ServletContextListener, org.springframework.context.ApplicationContextAware{
	/**
	 * <b>{@link SpringContextUtils}必须初始化，否者将不能获取spring的context。</b>
	 */
	private static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		context = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		context = null;
	}

	/**
	 * <b>{@link SpringContextUtils}必须初始化，否者将不能获取spring的context。</b>
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext springContext) {
		SpringContextUtils.context = springContext;
	}
}
