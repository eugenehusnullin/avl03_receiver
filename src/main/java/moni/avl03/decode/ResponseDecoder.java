package moni.avl03.decode;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import moni.avl03.domain.Message;
import moni.avl03.domain.ProtocolType;
import moni.avl03.domain.ResponseMessage;
import moni.avl03.netty.MessageContainer;

public class ResponseDecoder implements Decoder {
	// private static final Logger logger = LoggerFactory.getLogger(ResponseDecoder.class);
	private static final Logger responsesLogger = LoggerFactory.getLogger("responses");
	private Charset asciiCharset = Charset.forName("ASCII");

	@Override
	public Message decode(MessageContainer mc) {
		byte[] bytes = mc.getBytes();
		String str = new String(bytes, asciiCharset);
		responsesLogger.info(str);
		return decode(str);
	}

	@Override
	public Message decode(String str) {
		ResponseMessage am = new ResponseMessage(ProtocolType.response);
		am.setResponse(str);
		return am;
	}
}
