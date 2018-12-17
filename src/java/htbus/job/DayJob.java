package htbus.job;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import htbus.cache.DBStatusCache;
import htbus.service.DataService;
import htbus.service.Service;

public class DayJob implements Job{
	Logger logger = Logger.getLogger(DayJob.class);
	DataService dataService = new DataService();
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			 logger.info("开始执行每日任务");
			dataService.insertTotalData();
			 logger.info("每日任务执行完毕");
		} catch (Exception e) {
			logger.error(e);;
		}
	}

}
