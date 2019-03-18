package com.wooricard.scheduler.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dkntech.d3f.common.Data;
import com.dkntech.utils.Utils;
import com.uracle.scheduler.repository.PushResult;
import com.uracle.scheduler.repository.ResultParams;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.wooricard.scheduler.batch.fetcher.BatchMsgFetcher;
import com.wooricard.scheduler.common.QueryParamsEx;
import com.wooricard.scheduler.common.WooriCardResult;
import com.wooricard.scheduler.entity.BatchData;
import com.wooricard.scheduler.entity.MessageData;
import com.wooricard.scheduler.entity.ResultData;
import com.wooricard.scheduler.single.fetcher.SingleMsgFetcher;

@Component
public class PushRepository implements SchedulerRepository {
	private static final Logger logger = LoggerFactory.getLogger(PushRepository.class);

	@Autowired
	private PushMapper mapper;
	
	private Calendar cal = Calendar.getInstance();
	private int serverTimeGap;
	
	@PostConstruct
	public void init() {
		Date dbDate = mapper.selectDbTime();
		Date myDate = new Date();
		
		long gap = dbDate.getTime() - myDate.getTime();
		serverTimeGap = (int) Math.floor(gap / 1000.0);
		logger.info(String.format("DB time = %s, server time  = %s, time gap = %d", dbDate.toString(), myDate.toString(), serverTimeGap));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Data> selectData(String fetcherId, String serverName, int maxItems, String includes) {
		List<MessageData> list = mapper.selectSingleMessageData(maxItems);
		
		List<Data> result = (List<Data>)(List<?>) list;
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Data> selectBatchData(String fetcherId, String serverName, int maxItems, String includes) {
		List<MessageData> list = mapper.selectBatchMessageData(maxItems);
		
		List<Data> result = (List<Data>)(List<?>) list;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int updateSelectedData(String fetcherId, String serverName, List<Data> data) {
		int result = 0;
		
		if(data != null && ! data.isEmpty()) {
			List<MessageData> list = (List<MessageData>)(List<?>) data;
			
			String legacyId = makeServerId(serverName, fetcherId);
			QueryParamsEx params = new QueryParamsEx(legacyId, 0, null);

			List<Long> keys = new ArrayList<Long>();
			for(MessageData msg : list) {
				keys.add(msg.getMsgNo());
				msg.setLegacyId(legacyId);
			}
			params.setList(keys);
			
			try {
				result = mapper.updateSingleMessageDataToProcess(params);
			}
			catch(Exception e) {
				logger.error("updateSelectedData : ", e);
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public int updateBatchSelectedData(String fetcherId, String serverName, List<Data> data) {
		int result = 0;
		
		if(data != null && ! data.isEmpty()) {
			List<MessageData> list = (List<MessageData>)(List<?>) data;
			
			String legacyId = makeServerId(serverName, fetcherId);
			QueryParamsEx params = new QueryParamsEx(legacyId, 0, null);

			List<Long> keys = new ArrayList<Long>();
			for(MessageData msg : list) {
				keys.add(msg.getMsgNo());
				msg.setLegacyId(legacyId);
			}
			params.setList(keys);
			
			try {
				result = mapper.updateBatchMessageDataToProcess(params);
			}
			catch(Exception e) {
				logger.error("updateBatchSelectedData : ", e);
			}
		}
		
		return result;
	}

	@Override
	public int updateDataRollback(String fetcherId, String serverName) {
		if(Utils.isEmpty(fetcherId) || Utils.isEmpty(serverName)) {
			logger.warn(String.format("updateDataRollback : fetcher = %s, server = %s", fetcherId, serverName));
			
			String host = System.getProperty("hostName");
			int result = 0;
			if(! Utils.isEmpty(serverName) && serverName.equals(host)) {
				result += mapper.updateSingleMessageDataRollback(makeServerId(serverName, SingleMsgFetcher.ID));
				result += mapper.updateBatchMessageDataRollback(makeServerId(serverName, BatchMsgFetcher.ID));
			}

			return result;
		}
		
		return mapper.updateSingleMessageDataRollback(makeServerId(serverName, fetcherId));
	}

	public int updateBatchDataRollback(String fetcherId, String serverName) {
		if(Utils.isEmpty(fetcherId) || Utils.isEmpty(serverName)) {
			logger.warn(String.format("updateBatchDataRollback : fetcher = %s, server = %s", fetcherId, serverName));
			return 0;
		}
			
		return mapper.updateBatchMessageDataRollback(makeServerId(serverName, fetcherId));
	}
	
	@Override
	public int updateReadResults(ResultParams params) {
		QueryParamsEx p = new QueryParamsEx(null, 0, applyServerGap(params.getDate()));
		p.setKeyword(WooriCardResult.SUC_MSG_RECEIVED.toString());
		p.setKeyword2(WooriCardResult.SUC_MSG_RECEIVED.getDesc());
		p.setNumber2(params.getMaxCheckHours());
		
		return mapper.updateSingleReadResult(p);
	}

	public int updateBatchReadResults(ResultParams params) {
		QueryParamsEx p = new QueryParamsEx(null, 0, applyServerGap(params.getDate()));
		p.setKeyword(WooriCardResult.SUC_MSG_RECEIVED.toString());
		p.setKeyword2(WooriCardResult.SUC_MSG_RECEIVED.getDesc());
		p.setNumber2(params.getMaxCheckHours());
		
		return mapper.updateBatchReadResult(p);
	}

	@Override
	public int updateFailResults(ResultParams params) {
		QueryParamsEx p = new QueryParamsEx(null, 0, applyServerGap(params.getDate()));
		p.setNumber2(params.getMaxCheckHours());
		
		int result = mapper.updateSingleFailedResult(p);
		
		return result;
	}

	public int updateBatchFailResults(ResultParams params) {
		QueryParamsEx p = new QueryParamsEx(null, 0, applyServerGap(params.getDate()));
		p.setNumber2(params.getMaxCheckHours());
		
		int result = mapper.updateBatchFailedResult(p);
		
		return result;
	}
	
	@Override
	public int updateReceivedResults(ResultParams params) {
		return 0;
	}
	
	@Override
	public int saveResult(PushResult pr) {
		ResultData result = new ResultData(pr);
		int r = 0;
		
		logger.debug(String.format("saveResult : code = %s, msg = %s", pr.getResultCode(), pr.getResultMsg()));
		
		try {
			r = mapper.insertSingleResult(result);
		}
		catch(Exception e) {
			logger.error(String.format("saveResult : insertSingleResult : [%s] %s, msgNo = %d", e.getClass().getSimpleName(), e.getMessage(), result.getMsgNo()));
		}
		
		if(r < 1) {
			logger.error(String.format("saveResult : msgNo = %s, r = %d", result.getMsgNo(), r));
		}
		
//		mapper.deleteSingleMessageData(result.getMsgNo());
		mapper.updateSingleMessageDataDone(result.getMsgNo());
		
		return r;
	}	
	
	public int saveBatchResult(PushResult pr) {
		ResultData result = new ResultData(pr);
		int r = 0;
		
		logger.debug(String.format("saveBatchResult : code = %s, msg = %s", pr.getResultCode(), result.getResultMsg()));
		
		try {
			r = mapper.insertBatchResult(result);
		}
		catch(Exception e) {
			logger.error(String.format("saveBatchResult : insertBatchResult : [%s] %s, msgNo = %d", e.getClass().getSimpleName(), e.getMessage(), result.getMsgNo()));
		}
		
		if(r < 1) {
			logger.error(String.format("saveBatchResult : msgNo = %s, r = %d", result.getMsgNo(), r));
		}
		
		BatchData data = new BatchData(result.getBatchId());
		mapper.updateBatchSentCount(data);
		mapper.deleteBatchMessageData(result.getMsgNo());
		
		return r;
	}	
	
	@Override
	public int insertMsgToInbox(PushResult pr) {
		return 0;
	}

	@Override
	public int selectBadgeCount(String appId, String cuid) {
		QueryParamsEx params = new QueryParamsEx(appId, 0, null);
		params.setKeyword2(cuid);
		
		return mapper.selectBadgeCount(params);
	}

//	public void deleteOldData(int days) {
//		int result = mapper.deleteFromIntf(days);
//		logger.info("deleteOldData : deleteFromIntf = " + result);
//
//		result = mapper.deleteFromPushMeta(days);
//		logger.info("deleteOldData : deleteFromPushMeta = " + result);
//
//		result = mapper.deleteFromPushMaster(days);
//		logger.info("deleteOldData : deleteFromPushMaster = " + result);
//
//		result = mapper.deleteFromFail(days);
//		logger.info("deleteOldData : deleteFromFail = " + result);
//	}
	
//	public void updateStatData() {
//		List<ResultData> list = mapper.selectPushMeta();
//		if(list == null || list.isEmpty()) {
//			return;
//		}
//		
//		String success = KBankResult.SUC_OK.toString();
//		String received = KBankResult.SUC_MSG_RECEIVED.toString();
//		
//		int sSucCount = 0, sFailCount = 0, bSucCount = 0, bFailCount = 0;
//		
//		for(ResultData d : list) {
//			String agentId = d.getSndAgtId();
//			if(Utils.isEmpty(agentId)) {
//				continue;
//			}
//			
//			if(agentId.equals(singleId)) {
//				if(success.equals(d.getResultCd()) || received.equals(d.getResultCd())) {
//					sSucCount++;
//				}
//				else {
//					sFailCount++;
//				}
//			}
//			else if(agentId.equals(batchId)) {
//				if(success.equals(d.getResultCd()) || received.equals(d.getResultCd())) {
//					bSucCount++;
//				}
//				else {
//					bFailCount++;
//				}				
//			}
//		}
//		
//		if(sSucCount > 0 || sFailCount > 0) {
//			logger.info(String.format("updateStatData : sSucCount = %d, sFailCount = %d", sSucCount, sFailCount));
//		}
//		
//		StatData data = new StatData(singleId, sSucCount, sFailCount);
//		mapper.spStatData(data);
//		
//		if(bSucCount > 0 || bFailCount > 0) {
//			logger.info(String.format("updateStatData : bSucCount = %d, bFailCount = %d", bSucCount, bFailCount));
//		}
//		
//		data = new StatData(batchId, bSucCount, bFailCount);
//		mapper.spStatData(data);
//
//		for(ResultData d : list) {
//			mapper.updatePushMeta(d.getResponseKey());
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	public List<Data> selectCbzData(String fetcherId, String serverName, int maxItems, String includes) {
//		QueryParams params = new QueryParams(fetcherId, maxItems, null);
//		params.setKeyword2(Constants.CATERORY_CBZ);
//		List<MessageData> list = mapper.selectSingleMessageData(params);
//		
//		List<Data> result = (List<Data>)(List<?>) list;
//		return result;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public List<Data> selectCbzBatchData(String fetcherId, String serverName, int maxItems, String includes) {
//		QueryParams params = new QueryParams(fetcherId, maxItems, null);
//		params.setKeyword2(Constants.CATERORY_CBZ);
//		List<MessageData> list = mapper.selectBatchMessageData(params);
//		
//		List<Data> result = (List<Data>)(List<?>) list;
//		return result;
//	}
//	
//	public MessageData selectAdditionalData(boolean single, String umsKey) {
//		QueryParams params = new QueryParams(umsKey, 0, null);
//		MessageData md = single ? mapper.selectSingleAdditionalData(params) : mapper.selectBatchAdditionalData(params);
//		return md;
//	}

	private String makeServerId(String serverName, String fetcherId) {
		return serverName + ":" + fetcherId;
	}
	
	private Date applyServerGap(Date date) {
		if(serverTimeGap < 0) {
			cal.setTime(date);
			cal.add(Calendar.SECOND, serverTimeGap);
			return cal.getTime();
		}
		
		return date;
	}

	public int completeBatch() {
		List<String> batchIds = mapper.selectCompletedBatch();
		if(batchIds == null || batchIds.isEmpty()) {
//			logger.info("completeBatch : no batch to complete.");
			return 0;
		}
		
//		int result = 0;
//		for(String id : batchIds) {
//			logger.info("completeBatch : batch_id = " + id);
//			result += mapper.updateBatchCompleted(id);
//		}
		
		for(String id : batchIds) {
			logger.info("completeBatch : batch_id = " + id);
		}
		
		int result = mapper.updateBatchCompleted();
		return result;
	}

	public int deleteCompletedMsgs() {
		return mapper.deleteCompletedMsgs();
	}

	public void updateContents(long seqno, String contents) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seqno", seqno);
		map.put("msg", contents);
		
		mapper.updatePushMasterMsg(map);
	}

	public int monitorProvider() {
		// TODO Auto-generated method stub
		return 0;
	}
}
