package au.com.mineauz.masa.commands;

import org.bukkit.entity.Player;

public interface BaseCommand {
	
	public String getName();
	
	public String getPermission();
	
	public void execute(Player player, String chat, boolean privateChat);
}
