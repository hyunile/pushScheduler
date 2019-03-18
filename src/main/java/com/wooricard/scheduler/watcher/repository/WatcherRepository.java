package com.wooricard.scheduler.watcher.repository;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wooricard.scheduler.common.QueryParamsEx;
import com.wooricard.scheduler.entity.ResultData;

@Component
public class WatcherRepository {
	private static final Logger logger = LoggerFactory.getLogger(WatcherRepository.class);

	@Autowired
	private WatcherMapper watcherMapper;
	
	public int saveServerError(ResultData data) {
		return watcherMapper.insertServerError(data);
	}

	public int getSavedServerError(String server, String resultCode, int interval) {
		QueryParamsEx params = new QueryParamsEx(server, 0, null);
		params.setKeyword2(resultCode);
		params.setNumber2(interval);
		
		int result = watcherMapper.selectServerError(params);
		return result;
	}

	public Map<String, String> selectFailMsg(long seqno) {
		return watcherMapper.selectFailMsg(seqno);
	}

	public List<Map<String, String>> selectSmsReceivers() {
		return watcherMapper.selectSmsReceivers();
	}
}
