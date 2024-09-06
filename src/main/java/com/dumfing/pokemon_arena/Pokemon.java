package com.dumfing.pokemon_arena;
//Pokemon.java
//Aaron Li
// Class for dealing with constructing pokemon from Strings as well as the pokemon themselves and when they attack
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

@Builder(toBuilder = true)
public class Pokemon{
	//Pokemon variables
	@Getter
    private int health;
    @Getter
    private final int maxHealth;
	@Getter
    private final String name;
    @Getter
    private final String type;
    @Getter
    private final String resistance;
    @Getter
    private final String weakness;
	@Setter
	@Builder.Default
    private boolean attacked = false; //whether the pokemon attacked in the last turn. If this is true then the pokemon will not recharge at the end of the users turn
	private final boolean[]debuffs = {false,false};
	@Getter
	@Builder.Default
    private int energy = 50;
	@Getter
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
	
	public static Pokemon fromString(String infoIn){ // Constructor
		int energyCost,damage,numAttacks;
		String atName, special;
		String[] pokeInfo = infoIn.split(","); // split the input into an array of Strings
		System.out.println(Arrays.toString(pokeInfo));
		PokemonBuilder builder = Pokemon.builder()
				.name(pokeInfo[NAME])
				.health(Integer.parseInt(pokeInfo[HEALTH]))
				.maxHealth(Integer.parseInt(pokeInfo[HEALTH]))
				.type(Character.toUpperCase(pokeInfo[TYPE].charAt(0))+pokeInfo[TYPE].substring(1))
				.resistance(Character.toUpperCase(pokeInfo[RESISTANCE].charAt(0))+pokeInfo[RESISTANCE].substring(1))
				.weakness(Character.toUpperCase(pokeInfo[WEAKNESS].charAt(0))+pokeInfo[WEAKNESS].substring(1));
		ArrayList<Attack> moves = new ArrayList<>();
		numAttacks = Integer.parseInt(pokeInfo[NUM_ATTACKS]);// used for getting the right amount of attacks
		for(int i = ATTACKS_START; i<ATTACKS_START+(4*numAttacks); i+=4){ 
			atName = pokeInfo[i+ATTACK_NAME];
			energyCost = Integer.parseInt(pokeInfo[i+ENERGY_COST]);
			damage = Integer.parseInt(pokeInfo[i+DAMAGE]);
			special = pokeInfo[i+SPECIAL].toUpperCase(Locale.ROOT).replace(" ", "_"); // capitalize the special for printing
			if(special.equals("_")) {
				special = "NONE";
			}
			moves.add(new Attack(atName,Attack.Special.valueOf(special),energyCost,damage)); // add attack to the pokemon's move list
		}
		return builder.moves(moves).build();
	}

    public boolean getDisable(){
		return debuffs[DISABLE_STATUS];
	}
	public boolean getStun(){
		return debuffs[STUN_STATUS];
	}

