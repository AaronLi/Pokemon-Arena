import java.util.Scanner;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PkmnTools{
	private static ArrayList<String> possibleNames = new ArrayList<String>();
	public static String enemyName(){
		try{
			Scanner nameFile = new Scanner(new BufferedReader(new FileReader("possibleNames.txt")));
			while(nameFile.hasNextLine()){
				possibleNames.add(nameFile.nextLine());
			}
		}
		catch(IOException e){
			System.err.println("File not found");
		}
		return possibleNames.get(PkmnArena.rand.nextInt(possibleNames.size()));
	}
}