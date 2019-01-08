package complexity.utils;

import java.util.Random;

public class RandomSingleton {
	private static RandomSingleton instance;
	private Random rng;

    private RandomSingleton() {
        rng = new Random();
		rng.setSeed(Config.seed);        
    }

    public static RandomSingleton getInstance() {
        if(instance == null) {
            instance = new RandomSingleton();
        }
        return instance;
    }

    public int nextInt() {
         return rng.nextInt();
    }
    
    public int nextInt(int bound) {
    		return rng.nextInt(bound);
    }
    
	public boolean nextBoolean() {
		return rng.nextBoolean();
	}
    
}
