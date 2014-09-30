import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class SharedMethod {
	// get current IP address
	public String getCurrentIP(String name) throws UnknownHostException{
		String addr;
		addr = InetAddress.getLocalHost().getHostAddress();
		System.out.println(name+" server IP: "+ addr);
		return addr; 
	}
	
	// read line from well-known file
	public NS_MapValue getFromWKFile(String fileName) {
		NS_MapValue temp = new NS_MapValue();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			InetAddress NSIP =  InetAddress.getByName(line.split("\\|")[1]);
			int NSPort = Integer.parseInt(line.split("\\|")[2]);
			temp.setIp(NSIP);
			temp.setPort(NSPort);
			System.out.println("Name server IP: "+NSIP+", port: "+NSPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
}
