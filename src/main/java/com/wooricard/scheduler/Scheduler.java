package com.wooricard.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.uracle.scheduler.SchedulerDaemon;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.wooricard.scheduler.batch.fetcher.BatchMsgFetcher;
import com.wooricard.scheduler.batch.sender.BatchPushMsgSender;
import com.wooricard.scheduler.collector.BatchSentResultCollector;
import com.wooricard.scheduler.collector.SingleSentResultCollector;
import com.wooricard.scheduler.common.Constants;
import com.wooricard.scheduler.config.GeneralConfig;
import com.wooricard.scheduler.repository.PushRepository;
import com.wooricard.scheduler.single.fetcher.SingleMsgFetcher;
import com.wooricard.scheduler.single.sender.SinglePushMsgSender;

public class Scheduler extends SchedulerDaemon {
	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	
	public static void main(String[] args) {
		List<String> propList = new ArrayList<String>();
		propList.add(Constants.PROP_FILE_PREFIX_DATABASE);
		propList.add(Constants.PROP_FILE_PREFIX_DAEMON);
		
		Scheduler scheduler = (Scheduler) create(Scheduler.class, GeneralConfig.class, Constants.TITLE, propList);
		if(scheduler == null) {
			logger.error("main : Scheduler creation failed.");
			return;
		}
		
		if(scheduler.init()) {
			try {
				if(! scheduler.run()) {
					logger.error("main : Scheduler failed to run.");
					return;
				}
			}
			catch(Exception e) {
				logger.error("main : scheduler.run()", e);
			}			
		}
		else {
			logger.error("main : scheduler.init() failed.");
		}
		
		logger.info("main : Scheduler finished.");
	}

///////////////////////////////////////////////////////////////////////////////////////

	@Autowired
	private SingleMsgFetcher singleFetcher;
	
	@Autowired
	private SingleSentResultCollector singleCollector;
	
	@Autowired
	private BatchMsgFetcher batchFetcher;
	
	@Autowired
	private BatchSentResultCollector batchCollector;
	
	@Autowired
	private TaskExecutor executor;
	
	@Autowired
	private PushRepository repository;
	
	@Autowired
	private DataSourceTransactionManager tm;
	
	@Value("${fetcher.single.maxitems}")
	private int singleMaxItems;
	
	@Value("${fetcher.batch.maxitems}")
	private int batchMaxItems;
	
	@Value("${fetcher.single.interval}")
	private int singleInterval;
	
	@Value("${fetcher.batch.interval}")
	private int batchInterval;
	
	@Value("${fetcher.single.threads}")
	private int singleThreadCount;
	
	@Value("${fetcher.batch.threads}")
	private int batchThreadCount;
	
	@Value("${fetcher.single.q.max}")
	private int singleQueueMax;
	
	@Value("${fetcher.batch.q.max}")
	private int batchQueueMax;
	
	@Value("${dual.type}")
	private String dualServerType;
	
	@Value("${dual.id}")
	private String serverId;
	
	@Value("${dual.port}")
	private int aliveCheckPort;
	
	@Value("${dual.check.interval}")
	private int aliveCheckInterval;
	
	@Value("${dual.active.ip}")
	private String activeServerIp;
	
	@Value("${healthcheck.interval}")
	private int healthCheckInterval;
	
	@Value("${sender.upmc.id}")
	private String upmcId;

	@Value("${sender.upmc.url}")
	private String upmcUrl;

	@Value("${upmc.senderid.default}")
	private String senderId;

	@Value("${sender.image.url}")
	private String imageUrl;
	
	@Value("${commander.port}")
	private int commanderPort;

	@Value("${upmc.servicecode.default}")
	private String upmcServiceCode;

	@Value("${collector.single.interval}")
	private int singleCollectorInterval;

	@Value("${collector.batch.interval}")
	private int batchCollectorInterval;

	@Value("${collector.check.hours}")
	private int collectorCheckHours;

	@Override
	public SchedulerRepository getRepository() {
		return repository;
	}
	
