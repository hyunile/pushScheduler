package com.wooricard.scheduler.batch.fetcher;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.repository.Repository;
import com.dkntech.utils.Utils;
import com.wooricard.scheduler.base.MsgFetcherBase;
import com.wooricard.scheduler.repository.PushMapper;
import com.wooricard.scheduler.repository.PushRepository;

@Component
public class BatchMsgFetcher extends MsgFetcherBase {
	public static final String ID = "B";
	
	@Autowired
	private PushMapper mapper;
	
	public BatchMsgFetcher() {
		super(ID, LoggerFactory.getLogger(BatchMsgFetcher.class));
	}

	@Override
	public List<Data> selectPushData(Repository repository, String serverName, int maxItems, String includes) {
		if(! isSendAllowed()) {
			return null;
		}
		
		PushRepository r = (PushRepository) repository;
		return r.selectBatchData(ID, serverName, maxItems, includes);
	}

	@Override
	public int updateSelectedPushData(Repository repository, String fetcherId, String serverName, List<Data> list) {
		PushRepository r = (PushRepository) repository;
		return r.updateBatchSelectedData(fetcherId, serverName, list);
	}

	@Override
	public int updatePushDataRollback(Repository repository, String fetcherId, String serverName) {
		PushRepository r = (PushRepository) repository;
		return r.updateBatchDataRollback(fetcherId, serverName);
	}
	
	public boolean isSendAllowed() {
		boolean result = true;
		Map<String, String> map = mapper.selectAllowedTime();
		String useAllow = map.get("ALLOW");
		
		if(! "Y".equalsIgnoreCase(useAllow)) {
			return result;
		}
		
		String begin = map.get("BEGINTIME");
		String end = map.get("ENDTIME");
		String now = Utils.dateToStr(new Date(), "HHmm");

		if(now.compareTo(begin) < 0 || now.compareTo(end) >= 0) {
			result = false;
			logger.info("isSendAllowed : result = false");
		}

		return result;
	}
}
