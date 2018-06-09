package cf.catworlds.streamcraft.bungee.listener;

import cf.catworlds.streamcraft.bungee.MainClass;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SomeListener implements Listener {

	MainClass plugin;

	public SomeListener(MainClass pl) {
		this.plugin = pl;
	}

	@EventHandler
	public void onPostLogin(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		plugin.getProxy().getLogger().info(player.getName() + " : " + event.getMessage());
	}

}
