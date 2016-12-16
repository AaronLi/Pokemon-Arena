//PkmnTools.java
//Aaron Li
//Currently only holds a method for reading the name file and picking a random name for the bot
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
	public static void computerMove(Party computerParty, Party userParty){
		if(computerParty.currentPokemon().getHealth() >0){
			Integer[] possibleAttacks = computerParty.currentPokemon().availableAttacks();
			if(computerParty.currentPokemon().getStun()){
				System.out.printf("%s is stunned! The %s's turn has been skipped.\n",computerParty.currentPokemon().getName(),computerParty.getOwner());
				computerParty.currentPokemon().setStun(false);
			}
			else if(possibleAttacks.length > 0){
				Pokemon attackingPokemon = userParty.currentPokemon();
				Attack plannedAttack = computerParty.currentPokemon().getAttack(possibleAttacks[PkmnArena.rand.nextInt(possibleAttacks.length)]);
				System.out.printf("%s's %s used %s!\n",computerParty.getOwner(),computerParty.currentPokemon().getName(),plannedAttack.getName());
				computerParty.currentPokemon().attack(attackingPokemon,plannedAttack);
				if(PkmnArena.options[PkmnArena.RESULT_DETAILS]){
					System.out.printf("%s's %s now has %d energy\n",computerParty.getOwner(),computerParty.currentPokemon().getName(),computerParty.currentPokemon().getEnergy());
					System.out.printf("Your %s now has %d health\n",userParty.currentPokemon().getName(),userParty.currentPokemon().getHealth());
				}
			}
			else{
				System.out.println(String.format("%s skipped their turn",computerParty.getOwner()));
			}
		}
		else if(computerParty.currentPokemon().getHealth() == 0){
			userParty.healAll();
			String oldPokemonName = computerParty.currentPokemon().getName();
			computerParty.setActive(computerParty.getActiveIndex()+1);
			System.out.printf("%s has fainted, next choice: %s!\n",oldPokemonName,computerParty.currentPokemon().getName());//lisa recommended this
		}
		else{
			
		}
	}
	public static void computerTurn(Party uParty, Party cParty){
		//Computer
		uParty.restAll();
		if(uParty.numAlive() == 0){
			// Thing that happens when the user has no pokemon anymore
		}
		else if(cParty.numAlive()>0){
			System.out.println(String.format("---------- %s's Turn! ----------",cParty.getOwner()));
			computerMove(cParty,uParty);
		}
			//phase = SELECTING_ACTION;
		else{
			System.out.printf("%s has fainted!\n",cParty.currentPokemon().getName());
			System.out.printf("%s has no more available pokemon!\n",cParty.getOwner());
		}
		if(uParty.currentPokemon().getHealth()<=0){
			System.out.printf("%s has fainted!\n",uParty.currentPokemon().getName());
			if(cParty.numAlive()>0){
					System.out.println("Pick a new pokemon");
					uParty.pickActive(false);
			}
		}
		cParty.restAll();
	}
	public static String makeBar(int currentHealth, int maxHealth){
		Double remainingHealth = new Double(((float)currentHealth/(float)maxHealth)*10.0);
		int remainingHealthInt = remainingHealth.intValue();
		int leftoverHealth = 10-remainingHealthInt;
		return multiplyLetter(remainingHealthInt,"|")+multiplyLetter(leftoverHealth,":");
	}
	public static String multiplyLetter(int times, String letter){
		return multiplyLetter(times,letter,"");
	}
	public static String multiplyLetter(int times, String letter, String made){
		return times == 0?made:multiplyLetter(times-1,letter,made+letter);
	}
}