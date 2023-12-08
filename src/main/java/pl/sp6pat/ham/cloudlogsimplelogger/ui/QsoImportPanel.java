package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.qso.QsoBand;
import pl.sp6pat.ham.cloudlogsimplelogger.qso.QsoMode;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class QsoImportPanel extends ImportPanel {

    private static final Logger log = LoggerFactory.getLogger(QsoImportPanel.class);


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Timer timer;
    private final JCheckBox qsoOffline = new JCheckBox("Offline");
    private final JFormattedTextField qsoDate = new JFormattedTextField(dateFormat);
    private final JFormattedTextField qsoTime = new JFormattedTextField(timeFormat);
    private final JComboBox<QsoMode> qsoMode = new JComboBox<>();
    private final JComboBox<QsoBand> qsoBand = new JComboBox<>();
    private final JTextField qsoFreq = new JTextField();
    private final JTextField qsoCall = new JTextField();
    private final JTextField qsoRstS = new JTextField("59");
    private final JTextField qsoRstR = new JTextField("59");
    private final JTextField qsoName = new JTextField();
    private final JTextField qsoQth = new JTextField();
    private final JTextArea qsoComment = new JTextArea();
    private final JButton qsoAdd = new JButton("Add QSO");

    public QsoImportPanel(CloudlogIntegrationService service, Settings settings) {
        super(service, settings);
        initializeComponents();
        initializeActions();
        fillComboBoxes();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {
        qsoDate.setEditable(false);
        qsoTime.setEditable(false);
    }

    private void initializeActions() {
        qsoOffline.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                qsoDate.setEditable(true);
                qsoDate.setText("");
                qsoTime.setEditable(true);
                qsoTime.setText("");
                timer.stop();
            } else {
                qsoDate.setEditable(false);
                qsoTime.setEditable(false);
                timer.start();
            }
        });



        qsoAdd.addActionListener( e -> {

            Station stationSelectedItem = (Station) cloudlogStation.getSelectedItem();
            QsoMode qsoModeSelectedItem = (QsoMode) qsoMode.getSelectedItem();
            QsoBand qsoBandSelectedItem = (QsoBand) qsoBand.getSelectedItem();

            if (stationSelectedItem == null || qsoModeSelectedItem == null || qsoBandSelectedItem == null || !StringUtils.hasText(qsoCall.getText())) {
                return;
            }

            Adif3Record record = new Adif3Record();

            record.setCall(qsoCall.getText());
            record.setRstSent(qsoRstS.getText());
            record.setRstRcvd(qsoRstR.getText());
            record.setName(qsoName.getText());
            record.setQth(qsoQth.getText());
            record.setComment(qsoComment.getText());

            record.setQsoDate(LocalDate.parse(qsoDate.getText()));
            record.setTimeOn(LocalTime.parse(qsoTime.getText()));

            Mode mode = Mode.findByCode(qsoModeSelectedItem.getMode());
            record.setMode(mode);

            Band band = Band.findByCode(qsoBandSelectedItem.getBand());
            record.setBand(band);

            record.setFreq(Long.parseLong(qsoFreq.getText()) / 1000.0);

            record.setStationCallsign(stationSelectedItem.getStationCallsign());
            record.setOperator(settings.getOperator());

            AdiWriter writer = new AdiWriter();
            writer.append(record);

            qsoAdd.setEnabled(false);
            appLogs.setText("");
            AddQsoWorker worker = new AddQsoWorker(writer.toString(), stationSelectedItem);
            worker.execute();

        });



        qsoMode.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                determineFreq();
            }
        });

        qsoBand.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                determineFreq();
            }
        });

        createTimer();
    }

    protected void fillComboBoxes() {
       super.fillComboBoxes();



        Arrays.stream(QsoMode.values()).forEach(qsoMode::addItem);
        qsoMode.setSelectedItem(QsoMode.SSB);

        Arrays.stream(QsoBand.values()).forEach(qsoBand::addItem);
        qsoBand.setSelectedItem(QsoBand.BAND_80m);
    }

    private Component getMainPanel() {
        FormLayout layout = new FormLayout(
                "r:p, 3dlu, f:p:g",
                "p, 3dlu, p, 8dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, f:20dlu:g, 8dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 6dlu, " +
                        "p, 6dlu, f:20dlu:g");

        return FormBuilder.create()
                .layout(layout)
                .padding("10dlu, 10dlu, 10dlu, 10dlu")
                .add(qsoOffline).xyw(1, 1, 3)
                .addLabel("Station:").xy(1,3)
                .add(cloudlogStation).xy(3,3)

                .addLabel("Call:").xy(1,5)
                .add(qsoCall).xy(3,5)
                .addLabel("RST (S):").xy(1,7)
                .add(qsoRstS).xy(3,7)
                .addLabel("RST (R):").xy(1,9)
                .add(qsoRstR).xy(3,9)
                .addLabel("Name:").xy(1,11)
                .add(qsoName).xy(3, 11)
                .addLabel("QTH:").xy(1,13)
                .add(qsoQth).xy(3, 13)
                .addLabel("Comment:").xy(1,15)
                .add(new JScrollPane(qsoComment)).xy(3, 15)

                .addLabel("Date:").xy(1,17)
                .add(qsoDate).xy(3, 17)
                .addLabel("Time:").xy(1,19)
                .add(qsoTime).xy(3, 19)
                .addLabel("Mode:").xy(1,21)
                .add(qsoMode).xy(3, 21)
                .addLabel("Band:").xy(1,23)
                .add(qsoBand).xy(3, 23)
                .addLabel("Freq:").xy(1,25)
                .add(qsoFreq).xy(3, 25)

                .add(qsoAdd).xyw(1, 27, 3)
                .addLabel("Status:"). xy(1,29)
                .add(new JScrollPane(appLogs)).xy(3, 29)
                .build();
    }

    private void createTimer() {
        ActionListener updateClockAction = e -> {
            if (!qsoOffline.isSelected()) {
                qsoDate.setText(dateFormatter.format(getGmtDateTime()));
                qsoTime.setText(timeFormatter.format(getGmtDateTime()));
            }
        };
        timer = new Timer(1000, updateClockAction); // Timer wyzwala się co 1000ms (1 sekunda)
        timer.start();
    }

    private void determineFreq() {
        QsoMode mode = (QsoMode) qsoMode.getSelectedItem();
        QsoBand band = (QsoBand) qsoBand.getSelectedItem();

        if (mode != null && band != null) {
            long freq =
                    switch (mode.getKind()) {
                        case VOICE -> band.getVoiceFreq();
                        case DATA -> band.getDataFreq();
                        case CW -> band.getCwFreq();
                    };

            qsoFreq.setText(String.valueOf(freq));
        }
    }

    class AddQsoWorker extends SwingWorker<Void, Void> {
        private final String qso;
        private final Station station;
        private String status;

        public AddQsoWorker(String qso, Station station) {
            this.qso = qso;
            this.station = station;
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("QSO to add: {}", qso);
            status = service.importQso(settings, station.getStationId(), qso); //FIXME:
            return null;
        }

        @Override
        protected void done() {
            qsoAdd.setEnabled(true);
            appLogs.setText(status);

            qsoCall.setText("");
            qsoRstS.setText("59");
            qsoRstR.setText("59");
            qsoName.setText("");
            qsoQth.setText("");
            qsoComment.setText("");
        }


    }

    private ZonedDateTime getGmtDateTime() {
        return ZonedDateTime.now(ZoneId.of("GMT"));
    }

}
