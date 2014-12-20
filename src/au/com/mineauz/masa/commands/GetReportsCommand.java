package au.com.mineauz.masa.commands;

import java.util.List;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;

public class GetReportsCommand implements BaseCommand {

	@Override
	public String getName() {
		return "getreports";
	}

	@Override
	public String getPermission() {
		return "masa.reports.get";
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
			
			List<String> reports = MASA.getData().getMemory().getReports().getReports(number);
			for(String report : reports){
				player.sendMessage(report);
			}
		}
	}

}
