package com.dumfing.pokemon_arena;
//PkmnTools.java
//Aaron Li
//Deals with the bot's moves and random naming as well as visualising health bars
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class PkmnTools{
	private static List<String> possibleNames = List.of(); // all names in the file
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_BRIGHT_CYAN = "\u001B[96m";
	public static final String ANSI_BG_WHITE = "\u001B[47m";
	public static final String ANSI_BG_RED = "\u001B[41m";
	public static final String[] ANSI_RYG = {ANSI_RED, ANSI_YELLOW, ANSI_GREEN};
	public static final String[] ANSI_PBC = {ANSI_PURPLE, ANSI_CYAN, ANSI_BRIGHT_CYAN};
	public static String enemyName() throws IOException {
		if(possibleNames.isEmpty()) {
			try (Stream<String> lines = Files.lines(Path.of("possibleNames.txt"))) {
				possibleNames = lines.toList();
			}
		}

		return possibleNames.get(PkmnBattle.rand.nextInt(possibleNames.size()));
	}
	public static void computerMove(Party computerParty, Party userParty){ // What happens when it's the Computer's turn to attack
		List<Attack> possibleAttacks = computerParty.currentPokemon().availableAttacks(); // get the attacks that the pokemon has enough energy to use
		if(computerParty.currentPokemon().getStun()){ // will skip the turn if the pokemon is stunned
			System.out.printf("%s is stunned! The %s's turn has been skipped.\n",computerParty.currentPokemon().getName(),computerParty.getOwner());
			computerParty.currentPokemon().setStun(false);
		}
		else if(!possibleAttacks.isEmpty()){ //if there are usable attacks for the pokemon to use
			Pokemon attackingPokemon = userParty.currentPokemon();
			Attack plannedAttack = possibleAttacks.get(PkmnBattle.rand.nextInt(possibleAttacks.size())); // get a random attack from the list of possible attacks
			System.out.printf("%s's %s used %s!\n",computerParty.getOwner(),computerParty.currentPokemon().getName(),plannedAttack.getName()); // print out what the computer chose
			computerParty.currentPokemon().attack(attackingPokemon,plannedAttack); // attack the user's pokemon with the seslected attack
			if(PkmnBattle.options[PkmnBattle.RESULT_DETAILS]){ // if the user wants more details about the attack
				System.out.printf("%s's %s now has"+PkmnTools.pbcColourMultiplier(computerParty.currentPokemon().getEnergy(), 50)+" %d energy\n"+PkmnTools.ANSI_RESET,computerParty.getOwner(),computerParty.currentPokemon().getName(),computerParty.currentPokemon().getEnergy());
				System.out.printf("Your %s now has"+PkmnTools.rygColourMultiplier(userParty.currentPokemon().getHealth(),userParty.currentPokemon().getMaxHealth())+" %d health\n"+PkmnTools.ANSI_RESET,userParty.currentPokemon().getName(),userParty.currentPokemon().getHealth());
			}
		}
		else{
			System.out.printf("%s skipped their turn%n",computerParty.getOwner()); // What happens when the bot doesn't have enough energy to attack
		}
	}
	public static void computerTurn(Party uParty, Party cParty, Scanner kb){ // deals with whether the bot has been defeated or not
		//Computer

		if(!cParty.getLivingPokemon().isEmpty()){ // if the computer's party still has pokemon

			// If computer starts its turn with no health, have it pick a new pokemon
			if(cParty.currentPokemon().getHealth() == 0) {
				uParty.healAll(); // heal all the user's pokemon
				String oldPokemonName = cParty.currentPokemon().getName(); // the name of the pokemon that fainted
				Optional<Pokemon> nextPokemon = cParty.getNextActivePokemon();
				cParty.setActive(nextPokemon.get()); // never false because getLivingPokemon is not empty
				System.out.printf(PkmnTools.ANSI_RED + "%s has fainted, next choice: %s!\n" + PkmnTools.ANSI_RESET, oldPokemonName, cParty.currentPokemon().getName());//print out the bot's next active pokemon
			}

			System.out.printf("---------- %s's Turn! ----------%n",cParty.getOwner()); // Start bot's turn
			computerMove(cParty, uParty);// let the bot pick an attack to use
		}
		else{ // if the computerParty has no living pokemon
			System.out.printf(PkmnTools.ANSI_RED+"%s has fainted!\n"+PkmnTools.ANSI_RESET,cParty.currentPokemon().getName()); // When the bot's pokemon have all fainted
			System.out.printf("%s has no more available pokemon!\n",cParty.getOwner());
		}
		cParty.restAll(); // recharge all the pokemon on the computer's team
	}
	public static String makeBar(int currentHealth, int maxHealth){ // Make a health bar for printing a visual representation of the pokemon's health
		double remainingHealth = ((float) currentHealth / (float) maxHealth) * 10.0; // divide with floats first because dividing with integers will lose the decimal places
		int remainingHealthInt = (int) remainingHealth; // convert from Double to int
		int leftoverHealth = 10-remainingHealthInt; // find the remaining units in the health bar
		return PkmnTools.rygColourMultiplier(currentHealth,maxHealth)+multiplyLetter(remainingHealthInt,"▓")+multiplyLetter(leftoverHealth,"░")+PkmnTools.ANSI_RESET; // make the health bar with | and :
	}
	public static String multiplyLetter(int times, String letter){ // recursively add multiple letters together
		return multiplyLetter(times,letter,"");
	}
	public static String multiplyLetter(int times, String letter, String made){
		return times == 0?made:multiplyLetter(times-1,letter,made+letter);//call multiplyLetter while adding the letter to the end of made then reducing the number of times it should be added
	}
	public static String pickColourMultiplier(int val, int max, String[] colourScale){
		return colourScale[(int)((colourScale.length-1)*((double)val/(double)max))];
	}
	public static String rygColourMultiplier(int val, int max){
		return pickColourMultiplier(val, max, ANSI_RYG);
	}
	public static String pbcColourMultiplier(int val, int max){
		return pickColourMultiplier(val, max, ANSI_PBC);
	}
}
