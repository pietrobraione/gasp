package gasp.ga.jbse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gasp.ga.FoundWorstIndividualException;
import gasp.ga.IndividualGenerator;
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
import jbse.mem.exc.HeapMemoryExhaustedException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.rewr.CalculatorRewriting;
import jbse.rewr.RewriterOperationOnSimplex;
import jbse.rules.ClassInitRulesRepo;
import jbse.rules.LICSRulesRepo;
import jbse.tree.StateTree.BranchPoint;
import jbse.val.Expression;
import jbse.val.Primitive;
import jbse.val.ReferenceSymbolic;
import jbse.val.ReferenceSymbolicMember;
import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidTypeException;

public final class IndividualGeneratorJBSE implements IndividualGenerator<GeneJBSE, IndividualJBSE> {
	private static final Logger logger = LogManager.getFormatterLogger(IndividualGeneratorJBSE.class);
	
	private static final String SWITCH_CHAR = System.getProperty("os.name").toLowerCase().contains("windows") ? "/" : "-";

	private final long maxFitness;
	private final Random random;
	private final String[] classpath;
	private final String z3Path;
	private final ArrayList<String> z3CommandLine;
	private final RunnerParameters commonParams;
	private final State initialState;
	
	public IndividualGeneratorJBSE(long maxFitness, Random random, List<Path> classpath, Path jbsePath, Path z3Path, String methodClassName, String methodDescriptor, String methodName) {
		if (maxFitness <= 0) {
			throw new IllegalArgumentException("The maximum fitness cannot be  less or equal to 0.");
		}
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
		
		this.maxFitness = maxFitness;
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
		final Runner r;
		try {
			r = newRunner(actions);
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException | HeapMemoryExhaustedException e) {
			//TODO throw better exception
			throw new RuntimeException(e);
		}
		try {
			r.run();
			this.initialState = r.getEngine().getInitialState().clone();
		} catch (DecisionException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException e) {
			//TODO throw better exception
			throw new RuntimeException(e);
		} finally {
			try {
				r.getEngine().close();
			} catch (DecisionException e) {
				//TODO throw better exception
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public IndividualJBSE generateRandomIndividual(List<GeneJBSE> chromosome) throws FoundWorstIndividualException {
		final ArrayList<GeneJBSE> chromosomeShuffled = new ArrayList<>(chromosome);
		Collections.shuffle(chromosomeShuffled, this.random);
		final ChromosomeChecker chk;
		try {
			chk = new ChromosomeChecker(chromosomeShuffled);
		} catch (DecisionException | InvalidTypeException | InvalidOperandException | InvalidInputException e) {
			//TODO throw better exception
			throw new RuntimeException(e);
		}
		final ActionsRunner actions = new ActionsRunner(chk);
		final Runner r;
		try {
			r = newRunner(actions, chk.chromosomeFiltered);
		} catch (DecisionException | CannotBuildEngineException | InitializationException
				| InvalidClassFileFactoryClassException | NonexistingObservedVariablesException | ClasspathException
				| CannotBacktrackException | CannotManageStateException | ThreadStackEmptyException
				| ContradictionException | EngineStuckException | FailureException
				| HeapMemoryExhaustedException e) {
			//TODO throw better exception
			throw new RuntimeException(e);
		}
		try {
			r.run();
			if (actions.outcome == Outcome.FOUND) {
				return new IndividualJBSE(simplify(actions.chromosome), actions.fitness, actions.pathIdentifier);
			} else if (actions.outcome == Outcome.MAXIMUM_FITNESS_REACHED) {
				throw new FoundWorstIndividualException(new IndividualJBSE(simplify(actions.chromosome), actions.fitness, actions.pathIdentifier));
			} else {
				return null; //TODO distinguish the two remaining subcases of action.outcome
			}
		} catch (DecisionException | InvalidTypeException | NonexistingObservedVariablesException 
				| ClasspathException | CannotBacktrackException | CannotManageStateException 
				| ThreadStackEmptyException | ContradictionException | EngineStuckException 
				| FailureException | InvalidInputException e) {
			//TODO throw better exception
			throw new RuntimeException(e);
		} finally {
			try {
				r.getEngine().close();
			} catch (DecisionException e) {
				//TODO throw better exception
				throw new RuntimeException(e);
			}
		}
	}
	
	private enum Outcome { FOUND, ASSUMPTION_VIOLATED, PRECONDITION_TOO_STRICT, MAXIMUM_FITNESS_REACHED };

	private class ActionsRunner extends Actions {
		private final ChromosomeChecker chk;
		private final ArrayList<GeneJBSE> chromosome = new ArrayList<>();
		private Outcome outcome = null;
		private long fitness = 0;
		private String pathIdentifier = null;
		
		public ActionsRunner(ChromosomeChecker chk) {
			this.chk = chk;
			this.chromosome.addAll(chk.chromosomeFiltered);
		}
		
		private long fitness() {
			return getEngine().getAnalyzedStates() + 1;
		}
		
		@Override
		public boolean atRoot() {
			final List<Clause> pathCondition = getEngine().getCurrentState().getPathCondition();
			final Clause lastPathConditionClause = pathCondition.get(pathCondition.size() - 1);
			if (lastPathConditionClause instanceof ClauseAssumeExpands && "{ROOT}:this".equals(((ClauseAssumeExpands) lastPathConditionClause).getReference().asOriginString())) {
				this.chromosome.add(new GeneJBSE(lastPathConditionClause));
			}
			return super.atRoot();
		}
		
		@Override
		public boolean atStepPost() {
			final long fitness = fitness();
			if (fitness >= IndividualGeneratorJBSE.this.maxFitness) {
				this.outcome = Outcome.MAXIMUM_FITNESS_REACHED;
				this.fitness = fitness;
				this.pathIdentifier = getEngine().getCurrentState().getIdentifier();
				logger.debug("Trace finished, maximum fitness reached, fitness %d", fitness);
				return true;
			}
			return super.atStepPost();
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
					throw new AssertionError(e);
				}
			}
			
			//if none found, terminate
			if (compliantStateIndex == none) {
				this.outcome = Outcome.PRECONDITION_TOO_STRICT;
				logger.debug("Trace finished, no state complies with precondition (too strict?), fitness %d", fitness());
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
					final ReferenceSymbolic reference = ca.getReference();
					final ReferenceSymbolic originAlias = ca.getObjekt().getOrigin();
					if (this.chk.contradictsAlias(reference, originAlias)) {
						return true;
					}
				} else if (c instanceof ClauseAssumeExpands) {
					final ClauseAssumeExpands ce = (ClauseAssumeExpands) c;
					final ReferenceSymbolic reference = ce.getReference();
					final String className = ce.getObjekt().getType().getClassName();
					if (this.chk.contradictsExpands(reference, className)) {
						return true;
					}
				} else if (c instanceof ClauseAssumeNull) {
					final ClauseAssumeNull cn = (ClauseAssumeNull) c;
					final ReferenceSymbolic reference = cn.getReference();
					if (this.chk.contradictsNull(reference)) {
						return true;
					}
				}  //else, do nothing
			}
			return false;
		}
		
		@Override
		public boolean atContradictionException(ContradictionException e) {
			this.outcome = Outcome.ASSUMPTION_VIOLATED;
			logger.debug("Trace finished, assumption violated, fitness %d", fitness());
			return true;
		}

		@Override
		public boolean atTraceEnd() {
			this.outcome = Outcome.FOUND;
			this.fitness = fitness();
			this.pathIdentifier = getEngine().getCurrentState().getIdentifier();
			logger.debug("Trace finished, found individual, fitness %d", this.fitness);
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
	FailureException, HeapMemoryExhaustedException {
		return newRunner(actions, null);
	}
	
	private Runner newRunner(Actions actions, List<GeneJBSE> chromosome)
	throws DecisionException, CannotBuildEngineException, InitializationException, 
	InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
	ClasspathException, CannotBacktrackException, CannotManageStateException, 
	ThreadStackEmptyException, ContradictionException, EngineStuckException, 
	FailureException, HeapMemoryExhaustedException {
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
		final State initialState = (this.initialState == null ? null : this.initialState.clone());
		if (initialState != null) {
			if (chromosome != null) {				
				//sorts the chromosome
				final ArrayList<GeneJBSE> chromosomeSorted = new ArrayList<>(chromosome);
				Collections.sort(chromosomeSorted, new ComparatorGeneJBSE());
				
				//assumes all the positive clauses in the chromosome
				for (GeneJBSE gene : chromosome) {
					try {
						final Clause clause = gene.getClause();
						if (clause instanceof ClauseAssumeAliases) {
							final ClauseAssumeAliases ca = (ClauseAssumeAliases) clause;
							if (!gene.isNegated()) {
								initialState.assumeAliases(ca.getReference(), ca.getObjekt().getOrigin());
							}
						} else if (clause instanceof ClauseAssumeExpands) {
							final ClauseAssumeExpands ce = (ClauseAssumeExpands) clause;
							if (!gene.isNegated()) {
								initialState.assumeExpands(ce.getReference(), ce.getObjekt().getType());
							}
						} else if (clause instanceof ClauseAssumeNull) {
							final ClauseAssumeNull cn = (ClauseAssumeNull) clause;
							if (!gene.isNegated()) {
								initialState.assumeNull(cn.getReference());
							}
						} else if (clause instanceof ClauseAssume) {
							final Primitive condition = gene.isNegated() ? ((ClauseAssume) clause).getCondition().not() : ((ClauseAssume) clause).getCondition();
							initialState.assume(condition);
						}
					} catch (ContradictionException e) {
						//found a duplicate or contradictory reference clause that
						//survived filtering: just skip it
						continue; //pleonastic
					} catch (InvalidInputException | InvalidTypeException e) {
						//this should never happen
						throw new AssertionError("Found an invalid condition in a clause.", e);
					}
				}
			}
			params.setInitialState(initialState);
		}

		//builds the runner and returns it
		final RunnerBuilder rb = new RunnerBuilder();
		final Runner r = rb.build(params);		
		return r;
	}
	
	private ArrayList<GeneJBSE> simplify(ArrayList<GeneJBSE> toSimplify) throws DecisionException, InvalidTypeException, InvalidInputException {
		final ArrayList<GeneJBSE> retVal = new ArrayList<>();
		
		//first pass: delete redundant numeric clauses
		final CalculatorRewriting calc = new CalculatorRewriting();
		calc.addRewriter(new RewriterOperationOnSimplex());
		try (DecisionProcedureSMTLIB2_AUFNIRA dec = new DecisionProcedureSMTLIB2_AUFNIRA(new DecisionProcedureAlwSat(), calc, IndividualGeneratorJBSE.this.z3CommandLine)) {
			for (GeneJBSE gene : reverse(toSimplify)) {
				final Clause clause = gene.getClause();
				if (clause instanceof ClauseAssumeReferenceSymbolic) {
					retVal.add(0, gene);
				} else if (clause instanceof ClauseAssume) {
					final Expression conditionNegated = gene.isNegated() ? (Expression) ((ClauseAssume) clause).getCondition() : (Expression) ((ClauseAssume) clause).getCondition().not();
					if (dec.isSat(conditionNegated)) {
						retVal.add(0, gene);
						dec.pushAssumption(clause);
					} //else, discard it
				} //else, discard it
			}
		}
		
		//second pass: delete redundant reference genes
		final HashSet<GeneJBSE> seen = new HashSet<>();
		for (Iterator<GeneJBSE> it = retVal.iterator(); it.hasNext(); ) {
			final GeneJBSE gene = it.next();
			final Clause clause = gene.getClause();
			if (clause instanceof ClauseAssumeReferenceSymbolic) {
				if (seen.contains(gene)) {
					it.remove();
				} else {
					seen.add(gene);
				}
			} //else, do nothing
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
		/** 
		 * Maps a ReferenceSymbolic that must expand to the set of 
		 * the ReferenceSymbolic that must alias it. 
		 */
		final HashMap<ReferenceSymbolic, HashSet<ReferenceSymbolic>> aliases = new HashMap<>();

		/** 
		 * Maps a ReferenceSymbolic that may expand to the set of 
		 * the ReferenceSymbolic that may not alias it (i.e., that
		 * either are not alias or alias something else). 
		 */
		final HashMap<ReferenceSymbolic, HashSet<ReferenceSymbolic>> notAliases = new HashMap<>();
		
		/** 
		 * Maps a ReferenceSymbolic that must expand to the name of 
		 * the class of the object it must points to (or null if we
		 * just assume expansion without any assumption on the class). 
		 */
		final HashMap<ReferenceSymbolic, String> expands = new HashMap<>();

		/** 
		 * Maps a ReferenceSymbolic that may expands to the name of 
		 * the classes of the object it may not point to. 
		 */
		final HashMap<ReferenceSymbolic, HashSet<String>> doesNotExpandTo = new HashMap<>();
		
		/** Set of ReferenceSymbolic that are nulls. */
		final HashSet<ReferenceSymbolic> nulls = new HashSet<>();
		
		/** Set of ReferenceSymbolic that are not nulls. */
		final HashSet<ReferenceSymbolic> notNulls = new HashSet<>();
		
		/** The filtered chromosome. */
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
								final ReferenceSymbolic reference = ca.getReference();
								final ReferenceSymbolic originAlias = ca.getObjekt().getOrigin();
								if (contradictsNotAlias(reference, originAlias)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNotAlias(reference, originAlias);
								}
							} else if (clause instanceof ClauseAssumeExpands) {
								final ClauseAssumeExpands ce = (ClauseAssumeExpands) clause;
								final ReferenceSymbolic origin = ce.getReference();
								final String className = ce.getObjekt().getType().getClassName();
								if (contradictsDoesNotExpand(origin, className)) {
									contradictoryGenesPositions.add(i);
								} else {
									addDoesNotExpand(origin, className);
								}
							} else { //clause instanceof ClauseAssumeNull
								final ClauseAssumeNull cn = (ClauseAssumeNull) clause;
								final ReferenceSymbolic origin = cn.getReference();
								if (contradictsNotNull(origin)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNotNull(origin);
								}
							}
						} else {
							if (clause instanceof ClauseAssumeAliases) {
								final ClauseAssumeAliases ca = (ClauseAssumeAliases) clause;
								final ReferenceSymbolic reference = ca.getReference();
								final ReferenceSymbolic originAlias = ca.getObjekt().getOrigin();
								if (contradictsAlias(reference, originAlias)) {
									contradictoryGenesPositions.add(i);
								} else {
									addAlias(reference, originAlias);
								}
							} else if (clause instanceof ClauseAssumeExpands) {
								final ClauseAssumeExpands ce = (ClauseAssumeExpands) clause;
								final ReferenceSymbolic reference = ce.getReference();
								final String className = ce.getObjekt().getType().getClassName();
								if (contradictsExpands(reference, className)) {
									contradictoryGenesPositions.add(i);
								} else {
									addExpands(reference, className);
								}
							} else { //clause instanceof ClauseAssumeNull
								final ClauseAssumeNull cn = (ClauseAssumeNull) clause;
								final ReferenceSymbolic reference = cn.getReference();
								if (contradictsNull(reference)) {
									contradictoryGenesPositions.add(i);
								} else {
									addNull(reference);
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
				return !dec.isSat((Expression) conditionAnd);
			}
		}

		boolean contradictsNotAlias(ReferenceSymbolic reference, ReferenceSymbolic originAlias) {
			//if originAlias expands, then reference does not alias, or aliases
			//something else; Note that this.expands.keySet().equals(this.aliases.keySet(), 
			//thus this.aliases.containsKey(originAlias) iff originAlias expands
			if (this.aliases.containsKey(originAlias)) { 
				final HashSet<ReferenceSymbolic> aliasSet = this.aliases.get(originAlias);
				if (aliasSet.contains(reference)) {
					return true;
				}
			}
			
			//all checks ok: it is possible to assume that reference does
			//not alias originAlias
			return false;
		}

		void addNotAlias(ReferenceSymbolic reference, ReferenceSymbolic originAlias) {
			if (!this.notAliases.containsKey(originAlias)) {
				this.notAliases.put(originAlias, new HashSet<>());
			}
			this.notAliases.get(originAlias).add(reference);
		}

		boolean contradictsDoesNotExpand(ReferenceSymbolic reference, String className) {
			//if reference expands, then it must not expand to className
			if (this.expands.containsKey(reference)) {
				final String assumedClassName = this.expands.get(reference);
				if (assumedClassName != null && !assumedClassName.equals(className)) {
					return true;
				}
			}
			
			//all checks ok: it is possible to assume that reference does
			//not expand to className
			return false;
		}

		void addDoesNotExpand(ReferenceSymbolic reference, String className) {
			if (!this.doesNotExpandTo.containsKey(reference)) {
				this.doesNotExpandTo.put(reference, new HashSet<>());
			}
			this.doesNotExpandTo.get(reference).add(className);
		}

		boolean contradictsNotNull(ReferenceSymbolic reference) {
			return this.nulls.contains(reference);
		}

		void addNotNull(ReferenceSymbolic reference) {
			this.notNulls.add(reference);
		}

		boolean contradictsAlias(ReferenceSymbolic reference, ReferenceSymbolic originAlias) {
			//originAlias must expand
			if (contradictsExpands(originAlias, null)) {
				return true;
			}
			
			//reference must not be null
			if (this.nulls.contains(reference)) {
				return true;
			}
			
			//reference must not expand
			if (this.expands.containsKey(reference)) {
				return true;
			}
			
			//if reference was already assumed to alias something, 
			//then it must be originAlias
			for (Map.Entry<ReferenceSymbolic, HashSet<ReferenceSymbolic>> entry : this.aliases.entrySet()) {
				if (entry.getValue().contains(reference) && !entry.getKey().equals(originAlias)) {
					return true;
				}
			}
			
			//reference must not be in the set of the forbidden
			//aliases for originAlias
			if (this.notAliases.containsKey(originAlias) && this.notAliases.get(originAlias).contains(reference)) {
				return true;
			}
			
			//if reference has a container, then assuming the expansion of the 
			//container must not yield a contradiction 
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				if (contradictsExpands(container, null)) {
					return true;
				}
			}
			
			//all checks ok: reference may alias originAlias
			return false;
		}

		void addAlias(ReferenceSymbolic reference, ReferenceSymbolic originAlias) {
			//assume originAlias to expand
			addExpands(originAlias, null);
			
			//add the alias to this.aliases
			this.aliases.get(originAlias).add(reference);
			
			//if reference has a container, assume the expansion 
			//of the container recursive closure
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				addExpands(container, null);
			}
		}

		boolean contradictsExpands(ReferenceSymbolic reference, String className) {
			//reference must not be null
			if (this.nulls.contains(reference)) {
				return true;
			}
			
			//reference must not alias
			for (HashSet<ReferenceSymbolic> aliasSet : this.aliases.values()) {
				if (aliasSet.contains(reference)) {
					return true;
				}
			}
			
			//if reference was already assumed to expand to some class, 
			//then it must not expand to a class different from className
			//(if present)
			if (this.expands.containsKey(reference)) {
				final String assumedClassName = this.expands.get(reference);
				if (className != null && assumedClassName != null && !assumedClassName.equals(className)) {
					return true;
				}
			}
		
			//className (if present) must not be contained in the 
			//forbidden expansions of reference (if any)
			if (className != null && this.doesNotExpandTo.containsKey(reference)) {
				if (this.doesNotExpandTo.get(reference).contains(className)) {
					return true;
				}
			}
			
			//if reference has a container, then assuming the expansion of the 
			//container must not yield a contradiction 
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				if (contradictsExpands(container, null)) {
					return true;
				}
			}
			
			//all checks ok: reference may expand to an object with 
			//class className
			return false;
		}

