package gasp.ga.jbse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

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
import jbse.val.Expression;
import jbse.val.Primitive;
import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidTypeException;

public class IndividualGeneratorJBSE implements IndividualGenerator<GeneJBSE> {
	private static final String SWITCH_CHAR = System.getProperty("os.name").toLowerCase().contains("windows") ? "/" : "-";

	private final Random random;
	private final String[] classpath;
	private final String z3Path;
	private final ArrayList<String> z3CommandLine;
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
		this.z3CommandLine  = new ArrayList<>();
		this.z3CommandLine.add(this.z3Path);
		this.z3CommandLine.add(SWITCH_CHAR + "smt2");
		this.z3CommandLine.add(SWITCH_CHAR + "in");
		this.z3CommandLine.add(SWITCH_CHAR + "t:10");
		this.commonParams = new RunnerParameters();
		this.commonParams.setMethodSignature(methodClassName, methodDescriptor, methodName);
		this.commonParams.addUserClasspath(this.classpath);
		final Actions actions = new Actions() {
			@Override
			public boolean atInitial() {
				return true;
			}
		};
		try {
			final Runner r = newRunner(actions);
			r.run();
			this.initialState = r.getEngine().getInitialState().clone();
			r.getEngine().close();
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			// TODO throw better exception
			throw new RuntimeException(e);
		}
	}

	private class ActionsRunner extends Actions {
		private final ChromosomeChecker chk;
		private final ArrayList<GeneJBSE> chromosome = new ArrayList<>();
		private State endState = null;
		
		public ActionsRunner(ChromosomeChecker chk) {
			this.chk = chk;
		}
		
		@Override
		public boolean atRoot() {
			final Engine engine = getEngine();
			addGenes(engine.getCurrentState().getPathCondition());
			return super.atInitial();
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
			int numberOfCompliantStates = 0;
			for (int index : indices) {
				try {
					final State s = (index == 0 ? engine.getCurrentState() : engine.getStateAtBranch(bp, index - 1));
					if (contradictsReferencePrecondition(s)) {
						continue;
					} else {
						++numberOfCompliantStates;
						if (compliantStateIndex == none) {
							compliantStateIndex = index;
						}
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
			
			//get the last added clauses in the state and add them to the chromosome, 
			//given that they are not redundant w.r.t. the preconditions
			if (numberOfCompliantStates > 1) {
				addGenes(engine.getCurrentState().getLastPathConditionPushedClauses());
			}
			
			return super.atBranch(bp);
		}
		
		private void addGenes(Iterable<Clause> clauses) {
			for (Clause clause : clauses) {
				if (clause instanceof ClauseAssume || clause instanceof ClauseAssumeReferenceSymbolic) {
					this.chromosome.add(new GeneJBSE(clause));
				}
			}
		}

		private boolean contradictsReferencePrecondition(State s) {
			for (Clause c : s.getPathCondition()) {
				if (c instanceof ClauseAssumeAliases) {
					final ClauseAssumeAliases ca = (ClauseAssumeAliases) c; 
					final String originString1 = ca.getReference().asOriginString();
					final String originString2 = ca.getObjekt().getOrigin().asOriginString();
					if (this.chk.contradictsAlias(originString1, originString2)) {
						return true;
					}
				} else if (c instanceof ClauseAssumeExpands) {
					final ClauseAssumeExpands ce = (ClauseAssumeExpands) c;
					final String originString = ce.getReference().asOriginString();
					final String className = ce.getObjekt().getType().getClassName();
					if (this.chk.contradictsExpands(originString, className)) {
						return true;
					}
				} else if (c instanceof ClauseAssumeNull) {
					final ClauseAssumeNull cn = (ClauseAssumeNull) c;
					final String originString = cn.getReference().asOriginString();
					if (this.chk.contradictsNull(originString)) {
						return true;
					}
				}  //else, do nothing
			}
			return false;
		}

		@Override
		public boolean atTraceEnd() {
			this.endState = this.getEngine().getCurrentState().clone();
			return true;
		}

		@Override
		public void atEnd() {
			super.atEnd();
		}		
	}

	private Runner newRunner(Actions actions)
	throws DecisionException, CannotBuildEngineException, InitializationException, 
	InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
	ClasspathException, CannotBacktrackException, CannotManageStateException, 
	ThreadStackEmptyException, ContradictionException, EngineStuckException, 
	FailureException {
		return newRunner(actions, null);
	}
	
	private Runner newRunner(Actions actions, State sInitial)
	throws DecisionException, CannotBuildEngineException, InitializationException, 
	InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
	ClasspathException, CannotBacktrackException, CannotManageStateException, 
	ThreadStackEmptyException, ContradictionException, EngineStuckException, 
	FailureException {

		//builds the parameters
		final RunnerParameters params = this.commonParams.clone();
		
		//sets the calculator
		final CalculatorRewriting calc = new CalculatorRewritingSynchronized();
		calc.addRewriter(new RewriterOperationOnSimplex());
		params.setCalculator(calc);
		
		//sets the decision procedures
		params.setDecisionProcedure(new DecisionProcedureAlgorithms(
				new DecisionProcedureClassInit( //useless?
						new DecisionProcedureLICS( //useless?
								new DecisionProcedureSMTLIB2_AUFNIRA(
										new DecisionProcedureAlwSat(), 
										calc, this.z3CommandLine), 
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
			final ActionsRunner actions = new ActionsRunner(new ChromosomeChecker(chromosome));
			final State sInitial = this.initialState.clone();
			final Runner r = newRunner(actions, sInitial);
			r.run();
			s = actions.endState;
			r.getEngine().close();
			if (s == null) {
				return null;
			}
			return new Individual<>(simplify(actions.chromosome), s.getDepth() + 1);
		} catch (DecisionException | CannotBuildEngineException | InitializationException | InvalidTypeException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException | InvalidInputException | InvalidOperandException e) {
			//TODO improve!
			throw new RuntimeException(e);
		}
	}
	
	private ArrayList<GeneJBSE> simplify(ArrayList<GeneJBSE> toSimplify) throws DecisionException, InvalidTypeException, InvalidInputException {
		final CalculatorRewriting calc = new CalculatorRewriting();
		calc.addRewriter(new RewriterOperationOnSimplex());
		final ArrayList<GeneJBSE> retVal = new ArrayList<>();
		try (DecisionProcedureSMTLIB2_AUFNIRA dec = new DecisionProcedureSMTLIB2_AUFNIRA(new DecisionProcedureAlwSat(), calc, IndividualGeneratorJBSE.this.z3CommandLine)) {
			for (GeneJBSE gene : reverse(toSimplify)) {
				final Clause clause = gene.getClause();
				if (clause instanceof ClauseAssumeReferenceSymbolic) {
					retVal.add(0, gene);
				} else if (clause instanceof ClauseAssume) {
					final Expression conditionNegated = (Expression) ((ClauseAssume) clause).getCondition().not();
					if (dec.isSat(conditionNegated)) {
						retVal.add(0, gene);
						dec.pushAssumption(clause);
					} //else, discard it
				} //else, discard it
			}
		}
		return retVal;
	}
	
	private <T> Iterable<T> reverse(List<T> list) {
		return new Iterable<T>() {
			final ListIterator<T> it = list.listIterator(list.size());
			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					@Override
					public boolean hasNext() {
						return it.hasPrevious();
					}

					@Override
					public T next() {
						return it.previous();
					}
					
					@Override
					public void remove() {
						it.remove();
					}
				};
			}
		};
	}
	
	private class ChromosomeChecker {
		final ArrayList<HashSet<String>> aliases = new ArrayList<>();
		final HashMap<String, HashSet<String>> notAliases = new HashMap<>();
		final ArrayList<String> pointsTo = new ArrayList<>();
		final ArrayList<HashSet<String>> doesNotPointTo = new ArrayList<>();
		final HashSet<String> nulls = new HashSet<>();
		final HashSet<String> notNulls = new HashSet<>();
		final ArrayList<GeneJBSE> chromosomeFiltered = new ArrayList<>();
		
		ChromosomeChecker(List<GeneJBSE> chromosome) throws DecisionException, InvalidTypeException, InvalidOperandException, InvalidInputException {
			final HashSet<Integer> contradictoryGenesPositions = new HashSet<>();
			Primitive precondition = null;
			final CalculatorRewriting calc = new CalculatorRewriting();
			calc.addRewriter(new RewriterOperationOnSimplex());
			try (DecisionProcedureSMTLIB2_AUFNIRA dec = new DecisionProcedureSMTLIB2_AUFNIRA(new DecisionProcedureAlwSat(), calc, IndividualGeneratorJBSE.this.z3CommandLine)) {
				for (int i = 0; i < chromosome.size(); ++i) {
					final GeneJBSE gene = chromosome.get(i);
					final Clause clause = gene.getClause();
					if (clause instanceof ClauseAssume) {
						Primitive condition = ((ClauseAssume) clause).getCondition();
						if (gene.isNegated()) {
							condition = condition.not();
						}
						if (contradicts(dec, precondition, condition)) {
							contradictoryGenesPositions.add(i);
						} else {
							precondition = (precondition == null ? condition : precondition.and(condition));
						}
					} else if (clause instanceof ClauseAssumeReferenceSymbolic) {
						if (gene.isNegated()) {
							if (clause instanceof ClauseAssumeAliases) {
								final ClauseAssumeAliases ca = (ClauseAssumeAliases) clause;
								final String originString1 = ca.getReference().asOriginString();
								final String originString2 = ca.getObjekt().getOrigin().asOriginString();
								if (contradictsNotAlias(originString1, originString2)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNotAlias(originString1, originString2);
								}
							} else if (clause instanceof ClauseAssumeExpands) {
								final ClauseAssumeExpands ce = (ClauseAssumeExpands) clause;
								final String originString = ce.getReference().asOriginString();
								final String className = ce.getObjekt().getType().getClassName();
								if (contradictsDoesNotPointTo(originString, className)) {
									contradictoryGenesPositions.add(i);
								} else {
									addDoesNotPointTo(originString, className);
								}
							} else { //clause instanceof ClauseAssumeNull
								final ClauseAssumeNull cn = (ClauseAssumeNull) clause;
								final String originString = cn.getReference().asOriginString();
								if (contradictsNotNull(originString)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNotNull(originString);
								}
							}
						} else {
							if (clause instanceof ClauseAssumeAliases) {
								final ClauseAssumeAliases ca = (ClauseAssumeAliases) clause;
								final String originString1 = ca.getReference().asOriginString();
								final String originString2 = ca.getObjekt().getOrigin().asOriginString();
								if (contradictsAlias(originString1, originString2)) {
									contradictoryGenesPositions.add(i);
								} else {
									addAlias(originString1, originString2);
								}
							} else if (clause instanceof ClauseAssumeExpands) {
								final ClauseAssumeExpands ce = (ClauseAssumeExpands) clause;
								final String originString = ce.getReference().asOriginString();
								final String className = ce.getObjekt().getType().getClassName();
								if (contradictsExpands(originString, className)) {
									contradictoryGenesPositions.add(i);
								} else {
									addExpands(originString, className);
								}
							} else { //clause instanceof ClauseAssumeNull
								final ClauseAssumeNull cn = (ClauseAssumeNull) clause;
								final String originString = cn.getReference().asOriginString();
								if (contradictsNull(originString)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNull(originString);
								}
							}
						}
					} //else skip
				}
			}
			
			//builds the filtered chromosome
			for (int i = 0; i < chromosome.size(); ++i) {
				if (contradictoryGenesPositions.contains(i)) {
					//do nothing
				} else {
					this.chromosomeFiltered.add(chromosome.get(i));
				}
			}
		}
	
		boolean contradicts(DecisionProcedureSMTLIB2_AUFNIRA dec, Primitive precondition, Primitive condition) 
				throws InvalidOperandException, InvalidTypeException, InvalidInputException, DecisionException {
			final Primitive conditionAnd = (precondition == null ? condition : precondition.and(condition));
			if (conditionAnd.surelyFalse()) {
				return true;
			} else if (conditionAnd.surelyTrue()) {
				return false;
			} else {
				return dec.isSat((Expression) conditionAnd);
			}
		}

		boolean contradictsNotAlias(String originString1, String originString2) {
			if (this.nulls.contains(originString1) || this.nulls.contains(originString2)) {
				return false;
			}
			HashSet<String> aliasSet1 = null, aliasSet2 = null;
			for (HashSet<String> aliasSet : this.aliases) {
				if (aliasSet.contains(originString1)) {
					aliasSet1 = aliasSet;
				}
				if (aliasSet.contains(originString2)) {
					aliasSet2 = aliasSet;
				}
			}
			if (aliasSet1 != null && aliasSet2 != null && aliasSet1 == aliasSet2) {
				return true;
			}
			return false;
		}

		void addNotAlias(String originString1, String originString2) {
			final HashSet<String> inequivalent1 = (this.notAliases.containsKey(originString1) ? this.notAliases.get(originString1) : new HashSet<>());
			inequivalent1.add(originString2);
			notAliases.put(originString1, inequivalent1);
			final HashSet<String> inequivalent2 = (this.notAliases.containsKey(originString2) ? this.notAliases.get(originString2) : new HashSet<>());
			inequivalent2.add(originString1);
			notAliases.put(originString2, inequivalent2);
		}

		boolean contradictsDoesNotPointTo(String originString, String className) {
			for (int i = 0; i < this.aliases.size(); ++i) {
				final HashSet<String> aliasSet = this.aliases.get(i);
				if (aliasSet.contains(originString) && this.pointsTo.get(i).equals(className)) {
					return true;
				}
			}
			return false;
		}

		void addDoesNotPointTo(String originString, String className) {
			for (int i = 0; i < this.aliases.size(); ++i) {
				final HashSet<String> aliasSet = this.aliases.get(i);
				if (aliasSet.contains(originString)) {
					this.doesNotPointTo.get(i).add(className);
					return;
				}
			}
			this.aliases.add(new HashSet<>());
			this.aliases.get(aliases.size() - 1).add(originString);
			this.pointsTo.add(null);
			this.doesNotPointTo.add(new HashSet<>());
			this.doesNotPointTo.get(doesNotPointTo.size() - 1).add(className);
		}

		boolean contradictsNotNull(String originString) {
			return this.nulls.contains(originString);
		}

		void addNotNull(String originString) {
			this.notNulls.add(originString);
		}

		boolean contradictsAlias(String originString1, String originString2) {
			if (this.nulls.contains(originString1) || this.nulls.contains(originString2)) {
				return true;
			}
			if (this.notAliases.containsKey(originString1) && this.notAliases.get(originString1).contains(originString2)) {
				return true;
			}
			if (this.notAliases.containsKey(originString2) && this.notAliases.get(originString2).contains(originString1)) {
				return true;
			}
			HashSet<String> aliasSet1 = null, aliasSet2 = null;
			for (HashSet<String> aliasSet : this.aliases) {
				if (aliasSet.contains(originString1)) {
					aliasSet1 = aliasSet;
				}
				if (aliasSet.contains(originString2)) {
					aliasSet2 = aliasSet;
				}
			}
			if (aliasSet1 != null && aliasSet2 != null && aliasSet1 != aliasSet2) {
				return true;
			}
			return false;
		}

		void addAlias(String originString1, String originString2) {
			for (HashSet<String> aliasSet : this.aliases) {
				if (aliasSet.contains(originString1) || aliasSet.contains(originString2)) {
					aliasSet.add(originString1);
					aliasSet.add(originString2);
					return;
				}
			}
			this.aliases.add(new HashSet<>());
			this.aliases.get(aliases.size() - 1).add(originString1);
			this.aliases.get(aliases.size() - 1).add(originString2);
			this.pointsTo.add(null);
			this.doesNotPointTo.add(new HashSet<>());
		}

		boolean contradictsExpands(String originString, String className) {
			for (int i = 0; i < this.aliases.size(); ++i) {
				final HashSet<String> aliasSet = this.aliases.get(i);
				if (aliasSet.contains(originString)) {
					return (this.doesNotPointTo.get(i).contains(className) ||
							(this.pointsTo.get(i) != null && !className.equals(this.pointsTo.get(i))));
				}
			}
			return false;
		}

		void addExpands(String originString, String className) {
			for (int i = 0; i < this.aliases.size(); ++i) {
				final HashSet<String> aliasSet = this.aliases.get(i);
				if (aliasSet.contains(originString)) {
					this.pointsTo.set(i, className);
					return;
				}
			}
			this.aliases.add(new HashSet<>());
			this.aliases.get(aliases.size() - 1).add(originString);
			this.pointsTo.add(className);
			this.doesNotPointTo.add(new HashSet<>());
		}

		boolean contradictsNull(String originString) {
			if (this.notNulls.contains(originString)) {
				return true;
			}
			for (HashSet<String> aliasSet : aliases) {
				if (aliasSet.contains(originString)) {
					return true;
				}
			}
			return false;
		}

		void addNull(String originString) {
			this.nulls.add(originString);
		}
	}
}
