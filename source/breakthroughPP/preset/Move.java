package breakthroughPP.preset;

public class Move implements java.io.Serializable {

	public Move(Position start, Position end) throws PresetException {
		setStart(start);
		setEnd(end);
	}

	public Move(Move move) {
		this.start = new Position(move.start);
		this.end = new Position(move.end);
	}

	// ----------------------------------------------------------------

	public Position getStart() {
		return start;
	}

	public Position getEnd() {
		return end;
	}

	public void setStart(Position start) throws PresetException {
		if (start == null) throw new PresetException("position null not allowed");
		this.start = start;
	}

	public void setEnd(Position end) throws PresetException {
		if (end == null) throw new PresetException("position null not allowed");
		this.end = end;
	}

	// ----------------------------------------------------------------

	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (obj == null || !(obj instanceof Move)) return false;

		Move mov = (Move) obj;
		return start.equals(mov.start) && end.equals(mov.end);
	}

	public int hashCode() {
		// Unique for Position.number, Position.letter < 26
		int base = Position.getAlphabet().length();
		return start.hashCode() * (base * base + base) + end.hashCode();
	}

	public String toString() {
		return start.toString() + end.toString();
	}

	// private -------------------------------------------------------
	private Position start, end;

	// private static ------------------------------------------------
	private static final long serialVersionUID = 1L;
}