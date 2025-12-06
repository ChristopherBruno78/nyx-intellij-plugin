package com.nyx.lang;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NyxLexer extends LexerBase {
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentOffset;
    private IElementType currentTokenType;
    private int currentTokenEnd;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentOffset = startOffset;
        this.currentTokenEnd = startOffset;
        advance();
    }

    @Override
    public int getState() {
        return 0;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return currentOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentTokenEnd;
    }

    @Override
    public void advance() {
        if (currentTokenEnd >= endOffset) {
            currentTokenType = null;
            return;
        }

        currentOffset = currentTokenEnd;

        if (currentOffset >= endOffset) {
            currentTokenType = null;
            return;
        }

        char c = buffer.charAt(currentOffset);

        // Skip whitespace
        if (Character.isWhitespace(c)) {
            int pos = currentOffset;
            while (pos < endOffset && Character.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            currentTokenEnd = pos;
            currentTokenType = NyxTokenTypes.WHITE_SPACE;
            return;
        }

        // Comments
        if (c == '/' && currentOffset + 1 < endOffset) {
            char next = buffer.charAt(currentOffset + 1);
            if (next == '/') {
                // Line comment
                int pos = currentOffset + 2;
                while (pos < endOffset && buffer.charAt(pos) != '\n') {
                    pos++;
                }
                currentTokenEnd = pos;
                currentTokenType = NyxTokenTypes.COMMENT;
                return;
            } else if (next == '*') {
                // Block comment
                int pos = currentOffset + 2;
                while (pos < endOffset - 1) {
                    if (buffer.charAt(pos) == '*' && buffer.charAt(pos + 1) == '/') {
                        pos += 2;
                        break;
                    }
                    pos++;
                }
                currentTokenEnd = Math.min(pos, endOffset);
                currentTokenType = NyxTokenTypes.COMMENT;
                return;
            }
        }

        // Strings
        if (c == '"' || c == '\'' || c == '`') {
            char quote = c;
            int pos = currentOffset + 1;
            while (pos < endOffset) {
                char ch = buffer.charAt(pos);
                if (ch == quote && buffer.charAt(pos - 1) != '\\') {
                    pos++;
                    break;
                }
                pos++;
            }
            currentTokenEnd = pos;
            currentTokenType = NyxTokenTypes.STRING;
            return;
        }

        // Numbers
        if (Character.isDigit(c)) {
            int pos = currentOffset;
            while (pos < endOffset && (Character.isDigit(buffer.charAt(pos)) || buffer.charAt(pos) == '.')) {
                pos++;
            }
            currentTokenEnd = pos;
            currentTokenType = NyxTokenTypes.NUMBER;
            return;
        }

        // Identifiers and keywords
        if (Character.isJavaIdentifierStart(c)) {
            int pos = currentOffset;
            while (pos < endOffset && Character.isJavaIdentifierPart(buffer.charAt(pos))) {
                pos++;
            }
            currentTokenEnd = pos;
            String text = buffer.subSequence(currentOffset, currentTokenEnd).toString();

            // Check if it's a keyword
            if (isKeyword(text)) {
                currentTokenType = NyxTokenTypes.KEYWORD;
            } else {
                currentTokenType = NyxTokenTypes.IDENTIFIER;
            }
            return;
        }

        // Braces, brackets, and parentheses
        switch (c) {
            case '{':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.LBRACE;
                return;
            case '}':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.RBRACE;
                return;
            case '[':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.LBRACKET;
                return;
            case ']':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.RBRACKET;
                return;
            case '(':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.LPAREN;
                return;
            case ')':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.RPAREN;
                return;
            case ';':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.SEMICOLON;
                return;
            case ',':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.COMMA;
                return;
            case '.':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.DOT;
                return;
            case ':':
                currentTokenEnd = currentOffset + 1;
                currentTokenType = NyxTokenTypes.COLON;
                return;
        }

        // Operators
        if (isOperatorChar(c)) {
            int pos = currentOffset;
            while (pos < endOffset && isOperatorChar(buffer.charAt(pos))) {
                pos++;
            }
            currentTokenEnd = pos;
            currentTokenType = NyxTokenTypes.OPERATOR;
            return;
        }

        // Default: single character
        currentTokenEnd = currentOffset + 1;
        currentTokenType = NyxTokenTypes.BAD_CHARACTER;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }

    private boolean isKeyword(String text) {
        return text.equals("func") || text.equals("init") || text.equals("enum") ||
               text.equals("prop") || text.equals("type") || text.equals("interface") ||
               text.equals("readonly") || text.equals("as") || text.equals("global") ||
               // JavaScript keywords
               text.equals("var") || text.equals("let") || text.equals("const") ||
               text.equals("function") || text.equals("class") || text.equals("extends") ||
               text.equals("implements") || text.equals("return") || text.equals("if") ||
               text.equals("else") || text.equals("for") || text.equals("while") ||
               text.equals("do") || text.equals("switch") || text.equals("case") ||
               text.equals("break") || text.equals("continue") || text.equals("default") ||
               text.equals("try") || text.equals("catch") || text.equals("finally") ||
               text.equals("throw") || text.equals("new") || text.equals("this") ||
               text.equals("super") || text.equals("import") || text.equals("export") ||
               text.equals("from") || text.equals("async") || text.equals("await") ||
               text.equals("yield") || text.equals("static") || text.equals("public") ||
               text.equals("private") || text.equals("protected") || text.equals("get") ||
               text.equals("set") || text.equals("typeof") || text.equals("instanceof") ||
               text.equals("void") || text.equals("delete") || text.equals("in") ||
               text.equals("of") || text.equals("null") || text.equals("undefined") ||
               text.equals("true") || text.equals("false");
    }

    private boolean isOperatorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' ||
               c == '=' || c == '!' || c == '<' || c == '>' || c == '&' ||
               c == '|' || c == '^' || c == '~' || c == '?';
    }
}
