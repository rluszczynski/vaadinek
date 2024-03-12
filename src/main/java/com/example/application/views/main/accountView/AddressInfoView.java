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

@Route(value = "addressView", layout = MainLayout.class)
@PageTitle("Address Information | MyApplication")
@CssImport("./styles/registration-view-styles.css")
public class AddressInfoView extends VerticalLayout implements BeforeEnterObserver {
    @Value("${application.security.jwt.cookie.name}")
    String cookieName;
    private final WebClient webClient;
    private final UI ui;

    public AddressInfoView(UserService userService) {
        this.webClient = WebClient.create("http://localhost:8081");
        this.ui = UI.getCurrent();
        addClassName("registration-form");

        UserAccount currentUser = userService.getUserDetails("tokenGet()");

        H3 addressTitle = new H3("Address Information");
        FormLayout addressForm = new FormLayout();
        TextField addressField = new TextField("Address", currentUser.getAddress());
        TextField cityField = new TextField("City", currentUser.getCity());
        TextField zipCodeField = new TextField("Zip Code", currentUser.getZipCode());
        Button updateAddressButton = new Button("Update Address");
        addressForm.add(addressField, cityField, zipCodeField, updateAddressButton);
        addressForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        add(addressTitle, addressForm);
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
