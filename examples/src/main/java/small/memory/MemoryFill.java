package small.memory;

public class MemoryFill {
	public void memoryFill(int[] l) {
		int[] memory = new int[16];
		int free = 16;
		
		if (l.length <= free) {
			for (int i = 0; i < l.length; ++i) {
				memory[i] = l[i];
			}
			return;
		}
		
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
