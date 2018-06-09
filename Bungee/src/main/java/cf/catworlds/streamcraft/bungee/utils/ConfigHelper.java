package cf.catworlds.streamcraft.bungee.utils;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigHelper {

	static public Configuration loadConfig(File file) {
		if (!file.exists())
			return new Configuration();
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			return new Configuration();
		}
	}

	static public boolean saveConfig(Configuration config, File file) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	static public Configuration getPluginConfig(Plugin plugin) {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();

		File file = new File(plugin.getDataFolder(), "config.yml");

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		return loadConfig(file);

	}

	static public boolean savePluginConfig(Configuration config, Plugin plugin) {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();

		File file = new File(plugin.getDataFolder(), "config.yml");
		return saveConfig(config, file);
	}

	// static public <V> Map<String, V> getMap(final Configuration config,
	// Class<V> type) {
	// Map<String, V> map = new HashMap<String, V>();
	//
	// for (String key : config.getKeys()) {
	// Object val = config.get(key);
	// if (val instanceof Configuration) {
	// continue;
	// }
	// if (val.getClass().isInstance(type))
	// map.put(key, (V) val);
	// }
	//
	// return map;
	// }

}
