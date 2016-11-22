//Attack.java
//Aaron Li
public class Attack{
	private String attackName;
	private int energyCost, damage, special;
	
	public static final int NO_SPECIAL = 0;
	public static final int STUN = 1;
	public static final int WILD_CARD = 2;
	public static final int WILD_STORM = 3;
	public static final int DISABLE = 4;
	public static final int RECHARGE = 5;
	public static final int STUN_STATUS = 0;
	public static final int DISABLE_STATUS = 1;
	public static final String[] specials = {" ", "stun", "wild card", "wild storm", "disable", "recharge"};
	public static final String[] specialDisplay = {"None", "Stun", "Wild Card", "Wild Storm", "Disable", "Recharge"};
	
	public Attack(String atName, int cost,int damage,int special){
		this.attackName = atName;
		this.energyCost = cost;
		this.damage = damage;
		this.special = special;
	}
	
	public int getCost(){
		return energyCost;
	}
	public int getDamage(){
		return damage;
	}
	public String toString(){
		String sOut = "";
		sOut += attackName+":\n\tEnergy Cost: "+energyCost+"\n\tDamage: "+damage+"\n\tSpecial: "+specialDisplay[special];
		return sOut;
	}
	
}