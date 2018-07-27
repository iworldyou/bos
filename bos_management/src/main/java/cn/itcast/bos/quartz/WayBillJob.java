package cn.itcast.bos.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.service.base.IWayBillService;

public class WayBillJob implements Job{
	
	@Autowired
	private IWayBillService wayBillService;

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		wayBillService.getNew();
		System.out.println("100秒--同步数据库中的运单信息到索引库");
		
	}

}
