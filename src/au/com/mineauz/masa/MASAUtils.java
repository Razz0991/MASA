package au.com.mineauz.masa;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import au.com.mineauz.masa.commands.Commands;

public class MASAUtils {
	private static MASA plugin = MASA.plugin;
	
	/**
	 * Gets a Levenshtein Distance based on two Strings
	 * @param test - The testing word
	 * @param comparitor - The comparing word
	 * @return A number on how many edits must be made for the two words to be the same.
	 */
	//Found on:
	//http://en.wikibooks.org/wiki/Algorithm_implementation/Strings/Levenshtein_distance#Java
	public static Integer fussyMatch(String test, String comparitor){
		int[][] distance = new int[test.length() + 1][comparitor.length() + 1];
		
		for(int i = 0; i <= test.length(); i++)
            distance[i][0] = i;
		for(int j = 1; j <= comparitor.length(); j++)
            distance[0][j] = j;
		
		for(int i = 1; i <= test.length(); i++){
			for(int j = 1; j <= comparitor.length(); j++){
				distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, 
						distance[i][j - 1] + 1), 
						distance[i - 1][j - 1] + ((test.charAt(i - 1) == comparitor.charAt(j - 1)) ? 0 : 1));
			}
		}
		
		return distance[test.length()][comparitor.length()];
	}
	
	/**
	 * Checks whether the sentence given matches a pattern in the MatchBranch tree. (Usually the main branch from the MASA class).
	 * @param sentence - The sentence to check
	 * @param branch - The branch to check the pattern against
	 * @return True if there is a pattern match available.
	 */
	public static boolean canPatternMatch(String sentence, MatchBranch branch){
		String[] sentArr = sentence.split(" ");
		MatchBranch curBranch = branch;
		int bestMatchInt = 5;
		
		for(String word : sentArr){
			int maxErrors = (int) Math.floor(0.3 * word.length());
			if(maxErrors > 3)
				maxErrors = 3;
			
			if(curBranch.hasBranches()){
				for(MatchBranch br : curBranch.getBranches()){
					if(fussyMatch(word, br.getBranchName()) <= maxErrors){
						if(fussyMatch(word, br.getBranchName()) < bestMatchInt){
							curBranch = br;
							bestMatchInt = fussyMatch(word, br.getBranchName());
							
							if(word.equals(br.getBranchName())){
								break;
							}
						}
					}
				}
				bestMatchInt = 5;
			}
			else{
				break;
			}
		}
		
		if(curBranch.hasResponses()){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the sentence given matches a pattern in the MatchBranch tree. (Usually the main branch from the MASA class).
	 * @param sentence - The sentence to check
	 * @param branch - The branch to check the pattern against
	 * @return A random response from the branch.
	 */
	public static String patternMatch(String sentence, MatchBranch branch){
		String[] sentArr = sentence.split(" ");
		MatchBranch curBranch = branch;
		int bestMatchInt = 5;
		
		for(String word : sentArr){
			int maxErrors = (int) Math.floor(0.3 * word.length());
			if(maxErrors > 3)
				maxErrors = 3;
			
			if(curBranch.hasBranches()){
				for(MatchBranch br : curBranch.getBranches()){
					if(fussyMatch(word, br.getBranchName()) <= maxErrors){
						if(fussyMatch(word, br.getBranchName()) < bestMatchInt){
							curBranch = br;
							bestMatchInt = fussyMatch(word, br.getBranchName());

							if(word.equals(br.getBranchName())){
								break;
							}
						}
					}
				}
				bestMatchInt = 5;
			}
			else{
				break;
			}
		}

		if(curBranch.hasResponses()){
			return curBranch.getRandomResponse();
		}
		return null;
	}
	
	public static boolean clearLot(String lotname, Player player){
		if(plugin.getWorldGuard().getRegionManager(player.getWorld()).hasRegion(lotname)){
			plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(lotname).getOwners().getGroups().clear();
			plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(lotname).getOwners().getPlayers().clear();
			plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(lotname).getMembers().getGroups().clear();
			plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(lotname).getMembers().getPlayers().clear();
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void chatMessage(String chatMessage, Player caller, String playerMessage){
		if(caller != null){
			chatMessage = chatMessage.replace("%player", caller.getName());
		}
		
		String[] aChat = chatMessage.split(" ");
		List<String> commands = new ArrayList<String>();
		for(String chat : aChat){
			if(chat.startsWith("%")){
				String comd = chat.replace("%", "");
				if(Commands.hasCommand(comd)){
					commands.add(comd);
				}
			}
		}

		final String chat = chatMessage;
		final String pchat = playerMessage;
		final List<String> chatCommands = commands;
		final Player player = caller;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(chatCommands.isEmpty()){
					MASA.plugin.getServer().broadcastMessage(
						ChatColor.LIGHT_PURPLE + "Masa: " + ChatColor.WHITE + chat);
				}
				else{
					for(String command : chatCommands){
						Commands.getCommand(command).execute(player, pchat, false);
					}
				}
			}
		}, 20L);
	}
	
	public static void privateChatMessage(String chatMessage, final Player player, String playerMessage){
		chatMessage = chatMessage.replace("%player", player.getName());
		
		String[] aChat = chatMessage.split(" ");
		List<String> commands = new ArrayList<String>();
		for(String chat : aChat){
			if(chat.startsWith("%")){
				String comd = chat.replace("%", "");
				if(Commands.hasCommand(comd)){
					commands.add(comd);
				}
			}
		}

		final String chat = chatMessage;
		final String pchat = playerMessage;
		final List<String> chatCommands = commands;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(chatCommands.isEmpty()){
					player.sendMessage(
						ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Masa" + ChatColor.GRAY + " -> Me] " + 
								ChatColor.WHITE + chat);
				}
				else{
					for(String command : chatCommands){
						Commands.getCommand(command).execute(player, pchat, true);
					}
				}
			}
		}, 20L);
	}
}
