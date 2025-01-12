package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMContactMessage;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMContactMessageType;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMService;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class N1MMImportPanel extends ImportPanel {

    private static final Logger log = LoggerFactory.getLogger(N1MMImportPanel.class);

    private final N1MMService n1MMService;
    private final JCheckBox enableN1MM = new JCheckBox("Enable N1MM");
    private final JTextField port = new JTextField("9876");

    private DatagramSocket socket;
    private Thread socketThread;
    private volatile boolean running = false;


    public N1MMImportPanel(CloudlogIntegrationService service, SettingsManager settingsMgr, N1MMService n1MMService) {
        super(service, settingsMgr);
        this.n1MMService = n1MMService;
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
            if (enableN1MM.isSelected() && !running) {
                startSocket();
            } else if (!enableN1MM.isSelected() && running){
                stopSocket();
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

    private void startSocket() {
        try {
            int p = Integer.parseInt(port.getText().trim());
            Station stationSelectedItem = (Station) cloudlogStation.getSelectedItem();
            if (stationSelectedItem == null) {
                appLogs.setText("No station was selected\n");
                enableN1MM.setSelected(false);
                return;
            }
            log.debug("Selected port: {}", p);
            socket = new DatagramSocket(p);
            running = true;
            socketThread = new Thread(() -> {
                byte[] buffer = new byte[4024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (running) {
                    try {
                        log.debug("Waiting for message...");

                        socket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        log.info("Received message:\n{}", received);
                        N1MMContactMessage n1mmContact = n1MMService.processXml(received);
                        log.debug("N1MMContactMessage: {}", n1mmContact);
                        Adif3Record r = n1mmContact.getAdif3Record();
                        SwingUtilities.invokeLater(() -> appLogs.append("Received: " + r.getQsoDate() + "  " + r.getTimeOn() + " " + r.getCall() + "\n"));
                        AddQsoWorker worker = new AddQsoWorker(n1mmContact, stationSelectedItem);
                        worker.execute();
                    } catch (Exception ex) {
                        if (running) {
                            log.error("Error while receiving message", ex);
                            SwingUtilities.invokeLater(() -> appLogs.append("Error while receiving message:\n" + ex.getLocalizedMessage() + "\n"));
                        }
                    }
                }
                log.debug("Socket stopping...");
            });
            socketThread.start();
            SwingUtilities.invokeLater(() -> appLogs.setText("Listening for UDP messages on port: " + p + " ...\n"));
            log.debug("Listening for UDP messages on port: {} ...", p);
        } catch (Exception ex) {
            log.error("Datagram socket creation error", ex);
            SwingUtilities.invokeLater(() -> appLogs.append("Datagram socket creation error:\n" + ex.getLocalizedMessage() + "\n"));
            enableN1MM.setSelected(false);
        }
    }

    private void stopSocket() {
        try {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (socketThread != null && socketThread.isAlive()) {
                socketThread.join();
            }
            log.debug("Socket stopped.");
            SwingUtilities.invokeLater(() -> appLogs.append("Listening for UDP messages stopped\n"));
        } catch (Exception ex) {
            log.error("Datagram socket stopping error", ex);
            SwingUtilities.invokeLater(() -> appLogs.append("Datagram socket stopping error:\n" + ex.getLocalizedMessage() + "\n"));

        }
    }

    public void reloadData() {
        super.reloadData();
    }

    class AddQsoWorker extends SwingWorker<Void, Void> {
        private final N1MMContactMessage contact;
        private final Station station;
        private String status;

        public AddQsoWorker(N1MMContactMessage contact, Station station) {
            this.contact = contact;
            this.station = station;
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("N1MM Contact to add: {}", contact);

            if (N1MMContactMessageType.CONTACT_ADD == contact.getType()) {
                status = service.importQso(station.getStationId(), contact.getAdif3Record());
            }
            log.info("N1MM Contact add status: {}", status);
            return null;
        }

        @Override
        protected void done() {
            appLogs.append(status + "\n");
        }
    }

}
