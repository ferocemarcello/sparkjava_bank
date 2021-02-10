package io.bankbridge.handler;

import io.bankbridge.Main;
import org.ehcache.CacheManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanksCacheBased {


    public static CacheManager cacheManager;

    /*public static void init() throws Exception {
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

    }*/

    /**
     * @param request
     * @param response
     * @return Data from version 1 (cached) as json
     */
    public static JSONArray handleBanksVOne_json(Request request, Response response) {
        response.type("application/json");
        return BanksCacheBased.getBanksJsonVersionOne();
    }

    /**
     * @return a JsonArray of the data v1, present in memory, through the bankDao. it doesn't get the parameter "auth"
     */
    public static JSONArray getBanksJsonVersionOne() {
        List<Map<String, Object>> filtered_banks = Main.bankDao.filterBanks(new String[]{"name", "bic", "countryCode", "products"});
        JSONArray jsonArray = new JSONArray();
        filtered_banks.forEach(m -> jsonArray.add(new JSONObject(m)));
        return jsonArray;
    }

    /**
     * @return model to be use in a template. the model has some data, and the list of banks v1 with all the details
     */
    public static Map<String, Object> handleBanksVOne_model() {
        Map<String, Object> model = new HashMap<>();
        model.put("banks", Main.bankDao.filterBanks(new String[]{"name", "bic", "countryCode", "products"}));
        model.put("message", "Banks, Version 1, Cached");
        model.put("title", "Banks v1");
        return model;
    }

}
