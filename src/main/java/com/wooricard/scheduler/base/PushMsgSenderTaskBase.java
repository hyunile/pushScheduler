package com.wooricard.scheduler.base;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;

import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.fetcher.DataQueue;
import com.dkntech.d3f.processor.Processor;
import com.dkntech.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uracle.scheduler.common.Result;
import com.uracle.scheduler.repository.Message;
import com.uracle.scheduler.repository.PushResult;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.uracle.scheduler.sender.SendTask;
import com.uracle.scheduler.sender.Sender.ServerInfo;
import com.wooricard.scheduler.common.Constants;
import com.wooricard.scheduler.common.WooriCardResult;
import com.wooricard.scheduler.entity.MessageData;
import com.wooricard.scheduler.sender.data.PushMessage;

public abstract class PushMsgSenderTaskBase extends SendTask {
	protected Logger logger;
	
	protected ObjectMapper mapper;
	
	public PushMsgSenderTaskBase(String senderId, DataQueue<Data> queue, DataQueue<Data> highQueue, ServerInfo server, Logger logger) {
		super(senderId, queue, highQueue, server);
		this.logger = logger;
		mapper = new ObjectMapper();
	}

	@Override
	public void init(Processor processor) {
		super.init(processor);	
		
		setLogger(logger);
	}

	@Override
	public void saveResult(SchedulerRepository repository, PushResult pr) {
		WooriCardResult result = WooriCardResult.convertUpmcToWooriCard(pr.getResultCode());

		logger.debug(String.format("saveResult : code = %s, msg = %s", result.toString(), result.getDesc()));
		
		pr.setResultCode(result.toString());
		pr.setResultMsg(result.getDesc());
		
		if(result != WooriCardResult.SUC_OK) {
			String raw = "";
			if(! Utils.isEmpty(pr.getRawResult())) {
				raw += " [ " + pr.getRawResult();
			}
			if(! Utils.isEmpty(pr.getRawResultMsg())) {
				raw += Utils.isEmpty(raw) ? " [ " : " : ";
				raw += pr.getRawResultMsg();
			}
			if(! Utils.isEmpty(raw)) {
				raw += " ]";
				pr.setResultMsg(pr.getResultMsg() + raw);
			}
		}
		
		savePushResult(repository, pr);
	}

	
	@Override
	public int selectBadgeCount(SchedulerRepository repository, String appId, String cuid) {
//		return repository.selectBadgeCount(appId, cuid);
		return 0;
	}

	@Override
	public Message convertDataToMsg(Data data) {
		PushMessage msg = new PushMessage();
		
		MessageData d = (MessageData) data;
		
		msg.setMsgNo(d.getMsgNo());
		msg.setBatchId(d.getBatchId());
		msg.setDescription(d.getDescription());
		msg.setUseTemplete(d.getUseTemplete());
		msg.setTemplete(d.getTemplete());
		msg.setImage(d.getImgUrl1());
	    msg.setLinkInfo(d.getLinkUrl());
		msg.setTransCode(d.getTransCode());
		msg.setMsgCode(d.getMsgCode());
		msg.setTransUnique(d.getTransUnique());
		msg.setProcessDate(d.getProcessDate());
		msg.setProcessTime(d.getProcessTime());
		msg.setCardNo(d.getCardNo());
		msg.setApvNo(d.getApvNo());
		msg.setNameNo(d.getNameNo());
		msg.setTelNo(d.getTelNo());
		msg.setName(d.getName());
		msg.setPushType(d.getPushType());
		msg.setTransType(d.getTransType());
		msg.setApvMonth(d.getApvMonth());
		msg.setApvrqMtdCd(d.getApvrqMtdCd());
		msg.setApvAmount(d.getApvAmount());
		msg.setChkBl(d.getChkBl());
		msg.setMchName(d.getMchName());
		msg.setTransDate(d.getTransDate());
		msg.setStlDate(d.getStlDate());
		msg.setAppInstYn(d.getAppInstYn());
		msg.setRejectReason(d.getRejectReason());
		msg.setSmartAppYn(d.getSmartAppYn());
		msg.setAppId(d.getAppId());
		msg.setMessage(d.getMessage());
		msg.setExt(d.getExt());
		msg.setSenderId(d.getSenderCode());
		msg.setServiceCode(d.getServiceCode());
		msg.setPriority(d.getPriority());
		msg.setTitle(d.getTitle());
		msg.setReserveDate(d.getReserveDate());
		msg.setSendTimeLimit(d.getSendTimeLimit());
		msg.setCuid(d.getCuid());
		msg.setLegacyId(d.getLegacyId());
		msg.setPushFailSmsSend(d.getPushFailSmsSend());
		msg.setSmsReadWaiMinute(d.getSmsReadWaiMinute());
		msg.setDozGcmSend(d.getDozGcmSend());
		msg.setSelectDate(d.getSelectDate());
		
	    msg.setBatchTitle(d.getBatchTitle());
	    msg.setPageId(d.getPageId());
	    msg.setImgUrl2(d.getImgUrl2());
	    msg.setCmpgnId(d.getCmpgnId());
	    msg.setDnis(d.getDnis());
	    
	    msg.setKey1(d.getKey1());
	    msg.setKey2(d.getKey2());
	    
	    msg.setPopup(d.getPopup());
	    
		if(Utils.isEmpty(msg.getDnis()))
			msg.setDnis(Constants.DNIS_DEFAULT);

		msg.setImgUrl2(d.getImgUrl2());
		msg.setImgUrl3(d.getImgUrl3());
		
		msg.setLinkUrl1(d.getLinkUrl1());
		msg.setLinkUrl2(d.getLinkUrl2());
		msg.setLinkUrl3(d.getLinkUrl3());
		
		msg.setType1(d.getType1());
		msg.setType2(d.getType2());
		msg.setType3(d.getType3());

	    msg.setEventNo(d.getEventNo());

		return msg;
	}
	
