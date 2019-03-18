package com.wooricard.scheduler.monitor;

import com.dkntech.d3f.Daemon;
import com.dkntech.d3f.commander.Commander;

public class SchedulerCommander extends Commander {

	@Override
	public void initialize(Daemon daemon, int port) {
		super.initialize(daemon, port);
		
		addServlet(new StatusMonitor(this), "/status");
	}
}
