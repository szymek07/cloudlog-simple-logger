package pl.sp6pat.ham.cloudlogsimplelogger.qso;

public enum QsoBand {

    BAND_160m("160m", 1.9f, 1.838f, 1.83f),
    BAND_80m("80m", 3.7f, 3.583f, 3.55f),
    BAND_60m("60m", 5.33f, 5.33f, 5.26f),
    BAND_40m("40m", 7.1f, 7.04f, 7.02f),
    BAND_30m("30m", 10.12f, 10.145f, 10.12f),
    BAND_20m("20m", 14.2f, 14.08f, 14.02f),
    BAND_17m("17m", 18.13f, 18.105f, 18.08f),
    BAND_15m("15m", 21.3f, 21.08f, 21.02f),
    BAND_12m("12m", 24.95f, 24.925f, 24.9f),
    BAND_10m("10m", 28.3f, 28.12f, 28.05f),
    BAND_6m("6m", 50.15f, 50.23f, 50.09f),
    BAND_4m("4m", 70.2f, 70.2f, 70.2f),
    BAND_2m("2m", 144.3f, 144.37f, 144.05f),
    BAND_1_25m("1.25m", 222.1f, 222.1f, 222.1f),
    BAND_70cm("70cm", 432.2f, 432.088f, 432.05f),
    BAND_33cm("33cm", 902.1f, 902.1f, 902.1f),
    BAND_23cm("23cm", 1296.0f, 1296.138f, 1296.0f),
    BAND_13cm("13cm", 2320.8f, 2320.8f, 2320.8f),
    BAND_9cm("9cm", 3410.0f, 3410.0f, 3400.0f),
    BAND_6cm("6cm", 5670.0f, 5670.0f, 5670.0f),
    BAND_3cm("3cm", 10225.0f, 10225.0f, 10225.0f),
    BAND_1_25cm("1.25cm",24000.0f, 24000.0f, 24000.0f)
    ;

    private final String band;
    private final float voiceFreq;
    private final float dataFreq;
    private final float cwFreq;

    QsoBand(String band, float voiceFreq, float dataFreq, float cwFreq) {
        this.band = band;
        this.voiceFreq = voiceFreq;
        this.dataFreq = dataFreq;
        this.cwFreq = cwFreq;
    }



    @Override
    public String toString() {
        return band;
    }

    public float getVoiceFreq() {
        return voiceFreq;
    }

    public float getDataFreq() {
        return dataFreq;
    }

    public float getCwFreq() {
        return cwFreq;
    }

    public String getBand() {
        return band;
    }
}
