//PkmnArena.java
//Aaron Li
//Deals with the main game and the user's actions

//ADDTIONS:
//Normal and Psychic types
//Heal special
//Bot has a name
//User and Computer both pick 6 pokemon(because 4 vs 147 is a bit unfair)
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
public class PkmnArena{
	
	public static Pokedex pokedex = new Pokedex("allPokemon.txt");
	public static final int SELECTING_ACTIVE = 0;
	public static final int SELECTING_ACTION = 1;
	public static final int PICKING_ATTACK = 2;
	public static final int RETREAT = 3;
	public static final int PASS = 4;
	public static final int COMPUTER_TURN = 5;
	public static final int OPTIONS = 5;
	public static Scanner kb = new Scanner(System.in);
	public static final Random rand = new Random();
	public static boolean[] options = {false,false,true}; // Pokemon Details, Attack Details, Health and energy results
	public static String[] optionNames = {"Pokemon Stats", "Attack Details", "Attack Results"}; // String version for displaying to the user
	public static final int POKEMON_DETAILS = 0;
	public static final int ATTACK_DETAILS = 1;
	public static final int RESULT_DETAILS = 2;
	
	
	public static Party userParty;
	public static Party computerParty;
	private static int uIn;
	private static boolean running = true;
	public static String botName = PkmnTools.enemyName();
	
	public static void main(String[]args){
		pickActive();
		battle();
	}
	
	
	public static void pickActive(){ // Let the user pick the pokemon they're going to use
		userParty = Party.pickParty(pokedex); // let the user pick
		computerParty = Party.computerParty(pokedex,userParty); // let the computer pick
		computerParty.setOwner(botName); // the computerParty method defaults to "" as the owner of the party, change the name to the bot name
		System.out.println("Pick a starting pokemon.");
		userParty.pickActive(false); // Ask the user to pick an active pokemon (false means they can't use the back option)
	}
	
	
	public static void changeOptions(){ // Allow the user to toggle the information they want to see from the battle
		int uIn = -1;
		while(uIn != 0){ // while the user input isn't back (will also be broken later)
			System.out.println("0. Back\nToggle...\n1. Pokemon Details "+(options[POKEMON_DETAILS]?"[ON]":"[OFF]")+"\n2. Attack Details"+(options[ATTACK_DETAILS]?"[ON]":"[OFF]")+"\n3. Attack Result Details"+(options[RESULT_DETAILS]?"[ON]":"[OFF]")); // print the state of the options
			uIn = Integer.parseInt(kb.nextLine()); //get the user's input
			if(uIn>0 && uIn <=options.length){ // if the user's input maps to an option
					options[uIn-1] = !options[uIn-1]; //subtract 1 from the input because options are listed 1 to x+1 while the list is 0 to x
					System.out.printf("%s has been turned %s\n",optionNames[uIn-1],options[uIn-1]?"ON":"OFF"); // tell the user what the setting has changed to
			}
		}
	}
	
	
	public static void battle(){ // used for organizing the user's and computer's turns
		if(rand.nextBoolean()){ // 50/50 chance of who goes first
			PkmnTools.computerTurn(userParty,computerParty); // if the computer goes first (normal flow involves the user going first)
		}
		while(true){
			pickNextAction(); // prompt the user to pick the next action
			PkmnTools.computerTurn(userParty,computerParty); // let the computer choose its action
			if(computerParty.numAlive()==0){ // if the computer has no more living pokemon
				System.out.println("You are Trainer Supreme!"); // crown the user as the winner
				break;
			}
			else if(userParty.numAlive() == 0){//Tell the user they lost
				System.out.println("You have no available pokemon!\nYou lose..."); // would be much more creepy if it said "I win"
				break;
			}
		}
	}
	
