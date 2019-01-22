package small.alternate;

public class Alternate0 {
	public void alternate_0(int[] l) {
		int check;
		for (int i = 0; i < l.length; ++i) {
			if (l[i] == 0) {
				check = 1 - (i % 2);
			} else {
				check = i % 2;
			}
			
			if (check != 0) {
				executeExpensiveTask();
			}
		}
	}
	
	private int executeExpensiveTask() {
		int i;
		for (i = 1000; i > 0; --i)
			;
		return i;
	}
}