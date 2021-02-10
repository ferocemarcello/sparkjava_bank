package io.bankbridge.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.JsonUtil;

import java.util.*;

public class BankDao {
    /**
     * with this method, I put the data which is in the json v1, into a list of objects "BankModel"
     *
     * @param json_name is the string of the json file name, inside the resources folder
     */
    public void initBanks(String json_name) {
        JSONArray banks_json_array = JsonUtil.getBanksJson(json_name);
        List<BankModel> bankList = new ArrayList<>();
        for (Object var : banks_json_array) {//I need to loop through all the fields in the json
            String bic = ((JSONObject) var).get("bic").toString();
            String name = ((JSONObject) var).get("name").toString();
            String countryCode = ((JSONObject) var).get("countryCode").toString();
            String auth = ((JSONObject) var).get("auth").toString();

            ArrayList<String> products = new ArrayList<>();
            ((JSONArray) ((JSONObject) var).get("products")).forEach(product -> {
                String product_string = (String) product;
                products.add(product_string);
            });
            BankModel b = new BankModel(bic, name, countryCode, auth, products);//I create then an object of class BankModel
            bankList.add(b);//I add the object a list of BankModel
        }
        BankModelList.banks = bankList; // the list is assigned to the one in BankModelList
    }

    /**
     * @param fieldNames the fields of the class BankModel that have to be kept
     * @return List of mapping. It filters some fields from the list of banks. every mapping has the field name as key, its value as value
     */
    public List<Map<String, Object>> filterBanks(String[] fieldNames) {
        List<String> fieldNames_l = Arrays.asList(fieldNames);
        List<Map<String, Object>> filtered_banks = new ArrayList<>();
        for (BankModel b : BankModelList.banks) {
            Map<String, Object> map = b.BankToMap();//here I convert a bank object to a map. the map is then filtered
            Set<String> toRemove_set = new HashSet<>();
            map.keySet().forEach(k -> {
                if (!fieldNames_l.contains(k)) {
                    toRemove_set.add(k);
                }
            });
            map.keySet().removeAll(toRemove_set);//I remove the keys which are not equals to one of the elements of the input array
            filtered_banks.add(map);
        }
        return filtered_banks;
    }
}
