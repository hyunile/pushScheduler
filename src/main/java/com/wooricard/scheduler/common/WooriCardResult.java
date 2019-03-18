package com.wooricard.scheduler.common;

public enum WooriCardResult {
	SUC_OK("S0000", "Success."),
	SUC_MSG_RECEIVED("S0001", "User received message."),
//	ERR_INVALID_FIELD("E0400", "Invalid field exists or Json parsing failed."),
//	ERR_SENDER_AUTH("E0401", "Sender authentication failed."),

	ERR_NETWORK ("E0011", "Network error."),
	ERR_HTTP ("E0012", "HTTP error."),
	ERR_SYSTEM ("E0013", "System error."),
	ERR_PARAMS ("E0101", "Parameter is missing."),
	ERR_NOUSER ("E0102", "User not registered."),
	ERR_LIMIT_PASSED("E0103", "Limit time passed."),
	ERR_TIMEOUT("E0501", "Time out."),
	ERR_UPMC_DOWN("E0502", "UPMC system down."),
	ERR_GCM_BAD_REGISTER ("E6400", "Bad registration."),
	ERR_UPMC ("E7000", "UPMC internal error."),
	ERR_APNS_PROCESSING("E7001", "Processing error."),
	ERR_APNS_DEVICE_TOKEN("E7002", "Device token missing."),
	ERR_APNS_TOPIC_MISSING("E7003", "Topic missing."),
	ERR_APNS_PAYLOAD_MISSING("E7004", "Payload missing."),
	ERR_APNS_TOKEN_SIZE("E7005", "Invalid token size."),
	ERR_APNS_TOPIC_SIZE("E7006", "Invalid topic size."),
	ERR_APNS_PAYLOAD_SIZE("E7007", "Invalid payload size."),
	ERR_APNS_INVALID_TOKEN("E7008", "Invalid token."),
	ERR_APNS_SHUTDOWN("E7009", "Shutdown."),
	ERR_APNS_PROTOCOL("E7010", "Protocol error. (APNs could not parse the notification.)"),
	ERR_APNS_UNKNOWN("E7011", "Unknown."),	
	ERR_UPNS_CONNECTION("E9000", "UPNS connection error."),
	ERR_UPNS_DEVICE_OFFLINE("E9001", "Device is not online."),
	ERR_PUBLIC_HOST("E9400", "Public host error."),	
	ERR_UNKNOWN ("E9990", "Unknown error.");
	
	WooriCardResult(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String toString() {
		return code;
	}
	
	public static WooriCardResult convertUpmcToWooriCard(String code) {
		WooriCardResult result = ERR_UNKNOWN;
		
		if(code.matches("\\d+")) {
			if("0000".equals(code)) {
				result = SUC_OK;
			}
			else if("0001".equals(code)) {
				result = ERR_NETWORK;
			}
			else if("0002".equals(code)) {
				result = ERR_HTTP;
			}
			else if("0003".equals(code)) {
				result = ERR_SYSTEM;
			}
			else if("0004".equals(code)) {
				result = ERR_UPMC;
			}
			else if("0011".equals(code)) {
				result = ERR_PARAMS;
			}
			else if("0012".equals(code)) {
				result = ERR_NOUSER;
			}
			else if("7001".equals(code)) {
				result = ERR_APNS_PROCESSING;
			}
			else if("7002".equals(code)) {
				result = ERR_APNS_DEVICE_TOKEN;
			}
			else if("7003".equals(code)) {
				result = ERR_APNS_TOPIC_MISSING;
			}
			else if("7004".equals(code)) {
				result = ERR_APNS_PAYLOAD_MISSING;
			}
			else if("7005".equals(code)) {
				result = ERR_APNS_TOKEN_SIZE;
			}
			else if("7006".equals(code)) {
				result = ERR_APNS_TOPIC_SIZE;
			}
			else if("7007".equals(code)) {
				result = ERR_APNS_PAYLOAD_SIZE;
			}
			else if("7008".equals(code)) {
				result = ERR_APNS_INVALID_TOKEN;
			}
			else if("7009".equals(code)) {
				result = ERR_APNS_SHUTDOWN;
			}
			else if("7010".equals(code)) {
				result = ERR_APNS_PROTOCOL;
			}
			else if("7011".equals(code)) {
				result = ERR_APNS_UNKNOWN;
			}
			else if("9000".equals(code)) {
				result = ERR_UPNS_CONNECTION;
			}
			else if("9001".equals(code)) {
				result = ERR_UPNS_DEVICE_OFFLINE;
			}
			else if("9400".equals(code)) {
				result = ERR_PUBLIC_HOST;
			}
			else /*if("9990".equals(code))*/ {
				result = ERR_UNKNOWN;
			}
		}
		else {
			result = find(code);
		}
		
		return result;
	}

	private static WooriCardResult find(String code) {
		WooriCardResult[] results = WooriCardResult.values();
		for(WooriCardResult r : results) {
			if(r.toString().equals(code))
				return r;
		}
		
		return null;
	}
	
	private String code;
	private String desc;
}
