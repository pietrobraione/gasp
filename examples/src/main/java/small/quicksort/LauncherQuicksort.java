package small.quicksort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import gasp.Main;
import gasp.Options;

public class LauncherQuicksort {
	public static void main(String[] s) {
		final Options o = new Options();
		
		o.setClasspath(PATH_EXAMPLES);
		o.setJBSEPath(PATH_JBSE);
		o.setZ3Path(PATH_Z3);
		o.setGenerations(50);
		o.setMethodSignature("small/quicksort/Quicksort:([I)V:sort");
		
		final Main m = new Main(o);
		m.run();
	}
}