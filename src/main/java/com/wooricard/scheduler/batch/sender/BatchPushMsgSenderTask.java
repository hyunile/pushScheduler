package com.wooricard.scheduler.batch.sender;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.LoggerFactory;

import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.fetcher.DataQueue;
import com.dkntech.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.uracle.scheduler.repository.Message;
import com.uracle.scheduler.repository.PushResult;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.uracle.scheduler.sender.Sender.ServerInfo;
import com.wooricard.scheduler.base.PushMsgSenderTaskBase;
import com.wooricard.scheduler.common.Constants;
import com.wooricard.scheduler.repository.PushRepository;
import com.wooricard.scheduler.sender.data.PushMessage;

public class BatchPushMsgSenderTask extends PushMsgSenderTaskBase {

	public BatchPushMsgSenderTask(String senderId, DataQueue<Data> queue, DataQueue<Data> highQueue, ServerInfo server) {
		super(senderId, queue, highQueue, server, LoggerFactory.getLogger(BatchPushMsgSenderTask.class));
	}

	@Override
	public int savePushResult(SchedulerRepository repository, PushResult pr) {
		PushRepository r = (PushRepository) repository;
		return r.saveBatchResult(pr);
	}
	
	@Override
	protected String makeExt(Message msg) {
		PushMessage pmsg = (PushMessage) msg;
		
		if(! Utils.isEmpty(pmsg.getExt()))
			return null;
		
		String ext = null;
		String transCode = pmsg.getTransCode();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(Constants.TRANS_CODE_WPAY.equals(transCode)) {
			map.put("TYPE", pmsg.getPushType());
			map.put("DNIS", pmsg.getDnis());
			map.put("TITLE", Utils.safeStr(pmsg.getTitle()));
			map.put("TEXT", pmsg.getMessage());
			map.put("CMPID", Utils.safeStr(pmsg.getCmpgnId()));
			map.put("PARAM1", Utils.safeStr(pmsg.getLinkInfo()));
			map.put("PARAM2", "");		
			map.put("ID", Utils.safeStr(pmsg.getEventNo()));
			map.put("IMAGE1", Utils.safeStr(pmsg.getImage()));
			map.put("IMAGE2", Utils.safeStr(pmsg.getImgUrl2()));			
			map.put("IMAGE2", Utils.safeStr(pmsg.getImgUrl2()));			
		}
		else if(Constants.TRANS_CODE_SMART.equals(transCode)) {
			map.put("TYPE", pmsg.getPushType());
			map.put("DNIS", pmsg.getDnis());
			map.put("TITLE", Utils.safeStr(pmsg.getTitle()));
			map.put("TEXT", pmsg.getMessage());
			map.put("CMPID", "");
			map.put("ID", Utils.safeStr(pmsg.getEventNo()));
			if("0N".equals(pmsg.getPushType())) {
				map.put("PARAM1", makeParam1_0N(pmsg));
			}
			else {
				map.put("PARAM1", "");
			}
			map.put("PARAM2", "");
			
			String url = getImageServerUrl();
			
			map.put("IMAGE2", makeImageUrlStr(url, pmsg.getImgUrl2(), false));			
			map.put("IMAGE3", makeImageUrlStr(url, pmsg.getImgUrl3(), false));			
			map.put("URL_TYPE1", Utils.safeStr(pmsg.getType1()));
			map.put("URL_TYPE2", Utils.safeStr(pmsg.getType2()));			
			map.put("URL1", Utils.safeStr(pmsg.getLinkUrl1()));			
			map.put("URL2", Utils.safeStr(pmsg.getLinkUrl2()));			
//			map.put("URL_TYPE1", "WF");
//			map.put("URL1", Constants.DEFAULT_URL);			
			
			map.put("SENDDATE", Utils.dateToStr(new Date(), Utils.FRMT_DATETIME));			
		}
		else {
			map.put("TYPE", pmsg.getPushType());
			map.put("DNIS", pmsg.getDnis());
			map.put("TITLE", Utils.safeStr(pmsg.getTitle()));
			map.put("TEXT", pmsg.getMessage());
			map.put("CMPID", Utils.safeStr(pmsg.getCmpgnId()));
			map.put("ID", Utils.safeStr(pmsg.getEventNo()));
			map.put("PARAM2", "");
			
			String url = getImageServerUrl();
			
			map.put("IMAGE1", Utils.safeStr(pmsg.getImage()));
			map.put("IMAGE2", makeImageUrlStr(url, pmsg.getImgUrl2(), false));			
			map.put("IMAGE3", makeImageUrlStr(url, pmsg.getImgUrl3(), false));			
			map.put("URL_TYPE1", Utils.safeStr(pmsg.getType1()));
			map.put("URL_TYPE2", Utils.safeStr(pmsg.getType2()));			
			map.put("URL1", Utils.safeStr(pmsg.getLinkUrl1()));			
			map.put("URL2", Utils.safeStr(pmsg.getLinkUrl2()));		
//			map.put("URL_TYPE1", "WF");
//			map.put("URL1", Constants.DEFAULT_URL);			
			
			HashMap<String, String> param1 = null;
			if(Constants.TRANS_CODE_D20.equals(transCode)) {
				param1 = makeParam1_0I(pmsg);
			}
			else if(Constants.TRANS_CODE_D30.equals(transCode)) {
				param1 = makeParam1_0I(pmsg);
			}
			else if(Constants.TRANS_CODE_D40.equals(transCode)) {
				param1 = makeParam1_0G(pmsg);
			}
			else if(Constants.TRANS_CODE_D50.equals(transCode)) {
				param1 = makeParam1_11_Loan(pmsg);
			}
			else if(Constants.TRANS_CODE_B20.equals(transCode)) {
				param1 = makeParam1_0I(pmsg);
			}
			else if(Constants.TRANS_CODE_CMS.equals(transCode)) {
				map.put("TEXT", pmsg.getContents());
				map.put("CMPID", Utils.safeStr(pmsg.getKey2()));
				
				param1 = makeParam1_11_Cms(pmsg);
				String img = pmsg.getImage();
				img = removeUrl(img, url);
				
				map.put("IMAGE1", Utils.safeStr(img));
				map.put("IMAGE2", "");
				map.put("IMAGE3", Utils.safeStr(pmsg.getImgUrl2()));
			}
			
			map.put("PARAM1", param1 != null && ! param1.isEmpty() ? param1 : "");
			map.put("SENDDATE", Utils.dateToStr(new Date(), Utils.FRMT_DATETIME));			
		}

		try {
			ext = mapper.writeValueAsString(map);
		} 
		catch (JsonProcessingException e) {
			logger.error("makeExt : ", e);
		}			

		return ext;
	}

