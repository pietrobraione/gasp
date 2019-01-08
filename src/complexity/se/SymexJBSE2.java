package complexity.se;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import complexity.utils.Config;
import complexity.utils.userScan;

//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ast.expr.MethodCallExpr;
//import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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
import jbse.dec.exc.DecisionException;
import jbse.jvm.Runner;
import jbse.jvm.RunnerBuilder;
import jbse.jvm.RunnerParameters;
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
import jbse.mem.State;
import jbse.mem.exc.ContradictionException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterOperationOnSimplex;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;
import jbse.tree.StateTree.BranchPoint;

public class SymexJBSE2 implements Symex {
	private static final String COMMANDLINE_LAUNCH_Z3 = System.getProperty("os.name").toLowerCase().contains("windows") ? " /smt2 /in /t:10" : " -smt2 -in -t:10";

	private String[] classpath;
	private String z3Path;
	//private final TestCase testCase;
	private final RunnerParameters commonParamsGuided;
		
	public SymexJBSE2(/*Options o, EvosuiteResult item*/) {
		this.classpath = new String[3];
		this.classpath[0] = Config.dataTarget;
		this.classpath[1] = Config.programPath;
		this.classpath[2] = Config.classTarget;
		this.z3Path = Config.z3Path; 
		this.commonParamsGuided = new RunnerParameters();
		this.commonParamsGuided.setMethodSignature(Config.className, Config.descriptor, Config.methodName/*"example/IfExample", "(I)V", "m"*/);
		this.commonParamsGuided.addClasspath(this.classpath);
		this.commonParamsGuided.setBreadthMode(BreadthMode.ALL_DECISIONS_NONTRIVIAL);
/*this.outPath = o.getOutDirectory().toString();
		//this.testCase = item.getTestCase();
		//builds the template parameters object for the guided (symbolic) execution
		if (o.getHeapScope() != null) {
			for (Map.Entry<String, Integer> e : o.getHeapScope().entrySet()) {
				this.commonParamsGuided.setHeapScope(e.getKey(), e.getValue());
			}
		}*/
		/*if (o.getCountScope() > 0) {
			this.commonParamsGuided.setCountScope(o.getCountScope());
		}*/
		
		//builds the template parameters object for the guiding (concrete) execution
		/*this.commonParamsGuiding = new RunnerParameters();
		this.commonParamsGuiding.addClasspath(this.classpath);
		this.commonParamsGuiding.setStateIdentificationMode(StateIdentificationMode.COMPACT);
		this.commonParamsGuiding.setBreadthMode(BreadthMode.ALL_DECISIONS);
*/
	}

	private static class ActionsRunner extends Actions {
		private final ArrayList<State> stateList = new ArrayList<State>();
		
		public ActionsRunner() {
			//this.guid = guid;
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
			//return super.atTraceEnd();
		}

		@Override
		public boolean atBranch(BranchPoint bp) {
			System.out.println("atBranch");
			int numOfStates = getEngine().getNumOfStatesAtBranch(bp);
			System.out.println("Branch remaining = "+numOfStates);
			return super.atBranch(bp);
		}

		@Override
		public boolean atBacktrackPost(BranchPoint bp) {
			//this.getEngine().stopCurrentTrace();
			System.out.println("atBacktrackPost");
			return super.atBacktrackPost(bp);
		}

		@Override
		public void atEnd() {
			//final State finalState = this.getEngine().getCurrentState();
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
	public List<State> runProgram()
			throws DecisionException, CannotBuildEngineException, InitializationException, 
			InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
			ClasspathException, CannotBacktrackException, CannotManageStateException, 
			ThreadStackEmptyException, ContradictionException, EngineStuckException, 
			FailureException {

		//builds the parameters
		final RunnerParameters pGuided = this.commonParamsGuided.clone();
		//final RunnerParameters pGuiding = this.commonParamsGuiding.clone();

		//sets the calculator
		final CalculatorRewriting calc = new CalculatorRewriting();
		calc.addRewriter(new RewriterOperationOnSimplex());
		pGuided.setCalculator(calc);
		//pGuiding.setCalculator(calc);
		
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
		final ActionsRunner actions = new ActionsRunner();
		pGuided.setActions(actions);

		//builds the runner and runs it
		final RunnerBuilder rb = new RunnerBuilder();
		final Runner r = rb.build(pGuided);
		r.run();

		//outputs
		this.initialState = rb.getEngine().getInitialState();
		return actions.getStateList();
	}
	
	/*private static class CountVisitor extends VoidVisitorAdapter<Object> {
		final String methodName;
		int methodCallCounter = 0;

		public CountVisitor(String methodName) {
			this.methodName = methodName;
		}

		@Override
		public void visit(MethodCallExpr n, Object arg) {
			super.visit(n, arg);
			if (n.getNameAsString().equals(this.methodName)) {
				this.methodCallCounter++;
			}
		}
	}*/

	/*private int countNumberOfInvocations(String className, String methodName){
		//TODO use the whole signature of the target method to avoid ambiguities (that's quite hard)
		final CountVisitor v = new CountVisitor(methodName);
		try {
			final FileInputStream in = new FileInputStream(this.outPath + "/" + className + ".java");
			v.visit(JavaParser.parse(in), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return v.methodCallCounter;
	}*/

	
	/**
	 * Must be invoked after an invocation of {@link #runProgram(TestCase, int)}.
	 * Returns the initial state of symbolic execution.
	 * 
	 * @return a {@link State} or {@code null} if this method is invoked
	 *         before an invocation of {@link #runProgram(TestCase, int)}.
	 */
	public State getInitialState() {
		return this.initialState;
	}
	
	public static void main(String[] args) {
		SymexJBSE2 runner = new SymexJBSE2();
		try {
			runner.runProgram();
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			e.printStackTrace();
		}
	}
	int instructionCount=0;
	@Override
	public List<Constraint> randomWalkSymbolicExecution(List<Constraint> precondition) {
		State s = null;
		
		/*List<Clause> pre = new ArrayList<>();
		for (Constraint c : precondition) {
			pre.add(new Clause(c));
		}*/
		
		try {
			/* TODO:
			 * s = Invoca JBSE con precondizione pre per calcolare esecuzione simbolica di path compatibile con pre 
			 */
			List<State> states = runProgram();
			if (states.size() != 1) {
				throw new RuntimeException();
			}
			s = states.get(0);
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		List<Constraint> ret = new ArrayList<>();
		Collection<Clause> pathCondition = s.getPathCondition();
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
