package io.bankbridge;

import static spark.Spark.get;
import static spark.Spark.port;
import static util.JsonUtil.getJsonFromFile;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.model.BankDao;
import io.bankbridge.model.BankModelList;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.Map;

public class Main {
    public static BankDao bankDao;
    public static boolean Initialized = false;

    private static String getVelocityTemplate(Request req, Response res, boolean isversion_one) {
        res.type("text/html");
        Map<String, Object> model;
        if (isversion_one) {
            res.header("num_banks", String.valueOf(BankModelList.banks.size()));
            res.header("version", "1");
            model = BanksCacheBased.handleBanksVOne_model();
        } else {
            String jsonpath = "banks-v2.json";
            res.header("num_banks", String.valueOf(getJsonFromFile(jsonpath).size()));
            res.header("version", "2");
            model = BanksRemoteCalls.handleBanksVTwo_model();
        }
        return new VelocityTemplateEngine().render(
                new ModelAndView(model, "velocity/banks.vm")
        );
    }

    public static void main(String[] args) throws Exception {
        port(8080);

        if (!Main.Initialized) {
            bankDao = new BankDao();
            bankDao.initBanks("banks-v1.json");
            Initialized = true;
        }
        //BanksCacheBased.init();
        //BanksRemoteCalls.init();
        //get("/v1/banks/all", (request, response) -> BanksCacheBased.handle(request, response));

        get("/v1/banks/all_json", (request, response) -> BanksCacheBased.handleBanksVOne_json(request, response));
        get("/v1/banks/all", (req, res) -> {
            return getVelocityTemplate(req, res, true);
        });
        get("/v2/banks/all_json", (request, response) -> BanksRemoteCalls.handleBanksVTwo_json(request, response));
        get("/v2/banks/all", (req, res) -> {
            return getVelocityTemplate(req, res, false);
        });
    }
}