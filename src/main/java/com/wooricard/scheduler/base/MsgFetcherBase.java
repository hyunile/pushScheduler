package com.wooricard.scheduler.base;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.dao.CannotAcquireLockException;

import com.dkntech.d3f.Activator;
import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.repository.Repository;
import com.dkntech.utils.Utils;
import com.uracle.scheduler.fetcher.MsgFetcher;

public abstract class MsgFetcherBase extends MsgFetcher {
	protected Logger logger;
	
	public MsgFetcherBase(String id, Logger logger) {
		super(id);
		this.logger = logger;
	}
	
	@Override
	public boolean init(Activator activator, Repository repository, boolean useHighQueue, int maxItems, int interval, boolean needTps) {
		
		setLogger(logger);
		
		return super.init(activator, repository, useHighQueue, maxItems, interval, needTps);
	}
	
	@Override
	protected List<Data> selectData(Repository repository, String fetcherId, String serverName, int maxItems, String includes) {
		List<Data> list = null;
		try {
			list = selectPushData(repository, serverName, maxItems, includes);
		}
		catch(CannotAcquireLockException e) {
			logger.info(getName() + " : selectData : table is busy");
		}
		catch(Exception e) {
			String msg = e.getMessage();
			if(! Utils.isEmpty(msg) && msg.contains("NOWAIT")) {
				logger.info(getName() + " : selectData : table is busy");
			}
			else {
				logger.error(getName() + " : selectData : selectData DB query failed.", e);
			}
		}
		
		if(list != null && ! list.isEmpty()) {
			logger.info(String.format("%s : selectData : size = %d", getName(), list.size()));
		}
		
		return list;
	}

	@Override
	protected void updateSelectedData(Repository repository, String fetcherId, String serverName, List<Data> list) {
		int result = updateSelectedPushData(repository, fetcherId, serverName, list);
		logger.info(String.format("%s : updateSelectedData : result = %d", getName(), result));
	}

	@Override
	protected void updateDataRollback(Repository repository, String fetcherId, String serverName) throws Exception {
		int result = updatePushDataRollback(repository, fetcherId, serverName);
		logger.info(String.format("%s : updateDataRollback : result = %d", getName(), result));
	}

	@Override
	protected List<String> getHighPriorityList() {
		return null;
	}
	
	abstract public List<Data> selectPushData(Repository repository, String serverName, int maxItems, String includes);
	abstract public int updateSelectedPushData(Repository repository, String fetcherId, String serverName, List<Data> list);
	abstract public int updatePushDataRollback(Repository repository, String fetcherId, String serverName);
}
