import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ChordChannel implements Runnable {
    private ChordNode parent;
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream dis;


    public ChordChannel(ChordNode parent) {
        this.parent = parent;
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

                String messageStr = new String(message, StandardCharsets.ISO_8859_1);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
