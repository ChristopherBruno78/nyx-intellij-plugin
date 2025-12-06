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

    // Braces, brackets, and parentheses
    IElementType LBRACE = new NyxTokenType("LBRACE");
    IElementType RBRACE = new NyxTokenType("RBRACE");
    IElementType LBRACKET = new NyxTokenType("LBRACKET");
    IElementType RBRACKET = new NyxTokenType("RBRACKET");
    IElementType LPAREN = new NyxTokenType("LPAREN");
    IElementType RPAREN = new NyxTokenType("RPAREN");

    // Common punctuation
    IElementType SEMICOLON = new NyxTokenType("SEMICOLON");
    IElementType COMMA = new NyxTokenType("COMMA");
    IElementType DOT = new NyxTokenType("DOT");
    IElementType COLON = new NyxTokenType("COLON");
}
