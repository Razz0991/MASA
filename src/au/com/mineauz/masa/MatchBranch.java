package au.com.mineauz.masa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sets a branch to MASA's sentence matching tree. MASA will follow the branches until she cannot follow any more. If there's a response
 * at the end of a branch, she will then return a random response from the list.
 * @author _Razz_
 *
 */
public class MatchBranch {
	private String name;
	private List<MatchBranch> branches;
	private List<String> responses;
	
	public MatchBranch(String name, List<MatchBranch> branches){
		this.name = name.toLowerCase();
		this.branches = branches;
		responses = null;
	}
	
	public MatchBranch(String name, List<MatchBranch> branches, List<String> responses){
		this.name = name.toLowerCase();
		this.branches = branches;
		this.responses = responses;
	}
	
	public MatchBranch(String name){
		this.name = name.toLowerCase();
		responses = null;
		branches = null;
	}
	
	/**
	 * Gets the name of this branch.
	 * @return The name of the branch.
	 */
	public String getBranchName(){
		return name;
	}
	
	/**
	 * Gets all the branches that come from this branch.
	 * @return A list of branches.
	 */
	public List<MatchBranch> getBranches(){
		return branches;
	}
	
	/**
	 * Gets whether this branch has any additional branches.
	 * @return True if there are more branches from this branch.
	 */
	public boolean hasBranches(){
		if(branches != null)
			return true;
		return false;
	}
	
	/**
	 * Adds a branch to this branch.
	 * @param branch - The branch you wish to add.
	 */
	public void addBranch(MatchBranch branch){
		if(branches == null){
			branches = new ArrayList<MatchBranch>();
		}
		branches.add(branch);
	}
	
	/**
	 * Sets the branches that extrude from this branch.
	 * @param branches - A list of branches.
	 */
	public void setBranches(List<MatchBranch> branches){
		this.branches = branches;
	}
	
	/**
	 * Gets all the possible responses for this branch.
	 * @return A string list of the responses.
	 */
	public List<String> getResponses(){
		return responses;
	}
	
	/**
	 * Gets a random response from all the possible responses.
	 * @return A response.
	 */
	public String getRandomResponse(){
		Random rand = new Random();
		return responses.get(rand.nextInt(responses.size()));
	}
	
	/**
	 * Gets whether this branch has any responses.
	 * @return True if there are responses available.
	 */
	public boolean hasResponses(){
		if(responses != null)
			return true;
		return false;
	}
	
	/**
	 * Adds a response to the branch.
	 * @param response - The response to add.
	 */
	public void addResponse(String response){
		if(responses == null){
			responses = new ArrayList<String>();
		}
		responses.add(response);
	}
	
	/**
	 * Sets the responses for this branch.
	 * @param responses - A string list of responses.
	 */
	public void setResponses(List<String> responses){
		this.responses = responses;
	}
}
