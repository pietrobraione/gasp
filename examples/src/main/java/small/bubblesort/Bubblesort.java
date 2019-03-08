package small.bubblesort;

import jbse.meta.Analysis;

/**
 * Worst case: array sorted in reverse order.
 */
public class Bubblesort {
	public void sort(int[] l) {
		Analysis.assume(l.length == 100);
		for (int i = l.length - 1; i >= 0; --i) {
			for (int j = 0; j < i; ++j) {
				if (l[j] > l [j + 1]) {
					final int tmp = l[j];
					l[j] = l[j + 1];
					l[j + 1] = tmp;
				}
			}
		}
	}
}
