package au.com.mineauz.masa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.logging.BlockLogger;
import au.com.mineauz.masa.memory.Memories;
import au.com.mineauz.masa.pathfinding.Destinations;

public class MASAData {
	private Map<Player, Integer> playerWarnings = new HashMap<Player, Integer>();
	private int maxWarnings = 3;
	private String jailCommand = "jail %player sjail1";
	private Destinations destinations = new Destinations();
	private List<String> curses = new ArrayList<String>();
	private Memories memory;
	private BlockLogger blockLogger;
	
	public MASAData(){
		maxWarnings = MASA.plugin.getConfig().getInt("autoJail.warnings");
		jailCommand = MASA.plugin.getConfig().getString("autoJail.jailCommand");
		
		curses.addAll(MASA.plugin.getConfig().getStringList("curseWords"));
		
		memory = new Memories();
		blockLogger = new BlockLogger();
	}
	
	public boolean addPlayerWarning(Player player){
		if(!playerWarnings.containsKey(player)){
			playerWarnings.put(player, 1);
			return false;
		}
		else{
			playerWarnings.put(player, playerWarnings.get(player) + 1);
			if(playerWarnings.get(player) >= maxWarnings){
				return true;
			}
			return false;
		}
	}
	
	public void removePlayerWarnings(Player player){
		if(playerWarnings.containsKey(player)){
			playerWarnings.remove(player);
		}
	}
	
	public void jailPlayer(Player player){
		MASA.plugin.getServer().dispatchCommand(MASA.plugin.getServer().getConsoleSender(), jailCommand.replace("%player", player.getName()));
	}
	
	public Destinations getDestinations(){
		return destinations;
	}
	
	public List<String> getCurses(){
		return curses;
	}
	
	public Memories getMemory(){
		return memory;
	}
	
	public BlockLogger getBlockLogger(){
		return blockLogger;
	}
}
