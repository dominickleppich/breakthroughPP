package breakthroughPP.graphic.simple;

import javax.swing.JFrame;

import breakthroughPP.game.GameDisplay;
import breakthroughPP.preset.Move;
import breakthroughPP.preset.Requestable;
import breakthroughPP.preset.Status;
import breakthroughPP.preset.Viewer;

/**
 * Simple Java2D Gui
 *
 * @author Dominick Leppich
 */
public class Java2DGui extends JFrame implements Requestable, GameDisplay {
	private static final long serialVersionUID = 1L;
	
	private Viewer viewer;
	private Java2DPanel panel;

	// ------------------------------------------------------------

	/**
	 * Create a new Java2D Gui.
	 */
	public Java2DGui() {
		setTitle("breakthroughPP");
		panel = new Java2DPanel(this);
		add(panel);
		setResizable(false);
		setVisible(true);

		// TODO remove this
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	// ------------------------------------------------------------

	/**
	 * Request a move from the gui
	 *
	 * @return Valid move
	 *
	 * @throws Exception
	 * 		if something fails (should not happen)
	 */
	public Move deliver() throws Exception {
		return null;
	}

	// ------------------------------------------------------------

	/**
	 * Set the viewer of the board to display.
	 *
	 * @param viewer
	 * 		Viewer
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
		pack();
	}

	/**
	 * Get the viewer of the board to display.
	 *
	 * @return Viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Move move, boolean kicked, Status status) {
		panel.update(move, kicked, status);
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
		// Not implemented here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMoveTime(long current, long max) {
		// Not implemented here
	}
}
