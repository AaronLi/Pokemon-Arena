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
}