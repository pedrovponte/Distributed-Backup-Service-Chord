import java.math.BigInteger;
import java.net.InetSocketAddress;

public class SuccFoundThread implements Runnable {
    Message message;

    public SuccFoundThread(Message message) {
        this.message = message;
    }

	@Override
	public void run() {
		//System.out.println("INSIDE SuccFoundThread");
        // Version SUCCFOUND + nodeId + address + port
        String[] header = this.message.getHeader();
        
        System.out.println("RECEIVED: " + header[0] + " " + header[1] + " " + header[2] + " " + header[3] + " " + header[4]);

        BigInteger nodeId = new BigInteger(header[2]);
        String address = header[3];
        int port = Integer.parseInt(header[4]);

        NodeInfo nodeInfo = new NodeInfo(nodeId, new InetSocketAddress(address, port));
        Peer.getChordNode().setSuccessor(nodeInfo);
        Peer.getChordNode().setFingerTablePosition(0, nodeInfo);
        Peer.getChordNode().decrementJoinCountDownLatch();
	}
}