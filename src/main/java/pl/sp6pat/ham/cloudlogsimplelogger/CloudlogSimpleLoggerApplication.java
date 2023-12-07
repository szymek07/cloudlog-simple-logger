package pl.sp6pat.ham.cloudlogsimplelogger;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import lombok.SneakyThrows;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.qso.QsoBand;
import pl.sp6pat.ham.cloudlogsimplelogger.qso.QsoMode;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.AdifImportPanel;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.SettingsPanel;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;

@SpringBootApplication
public class CloudlogSimpleLoggerApplication extends JFrame  {

	private static Logger log = LoggerFactory.getLogger(CloudlogSimpleLoggerApplication.class);

	private Settings settings;
	private CloudlogIntegrationService service;

	private SettingsPanel settingsPanel;
	private AdifImportPanel adifImportPanel;



	private final JTabbedPane tab = new JTabbedPane();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private Timer timer;
	private final JCheckBox qsoOffline = new JCheckBox("Offline");
	private final JComboBox<Station> qsoStation = new JComboBox<>();
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
	private final JTextArea qsoStatus = new JTextArea();
	private final JButton qsoAdd = new JButton("Add QSO");;


	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(CloudlogSimpleLoggerApplication.class)
				.headless(false).run(args);

		EventQueue.invokeLater(() -> {
			CloudlogSimpleLoggerApplication ex = ctx.getBean(CloudlogSimpleLoggerApplication.class);
			ex.setVisible(true);
		});
	}

	public CloudlogSimpleLoggerApplication() {
		super("Cloudlog Simple Logger");

		initializeComponents();

		loadSettings();
		service = new CloudlogIntegrationService(settings);


		initializeActions();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(getMainPanel());
		setLocationRelativeTo(null);
		pack();

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		fillComboBoxes();
	}

	private void initializeComponents() {

		settingsPanel = new SettingsPanel();
		adifImportPanel = new AdifImportPanel(service, settings);

		qsoStatus.setEditable(false);
		qsoStatus.setEnabled(false);
		qsoStatus.setFocusable(false);
		qsoDate.setEditable(false);
		qsoTime.setEditable(false);



		tab.add("QSO", getQsoPanel());
		tab.add("Import", adifImportPanel);
		tab.add("Settings", settingsPanel);
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

			Station stationSelectedItem = (Station) qsoStation.getSelectedItem();

			Adif3Record record = new Adif3Record();

			record.setCall(qsoCall.getText());
			record.setRstSent(qsoRstS.getText());
			record.setRstRcvd(qsoRstR.getText());
			record.setName(qsoName.getText());
			record.setQth(qsoQth.getText());
			record.setComment(qsoComment.getText());

			record.setQsoDate(LocalDate.parse(qsoDate.getText()));
			record.setTimeOn(LocalTime.parse(qsoTime.getText()));

			QsoMode qsoModeSelectedItem = (QsoMode) qsoMode.getSelectedItem();
			Mode mode = Mode.findByCode(qsoModeSelectedItem.getMode());
			record.setMode(mode);

			QsoBand qsoBandSelectedItem = (QsoBand) qsoBand.getSelectedItem();
			Band band = Band.findByCode(qsoBandSelectedItem.getBand());
			record.setBand(band);

			record.setFreq(Long.parseLong(qsoFreq.getText()) / 1000.0);

			record.setStationCallsign(stationSelectedItem.getStationCallsign());
			record.setOperator(settings.getOperator());			;

			AdiWriter writer = new AdiWriter();
			writer.append(record);

			qsoAdd.setEnabled(false);
			qsoStatus.setText("");
			AddQsoWorker worker = new AddQsoWorker(writer.toString(), stationSelectedItem);
			worker.execute();

		});



		qsoMode.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Wybrany element: " + e.getItem());
				determineFreq();
			}
		});

		qsoBand.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Wybrany element: " + e.getItem());
				determineFreq();
			}
		});

		createTimer();
	}

	private void createTimer() {
		ActionListener updateClockAction = e -> {
			if (!qsoOffline.isSelected()) {
				qsoDate.setText(dateFormat.format(new Date()));
				qsoTime.setText(timeFormat.format(new Date()));
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

	private void loadSettings() {
		Optional<Settings> settingsOpt = SettingsManager.load();

		if (settingsOpt.isPresent()) {
			settings = settingsOpt.get();
			settingsPanel.fillPanel(settings);
		}
	}

	private void fillComboBoxes() {
		SwingWorker<List<Station>, Void> worker = new SwingWorker<List<Station>, Void>() {
			@Override
			protected List<Station> doInBackground() throws Exception {
                return service.getStations();
			}

			@SneakyThrows
			@Override
			protected void done() {
					List<Station> data = get();
					for (Station item : data) {
						qsoStation.addItem(item);
					}
				Optional<Station> activeStation = data.stream().filter(e -> e.getStationActive() > 0).findFirst();
                activeStation.ifPresent(qsoStation::setSelectedItem);
			}
		};
		worker.execute();

		Arrays.stream(QsoMode.values()).forEach(qsoMode::addItem);
		qsoMode.setSelectedItem(QsoMode.SSB);

		Arrays.stream(QsoBand.values()).forEach(qsoBand::addItem);
		qsoBand.setSelectedItem(QsoBand.BAND_80m);
	}


	private Container getMainPanel() {
		FormLayout layout = new FormLayout(
				"f:200dlu:g",
				"f:p:g");

		return FormBuilder.create()
				.layout(layout)
				.padding("10dlu, 10dlu, 10dlu, 10dlu")
				.add(tab).xy(1,1)
				.build();
	}

	private Component getQsoPanel() {
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
				.add(qsoStation).xy(3,3)

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
				.add(new JScrollPane(qsoStatus)).xy(3, 29)
				.build();
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
			qsoStatus.setText(status);

			qsoCall.setText("");
			qsoRstS.setText("59");
			qsoRstR.setText("59");
			qsoName.setText("");
			qsoQth.setText("");
			qsoComment.setText("");
		}


	}





}
