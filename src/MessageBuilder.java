public class MessageBuilder {
    public MessageBuilder() {

    }

    // Version FINDSUCC + nodeId + address + port
    public byte[] constructFindSuccessorMessage(NodeInfo nodeInfo) {
        String message = "1.0 FINDSUCC " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }

    // Version SUCCFOUND + nodeId + address + port
    public byte[] constructSuccessorFoundMessage(NodeInfo nodeInfo) {
        //System.out.println("INSIDE SUCCFOUND");
        String message = "1.0 SUCCFOUND " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }

    public byte[] constructFindPredecessorMessage(NodeInfo nodeInfo) {
        System.out.println("INSIDE FINDPRED");
        String message = "1.0 FINDPRED " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }

    public byte[] constructPredecessorFoundMessage(NodeInfo nodeInfo) {
        System.out.println("INSIDE PREDFOUND");
        String message = "1.0 PREDFOUND " + nodeInfo.getNodeId() + " " + nodeInfo.getIp() + " " + nodeInfo.getPort() + "\r\n\r\n";
        return message.getBytes();
    }
    
}