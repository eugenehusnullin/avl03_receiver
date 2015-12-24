package moni.avl03.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

public class SocketStarter {
	private static final Logger logger = LoggerFactory.getLogger(SocketStarter.class);

	private String host;
	private int port;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap serverBootstrap;
	private ChannelFuture channelFuture;
	private MessageDecoder messageDecoder;

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMessageDecoder(MessageDecoder messageDecoder) {
		this.messageDecoder = messageDecoder;
	}

	public void run() throws InterruptedException {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();

		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ZeroDecoder(), messageDecoder);
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		channelFuture = serverBootstrap.bind(host, port).sync();
		logger.info("Socket started, on host - " + host + ", port - " + port + ".");
	}

	public void stop() throws InterruptedException {
		ChannelFuture closeFuture = channelFuture.channel().close();
		closeFuture.sync();

		@SuppressWarnings("rawtypes")
		Future fw = workerGroup.shutdownGracefully();
		@SuppressWarnings("rawtypes")
		Future fb = bossGroup.shutdownGracefully();
		try {
			fb.await();
			fw.await();
		} catch (InterruptedException ignore) {
		}

		logger.info("Socket stoped.");
	}
}
