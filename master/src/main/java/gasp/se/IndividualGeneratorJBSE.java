package gasp.se;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import gasp.ga.Individual;
import gasp.ga.IndividualGenerator;
import jbse.algo.exc.CannotManageStateException;
import jbse.bc.exc.InvalidClassFileFactoryClassException;
import jbse.common.exc.ClasspathException;
import jbse.common.exc.InvalidInputException;
import jbse.common.exc.UnexpectedInternalException;
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
import jbse.jvm.Runner.Actions;
import jbse.jvm.exc.CannotBacktrackException;
import jbse.jvm.exc.CannotBuildEngineException;
import jbse.jvm.exc.EngineStuckException;
import jbse.jvm.exc.FailureException;
import jbse.jvm.exc.InitializationException;
import jbse.jvm.exc.NonexistingObservedVariablesException;
import jbse.mem.Clause;
import jbse.mem.ClauseAssume;
import jbse.mem.ClauseAssumeAliases;
import jbse.mem.ClauseAssumeExpands;
import jbse.mem.ClauseAssumeNull;
import jbse.mem.ClauseAssumeReferenceSymbolic;
import jbse.mem.State;
import jbse.mem.exc.ContradictionException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterOperationOnSimplex;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;
import jbse.tree.StateTree.BranchPoint;
import jbse.val.Primitive;
import jbse.val.exc.InvalidTypeException;

public class IndividualGeneratorJBSE implements IndividualGenerator<GeneJBSE> {
	private static final String SWITCH_CHAR = System.getProperty("os.name").toLowerCase().contains("windows") ? "/" : "-";

	private final Random random;
	private final String[] classpath;
	private final String z3Path;
	private final RunnerParameters commonParams;
	private final State initialState;
	
	public IndividualGeneratorJBSE(Random random, List<Path> classpath, Path jbsePath, Path z3Path, String methodClassName, String methodDescriptor, String methodName) {
		if (random == null) {
			throw new IllegalArgumentException("The random generator cannot be null.");
		}
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
		
		this.random = random;
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
		final Actions actions = new Actions() {
			@Override
			public boolean atInitial() {
				return true;
			}
		};
		try {
			final Runner r = newRunner(actions);
			r.run();
			this.initialState = r.getEngine().getInitialState();
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			// TODO throw better exception
			throw new RuntimeException(e);
		}
	}

	private class ActionsRunner extends Actions {
		private final ArrayList<HashSet<String>> partition = new ArrayList<>();
		private final ArrayList<String> partitionPointsToClass = new ArrayList<>();
		private final HashSet<String> nulls = new HashSet<>();
		
		private State endState = null;
		
		public ActionsRunner(ArrayList<ClauseAssumeReferenceSymbolic> referencesPrecondition, 
							 ArrayList<Boolean> negated) throws ContradictionException {
			//first, positive clauses
			for (int i = 0; i < referencesPrecondition.size(); ++i) {
				final ClauseAssumeReferenceSymbolic c = referencesPrecondition.get(i);
				final boolean isNegated = negated.get(i);
				if (!isNegated) {
					if (c instanceof ClauseAssumeAliases) {
						final ClauseAssumeAliases ca = (ClauseAssumeAliases) c;
						final String originString1 = ca.getReference().asOriginString();
						final String originString2 = ca.getObjekt().getOrigin().asOriginString();
						addToSamePartitionSet(originString1, originString2);
					} else if (c instanceof ClauseAssumeExpands) {
						final ClauseAssumeExpands ce = (ClauseAssumeExpands) c;
						final String originString = ce.getReference().asOriginString();
						final String className = ce.getObjekt().getType().getClassName();
						addToSamePartitionSet(originString);
						assertPartitionClass(originString, className);
					} else { //c instanceof ClauseAssumeNull
						addToNulls(c.getReference().asOriginString());
					}
				}
			}
			//then, negative clauses
			for (int i = 0; i < referencesPrecondition.size(); ++i) {
				final ClauseAssumeReferenceSymbolic c = referencesPrecondition.get(i);
				final boolean isNegated = negated.get(i);
				if (isNegated) {
					if (c instanceof ClauseAssumeAliases) {
						final ClauseAssumeAliases ca = (ClauseAssumeAliases) c;
						final String originString1 = ca.getReference().asOriginString();
						final String originString2 = ca.getObjekt().getOrigin().asOriginString();
						checkNotSamePartitionSet(originString1, originString2);
					} else if (c instanceof ClauseAssumeExpands) {
						final ClauseAssumeExpands ce = (ClauseAssumeExpands) c;
						final String originString = ce.getReference().asOriginString();
						final String className = ce.getObjekt().getType().getClassName();
						checkNotPartitionClass(originString, className);
					} else { //c instanceof ClauseAssumeNull
						checkNotNull(c.getReference().asOriginString());
					}
				}
			}
		}
		
