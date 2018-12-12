//-----------------------------------------------------------------
//
//	Description:Program that sends both a UDP packet and TCP packet to a server.
//
//	Joshua Hofmann
//	ECE 570
//	11/28/2018
//
//	Built with Eclipse java Version: 2018-09 (4.9.0)
//	Java 1.8.0_111
//
//-----------------------------------------------------------------

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class UDPTCPClient {
	final static String IPADDRESS = "152.46.19.52";
	final static int PORT	 	  = 8222;
	public static void main(String[] args) {
		String preamble = 	"NCSU" + "200091906";
		Integer studentID = 200091906;
		Integer.reverseBytes(studentID);
		
		String datatoSend = 	"Joshua Hofmann\n" +
					"200091906\n" ;
		List<Byte> byteList = new ArrayList<Byte>();	
		for (Byte byte1 : preamble.getBytes()) {
			byteList.add(byte1);
		}
		for (Byte byte1 :  ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(studentID).array()) {
			byteList.add(byte1);
		}
		for (Byte byte1 : datatoSend.getBytes()) {
			byteList.add(byte1);
		}
		
		
		try {
			SendDatatoUDPServer(byteList);
			SendDatatoTCPServer(byteList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void SendDatatoTCPServer(List<Byte> byteList) throws IOException {
		Socket clientSocket = new Socket(IPADDRESS, PORT);
		String ippaddr = clientSocket.getLocalAddress().getHostAddress() +"\n" + clientSocket.getLocalPort() + "\n" +"\0";
		for (Byte byte1 : ippaddr.getBytes()) {
			byteList.add(byte1);
		}
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outToServer.write(getByteArry(byteList));
		String response = fromServer.readLine();
		System.out.println(response);
		clientSocket.close();
	}

	private static void SendDatatoUDPServer(List<Byte> byteList) throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(IPADDRESS);

		byte[] receiveData = new byte[1024];
		String ipaddr = clientSocket.getLocalAddress().getHostAddress() +"\n" + clientSocket.getLocalPort() + "\n" + "\0";
		for (Byte byte1 : ipaddr.getBytes()) {
			byteList.add(byte1);
		}
		byte[] data =getByteArry(byteList);
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, PORT);
		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println(modifiedSentence);
		clientSocket.close();
		
	}
	
	private static byte[] getByteArry(List<Byte> byteList) {
		byte[] data = new byte[byteList.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = byteList.get(i);
			
		}
		return data;
	}

}
