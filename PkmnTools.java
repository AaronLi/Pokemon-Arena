//PkmnTools.java
//Aaron Li
//Deals with the bot's moves and random naming as well as visualising health bars
import java.util.Scanner;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PkmnTools{
	private static ArrayList<String> possibleNames = new ArrayList<String>(); // all names in the file
	public static String enemyName(){
		try{
			Scanner nameFile = new Scanner(new BufferedReader(new FileReader("possibleNames.txt"))); // open the file for reading
			while(nameFile.hasNextLine()){
				possibleNames.add(nameFile.nextLine()); // read the names and add them to the arraylist
			}
		}
		catch(IOException e){
			System.err.println("File not found");
		}
		return possibleNames.get(PkmnArena.rand.nextInt(possibleNames.size())); // pick random index in the arraylist
	}
	public static void computerMove(Party computerParty, Party userParty){ // What happens when it's the Computer's turn to attack
		if(computerParty.currentPokemon().getHealth() >0){ // If the computer's Pokemon is still alive
			Integer[] possibleAttacks = computerParty.currentPokemon().availableAttacks(); // get the attacks that the pokemon has enough energy to use
			if(computerParty.currentPokemon().getStun()){ // will skip the turn if the pokemon is stunned
				System.out.printf("%s is stunned! The %s's turn has been skipped.\n",computerParty.currentPokemon().getName(),computerParty.getOwner());
				computerParty.currentPokemon().setStun(false);
			}
			else if(possibleAttacks.length > 0){ //if there are usable attacks for the pokemon to use
				Pokemon attackingPokemon = userParty.currentPokemon();
				Attack plannedAttack = computerParty.currentPokemon().getAttack(possibleAttacks[PkmnArena.rand.nextInt(possibleAttacks.length)]); // get a random attack from the list of possible attacks
				System.out.printf("%s's %s used %s!\n",computerParty.getOwner(),computerParty.currentPokemon().getName(),plannedAttack.getName()); // print out what the computer chose
				computerParty.currentPokemon().attack(attackingPokemon,plannedAttack); // attack the user's pokemon with the seslected attack
				if(PkmnArena.options[PkmnArena.RESULT_DETAILS]){ // if the user wants more details about the attack
					System.out.printf("%s's %s now has %d energy\n",computerParty.getOwner(),computerParty.currentPokemon().getName(),computerParty.currentPokemon().getEnergy());
					System.out.printf("Your %s now has %d health\n",userParty.currentPokemon().getName(),userParty.currentPokemon().getHealth());
				}
			}
			else{
				System.out.println(String.format("%s skipped their turn",computerParty.getOwner())); // What happens when the bot doesn't have enough energy to attack
			}
		}
		else if(computerParty.currentPokemon().getHealth() == 0){ // if the computer's bot has fainted
			userParty.healAll(); // heal all the user's pokemon
			String oldPokemonName = computerParty.currentPokemon().getName(); // the name of the pokemon that fainted
			computerParty.setActive(computerParty.getActiveIndex()+1);
			System.out.printf("%s has fainted, next choice: %s!\n",oldPokemonName,computerParty.currentPokemon().getName());//print out the bot's next active pokemon
		}
	}
	public static void computerTurn(Party uParty, Party cParty){ // deals with whether the bot has been defeated or not
		//Computer
		
		if(cParty.numAlive()>0){ // if the computer's party still has pokemon
			System.out.println(String.format("---------- %s's Turn! ----------",cParty.getOwner())); // Start bot's turn
			computerMove(cParty,uParty); // let the bot pick an attack to use
			if(uParty.currentPokemon().getHealth()<=0){ // If the user's pokemon has fainted after the bot has attacked
				System.out.printf("%s has fainted!\n",uParty.currentPokemon().getName());
				if(uParty.numAlive()>0){ // If the user still has pokemon alive
					System.out.println("Pick a new pokemon");
					uParty.pickActive(false); // let the user pick a new starting pokemon
				}
			}
		}
		else{ // if the computerParty has no living pokemon
			System.out.printf("%s has fainted!\n",cParty.currentPokemon().getName()); // When the bot's pokemon have all fainted
			System.out.printf("%s has no more available pokemon!\n",cParty.getOwner());
		}
		cParty.restAll(); // recharge all the pokemon on the computer's team
	}
	public static String makeBar(int currentHealth, int maxHealth){ // Make a health bar for printing a visual representation of the pokemon's health
		Double remainingHealth = new Double(((float)currentHealth/(float)maxHealth)*10.0); // divide with floats first because dividing with integers will lose the decimal places
		int remainingHealthInt = remainingHealth.intValue(); // convert from Double to int
		int leftoverHealth = 10-remainingHealthInt; // find the remaining units in the health bar
		return multiplyLetter(remainingHealthInt,"|")+multiplyLetter(leftoverHealth,":"); // make the health bar with | and :
	}
	public static String multiplyLetter(int times, String letter){ // recursively add multiple letters together
		return multiplyLetter(times,letter,"");
	}
	public static String multiplyLetter(int times, String letter, String made){ 
		return times == 0?made:multiplyLetter(times-1,letter,made+letter);//call multiplyLetter while adding the letter to the end of made then reducing the number of times it should be added
	}
}