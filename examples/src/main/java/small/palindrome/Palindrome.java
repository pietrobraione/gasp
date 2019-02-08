package small.palindrome;

import jbse.meta.Analysis;

public class Palindrome {
	/**
	 * Decides if the given list is a palindrome. 
	 * Worst case: a palindrome list.
	 */
	public boolean isPalindrome(int[] l) {
		Analysis.assume(l.length == 20);
		for (int i = 0; i < l.length; ++i) {
			if (l[i] != l[l.length - 1 - i]) {
				return false;
			}
		}
		return true;
	}
}
