package pl.sp6pat.ham.cloudlogsimplelogger;

import com.jgoodies.forms.builder.FormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import pl.sp6pat.ham.cloudlogsimplelogger.cloudlog.CloudlogIntegrationService;
import pl.sp6pat.ham.cloudlogsimplelogger.jtdx.JTDXService;
import pl.sp6pat.ham.cloudlogsimplelogger.n1mm.N1MMService;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;
import pl.sp6pat.ham.cloudlogsimplelogger.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

@SpringBootApplication
public class CloudlogSimpleLoggerApplication extends JFrame  {

	private static final Logger log = LoggerFactory.getLogger(CloudlogSimpleLoggerApplication.class);

	public final static String PRG_NAME = "cloudlog-simple-logger";

	private SettingsManager settingsMgr;
	private final CloudlogIntegrationService service;
	private final N1MMService n1mmService;
	private final JTDXService jtdxService;

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
		service = new CloudlogIntegrationService(settingsMgr);
		n1mmService = new N1MMService();
		jtdxService = new JTDXService();
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
		QsoImportPanel qsoImportPanel = new QsoImportPanel(service, settingsMgr);
		AdifImportPanel adifImportPanel = new AdifImportPanel(service, settingsMgr);
		N1MMImportPanel n1MMImportPanel = new N1MMImportPanel(service, settingsMgr, n1mmService);
		JTDXImportPanel jtdxImportPanel = new JTDXImportPanel(service, settingsMgr, jtdxService);
		SettingsPanel settingsPanel = new SettingsPanel(settingsMgr);

		tab.add("QSO", qsoImportPanel);
		tab.add("Import", adifImportPanel);
		tab.add("N1MM", n1MMImportPanel);
		tab.add("JTDX", jtdxImportPanel);
		tab.add("Settings", settingsPanel);

		tab.addChangeListener(e -> {
            int selectedIndex = tab.getSelectedIndex();
			log.debug("Active tab index: {}", selectedIndex);

            switch (selectedIndex) {
                case 0:
                    qsoImportPanel.reloadData();
                    break;
                case 1:
                    adifImportPanel.reloadData();
                    break;
				case 2:
					n1MMImportPanel.reloadData();
					break;
				case 3:
					jtdxImportPanel.reloadData();
					break;
                case 4:
                    settingsPanel.reloadData();
                    break;
                default:
                    log.debug("Unknown tab");
                    break;
            }
        });
	}

	private void loadSettings() {
		settingsMgr = new SettingsManager();
		settingsMgr.load();
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
