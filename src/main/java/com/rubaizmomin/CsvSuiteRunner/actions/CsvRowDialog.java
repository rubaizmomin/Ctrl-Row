package com.rubaizmomin.CsvSuiteRunner.actions;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CsvRowDialog extends DialogWrapper {

    private final JTextField rowField = new JTextField();

    public CsvRowDialog() {
        super(true);

        setTitle("CSV Suite Runner");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel(
                "Enter CSV row numbers (e.g. 1,3,5 or 2-6):"
        );

        panel.add(label, BorderLayout.NORTH);
        panel.add(rowField, BorderLayout.CENTER);

        return panel;
    }

    public String getRows() {
        return rowField.getText().trim();
    }
}