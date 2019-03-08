package small.palindrome;

import jbse.meta.Analysis;

/**
 * Decides if an array is a palindrome.
 * 
 * Expected worst case: the array is a palindrome.
 */
public class Palindrome {
	public boolean isPalindrome(int[] l) {
		Analysis.assume(l.length == 100);
		for (int i = 0; i < l.length; ++i) {
			if (l[i] != l[l.length - 1 - i]) {
				return false;
			}
		}
		return true;
	}
}
