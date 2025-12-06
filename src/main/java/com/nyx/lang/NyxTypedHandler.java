package com.nyx.lang;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class NyxTypedHandler extends TypedHandlerDelegate {

    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        if (!(fileType instanceof NyxFileType)) {
            return Result.CONTINUE;
        }

        int offset = editor.getCaretModel().getOffset();
        CharSequence text = editor.getDocument().getCharsSequence();

        // Skip over closing characters if they're already present
        if (offset < text.length()) {
            char charAtCaret = text.charAt(offset);
            if ((c == '}' && charAtCaret == '}') ||
                (c == ']' && charAtCaret == ']') ||
                (c == ')' && charAtCaret == ')')) {
                // Move caret forward and consume the typed character
                editor.getCaretModel().moveToOffset(offset + 1);
                return Result.STOP;
            }
        }

        // Auto-insert closing characters for opening ones
        if (c == '{' || c == '[' || c == '(') {
            // Don't auto-close ')' in constructor context - it interferes with auto-popup
            boolean isConstructorContext = isInConstructorContext(text, offset);

            if (shouldAutoClose(text, offset, c) && !isConstructorContext) {
                char closing = getClosingChar(c);
                editor.getDocument().insertString(offset, String.valueOf(closing));
                // Don't move caret - it will be between the opening and closing chars
            }
        }

        return Result.CONTINUE;
    }

    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!(file.getFileType() instanceof NyxFileType)) {
            return Result.CONTINUE;
        }

        System.out.println("NYX TYPED: charTyped called for: " + c);

        // Auto-popup after '.' for member access
        if (c == '.') {
            System.out.println("NYX TYPED: Triggering auto-popup for member access");
            com.intellij.codeInsight.AutoPopupController controller = com.intellij.codeInsight.AutoPopupController.getInstance(project);
            controller.scheduleAutoPopup(editor);
            controller.autoPopupMemberLookup(editor, null);
        }

        // Auto-popup after '(' for constructor
        if (c == '(') {
            CharSequence text = editor.getDocument().getCharsSequence();
            int offset = editor.getCaretModel().getOffset();

            // Check if we're in a constructor context
            String textBefore = text.subSequence(0, Math.max(0, offset)).toString();
            System.out.println("NYX TYPED: Text before cursor: " + textBefore.substring(Math.max(0, textBefore.length() - 50)));

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\($");
            if (pattern.matcher(textBefore).find()) {
                System.out.println("NYX TYPED: Constructor context detected! Triggering auto-popup");
                com.intellij.codeInsight.AutoPopupController controller = com.intellij.codeInsight.AutoPopupController.getInstance(project);
                // Try both methods to ensure popup appears
                controller.scheduleAutoPopup(editor);
                controller.autoPopupMemberLookup(editor, null);
            }
        }

        return Result.CONTINUE;
    }

    private boolean isInConstructorContext(CharSequence text, int offset) {
        // Check if we have "new ClassName" pattern before the cursor
        String textBefore = text.subSequence(0, offset).toString();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*$");
        return pattern.matcher(textBefore).find();
    }

    private boolean shouldAutoClose(CharSequence text, int offset, char openChar) {
        // Auto-close if we're at the end of the document
        if (offset >= text.length()) {
            return true;
        }

        char nextChar = text.charAt(offset);

        // Auto-close if the next character is whitespace, closing brace, semicolon, comma, or end of line
        return Character.isWhitespace(nextChar) ||
                nextChar == '}' ||
                nextChar == ']' ||
               nextChar == ';' ||
                nextChar == ')' ||
               nextChar == ',' ||
               nextChar == '\n' ||
               nextChar == '\r';
    }

    private char getClosingChar(char openChar) {
        switch (openChar) {
            case '{': return '}';
            case '[': return ']';
            case '(': return ')';
            default: return openChar;
        }
    }
}
