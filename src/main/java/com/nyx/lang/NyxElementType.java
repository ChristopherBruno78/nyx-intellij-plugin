package com.nyx.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class NyxElementType extends IElementType {
    public NyxElementType(@NotNull String debugName) {
        super(debugName, NyxLanguage.INSTANCE);
    }
}
