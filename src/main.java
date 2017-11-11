import java.util.Scanner;
import com.fasterxml.*;

import java.util.Arrays;

import model.TournamentType;

import java.io.*;
import java.util.*;

import http.*;
import model.*;
import model.parser.*;
import request.*;
import model.TournamentType;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.*;


public class main implements Serializable {
	
	private static final String filepath="data.txt";

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
	
		SetLibrary setlibrary = new SetLibrary();


		
		
		Scanner input = new Scanner(System.in); // scanner to be used thruout
		int menu_nav = 0; // menu nav to navigate creating sets and finding
							// h2h's
		while (menu_nav != 5) {
			int submenu; // used to navigate menus in each subsection of main
							// menu
			boolean done = false;
			while(!done) {
				try {
					
					menu();
					
					menu_nav = input.nextInt();
					Exception e = new Exception();
					if(menu_nav < 1 || menu_nav > 5) {
						throw e;
					}
					done=true;
				}
				catch(Exception e){
					System.out.println("Try again with a valid input\n");
					input.nextLine();
				}
			}
				// creating new set block
			
			if (menu_nav == 1) {
				
				Set set = new bo3();
				Game game; // new game temp to set values in bo5 or bo3
				String tag; // tag is tep var to store player tags in each
							// game/set
				int stage_int = 0;
				ArrayList<Integer> p1_bans = new ArrayList<Integer>();
				ArrayList<Integer> p2_bans = new ArrayList<Integer>();

				System.out.println("Enter p1 tag");

				tag = input.next();
				player p1 = new player(tag);
				
				playerFileCreation(tag,p1);

				System.out.println("Enter p2 tag");
				
				tag = input.next();
				player p2 = new player(tag);
				playerFileCreation(tag,p2);
				
				ObjectOutputStream oosp1 = new ObjectOutputStream(new FileOutputStream(new File(p1.getTag()+".txt")));
				ObjectOutputStream oosp2 = new ObjectOutputStream(new FileOutputStream(new File(p2.getTag()+".txt")));
				
				System.out.println("1) Bo3");
				System.out.println("2) Bo5");
				int set_type = input.nextInt();
				
				if (set_type == 1) { // b03
					set = new bo3();
					
					
				} else if (set_type == 2) { // b05
					set = new bo5();
					
					
				}
				
				
				//iterating thru games
				ArrayList<Integer> bannedStages = new ArrayList<Integer>();
				for (int i = 0; i < set.length(); i++) {
					if (i == 0) {
						// specific bans for game 1
						System.out.println("Who bans first?");
						System.out.println("1) " + p1.getTag());
						System.out.println("2) " + p2.getTag());
						int p_ban = input.nextInt();
						bannedStages.add(5);
						if (p_ban == 1) {
							stage_int = bans(p1,p2,bannedStages,i,input);
						} 
						else if (p_ban == 2) {
							stage_int = bans(p2,p1,bannedStages,i,input);
						}
						System.out.println("Chose stage is " + stage_int);
						
						System.out.println("Who won game 1 ?");
						System.out.println("1) " + p1.getTag() );
						System.out.println("2) " + p2.getTag() );
						int winner = input.nextInt();
						if (winner == 1) {
							set.addGame(0, stage_int, p1, p2);
						} 
						else if (winner == 2) {
							set.addGame(0, stage_int, p2, p1);
						}
					}

					// not game 1

					else { // anything not game 1
						if(set.getWinner() != null){
							break;
						}
						bannedStages.removeAll(bannedStages);
						set.getStages(bannedStages);
						for(int j=0; j<bannedStages.size(); j++){
							System.out.println(bannedStages.get(j));
						}
						
						System.out.println(set.getGame(i-1).getWinner().getTag() + " won last game");
						if(p1 == set.getGame(i-1).getWinner()){
							bans(p1, p2, bannedStages,i, input);
							System.out.println("what stage does " + p2.getTag() + " choose?");
							stagelist(bannedStages);
							stage_int = input.nextInt(); // selection what stage to play on done
							
						}
						else{
							bans(p2, p1, bannedStages,i, input);
							System.out.println("what stage does " + p1.getTag() + " choose?");
							stagelist(bannedStages);
							stage_int = input.nextInt(); // selection what stage to play on done
						}
						System.out.println("Who won game " + (i+1) +" ?");
						System.out.println("1) " + p1.getTag() );
						System.out.println("2) " + p2.getTag() );
						int winner = input.nextInt();
						if (winner == 1) {
							set.addGame(i, stage_int, p1, p2);
						} else if (winner == 2) {
							set.addGame(i, stage_int, p2, p1);
						}
						
						} // end of checking if bo3 or not

						// now we must decide stage for game we are looping on
						// we must decide winner, loser
						

					} // end of else
				System.out.println("Congratulations to " + set.getWinner().getTag());
				//now store update player objs in files
				playertoJson(p1);
				playertoJson(p2);
				
				//now store set library as a file
				setlibrary.addSet(set);
				
			
				//work on making sure inputs can be tred and caught if they are invalid
			} // end of set creating block

			else if (menu_nav == 2) { //will finish later
				Challonge tourneys = new Challonge("qa0PeY6wmP2Ps4qSxr8ZWnypxuFBW6E4n2WhSw1n");
				System.out.println(tourneys.getTournament("nyumelee6").getParticipants());
				
				for(int i=0; i<tourneys.listMatches("nyumelee6").size();i++){
					int p1_id= tourneys.listMatches("nyumelee6").get(i).getPlayerOneId();
					int p2_id = tourneys.listMatches("nyumelee6").get(i).getPlayerTwoId();
					String p1_tag="";
					String p2_tag="";
					for(int j=0; j<tourneys.getTournament("nyumelee6").getParticipants().size();j++){
						
						if(tourneys.getTournament("nyumelee6").getParticipants().get(j).getId()==p1_id){
							p1_tag=tourneys.getTournament("nyumelee6").getParticipants().get(j).getName();
							System.out.println("test");
						}
						if(tourneys.getTournament("nyumelee6").getParticipants().get(j).getId()==p2_id){
							p2_tag=tourneys.getTournament("nyumelee6").getParticipants().get(j).getName();
							System.out.println("test");
						}
						System.out.println("test");
						System.out.println(p1_tag + " vs " + p2_tag);
				}
				}

			} else if (menu_nav == 3) {
				player p1 = new player("Testjson");
				p1.set_gamewins(20);
			//	p1.add_ban("Dreamland");
			//	p1.add_ban("Dreamland");
			//	p1.add_ban("Dreamland");
				playertoJson(p1);
				System.out.println("Enter player's tag");
				String p1_tag=input.next();
				System.out.println("Enter second player's tag");
				String p2_tag=input.next();
				ArrayList sets = new ArrayList();
				for(int i=0; i< setlibrary.getLength();i++){
					if(setlibrary.getSet(i).getWinner().getTag().equals(p1_tag) && setlibrary.getSet(i).getLoser().getTag().equals(p2_tag)){
						sets.add(setlibrary.getSet(i));
					}
					else if(setlibrary.getSet(i).getWinner().getTag().equals(p2_tag) && setlibrary.getSet(i).getLoser().getTag().equals(p1_tag)){
						sets.add(setlibrary.getSet(i));
					}
				}
				for(int i=0; i<sets.size();i++){
					sets.get(i).toString();
				}
				

			} else if (menu_nav == 4) {
				System.out.println("Enter player's tag");
				String p1_tag=input.next();
				System.out.println("Enter second player's tag");
				String p2_tag=input.next();
				int p1_wins=0;
				int p2_wins=0;
				for(int i=0; i< setlibrary.getLength();i++){
					if(setlibrary.getSet(i).getWinner().getTag().equals(p1_tag) && setlibrary.getSet(i).getLoser().getTag().equals(p2_tag)){
						p1_wins+=1;
					}
					else if(setlibrary.getSet(i).getWinner().getTag().equals(p2_tag) && setlibrary.getSet(i).getLoser().getTag().equals(p1_tag)){
						p2_wins+=1;
					}
				}
				System.out.println(p1_wins + " - " + p2_wins);
				
			}
			else if (menu_nav == 5) {
				input.close();
				break;

			}

		}

	}

	private SetLibrary ReadObjectFromFile(String filepath2) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void menu() {
		System.out.println("1 create new set (not in bracket)");
		System.out.println("2 Check Sets to be played in Bracket");
		System.out.println("3 View player's set history");
		System.out.println("4 View h2h records");
		System.out.println("5 Exit");
	}
	
	public static int bans(player first_ban, player second_ban, ArrayList<Integer> bannedStages, int game_num, Scanner input) {
		int stage_ban_index;
		if(game_num==0){
			System.out.println("What stage does " + first_ban.getTag() + " ban first?");
			stagelist(bannedStages);
			stage_ban_index = input.nextInt();
			first_ban.add_ban(stage_ban_index);
			bannedStages.add(stage_ban_index);
			System.out.println("What stage does " + second_ban.getTag() + " ban first?");
			stagelist(bannedStages);
			stage_ban_index = input.nextInt();
			second_ban.add_ban(stage_ban_index);
			bannedStages.add(stage_ban_index);
			System.out.println("What stage does " + second_ban.getTag() + " ban second?");
			stagelist(bannedStages);
			stage_ban_index = input.nextInt();
			second_ban.add_ban(stage_ban_index);
			bannedStages.add(stage_ban_index);
			System.out.println("What stage does " + first_ban.getTag() + " ban last?");
			stagelist(bannedStages);
			stage_ban_index = input.nextInt();
			first_ban.add_ban(stage_ban_index);
			bannedStages.add(stage_ban_index);
			
			for(int i=0; i<bannedStages.size(); i++){
				if(!bannedStages.contains(i)){
					return i;
				}
				
			}
		}
		else{
			System.out.println("What stage does " + first_ban.getTag() + " ban?");
			stagelist(bannedStages);
			stage_ban_index = input.nextInt();
			first_ban.add_ban(stage_ban_index);
			bannedStages.add(stage_ban_index);
			
		}
		return -1;
	}

	public static void stagelist(ArrayList<Integer> banned_stages) {
		String[] Stages = { "Battlefield", "Yoshi's Story", "Final Destination", "Fountain of Dreams", "Dream Land",
				"Pokemon Stadium" };
		for (int i = 0; i < Stages.length; i++) {
			if(banned_stages.contains(i) == false){
				System.out.println(i+") "+ Stages[i]);
			}
		}
	}
	
	
	
	
	public static void playertoJson(player p) throws JsonGenerationException, JsonMappingException, IOException{

	    final OutputStream out = new ByteArrayOutputStream();
	    final ObjectMapper mapper = new ObjectMapper();

	    mapper.writeValue(new File(p.getTag()+".json"), p );
	    String jsonInString = mapper.writeValueAsString(p);
	}
	
	public static void playerFileCreation(String tag, player p) {
		try {

			File file = new File(tag + ".txt");

			if (file.createNewFile()) {
				System.out.println("New Player created!");
				p = new player(tag);
				
			} else {
				System.out.println("Player found in system!");
				p = new player(tag);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	// expand stage list to exclude PS on game 1
	// allow method to take in banned stages and not print them
}
