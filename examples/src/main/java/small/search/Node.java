package small.search;

public class Node {
	private Node[] outgoing = new Node[0];
	
	void addOutgoing(Node to) {
		if (to == null) {
			return;
		}
		for (Node n : this.outgoing) {
			if (n == to) {
				return;
			}
		}
		final Node[] outgoingOld = this.outgoing;
		this.outgoing = new Node[outgoingOld.length + 1];
		System.arraycopy(outgoingOld, 0, this.outgoing, 0, outgoingOld.length);
		this.outgoing[this.outgoing.length - 1] = to;
	}
	
	public Node[] getOutgoing() {
		return this.outgoing;
	}
}
