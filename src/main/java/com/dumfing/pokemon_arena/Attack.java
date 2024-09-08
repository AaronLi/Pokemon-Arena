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

	public void apply(Pokemon attacker, Pokemon attacked) {
		int damage = this.getDamage(); // basic damage without any modifiers
		attacker.setEnergy(attacker.getEnergy() - this.getCost()); // reduce the attack cost from the attacking pokemon's energy pool
		if(this.getSpecial().equals(Attack.Special.WILD_CARD) && PkmnBattle.rand.nextBoolean()){ // check whether or not the wild card effect is possible
			System.out.println(PkmnTools.ANSI_PURPLE+"The attack failed..."+PkmnTools.ANSI_RESET);
		}
		else{
			if(attacker.getType().equals(attacked.getWeakness())){ // double the damage if the pokemon's type is the target's weakness
				damage*=2;
				System.out.println("Super effective! "+PkmnTools.ANSI_GREEN+"x2 damage!"+PkmnTools.ANSI_RESET);
			}
			else if(attacker.getType().equals(attacked.getResistance())){ // halve the damage if the pokemon's type is the target's resistance
				damage/=2;
				System.out.println("Not very effective..."+PkmnTools.ANSI_RED+" x1/2 damage"+PkmnTools.ANSI_RESET);
			}
			System.out.printf("%s dealt "+PkmnTools.ANSI_RED+"%d damage!\n"+PkmnTools.ANSI_RESET,name,Math.max(0,damage-(attacker.getDisable()?10:0))); // Print the resulting damage with the disable debuff deducted
			attacked.damage(damage-(attacker.getDisable()?10:0)); // damage the targeted pokemon
		}
		//Specials
		if(this.getSpecial().equals(Attack.Special.STUN)){ // if the special is stun
			if(PkmnBattle.rand.nextBoolean()){ // rng 50/50 chance
				System.out.printf(PkmnTools.ANSI_YELLOW+"%s has been stunned!\n"+PkmnTools.ANSI_RESET,attacked.getName()); // Print if the pokemon has been stunned
				attacked.setStun(true); // make the pokemon stunned
			}
		}
		else if(this.getSpecial().equals(Attack.Special.WILD_STORM)){ // If the special is Wild storm
			while(PkmnBattle.rand.nextBoolean()){ // Loop with 50/50 chance of breaking each iteration
				System.out.printf(PkmnTools.ANSI_RED+"Wild storm!"+PkmnTools.ANSI_RESET+" %s used %s again and dealt an additional"+PkmnTools.ANSI_RED+" %d damage!\n"+PkmnTools.ANSI_RESET,name,this.getName(),Math.max(damage-(attacker.getDisable()?10:0),0)); // Print the resulting successful wild storm
				attacked.damage(damage-(attacker.getDisable()?10:0)); // damage the target
			}
		}
		else if(this.getSpecial().equals(Attack.Special.DISABLE)){ // if the special is Disable
			System.out.printf(PkmnTools.ANSI_YELLOW+"%s has been disabled!\n"+PkmnTools.ANSI_RESET,attacked.getName()); // print that the target is disabled
			attacked.setDisable(true); // disable the target
		}
		else if(this.getSpecial().equals(Attack.Special.RECHARGE)){ // if the special is Recharge
			System.out.printf(PkmnTools.ANSI_CYAN+"%s has recharged 20 energy\n"+PkmnTools.ANSI_RESET,this.name); // print the result
			attacker.recharge(20); // recharge 20 energy
		}
		else if(this.getSpecial().equals(Attack.Special.HEAL)){ // if the special is heal
			System.out.printf(PkmnTools.ANSI_GREEN+"%s has healed 20 health\n"+PkmnTools.ANSI_RESET,this.name); // print the result
			attacker.heal(20); // heal 20 hp
		}
		attacker.setAttackedThisTurn(true); // set the pokemon to attacked so it won't gain energy at the end of the round
	}
	
}