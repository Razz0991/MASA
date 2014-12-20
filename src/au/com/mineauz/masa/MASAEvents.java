package au.com.mineauz.masa;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MASAEvents implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerChat(AsyncPlayerChatEvent event){
		if(event.isCancelled())
			return;
		
		String[] words = event.getMessage().split(" ");
		String finishedmsg = "";
		for(String word : words){
			if(!finishedmsg.equals("")){
				finishedmsg += " ";
			}
			for(String curse : MASA.getData().getCurses()){
				word = word.replaceAll("(?i)\\b" + curse + "\\b", "****");
			}
			finishedmsg += word;
		}
		
		event.setMessage(finishedmsg);
		
		String tempChat = event.getMessage().toLowerCase();
		String[] tempChatarr = tempChat.split(" ");

		if(tempChat.startsWith("@masa ")){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.GRAY + "[Me -> " + ChatColor.LIGHT_PURPLE + "Masa" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getMessage().substring(6, event.getMessage().length()));
			if(MASAUtils.canPatternMatch(tempChat, MASA.plugin.getMainBranch())){
				MASAUtils.privateChatMessage(MASAUtils.patternMatch(tempChat, MASA.plugin.getMainBranch()), event.getPlayer(), tempChat);
			}
		}
		else if(MASAUtils.fussyMatch(tempChatarr[0], "masa") <= 1 || MASAUtils.fussyMatch(tempChatarr[tempChatarr.length - 1], "masa") <= 1){
			if(MASAUtils.canPatternMatch(tempChat, MASA.plugin.getMainBranch())){
				MASAUtils.chatMessage(MASAUtils.patternMatch(tempChat, MASA.plugin.getMainBranch()), event.getPlayer(), tempChat);
			}
		}
	}
	
	@EventHandler
	private void playerCommand(PlayerCommandPreprocessEvent event){
		String[] words = event.getMessage().split(" ");
		String finishedmsg = "";
		for(String word : words){
			if(!finishedmsg.equals("")){
				finishedmsg += " ";
			}
			for(String curse : MASA.getData().getCurses()){
				word = word.replaceAll("(?i)\\b" + curse + "\\b", "****");
			}
			finishedmsg += word;
		}

		event.setMessage(finishedmsg);
	}
	
	@EventHandler
	private void playerJoin(PlayerJoinEvent event){
		if(!event.getPlayer().hasPlayedBefore()){
			List<String> welcome = MASA.plugin.getConfig().getStringList("newPlayerWelcome");
			for(String sent : welcome){
				MASAUtils.privateChatMessage(sent, event.getPlayer(), null);
			}
		}
		if(event.getPlayer().hasPermission("masa.reports.get")){
			if(MASA.getData().getMemory().getReports().getReportCount() > 0){
				MASAUtils.privateChatMessage("Hello %player, there are " + MASA.getData().getMemory().getReports().getReportCount() + " reports for you.", event.getPlayer(), null);
			}
		}
	}
	
	@EventHandler
	private void playerSign(SignChangeEvent event){
		int linenum = 0;
		
		for(String line : event.getLines()){
			String[] words = line.split(" ");
			String finishedmsg = "";
			for(String word : words){
				if(!finishedmsg.equals("")){
					finishedmsg += " ";
				}
				for(String curse : MASA.getData().getCurses()){
					word = word.replaceAll("(?i)\\b" + curse + "\\b", "****");
				}
				finishedmsg += word;
			}
			event.setLine(linenum, finishedmsg);
			linenum++;
		}
	}
	
	@EventHandler
	private void playerTrampleCrops(PlayerInteractEvent event){
		if(MASA.hasWorldGuard && !event.getPlayer().hasPermission("masa.admin")){
			Location below = event.getPlayer().getLocation();
			below.setY(below.getY() - 1);
			if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL && !MASA.wgplugin.canBuild(event.getPlayer(), below)){
				if(MASA.getData().addPlayerWarning(event.getPlayer())){
					MASA.getData().jailPlayer(event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	private void playerAttackAnimal(EntityDamageByEntityEvent event){
		if(!MASA.hasWorldGuard)
			return;
		if(event.getDamager() instanceof Player && (event.getEntity() instanceof Animals || event.getEntity() instanceof Villager)){
			Player ply = (Player)event.getDamager();
			if(!MASA.wgplugin.canBuild(ply, event.getEntity().getLocation()) && !ply.hasPermission("masa.admin")){
				event.setCancelled(true);
				if(event.getEntity() instanceof Animals)
					MASAUtils.privateChatMessage("You cannot harm animals in areas you don't own!", ply, null);
				else
					MASAUtils.privateChatMessage("You cannot harm NPCs in areas you don't own!", ply, null);
			}
		}
		else if(event.getDamager() instanceof Arrow && (event.getEntity() instanceof Animals || event.getEntity() instanceof Villager)){
			Arrow arr = (Arrow) event.getDamager();
			if(arr.getShooter() instanceof Player){
				Player ply = (Player) arr.getShooter();
				if(!MASA.wgplugin.canBuild(ply, event.getEntity().getLocation())){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void playerBreakBlock(BlockBreakEvent event){
		if(event.getPlayer() != null && (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.GOLD_ORE) && event.getPlayer().getGameMode() != GameMode.CREATIVE){
			Material mat = event.getBlock().getType();
			if(!MASA.getData().getBlockLogger().hasPlayer(event.getPlayer())){
				MASA.getData().getBlockLogger().addPlayer(event.getPlayer());
			}
			
			if(mat == Material.DIAMOND_ORE){
				MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).addDiamond();
				MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).setLastDiamondLocation(
						"X: " + String.valueOf(event.getBlock().getLocation().getBlockX()) + 
						" Y: " + String.valueOf(event.getBlock().getLocation().getBlockY()) + 
						" Z: " + String.valueOf(event.getBlock().getLocation().getBlockZ()));
			}
			else{
				MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).addGold();
				MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).setLastGoldLocation(
						"X: " + String.valueOf(event.getBlock().getLocation().getBlockX()) + 
						" Y: " + String.valueOf(event.getBlock().getLocation().getBlockY()) + 
						" Z: " + String.valueOf(event.getBlock().getLocation().getBlockZ()));
			}
		}
	}
	
	@EventHandler
	public void playerPlaceBlock(BlockPlaceEvent event){
		if(event.getPlayer() != null && (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.GOLD_ORE) && event.getPlayer().getGameMode() != GameMode.CREATIVE){
			Material mat = event.getBlock().getType();
			if(MASA.getData().getBlockLogger().hasPlayer(event.getPlayer())){
				if(mat == Material.DIAMOND_ORE){
					MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).removeDiamond();
				}
				else{
					MASA.getData().getBlockLogger().getPlayer(event.getPlayer()).removeGold();
				}
			}
		}
	}
	
	@EventHandler
	public void saplingAutoPlant(ItemDespawnEvent event){
		if(event.getEntity().getItemStack().getType() == Material.SAPLING){
			Location loc = event.getLocation().clone();
			loc.setY(loc.getY() - 1);
			
			if(loc.getBlock().getType() == Material.GRASS && event.getLocation().getBlock().getType() == Material.AIR){
				if(Math.random() <= 0.3){
					event.getLocation().getBlock().setTypeIdAndData(event.getEntity().getItemStack().getTypeId(), event.getEntity().getItemStack().getData().getData(), false);
				}
			}
		}
	}
	
	@EventHandler
	public void witherSpawn(CreatureSpawnEvent event){
		if(event.getSpawnReason() == SpawnReason.BUILD_WITHER && !event.getLocation().getWorld().getName().equals("world_nether")){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private void playerQuit(PlayerQuitEvent event){
		if(event.getPlayer().isDead()){
			Player player = event.getPlayer();
			String quitmsg = "";
			if(player.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)){
				if(player.getLastDamageCause().getEntityType().equals(EntityType.SPIDER) || 
						player.getLastDamageCause().getEntityType().equals(EntityType.CAVE_SPIDER)){
					quitmsg = "A tiny spider scared " + player.getName() + " so they rage quit.";
				}
				else if(player.getLastDamageCause().getEntityType().equals(EntityType.ZOMBIE)){
					quitmsg = "The Zombie was too much for " + player.getName() + " and they rage quit.";
				}
				else if(player.getLastDamageCause().getEntityType().equals(EntityType.ENDERMAN)){
					quitmsg = "An Enderman forced " + player.getName() + " to rage quit.";
				}
				else if(player.getLastDamageCause().getEntityType().equals(EntityType.WOLF)){
					quitmsg = player.getName() + " was eaten by a puppy so they rage quit.";
				}
				else{
					quitmsg = player.getName() + " rage quit.";
				}
			}
			else if(player.getLastDamageCause().getCause().equals(DamageCause.SUICIDE)){
				quitmsg = "Living was too much for " + player.getName() + " so they rage quit.";
			}
			else if(player.getLastDamageCause().getCause().equals(DamageCause.ENTITY_EXPLOSION)){
				quitmsg = player.getName() + " blew up and rage quit.";
			}
			else{
				quitmsg = player.getName() + " rage quit.";
			}
			event.setQuitMessage(ChatColor.YELLOW + quitmsg);
		}
	}
	
	@EventHandler
	private void interactHorse(PlayerInteractEntityEvent event){
		if(event.getRightClicked() instanceof Horse && !event.getPlayer().hasPermission("masa.owner.bypass")){
			Horse horse = (Horse)event.getRightClicked();
			if(horse.getOwner() != null && !horse.getOwner().getName().equals(event.getPlayer().getName())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + horse.getOwner().getName() + " owns this horse! They need to tansfer ownership to you.");
			}
		}
	}
	
	@EventHandler
	private void mobspawn(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.OCELOT){
			event.setCancelled(true);
		}
	}
}
