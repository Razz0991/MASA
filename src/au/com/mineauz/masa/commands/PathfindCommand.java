package au.com.mineauz.masa.commands;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;
import au.com.mineauz.masa.MASAUtils;
import au.com.mineauz.masa.pathfinding.Destination;
import au.com.mineauz.masa.pathfinding.Pathfinder;

public class PathfindCommand implements BaseCommand {

	@Override
	public String getName() {
		return "pathfind";
	}
	
	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public void execute(Player player, String chat, boolean privateChat) {
		String[] chatA = chat.split(" ");
		Destination destination = null;
		String destName = null;
		for(String word : chatA){
			if(MASA.getData().getDestinations().hasDestination(word)){
				destination = MASA.getData().getDestinations().getDestination(word, player);
				destName = word;
				break;
			}
		}
		
		if(destination != null){
			if(!privateChat){
				MASAUtils.chatMessage("I found " + destName + " near your location %player, please wait while I find a path.", player, null);
			}
			else{
				MASAUtils.privateChatMessage("I found " + destName + " near your location %player, please wait while I find a path.", player, null);
			}
			Pathfinder finder = new Pathfinder(player, destination.getDestination());
			finder.start();
		}
		else{
			if(!privateChat){
				MASAUtils.chatMessage("Sorry %player, I could not find anything.", player, null);
			}
			else{
				MASAUtils.privateChatMessage("Sorry %player, I could not find anything.", player, null);
			}
		}
	}

}