	private boolean init() {
		int maxHttpConnection = singleThreadCount + batchThreadCount /*+ cbzSingleThreadCount + cbzBatchThreadCount*/;
		if(! initialize(executor, repository, tm, maxHttpConnection * 2, senderId, imageUrl, commanderPort)) {
			logger.error("init : initialize() failed.");
			return false;
		}
		
		setUpmcServer(upmcId, upmcUrl, upmcServiceCode, /*upmcDbIn*/Constants.UPMC_DBIN);
		
		
		checkMaxItemsRange();
		
		if(! addSingleFetcher()) {
			return false;
		}

		if(! addBatchFetcher()) {
			return false;
		}

//		if(! addSingleCollector()) {
//			return false;
//		}
//
//		if(! addBatchCollector()) {
//			return false;
//		}

		if(! initHealthChecker(healthCheckInterval)) {
			return false;
		}
		
		if(aliveCheckInterval < Constants.DUALCHECK_INTERVAL_MIN) {
			aliveCheckInterval = Constants.DUALCHECK_INTERVAL_MIN;
		}
		
		if(! initDualMode(dualServerType, aliveCheckPort, serverId, aliveCheckInterval, activeServerIp)) {
			return false;
		}
		
		return true;
	}
	
	private boolean addSingleFetcher() {
		try {
			if(! singleFetcher.init(this, repository, false, singleMaxItems, singleInterval, true)) {
				logger.error("addSingleFetcher : singleFetcher.init() failed.");
				return false;
			}
		}
		catch (Exception e) {
			logger.error("addSingleFetcher : ", e);
			return false;
		}
		
		SinglePushMsgSender sender = (SinglePushMsgSender) context.getBean(SinglePushMsgSender.class);
		if(sender == null) {
			logger.error("addSingleFetcher : sender == null");
			return false;
		}
		
		if(! singleFetcher.setSender(sender, singleThreadCount)) {
			logger.error("addSingleFetcher : singleFetcher.setSender() failed.");
			return false;
		}
		
		singleFetcher.setAppId("From DB later.");
		singleFetcher.setQueueMax(singleQueueMax);
		singleFetcher.activeAlways(true);
		
		if(! addFetcher(singleFetcher)) {
			logger.error("addSingleFetcher : addFetcher(singleFetcher) failed.");
			return false;
		}
		
		return true;
	}
	
	private boolean addBatchFetcher() {
		try {
			if(! batchFetcher.init(this, repository, false, batchMaxItems, batchInterval, true)) {
				logger.error("addBatchFetcher : batchFetcher.init() failed.");
				return false;
			}
		}
		catch (Exception e) {
			logger.error("addBatchFetcher : ", e);
			return false;
		}
		
		BatchPushMsgSender sender = (BatchPushMsgSender) context.getBean(BatchPushMsgSender.class);
		if(sender == null) {
			logger.error("addBatchFetcher : sender == null");
			return false;
		}
		
		if(! batchFetcher.setSender(sender, batchThreadCount)) {
			logger.error("addSingleFetcher : batchFetcher.setSender() failed.");
			return false;
		}
		
		batchFetcher.setAppId("From DB later.");
		batchFetcher.setQueueMax(batchQueueMax);
		batchFetcher.activeAlways(true);
		
		if(! addFetcher(batchFetcher)) {
			logger.error("addBatchFetcher : addFetcher(batchFetcher) failed.");
			return false;
		}
		
		return true;
	}

	private boolean addSingleCollector() {
		if(singleCollectorInterval < Constants.COLLECTOR_INTERVAL_MIN) {
			singleCollectorInterval = Constants.COLLECTOR_INTERVAL_MIN;
		}
		
		if(! singleCollector.init(this, repository, singleCollectorInterval, collectorCheckHours)) {
			logger.error("addSingleCollector : singleCollector.init() failed.");
			return false;
		}
		
		if(! addCollector(singleCollector)) {
			logger.error("addSingleCollector : addCollector(singleCollector) failed.");
			return false;
		}
		
		return true;
	}
	
	private boolean addBatchCollector() {
		if(batchCollectorInterval < Constants.COLLECTOR_INTERVAL_MIN) {
			batchCollectorInterval = Constants.COLLECTOR_INTERVAL_MIN;
		}
		
		if(! batchCollector.init(this, repository, batchCollectorInterval, collectorCheckHours)) {
			logger.error("addBatchCollector : batchCollector.init() failed.");
			return false;
		}
		
		if(! addCollector(batchCollector)) {
			logger.error("addBatchCollector : addCollector(batchCollector) failed.");
			return false;
		}
		
		return true;
	}
	
	private void checkMaxItemsRange() {
		singleMaxItems = Math.max(Constants.FETCHER_MAXITEMS_MIN, Math.min(Constants.FETCHER_MAXITEMS_MAX, singleMaxItems));
		batchMaxItems = Math.max(Constants.FETCHER_MAXITEMS_MIN, Math.min(Constants.FETCHER_MAXITEMS_MAX, batchMaxItems));
	}
}