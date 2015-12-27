package moni.avl03.handlers;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import moni.avl03.domain.Message;

public class JmsSenderHandler implements Handler {

	private Gson gson = new GsonBuilder().setDateFormat("yyyy.MM.dd HH:mm:ss z").create();
	private JmsTemplate jmsTemplate;
	private ActiveMQQueue fromDeviceQueue;
	private List<Long> deviceIds;

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setDeviceIds(List<Long> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public void setFromDeviceQueue(ActiveMQQueue fromDeviceQueue) {
		this.fromDeviceQueue = fromDeviceQueue;
	}

	@Override
	public void handle(Message message) {
		if (deviceIds != null && !deviceIds.contains(message.getDeviceId())) {
			return;
		}

		String messageStr = gson.toJson(message);
		jmsTemplate.send(fromDeviceQueue, new MessageCreator() {
			@Override
			public javax.jms.Message createMessage(Session session) throws JMSException {
				TextMessage tm = session.createTextMessage(messageStr);
				return tm;
			}
		});
	}
}
