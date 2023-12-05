package pl.sp6pat.ham.cloudlogsimplelogger.qso;

public enum QsoMode {
    AM("AM", QsoKind.VOICE),
    FM("FM", QsoKind.VOICE),
    CW("CW", QsoKind.CW),
    SSB("SSB", QsoKind.VOICE),
    SSB_LSB("LSB", QsoKind.VOICE),
    SSB_USB("USB", QsoKind.VOICE),
    FT8("FT8", QsoKind.DATA);

    private final String mode;
    private final QsoKind kind;

    QsoMode(String mode, QsoKind kind) {
        this.mode = mode;
        this.kind = kind;
    }

    public QsoKind getQsoKind() {
        return this.kind;
    }

}
