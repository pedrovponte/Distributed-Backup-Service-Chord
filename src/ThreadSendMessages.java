import java.net.InetAddress;

public class ThreadSendMessages implements Runnable {
    private byte[] message;
    private ChannelController channel;
    private InetAddress address;
    
    public ThreadSendMessages(ChannelController channel, byte[] message, InetAddress address) {
        this.message = message;
        this.channel = channel;
        this.address = address;
    }


    @Override
    public void run() {
        this.channel.sendMessage(this.message, this.address);
    }

    
}
