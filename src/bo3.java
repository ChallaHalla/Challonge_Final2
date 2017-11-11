import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class bo3 extends Set implements Serializable{
	private Game[] games = new Game[3];
	private ArrayList<player> gameWins = new ArrayList<player>();
	player temp;
	
	
	public void addGame(int game_num, int stage, player p1, player p2){
		
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		this.gameWins.add(p1);
		
		this.games[game_num].setStage(stage);
		if(game_num>0){
			if(Collections.frequency(this.gameWins, p1) == 2){
				this.setWinner(p1);
				this.setLoser(p2);
			}
		}
		
	}
	
	bo3(){
		for(int i=0; i<3; i++){
			this.games[i] = new Game(i,-1, temp, temp);
		}
		
	}
	
	public int length(){
		return this.games.length;
	}
	public Game getGame(int game_num){
		return this.games[game_num];
	}
	
	public void getStages(ArrayList<Integer> bannedStages) {
		for(int i=0; i<this.games.length; i++){
			bannedStages.add(this.games[i].getStage());
		}
		
	}
	

}
