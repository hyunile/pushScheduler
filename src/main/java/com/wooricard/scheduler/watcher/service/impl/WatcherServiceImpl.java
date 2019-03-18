package com.wooricard.scheduler.watcher.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dkntech.utils.HttpUtils;
import com.dkntech.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wooricard.scheduler.entity.ResultData;
import com.wooricard.scheduler.watcher.repository.WatcherRepository;
import com.wooricard.scheduler.watcher.service.WatcherService;

@Service
public class WatcherServiceImpl implements WatcherService {
	private static final Logger logger = LoggerFactory.getLogger(WatcherServiceImpl.class);

	private static final String PROVIDER_API		= "getSystemInfo.prv";
	
	private static final String ERR_PRV_CONN		= "50001";
	private static final String ERR_PRV_STATUS	= "50002";
	private static final String ERR_UPMC_CONN	= "50003";
	
	private static final String ERR_PRV_CONN_MSG	= "%s(%s) 접속 오류";
	private static final String ERR_PRV_STATUS_MSG	= "%s(%s) 동작 오류 : ";
	private static final String ERR_UPMC_CONN_MSG	= "%s 접속 오류";
	
	@Autowired
	private WatcherRepository repository;
	
	@Value("${monitor.provider.ip1}")
	private String providerIp1;
	
	@Value("${monitor.provider.ip2}")
	private String providerIp2;
	
	@Value("${monitor.provider.port}")
	private int port;
	
	@Value("${sms.send.interval}")
	private int interval;
	
	@Value("${sms.admin.domain}")
	private String smsUrl;

	@Value("${sender.upmc.url}")
	private String upmcUrl;

	
	private static ObjectMapper objMapper = new ObjectMapper();
	
	@Override
	public int watchUpmc() {
		int result = 0;
		logger.info("watchUpmc : upmcUrl = " + upmcUrl);
		
		long seqno = 0; 
		StringBuffer buffer = new StringBuffer();
		
		try {
			int httpResult = HttpUtils.rest(upmcUrl, "POST", null, null, buffer);
			if(httpResult != 200 && httpResult != 201) {
				logger.info("watchUpmc : result = " + httpResult);
			}
		} 
		catch (HttpHostConnectException e) {
			logger.error(String.format("watchUpmc : %s : %s", e.getClass().getName(), e.getMessage()));
			String str = String.format(ERR_UPMC_CONN_MSG, upmcUrl);
			seqno = saveServerError(upmcUrl, "UPMC", ERR_UPMC_CONN, str);
		}
		catch (Exception e) {
			logger.error(String.format("watchUpmc : %s : %s", e.getClass().getName(), e.getMessage()));
		}
		
		if(seqno > 0) {
			result = sendSms(seqno);
			logger.info("watchUpmc : sms sent result = " + result);
		}
		
		return result;
	}
	
	@Override
	public int watchProvider() {
		int result = 0;
		logger.info("watchProvider : providerIp1 = " + providerIp1);
		
		if(! Utils.isEmpty(providerIp1)) {
			long seqno = watchProvider(providerIp1, "Provider 1");
			logger.info("watchProvider : seqno = " + seqno);
			if(seqno > 0) {
				result = sendSms(seqno);
				logger.info("watchProvider : Provider 1 : sms sent result = " + result);
			}
		}
		
		logger.info("watchProvider : providerIp2 = " + providerIp2);
		
		if(! Utils.isEmpty(providerIp2)) {
			long seqno = watchProvider(providerIp2, "Provider 2");
			logger.info("watchProvider : seqno = " + seqno);
			if(seqno > 0) {
				result = sendSms(seqno);
				logger.info("watchProvider : Provider 2 : sms sent result = " + result);
			}
		}
		
		return result;
	}
	
	private long watchProvider(String ip, String server) {
		long seqno = 0; 
		StringBuffer result = new StringBuffer();
		String url = getProviderUrl(ip);
		logger.info("watchProvider : url = " + url);
		
		try {
			int httpResult = HttpUtils.rest(url, "GET", null, null, result);
			if(httpResult == 200 || httpResult == 201) {
				StringBuffer msg = new StringBuffer();
				if(! providerWorking(result.toString(), msg)) {
					String str = String.format(ERR_PRV_STATUS_MSG, server, ip) + msg.toString();
					seqno = saveServerError(server, "PROVIDER", ERR_PRV_STATUS, str);
				}
			}
			else {
				logger.error("watchProvider : HTTP error : ip = " + providerIp1 + " : result = " + httpResult);
			}
		} 
		catch (HttpHostConnectException e) {
			logger.error(String.format("watchProvider : %s : %s", e.getClass().getName(), e.getMessage()));
			String str = String.format(ERR_PRV_CONN_MSG, server, ip);
			seqno = saveServerError(server, "PROVIDER", ERR_PRV_CONN, str);
		}
		catch (Exception e) {
			logger.error(String.format("watchProvider : %s : %s", e.getClass().getName(), e.getMessage()));
		}
		
		return seqno;
	}
	
	private long saveServerError(String server, String type, String resultCode, String msg) {
		long seqno = 0;
		if(repository.getSavedServerError(server, resultCode, interval) < 1) {
			ResultData data = new ResultData();
			data.setLegacyId(server);
			data.setTransType(type);
			data.setResultCode(resultCode);
			data.setResultMsg(msg);
			
			repository.saveServerError(data);
			seqno = data.getSeqNo();
		}
		
		return seqno;
	}
	
