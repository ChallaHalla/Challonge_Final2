import java.io.Serializable;

public class bo5 extends Set implements Serializable{
	private Game[] games = new Game[5];
	
	
	public void addGame(int game_num, int stage, player p1, player p2){
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		this.games[game_num].setStage(stage);
	}
	bo5(){
		for(int i=0; i<5; i++){
			this.games[i] = new Game();
		}
		
	}
	
	public int length(){
		return this.games.length;
	}
	
	public Game getGame(int game_num){
		return this.games[game_num];
	}

}
