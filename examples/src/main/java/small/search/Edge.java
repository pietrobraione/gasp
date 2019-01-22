package small.search;

public class Edge {
	private final Node from, to;
	
	public Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
	}
	
	public Node getNodeFrom() {
		return this.from;
	}
	
	public Node getNodeTo() {
		return this.to;
	}
}
