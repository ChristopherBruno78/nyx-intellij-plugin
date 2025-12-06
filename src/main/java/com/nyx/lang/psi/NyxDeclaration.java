package com.nyx.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import com.nyx.lang.NyxElementTypes;
import com.nyx.lang.NyxTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NyxDeclaration extends ASTWrapperPsiElement implements NyxNamedElement {
    public NyxDeclaration(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        // Constructors don't have a name identifier - they use the 'init' keyword
        if (getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
            // Find the 'init' keyword token
            ASTNode[] children = getNode().getChildren(null);
            for (ASTNode child : children) {
                if (child.getElementType() == NyxTokenTypes.KEYWORD &&
                    "init".equals(child.getText())) {
                    return child.getPsi();
                }
            }
            return null;
        }

        // Find the first identifier token for other declarations
        ASTNode[] children = getNode().getChildren(null);
        for (ASTNode child : children) {
            if (child.getElementType() == NyxTokenTypes.IDENTIFIER) {
                return child.getPsi();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getName() {
        // For constructors, return "init" as the name
        if (getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
            return "init";
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        // For now, just return this - full rename support would need more work
        return this;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getTextOffset() : super.getTextOffset();
    }
}
