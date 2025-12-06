package com.nyx.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.nyx.lang.NyxFileType;
import com.nyx.lang.NyxLanguage;
import org.jetbrains.annotations.NotNull;

public class NyxFile extends PsiFileBase {
    public NyxFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, NyxLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return NyxFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Nyx File";
    }
}
