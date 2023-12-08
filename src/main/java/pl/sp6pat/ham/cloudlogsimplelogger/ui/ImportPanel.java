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

    private static Logger log = LoggerFactory.getLogger(ImportPanel.class);

    protected final CloudlogIntegrationService service;
    protected final Settings settings;

    protected final JComboBox<Station> cloudlogStation = new JComboBox<>();

    public ImportPanel(CloudlogIntegrationService service, Settings settings) {
        this.service = service;
        this.settings = settings;
    }

    protected void fillComboBoxes() {
        SwingWorker<List<Station>, Void> worker = new SwingWorker<List<Station>, Void>() {
            @Override
            protected List<Station> doInBackground() throws Exception {
                return service.getStations();
            }

            @Override
            protected void done() {
                try {
                    List<Station> data = get();
                    for (Station item : data) {
                        cloudlogStation.addItem(item);
                    }
                    Optional<Station> activeStation = data.stream().filter(e -> e.getStationActive() > 0).findFirst();
                    activeStation.ifPresent(cloudlogStation::setSelectedItem);
                } catch (InterruptedException | ExecutionException e) {
                    //FIXME:
                    //qsoStatus.setText("Cloudlog error: " + e.getMessage());
                }
            }
        };

        if (StringUtils.hasText(settings.getCloudlogUrl()) && StringUtils.hasText(settings.getApiKey())) {
            worker.execute();
        } else {
            log.warn("Settings no found.");
        }


    }


}