	@Override
	protected boolean pushRefused(PushResult pr) {
		PushMessage m = (PushMessage) pr.getMsgObj();
		
		if(Utils.isEmpty(m.getAppId()) || 
			Utils.isEmpty(m.getCuid()) ||
			Utils.isEmpty(m.getMessage())) {
			
			pr.setResultCode(Result.ERR_PARAMS.toString());
			
			return true;
		}
		
		Date limit = m.getSendTimeLimit();
		Date now = new Date();
		
		if(limit != null && now.compareTo(limit) > 0) {
			pr.setResultCode(WooriCardResult.ERR_LIMIT_PASSED.toString());
			
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public void insertMsgToInbox(SchedulerRepository repository, PushResult pr) {
	}
	
	protected void makeApvMessage(PushMessage pmsg) {
		final String SEPARATOR = ".";
		
		String type = pmsg.getTransType();
		boolean isCheckCard = Constants.TRANS_TYPE_CHECK.equals(pmsg.getApvrqMtdCd());
		boolean isAbroad = false;
		boolean needSum = true;
		
		String t = null, r = null;
		String m = getLimitedMchName(pmsg.getMchName());
		String s = Constants.TEXT_APV_SUM;
		
		if(Constants.TRANS_TYPE_ONCE.equals(type)) {
			t = Constants.TEXT_APV_ONCE;
			r = Constants.TEXT_APV_OK;
		}
		else if(Constants.TRANS_TYPE_ONCE_CANCEL.equals(type)) {
			t = Constants.TEXT_APV_ONCE;
			r = Constants.TEXT_APV_CANCEL;
		}
		else if(Constants.TRANS_TYPE_SPLIT.equals(type)) {
			t = pmsg.getApvMonth().trim() + Constants.TEXT_APV_SPLIT;
			r = Constants.TEXT_APV_OK;			
		}
		else if(Constants.TRANS_TYPE_SPLIT_CANCEL.equals(type)) {
			String month = pmsg.getApvMonth().trim();
			if(Utils.isEmpty(month) || "0".equals(month) || "00".equals(month)) {
				t = Constants.TEXT_APV_MONTH;
			}
			else {
				t = month + Constants.TEXT_APV_SPLIT;
			}
			r = Constants.TEXT_APV_CANCEL;			
		}
		else if(Constants.TRANS_TYPE_ABROAD.equals(type)) {
			t = Constants.TEXT_APV_ABROAD;
			r = Constants.TEXT_APV_OK;
			m = pmsg.getMchName();
			needSum = false;
			isAbroad = true;
		}
		else if(Constants.TRANS_TYPE_ABROAD_CANCEL.equals(type)) {
			t = Constants.TEXT_APV_ABROAD;
			r = Constants.TEXT_APV_CANCEL;
			m = pmsg.getMchName();
			needSum = false;
			isAbroad = true;
		}
		else if(Constants.TRANS_TYPE_REJECT.equals(type)) {
			t = Constants.TEXT_APV_REJECT;
			m = Utils.safeStr(pmsg.getRejectReason());
			needSum = false;
		}
		else {
			pmsg.setMessage("");
			return;
		}
		
		if(isCheckCard) {
			if(! Constants.TEXT_APV_REJECT.equals(t)) {
				t = isAbroad ? Constants.TEXT_APV_ABCHK : Constants.TEXT_APV_CHECK;
			}
			
			s = Constants.TEXT_APV_LEFT;
		}
		
		r = r != null ? SEPARATOR + r : "";
		t = t + r;
				
		String date = pmsg.getTransDate();
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);
		String hour = date.substring(8, 10);
		String minute = date.substring(10, 12);
		
		long amount = 0, total = 0;
		
		try {
			amount = Long.valueOf(pmsg.getApvAmount());
			if(! isAbroad) {
				if(Utils.isEmpty(pmsg.getChkBl())) {
					needSum = false;
				}
				else {
					total = Long.valueOf(pmsg.getChkBl().substring(1));
				}
			}
		}
		catch(NumberFormatException e) {
			logger.error("makeApvMessage : ", e);
			pmsg.setMessage("");
			return;
		}			
		
		String msg = String.format(Constants.MSG_TMPLT_APV, 
			t, month, day, hour, minute, amount, m);
			
		if(needSum) {
			msg += String.format(Constants.MSG_TMPLT_SUM, s, total);
		}
		
		pmsg.setMessage(msg);
	}

	protected void makeTempleteMessage(PushMessage pmsg) {
		String[] tokens = pmsg.getMessage().split("\\|");
		String message = pmsg.getTemplete();
		if(! Utils.isEmpty(message) && tokens != null && tokens.length > 0) {
			for(int i = 0; i < tokens.length; i++) {
				String tmplt = String.format(Constants.TMPLT_MARKER, i + 1);
				message = message.replace(tmplt, tokens[i]);
			}

			pmsg.setMessage(message);
		}
	}

	protected HashMap<String, String> makeParam1_0G(PushMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("USER_ID", Utils.safeStr(msg.getCuid()));
		map.put("APPR_NO", Utils.safeStr(msg.getApvNo()));
		map.put("APPR_DT", Utils.safeStr(msg.getTransDate()));
		map.put("PAGE_ID", Utils.safeStr(msg.getPageId()));
		map.put("POPUP_TYPE", Utils.safeStr(msg.getPopup()));
		return map;
	}

	protected HashMap<String, String> makeParam1_0I(PushMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("USER_ID", msg.getCuid());
		map.put("DATE", msg.getProcessDate());
		map.put("PAGE_ID", Utils.safeStr(msg.getPageId()));
		map.put("POPUP_TYPE", Utils.safeStr(msg.getPopup()));
		return map;
	}

	protected HashMap<String, String> makeParam1_11_Cms(PushMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();

		String str = msg.getPageId();
		if(! Utils.isEmpty(str)) {
			String[] tokens = str.split("\\|");

			map.put("WORK", tokens[0]);
			map.put("IS_LOGIN", tokens[1]);
			map.put("PAR", tokens[2]);
		}

		return map;
	}

	protected HashMap<String, String> makeParam1_11_Loan(PushMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();

		String str = msg.getPageId();
		if(! Utils.isEmpty(str)) {
			String[] tokens = str.split("\\|");

			map.put("WORK", tokens[0]);
			map.put("IS_LOGIN", tokens[1]);
			map.put("PAGE_ID", tokens[2]);
			map.put("STDT", tokens[3]);
			map.put("ENDT", tokens[4]);
		}
		map.put("POPUP_TYPE", Utils.safeStr(msg.getPopup()));

		return map;
	}
	
	protected HashMap<String, String> makeParam1_0N(PushMessage msg) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("PAGE_ID", Utils.safeStr(msg.getLinkInfo()));
		map.put("POPUP_TYPE", Utils.safeStr(msg.getPopup()));

		return map;
	}
	
