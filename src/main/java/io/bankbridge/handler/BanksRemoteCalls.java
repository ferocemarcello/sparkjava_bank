package io.bankbridge.handler;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	public static JSONArray handleBanksVTwo(Request request, Response response) {
		JSONObject jsonob = null;
		response.type("application/json");
		String home_dir = System.getProperty("user.dir");
		String jsonpath = home_dir+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"banks-v2.json";
		JSONObject json_output = JsonUtil.getJsonFromFile(jsonpath);
		List<String> response_bodies = new ArrayList<>();
		JSONParser parser = new JSONParser();
		for (Object o:json_output.entrySet().toArray()) {
			Map.Entry<String, String> map = (Map.Entry<String, String>) o;
			String name = map.getKey();
			String url = map.getValue();
			url = "http://localhost:1234/"+url.split("/")[url.split("/").length-1];
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest req = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.build();
			HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
			client.sendAsync(req,bh).thenApply(x->x.body()).thenAccept(x -> response_bodies.add(x)).join();
		}
		JSONArray js_arr = new JSONArray();
		for (String b: response_bodies) {
			try {
				jsonob = (JSONObject) parser.parse(b);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			js_arr.add(jsonob);
		}
		return js_arr;
	}

}
