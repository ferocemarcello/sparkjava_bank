package io.bankbridge.handler;
import java.io.File;
import java.util.*;

import io.bankbridge.Main;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.model.BankModel;
import io.bankbridge.model.BankModelList;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import spark.Route;
import util.JsonUtil;
import util.ViewUtil;

import static util.RequestUtil.clientAcceptsHtml;

public class BanksCacheBased {


	public static CacheManager cacheManager;

	public static void init() throws Exception {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, BankModel.class, ResourcePoolsBuilder.heap(20)))
				.build();
		cacheManager.init();
		Cache cache = cacheManager.getCache("banks", String.class, BankModel.class);
		try {
			BankModelList models = new ObjectMapper().readValue(
					Thread.currentThread().getContextClassLoader().getResource("banks-v1.json"), BankModelList.class);
			for (BankModel model : models.banks) {
				cache.put(model.getBic(), model);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static String handle(Request request, Response response) {
		List<BankModel> result = new ArrayList<>();
		cacheManager.getCache("banks", String.class, BankModel.class).forEach(entry -> {
			result.add(entry.getValue());
		});
		try {
			String resultAsString = new ObjectMapper().writeValueAsString(result);
			return resultAsString;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}

	}
	public static JSONArray handleBanksVOne_json(Request request, Response response) {
		response.type("application/json");
		JSONArray banks_json = JsonUtil.getBanksJson("banks-v1.json");
		Iterator<Object> iterator = banks_json.iterator();
		while(iterator.hasNext()){
			JSONObject jsonObject = (JSONObject) iterator.next();
			/*Map<String, Object> map = new HashMap<String, Object>();

			map.put("name",jsonObject.get("name"));
			map.put("bic",jsonObject.get("bic"));
			map.put("countryCode",jsonObject.get("countryCode"));
			map.put("products",jsonObject.get("products"));

			banks_json.remove(jsonObject);
			jsonObject = new JSONObject(map);
			banks_json.add(jsonObject);*/
			jsonObject.remove("auth");
		}
		return banks_json;
	}
	public static Map<String, Object> handleBanksVOne_model() {
		Map<String, Object> model = new HashMap<>();
		model.put("banks", Main.bankDao.filterBanks(new String[]{"name","bic","countryCode","products"}));
		model.put("message", "Banks, Version 1, Cached");
		return model;
	}

}
