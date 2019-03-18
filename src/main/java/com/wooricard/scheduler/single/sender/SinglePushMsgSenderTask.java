package com.wooricard.scheduler.single.sender;

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
import com.wooricard.scheduler.sender.data.PushMessage;

public class SinglePushMsgSenderTask extends PushMsgSenderTaskBase {

	public SinglePushMsgSenderTask(String senderId, DataQueue<Data> queue, DataQueue<Data> highQueue, ServerInfo server) {
		super(senderId, queue, highQueue, server, LoggerFactory.getLogger(SinglePushMsgSenderTask.class));
	}

	@Override
	public int savePushResult(SchedulerRepository repository, PushResult pr) {
		return repository.saveResult(pr);
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
			map.put("PARAM2", "");
			
			String type = pmsg.getPushType();
			if("E1".equals(type) || "N1".equals(type)) {
				map.put("PARAM1", Utils.safeStr(pmsg.getLinkInfo()));
				map.put("IMAGE1", "");
				map.put("IMAGE2", "");
				
				map.put("ID", Utils.safeStr(pmsg.getEventNo()));
							
//				parseImageInfo(pmsg, map);
			}
			else {
				map.put("PARAM1", Utils.safeStr(pmsg.getLinkInfo()));
				map.put("IMAGE1", Utils.safeStr(pmsg.getImage()));
				map.put("IMAGE2", "");
				
				map.put("ID", Utils.safeStr(pmsg.getEventNo()));
			}
		}
		else if(Constants.TRANS_CODE_EAI.equals(transCode)) {
			map.put("TYPE", pmsg.getPushType());
			map.put("DNIS", pmsg.getDnis());
			map.put("TITLE", Utils.safeStr(pmsg.getTitle()));
			map.put("TEXT", pmsg.getMessage());
			map.put("CMPID", Utils.safeStr(pmsg.getCmpgnId()));			
			map.put("ID", Utils.safeStr(pmsg.getEventNo()));
			map.put("PARAM2", "");
			
//			map.put("IMAGE1", Utils.safeStr(pmsg.getImage()));
//			map.put("IMAGE2", "");	
			
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
						
			pmsg.setPageId(pmsg.getLinkInfo());
			HashMap<String, String> sub = makeParam1_0G(pmsg);
			map.put("PARAM1", sub != null && ! sub.isEmpty() ? sub : "");
			
			map.put("SENDDATE", Utils.dateToStr(new Date(), Utils.FRMT_DATETIME));			

//			parseImageInfo(pmsg, map);
			
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
				map.put("PARAM1", Utils.safeStr(pmsg.getLinkInfo()));
			}
			map.put("PARAM2", "");
			map.put("IMAGE1", "");
			map.put("IMAGE2", "");
			
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
			
			map.put("SENDDATE", Utils.dateToStr(new Date(), Utils.FRMT_DATETIME));			

//			parseImageInfo(pmsg, map);			
		}
		else {
			return null;
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
	
		if(Constants.TRANS_CODE_EAI.equals(pmsg.getTransCode())) {
			makeApvMessage(pmsg);
		}
		
		return msg;
	}	
	
//	private void parseImageInfo(PushMessage msg, HashMap<String, Object> map) {
//		String url = getImageServerUrl();
//		String temp = msg.getImage();
//		
//		if(Utils.isEmpty(temp) || (!temp.contains("[1]") && !temp.contains("[2]"))) {
//			return;
//		}
//		
//		temp = removeUrl(temp, url);
//		
//		map.put("IMAGE1", "");
//		map.put("IMAGE2", "");	
//		
//		if(! Utils.isEmpty(temp)) {
//			String[] tokens = temp.split(",");
//			for(String str : tokens) {
//				if(str.startsWith("[1]"))
//					map.put("IMAGE1", makeImageUrlStr(url, str, true));	
//				else if(str.startsWith("[2]"))
//					map.put("IMAGE2", makeImageUrlStr(url, str, true));	
//			}
//		}	
//	}
}
