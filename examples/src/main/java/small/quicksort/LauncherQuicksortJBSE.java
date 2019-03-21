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
		o.setMethodSignature("small/quicksort/Quicksort2", "()V", "sort");
		o.getRunnerParameters().getEngineParameters().setMakePreInitClassesSymbolic(false);
		o.setStateFormatMode(StateFormatMode.TRACE);
		o.setStepShowMode(StepShowMode.ALL);
		
		//o.setGuided("small/quicksort/Quicksort", "sortWorstCase");
		o.setGuided("small/quicksort/Quicksort2", "sortWorstCase");
		o.setGuidanceType(GuidanceType.JDI);
		
		new Run(o).run();
		//resulting maximal fitness: ?? for Quicksort, 22953 for Quicksort2
	}
}
