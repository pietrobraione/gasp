package complexity.se;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import complexity.utils.Config;
import complexity.utils.userScan;
import jbse.algo.exc.CannotManageStateException;
import jbse.apps.run.DecisionProcedureGuidance;
import jbse.apps.run.DecisionProcedureGuidanceJDI;
import jbse.bc.exc.InvalidClassFileFactoryClassException;
import jbse.common.exc.ClasspathException;
import jbse.dec.DecisionProcedureAlgorithms;
import jbse.dec.DecisionProcedureAlwSat;
import jbse.dec.DecisionProcedureClassInit;
import jbse.dec.DecisionProcedureLICS;
import jbse.dec.DecisionProcedureSMTLIB2_AUFNIRA;
import jbse.dec.exc.DecisionBacktrackException;
import jbse.dec.exc.DecisionException;
import jbse.jvm.Runner;
import jbse.jvm.RunnerBuilder;
import jbse.jvm.RunnerParameters;
import jbse.jvm.Engine;
import jbse.jvm.EngineParameters.BreadthMode;
import jbse.jvm.EngineParameters.StateIdentificationMode;
import jbse.jvm.Runner.Actions;
import jbse.jvm.exc.CannotBacktrackException;
import jbse.jvm.exc.CannotBuildEngineException;
import jbse.jvm.exc.EngineStuckException;
import jbse.jvm.exc.FailureException;
import jbse.jvm.exc.InitializationException;
import jbse.jvm.exc.NonexistingObservedVariablesException;
import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.mem.ClauseAssumeClassInitialized;
import jbse.mem.ClauseAssumeClassNotInitialized;
import jbse.mem.State;
import jbse.mem.exc.CannotRefineException;
import jbse.mem.exc.ContradictionException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterOperationOnSimplex;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;
import jbse.tree.StateTree.BranchPoint;
import jbse.val.Primitive;

public class SymexJBSE implements Symex {
	private static final String COMMANDLINE_LAUNCH_Z3 = System.getProperty("os.name").toLowerCase().contains("windows") ? " /smt2 /in /t:10" : " -smt2 -in -t:10";

	private String[] classpath;
	private String z3Path;
	private final RunnerParameters commonParamsGuided;
	private static int target;
		
	public SymexJBSE() {
		this.classpath = new String[3];
		this.classpath[0] = Config.dataTarget;
		this.classpath[1] = Config.programPath;
		this.classpath[2] = Config.classTarget;
		this.z3Path = Config.z3Path; 
		this.commonParamsGuided = new RunnerParameters();
		this.commonParamsGuided.setMethodSignature(Config.className, Config.descriptor, Config.methodName/*"example/IfExample", "(I)V", "m"*/);
		this.commonParamsGuided.addClasspath(this.classpath);
		this.commonParamsGuided.setBreadthMode(BreadthMode.ALL_DECISIONS_NONTRIVIAL);
	}

	private static class ActionsRunner extends Actions {
		private final ArrayList<State> stateList = new ArrayList<State>();
		
		public ActionsRunner() {
		}
		
		public ArrayList<State> getStateList() {
			return this.stateList;
		}
		
		@Override
		public boolean atRoot() {
			System.out.println("atRoot");
			return super.atRoot();
		}

		@Override
		public boolean atTraceEnd() {
			System.out.println("atTraceEnd");
			stateList.add(this.getEngine().getCurrentState());
			return true;
		}

