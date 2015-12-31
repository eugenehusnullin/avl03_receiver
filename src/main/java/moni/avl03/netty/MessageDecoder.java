package moni.avl03.netty;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import moni.avl03.decode.Decoder;
import moni.avl03.decode.InfoDecoder;
import moni.avl03.decode.ResponseDecoder;
import moni.avl03.domain.DisconnectMessage;
import moni.avl03.domain.InfoMessage;
import moni.avl03.domain.Message;
import moni.avl03.domain.ProtocolType;
import moni.avl03.handlers.Handler;
import moni.avl03.state.ContextKeeper;

@Sharable
public class MessageDecoder extends ChannelHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
	private InfoDecoder infoDecoder;
	private ResponseDecoder responseDecoder;
	private ContextKeeper contextKeeper;
	private List<Handler> handlers;
	private SocketManager socketManager;

	public final static AttributeKey<Long> AK_ID = AttributeKey.valueOf("id");

	public MessageDecoder() {
		this.infoDecoder = new InfoDecoder();
		this.responseDecoder = new ResponseDecoder();
	}

	public void setHandlers(List<Handler> handlers) {
		this.handlers = handlers;
	}

	public void setContextKeeper(ContextKeeper contextKeeper) {
		this.contextKeeper = contextKeeper;
	}

	public void setSocketManager(SocketManager socketManager) {
		this.socketManager = socketManager;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof MessageContainer)) {
			logger.warn("msg is not instance of MessageContainer, it is: " + msg.getClass().getName());
			return;
		}
		
		Long deviceId = ctx.channel().attr(AK_ID).get();

		MessageContainer mc = (MessageContainer) msg;
		Decoder decoder = chooseDecoder(mc);
		Message message = decoder.decode(deviceId, mc);

		if (message != null) {
			if (deviceId == null) {
				if (mc.getType() == MessageType.info) {
					deviceId = ((InfoMessage) message).getImei();
					ctx.channel().attr(AK_ID).set(deviceId);
					contextKeeper.putContext(deviceId, ctx);
				} else {
					// don't have deviceId
					return;
				}
			}

			message.setDeviceId(deviceId);
			message.setDate(new Date());
			handle(message);

		} else {
			ctx.close().sync();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (!(cause instanceof IOException)) {
			super.exceptionCaught(ctx, cause);
		}
	}

	private void handle(Message message) {
		for (Handler handler : handlers) {
			try {
				handler.handle(message);
			} catch (Exception e) {
				logger.error("Error handling message.", e);
				socketManager.stopSocketAsync();
			}
		}
	}

	private Decoder chooseDecoder(MessageContainer mc) {
		if (mc.getType() == MessageType.info) {
			return infoDecoder;
		} else {
			return responseDecoder;
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		Long deviceId = ctx.channel().attr(AK_ID).get();
		if (deviceId != null) {
			contextKeeper.removeContext(deviceId);

			DisconnectMessage dm = new DisconnectMessage(ProtocolType.disconnect);
			dm.setDeviceId(deviceId);
			dm.setDate(new Date());
			handle(dm);
		}
		super.channelUnregistered(ctx);
	}
}
