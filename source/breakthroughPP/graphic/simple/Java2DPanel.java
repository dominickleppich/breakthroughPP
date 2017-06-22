package breakthroughPP.graphic.simple;

import breakthroughPP.preset.Move;
import breakthroughPP.preset.Setting;
import breakthroughPP.preset.Status;
import breakthroughPP.preset.Viewer;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Java2D Panel for the graphical board.
 *
 * @author Dominick Leppich
 */
public class Java2DPanel extends JPanel implements Setting {
	private static final long serialVersionUID = 1L;

	private static final int FIELD_SIZE = 50;

	// ------------------------------------------------------------

	private Java2DGui gui;

	// ------------------------------------------------------------

	/**
	 * Create a new panel for the gui
	 *
	 * @param gui
	 * 		Parent gui window
	 */
	public Java2DPanel(Java2DGui gui) {
		this.gui = gui;
		setDoubleBuffered(true);
	}

	// ------------------------------------------------------------

	/**
	 * Determine the size of the panel
	 *
	 * @return Dimension
	 */
	public Dimension getPreferredSize() {
		return new Dimension(gui.getViewer().getDimX() * FIELD_SIZE, gui.getViewer().getDimY() * FIELD_SIZE);
	}

	// ------------------------------------------------------------

	/**
	 * Update the panel with the made move
	 *
	 * @param move
	 * 		Move
	 * @param kicked
	 * 		true, if a token was kicked
	 * @param status
	 * 		Status of the move
	 */
	public void update(Move move, boolean kicked, Status status) {
		// Nothing special so far
		repaint();
	}

	// ------------------------------------------------------------

	/**
	 * Paint method
	 *
	 * @param g
	 * 		Graphics context
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		drawBoard(g2d);
	}

	/**
	 * Draw the board
	 *
	 * @param g
	 * 		Graphics2D context
	 */
	public void drawBoard(Graphics2D g) {
		Viewer viewer = gui.getViewer();

		// Draw possible moves
		for (Move move : viewer.getValidMoves()) {
			g.setStroke(new BasicStroke(5.0f));
			g.setColor(Color.GREEN);
			g.drawLine(move.getStart().getLetter() * FIELD_SIZE + FIELD_SIZE / 2, (viewer.getDimY() - 1 - move.getStart().getNumber()) * FIELD_SIZE + FIELD_SIZE / 2, move.getEnd().getLetter() * FIELD_SIZE +
                    FIELD_SIZE / 2, (viewer.getDimY() - 1 - move.getEnd().getNumber()) * FIELD_SIZE + FIELD_SIZE / 2);
		}

		// Draw board
		for (int l = 0; l < viewer.getDimX(); l++) {
			for (int n = 0; n < viewer.getDimY(); n++) {
				// number coordinates are swapped
				int letter = l, number = viewer.getDimY() - 1 - n;
				// Draw grid
				g.setColor(Color.BLACK);
				g.drawRect(letter * FIELD_SIZE, number * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE);

				// Fill maybe
				switch (viewer.getColor(l, n)) {
					case RED:
						g.setColor(Color.RED);
						g.fillOval(letter * FIELD_SIZE + FIELD_SIZE / 4, number * FIELD_SIZE + FIELD_SIZE / 4,
                                FIELD_SIZE / 2, FIELD_SIZE / 2);
						break;
					case BLUE:
						g.setColor(Color.BLUE);
						g.fillOval(letter * FIELD_SIZE + FIELD_SIZE / 4, number * FIELD_SIZE + FIELD_SIZE / 4,
                                FIELD_SIZE / 2, FIELD_SIZE / 2);
						break;
					case NONE:
					default:
				}
			}
		}
	}
}
