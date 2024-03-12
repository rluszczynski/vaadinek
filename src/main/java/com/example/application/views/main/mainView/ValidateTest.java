package com.example.application.views.main.mainView;

import com.example.application.common.*;
import com.example.application.config.LanguageButton;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Route("valid")
@PageTitle("Login | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class ValidateTest extends VerticalLayout{


    public ValidateTest() {
        validate();
    }

    private void validate() {

        Button loginButton = new Button("Zaloguj");

        EmailField emailField = new EmailField("email");
        emailField.addClassName("registration-field");
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setPlaceholder("Wpisz swój adres e-mail");
        emailField.setErrorMessage("Proszę wprowadzić adres e-mail poprawnie");
        emailField.setClearButtonVisible(true);
        emailField.addValidationStatusChangeListener(event -> {
            if (emailField.isEmpty() || !emailField.getValue().matches(EmailValidator.PATTERN)) {
                emailField.setErrorMessage("Pole nie może być puste");
                emailField.setInvalid(true);
            }else {
                emailField.setInvalid(false);
            }
        });

        PasswordField passwordField = new PasswordField("haslo");
        passwordField.addClassName("registration-field");
        passwordField.setRequired(true);
        passwordField.setPlaceholder("Wpisz swoje hasło");
        passwordField.addValidationStatusChangeListener(event -> {
            if (passwordField.isEmpty()) {
                passwordField.setErrorMessage("Pole nie może być puste");
                passwordField.setInvalid(true);
            }else {
                passwordField.setInvalid(false);
            }
        });

        loginButton.addClickListener(e -> loginUser(emailField, passwordField));
        loginButton.addClickShortcut(com.vaadin.flow.component.Key.ENTER);

        add(new H1("Prosta strona logowania"), emailField, passwordField, loginButton);

    }

    private void loginUser(EmailField loginField, PasswordField passwordField) {
        if (validateInput(loginField, passwordField)) {
            Notification.show("Pomyślnie zalogowano!");
        } else {
            Notification.show("Login i hasło są wymagane!");
        }
    }

    private boolean validateInput(EmailField loginField, PasswordField passwordField) {
        return !loginField.getValue().trim().isEmpty() && !passwordField.getValue().trim().isEmpty();
    }
}
