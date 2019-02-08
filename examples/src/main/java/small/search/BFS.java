package small.search;

import java.util.ArrayList;
import java.util.HashSet;

public class BFS {
	public boolean search(Node start, Node finish) {
		final ArrayList<Node> queue = new ArrayList<>();
		queue.add(start);
		final HashSet<Node> visited = new HashSet<>();
		visited.add(start);
		
		while (queue.size() > 0) {
			final Node c = queue.remove(0);
			if (c == finish) {
				return true;
			}
			
			for (Node n : c.getOutgoing()) {
				if (!visited.contains(n)) {
					visited.add(n);
					queue.add(n);
				}
			}
		}
		
		return false;
	}
}
