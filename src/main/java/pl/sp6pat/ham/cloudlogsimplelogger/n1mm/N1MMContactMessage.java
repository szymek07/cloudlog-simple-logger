package pl.sp6pat.ham.cloudlogsimplelogger.n1mm;

import lombok.Builder;
import lombok.Data;
import org.marsik.ham.adif.Adif3Record;

@Data
@Builder
public class N1MMContactMessage {

    private N1MMContactMessageType type;

    private final Adif3Record adif3Record;

}
