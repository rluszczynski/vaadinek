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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Route(value = "account", layout = MainLayout.class)
@PageTitle("Manage Account | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class AccountView extends VerticalLayout implements BeforeEnterObserver {

    @Value("${application.security.jwt.cookie.name}")
    private String cookieName;

    @Value("${backend.url}")
    private String backendUrl;
    private final WebClient webClient;
    private final UI ui;
    private String tokenJwt;
    private final UserService userService;

    public AccountView(UserService userService) {
        System.out.println("AccountView");
        this.webClient = WebClient.create("http://localhost:8081");
        this.ui = UI.getCurrent();
        this.userService = userService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        ui.getPage().executeJs("return localStorage.getItem('"+cookieName+"')")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("if true: " + token);
                        authenticateTokenAndRedirect(token);
                        this.tokenJwt = token;
                        loadUserData();
                    }else {
                        System.out.println("if false: " + token);
                        ui.navigate("login");
                    }
                });
    }

    private void authenticateTokenAndRedirect(String token) {
        System.out.println("authenticateTokenAndRedirect " + token);
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
                    System.out.println("errorui: " + tokenJwt);
                    UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"');");
                    Notification.show("Token nieprawidłowy. Zaloguj się ponownie.", 3000, Notification.Position.MIDDLE);
                }));
    }

    private void loadUserData() {
        removeAll();
        System.out.println("loadUserData " + tokenJwt);
        UserAccount currentUser = userService.getUserDetails(tokenJwt);

        addClassName("registration-form");

        H3 personalInfoTitle = new H3("Personal Information");
        FormLayout personalInfoForm = new FormLayout();
        TextField firstNameField = new TextField("First Name", currentUser.getFirstName());
        TextField lastNameField = new TextField("Last Name", currentUser.getLastName());
        TextField phoneField = new TextField("Phone Number", currentUser.getPhoneNumber());
        Button updatePersonalInfoButton = new Button("Update Personal Information");
        personalInfoForm.add(firstNameField, lastNameField, phoneField, updatePersonalInfoButton);
        personalInfoForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));


        add(personalInfoTitle, personalInfoForm);
        setAlignItems(Alignment.START);
    }
}