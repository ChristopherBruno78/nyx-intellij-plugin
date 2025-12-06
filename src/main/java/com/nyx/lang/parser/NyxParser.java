package com.nyx.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.nyx.lang.NyxElementTypes;
import com.nyx.lang.NyxTokenTypes;
import org.jetbrains.annotations.NotNull;

public class NyxParser implements PsiParser {
    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();

        while (!builder.eof()) {
            parseTopLevel(builder);
        }

        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private void parseTopLevel(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == null) {
            return;
        }

        String tokenText = builder.getTokenText();

        // Parse declarations
        if (tokenType == NyxTokenTypes.KEYWORD && tokenText != null) {
            switch (tokenText) {
                case "class":
                    parseClassDeclaration(builder);
                    return;
                case "function":
                    parseFunctionDeclaration(builder);
                    return;
                case "interface":
                    parseInterfaceDeclaration(builder);
                    return;
                case "enum":
                    parseEnumDeclaration(builder);
                    return;
                case "const":
                case "let":
                case "var":
                    parseVariableDeclaration(builder);
                    return;
            }
        }

        builder.advanceLexer();
    }

    private void parseClassDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'class'

        // Expect identifier (class name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Parse class body with methods
        parseClassBody(builder);

        marker.done(NyxElementTypes.CLASS_DECLARATION);
    }

    private void parseClassBody(PsiBuilder builder) {
        int braceCount = 0;
        boolean foundOpenBrace = false;

        while (!builder.eof()) {
            String text = builder.getTokenText();
            IElementType tokenType = builder.getTokenType();

            if (text != null) {
                if (text.equals("{")) {
                    braceCount++;
                    foundOpenBrace = true;
                    builder.advanceLexer();
                    continue;
                } else if (text.equals("}")) {
                    braceCount--;
                    if (foundOpenBrace && braceCount == 0) {
                        builder.advanceLexer(); // consume the closing brace
                        break;
                    }
                    builder.advanceLexer();
                    continue;
                }
            }

            // Inside the class body, parse methods
            if (foundOpenBrace && braceCount == 1) {
                if (tokenType == NyxTokenTypes.KEYWORD && text != null && text.equals("func")) {
                    parseMethodDeclaration(builder);
                    continue;
                }
            }

            builder.advanceLexer();
        }
    }

    private void parseMethodDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'func'

        // Expect identifier (method name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Skip everything until we find the matching closing brace or EOF
        skipToClosingBrace(builder);

        marker.done(NyxElementTypes.METHOD_DECLARATION);
    }

    private void parseFunctionDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'function'

        // Expect identifier (function name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Skip everything until we find the matching closing brace or EOF
        skipToClosingBrace(builder);

        marker.done(NyxElementTypes.FUNCTION_DECLARATION);
    }

    private void parseInterfaceDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'interface'

        // Expect identifier (interface name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Skip everything until we find the matching closing brace or EOF
        skipToClosingBrace(builder);

        marker.done(NyxElementTypes.INTERFACE_DECLARATION);
    }

    private void parseEnumDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'enum'

        // Expect identifier (enum name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Skip everything until we find the matching closing brace or EOF
        skipToClosingBrace(builder);

        marker.done(NyxElementTypes.ENUM_DECLARATION);
    }

    private void parseVariableDeclaration(PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); // 'const', 'let', or 'var'

        // Expect identifier (variable name)
        if (builder.getTokenType() == NyxTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }

        // Skip until semicolon, newline, or EOF
        while (!builder.eof()) {
            String text = builder.getTokenText();
            if (text != null && text.equals(";")) {
                builder.advanceLexer();
                break;
            }
            if (builder.getTokenType() == NyxTokenTypes.KEYWORD) {
                // Don't consume the next keyword
                break;
            }
            builder.advanceLexer();
        }

        marker.done(NyxElementTypes.VARIABLE_DECLARATION);
    }

    private void skipToClosingBrace(PsiBuilder builder) {
        int braceCount = 0;
        boolean foundOpenBrace = false;

        while (!builder.eof()) {
            String text = builder.getTokenText();
            if (text != null) {
                if (text.equals("{")) {
                    braceCount++;
                    foundOpenBrace = true;
                } else if (text.equals("}")) {
                    braceCount--;
                    if (foundOpenBrace && braceCount == 0) {
                        builder.advanceLexer(); // consume the closing brace
                        break;
                    }
                }
            }
            builder.advanceLexer();
        }
    }
}
