package au.com.mineauz.masa.pathfinding;

import org.bukkit.Location;

public class Destination {
	private Location destination;
	private String[] tags;
	
	public Destination(Location loc, String[] tags){
		destination = loc;
		this.tags = tags;
	}

	public Location getDestination() {
		return destination;
	}

	public String[] getTags() {
		return tags;
	}
}
