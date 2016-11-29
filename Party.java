//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>();
	private int active = 0;
	public Party(Party partyIn){
		this.party = partyIn.party;
	}
	public Party(){
	}
	private static Scanner kb = new Scanner(System.in);
	public void addPokemon(Pokemon pkmnIn){
		party.add(pkmnIn);
	}
	public int size(){
		return party.size();
	}
	public Pokemon currentPokemon(){
		return party.get(active);
	}
	public void setActive(int reqActive){
		active = reqActive;
	}
	public boolean contains(Pokemon pkmnIn){
		return party.contains(pkmnIn);
	}
	public void restAll(){
		for(Pokemon pkmn:party){
			if(!pkmn.hasAttacked()){
				pkmn.recharge(10);
			}
			else{
				pkmn.setAttacked(false);
			}
		}
	}
	public void attack(Party enemyParty){
	
	}
	public static Party pickParty(Pokedex pokedex){
		int picked;
		Party userParty = new Party();
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();
		ArrayList<Integer> pokemonNumbers = new ArrayList<Integer>();
		for(int i = 0; i < pickablePokemon.size(); i++){
			pokemonNumbers.add(new Integer(i));
		}
		
		int numPicked = 0;
		while(numPicked < 4){
			System.out.println("Please pick a pokemon:\n");
			for(int i = 0;i<pickablePokemon.size(); i++){
				if(i%4 == 0){
					System.out.println();
				}
				System.out.printf("%2d. %10s ",i+1,pickablePokemon.get(i));
			}
			System.out.println();
			picked = Integer.parseInt(kb.nextLine());
			if (0<picked && picked < pickablePokemon.size()+1){
				userParty.addPokemon(pokedex.getPokemon(pokemonNumbers.get(picked-1)));
				pickablePokemon.remove(picked-1);
				pokemonNumbers.remove(picked-1);
				numPicked++;
			}
			else{
				System.out.println("Invalid Number");
			}
		}
		return userParty;
	}
	
	public static Party computerParty(Pokedex pokedex, Party userPokemon){
		Party computerParty = new Party();
		ArrayList<Pokemon> remainingPokemon = pokedex.allPokemon();
		remainingPokemon.removeAll(userPokemon.allMembers());
		for(Pokemon pkmn : remainingPokemon){
			computerParty.addPokemon(pkmn);
		}
		return computerParty;
	}
	
	/*public Party removeAll(Party toRemove){
		Party toKeep = new Party(party);
		for(int i = 0;i < toRemove.size();i++){
			if(toKeep.contains(toRemove.party.get(i))
		}
	} */
	
	public ArrayList<String> partyNames(){
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allMembers(){
		//return party;
		return new ArrayList<Pokemon>(party);
	}
	
	public String toString(){
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=party.get(i)+"\n";
		}
		return sOut;
	}
	public void pickActive(){
		int uIn = 0;
		for(int i = 0;i<party.size();i++){
			System.out.printf(party.get(i).getHealth()>0?"%2d. %-10s\n":"%2d. FAINTED %-10s\n",i+1,party.get(i).getName());
		}
		uIn = Integer.parseInt(kb.nextLine());
		if(uIn>0 && uIn<5){
			setActive(uIn-1);
			System.out.printf("%s, I choose you!\n",party.get(uIn-1).getName());
		}
		else{
			//Action if input is not linked to option
		}
	}
	public int getActiveIndex(){
		return active;
	}
}