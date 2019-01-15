package gasp.utils;

import java.util.Random;

public class RandomNumberSupplier {
	private static RandomNumberSupplier instance;
	private Random rng;

    private RandomNumberSupplier() {
        rng = new Random();
		rng.setSeed(Config.seed);        
    }

    public static RandomNumberSupplier _I() {
        if(instance == null) {
            instance = new RandomNumberSupplier();
        }
        return instance;
    }

    public double nextDouble() {
         return rng.nextDouble();
    }
    
    public int nextInt(int bound) {
    		return rng.nextInt(bound);
    }
    
	public boolean nextBoolean() {
		return rng.nextBoolean();
	}
    
}
