public class MessageBuilder {
    public MessageBuilder() {

    }

    // FINDSUCC + nodeId + address + port
    public byte[] constructFindSuccessorMessage(NodeInfo nodeInfo) {
        String message = "1.0 FINDSUCC " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }

    // SUCCFOUND + nodeId + address + port
    public byte[] constructSuccessorFoundMessage(NodeInfo nodeInfo) {
        String message = "1.0 SUCCFOUND " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }
    
}