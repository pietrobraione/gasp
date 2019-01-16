package gasp.se;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import gasp.ga.Constraint;
import jbse.algo.exc.CannotManageStateException;
import jbse.bc.exc.InvalidClassFileFactoryClassException;
import jbse.common.exc.ClasspathException;
import jbse.common.exc.InvalidInputException;
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
import jbse.jvm.EngineParameters.BreadthMode;
import jbse.jvm.Runner.Actions;
import jbse.jvm.exc.CannotBacktrackException;
import jbse.jvm.exc.CannotBuildEngineException;
import jbse.jvm.exc.EngineStuckException;
import jbse.jvm.exc.FailureException;
import jbse.jvm.exc.InitializationException;
import jbse.jvm.exc.NonexistingObservedVariablesException;
import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.mem.State;
import jbse.mem.exc.ContradictionException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterOperationOnSimplex;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;
import jbse.tree.StateTree.BranchPoint;
import jbse.val.Primitive;

public class SymexJBSE implements Symex {
	private static final String SWITCH_CHAR = System.getProperty("os.name").toLowerCase().contains("windows") ? "/" : "-";

	private final String[] classpath;
	private final String z3Path;
	private final RunnerParameters commonParams;
	private static int target;
		
	public SymexJBSE(List<Path> classpath, Path jbsePath, Path z3Path, String methodClassName, String methodDescriptor, String methodName) {
		if (classpath == null) {
			throw new IllegalArgumentException("Classpath cannot be null");
		}
		if (jbsePath == null) {
			throw new IllegalArgumentException("JBSE path cannot be null");
		}
		if (z3Path == null) {
			throw new IllegalArgumentException("Z3 path cannot be null");
		}
		if (methodClassName == null) {
			throw new IllegalArgumentException("Method class name cannot be null");
		}
		if (methodDescriptor == null) {
			throw new IllegalArgumentException("Method descriptor cannot be null");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null");
		}
		
		this.classpath = new String[classpath.size() + 1];
		for (int i = 0; i < classpath.size(); ++i) {
			this.classpath[i] = classpath.get(i).toString();
		}
		this.classpath[classpath.size()] = jbsePath.toString();
		this.z3Path = z3Path.toString(); 
		this.commonParams = new RunnerParameters();
		this.commonParams.setMethodSignature(methodClassName, methodDescriptor, methodName);
		this.commonParams.addUserClasspath(this.classpath);
		this.commonParams.setBreadthMode(BreadthMode.ALL_DECISIONS_NONTRIVIAL);
	}

	private static class ActionsRunner extends Actions {
		private final ArrayList<State> stateList = new ArrayList<State>();
		
		public ArrayList<State> getStateList() {
			return this.stateList;
		}
		
		@Override
		public boolean atTraceEnd() {
			this.stateList.add(this.getEngine().getCurrentState());
			return true;
		}

		@Override
		public boolean atBranch(BranchPoint bp) {
			final int numOfStates = getEngine().getNumOfStatesAtBranch(bp);
			if (numOfStates == 0) {
				return super.atBranch(bp);
			}
			final Random r = new Random();
			final int high = numOfStates + 1;
			target=r.nextInt(high) + 1;
	
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
		final RunnerParameters params = this.commonParams.clone();
		
		//sets the calculator
		final CalculatorRewriting calc = new CalculatorRewriting();
		calc.addRewriter(new RewriterOperationOnSimplex());
		params.setCalculator(calc);
		
		//sets the decision procedures
		final ArrayList<String> z3CommandLine = new ArrayList<>();
		z3CommandLine.add(this.z3Path);
		z3CommandLine.add(SWITCH_CHAR + "smt2");
		z3CommandLine.add(SWITCH_CHAR + "in");
		z3CommandLine.add(SWITCH_CHAR + "t:10");
		params.setDecisionProcedure(new DecisionProcedureAlgorithms(
				new DecisionProcedureClassInit( //useless?
						new DecisionProcedureLICS( //useless?
								new DecisionProcedureSMTLIB2_AUFNIRA(
										new DecisionProcedureAlwSat(), 
										calc, z3CommandLine), 
								calc, new LICSRulesRepo()), 
						calc, new ClassInitRulesRepo()), calc));

		//sets the actions
		params.setActions(actions);

		//sets the initial state
		if (sInitial != null) {
			params.setInitialState(sInitial);
		}

		//builds the runner and runs it
		final RunnerBuilder rb = new RunnerBuilder();
		final Runner r = rb.build(params);
		
		//ugly!
		//the first time is invoked it initializes this.initialState
		if (this.initialState == null) {
			r.run();
			this.initialState = rb.getEngine().getInitialState();
		}
		return r;
	}
	
	private int instructionCount = 0;
	
	@Override
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition) {
		State s = null;
		
		try {
			if (this.initialState == null) {
				 //ugly!
				final ActionsRunner actions = new ActionsRunner() {
					@Override
					public boolean atInitial() {
						return true;
					}
				};
				newRunner(actions);
			}
			final State sInitial = this.initialState.clone();
			for (Constraint c : precondition) {
				final ConstraintJBSE cJBSE = (ConstraintJBSE) c;
				final Clause clause = cJBSE.getClause();
				if (clause instanceof ClauseAssume) {
					final Primitive condition = ((ClauseAssume) clause).getCondition();
					sInitial.assume(condition);
				} //TODO else???
			}
			final ActionsRunner actions = new ActionsRunner();
			final Runner r = newRunner(actions, sInitial);
			r.run();
			final List<State> states = actions.getStateList();
			if (states.size() != 1) {
				throw new RuntimeException();
			}
			s = states.get(0);
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException | InvalidInputException e) {
			//TODO improve!
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.instructionCount = s.getDepth() + 1;
		final List<Constraint> retVal = s.getPathCondition().stream().map(clause -> new ConstraintJBSE(clause)).collect(Collectors.toList());		
		return retVal;
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
		return this.instructionCount;
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
