package pl.sp6pat.ham.cloudlogsimplelogger.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsManager {

    private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);

    private Settings settings;

    public void save(Settings settings) {

        File yamlFile = getConfigFile();
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(yamlFile)) {
            yaml.dump(settings, writer);
        } catch (IOException e) {
            log.error("Save settings error", e);
            throw new RuntimeException(e.getMessage());
        }

        load();

    }

    public void load() {
        LoaderOptions loaderOptions = new LoaderOptions();
        TagInspector taginspector = tag -> tag.getClassName().equals(Settings.class.getName());
        loaderOptions.setTagInspector(taginspector);

        File yamlFile = getConfigFile();
        if (yamlFile.exists()) {
            Yaml yaml = new Yaml(new Constructor(Settings.class, loaderOptions));
            try (FileReader reader = new FileReader(yamlFile)) {
                settings = yaml.load(reader);
            } catch (IOException e) {
                log.error("Load settings error", e);
                throw new RuntimeException("Load settings error", e);
            }
        }
    }

    public Settings getSettings() {
        return settings;
    }

    private static File getConfigFile() {
        String homeDirectory = System.getProperty("user.home");
        String directoryPath = homeDirectory + File.separator + ".config" + File.separator + "cloudlog_simple_logger";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory, "cloudlog_simple_logger_settings.yaml");
    }

}
