package dbm.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import dbm.impl.BackUp;


public class planListener implements ServletContextListener{
	public void contextDestroyed(ServletContextEvent event) {
		// event.getServletContext().log("定时器销毁");
	}

	public void contextInitialized(ServletContextEvent event)   
    {   
		if(BackUp.startAllPlan()){
			System.out.println("initBackUpPlan initialized success.");
		}else{
			System.out.println("initBackUpPlan initialized defeate.");
		}
    }   
}
