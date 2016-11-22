//PkmnArena.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class PkmnArena{
	
	public static Pokedex pokedex = new Pokedex("pokemon.txt");
	public static final int SELECTING_ACTIVE = 0;
	public static final int SELECTING_ACTION = 1;
	public static final int PICKING_ATTACK = 2;
	public static final int RETREAT = 3;
	public static final int PASS = 4;
	public static final int COMPUTER_TURN = 5;
	
	public static void main(String[]args){
		Scanner kb = new Scanner(System.in);
		int phase = 0;
		int uIn;
		Party userParty = Party.pickParty(pokedex);
		Party computerParty = Party.computerParty(pokedex,userParty);
		System.out.println(userParty.toString());
		boolean running = true;
		while(running){
			switch(phase){
				case SELECTING_ACTIVE:
					userParty.pickActive();
					phase++;
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
					String[] currentAttacks = userParty.currentPokemon().attacks();
					System.out.println("Pick an attack:");
					for(int i = 0;i<currentAttacks.length;i++){
						System.out.printf("%2d. %s\n",i+1,currentAttacks[i]);
					}
					uIn = Integer.parseInt(kb.nextLine());
					if(uIn>0 && uIn < currentAttacks.length+1){
						if(userParty.currentPokemon().attack(computerParty.currentPokemon(),userParty.currentPokemon().getAttack(currentAttacks[uIn-1]))){
							System.out.printf("%s used %s!\n",userParty.currentPokemon().getName(),currentAttacks[uIn-1]);
							phase = COMPUTER_TURN;
						}
					}
				break;
				case RETREAT:
					userParty.pickActive();
					phase = COMPUTER_TURN;
				break;
				case PASS:
					phase = COMPUTER_TURN;
				break;
				case COMPUTER_TURN:
				break;
			}
			
		}
	}

}