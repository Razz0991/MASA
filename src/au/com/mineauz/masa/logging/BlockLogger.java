package au.com.mineauz.masa.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public class BlockLogger {
	private Map<String, BlockTable> logger = new HashMap<String, BlockTable>();
	
	public void addPlayer(Player ply){
		logger.put(ply.getName(), new BlockTable(ply));
	}
	
	public BlockTable getPlayer(Player ply){
		return logger.get(ply.getName());
	}
	
	public BlockTable getPlayer(String ply){
		return logger.get(ply);
	}
	
	public boolean hasPlayer(Player ply){
		if(logger.containsKey(ply.getName())){
			return true;
		}
		return false;
	}
	
	public Set<String> getAllPlayers(){
		return logger.keySet();
	}
}
