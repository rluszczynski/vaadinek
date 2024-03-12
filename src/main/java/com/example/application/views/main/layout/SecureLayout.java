package com.example.application.views.main.layout;

import com.example.application.common.AuthException;
import com.example.application.common.UserAccount;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@ParentLayout(MainLayout.class)
public class SecureLayout extends Div implements RouterLayout, BeforeEnterObserver {

    @Value("${application.security.jwt.cookie.name}")
    private String cookieName;
    private final WebClient webClient;
    private final UI ui;

    public SecureLayout() {
        this.webClient = WebClient.create("http://localhost:8081");
        this.ui = UI.getCurrent();
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        ui.getPage().executeJs("return localStorage.getItem('"+cookieName+"')")
                .then(String.class, token -> {
                    System.out.println("Token2: " + token);
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token3: " + token);
                        authenticateTokenAndRedirect(token);
                    }
                    //else {
//                        ui.navigate("login");
//                    }
                });
    }
    private void authenticateTokenAndRedirect(String token) {
        System.out.println("Token4: " + token);

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
                }
                , error -> ui.access(() -> {
                    UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"');");
                    Notification.show("Token nieprawidłowy. Zaloguj się ponownie.", 3000, Notification.Position.MIDDLE);
                }));


    }

}
