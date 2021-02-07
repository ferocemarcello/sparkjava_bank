import io.bankbridge.Main;
import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.model.BankDao;
import io.bankbridge.model.BankModelList;
import junit.framework.TestCase;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;

public class BankTest extends TestCase {
    @Before
    public void Init() {
        if(!Main.Initialized) {
            Main.bankDao.initBanks("banks-v1.json");
            Main.Initialized = true;
        }
    }
    @Test
    public void testOne(){
        int num = 0+1;
        assertTrue(num == 1);
    }
    @Test
    public void testTwo(){
        int num = 1+1;
        assertTrue(num == 2);
    }
    public void testBankVersionOne(){
        assertTrue(true);
    }
}
