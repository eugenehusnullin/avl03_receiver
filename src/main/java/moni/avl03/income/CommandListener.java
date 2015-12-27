package moni.avl03.income;

import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import moni.avl03.state.ContextKeeper;

public class CommandListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	private static final Logger commandsLogger = LoggerFactory.getLogger("commands");

	private ContextKeeper contextKeeper;

	public void setContextKeeper(ContextKeeper contextKeeper) {
		this.contextKeeper = contextKeeper;
	}

	@Override
	public void onMessage(Message message) {
		if (!(message instanceof TextMessage)) {
			logger.error("JMS command message is not type of TextMessage.");
			return;
		}

		try {
			String str = ((TextMessage) message).getText();
			logger.debug(str);

			sendCommand(str);

		} catch (JMSException e) {
			logger.error("JMSException.", e);
		}
	}

	private void sendCommand(String str) {
		try {
			JSONObject json = (JSONObject) new JSONTokener(str).nextValue();
			Long deviceId = json.getLong("deviceId");
			String command = json.getString("command");

			commandsLogger.info(command);

			try {
				contextKeeper.writeToContext(deviceId, command.getBytes("ASCII"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Send to device error.", e);
			}
		} catch (JSONException e) {
			logger.error("Command is not valid json.", e);
		}
	}
}
