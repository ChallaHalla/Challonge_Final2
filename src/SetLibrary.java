import java.io.Serializable;
import java.util.ArrayList;

public class SetLibrary implements Serializable{
	ArrayList<Set> SetLibrary = new ArrayList<Set>();
	
	SetLibrary(){
		
	}
	
	public void addSet(Set set){
		this.SetLibrary.add(set);
	}
	
	public Set getSet(int a){
		return this.SetLibrary.get(a);
	}
	
	public int getLength(){
		return this.SetLibrary.size();
	}
}
