package au.com.mineauz.masa.memory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Notifier {
	private List<String> notes;
	private Memories memory = Memories.memory;
	
	public Notifier(){
		notes = new ArrayList<String>();
		loadNotes();
	}
	
	public void addNote(String note, String to){
		notes.add("%" + to + "%" + note);
		saveNotes();
	}
	
	public void saveNotes(){
		memory.getMemories().set("memories.notes", notes);
		memory.saveMemories();
	}
	
	public void loadNotes(){
		notes = memory.getMemories().getStringList("memories.notes");
	}
	
	public List<String> getNotes(Player player){
		List<String> noteSect = new ArrayList<String>();
		
		for(int i = 0; i < notes.size(); i++){
			if(notes.get(i).startsWith("%" + player.getName().toLowerCase())){
				noteSect.add(notes.get(i));
				notes.remove(i);
			}
		}
		saveNotes();
		return noteSect;
	}
}
