package pl.sp6pat.ham.cloudlogsimplelogger.qso;

public enum QsoBand {

    BAND_160m("160m", 1900000, 1838000, 1830000),

    BAND_80m("80m", 3700000, 3583000, 3550000),
    BAND_60m("60m", 5330000, 5330000, 5260000),
    BAND_40m("40m", 7100000, 7040000, 7020000),
    BAND_30m("30m", 10120000, 10145000, 10120000),
    BAND_20m("20m", 14200000, 14080000, 14020000),
    BAND_17m("17m", 18130000, 18105000, 18080000),
    BAND_15m("15m", 21300000, 21080000, 21020000),
    BAND_12m("12m", 24950000, 24925000, 24900000),
    BAND_10m("10m", 28300000, 28120000, 28050000),
    BAND_6m("6m", 50150000, 50230000, 50090000),
    BAND_4m("4m", 70200000, 70200000, 70200000),
    BAND_2m("2m", 144300000, 144370000, 144050000),
    BAND_70cm("70cm", 432200000, 432088000, 432050000);

    ;

    private final String band;
    private final long voiceFreq;
    private final long dataFreq;
    private final long cwFreq;

    QsoBand(String band, long voiceFreq, long dataFreq, long cwFreq) {
        this.band = band;
        this.voiceFreq = voiceFreq;
        this.dataFreq = dataFreq;
        this.cwFreq = cwFreq;
    }



    @Override
    public String toString() {
        return band;
    }

    public long getVoiceFreq() {
        return voiceFreq;
    }

    public long getDataFreq() {
        return dataFreq;
    }

    public long getCwFreq() {
        return cwFreq;
    }

    public String getBand() {
        return band;
    }
}
