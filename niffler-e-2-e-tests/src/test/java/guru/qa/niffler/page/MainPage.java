package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDifResult;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement profileBtn = $("[data-testid='PersonIcon']");
    private final SelenideElement profileLink = $(By.linkText("Profile"));
    private final ElementsCollection menuItems = $$("ul[role='menu'] li");
    private final SelenideElement searchField = $("input[placeholder='Search']");
    private final SelenideElement statDiagram = $("canvas[role='img']");
    private final ElementsCollection statCategories = $$("#legend-container li");
    private final SelenideElement deleteButton = $("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");


    public EditSpendingPage editSpending(String spendingDescription) {
        findSpending(spendingDescription);
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public void checkMainPageShouldBeLoaded() {
        checkStatisticsShouldBeLoaded();
        checkHistoryOfSpendingShouldBeLoaded();
    }

    public void checkStatisticsShouldBeLoaded() {
        $$("h2")
                .get(0)
                .shouldHave(text("Statistics"));
    }

    public void checkHistoryOfSpendingShouldBeLoaded() {
        $$("h2")
                .get(1)
                .shouldHave(text("History of Spendings"));
    }

    public ProfilePage goToProfilePage() {
        profileBtn
                .click();
        profileLink
                .click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsTab() {
        profileBtn
                .click();
        menuItems
                .findBy(text("Friends"))
                .click();
        return new FriendsPage();
    }

    public AllPeoplePage goToAllPeopleTab() {
        profileBtn
                .click();
        menuItems
                .findBy(text("All People"))
                .click();
        return new AllPeoplePage();
    }

    public void findSpending(String spendingDescription) {
        searchField
                .setValue(spendingDescription)
                .pressEnter();
    }

    public MainPage verifyStatDiagram(BufferedImage expected) {
        Selenide.sleep(3000);
        BufferedImage actual;
        try {
            actual = ImageIO.read(Objects.requireNonNull(statDiagram.screenshot()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertFalse(new ScreenDifResult(
                expected,
                actual
        ));
        return this;
    }

    public MainPage shouldHaveStatSpendings(List<String> expectedCategories) {
        statCategories.shouldHave(CollectionCondition.texts(expectedCategories));
        return this;
    }

    public void selectSpendingCheckbox(String categoryName){
        $$("#spendings tbody tr")
                .findBy(text(categoryName))
                .$("input[type='checkbox']")
                .click();
    }

    public MainPage deleteSelectedSpending(String categoryName) {
        selectSpendingCheckbox(categoryName);
        deleteButton
                .click();
        dialogWindow.$(byText("Delete"))
                .click();
        return this;
    }

}
