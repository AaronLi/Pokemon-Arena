//Pokemon.java
//Aaron Li
// Class for dealing with constructing pokemon from Strings as well as the pokemon themselves and when they attack
import java.util.ArrayList;
import java.util.Arrays;

public class Pokemon{
	//Pokemon variables
	private int hp, maxHp;
	private String name, type, resistance, weakness;
	private boolean attacked = false; //whether the pokemon attacked in the last turn. If this is true then the pokemon will not recharge at the end of the users turn
	private boolean[]debuffs = {false,false};
	private int energy = 50;
	private ArrayList<Attack> moves = new ArrayList<Attack>();
	//Static variables for constructing a Pokemon object from a String
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
	
	//Status effects 
	public static final int STUN_STATUS = 0;
	public static final int DISABLE_STATUS = 1;
	
	public Pokemon(String infoIn){ // Constructor
		int energyCost,damage,numAttacks;
		String atName, special;
		String[] pokeInfo = infoIn.split(","); // split the input into an array of Strings
		name = pokeInfo[NAME];
		hp = Integer.parseInt(pokeInfo[HEALTH]); // health and max health should be integers
		maxHp = Integer.parseInt(pokeInfo[HEALTH]);
		type = Character.toUpperCase(pokeInfo[TYPE].charAt(0))+pokeInfo[TYPE].substring(1); // capitalize the type, weakness, and resistance for easier printing in the future
		resistance = Character.toUpperCase(pokeInfo[RESISTANCE].charAt(0))+pokeInfo[RESISTANCE].substring(1); 
		weakness = Character.toUpperCase(pokeInfo[WEAKNESS].charAt(0))+pokeInfo[WEAKNESS].substring(1);
		numAttacks = Integer.parseInt(pokeInfo[NUM_ATTACKS]);// used for getting the right amount of attacks
		for(int i = ATTACKS_START; i<ATTACKS_START+(4*numAttacks); i+=4){ 
			atName = pokeInfo[i+ATTACK_NAME];
			energyCost = Integer.parseInt(pokeInfo[i+ENERGY_COST]);
			damage = Integer.parseInt(pokeInfo[i+DAMAGE]);
			special = Character.toUpperCase(pokeInfo[i+SPECIAL].charAt(0))+pokeInfo[i+SPECIAL].substring(1); // capitalize the special for printing
			moves.add(new Attack(atName,energyCost,damage,special)); // add attack to the pokemon's move list
		}
	}
	public Pokemon(Pokemon pkmnIn){ //constructor for cloning pokemon
		this.name = new String(pkmnIn.name);
		this.hp = pkmnIn.hp;
		this.maxHp = pkmnIn.maxHp;
		this.type = pkmnIn.type;
		this.resistance = pkmnIn.resistance;
		this.weakness = pkmnIn.weakness;
		this.energy = pkmnIn.energy;
		this.moves = new ArrayList<Attack>(pkmnIn.moves);
	}
	public String getName(){
		return this.name;
	}
	public boolean getDisable(){
		return debuffs[DISABLE_STATUS];
	}
	public boolean getStun(){
		return debuffs[STUN_STATUS];
	}
	public int getHealth(){
		return this.hp;
	}
	public int getMaxHealth(){
		return this.maxHp;
	}
	public int getEnergy(){
		return energy;
	}
	public void setAttacked(boolean attacked){
		this.attacked = attacked;
	}
	public void setHealth(int health){
		this.hp = Math.min(Math.max(0,health),maxHp); // health can't go below 0 or above max health
	}
	public void setDisable(boolean value){
		debuffs[DISABLE_STATUS] = value;
	}
	public void setStun(boolean value){
		debuffs[STUN_STATUS] = value;
	}
	public void recharge(int amount){
		energy = Math.min(50,energy+amount); // recharge method that won't allow the pokemon to exceed 
	}
	public void heal(int amount){
		setHealth(this.hp+amount);
	}
	public Integer[] availableAttacks(){ // returns the indexes of the attacks the pokemon can use
		ArrayList<Integer> possibleAttacks = new ArrayList<Integer>();
		for(int i = 0;i < moves.size();i++){
			if(moves.get(i).getCost() <= energy){ // will only add it to the list of returned values if the pokemon has more energy than the cost of the attack
				possibleAttacks.add(new Integer(i));
			}
		}
		return possibleAttacks.toArray(new Integer[possibleAttacks.size()]); // return as an array
	}
	public void attack(Pokemon target, Attack attack){ //Method used for attacking other pokemon
		int damage = attack.getDamage(); // basic damage without any modifiers
		this.energy -= attack.getCost(); // reduce the attack cost from the attacking pokemon's energy pool
		if(attack.getSpecial().equals("Wild card") && PkmnArena.rand.nextBoolean()){ // check whether or not the wild card effect is possible
			System.out.println("The attack failed...");
		}
		else{
			if(this.type.equals(target.weakness)){ // double the damage if the pokemon's type is the target's weakness
				damage*=2;
				System.out.println("Super effective! x2 damage!");
			}
			else if(this.type.equals(target.resistance)){ // halve the damage if the pokemon's type is the target's resistance
				damage/=2;
				System.out.println("Not very effective... x1/2 damage");
			}
			System.out.printf("%s dealt %d damage!\n",name,Math.max(0,damage-(debuffs[DISABLE_STATUS]?10:0))); // Print the resulting damage with the disable debuff deducted
			target.damage(damage-(debuffs[DISABLE_STATUS]?10:0)); // damage the targeted pokemon
		}
		//Specials
		if(attack.getSpecial().equals("Stun")){ // if the special is stun
			if(PkmnArena.rand.nextBoolean()){ // rng 50/50 chance
				System.out.printf("%s has been stunned!\n",target.getName()); // Print if the pokemon has been stunned
				target.debuffs[STUN_STATUS] = true; // make the pokemon stunned
			}
		}
		else if(attack.getSpecial().equals("Wild storm")){ // If the special is Wild storm
			while(PkmnArena.rand.nextBoolean()){ // Loop with 50/50 chance of breaking each iteration
				System.out.printf("Wild storm! %s used %s again and dealt an additional %d damage!\n",name,attack.getName(),Math.max(damage-(debuffs[DISABLE_STATUS]?10:0),0)); // Print the resulting successful wild storm
				target.damage(damage-(debuffs[DISABLE_STATUS]?10:0)); // damage the target
			}
		}
		else if(attack.getSpecial().equals("Disable")){ // if the special is Disable
			System.out.printf("%s has been disabled!\n",target.getName()); // print that the target is disabled
			target.debuffs[DISABLE_STATUS] = true; // disable the target
		}
		else if(attack.getSpecial().equals("Recharge")){ // if the special is Recharge
			System.out.printf("%s has recharged 20 energy\n",this.name); // print the result
			recharge(20); // recharge 20 energy
		}
		else if(attack.getSpecial().equals("Heal")){ // if the special is heal
			System.out.printf("%s has healed 20 health\n",this.name); // print the result
			heal(20); // heal 20 hp
		};
		attacked = true; // set the pokemon to attacked so it won't gain energy at the end of the round
	}
	

