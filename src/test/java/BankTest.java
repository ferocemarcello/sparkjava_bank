import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.Main;
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
import org.junit.Test;

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

public class BankTest extends TestCase {
    @Before
    public void Init() {
        if (!Main.Initialized) {
            Main.bankDao.initBanks("banks-v1.json");
            Main.Initialized = true;
        }
    }

    @Test
    public void testOne() {
        int num = 0 + 1;
        assertTrue(num == 1);
    }

    @Test
    public void testTwo() {
        int num = 1 + 1;
        assertTrue(num == 2);
    }

    public void testBankVersionOne() throws IOException, InterruptedException, ParseException {
        String url = "http://localhost:8080/v1/banks/all";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(req, bh);
        int statusCode = response.statusCode();
        String body = response.body();

        Document doc = Jsoup.connect("http://localhost:8080/v1/banks/all").get();
        int num_banks_html = (int)doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").count();
        var result = doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").collect(Collectors.toList());
        Map<String,Object> bankmap = new HashMap<>();
        for (Node n: result
        ) {
            String key = n.childNodes().get(0).toString().trim();
            List<Node> collect = n.childNodes().get(1).childNodes().stream().filter(no -> no.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());
            List<Object> values = new ArrayList<>();
            for (Node c:collect
                 ) {
                String element =((Element)c).text().trim();
                if(!element.equals(key)) {
                    if(element.contains("[")) {
                        List<String> productlist = new ArrayList<>(Arrays.asList(element.split(","))).stream().collect(Collectors.toList());
                        productlist.set(0,productlist.get(0).replace("[",""));
                        productlist.set(productlist.size()-1,productlist.get(productlist.size()-1).replace("]",""));
                        values.add(productlist);
                    }
                    else {values.add(element);}
                }
            }
            /*collect.forEach(c-> {if(((Element)c).text() != key) {
                if(((Element)c).text().contains("[")) {
                    List<String> productlist = new ArrayList<>(Arrays.asList(((Element)c).text().split(",")));
                    values.add(productlist);
                }
                else {values.add(((Element)c).text());}
            }
            });*/

            //(List<Element>) (n.childNodes().get(1).childNodes().stream().filter(no -> no.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList()));
            bankmap.put(n.toString(),((Element) n).text());
        }
        var cacca = result.get(0);
        var pipi = cacca.childNodes().get(1).childNodes().stream().filter(n -> n.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());
        for (Node n: pipi
             ) {
            bankmap.put(n.toString(),((Element) n).text());
        }
        pipi.forEach(n -> {bankmap.put(n.toString(),"");});
        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);
        headers.allValues("date");
        assertEquals(statusCode, 200);
        assertEquals(version, "1");
        assertEquals(num_banks_header, Integer.toString(num_banks_html),"20");
    }
}
