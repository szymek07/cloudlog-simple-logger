package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QsoResult {

    private String status;
    private String type;
    private String string;
    private String reason;

}
