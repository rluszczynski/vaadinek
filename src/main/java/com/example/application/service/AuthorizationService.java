package com.example.application.service;

import com.example.application.common.AuthException;
import com.example.application.common.UserAccount;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthorizationService {

    @Value("${application.security.jwt.cookie.name}")
    String cookieName;

    private final WebClient webClient;
    UI ui;

    public AuthorizationService() {
        this.ui = UI.getCurrent();
        this.webClient = WebClient.create("http://localhost:8081");
    }

    public void checkJwtAndRedirect() {
        UI.getCurrent().getPage().executeJs(
                        "return localStorage.getItem('"+cookieName+"')")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        authenticateTokenAndRedirect(token);
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
                    ui.access(() -> {
                        Notification.show("Authenticated user: " + response.getFirstName() + " " + response.getLastName());
                        ui.navigate("account");
                    });
                }, error -> ui.access(() -> {
                    UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"');");
                    Notification.show("Token nieprawidłowy. Zaloguj się ponownie.", 3000, Notification.Position.MIDDLE);
                }));
    }
}
