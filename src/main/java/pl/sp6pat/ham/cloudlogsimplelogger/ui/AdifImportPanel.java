package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import lombok.SneakyThrows;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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


    public AdifImportPanel(CloudlogIntegrationService service, Settings settings) {
        super(service, settings);
        initializeComponents();
        fillComboBoxes();
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {
        adifProgress.setStringPainted(true);

    }

    private void initializeActions() {
        adifBrowse.addActionListener(e -> SwingUtilities.invokeLater(this::browseBtAction));

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
                "p, 3dlu, p, 3dlu, p, 3dlu, f:p, 6dlu, f:15dlu, 3dlu, f:p, 3dlu, f:p:g");

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

    @SneakyThrows
    private void browseBtAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ADI Files", "adi");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            appLogs.setText("");
            File selectedFile = fileChooser.getSelectedFile();
            adifPath.setText(selectedFile.getAbsolutePath());

            Optional<Adif3> adif3 = readADI(selectedFile);
            if (adif3.isPresent()) {
                adifRecords = adif3.get().getRecords();
                int size = adifRecords.size();
                adifQsoCnt.setText(String.valueOf(size));
                adifProgress.setMaximum(size);
                adifProgress.setValue(0);
                adifProgress.setString("0/" + size);
            }
        }
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
            appLogs.setText("QSO to import: " + qsos.size() + "\n");
            this.qsos = qsos;
            this.station = station;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (Adif3Record c: qsos) {
                try {

                    if (StringUtils.hasText(settings.getOperator())) {
                        c.setOperator(settings.getOperator());
                    }
                    String s = service.importQso(settings, station.getStationId(), c);
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


}
