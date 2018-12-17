package cn.miao.framework.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import cn.miao.framework.factory.ServiceFactory;
import cn.miao.framework.helper.DatabaseHepler;
import cn.miao.framework.helper.DispatchHelper;
import cn.miao.framework.helper.UpdownloadHelper;
import cn.miao.framework.util.Cache;

public class ContextListener implements ServletContextListener {

	Logger logger = Logger.getLogger(this.getClass());

	public void contextDestroyed(ServletContextEvent event) {
		// event.getServletContext().log("定时器销毁");
	}

	public void contextInitialized(final ServletContextEvent event) {
//		new LicenseChecker().check(event.getServletContext()
//				.getServletContextName(), event.getServletContext().getContextPath());
//		System.out.println(event.getServletContext()
//				.getServletContextName() + ";" + event.getServletContext().getContextPath());
		logger.info("基础平台初始化...");
		String webRoot = event.getServletContext().getRealPath("/");
		logger.info("WebApp Root:" + webRoot);
		logger.info("初始化配置文件...");

		UpdownloadHelper.init();
		Cache.dispatchers = DispatchHelper.readConfig();
		Cache.dataSources = DatabaseHepler.initDataSource();
		Cache.services = ServiceFactory.initServices();
		// event.getServletContext().log("");
		// 定时器启动
		logger.info("基础平台初始化结束");
	}

	/**
	 * Constructor of the object.
	 */
	public ContextListener() {
		super();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
	}

}
