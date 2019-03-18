package com.wooricard.scheduler.monitor;

import java.util.List;
import java.util.Map;

import com.dkntech.d3f.collector.Collector;
import com.dkntech.d3f.commander.Commander;
import com.dkntech.d3f.commander.StatusServlet;
import com.dkntech.d3f.fetcher.Fetcher;
import com.dkntech.utils.Utils;
import com.wooricard.scheduler.base.ResultCollectorBase;

public class StatusMonitor extends StatusServlet {
	private static final long serialVersionUID = 1L;

	public StatusMonitor(Commander commander) {
		super(commander);
	}

	@Override
	public String makeTextResult(List<Map<String, Object>> list) {
		String line = "--------------------------------------------------------------------------------";
		String blank40 = "                                        ";
		String blank20 = "                    ";
		String blank55 = "                                                       ";
		
		StringBuilder str = new StringBuilder(85 * 60);
		for(Map<String, Object> map : list) {
			if("F".equals(map.get("type"))) {
				str.append(String.format(">>> %-15s (status = %-8s) %s\n", map.get(Fetcher.ATTR_NAME), map.get(Fetcher.ATTR_STATUS), blank40));
				str.append(line + "\n");
				
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> qList = (List<Map<String, Object>>) map.get(Fetcher.ATTR_Q_LIST);
				for(Map<String, Object> sub : qList) {
					str.append(String.format("    %s [ Total : %-7s  Current : %-7s  TPS : %-7s  Max TPS : %-7s ] \n", 
						sub.get(Fetcher.ATTR_NAME), sub.get(Fetcher.ATTR_TOTAL), sub.get(Fetcher.ATTR_CURRENT),
						sub.get(Fetcher.ATTR_TPS), sub.get(Fetcher.ATTR_MAX_TPS)));
				}
				
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> pList = (List<Map<String, Object>>) map.get(Fetcher.ATTR_P_LIST);
				long total = 0;
				
				if(pList != null && pList.isEmpty()) {
					str.append(line + "\n");
					for(Map<String, Object> sub : pList) {
						str.append(String.format("    %s [ ", sub.get(Fetcher.ATTR_NAME)));
						String count = (String) sub.get(Fetcher.ATTR_FROM_Q0);
						if(! Utils.isEmpty(count)) {
							str.append(String.format("From Q0 : %-7s  ", count));
						}
						
						str.append(String.format("From Q1 : %-7s  Processed : %-7s ]%s\n", 
							sub.get(Fetcher.ATTR_FROM_Q1), sub.get(Fetcher.ATTR_PROCESSED), blank20));
						total += Long.valueOf((String) sub.get(Fetcher.ATTR_PROCESSED));
					}
					str.append(line + "\n");
					str.append(String.format("    Sent Total : %-8d%s\n", total, blank55));
				}
				str.append(line + "\n");
			}
			else if("C".equals(map.get("type"))) {
				str.append(String.format(">>> %-15s (status = %-8s) %s\n", map.get(Fetcher.ATTR_NAME), map.get(Fetcher.ATTR_STATUS), blank40));
				str.append(line + "\n");
				str.append(String.format("    Collected : %-7s  Received = %-7s  Failed : %-7s%s\n" , map.get(Collector.ATTR_COLLECTED),
						map.get(ResultCollectorBase.ATTR_RECEIVED), map.get(ResultCollectorBase.ATTR_FAILED), blank20));
				str.append(line + "\n");				
			}
		}
		
		return str.toString();
	}
}
