package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.condition.model.Bubble;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990,
          currency = CurrencyValues.RUB
      )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .editSpending("Обучение Advanced 2.0")
        .editDescription(newDescription)
        .save();

    new MainPage().checkThatTableContainsSpending(newDescription);
  }

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990,
          currency = CurrencyValues.RUB
      )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .getStatComponent()
        .checkStatisticImage(expected)
        .checkBubbles(new Bubble(Color.yellow, "Обучение 79990 ₽"));
  }

  @User(
      categories = {
          @Category(name = "Поездки"),
          @Category(name = "Ремонт", archived = true),
          @Category(name = "Страховка", archived = true)
      },
      spendings = {
          @Spend(
              category = "Поездки",
              description = "В Москву",
              amount = 9500,
              currency = CurrencyValues.RUB
          ),
          @Spend(
              category = "Ремонт",
              description = "Цемент",
              amount = 100,
              currency = CurrencyValues.RUB
          ),
          @Spend(
              category = "Страховка",
              description = "ОСАГО",
              amount = 3000,
              currency = CurrencyValues.RUB
          )
      }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
  void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .getStatComponent()
        .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
        .checkStatisticImage(expected)
        .checkBubblesInAnyOrder(
                new Bubble(Color.yellow, "Поездки 9500 ₽"),
                new Bubble(Color.green, "Archived 3100 ₽")
        );
  }

    @User(
            spendings = {
                    @Spend(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Обучение 2.1",
                            description = "Обучение Advanced 2.1",
                            amount = 89990,
                            currency = CurrencyValues.RUB
                    )
            }
    )
    @Test
  void checkSpendingRow(UserJson user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkSpendingByFields(user.testData().spendings().toArray(SpendJson[]::new));
  }
}

