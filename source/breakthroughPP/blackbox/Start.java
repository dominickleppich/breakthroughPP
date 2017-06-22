package breakthroughPP.blackbox;

import breakthroughPP.preset.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

/**
 * Board class of this game. All rules are implemented here. You can create boards of different sizes with 2 <= dimX <=
 * 26 and 6 <= dimY <= 26. The board has a method {@code getValidMoves()} to get a vector of all possible moves for the
 * current player. You can make moves on this board by calling the {@code make} method. Once one player won you cannot
 * any moves. Once an illegal moves was sent, the board status is {@code ILLEGAL} and it doesn't accept any more moves
 * until the board is reseted.
 *
 * @author Dominick Leppich
 */
public class Start implements Setting {
	private Registry registry;

	// ------------------------------

	public static void main(String[] args) {
		new Start(args);
	}

	public Start(String args[]) {
		if (args.length != 1 && args.length != 4) {
			System.out.println("Usage: java -jar breakthroughPP-blackbox.jar <Mode> (XDim) (YDim) (Rnd-color)");
			System.out.println("------------------------------\n" +
					"<Mode>\t\t- [host / guest]\t- host:  Blackbox is hosting a game and using a network player as ai " +

					"(you have to register your ai to a local rmi)\n" +
					"\t\t\t\t\t  guest: Blackbox can be used as second player in your main game (random player is " +
                    "registered to a local rmi with name \"RANDOM\")\n" +
					"(XDim)\t\t- [2 - 26]\t\t- Number of letters (columns) (only needed in \"host\" mode)\n" +
					"(YDim)\t\t- [6 - 26]\t\t- Number of numbers (rows) (only needed in \"host\" mode)\n" +
					"(Rnd-color)\t- [red / blue]\t\t- Set the color of the random player (only needed in \"host\" " +
                    "mode)\n");

			System.exit(1);
		}

		if (args[0].equals("host")) {
			if (args.length != 4) {
				System.out.println("Arguments missing!");
				System.exit(1);
			}
			int dimX = Integer.parseInt(args[1]);
			int dimY = Integer.parseInt(args[2]);
			boolean redRandom = true;
			if (args[3].equals("red")) ;
			else if (args[3].equals("blue")) redRandom = false;
			else {
				System.err.println("Random player has to be \"red\" or \"blue\"!");
				System.exit(1);
			}
			waitForSecondPlayer(redRandom, dimX, dimY);
		} else if (args[0].equals("guest")) {
			// Register random ai to rmi
			try {
				System.out.println("Starting local rmi registry on port 1099");
				registry = LocateRegistry.createRegistry(1099);
				System.out.println("Offering RANDOM player on rmi registry...");
				registry.rebind("RANDOM", new NetworkPlayer(new RandomAI(true)));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Mode hast to be \"host\" or \"guest\"!");
		}
	}

