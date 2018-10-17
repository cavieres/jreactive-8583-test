package com.example.jreactive8583client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jreactive8583ClientApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Jreactive8583ClientApplication.class, args);
		
		Client client = new Client();
		
		client.start();
		
		client.sendData();
		
		//client.stop();
	}
}
