package small.quicksort;

import jbse.meta.Analysis;

public class Quicksort {
	public void sort(int[] l) {
		Analysis.assume(l.length == 20);
		_sort(l, 0, l.length - 1);
	}
	
	private void _sort(int[] l, int begin, int end) {
		if (begin < end) {
			final int pivot = partition(l, begin, end);
			_sort(l, begin, pivot - 1);
			_sort(l, pivot + 1, end);
		}
	}
	
	private int partition(int[] l, int begin, int end) {
	    int pivot = begin;

	    for (int i = begin + 1; i <= end; ++i) {
	    	if (l[i] <= l[begin]) {
	    		pivot += 1;
	    		final int temp = l[i];
	    		l[i] = l[pivot];
	    		l[pivot] = temp;
	    	}
	    }
		final int temp = l[pivot];
		l[pivot] = l[begin];
		l[begin] = temp;
		
		return pivot;
	}
}
