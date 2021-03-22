package small.quicksort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import jbse.apps.run.Run;
import jbse.apps.run.RunParameters;
import jbse.apps.run.RunParameters.DecisionProcedureType;
import jbse.apps.run.RunParameters.GuidanceType;
import jbse.apps.run.RunParameters.StateFormatMode;
import jbse.apps.run.RunParameters.StepShowMode;

public class LauncherQuicksortJBSE {
	public static void main(String[] s) {
		final RunParameters o = new RunParameters();
		
		o.addUserClasspath(PATH_JBSE, PATH_EXAMPLES);
		o.setDecisionProcedureType(DecisionProcedureType.Z3);
		o.setExternalDecisionProcedurePath(PATH_Z3);		
		//o.setMethodSignature("small/quicksort/Quicksort", "([I)V", "sort");
		//o.setMethodSignature("small/quicksort/Quicksort10", "()V", "sort");
		//o.setMethodSignature("small/quicksort/Quicksort50", "()V", "sort");
		//o.setMethodSignature("small/quicksort/Quicksort75", "()V", "sort");
		o.setMethodSignature("small/quicksort/Quicksort100", "()V", "sort");
		o.getRunnerParameters().getEngineParameters().setMakePreInitClassesSymbolic(false);
		o.setStateFormatMode(StateFormatMode.PATH);
		o.setStepShowMode(StepShowMode.ALL);
		
		//o.setGuided("small/quicksort/Quicksort", "sortWorstCase");
		//o.setGuided("small/quicksort/Quicksort10", "sortWorstCase");
		//o.setGuided("small/quicksort/Quicksort50", "sortWorstCase");
		//o.setGuided("small/quicksort/Quicksort75", "sortWorstCase");
		o.setGuided("small/quicksort/Quicksort100", "sortWorstCase");
		o.setGuidanceType(GuidanceType.JDI);
		
		new Run(o).run();
		//resulting maximal fitness: 
		//for size 10: 1479 for harder, 1522 for easier
		//for size 50: OOM for harder, 10176 for easier
		//for size 75: OOM for harder, 15987 for easier
		//for size 100: OOM for harder, 22953 for easier
	}
}
