package breakthroughPP.game;

import breakthroughPP.preset.Setting;

/**
 * Controls the timelimit per player
 *
 * @author Dominick Leppich
 */
public class TimeControlThread implements Setting, Runnable {
	private Match match;
	private GameDisplay display;
	private boolean active = false;
	private boolean running = true;

	private long playerTime, moveTime;
	private int playerColor;

	private Thread thread;

	// ------------------------------

	/**
	 * Create a new time limit control thread
	 * 
	 * @param match
	 *            match which belongs to this thread
	 * @param display
	 *            display object
	 */
	public TimeControlThread(Match match, GameDisplay display) {
		this.match = match;
		this.display = display;
		this.thread = new Thread(this);
		this.thread.start();
	}

	// ------------------------------

	/**
	 * Start control
	 *
	 * @param playerTime
	 *            Remaining player time
	 * @param playerColor
	 *            Player color
	 */
	public synchronized void start(long playerTime, int playerColor) {
		this.playerTime = playerTime;
		this.moveTime = 0L;
		this.playerColor = playerColor;
		active = true;
		notify();
	}

	/**
	 * Stop control and return remaining player time
	 */
	public long stop() {
		active = false;
		return playerTime;
	}

	/**
	 * Shuts down the thread
	 */
	public synchronized void exit() {
		running = false;
		notify();
	}

	// ------------------------------

	/**
	 * Time measure loop
	 */
	public void run() {
		while (running) {
			try {
				if (active) {
					Thread.sleep(100);
					playerTime += 100;
					moveTime += 100;

					long maxPlayerTime = Game.getSettings().get("timelimit_player").getLong();
					long maxMoveTime = Game.getSettings().get("timelimit_move").getLong();

					// This works only for two players
					display.setPlayerTime(playerColor, playerTime, maxPlayerTime);
					display.setMoveTime(moveTime, maxMoveTime);
					if (playerTime > maxPlayerTime || moveTime > maxMoveTime) {
						Game.getLogger().info(this, "Timit limit exceeded");
						display.setInputEnabled(false);
						match.win(playerColor == RED ? BLUE : RED, "Time limit");
						// TODO gui message
					}
				} else {
					synchronized (this) {
						wait();
					}
				}
			} catch (InterruptedException e) {
				Game.getLogger().error(this, "Interrupted exception");
				e.printStackTrace();
			}
		}
	}
}
