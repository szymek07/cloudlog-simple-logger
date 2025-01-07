package pl.sp6pat.ham.cloudlogsimplelogger.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    private String cloudlogUrl;
    private String apiKey;
    private String operator;
    private String qrzLogin;
    private String qrzPass;
    private Boolean preserveComment;

}
