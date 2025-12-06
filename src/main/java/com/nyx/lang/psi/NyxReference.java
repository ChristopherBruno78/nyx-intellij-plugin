package com.nyx.lang.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NyxReference extends PsiReferenceBase<PsiElement> {
    private final String key;

    public NyxReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return NyxUtil.findDeclaration(myElement.getContainingFile(), key);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return NyxUtil.findDeclarations(myElement.getContainingFile()).toArray();
    }
}
