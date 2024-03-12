package com.example.application;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class MyI18NProvider implements I18NProvider {

    private static final String BUNDLE_NAME = "messages";
    private static final Locale LOCALE_PL = new Locale("pl", "PL");
    private static final Locale LOCALE_EN = Locale.ENGLISH;
    private final ResourceBundle bundlePL;
    private final ResourceBundle bundleEN;

    public MyI18NProvider() throws IOException {
        this.bundlePL = loadResourceBundle(LOCALE_PL);
        this.bundleEN = loadResourceBundle(LOCALE_EN);
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(LOCALE_EN, LOCALE_PL);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        ResourceBundle bundle = LOCALE_PL.equals(locale) ? bundlePL : bundleEN;
        return bundle.getString(key);
    }

    private ResourceBundle loadResourceBundle(Locale locale) throws IOException {
        String bundlePath = BUNDLE_NAME + "_" + locale.getLanguage() + ".properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(bundlePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(reader);
        }
    }
}
