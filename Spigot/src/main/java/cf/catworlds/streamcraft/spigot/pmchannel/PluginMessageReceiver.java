package cf.catworlds.streamcraft.spigot.pmchannel;

import cf.catworlds.streamcraft.core.protocol.HandlePriority;
import cf.catworlds.streamcraft.core.protocol.PacketHandler;
import cf.catworlds.streamcraft.core.protocol.PacketListener;
import cf.catworlds.streamcraft.core.protocol.packet.HelloWorldPacket;
import cf.catworlds.streamcraft.spigot.MainClass;

public class PluginMessageReceiver implements PacketListener {

	final MainClass plugin;

	public PluginMessageReceiver(MainClass pl) {
		this.plugin = pl;
	}

	@PacketHandler(priority = HandlePriority.LOWEST)
	public void onHelloWorld(HelloWorldPacket p) {
		plugin.log(1, p.getName());
	}

	@PacketHandler
	public void onHelloWorld2(HelloWorldPacket p) {
		plugin.log(2, p.getName());
	}

}
