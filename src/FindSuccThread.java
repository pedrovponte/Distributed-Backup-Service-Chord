import java.math.BigInteger;

public class FindSuccThread implements Runnable {
    Message message;
    public FindSuccThread(Message message) {
        this.message = message;
    }

	@Override
	public void run() {
        System.out.println("INSIDE FindSuccThread");
        // FINDSUCC + nodeId + address + port
        String[] header = this.message.getHeader();
        
        System.out.println("RECEIVED: " + header[0] + " " + header[1] + " " + header[2] + " " + header[3]);

        BigInteger nodeId = new BigInteger(header[1]);
        String address = header[2];
        int port = Integer.parseInt(header[3]);

        NodeInfo nodeInfo = Peer.getPeer().getChordNode().findSuccessor(address, port, nodeId); // retornar NodeInfo ou ChordNode?


	}
}