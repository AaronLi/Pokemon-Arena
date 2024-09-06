package com.dumfing.pokemon_arena;
//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon, picking pokemon, dealing with the active pokemon, healing, recharging
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Scanner;

@RequiredArgsConstructor
public class Party{
	private final ArrayList<Pokemon> members = new ArrayList<>(); // Pokemon that the owner of this party owns
    // change the active pokemon
    @Setter
    private Pokemon active = null; // current pokemon that is fighting
    // gets the name of the owner
    @Getter
    private final String owner; // the name of the owner (player, bot)

    public void addPokemon(Pokemon pkmnIn){ // add a pokemon to the party
		members.add(pkmnIn);
		if (active == null) {
			active = pkmnIn;
		}
	}
	public int size(){ // get the amount of pokemon in the party
		return members.size();
	}
	public Pokemon currentPokemon(){ // get the pokemon that is currently fighting in the party
		return active;
	}

    public boolean contains(Pokemon pkmnIn){ // check whether a pokemon is in the party or not
		return members.contains(pkmnIn);
	}
	public void restAll() { // give energy to all pokemon that haven't attacked
		members.forEach(member -> {
			if (!member.hasAttacked()) { // if the pokemon has attacked it will recharge
				member.recharge(10);
			} else {
				member.setAttacked(false); // if the pokemon attacked in the round before it will be able to recharge next round (unless it attacks again)
			}
		});
	}
	public void healAll(){ // heal all pokemon in the party by 20 health
		members.stream().filter(member->member.getHealth() > 0).forEach(member->member.heal(20));
	}
	public static Party pickParty(Pokedex pokedex, Scanner kb){ // used for picking the contents of a party
		int picked; // number of the pokemon the user picked
		int partySize = 6;
		String uIn; // user input
		String[] pokemonPickedWords = {"","second ","third ","fourth ","fifth ","sixth ","sevent ","eighth ","ninth "}; // Used for making the prompt read more easily. Probably won't use all but expansion is there
		Party userParty = new Party("User"); // create a new party with an index of -1
		ArrayList<Pokemon> pickablePokemon = new ArrayList<>(pokedex.allPokemon());  // the names of all pokemon in the pokedex

		while(userParty.size() < partySize){
			System.out.println("Please pick a "+pokemonPickedWords[userParty.size()]+"pokemon:\n"); // print the prompt
			System.out.println((PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]?"s. simple\n":"d. Details\n")+"p. Pick for me");
			if(userParty.size()>0){
				System.out.println("f. Finished");
			}
			if(PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]){ // print the details or just the name of each pokemon
				ListIterator<Pokemon> pokemonListIterator = pickablePokemon.listIterator();
				while (pokemonListIterator.hasNext()) {
					int choiceIndex = pokemonListIterator.nextIndex();
					Pokemon choice = pokemonListIterator.next();
					System.out.printf(PkmnTools.ANSI_CYAN+"%3d"+PkmnTools.ANSI_RESET+" %s\n",choiceIndex+1, choice); // print detailed version
				}

			}
			else{
				ListIterator<Pokemon> pokemonListIterator = pickablePokemon.listIterator();
				while (pokemonListIterator.hasNext()) {
					int choiceIndex = pokemonListIterator.nextIndex();
					Pokemon choice = pokemonListIterator.next();
					if(choiceIndex % 4 == 0){ // new line every fourth pokemon
						System.out.println();
					}
					System.out.printf(PkmnTools.ANSI_CYAN+"%3d"+PkmnTools.ANSI_RESET +" %-13s ",choiceIndex+1,choice.getName()); // Print simple version in a compact grid
				}

				System.out.println();
			}
			System.out.println("Enter a number: ");
			uIn = kb.nextLine();
			if(uIn.equals("d")){ // if the user wants the detailed view
				PkmnBattle.options[PkmnBattle.POKEMON_DETAILS] = true;
			}
			else if(uIn.equals("s")){ // if the user wants the simple view
				PkmnBattle.options[PkmnBattle.POKEMON_DETAILS] = false;
			}
			else if(uIn.equals("p")){
                return userParty.fillPartyWithRandomPokemon(pokedex, null, 6);
			}
			else if(uIn.equals("f") && userParty.size()>0){
				return userParty;
			}
			else if(uIn.replaceAll("[0-9]+", "").isEmpty() && !uIn.isEmpty()){ // if the user's input only contains numbers (removing all numbers results in nothing)
				picked = Integer.parseInt(uIn); // turn the input into an integer that cna be used to get pokemon
				if (0<picked && picked < pickablePokemon.size()+1){ // if the selected pokemon is within the range of selectable pokemon
					Pokemon selectedPokemon = pickablePokemon.get(picked - 1);
					System.out.printf("You picked "+PkmnTools.ANSI_CYAN+"%s"+PkmnTools.ANSI_RESET+"!\n",selectedPokemon.getName());
					System.out.printf("Health: %s"+PkmnTools.ANSI_GREEN+" %d/%d"+PkmnTools.ANSI_RESET+"\nType: %s\nWeakness: %s\nResistance: %s\nMoves:\n",PkmnTools.makeBar(selectedPokemon.getHealth(),selectedPokemon.getMaxHealth()),selectedPokemon.getHealth(),selectedPokemon.getMaxHealth(),selectedPokemon.getType(), selectedPokemon.getWeakness(), selectedPokemon.getResistance());
					for(int i = 0; i<selectedPokemon.getMoves().size(); i++){
							System.out.printf(PkmnTools.ANSI_GREEN+"\t%3d."+PkmnTools.ANSI_RESET+" %s\n",i+1 ,selectedPokemon.getAttack(i));
					}
					System.out.println("\nConfirm? ("+PkmnTools.ANSI_GREEN+"Y"+PkmnTools.ANSI_WHITE+"/"+PkmnTools.ANSI_RED+"N"+PkmnTools.ANSI_RESET+")");
					uIn = kb.nextLine();
					if(uIn.equalsIgnoreCase("y")) {
						userParty.addPokemon(pickablePokemon.remove(picked - 1)); // add the pokemon to the output party
					}
				}
				else{
					System.out.println("Invalid Number");
				}
			}
		}
		return userParty;
	}
	
	public Party fillPartyWithRandomPokemon(@NonNull Pokedex pokedex, Party excluded, int desiredSize){
		int amountToAdd = desiredSize-this.size();
		ArrayList<Pokemon> remainingPokemon = new ArrayList<>(pokedex.allPokemon()); //get all the pokemon from the pokedex
		if (excluded != null) {
			remainingPokemon.removeAll(excluded.allMembers());
		}
		remainingPokemon.removeAll(this.allMembers());
		for(int i = 0; i< amountToAdd; i++){ // pick the amount of pokemon defined in computerPartySize
            // pick a random pokemon from remainingPokemon
            // the pokemon that will be added to the party
            int nextPokemon = PkmnBattle.rand.nextInt(remainingPokemon.size());
            this.addPokemon(remainingPokemon.remove(nextPokemon)); // add the pokemon to the computerParty
		}
		return this;
	}
	
	public ArrayList<String> partyNames(){ // get the names of the pokemon from the party
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : members){ // goes through the party and adds the names of the pokemon in the party to the arraylist
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allMembers(){ // Get a copy of all the Pokemon in the party
		return new ArrayList<>(members);
	}
	public String toString(){
		return toString(false);
	}
	public String toString(boolean detail){ // turn the party into a string
		return members.stream().map(member->detail?member:member.getName()+"\n").toString();
	}
	public int numAlive(){ // returns the amount of pokemon with hp > 0
		return (int)this.members.stream().map(Pokemon::getHealth).filter(health->health > 0).count();
	}

	public List<Pokemon> getLivingPokemon() {
		return members.stream().filter(member -> member.getHealth() > 0).toList();
	}

	public Optional<Pokemon> getNextActivePokemon() {
		return members.stream().filter(member -> member.getHealth() > 0).findFirst();
	}

	public boolean pickActive(boolean allowBack, Scanner kb){ // deals with showing the user which pokemon they can to switch to and allows them to select one
		String uIn;
		boolean pickedNewPokemon = false; // false if the user decided to enter 0 to return
		List<Pokemon> availableToPick = getLivingPokemon().stream().filter(pokemon->pokemon != active).toList();
		while(true){
			if(allowBack){
				System.out.println("0. Back");
			}
			System.out.println(PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]?"s. Simple":"d. Details"); // add option for switching pokemon details on and off
			ListIterator<Pokemon> pokemonListIterator = availableToPick.listIterator();
			while (pokemonListIterator.hasNext()) {
				int choiceIndex = pokemonListIterator.nextIndex();
				Pokemon choice = pokemonListIterator.next();
				System.out.printf("%d. %s\n",choiceIndex+1, PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]? choice: choice.getName()); // print the pokemon name or the pokemon details based on the options defined in pokemon arena
			}
			uIn = kb.nextLine();
			if(uIn.equals("s") && PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]){ // if the user is trying to turn off detailed pokemon info
				PkmnBattle.options[PkmnBattle.POKEMON_DETAILS] = false;
			}
			else if(uIn.equals("d") && !PkmnBattle.options[PkmnBattle.POKEMON_DETAILS]){ // if the user is trying to turn on detailed pokemon info
				PkmnBattle.options[PkmnBattle.POKEMON_DETAILS] = true;
			}
			else if(uIn.replaceAll("[0-9]+", "").isEmpty() && !uIn.isEmpty()){
				int parsedSelection = Integer.parseInt(uIn);
				if(parsedSelection >0 && parsedSelection <availableToPick.size()+1) { // check if the pokemon the user is trying to select is a selectable pokemon and that the input contains only numbers
					setActive(availableToPick.get(parsedSelection - 1)); // set the user's selection to the active pokemon
					System.out.printf("%s, I choose you!\n", this.currentPokemon().getName());
					pickedNewPokemon = true;
					break;
				}
				else if(parsedSelection == 0 && allowBack){ // if the user wants to go back
					break;
				}
			}
			else{
				//Action if input is not linked to option
			}
		}
		return pickedNewPokemon;
	}
}