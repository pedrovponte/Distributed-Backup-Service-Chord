import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DeleteMessageThread implements Runnable {
    private Message message;
    private int senderId;
    private String address;
    private int port;
    private String fileId;
    private int chunkNo;
    private String protocolVersion;

    public DeleteMessageThread(Message message) {
        this.message = message;
        String[] header = this.message.getHeader();
        this.senderId = Integer.parseInt(header[2]);
        this.address = header[3];
        this.port = Integer.parseInt(header[4]);
        this.fileId = header[5];
        this.chunkNo = Integer.parseInt(header[6]);
        this.protocolVersion = header[0];
    }

    @Override
    public void run() {
        System.out.println("RECEIVED: " + this.protocolVersion + " DELETE " + this.senderId + " " + this.address + " " + this.port + " " + this.fileId + " " + this.chunkNo);
        String path = "peer_" + Peer.getPeerId() + "/backup/"+ this.fileId + "_" + this.chunkNo;
        //System.out.println("PATH: "+ path);

        ConcurrentHashMap<String, Chunk> chunks = Peer.getStorage().getChunksStored();

        for(String key : chunks.keySet()) {
            Chunk chunk = chunks.get(key);
            if(chunk.getFileId().equals(this.fileId)) {
                Peer.getStorage().deleteChunk(key);
                File filename = new File(path);
                filename.delete();
            }
        }
    }
}
