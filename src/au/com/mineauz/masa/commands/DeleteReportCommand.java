package au.com.mineauz.masa.commands;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;
import au.com.mineauz.masa.MASAUtils;

public class DeleteReportCommand implements BaseCommand {

	@Override
	public String getName() {
		return "deletereports";
	}

	@Override
	public String getPermission() {
		return "masa.reports.delete";
	}

	@Override
	public void execute(Player player, String chat, boolean privateChat) {
		if(player.hasPermission(getPermission())){
			int number = 1;
			for(String word : chat.split(" ")){
				if(word.matches("[0-9]+")){
					number = Integer.parseInt(word);
				}
			}
			
			MASA.getData().getMemory().getReports().deleteReports(number);
			if(privateChat)
				MASAUtils.privateChatMessage("Deleted those reports for you.", player, chat);
			else
				MASAUtils.chatMessage("Deleted those reports for you %player.", player, chat);
		}
	}

}
