package au.com.mineauz.masa.logging;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.entity.Player;

import au.com.mineauz.masa.MASA;

public class BlockTable {
	private Player player;
	private int totalDiamondMined = 0;
	private int totalGoldMined = 0;
	private int diamondMinedThisHour = 0;
	private int goldMinedThisHour = 0;
	private double averageDiamondsPerHour = 0;
	private double averageGoldPerHour = 0;
	
	private Calendar firstDiamondMineTime = null;
	private Calendar lastDiamondMineTime = null;
	private Calendar firstGoldMineTime = null;
	private Calendar lastGoldMineTime = null;
	
	private String lastDiamondLocation = "";
	private String lastGoldLocation = "";
	
	public BlockTable(Player ply){
		player = ply;
	}
	
	public void addDiamond(){
		lastDiamondMineTime = new GregorianCalendar();
		
		if(firstDiamondMineTime != null && lastDiamondMineTime != null){
			if(lastDiamondMineTime.getTimeInMillis() > firstDiamondMineTime.getTimeInMillis() + 3600000){
				if(averageDiamondsPerHour != 0){
					averageDiamondsPerHour = (diamondMinedThisHour + averageDiamondsPerHour) / 2;
				}
				else{
					averageDiamondsPerHour = diamondMinedThisHour;
				}
				diamondMinedThisHour = 1;
				firstDiamondMineTime = new GregorianCalendar();
				
				if(averageDiamondsPerHour >= 26){
					MASA.getData().getMemory().getReports().addReport(player.getName() + "'s diamonds per hour is high. DPH: " + averageDiamondsPerHour + " Location: " + lastDiamondLocation);
				}
			}
			else{
				diamondMinedThisHour += 1;
			}
		}
		else{
			firstDiamondMineTime = new GregorianCalendar();
		}
		totalDiamondMined += 1;
	}
	
	public void removeDiamond(){
		totalDiamondMined -= 1;
		diamondMinedThisHour -= 1;
	}
	
	public void addGold(){
		lastGoldMineTime = new GregorianCalendar();
		
		if(firstGoldMineTime != null && lastGoldMineTime != null){
			if(lastGoldMineTime.getTimeInMillis() > firstGoldMineTime.getTimeInMillis() + 3600000){
				if(averageGoldPerHour != 0){
					averageGoldPerHour = (goldMinedThisHour + averageGoldPerHour) / 2;
				}
				else{
					averageGoldPerHour = goldMinedThisHour;
				}
				goldMinedThisHour = 1;
				firstGoldMineTime = new GregorianCalendar();
				
				if(averageGoldPerHour >= 40){
					MASA.getData().getMemory().getReports().addReport(player.getName() + "'s gold per hour is high. GPH: " + averageGoldPerHour + " Location: " + lastGoldLocation);
				}
			}
			else{
				goldMinedThisHour += 1;
			}
		}
		else{
			firstGoldMineTime = new GregorianCalendar();
		}
		totalGoldMined += 1;
	}
	
	public void removeGold(){
		totalGoldMined -= 1;
		goldMinedThisHour -= 1;
	}
	
	public Integer getTotalDiamond(){
		return totalDiamondMined;
	}
	
	public Integer getTotalGold(){
		return totalGoldMined;
	}
	
	public Double getDiamondPerHour(){
		if(averageDiamondsPerHour != 0.0){
			return (averageDiamondsPerHour + diamondMinedThisHour) / 2;
		}
		else{
			return (double) diamondMinedThisHour;
		}
	}
	
	public Double getGoldPerHour(){
		if(averageGoldPerHour != 0.0){
			return (averageGoldPerHour + goldMinedThisHour) / 2;
		}
		else{
			return (double) goldMinedThisHour;
		}
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void setFirstDiamondMineTime(){
		firstDiamondMineTime = new GregorianCalendar();
	}
	
	public Calendar getFirstDiamondMineTime(){
		return firstDiamondMineTime;
	}
	
	public void setLastDiamondMineTime(){
		lastDiamondMineTime = new GregorianCalendar();
	}
	
	public Calendar getLastDiamondMineTime(){
		return lastDiamondMineTime;
	}
	
	public void setFirstGoldMineTime(){
		firstGoldMineTime = new GregorianCalendar();
	}
	
	public Calendar getFirstGoldMineTime(){
		return firstGoldMineTime;
	}
	
	public void setLastGoldMineTime(){
		lastGoldMineTime = new GregorianCalendar();
	}
	
	public Calendar getLastGoldMineTime(){
		return lastGoldMineTime;
	}

	public String getLastDiamondLocation() {
		return lastDiamondLocation;
	}

	public void setLastDiamondLocation(String lastDiamondLocation) {
		this.lastDiamondLocation = lastDiamondLocation;
	}

	public String getLastGoldLocation() {
		return lastGoldLocation;
	}

	public void setLastGoldLocation(String lastGoldLocation) {
		this.lastGoldLocation = lastGoldLocation;
	}
}
