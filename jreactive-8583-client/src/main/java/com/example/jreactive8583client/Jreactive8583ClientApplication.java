package com.example.jreactive8583client;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

@SpringBootApplication
public class Jreactive8583ClientApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Jreactive8583ClientApplication.class, args);
		
		Client client = new Client();
		
		client.start();
		
		client.sendData();
		
		client.stop();
	}
}
