package cf.catworlds.streamcraft.spigot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import cf.catworlds.streamcraft.core.protocol.packet.HelloWorldPacket;
import cf.catworlds.streamcraft.core.protocol.pmlistener.SpigotChannelManager;
import cf.catworlds.streamcraft.core.text.BasicText;
import cf.catworlds.streamcraft.core.text.TextHelper;
import cf.catworlds.streamcraft.spigot.MainClass;

public class MainCommand implements TabExecutor {

	MainClass plugin;

	public MainCommand(MainClass pl) {
		this.plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(TextHelper.format(BasicText.NotPlayer));
			return true;
		}
		SpigotChannelManager.sendToBungee(new HelloWorldPacket(2, "A Message From Spigot"));
		return true;
	}

	private static final List<String> FirstLevel = Arrays.asList("SubCommand1", "SubCommand2", "SubCommand3");

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<String>();
		if (args.length == 1)
			StringUtil.copyPartialMatches(args[0], FirstLevel, completions);
		// if (args.length == 2 && args[0].equalsIgnoreCase("SubCommand1"))
		// Next level ...
		return completions;
	}

}
