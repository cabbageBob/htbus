package dbm.impl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class cpuMemoryJob implements Job {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			CheckAll.getCpuMemory();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws JobExecutionException {
		new cpuMemoryJob().execute(null);
	}
}
