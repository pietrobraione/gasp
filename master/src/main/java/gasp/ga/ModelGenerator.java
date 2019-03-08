package gasp.ga;

public interface ModelGenerator<T extends Gene<T>, U extends Individual<T>,  R extends Model<T>> {
	R generateModel(U individual);
}
