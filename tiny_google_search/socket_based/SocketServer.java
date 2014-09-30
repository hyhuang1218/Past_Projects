import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/*
 * @author: Song Jin
 * @date: 11/25/13
 * tiny-Google server
 * 1: server to listening requests from client: ProcessRequest class
 * 2: client to send requests to helper: Worker class
 */
public class SocketServer {
	private static final String WK_FILE = "wkFile.txt";
	private static final String NAME = "tiny-Google";
	private static final int HELPER_AMOUNT = 2;
	private static final int WORKER_AMOUNT = 2;
	private static final int Q_SIZE = 5;
	
	private ServerSocket serverSocket;
	private InetAddress IP;
	private int port;
	private NS_MapValue NSresult;
	private static InetAddress NSIP;
	private static int NSPort;
	private SharedMethod func;
	
	private InetAddress clientIP;
	private int clientPort;
	
	private int merge_count;
	private ArrayList<String> tempList;
	private OperateFile op;
	private SearchDoc sdDoc;
	
	// constructor for initializing variables
	public SocketServer() throws IOException {
		serverSocket = new ServerSocket(0);
		port = serverSocket.getLocalPort();
		func = new SharedMethod();
		merge_count = 0;
		tempList = new ArrayList<String>();
		op = new OperateFile();
		sdDoc = new SearchDoc();
		
		// get current IP
		System.out.println("\n****** Server info ******");
		IP = InetAddress.getByName(func.getCurrentIP("Tiny-Google"));
		System.out.println("Tiny-Google server port: "+port);
		
		// get name server IP and port
		System.out.println("\n****** Get name server address ******");
		NSresult = func.getFromWKFile(WK_FILE);
		NSIP = NSresult.getIp();
		NSPort = NSresult.getPort();
		// register tiny-Google server to the name server
		regToNS();
	}
	
