import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*
 * @author: Song Jin
 * @date: 11/25/13
 * client for connecting to name server and tiny-Google server
 * arg[0]: port number of name server
 */

public class SocketClient {
	private static final String WK_FILE = "wkFile.txt";
	private static final String NAME = "tiny-Google";
	
	private static SharedMethod func;
	private static NS_MapValue NSresult;
	private static InetAddress NSIP;
	private static int NSPort;
	private static InetAddress TGIP;
	private static int TGPort;
	
	private static ServerSocket serverSocket;
	private static int port;
	private static InetAddress IP;
	
	public String getName(String name, InetAddress nsip, int nsport) {
		String response = "";
		System.out.println("Connecting to name server on IP "+nsip+", port "+nsport);
		try {
			Socket client = new Socket(nsip, nsport);
			System.out.println("Connected successfully to "+client.getRemoteSocketAddress());
			
			// create connection stream 
			OutputStream sendToNS = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(sendToNS);
			InputStream fromNS = client.getInputStream();
			DataInputStream in = new DataInputStream(fromNS);
			
			// send resolve request
			out.writeUTF("0"+"|"+name);
			System.out.println("\n****** Resolve phase ******");
			System.out.println("Sent: "+"0"+"|"+name);
			response = in.readUTF();
			System.out.println("Response from name server: "+response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	// get client IP and port
	private void getIPPort() throws IOException {
		serverSocket = new ServerSocket(0);
		port = serverSocket.getLocalPort();
		func = new SharedMethod();
		IP = InetAddress.getByName(func.getCurrentIP("Client"));
	}
	
	private class IssueRequest extends Thread {
		public IssueRequest() {
			try {
				getIPPort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			String result;

			try {
				func = new SharedMethod();
				getIPPort();
				
				// get name server IP and port
				System.out.println("\n****** Get name server address ******");
				NSresult = func.getFromWKFile(WK_FILE);
				NSIP = NSresult.getIp();
				NSPort = NSresult.getPort();
				
				/*** retrieve tiny-Google server info from name server ***/
				String response = SocketClient.this.getName(NAME, NSIP, NSPort);
				TGIP = InetAddress.getByName(response.split("\\|")[0].substring(1));
				TGPort = Integer.parseInt(response.split("\\|")[1]);
				
				System.out.println("Client IP: "+IP+", port: "+port);
				String input2 = IP+"|"+port;
				
				// create new socket link
				System.out.println("\nConnecting to tiny-Google server with IP "+TGIP+" on port "+TGPort);
				Socket client2 = new Socket(TGIP, TGPort);
				System.out.println("Connected successfully to "+client2.getRemoteSocketAddress());
				OutputStream sendToServer2 = client2.getOutputStream();
				DataOutputStream out2 = new DataOutputStream(sendToServer2);
				
				// sent result
				System.out.println("Sent: "+"3"+input2);
				out2.writeUTF("3"+input2);
				client2.close();
				
				/*** connect to tiny-Google server ***/
				Scanner s = new Scanner(System.in);
				while (true) {
					System.out.println("\n****** User-input Phase ******");
					System.out.println("Input '1' for indexing requests, 2 for search queries");
					String readFromKB = s.next();
					String input = "";
					
					/*** indexing request ***/
					if (readFromKB.equals("1")) {
						System.out.println("Please input a directory path name:");
						Scanner readPath = new Scanner(System.in);
						input = readPath.next();
					/*** search query ***/
					} else if (readFromKB.equals("2")) {
						System.out.println("Please input a search query:");
						Scanner readKW = new Scanner(System.in);
						input = readKW.nextLine();
					} else {
						System.out.println("Wrong input");
					}

					// create new socket link
					System.out.println("\nConnecting to tiny-Google server with IP "+TGIP+" on port "+TGPort);
					Socket client1 = new Socket(TGIP, TGPort);
					System.out.println("Connected successfully to "+client1.getRemoteSocketAddress());
					OutputStream sendToServer = client1.getOutputStream();
					DataOutputStream out1 = new DataOutputStream(sendToServer);
					
					InputStream fromServer = client1.getInputStream();
					DataInputStream in1 = new DataInputStream(fromServer);
					
					if (readFromKB.equals("1")) {
						// send request (path) to tiny-Google server
						// use '1' as the first character to indicate an indexing request
						out1.writeUTF("1"+input);
					} else if (readFromKB.equals("2")) {
						// send request (key word list) to tiny-Google server
						// use '2' as the first character to indicate an search query
						out1.writeUTF("2"+input);
						// wait until server response
						Socket server = serverSocket.accept();
						DataInputStream in = new DataInputStream(server.getInputStream());
						String finalResult = in.readUTF();
						System.out.println("Result: "+finalResult);
						server.close();
					} else {
						System.out.println("Wrong input");
					}
					result = in1.readUTF();
					client1.close();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		SocketClient client = new SocketClient();
		// create new threads
		Thread request = client.new IssueRequest();
		request.start();
	}
}
