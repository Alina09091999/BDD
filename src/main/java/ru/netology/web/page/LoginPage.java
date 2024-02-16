package ru.netology.web.page;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import ru.netology.web.data.DataHelper;

public class LoginPage {

        private final SelenideElement loginField = $("[data-test-id=login] input");
        private final SelenideElement passwordField = $("[data-test-id=password] input");
        private final SelenideElement loginButton = $("[data-test-id=action-login]");

        public VerificationPage validLogin(DataHelper.AuthInfo info) {
            loginField.setValue(info.getLogin());
            passwordField.setValue(info.getPassword());
            loginButton.click();
            return new VerificationPage();
        }
    }

