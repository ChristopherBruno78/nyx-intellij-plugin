package com.nyx.lang;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

/**
 * Insert handler for constructor parameter completion
 */
public class NyxConstructorInsertHandler implements InsertHandler<LookupElement> {
    private final String parameters;

    public NyxConstructorInsertHandler(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {

        Editor editor = context.getEditor();
        Document document = editor.getDocument();
        int offset = context.getTailOffset();

        // Check if there's already a closing paren after the cursor
        CharSequence text = document.getCharsSequence();
        boolean hasClosingParen = false;
        if (offset < text.length() && text.charAt(offset) == ')') {
            hasClosingParen = true;
        }

        // Parse parameters to extract names
        String[] paramNames = extractParameterNames(parameters);

        if (paramNames.length == 0) {
            // No parameters - if no closing paren, add one
            if (!hasClosingParen) {
                document.insertString(offset, ")");
            }
            editor.getCaretModel().moveToOffset(offset);
        } else {
            // Use template system for tab navigation between parameters
            TemplateManager templateManager = TemplateManager.getInstance(context.getProject());
            Template template = templateManager.createTemplate("", "");
            template.setToReformat(false);

            // Build template with variables for each parameter value
            for (int i = 0; i < paramNames.length; i++) {
                if (i > 0) {
                    template.addTextSegment(", ");
                }
                // Add parameter name
                template.addTextSegment(paramNames[i] + ": ");
                // Add variable that user can tab to
                template.addVariable("param" + i, new ConstantNode(""), true);
            }

            // Only add closing paren if it doesn't already exist
            if (!hasClosingParen) {
                template.addTextSegment(")");
            } else {
                // If closing paren exists, we need to delete it first and add it in the template
                // so the cursor ends up in the right place
                if (offset < text.length() && text.charAt(offset) == ')') {
                    document.deleteString(offset, offset + 1);
                    template.addTextSegment(")");
                }
            }

            // Start the template at the current position
            templateManager.startTemplate(editor, template);
        }
    }

    private String[] extractParameterNames(String params) {
        if (params == null || params.trim().isEmpty()) {
            return new String[0];
        }

        // Split by comma
        String[] parts = params.split(",");
        String[] names = new String[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            // Extract parameter name (before the colon)
            int colonIndex = part.indexOf(':');
            if (colonIndex > 0) {
                names[i] = part.substring(0, colonIndex).trim();
            } else {
                // No type annotation, use the whole part
                names[i] = part;
            }
        }

        return names;
    }
}
