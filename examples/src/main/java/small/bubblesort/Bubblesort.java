package small.bubblesort;

import jbse.meta.Analysis;

public class Bubblesort {
	public void sort(int[] l) {
		Analysis.assume(l.length == 20);
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