	private void regToNS() {
		System.out.println("Connecting to name server on IP "+NSIP+", port "+NSPort);
		try {
			Socket client = new Socket(NSIP, NSPort);
			System.out.println("Connected successfully to "+client.getRemoteSocketAddress());
			
			// create connection stream 
			OutputStream sendToNS = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(sendToNS);
			InputStream fromNS = client.getInputStream();
			DataInputStream in = new DataInputStream(fromNS);
			
			// send register request
			out.writeUTF("1"+"|"+NAME+"|"+IP.toString().substring(1)+"|"+port);
			System.out.println("\n****** Register phase ******");
			System.out.println("Sent: "+"1"+"|"+NAME+"|"+IP.toString().substring(1)+"|"+port);
			System.out.println(in.readUTF());
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getHelper(int num) {
		String response = "";
		String name = "HELPER_"+ num;
		System.out.println("Connecting to name server on IP "+NSIP+", port "+NSPort);
		try {
			Socket client = new Socket(NSIP, NSPort);
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
			client.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	// assign correct number of files to each helper
	// separator: "|"
	private ArrayList<String> computeAmountToHelper(ArrayList<File> input) {
		ArrayList<String> transToHelperArrayList = new ArrayList<String>();
		int n = input.size();
		int amount = n / HELPER_AMOUNT;
		int residue = n % HELPER_AMOUNT;
		for (int i = 0; i < HELPER_AMOUNT; i++) {
			StringBuilder sb = new StringBuilder();
			if (i==0) {
				for (int j = 0; j < amount+residue; j++) {
					sb.append(input.remove(0)+"|");
				}
			} else {
				for (int j = 0; j < amount; j++) {
					sb.append(input.remove(0)+"|");
				}
			}
			transToHelperArrayList.add(sb.toString());
		}
		return transToHelperArrayList;
	}
	
	// assign corrent number of keyword to each helper
	// separator: "^"
	private ArrayList<String> queryAmountToHelper(ArrayList<String> input) {
		ArrayList<String> transToHelperArrayList = new ArrayList<String>();
		// for calculate weight
		int weight = 1;
		int n = input.size();
		int amount = n / HELPER_AMOUNT;
		int residue = n % HELPER_AMOUNT;
		for (int i = 0; i < HELPER_AMOUNT; i++) {
			StringBuilder sb = new StringBuilder();
			if (i==0) {
				for (int j = 0; j < amount+residue; j++) {
					sb.append(input.remove(0)+"@"+weight+"^");
					weight++;
				}
			} else {
				for (int j = 0; j < amount; j++) {
					sb.append(input.remove(0)+"@"+weight+"^");
					weight++;
				}
			}
			transToHelperArrayList.add(sb.toString());
		}
		return transToHelperArrayList;
	}
	
	// input : directory output: array list of files
	private ArrayList<File> parseDir(String dirPath) {
		ArrayList<File> resultList = new ArrayList<File>();
		ArrayList<File> dirList = new ArrayList<File>();
		File directory = new File(dirPath + File.separator);
		try {
			System.out.println("*** File list under "+dirPath);
			for (File f : directory.listFiles()) {
				if (f.isDirectory()) {
					dirList.add(f);
				} else {  
					System.out.println("> "+f.toString());
					resultList.add(f);
				}
			}
			// directory list, only for Shakespeare folder
			if (dirList.size()>0) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
		return resultList;
	}
	
	private ArrayList<String> parseWord(String input) {
		ArrayList<String> kwList = new ArrayList<String>();
		String[] keywords = input.split("\\s");
		for (int i = 0; i < keywords.length; i++) {
			String s = keywords[i];
			kwList.add(s);
		}
		return kwList;
	}
	
	// shared work queue in Tiny-Google server
	private class WorkQueue {
		private int capacity;
		private int size;
		private Element[] workQueue;
		private int head;
		private int tail;
		
		public WorkQueue(int sz) {
			capacity = sz;
			workQueue = new Element[capacity];
			size = head = 0;
			tail = -1;
		}
		
		public synchronized void putItem(String req, char type) {
			System.out.println("*** Put into the queue ***");
			// wait if full
			while (size == capacity) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			tail = (tail+1) % capacity;
			size++;
			Element e = new Element();
			e.setReqContent(req);
			e.setType(type);
			workQueue[tail] = e;
			System.out.println("Request processed: put..<"+e.getReqContent()+","+e.getType()+">");
			notify();
		}
		
		public synchronized Element getItem() {
			System.out.println("*** Get from the queue ***");
			// wait if empty
			while (size == 0) {
				try {
					System.out.println("I'm waiting..");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			notify();
			Element e = workQueue[head];
			head = (head+1) % capacity;
			size--;
			System.out.println("Worker processed: get..<"+e.getReqContent()+","+e.getType()+">");
			return e;
		}
		
	}
	
	// this thread processes request from client
	// put the request result into the workQueue
	private class ProcessRequest extends Thread {
		private WorkQueue queue;
		private char reqType;
		private ArrayList<File> listForHelper;
		private String helperOutput;
		
		public ProcessRequest(WorkQueue buf) {
			queue = buf;
		}
		
		public void run() {
			while (true) {
				System.out.println("\n****** Listening phase ******");
				System.out.println("Waiting for client on port "+
						serverSocket.getLocalPort() + "..");
				try {
					StringBuilder sb = new StringBuilder();
					// create server socket
					Socket server = serverSocket.accept();
					System.out.println("Connected to "+server.getRemoteSocketAddress());
					DataInputStream in = new DataInputStream(server.getInputStream());
					DataOutputStream out = new DataOutputStream(server.getOutputStream());
					
					// get directory path name request
					String readDir = in.readUTF();
					reqType = readDir.charAt(0);
					readDir = readDir.substring(1);
					
					// check if the request is valid
					if (reqType=='1') {
						// indexing request
						// Pattern: Shakespeare/comedies
						listForHelper = parseDir(readDir);
						// Shakespeare contains sub-directories
						if (listForHelper==null) {
							sb.append("No such directory or it contains sub-dir. Please input again.\n");
							sb.append("Usage for Shakespeare directory: Shakespeare/<sub-dir>");
						// return array list containing files in a directory 
						} else {
							sb.append("The following docs will be processed: "+listForHelper.toString()); // need modification if there's merge result
							// put the request in the work queue
							queue.putItem(readDir, reqType);
							// clear request
							listForHelper.clear();
							System.out.println("Put <"+readDir+","+reqType+"> into work queue.");
						}
					} else if (reqType=='2') {
						// search query
						// Pattern: love war peace
						sb.append("Query received: "+readDir);
						queue.putItem(readDir, reqType);
						System.out.println("Put <"+readDir+","+reqType+"> into work queue.");
					} else if (reqType=='3') {
						// get client IP and port
						readDir = readDir.substring(1);
						System.out.println("Client IP/port received: "+readDir);
						clientIP = InetAddress.getByName(readDir.split("\\|")[0]);
						clientPort = Integer.parseInt(readDir.split("\\|")[1]);
						sb.append("Success");
					} else if (reqType=='I') {
						// response from helper
						// Pattern: I_W0HELPER_0Result.txt
						helperOutput = "I"+readDir;
						System.out.println("Got filenames which need to be merged from helper: "+helperOutput);
						System.out.println();
						merge_count++;
						tempList.add(helperOutput);
						if (merge_count == HELPER_AMOUNT) {
							op.mergeIndex(ServerInfo.MIIPath, tempList);
							sb.append(ServerInfo.MIIPath);
							tempList.clear();
							merge_count = 0;
						}
					} else if (reqType=='S') {
						// response from helper
						// Pattern: S_W0HELPER_0Result.txt
						helperOutput = "S"+readDir;
						System.out.println("Got filenames which need to be merged from helper: "+helperOutput);
						System.out.println();
						merge_count++;
						tempList.add(helperOutput);
						if (merge_count == HELPER_AMOUNT) {
							System.out.println("temp list: "+tempList);
							String result = op.mergeRar("SearchResult", tempList);
							sb.append(ServerInfo.MIIPath);
							String[] rs = sdDoc.getDocNameByRaRFile(result);
							for(int i=0;i<rs.length;i++){
								System.out.println((i+1)+"  "+rs[i]);
							}
							tempList.clear();
							merge_count = 0;
							
							/*** connect to Client ***/
							System.out.println("\nConnecting to client with IP "+clientIP+" on port "+clientPort);
							Socket client = new Socket(clientIP, clientPort);
							System.out.println("Connected successfully to "+client.getRemoteSocketAddress());
							OutputStream sendToServer = client.getOutputStream();
							DataOutputStream out1 = new DataOutputStream(sendToServer);
							out1.writeUTF(Arrays.toString(rs));
							System.out.println("Search result sent: "+Arrays.toString(rs));
						}
					} else if (reqType=='N') {
						helperOutput = "N"+readDir;
						System.out.println(helperOutput);
						
						/*** connect to Client ***/
						System.out.println("\nConnecting to client with IP "+clientIP+" on port "+clientPort);
						Socket client = new Socket(clientIP, clientPort);
						System.out.println("Connected successfully to "+client.getRemoteSocketAddress());
						OutputStream sendToServer = client.getOutputStream();
						DataOutputStream out1 = new DataOutputStream(sendToServer);
						out1.writeUTF(helperOutput);
						System.out.println("Search result sent: "+helperOutput);
					}
					// write back to client
					out.writeUTF(sb.toString());
					System.out.println("<"+sb.toString()+">");
					System.out.println("--> Successfully sent back");
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// this thread removes item from work queue
	private class Worker extends Thread{
		private int n;
		private WorkQueue queue;
		private ArrayList<File> listForHelper;
		private ArrayList<String> sForHelper;
		private char reqType;
		private String request;
		private InetAddress HPIP;
		private int HPPort;
		
		public Worker(int amount, WorkQueue buf) {
			n = amount;
			queue = buf;
		}
		
		public void run() {
			while (true) {
				for (int i = 0; i < n; i++) {
					// get request from work queue
					System.out.println("\n*** Worker: "+i);
					Element element = queue.getItem();
					// the first character represents the request type
					reqType = element.getType();
					request = element.getReqContent();
					
					if (reqType=='1') {
						// indexing request
						listForHelper = parseDir(request);
						sForHelper = computeAmountToHelper(listForHelper);
						System.out.println("*** File ready: "+sForHelper.toString());
					} else if (reqType=='2') {
						// search query
						sForHelper = queryAmountToHelper(parseWord(request));
						System.out.println("*** Query ready: "+sForHelper.toString());
					}
					
					for (int j = 0; j < HELPER_AMOUNT; j++) {
						/*** send items in array list to each helpers ***/
						/*** retrieve helper info from name server ***/
						String response = getHelper(j);
						try {
							HPIP = InetAddress.getByName(response.split("\\|")[0].substring(1));
							HPPort = Integer.parseInt(response.split("\\|")[1]);
							
							/*** connect to Helper ***/
							System.out.println("\nConnecting to helper "+j+" with IP "+HPIP+" on port "+HPPort);
							Socket client1 = new Socket(HPIP, HPPort);
							System.out.println("Connected successfully to "+client1.getRemoteSocketAddress());
							OutputStream sendToServer = client1.getOutputStream();
							DataOutputStream out1 = new DataOutputStream(sendToServer);
							// add worker info to items in the list
							String temp = i+sForHelper.remove(0);
							out1.writeUTF(temp);
							System.out.println("Worker sent: "+temp);
							
							/*** response from helper ***/
							InputStream fromServer = client1.getInputStream();
							DataInputStream in1 = new DataInputStream(fromServer);
							
							System.out.println("\n****** From helper "+j+" ******:\n"+in1.readUTF());
							client1.close();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	// main function for running producer and consumer
	public static void main(String args[]) {
		try {
			SocketServer server = new SocketServer();
			WorkQueue buf = server.new WorkQueue(Q_SIZE);
			
			// create new threads
			Thread processReq = server.new ProcessRequest(buf);
			Thread worker = server.new Worker(WORKER_AMOUNT, buf);
			
			// start threads
			processReq.start();
			worker.start();
			
			// wait for the threads to finish
			processReq.join();
			worker.join();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
