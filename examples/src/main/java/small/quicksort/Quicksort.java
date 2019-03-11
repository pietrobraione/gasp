package small.quicksort;

import jbse.meta.Analysis;

/**
 * Sorts an array with the quicksort algorithm.
 * 
 * Expected worst case: array sorted in same or reverse order.
 */
public class Quicksort {
	public void sort(int[] l) {
		Analysis.assume(l.length == 100);
		_sort(l, 0, l.length - 1);
	}
	
	private void _sort(int[] l, int off, int size) {
		//Choose a partition element, v
	    int pivot = off + (size / 2); //Small arrays
	    if (size > 7) {
	        int e1 = off;
	        int e2 = off + size - 1;
	        if (size > 40) { //Big arrays, pseudomedian of 9
	        	int e3 = size / 8;
	            e1 = med3(l, e1, e1 + e3, e1 + 2 * e3);
	            pivot = med3(l, pivot - e3, pivot, pivot + e3);
	            e2 = med3(l, e2 - 2 * e3, e2 - e3, e2);
	        }
	        pivot = med3(l, e1, pivot, e2); //# Mid - size, med of 3
	    }
	    int v = l[pivot];
	    
	    // Establish Invariant: v * (< v) * (> v) * v *
	    int a = off;
	    int b = a;
	    int c = off + size - 1;
	    int d = c;
	    while (true) {
	        while (b <= c && l[b] <= v) {
	            if (l[b] == v) {
	            	int temp = l[a];
	                l[a] = l[b];
	                l[b] = temp;
	                a = a + 1;
	            }
	            b = b + 1;
	        }
	        while (c >= b && l[c] >= v) {
	            if (l[c] == v) {
	            	int temp = l[c];
	            	l[c] = l[d];
	            	l[d] = temp;
	                d = d - 1;
	            }
	            c = c - 1;
	        }
	        if (b > c) {
	            break;
	        }
	        int temp = l[b];
	        l[b] = l[c];
	        l[c] = temp;
	        b = b + 1;
	        c = c - 1;
	    }

	    //Swap partition elements back to middle
	    int n = off + size;
	    int s = Math.min(a - off, b - a);
	    vecswap(l, off, b - s, s);
	    s = Math.min(d - c, n - d - 1);
	    vecswap(l, b, n - s, s);
	    //print (" * inv on pivot back to middle: ", x[off:off+size])

	    // Recursively sort non-partition elements
	    s = b - a;
	    //print (" * s = b - a: ", s, " -  a, b = ", b, ", ", a)
	    if (s > 1) {
	        _sort(l, off, s);
	    }
	    s = d - c;
	    //print (" * s = d - c: ", s, " - d, c = ", d, ", ", c)
	    if (s > 1) {
	        _sort(l, n - s, s);
	    }
	}


	private int med3(int[] x, int a, int b, int c) {
	    if (x[a] < x[b]) {
	        if (x[b] < x[c]) {
	            return b;
	        } else if (x[a] < x[c]) {
	            return c;
	        } else {
	            return a;
	        }
	    } else {
	        if (x[b] > x[c]) {
	            return b;
	        } else if (x[a] > x[c]) {
	            return c;
	        } else {
	            return a;
	        }
	    }
	}

	private void vecswap(int[] x, int a, int b, int n) {
	    for (int i = 0; i < n; i++) {
	    	int temp = x[a];
	        x[a] = x[b];
	        x[b] = temp;
	        a = a + 1;
	        b = b + 1;
	    }
	}
}
