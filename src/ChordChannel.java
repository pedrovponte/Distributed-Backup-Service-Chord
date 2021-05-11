import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class ChordChannel implements Runnable {
    private ChordNode parent;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dis;
    protected final ConcurrentLinkedQueue<String[]> receivedMessages; // Queue where the received messages are stored
    private int peerId;


    public ChordChannel(ChordNode parent) {
        this.parent = parent;
        this.receivedMessages = new ConcurrentLinkedQueue<>();
        this.peerId = 1;
        open();
    }

    public void open() {
        try {
            this.serverSocket = new ServerSocket(this.parent.getNodeInfo().getPort());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                this.socket = this.serverSocket.accept();
                this.dis = new DataInputStream(socket.getInputStream());

                byte[] message = new byte[65000];
                this.dis.read(message);

                //handleMessage(message);

                this.socket.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*public void sendMessage(InetSocketAddress address, byte[] message) {
        if(address.equals(this.parent.getNodeInfo().getSocketAddress())) {
            handleMessage(message);
            return;
        }

        try {
            Socket s = new Socket(address.getAddress(), address.getPort());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(message.length);
            dos.write(message, 0, message.length);
            dos.flush();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleMessage(byte[] message) {
        String[] messageStr = new String(message).split(" ");

        switch (messageStr[0]) {
            case "HELLO":
                handleHelloMessage(messageStr);
                break;

            case "HELLO_RESPONSE":
                handleHelloResponseMessage(messageStr);
                break;

            case "FINDSUCC":

                break;
        }
    }

    public void handleHelloMessage(String[] message) {
        // HELLO + peerId
        System.out.println("RECEIVED HELLO");
        
        byte[] response = constructHelloResponseMessage();

        this.parent.getChannel().sendMessage(new InetSocketAddress(message[0], Integer.parseInt(message[1])), response);
    }


    public void handleHelloResponseMessage(String[] message) {
        System.out.println("RECEIVED HELLO_RESPONSE");
    }

    public void handleFindSuccessor(String[] message) {
        // FINDSUCC + nodeId + address + port
        
    }


    // FINDSUCC + nodeId + address + port
    public byte[] constructFindSuccessorMessage(int nodeId, NodeInfo nodeInfo) {
        String message = "FINDSUCC " + nodeId + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }


    // HELLO + peerId + address + port
    public byte[] constructHelloMessage(NodeInfo nodeInfo) {
        String message = "HELLO " +  this.peerId + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }

    public byte[] constructHelloResponseMessage() {
        String message = "HELLO_RESPONSE " + this.peerId + "\r\n\r\n";
        return message.getBytes();
    }*/

}
