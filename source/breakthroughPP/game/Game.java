package breakthroughPP.game;

import eu.nepster.frozencube.message.Logger;
import eu.nepster.frozencube.utility.settings.Setting;
import eu.nepster.frozencube.utility.settings.Settings;

/**
 * The game class. All used components are created here as singletons
 *
 * @author Dominick Leppich
 */
public class Game {
	private static Logger logger;
	private static Settings settings;

	// ------------------------------------------------------------

	/**
	 * Get the logging instance
	 * 
	 * @return Logger
	 */
	public static Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
			logger.register(System.out, Logger.ALL);
		}
		return logger;
	}

	/**
	 * Get the settings instance. If not existing, create and init one.
	 * 
	 * @return Settings
	 */
	public static Settings getSettings() {
		if (settings == null)
			initSettings();

		return settings;
	}

	// ------------------------------------------------------------

	/**
	 * Create and initialize settings object with default settings
	 */
	private static void initSettings() {
		settings = new Settings();

		settings.put("timelimit_move", new Setting(Setting.Type.Long, 5000L));
		settings.put("timelimit_player", new Setting(Setting.Type.Long, 1000L));

		settings.put("move_timeout", new Setting(Setting.Type.Integer, 1000));
	}
}
