package com.dumfing.pokemon_arena;

import lombok.Data;

//Attack.java
//Aaron Li
//deals with attacks, names, damage, cost, special, displaying the attack info
@Data
public class Attack{
	private final String name;
    private final Special special;
	private final int cost;
    private final int damage;

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
			StringBuilder out = new StringBuilder();
			for(String word : segments){
				out.append(word.charAt(0));
				out.append(word.substring(1).toLowerCase());
				out.append(" ");
			}
			return out.toString();
		}
	}

	public String toString(){
		return String.format("%-15s CST: %3d DMG: %3d SPL: %-10s", name, cost,damage,special);
	}
	
}