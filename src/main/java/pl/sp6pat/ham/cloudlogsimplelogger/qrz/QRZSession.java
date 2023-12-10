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
public class QRZSession {

    @XmlElement(name = "Key")
    private String key;

    @XmlElement(name = "Count")
    private Integer count;

    @XmlElement(name = "SubExp")
    private String subExp;


}
