package com.nyx.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class NyxTokenType extends IElementType {
    public NyxTokenType(@NotNull String debugName) {
        super(debugName, NyxLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "NyxTokenType." + super.toString();
    }
}
