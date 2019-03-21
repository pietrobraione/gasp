package small.quicksort;

import java.util.Arrays;

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
	
	public void sortWorstCase() {
		final int[] worst100 = {66, 67, 65, 63, 64, 68, 72, 73, 74, 71, 69, 70, 62, 54, 55, 53, 51, 52, 56, 60, 61, 59, 57, 58, 75, 92, 93, 91, 89, 90, 94, 98, 99, 100, 97, 95, 96, 88, 79, 80, 78, 76, 77, 81, 85, 86, 87, 84, 82, 83, 50, 16, 17, 15, 13, 14, 18, 22, 23, 24, 21, 19, 20, 12, 4, 5, 3, 1, 2, 6, 10, 11, 9, 7, 8, 25, 41, 42, 40, 38, 39, 43, 47, 48, 49, 46, 44, 45, 37, 29, 30, 28, 26, 27, 31, 35, 36, 34, 32, 33};
		sort(worst100);
	}
	
	/**
	 * Returns a worst-case array for the quicksort
	 * algorithm. The worst case holds when on the 
	 * left of the pivot there are always elements 
	 * greater than the pivot value, and on the right 
	 * there are only elements are less than the pivot 
	 * value.
	 * 
	 * @param size the size of the array.
	 * @return a worst-case array for quicksort.
	 */
	private static int[] worstCase(int size) {
		if (size < 0) {
			throw new NegativeArraySizeException();
		} else {
			return worstCaseRange(1, size);
		}
	}
			
	private static int[] worstCaseRange(int min, int max) {
		if (min > max) {
			return new int[0];
		} else if (min == max) {
			return new int[] { min };
		} else {
			//the final array will be balanced, thus the pivot will always chosen 
			//to be in the middle of the array; this allows to split recursively
			//the problem by having the central value of [min, max] to be the
			//pivot, half the values in the range (the ones greater than the pivot)
			//on the left of the pivot, and half the values (the ones less than the
			//pivot) on the right of the pivot.
			int pivotValue = (min + max) / 2; 
			int[] lower = reverse(worstCaseRange(pivotValue + 1, max)); //we must reverse because the swapping procedure reverses the lower/upper arrays when copying them
			int[] upper = reverse(worstCaseRange(min, pivotValue - 1)); //same
			
			//concatenates to obtain the return value
			final int[] retVal = new int[max - min + 1];
			System.arraycopy(lower, 0, retVal, 0, lower.length);
			retVal[lower.length] = pivotValue;
			System.arraycopy(upper, 0, retVal, lower.length + 1, upper.length);
			return retVal;
		}
	}
	
	private static int[] reverse(int[] a) {
		final int[] retVal = new int[a.length];
		for (int i = 0; i < a.length; ++i) {
			retVal[a.length - i - 1] = a[i];
		}
		return retVal;
	}
	
	public static void main(String[] s) {
		System.out.println(Arrays.toString(worstCase(100)));
	}
}
