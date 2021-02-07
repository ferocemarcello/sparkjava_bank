package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.resource.ClassPathResource;

import java.io.File;
import java.io.FileReader;

public class JsonUtil {
    public static JSONObject getJsonFromFile(String jsonpath) {
        JSONParser parser = new JSONParser();
        try {
            ClassPathResource res = new ClassPathResource(jsonpath);
            Object obj = parser.parse(new FileReader(res.getFile()));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getBanksJson(String json_name) {
        JSONObject books_v1_json = JsonUtil.getJsonFromFile(json_name);
        return (JSONArray) books_v1_json.get("banks");
    }
}
