package com.nyx.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NyxUtil {
    /**
     * Find all named declarations in a file
     */
    public static List<NyxNamedElement> findDeclarations(PsiFile file) {
        List<NyxNamedElement> result = new ArrayList<>();
        Collection<NyxDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, NyxDeclaration.class);
        result.addAll(declarations);
        return result;
    }

    /**
     * Find a declaration by name in a file
     */
    public static NyxNamedElement findDeclaration(PsiFile file, String name) {
        Collection<NyxDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, NyxDeclaration.class);
        for (NyxDeclaration declaration : declarations) {
            if (name.equals(declaration.getName())) {
                return declaration;
            }
        }
        return null;
    }
}
