package com.example.application.views.main.mainView;

import com.example.application.common.AuthException;
import com.example.application.common.AuthenticationResponse;
import com.example.application.common.LoginRequest;
import com.example.application.config.LanguageButton;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Route("login")
@PageTitle("Login | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    @Value("${application.security.jwt.cookie.name}")
    private String cookieName;
    private final WebClient webClient;
    private final UI ui;

    @Autowired
    public LoginView(@Value("${backend.url}") String backendUrl) {
        this.webClient = WebClient.create(backendUrl);
        this.ui = UI.getCurrent();
    }
    private static String localStorageAccessor = "return localStorage.getItem('%s')";

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        ui.getPage().executeJs(String.format(localStorageAccessor, cookieName))
                .then(String.class, this::processToken);
    }
    private void processToken(String token) {
        if (token != null && !token.isBlank()) {
            authenticateTokenAndRedirect(token);
        } else {
            loadUserData();
        }
    }
    private void authenticateTokenAndRedirect(String token) {
        webClient.post()
                .uri("/authorization")
                .header("Authorization", token)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(AuthException.class).flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(AuthenticationResponse.class)
                .subscribe(response -> {
                    ui.access(() -> {
                        ui.navigate("account");
                    });
                }, error -> ui.access(() -> {
                    UI.getCurrent().getPage().executeJs("localStorage.removeItem('" + cookieName + "');");
                    Notification.show("Token nieaktualny. Zaloguj się ponownie.", 5000, Notification.Position.MIDDLE);
                    loadUserData();
                }));
    }

    private void loadUserData() {
        addClassName("registration-form");

        LanguageButton languageButton = new LanguageButton();
        Button changeLanguageButton = new Button(languageButton.getButtonLabel());

        H2 title = new H2(languageButton.getTranslationProvider().getTranslation("login.title.message"));
        title.addClassName("registration-title");

        changeLanguageButton.addClickListener(event -> languageButton.toggleLanguage());
        EmailField emailField = new EmailField(languageButton.getTranslationProvider().getTranslation("login.email.message"));
        emailField.addClassName("registration-field");
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setPlaceholder("Wpisz swój adres e-mail");
        emailField.setErrorMessage("Proszę wprowadzić adres e-mail poprawnie");
        emailField.setClearButtonVisible(true);
        emailField.addValidationStatusChangeListener(event -> {
            if (!emailField.getValue().trim().isEmpty() && !emailField.getValue().matches(EmailValidator.PATTERN)) {
                emailField.setErrorMessage("Pole nie może być puste");
                emailField.setInvalid(true);
            }else {
                emailField.setInvalid(false);
            }
        });


        PasswordField passwordField = new PasswordField(languageButton.getTranslationProvider().getTranslation("login.password.message"));
        passwordField.addClassName("registration-field");
        passwordField.setRequired(true);
        passwordField.setPlaceholder("Wpisz swoje hasło");
        passwordField.addValidationStatusChangeListener(event -> {
            if (passwordField.isEmpty() || passwordField.getValue().trim().isEmpty() || passwordField.getValue().length() < 8) {
                passwordField.setErrorMessage("Pole nie może być puste");
                passwordField.setInvalid(true);
            }else {
                passwordField.setInvalid(false);
            }
        });

        Button loginButton = new Button(languageButton.getTranslationProvider().getTranslation("login.login.message"));

        loginButton.addClickListener(event -> {
            if (!emailField.getValue().trim().isEmpty() &&
                    !passwordField.getValue().trim().isEmpty() &&
                    !emailField.isInvalid() &&
                    !passwordField.isInvalid()){
                login(emailField, passwordField);
            } else {
                Notification.show("Wprowadź poprawne dane", 5000, Notification.Position.MIDDLE);
            }
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClassName("registration-button");
        loginButton.addClickShortcut(com.vaadin.flow.component.Key.ENTER);

        Button forgotPasswordButton = new Button(languageButton.getTranslationProvider().getTranslation("login.forget.message"), event -> {
            getUI().ifPresent(ui -> ui.navigate("forgot-password"));
        });
        forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        forgotPasswordButton.addClassName("forgot-password-button");


        Button googleButton = createSocialButton(languageButton.getTranslationProvider().getTranslation("login.google.message"), VaadinIcon.GOOGLE_PLUS, "google-button");
        Button facebookButton = createSocialButton(languageButton.getTranslationProvider().getTranslation("login.facebook.message"), VaadinIcon.FACEBOOK, "facebook-button");

        Button registerButton = new Button(languageButton.getTranslationProvider().getTranslation("login.register.message"), event -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        registerButton.addClassName("registration-button");

        add(changeLanguageButton, title, emailField, passwordField, loginButton, forgotPasswordButton, googleButton, facebookButton, registerButton);
        setAlignItems(Alignment.CENTER);

    }

    private Button createSocialButton(String text, VaadinIcon icon, String className) {
        Icon i = new Icon(icon);
        i.addClassName("social-icon");
        Button button = new Button(text, i);
        button.addClassName("social-button");
        button.addClassName(className);
        return button;
    }

    private void login(EmailField emailField, PasswordField passwordField) {
        LoginRequest build = LoginRequest.builder()
                .email(emailField.getValue())
                .password(passwordField.getValue())
                .build();

        webClient.post()
                .uri("/login")
                .body(Mono.just(build), LoginRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(AuthException.class).flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(AuthenticationResponse.class)
                .subscribe(response -> {
                    ui.access(() -> {
                        saveTokenInBrowser(response.getAccessToken());
                        ui.navigate("account");
                    });
                }, error -> ui.access(() -> Notification.show(error.getMessage())));
    }

    private void saveTokenInBrowser(String jwt) {
        UI.getCurrent().getPage().executeJs("localStorage.setItem('" + cookieName + "', $0)", jwt);
    }
}