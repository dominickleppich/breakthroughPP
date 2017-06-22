package breakthroughPP.player.ai;

import breakthroughPP.preset.Player;

/**
 * A contest player contains all information about a player, to take part in an AI contest.
 *
 * @author Dominick Leppich
 */
public class ContestPlayer {
	private Player player;
	private String name;

	// ------------------------------------------------------------

	/**
	 * Create a new contest player
	 *
	 * @param player
	 * 		Player reference
	 * @param name
	 * 		Name
	 */
	public ContestPlayer(Player player, String name) {
		this.player = player;
		this.name = name;
	}

	// ------------------------------------------------------------

	/**
	 * Returns the player reference
	 * @return Player reference
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the name of the player
	 *
	 * @return Name
	 */
	public String getName() {
		return name;
	}
}
