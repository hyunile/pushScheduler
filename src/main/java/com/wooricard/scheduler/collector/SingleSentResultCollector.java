package com.wooricard.scheduler.collector;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uracle.scheduler.repository.ResultParams;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.wooricard.scheduler.base.ResultCollectorBase;
import com.wooricard.scheduler.repository.PushRepository;

@Component
public class SingleSentResultCollector extends ResultCollectorBase {
	private static final String ID = "S";
	
	public SingleSentResultCollector() {
		super(ID, LoggerFactory.getLogger(SingleSentResultCollector.class));
	}

	@Override
	public int updateFailResults(SchedulerRepository repository, ResultParams params) {
		PushRepository r = (PushRepository) repository;
		int result = r.updateFailResults(params);
		
		if(result > 0) {
			failed += result;
			
			logger.info("updateFailResults : result = " + result);
		}

		return result;
	}

	@Override
	public int updateReadResults(SchedulerRepository repository, ResultParams params) {
		PushRepository r = (PushRepository) repository;
		int result = r.updateReadResults(params);
		
		if(result > 0) {
			received += result;
			
			logger.info("updateReadResults : result = " + result);
		}

		return result;
	}
}
