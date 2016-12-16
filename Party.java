//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon, picking pokemon, dealing with the active pokemon, healing, recharging
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>(); // Pokemon that the owner of this party owns
	private int active = -1; // current pokemon that is fighting
	public Party(Party partyIn){ // if you wish to clone a party
		this.party = partyIn.party;
	}
	
	public Party(int active){ // Create a party with an already assigned active index
		this.active = active;
	}
	
	public void addPokemon(Pokemon pkmnIn){ // add a pokemon to the party
		party.add(pkmnIn);
	}
	public int size(){ // get the amount of pokemon in the party
		return party.size();
	}
	public Pokemon currentPokemon(){ // get the pokemon that is currently fighting in the party
		return party.get(active);
	}
	public void setActive(int reqActive){ // change the active pokemon
		active = reqActive;
	}
	public boolean contains(Pokemon pkmnIn){ // check whether a pokemon is in the party or not
		return party.contains(pkmnIn);
	}
	public void restAll(){ // give energy to all pokemon that haven't attacked
		for(Pokemon pkmn:party){
			if(!pkmn.hasAttacked()){ // if the pokemon has attacked it will recharge
				pkmn.recharge(10);
			}
			else{
				pkmn.setAttacked(false); // if the pokemon attacked in the round before it will be able to recharge next round (unless it attacks again)
			}
		}
	}
	public void healAll(){ // heal all pokemon in the party by 20 health
		for(Pokemon pkmn:party){
			if(pkmn.getHealth()>0){ // the pokemon needs to be alive to heal
				pkmn.heal(20);
			}
		}
	}
	public static Party pickParty(Pokedex pokedex){ // used for picking the contents of a party
		int picked; // number of the pokemon the user picked
		int partySize = 1; //RETURN TO 6 LATER
		String uIn; // user input
		String[] pokemonPickedWords = {"","second ","third ","fourth ","fifth ","sixth ","sevent ","eighth ","ninth "}; // Used for making the prompt read more easily. Probably won't use all but expansion is there
		Party userParty = new Party(-1); // create a new party with an index of -1
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();  // the names of all pokemon in the pokedex
		ArrayList<Integer> pokemonNumbers = new ArrayList<Integer>(); // the indexes of all pokemon (allows you to pick a pokemon correctly when the already picked ones are removed)
		for(int i = 0; i < pickablePokemon.size(); i++){ // add the indexes to the pokemonnumbers arraylist
			pokemonNumbers.add(new Integer(i));
		}
		
		int numPicked = 0; // amount of pokemon you've picked
		while(numPicked < partySize){
			System.out.println("Please pick a "+pokemonPickedWords[numPicked]+"pokemon:\n"); // print the prompt
			System.out.println(PkmnArena.options[PkmnArena.POKEMON_DETAILS]?"s. simple\n":"d. Details");
			if(PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // print the details or just the name of each pokemon
				for(int i = 0;i<pickablePokemon.size();i++){ // print all the pickable pokemon
					System.out.printf("%2d. %s\n",i+1,pokedex.getPokemon(pokemonNumbers.get(i))); // print detailed version
				}
			}
			else{
				for(int i = 0;i<pickablePokemon.size(); i++){
					if(i%4 == 0){ // new line every fourth pokemon
						System.out.println();
					}
					System.out.printf("%3d. %-13s ",i+1,pickablePokemon.get(i)); // Print simple version in a compact grid
				}	
			}
			System.out.println();
			uIn = PkmnArena.kb.nextLine();
			if(uIn.equals("d")){ // if the user wants the detailed view
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = true;
			}
			else if(uIn.equals("s")){ // if the user wants the simple view
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = false;
			}
			else{
				picked = Integer.parseInt(uIn); // turn the input into an integer that cna be used to get pokemon
				if (0<picked && picked < pickablePokemon.size()+1){ // if the selected pokemon is within the range of selectable pokemon
					userParty.addPokemon(pokedex.getPokemon(pokemonNumbers.get(picked-1))); // add the pokemon to the output party
					pickablePokemon.remove(picked-1); // remove the name of the pokemon
					pokemonNumbers.remove(picked-1); // remove the index of the pokemon
					numPicked++;
				}
				else{
					System.out.println("Invalid Number");
				}
			}
		}
		return userParty;
	}
	
	public static Party computerParty(Pokedex pokedex, Party userPokemon){
		Party computerParty = new Party(0); //create a party with the default index of 0
		int computerPartySize = 1; //RETURN TO 6 LATER
		int nextPokemon; // the pokemon that will be added to the party
		ArrayList<Pokemon> remainingPokemon = pokedex.allPokemon(); //get all the pokemon from the pokedex
		remainingPokemon.removeAll(userPokemon.allMembers()); // remove the pokemon that the user already picked
		for(int i = 0; i< computerPartySize; i++){ // pick the amount of pokemon defined in computerPartySize
			nextPokemon = PkmnArena.rand.nextInt(remainingPokemon.size()); // pick a random pokemon from remainingPokemon
			computerParty.addPokemon(remainingPokemon.get(nextPokemon)); // add the pokemon to the computerParty
			remainingPokemon.remove(nextPokemon); //remove the pokemon from the selectable pokemon
		}
		return computerParty;
	}
	
	public ArrayList<String> partyNames(){ // get the names of the pokemon from the party
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){ // goes through the party and adds the names of the pokemon in the party to the arraylist
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allMembers(){ // Get a copy of all the Pokemon in the party
		return new ArrayList<Pokemon>(party);
	}
	public String toString(){
		return toString(false);
	}
	public String toString(boolean detail){ // turn the party into a string
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=detail?party.get(i):party.get(i).getName()+"\n"; // print it with or without details based on detail
		}
		return sOut;
	}
	public int numAlive(){ // returns the amount of pokemon with hp > 0
		int alive = 0;
		for(Pokemon pkmn : this.party){ // goes through the party increasing alive by 1 with each pokemon that has more than 0 health
			if(pkmn.getHealth()>0){
				alive++;
			}
		}
		return alive;
	}
	public boolean pickActive(){
		return pickActive(true);
	}
	public boolean pickActive(boolean allowBack){ // deals with showing the user which pokemon they can to switch to and allows them to select one
		String uIn;
		boolean pickedNewPokemon = false; // false if the user decided to enter 0 to return
		ArrayList<Integer> livingPokemon =  new ArrayList<Integer>(); //the pokemon the user can pick that have more than 0 health
		for(int i = 0;i<party.size();i++){
			if(party.get(i).getHealth()>0 && active != i){
				livingPokemon.add(new Integer(i)); // add the index of the pokemon to the arraylist of pokemon the user can pick
			}
		}
		while(true){
			if(allowBack){
				System.out.println("0. Back");
			}
			System.out.println(PkmnArena.options[PkmnArena.POKEMON_DETAILS]?"s. Simple":"d. Details"); // add option for switching pokemon details on and off
			for(int i = 0; i<livingPokemon.size(); i++){
				System.out.printf("%d. %s\n",i+1,PkmnArena.options[PkmnArena.POKEMON_DETAILS]?party.get(livingPokemon.get(i)):party.get(livingPokemon.get(i)).getName()); // print the pokemon name or the pokemon details based on the options defined in pokemon arena
			}
			uIn = PkmnArena.kb.nextLine();
			if(uIn.equals("s") && PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // if the user is trying to turn off detailed pokemon info
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = false;
			}
			else if(uIn.equals("d") && !PkmnArena.options[PkmnArena.POKEMON_DETAILS]){ // if the user is trying to turn on detailed pokemon info
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = true;
			}
			else if(Integer.parseInt(uIn)>0 && Integer.parseInt(uIn)<livingPokemon.size()+1){ // check if the pokemon the user is trying to select is a selectable pokemon
				setActive(livingPokemon.get(Integer.parseInt(uIn)-1)); // set the user's selection to the active pokemon
				System.out.printf("%s, I choose you!\n",this.currentPokemon().getName());
				pickedNewPokemon = true;
				break;
			}
			else if(Integer.parseInt(uIn) == 0 && allowBack){ // if the user wants to go back
				break;
			}
			else{
				//Action if input is not linked to option
			}
		}
		return pickedNewPokemon;
	}
	public int getActiveIndex(){ // get the index of the active pokemon
		return active;
	}
}