	protected String removeUrl(String img, String url) {
		if(! Utils.isEmpty(img) && !Utils.isEmpty(url) && img.startsWith(url)) {
			img = img.substring(url.length());
			while(! img.isEmpty() && img.startsWith("/")) {
				img = img.substring(1);
			}
		}
		
		return img;
	}
	
	protected String makeImageUrlStr(String url, String img, boolean cut) {
		if(Utils.isEmpty(img)) {
			return "";
		}
	
		if(cut)
			img = img.substring(3);
		
		if(! img.startsWith("/"))
			img = "/" + img;
		
		return url + img;
	}
	
	private String getLimitedMchName(String name) {
		char sp = '\u3000';
		
		while(! Utils.isEmpty(name) && name.endsWith(" ")) {
			name = name.substring(0, name.length() - 1);
		}
		
		while(! Utils.isEmpty(name) && name.charAt(name.length() - 1) == sp) {
			name = name.substring(0, name.length() - 1);
		}
		
		if(Utils.isEmpty(name))
			return "";
		
		if(name.length() <= Constants.MAX_MCH_CHARS)
			return name;
		
		String temp = name.substring(0, Constants.MAX_MCH_CHARS);
		temp += " ...";
		
		return temp;
	}
	
	abstract public int savePushResult(SchedulerRepository repository, PushResult pr);
}
