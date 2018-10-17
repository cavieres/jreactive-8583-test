package com.example.jreactive8583server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

import io.netty.channel.ChannelHandlerContext;

public class Server {
private Iso8583Server<IsoMessage> server;
	
	private final Map<Integer, IsoMessage> receivedMessages = new ConcurrentHashMap<>();
	
	public void start() throws Exception {
		Integer port = 9080;
		
		final ServerConfiguration configuration = ServerConfiguration.newBuilder()
                .logSensitiveData(false)
                .workerThreadsCount(4)
                .build();
		
		//MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();
		
		server = new Iso8583Server<>(port, configuration, serverMessageFactory());
		
		server.addMessageListener(new IsoMessageListener<IsoMessage>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return true;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
            	System.out.println("Server onMessage event.");
                if (isoMessage.hasField(11)) {
                    final Integer stan = Integer.valueOf(isoMessage.getObjectValue(11));
                    receivedMessages.put(stan, isoMessage);
                    return true;
                }
                return true;
            }
        });
		
		server.addMessageListener(new IsoMessageListener<IsoMessage>() {

            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() == 0x200; // 0x200
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
            	System.out.println("Server onMessage event II.");
                final IsoMessage response = server.getIsoMessageFactory().createResponse(isoMessage);
                response.setField(39, IsoType.ALPHA.value("00", 2));
                response.setField(60, IsoType.LLLVAR.value("XXX", 3));
                ctx.writeAndFlush(response);
                return true;
            }
        });
		
		configureServer(server);
        server.init();
        server.start();
	}
	
	private MessageFactory<IsoMessage> serverMessageFactory() throws IOException {
        final MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }
	
	public void stop() {
		server.shutdown();
	}
	
	protected void configureServer(Iso8583Server<IsoMessage> server) {
        // to be overridden in tests
    }
}
