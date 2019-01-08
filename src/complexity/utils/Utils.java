package complexity.utils;

import java.util.*;

import complexity.ga.Individual;
import complexity.se.Constraint;
import complexity.se.Symex;

public abstract class Utils {
	
	static Symex se = Symex.makeEngine();
	
	public static void conjuncts() {
		//TODO:
	}
	
	public static void toSmt2() {
		//TODO:
	}
	
	public static Constraint negate(Constraint c) {
		return c.mkNot();
	}
			
	public static void assertInRange() {
		//TODO:
	}
	
	public static void assertSorted() {
		//TODO:
	}
	
	public static void assertAllDifferent() {
		//TODO:
	}
	
	public static void assertAllPositive() {
		//TODO:
	}
	
	//Print the input profiles
	public static void ppWcetProfiles(List<Individual> profiles) {
    	for(int i = 0; i < profiles.size(); i++){
        	int id = i + 1;
    		System.out.println("id: " + id);
        	System.out.println("cost: " + profiles.get(i).getFitness());
        	//TODO:
        	//System.out.println("complete: {profile.complete!r}");
        	//System.out.println("path: {profile.path!r}");
        	//System.out.println("pc: {profile.pc!r}");
        	System.out.println(profiles.get(i).getModel());
        	System.out.println();
        	}
	}
	
	//return the negation of the given constraint
	public static Constraint mkNot(Constraint c) {
		return c.mkNot();
	}
	
	public static Constraint mkAnd(List<Constraint> refs) {
		return se.mkAnd(refs);
	}
	
	public static Constraint mkImplies(Constraint c1, Constraint c2) {
		return se.boolRef(c1, c2);
	}
	
	public static void solverAssert() {
		//TODO: (GeneticExecutor)ga_wcet_generator -> solver_assert(solver, pc)
	}
	
	public static boolean isContradiction(Constraint c) {
		return se.quickCheck(c);
	}
	
	public static boolean isTautology(Constraint c) {
		return se.quickCheck(c);
	}
	
	public static boolean isImplied(List<Constraint> constraintSet, Constraint c) {
		return isTautology(mkImplies(mkAnd(constraintSet), c));
	}
	
	public static boolean isInconsistent(List<Constraint> constraintSet) {
		return isContradiction(mkAnd(constraintSet));
	}
}
