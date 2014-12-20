package au.com.mineauz.masa.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;
import au.com.mineauz.masa.MASAUtils;


public class Pathfinder extends Thread{
	private Player player;
	private List<String> unwalkable = new ArrayList<String>();
	private List<String> ignored = new ArrayList<String>();
	private Location beginpos;
	private Location destination;
	private NavStore openList = new NavStore();
	private NavStore closedList = new NavStore();
	private int prevdata = 0;
	private NavNode curnode;
	private NavNode smallCurNode;
	private static MASA plugin = MASA.plugin;
	
	boolean noPath = false;
	boolean notInRange = false;
	boolean running = true;
	
	public Pathfinder(Player player, Location destination){
		this.player = player;
		this.destination = destination.getBlock().getLocation();
		beginpos = player.getLocation().getBlock().getLocation();
		unwalkable.add("FENCE");
		unwalkable.add("STATIONARY_LAVA");
		unwalkable.add("COBBLE_WALL");
		
		ignored.add("AIR");
		ignored.add("STATIONARY_WATER");
		ignored.add("WATER");
		ignored.add("WOODEN_DOOR");
		ignored.add("IRON_DOOR");
		ignored.add("DEAD_BUSH");
		ignored.add("LONG_GRASS");
		ignored.add("PORTAL");
		ignored.add("TORCH");
		ignored.add("SUGAR_CANE_BLOCK");
		ignored.add("CROPS");
		ignored.add("SAPLING");
		ignored.add("FENCE_GATE");
		ignored.add("SIGN_POST");
		ignored.add("WALL_SIGN");
		ignored.add("SNOW");
		ignored.add("RAILS");
		ignored.add("POWERED_RAIL");
		ignored.add("DETECTOR_RAIL");
		ignored.add("WOOD_PLATE");
		ignored.add("STONE_PLATE");
		ignored.add("VINE");
		ignored.add("REDSTONE");
		ignored.add("LEVER");
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Location getDestination(){
		return destination;
	}
	
	public void run(){
		int ms = 0;
		while(running){
			closedList.addNode(new NavNode(beginpos, null, 0, 0));
			prevdata = 0;
			curnode = closedList.getNodes().get(0);
			
			NavNode newnode;
			newnode = curnode.clone();
			
			while(!compareLoc(curnode.getNode(), destination) && !noPath){
				for(int i = 1; i <= 4; i++){
					newnode = curnode.clone();
					
					switch(i){
					case 1 : nodeAdd(newnode, 1, 0);
					break;
					case 2 : nodeAdd(newnode, -1, 0);
					break;
					case 3 : nodeAdd(newnode, 0, 1);
					break;
					case 4 : nodeAdd(newnode, 0, -1);
					break;
					case 5 : nodeAdd(newnode, 1, 1);
					break;
					case 6 : nodeAdd(newnode, 1, -1);
					break;
					case 7 : nodeAdd(newnode, -1, 1);
					break;
					case 8 : nodeAdd(newnode, -1, -1);
					break;
					}
				}
				
				List<NavNode> open = openList.getNodes();
				
				if(smallCurNode != null){
					if(smallCurNode.getF() > open.get(0).getF()){
						smallCurNode = open.get(0);
					}
					
					openList.removeNode(smallCurNode);
					closedList.addNode(smallCurNode);
					
					curnode = smallCurNode.clone();
					prevdata = smallCurNode.getG();
					smallCurNode = null;
					
					if(curnode.getF() > 256){
						noPath = true;
						notInRange = true;
					}
				}
				else if(!openList.getNodes().isEmpty()){
					closedList.addNode(open.get(0));
					
					curnode = open.get(0).clone();
					prevdata = open.get(0).getG();
					open.remove(0);
					
					if(curnode.getF() > 600){
						noPath = true;
						notInRange = true;
					}
				}
				else{
					noPath = true;
				}
				
				ms += 1;
				if(ms >= 25000){
					noPath = true;
				}
			}
			
			if(compareLoc(curnode.getNode(), destination) && !noPath){
				curnode.getNode().setY(curnode.getNode().getY() - 1);
				//player.sendBlockChange(curnode.getNode(), Material.GLOWSTONE, (byte) 0);
				List<NavNode> locs = new ArrayList<NavNode>();
				while(curnode.getParent() != null){
					locs.add(curnode);
					curnode = curnode.getParent();
					curnode.getNode().setY(curnode.getNode().getY() - 1);
				}
				Collections.reverse(locs);
				
				int time = 0;
				int sector = 0;
				int count = 0;
				boolean on = true;
				while(time < 120){
					if(sector >= 6){
						sector = 0;
					}
					
					for(NavNode node : locs){
						if(count >= 6){
							count = 0;
						}
						
						if((count - sector >= 0 && count - sector < 4) || (count - sector > -6 && count - sector < -2)){
							on = true;
						}
						else{
							on = false;
						}
						
						if(on){
							player.sendBlockChange(node.getNode(), Material.DIAMOND_BLOCK, (byte) 0);
						}
						else{
							player.sendBlockChange(node.getNode(), node.getNode().getBlock().getType(), node.getNode().getBlock().getData());
						}

						count++;
					}
					
					try{
						Thread.sleep(1000);
					}
					catch(InterruptedException e){
						plugin.getLogger().severe("Failed to sleep after placing diamond path");
					}
					count = 0;
					sector++;
					time++;
				}
				
				for(NavNode nav : locs){
					player.sendBlockChange(nav.getNode(), nav.getNode().getBlock().getType(), nav.getNode().getBlock().getData());
				}
			}
			else if(noPath){
				if(!notInRange){
					MASAUtils.chatMessage("Sorry %player, I can't find a path from your current position.", player, null);
				}
				else{
					MASAUtils.chatMessage("Sorry %player, thats out of my path finding range.", player, null);
				}
			}
			running = false;
		}
	}
	
	public void nodeAdd(NavNode newnode, int xdir, int zdir){
		newnode.getNode().setX(curnode.getNode().getX() + xdir);
		newnode.getNode().setZ(curnode.getNode().getZ() + zdir);
		boolean inopenlist = false;
		boolean inclosedlist = false;
		boolean diagmove = false;
		
		if(xdir != 0 && zdir != 0){
			diagmove = true;
		}
		
		NavNode ndu = newnode.clone();
		ndu.getNode().setY(ndu.getNode().getY() + 1);
		NavNode ndd = newnode.clone();
		ndd.getNode().setY(ndd.getNode().getY() - 1);
		int falldistance = 0;
		int jump = 0;
		
		if(ndu.getNode().getBlock().getType() == Material.AIR && !ignored.contains(newnode.getNode().getBlock().getType().toString())){
			newnode = ndu;
			jump = 1;
		}
		
		if(ignored.contains(ndd.getNode().getBlock().getType().toString())){
			while(ignored.contains(ndd.getNode().getBlock().getType().toString()) && falldistance < 5){
				ndd.getNode().setY(ndd.getNode().getY() - 1);
				falldistance += 1;
			}
			ndd.getNode().setY(ndd.getNode().getY() + 1);
			newnode = ndd;
		}
		
		ndu = newnode.clone();
		ndu.getNode().setY(ndu.getNode().getY() + 1);
		ndd = newnode.clone();
		ndd.getNode().setY(ndd.getNode().getY() - 1);
		
		List<NavNode> ol = openList.getNodes();
		List<NavNode> cl = closedList.getNodes();
		for(NavNode loc : ol){
			if(compareLoc(loc.getNode(), newnode.getNode())){
				inopenlist = true;
			}
		}
		
		for(NavNode loc : cl){
			if(compareLoc(loc.getNode(), newnode.getNode())){
				inclosedlist = true;
			}
		}
		
		if(!unwalkable.contains(ndd.getNode().getBlock().getType().toString()) && !inopenlist && !inclosedlist && falldistance < 3){
			if((ignored.contains(newnode.getNode().getBlock().getType().toString()) && ignored.contains(ndu.getNode().getBlock().getType().toString()))){
				if((falldistance > 0 && !diagmove) || (falldistance == 0 && (diagmove || !diagmove))){
					if((jump == 1 && !diagmove) || (jump == 0 && (diagmove || !diagmove))){
						int g = prevdata + 1 + jump - falldistance;
						int h = (int) (Math.sqrt(Math.pow(destination.getX() - newnode.getNode().getX(), 2) + Math.pow(destination.getZ() - newnode.getNode().getZ(), 2) + Math.pow(destination.getY() - newnode.getNode().getY(), 2)));
						int f = g + h;
						
						newnode.setF(f);
						newnode.setG(g);
						newnode.setParent(curnode);
						
						openList.addNode(newnode);
						if(smallCurNode == null || newnode.getF() < smallCurNode.getF()){
							smallCurNode = newnode.clone();
						}
					}
				}
			}
		}
	}
	
	public String[] locToData(Location loc){
		String[] arr = new String[4];
		arr[0] = String.valueOf(loc.getBlockX());
		arr[1] = String.valueOf(loc.getBlockY());
		arr[2] = String.valueOf(loc.getBlockZ());
		arr[3] = loc.getWorld().getName();
		return arr;
	}
	
	public Location dataToLoc(String[] data){
		Location loc = new Location(plugin.getServer().getWorld(data[3]), Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
		
		return loc;
	}
	
	public boolean compareLoc(Location loc1, Location loc2){
		if(loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ()){
			return true;
		}
		return false;
	}
}
