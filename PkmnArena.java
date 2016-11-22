//PkmnArena.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
public class PkmnArena{
	public static void main(String[]args){
		Pokedex pokedex = new Pokedex("pokemon.txt");
		Pokemon voltorb = pokedex.getPokemon("Voltorb");
		Pokemon gyarados = pokedex.getPokemon("Gyarados");
		System.out.println(gyarados.toString());
		voltorb.attack(gyarados,voltorb.getAttack("Explode"));
		System.out.println(gyarados.toString());
		System.out.println(pokedex.getPokemon("Gyarados").toString());
	}
}