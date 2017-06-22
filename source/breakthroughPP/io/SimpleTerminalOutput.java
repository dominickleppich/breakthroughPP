package breakthroughPP.io;

import breakthroughPP.game.GameDisplay;
import breakthroughPP.preset.*;

/**
 * A simple terminal output class.
 *
 * @author Dominick Leppich
 */
public class SimpleTerminalOutput implements Setting, Requestable, GameDisplay {
	private Viewer viewer;

	// ------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 *
	 * @param viewer
	 * 		Viewer
	 */
	@Override
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void update(Move move, boolean kicked, Status status) {
		String s = "";
		int dimX = viewer.getDimX();
		int dimY = viewer.getDimY();

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
				switch (viewer.getColor(l, n)) {
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

		System.out.println(s);
	}

	// ------------------------------------------------------------

	@Override
	public void setInputEnabled(boolean state) {
		// Nothing to do
	}

	@Override
	public void setPlayerTime(int color, long current, long max) {
		// Not implemented here
	}

	@Override
	public void setMoveTime(long current, long max) {
		// Not implemented here
	}

	@Override
	public Move deliver() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
