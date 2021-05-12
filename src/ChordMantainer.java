public class ChordMantainer implements Runnable{
    private ChordNode node;
    private String mantainerType;

    public ChordMantainer(ChordNode node, String mantainerType) {
        this.node = node;
        this.mantainerType = mantainerType;
    }

	@Override
	public void run() {
        if(this.mantainerType.equals("stabilize")) {
            this.node.stabilize();
        }
        else if(this.mantainerType.equals("fix_fingers")) {
            this.node.fix_fingers();
        }
        else if(this.mantainerType.equals("check_predecessor")) {
            this.node.check_predecessor();
        }
	}
}