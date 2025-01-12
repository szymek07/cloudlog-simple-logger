package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.SettingsManager;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudlogIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CloudlogIntegrationService.class);

    private final SettingsManager settingsMgr;

    public CloudlogIntegrationService(SettingsManager settingsMgr) {
        this.settingsMgr = settingsMgr;

    }
    public List<Station> getStations() {
        Settings settings = settingsMgr.getSettings();
        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        return getWebClient().get()
                .uri("/index.php/api/station_info/{API_KEY}", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Station.class)
                .collectList()
                .block();
    }

    public String importQso(String stationId, Adif3Record qso) throws JsonProcessingException {
        Settings settings = settingsMgr.getSettings();
        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        AdiWriter writer = new AdiWriter();
        writer.append(qso);

        String qsoStr = replaceCommaToDotInFreq(writer.toString());

        Qso qsoRequest = Qso.builder()
                .key(apiKey)
                .stationProfileId(String.valueOf(stationId))
                .type("adif")
                .string(qsoStr)
                .build();

        return uploadToCloudlog(qsoRequest);
    }

    private String uploadToCloudlog(Qso qsoRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(qsoRequest) ;

        log.debug(body);

        QsoResult result = getWebClient().post()
                .uri("/index.php/api/qso")
                .body(Mono.just(body), String.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(QsoResult.class)
                .block();

        if (result == null) {
            return "QSO Not added";
        } else if ("created".equalsIgnoreCase(result.getStatus())) {
            String call = getCallsign(result.getString());
            return call + ": QSO added";
        } else {
            return null;
        }
    }

    private static String getCallsign(String adiRecord)  {
        StringReader stringReader = new StringReader(adiRecord);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        AdiReader ar = new AdiReader();
        try {
            Optional<Adif3> adif3 = ar.read(bufferedReader);
            if (adif3.isPresent()) {
                List<Adif3Record> records = adif3.get().getRecords();
                Adif3Record first = records.get(0);
                return first.getCall();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "???";
    }

    private String replaceCommaToDotInFreq(String input) {
        String result = input;
        String regex = "(<FREQ:\\d+>)(\\d+),(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        if (matcher.find()) {
            log.warn("Replaced comma to dot in freq: {}", result);
            result = matcher.replaceFirst(matcher.group(1) + matcher.group(2) + "." + matcher.group(3));
        }

        regex = "(<FREQ_RX:\\d+>)(\\d+),(\\d+)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(result);

        if (matcher.find()) {
            log.warn("Replaced comma to dot in freq rx: {}", result);
            result = matcher.replaceFirst(matcher.group(1) + matcher.group(2) + "." + matcher.group(3));
        }

        return result;
    }

    private WebClient getWebClient() {
        Settings settings = settingsMgr.getSettings();
        if (settings != null && StringUtils.hasText(settings.getCloudlogUrl()) && StringUtils.hasText(settings.getApiKey())) {
            return WebClient.builder()
                    .baseUrl(settings.getCloudlogUrl())
                    .build();
        }
        return null;
    }

}
