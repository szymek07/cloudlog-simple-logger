package pl.sp6pat.ham.cloudlogsimplelogger.qso;

import lombok.Getter;

@Getter
public enum QsoMode {
    AM("AM", QsoKind.VOICE),
    FM("FM", QsoKind.VOICE),
    CW("CW", QsoKind.CW),
    SSB("SSB", QsoKind.VOICE),
    FT8("FT8", QsoKind.DATA);

    private final String mode;
    private final QsoKind kind;

    QsoMode(String mode, QsoKind kind) {
        this.mode = mode;
        this.kind = kind;
    }

}
