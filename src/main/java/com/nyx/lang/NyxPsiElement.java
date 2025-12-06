package com.nyx.lang;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class NyxPsiElement extends ASTWrapperPsiElement {
    public NyxPsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
