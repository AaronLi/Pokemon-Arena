//PkmnArena.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
public class PkmnArena{
	
	public static Pokedex pokedex = new Pokedex("pokemon.txt");
	
	public static void main(String[]args){
		Party userParty = Party.pickParty(pokedex);
		Party computerParty = Party.computerParty(pokedex,userParty);
		boolean running = false;
		while(running){
			
		}
	}

}