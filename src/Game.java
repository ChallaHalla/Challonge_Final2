import java.io.Serializable;

public class Game implements Serializable{
	private int game_number;
	private int stage;
	private player winner;
	private player loser;
	
	Game(){
		
	}
	
	Game(int game_number, int stage, player winner, player loser){
		
		this.game_number=game_number;
		this.stage=stage;
		this.winner=winner;
		this.loser=loser;
	}

	
	
	//getters
	public int getGame_number(){
		return this.game_number;
	}
	
	public int getStage(){
		return this.stage;
	}
	
	public player getWinner(){
		return this.winner;
	}
	public player getLoser(){
		return this.loser;
	}
	
	
	//setters
	
	public void setGame_number(int game_number){
		this.game_number=game_number;
	}
	
	public void setStage(int stage){
		this.stage=stage;
	}
	
	public void setWinner(player winner){
		this.winner=winner;
	}
	public void setLoser(player loser){
		this.loser=loser;
	}
	
	//other METHODS
	
	

}
