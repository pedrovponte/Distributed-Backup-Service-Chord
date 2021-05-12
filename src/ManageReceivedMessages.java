public class ManageReceivedMessages implements Runnable {
    private Message message;

    public ManageReceivedMessages(Message message) {
        this.message = message;
    }

    
    // checks the message type and then creates a new thread to treat that message
    public void run() {
        System.out.println("INSIDE MESSAGE MANAGER");
        // message: <Version> <MessageType> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>
        String[] header = this.message.getHeader();
        System.out.println("MESSAGE: " + header[0] + " " + header[1] + " " + header[2] + " " + header[3] + " " + header[4]);

        switch (header[1]) {
            case "FINDSUCC":
                Peer.getThreadExec().execute(new FindSuccThread(this.message));
                break;

            case "SUCCFOUND":
                Peer.getThreadExec().execute(new SuccFoundThread(this.message));
                break;

            case "FINDPRED":
                Peer.getThreadExec().execute(new FindPredThread(this.message));
                break;

            case "PREDFOUND":
                Peer.getThreadExec().execute(new PredFoundThread(this.message));
                break;

            case "PUTCHUNK":

                break;

            case "STORED":

                break;

            case "DELETE":

                break;

            case "GETCHUNK":

                break;

            case "CHUNK":

                break;

            case "REMOVED":

                break;

            case "DELETED":

                break;

            default:

                break;
        }
        /*String[] messageStr = new String(this.message).split(" ");
        // System.out.println("Manager message: " + messageStr);
        switch (messageStr[1]){
            case "PUTCHUNK":
                Random r = new Random();
                int low = 0;
                int high = 400;
                int result = r.nextInt(high-low) + low;
                this.peer.getThreadExec().schedule(new PutChunkMessageThread(this.message, this.peer), result, TimeUnit.MILLISECONDS);
                break;

            case "STORED":
                this.peer.getThreadExec().execute(new StoredMessageThread(this.message, this.peer));
                break;

            case "DELETE":
                this.peer.getThreadExec().execute(new DeleteMessageThread(this.message, this.peer));
                break;

            case "GETCHUNK":
                this.peer.getThreadExec().execute(new GetChunkMessageThread(this.message, this.peer));
                break;

            case "CHUNK":
                this.peer.getThreadExec().execute(new ChunkMessageThread(this.message, this.peer));
                break;

            case "REMOVED":
                this.peer.getThreadExec().execute(new RemovedMessageThread(this.message, this.peer));
                break;

            case "DELETED":
                this.peer.getThreadExec().execute(new DeletedMessageThread(this.message, this.peer));
                break;

            case "WORKING":
                this.peer.getThreadExec().execute(new WorkingMessageThread(this.message, this.peer));
                break;
            
            case "CHUNKTCP":
                this.peer.getThreadExec().execute(new ChunkTCPMessageThread(this.message, this.peer));
                break;
                
            default:
                break;
        }*/

    }
    
}
