package cf.catworlds.streamcraft.bungee.pmchannel;

import cf.catworlds.streamcraft.core.protocol.PacketHandler;
import cf.catworlds.streamcraft.core.protocol.PacketListener;
import cf.catworlds.streamcraft.core.protocol.packet.HelloWorldPacket;
import cf.catworlds.streamcraft.bungee.MainClass;
import net.md_5.bungee.api.ChatColor;

public class PluginMessageReceiver implements PacketListener {

	private MainClass plugin;

	public PluginMessageReceiver(MainClass plugin) {
		this.plugin = plugin;
	}

	@PacketHandler
	public void fromSpigot(HelloWorldPacket packet) {
		plugin.getLogger().info(ChatColor.GREEN + "GET Packet!! : " + packet.getName());
	}
}
