package small.quicksort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import java.time.Duration;

import gasp.Main;
import gasp.Options;
import gasp.Options.LocalSearchAlgorithmType;

public class LauncherQuicksortTable4 {
	public static void main(String[] s) {
		final Options o = new Options();
		
		o.setClasspath(PATH_EXAMPLES);
		o.setJBSEPath(PATH_JBSE);
		o.setZ3Path(PATH_Z3);
		o.setNumberOfThreads(30);
		o.setGenerations(0);
		o.setTimeout(Duration.ofHours(1));
		o.setLocalSearchAttempts(25);
		o.setMethodSignature("small/quicksort/Quicksort100:()V:sort");
		
		
		final int[] populationSize = { 10, 10, 10, 10, 50, 50, 50, 50, 75, 75, 75, 75, 100, 100, 100, 100 };
		final int[] localSearchPeriod = { 0, 10, 50, 100, 0, 10, 50, 100, 0, 10, 50, 100, 0, 10, 50, 100 };
		final double[] mutationProbability = { 0.0, 0.2, 0.5, 1.0, 0.2, 0.5, 1.0, 0.0, 0.5, 1.0, 0.0, 0.2, 1.0, 0.0, 0.2, 0.5 };
		final int[] eliteSize = { 0, 1, 5, 10, 5, 10, 0, 1, 0, 1, 5, 10, 5, 10, 0, 1 };
		
		for (int i = 0; i < populationSize.length; ++i) {
			o.setPopulationSize(populationSize[i]);
			if (localSearchPeriod[i] == 0) {
				o.setLocalSearchAlgorithmType(LocalSearchAlgorithmType.NONE);
			} else {
				o.setLocalSearchAlgorithmType(LocalSearchAlgorithmType.HILL_CLIMBING);
				o.setLocalSearchPeriod(localSearchPeriod[i]);
			}
			o.setMutationProbability(mutationProbability[i]);
			o.setEliteSize(eliteSize[i]);
			System.out.println("=========================");
			System.out.println("popsize=" + populationSize[i] + ", lsperiod=" + localSearchPeriod[i] + ", muteprob=" + mutationProbability[i] + ", elitsize=" + eliteSize[i]);			
			System.out.println("=========================");
			final Main m = new Main(o);
			m.run();
		}
	}
}
