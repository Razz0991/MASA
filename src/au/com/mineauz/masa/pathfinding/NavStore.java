package au.com.mineauz.masa.pathfinding;

import java.util.ArrayList;
import java.util.List;


public class NavStore {
	
	private List<NavNode> list;
	//private Main plugin;
	
	public NavStore(){
		list = new ArrayList<NavNode>();
		//plugin = Main.plugin;
	}
	
	public void addNode(NavNode node){
		if(list.isEmpty()){
			list.add(node);
		}/*
		else if(node.getF() < list.get(0).getF()){
			plugin.log.info("Node F: " + node.getF() + " List F: " + list.get(0).getF());
			list.add(0, node);
		}*/
		else{
			int loop = 0;
			while(loop < list.size() && node.getF() > list.get(loop).getF()){
				loop += 1;
			}
			list.add(loop, node);
		}
	}
	
	public List<NavNode> getNodes(){
		return list;
	}
	
	public void removeNode(NavNode nav){
		list.remove(nav);
	}
}
