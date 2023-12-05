package pl.sp6pat.ham.cloudlogsimplelogger;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.Station;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class CloudlogSimpleLoggerApplication extends JFrame  {

	private static Logger log = LoggerFactory.getLogger(CloudlogSimpleLoggerApplication.class);

	private Settings settings;
	private final JTabbedPane tab = new JTabbedPane();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private final JCheckBox qsoOffline = new JCheckBox("Offline");
	private final JComboBox<String> qsoStation = new JComboBox<>();
	private final JFormattedTextField qsoDate = new JFormattedTextField(dateFormat);
	private final JFormattedTextField qsoTime = new JFormattedTextField(timeFormat);
	private final JComboBox<String> qsoMode = new JComboBox<>();
	private final JComboBox<String> qsoBand = new JComboBox<>();
	private final JTextField qsoFreq = new JTextField();
	private final JTextField qsoCall = new JTextField();
	private final JTextField qsoRstS = new JTextField();
	private final JTextField qsoRstR = new JTextField();
	private final JTextField qsoName = new JTextField();
	private final JTextField qsoLocation = new JTextField();
	private final JTextArea qsoComment = new JTextArea();
	private final JTextArea qsoStatus = new JTextArea();
	private final JButton qsoAdd = new JButton("Add QSO");;

	private final JComboBox<String> adifStation = new JComboBox<>();
	private final JTextField adifPath = new JTextField();
	private final JButton adifBrowse = new JButton("...");
	private final JLabel adifValidQso = new JLabel();
	private final JLabel adifInvalidQso = new JLabel();
	private final JButton adifImport = new JButton("Import");
	private final JProgressBar adifProgress = new JProgressBar();
	private final JTextArea adifLogs  = new JTextArea();

	private final JTextField settCloudlogUrl = new JTextField();
	private final JTextField settApiKey = new JTextField();
	private final JTextField settOperator = new JTextField();
	private final JTextField settQrzLogin = new JTextField();
	private final JPasswordField settQrzPass = new JPasswordField();
	private final JButton settSave = new JButton("Save");


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
		initializeActions();

		loadSettings();

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

		qsoComment.setFocusable(false);
		qsoStatus.setEditable(false);
		qsoStatus.setEnabled(false);
		qsoStatus.setFocusable(false);

		adifLogs.setEditable(false);
		adifLogs.setEnabled(false);

		tab.add("QSO", getQsoPanel());
		tab.add("Import", getImportPanel());
		tab.add("Settings", getSettingsPanel());
	}

	private void initializeActions() {
		settSave.addActionListener(e -> {
			String pass = Base64.getEncoder().encodeToString(String.valueOf(settQrzPass.getPassword()).getBytes());
			Settings settings = Settings.builder()
					.cloudlogUrl(settCloudlogUrl.getText())
					.apiKey(settApiKey.getText())
					.operator(settOperator.getText())
					.qrzLogin(settQrzLogin.getText())
					.qrzPass(pass)
					.build();
			SettingsManager.save(settings);
			JOptionPane.showMessageDialog(this, "Saved");
		});
	}

	private void loadSettings() {
		Optional<Settings> settingsOpt = SettingsManager.load();

		if (settingsOpt.isPresent()) {
			settings = settingsOpt.get();
			settCloudlogUrl.setText(settings.getCloudlogUrl());
			settApiKey.setText(settings.getApiKey());
			settOperator.setText(settings.getOperator());
			settQrzLogin.setText(settings.getQrzLogin());
			byte[] decodedBytes = Base64.getDecoder().decode(settings.getQrzPass());
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			settQrzPass.setText(decodedString);
		}
	}

	private void fillComboBoxes() {
		SwingWorker<List<Station>, Void> worker = new SwingWorker<List<Station>, Void>() {
			@Override
			protected List<Station> doInBackground() throws Exception {
				CloudlogIntegrationService s = new CloudlogIntegrationService(settings);
                return s.getStations();
			}

			@SneakyThrows
			@Override
			protected void done() {
					List<Station> data = get();
					for (Station item : data) {
						qsoStation.addItem(item.toString());
					}
			}
		};
		worker.execute();
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
				.addLabel("Location:").xy(1,13)
				.add(qsoLocation).xy(3, 13)
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

	private Component getImportPanel() {
		FormLayout layout = new FormLayout(
				"p, 3dlu, f:70dlu:g, 3dlu, p",
				"p, 3dlu, p, 3dlu, p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p:g");

		return FormBuilder.create()
				.layout(layout)
				.padding("10dlu, 10dlu, 10dlu, 10dlu")
				.addLabel("ADIF:").xy(1, 1)
				.add(adifPath).xy(3, 1)
				.add(adifBrowse).xy(5,1)
				.addLabel("QSO Count:").xy(1,3)
				.add(adifValidQso).xy(3,3)
				.addLabel("Invalid QSO Count:").xy(1,5)
				.add(adifInvalidQso).xy(3,5)
				.add(adifImport).xyw(1, 7, 5)
				.add(adifProgress).xyw(1,9,5)
				.addLabel("Logi:").xy(1, 11)
				.add(new JScrollPane(adifLogs)).xyw(1, 13, 5)
				.build();
	}

	private Component getSettingsPanel() {
		FormLayout layout = new FormLayout(
				"p, 3dlu, f:50dlu:g, 3dlu, p",
				"f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p, 3dlu, f:p");

		return FormBuilder.create()
				.layout(layout)
				.padding("10dlu, 10dlu, 10dlu, 10dlu")
				.addLabel("Cloudlog URL:").xy(1, 1)
				.add(settCloudlogUrl).xy(3, 1)
				.addLabel("API Key:").xy(1, 3)
				.add(settApiKey).xy(3, 3)
				.addLabel("Operator:").xy(1, 5)
				.add(settOperator).xy(3, 5)
				.addLabel("QRZ Login:").xy(1, 7)
				.add(settQrzLogin).xy(3, 7)
				.addLabel("QRZ Pass:").xy(1, 9)
				.add(settQrzPass).xy(3, 9)
				.add(settSave).xy(3,11)
				.build();
	}




}
