package com.nyx.lang;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class NyxColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keyword", NyxSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("String", NyxSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", NyxSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Comment", NyxSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Identifier", NyxSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Operator", NyxSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Bad Character", NyxSyntaxHighlighter.BAD_CHARACTER)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return NyxIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new NyxSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
                // Nyx Language Demo
                /* Block comment example */

                import { Component } from "framework";

                type Point = {
                    x: number,
                    y: number
                };

                interface Drawable {
                    func draw(): void;
                }

                class Button implements Drawable {
                    private title: string;
                    readonly color: string?;

                    init(title: string, color: string?) {
                        this.title = title;
                        this.color = color;
                    }

                    func greet(person: string): string {
                        return `Hello, ${person}!`;
                    }

                    func draw(): void {
                        console.log("Drawing button");
                    }
                }

                enum Status {
                    Active = 1,
                    Inactive = 0
                }

                const button = new Button(title: "Click Me", color: "#ff0000");
                let message = button.greet(person: "World");

                for (let i = 0; i < 10; i++) {
                    console.log(i);
                }
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Nyx";
    }
}
