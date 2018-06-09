package cf.catworlds.streamcraft.core.protocol.pmlistener;

import cf.catworlds.streamcraft.core.protocol.AbstractPacket;
import cf.catworlds.streamcraft.core.protocol.PacketManager;
import cf.catworlds.streamcraft.core.protocol.Protocol;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeChannelManager implements Listener {

	static private Plugin plugin;

	public static void init(Plugin plugin) {
		BungeeChannelManager.plugin = plugin;

		plugin.getProxy().registerChannel(Protocol.ChannelTag); // IO
		plugin.getProxy().getPluginManager().registerListener(plugin, new BungeeChannelManager());
	}

	@EventHandler
	public void onPluginMessageReceived(PluginMessageEvent event) {
		if (event.isCancelled())
			return;
		if (!(event.getSender() instanceof Server))
			return;
		if (!event.getTag().equals(Protocol.ChannelTag))
			return;
		event.setCancelled(true);

		AbstractPacket packet = Protocol.toPacket(event.getData());
		PacketManager.firePacket(packet, plugin.getLogger());
	}

	public static void sendToServer(final ServerInfo server, final AbstractPacket packet) {
		final byte[] data = Protocol.toByte(packet);
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

			@Override
			public void run() {
				// TODO queue setting
				server.sendData(Protocol.ChannelTag, data, false);
			}
		});
	}

	public static void sendToPlayer(final ProxiedPlayer player, final AbstractPacket packet) {
		sendToServer(player.getServer().getInfo(), packet);
	}

	// TODO auto unregister listener when pluginUnloadEvent
}
