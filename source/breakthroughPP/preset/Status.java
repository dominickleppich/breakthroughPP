package breakthroughPP.preset;

public class Status implements Setting, java.io.Serializable {

	public Status(int status) {
		setStatus(status);
	}

	public Status(Status stat) {
		setStatus(stat.status);
	}

	// ----------------------------------------------------------------

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		if (status < OK || status > ILLEGAL) this.status = UNDEFINED;
		else this.status = status;
	}

	// ----------------------------------------------------------------

	public boolean isOk() {
		return status == OK;
	}

	public boolean isRedWin() {
		return status == RED_WIN;
	}

	public boolean isBlueWin() {
		return status == BLUE_WIN;
	}

	public boolean isIllegal() {
		return status == ILLEGAL;
	}

	public boolean isUndefined() {
		return status == UNDEFINED;
	}

	// ----------------------------------------------------------------

	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (obj == null || !(obj instanceof Status)) return false;

		Status stat = (Status) obj;
		return stat.status == status;
	}

	public int hashCode() {
		return status;
	}

	public String toString() {
		return statusString[status];
	}

	// private -------------------------------------------------------
	private int status;

	// private static ------------------------------------------------
	private static final long serialVersionUID = 1L;
}