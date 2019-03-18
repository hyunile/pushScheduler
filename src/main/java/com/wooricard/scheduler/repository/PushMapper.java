package com.wooricard.scheduler.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.wooricard.scheduler.common.QueryParamsEx;
import com.wooricard.scheduler.entity.BatchData;
import com.wooricard.scheduler.entity.MessageData;
import com.wooricard.scheduler.entity.ResultData;

@Mapper
public interface PushMapper {
	///////////////////////////////////////////////////////////////////////
	// single
	
	public List<MessageData> selectSingleMessageData(int count);
	public int updateSingleMessageDataToProcess(QueryParamsEx params);
	public int updateSingleMessageDataRollback(String legacyId);
	
	public int insertSingleResult(ResultData result);
	public int deleteSingleMessageData(long msgNo);
	public int updateSingleMessageDataDone(long msgNo);
	
	public int updateSingleReadResult(QueryParamsEx params);
	public int updateSingleFailedResult(QueryParamsEx params);

	public int deleteCompletedMsgs();
	
	///////////////////////////////////////////////////////////////////////
	// batch
	
	public List<MessageData> selectBatchMessageData(int count);
	public int updateBatchMessageDataToProcess(QueryParamsEx params);
	public int updateBatchMessageDataRollback(String legacyId);
	
	public int insertBatchResult(ResultData result);
	public int deleteBatchMessageData(long msgNo);
	public void updateBatchSentCount(BatchData data);
	
	public int updateBatchReadResult(QueryParamsEx params);
	public int updateBatchFailedResult(QueryParamsEx params);
	
	public List<String> selectCompletedBatch();
//	public int updateBatchCompleted(String id);
	public int updateBatchCompleted();
	
	public void updatePushMasterMsg(Map<String, Object> map);
	
	///////////////////////////////////////////////////////////////////////
	// common
		
	public int selectBadgeCount(QueryParamsEx params);
	public Date selectDbTime();
	Map<String, String> selectAllowedTime();
}
