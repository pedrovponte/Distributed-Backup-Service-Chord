package chord;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ChordChannel implements Runnable {
    private ChordNode parent;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dis;
    protected final ConcurrentLinkedQueue<String[]> receivedMessages; // Queue where the received messages are stored


    public ChordChannel(ChordNode parent) {
        this.parent = parent;
        this.receivedMessages = new ConcurrentLinkedQueue<>();
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

                handleMessage(message);

                this.socket.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(InetSocketAddress address, byte[] message) {
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

            case "FINDSUCC":

                break;
        }
    }

    public void handleHelloMessage(String[] message) {
        // HELLO + nodeId + address + port
        int newNodeId = Integer.parseInt(messageStr[1]);
        NodeInfo successorInfo = this.node.findSuccessor(this.parent.getNodeInfo().getSocketAddress(), newNodeId);
    }

    public void handleFindSuccessor(String[] message) {
        // FINDSUCC + nodeId + address + port
        
    }


    // FINDSUCC + nodeId + address + port
    public byte[] constructFindSuccessorMessage(int nodeId, NodeInfo nodeInfo) {
        String message = "FINDSUCC " + nodeId.toString() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }


    // JOIN + nodeId + address + port
    public byte[] constructHelloMessage(int nodeId) {
        String message = "HELLO " + nodeId + " " + this.parent.getNodeInfo().getIp() + " " + this.parent.getNodeInfo().getPort() + "\r\n\r\n";
        return nodeId.getBytes();
    }

}
