package small.memory;

import jbse.meta.Analysis;

public class MemoryFill {
	/**
	 * Copy non-zero values in a buffer of 16 cells. 
	 * Worst case: a list of non-zero values.
	 */
	public void memoryFill(int[] l) {
		Analysis.assume(l.length == 100);
		int[] memory = new int[16];
		int free = 16;
		
		if (l.length <= free) {
			for (int i = 0; i < l.length; ++i) {
				memory[i] = l[i];
			}
			return;
		}
		
		//compresses by skipping zeros, and fills
		//until space is available
		for (int i = 0; i < l.length; ++i) {
			if (l[i] != 0) {
				free -= 1;
				if (free >= 0) {
					memory[free] = l[i];
				}
			}
		}
	}
}
