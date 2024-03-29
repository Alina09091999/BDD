import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import web.data.DataHelper;
import web.page.DashboardPage;
import web.page.LoginPage;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static web.data.DataHelper.*;

public class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setup(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo=DataHelper.getAuthInfo();
        var verificationPage=loginPage.validLogin(authInfo);
        var verificationCode=DataHelper.getVerificationCode();
        dashboardPage=verificationPage.validVerify(verificationCode);
        firstCardInfo=getFirstCardInfo();
        secondCardInfo=getSecondCardInfo();
        firstCardBalance=dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance=dashboardPage.getCardBalance(secondCardInfo);
    }


    @Test
    void shouldTransferFromFirstToSecond(){
        var amount=generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard=firstCardBalance-amount;
        var expectedBalanceSecondCard=secondCardBalance+amount;
        var transferPage=dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage=transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard=dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard=dashboardPage.getCardBalance(secondCardInfo);
        assertAll(() -> assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));

    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance(){
        var amount=generateInvalidAmount(secondCardBalance);
        var transferPage=dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage. makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard=dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard=dashboardPage.getCardBalance(secondCardInfo);
        assertAll(() ->transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания"),
                () -> assertEquals(firstCardBalance, actualBalanceFirstCard),
                () -> assertEquals(secondCardBalance, actualBalanceSecondCard));
    }
}
