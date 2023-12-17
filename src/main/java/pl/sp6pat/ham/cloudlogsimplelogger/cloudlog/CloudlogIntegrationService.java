package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudlogIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CloudlogIntegrationService.class);

    private final Settings settings;

    private WebClient webClient;

    public CloudlogIntegrationService(Settings settings) {
        this.settings = settings;

        if (settings != null && StringUtils.hasText(settings.getCloudlogUrl()) && StringUtils.hasText(settings.getApiKey())) {
            webClient = WebClient.builder()
                    .baseUrl(settings.getCloudlogUrl())
                    .build();
        }
    }
    public List<Station> getStations() {
        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        return webClient.get()
                .uri("/index.php/api/station_info/{API_KEY}", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Station.class)
                .collectList()
                .block();
    }

    public String importQso(String stationId, String qso) throws JsonProcessingException {
        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        Qso qsoRequest = Qso.builder()
                .key(apiKey)
                .stationProfileId(String.valueOf(stationId))
                .type("adif")
                .string(qso)
                .build();

        return uploadToCloudlog(qsoRequest);
    }

    public String importQso(String stationId, Adif3Record qso) throws JsonProcessingException {
        byte[] apiKeyDecodedBytes = Base64.getDecoder().decode(settings.getApiKey());
        String apiKey = new String(apiKeyDecodedBytes, StandardCharsets.UTF_8);
        AdiWriter writer = new AdiWriter();
        writer.append(qso);

        Qso qsoRequest = Qso.builder()
                .key(apiKey)
                .stationProfileId(String.valueOf(stationId))
                .type("adif")
                .string(writer.toString())
                .build();

        return uploadToCloudlog(qsoRequest);
    }

    private String uploadToCloudlog(Qso qsoRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(qsoRequest) ;

        log.debug(body);

        String result = this.webClient.post()
                .uri("/index.php/api/qso")
                .body(Mono.just(body), String.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (result == null) {
            return "QSO Not added";
        }

        Pattern pattern = Pattern.compile("Message: (.+?)</p>");
        Matcher matcher = pattern.matcher(result);

        log.debug("Result:\n" + result);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "QSO Added";
        }
    }

}
