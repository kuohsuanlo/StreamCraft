package cf.catworlds.streamcraft.core.protocol.pmlistener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Iterables;

import cf.catworlds.streamcraft.core.protocol.AbstractPacket;
import cf.catworlds.streamcraft.core.protocol.PacketManager;
import cf.catworlds.streamcraft.core.protocol.Protocol;

public class SpigotChannelManager implements PluginMessageListener {

	static private Plugin plugin;

	public static void init(Plugin plugin) {
		SpigotChannelManager.plugin = plugin;
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, Protocol.ChannelTag,
				new SpigotChannelManager());
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, Protocol.ChannelTag);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		AbstractPacket packet = Protocol.toPacket(message);
		PacketManager.firePacket(packet, plugin.getLogger());
	}

	public static void sendToBungee(final AbstractPacket packet) {
		final byte[] data = Protocol.toByte(packet);
		new BukkitRunnable() {

			@Override
			public void run() {
				Player p = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
				if (p == null) {
					plugin.getLogger()
							.warning("Unable to send Plugin Message - No players online.(" + packet.getClass() + ")");
					return;
				}
				p.sendPluginMessage(plugin, Protocol.ChannelTag, data);
			}
		}.runTaskAsynchronously(plugin);
	}

	// TODO auto unregister listener when pluginUnloadEvent

}
