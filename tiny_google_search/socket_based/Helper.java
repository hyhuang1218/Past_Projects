import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/*
 * @author: Song Jin
 * @date: 12/04/13
 * helper thread in single machine
 */
public class Helper extends Thread {
	private static final String WK_FILE = "wkFile.txt";
	private static final int NUM_THREAD = 3;
	private static final int Q_SIZE = 50;
	private static final String NAME = "tiny-Google";
	
	private ServerSocket serverSocket;
	private InetAddress IP;
	private int port;
	private String name;
	private static SharedMethod func = new SharedMethod();
	private static NS_MapValue NSresult;
	private static InetAddress NSIP;
	private static int NSPort;
	private OperateFile op;
	private ArrayList<String> tempList;
	private ArrayList<String> tempSearchList;
	private int docsCount;
	private int wordsCount;
	private char worker_id;
	private boolean nullFlag;
	
	public Helper(int num) throws IOException {
		nullFlag = false;
		name = "HELPER_"+ num;
		docsCount = wordsCount = -1;
		serverSocket = new ServerSocket(0);
		port = serverSocket.getLocalPort();
		tempList = new ArrayList<String>();
		tempSearchList = new ArrayList<String>();
		op = new OperateFile();
		func = new SharedMethod();
		worker_id = ' ';
		
		// get current IP
		IP = InetAddress.getByName(func.getCurrentIP("Helper"));
		System.out.println("Helper server port: "+port);
		
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
			out.writeUTF("1"+"|"+name+"|"+IP.toString().substring(1)+"|"+port);
			System.out.println("\n****** Register phase ******");
			System.out.println("Sent: "+"1"+"|"+name+"|"+IP.toString().substring(1)+"|"+port);
			System.out.println(in.readUTF());
			client.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// compare this number with actual amount of files
	private int countFileAmount(String input) {
		int index = 0;
		int count = 0;
		while (index < input.length()) {
			if (input.charAt(index)=='|') {
				count++;
			}
			index++;
		}
		return count;
	}
	
	private int countWordAmount(String input) {
		int index = 0;
		int count = 0;
		while (index < input.length()) {
			if (input.charAt(index)=='^') {
				count++;
			}
			index++;
		}
		return count;
	}
	
	// helper queue containing list of documents or keywords
	private class HelpQueue {
		private int capacity;
		private int size;
		private Element[] helpQueue;
		private int head;
		private int tail;
		
		public HelpQueue(int sz) {
			capacity = sz;
			helpQueue = new Element[capacity];
			size = head = 0;
			tail = -1;
		}
		
		public synchronized void putItem(String req, char type) {
			System.out.println("*** Put into the queue ***");
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
			helpQueue[tail] = e;
			System.out.println("Master thread allocation finished: put..<"+e.getReqContent()+","+e.getType()+">");
			notify();
		}
		
		public synchronized Element getItem() {
			System.out.println("*** Get from the queue ***");
			while (size == 0) {
				try {
					System.out.println("I'm waiting..");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			notify();
			Element e = helpQueue[head];
			head = (head+1) % capacity;
			size--;
			System.out.println("Slave thread processed: get..<"+e.getReqContent()+","+e.getType()+">");
			return e;
		}
	}
	
	// this master thread processes request from worker
	// put individual element (1.txt or abc) into helper queue 
	private class ProcessWorkerReq extends Thread {
		private HelpQueue queue;
		
		public ProcessWorkerReq(HelpQueue buf) {
			queue = buf;
		}
		
		public void run() {
			while (true) {
				System.out.println("\n****** Listening phase ******");
				System.out.println("Waiting for client on port "+
						serverSocket.getLocalPort() + "..");
				try {
					// create server socket
					Socket server = serverSocket.accept();
					System.out.println("Connected to "+server.getRemoteSocketAddress());
					DataInputStream in = new DataInputStream(server.getInputStream());
					DataOutputStream out = new DataOutputStream(server.getOutputStream());
					
					// get directory path name request
					String readReq = in.readUTF();
					worker_id = readReq.charAt(0);
					System.out.println("Worker id: "+worker_id);
					readReq = readReq.substring(1);
					System.out.println(name+": Received from "+readReq);
					
					/*** put request partition result into the queue ***/
					if (readReq.contains("|")) {
						// get docs count
						docsCount = countFileAmount(readReq);
						String[] indexingReq = readReq.split("\\|");
						for (String s : indexingReq) {
							queue.putItem(s, '1');
						}
					} else if (readReq.contains("^")) {
						// get words count
						wordsCount = countWordAmount(readReq);
						String[] indexingReq = readReq.split("\\^");
						for (String s : indexingReq) {
							queue.putItem(s, '2');
						}
					}

					// write back to client
					out.writeUTF("OK"); // need modification
					System.out.println("--> Successfully sent back");
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// this thread is assigned objectives to perform real function
	private class HelperThread extends Thread {
		private int n;
		private HelpQueue queue;
		private char reqType;
		private String request;
		private InetAddress TGIP;
		private int TGPort;
		private SocketClient client;
		
		public HelperThread(int amount, HelpQueue buf) {
			n = amount;
			queue = buf;
			client = new SocketClient();
		}
		
		public void run() {
			while (true) {
				String tempFile = "";
				for (int i = 0; i < n; i++) {
					nullFlag = false;
					// get element from helper queue
					System.out.println("\n*** Helper thread: "+i);
					Element element = queue.getItem();
					// 1 for indexing request, 2 for document name/keyword
					reqType = element.getType();
					request = element.getReqContent();
					
					if (reqType=='1') {
						System.out.println("Got indexing document request: "+request);
						
						/*** use request handle API ***/
						tempFile = QueryProcess.startIndex(request, "");
						System.out.println("Return inverted index file name: "+tempFile);
						if(tempFile!=null){
							tempList.add(tempFile);
						}
						System.out.println("&&&"+tempList+"&&&");
					} else if (reqType=='2') {
						tempFile = QueryProcess.startSearch(request, "");
						System.out.println("Return search result temporary file: "+tempFile);
						if(tempFile!=null){
							tempSearchList.add(tempFile);
						} else {
							nullFlag = true;
						}
						System.out.println("&&&"+tempSearchList+"&&&");
					}
					
					// merge if list size is the same with the document amount/words amount
					String indexOutput = "I_W"+worker_id+name+"Result.txt";
					String searchOutput = "S_W"+worker_id+name+"Result.txt";
					System.out.println("!!! "+"tempList.size() "+tempList.size()+" docsCount "+docsCount+
							"tempSearchList.size() "+tempSearchList.size()+" wordsCount "+wordsCount+" !!!");

					if (!nullFlag) {
						if ((tempList.size() == docsCount)){
							if (op.mergeIndex(indexOutput, tempList) == null) {
								nullFlag = true;
							}
						} else if ((tempSearchList.size() == wordsCount)) {
							if (op.mergeRar(searchOutput, tempSearchList) == null) {
								nullFlag = true;
							}
						}	
					}
					
					// create new socket link
					try {
						/*** create a new socket connection to tiny-Google server ***/
						/*** send back output file name ***/
						System.out.println("\n*** Send generated file name back to tiny-Google server ***");
						/*** retrieve name server info from well-known file ***/
						String tgAddr = client.getName(NAME, NSIP, NSPort);
						TGIP = InetAddress.getByName(tgAddr.split("\\|")[0].substring(1));
						TGPort = Integer.parseInt(tgAddr.split("\\|")[1]);
						System.out.println("\nConnecting to tiny-Google server with IP "+TGIP+" on port "+TGPort);
						Socket helperClient = new Socket(TGIP, TGPort);
						System.out.println("Connected successfully to "+helperClient.getRemoteSocketAddress());
						OutputStream sendToServer = helperClient.getOutputStream();
						DataOutputStream out1 = new DataOutputStream(sendToServer);
						
						if ((tempList.size() == docsCount)){
							out1.writeUTF(indexOutput);
							helperClient.close();
							tempList.clear();
						} else if ((tempSearchList.size() == wordsCount)) {
							out1.writeUTF(searchOutput);
							helperClient.close();
							tempSearchList.clear();
						} else if (nullFlag) {
							out1.writeUTF("No documents for the requested keywords");
							helperClient.close();
							tempList.clear();
							tempSearchList.clear();
						}
						helperClient.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			Helper helper = new Helper(Integer.parseInt(args[0]));
			HelpQueue buf = helper.new HelpQueue(Q_SIZE);
			
			Thread masterThread = helper.new ProcessWorkerReq(buf);
			Thread helperThread = helper.new HelperThread(NUM_THREAD, buf);

			masterThread.start();
			helperThread.start();
			
			try {
				masterThread.join();
				helperThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
