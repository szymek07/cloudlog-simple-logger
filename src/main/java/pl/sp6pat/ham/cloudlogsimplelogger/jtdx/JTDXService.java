package pl.sp6pat.ham.cloudlogsimplelogger.jtdx;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

public class JTDXService {

    private static final Logger log = LoggerFactory.getLogger(JTDXService.class);

    public Adif3Record processAdi(String adifRecord) {
        StringReader stringReader = new StringReader(adifRecord);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        AdiReader ar = new AdiReader();
        try {
            Optional<Adif3> adif3 = ar.read(bufferedReader);
            if (adif3.isPresent()) {
                List<Adif3Record> records = adif3.get().getRecords();
                Adif3Record first = records.get(0);
                return first;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
