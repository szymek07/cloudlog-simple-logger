package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JTextFiledParametrizedTest {

    @ParameterizedTest
    @ValueSource(strings = {"aaa", "aaa  ", " aaa ", "\taaa\t"})
    void testTrimText(String s) {
        JTextField field = new JTextField(s);
        assertEquals("aaa", getTrimmedText(field));
    }

    @ParameterizedTest
    @NullSource
    void testTrimTextNullInput(String s) {
        JTextField field = new JTextField(s);
        assertEquals("", getTrimmedText(field));
    }

    @ParameterizedTest
    @EmptySource
    void testTrimTextEmptyInput(String s) {
        JTextField field = new JTextField(s);
        assertEquals("", getTrimmedText(field));
    }

    private String getTrimmedText(JTextField field) {
        return field.getText().trim();
    }

}
