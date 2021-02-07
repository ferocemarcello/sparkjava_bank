package io.bankbridge;
import static spark.Spark.get;
import static spark.Spark.port;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.model.BankDao;

public class Main {
	public static BankDao bankDao;

	public static void main(String[] args) throws Exception {
		bankDao = new BankDao();
		
		port(8080);

		bankDao.initBanks("banks-v1.json");
		BanksRemoteCalls.init();

		get("/v1/banks/all", (request, response) -> BanksRemoteCalls.handleBanksVOne(request, response));
		//get("/v1/banks/all", (request, response) -> BanksCacheBased.handle(request, response));
		get("/v2/banks/all", (request, response) -> BanksRemoteCalls.handleBanksVTwo(request, response));
	}
}