	private void waitForSecondPlayer(boolean redRandom, int dimX, int dimY) {
		String foundPlayer = null;
		System.out.println("Waiting for network player...");
		while (foundPlayer == null) {
			try {
				registry = LocateRegistry.getRegistry(1099);
				String[] players = registry.list();
				if (players.length > 0) foundPlayer = players[0];
				else Thread.sleep(1000);
			} catch (RemoteException e) { } catch (InterruptedException e) { }
		}
		System.out.println("Found player: " + foundPlayer);
		// Load player
		try {
			Player ai = (Player) registry.lookup(foundPlayer);
			Player rnd = new RandomAI(true);
			if (redRandom) playGame(rnd, ai, dimX, dimY);
			else playGame(ai, rnd, dimX, dimY);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void playGame(Player red, Player blue, int dimX, int dimY) {
		try {
			red.init(dimX, dimY, RED);
			blue.init(dimX, dimY, BLUE);
		} catch (Exception e) {
			System.err.println("Failed to init player!");
			e.printStackTrace();
			System.exit(1);
		}

		Board board = new Board();
		try {
			board.reset(dimX, dimY);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		boolean isRed = true;
		while (board.getStatus().isOk()) {
			Move move = null;
			try {
				move = (isRed ? red.request() : blue.request());
			} catch (Exception e) {
				System.err.println("Unable to request move from player!");
				e.printStackTrace();
				System.exit(1);
			}

			// Make move on board
			board.make(move);

			// Confirm move
			try {
				if (isRed) red.confirm(board.getStatus());
				else blue.confirm(board.getStatus());
			} catch (Exception e) {
				System.err.println("Unable to confirm move!");
				e.printStackTrace();
				System.exit(1);
			}

			// Update move
			try {
				if (isRed) blue.update(move, board.getStatus());
				else red.update(move, board.getStatus());
			} catch (Exception e) {
				System.err.println("Unable to update move!");
				e.printStackTrace();
				System.exit(1);
			}

			if (board.getStatus().isIllegal()) {
				System.out.println("Move " + move + " is illegal. Analyzing move...");
				switch (board.analyzeMove(move)) {
					case Board.MOVE_START_OUT_OF_BOUNDS:
						System.out.println("Moves start position is not on the board!");
						break;
					case Board.MOVE_END_OUT_OF_BOUNDS:
						System.out.println("Moves end position is not on the board!");
						break;
					case Board.MOVE_START_NO_TOKEN:
						System.out.println("There is no token at moves start position!");
						break;
					case Board.MOVE_START_ENEMY_TOKEN:
						System.out.println("The token at start position belongs to the opponent!");
						break;
					case Board.MOVE_KICKED_OWN_TOKEN:
						System.out.println("You tried to kick your own token!");
						break;
					case Board.MOVE_KICKED_FORWARD:
						System.out.println("You tried to kick a token in front of you!");
						break;
					case Board.MOVE_WRONG_DIRECTION:
						System.out.println("You made a move in the wrong direction!");
						break;
					case Board.MOVE_ON_SAME_ROW:
						System.out.println("Your move is on the same row!");
						break;
					case Board.MOVE_TOO_FAR:
						System.out.println("You tried to move too far!");
						break;
					case Board.MOVE_UNKNOWN_ERROR:
					default:
						System.out.println("Something weird happened! Report this move to Dominick Leppich!!!");
				}
				System.exit(1);
			}

			// swap players
			isRed = !isRed;
		}
		System.exit(0);
	}

	// ------------------------------------------------------------

	class NetworkPlayer extends UnicastRemoteObject implements Player {
		private static final long serialVersionUID = 1L;

		// ------------------------------------------------------------

		// Reference to real player to play with
		private Player player;

		// ------------------------------------------------------------

		/**
		 * Create a network player given a local player
		 *
		 * @param player
		 * 		Player to offer to the network
		 */
		public NetworkPlayer(Player player) throws RemoteException {
			super();
			this.player = player;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Move request() throws Exception, RemoteException {
			return player.request();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void confirm(Status boardStatus) throws Exception, RemoteException {
			player.confirm(boardStatus);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
			player.update(opponentMove, boardStatus);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void init(int dimX, int dimY, int color) throws Exception, RemoteException {
			player.init(dimX, dimY, color);
		}
	}

	abstract class AbstractPlayer implements Setting, Player {
		public static final int NONE = -1;
		public static final int REQUEST = 0;
		public static final int CONFIRM = 1;
		public static final int UPDATE = 2;

		public final String[] callString = {"REQUEST", "CONFIRM", "UPDATE"};

		// ------------------------------------------------------------

		protected Board board;
		protected int color;
		private boolean showBoard;

		private int expectedCall;

		private Move lastMove;

		// ------------------------------------------------------------

		/**
		 * Create a new player and create a board for it
		 */
		public AbstractPlayer(boolean showBoard) {
			this.showBoard = showBoard;
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
			System.out.println("Move requested: " + lastMove);

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
				throw new Exception("Game board status " + boardStatus + " and player board status " + board.getStatus
                        () + " mismatch!");
			System.out.println("Move confirmed, status: " + boardStatus);
			if (showBoard) System.out.println(board);
			System.out.println("--------------------------------------------------------");

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
				throw new Exception("Game board status " + boardStatus + " and player board status " + board.getStatus
                        () + " mismatch!");
			System.out.println("Opponent made move: " + opponentMove + ", status: " + boardStatus);
			if (showBoard) System.out.println(board);
			System.out.println("--------------------------------------------------------");
			if (board.getStatus().isIllegal()) {
				System.out.println("Move " + opponentMove + " is illegal. Analyzing move...");
				switch (board.analyzeMove(opponentMove)) {
					case Board.MOVE_START_OUT_OF_BOUNDS:
						System.out.println("Moves start position is not on the board!");
						break;
					case Board.MOVE_END_OUT_OF_BOUNDS:
						System.out.println("Moves end position is not on the board!");
						break;
					case Board.MOVE_START_NO_TOKEN:
						System.out.println("There is no token at moves start position!");
						break;
					case Board.MOVE_START_ENEMY_TOKEN:
						System.out.println("The token at start position belongs to the opponent!");
						break;
					case Board.MOVE_KICKED_OWN_TOKEN:
						System.out.println("You tried to kick your own token!");
						break;
					case Board.MOVE_KICKED_FORWARD:
						System.out.println("You tried to kick a token in front of you!");
						break;
					case Board.MOVE_WRONG_DIRECTION:
						System.out.println("You made a move in the wrong direction!");
						break;
					case Board.MOVE_ON_SAME_ROW:
						System.out.println("Your move is on the same row!");
						break;
					case Board.MOVE_TOO_FAR:
						System.out.println("You tried to move too far!");
						break;
					case Board.MOVE_UNKNOWN_ERROR:
					default:
						System.out.println("Something weird happened! Report this move to Dominick Leppich!!!");
				}
			}

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
			System.out.println("Player initialized, dimX: " + dimX + ", dimY: " + dimY + ", color: " + (color == RED ?
                    "red" : "blue"));
			System.out.println("--------------------------------------------------------");
			if (showBoard) System.out.println(board);
			System.out.println("--------------------------------------------------------");
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

	public class RandomAI extends AbstractPlayer {
		private Random rnd;

		// ------------------------------------------------------------

		/**
		 * Create a new random ai
		 */
		public RandomAI(boolean showBoard) {
			super(showBoard);
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

	// ------------------------------------------------------------

	class Board implements Setting {
		public static final int MOVE_OK = 0;
		public static final int MOVE_SURRENDER = 1;
		public static final int MOVE_START_OUT_OF_BOUNDS = 2;
		public static final int MOVE_END_OUT_OF_BOUNDS = 3;
		public static final int MOVE_START_NO_TOKEN = 4;
		public static final int MOVE_START_ENEMY_TOKEN = 5;
		public static final int MOVE_KICKED_OWN_TOKEN = 6;
		public static final int MOVE_WRONG_DIRECTION = 7;
		public static final int MOVE_ON_SAME_ROW = 8;
		public static final int MOVE_KICKED_FORWARD = 9;
		public static final int MOVE_TOO_FAR = 10;
		public static final int MOVE_UNKNOWN_ERROR = 11;

		// ------------------------------------------------------------

		private int[][] board = null;
		private int dimX, dimY;

		// Status of the board
		private Status status;
		// Current player
		private int currentPlayer;
		// Was a token kicked in the last move?
		private boolean kicked = false;
		// Set of valid moves
		private HashSet<Move> redMoves, blueMoves;

		// ------------------------------------------------------------

		/**
		 * Creates a new board. The board has to be resetted in order to use it!
		 */
		public Board() {
			status = new Status(OK);
			redMoves = new HashSet<Move>();
			blueMoves = new HashSet<Move>();
		}

		// ------------------------------------------------------------

		/**
		 * Reset the board to the new size and set it to start positions
		 *
		 * @param dimX
		 * 		X-dimension
		 * @param dimY
		 * 		Y-dimension
		 */
		public void reset(int dimX, int dimY) throws Exception {
			if (dimX < 2 || dimX > 26 || dimY < 6 || dimY > 26) throw new Exception("Board dimension wrong");

			board = new int[dimX][dimY];
			this.dimX = dimX;
			this.dimY = dimY;

			currentPlayer = RED;
			status.setStatus(OK);

			// Reset valid moves
			redMoves.clear();
			blueMoves.clear();

			init();
		}

		/**
		 * Set the board to the start position
		 */
		private void init() {
			// Set all empty
			for (int l = 0; l < dimX; l++)
				for (int n = 0; n < dimY; n++)
					board[l][n] = NONE;

			try {
				// Set the red and blue tokens
				int R = (dimY + 3) / 4;
				for (int l = 0; l < dimX; l++) {
					for (int i = 0; i < R; i++) {
						// Red tokens
						board[l][i] = RED;
						// Blue tokens
						board[l][dimY - i - 1] = BLUE;

						// Add possible moves
						if (i == R - 1) {
							// In a line
							redMoves.add(new Move(new Position(l, i), new Position(l, i + 1)));
							blueMoves.add(new Move(new Position(l, dimY - i - 1), new Position(l, dimY - i - 2)));

							// Diagonal if possible
							if (l > 0) {
								redMoves.add(new Move(new Position(l, i), new Position(l - 1, i + 1)));
								blueMoves.add(new Move(new Position(l, dimY - i - 1), new Position(l - 1, dimY - i - 2)));

							}
							if (l < dimX - 1) {
								redMoves.add(new Move(new Position(l, i), new Position(l + 1, i + 1)));
								blueMoves.add(new Move(new Position(l, dimY - i - 1), new Position(l + 1, dimY - i -
										2)));
							}
						}
					}
				}
			} catch (PresetException e) {
				e.printStackTrace();
			}
		}

		// ------------------------------------------------------------

		/**
		 * Get the X-dimension of the board
		 *
		 * @return X-dimension
		 */
		public int getDimX() {
			return dimX;
		}

		/**
		 * Get the Y-dimension of the board
		 *
		 * @return Y-dimension
		 */
		public int getDimY() {
			return dimY;
		}

		/**
		 * Get the value of a given field
		 *
		 * @param letter
		 * 		Letter
		 * @param number
		 * 		Number
		 *
		 * @return Field value
		 */
		public synchronized int getField(int letter, int number) {
			if (letter < 0 || letter >= dimX || number < 0 || number >= dimY)
				throw new IndexOutOfBoundsException("letter or number out of bounds!");

			return board[letter][number];
		}

		/**
		 * Get the color of the current player
		 *
		 * @return Current player
		 */
		public synchronized int getCurrentPlayer() {
			return currentPlayer;
		}

		/**
		 * Get the status of the board
		 *
		 * @return Status
		 */
		public synchronized Status getStatus() {
			return new Status(status);
		}

		// ------------------------------------------------------------

		/**
		 * Get a vector of all possible moves for the current player
		 *
		 * @return All possible moves
		 */
		public synchronized Vector<Move> getValidMoves() {
			Vector<Move> res = new Vector<Move>();

			for (Move m : currentPlayer == RED ? redMoves : blueMoves)
				res.addElement(new Move(m));

			return res;
		}

		/**
		 * Get the status of the board after this move would have been done
		 *
		 * @param move
		 * 		Move
		 *
		 * @return Status of the board after this move (it will not be done)
		 */
		public synchronized Status getMoveStatus(Move move) {
			if (!isValidMove(move)) return new Status(ILLEGAL);
			return new Status(calculateNextStatus(move));
		}

		/**
		 * Check if a token was kicked in the last move
		 *
		 * @return Token was kicked in the last move
		 */
		public synchronized boolean wasKicked() {
			return kicked;
		}

		/**
		 * Make a move on the board
		 *
		 * @param move
		 * 		Move
		 *
		 * @return Move was made
		 */
		public synchronized boolean make(Move move) {
			// Check if the game is still running
			if (!status.isOk()) return false;

			// Check if player wants to surrender
			if (move == null) {
				if (currentPlayer == RED) status.setStatus(BLUE_WIN);
				else status.setStatus(RED_WIN);
				return true;
			}

			// Check the move. If it was not valid, the board status will be illegal
			if (!isValidMove(move)) {
				status.setStatus(ILLEGAL);
				return false;
			}

			// Everything was okay :) Update the board
			updateBoard(move);

			// Check for a win situation
			status.setStatus(calculateNextStatus(move));

			nextPlayer();

			return true;
		}

		/**
		 * Update the board after the move was made. Move token and update possible moves for both players.
		 * <h1>Strategy</h1> <p> Check whether the move kicks an enemy or not and save the result in the boolean flag
		 * {@code kicked}. Perform the move on the board.</p> <p>Determine which set of moves belong to own tokens and
		 * which one to enemy ones. </p> <p> Update the sets of possible moves using these rules: <ul> <li>Remove all
		 * moves with the active token, which were possible before this move</li> <li>Remove all moves which ended at
		 * the end position of the active move (these ones are blocked now)</li> <li>If an enemy was kicked, remove all
		 * possible moves of the kicked token</li> <li>Add all new moves, the active token is able to make after this
		 * move.</li> <li>In case no enemy was kicked (the move was non-diagonal) it is possible that an enemy is
		 * blocked now, remove that move if existing</li> <li>Add all moves which were not possible before and are
		 * possible now (because the start position of the move blocked them)</li> </ul> </p>
		 *
		 * @param move
		 * 		Move
		 */
		private synchronized void updateBoard(Move move) {
			// Determine start and end positions
			Position start = move.getStart();
			Position end = move.getEnd();

			// Determine start and end letters and numbers
			int startLetter, endLetter, startNumber, endNumber;
			startLetter = start.getLetter();
			endLetter = end.getLetter();
			startNumber = start.getNumber();
			endNumber = end.getNumber();

			// Move token on the board and kick end position if needed
			int token = board[startLetter][startNumber];
			board[startLetter][startNumber] = NONE;
			kicked = board[endLetter][endNumber] != NONE;
			board[endLetter][endNumber] = token;

			// Determine own and enemy set of valid moves
			HashSet<Move> own, enemy;
			if (currentPlayer == RED) {
				own = redMoves;
				enemy = blueMoves;
			} else {
				own = blueMoves;
				enemy = redMoves;
			}

			try {
				// Removes all moves with the active token
				removeMove(own, new Move(start, new Position(startLetter, endNumber)));
				if (startLetter > 0) removeMove(own, new Move(start, new Position(startLetter - 1, endNumber)));
				if (startLetter < dimX - 1) removeMove(own, new Move(start, new Position(startLetter + 1, endNumber)));

				// Remove moves, which are blocked now
				removeMove(own, new Move(new Position(endLetter, startNumber), end));
				if (endLetter > 0) removeMove(own, new Move(new Position(endLetter - 1, startNumber), end));
				if (endLetter < dimX - 1) removeMove(own, new Move(new Position(endLetter + 1, startNumber), end));

				// If an enemy token was kicked, remove all moves with this token
				if (kicked) {
					// Delete all moves starting at the end position
					removeMove(enemy, new Move(end, new Position(endLetter, startNumber)));
					if (endLetter > 0) removeMove(enemy, new Move(end, new Position(endLetter - 1, startNumber)));
					if (endLetter < dimX - 1) removeMove(enemy, new Move(end, new Position(endLetter + 1, startNumber)));
				}

				if (currentPlayer == RED) {
					// Add new moves for active token
					if (endNumber < dimY - 1) {
						if (board[endLetter][endNumber + 1] == NONE)
							addMove(own, new Move(end, new Position(endLetter, endNumber + 1)));
						if (endLetter > 0 && board[endLetter - 1][endNumber + 1] != RED)
							addMove(own, new Move(end, new Position(endLetter - 1, endNumber + 1)));
						if (endLetter < dimX - 1 && board[endLetter + 1][endNumber + 1] != RED)
							addMove(own, new Move(end, new Position(endLetter + 1, endNumber + 1)));

						// Remove forward enemy move, which might be blocked now
						removeMove(enemy, new Move(new Position(endLetter, endNumber + 1), end));
					}
					// Add enemy move, which might was blocked by the active token before
					if (startNumber < dimY - 1 && board[startLetter][startNumber + 1] == BLUE)
						addMove(enemy, new Move(new Position(startLetter, startNumber + 1), start));
					// Add new moves for possibly blocked tokens
					if (startNumber > 0) {
						if (board[startLetter][startNumber - 1] == RED)
							addMove(own, new Move(new Position(startLetter, startNumber - 1), start));
						if (startLetter > 0 && board[startLetter - 1][startNumber - 1] == RED)
							addMove(own, new Move(new Position(startLetter - 1, startNumber - 1), start));
						if (startLetter < dimX - 1 && board[startLetter + 1][startNumber - 1] == RED)
							addMove(own, new Move(new Position(startLetter + 1, startNumber - 1), start));
					}
				} else {
					// Add new moves for moved token
					if (endNumber > 0) {
						if (board[endLetter][endNumber - 1] == NONE)
							addMove(own, new Move(end, new Position(endLetter, endNumber - 1)));
						if (endLetter > 0 && board[endLetter - 1][endNumber - 1] != BLUE)
							addMove(own, new Move(end, new Position(endLetter - 1, endNumber - 1)));
						if (endLetter < dimX - 1 && board[endLetter + 1][endNumber - 1] != BLUE)
							addMove(own, new Move(end, new Position(endLetter + 1, endNumber - 1)));

						// Remove enemy blocked move
						removeMove(enemy, new Move(new Position(endLetter, endNumber - 1), end));
					}
					// Add enemy move, which might was blocked by the active token before
					if (startNumber > 0 && board[startLetter][startNumber - 1] == RED)
						addMove(enemy, new Move(new Position(startLetter, startNumber - 1), start));
					// Add new moves for possibly blocked tokens
					if (startNumber < dimY - 1) {
						if (board[startLetter][startNumber + 1] == BLUE)
							addMove(own, new Move(new Position(startLetter, startNumber + 1), start));
						if (startLetter > 0 && board[startLetter - 1][startNumber + 1] == BLUE)
							addMove(own, new Move(new Position(startLetter - 1, startNumber + 1), start));
						if (startLetter < dimX - 1 && board[startLetter + 1][startNumber + 1] == BLUE)
							addMove(own, new Move(new Position(startLetter + 1, startNumber + 1), start));
					}
				}
			} catch (PresetException e) {
				// This will not happen due to correct rules
				e.printStackTrace();
			}
		}

		/**
		 * Add a move to a set of possible moves if not already existing
		 *
		 * @param set
		 * 		Set
		 * @param move
		 * 		Move
		 */
		private void addMove(HashSet<Move> set, Move move) {
			if (!set.contains(move)) set.add(move);
		}

		/**
		 * Remove a move from a set if existing
		 *
		 * @param set
		 * 		Set
		 * @param move
		 * 		Move
		 */
		private void removeMove(HashSet<Move> set, Move move) {
			if (set.contains(move)) set.remove(move);
		}

		/**
		 * Checks if a move is valid in the current situation
		 *
		 * @param move
		 * 		Move
		 *
		 * @return Move is valid
		 */
		public synchronized boolean isValidMove(Move move) {
			// null is a valid move
			if (move == null) return true;
			else if (move.getStart() == null || move.getEnd() == null) return false;
			return currentPlayer == RED ? redMoves.contains(move) : blueMoves.contains(move);
		}

		/**
		 * Analyzes a move and returns a status integer, giving information about the move. If the move is valid, the
		 * status is {@code MOVE_OK}. Otherwise the method will check what went wrong and return a more detailed status
		 * code. Possible codes are: <ul> <li></li> </ul>
		 *
		 * @param move
		 * 		move
		 *
		 * @return Status code
		 */
		public synchronized int analyzeMove(Move move) {
			// Check for surrender move
			if (move == null) return MOVE_SURRENDER;

			// Check if move is contained in the move set of the active player, to determine if it's valid
			if (currentPlayer == RED && redMoves.contains(move)) return MOVE_OK;
			else if (currentPlayer == BLUE && blueMoves.contains(move)) return MOVE_OK;

			// Check if start and end positions are inside the board dimensions
			Position start = move.getStart();
			Position end = move.getEnd();

			if (start.getLetter() < 0 || start.getLetter() >= dimX || start.getNumber() < 0 || start.getNumber() >=
                    dimY)
				return MOVE_START_OUT_OF_BOUNDS;
			if (end.getLetter() < 0 || end.getLetter() >= dimX || end.getNumber() < 0 || end.getNumber() >= dimY)
				return MOVE_END_OUT_OF_BOUNDS;

			// Check if the start position belongs to an own token
			int startToken = board[start.getLetter()][start.getNumber()];
			if (startToken == NONE) return MOVE_START_NO_TOKEN;
			if (startToken == (currentPlayer == RED ? BLUE : RED)) return MOVE_START_ENEMY_TOKEN;

			// Check if move direction is correct
			if (end.getNumber() == start.getNumber()) return MOVE_ON_SAME_ROW;
			if (((currentPlayer == RED && end.getNumber() < start.getNumber()) || currentPlayer == BLUE && end
                    .getNumber() > start.getNumber()))
				return MOVE_WRONG_DIRECTION;

			// Check if the move distance is too far
			if (Math.abs(start.getLetter() - end.getLetter()) > 1 || Math.abs(start.getNumber() - end.getNumber()) > 1)
				return MOVE_TOO_FAR;

			// Check if an own token was kicked or end position is not empty
			int endToken = board[end.getLetter()][end.getNumber()];
			if (currentPlayer == endToken) return MOVE_KICKED_OWN_TOKEN;
			// we can check if end position is not empty to find the forward error, because we have already checked for
			// invalid moves before
			if (endToken != NONE) return MOVE_KICKED_FORWARD;

			// We found no error so far but the move is invalid. Something went wrong!
			return MOVE_UNKNOWN_ERROR;
		}

		/**
		 * Check for a win situation
		 *
		 * @param move
		 * 		The move just made
		 *
		 * @return new Status value
		 */
		private int calculateNextStatus(Move move) {
			int number = move.getEnd().getNumber();
			if (number == dimY - 1) return RED_WIN;
			else if (number == 0) return BLUE_WIN;
			return OK;
		}

		/**
		 * Change to next player
		 */
		private void nextPlayer() {
			if (currentPlayer == RED) currentPlayer = BLUE;
			else currentPlayer = RED;
		}

		// ------------------------------------------------------------

		/**
		 * Get a string representation of the board
		 */
		@Override
		public String toString() {
			String s = "";

			// Top border
			s += "    ";
			for (int i = 0; i < dimX; i++)
				s += Position.getAlphabet().charAt(i) + " ";
			s += "\n    ";
			for (int i = 0; i < dimX - 1; i++)
				s += "--";
			s += "-\n";

			// Field
			for (int n = dimY - 1; n >= 0; n--) {
				s += (n < 9 ? " " : "") + (n + 1) + " |";
				for (int l = 0; l < dimX; l++) {
					char c;
					switch (board[l][n]) {
						case RED:
							c = 'R';
							break;
						case BLUE:
							c = 'B';
							break;
						case NONE:
						default:
							c = '.';
							break;
					}
					s += c;
					if (l < dimX - 1) s += ' ';
				}
				s += "| " + (n < 9 ? " " : "") + (n + 1) + "\n";
			}

			// Bottom border

			s += "    ";
			for (int i = 0; i < dimX - 1; i++)
				s += "--";
			s += "-\n    ";
			for (int i = 0; i < dimX; i++)
				s += Position.getAlphabet().charAt(i) + " ";

			return s;
		}
	}
}
