package com.example.application.views.main.mainView;

import com.example.application.common.AuthException;
import com.example.application.common.AuthenticationResponse;
import com.example.application.common.ResetPasswordRequest;
import com.example.application.config.LanguageButton;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Route(value = "forgot-password")
@PageTitle("Forgot Password | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class ForgotPasswordView extends VerticalLayout {

    private final WebClient webClient;
    private final UI ui;

    public ForgotPasswordView(@Value("${backend.url}") String backendUrl) {
        this.webClient = WebClient.create(backendUrl);
        this.ui = UI.getCurrent();
        loadUserData();
    }

    private void loadUserData() {
        LanguageButton languageButton = new LanguageButton();
        Button changeLanguageButton = new Button(languageButton.getButtonLabel());

        addClassName("registration-form");
        changeLanguageButton.addClickListener(event -> languageButton.toggleLanguage());

        H2 title = new H2(languageButton.getTranslationProvider().getTranslation("forgot.title.message"));
        title.addClassName("registration-title");

        EmailField emailField = new EmailField(languageButton.getTranslationProvider().getTranslation("forgot.email.message"));
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
            } else {
                emailField.setInvalid(false);
            }
        });

        Button resetButton = new Button(languageButton.getTranslationProvider().getTranslation("forgot.reset.message"), event -> {
            if (!emailField.isInvalid()) {
                resetPassword(emailField);
                Notification.show(languageButton.getTranslationProvider().getTranslation("forgot.notigication1.message")
                        + emailField.getValue() + " "
                        + languageButton.getTranslationProvider().getTranslation("forgot.notigication2.message"));
                ui.navigate("login");
            }

        });
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resetButton.addClassName("registration-button");
        resetButton.addClickShortcut(Key.ENTER);
        Button backToLoginButton = new Button(languageButton.getTranslationProvider().getTranslation("forgot.login.message"),
                event -> {
                    ui.navigate("login");
                });

        backToLoginButton.addClassName("registration-button");

        add(changeLanguageButton, title, emailField, resetButton, backToLoginButton);
        setAlignItems(Alignment.CENTER);
    }

    private void resetPassword(EmailField emailField) {

        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .email(emailField.getValue())
                .build();

        webClient.post()
                .uri("/resetPassword")
                .body(Mono.just(resetPasswordRequest), ResetPasswordRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(AuthException.class).flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(String.class)
                .subscribe(response -> {
                }, error -> ui.access(() -> Notification.show(error.getMessage())));
    }
}
