package pl.sp6pat.ham.cloudlogsimplelogger.qrz;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class QRZSearchResult {

    @XmlElement(name = "call")
    private String call;

    @XmlElement(name = "fname")
    private String fname;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "addr1")
    private String addrLine1;

    @XmlElement(name = "addr2")
    private String addrLine2;

    @XmlElement(name = "zip")
    private String zip;

    @XmlElement(name = "country")
    private String country;

    @XmlElement(name = "ituzone")
    private Integer itu;

    @XmlElement(name = "cqzone")
    private Integer cq;

    @XmlElement(name = "grid")
    private String grid;

}
