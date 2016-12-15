//PkmnIOTools.java
//Aaron Li
//Pokedex, class that is used for reading information from the data file then turning it into an arraylist of pokemon that you can get from it
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Pokedex{

	private ArrayList<Pokemon>pkmnList = new ArrayList<Pokemon>(); // used for storing all pokemon
	
	public Pokedex(String dataFile){ //constructor, you pass the file name and it'll read the pokemon into the pokemon list
		pkmnList = readPokemon(dataFile);
	}
	
	public ArrayList<Pokemon> readPokemon(String dataFile){ // Method used for reading the pokemon from the textfile and adding them to the pkmnList
		ArrayList<Pokemon> pokeListOut = new ArrayList<Pokemon>(); // Arraylist that will be returned
		try{
			Scanner pokeFile = new Scanner(new BufferedReader(new FileReader(dataFile))); // open the file for reading
			Pokemon pokeOut; // Pokemon object for adding to the list
			int numPokemon = Integer.parseInt(pokeFile.nextLine()); // Skip first line that says how many pokemon there are
			while(pokeFile.hasNextLine()){ // continue reading till end of file
				pokeOut = new Pokemon(pokeFile.nextLine()); // create a new Pokemon object with the line that was read
				pokeListOut.add(pokeOut); // add the new pokemon to the pokelist
			}
		}
		catch(IOException ex){ // if there is an IOException
			System.err.println("File not found");
		}
		return pokeListOut;
	}
	
	public Pokemon getPokemon(int pokeIndex){ // get a pokemon from the pkmnList by index
		return new Pokemon(pkmnList.get(pokeIndex)); // return a copy of that pokemon
	}
	public ArrayList<String> pokemonNames(){ // The returns the names of all loaded pokemon in an arraylist of strings 
		ArrayList<String>namesOut = new ArrayList<String>();
		for(Pokemon pkmn:pkmnList){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allPokemon(){ // return an copy of the arraylist that holds all of the pokemon
		return new ArrayList<Pokemon>(pkmnList);
	}
	public int size(){ // returns the number of pokemon there are
		return pkmnList.size();
	}
}