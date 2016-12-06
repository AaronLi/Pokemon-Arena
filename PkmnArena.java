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
	
	
	public static void main(String[]args){
		int phase = 0;
		int uIn;
		Party userParty = Party.pickParty(pokedex);
		Party computerParty = Party.computerParty(pokedex,userParty);
		computerParty.setActive(0);
		//System.out.println(userParty.toString());
		boolean running = true;
		while(running){
			switch(phase){
				case SELECTING_ACTIVE:
					System.out.println("Pick a starting pokemon.");
					phase = userParty.pickStarting();
				break;
				case SELECTING_ACTION:
					System.out.println("-----USER PHASE -----");
					if(userParty.currentPokemon().getStun()){
						System.out.printf("%s is stunned! Your turn has been skipped\n",userParty.currentPokemon().getName());
						userParty.currentPokemon().setStun(false);
						phase = COMPUTER_TURN;
					}
					else{
						System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass\n4. Options");
						uIn = Integer.parseInt(kb.nextLine());
						if(uIn>0&&uIn<5){
							if(uIn == 1 && userParty.currentPokemon().availableAttacks().length == 0){
								System.out.println("Not enough energy for any attacks!");
							}
							else if(uIn == 4){
								phase = changeOptions();
							}
							else{
								phase = 2+uIn-1;
							}
						}
						else{
							//Action if input is not linked to option
						}
					}
				break;
				case PICKING_ATTACK:
					
					phase = pickAttack(userParty.currentPokemon(),computerParty.currentPokemon());
					//phase = COMPUTER_TURN;
				break;
				case RETREAT:
					System.out.println("Pick a replacement pokemon.");
					phase = userParty.pickActive(); 
					//phase = COMPUTER_TURN;
				break;
				case PASS:
					phase = COMPUTER_TURN;
				break;
				case COMPUTER_TURN:
					//Computer
					userParty.restAll();
					System.out.println("-----COMPUTER PHASE -----");
					computerMove(computerParty,userParty);
					if(userParty.currentPokemon().getHealth()<=0){
						System.out.printf("%s has fainted!\n",userParty.currentPokemon().getName());
						System.out.println("Pick a new pokemon");
						userParty.pickStarting();
					}
					else{
						phase = SELECTING_ACTION;//Don't know who's turn it is after your pokemon faints
					}
					computerParty.restAll();
				break;
				case OPTIONS:
					phase = changeOptions();
			}
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
	
	public static int changeOptions(){
		int result = OPTIONS;
		System.out.println("0. Back\nToggle...\n1. Pokemon Details "+(options[POKEMON_DETAILS]?"[ON]":"[OFF]")+"\n2. Attack Details"+(options[ATTACK_DETAILS]?"[ON]":"[OFF]")+"\n3. Attack Result Details"+(options[RESULT_DETAILS]?"[ON]":"[OFF]"));
		int uIn = Integer.parseInt(kb.nextLine());
		if(uIn>=0 && uIn <=3){
			if(uIn == 0){
				result = SELECTING_ACTION;
			}
			else{
				options[uIn-1] = !options[uIn-1];
				System.out.printf("%s has been turned %s\n",optionNames[uIn-1],options[uIn-1]?"ON":"OFF");
			}
		}
		return result;
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