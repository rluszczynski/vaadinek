package com.example.application.views.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Value;

@Route("token")
public class TokenView extends VerticalLayout {

    @Value("${application.security.jwt.cookie.name}")
    String cookieName;

    String tokenJwt;

    private final UI ui;


    public TokenView() {
        this.ui = UI.getCurrent();
        Button getTokenButton = new Button("Pobierz Token JWT", event -> getTokenFromBrowserAndSendToServer());
        add(getTokenButton);
        Button logoutButton = new Button("Wyloguj", event -> logout());
        add(logoutButton);
    }

    private void getTokenFromBrowserAndSendToServer() {
        ui.getPage().executeJs("return localStorage.getItem('"+cookieName+"')")
                .then(String.class, token -> {
                    Notification.show("Odebrany token JWT: " + token);
                    this.tokenJwt = token;
                    //sendRequestWithToken(token);
                });
    }

    private void sendRequestWithToken(String jwtToken) {
        System.out.println(jwtToken);
    }
    private void logout() {
        UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"'); sessionStorage.removeItem('"+cookieName+"');");
        //VaadinServletRequest.getCurrent().getHttpServletRequest().getSession().invalidate();
        //UI.getCurrent().navigate(""); // Przekierowanie do strony głównej lub strony logowania
    }
}
