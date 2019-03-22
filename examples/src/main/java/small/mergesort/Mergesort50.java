package small.mergesort;

/**
 * Sorts an array with the mergesort algorithm.
 * 
 * Expected worst case: generated by the method {@code worstCaseArrayOfSize(int)} 
 * (maximizes the number of comparisons).
 */
public class Mergesort50 {
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
	
	public void sortWorstCase() {
		//TODO
		sort();
	}
}
