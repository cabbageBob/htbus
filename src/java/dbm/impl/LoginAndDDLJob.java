package dbm.impl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LoginAndDDLJob implements Job {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			CheckAll.getTodayLoginAndDDL();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws JobExecutionException {
		new LoginAndDDLJob().execute(null);
	}
}
