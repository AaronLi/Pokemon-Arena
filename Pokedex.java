//PkmnIOTools.java
//Aaron Li
//Pokedex, used for reading from datafile first then used for getting information about a pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Pokedex{
	
	public static HashMap<String,Integer> types = new HashMap<String,Integer>();
	private ArrayList<Pokemon>pkmnList = new ArrayList<Pokemon>();
	
	public Pokedex(String dataFile){
		types.put(" ",Pokemon.NO_TYPE);
		types.put("earth",Pokemon.EARTH);
		types.put("fire",Pokemon.FIRE);
		types.put("leaf",Pokemon.LEAF);
		types.put("water",Pokemon.WATER);
		types.put("fighting",Pokemon.FIGHTING);
		types.put("electric",Pokemon.ELECTRIC);
		types.put("normal",Pokemon.NORMAL);
		types.put("psychic",Pokemon.PSYCHIC);
		pkmnList = readPokemon(dataFile);
	}
	
	public ArrayList<Pokemon> readPokemon(String dataFile){
		ArrayList<Pokemon> pokeListOut = new ArrayList<Pokemon>();
		try{
			Scanner pokeFile = new Scanner(new BufferedReader(new FileReader(dataFile)));
			Pokemon pokeOut;
			int numPokemon = Integer.parseInt(pokeFile.nextLine());
			while(pokeFile.hasNextLine()){
				pokeOut = new Pokemon(pokeFile.nextLine());
				pokeListOut.add(pokeOut);
			}
		}
		catch(IOException ex){
			System.err.println("File not found");
		}
		return pokeListOut;
	}
	
	public Pokemon getPokemon(int pokeIndex){
		return new Pokemon(pkmnList.get(pokeIndex));
	}
	public ArrayList<String> pokemonNames(){
		ArrayList<String>namesOut = new ArrayList<String>();
		for(Pokemon pkmn:pkmnList){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allPokemon(){
		return new ArrayList<Pokemon>(pkmnList);
	}
	public int size(){
		return pkmnList.size();
	}
}