package pl.sp6pat.ham.cloudlogsimplelogger.qso;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.Mode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QsoModeTest {
    @Test
    void cloudlogModeMappingTest() {
        assertEquals(Mode.AM, Mode.findByCode(QsoMode.AM.getMode()));
        assertEquals(Mode.FM, Mode.findByCode(QsoMode.FM.getMode()));
        assertEquals(Mode.CW, Mode.findByCode(QsoMode.CW.getMode()));
        assertEquals(Mode.SSB, Mode.findByCode(QsoMode.SSB.getMode()));
        assertEquals(Mode.FT8, Mode.findByCode(QsoMode.FT8.getMode()));
    }
}