	public static boolean pickAttack(Pokemon attacking, Pokemon defending){ //Prompt the user to pick the attack they wish to use, then attacks the computer's pokemon with it
		Integer[] currentAttacks = attacking.availableAttacks(); // get the attacks the attacking pokemon has enough energy for
		while(true){
			System.out.println("Pick an attack:\n 0. Back");
			System.out.println(options[ATTACK_DETAILS]?" s. Simple":" d. Details");
			if(options[ATTACK_DETAILS]){ // if the user wants the detailed view
				for(int i = 0; i<currentAttacks.length;i++){ // print out the detailed version of the attacks
					System.out.printf("%2d. %s\n",i+1,attacking.getAttack(currentAttacks[i]).toString());
				}
			}
			else{
				for(int i = 0;i<currentAttacks.length;i++){ //print out the names of the attacks
					System.out.printf("%2d. %s\n",i+1,attacking.getAttack(currentAttacks[i]).getName());
				}
			}
			String uIn = kb.nextLine(); // the user's input is handled as a string first so they can pick more or less details
			if(uIn.equals("d")){ // if they want more details
				options[ATTACK_DETAILS] = true; // change setting
			}
			else if(uIn.equals("s")){
				options[ATTACK_DETAILS] = false;
			}
			else{
				int attackNumber = Integer.parseInt(uIn); // If they aren't trying to change the setting, handle the input as an Integer
				if(attackNumber>0 && attackNumber < currentAttacks.length+1){ //if the chosen attack is a valid choice
					System.out.printf("Your %s used %s!\n",attacking.getName(),attacking.getAttack(currentAttacks[attackNumber-1]).getName()); // prompt the user that their pokemon attacked
					attacking.attack(defending,attacking.getAttack(currentAttacks[attackNumber-1])); // Attack the computer's pokemon
					if(options[RESULT_DETAILS]){	//if the user wants more details then print more details
						System.out.printf("Your %s now has %d energy\n",attacking.getName(),attacking.getEnergy());
						System.out.printf("%s's %s now has %d health\n",botName,defending.getName(),defending.getHealth());
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
	
	
	
	
	public static void pickNextAction(){
		boolean pickingAction = true;
		System.out.println("---------- Your Turn! ----------"); // State that it's the user's turn
		if(options[POKEMON_DETAILS]){ //if the user wants more details about their pokemon
			System.out.printf("%s %s Energy: %d/50\n",userParty.currentPokemon().getName(),PkmnTools.makeBar(userParty.currentPokemon().getHealth(),userParty.currentPokemon().getMaxHealth()),userParty.currentPokemon().getEnergy()); //print the health bar and energy
		}
		if(userParty.currentPokemon().getStun()){ // if the user's pokemon has been stunned
			System.out.printf("%s is stunned! Your turn has been skipped\n",userParty.currentPokemon().getName());
			userParty.currentPokemon().setStun(false); // unstun the pokemon
		}
		else{ // if the user's pokemon hasn't been stunned
			while(pickingAction){
				System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass\n4. Options");
				uIn = Integer.parseInt(kb.nextLine()); //get user input
				switch(uIn+1){ // switch based on input
					case PICKING_ATTACK: // if the user chooses to attack
						if(userParty.currentPokemon().availableAttacks().length == 0){ // if the pokemon has no usable attacks
							System.out.println("Not enough energy for any attacks!"+(options[POKEMON_DETAILS]?String.format("(%s has %d energy)",userParty.currentPokemon().getName(),userParty.currentPokemon().getEnergy()):"")); // if the user wants more detail then it will tell them how much energy the pokemon has
						}
						else {
							pickingAction = !pickAttack(userParty.currentPokemon(),computerParty.currentPokemon()); // pickAttack returns true or false depending on whether the user attacked or not, it will become the bot's turn when pickingAction becomes false
						}
					break;
					case RETREAT: // if the user chooses to retreat
						System.out.println("Pick a replacement pokemon."); // prompt the user
						pickingAction = !userParty.pickActive(); // let them pick a new pokemon
						
					break;
					case PASS:
						pickingAction = false; //break the loop
					break;
					case OPTIONS:
						changeOptions(); // let the user change options
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