//Pokemon.java
//Aaron Li
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pokemon{
	private int hp, type, resistance, weakness;
	private String name;
	private boolean attacked = false;
	
	private boolean[]debuffs = {false,false};
	private int energy = 50;
	private HashMap<String,Attack> moves = new HashMap<String,Attack>();
	
	public static final int NO_TYPE = 0;
	public static final int EARTH = 1;
	public static final int FIRE = 2;
	public static final int LEAF = 3;
	public static final int WATER = 4;
	public static final int FIGHTING = 5;
	public static final int ELECTRIC = 6;
	public static final int NORMAL = 7;
	public static final int PSYCHIC = 8;
	public static final String[] displayTypes = {"None","Earth","Fire","Grass","Water","Fighting","Electric"};
	
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
	public static final int STUN_STATUS = 0;
	public static final int DISABLE_STATUS = 1;
	
	public Pokemon(String infoIn){
		int special = 0;
		int energyCost,damage,numAttacks;
		String atName;
		String[] pokeInfo = infoIn.split(",");
		name = pokeInfo[NAME];
		hp = Integer.parseInt(pokeInfo[HEALTH]);
		type = Pokedex.types.get(pokeInfo[TYPE]);
		resistance = Pokedex.types.get(pokeInfo[RESISTANCE]);
		weakness = Pokedex.types.get(pokeInfo[WEAKNESS]);
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
		this.type = pkmnIn.type;
		this.resistance = pkmnIn.resistance;
		this.weakness = pkmnIn.weakness;
		this.energy = pkmnIn.energy;
		this.moves = new HashMap<String,Attack>(pkmnIn.moves);
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
		this.hp = Math.max(0,health);
	}
	public void setDisable(boolean value){
		debuffs[DISABLE_STATUS] = value;
	}
	public void setStun(boolean value){
		debuffs[STUN_STATUS] = value;
	}
	public void addAttack(String name, int cost, int damage, int special){
		moves.put(name,new Attack(name,cost,damage,special));
	}
	public void recharge(int amount){
		energy = Math.min(50,energy+amount);
	}
	public String[] availableAttacks(){
		ArrayList<String> possibleAttacks = new ArrayList<String>();
		for(String atName : attacks()){
			if(moves.get(atName).getCost() <= energy){//LEFT OFF HERE
				possibleAttacks.add(atName);
			}
		}
		return possibleAttacks.toArray(new String[possibleAttacks.size()]);
	}
	public boolean attack(Pokemon target, Attack attack){
		boolean success = false;
		int damage = debuffs[DISABLE_STATUS]? Math.max(attack.getDamage()-10,0): attack.getDamage(); // damage without disable and with type advantages
		if(this.energy>=attack.getCost()){
			this.energy -= attack.getCost();
			if(attack.getSpecial() == Attack.WILD_CARD && PkmnArena.rand.nextBoolean()){
				System.out.println("The attack failed...");
			}
			else{
				if(this.type == target.weakness){
					damage*=2;
					System.out.println("Super effective! x2 damage!");
				}
				else if(this.type == target.resistance){
					damage/=2;
					System.out.println("Not very effective... x1/2 damage");
				}
				System.out.printf("%s dealt %d damage!\n",name,damage);
				target.setHealth(target.hp-damage);
			}
			success = true;
			switch(attack.getSpecial()){
				case Attack.NO_SPECIAL:
				break;
				case Attack.STUN:
					if(PkmnArena.rand.nextBoolean()){
						System.out.printf("%s has been stunned!\n",target.getName());
						target.debuffs[STUN_STATUS] = true;
					}
				break;
				case Attack.WILD_STORM:
					while(PkmnArena.rand.nextBoolean()){
						System.out.printf("Wild storm! %s used %s again and dealt an additional %d damage!\n",name,attack.getName(),damage);
						//System.out.printf("%s dealt %d damage!\n",name,damage); REMOVETHIS
						target.setHealth(target.hp-damage);
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
			}
		}
		attacked = true;
		return success;
	}
	

	public String[] attacks(){
		String[] sOut = moves.keySet().toArray(new String[moves.keySet().size()]);
		return sOut;
	}
	public Attack getAttack(String attackName){
		return moves.get(attackName);
	}
	public boolean hasAttacked(){
		return attacked;
	}


	public String toString(){
		String sOut = "";//Change out with stringf version
		sOut+=name+"\nHP: "+hp+"\nEnergy: "+energy+"\nType: "+displayTypes[type]+"\nResistance: "+displayTypes[resistance]+"\nWeakness: "+displayTypes[weakness]+"\nStunned: "+debuffs[0]+"\nDisabled: "+debuffs[1]+"\n\n";
		for(String moveName : moves.keySet()){
			sOut+=moves.get(moveName).toString()+"\n\n";
		}
		return sOut;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Pokemon){
			return ((Pokemon)obj).name.equals(this.name);
		}
		return false;
	}
	
}