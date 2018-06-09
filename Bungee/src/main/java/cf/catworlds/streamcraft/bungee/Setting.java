package cf.catworlds.streamcraft.bungee;

import java.util.logging.Logger;

import cf.catworlds.streamcraft.core.bungee.BungeeConfigUnit;
import cf.catworlds.streamcraft.core.setting.SettingLoader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

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

	static public void loadSetting(Configuration config, Logger logger) {
		default_load(new BungeeConfigUnit(config), logger, Setting.class);
	}

	static public void loadSetting(Configuration config, Plugin plugin) {
		loadSetting(config, plugin.getLogger());
	}

}
