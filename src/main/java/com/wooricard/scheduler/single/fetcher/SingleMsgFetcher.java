package com.wooricard.scheduler.single.fetcher;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Component;

import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.repository.Repository;
import com.dkntech.utils.Utils;
import com.wooricard.scheduler.base.MsgFetcherBase;

@Component
public class SingleMsgFetcher extends MsgFetcherBase {
	public static final String ID = "S";
	
	public SingleMsgFetcher() {
		super(ID, LoggerFactory.getLogger(SingleMsgFetcher.class));
	}

	@Override
	public List<Data> selectPushData(Repository repository, String serverName, int maxItems, String includes) {
		List<Data> result = null;
		try {
			result = repository.selectData(ID, serverName, maxItems, includes);
		}
		catch(CannotAcquireLockException e) {
			logger.info("selectData : table is busy.");
		}
		catch(Exception e) {
			String msg = e.getMessage();
			if(! Utils.isEmpty(msg) && msg.contains("NOWAIT")) {
				logger.info("selectData : table is busy.");
			}
			else {
				logger.error("selectPushData : ", e);
			}
		}

		return result;
	}

	@Override
	public int updateSelectedPushData(Repository repository, String fetcherId, String serverName, List<Data> list) {
		return repository.updateSelectedData(fetcherId, serverName, list);
	}

	@Override
	public int updatePushDataRollback(Repository repository, String fetcherId, String serverName) {
		int result = 0;
		try {
			result = repository.updateDataRollback(fetcherId, serverName);
		}
		catch(Exception e) {
			logger.error("updatePushDataRollback", e);
		}

		return result;
	}
}
