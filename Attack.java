//Attack.java
//Aaron Li
//deals with attacks, names, damage, cost, special, displaying the attack info
public class Attack{
	private String attackName, special;
	private int energyCost, damage;
	
	public static final int NO_SPECIAL = 0;
	public static final int STUN = 1;
	public static final int WILD_CARD = 2;
	public static final int WILD_STORM = 3;
	public static final int DISABLE = 4;
	public static final int RECHARGE = 5;
	public static final int HEAL = 6;
	
	public static final String[] specials = {" ", "stun", "wild card", "wild storm", "disable", "recharge","heal"}; // used when reading the specials from the file
	public static final String[] specialDisplay = {"None", "Stun", "Wild Card", "Wild Storm", "Disable", "Recharge","Heal"}; // The way a special is shown to the user
	
	public Attack(String atName, int cost,int damage,String special){//constructor
		this.attackName = atName;
		this.energyCost = cost;
		this.damage = damage;
		this.special = special;
	}
	public String getSpecial(){
		return special;
	}
	public int getCost(){
		return energyCost;
	}
	public int getDamage(){
		return damage;
	}
	public String getName(){
		return attackName;
	}
	public String toString(){
		return String.format("%-15s CST: %3d DMG: %3d SPL: %-10s",attackName,energyCost,damage,special);
	}
	
}