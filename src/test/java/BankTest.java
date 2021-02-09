import io.bankbridge.Main;
import io.bankbridge.model.BankDao;
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

        HttpResponse response = geResponse(url);
        int statusCode = response.statusCode();
        //String body = response.body(); we might need the bod

        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);

        Map<String,Object> html_return = getHtmlContent(url);
        Set<Set<Object>> bankset_json = getBankSet_vone();
        Set<Set<Object>> bankset_html = (Set<Set<Object>>) html_return.get("bankset");

        assertEquals(statusCode, 200);
        assertEquals(version, "1");
        assertEquals(num_banks_header, Integer.toString((Integer) html_return.get("num_banks")),"20");
        assertTrue(bankset_html.equals(bankset_json));
    }

    @Test
    public void testBankVersionTwo() throws IOException, InterruptedException, ParseException {
        String url = "http://localhost:8080/v2/banks/all";

        HttpResponse response = geResponse(url);
        int statusCode = response.statusCode();
        //String body = response.body(); we might need the bod

        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);

        Map<String,Object> html_return = getHtmlContent(url);
        Set<Set<Object>> bankset_json = getBankSet_vone();
        Set<Set<Object>> bankset_html = (Set<Set<Object>>) html_return.get("bankset");

        assertEquals(statusCode, 200);
        assertEquals(version, "2");
        assertEquals(num_banks_header, Integer.toString((Integer) html_return.get("num_banks")),"20");
        assertTrue(bankset_html.equals(bankset_json));
    }

    private HttpResponse<String> geResponse(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(req, bh);

        return response;
    }

    private Set<Set<Object>> getBankSet_vone() {
        var json_banks = Main.bankDao.filterBanks(new String[]{"name","bic","countryCode","products"});
        Set<Set<Object>> bankset = new HashSet<>();

        for (var m: json_banks) {
            HashSet<Object> ob = new HashSet<>();
            m.values().forEach(val -> ob.add(val));
            bankset.add(ob);
        }
        return bankset;
    }

    private Map<String, Object> getHtmlContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        int num_banks_html = (int)doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").count();
        var result = doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() ==  "org.jsoup.nodes.Element").collect(Collectors.toList());
        Set<Set<Object>> bankset = new HashSet<>();
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
            bankset.add(values);
        }
        Map<String, Object> html_content = new HashMap<>();
        html_content.put("num_banks",num_banks_html);
        html_content.put("bankset",bankset);
        return html_content;
    }
}