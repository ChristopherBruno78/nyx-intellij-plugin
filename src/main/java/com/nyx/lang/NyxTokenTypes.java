package com.nyx.lang;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

public interface NyxTokenTypes {
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

    IElementType COMMENT = new NyxTokenType("COMMENT");
    IElementType STRING = new NyxTokenType("STRING");
    IElementType NUMBER = new NyxTokenType("NUMBER");
    IElementType KEYWORD = new NyxTokenType("KEYWORD");
    IElementType IDENTIFIER = new NyxTokenType("IDENTIFIER");
    IElementType OPERATOR = new NyxTokenType("OPERATOR");
}
