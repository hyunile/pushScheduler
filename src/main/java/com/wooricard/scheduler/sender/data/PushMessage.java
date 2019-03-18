package com.wooricard.scheduler.sender.data;

import java.util.Date;

import com.uracle.scheduler.repository.Message;

public class PushMessage extends Message {
	private String batchId;
	private String description;
	private String useTemplete;
	private String templete;
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
	private Date sendTimeLimit;
	private Date selectDate;

//    private String batchCode;
    private String batchTitle;
    private String pageId;
    private String imgUrl2;
    private String imgUrl3;
    private String linkUrl1;
    private String linkUrl2;
    private String linkUrl3;
    private String type1;
    private String type2;
    private String type3;
    private String cmpgnId;
    private String dnis;
    private String key1;
    private String key2;
    
    private String popup;
    private String eventNo;

	public String getBatchId() {
		return batchId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUseTemplete() {
		return useTemplete;
	}
	
	public void setUseTemplete(String useTemplete) {
		this.useTemplete = useTemplete;
	}
	
	public String getTemplete() {
		return templete;
	}
	
	public void setTemplete(String templete) {
		this.templete = templete;
	}
	
	public String getTransCode() {
		return transCode;
	}
	
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	
	public String getMsgCode() {
		return msgCode;
	}
	
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	
	public String getTransUnique() {
		return transUnique;
	}

	public void setTransUnique(String transUnique) {
		this.transUnique = transUnique;
	}
	
	public String getProcessDate() {
		return processDate;
	}
	
	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}
	
	public String getCardNo() {
		return cardNo;
	}
	
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	
	public String getApvNo() {
		return apvNo;
	}
	
	public void setApvNo(String apvNo) {
		this.apvNo = apvNo;
	}
	
	public String getNameNo() {
		return nameNo;
	}
	
	public void setNameNo(String nameNo) {
		this.nameNo = nameNo;
	}
	
	public String getTelNo() {
		return telNo;
	}
	
	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTransType() {
		return transType;
	}
	
	public void setTransType(String transType) {
		this.transType = transType;
	}
	
	public String getApvMonth() {
		return apvMonth;
	}
	
	public void setApvMonth(String apvMonth) {
		this.apvMonth = apvMonth;
	}
	
	public String getApvrqMtdCd() {
		return apvrqMtdCd;
	}
	
	public void setApvrqMtdCd(String apvrqMtdCd) {
		this.apvrqMtdCd = apvrqMtdCd;
	}
	
	public String getApvAmount() {
		return apvAmount;
	}
	
	public void setApvAmount(String apvAmount) {
		this.apvAmount = apvAmount;
	}
	
	public String getChkBl() {
		return chkBl;
	}
	
	public void setChkBl(String chkBl) {
		this.chkBl = chkBl;
	}
	
	public String getMchName() {
		return mchName;
	}
	
	public void setMchName(String mchName) {
		this.mchName = mchName;
	}
	
	public String getTransDate() {
		return transDate;
	}
	
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	
	public String getStlDate() {
		return stlDate;
	}
	
	public void setStlDate(String stlDate) {
		this.stlDate = stlDate;
	}
	
	public String getAppInstYn() {
		return appInstYn;
	}
	
	public void setAppInstYn(String appInstYn) {
		this.appInstYn = appInstYn;
	}
	
	public String getRejectReason() {
		return rejectReason;
	}
	
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	
	public String getSmartAppYn() {
		return smartAppYn;
	}
	
	public void setSmartAppYn(String smartAppYn) {
		this.smartAppYn = smartAppYn;
	}
	
	public Date getSendTimeLimit() {
		return sendTimeLimit;
	}
	
	public void setSendTimeLimit(Date sendTimeLimit) {
		this.sendTimeLimit = sendTimeLimit;
	}
	
	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public String getBatchTitle() {
		return batchTitle;
	}

	public void setBatchTitle(String batchTitle) {
		this.batchTitle = batchTitle;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getImgUrl2() {
		return imgUrl2;
	}

	public void setImgUrl2(String imgUrl2) {
		this.imgUrl2 = imgUrl2;
	}

	public String getCmpgnId() {
		return cmpgnId;
	}

	public void setCmpgnId(String cmpgnId) {
		this.cmpgnId = cmpgnId;
	}

	public String getDnis() {
		return dnis;
	}

	public void setDnis(String dnis) {
		this.dnis = dnis;
	}

	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getPopup() {
		return popup;
	}

	public void setPopup(String popup) {
		this.popup = popup;
	}

	public String getProcessTime() {
		return processTime;
	}

	public void setProcessTime(String processTime) {
		this.processTime = processTime;
	}

	public String getImgUrl3() {
		return imgUrl3;
	}

	public void setImgUrl3(String imgUrl3) {
		this.imgUrl3 = imgUrl3;
	}

	public String getLinkUrl1() {
		return linkUrl1;
	}

	public void setLinkUrl1(String linkUrl1) {
		this.linkUrl1 = linkUrl1;
	}

	public String getLinkUrl2() {
		return linkUrl2;
	}

	public void setLinkUrl2(String linkUrl2) {
		this.linkUrl2 = linkUrl2;
	}

	public String getLinkUrl3() {
		return linkUrl3;
	}

	public void setLinkUrl3(String linkUrl3) {
		this.linkUrl3 = linkUrl3;
	}

	public String getType1() {
		return type1;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public String getType3() {
		return type3;
	}

	public void setType3(String type3) {
		this.type3 = type3;
	}

	public String getEventNo() {
		return eventNo;
	}

	public void setEventNo(String eventNo) {
		this.eventNo = eventNo;
	}
}
