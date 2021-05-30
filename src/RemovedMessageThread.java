public class RemovedMessageThread implements Runnable {
    private Message message;
    private String[] header;
    private int senderId;
    private String fileId;
    private int chunkNo;
    private int replication_degree;
    private String protocolVersion;
    private String address;
    private int port;
    
    // Version REMOVED <SenderId> <Address> <Port> <FileId> <ChunkNo>
    public RemovedMessageThread(Message message) {
        this.message = message;
        this.header = this.message.getHeader();
        this.protocolVersion = this.header[0];
        this.senderId = Integer.parseInt(this.header[2]);
        this.address = this.header[3];
        this.port = Integer.parseInt(header[4]);
        this.fileId = this.header[5];
        this.chunkNo = Integer.parseInt(header[6]);
    }


	@Override
	public void run() {
		
		
	}
    
}