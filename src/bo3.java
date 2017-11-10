import java.io.Serializable;

public class bo3 extends Set implements Serializable{
	private Game[] games = new Game[3];
	
	
	public void addGame(int game_num, int stage, player p1, player p2){
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		this.games[game_num].setStage(stage);
		
	}
	public boolean addGame(int game_num, player p1, player p2){
		if(game_num==0){
			return false;	
			}
		this.games[game_num].setWinner(p1);
		this.games[game_num].setLoser(p2);
		return true;
	}
	bo3(){
		for(int i=0; i<3; i++){
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
