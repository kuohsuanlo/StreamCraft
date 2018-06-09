package cf.catworlds.streamcraft.spigot.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHelper {

	static public FileConfiguration loadConfig(File file) {
		if (!file.exists())
			return new YamlConfiguration();
		return YamlConfiguration.loadConfiguration(file);
	}

	static public boolean saveConfig(FileConfiguration config, File file) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		try {
			config.options().copyDefaults(true);
			config.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
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
