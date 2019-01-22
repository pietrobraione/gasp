package gasp.ga;

import java.util.ArrayList;
import java.util.List;

public class Individual<T extends Gene<T>> implements Cloneable, Comparable<Individual<T>> {
	private final List<T> chromosome;
	private final int fitness;
	private final String toString;
	
	public Individual(List<T> chromosome, int fitness) {
		if (chromosome == null || chromosome.isEmpty()) {
			throw new IllegalArgumentException("The chromosome can't be null or an empty list of genes.");
		}
		
		this.chromosome = new ArrayList<>(chromosome);
		this.fitness = fitness;
		this.toString = "Individual [fitness = " + this.fitness + ", chromosome = " + this.chromosome + "]";
	}

	public List<T> getChromosome() {
		return new ArrayList<T>(this.chromosome);
	}

	public int size() {
		return this.chromosome.size();
	}

	public int getFitness() {
		return this.fitness;
	}

	public Model<T> getModel() {
		return new Model<T>(this);
	}
	
	@Override
	public String toString() {
		return this.toString;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Individual<T> clone() {
    	try {
    		return (Individual<T>) super.clone();
    	} catch (CloneNotSupportedException e) {
    		throw new AssertionError("Unexpected failure of clone method.", e);
    	}
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.chromosome.hashCode();
		result = prime * result + this.fitness;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final Individual<T> other = (Individual<T>) obj;
		if (!this.chromosome.equals(other.chromosome)) {
			return false;
		}
		if (this.fitness != other.fitness) {
			return false;
		}
		
		return true;
	}

	@Override
	public int compareTo(Individual<T> other) {
		if (this.fitness > other.fitness) {
			return -1;
		} else if (this.fitness == other.fitness) {
			return 0;
		} else {
			return 1;
		}
	}
}
