package com.nyx.lang;

import com.intellij.psi.tree.IElementType;

public interface NyxElementTypes {
    IElementType CLASS_DECLARATION = new NyxElementType("CLASS_DECLARATION");
    IElementType FUNCTION_DECLARATION = new NyxElementType("FUNCTION_DECLARATION");
    IElementType METHOD_DECLARATION = new NyxElementType("METHOD_DECLARATION");
    IElementType VARIABLE_DECLARATION = new NyxElementType("VARIABLE_DECLARATION");
    IElementType PARAMETER = new NyxElementType("PARAMETER");
    IElementType INTERFACE_DECLARATION = new NyxElementType("INTERFACE_DECLARATION");
    IElementType ENUM_DECLARATION = new NyxElementType("ENUM_DECLARATION");
    IElementType IDENTIFIER_REFERENCE = new NyxElementType("IDENTIFIER_REFERENCE");
}
