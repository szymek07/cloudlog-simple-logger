package pl.sp6pat.ham.cloudlogsimplelogger.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpListener {
    public static void main(String[] args) {
        int port = 9876; // Port, na którym nasłuchujemy
        byte[] buffer = new byte[10240]; // Bufor na odebrane dane

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Nasłuchiwanie pakietów UDP na porcie " + port);

            while (true) {
                // Przygotowanie pakietu na odebrane dane
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Odbierz pakiet
                socket.receive(packet);

                // Wyświetl informacje o pakiecie
                String receivedData = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                System.out.println("Odebrano pakiet od " + senderAddress + ":" + senderPort);
                System.out.println("Dane: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
<?xml version="1.0" encoding="utf-8"?>
<contactinfo>
	<app>N1MM</app>
	<contestname>DX</contestname>
	<dbname>N1MM DXLog.s3db</dbname>
	<contestnr>0</contestnr>
	<timestamp>2025-01-06 00:33:08</timestamp>
	<mycall>SP6PAT</mycall>
	<band>10</band>
	<rxfreq>1015000</rxfreq>
	<txfreq>1015000</txfreq>
	<operator>SP6PAT</operator>
	<mode>USB</mode>
	<call>SP6PAT</call>
	<countryprefix>SP</countryprefix>
	<wpxprefix>SP6</wpxprefix>
	<stationprefix>SP6PAT</stationprefix>
	<continent>EU</continent>
	<snt>59</snt>
	<sntnr>10</sntnr>
	<rcv>59</rcv>
	<rcvnr>0</rcvnr>
	<gridsquare></gridsquare>
	<exchange1></exchange1>
	<section></section>
	<comment></comment>
	<qth></qth>
	<name></name>
	<power></power>
	<misctext></misctext>
	<zone>15</zone>
	<prec></prec>
	<ck>0</ck>
	<ismultiplier1>0</ismultiplier1>
	<ismultiplier2>0</ismultiplier2>
	<ismultiplier3>0</ismultiplier3>
	<points>0</points>
	<radionr>1</radionr>
	<run1run2>1</run1run2>
	<RoverLocation></RoverLocation>
	<RadioInterfaced>0</RadioInterfaced>
	<NetworkedCompNr>0</NetworkedCompNr>
	<IsOriginal>True</IsOriginal>
	<NetBiosName>PREC5510</NetBiosName>
	<IsRunQSO>0</IsRunQSO>
	<StationName>PREC5510</StationName>
	<ID>ac37862dbc3843a3a34472f7c5bde392</ID>
	<IsClaimedQso>1</IsClaimedQso>
	<oldtimestamp>2025-01-06 00:33:08</oldtimestamp>
	<oldcall>SP6PAT</oldcall>
	<SentExchange />
</contactinfo>
 */