package au.com.mineauz.masa.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import au.com.mineauz.masa.ConfigSave;
import au.com.mineauz.masa.MASA;
import au.com.mineauz.masa.MASAUtils;

public class Memories {
	
	private ConfigSave memories;
	static Memories memory;
	
	private static Reports reports;
	private Notifier notifier;
	
	private Map<Player, Integer> croptrample = new HashMap<Player, Integer>();
	private List<String> messages;
	private int repeatingBroadcast;
	private String[] copiedSign = new String[4];
	private int nextMsgInt = 0;
	
	public Memories(){
		memories = new ConfigSave("memory");
		memory = this;
		
		reports = new Reports();
		notifier = new Notifier();
		
		messages = memories.getConfig().getStringList("memories.broadcasts");
		
		repeatingBroadcast = MASA.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(MASA.plugin, new Runnable() {
			
			@Override
			public void run() {
				if(!messages.isEmpty() && MASA.plugin.getServer().getOnlinePlayers().length > 0){
					MASAUtils.chatMessage(messages.get(nextMsgInt), null, null);
					nextMsgInt++;
					if(nextMsgInt >= messages.size()){
						nextMsgInt = 0;
					}
				}
			}
		}, 18000L, 18000L);
	}
	
	public void saveMemories(){
		memories.saveConfig();
	}
	
	public Configuration getMemories(){
		return memories.getConfig();
	}
	
	public Reports getReports(){
		return reports;
	}
	
	public Notifier getNotifier(){
		return notifier;
	}
	
	public Integer addPlayerTrample(Player ply){
		if(!croptrample.containsKey(ply)){
			croptrample.put(ply, 0);
		}
		croptrample.put(ply, croptrample.get(ply) + 1);
		return croptrample.get(ply);
	}
	
	public void resetPlayerTrample(Player ply){
		croptrample.remove(ply);
	}
	
	public void addBroadcastMessage(String message){
		if(messages == null){
			messages = new ArrayList<String>();
		}
		messages.add(message);
		memories.getConfig().set("memories.broadcasts", messages);
	}
	
	public String getBroadcastMessage(int id){
		if(id < messages.size()){
			return messages.get(id);
		}
		return null;
	}
	
	public boolean removeBroadcastMessage(int id){
		if(id < messages.size()){
			messages.remove(id);
			memories.getConfig().set("memories.broadcasts", messages);
			return true;
		}
		return false;
	}
	
	public Integer getRepeatingBroadcast(){
		return repeatingBroadcast;
	}

	public String[] getCopiedSign() {
		return copiedSign;
	}

	public void setCopiedSign(String[] copiedSign) {
		this.copiedSign = copiedSign;
	}
}
