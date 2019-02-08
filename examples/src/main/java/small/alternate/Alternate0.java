package small.alternate;

import jbse.meta.Analysis;

public class Alternate0 {
	/**
	 * Iterates an expensive task based on the values of the input list.
	 * Worst case: a list that alternates zeros and non-zero values.
	 */
	public void alternate_0(int[] l) {
		Analysis.assume(l.length == 20);
		int check;
		for (int i = 0; i < l.length; ++i) {
			if (l[i] == 0) {
				check = 1 - (i % 2);
			} else {
				check = i % 2;
			}
			
			if (check != 0) {
				executeExpensiveTask();
			}
		}
	}
	
	private int executeExpensiveTask() {
		int i;
		for (i = 1000; i > 0; --i)
			;
		return i;
	}
}
