package small.quicksort;

/**
 * Sorts an array with the quicksort algorithm.
 * 
 * Expected worst case: see worstCase method.
 */
public class Quicksort100 {
	int i0 = 0,  i1 = 0,  i2 = 0,  i3 = 0,  i4 = 0,  i5 = 0,  i6 = 0,  i7 = 0,  i8 = 0,  i9 = 0;
	int i10 = 0, i11 = 0, i12 = 0, i13 = 0, i14 = 0, i15 = 0, i16 = 0, i17 = 0, i18 = 0, i19 = 0;
	int i20 = 0, i21 = 0, i22 = 0, i23 = 0, i24 = 0, i25 = 0, i26 = 0, i27 = 0, i28 = 0, i29 = 0;
	int i30 = 0, i31 = 0, i32 = 0, i33 = 0, i34 = 0, i35 = 0, i36 = 0, i37 = 0, i38 = 0, i39 = 0;
	int i40 = 0, i41 = 0, i42 = 0, i43 = 0, i44 = 0, i45 = 0, i46 = 0, i47 = 0, i48 = 0, i49 = 0;
	int i50 = 0, i51 = 0, i52 = 0, i53 = 0, i54 = 0, i55 = 0, i56 = 0, i57 = 0, i58 = 0, i59 = 0;
	int i60 = 0, i61 = 0, i62 = 0, i63 = 0, i64 = 0, i65 = 0, i66 = 0, i67 = 0, i68 = 0, i69 = 0;
	int i70 = 0, i71 = 0, i72 = 0, i73 = 0, i74 = 0, i75 = 0, i76 = 0, i77 = 0, i78 = 0, i79 = 0;
	int i80 = 0, i81 = 0, i82 = 0, i83 = 0, i84 = 0, i85 = 0, i86 = 0, i87 = 0, i88 = 0, i89 = 0;
	int i90 = 0, i91 = 0, i92 = 0, i93 = 0, i94 = 0, i95 = 0, i96 = 0, i97 = 0, i98 = 0, i99 = 0;
	
	public void sort() {
		final int[] l = new int[100];
		l[0] = i0;   l[1] = i1;   l[2] = i2;   l[3] = i3;   l[4] = i4;   l[5] = i5;   l[6] = i6;   l[7] = i7;   l[8] = i8;   l[9] = i9;
		l[10] = i10; l[11] = i11; l[12] = i12; l[13] = i13; l[14] = i14; l[15] = i15; l[16] = i16; l[17] = i17; l[18] = i18; l[19] = i19;
		l[20] = i20; l[21] = i21; l[22] = i22; l[23] = i23; l[24] = i24; l[25] = i25; l[26] = i26; l[27] = i27; l[28] = i28; l[29] = i29;
		l[30] = i30; l[31] = i31; l[32] = i32; l[33] = i33; l[34] = i34; l[35] = i35; l[36] = i36; l[37] = i37; l[38] = i38; l[39] = i39;
		l[40] = i40; l[41] = i41; l[42] = i42; l[43] = i43; l[44] = i44; l[45] = i45; l[46] = i46; l[47] = i47; l[48] = i48; l[49] = i49;
		l[50] = i50; l[51] = i51; l[52] = i52; l[53] = i53; l[54] = i54; l[55] = i55; l[56] = i56; l[57] = i57; l[58] = i58; l[59] = i59;
		l[60] = i60; l[61] = i61; l[62] = i62; l[63] = i63; l[64] = i64; l[65] = i65; l[66] = i66; l[67] = i67; l[68] = i68; l[69] = i69;
		l[70] = i70; l[71] = i71; l[72] = i72; l[73] = i73; l[74] = i74; l[75] = i75; l[76] = i76; l[77] = i77; l[78] = i78; l[79] = i79;
		l[80] = i80; l[81] = i81; l[82] = i82; l[83] = i83; l[84] = i84; l[85] = i85; l[86] = i86; l[87] = i87; l[88] = i88; l[89] = i89;
		l[90] = i90; l[91] = i91; l[92] = i92; l[93] = i93; l[94] = i94; l[95] = i95; l[96] = i96; l[97] = i97; l[98] = i98; l[99] = i99;
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
		i0 = 66;  i1 = 67;  i2 = 65;  i3 = 63;   i4 = 64;  i5 = 68;  i6 = 72;  i7 = 73;  i8 = 24;  i9 = 71; 
		i10 = 69; i11 = 70; i12 = 62; i13 = 54;  i14 = 55; i15 = 53; i16 = 51; i17 = 52; i18 = 56; i19 = 60; 
		i20 = 61; i21 = 59; i22 = 57; i23 = 58;  i24 = 75; i25 = 92; i26 = 93; i27 = 91; i28 = 89; i29 = 90; 
		i30 = 94; i31 = 98; i32 = 99; i33 = 100; i34 = 97; i35 = 95; i36 = 96; i37 = 88; i38 = 79; i39 = 80; 
		i40 = 78; i41 = 76; i42 = 77; i43 = 81;  i44 = 85; i45 = 86; i46 = 87; i47 = 84; i48 = 82; i49 = 83; 
		i50 = 50; i51 = 16; i52 = 17; i53 = 15;  i54 = 13; i55 = 14; i56 = 18; i57 = 22; i58 = 23; i59 = 24; 
		i60 = 21; i61 = 19; i62 = 20; i63 = 12;  i64 = 4;  i65 = 5;  i66 = 3;  i67 = 1;  i68 = 2;  i69 = 6; 
		i70 = 10; i71 = 11; i72 = 9;  i73 = 7;   i74 = 8;  i75 = 25; i76 = 41; i77 = 42; i78 = 40; i79 = 38; 
		i80 = 39; i81 = 43; i82 = 47; i83 = 48;  i84 = 49; i85 = 46; i86 = 44; i87 = 45; i88 = 37; i89 = 29; 
		i90 = 30; i91 = 28; i92 = 26; i93 = 27;  i94 = 31; i95 = 35; i96 = 36; i97 = 34; i98 = 32; i99 = 33;
		sort();
	}
}
