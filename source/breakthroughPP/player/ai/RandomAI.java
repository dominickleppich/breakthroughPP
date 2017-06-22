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
public class RandomAI extends AbstractPlayer {
	private Random rnd;

	// ------------------------------------------------------------

	/**
	 * Create a new random ai
	 */
	public RandomAI() {
		rnd = new Random(System.currentTimeMillis());
	}

	/**
	 * Return a random move
	 *
	 * @return Random valid move
	 */
	@Override
	public Move deliver() throws Exception {
		Vector<Move> moves = board.getValidMoves();
		return moves.get(rnd.nextInt(moves.size()));
	}

}
