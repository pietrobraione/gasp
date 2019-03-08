package small.search;

import static jbse.meta.Analysis.assume;

import java.util.HashSet;

/**
 * Determines if the vertex "finish" is reachable from the vertex "start" 
 * in the given graph by means of a depth-first visit of the graph.
 * 
 * Expected worst case: a fully connected directed graph, except for the vertex 
 * "finish" that is reachable only from the "start" vertex of which it 
 * is the last neighbor. 
 */
public class DFS {
	public boolean search(Node start, Node finish) {
		return _search(start, finish, new HashSet<>());
	}
	
	private boolean _search(Node current, Node finish, HashSet<Node> visited) {
		if (current == finish) {
			return true;
		}
		
		visited.add(current);
			
		final Node[] outgoing = current.getOutgoing();
		assume(outgoing != null);
		assume(outgoing.length <= 10);
		for (Node neighbor : outgoing) {
			assume(neighbor != null);
			if (!visited.contains(neighbor)) {
				if (_search(neighbor, finish, visited)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
