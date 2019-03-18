package com.wooricard.scheduler.base;

import java.util.Map;

import org.slf4j.Logger;

import com.dkntech.d3f.Activator;
import com.dkntech.d3f.repository.Repository;
import com.uracle.scheduler.collector.ResultCollector;
import com.uracle.scheduler.repository.ResultParams;
import com.uracle.scheduler.repository.SchedulerRepository;

public abstract class ResultCollectorBase extends ResultCollector {
	public static final String ATTR_FAILED		= "failed";
	public static final String ATTR_RECEIVED		= "received";

	protected Logger logger;
	
	protected long failed;
	protected long received;
	
	public ResultCollectorBase(String id, Logger logger) {
		super(id);
		
		this.logger = logger;
	}

	@Override
	public boolean init(Activator activator, Repository repository, int interval, int checkHours) {
		setLogger(logger);
		
		return super.init(activator, repository, interval, checkHours);
	}

//	@Override
//	protected int updateCollectingData(Repository repository, String collectorId, int checkHours, Date lastTime) {
//		int result = 0;
//		
//		try {
//			result = updateCollectingData(repository, collectorId, checkHours, lastTime);
//		}
//		catch(Exception e) {
//			logger.error("updateCollectingData", e);
//		}
//
//		return result;
//	}

//	@Override
//	public int updateReadResults(SchedulerRepository repository, ResultParams params) {
//		return 0;
//	}
	
	@Override
	public Map<String, Object> getStatusData() {
		Map<String, Object> map = super.getStatusData();
		map.put(ATTR_FAILED, String.valueOf(failed));
		map.put(ATTR_RECEIVED, String.valueOf(received));

		return map;
	}

//	@Override
//	public int updateFailResults(SchedulerRepository repository, ResultParams params) {
//		return 0;
//	}

	@Override
	public int updateReceivedResults(SchedulerRepository repository, ResultParams params) {
		return 0;
	}
	
	public void initStatData() {
		long data = initCollected();
		logger.info(String.format("initStatData : collected = %d, received = %d, failed = %d",  data, received, failed));
		
		failed = received = 0;
	}
}
