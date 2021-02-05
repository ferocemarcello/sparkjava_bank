package io.bankbridge.handler;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

public class BanksRemoteCalls {

	private static Map config;

	public static void init() throws Exception {
		config = new ObjectMapper()
				.readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), Map.class);
	}

	public static String handle(Request request, Response response) {
		System.out.println(config);
		throw new RuntimeException("Not implemented");
	}

}
