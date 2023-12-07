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

public class AdifImportPanel extends JPanel {

    private static Logger log = LoggerFactory.getLogger(AdifImportPanel.class);
    private final CloudlogIntegrationService service;
    private final Settings settings;

    private List<Adif3Record> adifRecords;

    private final JComboBox<String> adifStation = new JComboBox<>();
    private final JTextField adifPath = new JTextField();
    private final JButton adifBrowse = new JButton("...");
    private final JLabel adifQsoCnt = new JLabel();
    private final JLabel adifInvalidQso = new JLabel();
    private final JButton adifImport = new JButton("Import");
    private final JProgressBar adifProgress = new JProgressBar();
    private final JTextArea adifLogs  = new JTextArea();

    public AdifImportPanel(CloudlogIntegrationService service, Settings settings) {
        this.service = service;
        this.settings = settings;
        initializeComponents();
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {
        adifProgress.setStringPainted(true);
        adifLogs.setEditable(false);
        adifLogs.setEnabled(false);
    }

    private void initializeActions() {
        adifBrowse.addActionListener(e -> SwingUtilities.invokeLater(this::browseBtAction));

        adifImport.addActionListener(e -> {
            adifImport.setEnabled(false);
            adifLogs.setText("");

            Station stationSelectedItem = (Station) adifStation.getSelectedItem();

            AdifImportWorker worker = new AdifImportWorker(adifRecords, stationSelectedItem);
            worker.execute();

        });
    }

    private Component getMainPanel() {
        FormLayout layout = new FormLayout(
                "p, 3dlu, f:70dlu:g, 3dlu, p",
                "p, 3dlu, p, 3dlu, f:p, 6dlu, f:15dlu, 3dlu, f:p, 3dlu, f:p:g");

        return FormBuilder.create()
                .layout(layout)
                .padding("10dlu, 10dlu, 10dlu, 10dlu")
                .addLabel("ADIF:").xy(1, 1)
                .add(adifPath).xy(3, 1)
                .add(adifBrowse).xy(5,1)
                .addLabel("QSO Count:").xy(1,3)
                .add(adifQsoCnt).xy(3,3)
                .add(adifImport).xyw(1, 5, 5)
                .add(adifProgress).xyw(1,7,5)
                .addLabel("Logi:").xy(1, 9)
                .add(new JScrollPane(adifLogs)).xyw(1, 11, 5)
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

        public AdifImportWorker(List<Adif3Record> qsos, Station station) {
            this.qsos = qsos;
            this.station = station;
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("Qso to process: {}", qsos);
            for (Adif3Record c: qsos) {
                publish(c);
                String s = service.importQso(settings, station.getStationId(), c);
                adifLogs.append(c.getCall() + " " + s);
            }

            return null;
        }

        @Override
        protected void process(List<Adif3Record> chunks) {
            for (Adif3Record c: chunks) {
                adifProgress.setValue(adifProgress.getValue()+1);
                adifProgress.setString(adifProgress.getValue() + "/" + adifProgress.getMaximum());
            }
        }

        @Override
        protected void done() {
            adifImport.setEnabled(true);
            adifLogs.append("\nDone.");
        }
    }

}
