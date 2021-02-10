package io.bankbridge.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.JsonUtil;

import java.io.File;
import java.util.*;

public class BankDao {
    public void initBanks(String json_name) {
        JSONArray banks_json_array = JsonUtil.getBanksJson(json_name);
        List<BankModel> bankList = new ArrayList<>();
        for (Object var : banks_json_array) {
            String bic = ((JSONObject) var).get("bic").toString();
            String name = ((JSONObject) var).get("name").toString();
            String countryCode = ((JSONObject) var).get("countryCode").toString();
            String auth = ((JSONObject) var).get("auth").toString();

            ArrayList<String> products = new ArrayList<String>();
            ((JSONArray) ((JSONObject) var).get("products")).forEach(product -> {
                String product_string = (String) product;
                products.add(product_string);
            });
            BankModel b = new BankModel(bic, name, countryCode, auth, products);
            bankList.add(b);
        }
        BankModelList.banks = bankList;
    }

    public List<Map<String, Object>> filterBanks(String[] fieldNames) {
        List<String> fieldNames_l = Arrays.asList(fieldNames);
        List<Map<String, Object>> filtered_banks = new ArrayList<>();
        for (BankModel b : BankModelList.banks) {
            Map<String, Object> map = b.BankToMap();
            Set<String> toRemove_set = new HashSet<>();
            map.keySet().forEach(k -> {
                if (!fieldNames_l.contains(k)) {
                    toRemove_set.add(k);
                }
            });
            map.keySet().removeAll(toRemove_set);
            filtered_banks.add(map);
        }
        return filtered_banks;
    }
}
