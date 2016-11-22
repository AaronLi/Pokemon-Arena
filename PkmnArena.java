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
	
	public static void main(String[]args){
		int phase = 0;
		int uIn;
		Party userParty = Party.pickParty(pokedex);
		Party computerParty = Party.computerParty(pokedex,userParty);
		System.out.println(userParty.toString());
		boolean running = true;
		while(running){
			switch(phase){
				case SELECTING_ACTIVE:
					System.out.println("Pick a starting pokemon.");
					userParty.pickActive();
					phase = coinFlip()?SELECTING_ACTION : COMPUTER_TURN;
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
					userParty.attack(computerParty);
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
	public static boolean coinFlip(){ // coin flip with 50/50 chance
		Random rand = new Random();
		return rand.nextInt(1) == 1;
	}

}