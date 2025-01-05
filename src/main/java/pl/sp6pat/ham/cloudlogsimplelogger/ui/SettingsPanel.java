package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SettingsPanel extends JPanel {
    private final static Logger log = LoggerFactory.getLogger(SettingsPanel.class);

    private final JTextField settCloudlogUrl = new JTextField();
    private final JPasswordField settApiKey = new JPasswordField();
    private final JCheckBox settApiKeyShow = new JCheckBox();
    private final JTextField settOperator = new JTextField();
    private final JTextField settQrzLogin = new JTextField();
    private final JPasswordField settQrzPass = new JPasswordField();
    private final JCheckBox settQrzPassShow = new JCheckBox();
    private final JButton settSave = new JButton("Save");

    private final SettingsManager settingsMgr;

    public SettingsPanel(SettingsManager settingsMgr) {
        this.settingsMgr = settingsMgr;
        initializeComponents();
        initializeActions();
        fillPanel();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {
        settApiKeyShow.setToolTipText("Show/Hide API Key");
        settQrzPassShow.setToolTipText("Show/Hide QRZ Password");
    }

    private void initializeActions() {
        settApiKeyShow.addActionListener(e -> settApiKey.setEchoChar(settApiKeyShow.isSelected() ? '\u0000' : (Character)UIManager.get("PasswordField.echoChar")));
        settQrzPassShow.addActionListener(e -> settQrzPass.setEchoChar(settQrzPassShow.isSelected() ? '\u0000' : (Character)UIManager.get("PasswordField.echoChar")));

        settSave.addActionListener(e -> {
            String key = Base64.getEncoder().encodeToString(String.valueOf(settApiKey.getPassword()).trim().getBytes());
            String pass = Base64.getEncoder().encodeToString(String.valueOf(settQrzPass.getPassword()).trim().getBytes());
            Settings settings = Settings.builder()
                    .cloudlogUrl(settCloudlogUrl.getText().trim())
                    .apiKey(key)
                    .operator(settOperator.getText().trim())
                    .qrzLogin(settQrzLogin.getText().trim())
                    .qrzPass(pass)
                    .build();

            settingsMgr.save(settings);
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
                .add(getPasswordFieldPanel(settApiKey, settApiKeyShow)).xy(3, 3)
                .addLabel("Operator:").xy(1, 5)
                .add(settOperator).xy(3, 5)
                .addLabel("QRZ Login:").xy(1, 7)
                .add(settQrzLogin).xy(3, 7)
                .addLabel("QRZ Pass:").xy(1, 9)
                .add(getPasswordFieldPanel(settQrzPass, settQrzPassShow)).xy(3, 9)
                .add(settSave).xy(3,11)
                .build();
    }

    private Component getPasswordFieldPanel(Component field, Component check) {
        FormLayout layout = new FormLayout(
                "f:p:g, 1dlu, p",
                "p");

        return FormBuilder.create()
                .layout(layout)
                .add(field).xy(1, 1)
                .add(check).xy(3, 1)
                .build();
    }

    private void fillPanel() {
        Settings settings = settingsMgr.getSettings();
        if (settings == null) {
            log.warn("Settings not found");
            return;
        }
        settCloudlogUrl.setText(settings.getCloudlogUrl());

        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        settApiKey.setText(apiKey);
        settOperator.setText(settings.getOperator());
        settQrzLogin.setText(settings.getQrzLogin());

        byte[] qrzPassDecodedBytes = Base64.getDecoder().decode(settings.getQrzPass());
        String qrzPass = new String(qrzPassDecodedBytes, StandardCharsets.UTF_8);
        settQrzPass.setText(qrzPass);
    }

    public void reloadData() {
        this.fillPanel();
    }

}
