package pl.sp6pat.ham.cloudlogsimplelogger;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.AdifImportPanel;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.QsoImportPanel;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Optional;

@SpringBootApplication
public class CloudlogSimpleLoggerApplication extends JFrame  {

	private static final Logger log = LoggerFactory.getLogger(CloudlogSimpleLoggerApplication.class);

	public final static String PRG_NAME = "cloudlog-simple-logger";

	private Settings settings;
	private final CloudlogIntegrationService service;

	private final JTabbedPane tab = new JTabbedPane();


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
		loadSettings();
		service = new CloudlogIntegrationService(settings);
		initializeComponents();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(getMainPanel());
		pack();
		setLocationRelativeTo(null);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void initializeComponents() {
		SettingsPanel settingsPanel = new SettingsPanel(settings);
		AdifImportPanel adifImportPanel = new AdifImportPanel(service, settings);
		QsoImportPanel qsoImportPanel = new QsoImportPanel(service, settings);

		tab.add("QSO", qsoImportPanel);
		tab.add("Import", adifImportPanel);
		tab.add("Settings", settingsPanel);
	}

	private void loadSettings() {
		Optional<Settings> settingsOpt = SettingsManager.load();
        settingsOpt.ifPresent(value -> settings = value);
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

}
