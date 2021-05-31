import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class GetChunkMessageThread implements Runnable {
    private Message message;
    private int senderId;
    private String address;
    private int port;
    private String fileId;
    private int chunkNo;
    private String protocolVersion;
    private final ScheduledThreadPoolExecutor threadExec;

    private String senderAddress;
    private int senderPort;


    public GetChunkMessageThread(Message message, ScheduledThreadPoolExecutor threadExec, String senderAddress, int senderPort) {
        this.message = message;
        String[] header = this.message.getHeader();
        this.senderId = Integer.parseInt(header[2]);
        this.address = header[3];
        this.port = Integer.parseInt(header[4]);
        this.fileId = header[5];
        this.chunkNo = Integer.parseInt(header[6]);
        this.protocolVersion = header[0];
        this.threadExec = threadExec;
        this.senderPort = senderPort;
        this.senderAddress = senderAddress;
    }

    @Override
    public void run() {
        System.out.println("RECEIVED: " + this.protocolVersion + " GETCHUNK " + this.senderId + " " + this.address + " " + this.port + " " + this.fileId + " " + this.chunkNo);
        String path = "peer_" + Peer.getPeerId() + "/backup/" + this.fileId + "_" + this.chunkNo;
        System.out.println("PATH: " + path);

        FileManager fileManager = new FileManager(path, Peer.getPeerId());
        Chunk chunk= fileManager.getFileChunks().get(0);
        MessageBuilder messageBuilder = new MessageBuilder();

        //TODO contar os chunks recebidos
        byte[] message = messageBuilder.constructChunkMessage(this.address,this.port, this.fileId, this.chunkNo, chunk.getChunkMessage());
        threadExec.execute(new ThreadSendMessages(senderAddress, senderPort, message));
    }
}
