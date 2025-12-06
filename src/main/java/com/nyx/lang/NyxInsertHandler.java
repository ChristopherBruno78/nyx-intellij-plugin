package com.nyx.lang;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TextExpression;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NyxInsertHandler implements InsertHandler<LookupElement> {
    private final String parametersText;

    public NyxInsertHandler(String parametersText) {
        this.parametersText = parametersText;
    }

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        Editor editor = context.getEditor();
        Document document = editor.getDocument();

        // Parse parameters to extract parameter names
        List<String> paramNames = extractParameterNames(parametersText);

        if (paramNames.isEmpty()) {
            // No parameters, just insert ()
            document.insertString(context.getTailOffset(), "()");
            editor.getCaretModel().moveToOffset(context.getTailOffset());
        } else {
            // Create a live template with parameter placeholders
            TemplateManager templateManager = TemplateManager.getInstance(context.getProject());
            Template template = templateManager.createTemplate("", "");
            template.setToReformat(false);

            template.addTextSegment("(");

            for (int i = 0; i < paramNames.size(); i++) {
                if (i > 0) {
                    template.addTextSegment(", ");
                }
                String paramName = paramNames.get(i);
                template.addTextSegment(paramName + ": ");
                template.addVariable(new TextExpression(""), true);
            }

            template.addTextSegment(")");
            template.addEndVariable();

            // Insert the template at the cursor position (after the function name)
            editor.getCaretModel().moveToOffset(context.getTailOffset());
            templateManager.startTemplate(editor, template);
        }
    }

    private List<String> extractParameterNames(String params) {
        List<String> names = new ArrayList<>();

        if (params == null || params.trim().isEmpty()) {
            return names;
        }

        // Pattern to match parameter names before the colon
        // Handles: "name: type", "x: number", etc.
        Pattern pattern = Pattern.compile("(\\w+)\\s*:");
        Matcher matcher = pattern.matcher(params);

        while (matcher.find()) {
            names.add(matcher.group(1));
        }

        return names;
    }
}
