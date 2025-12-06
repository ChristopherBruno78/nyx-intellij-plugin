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
        "func", "init", "enum", "prop", "type", "interface", "readonly", "as", "global",

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

                        // First, check if we're in a constructor call context
                        PsiElement position = parameters.getPosition();

                        // Use the original document text, not the PSI text (which has IntelliJ's dummy identifier)
                        String originalText = parameters.getEditor().getDocument().getText();
                        int offset = parameters.getOffset();

                        System.out.println("=== NYX DEBUG ===");
                        System.out.println("Position text: " + position.getText());
                        System.out.println("Position type: " + position.getNode().getElementType());
                        System.out.println("Original offset: " + offset);

                        String textBeforeCursor = originalText.substring(0, Math.min(offset, originalText.length()));
                        System.out.println("Text before cursor (last 100 chars): " +
                            textBeforeCursor.substring(Math.max(0, textBeforeCursor.length() - 100)));

                        // Try to detect "new ClassName(" pattern
                        // Need to handle both cases:
                        // 1. "new Button(" - user typed opening paren
                        // 2. "new Button()" - auto-close added closing paren, cursor is between them
                        // We only look at text BEFORE cursor, so just find the last "new ClassName("
                        java.util.regex.Pattern constructorPattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\($");
                        java.util.regex.Matcher matcher = constructorPattern.matcher(textBeforeCursor);

                        String constructorClassName = null;
                        while (matcher.find()) {
                            constructorClassName = matcher.group(1);
                        }

                        // Also check if we're inside parentheses: "new Button(|)" where | is cursor
                        if (constructorClassName == null) {
                            // Try pattern with content inside parens
                            constructorPattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^)]*$");
                            matcher = constructorPattern.matcher(textBeforeCursor);
                            while (matcher.find()) {
                                constructorClassName = matcher.group(1);
                            }
                        }

                        System.out.println("Constructor class name detected: " + constructorClassName);

                        if (constructorClassName != null) {
                            // We're in a constructor call! Only add constructor completions
                            System.out.println("Adding constructor completions for: " + constructorClassName);
                            addConstructorCompletions(position, constructorClassName, result);
                            System.out.println("=== END NYX DEBUG ===");
                            // Return early - don't show keywords/other stuff in constructor context
                            return;
                        }

                        // Check if we're after a dot (member access)
                        String textAfterDot = getTextAfterDot(originalText, offset);
                        if (textAfterDot != null) {
                            System.out.println("Member access detected after: " + textAfterDot);
                            addMemberCompletions(position, result);
                            System.out.println("=== END NYX DEBUG ===");
                            // Return early - don't show keywords in member access context
                            return;
                        }

                        System.out.println("=== END NYX DEBUG ===");

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
        } else if (element.getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
            return "constructor";
        }
        return "symbol";
    }

    private boolean isFunctionOrMethod(NyxNamedElement element) {
        return element.getNode().getElementType() == NyxElementTypes.FUNCTION_DECLARATION ||
               element.getNode().getElementType() == NyxElementTypes.METHOD_DECLARATION ||
               element.getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION;
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
        } else if (element.getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
            return com.intellij.icons.AllIcons.Nodes.Method;
        }
        return com.intellij.icons.AllIcons.Nodes.Variable;
    }

    /**
     * Add constructor parameter completions when user types "new ClassName("
     */
    private void addConstructorCompletions(PsiElement position, String className, CompletionResultSet result) {
        System.out.println(">>> addConstructorCompletions called for class: " + className);

        // Find the class declaration
        com.intellij.psi.PsiFile file = position.getContainingFile();
        List<NyxNamedElement> declarations = NyxUtil.findDeclarations(file);

        System.out.println(">>> Found " + declarations.size() + " declarations in file");

        NyxNamedElement classDecl = null;
        for (NyxNamedElement decl : declarations) {
            System.out.println(">>> Declaration: " + decl.getName() + " type: " + decl.getNode().getElementType());
            if (className.equals(decl.getName()) &&
                decl.getNode().getElementType() == NyxElementTypes.CLASS_DECLARATION) {
                classDecl = decl;
                System.out.println(">>> Found class declaration!");
                break;
            }
        }

        if (classDecl == null) {
            System.out.println(">>> Class declaration not found!");
            return;
        }

        // Find all constructors in the class
        java.util.Collection<com.nyx.lang.psi.NyxDeclaration> allDeclarations =
            com.intellij.psi.util.PsiTreeUtil.findChildrenOfType(classDecl, com.nyx.lang.psi.NyxDeclaration.class);

        System.out.println(">>> Found " + allDeclarations.size() + " child declarations");

        java.util.List<NyxNamedElement> constructors = new java.util.ArrayList<>();
        for (com.nyx.lang.psi.NyxDeclaration decl : allDeclarations) {
            System.out.println(">>> Child declaration type: " + decl.getNode().getElementType() + " name: " + decl.getName());
            if (decl.getNode().getElementType() == NyxElementTypes.CONSTRUCTOR_DECLARATION) {
                constructors.add(decl);
                System.out.println(">>> Found constructor!");
            }
        }

        System.out.println(">>> Total constructors found: " + constructors.size());

        // Add completion for each constructor (skip constructors with no parameters)
        for (NyxNamedElement constructor : constructors) {
            String params = NyxPsiUtil.extractParameters(constructor);

            // Skip constructors with no parameters
            if (params == null || params.trim().isEmpty()) {
                System.out.println(">>> Skipping constructor with no parameters");
                continue;
            }

            String signature = "init(" + params + ")";

            System.out.println(">>> Adding constructor completion: " + signature);

            // Use empty string for create() so it doesn't insert anything by default
            // The insert handler will handle the actual insertion
            LookupElementBuilder element = LookupElementBuilder.create("")
                    .withPresentableText(signature)
                    .withIcon(com.intellij.icons.AllIcons.Nodes.Method)
                    .withTypeText("constructor")
                    .withInsertHandler(new NyxConstructorInsertHandler(params));

            result.addElement(element);
        }

        // Don't add any default constructor - user doesn't want constructors without parameters
    }

    /**
     * Check if we're completing after a dot (member access like "obj.")
     * Returns the identifier before the dot, or null if not in member access context
     */
    private String getTextAfterDot(String text, int offset) {
        // Look backwards from cursor to find a dot
        if (offset <= 0) return null;

        int i = offset - 1;
        // Skip whitespace
        while (i >= 0 && Character.isWhitespace(text.charAt(i))) {
            i--;
        }

        // Check if we have a dot
        if (i >= 0 && text.charAt(i) == '.') {
            // Found a dot! Now look for the identifier before it
            i--;
            while (i >= 0 && Character.isWhitespace(text.charAt(i))) {
                i--;
            }

            // Extract the identifier
            int endPos = i + 1;
            while (i >= 0 && Character.isJavaIdentifierPart(text.charAt(i))) {
                i--;
            }
            int startPos = i + 1;

            if (startPos < endPos) {
                return text.substring(startPos, endPos);
            }
        }

        return null;
    }

    /**
     * Add member completions (methods/properties) - no keywords
     */
    private void addMemberCompletions(PsiElement position, CompletionResultSet result) {
        // For now, add all declarations from the file that are methods/properties
        // In the future, this could be enhanced to resolve the type and show only relevant members
        com.intellij.psi.PsiFile file = position.getContainingFile();
        List<NyxNamedElement> declarations = NyxUtil.findDeclarations(file);

        for (NyxNamedElement declaration : declarations) {
            String name = declaration.getName();
            if (name != null && !name.isEmpty()) {
                // Only add functions and methods, not classes or other declarations
                if (isFunctionOrMethod(declaration)) {
                    String signature = NyxPsiUtil.extractSignature(declaration);
                    String params = NyxPsiUtil.extractParameters(declaration);
                    String typeText = getDeclarationType(declaration);

                    result.addElement(LookupElementBuilder.create(name)
                            .withPresentableText(signature)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Function)
                            .withTypeText(typeText)
                            .withInsertHandler(new NyxInsertHandler(params)));
                }
            }
        }
    }
}
