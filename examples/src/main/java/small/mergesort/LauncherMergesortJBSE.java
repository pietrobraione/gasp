package small.mergesort;

import static settings.Settings.PATH_EXAMPLES;
import static settings.Settings.PATH_JBSE;
import static settings.Settings.PATH_Z3;

import jbse.apps.run.Run;
import jbse.apps.run.RunParameters;
import jbse.apps.run.RunParameters.DecisionProcedureType;
import jbse.apps.run.RunParameters.GuidanceType;
import jbse.apps.run.RunParameters.StateFormatMode;
import jbse.apps.run.RunParameters.StepShowMode;

public class LauncherMergesortJBSE {
	public static void main(String[] s) {
		final RunParameters o = new RunParameters();
		
		o.addUserClasspath(PATH_JBSE, PATH_EXAMPLES);
		o.setDecisionProcedureType(DecisionProcedureType.Z3);
		o.setExternalDecisionProcedurePath(PATH_Z3);		
		//o.setMethodSignature("small/mergesort/Mergesort", "([I)V", "sort");
		//o.setMethodSignature("small/mergesort/Mergesort10", "()V", "sort");
		//o.setMethodSignature("small/mergesort/Mergesort50", "()V", "sort");
		//o.setMethodSignature("small/mergesort/Mergesort75", "()V", "sort");
		o.setMethodSignature("small/mergesort/Mergesort100", "()V", "sort");
		o.getRunnerParameters().getEngineParameters().setMakePreInitClassesSymbolic(false);
		o.setStateFormatMode(StateFormatMode.TRACE);
		o.setStepShowMode(StepShowMode.ALL);
		
		//o.setGuided("small/mergesort/Mergesort", "sortWorstCase");
		//o.setGuided("small/mergesort/Mergesort10", "sortWorstCase");
		//o.setGuided("small/mergesort/Mergesort50", "sortWorstCase");
		//o.setGuided("small/mergesort/Mergesort75", "sortWorstCase");
		o.setGuided("small/mergesort/Mergesort100", "sortWorstCase");
		o.setGuidanceType(GuidanceType.JDI);
		
		new Run(o).run();
		//resulting maximal fitness: 
		//for size 10: 1334 for harder, 1377 for easier
		//for size 50: 9652 for harder, 9895 for easier
		//for size 75: 15542 for harder, 15910 for easier
		//for size 100: 21789 for harder, 22282 for easier
	}
}
