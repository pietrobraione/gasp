package small.search;

import static jbse.meta.Analysis.assume;

import java.util.ArrayList;

public class Search {
	public boolean depthFirstSearch(Node start, Node finish) {
		final ArrayList<Node> visited = new ArrayList<>();
		visited.add(start);
		
		while (visited.size() > 0) {
			final Node current = visited.get(visited.size() - 1);
			
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
					visited.add(neighbor);
				}
			}
		}
		
		return false;
	}
}
