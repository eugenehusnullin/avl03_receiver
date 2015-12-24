package moni.avl03.decode;

import moni.avl03.domain.Message;
import moni.avl03.netty.MessageContainer;

public interface Decoder {

	Message decode(MessageContainer mc);

	Message decode(String str);
}
