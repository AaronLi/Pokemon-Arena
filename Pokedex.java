//PkmnIOTools.java
//Aaron Li
//Pokedex, used for reading from datafile first then used for getting information about a pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Pokedex{
	private static final int NAME = 0;
	private static final int HEALTH = 1;
	private static final int TYPE = 2;
	private static final int RESISTANCE = 3;
	private static final int WEAKNESS = 4;
	private static final int NUM_ATTACKS = 5;
	private static final int ATTACKS_START = 6;
	
	private static final int ATTACK_NAME = 0;
	private static final int ENERGY_COST = 1;
	private static final int DAMAGE = 2;
	private static final int SPECIAL = 3;
	
	
	Map<String,Integer> types = new HashMap<String,Integer>();
	private HashMap<String,Pokemon> pkmnList = new HashMap<String,Pokemon>();
	
	public Pokedex(String dataFile){
		types.put(" ",Pokemon.NO_TYPE);
		types.put("earth",Pokemon.EARTH);
		types.put("fire",Pokemon.FIRE);
		types.put("leaf",Pokemon.LEAF);
		types.put("water",Pokemon.WATER);
		types.put("fighting",Pokemon.FIGHTING);
		types.put("electric",Pokemon.ELECTRIC);
		pkmnList = readPokemon(dataFile);
	}
	
	public HashMap<String,Pokemon> readPokemon(String dataFile){
		
		HashMap<String,Pokemon> pokeListOut = new HashMap<String,Pokemon>();
		try{
			Scanner pokeFile = new Scanner(new BufferedReader(new FileReader(dataFile)));
			
			int hp,numAttacks, type, resistance, weakness, energyCost, damage;
			int special = 0;
			String name, atName;
			String[] lineIn;
			Pokemon pokeOut;
			int numPokemon = Integer.parseInt(pokeFile.nextLine());
			
			while(pokeFile.hasNextLine()){
				lineIn = pokeFile.nextLine().split(",");
				name = lineIn[NAME];
				hp = Integer.parseInt(lineIn[HEALTH]);
				type = types.get(lineIn[TYPE]);
				resistance = types.get(lineIn[RESISTANCE]);
				weakness = types.get(lineIn[WEAKNESS]);
				numAttacks = Integer.parseInt(lineIn[NUM_ATTACKS]);
				pokeOut = new Pokemon(name,hp,type,resistance,weakness);
				for(int i = ATTACKS_START; i<ATTACKS_START+(4*numAttacks); i+=4){
					atName = lineIn[i+ATTACK_NAME];
					energyCost = Integer.parseInt(lineIn[i+ENERGY_COST]);
					damage = Integer.parseInt(lineIn[i+DAMAGE]);
					for(int j = 0;j<Attack.specials.length;j++){
						if(Attack.specials[j].equals(lineIn[i+SPECIAL])){
							special = j;
							break;
						}
					}
					pokeOut.addAttack(atName,energyCost,damage,special);
				}
				pokeListOut.put(name,pokeOut);
			}
		}
		catch(IOException ex){
			System.out.println("File not found");
		}
		return pokeListOut;
	}
	
	public Pokemon getPokemon(String pokeName){
		return new Pokemon(pkmnList.get(pokeName));
	}
}