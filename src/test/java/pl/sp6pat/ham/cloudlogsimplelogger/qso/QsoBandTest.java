package pl.sp6pat.ham.cloudlogsimplelogger.qso;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.Band;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QsoBandTest {

    @Test
    void cloudlogBandMappingTest() {
        assertEquals(Band.BAND_160m, Band.findByCode(QsoBand.BAND_160m.getBand()));
        assertEquals(Band.BAND_80m, Band.findByCode(QsoBand.BAND_80m.getBand()));
        assertEquals(Band.BAND_60m, Band.findByCode(QsoBand.BAND_60m.getBand()));
        assertEquals(Band.BAND_40m, Band.findByCode(QsoBand.BAND_40m.getBand()));
        assertEquals(Band.BAND_30m, Band.findByCode(QsoBand.BAND_30m.getBand()));
        assertEquals(Band.BAND_20m, Band.findByCode(QsoBand.BAND_20m.getBand()));
        assertEquals(Band.BAND_17m, Band.findByCode(QsoBand.BAND_17m.getBand()));
        assertEquals(Band.BAND_15m, Band.findByCode(QsoBand.BAND_15m.getBand()));
        assertEquals(Band.BAND_12m, Band.findByCode(QsoBand.BAND_12m.getBand()));
        assertEquals(Band.BAND_10m, Band.findByCode(QsoBand.BAND_10m.getBand()));
        assertEquals(Band.BAND_6m, Band.findByCode(QsoBand.BAND_6m.getBand()));
        assertEquals(Band.BAND_4m, Band.findByCode(QsoBand.BAND_4m.getBand()));
        assertEquals(Band.BAND_2m, Band.findByCode(QsoBand.BAND_2m.getBand()));
        assertEquals(Band.BAND_1_25m, Band.findByCode(QsoBand.BAND_1_25m.getBand()));
        assertEquals(Band.BAND_70cm, Band.findByCode(QsoBand.BAND_70cm.getBand()));
        assertEquals(Band.BAND_33cm, Band.findByCode(QsoBand.BAND_33cm.getBand()));
        assertEquals(Band.BAND_23cm, Band.findByCode(QsoBand.BAND_23cm.getBand()));
        assertEquals(Band.BAND_13cm, Band.findByCode(QsoBand.BAND_13cm.getBand()));
        assertEquals(Band.BAND_9cm, Band.findByCode(QsoBand.BAND_9cm.getBand()));
        assertEquals(Band.BAND_6cm, Band.findByCode(QsoBand.BAND_6cm.getBand()));
        assertEquals(Band.BAND_3cm, Band.findByCode(QsoBand.BAND_3cm.getBand()));
        assertEquals(Band.BAND_1_25cm, Band.findByCode(QsoBand.BAND_1_25cm.getBand()));
    }

}