package com.wooricard.scheduler.common;

public class Constants {
	public static final String TITLE = "WooriCard Push Scheduler";
	
	public static final String PROP_FILE_PREFIX_DATABASE	= "db";
	public static final String PROP_FILE_PREFIX_DAEMON		= "daemon";
	
	public static final int FETCHER_MAXITEMS_MIN	= 1;
	public static final int FETCHER_MAXITEMS_MAX	= 1000;
	
	public static final int DUALCHECK_INTERVAL_MIN	= 5;	// seconds		
	public static final int COLLECTOR_INTERVAL_MIN	= 5;	// seconds		
	public static final int STAT_INTERVAL_MIN		= 10;	// seconds		
	
	public static final String UPMC_DBIN	= "Y";
	
	public static final String ENC_SEED = "HsUpDr@c!r0oW";

	public static final String TRANS_CODE_WPAY 	= "WPAY";
	public static final String TRANS_CODE_SMART	= "SMART";
	public static final String TRANS_CODE_EAI 	= "TPUSHSVR";
	
	public static final String TRANS_CODE_D20 	= "D20";
	public static final String TRANS_CODE_D30 	= "D30";
	public static final String TRANS_CODE_D40 	= "D40";
	public static final String TRANS_CODE_D50 	= "D50";
	public static final String TRANS_CODE_B20 	= "B20";
	public static final String TRANS_CODE_CMS 	= "CMS";

//	public static final String MSG_TMPLT_D40 		= "[%s.취소]%s.%s %s:%s %s원\n%s.../누적:%s원";
	public static final String MSG_TMPLT_APV			= "[%s]%s.%s %s:%s %,d원\n%s";
	public static final String MSG_TMPLT_SUM 		= "/%s:%,d원";

	public static final String TRANS_TYPE_ONCE				= "05";
	public static final String TRANS_TYPE_ONCE_CANCEL		= "15";
	public static final String TRANS_TYPE_SPLIT				= "08";
	public static final String TRANS_TYPE_SPLIT_CANCEL		= "18";
	public static final String TRANS_TYPE_ABROAD			= "09";
	public static final String TRANS_TYPE_ABROAD_CANCEL	= "19";
	public static final String TRANS_TYPE_CHECK				= "02";
	public static final String TRANS_TYPE_REJECT			= "99";
	
	public static final String TEXT_APV_ONCE		= "일시불";
	public static final String TEXT_APV_MONTH	= "할부";
	public static final String TEXT_APV_SPLIT	= "개월";
	public static final String TEXT_APV_CHECK	= "체크";
	public static final String TEXT_APV_ABROAD	= "해외";
	public static final String TEXT_APV_ABCHK	= "해외체크";
	public static final String TEXT_APV_REJECT	= "승인거절";
	                                
	public static final String TEXT_APV_OK 		= "승인";
	public static final String TEXT_APV_CANCEL	= "취소";
                                
	public static final String TEXT_APV_SUM 		= "누적";
	public static final String TEXT_APV_LEFT		= "잔액";
	
	public static final String DNIS_DEFAULT 		= "0000";
	public static final String TMPLT_MARKER 		= "{FIELD%d}";
	
	public static final int MAX_MCH_CHARS		= 8;
	
//	public static final String DEFAULT_URL = "https://sccd.wooribank.com/smtccd/mw/html/CARDINFO/CARDINFO_0111.html?prd_cd=835961";
}
