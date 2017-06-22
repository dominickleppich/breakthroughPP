package breakthroughPP.player;

import breakthroughPP.board.Board;
import breakthroughPP.preset.Move;
import breakthroughPP.preset.Player;
import breakthroughPP.preset.Setting;
import breakthroughPP.preset.Status;

import java.rmi.RemoteException;

/**
 * Abstract player class
 *
 * @author Dominick Leppich
 */
public abstract class AbstractPlayer implements Setting, Player {
	public static final int NONE = -1;
	public static final int REQUEST = 0;
	public static final int CONFIRM = 1;
	public static final int UPDATE = 2;

	public static final String[] callString = {"REQUEST", "CONFIRM", "UPDATE"};

	// ------------------------------------------------------------

	protected Board board;
	protected int color;

	private int expectedCall;

	private Move lastMove;

	// ------------------------------------------------------------

	/**
	 * Create a new player and create a board for it
	 */
	public AbstractPlayer() {
		board = new Board();
		expectedCall = NONE;
	}

	// ------------------------------------------------------------

	/**
	 * Set the player board (is needed for loading)
	 *
	 * @param board
	 * 		Board
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception, RemoteException {
		// IO.debugln("Player " + this + " REQUEST @ AbstractPlayer.request");
		if (expectedCall == NONE) throw new Exception("Player needs to be resetted before usage!");
		if (expectedCall != REQUEST) throw new Exception("Wrong call order! Expected: REQUEST");

		lastMove = deliver();
		// IO.debugln("Player sent " + lastMove + " @ AbstractPlayer.request");

		setExpectedCall(CONFIRM);
		return lastMove;
	}

	/**
	 * Get a move from the subclass of this player.
	 *
	 * @return Move
	 */
	public abstract Move deliver() throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(Status boardStatus) throws Exception, RemoteException {
		// IO.debugln("Player " + this + " CONFIRM @ AbstractPlayer.confirm");
		if (expectedCall == NONE) throw new Exception("Player needs to be resetted before usage!");
		if (expectedCall != CONFIRM) throw new Exception("Wrong call order! Expected: CONFIRM");

		// Make the requested move on the board
		board.make(lastMove);

		// compare own board status with received status
		if (!board.getStatus().equals(boardStatus))
			throw new Exception("Game board status " + boardStatus + " and player board status " + board.getStatus() +
					" mismatch!");

		setExpectedCall(UPDATE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		// IO.debugln("Player " + this + " UPDATE @ AbstractPlayer.update");
		if (expectedCall == NONE) throw new Exception("Player needs to be resetted before usage!");
		if (expectedCall != UPDATE) throw new Exception("Wrong call order! Expected: UPDATE");

		// Make opponents move on board
		board.make(opponentMove);

		// compare own board status with received status
		if (!board.getStatus().equals(boardStatus))
			throw new Exception("Game board status " + boardStatus + " and player board status " + board.getStatus() +
					" mismatch!");

		setExpectedCall(REQUEST);
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(int dimX, int dimY, int color) throws Exception, RemoteException {
		this.color = color;

		board.reset(dimX, dimY);

		setExpectedCall(color == RED ? REQUEST : UPDATE);

		// IO.debugln("Resetted player " + this + " @ AbstractPlayer.reset");
	}

	/**
	 * Set the next expected call
	 *
	 * @param call
	 * 		Next expected call
	 */
	private void setExpectedCall(int call) {
		expectedCall = call;
		//		IO.debugln("Next expected call for player " + this + " is " + callString[call]
		//				+ " @ AbstractPlayer.setExpectedCall");
	}

}
