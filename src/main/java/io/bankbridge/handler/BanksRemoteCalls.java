package io.bankbridge.handler;
import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.Main;
import io.bankbridge.model.BankDao;
import io.bankbridge.model.BankModelList;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;

import static util.JsonUtil.getJsonFromFile;

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
	public static JSONObject handleBanksVOne(Request request, Response response) {
		response.type("application/json");
		Object bml = Main.bankDao.filterBanks(new String[]{"name", "bic", "countryCode", "product"});
		return (JSONObject) BankModelList.banks;
	}
	public static JSONObject handleBanksVTwo(Request request, Response response) {
		response.type("application/json");
		String home_dir = System.getProperty("user.dir");
		String jsonpath = home_dir+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"banks-v2.json";
		JSONObject json_output = getJsonFromFile(jsonpath);
		return json_output;
	}

}
