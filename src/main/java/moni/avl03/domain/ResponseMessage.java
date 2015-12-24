package moni.avl03.domain;

public class ResponseMessage extends Message {
	private String response;

	public ResponseMessage(ProtocolType protocolType) {
		super(protocolType);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
