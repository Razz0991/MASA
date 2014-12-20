package au.com.mineauz.masa.commands;

import java.util.List;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;
import au.com.mineauz.masa.MASAUtils;

public class JailCommand implements BaseCommand {

	@Override
	public String getName() {
		return "jailplayer";
	}
	
	@Override
	public String getPermission() {
		return "masa.admin.jailcommand";
	}

	@Override
	public void execute(Player player, String chat, boolean privateChat) {
		if(player.hasPermission(getPermission())){
			String[] split = chat.split(" ");
			int playerNameLoc = -1;
			int inc = 0;
			for(String word : split){
				if(MASAUtils.fussyMatch(word, "jail") < 2){
					playerNameLoc = inc + 1;
				}
				inc++;
			}
			
			if(split.length <= playerNameLoc){
				MASAUtils.privateChatMessage("You need to tell me which player to jail.", player, chat);
			}
			else{
				String plyName = split[playerNameLoc];
				List<Player> plyList = MASA.plugin.getServer().matchPlayer(plyName);
				if(plyList.isEmpty()){
					if(privateChat){
						MASAUtils.privateChatMessage("No players found matching the name \"" + plyName + "\"", player, chat);
					}
					else{
						MASAUtils.chatMessage("No players found matching the name \"" + plyName + "\" %player", player, chat);
					}
				}
				else{
					MASA.getData().jailPlayer(plyList.get(0));
					if(privateChat){
						MASAUtils.privateChatMessage("I've jailed " + plyList.get(0).getName() + " for you", player, chat);
					}
					else{
						MASAUtils.chatMessage("I've jailed " + plyList.get(0).getName() + " for you %player", player, chat);
					}
				}
			}
		}
		else{
			if(privateChat){
				MASAUtils.privateChatMessage("You do not have permission to jail someone", player, chat);
			}
			else{
				MASAUtils.chatMessage("You do not have permission to jail someone %player", player, chat);
			}
		}
	}

}
