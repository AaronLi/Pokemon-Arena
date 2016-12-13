//PkmnArena.java
//Aaron Li
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
	public static final int OPTIONS = 6;
	public static Scanner kb = new Scanner(System.in);
	public static final Random rand = new Random();
	public static boolean[] options = {false,false,false}; // Pokemon Details, Attack Details, Health and energy results
	public static String[] optionNames = {"Pokemon Stats", "Attack Details", "Attack Results"};
	public static final int POKEMON_DETAILS = 0;
	public static final int ATTACK_DETAILS = 1;
	public static final int RESULT_DETAILS = 2;
	
	
	public static Party userParty = Party.pickParty(pokedex);
	public static Party computerParty = Party.computerParty(pokedex,userParty);
	private	static int phase = 0;
	private static int uIn;
	private static boolean running = true;
	
	public static void main(String[]args){
		pickActive();
		battle();
	}
	
	
	public static void pickActive(){
		System.out.println("Pick a starting pokemon.");
		phase = userParty.pickStarting();
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
			computerTurn();
		}
		while(true){
			pickNextAction();
			computerTurn();
			if(computerParty.numAlive()==0){
				System.out.println("You are Trainer Supreme!");
				break;
			}
			else if(userParty.numAlive() == 0){
				System.out.println("You lose...");
				break;
			}
		}
	}
	
	public static int pickAttack(Pokemon attacking, Pokemon defending){
		int nextPhase = SELECTING_ACTION;
		String[] currentAttacks = attacking.availableAttacks();
		while(true){
			System.out.println("Pick an attack:");
			System.out.println(" 0. Back");
			if(options[ATTACK_DETAILS]){
				System.out.println(" s. Simple");
				for(int i = 0; i<currentAttacks.length;i++){
					System.out.printf("%2d. %s\n",i+1,attacking.getAttack(currentAttacks[i]));
				}
			}
			else{
				System.out.println(" d. Details");
				for(int i = 0;i<currentAttacks.length;i++){
					System.out.printf("%2d. %s\n",i+1,currentAttacks[i]);
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
					System.out.printf("Your %s used %s!\n",attacking.getName(),currentAttacks[attackNumber-1]);
					if(attacking.attack(defending,attacking.getAttack(currentAttacks[attackNumber-1])) && options[RESULT_DETAILS]){	
						System.out.printf("Your %s now has %d energy\n",attacking.getName(),attacking.getEnergy());
						System.out.printf("The opponent's %s now has %d health\n",defending.getName(),defending.getHealth());
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
		System.out.println("-----USER PHASE -----");
		if(userParty.currentPokemon().getStun()){
			System.out.printf("%s is stunned! Your turn has been skipped\n",userParty.currentPokemon().getName());
			userParty.currentPokemon().setStun(false);
			phase = COMPUTER_TURN;
		}
		else{
			while(pickingAction){
				System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass\n4. Options");
				uIn = Integer.parseInt(kb.nextLine());
				if(uIn>0&&uIn<5){
					if(uIn == 1 && userParty.currentPokemon().availableAttacks().length == 0){
						System.out.println("Not enough energy for any attacks!");
					}
					else if(uIn == 4){
						changeOptions();
					}
					else{
						System.out.println(uIn);//REMOVE THIS
						switch(uIn+1){
							case PICKING_ATTACK:
								if(pickAttack(userParty.currentPokemon(),computerParty.currentPokemon()) == COMPUTER_TURN){
									pickingAction = false;
								}
							break;
							case RETREAT:
								System.out.println("Pick a replacement pokemon.");
								if(userParty.pickActive() == COMPUTER_TURN){
									pickingAction = false;
								} 
							break;
							case PASS:
								pickingAction = false;
							break;
							
						}
					}
				}
				else{
					//Action if input is not linked to option
				}
			}
		}
	}
	
	public static void computerTurn(){
		//Computer
		userParty.restAll();
		System.out.println("-----COMPUTER PHASE -----");
		computerMove(computerParty,userParty);
		if(userParty.currentPokemon().getHealth()<=0){
			System.out.printf("%s has fainted!\n",userParty.currentPokemon().getName());
			System.out.println("Pick a new pokemon");
			userParty.pickActive();
		}
		else{
			phase = SELECTING_ACTION;//Don't know who's turn it is after your pokemon faints
		}
		computerParty.restAll();
	}
	
	
	
	public static void computerMove(Party computerParty, Party userParty){
		if(computerParty.currentPokemon().getHealth() >0){
			String[] possibleAttacks = computerParty.currentPokemon().availableAttacks();
			if(computerParty.currentPokemon().getStun()){
				System.out.printf("%s is stunned! The computer's turn has been skipped.\n",computerParty.currentPokemon().getName());
				computerParty.currentPokemon().setStun(false);
			}
			else if(possibleAttacks.length > 0){
				Pokemon attackingPokemon = userParty.currentPokemon();
				Attack plannedAttack = computerParty.currentPokemon().getAttack(possibleAttacks[rand.nextInt(possibleAttacks.length)]);
				System.out.printf("The opponnent %s used %s!\n",computerParty.currentPokemon().getName(),plannedAttack.getName());
				if(computerParty.currentPokemon().attack(attackingPokemon,plannedAttack)&&options[RESULT_DETAILS]){
					System.out.printf("The opponent's %s now has %d energy\n",computerParty.currentPokemon().getName(),computerParty.currentPokemon().getEnergy());
					System.out.printf("Your %s now has %d health\n",userParty.currentPokemon().getName(),userParty.currentPokemon().getHealth());
				}
			}
			else{
				System.out.println("The opponent passed");
			}
		}
		else if(computerParty.getActiveIndex() < computerParty.size()){
			String oldPokemonName = computerParty.currentPokemon().getName();
			computerParty.setActive(computerParty.getActiveIndex()+1);
			System.out.printf("%s has fainted, next choice: %s!\n",oldPokemonName,computerParty.currentPokemon().getName());//lisa recommended this
		}
		else{
			//You Win!
		}
	}
}