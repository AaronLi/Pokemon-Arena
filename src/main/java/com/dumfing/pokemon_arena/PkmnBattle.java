package com.dumfing.pokemon_arena;
//Aaron Li
//Deals with the main game and the user's actions

//ADDTIONS:
//Normal and Psychic types
//Heal special
//Bot has a name
//User and Computer both pick 6 pokemon(because 4 vs 147 is a bit unfair)
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Random;

import static com.dumfing.pokemon_arena.PkmnTools.ANSI_RED;
import static com.dumfing.pokemon_arena.PkmnTools.ANSI_RESET;

@RequiredArgsConstructor
public class PkmnBattle {
	
	private final Pokedex pokedex;
	public static final int SELECTING_ACTIVE = 0;
	public static final int SELECTING_ACTION = 1;
	public static final int PICKING_ATTACK = 2;
	public static final int RETREAT = 3;
	public static final int PASS = 4;
	public static final int COMPUTER_TURN = 5;
	public static final int OPTIONS = 5;
	public static final Random rand = new Random();
	public static boolean[] options = {false,false,true}; // Pokemon Details, Attack Details, Health and energy results
	public static String[] optionNames = {"Pokemon Stats", "Attack Details", "Attack Results"}; // String version for displaying to the user
	public static final int POKEMON_DETAILS = 0;
	public static final int ATTACK_DETAILS = 1;
	public static final int RESULT_DETAILS = 2;
	
	
	private Party userParty;
	private Party computerParty;
    private final String botName;
	
	
	public void prepareForBattle(Scanner kb){ // Let the user pick the pokemon they're going to use
		userParty = Party.pickParty(pokedex, kb); // let the user pick
		computerParty =  new Party(botName).fillPartyWithRandomPokemon(pokedex,userParty, 6); // let the computer pick
		System.out.println("Pick a starting pokemon.");
		userParty.pickActive(false, kb); // Ask the user to pick an active pokemon (false means they can't use the back option)
	}
	
	
	public static void changeOptions(Scanner kb){ // Allow the user to toggle the information they want to see from the battle
		int uIn = -1;
		while(uIn != 0){ // while the user input isn't back (will also be broken later)
			System.out.println("Toggle...\n0. Back\n1. Pokemon Details "+(options[POKEMON_DETAILS]?"[ON]":"[OFF]")+"\n2. Attack Details"+(options[ATTACK_DETAILS]?"[ON]":"[OFF]")+"\n3. Attack Result Details"+(options[RESULT_DETAILS]?"[ON]":"[OFF]")); // print the state of the options
			uIn = Integer.parseInt(kb.nextLine()); //get the user's input
			if(uIn>0 && uIn <=options.length){ // if the user's input maps to an option
					options[uIn-1] = !options[uIn-1]; //subtract 1 from the input because options are listed 1 to x+1 while the list is 0 to x
					System.out.printf("%s has been turned %s\n"+ ANSI_RESET,optionNames[uIn-1],options[uIn-1]?(PkmnTools.ANSI_GREEN+"ON"):(ANSI_RED+"OFF")); // tell the user what the setting has changed to
			}
		}
	}
	
	
	public void battle(Scanner kb){ // used for organizing the user's and computer's turns
		if(rand.nextBoolean()){ // 50/50 chance of who goes first
			PkmnTools.computerTurn(userParty,computerParty, kb); // if the computer goes first (normal flow involves the user going first)
		}
		while(true){
			pickNextAction(kb); // prompt the user to pick the next action
			PkmnTools.computerTurn(userParty,computerParty, kb); // let the computer choose its action
			if(computerParty.numAlive()==0){ // if the computer has no more living pokemon
				System.out.println(ANSI_RED+"*"+PkmnTools.ANSI_YELLOW+"*"+PkmnTools.ANSI_GREEN+"*"+PkmnTools.ANSI_BLUE+"*"+PkmnTools.ANSI_BRIGHT_CYAN+"You are Trainer Supreme!"+PkmnTools.ANSI_BLUE+"*"+PkmnTools.ANSI_GREEN+"*"+PkmnTools.ANSI_YELLOW+"*"+ ANSI_RED+"*"+ ANSI_RESET); // crown the user as the winner
				break;
			}
			if(userParty.numAlive() == 0){//Tell the user they lost
				System.out.println(ANSI_RED+"You have no available pokemon!\nYou lose..."+ ANSI_RESET); // would be much more creepy if it said "I win"
				break;
			}
		}
		//System.out.println(PkmnTools.ANSI_BG_RED+PkmnTools.ANSI_BLACK+"MADE"+PkmnTools.ANSI_BG_WHITE+PkmnTools.ANSI_BLACK+" IN CA"+PkmnTools.ANSI_BG_RED+PkmnTools.ANSI_BLACK+"NADA"+PkmnTools.ANSI_RESET);
		//System.out.println(PkmnTools.ANSI_BG_RED+PkmnTools.ANSI_BLACK+"By A"+PkmnTools.ANSI_BG_WHITE+PkmnTools.ANSI_BLACK+"aron L"+PkmnTools.ANSI_BG_RED+PkmnTools.ANSI_BLACK+"i   "+PkmnTools.ANSI_RESET);
	}
	
