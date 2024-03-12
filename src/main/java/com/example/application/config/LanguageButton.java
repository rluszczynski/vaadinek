package com.example.application.config;

import com.example.application.TranslationProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LanguageButton {
    public String getButtonLabel() {
        // Pobranie aktualnego Locale
        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        // Zwrócenie etykiety przycisku w zależności od aktualnego Locale
        return Locale.ENGLISH.equals(currentLocale) ? "PL" : "EN";
    }

    public void toggleLanguage() {
        // Pobranie aktualnego Locale
        Locale currentLocale = VaadinSession.getCurrent().getLocale();
        // Zmiana Locale na przeciwny język
        Locale newLocale = Locale.ENGLISH.equals(currentLocale) ? new Locale("pl", "PL") : Locale.ENGLISH;
        VaadinSession.getCurrent().setLocale(newLocale);
        // Odświeżenie UI, aby zastosować zmianę języka
        UI.getCurrent().getPage().reload();
    }

    public Locale getCurrentLocale() {
        return VaadinSession.getCurrent().getLocale();
    }

    public TranslationProvider getTranslationProvider() {
        return new TranslationProvider(getCurrentLocale());
    }
}
