import java.io.Serializable;

public abstract class Set implements Serializable{
	private Game[] games = new Game[5];
	
	private String set_count;
	private player winner;
	private player loser;
	Set(){
		for(int i=0; i<5; i++){
			this.games[i] = new Game();
		}
	}
	
	public void addGame(int game_num, int stage, player p1, player p2){
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		this.games[game_num].setStage(stage);
	}
	
	public boolean addGame(int game_num, player p1, player p2){
		if(game_num==0){
		return false;	
		}
		this.games[game_num-1].setWinner(p1);
		this.games[game_num-1].setLoser(p2);
		return true; //returns true if games were ceated 
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
}


