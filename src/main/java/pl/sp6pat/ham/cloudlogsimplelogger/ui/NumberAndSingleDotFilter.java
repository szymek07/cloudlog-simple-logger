package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberAndSingleDotFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null) {
            string = string.replace(',', '.');
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            if (isValid(currentText + string)) {
                super.insertString(fb, offset, string, attr);
            }
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (string != null) {
            string = string.replace(',', '.');
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = new StringBuilder(currentText).replace(offset, offset + length, string).toString();
            if (isValid(newText)) {
                super.replace(fb, offset, length, string, attr);
            }
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }

    private boolean isValid(String text) {
        return text.matches("[0-9]*\\.?[0-9]*");
    }

}