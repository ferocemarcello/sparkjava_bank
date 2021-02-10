package io.bankbridge.model;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BankModel {

    private String bic;
    private String name;
    private String countryCode;
    private String auth;
    private List<String> products;

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    public BankModel(String bic, String name, String countryCode, String auth, ArrayList<String> products) {
        this.bic = bic;
        this.name = name;
        this.countryCode = countryCode;
        this.auth = auth;
        this.products = products;
    }

    /**
     * converts a bankmodel object to a map
     *
     * @return map of key string, equals the field name, and Object value, the value of the field
     */
    public Map<String, Object> BankToMap() {
        Map<String, Object> map = new HashMap<>();
        //here I get the list of fields name from the class
        Arrays.asList(BankModel.class.getDeclaredFields()).stream().map(x -> x.getName()).collect(Collectors.toList()).forEach(field_name -> {
            //then for every field name, I use the getter to get the value. I put field name and velue into the map
            try {
                String methodName = "get" + field_name.substring(0, 1).toUpperCase() + field_name.substring(1);
                var method = BankModel.class.getDeclaredMethod(methodName);
                var getterresult = method.invoke(this);
                map.put(field_name, getterresult);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return map;
    }
}