	public String[] attacks(){ // returns a list of all the names of the pokemon's attacks
		String[] sOut = new String[moves.size()]; // create an array that's as large as the pokemon's move list
		for (int i = 0; i<moves.size();i++){ // go through the pokemons moves while adding the names of each one to sOut
			sOut[i] = moves.get(i).getName();
		}
		return sOut;
	}
	public Attack getAttack(int attackIndex){ // get an attack object from the pokemon's move list
		return moves.get(attackIndex);
	}
	public boolean hasAttacked(){
		return attacked;
	}
	public void damage(int dmgAmt){ // subtract health from a pokemon
		this.setHealth(this.getHealth()-Math.min(dmgAmt,this.getHealth())); // damage will either reduce health or set it to 0
	}

	public String toString(){
		String attackString = "";
		String[] atNames = this.attacks(); // get the names of the attacks
		for(int i =0; i<atNames.length;i++){
			attackString += String.format("MOV %d: %-13s ",i+1,atNames[i]); // format the attack information for printing
		}
		return String.format("%-15s HP: %s %-7s NRG: %-2d/50 TYP: %-8s RST: %-8s WKS: %-8s %-30s"+(debuffs[0]?" Stunned":"")+(debuffs[1]?" Disabled":""),name,PkmnTools.makeBar(hp,maxHp),hp+"/"+maxHp,energy,type,resistance,weakness,attackString); // add the debuffs onto the end of the pokemon's info when printing
	}
	
	public boolean equals(Object obj){ // used for figuring out whether two pokemon are equal or not
		if(obj instanceof Pokemon){ //check if the given object is a Pokemon
			return ((Pokemon)obj).name.equals(this.name); // is equal if the objects are both pokemon and their names are equal
		}
		return false;
	}
	
}