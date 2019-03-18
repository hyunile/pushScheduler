package com.wooricard.scheduler.watcher.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.wooricard.scheduler.common.QueryParamsEx;
import com.wooricard.scheduler.entity.ResultData;

@Mapper
public interface WatcherMapper {
	int insertServerError(ResultData data);
	int selectServerError(QueryParamsEx params);
	List<Map<String, String>> selectSmsReceivers();
	Map<String, String> selectFailMsg(long seqno);
}
