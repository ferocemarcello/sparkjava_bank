import io.bankbridge.Main;
import io.bankbridge.model.BankDao;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import static io.bankbridge.handler.BanksRemoteCalls.getBanksRemoteJsonTwo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BankTest {
    public static int rer = 0;

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

        Map<String, Object> response_info = getResponseInfo(getResponse(url));

        Map<String, Object> html_return = getHtmlContent(url);
        var json_banks = Main.bankDao.filterBanks(new String[]{"name", "bic", "countryCode", "products"});

        List<Set<Object>> banklist_json = getBankCollection(new ArrayList<>(), json_banks);
        List<Set<Object>> banklist_html = (List<Set<Object>>) html_return.get("banklist");

        assertEquals(response_info.get("statuscode"), 200);
        assertEquals(response_info.get("version"), "1");
        assertEquals((String) response_info.get("num_banks_header"), Integer.toString((Integer) html_return.get("num_banks")), "20");
        assertTrue(banklist_html.containsAll(banklist_json) && banklist_json.containsAll(banklist_html));
    }

    @Test
    public void testBankVersionTwo() throws IOException, InterruptedException, ParseException {
        String url = "http://localhost:8080/v2/banks/all";

        Map<String, Object> response_info = getResponseInfo(getResponse(url));

        Map<String, Object> html_return = getHtmlContent(url);
        JSONArray banks_json = getBanksRemoteJsonTwo();

        List<Set<Object>> banklist_json = getBankCollection(new ArrayList<>(), banks_json);
        List<Set<Object>> banklist_html = (List<Set<Object>>) html_return.get("banklist");

        assertEquals(response_info.get("statuscode"), 200);
        assertEquals(response_info.get("version"), "2");
        assertEquals((String) response_info.get("num_banks_header"), Integer.toString((Integer) html_return.get("num_banks")), "20");
        assertTrue(banklist_html.containsAll(banklist_json) && banklist_json.containsAll(banklist_html));
    }

    private Map<String, Object> getResponseInfo(HttpResponse<String> response) {
        Map<String, Object> response_info = new HashMap<>();
        int statusCode = response.statusCode();
        //String body = response.body(); we might need the bod

        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);

        response_info.put("statuscode", statusCode);
        response_info.put("version", version);
        response_info.put("num_banks_header", num_banks_header);

        return response_info;
    }

    private <T extends Collection<Set<Object>>> T getBankCollection(T toadd, Collection<Map<String, Object>> input) {
        input.forEach(x -> toadd.add(getSetMapValues(x)));
        return toadd;
    }

    private HttpResponse<String> getResponse(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(req, bh);

        return response;
    }

    private Set<Object> getSetMapValues(Map<String, Object> m) {
        HashSet<Object> ob = new HashSet<>();
        m.values().forEach(val -> ob.add(val));
        return ob;
    }

    private Map<String, Object> getHtmlContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        int num_banks_html = (int) doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() == "org.jsoup.nodes.Element").count();
        var result = doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());

        List<Set<Object>> banklist = new ArrayList<>();
        result.forEach(n -> {
            List<Node> collect = n.childNodes().get(1).childNodes().stream().filter(no -> no.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());
            Set<Object> values = new HashSet<>();
            collect.forEach(c -> {
                String element = ((Element) c).text().trim();
                if (element.contains("[")) {
                    element = element.replaceAll("\\s+", "").replaceAll("\\[|\\]", "");
                    List<String> productlist = new ArrayList<>(Arrays.asList(element.split(","))).stream().collect(Collectors.toList());
                    values.add(productlist);
                } else {
                    values.add(element);
                }
            });
            banklist.add(values);
        });

        Map<String, Object> html_content = new HashMap<>();
        html_content.put("num_banks", num_banks_html);
        html_content.put("banklist", banklist);
        return html_content;
    }
}