package breakthroughPP.player;

import breakthroughPP.preset.Move;
import breakthroughPP.preset.Requestable;
import breakthroughPP.preset.Status;

import java.rmi.RemoteException;

/**
 * Human player
 *
 * @author Dominick Leppich
 */
public class HumanPlayer extends AbstractPlayer {
	// Object to request moves
	private Requestable requestable;
	// TODO ask only human players for moves

	// ------------------------------------------------------------

	/**
	 * Create new human player
	 *
	 * @param requestable
	 * 		Object to request moves
	 */
	public HumanPlayer(Requestable requestable) {
		this.requestable = requestable;
	}

	// ------------------------------------------------------------

	/**
	 * Request a move from the requestable
	 *
	 * @return Move
	 */
	@Override
	public Move deliver() throws Exception {
		return requestable.deliver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(Status boardStatus) throws RemoteException, Exception {
		super.confirm(boardStatus);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws RemoteException, Exception {
		super.update(opponentMove, boardStatus);
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(int dimX, int dimY, int color) throws RemoteException, Exception {
		super.init(dimX, dimY, color);
	}

}
