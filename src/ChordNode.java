import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.*;

public class ChordNode {
    private final static int M = 128; // m-bit identifier
    private final static int N = (int) Math.pow(2, M); // maximum number of nodes of the circle
    private final static int R = (int) Math.log10(N); // routing entries
    private int peerId;
    private int port;
    private int id;
    private NodeInfo nodeInfo;
    private AtomicReferenceArray<NodeInfo> fingerTable; // ensure that the array couldn't be updated simultaneously by different threads
    private ArrayList<NodeInfo> successors;
    private NodeInfo predecessor;
    private ScheduledThreadPoolExecutor threadExec;
    private ChordChannel channel;

    public ChordNode(int peerId, int port) {
        this.peerId = peerId;
        this.port = port;
        String ipAddress = null;

        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        InetSocketAddress localAddress = new InetSocketAddress(ipAddress, port);
        this.id = createHashSocketAddress(localAddress.toString());
        this.nodeInfo = new NodeInfo(this.id, localAddress);
        this.fingerTable = new AtomicReferenceArray<>(M);
        this.successors = new ArrayList<>();
        this.threadExec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5); //serao suficientes ou serao muitas?
        initFingerTable();
        initSuccessors();
        createChannel();
    }


    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }


    public void initFingerTable() {
        for(int i = 0; i < M; i++) {
            this.fingerTable.set(i, this.nodeInfo);
        }
    }


    public void initSuccessors() {
        for(int i = 0; i < R; i++) {
            this.successors.add(i, this.nodeInfo);
        }
    }


    public void createChannel() {
        this.channel = new ChordChannel(this);
        this.threadExec.execute(this.channel);
    }




    public int createHashSocketAddress(String socketAddress) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] encodedHash = digest.digest(socketAddress.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.wrap(encodedHash);

            return Math.floorMod(buf.getInt(), N);
            
        } catch(Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
