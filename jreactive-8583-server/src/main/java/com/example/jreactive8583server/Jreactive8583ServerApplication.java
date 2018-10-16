package com.example.jreactive8583server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Jreactive8583ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Jreactive8583ServerApplication.class, args);
		
		System.out.println("App started.");
		
		Server server = new Server();
		
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
