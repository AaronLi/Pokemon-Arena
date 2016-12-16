//Pokemon.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;

public class Pokemon{
	//Pokemon variables
	private int hp, maxHp;
	private String name, type, resistance, weakness;
	private boolean attacked = false;
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
	
	public Pokemon(String infoIn){
		int special = 0;
		int energyCost,damage,numAttacks;
		String atName;
		String[] pokeInfo = infoIn.split(",");
		name = pokeInfo[NAME];
		hp = Integer.parseInt(pokeInfo[HEALTH]);
		maxHp = Integer.parseInt(pokeInfo[HEALTH]);
		type = Character.toUpperCase(pokeInfo[TYPE].charAt(0))+pokeInfo[TYPE].substring(1);
		resistance = Character.toUpperCase(pokeInfo[RESISTANCE].charAt(0))+pokeInfo[RESISTANCE].substring(1);
		weakness = Character.toUpperCase(pokeInfo[WEAKNESS].charAt(0))+pokeInfo[WEAKNESS].substring(1);
		numAttacks = Integer.parseInt(pokeInfo[NUM_ATTACKS]);
		for(int i = ATTACKS_START; i<ATTACKS_START+(4*numAttacks); i+=4){
			atName = pokeInfo[i+ATTACK_NAME];
			energyCost = Integer.parseInt(pokeInfo[i+ENERGY_COST]);
			damage = Integer.parseInt(pokeInfo[i+DAMAGE]);
			for(int j = 0;j<Attack.specials.length;j++){
				if(Attack.specials[j].equals(pokeInfo[i+SPECIAL])){
					special = j;
					break;
				}
			}
			addAttack(atName,energyCost,damage,special);
		}
	}
	public Pokemon(Pokemon pkmnIn){
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
	public int getEnergy(){
		return energy;
	}
	public void setAttacked(boolean attacked){ // So the attacking pokemon doesn't recover energy
		this.attacked = attacked;
	}
	public void setHealth(int health){
		this.hp = Math.min(Math.max(0,health),maxHp);
	}
	public void setDisable(boolean value){
		debuffs[DISABLE_STATUS] = value;
	}
	public void setStun(boolean value){
		debuffs[STUN_STATUS] = value;
	}
	public void addAttack(String name, int cost, int damage, int special){
		moves.add(new Attack(name,cost,damage,special));
	}
	public void recharge(int amount){
		energy = Math.min(50,energy+amount);
	}
	public void heal(int amount){
		setHealth(this.hp+amount);
	}
	public Integer[] availableAttacks(){
		ArrayList<Integer> possibleAttacks = new ArrayList<Integer>();
		for(int i = 0;i < moves.size();i++){
			if(moves.get(i).getCost() <= energy){
				possibleAttacks.add(new Integer(i));
			}
		}
		return possibleAttacks.toArray(new Integer[possibleAttacks.size()]);
	}
	public void attack(Pokemon target, Attack attack){
		int damage = attack.getDamage();
		this.energy -= attack.getCost();
		if(attack.getSpecial() == Attack.WILD_CARD && PkmnArena.rand.nextBoolean()){
			System.out.println("The attack failed...");
		}
		else{
			if(this.type.equals(target.weakness)){
				damage*=2;
				System.out.println("Super effective! x2 damage!");
			}
			else if(this.type.equals(target.resistance)){
				damage/=2;
				System.out.println("Not very effective... x1/2 damage");
			}
			System.out.printf("%s dealt %d damage!\n",name,Math.max(0,damage-(debuffs[DISABLE_STATUS]?10:0)));
			target.damage(damage-(debuffs[DISABLE_STATUS]?10:0));
		}
		switch(attack.getSpecial()){
			default:
			break;
			case Attack.STUN:
				if(PkmnArena.rand.nextBoolean()){
					System.out.printf("%s has been stunned!\n",target.getName());
					target.debuffs[STUN_STATUS] = true;
				}
			break;
			case Attack.WILD_STORM:
				while(PkmnArena.rand.nextBoolean()){
					System.out.printf("Wild storm! %s used %s again and dealt an additional %d damage!\n",name,attack.getName(),Math.max(damage-(debuffs[DISABLE_STATUS]?10:0),0));
					target.damage(damage-(debuffs[DISABLE_STATUS]?10:0));
				}
			break;
			case Attack.DISABLE:
				System.out.printf("%s has been disabled!\n",target.getName());
				target.debuffs[DISABLE_STATUS] = true;
			break;
			case Attack.RECHARGE:
				System.out.printf("%s has recharged 20 energy\n",this.name);
				recharge(20);
			break;
			case Attack.HEAL:
				System.out.printf("%s has healed 20 health\n",this.name);
				heal(20);
			break;
		}
		
		attacked = true;
	}
	

	public String[] attacks(){
		String[] sOut = new String[moves.size()];
		for (int i = 0; i<moves.size();i++){
			sOut[i] = moves.get(i).getName();
		}
		return sOut;
	}
	public Attack getAttack(int attackIndex){
		return moves.get(attackIndex);
	}
	public boolean hasAttacked(){
		return attacked;
	}
	public void damage(int dmgAmt){
		this.setHealth(this.getHealth()-dmgAmt);
	}

	public String toString(){
		String attackString = "";
		String[] atNames = this.attacks();
		for(int i =0; i<atNames.length;i++){
			attackString += String.format("MOV %d: %-13s ",i+1,atNames[i]);
		}
		return String.format("%-15s HP: %3d/%-3d NRG: %-2d/50 TYP: %-8s RST: %-8s WKS: %-8s %-30s"+(debuffs[0]?" Stunned":"")+(debuffs[1]?" Disabled":""),name,hp,maxHp,energy,type,resistance,weakness,attackString);
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Pokemon){
			return ((Pokemon)obj).name.equals(this.name);
		}
		return false;
	}
	
}