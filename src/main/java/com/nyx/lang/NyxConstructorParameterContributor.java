package com.nyx.lang;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.nyx.lang.psi.NyxFile;
import com.nyx.lang.psi.NyxNamedElement;
import com.nyx.lang.psi.NyxPsiUtil;
import com.nyx.lang.psi.NyxUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides constructor parameter completion when typing "new ClassName("
 */
public class NyxConstructorParameterContributor extends CompletionContributor {

    public NyxConstructorParameterContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(NyxLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                   @NotNull ProcessingContext context,
                                                   @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();

                        // Debug: Always log when completion is triggered
                        System.out.println("NYX DEBUG: Constructor completion triggered at: " + position.getText());

                        // Check if we're inside a constructor call
                        ConstructorCallContext callContext = detectConstructorCall(position);
                        if (callContext == null) {
                            System.out.println("NYX DEBUG: No constructor call context detected");
                            return;
                        }

                        System.out.println("NYX DEBUG: Found constructor call for class: " + callContext.className);

                        // Find the class being constructed
                        NyxNamedElement classDecl = findClassDeclaration(position, callContext.className);
                        if (classDecl == null) {
                            System.out.println("NYX DEBUG: Class declaration not found for: " + callContext.className);
                            return;
                        }

                        System.out.println("NYX DEBUG: Found class declaration: " + classDecl.getName());

                        // Find all constructors in the class
                        List<NyxNamedElement> constructors = findConstructorsInClass(classDecl);
                        System.out.println("NYX DEBUG: Found " + constructors.size() + " constructors");

                        // If no constructors found, add a default one
                        if (constructors.isEmpty()) {
                            System.out.println("NYX DEBUG: No constructors found, adding default");
                            LookupElementBuilder element = LookupElementBuilder.create(callContext.className)
                                    .withPresentableText("init()")
                                    .withIcon(com.intellij.icons.AllIcons.Nodes.Method)
                                    .withTypeText("default constructor");
                            result.addElement(element);
                        }

                        // Add completion items for each constructor
                        for (NyxNamedElement constructor : constructors) {
                            String params = NyxPsiUtil.extractParameters(constructor);
                            String signature = "init(" + params + ")";

                            System.out.println("NYX DEBUG: Adding constructor completion: " + signature);

                            LookupElementBuilder element = LookupElementBuilder.create(callContext.className)
                                    .withPresentableText(signature)
                                    .withIcon(com.intellij.icons.AllIcons.Nodes.Method)
                                    .withTypeText("constructor")
                                    .withInsertHandler(new NyxConstructorInsertHandler(params));

                            result.addElement(element);
                        }
                    }
                });
    }

    private ConstructorCallContext detectConstructorCall(PsiElement position) {
        // Get the text from the file up to the current position
        String fileText = position.getContainingFile().getText();
        int offset = position.getTextOffset();

        // IntelliJ may insert a dummy identifier (IntellijIdeaRulezzz or similar)
        // We need to look at the actual text before our position
        String textBeforeCursor = fileText.substring(0, Math.min(offset, fileText.length()));

        // Look for pattern: "new ClassName(" just before cursor
        // Use regex to find the last occurrence
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\($");
        java.util.regex.Matcher matcher = pattern.matcher(textBeforeCursor);

        String className = null;
        while (matcher.find()) {
            className = matcher.group(1);
        }

        if (className != null) {
            return new ConstructorCallContext(className);
        }

        // Also try to find if we're inside parentheses after "new ClassName("
        // This handles the case where user has already typed "new Button(" and cursor is inside
        pattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^)]*$");
        matcher = pattern.matcher(textBeforeCursor);

        className = null;
        while (matcher.find()) {
            className = matcher.group(1);
        }

        if (className != null) {
            return new ConstructorCallContext(className);
        }

        return null;
    }

    private NyxNamedElement findClassDeclaration(PsiElement position, String className) {
        com.intellij.psi.PsiFile file = position.getContainingFile();
        List<NyxNamedElement> declarations = NyxUtil.findDeclarations(file);
        for (NyxNamedElement decl : declarations) {
            if (className.equals(decl.getName()) &&
                decl.getNode().getElementType() == NyxElementTypes.CLASS_DECLARATION) {
                return decl;
            }
        }
        return null;
    }

    private List<NyxNamedElement> findConstructorsInClass(NyxNamedElement classDecl) {
        List<NyxNamedElement> constructors = new ArrayList<>();

        // Use PsiTreeUtil to find all constructor declarations recursively
        Collection<com.nyx.lang.psi.NyxDeclaration> allDeclarations =
            PsiTreeUtil.findChildrenOfType(classDecl, com.nyx.lang.psi.NyxDeclaration.class);

        for (com.nyx.lang.psi.NyxDeclaration decl : allDeclarations) {
            if (decl.getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
                constructors.add(decl);
            }
        }

        return constructors;
    }

    private static class ConstructorCallContext {
        final String className;

        ConstructorCallContext(String className) {
            this.className = className;
        }
    }
}
