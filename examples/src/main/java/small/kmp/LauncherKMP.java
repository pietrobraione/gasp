package small.kmp;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import gasp.Main;
import gasp.Options;

public class LauncherKMP {
	public static void main(String[] s) {
		final Options o = new Options();
		
		o.setClasspath(PATH_EXAMPLES);
		o.setJBSEPath(PATH_JBSE);
		o.setZ3Path(PATH_Z3);
		o.setGenerations(50);
		o.setMethodSignature("small/kmp/KMP:([C[C)Ljava/util/ArrayList;:kmp");
		
		o.setSeed(1549289506477L);
		o.setNumberOfThreads(1);
		
		final Main m = new Main(o);
		m.run();
	}
}