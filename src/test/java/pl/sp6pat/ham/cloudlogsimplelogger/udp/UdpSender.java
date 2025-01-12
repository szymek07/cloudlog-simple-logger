package pl.sp6pat.ham.cloudlogsimplelogger.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender {
    public static void main(String[] args) {
        String message = ADD_CONTACT;
        String host = "localhost";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] data = message.getBytes();
            InetAddress address = InetAddress.getByName(host);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            System.out.println("Wysłano pakiet: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String ADD_CONTACT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<contactinfo>\n" +
            "\t<app>N1MM</app>\n" +
            "\t<contestname>DX</contestname>\n" +
            "\t<dbname>N1MM DXLog.s3db</dbname>\n" +
            "\t<contestnr>0</contestnr>\n" +
            "\t<timestamp>2025-01-11 23:59:21</timestamp>\n" +
            "\t<mycall>SP6PAT</mycall>\n" +
            "\t<band>14</band>\n" +
            "\t<rxfreq>1410000</rxfreq>\n" +
            "\t<txfreq>1415000</txfreq>\n" +
            "\t<operator>SP6PAT</operator>\n" +
            "\t<mode>USB</mode>\n" +
            "\t<call>A2G</call>\n" +
            "\t<countryprefix>A2</countryprefix>\n" +
            "\t<wpxprefix>A2</wpxprefix>\n" +
            "\t<stationprefix>SP6PAT</stationprefix>\n" +
            "\t<continent>AF</continent>\n" +
            "\t<snt>59</snt>\n" +
            "\t<sntnr>11</sntnr>\n" +
            "\t<rcv>59</rcv>\n" +
            "\t<rcvnr>0</rcvnr>\n" +
            "\t<gridsquare></gridsquare>\n" +
            "\t<exchange1></exchange1>\n" +
            "\t<section></section>\n" +
            "\t<comment></comment>\n" +
            "\t<qth></qth>\n" +
            "\t<name>ATEST</name>\n" +
            "\t<power></power>\n" +
            "\t<misctext></misctext>\n" +
            "\t<zone>38</zone>\n" +
            "\t<prec></prec>\n" +
            "\t<ck>0</ck>\n" +
            "\t<ismultiplier1>1</ismultiplier1>\n" +
            "\t<ismultiplier2>0</ismultiplier2>\n" +
            "\t<ismultiplier3>0</ismultiplier3>\n" +
            "\t<points>1</points>\n" +
            "\t<radionr>1</radionr>\n" +
            "\t<run1run2>1</run1run2>\n" +
            "\t<RoverLocation></RoverLocation>\n" +
            "\t<RadioInterfaced>0</RadioInterfaced>\n" +
            "\t<NetworkedCompNr>0</NetworkedCompNr>\n" +
            "\t<IsOriginal>True</IsOriginal>\n" +
            "\t<NetBiosName>PREC5510</NetBiosName>\n" +
            "\t<IsRunQSO>0</IsRunQSO>\n" +
            "\t<StationName>PREC5510</StationName>\n" +
            "\t<ID>3351521482714e969630690ccaedb343</ID>\n" +
            "\t<IsClaimedQso>1</IsClaimedQso>\n" +
            "\t<oldtimestamp>2025-01-11 23:59:21</oldtimestamp>\n" +
            "\t<oldcall>A2G</oldcall>\n" +
            "\t<SentExchange />\n" +
            "</contactinfo>";

    private static final String EDIT_CONTACT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<contactreplace>\n" +
            "\t<app>N1MM</app>\n" +
            "\t<contestname>DX</contestname>\n" +
            "\t<dbname>N1MM DXLog.s3db</dbname>\n" +
            "\t<contestnr>0</contestnr>\n" +
            "\t<timestamp>2025-01-10 18:39:51</timestamp>\n" +
            "\t<mycall>SP6PAT</mycall>\n" +
            "\t<band>10</band>\n" +
            "\t<rxfreq>1015000</rxfreq>\n" +
            "\t<txfreq>1015000</txfreq>\n" +
            "\t<operator>SP6PAT</operator>\n" +
            "\t<mode>USB</mode>\n" +
            "\t<call>A1C</call>\n" +
            "\t<countryprefix></countryprefix>\n" +
            "\t<wpxprefix>A1</wpxprefix>\n" +
            "\t<stationprefix>SP6PAT</stationprefix>\n" +
            "\t<continent></continent>\n" +
            "\t<snt>59</snt>\n" +
            "\t<sntnr>11</sntnr>\n" +
            "\t<rcv>59</rcv>\n" +
            "\t<rcvnr>0</rcvnr>\n" +
            "\t<gridsquare> </gridsquare>\n" +
            "\t<exchange1></exchange1>\n" +
            "\t<section></section>\n" +
            "\t<comment></comment>\n" +
            "\t<qth></qth>\n" +
            "\t<name></name>\n" +
            "\t<power></power>\n" +
            "\t<misctext> </misctext>\n" +
            "\t<zone>0</zone>\n" +
            "\t<prec></prec>\n" +
            "\t<ck>0</ck>\n" +
            "\t<ismultiplier1>1</ismultiplier1>\n" +
            "\t<ismultiplier2>0</ismultiplier2>\n" +
            "\t<ismultiplier3>0</ismultiplier3>\n" +
            "\t<points>1</points>\n" +
            "\t<radionr>1</radionr>\n" +
            "\t<run1run2>1</run1run2>\n" +
            "\t<RoverLocation> </RoverLocation>\n" +
            "\t<RadioInterfaced>0</RadioInterfaced>\n" +
            "\t<NetworkedCompNr>0</NetworkedCompNr>\n" +
            "\t<IsOriginal>True</IsOriginal>\n" +
            "\t<NetBiosName>PREC5510</NetBiosName>\n" +
            "\t<IsRunQSO>0</IsRunQSO>\n" +
            "\t<StationName>PREC5510</StationName>\n" +
            "\t<ID>81a90b3550c54b1d8dc62080229737cb</ID>\n" +
            "\t<IsClaimedQso>1</IsClaimedQso>\n" +
            "\t<oldtimestamp>2025-01-10 18:39:51</oldtimestamp>\n" +
            "\t<oldcall>A1B</oldcall>\n" +
            "\t<SentExchange />\n" +
            "</contactreplace>";

    private static final String DEL_CONTACT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<contactdelete>\n" +
            "\t<app>N1MM</app>\n" +
            "\t<timestamp>2025-01-10 18:39:51</timestamp>\n" +
            "\t<call>A1C</call>\n" +
            "\t<contestnr>0</contestnr>\n" +
            "\t<StationName>PREC5510</StationName>\n" +
            "\t<ID>81a90b3550c54b1d8dc62080229737cb</ID>\n" +
            "</contactdelete>";
}