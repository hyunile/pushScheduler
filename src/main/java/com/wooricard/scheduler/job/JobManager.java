package com.wooricard.scheduler.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dkntech.d3f.job.ScheduledJobManager;
import com.wooricard.scheduler.Scheduler;
import com.wooricard.scheduler.batch.fetcher.BatchMsgFetcher;
import com.wooricard.scheduler.collector.BatchSentResultCollector;
import com.wooricard.scheduler.collector.SingleSentResultCollector;
import com.wooricard.scheduler.repository.PushRepository;
import com.wooricard.scheduler.single.fetcher.SingleMsgFetcher;
import com.wooricard.scheduler.watcher.service.impl.WatcherServiceImpl;

@Component
public class JobManager extends ScheduledJobManager {
	private static final Logger logger = LoggerFactory.getLogger(JobManager.class);
	
	@Autowired
	private SingleMsgFetcher singleFetcher;
	
	@Autowired
	private SingleSentResultCollector singleCollector;
	
	@Autowired
	private BatchMsgFetcher batchFetcher;
	
	@Autowired
	private BatchSentResultCollector batchCollector;
	
	@Autowired
	private PushRepository repository;
	
	@Autowired
	private WatcherServiceImpl watcherService;
	
	@Autowired
	private Scheduler scheduler;
	
	@Value("${batch.complete.cronexpr}")
	private String completeBatchTime;

	@Value("${completed.delete.cronexpr}")
	private String deleteCompletedMsgsTime;

	@Value("${monitor.provider.cronexpr}")
	private String watchProviderTime;
	
	@Value("${monitor.upmc.cronexpr}")
	private String watchUpmcTime;
	
	private int completeBatchCount;
	
	@Override
	public void initialize() {
		completeBatchCount = 0;
		
		Runnable watchUpmc = new Runnable() {
			@Override
			public void run() {
				if(scheduler.isActive()) {
					logger.info(String.format("watchUpmc : time = %s", watchUpmcTime));
					int result = watcherService.watchUpmc();
					logger.info("watchUpmc : result = " + result);
				}
				else {
					logger.info("watchUpmc : inactive");
				}
			}
		};
		
		addJobWithCronExpr(watchUpmc, watchUpmcTime);
		
		Runnable watchProvider = new Runnable() {
			@Override
			public void run() {
				if(scheduler.isActive()) {
					logger.info(String.format("watchProvider : time = %s", watchProviderTime));
					int result = watcherService.watchProvider();
					logger.info("watchProvider : result = " + result);
				}
				else {
					logger.info("watchProvider : inactive");
				}
			}
		};
		
		addJobWithCronExpr(watchProvider, watchProviderTime);
		
		Runnable deleteCompletedMsgs = new Runnable() {
			@Override
			public void run() {
				if(scheduler.isActive()) {
					logger.info(String.format("deleteCompletedMsgs : time = %s", deleteCompletedMsgsTime));
					int result = repository.deleteCompletedMsgs();
					logger.info("deleteCompletedMsgs : result = " + result);
				}
				else {
					logger.info("deleteCompletedMsgs : inactive");
				}
			}
		};

		addJobWithCronExpr(deleteCompletedMsgs, deleteCompletedMsgsTime);

		logger.info(String.format("initialize : deleteCompletedMsgs added. time = %s", deleteCompletedMsgsTime));

		Runnable completeBatch = new Runnable() {
			@Override
			public void run() {
				if(scheduler.isActive()) {
					completeBatchCount++;
					if(completeBatchCount % 5 == 0) {
						logger.info(String.format("completeBatch : time = %s", completeBatchTime));
					}
					
					int result = repository.completeBatch();
					if(result > 0) {
						logger.info("completeBatch : result = " + result);
					}
				}
				else {
//					logger.info("completeBatch : inactive");
				}
			}
		};

		addJobWithCronExpr(completeBatch, completeBatchTime);

		logger.info(String.format("initialize : completeBatch added. time = %s", completeBatchTime));

		Runnable initStatData = new Runnable() {
			@Override
			public void run() {
				logger.info("initStatData : start.");

				singleFetcher.initStatData();
				batchFetcher.initStatData();

				singleCollector.initStatData();
				batchCollector.initStatData();

				logger.info("initStatData : finished.");
			}
		};

		addJobWithCronExpr(initStatData, "0 0 0 * * ?");

		logger.info("initialize : initStatData added");
	}
}
