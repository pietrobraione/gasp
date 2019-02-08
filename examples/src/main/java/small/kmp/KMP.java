package small.kmp;

import java.util.ArrayList;

import jbse.meta.Analysis;

/**
 * Determine the starting indexes of the occurrences of {@code pattern} in {@code text} 
 * using the KMP algorithm.<br /><br />
 *
 * Expected worst case: the text does not contain the pattern and
 * <ol>
 * <li>the text has the form: A...AX,</li>
 * <li>the pattern has the form A...AY.</li>
 * </ol>
 *
 */
public class KMP {
	public ArrayList<Integer> kmp(char[] text, char[] pattern) {
		Analysis.assume(text.length <= 20);
		Analysis.assume(pattern.length <= 20);
		final ArrayList<Integer> table = makeTable(pattern);
		final ArrayList<Integer> retVal = new ArrayList<>();
		int j = 0;
		for (int i = 0; i < text.length; ++i) {
			while (j > 0 && text[i] != pattern[j]) {
				j = table.get(j - 1);
			}
			if (text[i] == pattern[j]) {
				j += 1;
			}
			if (j == pattern.length) {
				retVal.add(i - j + 1);
				j = 0;
			}
		}
		return retVal;
	}
	
	private ArrayList<Integer> makeTable(char[] pattern) {
		final ArrayList<Integer> retVal = new ArrayList<>();
		retVal.add(0);
		for (int i = 1; i < pattern.length; ++i) {
			int j = retVal.get(i - 1);
			while (j > 0 && pattern[j] != pattern[i]) {
				j = retVal.get(j - 1);
			}
			retVal.add(pattern[j] == pattern[i] ? j + 1 : j);
		}
		return retVal;
	}
}
