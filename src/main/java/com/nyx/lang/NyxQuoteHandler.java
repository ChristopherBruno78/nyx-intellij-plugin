package com.nyx.lang;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.tree.TokenSet;

public class NyxQuoteHandler extends SimpleTokenSetQuoteHandler {
    public NyxQuoteHandler() {
        super(TokenSet.create(NyxTokenTypes.STRING));
    }
}
