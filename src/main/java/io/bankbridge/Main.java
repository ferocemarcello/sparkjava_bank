package io.bankbridge;
import static io.bankbridge.handler.BanksCacheBased.handleBanksVOne_model;
import static spark.Spark.get;
import static spark.Spark.port;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.model.BankDao;
import spark.ModelAndView;
import spark.resource.ClassPathResource;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	public static BankDao bankDao;

	public static void main(String[] args) throws Exception {
		bankDao = new BankDao();
		
		port(8080);

		bankDao.initBanks("banks-v1.json");
		//BanksRemoteCalls.init();

		get("/v1/banks/all_json", (request, response) -> BanksCacheBased.handleBanksVOne_json(request, response));
		get("/v1/banks/all", (req, res) -> {
			Map<String, Object> model = handleBanksVOne_model();
			return new VelocityTemplateEngine().render(
					new ModelAndView(model, "velocity/banks_v1.vm")
			);
		});
		//get("/v1/banks/all", (request, response) -> BanksCacheBased.handle(request, response));
		get("/v2/banks/all", (request, response) -> BanksRemoteCalls.handleBanksVTwo(request, response));
	}
}