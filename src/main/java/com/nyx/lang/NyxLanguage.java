package com.nyx.lang;

import com.intellij.lang.Language;

public class NyxLanguage extends Language {
    public static final NyxLanguage INSTANCE = new NyxLanguage();

    private NyxLanguage() {
        super("Nyx");
    }
}
