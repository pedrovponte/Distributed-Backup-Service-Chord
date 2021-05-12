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
        
        System.out.println("RECEIVED: " + header[0] + " " + header[1] + " " + header[2] + " " + header[3] + " " + header[4]);

        BigInteger nodeId = new BigInteger(header[2]);
        String address = header[3];
        int port = Integer.parseInt(header[4]);

        NodeInfo nodeInfo = Peer.getChordNode().findSuccessor(address, port, nodeId); // retornar NodeInfo ou ChordNode?

        if(nodeInfo != null) {
            MessageBuilder messageBuilder = new MessageBuilder();
            byte[] response = messageBuilder.constructFindSuccessorMessage(nodeInfo);
            System.out.println("SENT: " + response.toString());
            Peer.getThreadExec().execute(new ThreadSendMessages(address, port, response));
        }
	}
}