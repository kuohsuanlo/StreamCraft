package cf.catworlds.streamcraft.spigot;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import cf.catworlds.streamcraft.core.protocol.PacketManager;
import cf.catworlds.streamcraft.core.protocol.pmlistener.SpigotChannelManager;
import cf.catworlds.streamcraft.core.spigot.SpigotConfigUnit;
import cf.catworlds.streamcraft.core.text.TextHelper;
import cf.catworlds.streamcraft.spigot.command.MainCommand;
import cf.catworlds.streamcraft.spigot.listener.SomeListener;
import cf.catworlds.streamcraft.spigot.pmchannel.PluginMessageReceiver;
import cf.catworlds.streamcraft.spigot.utils.ConfigHelper;

public class MainClass extends JavaPlugin {

	@Override
	public void onEnable() {
		loadText();
		loadConfig();
		registerChannel();
		registerListener();
		loadCommand();
	}
	
	@Override
	public void onDisable() {
		unregisterChannel();
	}

	private void loadText() {
		File langFile = new File(getDataFolder(), "lang" + File.separator + "message.yml");
		FileConfiguration config = ConfigHelper.loadConfig(langFile);
		TextHelper.init_BasicText(new SpigotConfigUnit(config));
		ConfigHelper.saveConfig(config, langFile);
	}

	private void loadConfig() {
		FileConfiguration config = getConfig();
		Setting.loadSetting(config, this);
		config.options().copyDefaults(true);
		saveConfig();
	}

	private void registerChannel() {
		SpigotChannelManager.init(this);
		PacketManager.registerListener(new PluginMessageReceiver(this), this, this.getLogger());
	}

	private void unregisterChannel() {
		PacketManager.unregisterAllListener(this, this.getLogger());
	}

	private void registerListener() {
		log2("Here1");
		getServer().getPluginManager().registerEvents(new SomeListener(this,1), this);
		log2("Here2");
		getServer().getPluginManager().registerEvents(new SomeListener(this,2), this);
		log2("Here3");
		SomeListener a = new SomeListener(this, 3);
		getServer().getPluginManager().registerEvents(a, this);
		log2("Here4");
		getServer().getPluginManager().registerEvents(a, this);
	}

	private void loadCommand() {
		getCommand("test").setExecutor(new MainCommand(this));
	}

	// TODO debug tools
	final ChatColor[] cls = new ChatColor[] { ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN,
			ChatColor.LIGHT_PURPLE };

	/**
	 * Auto broadcast colorful message.<br>
	 * TODO debug tools
	 * 
	 * @param msgs
	 *            debug message
	 */
	public void log(final Object... msgs) {
		StringBuilder out = new StringBuilder();
		int cl = 0;
		for (Object msg : msgs) {
			out.append(cls[cl++ % cls.length]);
			out.append(msg);
			out.append(" ");
		}
		getServer().broadcastMessage(out.toString());
	}

	/**
	 * Auto broadcast colorful message.<br>
	 * Only console<br>
	 * TODO debug tools
	 * 
	 * @param msgs
	 *            debug message
	 */
	public void log2(final Object... msgs) {
		StringBuilder out = new StringBuilder();
		int cl = 0;
		for (Object msg : msgs) {
			out.append(cls[cl++ % cls.length]);
			out.append(msg);
			out.append(" ");
		}
		getLogger().warning(out.toString());
	}

}
