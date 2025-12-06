package com.nyx.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NyxFoldingBuilder extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        // Find all brace pairs and create folding regions
        collectFoldingRegions(root, descriptors);

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private void collectFoldingRegions(PsiElement element, List<FoldingDescriptor> descriptors) {
        // Get the text of this element
        String text = element.getText();

        // Check if this element contains braces
        if (text.contains("{") && text.contains("}")) {
            // Find the positions of the braces
            int startBrace = text.indexOf('{');
            int endBrace = text.lastIndexOf('}');

            if (startBrace < endBrace && endBrace > startBrace + 1) {
                int startOffset = element.getTextRange().getStartOffset() + startBrace;
                int endOffset = element.getTextRange().getStartOffset() + endBrace + 1;

                // Create a folding region if it spans multiple lines
                TextRange range = new TextRange(startOffset, endOffset);
                if (range.getLength() > 2) {  // Only fold if there's content
                    descriptors.add(new FoldingDescriptor(element.getNode(), range));
                }
            }
        }

        // Recursively process children, but only if we didn't already fold this element
        for (PsiElement child : element.getChildren()) {
            collectFoldingRegions(child, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        // The text to show when the region is folded
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        // Don't collapse by default
        return false;
    }
}
