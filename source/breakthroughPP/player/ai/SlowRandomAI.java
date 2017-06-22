package breakthroughPP.player.ai;

import breakthroughPP.player.AbstractPlayer;
import breakthroughPP.preset.Move;

import java.util.Random;
import java.util.Vector;

/**
 * Random player class
 *
 * @author Dominick Leppich
 */
public class SlowRandomAI extends AbstractPlayer {
	private Random rnd;
	private int minSleep, maxSleep;

	// ------------------------------------------------------------

	/**
	 * Create a new random ai
	 */
	public SlowRandomAI(int minSleep, int maxSleep) {
		rnd = new Random(System.currentTimeMillis());
		this.minSleep = minSleep;
		this.maxSleep = maxSleep;
	}

	/**
	 * Return a random move
	 *
	 * @return Random valid move
	 */
	@Override
	public Move deliver() throws Exception {
		Vector<Move> moves = board.getValidMoves();
		Thread.sleep(minSleep + rnd.nextInt(maxSleep - minSleep)); 
		return moves.get(rnd.nextInt(moves.size()));
	}

}
