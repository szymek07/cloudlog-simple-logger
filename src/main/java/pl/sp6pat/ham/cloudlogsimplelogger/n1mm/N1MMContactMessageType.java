package pl.sp6pat.ham.cloudlogsimplelogger.n1mm;

import lombok.Getter;

@Getter
public enum N1MMContactMessageType {
    CONTACT_ADD("contactinfo"),
    CONTACT_REPLACE("contactreplace"),
    CONTACT_DELETE("contactdelete")
    ;

    private final String tag;

    N1MMContactMessageType(String tag) {
        this.tag = tag;
    }

}
