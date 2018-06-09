package cf.catworlds.streamcraft.core.bungee;

import java.util.List;

import cf.catworlds.streamcraft.core.setting.ConfigurationUnit;
import net.md_5.bungee.config.Configuration;

public class BungeeConfigUnit implements ConfigurationUnit {

	private Configuration config;

	public BungeeConfigUnit(Configuration config) {
		this.config = config;
	}

	@Override
	public void addDefault(String path, Object value) {
		System.out.println(path + ":" + value);
		if (!config.contains(path))
			config.set(path, value);
	}

	@Override
	public Object get(String path) {
		return config.get(path, null);
	}

	@Override
	public void set(String path, Object value) {
		config.set(path, value);
	}

	@Override
	public boolean isList(String path) {
		return config.get(path, null) instanceof List;
	}

	@Override
	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}
}
