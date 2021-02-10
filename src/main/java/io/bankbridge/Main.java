package io.bankbridge;

import static spark.Spark.get;
import static spark.Spark.port;
import static util.JsonUtil.getJsonFromFile;
import static util.RequestUtil.clientAcceptsHtml;

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
    /**
     * instance of the bankDao
     */
    public static BankDao bankDao;
    public static boolean Initialized = false;

    /**
     * @param req
     * @param res
     * @param isversion_one true if I want to get and send content of version 1. false if version 2
     * @return
     */
    private static String getVelocityTemplate(Request req, Response res, boolean isversion_one) {
        if (clientAcceptsHtml(req)) res.type("text/html");
        Map<String, Object> model;
        if (isversion_one) {
            //I put some info into the header
            res.header("num_banks", String.valueOf(BankModelList.banks.size()));//I put the number of expected banks
            res.header("version", "1");//I put the version number
            model = BanksCacheBased.handleBanksVOne_model();//I get the model(it is a map) for the version 1
        } else {
            //I put some info into the header
            String jsonpath = "banks-v2.json";
            res.header("num_banks", String.valueOf(getJsonFromFile(jsonpath).size()));//I put the number of expected banks
            res.header("version", "2");//I put the version number
            model = BanksRemoteCalls.handleBanksVTwo_model();//I get the model(it is a map) for the version 2
        }
        return new VelocityTemplateEngine().render(
                new ModelAndView(model, "velocity/banks.vm")//I return the html content through the velocity render and the template "banks.vm"
        );
    }

    public static void main(String[] args) throws Exception {
        port(8080);

        /**
         * It is supposed to initialize the banklist only once. The banklist might get initialized by other classes
         */
        if (!Main.Initialized) {
            bankDao = new BankDao();
            bankDao.initBanks("banks-v1.json");
            Initialized = true;
        }
        //BanksCacheBased.init();
        //BanksRemoteCalls.init();
        //get("/v1/banks/all", (request, response) -> BanksCacheBased.handle(request, response));

        //with this route, I return the v1 banks as json
        get("/v1/banks/all_json", (request, response) -> {
            return BanksCacheBased.handleBanksVOne_json(request, response);
        });
        //with this route, I return the v1 banks as html content
        get("/v1/banks/all", (req, res) -> {
            return getVelocityTemplate(req, res, true);//I build the html content through this method
        });
        //with this route, I return the v1 banks as json
        get("/v2/banks/all_json", (request, response) -> {
            return BanksRemoteCalls.handleBanksVTwo_json(request, response);
        });
        //with this route, I return the v1 banks as html content
        get("/v2/banks/all", (req, res) -> {
            return getVelocityTemplate(req, res, false);//I build the html content through this method
        });
    }
}