		@Override
		public boolean atBranch(BranchPoint bp) {
			System.out.println("atBranch");
			int numOfStates = getEngine().getNumOfStatesAtBranch(bp);
			if(numOfStates==0) {
				return super.atBranch(bp);
			}
			Random r=new Random();
			int high=numOfStates+1;
			System.out.println("Branch = "+high);
			target=r.nextInt(high) + 1;
	
			System.out.println("atBranch: randomly selected " + target);
			while (target > 1) {
				try {
					target--;
					getEngine().backtrack();
				} catch (DecisionBacktrackException | CannotBacktrackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return super.atBranch(bp);
			/*System.out.println("Branch remaining = "+numOfStates);
			return super.atBranch(bp);*/
		}

		@Override
		public void atEnd() {
			System.out.println("atEnd");
			super.atEnd();
		}
	}

	private State initialState = null;

	/**
	 * Performs symbolic execution of the target method guided by a test case 
	 * up to some depth, then peeks the states on the next branch.  
	 * 
	 * @param testCase a {@link TestCase}, it will guide symbolic execution.
	 * @param testDepth the maximum depth up to which {@code t} guides 
	 *        symbolic execution, or a negative value.
	 * @return a {@link List}{@code <}{@link State}{@code >} containing
	 *         all the states on branch at depth {@code stateDepth + 1}. 
	 *         In case {@code stateDepth < 0} executes the test up to the 
	 *         final state and returns a list containing only the final state.
	 * @throws DecisionException
	 * @throws CannotBuildEngineException
	 * @throws InitializationException
	 * @throws InvalidClassFileFactoryClassException
	 * @throws NonexistingObservedVariablesException
	 * @throws ClasspathException
	 * @throws CannotBacktrackException
	 * @throws CannotManageStateException
	 * @throws ThreadStackEmptyException
	 * @throws ContradictionException
	 * @throws EngineStuckException
	 * @throws FailureException
	 */
	public Runner newRunner(ActionsRunner actions)
		throws DecisionException, CannotBuildEngineException, InitializationException, 
		InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
		ClasspathException, CannotBacktrackException, CannotManageStateException, 
		ThreadStackEmptyException, ContradictionException, EngineStuckException, 
		FailureException {
		return newRunner(actions, null);
	}
	
	/**
	 * Performs symbolic execution of the target method guided by a test case 
	 * up to some depth, then peeks the states on the next branch.  
	 * 
	 * @param testCase a {@link TestCase}, it will guide symbolic execution.
	 * @param testDepth the maximum depth up to which {@code t} guides 
	 *        symbolic execution, or a negative value.
	 * @return a {@link List}{@code <}{@link State}{@code >} containing
	 *         all the states on branch at depth {@code stateDepth + 1}. 
	 *         In case {@code stateDepth < 0} executes the test up to the 
	 *         final state and returns a list containing only the final state.
	 * @throws DecisionException
	 * @throws CannotBuildEngineException
	 * @throws InitializationException
	 * @throws InvalidClassFileFactoryClassException
	 * @throws NonexistingObservedVariablesException
	 * @throws ClasspathException
	 * @throws CannotBacktrackException
	 * @throws CannotManageStateException
	 * @throws ThreadStackEmptyException
	 * @throws ContradictionException
	 * @throws EngineStuckException
	 * @throws FailureException
	 */
	public Runner newRunner(ActionsRunner actions, State sInitial)
			throws DecisionException, CannotBuildEngineException, InitializationException, 
			InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
			ClasspathException, CannotBacktrackException, CannotManageStateException, 
			ThreadStackEmptyException, ContradictionException, EngineStuckException, 
			FailureException {

		//builds the parameters
		final RunnerParameters pGuided = this.commonParamsGuided.clone();
		
		//sets the calculator
		final CalculatorRewriting calc = new CalculatorRewriting();
		calc.addRewriter(new RewriterOperationOnSimplex());
		pGuided.setCalculator(calc);
		
		//sets the decision procedures
		pGuided.setDecisionProcedure(new DecisionProcedureAlgorithms(
				new DecisionProcedureClassInit( //useless?
						new DecisionProcedureLICS( //useless?
								new DecisionProcedureSMTLIB2_AUFNIRA(
										new DecisionProcedureAlwSat(), 
										calc, this.z3Path + COMMANDLINE_LAUNCH_Z3), 
								calc, new LICSRulesRepo()), 
						calc, new ClassInitRulesRepo()), calc));

		//sets the actions
		pGuided.setActions(actions);

		//sets the initial state
		if (sInitial != null) {
			pGuided.setInitialState(sInitial);
		}

		//builds the runner and runs it
		final RunnerBuilder rb = new RunnerBuilder();
		final Runner r = rb.build(pGuided);
		
		//the first time is invoked it initializes this.initialState
		if (this.initialState == null) {
			this.initialState = rb.getEngine().getInitialState();
		}
		return r;
	}
	
	public static void main(String[] args) {
		SymexJBSE exec = new SymexJBSE();
		//TODO
	}
	
	int instructionCount=0;
	@Override
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition) {
		State s = null;
		
		try {
			ActionsRunner actions = new ActionsRunner();
			if (this.initialState == null) {
				newRunner(actions); //ugly!
			}
			final State sInitial = this.initialState.clone();
			for (Constraint c : precondition) {
				final Primitive condition = ((ClauseAssume) ((ConstraintJBSE) c).getClause()).getCondition();
				sInitial.assume(condition);
			}
			final Runner r = newRunner(actions, sInitial);
			r.run();
			List<State> states = actions.getStateList();
			if (states.size() != 1) {
				throw new RuntimeException();
			}
			s = states.get(0);
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		List<Constraint> ret = new ArrayList<>();
		Collection<Clause> pathCondition1 = s.getPathCondition();
		Collection<Clause> pathCondition=new ArrayList<>();
		for (Clause c : pathCondition1) {
			if (c instanceof ClauseAssume) {
				pathCondition.add(c);
			}
		}
		System.out.println("Computed the path condition: " + pathCondition);
		for (Clause c : pathCondition) {
			ret.add(new ConstraintJBSE(c));
		}
		
		this.instructionCount = s.getDepth() + 1;
		return ret;
	}

	@Override
	public List<Constraint> randomWalkSymbolicExecution() {
		return randomWalkSymbolicExecution(new ArrayList<>());
	}

	@Override
	public List<Constraint> formulaSlicing(List<Constraint> formula, Constraint target) {
		return formula;
	}

	@Override
	public int getInstructionCount() {
		return instructionCount;
	}

	@Override
	public Constraint mkAnd(List<Constraint> refs) {
		refs.get(0);
		return null;
	}

	@Override
	public boolean quickCheck(Constraint c) {
		return true;
	}

	@Override
	public Constraint boolRef(Constraint c1, Constraint c2) {
		return c1;
	}
}
