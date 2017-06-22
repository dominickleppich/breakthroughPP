package breakthroughPP.preset;

import java.util.Vector;

public interface Viewer {
	int turn();

	int getDimX();

	int getDimY();

	int getColor(int letter, int number);

	Vector<Move> getValidMoves();

	Status getStatus();
}