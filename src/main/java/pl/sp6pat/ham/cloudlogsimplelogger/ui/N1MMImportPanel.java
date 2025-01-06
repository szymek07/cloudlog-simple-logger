package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.lang3.tuple.Pair;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class N1MMImportPanel extends ImportPanel {

    private static final Logger log = LoggerFactory.getLogger(N1MMImportPanel.class);

    private final JCheckBox enableN1MM = new JCheckBox("Enable N1MM");
    private final JTextField port = new JTextField("2345");

    public N1MMImportPanel(CloudlogIntegrationService service, SettingsManager settingsMgr) {
        super(service, settingsMgr);
        initializeComponents();
        reloadData();
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {

    }

    private void initializeActions() {
        enableN1MM.addChangeListener(e -> {
            if (enableN1MM.isSelected()) {
                startServer();
            } else {
                //TODO: interrupt server
            }

        });
    }

    private Component getMainPanel() {
        FormLayout layout = new FormLayout(
                "p, 3dlu, f:70dlu:g",
                "p, 3dlu, p, 3dlu, p, 3dlu, f:p, 6dlu, f:15dlu, 3dlu, f:60dlu:g");

        return FormBuilder.create()
                .layout(layout)
                .padding("10dlu, 10dlu, 10dlu, 10dlu")

                .addLabel("Station:").xy(1,1)
                .add(cloudlogStation).xy(3,1)

                .add(enableN1MM).xyw(1, 3, 3)

                .addLabel("Port:").xy(1, 7)
                .add(port).xy(3, 7)

                .addLabel("Logi:").xy(1, 9)
                .add(new JScrollPane(appLogs)).xyw(1, 11, 3)
                .build();
    }

    private void startServer() {
        int p = Integer.parseInt(port.getText().trim());

    }

    private static void handleClient(Socket socket) {

    }

    private static void processXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            // Przetwarzanie XML: na przykład odczytanie wartości pola <mycall>
            String myCall = document.getElementsByTagName("mycall").item(0).getTextContent();
            log.debug("MyCall: {}", myCall);
        } catch (Exception ex) {
            System.err.println("Error parsing XML: " + ex.getMessage());
        }
    }

    public void reloadData() {
        super.reloadData();
    }
}
