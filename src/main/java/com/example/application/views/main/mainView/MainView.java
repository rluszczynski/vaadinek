package com.example.application.views.main.mainView;

import com.example.application.config.LanguageButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@CssImport("./styles/registration-view-styles.css")
public class MainView extends VerticalLayout {

    private final LanguageButton languageButton = new LanguageButton();
    Button changeLanguageButton = new Button(languageButton.getButtonLabel());

    public MainView() {
        changeLanguageButton.addClickListener(event -> languageButton.toggleLanguage());

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 header = new H1(languageButton.getTranslationProvider().getTranslation("main.title.message"));
        add(header);

        HorizontalLayout buttonLayout1 = new HorizontalLayout();
        buttonLayout1.setJustifyContentMode(JustifyContentMode.CENTER);
        HorizontalLayout buttonLayout2 = new HorizontalLayout();
        buttonLayout2.setJustifyContentMode(JustifyContentMode.CENTER);

        Button loginButton = new Button(languageButton.getTranslationProvider().getTranslation("main.loginButton.message"));
        Button registerButton = new Button(languageButton.getTranslationProvider().getTranslation("main.registerButton.message"));
        Button forgotPasswordButton = new Button(languageButton.getTranslationProvider().getTranslation("main.forgotPasswordButton.message"));
        Button accountButton = new Button(languageButton.getTranslationProvider().getTranslation("main.accountButton.message"));
        Button changePassword = new Button(languageButton.getTranslationProvider().getTranslation("main.changePassword.message"));
        Button button6 = new Button(languageButton.getTranslationProvider().getTranslation("main.button6.message"));

        setSquareButtonStyle(loginButton);
        setSquareButtonStyle(registerButton);
        setSquareButtonStyle(forgotPasswordButton);
        setSquareButtonStyle(accountButton);
        setSquareButtonStyle(changePassword);
        setSquareButtonStyle(button6);

        loginButton.addClickListener(e -> UI.getCurrent().navigate("login"));
        registerButton.addClickListener(e -> UI.getCurrent().navigate("register"));
        forgotPasswordButton.addClickListener(e -> UI.getCurrent().navigate("forgot-password"));
        accountButton.addClickListener(e -> UI.getCurrent().navigate("account"));
        changePassword.addClickListener(e -> UI.getCurrent().navigate("change-password"));

        buttonLayout1.add(loginButton, registerButton, forgotPasswordButton);
        buttonLayout2.add(accountButton, changePassword, button6);

        add(changeLanguageButton, buttonLayout1, buttonLayout2);
    }
    private void setSquareButtonStyle(Button button) {
        button.getStyle().set("margin", "10px");
        button.getStyle().set("width", "150px");
        button.getStyle().set("height", "150px");
    }
}