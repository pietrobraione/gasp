package gasp.ga.individualGenerator;

import java.util.List;

import gasp.ga.Individual;

public final class IndividualJBSE extends Individual<GeneJBSE> {
	private final String pathIdentifier;
	private final int hashCode;

	public IndividualJBSE(List<GeneJBSE> chromosome, long fitness, String pathIdentifier) {
		super(chromosome, fitness);
		
		if (pathIdentifier == null) {
			throw new IllegalArgumentException("The path identifier can't be null.");
		}
		
		this.pathIdentifier = pathIdentifier;
		
		//hashCode
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.pathIdentifier.hashCode();
		this.hashCode = result;
	}
	
	public String getPathIdentifier() {
		return this.pathIdentifier;
	}
	
	@Override
	public IndividualJBSE clone() {
		return (IndividualJBSE) super.clone();
	}
	
	@Override
	public String toString() {
		return super.toString(); //we don't show the path identifier
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final IndividualJBSE other = (IndividualJBSE) obj;
		if (!this.pathIdentifier.equals(other.pathIdentifier)) {
			return false;
		}
		return true;
	}

}
