package au.com.mineauz.masa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.masa.commands.Commands;
import au.com.mineauz.masa.pathfinding.Destination;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class MASA extends JavaPlugin{
	
	public static MASA plugin;
	private MatchBranch mainBranch;
    public static WorldGuardPlugin wgplugin = null;
    public static boolean hasWorldGuard = false;
    private static MASAData data;
    private static Commands commands;
    private ConfigSave destinationData;
	
	public void onEnable(){
		plugin = this;
		mainBranch = new MatchBranch("main");
		
		loadConfig();

		data = new MASAData();
		commands = new Commands();
		
		loadResponses();
		
		destinationData = new ConfigSave("destinations");
		
		try{
			Set<String> dests = destinationData.getConfig().getConfigurationSection("destinations").getKeys(false);
			for(String dest : dests){
				Destination d = new Destination(destinationData.loadDestinationLocation(Integer.parseInt(dest)), destinationData.loadDestinationTags(Integer.parseInt(dest)));
				data.getDestinations().addDestination(d);
			}
		}
		catch(NullPointerException e){
			getLogger().info("No locations saved.");
		}
		
		getServer().getPluginManager().registerEvents(new MASAEvents(), this);
		
		if(getWorldGuard() != null){
			 wgplugin = getWorldGuard();
			 hasWorldGuard = true;
		 }
		
		getLogger().info("sucessfully enabled!");
	}
	
	public WorldGuardPlugin getWorldGuard(){
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin)))
        {
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }
	
	public void onDisable(){
		getData().getMemory().saveMemories();
		getLogger().info("sucessfully disabled!");
	}
	
	public MatchBranch getMainBranch(){
		return mainBranch;
	}
	
	public static MASAData getData(){
		return data;
	}
	
	public static Commands getCommands(){
		return commands;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		if(player != null && cmd.getName().equalsIgnoreCase("masa")){
			if(args.length > 1 && args[0].equalsIgnoreCase("dest")){
				if(args.length >= 2 && args[1].equalsIgnoreCase("add") && player.hasPermission("masa.destination.add")){
					String[] tags = new String[args.length - 2];
					String stags = "";
					for(int i = 2; i < args.length; i++){
						tags[i - 2] = args[i];
						stags = stags + args[i] + " ";
					}
					
					data.getDestinations().addDestination(new Destination(player.getLocation().getBlock().getLocation(), tags));
					destinationData.saveDestinationLotation(player, tags);
					destinationData.saveConfig();
					
					player.sendMessage(ChatColor.GRAY + "Added the current location using the tags: " + stags);
					return true;
				}
				else if(args[1].equalsIgnoreCase("remove") && args.length >= 3 && player.hasPermission("masa.destination.remove")){
					if(data.getDestinations().removeDestination(args[2])){
						player.sendMessage(ChatColor.GRAY + "Successfully removed " + args[2]);
					}
					else{
						player.sendMessage(ChatColor.RED + "No such location: " + args[2]);
					}
					return true;
				}
				else{
					player.sendMessage(ChatColor.GRAY + "Invalid destination command, use: add, remove");
					return true;
				}
			}
			else if(args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("masa.reload")){
				loadConfig();
				loadResponses();
				getData().getCurses().clear();
				getData().getCurses().addAll(getConfig().getStringList("curseWords"));
				
				player.sendMessage(ChatColor.GREEN + "Reloaded MASA's configs!");
				return true;
			}
			else if(args.length > 2 && args[0].equalsIgnoreCase("broadcast") && player.hasPermission("masa.broadcast.set")){
				if(args[1].equalsIgnoreCase("add") && args.length > 3){
					String message = "";
					for(int i = 2; i < args.length; i++){
						message += args[i];
						if(i < args.length - 1){
							message += " ";
						}
					}
					getData().getMemory().addBroadcastMessage(message);
					getData().getMemory().saveMemories();
					player.sendMessage(ChatColor.GRAY + "Added broadcast to MASA");
				}
				else if(args[1].equalsIgnoreCase("remove") && args.length == 3){
					if(args[2].matches("[0-9]+")){
						getData().getMemory().removeBroadcastMessage(Integer.parseInt(args[2]));
						player.sendMessage(ChatColor.GRAY + "Removed broadcast from MASA");
					}
					else{
						player.sendMessage(ChatColor.RED + args[2] + " is not a number!");
					}
				}
				else if(args[1].equalsIgnoreCase("get") && args.length == 3){
					if(args[2].matches("[0-9]+")){
						getData().getMemory().getBroadcastMessage(Integer.parseInt(args[2]));
						player.sendMessage(ChatColor.GRAY + getData().getMemory().getBroadcastMessage(Integer.parseInt(args[2])));
					}
					else{
						player.sendMessage(ChatColor.RED + args[2] + " is not a number!");
					}
				}
				return true;
			}
			else if(args.length == 2 && args[0].equalsIgnoreCase("blocklogger") && player.hasPermission("masa.admin")){
				String plyname = args[1];
				String finalPly = null;
				Set<String> plys = getData().getBlockLogger().getAllPlayers();
				for(String ply : plys){
					if(ply.toLowerCase().contains(plyname)){
						finalPly = ply;
						break;
					}
				}
				if(finalPly != null){
					player.sendMessage(ChatColor.BLUE + "DPH: " + ChatColor.WHITE + getData().getBlockLogger().getPlayer(finalPly).getDiamondPerHour());
					player.sendMessage(ChatColor.BLUE + "Last Location: " + ChatColor.WHITE + getData().getBlockLogger().getPlayer(finalPly).getLastDiamondLocation());
					player.sendMessage(ChatColor.BLUE + "GPH: " + ChatColor.WHITE + getData().getBlockLogger().getPlayer(finalPly).getGoldPerHour());
					player.sendMessage(ChatColor.BLUE + "Last Location: " + ChatColor.WHITE + getData().getBlockLogger().getPlayer(finalPly).getLastGoldLocation());
				}
				else{
					player.sendMessage("This user has no data recorded currently.");
				}
				return true;
			}
			else if(args.length > 1 && args[0].equalsIgnoreCase("say") && player.hasPermission("masa.say")){
				String msg = "";
				for(int i = 1; i < args.length; i++){
					msg += args[i];
					if(i != args.length - 1){
						msg += " ";
					}
					MASAUtils.chatMessage(msg, player, null);
				}
			}
		}
		else if(player != null && cmd.getName().equalsIgnoreCase("lot") && args.length > 1 && hasWorldGuard){
			if(args[0].equalsIgnoreCase("clear") && args.length == 2){
				if(hasWorldGuard){
					if(MASAUtils.clearLot(args[1], player)){
						player.sendMessage(ChatColor.GREEN + "Cleared all players from " + args[1]);
					}
					else{
						player.sendMessage(ChatColor.RED + "There is no region called " + args[1]);
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "You do not have WorldGuard!");
				}
				return true;
			}
			else{
				player.sendMessage(ChatColor.RED + "Invalid arguments!");
				player.sendMessage(ChatColor.GRAY + "Possible arguments: clear");
				return true;
			}
		}
		else if(player != null && cmd.getName().equalsIgnoreCase("sign") && args.length >= 1){
			Block target = player.getTargetBlock(null, 20);
			if(target.getType() == Material.SIGN_POST || target.getType() == Material.WALL_SIGN){
				Sign sign = (Sign)target.getState();
				if(args[0].equalsIgnoreCase("set") && args.length > 2){
					if(args[1].matches("[1-4]")){
						String sentence = "";
						for(int i = 2; i < args.length; i++){
							if(i != 2){
								sentence += " ";
							}
							sentence += args[i];
						}
						
						sign.setLine(Integer.parseInt(args[1]) - 1, ChatColor.translateAlternateColorCodes("&".charAt(0), sentence));
						
						sign.update();
						return true;
					}
					else{
						player.sendMessage(ChatColor.RED + "Invalid command! Please use the following:");
						player.sendMessage("/sign set <1-4> <Message here>");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("clear")){
					for(int i = 0; i < 4; i++){
						sign.setLine(i, "");
					}
					sign.update();
					return true;
				}
			}
		}
		else if(player != null && cmd.getName().equalsIgnoreCase("meta") && args.length > 1){
			if(player.getItemInHand() != null){
				ItemStack item = player.getItemInHand();
				if(args.length > 1 && args[0].equalsIgnoreCase("name")){
					ItemMeta meta = item.getItemMeta();
					if(args[1].equalsIgnoreCase("CLEAR")){
						meta.setDisplayName("");
					}
					else{
						String name = "";
						for(int i = 1; i < args.length; i++){
							name += args[i];
							if(i < args.length - 1){
								name += " ";
							}
						}
						meta.setDisplayName(ChatColor.translateAlternateColorCodes("&".charAt(0), name));
					}
					item.setItemMeta(meta);
				}
				else if(args.length > 1 && args[0].equalsIgnoreCase("lore")){
					ItemMeta meta = item.getItemMeta();
					if(args[1].equals("CLEAR")){
						meta.setLore(null);
					}
					else if(args[1].equals("CLEARLAST")){
						if(meta.getLore() != null){
							List<String> loreList = new ArrayList<String>();
							loreList.addAll(meta.getLore());
							loreList.remove(meta.getLore().size() - 1);
							meta.setLore(loreList);
						}
					}
					else{
						String lore = "";
						for(int i = 1; i < args.length; i++){
							lore += args[i];
							if(i < args.length - 1){
								lore += " ";
							}
						}
						
						List<String> loreList = new ArrayList<String>();
						if(meta.getLore() != null){
							loreList.addAll(meta.getLore());
						}
						loreList.add(ChatColor.translateAlternateColorCodes("&".charAt(0), lore));
						
						meta.setLore(loreList);
					}
					item.setItemMeta(meta);
				}
				else if(args.length > 1 && args[0].equalsIgnoreCase("potion") && item.getItemMeta() instanceof PotionMeta){
					PotionMeta meta = (PotionMeta) item.getItemMeta();
					if(args[1].equals("CLEAR")){
						meta.clearCustomEffects();
					}
					else if(args[1].equals("CLEARLAST")){
						if(meta.getCustomEffects() != null){
							List<PotionEffect> potlist = new ArrayList<PotionEffect>();
							potlist.addAll(meta.getCustomEffects());
							potlist.remove(potlist.size() - 1);
							meta.clearCustomEffects();
							for(PotionEffect ef : potlist){
								meta.addCustomEffect(ef, true);
							}
						}
					}
					else{
						if(PotionEffectType.getByName(args[1]) != null || (args[1].matches("[0-9]+") && PotionEffectType.getById(Integer.parseInt(args[1])) != null)){
							PotionEffectType eff = PotionEffectType.getByName(args[1]);
							if(args[1].matches("[0-9]+"))
								eff = PotionEffectType.getById(Integer.parseInt(args[1]));
							int duration = 60 * 20;
							int amp = 1;
							if(args.length > 2 && args[2].matches("[0-9]+")){
								duration = Integer.parseInt(args[2]) * 20;
							}
							if(args.length > 3 && args[3].matches("[0-9]+")){
								amp = Integer.parseInt(args[3]);
							}
							PotionEffect effect = new PotionEffect(eff, duration, amp);
							meta.addCustomEffect(effect, true);
						}
					}
					item.setItemMeta(meta);
				}
				else{
					player.sendMessage(ChatColor.RED + "Invalid Argument! Possible: name, lore, potion");
				}
			}
			return true;
		}
		else if(player != null && cmd.getName().equalsIgnoreCase("owner")){
			if(args.length >= 1){
				if(player.getVehicle() != null && player.getVehicle() instanceof Horse){
					Horse horse = (Horse)player.getVehicle();
					final List<Player> plys = getServer().matchPlayer(args[0]);
					if(!plys.isEmpty()){
						horse.setOwner(plys.get(0));
						
						player.sendMessage(ChatColor.GREEN + "You've set this horses owner to " + plys.get(0).getName());
						plys.get(0).sendMessage(ChatColor.GREEN + "You now own one of " + player.getName() + "'s horses.");
					}
					else{
						player.sendMessage(ChatColor.RED + "Could not find a player by the name " + args[0]);
					}
				}
				else{
					player.sendMessage(ChatColor.RED + "You must be riding a horse you own to use this command!");
				}
			}
			else{
				player.sendMessage(ChatColor.GRAY + "Ride a horse you own and type the following command to change its owner:");
				player.sendMessage(ChatColor.GRAY + "/owner <PlayerName>");
			}
			return true;
		}
		return false;
	}
	
	private void loadResponses(){
		ConfigSave responses = new ConfigSave("responses");
		if(responses.getConfig().getKeys(false).size() == 0){
			InputStream is = getClassLoader().getResourceAsStream("responses.yml");
			OutputStream os = null;
			try {
				os = new FileOutputStream(getDataFolder() + "/responses.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			byte[] buffer = new byte[4096];
			int length;
			try {
				while ((length = is.read(buffer)) > 0) {
				    os.write(buffer, 0, length);
				}
				
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			responses.saveConfig();
		}
		
		Set<String> keys = responses.getConfig().getKeys(true);
		for(String key : keys){
			if(key.contains("|")){
				Set<String> keyList = new HashSet<String>();
				Set<String> keyListTemp = new HashSet<String>();
				keyListTemp.add(key);
				
				while(!keyListTemp.isEmpty()){
					Set<String> tempKeys = new HashSet<String>();
					for(String keyTemp : keyListTemp){
						String dupeKey = keyTemp;
						String[] splitKey = keyTemp.split("\\.");
						for(String skey : splitKey){
							if(skey.contains("|")){
								String[] skeySplit = skey.split("\\|");
								for(String skeys : skeySplit){
									String newdupeKey = dupeKey.replace(skey, skeys);
									if(newdupeKey.contains("|")){
										tempKeys.add(newdupeKey);
									}
									else{
										keyList.add(newdupeKey);
									}
								}
							}
						}
					}
					keyListTemp.clear();
					keyListTemp.addAll(tempKeys);
				}
				
				for(String lkey : keyList){
					String[] skeys = lkey.split("\\.");
					MatchBranch curBranch = mainBranch;
					for(String skey : skeys){
						if(curBranch.hasBranches()){
							boolean hasBranch = true;
							for(MatchBranch branch : curBranch.getBranches()){
								if(branch.getBranchName().equals(skey)){
									curBranch = branch;
									hasBranch = true;
									break;
								}
								else{
									hasBranch = false;
								}
							}
							if(!hasBranch){
								MatchBranch newBranch = new MatchBranch(skey);
								if(!responses.getConfig().getStringList(key).isEmpty()){
									newBranch.setResponses(responses.getConfig().getStringList(key));
								}
								curBranch.addBranch(newBranch);
								curBranch = newBranch;
							}
						}
						else{
							MatchBranch newBranch = new MatchBranch(skey);
							curBranch.addBranch(newBranch);
							if(!responses.getConfig().getStringList(key).isEmpty()){
								newBranch.setResponses(responses.getConfig().getStringList(key));
							}
							curBranch = newBranch;
						}
					}
				}
			}
			else{
				String[] skeys = key.split("\\.");
				MatchBranch curBranch = mainBranch;
				for(String skey : skeys){
					if(curBranch.hasBranches()){
						boolean hasBranch = true;
						for(MatchBranch branch : curBranch.getBranches()){
							if(branch.getBranchName().equals(skey)){
								curBranch = branch;
								hasBranch = true;
								break;
							}
							else{
								hasBranch = false;
							}
						}
						if(!hasBranch){
							MatchBranch newBranch = new MatchBranch(skey);
							if(!responses.getConfig().getStringList(key).isEmpty()){
								newBranch.setResponses(responses.getConfig().getStringList(key));
							}
							curBranch.addBranch(newBranch);
							curBranch = newBranch;
						}
					}
					else{
						MatchBranch newBranch = new MatchBranch(skey);
						if(!responses.getConfig().getStringList(key).isEmpty()){
							newBranch.setResponses(responses.getConfig().getStringList(key));
						}
						curBranch.addBranch(newBranch);
						curBranch = newBranch;
					}
				}
			}
		}
	}
	
	private void loadConfig(){
		try{
			this.getConfig().load(this.getDataFolder() + "/config.yml");
		}
		catch(FileNotFoundException ex){
			getLogger().info("Failed to load config, creating one.");
			InputStream is = getClassLoader().getResourceAsStream("config.yml");
			OutputStream os = null;
			try {
				os = new FileOutputStream(getDataFolder() + "/config.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			byte[] buffer = new byte[4096];
			int length;
			try {
				while ((length = is.read(buffer)) > 0) {
				    os.write(buffer, 0, length);
				}
				
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try{
				getConfig().load(getDataFolder() + "/config.yml");
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		catch(Exception e){
			getLogger().log(Level.SEVERE, "Failed to load config!");
			e.printStackTrace();
		}
	}
}
