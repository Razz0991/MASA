package au.com.mineauz.masa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ConfigSave {
	private FileConfiguration save = null;
	private File saveFile = null;
	private MASA plugin = MASA.plugin;
	private String name = null;
	
	public ConfigSave(String name){
		this.name = name;
		reloadFile();
		saveConfig();
	}
	
	public void reloadFile(){
		if(saveFile == null){
			saveFile = new File(plugin.getDataFolder() + "/", name + ".yml");
		}
		save = YamlConfiguration.loadConfiguration(saveFile);
	}
	
	public FileConfiguration getConfig(){
		if(save == null){
			reloadFile();
		}
		return save;
	}
	
	public void saveConfig(){
		if(save == null || saveFile == null){
			return;
		}
		try{
			save.save(saveFile);
		}
		catch(IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save " + name + " config file!");
		}
	}
	
	public void saveDestinationLotation(Player player, String[] tags){
		int number = 0;
		if(save.getConfigurationSection("destinations") != null){
			Set<String> numbers = save.getConfigurationSection("destinations").getKeys(false);
			number = numbers.size();
		}
		
		save.set("destinations." + number + ".x", player.getLocation().getX());
		save.set("destinations." + number + ".y", player.getLocation().getY());
		save.set("destinations." + number + ".z", player.getLocation().getZ());
		save.set("destinations." + number + ".world", player.getLocation().getWorld().getName());
		
		saveDestinationTags(tags, number);
	}
	
	public void saveDestinationTags(String[] tags, Integer number){
		List<String> list = new ArrayList<String>();
		for(String tag : tags){
			list.add(tag);
		}
		save.set("destinations." + number + ".tags", list);
	}
	
	public Location loadDestinationLocation(Integer number) {
		Double locx = (Double) save.get("destinations." + number + ".x");
		Double locy = (Double) save.get("destinations." + number + ".y");
		Double locz = (Double) save.get("destinations." + number + ".z");
		String world = (String) save.get("destinations." + number + ".world");
		
		Location loc = new Location(plugin.getServer().getWorld(world), locx, locy, locz);
		return loc;
	}
	
	public String[] loadDestinationTags(Integer number){
		List<String> list = save.getStringList("destinations." + number + ".tags");
		String[] tags = new String[list.size()];
		
		int count = 0;
		for(String tag : list){
			tags[count] = tag;
			count += 1;
		}
		
		return tags;
	}
}
