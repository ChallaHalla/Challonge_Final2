import java.io.Serializable;
import java.util.ArrayList;

public class player implements Serializable{

	private String tag;
	public int game_wins;
	private ArrayList<String> banned_stages = new ArrayList<String>();
	private ArrayList<Set> set_wins = new ArrayList<Set>();
	private ArrayList<Set> set_losses = new ArrayList<Set>();


	player(String tag) {
		this.tag = tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}
	public void set_gamewins(int game_wins) {
		this.game_wins = game_wins;
	}

	public int get_gamewins() {
		return this.game_wins;
	}
	
	public void add_win(Set set){
		this.set_wins.add(set);
	}
	
	public void add_loss(Set set){
		this.set_losses.add(set);
	}
	
	public void add_ban(String stage){
		this.banned_stages.add(stage);
	}
	
	
	
	

}
