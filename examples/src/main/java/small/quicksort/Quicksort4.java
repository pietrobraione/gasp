package small.quicksort;

/**
 * Sorts an array with the quicksort algorithm.
 * 
 * Expected worst case: see worstCase method.
 */
public class Quicksort4 {
	int i0 = 0,  i1 = 0,  i2 = 0,  i3 = 0,  i4 = 0,  i5 = 0,  i6 = 0,  i7 = 0,  i8 = 0,  i9 = 0;
	int i10 = 0, i11 = 0, i12 = 0, i13 = 0, i14 = 0, i15 = 0, i16 = 0, i17 = 0, i18 = 0, i19 = 0;
	int i20 = 0, i21 = 0, i22 = 0, i23 = 0, i24 = 0, i25 = 0, i26 = 0, i27 = 0, i28 = 0, i29 = 0;
	int i30 = 0, i31 = 0, i32 = 0, i33 = 0, i34 = 0, i35 = 0, i36 = 0, i37 = 0, i38 = 0, i39 = 0;
	int i40 = 0, i41 = 0, i42 = 0, i43 = 0, i44 = 0, i45 = 0, i46 = 0, i47 = 0, i48 = 0, i49 = 0;
	
	public void sort() {
		final int[] l = new int[50];
		l[0] = i0;   l[1] = i1;   l[2] = i2;   l[3] = i3;   l[4] = i4;   l[5] = i5;   l[6] = i6;   l[7] = i7;   l[8] = i8;   l[9] = i9;
		l[10] = i10; l[11] = i11; l[12] = i12; l[13] = i13; l[14] = i14; l[15] = i15; l[16] = i16; l[17] = i17; l[18] = i18; l[19] = i19;
		l[20] = i20; l[21] = i21; l[22] = i22; l[23] = i23; l[24] = i24; l[25] = i25; l[26] = i26; l[27] = i27; l[28] = i28; l[29] = i29;
		l[30] = i30; l[31] = i31; l[32] = i32; l[33] = i33; l[34] = i34; l[35] = i35; l[36] = i36; l[37] = i37; l[38] = i38; l[39] = i39;
		l[40] = i40; l[41] = i41; l[42] = i42; l[43] = i43; l[44] = i44; l[45] = i45; l[46] = i46; l[47] = i47; l[48] = i48; l[49] = i49;
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
	    //...
		sort();
	}
}
