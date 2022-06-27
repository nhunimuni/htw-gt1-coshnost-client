package util;
import java.util.Random;

import lenz.htw.coshnost.world.GraphNode;

public class RandomMove {

	public RandomMove() {}

	/**
	 * Gets a random position on the board baseline.
	 * 
	 * @param player
	 * @return
	 */
	public GraphNode getRandomDirection() {
		GraphNode direction = null;
		return direction;
	}

	/**
	 * Get a random number from 1 to 6.
	 * 
	 * @return
	 */
	private int getRandomNumber() {
		int min = 0;
		int max = 9;
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
}
