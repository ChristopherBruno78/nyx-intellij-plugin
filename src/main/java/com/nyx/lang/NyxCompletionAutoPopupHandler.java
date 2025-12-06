package com.nyx.lang;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Handles auto-popup of completions for specific contexts
 */
public class NyxCompletionAutoPopupHandler extends TypedHandlerDelegate {

    public NyxCompletionAutoPopupHandler() {
        System.out.println("NYX AUTO-POPUP HANDLER: Constructor called - handler is loaded!");
    }

    @NotNull
    @Override
    public Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        System.out.println("NYX AUTO-POPUP HANDLER: checkAutoPopup called for char: " + charTyped);

        if (!(file.getFileType() instanceof NyxFileType)) {
            System.out.println("NYX AUTO-POPUP HANDLER: Not a Nyx file, returning CONTINUE");
            return Result.CONTINUE;
        }

        System.out.println("NYX AUTO-POPUP HANDLER: Is a Nyx file!");

        // Auto-popup after '(' in constructor context
        if (charTyped == '(') {
            CharSequence text = editor.getDocument().getCharsSequence();
            int offset = editor.getCaretModel().getOffset();

            String textBefore = text.subSequence(0, Math.max(0, offset)).toString();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("new\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\($");




            if (pattern.matcher(textBefore).find()) {
                System.out.println("NYX AUTO-POPUP: Constructor context - returning STOP to trigger auto-popup");
                // Just return STOP - this tells IntelliJ to show auto-popup
                // Don't manually call scheduleAutoPopup
                return Result.STOP;
            }
        }

        // Note: '.' auto-popup is handled in charTyped and it works, so we don't need to handle it here

        return Result.CONTINUE;
    }
}
