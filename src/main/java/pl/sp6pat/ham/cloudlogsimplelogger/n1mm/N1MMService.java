package pl.sp6pat.ham.cloudlogsimplelogger.n1mm;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;

public class N1MMService {

    private static final Logger log = LoggerFactory.getLogger(N1MMService.class);

    private Document document;

    public N1MMContactMessage processXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            String rootTagName = document.getDocumentElement().getTagName();
            log.debug("First tag: {}", rootTagName);


            switch (rootTagName) {
                case "contactinfo":     return convertToNewQso();
                case "contactreplace":
                case "contactdelete":
                                        log.warn("Not supported yet");
                                        break;

            }
        } catch (Exception ex) {
            log.error("Error parsing XML: {}", ex.getMessage(), ex);
        }
        return null;
    }

    private N1MMContactMessage convertToNewQso() {
        Adif3Record record = new Adif3Record();
        record.setCall(getTagContentByName("call"));
        record.setRstSent(getTagContentByName("snt"));
        record.setRstRcvd(getTagContentByName("rcv"));
        record.setName(getTagContentByName("name"));
        record.setQth(getTagContentByName("qth"));
        record.setComment(getTagContentByName("comment"));
        record.setStationCallsign(getTagContentByName("mycall"));

        record.setOperator(getTagContentByName("operator"));

        String timestamp = getTagContentByName("timestamp");
        String [] splittedTimestamp = timestamp.split(" ", 2);
        LocalDate date = LocalDate.parse(splittedTimestamp[0]);
        LocalTime time = LocalTime.parse(splittedTimestamp[1]);
        record.setQsoDate(date);
        record.setTimeOn(time);

        String modeStr = getTagContentByName("mode");
        if ("USB".equals(modeStr) || "LSB".equals(modeStr)) {
            modeStr = "SSB";
        }
        Mode mode = Mode.findByCode(modeStr);
        record.setMode(mode);

        Band band = determineBand(getTagContentByName("band"));
        record.setBand(band);

        Double freqRx = Double.parseDouble(getTagContentByName("rxfreq"))/1000_000.0;
        record.setFreqRx(freqRx);

        Double freqTx = Double.parseDouble(getTagContentByName("txfreq"))/1000_000.0;
        record.setFreq(freqTx);

        N1MMContactMessage n1mmContact = N1MMContactMessage.builder().type(N1MMContactMessageType.CONTACT_ADD).adif3Record(record).build();
        return n1mmContact;
    }

    private String getTagContentByName(String tagName) {
        String tagContent = document.getElementsByTagName(tagName).item(0).getTextContent();
        log.debug("Tag content of {}: {}", tagName, tagContent);
        return tagContent;
    }

    private Band determineBand(String n1mmBand) {
        return switch (n1mmBand) {
            case "1,8" -> Band.BAND_160m;
            case "3,5" -> Band.BAND_80m;
            case "7" -> Band.BAND_40m;
            case "10" -> Band.BAND_30m;
            case "14" -> Band.BAND_20m;
            case "18" -> Band.BAND_17m;
            case "21" -> Band.BAND_15m;
            case "24" -> Band.BAND_12m;
            case "28" -> Band.BAND_10m;
            default -> null;
        };
    }
}
