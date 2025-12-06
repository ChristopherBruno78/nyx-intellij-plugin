package com.nyx.lang;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.nyx.lang.psi.NyxNamedElement;
import com.nyx.lang.psi.NyxPsiUtil;
import com.nyx.lang.psi.NyxUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NyxCompletionContributor extends CompletionContributor {

    private static final String[] NYX_KEYWORDS = {
        // Nyx-specific keywords
        "func", "init", "enum", "prop", "type", "interface", "readonly", "as",

        // JavaScript/TypeScript keywords
        "var", "let", "const", "function", "class", "extends", "implements",
        "return", "if", "else", "for", "while", "do", "switch", "case",
        "break", "continue", "default", "try", "catch", "finally", "throw",
        "new", "this", "super", "import", "export", "from", "async", "await",
        "yield", "static", "public", "private", "protected", "get", "set",
        "typeof", "instanceof", "void", "delete", "in", "of",
        "null", "undefined", "true", "false"
    };

    private static final String[] NYX_TYPES = {
        "string", "number", "boolean", "void", "any", "unknown", "never",
        "object", "symbol", "bigint"
    };

    public NyxCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(NyxLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                   @NotNull ProcessingContext context,
                                                   @NotNull CompletionResultSet result) {
                        // Add keywords
                        for (String keyword : NYX_KEYWORDS) {
                            result.addElement(LookupElementBuilder.create(keyword)
                                    .bold()
                                    .withTypeText("keyword"));
                        }

                        // Add type keywords
                        for (String type : NYX_TYPES) {
                            result.addElement(LookupElementBuilder.create(type)
                                    .withTypeText("type"));
                        }

                        // Add common snippets
                        result.addElement(LookupElementBuilder.create("func ")
                                .withPresentableText("func method")
                                .withInsertHandler((insertContext, item) -> {
                                    insertContext.getDocument().insertString(
                                            insertContext.getTailOffset(),
                                            "methodName(param: type): returnType {\n\t\n}"
                                    );
                                })
                                .withTypeText("snippet")
                                .bold());

                        result.addElement(LookupElementBuilder.create("init")
                                .withPresentableText("init constructor")
                                .withInsertHandler((insertContext, item) -> {
                                    insertContext.getDocument().insertString(
                                            insertContext.getTailOffset(),
                                            "(param: type) {\n\t\n}"
                                    );
                                })
                                .withTypeText("snippet")
                                .bold());

                        result.addElement(LookupElementBuilder.create("class ")
                                .withPresentableText("class declaration")
                                .withInsertHandler((insertContext, item) -> {
                                    insertContext.getDocument().insertString(
                                            insertContext.getTailOffset(),
                                            "ClassName {\n\t\n}"
                                    );
                                })
                                .withTypeText("snippet")
                                .bold());

                        result.addElement(LookupElementBuilder.create("interface ")
                                .withPresentableText("interface declaration")
                                .withInsertHandler((insertContext, item) -> {
                                    insertContext.getDocument().insertString(
                                            insertContext.getTailOffset(),
                                            "InterfaceName {\n\t\n}"
                                    );
                                })
                                .withTypeText("snippet")
                                .bold());

                        result.addElement(LookupElementBuilder.create("enum ")
                                .withPresentableText("enum declaration")
                                .withInsertHandler((insertContext, item) -> {
                                    insertContext.getDocument().insertString(
                                            insertContext.getTailOffset(),
                                            "EnumName {\n\t\n}"
                                    );
                                })
                                .withTypeText("snippet")
                                .bold());

                        // Add identifiers from declarations in the file
                        PsiElement position = parameters.getPosition();
                        List<NyxNamedElement> declarations = NyxUtil.findDeclarations(position.getContainingFile());
                        for (NyxNamedElement declaration : declarations) {
                            String name = declaration.getName();
                            if (name != null && !name.isEmpty()) {
                                String typeText = getDeclarationType(declaration);

                                // For functions and methods, show the signature with parameters
                                if (isFunctionOrMethod(declaration)) {
                                    String signature = NyxPsiUtil.extractSignature(declaration);
                                    String params = NyxPsiUtil.extractParameters(declaration);

                                    result.addElement(LookupElementBuilder.create(name)
                                            .withPresentableText(signature)
                                            .withIcon(com.intellij.icons.AllIcons.Nodes.Function)
                                            .withTypeText(typeText)
                                            .withInsertHandler(new NyxInsertHandler(params)));
                                } else {
                                    // For classes, variables, etc., show just the name
                                    result.addElement(LookupElementBuilder.create(name)
                                            .withIcon(getIconForType(declaration))
                                            .withTypeText(typeText));
                                }
                            }
                        }
                    }
                });
    }

    private String getDeclarationType(NyxNamedElement element) {
        if (element.getNode().getElementType() == NyxElementTypes.CLASS_DECLARATION) {
            return "class";
        } else if (element.getNode().getElementType() == NyxElementTypes.FUNCTION_DECLARATION) {
            return "function";
        } else if (element.getNode().getElementType() == NyxElementTypes.INTERFACE_DECLARATION) {
            return "interface";
        } else if (element.getNode().getElementType() == NyxElementTypes.ENUM_DECLARATION) {
            return "enum";
        } else if (element.getNode().getElementType() == NyxElementTypes.VARIABLE_DECLARATION) {
            return "variable";
        } else if (element.getNode().getElementType() == NyxElementTypes.METHOD_DECLARATION) {
            return "method";
        }
        return "symbol";
    }

    private boolean isFunctionOrMethod(NyxNamedElement element) {
        return element.getNode().getElementType() == NyxElementTypes.FUNCTION_DECLARATION ||
               element.getNode().getElementType() == NyxElementTypes.METHOD_DECLARATION;
    }

    private javax.swing.Icon getIconForType(NyxNamedElement element) {
        if (element.getNode().getElementType() == NyxElementTypes.CLASS_DECLARATION) {
            return com.intellij.icons.AllIcons.Nodes.Class;
        } else if (element.getNode().getElementType() == NyxElementTypes.INTERFACE_DECLARATION) {
            return com.intellij.icons.AllIcons.Nodes.Interface;
        } else if (element.getNode().getElementType() == NyxElementTypes.ENUM_DECLARATION) {
            return com.intellij.icons.AllIcons.Nodes.Enum;
        } else if (element.getNode().getElementType() == NyxElementTypes.VARIABLE_DECLARATION) {
            return com.intellij.icons.AllIcons.Nodes.Variable;
        }
        return com.intellij.icons.AllIcons.Nodes.Variable;
    }
}
