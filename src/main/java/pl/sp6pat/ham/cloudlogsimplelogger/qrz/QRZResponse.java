package pl.sp6pat.ham.cloudlogsimplelogger.qrz;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@XmlRootElement(name = "QRZDatabase", namespace = "http://xmldata.qrz.com")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class QRZResponse {

    @XmlElement(name = "Session")
    private QRZSession session;

    @XmlElement(name = "Callsign")
    private QRZSearchResult callsign;

}


