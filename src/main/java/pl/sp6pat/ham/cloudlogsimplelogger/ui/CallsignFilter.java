package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class CallsignFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        if (text != null) {
            text = filterText(text);
            super.insertString(fb, offset, text, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null) {
            text = filterText(text);
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private String filterText(String text) {
        StringBuilder filteredText = new StringBuilder();
        for (char c : text.toCharArray()) {
            // Sprawdzamy, czy znak to litera ANSI lub cyfra
            if (Character.isLetterOrDigit(c) && c < 128) {
                // Dodajemy znak jako wielką literę
                filteredText.append(Character.toUpperCase(c));
            }
        }
        return filteredText.toString();
    }

}
