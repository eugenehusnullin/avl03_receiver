package moni.avl03.netty;

public class MessageContainer {
	private MessageType type;
	private byte[] bytes;

	public MessageContainer(MessageType type, byte[] bytes) {
		this.type = type;
		this.bytes = bytes;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
