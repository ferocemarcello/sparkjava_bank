package io.bankbridge.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BankDao {
    public void initBanks(String json_name) {
        String home_dir = System.getProperty("user.dir");
        String jsonpath = home_dir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + json_name;
        JSONObject books_v1_json = JsonUtil.getJsonFromFile(jsonpath);
        JSONArray banks_json_array = (JSONArray) books_v1_json.get("banks");
        List<BankModel> bankList = new ArrayList<BankModel>();
        for (Object var : banks_json_array)
        {
            String bic = ((JSONObject) var).get("bic").toString();
            String name = ((JSONObject) var).get("name").toString();
            String countryCode = ((JSONObject) var).get("countryCode").toString();
            String auth = ((JSONObject) var).get("auth").toString();

            ArrayList<String> products = new ArrayList<String>();
            ((JSONArray) ((JSONObject) var).get("products")).forEach(product -> {
                String product_string = (String) product;
                products.add(product_string);
            });
            BankModel b = new BankModel(bic,name,countryCode,auth,products);
            bankList.add(b);
        }
        BankModelList.banks = bankList;
    }
    public Object[] filterBanks(String[] fieldNames) {
        List<String> fieldNames_l = Arrays.asList(fieldNames);
        List<BankModel> banks = BankModelList.banks;
        List<Object> filtered_banks = new ArrayList<Object>();
        banks.forEach(bankModel -> {
            ObjectMapper oMapper = new ObjectMapper();
            // object -> Map
            Map<String, Object> map = oMapper.convertValue(bankModel, Map.class);
            map.keySet().forEach(f -> {
                if(!fieldNames_l.contains(f)) {
                    map.remove(f);
                }
            });
            filtered_banks.add(map);
        });
        return filtered_banks.toArray();
    }
}
