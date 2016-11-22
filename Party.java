//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>();
	private int active = 0;
	public void addPokemon(Pokemon pkmnIn){
		party.add(pkmnIn);
	}
	public Pokemon currentPokemon(){
		return party.get(active);
	}
	public boolean contains(Pokemon pkmnIn){
		return party.contains(pkmnIn);
	}

	public static Party pickParty(Pokedex pokedex){
		int picked;
		Party userParty = new Party();
		Scanner kb = new Scanner(System.in);
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();
		int numPicked = 0;
		System.out.println(pickablePokemon.toString());
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
			userParty.addPokemon(pokedex.getPokemon(pickablePokemon.get(picked-1)));
			pickablePokemon.remove(picked-1);
			numPicked++;
		}
		return userParty;
	}
	public static Party computerParty(Pokedex pokedex, Party userPokemon){
		Party computerParty = new Party();
		ArrayList<String> remainingPokemon = new ArrayList<String>(pokedex.pokemonNames());
		remainingPokemon.removeAll(userPokemon.partyNames());
		for(String pkmn : remainingPokemon){
			computerParty.addPokemon(pokedex.getPokemon(pkmn));
		}
		return computerParty;
	}
	
	public ArrayList<String> partyNames(){
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public String toString(){
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=party.get(i).getName()+"\n";
		}
		return sOut;
	}
}