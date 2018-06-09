package cf.catworlds.streamcraft.core.setting;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import cf.catworlds.streamcraft.core.bungee.BungeeConfigUnit;
import cf.catworlds.streamcraft.core.spigot.SpigotConfigUnit;
import net.md_5.bungee.config.Configuration;

public interface ConfigurationUnit {

	void addDefault(String path, Object value);

	Object get(String path);

	void set(String path, Object value);

	boolean isList(String path);

	List<String> getStringList(String path);

	/**
	 * <blockquote>
	 * <table>
	 * <th>Generic</th>
	 * <th>-></th>
	 * <th>Wrapper</th>
	 * <tr>
	 * <td>{@link FileConfiguration}</td>
	 * <td></td>
	 * <td>{@link SpigotConfigUnit}</td>
	 * </tr>
	 * <tr>
	 * <td>{@link Configuration}</td>
	 * <td></td>
	 * <td>{@link BungeeConfigUnit}</td>
	 * </tr>
	 * </table>
	 * </blockquote>
	 * 
	 * @param config
	 *            (Generic)
	 * @return {@link ConfigurationUnit} (Wrapper)
	 */
	static public <T> ConfigurationUnit wrap(T config) {
		if (config instanceof FileConfiguration)
			return new SpigotConfigUnit((FileConfiguration) config);
		if (config instanceof Configuration)
			return new BungeeConfigUnit((Configuration) config);
		return null;
	}

}
