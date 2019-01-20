package gasp.ga;

public class GeneStub extends Gene<GeneStub> {
	private static final String CHARS = "abcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyzabcdefghijklmnopkrstuvwxyz";
	private static int nextChar = 0;

	private final String theConstraint; 
	
	public GeneStub() {
		this.theConstraint = CHARS.charAt(nextChar++ % CHARS.length() ) + " > 0";
	}

	public GeneStub(String constraint) {
		this.theConstraint = constraint;
	}

	@Override
	public GeneStub not() {
		return new GeneStub("not (" + this.theConstraint + ")");
	}

	@Override
	public String toString() {
		return this.theConstraint;
	}


	//TODO equals, hashCode
}
