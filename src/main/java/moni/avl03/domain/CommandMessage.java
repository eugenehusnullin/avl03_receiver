package moni.avl03.domain;

public class CommandMessage extends Message {
	private String command;

	public CommandMessage(ProtocolType protocolType) {
		super(protocolType);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
