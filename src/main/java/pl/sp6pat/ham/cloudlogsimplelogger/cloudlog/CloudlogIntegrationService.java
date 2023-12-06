package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.Yaml;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;

public class CloudlogIntegrationService {

    private static Logger log = LoggerFactory.getLogger(CloudlogIntegrationService.class);

    private final Settings settings;

    private final WebClient webClient;

    public CloudlogIntegrationService(Settings settings) {
        this.settings = settings;

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    clientDefaultCodecsConfigurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                })
                .build();

        webClient =  WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(settings.getCloudlogUrl())
                .build();
    }
    public List<Station> getStations() {
        return webClient.get()
                .uri("/index.php/api/station_info/{API_KEY}", settings.getApiKey())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Station.class)
                .collectList()
                .block();
    }

    public void importQso(Settings setting, Integer stationId, String qso) throws JsonProcessingException {

        Qso qsoRequest = Qso.builder()
                .key(setting.getApiKey())
                .stationProfileId(String.valueOf(stationId))
                .type("adif")
                .string(qso)
                .build();

        log.info("QSO Request:");
        log.info(qsoRequest.toString());

        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String body = objectMapper.writeValueAsString(qsoRequest) ;

        log.info(body);

        String result = this.webClient.post()
                .uri("/index.php/api/qso")
                .body(Mono.just(body), String.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Result");
        log.info(result.toString());
    }


    public void importQso(File file) {
//        Mono<QRZResponse> stringMono = webClient.get()
//                .uri("?username={USER}&password={PASS}&agent={AGENT}", login, pass, AdifToolApplication.PRG_NAME)
//                .accept(MediaType.APPLICATION_XML)
//                .retrieve()
//                .bodyToMono(QRZResponse.class);
//        return stringMono.block();
    }
}
