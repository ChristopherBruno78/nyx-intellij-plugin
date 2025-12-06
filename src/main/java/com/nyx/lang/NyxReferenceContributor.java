package com.nyx.lang;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.nyx.lang.psi.NyxReference;
import org.jetbrains.annotations.NotNull;

public class NyxReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(NyxTokenTypes.IDENTIFIER),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                  @NotNull ProcessingContext context) {
                        String text = element.getText();
                        if (text != null && !text.isEmpty()) {
                            return new PsiReference[]{
                                    new NyxReference(element, new TextRange(0, text.length()))
                            };
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
