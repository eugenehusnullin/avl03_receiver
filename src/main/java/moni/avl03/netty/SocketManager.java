package moni.avl03.netty;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class SocketManager {
	private static final Logger logger = LoggerFactory.getLogger(SocketManager.class);
	private SocketStarter socketStarter;
	private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
	private JmsTemplate jmsTemplate;
	private ActiveMQQueue testQueue;

	private boolean stoped = false;
	private Object sync = new Object();
	private boolean appStoped = false;

	public void setSocketStarter(SocketStarter socketStarter) {
		this.socketStarter = socketStarter;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setTestQueue(ActiveMQQueue testQueue) {
		this.testQueue = testQueue;
	}

	public void init() {
		taskScheduler.initialize();
	}

	public void stop() {
		appStoped = true;
		taskScheduler.shutdown();
	}

	public void stopSocketAsync() {
		if (appStoped) {
			return;
		}

		Date d = new Date(new Date().getTime() + (100));
		taskScheduler.schedule(this::stopSocket, d);
	}

	private void stopSocket() {
		if (appStoped) {
			return;
		}

		synchronized (sync) {
			if (!stoped) {
				stoped = true;
				try {
					socketStarter.stop();
				} catch (InterruptedException e) {
					logger.error("stop socket error.", e);
				}
				logger.info("socket stoped.");

				startScheduledJmsAvailablityCheck();
			} else {
				logger.warn("try stop socket, but it stoped before.");
			}
		}
	}

	private void startSocket() {
		if (appStoped) {
			return;
		}

		synchronized (sync) {
			if (stoped) {
				stoped = false;
				try {
					socketStarter.run();
					logger.info("socket started.");
				} catch (InterruptedException e) {
					logger.error("start socket error.", e);
				}
			} else {
				logger.warn("try start jms, but it started.");
			}
		}
	}

	private void startScheduledJmsAvailablityCheck() {
		Date d = new Date(new Date().getTime() + (60 * 1000));
		taskScheduler.schedule(this::checkJms, d);
	}

	private void checkJms() {
		if (appStoped) {
			return;
		}

		boolean canConnect = false;
		try {
			jmsTemplate.send(testQueue, new MessageCreator() {
				@Override
				public javax.jms.Message createMessage(Session session) throws JMSException {
					TextMessage tm = session.createTextMessage("TEST MESAGE. YOU CAN PURGE THIS QUEUE.");
					return tm;
				}
			});

			canConnect = true;
		} catch (Exception e) {
			logger.error("Check connect ERROR.", e);
		}

		if (canConnect) {
			logger.info("can connect to wialon. stop check connection and start jms.");
			startSocket();
		} else {
			logger.warn("cannot connect to wialon. next check throw little interval.");
			startScheduledJmsAvailablityCheck();
		}
	}

}
