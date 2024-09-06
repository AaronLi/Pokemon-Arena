package com.dumfing.pokemon_arena;
//PkmnIOTools.java
//Aaron Li
//Pokedex, class that is used for reading information from the data file then turning it into an arraylist of pokemon that you can get from it
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.stream.Stream;

public class Pokedex{

	private List<Pokemon>pkmnList = new ArrayList<Pokemon>(); // used for storing all pokemon
	
	public Pokedex(String dataFile) throws IOException { //constructor, you pass the file name and it'll read the pokemon into the pokemon list
		pkmnList = Pokedex.readPokemonFromFile(dataFile);
	}
	
	public static List<Pokemon> readPokemonFromFile(String dataFile) throws IOException { // Method used for reading the pokemon from the textfile and adding them to the pkmnList
		try(Stream<String> lines = Files.lines(Path.of(dataFile))){
			// skip first line which contains number of pokemon
			return lines.skip(1).map(Pokemon::fromString).toList();
		}
	}
	
	public Pokemon getPokemon(int pokeIndex){ // get a pokemon from the pkmnList by index
		return pkmnList.get(pokeIndex).toBuilder().attacked(false).build();
	}
	public List<String> pokemonNames(){ // The returns the names of all loaded pokemon in an arraylist of strings
		return pkmnList.stream().map(Pokemon::getName).toList();
	}
	public List<Pokemon> allPokemon(){ // return an copy of the arraylist that holds all of the pokemon
		return new ArrayList<>(pkmnList);
	}
	public int size(){ // returns the number of pokemon there are
		return pkmnList.size();
	}
}