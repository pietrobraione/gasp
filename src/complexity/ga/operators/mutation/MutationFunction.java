package complexity.ga.operators.mutation;

import java.util.List;

import complexity.ga.FitnessFunction;
import complexity.ga.Individual;
import complexity.se.Constraint;
import complexity.utils.Config;
import complexity.utils.RandomSingleton;
import complexity.utils.Utils;

public abstract class MutationFunction{

	public static Individual deleteMutation(Individual individual, double ratio) {
		int nTargets = (int) Math.round(ratio * individual.getConstraintSet().size());
        List<Constraint> childConstraints = individual.getConstraintSet();
        for(int i = 0; i < nTargets; i++) {
            int index = RandomSingleton.getInstance().nextInt((childConstraints.size() - 1) + 1);
            childConstraints.remove(index);
            }
        Individual child = FitnessFunction.evaluate(childConstraints);
        return child;
        }
	
	public static Individual negateMutation(Individual individual, double ratio) {
		int nConstraints = individual.getConstraintSet().size();
		int nTargets = (int) Math.round(ratio * nConstraints);
		List<Constraint> childConstraints = individual.getConstraintSet();
		for(int i = RandomSingleton.getInstance().nextInt(nConstraints); i < nTargets; i++) {
			Utils.negate(childConstraints.get(i));
			if(Utils.isInconsistent(childConstraints)) {
				Utils.negate(childConstraints.get(i));
				}
			}
        Individual child = FitnessFunction.evaluate(childConstraints);
        return child;
		}

	public static void deleteMutationBis(List<Constraint> childConstraints, double ratio) {
		int nTargets = (int) Math.round(ratio * childConstraints.size());
        for(int i = 0; i < nTargets; i++){
            int index = RandomSingleton.getInstance().nextInt((childConstraints.size() - 1) + 1);
            childConstraints.remove(index);
            if(RandomSingleton.getInstance().nextBoolean() ? true : false){
                break;
                }
            }
        }
	
	public static void mutationBis(List<Constraint> childConstraints) {
		double ratio = 0.1;
        if(RandomSingleton.getInstance().nextInt() < Config.mutationProb) {
            deleteMutationBis(childConstraints, ratio);
        }
	}
	
	public static Individual mutation(int seed, Individual individual) {
		double ratio = 0.1;
		if(RandomSingleton.getInstance().nextInt(seed) < Config.mutationProb) {
            if(RandomSingleton.getInstance().nextBoolean() ? true : false){
                return deleteMutation(individual, ratio);
            }else {
                return negateMutation(individual, ratio);
                }
            }
        return individual;
        }
}