    public void setHealth(int health){
		this.health = Math.min(Math.max(0,health), maxHealth); // health can't go below 0 or above max health
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
		System.out.printf(PkmnTools.ANSI_GREEN+"%s has healed 20 health!\n",getName());
		setHealth(this.health + amount);
	}
	public List<Attack> availableAttacks(){ // returns the indexes of the attacks the pokemon can use
		return moves.stream().filter(move->move.getCost() <= energy).toList();
	}
	public void attack(Pokemon target, Attack attack){ //Method used for attacking other pokemon
		int damage = attack.getDamage(); // basic damage without any modifiers
		this.energy -= attack.getCost(); // reduce the attack cost from the attacking pokemon's energy pool
		if(attack.getSpecial().equals(Attack.Special.WILD_CARD) && PkmnBattle.rand.nextBoolean()){ // check whether or not the wild card effect is possible
			System.out.println(PkmnTools.ANSI_PURPLE+"The attack failed..."+PkmnTools.ANSI_RESET);
		}
		else{
			if(this.type.equals(target.weakness)){ // double the damage if the pokemon's type is the target's weakness
				damage*=2;
				System.out.println("Super effective! "+PkmnTools.ANSI_GREEN+"x2 damage!"+PkmnTools.ANSI_RESET);
			}
			else if(this.type.equals(target.resistance)){ // halve the damage if the pokemon's type is the target's resistance
				damage/=2;
				System.out.println("Not very effective..."+PkmnTools.ANSI_RED+" x1/2 damage"+PkmnTools.ANSI_RESET);
			}
			System.out.printf("%s dealt "+PkmnTools.ANSI_RED+"%d damage!\n"+PkmnTools.ANSI_RESET,name,Math.max(0,damage-(debuffs[DISABLE_STATUS]?10:0))); // Print the resulting damage with the disable debuff deducted
			target.damage(damage-(debuffs[DISABLE_STATUS]?10:0)); // damage the targeted pokemon
		}
		//Specials
		if(attack.getSpecial().equals(Attack.Special.STUN)){ // if the special is stun
			if(PkmnBattle.rand.nextBoolean()){ // rng 50/50 chance
				System.out.printf(PkmnTools.ANSI_YELLOW+"%s has been stunned!\n"+PkmnTools.ANSI_RESET,target.getName()); // Print if the pokemon has been stunned
				target.debuffs[STUN_STATUS] = true; // make the pokemon stunned
			}
		}
		else if(attack.getSpecial().equals(Attack.Special.WILD_STORM)){ // If the special is Wild storm
			while(PkmnBattle.rand.nextBoolean()){ // Loop with 50/50 chance of breaking each iteration
				System.out.printf(PkmnTools.ANSI_RED+"Wild storm!"+PkmnTools.ANSI_RESET+" %s used %s again and dealt an additional"+PkmnTools.ANSI_RED+" %d damage!\n"+PkmnTools.ANSI_RESET,name,attack.getName(),Math.max(damage-(debuffs[DISABLE_STATUS]?10:0),0)); // Print the resulting successful wild storm
				target.damage(damage-(debuffs[DISABLE_STATUS]?10:0)); // damage the target
			}
		}
		else if(attack.getSpecial().equals(Attack.Special.DISABLE)){ // if the special is Disable
			System.out.printf(PkmnTools.ANSI_YELLOW+"%s has been disabled!\n"+PkmnTools.ANSI_RESET,target.getName()); // print that the target is disabled
			target.debuffs[DISABLE_STATUS] = true; // disable the target
		}
		else if(attack.getSpecial().equals(Attack.Special.RECHARGE)){ // if the special is Recharge
			System.out.printf(PkmnTools.ANSI_CYAN+"%s has recharged 20 energy\n"+PkmnTools.ANSI_RESET,this.name); // print the result
			recharge(20); // recharge 20 energy
		}
		else if(attack.getSpecial().equals(Attack.Special.HEAL)){ // if the special is heal
			System.out.printf(PkmnTools.ANSI_GREEN+"%s has healed 20 health\n"+PkmnTools.ANSI_RESET,this.name); // print the result
			heal(20); // heal 20 hp
		}
		attacked = true; // set the pokemon to attacked so it won't gain energy at the end of the round
	}
	

	public List<String> attackNames(){ // returns a list of all the names of the pokemon's attacks
		return moves.stream().map(Attack::getName).toList();
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

    @Override
	public String toString(){
		StringBuilder attackString = new StringBuilder();
		ListIterator<String> attackNameIterator = this.attackNames().listIterator();
		while (attackNameIterator.hasNext()) {
			int nextAttackNameIndex = attackNameIterator.nextIndex();
			String nextAttackName = attackNameIterator.next();
			attackString.append(String.format("MOV %d: %-16s ",nextAttackNameIndex+1,nextAttackName)); // format the attack information for printing
		}
		return String.format("%-15s "+PkmnTools.rygColourMultiplier(health, maxHealth)+"HP: %s"+PkmnTools.rygColourMultiplier(health, maxHealth)+" %-7s"+PkmnTools.pbcColourMultiplier(energy, 50)+" NRG: %-2d/50"+PkmnTools.ANSI_RESET+" TYP: %-8s RST: %-8s WKS: %-8s %-30s"+(debuffs[0]?" Stunned":"")+(debuffs[1]?" Disabled":""),name,PkmnTools.makeBar(health, maxHealth), health +"/"+ maxHealth,energy,type,resistance,weakness, attackString); // add the debuffs onto the end of the pokemon's info when printing
	}
	@Override
	public boolean equals(Object obj){ // used for figuring out whether two pokemon are equal or not
		if(obj instanceof Pokemon){ //check if the given object is a Pokemon
			return ((Pokemon)obj).name.equals(this.name); // is equal if the objects are both pokemon and their names are equal
		}
		return false;
	}
	
}