		private void checkNotSamePartitionSet(String... members) throws ContradictionException {
			final HashSet<String> membersSet = new HashSet<>(Arrays.asList(members));
			for (HashSet<String> set : this.partition) {
				if (set.containsAll(membersSet)) {
					//contradiction
					throw new ContradictionException();
				}
			}
		}
		
		private void addToSamePartitionSet(String... members) throws ContradictionException {
			final HashSet<String> membersSet = new HashSet<>(Arrays.asList(members));
			if (!Collections.disjoint(this.nulls, membersSet)) {
				//contradiction
				throw new ContradictionException();
			}
			HashSet<String> foundSet = null;
			for (HashSet<String> set : this.partition) {
				if (!Collections.disjoint(set, membersSet)) {
					if (foundSet != null) {
						//contradiction
						throw new ContradictionException();
					}
					foundSet = set;
				}
			}
			//no contradiction
			if (foundSet == null) {
				foundSet = new HashSet<>();
				this.partition.add(foundSet);
				this.partitionPointsToClass.add(null);
			}
			foundSet.addAll(membersSet);
		}
		
		private void assertPartitionClass(String member, String className) throws ContradictionException {
			for (int i = 0; i < this.partition.size(); ++i) {
				if (this.partition.get(i).contains(member)) {
					if (this.partitionPointsToClass.get(i) == null) {
						this.partitionPointsToClass.set(i, className);
						return;
					} else if (this.partitionPointsToClass.get(i).equals(className)) {
						//no contradiction, do nothing
						return;
					} else {
						//contradiction
						throw new ContradictionException();
					}
				}
			}
		}
		
		private void checkNotPartitionClass(String member, String className) throws ContradictionException {
			for (int i = 0; i < this.partition.size(); ++i) {
				if (this.partition.get(i).contains(member)) {
					if (className.equals(this.partitionPointsToClass.get(i))) {
						//contradiction
						throw new ContradictionException();
					} else {
						//no contradiction, do nothing
						return;
					}
				}
			}
		}
		
		private void checkNotNull(String member) throws ContradictionException {
			if (this.nulls.contains(member)) {
				//contradiction
				throw new ContradictionException();
			}
		}
		
		private void addToNulls(String member) throws ContradictionException {
			for (HashSet<String> set : this.partition) {
				if (set.contains(member)) {
					//contradiction
					throw new ContradictionException();
				}
			}
			this.nulls.add(member);
		}
		
		public State getEndState() {
			return this.endState;
		}
		
		@Override
		public boolean atTraceEnd() {
			this.endState = this.getEngine().getCurrentState();
			return true;
		}

