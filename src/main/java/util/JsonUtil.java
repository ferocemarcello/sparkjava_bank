package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.resource.ClassPathResource;

import java.io.FileReader;

public class JsonUtil {
    /**
     * @param jsonpath path of the json file inside the folder resources
     * @return the json object correspondent to the json file
     */
    public static JSONObject getJsonFromFile(String jsonpath) {
        JSONParser parser = new JSONParser();
        try {
            ClassPathResource res = new ClassPathResource(jsonpath);
            Object obj = parser.parse(new FileReader(res.getFile()));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param jsonpath path of the json file inside the folder resources
     * @return the json object correspondent to the json file. Basically we skip the first key "banks"
     */
    public static JSONArray getBanksJson(String jsonpath) {
        JSONObject books_v1_json = JsonUtil.getJsonFromFile(jsonpath);
        return (JSONArray) books_v1_json.get("banks");
    }
}
