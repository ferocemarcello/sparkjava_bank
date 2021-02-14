package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtil {
    /**
     * @param jsonpath path of the json file inside the folder resources
     * @return the json object correspondent to the json file
     */
    public static JSONObject getJsonFromFile(String jsonpath) {
        JSONParser parser = new JSONParser();
        InputStream is = JsonUtil.class.getClassLoader().getResourceAsStream(jsonpath);
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(
                    new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
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
