package com.example.application.views.main.layout;

import com.example.application.config.LanguageButton;
import com.example.application.views.main.accountView.AccountView;
import com.example.application.views.main.accountView.AddressInfoView;
import com.example.application.views.main.accountView.ChangePasswordView;
import com.example.application.views.main.accountView.InvoiceInfoView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

import static io.netty.handler.codec.http.HttpHeaders.getHeader;
@CssImport("./styles/navi-styles.css")
public class MainLayout extends AppLayout {

    @Value("${application.security.jwt.cookie.name}")
    String cookieName;
    private final LanguageButton languageButton = new LanguageButton();
    Button changeLanguageButton = new Button(languageButton.getButtonLabel());
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Moje Dane");
        logo.addClassName("logo");
        addToNavbar(false, logo);
    }

    private void createDrawer() {
        changeLanguageButton.addClickListener(event -> languageButton.toggleLanguage());
        Nav nav = new Nav();
        nav.addClassNames("menu-navigation");

        RouterLink myDataLink = createRouterLink("Moje Dane", AccountView.class);
        RouterLink myAddressLink = createRouterLink("Address", AddressInfoView.class);
        RouterLink myInvoiceLink = createRouterLink("Invoice", InvoiceInfoView.class);
        RouterLink changePasswordLink = createRouterLink("Zmiana hasła", ChangePasswordView.class);
        Button logoutButton = new Button("Wyloguj", event -> logout());

        nav.add(myDataLink, myAddressLink, myInvoiceLink, changePasswordLink, logoutButton, changeLanguageButton);

        VerticalLayout drawerLayout = new VerticalLayout();
        drawerLayout.setSizeUndefined();
        drawerLayout.addClassName("drawer-layout");
        drawerLayout.add(nav);
        addToDrawer(drawerLayout);

        setDrawerOpened(true);
        getElement().setAttribute("style", "overflow-x: hidden;");
    }

    private RouterLink createRouterLink(String text, Class<? extends Component> navigationTarget) {
        RouterLink routerLink = new RouterLink(text, navigationTarget);
        routerLink.setHighlightCondition(HighlightConditions.sameLocation());
        return routerLink;
    }

    private void logout() {
        UI.getCurrent().getPage().executeJs("localStorage.removeItem('"+cookieName+"'); sessionStorage.removeItem('"+cookieName+"');");
        VaadinServletRequest.getCurrent().getHttpServletRequest().getSession().invalidate();
    }
}
