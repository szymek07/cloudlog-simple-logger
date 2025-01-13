package pl.sp6pat.ham.cloudlogsimplelogger.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class JTDXUdpSender {
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

    private static final String ADD_CONTACT = "<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:4>8E3R <FREQ:9>14.075896 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>162345 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>162459 <RST_SENT:3>-11 <RST_RCVD:3>-20 <GRIDSQUARE:4>OI33 <EOR>\n";
    //<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:6>IS0YHV <FREQ:9>14.075896 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>162830 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>163114 <RST_SENT:3>-02 <RST_RCVD:3>+02 <GRIDSQUARE:4>JM49 <EOR>
    //<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:8>DL75DARC <FREQ:9>14.075896 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>163345 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>163559 <RST_SENT:3>-16 <RST_RCVD:3>-22 <EOR>
    //<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:6>SY9EFH <FREQ:9>14.075907 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>163819 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>163929 <RST_SENT:3>-04 <RST_RCVD:3>-03 <GRIDSQUARE:4>KM25 <EOR>
    //<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:6>VE1JBC <FREQ:9>14.075907 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>164245 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>164359 <RST_SENT:3>-11 <RST_RCVD:3>-13 <GRIDSQUARE:4>FN73 <EOR>
    //<BAND:3>20m <STATION_CALLSIGN:6>3Z6AEF <MY_GRIDSQUARE:6>JO81lc <CALL:6>SQ1PQQ <FREQ:9>14.075907 <MODE:3>FT8 <QSO_DATE:8>20250113 <TIME_ON:6>164430 <QSO_DATE_OFF:8>20250113 <TIME_OFF:6>164529 <RST_SENT:3>-11 <RST_RCVD:3>-19 <GRIDSQUARE:4>JO73 <EOR>


    private static final String EDIT_CONTACT = "";

    private static final String DEL_CONTACT = "";
}