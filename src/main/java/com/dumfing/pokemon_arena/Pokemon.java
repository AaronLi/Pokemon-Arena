package com.dumfing.pokemon_arena;
//Pokemon.java
//Aaron Li
// Class for dealing with constructing pokemon from Strings as well as the pokemon themselves and when they attack
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

@Builder(toBuilder = true)
public class Pokemon{

	@Data
	private static class Debuffs {
		private boolean stunned = false;
		private boolean disabled = false;
	}

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
    private boolean attackedThisTurn = false; //whether the pokemon attacked in the last turn. If this is true then the pokemon will not recharge at the end of the users turn
	private final Debuffs debuffs = new Debuffs();

	@Setter
	@Getter
	@Builder.Default
    private int energy = 50;
	@Getter
	@Builder.Default
    private ArrayList<Attack> moves = new ArrayList<>();
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
	
	public static Pokemon fromString(String infoIn){ // Constructor
		int energyCost,damage,numAttacks;
		String atName, special;
		String[] pokeInfo = infoIn.split(","); // split the input into an array of Strings
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
		return debuffs.isDisabled();
	}
	public boolean getStun(){
		return debuffs.isStunned();
	}

    public void setHealth(int health){
		this.health = Math.min(Math.max(0,health), maxHealth); // health can't go below 0 or above max health
	}
	public void setDisable(boolean value){
		debuffs.setDisabled(value);
	}
	public void setStun(boolean value){
		debuffs.setStunned(value);
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

	public List<String> attackNames(){ // returns a list of all the names of the pokemon's attacks
		return moves.stream().map(Attack::getName).toList();
	}
	public Attack getAttack(int attackIndex){ // get an attack object from the pokemon's move list
		return moves.get(attackIndex);
	}
	public boolean hasAttacked(){
		return attackedThisTurn;
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
		return String.format("%-15s "+PkmnTools.rygColourMultiplier(health, maxHealth)+"HP: %s"+PkmnTools.rygColourMultiplier(health, maxHealth)+" %-7s"+PkmnTools.pbcColourMultiplier(energy, 50)+" NRG: %-2d/50"+PkmnTools.ANSI_RESET+" TYP: %-8s RST: %-8s WKS: %-8s %-30s"+(debuffs.isStunned()?" Stunned":"")+(debuffs.isDisabled()?" Disabled":""),name,PkmnTools.makeBar(health, maxHealth), health +"/"+ maxHealth,energy,type,resistance,weakness, attackString); // add the debuffs onto the end of the pokemon's info when printing
	}
	@Override
	public boolean equals(Object obj){ // used for figuring out whether two pokemon are equal or not
		if(obj instanceof Pokemon){ //check if the given object is a Pokemon
			return ((Pokemon)obj).name.equals(this.name); // is equal if the objects are both pokemon and their names are equal
		}
		return false;
	}
	
}