	private int sendSms(long seqno) {
		int result = 0;
		List<Map<String, String>> list = repository.selectSmsReceivers();
		if(list != null && ! list.isEmpty()) {
			Map<String, String> msg = repository.selectFailMsg(seqno);
			if(msg != null && msg.size() > 1) {
				String resultCode = msg.get("RESULTCODE");
				String resultMsg = msg.get("RESULTMSG");
				
				List<HashMap<String, String>> paramList = new ArrayList<HashMap<String, String>>();

				for(Map<String, String> r : list) {
					String usr = r.get("USERID");
					String tel = r.get("TEL");
					if(! Utils.isEmpty(tel)) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("USER_PHONE", tel);
						params.put("USER_MSG", resultMsg);
						params.put("CUID", usr);
						params.put("RESULTCODE", resultCode);
						params.put("schedulerCheck", "Y");
						
						paramList.add(params);
					}
					else {
						logger.error("sendSms : No phone number. (" + usr + ")");
					}
				}
					
				try {
					String jsonParams = objMapper.writeValueAsString(paramList);
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Content-Type", "application/json");

					StringBuffer buffer = new StringBuffer();	
					int httpResult = HttpUtils.postOrPutWithJson(smsUrl, "POST", jsonParams, headers, buffer);
					if(httpResult == 200 || httpResult == 201) {
						logger.info("sendSms : sms sent : count = " + list.size());
						result = list.size();
					}
					else {
						logger.error("sendSms : http error : code = " + httpResult);
					}
				} 
				catch (Exception e) {
					logger.error(String.format("sendSms : %s : %s", e.getClass().getName(), e.getMessage()));
				} 
			}
			else {
				logger.error("sendSms : No msg to send.");
			}
		}
		else {
			logger.info("sendSms : No receiver exists.");
		}
				
		return result;
	}
	
	private boolean providerWorking(String str, StringBuffer result) {
		logger.info("providerWorking : " + str);
		
		JsonNode node = null;
		try {
//			node = objMapper.readTree(
//			"{\"resultCode\":\"200\",\"resultMsg\":\"SUCCESS\",\"data\":{\"SYSTEM_INFO\":"
//			+ "{\"FreePhysicalMemorySize\":\"0\",\"PhysicalMemorySize\":\"0\",\"OSArch\":\"?\","
//			+ "\"UsedHeapMemory\":\"0\",\"ThhreadCnt\":\"0\",\"OSName\":\"?\",\"MaxHeapMemory\":"
//			+ "\"0\",\"Processor\":\"?\"},\"APNS_INFO\":[{\"expire\":\"2019-09-20\",\"certName\""
//			+ ":\"apns_cert_wpay.p12\",\"appid\":\"com.wooricard.wpay\",\"msg\":\"OK\",\"mode\":"
//			+ "\"REAL\"},{\"expire\":\"2019-11-07\",\"certName\":\"apns_cert_smart.p12\",\"appid\""
//			+ ":\"com.wooricard.smart\",\"msg\":\"OK\",\"mode\":\"DEV\"},{\"expire\":\"2019-09-20\""
//			+ ",\"certName\":\"apns_cert_wpay.p12\",\"appid\":\"com.wooricard.wpay\",\"msg\":\"OK\""
//			+ ",\"mode\":\"DEV\"},{\"expire\":\"2019-11-07\",\"certName\":\"apns_cert_smart.p12\","
//			+ "\"appid\":\"com.wooricard.smart\",\"msg\":\"OK\",\"mode\":\"REAL\"}],\"GCM_INFO\":"
//			+ "[{\"com.wooricard.wpay\":\"OK\"},{\"com.wooricard.smart\":\"OK\"}],\"MSG_INFO\":"
//			+ "{\"timedWatingCnt\":\"0\",\"runnableCnt\":\"0\",\"queueCnt\":\"0\",\"terminatedCnt\""
//			+ ":\"0\",\"waitingCnt\":\"10\",\"activeWoker\":\"10\",\"newCnt\":\"0\",\"blockedCnt\""
//			+ ":\"0\"},\"TPM\":{\"INPUT_CNT\":\"0\",\"OUTPUT_CNT\":\"0\",\"MAX_INPUT_CNT\":\"9\""
//			+ ",\"MAX_OUTPUT_CNT\":\"9\",\"CHECK_DATE\":\"20181211220430\",\"INTERVAL\":\"60s\"}}}"
//			);
			
			node = objMapper.readTree(str);
			
			List<String> resultCode = node.findValuesAsText("resultCode");
			if(resultCode.isEmpty()) {
				logger.error("providerWorking : no resultCode.");
				return false;
			}
			
			if(! "200".equals(resultCode.get(0))) {
				logger.error("providerWorking : resultCode = " + resultCode.get(0));
				return false;
			}
			
			List<String> active = node.findValuesAsText("activeWoker");
			List<String> runnable = node.findValuesAsText("runnableCnt");
			List<String> intput = node.findValuesAsText("INPUT_CNT");
			List<String> output = node.findValuesAsText("OUTPUT_CNT");
			
			if(! output.isEmpty() && ! runnable.isEmpty() && ! active.isEmpty()) {
				if(active.get(0).equals(runnable.get(0)) && "0".equals(output.get(0))) {
					String msg = String.format("ACT = %s, RUN = %s, IN = %s, OUT = %s",
							active.get(0), runnable.get(0), intput.get(0), output.get(0));
					result.append(msg);
					
					logger.error("providerWorking : Provider not working !!! : " + msg);
					
					return false;
				}
			}
		} 
		catch (IOException e) {
			logger.error(String.format("providerWorking : %s : %s", e.getClass().getName(), e.getMessage()));
		}
		
		return true;
	}

	private String getProviderUrl(String ip) {
		return "http://" + ip + ":" + port + "/" + PROVIDER_API;
	}
}
