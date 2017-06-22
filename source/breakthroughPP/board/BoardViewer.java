package breakthroughPP.board;

import breakthroughPP.preset.Move;
import breakthroughPP.preset.Status;
import breakthroughPP.preset.Viewer;

import java.util.Vector;

/**
 * Viewer to a board
 *
 * @author Dominick Leppich
 */
public class BoardViewer implements Viewer {
	private Board board;

	// ------------------------------------------------------------

	/**
	 * Create a new board viewer
	 *
	 * @param board
	 * 		Board
	 */
	public BoardViewer(Board board) {
		this.board = board;
	}

	/**
	 * Get the current player
	 *
	 * @return Color of the current player
	 */
	@Override
	public int turn() {
		return board.getCurrentPlayer();
	}

	/**
	 * Get the X dimension of the board
	 *
	 * @return X dimension
	 */
	@Override
	public int getDimX() {
		return board.getDimX();
	}

	/**
	 * Get the Y dimension of the board
	 *
	 * @return Y dimension
	 */
	@Override
	public int getDimY() {
		return board.getDimY();
	}

	/**
	 * Get the value of a field
	 *
	 * @param letter
	 * 		Letter coordinate
	 * @param number
	 * 		Number coordinate
	 *
	 * @return Value of the field
	 */
	@Override
	public int getColor(int letter, int number) { return board.getField(letter, number); }

	/**
	 * Get the status of the board
	 *
	 * @return Status
	 */
	@Override
	public Status getStatus() {
		return board.getStatus();
	}

	/**
	 * Get a vector of all valid moves for the active color
	 *
	 * @return Vector with all valid moves
	 */
	@Override
	public Vector<Move> getValidMoves() { return board.getValidMoves(); }
}
