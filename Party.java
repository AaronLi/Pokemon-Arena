//Party.java
//Aaron Li
//Takes care of the player's and computer's Pokemon
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
public class Party{
	private ArrayList<Pokemon> party = new ArrayList<Pokemon>();
	private int active = -1;
	public Party(Party partyIn){
		this.party = partyIn.party;
	}
	public Party(int active){
		this.active = active;
	}
	private static Scanner kb = new Scanner(System.in);
	public void addPokemon(Pokemon pkmnIn){
		party.add(pkmnIn);
	}
	public int size(){
		return party.size();
	}
	public Pokemon currentPokemon(){
		return party.get(active);
	}
	public void setActive(int reqActive){
		active = reqActive;
	}
	public boolean contains(Pokemon pkmnIn){
		return party.contains(pkmnIn);
	}
	public void restAll(){
		for(Pokemon pkmn:party){
			if(!pkmn.hasAttacked()){
				pkmn.recharge(10);
			}
			else{
				pkmn.setAttacked(false);
			}
		}
	}
	public void healAll(){
		for(Pokemon pkmn:party){
			pkmn.heal(20);
		}
	}
	public void attack(Party enemyParty){
	
	}
	public static Party pickParty(Pokedex pokedex){
		int picked;
		String uIn;
		String[] pokemonPickedWords = {"","second ","third ","fourth "};
		Party userParty = new Party(-1);
		ArrayList<String> pickablePokemon = pokedex.pokemonNames();
		ArrayList<Integer> pokemonNumbers = new ArrayList<Integer>();
		for(int i = 0; i < pickablePokemon.size(); i++){
			pokemonNumbers.add(new Integer(i));
		}
		
		int numPicked = 0;
		while(numPicked < 4){
			System.out.println("Please pick a "+pokemonPickedWords[numPicked]+"pokemon:\n");
			
			if(PkmnArena.options[PkmnArena.POKEMON_DETAILS]){
				System.out.println("s. Simple");
				for(int i = 0;i<pickablePokemon.size();i++){
					System.out.printf("%2d. %s\n",i+1,pokedex.getPokemon(pokemonNumbers.get(i)));
				}
			}
			else{
				System.out.println("d. Details");
				for(int i = 0;i<pickablePokemon.size(); i++){
					if(i%4 == 0){
						System.out.println();
					}
					System.out.printf("%2d. %-10s ",i+1,pickablePokemon.get(i));
				}	
			}
			System.out.println();
			uIn = kb.nextLine();
			if(uIn.equals("d")){
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = true;
			}
			else if(uIn.equals("s")){
				PkmnArena.options[PkmnArena.POKEMON_DETAILS] = false;
			}
			else{
				picked = Integer.parseInt(uIn);
				if (0<picked && picked < pickablePokemon.size()+1){
					userParty.addPokemon(pokedex.getPokemon(pokemonNumbers.get(picked-1)));
					pickablePokemon.remove(picked-1);
					pokemonNumbers.remove(picked-1);
					numPicked++;
				}
				else{
					System.out.println("Invalid Number");
				}
			}
		}
		return userParty;
	}
	
	public static Party computerParty(Pokedex pokedex, Party userPokemon){
		Party computerParty = new Party(0);
		ArrayList<Pokemon> remainingPokemon = pokedex.allPokemon();
		remainingPokemon.removeAll(userPokemon.allMembers());
		for(Pokemon pkmn : remainingPokemon){
			computerParty.addPokemon(pkmn);
		}
		return computerParty;
	}
	
	public ArrayList<String> partyNames(){
		ArrayList<String> namesOut = new ArrayList<String>();
		for(Pokemon pkmn : party){
			namesOut.add(pkmn.getName());
		}
		return namesOut;
	}
	public ArrayList<Pokemon> allMembers(){
		//return party;
		return new ArrayList<Pokemon>(party);
	}
	
	public String toString(){
		String sOut = "";
		for(int i = 0; i<party.size();i++){
			sOut+=party.get(i)+"\n";
		}
		return sOut;
	}
	public int numAlive(){
		int alive = 0;
		for(Pokemon pkmn : this.party){
			if(pkmn.getHealth()>0){
				alive++;
			}
		}
		return alive;
	}
	public int pickActive(){
		int uIn = 0;
		int nextPhase = PkmnArena.SELECTING_ACTION;
		ArrayList<Integer> livingPokemon =  new ArrayList<Integer>();
		System.out.println("0. Back");
		for(int i = 0;i<party.size();i++){
			if(party.get(i).getHealth()>0 && active != i){
				livingPokemon.add(new Integer(i));
				System.out.printf("%d. %s\n",livingPokemon.size(),PkmnArena.options[PkmnArena.POKEMON_DETAILS]?party.get(i).getName():party.get(i));
			}
		}
		uIn = Integer.parseInt(kb.nextLine());
		if(uIn>0 && uIn<livingPokemon.size()+1){
			setActive(livingPokemon.get(uIn-1));
			System.out.printf("%s, I choose you!\n",this.currentPokemon().getName());
			nextPhase = PkmnArena.COMPUTER_TURN;
		}
		else{
			//System.out.println("Invalid pokemon");
			//Action if input is not linked to option
		}
		return nextPhase;
	}
	public int pickStarting(){
		int uIn = 0;
		int nextPhase = PkmnArena.SELECTING_ACTIVE;
		ArrayList<Integer> livingPokemon =  new ArrayList<Integer>();
		for(int i = 0;i<party.size();i++){
			if(party.get(i).getHealth()>0 && active != i){
				livingPokemon.add(new Integer(i));
				System.out.printf("%d. %s\n",livingPokemon.size(),PkmnArena.options[PkmnArena.POKEMON_DETAILS]?party.get(i):party.get(i).getName());
			}
		}
		uIn = Integer.parseInt(kb.nextLine());
		if(uIn>0 && uIn<livingPokemon.size()+1){
			setActive(livingPokemon.get(uIn-1));
			System.out.printf("%s, I choose you!\n",this.currentPokemon().getName());
			nextPhase = PkmnArena.rand.nextBoolean()?PkmnArena.SELECTING_ACTION : PkmnArena.COMPUTER_TURN;
		}
		else{
			//System.out.println("Invalid pokemon");
			//Action if input is not linked to option
		}
		return nextPhase;
	}
	public int getActiveIndex(){
		return active;
	}
}