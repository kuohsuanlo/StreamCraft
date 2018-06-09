package cf.catworlds.streamcraft.bungee;

import java.io.File;

import cf.catworlds.streamcraft.bungee.command.CommandTest;
import cf.catworlds.streamcraft.bungee.listener.SomeListener;
import cf.catworlds.streamcraft.bungee.pmchannel.PluginMessageReceiver;
import cf.catworlds.streamcraft.bungee.utils.ConfigHelper;
import cf.catworlds.streamcraft.core.bungee.BungeeConfigUnit;
import cf.catworlds.streamcraft.core.protocol.PacketManager;
import cf.catworlds.streamcraft.core.protocol.pmlistener.BungeeChannelManager;
import cf.catworlds.streamcraft.core.text.TextHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class MainClass extends Plugin {

	@Override
	public void onEnable() {
		getLogger().info(ChatColor.GREEN + "Hello world!");
		loadText();
		loadConfig();
		registerChannel();
		registerListener();
		loadCommand();
	}

	private void loadText() {
		File langFile = new File(getDataFolder(), "lang" + File.separator + "message.yml");
		Configuration message = ConfigHelper.loadConfig(langFile);
		TextHelper.init_BasicText(new BungeeConfigUnit(message));
		ConfigHelper.saveConfig(message, langFile);
	}

	private void loadConfig() {
		Configuration config = ConfigHelper.getPluginConfig(this);
		Setting.loadSetting(config, this);
		ConfigHelper.savePluginConfig(config, this);
	}

	private void registerChannel() {
		BungeeChannelManager.init(this);
		PacketManager.registerListener(new PluginMessageReceiver(this), this, this.getLogger());
	}

	private void registerListener() {
		getProxy().getPluginManager().registerListener(this, new SomeListener(this));

	}

	private void loadCommand() {
		getProxy().getPluginManager().registerCommand(this, new CommandTest(this));

	}
}
