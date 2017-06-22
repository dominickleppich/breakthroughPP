package breakthroughPP.preset;

public class Position implements java.io.Serializable {

	public Position(int letter, int number) throws PresetException {
		setLetter(letter);
		setNumber(number);
	}

	public Position(Position pos) {
		this.letter = pos.letter;
		this.number = pos.number;
	}

	// ----------------------------------------------------------------

	public int getLetter() {
		return letter;
	}

	public int getNumber() {
		return number;
	}

	public void setLetter(int letter) throws PresetException {
		if (letter < 0 || letter >= 26) throw new PresetException("letter out of bounds");
		this.letter = letter;
	}

	public void setNumber(int number) throws PresetException {
		if (number < 0 || number >= 26) throw new PresetException("number out of bounds");
		this.number = number;
	}

	// ----------------------------------------------------------------

	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (obj == null || !(obj instanceof Position)) return false;

		Position pos = (Position) obj;
		return (letter == pos.letter) && (number == pos.number);
	}

	public int hashCode() {
		return letter * alphabet.length() + number;
	}

	public String toString() { return "" + alphabet.charAt(letter) + (number + 1); }

	// static --------------------------------------------------------

	public static String getAlphabet() {
		return alphabet;
	}

	// private -------------------------------------------------------
	private int letter;
	private int number;

	// private static ------------------------------------------------
	private static final long serialVersionUID = 1L;
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
}