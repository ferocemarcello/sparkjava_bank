package io.bankbridge.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;
import util.JsonUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class BanksRemoteCalls {

    /*private static Map config;

    public static void init() throws Exception {
        config = new ObjectMapper()
                .readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), Map.class);
    }

    public static String handle(Request request, Response response) {
        System.out.println(config);
        throw new RuntimeException("Not implemented");
    }*/

    /**
     * @return Data from version 2 (remote) as json
     */
    public static JSONArray getBanksRemoteJsonTwo() {
        String jsonpath = "banks-v2.json";
        JSONObject json_output = JsonUtil.getJsonFromFile(jsonpath);//get json from the file
        List<String> response_bodies = new ArrayList<>();//list of bodies of the responses
        JSONParser parser = new JSONParser();

        HttpClient client = HttpClient.newHttpClient();//here I instantiate the http client

        json_output.forEach((name, uri) -> {//for each url of the json file, I use a client to retrieve do a request to that url
            // and to put the response into a list of strings
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create((String) uri))
                    .build();
            HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
            client.sendAsync(req, bh).thenApply(x -> x.body()).thenAccept(x -> response_bodies.add(x)).join();//i use the async method. I don't care about the order
        });
        JSONArray js_arr = new JSONArray();
        response_bodies.forEach(x -> {//for each response, I put the response into a json array, wich is then returned
            try {
                js_arr.add(parser.parse(x));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return js_arr;
    }

    /**
     * @return model to be use in a template. the model has some data, and the list of banks v2 with all the details
     */
    public static Map<String, Object> handleBanksVTwo_model() {
        Map<String, Object> model = new HashMap<>();
        JSONArray banks_json = getBanksRemoteJsonTwo();
        model.put("banks", banks_json);
        model.put("message", "Banks, Version 2, Remote");
        model.put("title", "Banks v2");
        return model;
    }

    /**
     * @param request
     * @param response
     * @return Data from version 2 (remote) as json
     */
    public static JSONArray handleBanksVTwo_json(Request request, Response response) {
        response.type("application/json");
        return getBanksRemoteJsonTwo();
    }
}
