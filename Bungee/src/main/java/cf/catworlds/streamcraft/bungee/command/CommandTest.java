package cf.catworlds.streamcraft.bungee.command;

import cf.catworlds.streamcraft.bungee.MainClass;
import cf.catworlds.streamcraft.core.protocol.packet.HelloWorldPacket;
import cf.catworlds.streamcraft.core.protocol.pmlistener.BungeeChannelManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandTest extends Command {

	MainClass plugin;

	public CommandTest(MainClass pl) {
		super("Command", null /* permission */, new String[0] /* aliases */);
		this.plugin = pl;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			plugin.getProxy().broadcast(new ComponentBuilder(sender.getName() + " say hi!").create());
			BungeeChannelManager.sendToPlayer((ProxiedPlayer) sender, new HelloWorldPacket(20, "A Test From Bungee"));
		} else {
			plugin.getProxy().broadcast(new ComponentBuilder("console say hi!").create());
		}
	}

}
