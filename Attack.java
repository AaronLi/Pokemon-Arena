//Attack.java
//Aaron Li
//deals with attacks, names, damage, cost, special, displaying the attack info
public class Attack{
	private String attackName;
	private Special special;
	private int energyCost, damage;
	
	//public static final String[] specials = {" ", "stun", "wild card", "wild storm", "disable", "recharge","heal"}; // used when reading the specials from the file
	//public static final String[] specialDisplay = {"None", "Stun", "Wild Card", "Wild Storm", "Disable", "Recharge","Heal"}; // The way a special is shown to the user
	public enum Special{
		NONE,
		STUN,
		WILD_CARD,
		WILD_STORM,
		DISABLE,
		RECHARGE,
		HEAL;

		@Override
		public String toString(){
			String[] segments = this.name().split("_");
			String out = "";
			for(String word : segments){
				out+=word.charAt(0)+word.substring(1).toLowerCase()+" ";
			}
			return out;
		}
		public static Special fromFileString(String sIn){
			switch(sIn.toLowerCase()){
				case "stun":
					return Special.STUN;
				case "wild card":
					return Special.WILD_CARD;
				case "disable":
					return Special.DISABLE;
				case "recharge":
					return Special.RECHARGE;
				case "heal":
					return Special.HEAL;
				case "wild storm":
					return Special.WILD_STORM;
				default:
					return Special.NONE;
			}
		}
	}
	public Attack(String atName, int cost,int damage,Special special){//constructor
		this.attackName = atName;
		this.energyCost = cost;
		this.damage = damage;
		this.special = special;
	}
	public Special getSpecial(){
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