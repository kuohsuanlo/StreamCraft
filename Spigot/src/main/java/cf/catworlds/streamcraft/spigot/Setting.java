package cf.catworlds.streamcraft.spigot;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import cf.catworlds.streamcraft.core.setting.SettingLoader;
import cf.catworlds.streamcraft.core.spigot.SpigotConfigUnit;

public class Setting extends SettingLoader {

	@SettingInfo(path = "Version")
	static public int version;

	@Override
	protected void default_setting() {
		version = 1;
	}

	@Override
	protected void setting_postload() {
	}

	static public List<String> loadSetting(FileConfiguration config, Logger logger) {
		return default_load(new SpigotConfigUnit(config), logger, Setting.class);
	}

	static public List<String> loadSetting(FileConfiguration config, Plugin plugin) {
		return loadSetting(config, plugin.getLogger());
	}

}
