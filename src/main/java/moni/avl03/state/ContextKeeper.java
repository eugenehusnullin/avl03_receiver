package moni.avl03.state;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ContextKeeper {
	private Map<Long, ChannelHandlerContext> contextMap = new HashMap<Long, ChannelHandlerContext>();

	public ChannelHandlerContext getContext(Long id) {
		synchronized (contextMap) {
			ChannelHandlerContext ctx = contextMap.get(id);
			contextMap.notifyAll();
			return ctx;
		}
	}

	public void putContext(Long id, ChannelHandlerContext ctx) {
		synchronized (contextMap) {
			contextMap.put(id, ctx);
			contextMap.notifyAll();			
		}
	}
	
	public void removeContext(Long id) {
		synchronized (contextMap) {
			contextMap.remove(id);
			contextMap.notifyAll();			
		}
	}
	
	public void writeToContext(Long id, byte[] bytes) {
		ChannelHandlerContext ctx = getContext(id);
		if (ctx == null) {
			return;
		}

		ByteBuf b = ctx.alloc().buffer(bytes.length);
		b.writeBytes(bytes);
		ctx.write(b);
		ctx.flush();
	}
}
