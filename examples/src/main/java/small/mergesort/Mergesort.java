package small.mergesort;

import jbse.meta.Analysis;

public class Mergesort {
	public void sort(int[] l) {
		Analysis.assume(l.length == 100);
		_sort(l);
	}
	
	private void _sort(int[] l) {
		if (l.length > 1) {
			final int mid = l.length / 2;
			final int[] left = new int[mid];
			final int[] right = new int[l.length - mid];
			System.arraycopy(l, 0, left, 0, mid);
			System.arraycopy(l, mid, right, 0, l.length - mid);
			
			_sort(left);
			_sort(right);
			
			int i = 0, j = 0, k = 0;
			while (i < left.length && j < right.length) {
				if (left[i] < right[j]) {
					l[k] = left[i];
					++i;
				} else {
					l[k] = right[j];
					++j;
				}
				++k;
			}
			
			while (i < left.length) {
				l[k] = left[i];
				++i;
				++k;
			}
			
			while (j < right.length) {
				l[k] = right[j];
				++j;
				++k;
			}
		}
	}
}
