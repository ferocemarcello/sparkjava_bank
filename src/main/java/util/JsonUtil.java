package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class JsonUtil {
    public static JSONObject getJsonFromFile(String jsonpath) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonpath));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getBanksJson(String json_name) {
        String home_dir = System.getProperty("user.dir");
        String jsonpath = home_dir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + json_name;
        JSONObject books_v1_json = JsonUtil.getJsonFromFile(jsonpath);
        return (JSONArray) books_v1_json.get("banks");
    }
}
