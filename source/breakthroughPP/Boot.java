package breakthroughPP;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.Vector;

import breakthroughPP.board.Board;
import breakthroughPP.game.Game;
import breakthroughPP.game.Match;
import breakthroughPP.graphic.simple.Java2DGui;
import breakthroughPP.io.AsciiUI;
import breakthroughPP.io.SimpleTerminalOutput;
import breakthroughPP.player.ContestPlayer;
import breakthroughPP.player.ai.SlowRandomAI;
import breakthroughPP.preset.Move;
import breakthroughPP.preset.Setting;

/**
 * Start class of the breakthroughPP project. It creates an instance of the
 * game.
 *
 * @author Dominick Leppich
 */
public class Boot implements Setting {
	private static Random rnd = new Random(System.currentTimeMillis());
	private static Board board = new Board();

	public static void main(String[] args) throws RemoteException, Exception {
		// Logger.getLogger("breakthroughPP").setLevel(Level.OFF);
		// System.out.println(profile(100));
		/*
		 * if (args.length < 2) { System.out.println(
		 * "Usage: java -jar breakthroughPP.jar <NUM-LETTERS> <NUM-NUMBERS> (ANYTHING)\nIf you "
		 * +
		 * "use parameter anything, an extended terminal output will be used...\nExample: java -jar "
		 * + "breakthroughPP.jar 10 10 xyz"); return; }
		 */
		if (args.length > 2)
			Game.getSettings().put("match_timeout", new eu.nepster.frozencube.utility.settings.Setting(
					eu.nepster.frozencube.utility.settings.Setting.Type.Integer, Integer.parseInt(args[2])));

		// randomGame();
		// AsciiUI ui = new AsciiUI();
		ContestPlayer a, b;
		a = new ContestPlayer(new SlowRandomAI(100, 300), "Hans");
		b = new ContestPlayer(new SlowRandomAI(50, 800), "Gunther");
		Match m = new Match(a, b, Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				args.length > 3 ? (args.length > 4 ? new Java2DGui() : new AsciiUI()) : new SimpleTerminalOutput());
		// Match m = new Match(a, b, Integer.parseInt(args[0]),
		// Integer.parseInt(args[1]), new SimpleTerminalOutput());
		m.startMatch();
		// System.out.println("hi 1");
		m.waitMatchEnd();
		// System.out.println("hi 2");

		// sleep = Integer.parseInt(args[2]);
		// board.reset(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		//// System.out.println(board);
		// AsciiUI ui = new AsciiUI();
		// board.addObserver(ui);
		// ui.setViewer(board.viewer());                                        
		//
		// while (board.getStatus().isOk()) {
		// Vector<Move> moves = board.getValidMoves();
		// Move m = moves.get(rnd.nextInt(moves.size()));
		// makeMove(m);
		// }
		// ui.exit();

		// System.out.println(board);
		// AsciiUI.printBoard(board);
		// board.reset(8, YELLOW);
		// for (Pair<Move, Status> p : board.getValidMovesWithStatus())
		// System.out.println(p.getValue0() + ", " + p.getValue1());

		// board.reset(6, 6);
		// System.out.println(board);
		// while (board.getStatus().isOk()) {
		// Vector<Move> moves = board.getValidMoves();
		// for (Move m : moves)
		// System.out.print(m + " ");
		// System.out.println();
		// Move move = moves.get(rnd.nextInt(moves.size()));
		// System.out.println(move + "\n");
		// board.make(move);
		// System.out.println();
		// System.out.println(board);
		// System.out.println();
		// System.out.println(board.getStatus());
		// System.out.println("\n\n---------------------------------------\n\n");
		// }
	}

	private static void randomGame() {
		try {
			board.reset(26, 26);
			while (board.getStatus().isOk()) {
				Vector<Move> moves = board.getValidMoves();
				Move move = moves.get(rnd.nextInt(moves.size()));
				board.make(move);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static long profile(int count) {
		long sum = 0;
		long last = System.nanoTime();
		for (int i = 0; i < count; i++) {
			randomGame();
			long time = System.nanoTime();
			sum += (time - last);
			last = time;
		}
		return sum / count;
	}
}
