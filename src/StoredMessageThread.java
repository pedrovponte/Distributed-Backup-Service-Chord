public class StoredMessageThread implements Runnable {
    private Message message;
    private int senderId;
    private String address;
    private int port;
    private String fileId;
    private int chunkNo;

    // Version STORED <SenderId> <Address> <Port> <FileId> <ChunkNo>
    public StoredMessageThread(Message message) {
        this.message = message;
        String[] header = this.message.getHeader();
        this.senderId = Integer.parseInt(header[2]);
        this.address = header[3];
        this.port = Integer.parseInt(header[4]);
        this.fileId = header[5];
        this.chunkNo = Integer.parseInt(header[6]);
    }
    
    
    @Override
	public void run() {
		
		
	}
    
}