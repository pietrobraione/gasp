package small.mergesort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import java.time.Duration;

import gasp.Main;
import gasp.Options;

public class LauncherMergesort {
	public static void main(String[] s) {
		final Options o = new Options();
		
		o.setClasspath(PATH_EXAMPLES);
		o.setJBSEPath(PATH_JBSE);
		o.setZ3Path(PATH_Z3);
		o.setNumberOfThreads(4);
		o.setGenerations(0);
		o.setTimeout(Duration.ofHours(1));
		o.setPopulationSize(50);
		o.setEliteSize(5);
		o.setMutationProbability(0.2);
		o.setLocalSearchPeriod(10);
		o.setLocalSearchAttempts(25);
		
		//o.setMethodSignature("small/mergesort/Mergesort:([I)V:sort");
		
		o.setMethodSignature("small/mergesort/Mergesort10:()V:sort");
		o.setMethodSignature("small/mergesort/Mergesort50:()V:sort");
		o.setMethodSignature("small/mergesort/Mergesort75:()V:sort");
		//o.setMethodSignature("small/mergesort/Mergesort100:()V:sort");
		
		final Main m = new Main(o);
		m.run();
	}
}
