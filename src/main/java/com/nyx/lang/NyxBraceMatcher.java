package com.nyx.lang;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NyxBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[]{
            new BracePair(NyxTokenTypes.LBRACE, NyxTokenTypes.RBRACE, true),
            new BracePair(NyxTokenTypes.LBRACKET, NyxTokenTypes.RBRACKET, false),
            new BracePair(NyxTokenTypes.LPAREN, NyxTokenTypes.RPAREN, false)
    };

    @Override
    public BracePair @NotNull [] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        // Allow braces before most tokens except other braces
        return contextType == null ||
               contextType == NyxTokenTypes.WHITE_SPACE ||
               contextType == NyxTokenTypes.COMMENT ||
               contextType == NyxTokenTypes.SEMICOLON ||
               contextType == NyxTokenTypes.COMMA ||
               contextType == NyxTokenTypes.RBRACE ||
               contextType == NyxTokenTypes.RBRACKET ||
               contextType == NyxTokenTypes.RPAREN;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
