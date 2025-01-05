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
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdifImportPanel extends ImportPanel {

    private static final Logger log = LoggerFactory.getLogger(AdifImportPanel.class);

    private List<Adif3Record> adifRecords;

    private final JTextField adifPath = new JTextField();
    private final JButton adifBrowse = new JButton("...");
    private final JLabel adifQsoCnt = new JLabel();
    private final JButton adifImport = new JButton("Import");
    private final JProgressBar adifProgress = new JProgressBar();

    public AdifImportPanel(CloudlogIntegrationService service, SettingsManager settingsMgr) {
        super(service, settingsMgr);
        initializeComponents();
        reloadData();
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {
        adifProgress.setStringPainted(true);

    }

    private void initializeActions() {
        adifBrowse.addActionListener(e -> browseBtAction());

        adifImport.addActionListener(e -> {

            Station stationSelectedItem = (Station) cloudlogStation.getSelectedItem();

            if (stationSelectedItem != null & adifRecords != null) {
                adifImport.setEnabled(false);
                appLogs.setText("");
                AdifImportWorker worker = new AdifImportWorker(adifRecords, stationSelectedItem);
                worker.execute();
            }

        });
    }

    private Component getMainPanel() {
        FormLayout layout = new FormLayout(
                "p, 3dlu, f:70dlu:g, 3dlu, p",
                "p, 3dlu, p, 3dlu, p, 3dlu, f:p, 6dlu, f:15dlu, 3dlu, f:p, 3dlu, f:60dlu:g");

        return FormBuilder.create()
                .layout(layout)
                .padding("10dlu, 10dlu, 10dlu, 10dlu")

                .addLabel("Station:").xy(1,1)
                .add(cloudlogStation).xy(3,1)

                .addLabel("ADIF:").xy(1, 3)
                .add(adifPath).xy(3, 3)
                .add(adifBrowse).xy(5,3)
                .addLabel("QSO Count:").xy(1,5)
                .add(adifQsoCnt).xy(3,5)
                .add(adifImport).xyw(1, 7, 5)
                .add(adifProgress).xyw(1,9,5)
                .addLabel("Logi:").xy(1, 11)
                .add(new JScrollPane(appLogs)).xyw(1, 13, 5)
                .build();
    }

    private void browseBtAction() {
        JFileChooser fileChooser = getFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Station stationSelectedItem = (Station) cloudlogStation.getSelectedItem();
            File selectedFile = fileChooser.getSelectedFile();

            appLogs.setText("");
            adifPath.setText(selectedFile.getAbsolutePath());

            try {
                Optional<Adif3> adif3 = readADI(selectedFile);
                if (adif3.isPresent()) {
                    adifRecords = adif3.get().getRecords();
                    AdifValidatorWorker worker = new AdifValidatorWorker(adifRecords, stationSelectedItem);
                    worker.execute();
                }
            } catch (IOException e) {
                log.debug("Read ADI error", e);
                appLogs.setText("Read ADI error: " + e.getMessage());
            }
        }
    }

    private static JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ADI Files", "adi");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }

    private Optional<Adif3> readADI(File file) throws IOException {
        BufferedReader buffInput = null;
        try {
            AdiReader reader = new AdiReader();
            reader.setQuirksMode(true);
            buffInput = new BufferedReader(new FileReader(file));
            return reader.read(buffInput);
        } catch (Exception e) {
            log.error("Error: ", e);
            appLogs.append("Error: " + e.getMessage() + "\n");
            throw e;
        } finally {
            try {
                if (buffInput != null) {
                    buffInput.close();
                }
            } catch (IOException ex) {
                log.error("ERROR", ex);
            }
        }
    }

    class AdifImportWorker extends SwingWorker<Void, Adif3Record> {
        private final List<Adif3Record> qsos;
        private final Station station;
        private boolean hasErrors = false;

        public AdifImportWorker(List<Adif3Record> qsos, Station station) {
            this.qsos = qsos;
            this.station = station;
            appLogs.setText("QSO to import: " + qsos.size() + "\n");
        }

        @Override
        protected Void doInBackground() throws Exception {
            Settings settings = settingsMgr.getSettings();
            for (Adif3Record c: qsos) {
                try {
                    if (StringUtils.hasText(settings.getOperator())) {
                        c.setOperator(settings.getOperator());
                    }
                    String s = service.importQso(station.getStationId(), c);
                    log.debug(s);
                    publish(c);
                } catch (Exception e) {
                    log.error("Error: ", e);
                    hasErrors = true;
                    appLogs.append("Error: " + e.getMessage() + "\n");
                    throw e;
                }
            }
            return null;
        }

        @Override
        protected void process(List<Adif3Record> chunks) {
            for (Adif3Record c: chunks) {
                markQsoImported(c);
            }
        }

        @Override
        protected void done() {
            resetUi(hasErrors);
        }
    }

    class AdifValidatorWorker extends SwingWorker<Void, Pair<Adif3Record, Optional<String>>> {
        private final List<Adif3Record> qsos;
        private final Station station;
        private Boolean hasErrors = false;

        public AdifValidatorWorker(List<Adif3Record> qsos, Station station) {
            this.qsos = qsos;
            this.station = station;
            appLogs.setText("QSO to validate: " + qsos.size() + "\n");
        }

        @Override
        protected Void doInBackground() {
            for (Adif3Record c: qsos) {
                List<String> errors = new ArrayList<>();

                reportEmptyStrings("CALL", c.getCall(), errors);
                reportEmptyStrings("RST_SEND", c.getRstSent(), errors);
                reportEmptyStrings("RST_RCV", c.getRstRcvd(), errors);
                reportEmptyStrings("STATION_CALLSIGN", c.getStationCallsign(), errors);
                reportNulls("BAND", c.getBand(), errors);
                reportNulls("MODE", c.getMode(), errors);
                reportNulls("QSO_DATE", c.getQsoDate(), errors);
                reportNulls("TIME_ON", c.getTimeOn(), errors);
                reportNotEqualString("STATION_CALLSIGN", station.getStationCallsign(), c.getStationCallsign(), errors);

                if (!errors.isEmpty()) {
                    log.debug("Found errors in validation: " + errors);
                    hasErrors = true;
                }
                Pair<Adif3Record, Optional<String>> validResult = Pair.of(c, errors.isEmpty() ? Optional.empty() : Optional.of(org.apache.commons.lang3.StringUtils.joinWith(", ", errors)));
                publish(validResult);
            }
            return null;
        }

        @Override
        protected void process(List<Pair<Adif3Record, Optional<String>>> chunks) {
            for (Pair<Adif3Record, Optional<String>> c: chunks) {
                Adif3Record r = c.getLeft();
                Optional<String> errors = c.getRight();
                errors.ifPresent(s -> appLogs.append(r.getCall() + " " + r.getQsoDate() + " " + r.getTimeOn() + ": " + s + "\n"));
            }
        }

        @Override
        protected void done() {
            appLogs.append("QSO validation complete " + (hasErrors? "with errors.": "without errors."));
            if (!hasErrors) {
                int size = adifRecords.size();
                adifQsoCnt.setText(String.valueOf(size));
                adifProgress.setMaximum(size);
                adifProgress.setValue(0);
                adifProgress.setString("0/" + size);
                adifImport.setEnabled(true);
            } else {
                adifImport.setEnabled(false);
            }
        }

        private void reportEmptyStrings(String fieldName, String value, List<String> errors) {
            if (!StringUtils.hasText(value)) {
                log.debug("{} empty", fieldName);
                errors.add(fieldName + " empty");
            }
        }

        private void reportNulls(String fieldName, Object value, List<String> errors) {
            if (value == null) {
                log.debug("{} empty", fieldName);
                errors.add(fieldName + " empty");
            }
        }

        private void reportNotEqualString(String fieldName, String value1, String value2, List<String> errors) {
            if (value1 != null) {
                if (!value1.equalsIgnoreCase(value2)) {
                    log.debug("{} mismatch", fieldName);
                    errors.add(fieldName + " mismatch");
                }
            }
        }

    }

    private void markQsoImported(Adif3Record c) {
        appLogs.append("QSO " + c.getCall() + " imported.\n");
        adifProgress.setValue(adifProgress.getValue() + 1);
        adifProgress.setString(adifProgress.getValue() + "/" + adifProgress.getMaximum());
    }

    private void resetUi(boolean hasErrors) {
        adifImport.setEnabled(true);
        appLogs.append(hasErrors? "Break!\n" : "Done.\n");
        adifPath.setText("");
        adifQsoCnt.setText("");
        adifProgress.setValue(0);
        adifProgress.setString("");
    }

    public void reloadData() {
        super.reloadData();
    }
}
