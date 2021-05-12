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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.*;

public class ChordNode {
    private final static int M = 128; // m-bit identifier
    private final static int N = (int) Math.pow(2, M); // maximum number of nodes of the circle
    private final static int R = (int) Math.log10(N); // routing entries
    private int port;
    private BigInteger id;
    private NodeInfo nodeInfo;
    private AtomicReferenceArray<NodeInfo> fingerTable; // ensure that the array couldn't be updated simultaneously by different threads
    private NodeInfo successor;
    private NodeInfo predecessor;
    private ScheduledThreadPoolExecutor threadExec;
    private InetSocketAddress localAddress;
    private CountDownLatch joinCountDownLatch;

    public ChordNode(String address, int port) {
        this.port = port;
        //System.out.println("PORT: " + this.port);
        String ipAddress = address;
        this.localAddress = new InetSocketAddress(ipAddress, port);
        byte[] idByte = createHashSocketAddress(localAddress);
        this.id = new BigInteger(1, idByte);
        this.nodeInfo = new NodeInfo(this.id, this.localAddress);
        this.fingerTable = new AtomicReferenceArray<>(M);
        this.predecessor = null;
        this.successor = this.nodeInfo;
        this.joinCountDownLatch = new CountDownLatch(1);
        initFingerTable();
    }


    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }


    public void initFingerTable() {
        for(int i = 0; i < M; i++) {
            this.fingerTable.set(i, this.nodeInfo);
        }
    }

    public void decrementJoinCountDownLatch() {
        //System.out.println("DECREMENT LATCH");
        this.joinCountDownLatch.countDown();
    }

    public void setFingerTablePosition(int pos, NodeInfo node) {
        //System.out.println("SET FINGER TABLE");
        this.fingerTable.set(pos, node);
    }

    public void setSuccessor(NodeInfo node) {
        //System.out.println("SET SUCCESSOR");
        this.successor = node;
    }

    public void setPredecessor(NodeInfo node) {
        this.predecessor = node;
    }

    public NodeInfo getPredecessor() {
        return this.predecessor;
    }


    // first node to enter in the ring needs to create that
    public void create() {
        //System.out.println("INSIDE CREATE CHORD");
        this.fingerTable.set(0, this.nodeInfo);
        // call threads that will search/ actualize successors, fingers, predecessor, etc...
        mantainer();
    }


    public void join(String address, int port) {
        // send message to find successor
        //System.out.println("INSIDE JOIN");
        MessageBuilder messageBuilder = new MessageBuilder();
        byte[] message = messageBuilder.constructFindSuccessorMessage(this.nodeInfo);
        System.out.println("SENT: " + message.toString());
        Peer.getThreadExec().execute(new ThreadSendMessages(address, port, message));
        
        try {
            this.joinCountDownLatch.await();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("JOINED");

        this.predecessor = this.successor;
        
        // necessario algum update da finget_table?
        mantainer();
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

        if(this.nodeInfo.getNodeId().equals(this.successor.getNodeId())) { // existe apenas um nó, por isso o sucessor é igual ao próprio nó
            //System.out.println("INSIDE FIRST IF");
            if(!nodeId.equals(this.nodeInfo.getNodeId())) { // o nó que procura o sucessor não é o mesmo que o que recebe a mensagem 
                this.predecessor = new NodeInfo(nodeId, new InetSocketAddress(ipAddress, port));
                this.successor = new NodeInfo(nodeId, new InetSocketAddress(ipAddress, port));
                this.fingerTable.set(0, this.successor);
            }
            return this.nodeInfo; // o sucessor do nó que o procura é este, assim como o sucessor deste nó é o nó que estava a procura de sucessor
        }

        if(compareNodeIds(this.nodeInfo.getNodeId(), nodeId, this.successor.getNodeId())) { // se estiver compreendido entre este nó e sucessor
            //System.out.println("INSIDE SECOND IF");
            return this.successor;
        }

        else {
            //System.out.println("INSIDE ELSE");
            NodeInfo n = closestPrecedingNode(nodeId);

            if(this.nodeInfo.getNodeId() == n.getNodeId()) {
                return this.nodeInfo;
            }

            MessageBuilder messageBuilder = new MessageBuilder();
            byte[] message = messageBuilder.constructFindSuccessorMessage(this.nodeInfo);
            System.out.println("SENT: " + message.toString());
            Peer.getThreadExec().execute(new ThreadSendMessages(n.getSocketAddress().getAddress().getHostAddress(), n.getSocketAddress().getPort(), message));

            return null;
        }
    }

    
    // search the local table for the highest predecessor of nodeId
    public NodeInfo closestPrecedingNode(BigInteger nodeId) {
        // search locally for the highest predecessor of id
        /*procedure closestPrecedingNode(id) {
            for i = m downto 1 do {
                if (finger[i] in (n, id)) then
                    return finger[i]
            }
            return n
        }*/

        NodeInfo fingerNode = null;

        for(int i = M - 1; i >= 0; i--) {
            if(this.fingerTable.get(i) == null) {
                continue;
            }

            if(compareNodeIds(this.nodeInfo.getNodeId(), this.fingerTable.get(i).getNodeId(), nodeId)) {
                return this.fingerTable.get(i);
            }
        }

        return this.nodeInfo;
    }

    public void mantainer() {
        Peer.getThreadExec().scheduleAtFixedRate(new ChordMantainer(this, "stabilize"), 3, 3, TimeUnit.SECONDS);
        Peer.getThreadExec().scheduleAtFixedRate(new ChordMantainer(this, "fix_fingers"), 3, 5, TimeUnit.SECONDS);
        Peer.getThreadExec().scheduleAtFixedRate(new ChordMantainer(this, "check_predecessor"), 3, 4, TimeUnit.SECONDS);
    }

    public void stabilize() {
        NodeInfo succ_pred = null;

        if(this.nodeInfo.getNodeId().equals(this.fingerTable.get(0).getNodeId()) && this.nodeInfo.getIp().equals(this.fingerTable.get(0).getIp()) && this.nodeInfo.getPort() == this.fingerTable.get(0).getPort()) {
            succ_pred = this.predecessor;
        }
        else {
            MessageBuilder messageBuilder = new MessageBuilder();
            byte[] message = messageBuilder.constructFindPredecessorMessage(this.nodeInfo);
            System.out.println("SENT: " + message.toString());
            Peer.getThreadExec().execute(new ThreadSendMessages(this.nodeInfo.getIp(), this.nodeInfo.getPort(), message));
        }
    }

    public void fix_fingers() {
        System.out.println("FIX FINGERS");
    }

    public void check_predecessor() {
        System.out.println("CHECK PREDECESSOR");
    }


    public boolean compareNodeIds(BigInteger lower, BigInteger test, BigInteger upper) {
        if(lower.compareTo(upper) == -1) { //lower < upper
            return lower.compareTo(test) == -1 && test.compareTo(upper) == -1; //lower < test and test < upper
        }
        else if(lower.compareTo(upper) == 0) {
            return true;
        }
        else {
            return test.compareTo(lower) == 1 || test.compareTo(upper) == -1; //test > lower || test < upper
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