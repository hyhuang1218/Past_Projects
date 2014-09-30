import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/*
 * @author: Song Jin
 * @date: 11/25/13
 * name server implementation
 */
public class NameServer extends Thread {
	private static HashMap<String, NS_MapValue> nameMap = new HashMap<String, NS_MapValue>();
	private InetAddress IP;
	private ServerSocket serverSocket;
	private int port;
	private PrintWriter writer;
	private SharedMethod func;
	
	public NameServer() throws IOException {
		serverSocket = new ServerSocket(0);
		port = serverSocket.getLocalPort();
		func = new SharedMethod();
		
		// get current IP
		IP = InetAddress.getByName(func.getCurrentIP("Name"));
		String transIP = IP.toString().substring(1);
		
		// write name server IP and port to the well-known file
		writeWKFile("NS"+"|"+transIP+"|"+port);
		System.out.println("\n****** Register itself to well-known file ******");
		System.out.println("Writing to Well Known file: "+"NS"+"|"+transIP+"|"+port);
	}
	
	private void writeWKFile(String input) throws IOException {
		// create and append each input into a new line
		writer = new PrintWriter(new BufferedWriter(new FileWriter("wkFile.txt")));
		writer.println(input);
		writer.close();
	}
	
	public void run() {
		while (true) {
			System.out.println("\n****** Daemon waiting for request ******");
			System.out.println("Waiting for client on port "+
					port + "..");
			try {
				// create socket in server side
				Socket server = serverSocket.accept();
				System.out.println("Connected to "+server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				
				// get Socket Address of the requested server
				String readFrom = in.readUTF();
				
				// 0 - request for addr, 1 - register
				if (readFrom.charAt(0)=='0') {
					// get ip/port pair from map based on user input
					String[] inputString = readFrom.split("\\|");
					String name = inputString[1];
					InetAddress ipAddress = nameMap.get(name).getIp();
					int portNum = nameMap.get(name).getPort();
					System.out.println("\n****** Resolve Request: get from map ******");
					System.out.println("Read from client: [resolve request] <name: "+name+">");
					
					// write ip/port pair back to client
					out.writeUTF(ipAddress+"|"+portNum);
					System.out.println("Response: "+ipAddress+"|"+portNum);
					server.close();
					
				} else if (readFrom.charAt(0)=='1') {
					// set name/ip/port pair based on user input
					NS_MapValue value = new NS_MapValue();
					String[] inputString = readFrom.split("\\|"); 
					String name = inputString[1];
					String ipString = inputString[2];
					String portString = inputString[3];
					value.setIp(InetAddress.getByName(ipString));
					value.setPort(Integer.parseInt(portString));
					nameMap.put(name, value);
					System.out.println("\n****** Register Request: store name/ip/port into a map ******");
					System.out.println("Read from client: [register request] <name: "+name+">, <ip: "+ipString+
										">, <port: "+portString+">");
					
					// write ip/port pair back to client
				
					out.writeUTF("From name server: Success");
					System.out.println("Map stored: "+nameMap);
					server.close();
				}

				System.out.println("--> Successfully sent back");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// create name server thread
		try {
			Thread t= new NameServer();
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
