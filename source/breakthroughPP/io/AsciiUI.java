package breakthroughPP.io;

import breakthroughPP.game.GameDisplay;
import breakthroughPP.preset.*;
import eu.nepster.frozencube.utility.terminal.TerminalDisplay;
import eu.nepster.frozencube.utility.terminal.UnicodeSymbols;

import static eu.nepster.frozencube.utility.terminal.TerminalController.*;
import static eu.nepster.frozencube.utility.terminal.TerminalDisplay.ALIGN_LEFT;

/**
 * A terminal user interface
 *
 * @author Dominick Leppich
 */
public class AsciiUI implements UnicodeSymbols, Requestable, GameDisplay {
	private static final String BOARD_RASTER_0 = formatGrayscale(BACKGROUND, 15);
	private static final String BOARD_RASTER_1 = formatGrayscale(BACKGROUND, 16);
	private static final String BOARD_RASTER_CURRENT = formatColor8Bit(BACKGROUND, NORMAL, GREEN);
	private static final String BORDER_BACKGROUND = formatColor8Bit(BACKGROUND, LIGHT, BLACK);
	private static final String BORDER_FOREGROUND = formatColor8Bit(FOREGROUND, LIGHT, WHITE);
	private static final String BORDER = BORDER_FOREGROUND + BORDER_BACKGROUND + formatAttribute(FOREGROUND, BOLD);
	private static final String RED_COLOR = formatColor8Bit(FOREGROUND, LIGHT, RED);
	private static final String BLUE_COLOR = formatColor8Bit(FOREGROUND, LIGHT, BLUE);
	private static final String KICKED_COLOR = formatColor8Bit(FOREGROUND, NORMAL, YELLOW);
	private static final String WIN_COLOR = formatColor8Bit(FOREGROUND, NORMAL, GREEN);
	private static final String ALL_RESET = formatAttribute(ENABLE, RESET);

	private static final int BORDER_HORIZONTAL = 5;
	private static final int BORDER_VERTICAL = 3;

	private static final int BOARD_LEFT = 5;
	private static final int BOARD_TOP = 3;

	private static final int INFO_LEFT = 5;
	private static final int INFO_TOP = 5;
	private static final String INFO_TITLE = "breakthroughPP - Version: 1.0";
	private static final int INFO_TITLE_LEFT = 8;
	private static final int INFO_MOVE_LEFT = 8;
	private static final int INFO_MOVE_TOP = 7;
	private static final int INFO_STATUS_LEFT = 8;
	private static final int INFO_STATUS_TOP = 9;
	private static final String INFO_MAKE_MOVE_PROMPT = "Make your move!";
	private static final int INFO_MAKE_MOVE_PROMPT_LEFT = 10;
	private static final int INFO_MAKE_MOVE_PROMPT_TOP = 5;

	private static final int LEFT = 0;
	private static final int FORWARD = 1;
	private static final int RIGHT = 2;

	// ------------------------------------------------------------

	private TerminalDisplay display;
	private Viewer viewer;

	// ------------------------------------------------------------

	/**
	 * Create a new Ascii user interface
	 */
	public AsciiUI() {
		display = new TerminalDisplay();

		// Set raw mode and disable cursor
		/*try {
			display.setRawMode();
		} catch (TerminalException e) {
			e.printStackTrace();
		}
		display.setCursorVisible(false);*/
	}

