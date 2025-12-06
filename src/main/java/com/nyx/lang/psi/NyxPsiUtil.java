package com.nyx.lang.psi;

import com.intellij.psi.PsiElement;

public class NyxPsiUtil {
    /**
     * Extract function/method/constructor signature including parameters from the full text
     */
    public static String extractSignature(PsiElement element) {
        String fullText = element.getText();

        // Find the function/method/constructor name and parameters
        // Pattern: "function name(params)", "func name(params)", or "init(params)"
        int nameStart = -1;
        int nameEnd = -1;
        int parenStart = -1;
        int parenEnd = -1;

        // Skip the keyword (function/func/init)
        String trimmed = fullText.trim();

        // Handle constructors (init)
        if (trimmed.startsWith("init")) {
            // Constructors don't have a name, just parameters
            parenStart = trimmed.indexOf('(');
            if (parenStart >= 0) {
                // Find the matching closing parenthesis
                int depth = 1;
                parenEnd = parenStart + 1;
                while (parenEnd < trimmed.length() && depth > 0) {
                    if (trimmed.charAt(parenEnd) == '(') depth++;
                    else if (trimmed.charAt(parenEnd) == ')') depth--;
                    parenEnd++;
                }
                parenEnd--; // Back up to the closing paren

                String params = trimmed.substring(parenStart, parenEnd + 1);
                return "init" + params;
            } else {
                return "init()";
            }
        }

        // Handle functions and methods
        if (trimmed.startsWith("function ")) {
            nameStart = trimmed.indexOf("function ") + 9;
        } else if (trimmed.startsWith("func ")) {
            nameStart = trimmed.indexOf("func ") + 5;
        }

        if (nameStart >= 0) {
            // Find the end of the name (space or opening paren)
            nameEnd = nameStart;
            while (nameEnd < trimmed.length() &&
                   Character.isJavaIdentifierPart(trimmed.charAt(nameEnd))) {
                nameEnd++;
            }

            // Find the opening parenthesis
            parenStart = trimmed.indexOf('(', nameEnd);
            if (parenStart >= 0) {
                // Find the matching closing parenthesis
                int depth = 1;
                parenEnd = parenStart + 1;
                while (parenEnd < trimmed.length() && depth > 0) {
                    if (trimmed.charAt(parenEnd) == '(') depth++;
                    else if (trimmed.charAt(parenEnd) == ')') depth--;
                    parenEnd++;
                }
                parenEnd--; // Back up to the closing paren

                // Extract name and parameters
                String name = trimmed.substring(nameStart, nameEnd);
                String params = trimmed.substring(parenStart, parenEnd + 1);
                return name + params;
            } else {
                // No parameters found
                String name = trimmed.substring(nameStart, nameEnd);
                return name + "()";
            }
        }

        return element.getText();
    }

    /**
     * Extract parameters as a formatted string
     */
    public static String extractParameters(PsiElement element) {
        String fullText = element.getText().trim();

        // Find parameters between parentheses
        int parenStart = fullText.indexOf('(');
        int parenEnd = fullText.lastIndexOf(')');

        if (parenStart >= 0 && parenEnd > parenStart) {
            String params = fullText.substring(parenStart + 1, parenEnd).trim();
            return params;
        }

        return "";
    }
}
