package moni.avl03.netty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class ZeroDecoder extends ReplayingDecoder<Void> {
	private static final Logger logger = LoggerFactory.getLogger(ZeroDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// $$A5359772039720983|88UA1255.71022N037.76457E000022|01.2|00.7|01.0|20151001205956|20151001205956|101001000000|14151276|00000000|1E698B19|0000|0.0000|0522||00000|CBCB

		int i = 0;
		while (i < 0xFF) {
			i++;
			byte b = in.readByte();

			if (b == 0x24) {
				if (in.readByte() == 0x24) {
					byte[] sizeBytes = new byte[2];
					in.readBytes(sizeBytes);
					int size = Integer.parseInt(new String(sizeBytes, "ASCII"), 16);

					in.readerIndex(in.readerIndex() - 4);
					byte[] messageBytes = new byte[size];
					in.readBytes(messageBytes);

					MessageContainer messageContainer = new MessageContainer(MessageType.info, messageBytes);
					out.add(messageContainer);
					return;
				}

			} else if (b == 0x52) { // 0x52 - ASCII is R
				in.readerIndex(in.readerIndex() - 1);
				byte[] receBytes = new byte[4];
				in.readBytes(receBytes);
				in.readerIndex(in.readerIndex() - 4);
				String rece = new String(receBytes, "ASCII");
				if (rece.equals("Rece")) {
					int size = in.bytesBefore(0xFF, (byte) 0x23);
					if (size != -1) {
						byte[] messageBytes = new byte[size];
						in.readBytes(messageBytes);

						MessageContainer messageContainer = new MessageContainer(MessageType.response, messageBytes);
						out.add(messageContainer);
						return;
					}
				}
				in.readerIndex(in.readerIndex() + 1);
			}
		}

		ctx.disconnect();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("ZeroDecoder.channelUnregistered");
		super.channelUnregistered(ctx);
	}
}
