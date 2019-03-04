package small.mergesort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import java.time.Duration;

import org.apache.logging.log4j.Level;

import gasp.Main;
import gasp.Options;

public class LauncherMergesort {
	public static void main(String[] s) {
		final Options o = new Options();
		
		o.setClasspath(PATH_EXAMPLES);
		o.setJBSEPath(PATH_JBSE);
		o.setZ3Path(PATH_Z3);
		o.setNumberOfThreads(10);
		o.setGenerations(0);
		o.setTimeout(Duration.ofHours(1));
		o.setPopulationSize(50);
		o.setEliteSize(5);
		o.setMutationProbability(0.2);
		o.setLocalSearchPeriod(10);
		o.setLocalSearchAttempts(25);
		o.setMethodSignature("small/mergesort/Mergesort:([I)V:sort");
		
		o.setVerbosity(Level.DEBUG);
		o.setSeed(1551314378141L);
		
		final Main m = new Main(o);
		m.run();
	}
}
