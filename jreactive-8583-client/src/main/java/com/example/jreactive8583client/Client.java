package com.example.jreactive8583client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.ClientConfiguration;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;

import io.netty.channel.ChannelHandlerContext;

public class Client {
	private Iso8583Client<IsoMessage> client;
	
	private final Map<Integer, IsoMessage> receivedMessages = new ConcurrentHashMap<>();
	
	public void start() throws Exception {
		String host = "127.0.0.1";
		Integer port = 9080;
		//String host = "tgr-qa.multicaja.cl";
		//Integer port = 7100;
		int idleTimeout = 3;
		
		SocketAddress socketAddress = new InetSocketAddress(host, port);

        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .idleTimeout(idleTimeout)
                .logSensitiveData(false)
                .workerThreadsCount(4)
                .build();
		
		//MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();
		client = new Iso8583Client<>(socketAddress, configuration, clientMessageFactory());
		
		client.addMessageListener(new IsoMessageListener<IsoMessage>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return true;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
            	System.out.println("Client onMessage event.");
                if (isoMessage.hasField(11)) {
                    final Integer stan = Integer.valueOf(isoMessage.getObjectValue(11));
                    receivedMessages.put(stan, isoMessage);
                    return true;
                }
                return false;
            }
        });
		
		configureClient(client);
        client.init();
        client.connect();
        
        if (client.isConnected()) {
        	System.out.println("Client connected.");
        } else {
        	System.out.println("Client not connected.");
        }
	}
	
	private MessageFactory<IsoMessage> clientMessageFactory() throws IOException {
        final MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        messageFactory.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System
                .currentTimeMillis() % 1000000)));
        return messageFactory;
    }
	
	public void stop() {
		client.shutdown();
	}
	
	public void sendData() {
		// given
        final IsoMessage finMessage = client.getIsoMessageFactory().newMessage(0x1200); // 0x0200
        
        // finMessage.setField(60, IsoType.LLLVAR.value("foo", 3));
        // finMessage.setField(4, IsoType.NUMERIC.value(153456, 12));
        
        finMessage.setField(3, IsoType.NUMERIC.value(500000, 6));
        //finMessage.setField(4, IsoType.NUMERIC.value(000000000000, 12));
        
        final Integer stan = finMessage.getObjectValue(11);
        // when
        //client.sendAsync(finMessage);
        try {
			client.send(finMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void configureClient(Iso8583Client<IsoMessage> client) {
        // to be overridden in tests
    }
}
