package pl.sp6pat.ham.cloudlogsimplelogger.qrz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sp6pat.ham.cloudlogsimplelogger.CloudlogSimpleLoggerApplication;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class QRZService {

    private static final Logger log = LoggerFactory.getLogger(QRZService.class);

    public static final String URL = "https://xmldata.qrz.com/xml/current/";
    private final WebClient webClient;
    private final String login;
    private final String pass;

    private QRZResponse authorization;

    public QRZService(String login, String pass) {
        this.login = login;
        this.pass = pass;

        ExchangeStrategies strategies = ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    clientDefaultCodecsConfigurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                })
                .build();

        webClient =  WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(URL)
                .build();
    }

    private QRZResponse authorize() {
        Mono<QRZResponse> stringMono = webClient.get()
                .uri("?username={USER}&password={PASS}&agent={AGENT}", login, pass, CloudlogSimpleLoggerApplication.PRG_NAME)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(QRZResponse.class);
        return stringMono.block();
    }

    public Optional<QRZSearchResult> qrzCallsignSearch(String call) {
        if (authorization == null) {
            authorization = authorize();
        }

        Mono<QRZResponse> stringMono = webClient.get()
                .uri("?s={KEY}&callsign={CALL}", authorization.getSession().getKey(), call)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(QRZResponse.class);
        QRZResponse block = stringMono.block();
        if (block != null && block.getCallsign() != null) {
            return Optional.of(block.getCallsign());
        } else {
            return Optional.empty();
        }
    }

}
