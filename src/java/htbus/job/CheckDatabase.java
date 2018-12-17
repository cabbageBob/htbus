package htbus.job;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;

import htbus.cache.DBStatusCache;
import htbus.service.DataService;
import htbus.util.RedisUtil;


public class CheckDatabase implements Job{

	Logger logger = Logger.getLogger(CheckDatabase.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("检测各数据库连接状态");
		//DBStatusCache.setListCache( "dbstatus", DataService.getInstanceStatus());
		RedisUtil.set("instanceStatus", JSON.toJSONString( DataService.getInstanceStatus()));
		//System.out.println(RedisUtil.get("instanceStatus"));
		logger.info("检测数据库连接状态完成");

		
	}

}
