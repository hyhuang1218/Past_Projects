import java.net.InetAddress;


public class NS_MapValue {
	private InetAddress ip;
	private int port;
	private String attribute;

	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	@Override
	public String toString() {
		return "NS_MapValue [ip=" + ip + ", port=" + port + "]";
	}
	
}
