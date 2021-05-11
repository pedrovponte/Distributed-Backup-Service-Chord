import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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
    private int port;
    private BigInteger id;
    private NodeInfo nodeInfo;
    private AtomicReferenceArray<NodeInfo> fingerTable; // ensure that the array couldn't be updated simultaneously by different threads
    private ArrayList<NodeInfo> successors;
    private NodeInfo predecessor;
    private ScheduledThreadPoolExecutor threadExec;
    private InetSocketAddress localAddress;

    public ChordNode(String address, int port) {
        this.port = port;
        String ipAddress = address;
        this.localAddress = new InetSocketAddress(ipAddress, port);
        byte[] idByte = createHashSocketAddress(localAddress);
        this.id = new BigInteger(1, idByte);
        this.nodeInfo = new NodeInfo(this.id, this.localAddress);
        this.fingerTable = new AtomicReferenceArray<>(M);
        this.successors = new ArrayList<>();
        initFingerTable();
        initSuccessors();
    }


    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }


    public NodeInfo getSuccessor() {
        return this.successors.get(0);
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


    // first node to enter in the ring needs to create that
    public void create() {
        System.out.println("INSIDE CREATE CHORD");
        this.predecessor = null;
        this.fingerTable.set(0, this.nodeInfo);
        this.successors.set(0, this.nodeInfo);
        // call threads that will search/ actualize successors, fingers, predecessor, etc...
    }


    public void join(String address, int port) {
        // send message to find successor
        System.out.println("INSIDE JOIN");
        MessageBuilder messageBuilder = new MessageBuilder();
        byte[] message = messageBuilder.constructFindSuccessorMessage(this.nodeInfo);
        System.out.println("SENT: " + message.toString());
        Peer.getThreadExec().execute(new ThreadSendMessages(address, port, message));
        
    }


    public NodeInfo findSuccessor(String ipAddress, int port, BigInteger nodeId) {
        // ask node n to find the successor of id
        /*procedure n.findSuccessor(id) {
            if (predecessor != nil and id in (predecessor, n]) then return n
            else if (id in (n, successor]) then
                return successor
            else { // forward the query around the circle
                m := closestPrecedingNode(id)
                return m.findSuccessor(id)
            }
        }*/
        
        System.out.println("INSIDE FIND SUCCESSOR");
        /*if(compareNodeIds(this.nodeInfo.getNodeId(), nodeId, this.fingerTable.get(0).getNodeId()) || nodeId == this.fingerTable.get(0).getNodeId()) {
            return this.fingerTable.get(0);
        }
        else {
            NodeInfo n = closestPrecedingNode(nodeId);

            if(this.nodeInfo.getNodeId() == n.getNodeId()) {
                return this.nodeInfo;
            }

            byte[] message = this.channel.constructFindSuccessorMessage(nodeId, n);
            this.channel.sendMessage(n.getSocketAddress(), message);
            return n;
        }*/
        return this.nodeInfo;
    }

    
    // search the local table for the highest predecessor of nodeId
    public NodeInfo closestPrecedingNode(int nodeId) {
        // search locally for the highest predecessor of id
        /*procedure closestPrecedingNode(id) {
            for i = m downto 1 do {
                if (finger[i] in (n, id)) then
                    return finger[i]
            }
            return n
        }*/

        /*NodeInfo fingerNode = null;

        for(int i = M - 1; i >= 0; i--) {
            if(this.fingerTable.get(i) == null) {
                continue;
            }

            if(compareNodeIds(this.nodeInfo.getNodeId(), this.fingerTable.get(i).getNodeId(), nodeId)) {
                fingerNode = this.fingerTable.get(i);
            }
        }

        if(fingerNode == null) {
            fingerNode = this.fingerTable.get(0);
        }

        for(int j = R - 1; j >= 0; j--) {
            if(this.successors.get(j) == null) {
                continue;
            }

            NodeInfo succ = this.successors.get(j);

            if(compareNodeIds(fingerNode.getNodeId(), succ.getNodeId(), nodeId)) {
                return succ;
            }
        }
        
        return fingerNode;*/
        return this.nodeInfo;
    }


    public boolean compareNodeIds(int lower, int test, int upper) {
        if(lower < upper) {
            return lower < test && test < upper;
        }
        else if(lower == upper) {
            return true;
        }
        else {
            return test > lower || test < upper;
        }
    }


    public byte[] createHashSocketAddress(InetSocketAddress socketAddress) {
        try{
            String toHash = socketAddress.getAddress().getHostAddress() + socketAddress.getPort();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] encodedHash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            
            return encodedHash;
        } catch(Exception e) {
            throw new RuntimeException(e);
        } 
    }
}