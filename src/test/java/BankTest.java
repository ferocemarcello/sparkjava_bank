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
import org.jsoup.nodes.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
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
        final int[] num_banks_html = {0};
        doc.select("ul[id$=banklist]").get(0).childNodes().forEach(n->{if (n.getClass().getName() == "org.jsoup.nodes.Element") {
            num_banks_html[0]++;
        }});
        HttpHeaders headers = response.headers();
        String version = headers.allValues("version").get(0);
        String num_banks_header = headers.allValues("num_banks").get(0);
        headers.allValues("date");
        assertEquals(statusCode, 200);
        assertEquals(version, "1");
        assertEquals(num_banks_header, Integer.toString(num_banks_html[0]),"20");
    }
}
