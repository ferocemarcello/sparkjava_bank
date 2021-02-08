package io.bankbridge.handler;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;
import util.JsonUtil;

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
	public static JSONArray getBanksRemoteJsonTwo() {
		String jsonpath = "banks-v2.json";
		JSONObject json_output = JsonUtil.getJsonFromFile(jsonpath);
		List<String> response_bodies = new ArrayList<>();
		JSONParser parser = new JSONParser();

		json_output.forEach((name,uri) -> {
			String url = (String) uri;
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest req = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.build();
			HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
			client.sendAsync(req,bh).thenApply(x->x.body()).thenAccept(x -> response_bodies.add(x)).join();
		});
		JSONArray js_arr = new JSONArray();
		response_bodies.forEach(x -> {
			try {
				js_arr.add(parser.parse(x));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
		return js_arr;
	}
	public static Map<String, Object> handleBanksVTwo_model() {
		Map<String, Object> model = new HashMap<>();
		JSONArray banks_json = getBanksRemoteJsonTwo();
		model.put("banks", banks_json);
		model.put("message", "Banks, Version 2, Remote");
		return model;
	}

	public static JSONArray handleBanksVTwo_json(Request request, Response response) {
		response.type("application/json");
		return getBanksRemoteJsonTwo();
	}
}
