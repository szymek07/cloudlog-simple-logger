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
import pl.sp6pat.ham.cloudlogsimplelogger.ui.QsoImportPanel;
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
		setLocationRelativeTo(null);
		pack();

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
