package au.com.mineauz.masa.commands;

import java.util.HashMap;
import java.util.Map;

public class Commands {
	private static Map<String, BaseCommand> commands = new HashMap<String, BaseCommand>();
	
	static{
		addCommand(new PathfindCommand());
		addCommand(new JailCommand());
		addCommand(new GetReportsCommand());
		addCommand(new DeleteReportCommand());
	}
	
	public static void addCommand(BaseCommand command){
		commands.put(command.getName(), command);
	}
	
	public static boolean hasCommand(String commandName){
		return commands.containsKey(commandName);
	}
	
	public static BaseCommand getCommand(String commandName){
		return commands.get(commandName);
	}
}
