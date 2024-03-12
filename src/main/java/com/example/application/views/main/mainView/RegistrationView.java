package com.example.application.views.main.mainView;

import com.example.application.common.*;
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

@Route("register")
@PageTitle("Register | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class RegistrationView extends VerticalLayout implements BeforeEnterObserver {

    @Value("${application.security.jwt.cookie.name}")
    String cookieName;
    private final WebClient webClient;
    private final UI ui;

    public RegistrationView(@Value("${backend.url}") String backendUrl) {
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

        H2 title = new H2(languageButton.getTranslationProvider().getTranslation("register.title.message"));
        title.addClassName("registration-title");

        changeLanguageButton.addClickListener(event -> languageButton.toggleLanguage());

        String onlyLettersPattern = "^[\\p{L} ]+$";

        TextField firstNameField = new TextField(languageButton.getTranslationProvider().getTranslation("register.fname.message"));
        firstNameField.addClassName("registration-field");
        firstNameField.setRequired(true);
        firstNameField.setValueChangeMode(ValueChangeMode.LAZY);
        firstNameField.setPlaceholder("Wpisz swoje imię");
        firstNameField.setPattern(onlyLettersPattern);
        firstNameField.setErrorMessage("Imię musi składać się z samych liter");
        firstNameField.addValidationStatusChangeListener(event -> {
            if (firstNameField.isEmpty() || !firstNameField.getValue().matches(onlyLettersPattern)) {
                firstNameField.setErrorMessage("Pole nie może być puste");
                firstNameField.setInvalid(true);
            }else {
                firstNameField.setInvalid(false);
            }
        });

        TextField lastNameField = new TextField(languageButton.getTranslationProvider().getTranslation("register.lname.message"));
        lastNameField.addClassName("registration-field");
        lastNameField.setRequired(true);
        lastNameField.setValueChangeMode(ValueChangeMode.LAZY);
        lastNameField.setPlaceholder("Wpisz swoje nazwisko");
        lastNameField.setPattern(onlyLettersPattern);
        lastNameField.setErrorMessage("Nazwisko musi składać się z samych liter");
        lastNameField.addValidationStatusChangeListener(event -> {
            if (lastNameField.isEmpty() || !lastNameField.getValue().matches(onlyLettersPattern)) {
                lastNameField.setErrorMessage("Pole nie może być puste");
                lastNameField.setInvalid(true);
            }else {
                lastNameField.setInvalid(false);
            }
        });

        TextField phoneNumberField = new TextField(languageButton.getTranslationProvider().getTranslation("register.phone.message"));
        phoneNumberField.addClassName("registration-field");
        phoneNumberField.setRequired(true);
        phoneNumberField.setValueChangeMode(ValueChangeMode.LAZY);
        phoneNumberField.setPlaceholder("Wpisz swój numer telefonu");
        String phonePattern = "\\d{9}|\\d{9}";
        phoneNumberField.setPattern(phonePattern);
        phoneNumberField.setErrorMessage("Numer telefonu musi zawierać 9 cyfr");
        phoneNumberField.addValidationStatusChangeListener(event -> {
            if (phoneNumberField.isEmpty() || !phoneNumberField.getValue().matches(phonePattern)) {
                phoneNumberField.setErrorMessage("Pole nie może być puste");
                phoneNumberField.setInvalid(true);
            }else {
                phoneNumberField.setInvalid(false);
            }
        });

        EmailField emailField = new EmailField(languageButton.getTranslationProvider().getTranslation("register.email.message"));
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

        PasswordField passwordField = new PasswordField(languageButton.getTranslationProvider().getTranslation("register.password.message"));
        passwordField.addClassName("registration-field");
        passwordField.setRequired(true);
        passwordField.setPlaceholder("Wpisz swoje hasło");
        passwordField.setPattern(".{8,}");
        passwordField.setErrorMessage("Hasło musi zawierać co najmniej 8 znaków");
        passwordField.addValidationStatusChangeListener(event -> {
            if (passwordField.isEmpty() || !passwordField.getValue().matches(".{8,}")) {
                passwordField.setErrorMessage("Pole nie może być puste");
                passwordField.setInvalid(true);
            }else {
                passwordField.setInvalid(false);
            }
        });

        Button registerButton = new Button(languageButton.getTranslationProvider().getTranslation("register.register.message"), event -> {
            if (!firstNameField.isInvalid() &&
                    !lastNameField.isInvalid() &&
                    !emailField.isInvalid() &&
                    !passwordField.isInvalid() &&
                    !phoneNumberField.isInvalid()){
                register(firstNameField, lastNameField, emailField, passwordField, phoneNumberField);
            }
            else {
                Notification.show(languageButton.getTranslationProvider().getTranslation("register.notigication.message"));
            }
        });

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClassName("registration-button");
        registerButton.addClickShortcut(Key.ENTER);

        Button googleButton = createSocialButton(languageButton.getTranslationProvider().getTranslation("register.google.message"), VaadinIcon.GOOGLE_PLUS, "google-button");
        Button facebookButton = createSocialButton(languageButton.getTranslationProvider().getTranslation("register.facebook.message"), VaadinIcon.FACEBOOK, "facebook-button");

        Button loginButton = new Button(languageButton.getTranslationProvider().getTranslation("register.login.message"), event -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        loginButton.addClassName("registration-button");

        add(changeLanguageButton, title, firstNameField, lastNameField, emailField, passwordField, phoneNumberField, registerButton, googleButton, facebookButton, loginButton);
        setAlignItems(Alignment.CENTER);
    }

    private Button createSocialButton(String text, VaadinIcon icon, String className) {
        Icon i = new Icon(icon);
        i.addClassName("social-icon");
        Button button = new Button(text, i);
        button.addClassName("social-button");
        button.addClassName(className);
        button.addThemeVariants(ButtonVariant.LUMO_LARGE);
        return button;
    }
    private void register(TextField firstNameField, TextField lastNameField, EmailField emailField, PasswordField passwordField, TextField phoneNumberField) {
        RegisterRequest build = RegisterRequest.builder()
                .firstName(firstNameField.getValue())
                .lastName(lastNameField.getValue())
                .email(emailField.getValue())
                .password(passwordField.getValue())
                .phone(phoneNumberField.getValue())
                .build();

        webClient.post()
                .uri("/register")
                .body(Mono.just(build), RegisterRequest.class)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(AuthException.class).flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(String.class)
                .subscribe(response -> {
                    ui.access(() -> {
                        Notification.show("Potwierdz maila " + response, 5000, Notification.Position.BOTTOM_START);
                        ui.navigate("login");
                    });
                }, error -> ui.access(() -> Notification.show(error.getMessage())));
    }
}
