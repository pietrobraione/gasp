package gasp.ga;

import java.util.ArrayList;
import java.util.List;

public class Individual<T extends Gene<T>> implements Cloneable, Comparable<Individual<T>> {
	private final List<T> chromosome;
	private final long fitness;
	private final String toString;
	private final int hashCode;
	
	public Individual(List<T> chromosome, long fitness) {
		if (chromosome == null || chromosome.isEmpty()) {
			throw new IllegalArgumentException("The chromosome can't be null or an empty list of genes.");
		}
		
		this.chromosome = new ArrayList<>(chromosome);
		this.fitness = fitness;
		this.toString = "Individual [fitness = " + this.fitness + ", chromosome = " + this.chromosome + "]";
		
		//hashCode
		final int prime = 31;
		int result = 1;
		result = prime * result + this.chromosome.hashCode();
		result = prime * result + (int) (fitness ^ (fitness >>> 32));
		this.hashCode = result;
	}

	public List<T> getChromosome() {
		return new ArrayList<T>(this.chromosome);
	}

	public int size() {
		return this.chromosome.size();
	}

	public long getFitness() {
		return this.fitness;
	}

	@Override
	public String toString() {
		return this.toString;
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
		return this.hashCode;
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
}
