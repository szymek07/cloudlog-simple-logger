package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sp6pat.ham.cloudlogsimplelogger.settings.Settings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private void importQso(Qso qso) {

    }

    private void importQso(String filePath) {
//        Mono<QRZResponse> stringMono = webClient.get()
//                .uri("?username={USER}&password={PASS}&agent={AGENT}", login, pass, AdifToolApplication.PRG_NAME)
//                .accept(MediaType.APPLICATION_XML)
//                .retrieve()
//                .bodyToMono(QRZResponse.class);
//        return stringMono.block();
    }
}