		void addExpands(ReferenceSymbolic reference, String className) {
			//this.aliases must contain the expansion, and must not
			//weaken the associated alias set
			if (!this.aliases.containsKey(reference)) {
				this.aliases.put(reference, new HashSet<>());
			}
			
			//this.expands must contain the expansion, and must not
			//weaken the associated class name 
			if (this.expands.containsKey(reference)) {
				final String assumedClassName = this.expands.get(reference);
				if (assumedClassName == null) {
					this.expands.put(reference, className);
				}
			} else {
				this.expands.put(reference, className);
			}
			
			//if reference has a container, assume the expansion 
			//of the container recursive closure
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				addExpands(container, null);
			}
		}

		boolean contradictsNull(ReferenceSymbolic reference) {
			//reference must not alias
			for (HashSet<ReferenceSymbolic> aliasSet : this.aliases.values()) {
				if (aliasSet.contains(reference)) {
					return true;
				}
			}
			
			//reference must not expand
			if (this.expands.containsKey(reference)) {
				return true;
			}
			
			//reference must not be previously assumed to be not null
			if (this.notNulls.contains(reference)) {
				return true;
			}
			
			//if reference has a container, then assuming the expansion of the 
			//container must not yield a contradiction 
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				if (contradictsExpands(container, null)) {
					return true;
				}
			}
			
			//all checks ok: reference may be null 
			return false;
		}

		void addNull(ReferenceSymbolic reference) {
			//add reference to nulls
			this.nulls.add(reference);
			
			//if reference has a container, assume the expansion 
			//of the container recursive closure
			if (reference instanceof ReferenceSymbolicMember) {
				final ReferenceSymbolic container = ((ReferenceSymbolicMember) reference).getContainer();
				addExpands(container, null);
			}
		}
	}
	
	private static class ComparatorGeneJBSE implements Comparator<GeneJBSE> {
		@Override
		public int compare(GeneJBSE o1, GeneJBSE o2) {
			final Clause c1 = o1.getClause();
			final Clause c2 = o2.getClause();
			//1- reference clauses come before numeric clauses
			//2- reference clause A comes before reference clause B if A resolves a
			//   container of what B resolves
			if (c1 instanceof ClauseAssume && c2 instanceof ClauseAssumeReferenceSymbolic) {
				return 1;
			} else if (c1 instanceof ClauseAssumeReferenceSymbolic && c2 instanceof ClauseAssume) {
				return -1;
			} else if (c1 instanceof ClauseAssume && c2 instanceof ClauseAssume) {
				return 0;
			} else if (c1 instanceof ClauseAssumeReferenceSymbolic && c2 instanceof ClauseAssumeReferenceSymbolic) {
				final ReferenceSymbolic r1 = ((ClauseAssumeReferenceSymbolic) c1).getReference();
				final ReferenceSymbolic r2 = ((ClauseAssumeReferenceSymbolic) c2).getReference();
				if (r1.equals(r2)) {
					return 0;
				} else if (r1.hasContainer(r2)) {
					return 1;
				} else if (r2.hasContainer(r1)) {
					return -1;
				} else {
					return 0; //uncomparable, really
				}
			} else {
				//this should never happen
				throw new AssertionError("Reached unreachable point: Possibly some unforeseen clause remained in a filtered chromosome.");
			}
		}
	}
}
