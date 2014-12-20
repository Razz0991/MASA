package au.com.mineauz.masa.pathfinding;

import org.bukkit.Location;

public class NavNode {
	
	private Location node;
	private NavNode parent;
	private Integer f;
	private Integer g;
	
	public NavNode(Location loc, NavNode parent, Integer f, Integer g){
		node = loc;
		this.parent = parent;
		this.f = f;
		this.g = g;
	}
	
	public Integer getF(){
		return f;
	}
	
	public void setF(Integer f){
		this.f = f;
	}
	
	public Integer getG(){
		return g;
	}
	
	public void setG(Integer g){
		this.g = g;
	}
	
	public NavNode getParent(){
		return parent;
	}
	
	public void setParent(NavNode par){
		parent = par;
	}
	
	public Location getNode(){
		return node;
	}
	
	public NavNode clone(){
		return new NavNode(node.clone(), parent, f, g);
	}
}