	/**
	 * Set the viewer and update the UI
	 *
	 * @param viewer
	 * 		Viewer
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
		if (viewer == null) return;
		display.clearScreen();
		printScreen(viewer, null, false, null, null, false);
	}

	// ------------------------------------------------------------

	/**
	 * Print a full game screen with all information on terminal
	 *
	 * @param viewer
	 * 		Viewer to the board
	 * @param move
	 * 		Move
	 * @param kicked
	 * 		Token was kicked in this move
	 * @param status
	 * 		Status of this move
	 * @param hoverPosition
	 * 		Current selected position
	 * @param makeMovePrompt
	 * 		Prompt
	 */
	public void printScreen(Viewer viewer, Move move, boolean kicked, Status status, Position hoverPosition, boolean
			makeMovePrompt) {
		// Display board
		display.print(BOARD_LEFT, BOARD_TOP, 0, 0, ALIGN_LEFT, getBoardSmall(viewer, move, kicked, status,
				hoverPosition));

		int infoLeft = BOARD_LEFT + (BORDER_HORIZONTAL + viewer.getDimX()) * 2 + INFO_LEFT;
		int infoTop = INFO_TOP;
		// Display game name
		display.print(formatColor8Bit(BACKGROUND, LIGHT, WHITE));
		display.print(formatColor8Bit(FOREGROUND, NORMAL, BLACK));
		display.clear(infoLeft + INFO_TITLE_LEFT - 2, INFO_TOP - 1, INFO_TITLE.length() + 4, 3);
		display.print(infoLeft + INFO_TITLE_LEFT, infoTop, 0, 0, ALIGN_LEFT, formatAttribute(ENABLE, BOLD) +
				INFO_TITLE + formatAttribute(ENABLE, RESET));

		// Display move
		// Reset old entry
		display.clear(infoLeft + INFO_MOVE_LEFT, INFO_TOP + INFO_MOVE_TOP, 30, 1);

		display.print(infoLeft + INFO_MOVE_LEFT, INFO_TOP + INFO_MOVE_TOP, 0, 0, ALIGN_LEFT, formatColor8Bit
				(FOREGROUND, LIGHT, WHITE) + formatAttribute(ENABLE, BOLD) + "Move:    ");
		int color = -1;
		if (status == null) {
			if (viewer.turn() == Setting.RED) color = RED;
			else color = BLUE;
		} else if (status != null && status.isIllegal()) {
			if (viewer.turn() == Setting.RED) color = RED;
			else color = BLUE;
		} else if (move == null || status.isRedWin() || status.isBlueWin()) color = GREEN;
			// Player turn already swapped, so we need to reverse it
		else if (viewer.turn() == Setting.RED) color = BLUE;
		else color = RED;
		display.print(formatColor8Bit(FOREGROUND, NORMAL, color) + (move == null ? "surrender" : (move.getStart() !=
				null ? move.getStart() : "  ") + " -> " + (move.getEnd() != null ? move.getEnd() : "  ") +
				formatAttribute(ENABLE, RESET)));

		// Display status
		// Reset old entry
		display.clear(infoLeft + INFO_STATUS_LEFT, INFO_TOP + INFO_STATUS_TOP, 30, 1);
		if (status != null) {
			display.print(infoLeft + INFO_MOVE_LEFT, INFO_TOP + INFO_STATUS_TOP, 0, 0, ALIGN_LEFT, formatColor8Bit
					(FOREGROUND, LIGHT, WHITE) + formatAttribute(ENABLE, BOLD) + "Status:  ");
			color = -1;
			if (status.isRedWin()) color = RED;
			else if (status.isBlueWin()) color = BLUE;
			else if (status.isOk()) color = WHITE;
			else color = BLACK;
			display.print(formatColor8Bit(FOREGROUND, NORMAL, color) + status + formatAttribute(ENABLE, RESET));
		}

		// Display prompt if needed
		if (makeMovePrompt)
			display.print(infoLeft + INFO_MAKE_MOVE_PROMPT_LEFT, INFO_TOP + INFO_MAKE_MOVE_PROMPT_TOP, 0, 0,
					ALIGN_LEFT, formatColor8Bit(FOREGROUND, LIGHT, WHITE) + formatAttribute(ENABLE, BOLD, INVERTED) +
							INFO_MAKE_MOVE_PROMPT + formatAttribute(ENABLE, RESET));
		else
			display.clear(infoLeft + INFO_MAKE_MOVE_PROMPT_LEFT, INFO_TOP + INFO_MAKE_MOVE_PROMPT_TOP,
					INFO_MAKE_MOVE_PROMPT.length(), 1);

		// Set cursor to bottom left (to not overwrite display after game
		// finished)
		display.setCursorPosition(1, BOARD_TOP + viewer.getDimY() + BORDER_VERTICAL * 2 + 1);
	}

