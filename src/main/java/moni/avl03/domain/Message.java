package moni.avl03.domain;

import java.util.Date;

public class Message {
	private ProtocolType protocolType;
	private Date date;
	private Long deviceId;

	public Message(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}

	public ProtocolType getProtocolType() {
		return protocolType;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long id) {
		deviceId = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