	public boolean pickAttack(Pokemon attacking, Pokemon defending, Scanner kb){ //Prompt the user to pick the attack they wish to use, then attacks the computer's pokemon with it
		List<Attack> currentAttacks = attacking.availableAttacks(); // get the attacks the attacking pokemon has enough energy for
		while(true){
			System.out.println("Pick an attack:\n 0. Back");
			System.out.println(options[ATTACK_DETAILS]?" s. Simple":" d. Details");
			ListIterator<Attack> attackListIterator = currentAttacks.listIterator();
			while (attackListIterator.hasNext()) {
				int attackIndex = attackListIterator.nextIndex();
				Attack attack = attackListIterator.next();
				System.out.printf("%3d. %s\n",attackIndex+1,options[ATTACK_DETAILS]?attack:attack.getName());
			}

			String uIn = kb.nextLine(); // the user's input is handled as a string first so they can pick more or less details
			if(uIn.equals("d")){ // if they want more details
				options[ATTACK_DETAILS] = true; // change setting
			}
			else if(uIn.equals("s")){
				options[ATTACK_DETAILS] = false;
			}
			else if(uIn.replaceAll("[0-9]", "").isEmpty() && !uIn.isEmpty()){
				int attackNumber = Integer.parseInt(uIn); // If they aren't trying to change the setting, handle the input as an Integer
				if(attackNumber>0 && attackNumber < currentAttacks.size()+1){ //if the chosen attack is a valid choice
					System.out.printf("Your %s used %s!\n",attacking.getName(),currentAttacks.get(attackNumber-1).getName()); // prompt the user that their pokemon attacked
					attacking.attack(defending,currentAttacks.get(attackNumber-1)); // Attack the computer's pokemon
					if(options[RESULT_DETAILS]){	//if the user wants more details then print more details
						System.out.printf("Your %s now has "+PkmnTools.pbcColourMultiplier(attacking.getEnergy(), 50)+"%d energy"+ ANSI_RESET+"\n",attacking.getName(),attacking.getEnergy());
						System.out.printf("%s's %s now has "+PkmnTools.rygColourMultiplier(defending.getHealth(), defending.getMaxHealth())+"%d health"+ ANSI_RESET+"\n",botName,defending.getName(),defending.getHealth());
					}
					return true; // the method returns true if the user ended up attacking
				}
				else if(attackNumber == 0){ // if the user chose back
					break;
				}
			}
		}
	return false; // the method returns false if the user went back
	}
	
	
	
	
	public void pickNextAction(Scanner kb){
		boolean pickingAction = true;

		if(userParty.currentPokemon().getHealth()<=0){ // If the user's pokemon has fainted after the bot has attacked
			System.out.printf(ANSI_RED+"%s has fainted!\n"+ANSI_RESET,userParty.currentPokemon().getName());
			if(!userParty.getLivingPokemon().isEmpty()){ // If the user still has pokemon alive
				System.out.println("Pick a new pokemon");
				userParty.pickActive(false, kb); // let the user pick a new starting pokemon
			} else {
				return;
			}
		}

		System.out.println("---------- Your Turn! ----------"); // State that it's the user's turn
		if(options[POKEMON_DETAILS]){ //if the user wants more details about their pokemon
			System.out.printf("%23s %s "+PkmnTools.pbcColourMultiplier(userParty.currentPokemon().getEnergy(),50)+"Energy: %d/50\n"+ ANSI_RESET, "Your "+userParty.currentPokemon().getName(),PkmnTools.makeBar(userParty.currentPokemon().getHealth(),userParty.currentPokemon().getMaxHealth()),userParty.currentPokemon().getEnergy()); //print the health bar and energy
			System.out.printf("%23s %s "+PkmnTools.pbcColourMultiplier(computerParty.currentPokemon().getEnergy(), 50)+"Energy: %d/50\n"+ ANSI_RESET, computerParty.getOwner()+"'s "+computerParty.currentPokemon().getName(),PkmnTools.makeBar(computerParty.currentPokemon().getHealth(),computerParty.currentPokemon().getMaxHealth()),computerParty.currentPokemon().getEnergy()); //print the health bar and energy
		}
		if(userParty.currentPokemon().getStun()){ // if the user's pokemon has been stunned
			System.out.printf(PkmnTools.ANSI_YELLOW+"%s is stunned! Your turn has been skipped\n"+ ANSI_RESET,userParty.currentPokemon().getName());
			userParty.currentPokemon().setStun(false); // unstun the pokemon
		}
		else{ // if the user's pokemon hasn't been stunned
			while(pickingAction){
				System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass\n4. Options");
				String lIn = kb.nextLine();
                int selectedAction = 0;
				if(lIn.replaceAll("[0-9]+", "").isEmpty() && !lIn.isEmpty()) {
					selectedAction = Integer.parseInt(lIn); //get user input
				}
				switch(selectedAction +1){ // switch based on input
					case PICKING_ATTACK: // if the user chooses to attack
						if(userParty.currentPokemon().availableAttacks().isEmpty()){ // if the pokemon has no usable attacks
							System.out.println(ANSI_RED+"Not enough energy for any attacks!"+(options[POKEMON_DETAILS]?String.format(ANSI_RESET+" (%s has"+PkmnTools.pbcColourMultiplier(userParty.currentPokemon().getEnergy(), 50)+" %d energy"+ ANSI_RESET+")",userParty.currentPokemon().getName(),userParty.currentPokemon().getEnergy()):"")+ ANSI_RESET); // if the user wants more detail then it will tell them how much energy the pokemon has
						}
						else {
							pickingAction = !pickAttack(userParty.currentPokemon(),computerParty.currentPokemon(), kb); // pickAttack returns true or false depending on whether the user attacked or not, it will become the bot's turn when pickingAction becomes false
						}
					break;
					case RETREAT: // if the user chooses to retreat
						System.out.println("Pick a replacement pokemon."); // prompt the user
						pickingAction = !userParty.pickActive(true, kb); // let them pick a new pokemon
						
					break;
					case PASS:
						pickingAction = false; //break the loop
					break;
					case OPTIONS:
						changeOptions(kb); // let the user change options
					break;
					default:
						System.out.println("Invalid choice"); // if the user picked something not linked to an option
					break;	
				}
			}
			userParty.restAll(); // rests all the user's pokemon at the end of their turn
		}
	}
	

	
	
	

}
