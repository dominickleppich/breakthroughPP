package breakthroughPP.game;

import java.rmi.RemoteException;
import java.util.LinkedList;

import breakthroughPP.board.Board;
import breakthroughPP.player.ContestPlayer;
import breakthroughPP.preset.Move;
import breakthroughPP.preset.Setting;
import breakthroughPP.preset.Status;
import eu.nepster.frozencube.utility.time.StopWatch;

/**
 * Match class
 *
 * @author Dominick Leppich
 */
public class Match implements Setting, Runnable {
	// Board display
	private GameDisplay display;

	// Match Thread
	private Thread matchThread;

	// Match board
	protected Board board;

	// Array of players
	private ContestPlayer[] players;

	// List of move records
	private LinkedList<MoveRecord> moveRecords;

	// Was it the first move made?
	private boolean firstMove = true;
	// active = true: Game is not paused
	private boolean active;
	private boolean running = true;

	private StopWatch stepWatch, cycleWatch;
	private TimeControlThread controlThread;
	private long[] timeUsed;

	// ------------------------------------------------------------

	/**
	 * Create a new match
	 *
	 * @param red
	 *            Red player
	 * @param blue
	 *            Blue player
	 * @param dimX
	 *            Board x dimension
	 * @param dimY
	 *            Board y dimension
	 */
	public Match(ContestPlayer red, ContestPlayer blue, int dimX, int dimY, GameDisplay display)
			throws RemoteException, Exception {
		// Init board
		board = new Board();
		board.reset(dimX, dimY);

		// Init players
		players = new ContestPlayer[2];
		players[0] = red;
		players[1] = blue;

		// Hacky huhm?
		for (int i = 0; i < 2; i++)
			players[i].getPlayer().init(dimX, dimY, i);

		// Set display
		this.display = display;
		this.display.setViewer(board.viewer());

		// Create list for move records
		moveRecords = new LinkedList<MoveRecord>();

		stepWatch = new StopWatch();
		cycleWatch = new StopWatch();
		// Create time control thread
		controlThread = new TimeControlThread(this, display);
		timeUsed = new long[2];
		// Player starts with no time used
		timeUsed[0] = 0L;
		timeUsed[1] = 0L;

		// Create and start match thread
		matchThread = new Thread(this);
		matchThread.start();

		Game.getLogger().info(this, "Match created");
	}

	// ------------------------------------------------------------

	/**
	 * Start the match
	 */
	public void startMatch() {
		if (board == null)
			throw new RuntimeException("Set board before starting the match!");

		active = true;
		synchronized (matchThread) {
			matchThread.notify();
		}

		Game.getLogger().info(this, "Match started");
	}

	/**
	 * Pause the game
	 */
	public void pauseMatch() {
		active = false;

		Game.getLogger().info(this, "Match paused");
	}

	/**
	 * Stop match (the brutal way)
	 */
	@SuppressWarnings("deprecation")
	public void stopMatch() {
		matchThread.interrupt();
		matchThread.stop();

		Game.getLogger().info(this, "Match stopped");
	}

	/**
	 * Wait for the match to finish
	 */
	public synchronized void waitMatchEnd() {
		try {
			Game.getLogger().info(this, "Waiting for match to end");
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Wake up waiting threads
	 */
	private synchronized void wakeUpWaitingThreads() {
		Game.getLogger().info(this, "Waking up waiting threads");
		notify();
	}

	// ------------------------------------------------------------

	/**
	 * Main loop
	 */
	public void run() {
		int activeColor = RED;

		// Loop while the game is not over and no error occured
		while (running && !matchThread.isInterrupted()) {
			// Game is not paused
			if (active) {
				try {
					// Update before first move
					if (firstMove) {
						display.update(null, false, null);
						firstMove = false;
					}

					// Request a move message TODO
					// IO.println(Language.get("player_move_request",
					// playerName));
					cycleWatch.start();

					int currentPlayer = board.getCurrentPlayer();
					int opponentPlayer = 1 - currentPlayer;

					activeColor = currentPlayer;
					controlThread.start(timeUsed[activeColor], activeColor);
					stepWatch.start();
					Move move = players[activeColor].getPlayer().request();
					stepWatch.stop();
					timeUsed[activeColor] = controlThread.stop();
					Game.getLogger().info(this, "Move " + move + " requested in " + stepWatch);

					// Make move on board
					board.make(move);

					// get Status
					Status status = board.getStatus();

					// Save the move record
					moveRecords.addLast(new MoveRecord(move, status, currentPlayer, stepWatch.delta()));

					Game.getLogger().info(this, "Move " + move + " was made with status " + status);

					// Confirm move
					activeColor = currentPlayer;
					controlThread.start(timeUsed[activeColor], activeColor);
					stepWatch.start();
					players[activeColor].getPlayer().confirm(status);
					stepWatch.stop();
					timeUsed[activeColor] = controlThread.stop();
					Game.getLogger().info(this, "Move " + move + " was confirmed in " + stepWatch);

					// Update opponent
					activeColor = opponentPlayer;
					controlThread.start(timeUsed[activeColor], activeColor);
					stepWatch.start();
					players[activeColor].getPlayer().update(move, status);
					stepWatch.stop();
					timeUsed[activeColor] = controlThread.stop();
					Game.getLogger().info(this, "Move " + move + " was updated in " + stepWatch);

					cycleWatch.stop();
					Game.getLogger().info(this, "Match cycle passed in " + cycleWatch);
					
					// Update display
					display.update(move, board.wasKicked(), status);

					// Timeout after move
					int sleepTime = (int) (Game.getSettings().get("move_timeout").getInteger()
							- cycleWatch.delta() / 1000000);
					if (sleepTime > 0) {
						Game.getLogger().info(this, "Sleeping for " + sleepTime + "ms");
						Thread.sleep(sleepTime);
					}
				} catch (InterruptedException e) {
					Game.getLogger().error(this, "Unable to timeout after move");
					e.printStackTrace();
				} catch (RemoteException e) {
					Game.getLogger().error(this, "Remote exception");
					e.printStackTrace();
				} catch (Exception e) {
					Game.getLogger().error(this, "Exception");
					e.printStackTrace();
					// A player throws an exception, opponent wins
					win(activeColor == RED ? BLUE : RED, "Exception");
				}
			}
			// Thread needs to wait until something happens
			else {
				synchronized (matchThread) {
					try {
						matchThread.wait();
					} catch (InterruptedException e) {
						Game.getLogger().error(this, "Error pausing match");
						e.printStackTrace();
					}
				}
			}

			// Check if the game situation has changed
			switch (board.getStatus().getStatus()) {
			case RED_WIN:
				win(RED, "Normal win");
				break;
			case BLUE_WIN:
				win(BLUE, "Normal win");
				break;
			}
		}
		wakeUpWaitingThreads();
	}

	// ------------------------------------------------------------

	/**
	 * Set the winner of the game
	 * 
	 * @param color
	 *            Color of the winning player
	 * @param reason
	 *            Reason of the win
	 */
	@SuppressWarnings("deprecation")
	public void win(int color, String reason) {
		running = false;
		Game.getLogger().info(this, "" + players[color].getName() + " won the game [" + reason + "]");
		controlThread.exit();
		matchThread.stop();
	}
}
