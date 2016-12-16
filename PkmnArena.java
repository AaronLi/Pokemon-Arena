//PkmnArena.java
//Aaron Li


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
	public static String[] optionNames = {"Pokemon Stats", "Attack Details", "Attack Results"};
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
	
	
	public static void pickActive(){
		userParty = Party.pickParty(pokedex);
		computerParty = Party.computerParty(pokedex,userParty);
		computerParty.setOwner(botName);
		System.out.println("Pick a starting pokemon.");
		userParty.pickActive(false);
	}
	
	
	public static void changeOptions(){
		int uIn = -1;
		while(uIn != 0){
			System.out.println("0. Back\nToggle...\n1. Pokemon Details "+(options[POKEMON_DETAILS]?"[ON]":"[OFF]")+"\n2. Attack Details"+(options[ATTACK_DETAILS]?"[ON]":"[OFF]")+"\n3. Attack Result Details"+(options[RESULT_DETAILS]?"[ON]":"[OFF]"));
			uIn = Integer.parseInt(kb.nextLine());
			if(uIn>0 && uIn <=3){
					options[uIn-1] = !options[uIn-1];
					System.out.printf("%s has been turned %s\n",optionNames[uIn-1],options[uIn-1]?"ON":"OFF");
			}
		}
	}
	
	
	public static void battle(){
		if(rand.nextBoolean()){
			PkmnTools.computerTurn(userParty,computerParty);
		}
		while(true){
			pickNextAction();
			PkmnTools.computerTurn(userParty,computerParty);
			if(computerParty.numAlive()==0){
				System.out.println("You are Trainer Supreme!");
				break;
			}
			else if(userParty.numAlive() == 0){
				System.out.println("You have no available pokemon!\nYou lose...");
				break;
			}
		}
	}
	
	public static int pickAttack(Pokemon attacking, Pokemon defending){
		int nextPhase = SELECTING_ACTION;
		Integer[] currentAttacks = attacking.availableAttacks();
		while(true){
			System.out.println("Pick an attack:\n 0. Back");
			if(options[ATTACK_DETAILS]){
				System.out.println(" s. Simple");
				for(int i = 0; i<currentAttacks.length;i++){
					System.out.printf("%2d. %s\n",i+1,attacking.getAttack(currentAttacks[i]).toString());
				}
			}
			else{
				System.out.println(" d. Details");
				for(int i = 0;i<currentAttacks.length;i++){
					System.out.printf("%2d. %s\n",i+1,attacking.getAttack(currentAttacks[i]).getName());
				}
			}
			String uIn = kb.nextLine();
			if(uIn.equals("d")){
				options[ATTACK_DETAILS] = true;
			}
			else if(uIn.equals("s")){
				options[ATTACK_DETAILS] = false;
			}
			else{
				int attackNumber = Integer.parseInt(uIn);
				if(attackNumber>0 && attackNumber < currentAttacks.length+1){
					System.out.printf("Your %s used %s!\n",attacking.getName(),attacking.getAttack(currentAttacks[attackNumber-1]).getName());
					attacking.attack(defending,attacking.getAttack(currentAttacks[attackNumber-1]));
					if(options[RESULT_DETAILS]){	
						System.out.printf("Your %s now has %d energy\n",attacking.getName(),attacking.getEnergy());
						System.out.printf("%s's %s now has %d health\n",botName,defending.getName(),defending.getHealth());
					}
					nextPhase = COMPUTER_TURN;
					break;
				}
				else if(attackNumber == 0){
					break;
				}
			}
		}
	return nextPhase;
	}
	
	
	
	
	public static void pickNextAction(){
		boolean pickingAction = true;
		System.out.println("---------- Your Turn! ----------");
		if(options[POKEMON_DETAILS){
			System.out.printf("%s %s Energy: %d/50\n",userParty.currentPokemon().getName(),PkmnTools.makeBar(userParty.currentPokemon().getHealth(),userParty.currentPokemon().getMaxHealth()),userParty.currentPokemon().getEnergy());
		}
		if(userParty.currentPokemon().getStun()){
			System.out.printf("%s is stunned! Your turn has been skipped\n",userParty.currentPokemon().getName());
			userParty.currentPokemon().setStun(false);
			//phase = COMPUTER_TURN;
		}
		else{
			while(pickingAction){
				System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass\n4. Options");
				uIn = Integer.parseInt(kb.nextLine());
				switch(uIn+1){
					case PICKING_ATTACK:
						if(uIn == 1 && userParty.currentPokemon().availableAttacks().length == 0){
							System.out.println("Not enough energy for any attacks!"+(options[POKEMON_DETAILS]?String.format("(%s has %d energy)",userParty.currentPokemon().getName(),userParty.currentPokemon().getEnergy()):""));
						}
						else if(pickAttack(userParty.currentPokemon(),computerParty.currentPokemon()) == COMPUTER_TURN){
							pickingAction = false;
						}
					break;
					case RETREAT:
						System.out.println("Pick a replacement pokemon.");
						pickingAction = !userParty.pickActive();
						
					break;
					case PASS:
						pickingAction = false;
					break;
					case OPTIONS:
						changeOptions();
					break;
					default:
					break;	
				}
			}
		}
	}
	

	
	
	

}