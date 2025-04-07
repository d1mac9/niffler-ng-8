package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement successRegisterMessage = $(".form__paragraph_success");
    private final SelenideElement signInBtn = $(".form_sign-in");
    private final SelenideElement validationErrorUserExists = $x("//span[contains(text(), 'already exists')]");
    private final SelenideElement validationErrorPasswordsShouldBeEqual = $x("//span[contains(text(), 'Passwords should be equal')]");

    public RegisterPage setUsername(String username) {
        usernameInput
                .shouldBe(visible)
                .setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput
                .shouldBe(visible)
                .setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput
                .shouldBe(visible)
                .setValue(passwordSubmit);
        return this;
    }

    public RegisterPage submitRegistration() {
        submitBtn
                .shouldBe(visible)
                .click();
        return new RegisterPage();
    }

    public RegisterPage checkSuccessRegisterMessage(String expectedMessage) {
        successRegisterMessage
                .shouldHave(text(expectedMessage));
        return new RegisterPage();
    }

    public LoginPage clickSignInBtn() {
        signInBtn
                .shouldBe(enabled)
                .click();
        return new LoginPage();
    }

    public RegisterPage doRegister(String username, String password) {
        setUsername(username);
        setPassword(password);
        setPasswordSubmit(password);
        submitRegistration();
        return new RegisterPage();
    }

    public void checkValidationErrorUserFieldIsDisplayed(String username) {
        validationErrorUserExists
                .shouldHave(text("Username `" + username + "` already exists"));
    }

    public void checkValidationErrorPasswordFieldIsDisplayed() {
        validationErrorPasswordsShouldBeEqual.shouldBe(visible);
    }
}
