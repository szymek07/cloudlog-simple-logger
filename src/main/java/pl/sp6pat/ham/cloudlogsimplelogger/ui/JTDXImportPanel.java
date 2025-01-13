package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.jtdx.JTDXService;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMContactMessage;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMContactMessageType;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMService;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class JTDXImportPanel extends ImportPanel {

    private static final Logger log = LoggerFactory.getLogger(JTDXImportPanel.class);

    private final JTDXService jtdxService;
    private final JCheckBox enableJtdx = new JCheckBox("Enable JTDX");
    private final JTextField port = new JTextField("9876");

    private DatagramSocket socket;
    private Thread socketThread;
    private volatile boolean running = false;


    public JTDXImportPanel(CloudlogIntegrationService service, SettingsManager settingsMgr, JTDXService jtdxService) {
        super(service, settingsMgr);
        this.jtdxService = jtdxService;
        initializeComponents();
        reloadData();
        initializeActions();
        this.setLayout(new FormLayout("f:p:g", "f:p:g"));
        this.add(getMainPanel(), new CellConstraints().xy(1, 1));
    }

    private void initializeComponents() {

    }

    private void initializeActions() {
        enableJtdx.addChangeListener(e -> {
            if (enableJtdx.isSelected() && !running) {
                startSocket();
            } else if (!enableJtdx.isSelected() && running){
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

                .add(enableJtdx).xyw(1, 3, 3)

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
                enableJtdx.setSelected(false);
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
                        Adif3Record adiContact = jtdxService.processAdi(received);
                        log.debug("JTDX contact message: {}", adiContact);
                        SwingUtilities.invokeLater(() -> appLogs.append("Received: " + adiContact.getQsoDate() + "  " + adiContact.getTimeOn() + " " + adiContact.getCall() + "\n"));
                        AddQsoWorker worker = new AddQsoWorker(adiContact, stationSelectedItem);
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
            enableJtdx.setSelected(false);
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
        private final Adif3Record contact;
        private final Station station;
        private String status;

        public AddQsoWorker(Adif3Record contact, Station station) {
            this.contact = contact;
            this.station = station;
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("JTDX Contact to add: {}", contact);
            status = service.importQso(station.getStationId(), contact);
            log.info("JTDX Contact add status: {}", status);
            return null;
        }

        @Override
        protected void done() {
            appLogs.append(status + "\n");
        }
    }

}
