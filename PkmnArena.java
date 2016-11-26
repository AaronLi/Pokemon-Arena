//PkmnArena.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
public class PkmnArena{
	
	public static Pokedex pokedex = new Pokedex("pokemon.txt");
	public static final int SELECTING_ACTIVE = 0;
	public static final int SELECTING_ACTION = 1;
	public static final int PICKING_ATTACK = 2;
	public static final int RETREAT = 3;
	public static final int PASS = 4;
	public static final int COMPUTER_TURN = 5;
	public static Scanner kb = new Scanner(System.in);
	public static final Random rand = new Random();
	
	public static void main(String[]args){
		int phase = 0;
		int uIn;
		Party userParty = Party.pickParty(pokedex);
		Party computerParty = Party.computerParty(pokedex,userParty);
		//System.out.println(userParty.toString());
		boolean running = true;
		while(running){
			switch(phase){
				case SELECTING_ACTIVE:
					System.out.println("Pick a starting pokemon.");
					userParty.pickActive();
					phase =PkmnArena.rand.nextBoolean()?SELECTING_ACTION : COMPUTER_TURN;
				break;
				case SELECTING_ACTION:
					System.out.println("Pick an action:\n1. Attack\n2. Retreat\n3. Pass");
					uIn = Integer.parseInt(kb.nextLine());
					if(uIn>0&&uIn<4){
						phase = 2+uIn-1;
					}
					else{
						//Action if input is not linked to option
					}
				break;
				case PICKING_ATTACK:
					
					pickAttack(userParty.currentPokemon(),computerParty.currentPokemon());
					phase = COMPUTER_TURN;
					userParty.restAll();
				break;
				case RETREAT:
					System.out.println("Pick a replacement pokemon.");
					userParty.pickActive();
					phase = COMPUTER_TURN;
					userParty.restAll();
				break;
				case PASS:
					phase = COMPUTER_TURN;
					userParty.restAll();
				break;
				case COMPUTER_TURN:
					//Computer
					System.out.println("Computer turn here");
					phase = SELECTING_ACTION;
					computerParty.restAll();
				break;
			}
			
		}
	}
	public static void pickAttack(Pokemon attacking, Pokemon defending){
		String[] currentAttacks = attacking.attacks();
		while(true){
			System.out.println("Pick an attack:");
			for(int i = 0;i<currentAttacks.length;i++){
				System.out.printf("%2d. %s\n",i+1,currentAttacks[i]);
			}
			int uIn = Integer.parseInt(kb.nextLine());
			if(uIn>0 && uIn < currentAttacks.length+1){
				System.out.printf("Your %s used %s!\n",attacking.getName(),currentAttacks[uIn-1]);
				if(attacking.attack(defending,attacking.getAttack(currentAttacks[uIn-1]))){	
					System.out.printf("Your %s now has %d energy\n",attacking.getName(),attacking.getEnergy());
					System.out.printf("The opponent's %s now has %d health\n",defending.getName(),defending.getHealth());
				}
				break;
			}
		}
	}
}