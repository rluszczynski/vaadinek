package com.example.application;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationProvider {

    private static final String BUNDLE_PREFIX = "messages";
    private final Locale locale;

    public TranslationProvider(Locale locale) {
        this.locale = locale;
    }

    public String getTranslation(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        return bundle.getString(key);
    }
}
