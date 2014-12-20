package au.com.mineauz.masa.memory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASAUtils;

public class Reports {
	
	private List<String> reports;
	private Memories memory = Memories.memory;
	
	public Reports(){
		reports = new ArrayList<String>();
		loadReports();
	}
	
	public void addReport(String report, Player reporter){
		reports.add(ChatColor.LIGHT_PURPLE + "(Report) " + reporter.getName() + ChatColor.GRAY + ": " + report);
		saveReports();
	}
	
	public void addReport(String report){
		reports.add(ChatColor.LIGHT_PURPLE + "(Report) Masa" + ChatColor.GRAY + ": " + report);
		for(Player ply : Bukkit.getServer().getOnlinePlayers()){
			if(ply.hasPermission("masa.reports.read")){
				MASAUtils.privateChatMessage(report, ply, null);
			}
		}
		saveReports();
	}
	
	public void saveReports(){
		memory.getMemories().set("memories.reports", reports);
		memory.saveMemories();
	}
	
	public void loadReports(){
		reports = memory.getMemories().getStringList("memories.reports");
	}
	
	public Integer getReportCount(){
		return reports.size();
	}
	
	public List<String> getReports(Integer amount){
		List<String> reportSect = new ArrayList<String>();
		if(amount > reports.size()){
			amount = reports.size();
		}
		for(int i = 0; i < amount; i++){
			reportSect.add(reports.get(i));
		}
		saveReports();
		return reportSect;
	}
	
	public void deleteReports(Integer amount){
		if(amount > reports.size()){
			amount = reports.size();
		}
		for(int i = 0; i < amount; i++){
			reports.remove(0);
		}
		saveReports();
	}
}
