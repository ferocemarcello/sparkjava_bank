package io.bankbridge.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BankModel {
	
	private String bic;
	private String name;
	private String countryCode;
	private String auth;
	private ArrayList<String> products;

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

	public ArrayList<String> getProducts() {
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

	public Map<String,Object> BankToMap () {
		Map<String,Object> map = new HashMap<>();
		map.put("name",this.getName());
		map.put("bic",this.getBic());
		map.put("countryCode",this.getCountryCode());
		map.put("products",this.getProducts());
		map.put("auth",this.getAuth());
		return map;
	}
}
