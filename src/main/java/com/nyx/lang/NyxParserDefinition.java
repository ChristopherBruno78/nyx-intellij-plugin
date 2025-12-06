package com.nyx.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.nyx.lang.parser.NyxParser;
import com.nyx.lang.psi.NyxFile;
import org.jetbrains.annotations.NotNull;

public class NyxParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(NyxLanguage.INSTANCE);

    public static final TokenSet COMMENTS = TokenSet.create(NyxTokenTypes.COMMENT);
    public static final TokenSet STRINGS = TokenSet.create(NyxTokenTypes.STRING);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new NyxLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new NyxParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        if (node.getElementType() == NyxElementTypes.CLASS_DECLARATION ||
            node.getElementType() == NyxElementTypes.FUNCTION_DECLARATION ||
            node.getElementType() == NyxElementTypes.INTERFACE_DECLARATION ||
            node.getElementType() == NyxElementTypes.ENUM_DECLARATION ||
            node.getElementType() == NyxElementTypes.VARIABLE_DECLARATION ||
            node.getElementType() == NyxElementTypes.METHOD_DECLARATION) {
            return new com.nyx.lang.psi.NyxDeclaration(node);
        }
        return new NyxPsiElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new NyxFile(viewProvider);
    }
}
