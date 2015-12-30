package moni.avl03.income;

import java.nio.charset.Charset;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import moni.avl03.domain.CommandMessage;
import moni.avl03.state.ContextKeeper;

public class CommandListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	private static final Logger commandsLogger = LoggerFactory.getLogger("commands");
	private Charset asciiCharset = Charset.forName("ASCII");

	private ContextKeeper contextKeeper;
	private Gson gson = new GsonBuilder().setDateFormat("yyyy.MM.dd HH:mm:ss z").create();

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

			handleCommand(str);

		} catch (JMSException e) {
			logger.error("JMSException.", e);
		}
	}

	private void handleCommand(String str) {
		try {
			CommandMessage cm = gson.fromJson(str, CommandMessage.class);
			commandsLogger.debug(cm.getDeviceId() + " " + cm.getCommand());
			contextKeeper.writeToContext(cm.getDeviceId(), cm.getCommand().getBytes(asciiCharset));
		} catch (JSONException e) {
			logger.error("Command is not valid json.", e);
		}
	}
}
