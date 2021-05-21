import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;

public class ChannelController implements Runnable {
    private int port;
    private static SSLServerSocket serverSocket;
    private String ipAddress;

    public ChannelController(String ipAddress, int port) {
        this.port = port;
        this.ipAddress = ipAddress;

        //set the type of trust store
        System.setProperty("javax.net.ssl.trustStoreType","JKS");

        //set the password with which the truststore is encripted
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        //set the name of the trust store containing the server's public key and certificate           
        System.setProperty("javax.net.ssl.trustStore", "../../keys/truststore");

        //set the password with which the client keystore is encripted
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        //set the name of the keystore containing the client's private and public keys
        System.setProperty("javax.net.ssl.keyStore","../../keys/keystore");
    }


    public void sendMessage(byte[] message) {
        // send request
        System.out.println("INSIDE SEND MESSAGE");

        SSLSocketFactory ssf;
        SSLSocket socket; 

        ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            socket = (SSLSocket) ssf.createSocket(this.ipAddress, this.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            Message toSend = new Message(message);
            out.writeObject(toSend);
            System.out.println("SEND MESSAGE"); // sera necessario fazer Thread.sleep()?
            socket.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        System.out.println("INSIDE RUN");

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket;

        try {
            serverSocket = (SSLServerSocket) ssf.createServerSocket(this.port);
            System.out.println("CREATED SERVER");

            while(true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                Peer.getThreadExec().execute(new ManageReceivedMessages(message));
                //socket.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
