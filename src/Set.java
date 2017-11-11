import java.io.Serializable;
import java.util.ArrayList;

public abstract class Set implements Serializable{
	private Game[] games = new Game[5];
	
	private String set_count;
	private player winner;
	private player loser;
	private player temp;
	
	Set(){
		for(int i=0; i<5; i++){
			this.games[i] = new Game(i,-1, temp, temp);
		}
	}
	
	public void addGame(int game_num, int stage, player p1, player p2){
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		this.games[game_num].setStage(stage);
		
	}
	
	
	
	public int length(){
		return 0;
	}
	public Game getGame(int game_num){
		return this.games[game_num];
	}
	
	public player getWinner(){
		return this.winner;
	}
	public player getLoser(){
		return this.loser;
	}
	public void setWinner(player winner){
		this.winner = winner;
	}
	public void setLoser(player loser){
		this.loser = loser;
	}
	
	

	public void getStages(ArrayList<Integer> bannedStages) {
		for(int i=0; i<this.games.length; i++){
			bannedStages.add(this.games[i].getStage());
		}
	}
}


