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
import java.lang.reflect.InvocationTargetException;
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

    /**
     * done before all tests, init the banks v1
     */
    @BeforeClass
    public static void Setup() {
        BankDao.getInstance().initBanks("banks-v1.json");
    }

    /**
     * asserts all info returned from the http client are correct for banks v1
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    @Test
    public void testBankVersionOne() throws IOException, InterruptedException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = "http://localhost:8080/v1/banks/all";

        Map<String, Object> response_info = getResponseInfo(getResponse(url));//gets the response info from the url

        Map<String, Object> html_return = getHtmlContent(url);//gets the html content from the url
        var json_banks = BankDao.getInstance().filterBanks(new String[]{"name", "bic", "countryCode", "products"});

        //I get the banks as a list of banks. every bank is represented as a set, we don't care about the order.
        //I use a list because it can happen to have multiple banks with the same value
        List<Set<Object>> banklist_json = getBankCollection(ArrayList.class, json_banks);
        List<Set<Object>> banklist_html = (List<Set<Object>>) html_return.get("banklist");//I get the same list from the html content

        assertEquals(response_info.get("statuscode"), 200);//assert response has status 200
        assertEquals(response_info.get("version"), "1");//assert version is 1
        //assert the info on the header about the number of banks is correct
        assertEquals((String) response_info.get("num_banks_header"), Integer.toString((Integer) html_return.get("num_banks")), "20");
        //assert the two lists of banks have the same values
        assertTrue(banklist_html.containsAll(banklist_json) && banklist_json.containsAll(banklist_html));
    }

    /**
     * asserts all info returned from the http client are correct for banks v1
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    @Test
    public void testBankVersionTwo() throws IOException, InterruptedException, ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String url = "http://localhost:8080/v2/banks/all";

        Map<String, Object> response_info = getResponseInfo(getResponse(url));//gets the response info from the url

        Map<String, Object> html_return = getHtmlContent(url);//gets the html content from the url
        JSONArray banks_json = getBanksRemoteJsonTwo();//get the bank list v2 with remote calls

        //I get the banks as a list of banks. every bank is represented as a set, we don't care about the order.
        //I use a list because it can happen to have multiple banks with the same value
        List<Set<Object>> banklist_json = getBankCollection(ArrayList.class, banks_json);
        List<Set<Object>> banklist_html = (List<Set<Object>>) html_return.get("banklist");

        assertEquals(response_info.get("statuscode"), 200);//assert response has status 200
        assertEquals(response_info.get("version"), "2");//assert version is 2
        //assert the info on the header about the number of banks is correct
        assertEquals((String) response_info.get("num_banks_header"), Integer.toString((Integer) html_return.get("num_banks")), "20");
        //assert the two lists of banks have the same values
        assertTrue(banklist_html.containsAll(banklist_json) && banklist_json.containsAll(banklist_html));
    }

    /**
     * @param response
     * @return infos from the response. like header, status
     */
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

    /**
     * @param input      Collection of banks from which to retrieve values. every entry is a map.
     * @param <T>        Type wich extends(or implements) Collection<Set<Object>>. can be a set or a list for instance
     * @return Collection of the values of a bank. The values are packed into a set
     */
    private <T extends Collection<Set<Object>>> T getBankCollection(Class<T> cls, Collection<Map<String, Object>> input) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T collection = cls.getConstructor().newInstance();
        input.forEach(x -> collection.add(getSetMapValues(x)));
        return collection;
    }

    /**
     * @param url url of the request
     * @return response from the request to the url
     * @throws IOException
     * @throws InterruptedException
     */
    private HttpResponse<String> getResponse(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse.BodyHandler<String> bh = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(req, bh);

        return response;
    }

    /**
     * @param bankmap map of a  bank. string is key, object is value
     * @return set with the values of a bank
     */
    private Set<Object> getSetMapValues(Map<String, Object> bankmap) {
        HashSet<Object> ob = new HashSet<>();
        bankmap.values().forEach(val -> ob.add(val));
        return ob;
    }

    /**
     * @param url url of the request
     * @return content of the html page as a map
     * @throws IOException
     */
    private Map<String, Object> getHtmlContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        //the banks are into a list with id "banklist"
        //I get all the children, i.e. the banks
        int num_banks_html = (int) doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() == "org.jsoup.nodes.Element").count();
        var result = doc.select("ul[id$=banklist]").get(0).childNodes().stream().filter(n -> n.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());

        List<Set<Object>> banklist = new ArrayList<>();
        result.forEach(n -> {//I loop through all the fields (the bic, country code ...)
            List<Node> collect = n.childNodes().get(1).childNodes().stream().filter(no -> no.getClass().getName() == "org.jsoup.nodes.Element").collect(Collectors.toList());
            Set<Object> values = new HashSet<>();
            collect.forEach(c -> {
                String element = ((Element) c).text().trim();//the proper content, removing noisy spaces
                if (element.contains("[")) {//products have "[" or "]" in the text. I have to remove these chars and put the products into a list
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