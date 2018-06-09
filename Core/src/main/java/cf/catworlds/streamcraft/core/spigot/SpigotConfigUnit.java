package cf.catworlds.streamcraft.core.spigot;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import cf.catworlds.streamcraft.core.setting.ConfigurationUnit;

public class SpigotConfigUnit implements ConfigurationUnit {

	private FileConfiguration config;

	public SpigotConfigUnit(FileConfiguration config) {
		this.config = config;
	}

	@Override
	public void addDefault(String path, Object value) {
		config.addDefault(path, value);
	}

	@Override
	public Object get(String path) {
		return config.get(path);
	}

	@Override
	public void set(String path, Object value) {
		config.set(path, value);
	}

	@Override
	public boolean isList(String path) {
		return config.isList(path);
	}

	@Override
	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

}
