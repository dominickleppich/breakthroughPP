package breakthroughPP.game;

import breakthroughPP.preset.Move;
import breakthroughPP.preset.Status;
import breakthroughPP.preset.Viewer;

/**
 * Interface which implements methods to display the board with all important information after each update
 *
 * @author Dominick Leppich
 */
public interface GameDisplay {
	/**
	 * Set the viewer of the display
	 *
	 * @param viewer
	 * 		Viewer
	 */
	void setViewer(Viewer viewer);

	/**
	 * Update the board display with the made move
	 *
	 * @param move
	 * 		Move
	 * @param kicked
	 * 		true, if a token was kicked
	 * @param status
	 * 		Status of the move
	 */
	void update(Move move, boolean kicked, Status status);

	/**
	 * Enables or disables the display input
	 *
	 * @param state
	 * 		true, if the display input should be enabled
	 */
	void setInputEnabled(boolean state);

	/**
	 * Set the amount of time the player used
	 *
	 * @param color
	 * 		Color of the player
	 * @param current
	 * 		Current amount of time used
	 * @param max
	 * 		Maximum player time available
	 */
	void setPlayerTime(int color, long current, long max);

	/**
	 * Set the amount of move time used
	 *
	 * @param current
	 * 		Current amount of time used
	 * @param max
	 * 		Maximum move time available
	 */
	void setMoveTime(long current, long max);
}