	/**
	 * Get the board as an ascii art. If the parameter {@code move} is given, the move made will be highlighted. If the
	 * parameter {@code status} is given, it will be outputted as well.
	 *
	 * @param viewer
	 * 		Viewer to the board to be printed
	 * @param move
	 * 		Move
	 * @param kicked
	 * 		token was kicked in this move
	 * @param status
	 * 		Status of this move
	 * @param hoverPosition
	 * 		Current selected position
	 *
	 * @return Ascii board as string
	 */
	public static String getBoardSmall(Viewer viewer, Move move, boolean kicked, Status status, Position
			hoverPosition) {
		if (viewer == null) return "";

		// Get dimensions
		int dimX = viewer.getDimX();
		int dimY = viewer.getDimY();

		// Changed Position
		Position changed = null;
		int direction = -1;
		if (hoverPosition == null && move != null && move.getStart() != null && move.getEnd() != null) {
			changed = move.getEnd();
			Position start = move.getStart();
			if (start.getLetter() == changed.getLetter()) direction = FORWARD;
			else direction = start.getLetter() < changed.getLetter() ? RIGHT : LEFT;
		}

		String s = "";
		// Draw the top border
		// Border space
		s += BORDER + "     ";
		for (int i = 0; i < dimX; i++)
			s += "  ";
		s += "     " + ALL_RESET + "\n"
				// Border letters
				+ BORDER + "     ";
		for (int i = 0; i < dimX; i++)
			s += Position.getAlphabet().charAt(i) + " ";
		s += "     " + ALL_RESET + "\n"
				// Border line
				+ BORDER + "    " + SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_TOP_LEFT];
		for (int i = 0; i < dimX; i++)
			s += SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_HORIZONTAL] +
					SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_HORIZONTAL];
		s += SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_TOP_RIGHT] + "    " + ALL_RESET + "\n";

		// Print the middle part (left + right boarder and board)
		for (int n = dimY - 1; n >= 0; n--) {
			s += BORDER + " " + (n < 9 ? " " : "") + (n + 1) + " " + SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_VERTICAL];
			for (int l = 0; l < dimX; l++) {
				try {
					Position current = new Position(l, n);
					// Print board raster
					s += (hoverPosition != null && hoverPosition.getLetter() == l && hoverPosition.getNumber() == n ? BOARD_RASTER_CURRENT : ((l + n) % 2 != 0 ? BOARD_RASTER_0 : BOARD_RASTER_1));


					// Get the token and print it
					switch (viewer.getColor(l, n)) {
						case Setting.RED:
							if (changed != null && changed.equals(current)) {
								// change color for kicked
								s += status.isRedWin() ? WIN_COLOR : (kicked ? KICKED_COLOR : RED_COLOR);
								// check direction
								switch (direction) {
									case LEFT:
										s += SYMBOL_TRIANGLE_TOP_LEFT + " ";
										break;
									case FORWARD:
										s += SYMBOL_TRIANGLE_UP + " ";
										break;
									case RIGHT:
										s += SYMBOL_TRIANGLE_TOP_RIGHT + " ";
										break;
								}
							} else s += RED_COLOR + SYMBOL_CIRCLE_FULL + " ";
							break;

						case Setting.BLUE:
							if (changed != null && changed.equals(current)) {
								// change color for kicked
								s += status.isBlueWin() ? WIN_COLOR : (kicked ? KICKED_COLOR : BLUE_COLOR);
								// check direction
								switch (direction) {
									case LEFT:
										s += SYMBOL_TRIANGLE_BOTTOM_LEFT + " ";
										break;
									case FORWARD:
										s += SYMBOL_TRIANGLE_DOWN + " ";
										break;
									case RIGHT:
										s += SYMBOL_TRIANGLE_BOTTOM_RIGHT + " ";
										break;
								}
							} else s += BLUE_COLOR + SYMBOL_CIRCLE_FULL + " ";
							break;

						case Setting.NONE:
						default:
							s += "  ";
					}
				} catch (PresetException e) {
					// Will not occur
					e.printStackTrace();
				}
			}
			s += BORDER + SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_VERTICAL] + " " + (n < 9 ? " " : "") + (n + 1) + " "
					+ ALL_RESET + "\n";
		}

		// Bottom border
		// Border line
		s += BORDER + "    " + SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_BOTTOM_LEFT];
		for (int i = 0; i < dimX; i++)
			s += SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_HORIZONTAL] +
					SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_HORIZONTAL];
		s += SYMBOLS_BORDER_DOUBLELINE[SYMBOL_INDEX_BOTTOM_RIGHT] + "    " + ALL_RESET + "\n"
				// Border letters
				+ BORDER + "     ";
		for (int i = 0; i < dimX; i++)
			s += Position.getAlphabet().charAt(i) + " ";
		s += "     " + ALL_RESET + "\n"
				// Border Space
				+ BORDER + "     ";
		for (int i = 0; i < dimX; i++)
			s += "  ";
		s += "     " + formatAttribute(ENABLE, RESET);

		return s;
	}

	/**
	 * Request a move from the player
	 *
	 * @return Valid move entered in the terminal
	 */
	@Override
	public Move deliver() throws Exception {
		// Loop until a valid move has been found
		Position current = new Position(0, 0);
		Move move = new Move(current, null);
		// Indicate if board has to be redrawn
		boolean changed = true;

		// Looking for start position or end position
		boolean startPos = true;
		boolean finish = false;

		while (!finish) {
			// Check input
			if (System.in.available() != 0) {
				int c = System.in.read();

				// Start position
				if (startPos) {
					switch (c) {
						// Arrow up
						case 65:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								changed = true;
								break;
							}
							if (current.getNumber() < viewer.getDimY() - 1)
								current = new Position(current.getLetter(), current.getNumber() + 1);
							move.setStart(current);
							changed = true;
							break;
						// Arrow down
						case 66:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								changed = true;
								break;
							}
							if (current.getNumber() > 0)
								current = new Position(current.getLetter(), current.getNumber() - 1);
							move.setStart(current);
							changed = true;
							break;
						// Arrow right
						case 67:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								changed = true;
								break;
							}
							if (current.getLetter() < viewer.getDimX() - 1)
								current = new Position(current.getLetter() + 1, current.getNumber());
							move.setStart(current);
							changed = true;
							break;
						// Arrow left
						case 68:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								changed = true;
								break;
							}
							if (current.getLetter() > 0)
								current = new Position(current.getLetter() - 1, current.getNumber());
							move.setStart(current);
							changed = true;
							break;
						// Return
						case 10:
							if (move == null) return null;
							startPos = false;
							changed = true;
							break;
						// Quit
						case 's':
							move = null;
							current = null;
							changed = true;
					}
					// End position
				} else {
					switch (c) {
						// Arrow up
						case 65:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								startPos = true;
								changed = true;
								break;
							}
							if (current.getNumber() < viewer.getDimY() - 1)
								current = new Position(current.getLetter(), current.getNumber() + 1);
							move.setEnd(current);
							changed = true;
							break;
						// Arrow down
						case 66:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								startPos = true;
								changed = true;
								break;
							}
							if (current.getNumber() > 0)
								current = new Position(current.getLetter(), current.getNumber() - 1);
							move.setEnd(current);
							changed = true;
							break;
						// Arrow right
						case 67:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								startPos = true;
								changed = true;
								break;
							}
							if (current.getLetter() < viewer.getDimX() - 1)
								current = new Position(current.getLetter() + 1, current.getNumber());
							move.setEnd(current);
							changed = true;
							break;
						// Arrow left
						case 68:
							if (move == null) {
								current = new Position(0, 0);
								move = new Move(current, null);
								startPos = true;
								changed = true;
								break;
							}
							if (current.getLetter() > 0)
								current = new Position(current.getLetter() - 1, current.getNumber());
							move.setEnd(current);
							changed = true;
							break;
						// Return
						case 10:
							if (move == null) return null;
							return move;
						// Quit
						case 's':
							move = null;
							current = null;
							changed = true;
					}
				}
			}

			if (changed) {
				// Redraw
				printScreen(viewer, move, false, null, current, true);
				changed = false;
			}
		}

		return move;
	}

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move move, boolean kicked, Status status) {
		printScreen(viewer, move, kicked, status, null, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInputEnabled(boolean state) {
		// Not implemented here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayerTime(int color, long current, long max) {
		// Not implemented here TODO
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMoveTime(long current, long max) {
		// Not implemented here TODO
	}

	// ------------------------------------------------------------

	/**
	 * Close the ascii user interface and reset all tty settings
	 */
	public void exit() {
		/*try {
			display.exit();
		} catch (TerminalException e) {
			e.printStackTrace();
		}
		display.setCursorVisible(true);*/
	}
}
