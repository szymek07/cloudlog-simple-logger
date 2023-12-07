package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SettingsPanel extends JPanel {
    private final JTextField settCloudlogUrl = new JTextField();
    private final JTextField settApiKey = new JTextField();
    private final JTextField settOperator = new JTextField();
    private final JTextField settQrzLogin = new JTextField();
    private final JPasswordField settQrzPass = new JPasswordField();
    private final JButton settSave = new JButton("Save");

    public SettingsPanel() {
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeActions() {
        settSave.addActionListener(e -> {
            String pass = Base64.getEncoder().encodeToString(String.valueOf(settQrzPass.getPassword()).getBytes());
            Settings settings = Settings.builder()
                    .cloudlogUrl(settCloudlogUrl.getText())
                    .apiKey(settApiKey.getText())
                    .operator(settOperator.getText())
                    .qrzLogin(settQrzLogin.getText())
                    .qrzPass(pass)
                    .build();
            SettingsManager.save(settings);
            JOptionPane.showMessageDialog(this, "Saved");
        });
    }

    private Component getMainPanel() {
        FormLayout layout = new FormLayout(
                "p, 3dlu, f:50dlu:g, 3dlu, p",
                "f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p");

        return FormBuilder.create()
                .layout(layout)
                .padding("10dlu, 10dlu, 10dlu, 10dlu")
                .addLabel("Cloudlog URL:").xy(1, 1)
                .add(settCloudlogUrl).xy(3, 1)
                .addLabel("API Key:").xy(1, 3)
                .add(settApiKey).xy(3, 3)
                .addLabel("Operator:").xy(1, 5)
                .add(settOperator).xy(3, 5)
                .addLabel("QRZ Login:").xy(1, 7)
                .add(settQrzLogin).xy(3, 7)
                .addLabel("QRZ Pass:").xy(1, 9)
                .add(settQrzPass).xy(3, 9)
                .add(settSave).xy(3,11)
                .build();
    }

    public void fillPanel(Settings settings) {
        settCloudlogUrl.setText(settings.getCloudlogUrl());
        settApiKey.setText(settings.getApiKey());
        settOperator.setText(settings.getOperator());
        settQrzLogin.setText(settings.getQrzLogin());
        byte[] decodedBytes = Base64.getDecoder().decode(settings.getQrzPass());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        settQrzPass.setText(decodedString);
    }


}
