package com.wooricard.scheduler.common;

import java.util.Date;
import java.util.List;

import com.uracle.scheduler.common.QueryParams;

public class QueryParamsEx extends QueryParams {
	private List<Long> list;
 
	public QueryParamsEx(String keyword, long number, Date date) {
		super(keyword, number, date);
	}

	public List<Long> getList() {
		return list;
	}

	public void setList(List<Long> list) {
		this.list = list;
	}
}
