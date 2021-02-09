import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.Main;
import io.bankbridge.model.BankDao;
import junit.framework.TestCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BankTest{
    public static int rer=0;

    @BeforeClass
    public static void Cacca() {
        if (!Main.Initialized) {
            Main.bankDao = new BankDao();
            Main.bankDao.initBanks("banks-v1.json");
            Main.Initialized = true;
        }
    }

    @Test
    public void testBankVersionOne() throws IOException, InterruptedException, ParseException {
        String url = "http://localhost:8080/v1/banks/all";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(req, bh);
        int statusCode = response.statusCode();
        //String body = response.body(); we might need the bod
        Map<String,Object> html_return = getHtmlContent(url);
        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);
        var db_banks = Main.bankDao.filterBanks(new String[]{"name","bic","countryCode","products"});
        Set<Object> bankset = new HashSet<>();
        db_banks.forEach(x-> bankset.add((HashSet<Object>)x.values()));
        headers.allValues("date");
        assertEquals(statusCode, 200);
        assertEquals(version, "1");
        assertEquals(num_banks_header, Integer.toString((Integer) html_return.get("num_banks")),"20");
    }

    private Map<String, Object> getHtmlContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        int num_banks_html = (int)doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").count();
        var result = doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").collect(Collectors.toList());
        Set<Set<Object>> banklist = new HashSet<>();
        for (Node n: result
        ) {
            List<Node> collect = n.childNodes().get(1).childNodes().stream().filter(no -> no.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());
            Set<Object> values = new HashSet<>();
            for (Node c:collect
            ) {
                String element =((Element)c).text().trim();
                if(element.contains("[")) {
                    element = element.replaceAll("\\s+","").replaceAll("\\[|\\]","");
                    List<String> productlist = new ArrayList<>(Arrays.asList(element.split(","))).stream().collect(Collectors.toList());
                    values.add(productlist);
                }
                else {values.add(element);}
            }
            banklist.add(values);
        }
        Map<String, Object> html_content = new HashMap<>();
        html_content.put("num_banks",num_banks_html);
        html_content.put("banklist",banklist);
        return html_content;
    }
}