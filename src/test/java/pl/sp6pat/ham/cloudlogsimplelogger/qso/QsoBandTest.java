package pl.sp6pat.ham.cloudlogsimplelogger.qso;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(Band.BAND_70cm, Band.findByCode(QsoBand.BAND_70cm.getBand()));
    }

}