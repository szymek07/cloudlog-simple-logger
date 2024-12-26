package pl.sp6pat.ham.cloudlogsimplelogger.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public abstract class ImportPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(ImportPanel.class);

    protected final CloudlogIntegrationService service;
    protected final Settings settings;

    protected final JComboBox<Station> cloudlogStation = new JComboBox<>();
    protected final JTextArea appLogs  = new JTextArea();

    public ImportPanel(CloudlogIntegrationService service, Settings settings) {
        this.service = service;
        this.settings = settings;

        appLogs.setEditable(false);
        appLogs.setLineWrap(true);
    }

    protected void fillComboBoxes() {
        SwingWorker<List<Station>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Station> doInBackground() {
                return service.getStations();
            }

            @Override
            protected void done() {
                try {
                    List<Station> data = get();
                    for (Station item : data) {
                        cloudlogStation.addItem(item);
                    }
                    Optional<Station> activeStation = data.stream().filter(e -> e.getStationActive() != null).findFirst();
                    activeStation.ifPresent(cloudlogStation::setSelectedItem);
                } catch (InterruptedException | ExecutionException e) {
                    appLogs.setText("Cloudlog error: " + e.getMessage());
                }
            }
        };

        if (settings != null && StringUtils.hasText(settings.getCloudlogUrl()) && StringUtils.hasText(settings.getApiKey())) {
            worker.execute();
        } else {
            log.warn("Settings not found.");
        }


    }


}
