package ru.netology.web.page;

public class DashboardPage {
    private final String balanceStart="баланс: ";
    private final String balanceFinish= " р.";
    private final SelenideElement heading = $("[data-test-id=dashboard]");

        public DashboardPage() {
            heading.shouldBe(visible);
        }
}