		@Override
		public boolean atBranch(BranchPoint bp) {
			final Engine engine = getEngine();
			final int numOfStates = engine.getNumOfStatesAtBranch(bp) + 1; //(one is the current one)
			
			//produces a random permutation of the indices of the
			//states at the branch
			final ArrayList<Integer> indices = new ArrayList<>();
			for (int i = 0; i < numOfStates; ++i) {
				indices.add(i);
			}
			Collections.shuffle(indices, IndividualGeneratorJBSE.this.random);
			
			//looks for a state that complies with the ClauseAssumeReferenceSymbolic
			final int none = -1;
			int compliantStateIndex = none;
			for (int index : indices) {
				try {
					final State s = (index == 0 ? engine.getCurrentState() : engine.getStateAtBranch(bp, index - 1));
					if (satisfiesReferencePrecondition(s)) {
						compliantStateIndex = index;
						break;
					}
				} catch (InvalidInputException e) {
					//this should never happen
					throw new UnexpectedInternalException(e);
				}
			}
			
			//if none found, just terminate
			if (compliantStateIndex == none) {
				return true;
			}
			
			//otherwise, backtrack to it
			for (int i = 0; i < compliantStateIndex; ++i) {
				try {
					engine.backtrack();
				} catch (DecisionBacktrackException | CannotBacktrackException e) {
					//TODO improve
					throw new RuntimeException(e);
				}
			}
			return super.atBranch(bp);
		}

		private boolean satisfiesReferencePrecondition(State s) {
			for (Clause c : s.getPathCondition()) {
				if (c instanceof ClauseAssumeAliases) {
					final ClauseAssumeAliases ca = (ClauseAssumeAliases) c;
					final String originString1 = ca.getReference().asOriginString();
					final String originString2 = ca.getObjekt().getOrigin().asOriginString();
					if (cannotBeAlias(originString1, originString2)) {
						return false;
					}
				} else if (c instanceof ClauseAssumeNull) {
					if (cannotBeNull(((ClauseAssumeNull) c).getReference().asOriginString())) {
						return false;
					}
				}
			}
			return true;
		}
		
		private boolean cannotBeAlias(String originString1, String originString2) {
			HashSet<String> set1 = null;
			HashSet<String> set2 = null;
			for (HashSet<String> set : this.partition) {
				if (set.contains(originString1)) {
					set1 = set;
				}
				if (set.contains(originString2)) {
					set2 = set;
				}
			}
			return (set1 != null && set2 != null && set1 != set2);
		}
		
		private boolean cannotBeNull(String originString) {
			if (this.nulls.contains(originString)) {
				return false;
			}
			for (HashSet<String> set : this.partition) {
				if (set.contains(originString)) {
					return true;
				}
			}
			return false;
		}


		@Override
		public void atEnd() {
			super.atEnd();
		}		
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
	private Runner newRunner(Actions actions)
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
	private Runner newRunner(Actions actions, State sInitial)
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

		//builds the runner and returns it
		final RunnerBuilder rb = new RunnerBuilder();
		final Runner r = rb.build(params);		
		return r;
	}
	
	@Override
	public Individual<GeneJBSE> generateRandomIndividual(List<GeneJBSE> chromosome) {
		State s = null;

		try {
			final State sInitial = this.initialState.clone();
			final ArrayList<ClauseAssumeReferenceSymbolic> referencesPrecondition = new ArrayList<>();
			final ArrayList<Boolean> negated = new ArrayList<>();
			for (GeneJBSE c : chromosome) {
				final Clause clause = c.getClause();
				if (clause instanceof ClauseAssume) {
					Primitive condition = ((ClauseAssume) clause).getCondition();
					if (c.isNegated()) {
						condition = condition.not();
					}
					sInitial.assume(condition);
				} else if (clause instanceof ClauseAssumeReferenceSymbolic) {
					referencesPrecondition.add((ClauseAssumeReferenceSymbolic) clause);
					negated.add(c.isNegated());
				} //else skip
			}
			final ActionsRunner actions = new ActionsRunner(referencesPrecondition, negated);
			final Runner r = newRunner(actions, sInitial);
			r.run();
			s = actions.getEndState();
		} catch (DecisionException | CannotBuildEngineException | InitializationException | InvalidTypeException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException | InvalidInputException e) {
			//TODO improve!
			throw new RuntimeException(e);
		}
		
		if (s == null) {
			return null;
		}
		
		final List<GeneJBSE> chromosomeIndividual = 
				s.getPathCondition().stream()
				.filter(clause -> (clause instanceof ClauseAssume || clause instanceof ClauseAssumeReferenceSymbolic))
				.map(clause -> new GeneJBSE(clause)).collect(Collectors.toList());		
		return new Individual<>(chromosomeIndividual, s.getDepth() + 1);
	}
}
