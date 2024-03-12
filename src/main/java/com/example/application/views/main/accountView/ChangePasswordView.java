package com.example.application.views.main.accountView;

import com.example.application.common.AuthException;
import com.example.application.common.UserAccount;
import com.example.application.service.UserService;
import com.example.application.views.main.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Route(value = "change-password", layout = MainLayout.class)
@PageTitle("Manage Account | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class ChangePasswordView extends VerticalLayout implements BeforeEnterObserver {
    @Value("${application.security.jwt.cookie.name}")
    String cookieName;
    private final WebClient webClient;
    private final UI ui;

    public ChangePasswordView(UserService userService) {
        this.webClient = WebClient.create("http://localhost:8081");
        this.ui = UI.getCurrent();
        UserAccount currentUser = userService.getUserDetails("dupa");

        addClassName("registration-form");

        // Formularz zmiany hasla
        H3 changePasswordTitle = new H3("Change Password");
        FormLayout changePasswordForm = new FormLayout();
        PasswordField passwordOldField = new PasswordField("Old Password");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField passwordConfirmField = new PasswordField("Confirm Password");
        Button updateChangePasswordButton = new Button("Update Password");
        changePasswordForm.add(passwordOldField, passwordField, passwordConfirmField, updateChangePasswordButton);
        changePasswordForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        passwordField.setRequired(true);
        passwordConfirmField.setRequired(true);

        updateChangePasswordButton.addClickListener(event -> {
            // Logika zmiany hasla
        });

        add(changePasswordTitle, changePasswordForm);
        setAlignItems(Alignment.START);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        checkJwtAndRedirect();
    }

    private void checkJwtAndRedirect() {
        UI.getCurrent().getPage().executeJs(
                        "return localStorage.getItem('"+cookieName+"')")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        authenticateTokenAndRedirect(token);
                    }else {
                        UI.getCurrent().navigate("login");
                    }
                });
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
                .bodyToMono(UserAccount.class)
                .subscribe(response -> {
                }, error -> ui.access(() -> {
                    UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"');");
                    Notification.show("Token nieprawidłowy. Zaloguj się ponownie.", 3000, Notification.Position.MIDDLE);
                }));
    }
}
