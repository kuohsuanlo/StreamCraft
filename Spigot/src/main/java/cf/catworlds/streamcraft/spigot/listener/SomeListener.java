package cf.catworlds.streamcraft.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.server.ServerCommandEvent;

import cf.catworlds.streamcraft.spigot.MainClass;

public class SomeListener implements Listener {

	MainClass plugin;
	int test;

	public SomeListener(MainClass pl, int test) {
		this.plugin = pl;
		this.test = test;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void someListener(PlayerCommandPreprocessEvent e) {

	}

	@EventHandler
	public void pmListener(PlayerChannelEvent e) {
		Player p = e.getPlayer();
		plugin.log("pmListenerEvent:", p.getName(), e.getChannel());
	}

	@EventHandler
	public void pmrListener(PlayerRegisterChannelEvent e) {
		Player p = e.getPlayer();
		plugin.log("pmRListenerEvent:", p.getName(), e.getChannel());
	}

	@EventHandler
	public void consoleCommand(ServerCommandEvent event) {
		plugin.log2(test + event.getEventName());
	}

}