	@Override
	public Message convertDataToMsg(Data data) {
		Message msg = super.convertDataToMsg(data);
		
		PushMessage pmsg = (PushMessage) msg;
		pmsg.setTitle(pmsg.getBatchTitle());
		
		try {
			if(Constants.TRANS_CODE_D40.equals(pmsg.getTransCode())) {
				makeApvMessage(pmsg);
			}
			else if("Y".equals(pmsg.getUseTemplete())) {
				makeTempleteMessage(pmsg);				
			}
		}
		catch(Exception e) {
			logger.error("convertDataToMsg : ", e);
		}

		if(Constants.TRANS_CODE_CMS.equals(pmsg.getTransCode())) {
			String str = pmsg.getMessage();
			pmsg.setContents(str);
			
			pmsg.setMessage(str.replace("\n", " "));
		}
		
		return msg;
	}

	@Override
	public void insertMsgToInbox(SchedulerRepository repository, PushResult pr) {
		long seqno = pr.getSeqNo();
		PushMessage msg = (PushMessage) pr.getMsgObj();
		if(seqno > 0 && Constants.TRANS_CODE_CMS.equals(msg.getTransCode())) {
			PushRepository repo = (PushRepository) repository;
			repo.updateContents(seqno, msg.getContents());
		}
	}
	
	
//	private HashMap<String, String> makeParam1_0F(PushMessage msg) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("PAGE_ID", Utils.safeStr(msg.getPageId()));
//		return map;
//	}

}
