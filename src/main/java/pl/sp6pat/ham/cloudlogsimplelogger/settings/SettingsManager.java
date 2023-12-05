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
import java.util.Optional;

public class SettingsManager {

    private static Logger log = LoggerFactory.getLogger(SettingsManager.class);

    public static void save(Settings settings) {

        File yamlFile = getConfigFile();
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(yamlFile)) {
            yaml.dump(settings, writer);
        } catch (IOException e) {
            log.error("Save settings error", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public static Optional<Settings> load() {
        LoaderOptions loaderOptions = new LoaderOptions();
        TagInspector taginspector = tag -> tag.getClassName().equals(Settings.class.getName());
        loaderOptions.setTagInspector(taginspector);

        File yamlFile = getConfigFile();
        Yaml yaml = new Yaml(new Constructor(Settings.class, loaderOptions));
        try (FileReader reader = new FileReader(yamlFile)) {
            return Optional.ofNullable(yaml.load(reader));
        } catch (IOException e) {
            log.error("Load settings error", e);
            throw new RuntimeException("Load settings error", e);
        }
    }

    private static File getConfigFile() {
        String homeDirectory = System.getProperty("user.home");
        String directoryPath = homeDirectory + File.separator + ".config" + File.separator + "cloudlog_simple_logger";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File yamlFile = new File(directory, "cloudlog_simple_logger_settings.yaml");
        return yamlFile;
    }

}
