package com.wooricard.scheduler.entity;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uracle.scheduler.repository.PushResult;
import com.wooricard.scheduler.sender.data.PushMessage;

public class ResultData {
	private static final Logger logger = LoggerFactory.getLogger(ResultData.class);
	
	private long msgNo;
	private long seqNo;
	private String batchId;
	private String description;
	private String useTemplete;
	private String templete;
	private String imgUrl;
	private String transCode;
	private String msgCode;
	private String transUnique;
	private String processDate;
	private String processTime;
	private String cardNo;
	private String apvNo;
	private String nameNo;
	private String telNo;
	private String name;
	private String pushType;
	private String transType;
	private String apvMonth;
	private String apvrqMtdCd;
	private String apvAmount;
	private String chkBl;
	private String mchName;
	private String transDate;
	private String stlDate;
	private String appInstYn;
	private String rejectReason;
	private String smartAppYn;
	private String appId;
	private String message;
	private String ext;
	private String senderId;
	private String serviceCode;
	private int priority;
	private String title;
	private Date reserveDate;
	private Date sendTimeLimit;
	private String cuid;
	private String legacyId;
	private String pushFailSmsSend;
	private int smsReadWaiMinute;
	private String dozGcmSend;
	private Date selectDate;
	
//	private long pushSeqNo;
	private int badgeNo;
	private String resultCode;
	private String resultMsg;
	private String device;
	
	private Date sentDate;
	private Date receiveDate;
	private Date readDate;
	
	public ResultData() {}
	
	public ResultData(PushResult pr) {
		PushMessage m = (PushMessage) pr.getMsgObj();

		msgNo = m.getMsgNo();
		seqNo = pr.getSeqNo();
		batchId = m.getBatchId();
		description = m.getDescription();
		imgUrl = m.getImage();
		transCode = m.getTransCode();
		msgCode = m.getMsgCode();
		transUnique = m.getTransUnique();
		processDate = m.getProcessDate();
		processTime = m.getProcessTime();
		cardNo = m.getCardNo();
		apvNo = m.getApvNo();
		nameNo = m.getNameNo();
		telNo = m.getTelNo();
		name = m.getName();
		pushType = m.getPushType();
		transType = m.getTransType();
		apvMonth = m.getApvMonth();
		apvrqMtdCd = m.getApvrqMtdCd();
		apvAmount = m.getApvAmount();
		chkBl = m.getChkBl();
		mchName = m.getMchName();
		transDate = m.getTransDate();
		stlDate = m.getStlDate();
		appInstYn = m.getAppInstYn();
		rejectReason = m.getRejectReason();
		smartAppYn = m.getSmartAppYn();
		appId = m.getAppId();
		message = m.getMessage();
		ext = m.getExt();
		senderId = m.getSenderId();
		serviceCode = m.getServiceCode();
		priority = m.getPriority();
		title = m.getTitle();
		reserveDate = m.getReserveDate();
		sendTimeLimit = m.getSendTimeLimit();
		cuid = m.getCuid();
		legacyId = m.getLegacyId();
		pushFailSmsSend = m.getPushFailSmsSend();
		smsReadWaiMinute = m.getSmsReadWaiMinute();
		dozGcmSend = m.getDozGcmSend();
		selectDate = m.getSelectDate();
		
		badgeNo = pr.getBadgeNo();
		resultCode = pr.getResultCode();
		resultMsg = pr.getResultMsg();
		device = pr.getDevice();
		
		sentDate = pr.getSentDate();
		receiveDate = pr.getReceiveDate();
		readDate = pr.getReadDate();
	}
	
	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public static Logger getLogger() {
		return logger;
	}

	public long getMsgNo() {
		return msgNo;
	}

	public long getSeqNo() {
		return seqNo;
	}

	public String getBatchId() {
		return batchId;
	}

	public String getDescription() {
		return description;
	}

	public String getUseTemplete() {
		return useTemplete;
	}

	public String getTemplete() {
		return templete;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public String getTransCode() {
		return transCode;
	}

	public String getMsgCode() {
		return msgCode;
	}

	public String getTransUnique() {
		return transUnique;
	}

	public String getProcessDate() {
		return processDate;
	}

	public String getCardNo() {
		return cardNo;
	}

	public String getApvNo() {
		return apvNo;
	}

	public String getNameNo() {
		return nameNo;
	}

	public String getTelNo() {
		return telNo;
	}

	public String getName() {
		return name;
	}

	public String getTransType() {
		return transType;
	}

	public String getApvMonth() {
		return apvMonth;
	}

	public String getApvrqMtdCd() {
		return apvrqMtdCd;
	}

	public String getApvAmount() {
		return apvAmount;
	}

	public String getChkBl() {
		return chkBl;
	}

	public String getMchName() {
		return mchName;
	}

	public String getTransDate() {
		return transDate;
	}

	public String getStlDate() {
		return stlDate;
	}

	public String getAppInstYn() {
		return appInstYn;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public String getSmartAppYn() {
		return smartAppYn;
	}

	public String getAppId() {
		return appId;
	}

	public String getMessage() {
		return message;
	}

	public String getExt() {
		return ext;
	}

	public String getSenderId() {
		return senderId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public int getPriority() {
		return priority;
	}

	public String getTitle() {
		return title;
	}

	public Date getReserveDate() {
		return reserveDate;
	}

	public Date getSendTimeLimit() {
		return sendTimeLimit;
	}

	public String getCuid() {
		return cuid;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public String getPushFailSmsSend() {
		return pushFailSmsSend;
	}

	public int getSmsReadWaiMinute() {
		return smsReadWaiMinute;
	}

	public String getDozGcmSend() {
		return dozGcmSend;
	}

	public Date getSelectDate() {
		return selectDate;
	}

//	public long getPushSeqNo() {
//		return pushSeqNo;
//	}
//
	public String getResultCode() {
		return resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public String getDevice() {
		return device;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	public int getBadgeNo() {
		return badgeNo;
	}

	public String getPushType() {
		return pushType;
	}

	public String getProcessTime() {
		return processTime;
	}
}
