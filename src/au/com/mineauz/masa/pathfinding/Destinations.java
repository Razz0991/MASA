package au.com.mineauz.masa.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;


public class Destinations {
	private List<Destination> list;
	private Map<String, List<Integer>> tags;
	
	public Destinations(){
		list = new ArrayList<Destination>();
		tags = new HashMap<String, List<Integer>>();
	}
	
	public void addDestination(Destination dest){
		list.add(dest);
		for(String tag : dest.getTags()){
			if(!tags.containsKey(tag)){
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(list.size() - 1);
				tags.put(tag, ids);
			}
			else{
				tags.get(tag).add(list.size() - 1);
			}
		}
	}
	
	public List<Destination> getAllDestinations(){
		return list;
	}
	
	public Destination getDestination(String tag, Player player){
		List<Integer> ids = tags.get(tag);
		List<Destination> dests = new ArrayList<Destination>();
		
		for(int id : ids){
			dests.add(list.get(id));
		}
		
		int shortest = -1;
		Destination closest = null;
		
		for(Destination dest : dests){
			int dist = (int) (Math.sqrt(Math.pow(dest.getDestination().getX() - player.getLocation().getX(), 2) + Math.pow(dest.getDestination().getZ() - player.getLocation().getZ(), 2) + Math.pow(dest.getDestination().getY() - player.getLocation().getY(), 2)));
			if (shortest == -1 || shortest > dist){
				shortest = dist;
				closest = dest;
			}
		}
		
		return closest;
	}
	
	public boolean hasDestination(String tag){
		if(tags.containsKey(tag)){
			return true;
		}
		return false;
	}
	
	public boolean removeDestination(String tag){
		if(tags.containsKey(tag)){
			tags.remove(tag);
			return true;
		}
		return false;
	}
}
