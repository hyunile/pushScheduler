package com.wooricard.scheduler.single.sender;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dkntech.d3f.common.Data;
import com.dkntech.d3f.fetcher.DataQueue;
import com.uracle.scheduler.repository.SchedulerRepository;
import com.uracle.scheduler.sender.SendTask;
import com.uracle.scheduler.sender.Sender;

@Component
@Scope("prototype")
public class SinglePushMsgSender extends Sender {

	public SinglePushMsgSender(SchedulerRepository repository) {
		super(repository);
	}

	@Override
	public SendTask createSendTask(String senderId, DataQueue<Data> queue, DataQueue<Data> highQueue, ServerInfo info) {
		SinglePushMsgSenderTask task = new SinglePushMsgSenderTask(senderId, queue, highQueue, info);
		return task;
	}
}
