package small.palindrome;

public class Palindrome {
	public boolean isPalindrome(int[] l) {
		for (int i = 0; i < l.length; ++i) {
			if (l[i] != l[l.length - 1 - i]) {
				return false;
			}
		}
		return true;
	}
}
