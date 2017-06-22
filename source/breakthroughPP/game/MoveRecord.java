package breakthroughPP.game;

import breakthroughPP.preset.Move;
import breakthroughPP.preset.Status;

/**
 * A class containing all information about a move which was made by a player.
 *
 * @author Dominick Leppich
 */
public class MoveRecord {
	private Move move;
	private Status status;
	private int color;
	private long time;

	// ------------------------------------------------------------

	/**
	 * Create a new move record
	 *
	 * @param move
	 *            Move
	 * @param status
	 *            Status of the move
	 * @param color
	 *            Color of the player
	 * @param time
	 *            Time needed for this move
	 */
	public MoveRecord(Move move, Status status, int color, long time) {
		this.move = move;
		this.status = status;
		this.color = color;
		this.time = time;
	}

	// ------------------------------------------------------------

	/**
	 * Get the move
	 * 
	 * @return Move
	 */
	public Move getMove() {
		return move;
	}

	/**
	 * Get the status of the move
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Get the color of the player
	 * 
	 * @return {@code RED} = 0, {@CODE BLUE} = 1
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Get the time needed for the move (in nanoseconds)
	 * 
	 * @return Time
	 */
	public long getTime() {
		return time;
	}
}
