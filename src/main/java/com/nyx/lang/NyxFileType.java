package com.nyx.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NyxFileType extends LanguageFileType {
    public static final NyxFileType INSTANCE = new NyxFileType();

    private NyxFileType() {
        super(NyxLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Nyx";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Nyx language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "nx";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return NyxIcons.FILE;
    }
}
